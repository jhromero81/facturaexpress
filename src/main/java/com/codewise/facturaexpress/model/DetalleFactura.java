package com.codewise.facturaexpress.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

// Entidad que representa el detalle (línea de producto) de una factura.
// Relaciones: @ManyToOne Factura, @ManyToOne Producto.
// El campo transient (productoNombre) se rellena via @PostLoad.
@Entity
@Table(name = "detalles_factura")
public class DetalleFactura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factura_id", nullable = false)
    private Factura factura;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Transient
    private String productoNombre;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal descuento;

    public DetalleFactura() {
        this.descuento = BigDecimal.ZERO;
    }

    @PostLoad
    private void postLoad() {
        if (producto != null) this.productoNombre = producto.getNombre();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Factura getFactura() { return factura; }
    public void setFactura(Factura factura) { this.factura = factura; }
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
    public String getProductoNombre() { return productoNombre; }
    public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public BigDecimal getDescuento() { return descuento; }
    public void setDescuento(BigDecimal descuento) { this.descuento = descuento; }

    @Override
    public String toString() {
        return "DetalleFactura{" + "id=" + id + ", producto=" + (producto != null ? producto.getNombre() : "null") + ", cantidad=" + cantidad + ", subtotal=" + subtotal + '}';
    }
}
