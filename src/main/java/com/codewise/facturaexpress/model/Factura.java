package com.codewise.facturaexpress.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Factura {

    private Long id;
    private Long clienteId;
    private String clienteNombre;
    private LocalDateTime fecha;
    private BigDecimal total;
    private String estado;
    private List<DetalleFactura> detalles;

    public Factura() {
        this.detalles = new ArrayList<>();
        this.fecha = LocalDateTime.now();
        this.estado = "PENDIENTE";
        this.total = BigDecimal.ZERO;
    }

    public Factura(Long id, Long clienteId, LocalDateTime fecha, BigDecimal total, String estado) {
        this.id = id;
        this.clienteId = clienteId;
        this.fecha = fecha;
        this.total = total;
        this.estado = estado;
        this.detalles = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<DetalleFactura> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleFactura> detalles) {
        this.detalles = detalles;
    }

    @Override
    public String toString() {
        return "Factura{" + "id=" + id + ", clienteId=" + clienteId + ", total=" + total + ", estado='" + estado + '\'' + '}';
    }
}
