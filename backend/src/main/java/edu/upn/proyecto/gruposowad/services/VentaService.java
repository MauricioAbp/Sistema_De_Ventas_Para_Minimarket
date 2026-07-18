package edu.upn.proyecto.gruposowad.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import edu.upn.proyecto.gruposowad.dtos.venta.AnularVentaRequest;
import edu.upn.proyecto.gruposowad.models.AnulacionVenta;
import edu.upn.proyecto.gruposowad.repositories.AnulacionVentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.upn.proyecto.gruposowad.dtos.venta.VentaDetalleRequest;
import edu.upn.proyecto.gruposowad.dtos.venta.VentaRequest;
import edu.upn.proyecto.gruposowad.dtos.boleta.BoletaResponse;
import edu.upn.proyecto.gruposowad.dtos.venta.ComprobanteAnulacionResponse;
import edu.upn.proyecto.gruposowad.dtos.venta.VentaResponse;
import edu.upn.proyecto.gruposowad.exceptions.BusinessException;
import edu.upn.proyecto.gruposowad.models.Boleta;
import edu.upn.proyecto.gruposowad.models.Caja;
import edu.upn.proyecto.gruposowad.models.Empresa;
import edu.upn.proyecto.gruposowad.models.DetalleVenta;
import edu.upn.proyecto.gruposowad.models.MovimientoInventario;
import edu.upn.proyecto.gruposowad.models.Producto;
import edu.upn.proyecto.gruposowad.models.Usuario;
import edu.upn.proyecto.gruposowad.models.Venta;
import edu.upn.proyecto.gruposowad.repositories.BoletaRepository;
import edu.upn.proyecto.gruposowad.repositories.CajaRepository;
import edu.upn.proyecto.gruposowad.repositories.EmpresaRepository;
import edu.upn.proyecto.gruposowad.repositories.MovimientoInventarioRepository;
import edu.upn.proyecto.gruposowad.repositories.ProductoRepository;
import edu.upn.proyecto.gruposowad.repositories.UsuarioRepository;
import edu.upn.proyecto.gruposowad.repositories.VentaRepository;
import jakarta.transaction.Transactional;

@Service
public class VentaService {
    @Autowired
    VentaRepository ventaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
private AnulacionVentaRepository anulacionVentaRepository;

    @Autowired
    private CajaRepository cajaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MovimientoInventarioRepository movimientoInventarioRepository;

    @Autowired
    private BoletaRepository boletaRepository;

    @Autowired
    private EmpresaRepository empresaRepository;
    
    @Autowired
    private BoletaService boletaService;

    public ArrayList<Venta> listar(){
        return (ArrayList<Venta>) ventaRepository.findAll();
    }

    @Transactional
    public Venta nuevo(Venta venta){
        // 1. Validamos y actualizamos el stock de los productos PRIMERO
        if(venta.getDetalles() != null){
            for(DetalleVenta detalle : venta.getDetalles()){
                // Vinculamos el detalle con su cabecera para que no quede huérfano
                detalle.setVenta(venta); 

                Producto producto = productoRepository.findById(detalle.getProducto().getId_producto())
                .orElseThrow(()-> new RuntimeException("Producto no encontrado ID: " + detalle.getProducto().getId_producto()));

                if(producto.getStock_actual() < detalle.getCantidad()){
                    throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre_producto());
                }
                
                int nuevoStock = producto.getStock_actual() - detalle.getCantidad();
                producto.setStock_actual(nuevoStock);
                productoRepository.save(producto);
            }
        }
        
        // 2. Al guardar la venta aquí, JPA guardará la cabecera y TODOS sus detalles en cascada con su ID correspondiente
        return ventaRepository.save(venta);
    }

    @Transactional
    public VentaResponse registrarPos(VentaRequest request) {
        Caja caja = cajaRepository.findById(request.getIdCaja())
                .orElseThrow(() -> new BusinessException("Caja no encontrada"));

        if (!"ABIERTA".equalsIgnoreCase(caja.getEstado())) {
            throw new BusinessException("La caja no esta abierta");
        }

        Usuario usuario = usuarioRepository.findById(request.getIdUsuario())
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        Venta venta = new Venta();
        venta.setCaja(caja);
        venta.setUsuario(usuario);
        venta.setNumero_venta("V-" + System.currentTimeMillis());
        venta.setFecha_venta(LocalDateTime.now());
        venta.setMetodo_pago(request.getMetodoPago());
        venta.setMonto_recibido(request.getMontoRecibido());
        venta.setEstado("COMPLETADA");

        BigDecimal subtotal = BigDecimal.ZERO;
        List<DetalleVenta> detalles = new ArrayList<>();
        List<MovimientoInventario> movimientos = new ArrayList<>();

        for (VentaDetalleRequest item : request.getDetalles()) {
            Producto producto = productoRepository.buscarPorIdConBloqueo(item.getIdProducto())
                    .orElseThrow(() -> new BusinessException("Producto no encontrado"));

            if (producto.getPrecio_venta() == null) {
                throw new BusinessException("El producto no tiene precio de venta: " + producto.getNombre_producto());
            }

            Integer stockActual = producto.getStock_actual();
            if (stockActual == null || stockActual < item.getCantidad()) {
                throw new BusinessException("Stock insuficiente para el producto: " + producto.getNombre_producto());
            }

            BigDecimal itemSubtotal = producto.getPrecio_venta()
                    .multiply(BigDecimal.valueOf(item.getCantidad()))
                    .setScale(2, RoundingMode.HALF_UP);
            subtotal = subtotal.add(itemSubtotal);

            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(venta);
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecio_unitario(producto.getPrecio_venta());
            detalle.setPrecio_compra(producto.getPrecio_compra());
            detalle.setDescuento(BigDecimal.ZERO);
            detalle.setSubtotal(itemSubtotal);
            detalles.add(detalle);

            producto.setStock_actual(stockActual - item.getCantidad());
            productoRepository.save(producto);

            MovimientoInventario movimiento = new MovimientoInventario();
            movimiento.setProducto(producto);
            movimiento.setUsuario(usuario);
            movimiento.setTipo_movimiento("SALIDA");
            movimiento.setCantidad(item.getCantidad());
            movimiento.setStock_anterior(stockActual);
            movimiento.setStock_posterior(producto.getStock_actual());
            movimiento.setMotivo("VENTA");
            movimiento.setTabla_referencia("venta");
            movimiento.setFecha_movimiento(LocalDateTime.now());
            movimientos.add(movimiento);
        }

        subtotal = subtotal.setScale(2, RoundingMode.HALF_UP);
        BigDecimal igv = subtotal.multiply(new BigDecimal("0.18")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(igv).setScale(2, RoundingMode.HALF_UP);

        if (request.getMontoRecibido().compareTo(total) < 0) {
            throw new BusinessException("Monto recibido insuficiente");
        }

        BigDecimal vuelto = request.getMontoRecibido().subtract(total).setScale(2, RoundingMode.HALF_UP);
        venta.setSubtotal(subtotal);
        venta.setIgv_monto(igv);
        venta.setTotal(total);
        venta.setVuelto(vuelto);
        venta.setDetalles(detalles);

        Venta ventaGuardada = ventaRepository.save(venta);

        for (MovimientoInventario movimiento : movimientos) {
            movimiento.setId_referencia(ventaGuardada.getId_venta());
            movimientoInventarioRepository.save(movimiento);
        }

        Boleta boleta = boletaService.generarParaVenta(ventaGuardada);

        return new VentaResponse(
                ventaGuardada.getId_venta(),
                boleta.getId_boleta(),
                ventaGuardada.getNumero_venta(),
                subtotal,
                igv,
                total,
                vuelto);
    }
    @Transactional
public Venta anular(Long idVenta, AnularVentaRequest request) {
    Venta venta = ventaRepository.findById(idVenta)
            .orElseThrow(() -> new BusinessException("Venta no encontrada"));

    if ("ANULADA".equalsIgnoreCase(venta.getEstado())) {
        throw new BusinessException("La venta ya se encuentra anulada");
    }

    if (venta.getCaja() != null && !"ABIERTA".equalsIgnoreCase(venta.getCaja().getEstado())) {
    throw new BusinessException("No se puede anular una venta de una caja cerrada");
}

    Usuario usuario = usuarioRepository.findById(request.getIdUsuario())
            .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

    if (venta.getDetalles() != null) {
        for (DetalleVenta detalle : venta.getDetalles()) {
            Producto producto = productoRepository.buscarPorIdConBloqueo(detalle.getProducto().getId_producto())
                    .orElseThrow(() -> new BusinessException("Producto no encontrado"));

            Integer stockAnterior = producto.getStock_actual() == null ? 0 : producto.getStock_actual();
            Integer cantidad = detalle.getCantidad() == null ? 0 : detalle.getCantidad();

            producto.setStock_actual(stockAnterior + cantidad);
            productoRepository.save(producto);

            MovimientoInventario movimiento = new MovimientoInventario();
            movimiento.setProducto(producto);
            movimiento.setUsuario(usuario);
            movimiento.setTipo_movimiento("ENTRADA");
            movimiento.setCantidad(cantidad);
            movimiento.setStock_anterior(stockAnterior);
            movimiento.setStock_posterior(producto.getStock_actual());
            movimiento.setMotivo("ANULACION_VENTA");
            movimiento.setTabla_referencia("venta");
            movimiento.setId_referencia(venta.getId_venta());
            movimiento.setFecha_movimiento(LocalDateTime.now());
            movimientoInventarioRepository.save(movimiento);
        }
    }

    venta.setEstado("ANULADA");
    Venta ventaAnulada = ventaRepository.save(venta);

    AnulacionVenta anulacion = new AnulacionVenta();
    anulacion.setVenta(ventaAnulada);
    anulacion.setUsuario(usuario);
    anulacion.setMotivo(request.getMotivo());
    anulacion.setStock_revertido(true);
    anulacionVentaRepository.save(anulacion);

    return ventaAnulada;
}

public ComprobanteAnulacionResponse generarComprobanteAnulacion(Venta venta, AnularVentaRequest request) {
BigDecimal montoDevuelto = venta.getTotal() == null ? BigDecimal.ZERO : venta.getTotal();
String numeroComprobante = "ANU-" + (venta.getNumero_venta() != null ? venta.getNumero_venta().replaceAll("[^0-9]", "") : venta.getId_venta());
String descripcion = String.format("Devolución de dinero al cliente por anulación de la venta %s.", venta.getNumero_venta());
List<String> lineas = venta.getDetalles() == null
        ? List.of()
        : venta.getDetalles().stream()
                .map(detalle -> String.format("%d x %s - S/ %s",
                        detalle.getCantidad() == null ? 0 : detalle.getCantidad(),
                        detalle.getProducto() == null ? "Producto" : detalle.getProducto().getNombre_producto(),
                        detalle.getSubtotal() == null ? "0.00" : detalle.getSubtotal().setScale(2, RoundingMode.HALF_UP).toPlainString()))
                .collect(Collectors.toList());

return new ComprobanteAnulacionResponse(
        numeroComprobante,
        LocalDateTime.now(),
        montoDevuelto,
        venta.getMetodo_pago(),
        request.getMotivo(),
        descripcion,
        lineas
);
}

public BoletaResponse obtenerDocumentoVenta(Long idVenta, String tipoDocumento) {
Venta venta = ventaRepository.findById(idVenta)
        .orElseThrow(() -> new BusinessException("Venta no encontrada"));

String tipoNormalizado = tipoDocumento == null ? "boleta" : tipoDocumento.trim().toLowerCase(Locale.ROOT);
if ("anulacion".equals(tipoNormalizado) || "anulacion-venta".equals(tipoNormalizado)) {
    AnulacionVenta anulacionVenta = anulacionVentaRepository.findByVentaIdVenta(idVenta)
            .orElseThrow(() -> new BusinessException("Comprobante de anulación no encontrado"));
    return construirDocumentoAnulacion(venta, anulacionVenta);
}

Boleta boleta = boletaRepository.findByVentaIdVenta(idVenta)
        .orElseThrow(() -> new BusinessException("Boleta no encontrada"));

return boletaService.obtenerFormal(boleta.getId_boleta());
}

private BoletaResponse construirDocumentoAnulacion(Venta venta, AnulacionVenta anulacionVenta) {
Empresa empresa = empresaRepository.findAll().stream().findFirst().orElse(null);
BigDecimal montoDevuelto = venta.getTotal() == null ? BigDecimal.ZERO : venta.getTotal().setScale(2, RoundingMode.HALF_UP);
String numeroDocumento = "ANU-" + (venta.getNumero_venta() == null ? venta.getId_venta() : venta.getNumero_venta().replaceAll("[^0-9]", ""));
List<String> lineas = new ArrayList<>();
if (venta.getDetalles() != null) {
    lineas.addAll(venta.getDetalles().stream()
            .map(detalle -> String.format("%d x %s - S/ %s",
                    detalle.getCantidad() == null ? 0 : detalle.getCantidad(),
                    detalle.getProducto() == null ? "Producto" : detalle.getProducto().getNombre_producto(),
                    detalle.getSubtotal() == null ? "0.00" : detalle.getSubtotal().setScale(2, RoundingMode.HALF_UP).toPlainString()))
            .collect(Collectors.toList()));
}
lineas.add(String.format("Monto devuelto: S/ %s", montoDevuelto.toPlainString()));
if (anulacionVenta.getMotivo() != null && !anulacionVenta.getMotivo().isBlank()) {
    lineas.add("Motivo: " + anulacionVenta.getMotivo());
}

String descripcion = String.format("Se devolvió S/ %s al cliente por la anulación de la venta %s.",
        montoDevuelto.toPlainString(), venta.getNumero_venta());

return new BoletaResponse(
        empresa == null ? null : empresa.getRuc(),
        empresa == null ? null : empresa.getRazon_social(),
        empresa == null ? null : empresa.getNombre_comercial(),
        empresa == null ? null : empresa.getDireccion(),
        "ANU",
        numeroDocumento,
        anulacionVenta.getFecha_anulacion(),
        obtenerCajero(venta),
        lineas,
        BigDecimal.ZERO,
        BigDecimal.ZERO,
        montoDevuelto,
        venta.getMetodo_pago(),
        "ANULACION",
        montoDevuelto,
        anulacionVenta.getMotivo(),
        descripcion);
}

private String obtenerCajero(Venta venta) {
if (venta == null || venta.getUsuario() == null) {
    return "";
}
String nombre = venta.getUsuario().getNombre() == null ? "" : venta.getUsuario().getNombre();
String apellido = venta.getUsuario().getApellido() == null ? "" : venta.getUsuario().getApellido();
return (nombre + " " + apellido).trim();
}

public Venta buscarPorId(Long idVenta) {
    return ventaRepository.findById(idVenta)
            .orElseThrow(() -> new BusinessException("Venta no encontrada"));
}
}
