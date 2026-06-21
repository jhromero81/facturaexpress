package com.codewise.facturaexpress.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Factura {

    private Long id;
    private Long clienteId;
    private String clienteNombre;
    private Long usuarioId;
    private LocalDateTime fecha;
    private BigDecimal total;
    private String estado;
    private String xml;
    private byte[] pdf;
    private String cune;
    private String firmaEstado;
    private Integer intentosDian;
    private Integer correoEnviado;
    private List<DetalleFactura> detalles;

    public Factura() {
        this.detalles = new ArrayList<>();
        this.fecha = LocalDateTime.now();
        this.estado = "PENDIENTE";
        this.firmaEstado = "pendiente";
        this.intentosDian = 0;
        this.correoEnviado = 0;
        this.total = BigDecimal.ZERO;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getXml() { return xml; }
    public void setXml(String xml) { this.xml = xml; }
    public byte[] getPdf() { return pdf; }
    public void setPdf(byte[] pdf) { this.pdf = pdf; }
    public String getCune() { return cune; }
    public void setCune(String cune) { this.cune = cune; }
    public String getFirmaEstado() { return firmaEstado; }
    public void setFirmaEstado(String firmaEstado) { this.firmaEstado = firmaEstado; }
    public Integer getIntentosDian() { return intentosDian; }
    public void setIntentosDian(Integer intentosDian) { this.intentosDian = intentosDian; }
    public Integer getCorreoEnviado() { return correoEnviado; }
    public void setCorreoEnviado(Integer correoEnviado) { this.correoEnviado = correoEnviado; }
    public List<DetalleFactura> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleFactura> detalles) { this.detalles = detalles; }

    @Override
    public String toString() {
        return "Factura{" + "id=" + id + ", clienteId=" + clienteId + ", total=" + total + ", estado='" + estado + '\'' + '}';
    }
}
