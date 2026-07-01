package com.codewise.facturaexpress.service;

import com.codewise.facturaexpress.model.DetalleFactura;
import com.codewise.facturaexpress.model.Factura;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

// Servicio para la generacion de XML de facturas electronicas (formato DIAN)
@Service
public class XmlService {

    // Formato de fecha ISO para el XML
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    // Genera el XML completo de una factura electronica
    public String generarXmlFactura(Factura factura) {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<FacturaElectronica xmlns=\"http://www.dian.gov.co/schemas/factura/v2\">\n");
        xml.append("  <Cabecera>\n");
        xml.append("    <IdFactura>").append(escape(factura.getId() != null ? factura.getId().toString() : "")).append("</IdFactura>\n");
        xml.append("    <FechaEmision>").append(factura.getFecha() != null ? factura.getFecha().format(FMT) : "").append("</FechaEmision>\n");
        xml.append("    <ClienteId>").append(escape(factura.getClienteId() != null ? factura.getClienteId().toString() : "")).append("</ClienteId>\n");
        xml.append("    <ClienteNombre>").append(escape(factura.getClienteNombre())).append("</ClienteNombre>\n");
        xml.append("    <Total>").append(factura.getTotal() != null ? factura.getTotal().toPlainString() : "0.00").append("</Total>\n");
        xml.append("    <Estado>").append(escape(factura.getEstado())).append("</Estado>\n");
        xml.append("    <CUNE>").append(escape(factura.getCune())).append("</CUNE>\n");
        xml.append("  </Cabecera>\n");
        xml.append("  <Detalles>\n");
        if (factura.getDetalles() != null) {
            int idx = 1;
            for (DetalleFactura d : factura.getDetalles()) {
                xml.append("    <Detalle>\n");
                xml.append("      <NumeroLinea>").append(idx++).append("</NumeroLinea>\n");
                xml.append("      <ProductoId>").append(d.getProductoId() != null ? d.getProductoId().toString() : "").append("</ProductoId>\n");
                xml.append("      <Cantidad>").append(d.getCantidad() != null ? d.getCantidad().toString() : "0").append("</Cantidad>\n");
                xml.append("      <PrecioUnitario>").append(d.getPrecioUnitario() != null ? d.getPrecioUnitario().toPlainString() : "0.00").append("</PrecioUnitario>\n");
                xml.append("      <Subtotal>").append(d.getSubtotal() != null ? d.getSubtotal().toPlainString() : "0.00").append("</Subtotal>\n");
                xml.append("      <Descuento>").append(d.getDescuento() != null ? d.getDescuento().toPlainString() : "0.00").append("</Descuento>\n");
                xml.append("    </Detalle>\n");
            }
        }
        xml.append("  </Detalles>\n");
        xml.append("</FacturaElectronica>\n");
        return xml.toString();
    }

    // Escapa caracteres especiales XML
    private String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&apos;");
    }
}
