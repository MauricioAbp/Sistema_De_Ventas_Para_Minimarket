package edu.upn.proyecto.gruposowad.dtos.venta;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import edu.upn.proyecto.gruposowad.dtos.venta.ComprobanteAnulacionResponse;
import edu.upn.proyecto.gruposowad.models.DetalleVenta;
import edu.upn.proyecto.gruposowad.models.Producto;
import edu.upn.proyecto.gruposowad.models.Usuario;
import edu.upn.proyecto.gruposowad.models.Venta;

public class VentaConsultaResponse {
    private Long id_venta;
    private String numero_venta;
    private BigDecimal subtotal;
    private BigDecimal igv_monto;
    private BigDecimal total;
    private String metodo_pago;
    private BigDecimal monto_recibido;
    private BigDecimal vuelto;
    private String estado;
    private LocalDateTime fecha_venta;
    private UsuarioResumen usuario;
    private List<DetalleResponse> detalles;
    private ComprobanteAnulacionResponse comprobante_anulacion;

    public VentaConsultaResponse(Venta venta) {
        this(venta, null);
    }

    public VentaConsultaResponse(Venta venta, ComprobanteAnulacionResponse comprobanteAnulacion) {
        this.id_venta = venta.getId_venta();
        this.numero_venta = venta.getNumero_venta();
        this.subtotal = venta.getSubtotal();
        this.igv_monto = venta.getIgv_monto();
        this.total = venta.getTotal();
        this.metodo_pago = venta.getMetodo_pago();
        this.monto_recibido = venta.getMonto_recibido();
        this.vuelto = venta.getVuelto();
        this.estado = venta.getEstado();
        this.fecha_venta = venta.getFecha_venta();
        this.usuario = venta.getUsuario() == null ? null : new UsuarioResumen(venta.getUsuario());
        this.detalles = venta.getDetalles() == null
                ? List.of()
                : venta.getDetalles().stream().map(DetalleResponse::new).toList();
        this.comprobante_anulacion = comprobanteAnulacion;
    }

    public Long getId_venta() {
        return id_venta;
    }

    public String getNumero_venta() {
        return numero_venta;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getIgv_monto() {
        return igv_monto;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public String getMetodo_pago() {
        return metodo_pago;
    }

    public BigDecimal getMonto_recibido() {
        return monto_recibido;
    }

    public BigDecimal getVuelto() {
        return vuelto;
    }

    public String getEstado() {
        return estado;
    }

    public LocalDateTime getFecha_venta() {
        return fecha_venta;
    }

    public UsuarioResumen getUsuario() {
        return usuario;
    }

    public List<DetalleResponse> getDetalles() {
        return detalles;
    }

    public ComprobanteAnulacionResponse getComprobante_anulacion() {
        return comprobante_anulacion;
    }

    public static class UsuarioResumen {
        private Long id_usuario;
        private String nombre;
        private String apellido;
        private String username;

        public UsuarioResumen(Usuario usuario) {
            this.id_usuario = usuario.getId_usuario();
            this.nombre = usuario.getNombre();
            this.apellido = usuario.getApellido();
            this.username = usuario.getUsername();
        }

        public Long getId_usuario() {
            return id_usuario;
        }

        public String getNombre() {
            return nombre;
        }

        public String getApellido() {
            return apellido;
        }

        public String getUsername() {
            return username;
        }
    }

    public static class DetalleResponse {
        private Long id_detalle;
        private ProductoResumen producto;
        private Integer cantidad;
        private BigDecimal precio_unitario;
        private BigDecimal precio_compra;
        private BigDecimal descuento;
        private BigDecimal subtotal;

        public DetalleResponse(DetalleVenta detalle) {
            this.id_detalle = detalle.getId_detalle();
            this.producto = detalle.getProducto() == null ? null : new ProductoResumen(detalle.getProducto());
            this.cantidad = detalle.getCantidad();
            this.precio_unitario = detalle.getPrecio_unitario();
            this.precio_compra = detalle.getPrecio_compra();
            this.descuento = detalle.getDescuento();
            this.subtotal = detalle.getSubtotal();
        }

        public Long getId_detalle() {
            return id_detalle;
        }

        public ProductoResumen getProducto() {
            return producto;
        }

        public Integer getCantidad() {
            return cantidad;
        }

        public BigDecimal getPrecio_unitario() {
            return precio_unitario;
        }

        public BigDecimal getPrecio_compra() {
            return precio_compra;
        }

        public BigDecimal getDescuento() {
            return descuento;
        }

        public BigDecimal getSubtotal() {
            return subtotal;
        }
    }

    public static class ProductoResumen {
        private Long id_producto;
        private String codigo_producto;
        private String nombre_producto;

        public ProductoResumen(Producto producto) {
            this.id_producto = producto.getId_producto();
            this.codigo_producto = producto.getCodigo_producto();
            this.nombre_producto = producto.getNombre_producto();
        }

        public Long getId_producto() {
            return id_producto;
        }

        public String getCodigo_producto() {
            return codigo_producto;
        }

        public String getNombre_producto() {
            return nombre_producto;
        }
    }
}