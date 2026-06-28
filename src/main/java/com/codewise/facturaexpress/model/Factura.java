package com.codewise.facturaexpress.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "facturas")
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @Transient
    private String clienteNombre;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Column(nullable = false)
    private String estado;

    @Column(columnDefinition = "LONGTEXT")
    private String xml;

    @Column(columnDefinition = "LONGBLOB")
    private byte[] pdf;

    private String cune;

    @Column(name = "firma_estado")
    private String firmaEstado;

    @Column(name = "intentos_dian")
    private Integer intentosDian;

    @Column(name = "correo_enviado", nullable = false)
    private boolean correoEnviado;

    @Transient
    private List<DetalleFactura> detalles;

    public Factura() {
        this.detalles = new ArrayList<>();
        this.fecha = LocalDateTime.now();
        this.estado = "PENDIENTE";
        this.firmaEstado = "pendiente";
        this.intentosDian = 0;
        this.correoEnviado = false;
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
    public boolean isCorreoEnviado() { return correoEnviado; }
    public void setCorreoEnviado(boolean correoEnviado) { this.correoEnviado = correoEnviado; }
    public List<DetalleFactura> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleFactura> detalles) { this.detalles = detalles; }

    @Override
    public String toString() {
        return "Factura{" + "id=" + id + ", clienteId=" + clienteId + ", total=" + total + ", estado='" + estado + '\'' + '}';
    }
}
