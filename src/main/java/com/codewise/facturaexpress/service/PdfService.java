package com.codewise.facturaexpress.service;

import com.codewise.facturaexpress.model.DetalleFactura;
import com.codewise.facturaexpress.model.Factura;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

// Servicio para la generacion de documentos PDF (facturas y reportes)
@Service
public class PdfService {

    // Formato de fecha para los PDFs
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    // Colores corporativos para el disenio de los PDFs
    private static final Color ACCENT = new Color(26, 188, 156);
    private static final Color DARK = new Color(26, 35, 53);
    private static final Color MUTED = new Color(144, 164, 174);
    private static final Color BG_LIGHT = new Color(245, 245, 245);

    // Genera el PDF de una factura con cabecera, tabla de detalles y total
    public byte[] generarPdfFactura(Factura factura) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            Document doc = new Document(PageSize.A4);
            PdfWriter.getInstance(doc, baos);
            doc.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, new Color(26, 188, 156));
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.DARK_GRAY);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.BLACK);

            doc.add(new Paragraph("FACTURA ELECTRONICA", titleFont));
            doc.add(new Paragraph(" "));

            doc.add(new Paragraph("Factura #: FAC-" + (factura.getId() != null ? String.format("%05d", factura.getId()) : "00000"), headerFont));
            doc.add(new Paragraph("Fecha: " + (factura.getFecha() != null ? factura.getFecha().format(FMT) : ""), normalFont));
            doc.add(new Paragraph("Cliente: " + (factura.getClienteNombre() != null ? factura.getClienteNombre() : ""), normalFont));
            doc.add(new Paragraph("Estado: " + (factura.getEstado() != null ? factura.getEstado() : ""), normalFont));
            if (factura.getCune() != null && !factura.getCune().isEmpty()) {
                doc.add(new Paragraph("CUNE: " + factura.getCune(), normalFont));
            }
            doc.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3f, 1f, 2f, 2f, 2f});

            String[] headers = {"Producto", "Cant", "Precio Unit.", "Desc.", "Subtotal"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, boldFont));
                cell.setBackgroundColor(new Color(26, 188, 156));
                cell.setPadding(6);
                table.addCell(cell);
            }

            if (factura.getDetalles() != null) {
                for (DetalleFactura d : factura.getDetalles()) {
                    table.addCell(new Phrase(d.getProductoId() != null ? d.getProductoId().toString() : "", normalFont));
                    table.addCell(new Phrase(d.getCantidad() != null ? d.getCantidad().toString() : "0", normalFont));
                    table.addCell(new Phrase("$" + formato(d.getPrecioUnitario()), normalFont));
                    table.addCell(new Phrase("$" + formato(d.getDescuento() != null ? d.getDescuento() : BigDecimal.ZERO), normalFont));
                    table.addCell(new Phrase("$" + formato(d.getSubtotal()), normalFont));
                }
            }

            doc.add(table);
            doc.add(new Paragraph(" "));

            PdfPTable totalTable = new PdfPTable(2);
            totalTable.setWidthPercentage(50);
            totalTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalTable.addCell(new Phrase("TOTAL:", boldFont));
            totalTable.addCell(new Phrase("$" + formato(factura.getTotal()), boldFont));
            doc.add(totalTable);

            doc.close();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF", e);
        }
        return baos.toByteArray();
    }

    // Genera un PDF de reporte de ventas con KPIs, grafico y top productos
    public byte[] generarPdfReporte(
            String tituloPeriodo,
            byte[] chartImage,
            BigDecimal ventasMes, Integer facturasMes,
            BigDecimal ventasDia, BigDecimal ticketPromedio,
            List<Map<String, Object>> topProductos
    ) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            Document doc = new Document(PageSize.A4, 36, 36, 54, 54);
            PdfWriter.getInstance(doc, baos);
            doc.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, ACCENT);
            Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 10, MUTED);
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, DARK);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.BLACK);
            Font kpiValFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, DARK);
            Font kpiLblFont = FontFactory.getFont(FontFactory.HELVETICA, 8, MUTED);

            doc.add(new Paragraph("REPORTE DE VENTAS", titleFont));
            doc.add(new Paragraph(tituloPeriodo + " — Generado el " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), subtitleFont));
            doc.add(Chunk.NEWLINE);

            if (chartImage != null && chartImage.length > 0) {
                try {
                    com.lowagie.text.Image img = com.lowagie.text.Image.getInstance(chartImage);
                    float w = img.getWidth();
                    float h = img.getHeight();
                    float maxW = doc.getPageSize().getWidth() - 72;
                    if (w > maxW) {
                        float ratio = maxW / w;
                        img.scaleToFit(maxW, h * ratio);
                    }
                    img.setAlignment(com.lowagie.text.Image.ALIGN_CENTER);
                    doc.add(img);
                } catch (Exception ignored) {}
                doc.add(Chunk.NEWLINE);
            }

            PdfPTable kpiTable = new PdfPTable(4);
            kpiTable.setWidthPercentage(100);
            kpiTable.setSpacingBefore(6);
            kpiTable.setSpacingAfter(12);
            float[] colWidths = {25f, 25f, 25f, 25f};
            kpiTable.setWidths(colWidths);

            addKpiCell(kpiTable, "$" + formato(ventasMes), "VENTAS DEL MES");
            addKpiCell(kpiTable, facturasMes != null ? facturasMes.toString() : "0", "FACTURAS EMITIDAS");
            addKpiCell(kpiTable, "$" + formato(ventasDia), "VENTAS DEL DIA");
            addKpiCell(kpiTable, "$" + formato(ticketPromedio), "TICKET PROMEDIO");
            doc.add(kpiTable);

            doc.add(new Paragraph("TOP PRODUCTOS", sectionFont));
            doc.add(Chunk.NEWLINE);

            PdfPTable prodTable = new PdfPTable(2);
            prodTable.setWidthPercentage(100);
            prodTable.setWidths(new float[]{7f, 3f});

            PdfPCell h1 = new PdfPCell(new Phrase("Producto", boldFont));
            h1.setBackgroundColor(ACCENT);
            h1.setPadding(6);
            h1.setBorderColor(Color.WHITE);
            PdfPCell h2 = new PdfPCell(new Phrase("Vendidos", boldFont));
            h2.setBackgroundColor(ACCENT);
            h2.setPadding(6);
            h2.setBorderColor(Color.WHITE);
            prodTable.addCell(h1);
            prodTable.addCell(h2);

            if (topProductos != null && !topProductos.isEmpty()) {
                for (int i = 0; i < topProductos.size(); i++) {
                    Map<String, Object> p = topProductos.get(i);
                    PdfPCell cn = new PdfPCell(new Phrase(String.valueOf(p.get("nombre")), normalFont));
                    cn.setPadding(5);
                    if (i % 2 == 1) cn.setBackgroundColor(BG_LIGHT);
                    PdfPCell cc = new PdfPCell(new Phrase(String.valueOf(p.get("cantidad")), boldFont));
                    cc.setPadding(5);
                    cc.setHorizontalAlignment(Element.ALIGN_CENTER);
                    if (i % 2 == 1) cc.setBackgroundColor(BG_LIGHT);
                    prodTable.addCell(cn);
                    prodTable.addCell(cc);
                }
            } else {
                PdfPCell nc = new PdfPCell(new Phrase("Sin datos", normalFont));
                nc.setColspan(2);
                nc.setPadding(5);
                nc.setHorizontalAlignment(Element.ALIGN_CENTER);
                prodTable.addCell(nc);
            }
            doc.add(prodTable);

            doc.add(Chunk.NEWLINE);
            double meta = 6400000;
            double actual = ventasMes != null ? ventasMes.doubleValue() : 0;
            int pct = (int) Math.min(100, (actual / meta) * 100);
            double faltan = Math.max(0, meta - actual);
            doc.add(new Paragraph("META DE VENTAS", sectionFont));
            doc.add(new Paragraph("$ " + formato(BigDecimal.valueOf(actual)) + " / $ " + formato(BigDecimal.valueOf(meta)) + " — " + pct + "%", normalFont));
            doc.add(new Paragraph("Faltan $ " + formato(BigDecimal.valueOf(faltan)) + " para la meta", normalFont));

            doc.close();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF de reporte", e);
        }
        return baos.toByteArray();
    }

    // Agrega una celda KPI a la tabla del reporte
    private void addKpiCell(PdfPTable table, String value, String label) {
        PdfPCell cell = new PdfPCell();
        cell.setPadding(8);
        cell.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
        Paragraph vp = new Paragraph(value, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, DARK));
        vp.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(vp);
        Paragraph lp = new Paragraph(label, FontFactory.getFont(FontFactory.HELVETICA, 7, MUTED));
        lp.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(lp);
        table.addCell(cell);
    }

    // Formatea un BigDecimal como moneda sin decimales
    private String formato(BigDecimal value) {
        if (value == null) return "0";
        return String.format("%,.0f", value);
    }
}
