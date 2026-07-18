package edu.upn.proyecto.gruposowad.dtos.venta;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ComprobanteAnulacionResponse {
    private String numero_comprobante;
    private LocalDateTime fecha_emision;
    private BigDecimal monto_devuelto;
    private String metodo_pago;
    private String motivo;
    private String descripcion;
    private List<String> lineas;

    public ComprobanteAnulacionResponse(String numero_comprobante, LocalDateTime fecha_emision,
            BigDecimal monto_devuelto, String metodo_pago, String motivo, String descripcion,
            List<String> lineas) {
        this.numero_comprobante = numero_comprobante;
        this.fecha_emision = fecha_emision;
        this.monto_devuelto = monto_devuelto;
        this.metodo_pago = metodo_pago;
        this.motivo = motivo;
        this.descripcion = descripcion;
        this.lineas = lineas;
    }

    public String getNumero_comprobante() {
        return numero_comprobante;
    }

    public LocalDateTime getFecha_emision() {
        return fecha_emision;
    }

    public BigDecimal getMonto_devuelto() {
        return monto_devuelto;
    }

    public String getMetodo_pago() {
        return metodo_pago;
    }

    public String getMotivo() {
        return motivo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public List<String> getLineas() {
        return lineas;
    }
}
