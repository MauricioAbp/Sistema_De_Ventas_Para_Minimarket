package edu.upn.proyecto.gruposowad.dtos.boleta;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class BoletaResponse {
    private String ruc;
    private String razonSocial;
    private String nombreComercial;
    private String direccion;
    private String serie;
    private String numero;
    private LocalDateTime fechaEmision;
    private String cajero;
    private List<String> detalles;
    private BigDecimal subtotal;
    private BigDecimal igv;
    private BigDecimal total;
    private String metodoPago;
    private String tipoDocumento;
    private BigDecimal montoDevuelto;
    private String motivo;
    private String descripcion;

    public BoletaResponse(String ruc, String razonSocial, String nombreComercial, String direccion,
            String serie, String numero, LocalDateTime fechaEmision, String cajero, List<String> detalles,
            BigDecimal subtotal, BigDecimal igv, BigDecimal total, String metodoPago) {
        this(ruc, razonSocial, nombreComercial, direccion, serie, numero, fechaEmision, cajero, detalles,
                subtotal, igv, total, metodoPago, "BOLETA", null, null, null);
    }

    public BoletaResponse(String ruc, String razonSocial, String nombreComercial, String direccion,
            String serie, String numero, LocalDateTime fechaEmision, String cajero, List<String> detalles,
            BigDecimal subtotal, BigDecimal igv, BigDecimal total, String metodoPago,
            String tipoDocumento, BigDecimal montoDevuelto, String motivo, String descripcion) {
        this.ruc = ruc;
        this.razonSocial = razonSocial;
        this.nombreComercial = nombreComercial;
        this.direccion = direccion;
        this.serie = serie;
        this.numero = numero;
        this.fechaEmision = fechaEmision;
        this.cajero = cajero;
        this.detalles = detalles;
        this.subtotal = subtotal;
        this.igv = igv;
        this.total = total;
        this.metodoPago = metodoPago;
        this.tipoDocumento = tipoDocumento;
        this.montoDevuelto = montoDevuelto;
        this.motivo = motivo;
        this.descripcion = descripcion;
    }

    public String getRuc() {
        return ruc;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public String getNombreComercial() {
        return nombreComercial;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getSerie() {
        return serie;
    }

    public String getNumero() {
        return numero;
    }

    public LocalDateTime getFechaEmision() {
        return fechaEmision;
    }

    public String getCajero() {
        return cajero;
    }

    public List<String> getDetalles() {
        return detalles;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getIgv() {
        return igv;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public BigDecimal getMontoDevuelto() {
        return montoDevuelto;
    }

    public String getMotivo() {
        return motivo;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
