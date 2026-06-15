<%-- Vista de detalle de una factura individual --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.codewise.facturaexpress.model.Factura, com.codewise.facturaexpress.model.DetalleFactura, java.time.format.DateTimeFormatter" %>
<%@ include file="/WEB-INF/jsp/layout/header.jsp" %>
<%-- Scriptlet: obtiene la factura completa con sus detalles --%>
<%
  Factura factura = (Factura) request.getAttribute("factura");
  String error = (String) request.getAttribute("error");
%>

<%-- Muestra error si existe --%>
<% if (error != null) { %>
  <div style="color:#e74c3c;background:rgba(231,76,60,0.1);padding:12px 16px;border-radius:8px;margin-bottom:16px;font-size:13px;"><i class="material-icons" style="font-size:16px;vertical-align:middle;">error_outline</i> <%= error %></div>
<% } %>

<% if (factura != null) { %>
  <div class="content-card">
    <%-- Encabezado: número de factura, fecha, estado y botón volver --%>
    <div style="display:flex;justify-content:space-between;align-items:center;flex-wrap:wrap;gap:12px;margin-bottom:24px;">
      <div>
        <div style="font-family:'Space Mono',monospace;font-size:20px;font-weight:700;color:var(--text-dark);">Factura #<%= factura.getId() %></div>
        <div style="font-size:12px;color:var(--text-muted);"><%= factura.getFecha() != null ? factura.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "" %></div>
      </div>
      <div style="display:flex;align-items:center;gap:8px;">
        <% String estado = factura.getEstado() != null ? factura.getEstado() : "PENDIENTE"; %>
        <span class="badge-status badge-sent" style="font-size:12px;padding:4px 14px;"><i class="material-icons" style="font-size:14px;vertical-align:middle;">check_circle</i> <%= estado %></span>
        <a href="<%= ctx %>/facturas" class="btn btn-dark waves-effect waves-light" style="height:34px;line-height:34px;padding:0 16px;font-size:12px;border-radius:8px;"><i class="material-icons left" style="font-size:16px;">arrow_back</i>Volver</a>
      </div>
    </div>

    <%-- Resumen: cliente y total --%>
    <div style="display:flex;gap:16px;flex-wrap:wrap;margin-bottom:24px;padding:16px;background:var(--bg-main);border-radius:8px;">
      <div class="detail-item"><div class="detail-label">Cliente</div><div class="detail-value"><%= factura.getClienteNombre() != null ? factura.getClienteNombre() : "ID: " + factura.getClienteId() %></div></div>
      <div class="detail-item"><div class="detail-label">Total</div><div class="detail-value" style="color:var(--accent);">$ <%= String.format("%,.0f", factura.getTotal()) %></div></div>
    </div>

    <div style="font-family:'Space Mono',monospace;font-size:14px;font-weight:700;color:var(--text-dark);margin-bottom:12px;">Detalles</div>
    <table class="striped highlight responsive-table">
      <thead>
        <tr><th>#</th><th>Producto</th><th>Cantidad</th><th>Precio Unitario</th><th>Subtotal</th></tr>
      </thead>
      <tbody>
        <%-- Iteración sobre los detalles de la factura --%>
        <% if (factura.getDetalles() != null) { int i = 1; for (DetalleFactura d : factura.getDetalles()) { %>
          <tr>
            <td><%= i++ %></td>
            <td><strong><%= d.getProductoNombre() != null ? d.getProductoNombre() : "ID: " + d.getProductoId() %></strong></td>
            <td><%= d.getCantidad() %></td>
            <td style="font-family:'Space Mono',monospace;">$ <%= String.format("%,.0f", d.getPrecioUnitario()) %></td>
            <td style="font-family:'Space Mono',monospace;font-weight:700;">$ <%= String.format("%,.0f", d.getSubtotal()) %></td>
          </tr>
        <% } } %>
      </tbody>
    </table>

    <div style="text-align:right;margin-top:16px;padding-top:16px;border-top:2px solid var(--border);">
      <span style="font-size:11px;color:var(--text-muted);text-transform:uppercase;letter-spacing:.5px;">Total:</span>
      <span style="font-family:'Space Mono',monospace;font-size:22px;font-weight:700;color:var(--accent);margin-left:12px;">$ <%= String.format("%,.0f", factura.getTotal()) %></span>
    </div>
  </div>
<% } %>

<%@ include file="/WEB-INF/jsp/layout/footer.jsp" %>
