<%-- Listado de facturas electrónicas con búsqueda en vivo --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, com.codewise.facturaexpress.model.Factura, java.time.format.DateTimeFormatter" %>
<%@ include file="/WEB-INF/jsp/layout/header.jsp" %>
<%-- Scriptlet: obtiene lista de facturas y mensajes --%>
<%
  List<Factura> facturas = (List<Factura>) request.getAttribute("facturas");
  String mensaje = (String) request.getAttribute("mensaje");
  String error = (String) request.getAttribute("error");
%>

<%-- Muestra mensaje de éxito si existe --%>
<% if (mensaje != null) { %>
  <div style="color:#27ae60;background:rgba(39,174,96,0.1);padding:12px 16px;border-radius:8px;margin-bottom:16px;font-size:13px;"><i class="material-icons" style="font-size:16px;vertical-align:middle;">check_circle</i> <%= mensaje %></div>
<% } %>
<%-- Muestra error si existe --%>
<% if (error != null) { %>
  <div style="color:#e74c3c;background:rgba(231,76,60,0.1);padding:12px 16px;border-radius:8px;margin-bottom:16px;font-size:13px;"><i class="material-icons" style="font-size:16px;vertical-align:middle;">error_outline</i> <%= error %></div>
<% } %>

<div class="content-card">
  <div style="display:flex;align-items:center;gap:12px;flex-wrap:wrap;margin-bottom:16px;">
  <div class="input-field" style="margin:0;flex:1;min-width:220px;">
    <i class="material-icons prefix" style="color:#90a4ae;">search</i>
    <input id="bFac" type="text" oninput="filtrarFacturas(this.value)" placeholder="Buscar factura o cliente...">
    <label for="bFac">B&uacute;squeda</label>
  </div>
  <%-- Botón para crear una nueva factura --%>
    <a class="btn btn-dark waves-effect waves-light" href="<%= ctx %>/facturas?action=nuevo">
      <i class="material-icons left">add</i>Nueva Factura
    </a>
  </div>

<%-- Estado vacío si no hay facturas --%>
  <% if (facturas == null || facturas.isEmpty()) { %>
    <div style="text-align:center;padding:40px;">
      <i class="material-icons" style="font-size:48px;color:var(--text-muted);">receipt_long</i>
      <p style="color:var(--text-muted);margin:16px 0;">No hay facturas registradas</p>
      <a href="<%= ctx %>/facturas?action=nuevo" class="btn btn-teal waves-effect waves-light"><i class="material-icons left">add</i>Crear primera factura</a>
    </div>
  <% } else { %>
    <table class="striped highlight responsive-table">
      <thead>
        <tr>
          <th>No. Factura</th>
          <th>Fecha de Emisi&oacute;n</th>
          <th>Cliente</th>
          <th>Total</th>
          <th>Acciones</th>
        </tr>
      </thead>
      <tbody id="facturasBody">
        <%-- Iteración sobre la lista de facturas --%>
        <% for (Factura f : facturas) { %>
          <tr data-search="<%= f.getId() %> <%= f.getClienteNombre() != null ? f.getClienteNombre().toLowerCase() : "" %>">
            <td><strong>FAC-<%= String.format("%06d", f.getId()) %></strong></td>
            <td><%= f.getFecha() != null ? f.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "-" %></td>
            <td><%= f.getClienteNombre() != null ? f.getClienteNombre() : "ID: " + f.getClienteId() %></td>
            <td style="font-family:'Space Mono',monospace;font-weight:700;">$ <%= String.format("%,.0f", f.getTotal()) %></td>
            <td>
              <a class="btn-flat btn-small" style="color:#546e7a;" href="<%= ctx %>/facturas?action=detalle&id=<%= f.getId() %>"><i class="material-icons tiny">visibility</i> Ver</a>
              <a class="btn-flat btn-small" style="color:#e74c3c;" href="<%= ctx %>/facturas?action=eliminar&id=<%= f.getId() %>" onclick="return confirm('¿Confirma que desea eliminar esta factura?')"><i class="material-icons tiny">delete</i></a>
            </td>
          </tr>
        <% } %>
      </tbody>
    </table>
  <% } %>
</div>

<%-- JS: filtro en vivo de facturas por ID o nombre de cliente --%>
<script>
function filtrarFacturas(valor) {
  var termino = valor.toLowerCase();
  document.querySelectorAll('#facturasBody tr').forEach(function(row) {
    var searchData = row.getAttribute('data-search') || '';
    row.style.display = searchData.includes(termino) ? '' : 'none';
  });
}
</script>

<%@ include file="/WEB-INF/jsp/layout/footer.jsp" %>
