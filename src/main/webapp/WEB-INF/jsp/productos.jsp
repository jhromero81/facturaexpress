<%-- Listado de productos con búsqueda en vivo y CRUD --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, com.codewise.facturaexpress.model.Producto" %>
<%@ include file="/WEB-INF/jsp/layout/header.jsp" %>
<%-- Scriptlet: obtiene lista de productos y mensajes --%>
<%
  List<Producto> productos = (List<Producto>) request.getAttribute("productos");
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
    <input id="bProd" type="text" oninput="filtrarProductos(this.value)" placeholder="Buscar producto...">
    <label for="bProd">B&uacute;squeda</label>
  </div>
  <%-- Botón para crear un nuevo producto --%>
    <a class="btn btn-dark waves-effect waves-light" href="<%= ctx %>/productos?action=nuevo">
      <i class="material-icons left">add</i>Nuevo Producto
    </a>
  </div>

<%-- Estado vacío si no hay productos --%>
  <% if (productos == null || productos.isEmpty()) { %>
    <div style="text-align:center;padding:40px;">
      <i class="material-icons" style="font-size:48px;color:var(--text-muted);">inventory_2</i>
      <p style="color:var(--text-muted);margin:16px 0;">No hay productos registrados</p>
      <a href="<%= ctx %>/productos?action=nuevo" class="btn btn-teal waves-effect waves-light"><i class="material-icons left">add</i>Registrar primer producto</a>
    </div>
  <% } else { %>
    <table class="striped highlight responsive-table">
      <thead>
        <tr>
          <th>ID</th>
          <th>Nombre</th>
          <th>Descripci&oacute;n</th>
          <th>Precio</th>
          <th>Stock</th>
          <th>Acciones</th>
        </tr>
      </thead>
      <tbody id="productosBody">
        <%-- Iteración sobre la lista de productos --%>
        <% for (Producto p : productos) { %>
          <tr data-search="<%= p.getId() %> <%= p.getNombre().toLowerCase() %>">
            <td><%= p.getId() %></td>
            <td><strong><%= p.getNombre() %></strong></td>
            <td><%= p.getDescripcion() != null ? p.getDescripcion() : "-" %></td>
            <td style="font-family:'Space Mono',monospace;font-weight:700;">$ <%= String.format("%,.0f", p.getPrecio()) %></td>
            <td><%= p.getStock() %></td>
            <td>
              <a class="btn-flat btn-small" style="color:#1a2535;font-weight:700;padding:0 8px;" href="<%= ctx %>/productos?action=editar&id=<%= p.getId() %>">Editar</a>
              <a class="btn-flat btn-small" style="color:#e74c3c;font-weight:700;padding:0 8px;" href="<%= ctx %>/productos?action=eliminar&id=<%= p.getId() %>" onclick="return confirm('¿Confirma que desea eliminar este producto?')">Eliminar</a>
            </td>
          </tr>
        <% } %>
      </tbody>
    </table>
  <% } %>
</div>

<%-- JS: filtro en vivo de productos por ID o nombre --%>
<script>
function filtrarProductos(valor) {
  var termino = valor.toLowerCase();
  document.querySelectorAll('#productosBody tr').forEach(function(row) {
    var searchData = row.getAttribute('data-search') || '';
    row.style.display = searchData.includes(termino) ? '' : 'none';
  });
}
</script>

<script src="<%= ctx %>/js/productos.js"></script>
<%@ include file="/WEB-INF/jsp/layout/footer.jsp" %>
