<%-- Listado de clientes con búsqueda en vivo y CRUD --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, com.codewise.facturaexpress.model.Cliente" %>
<%@ include file="/WEB-INF/jsp/layout/header.jsp" %>
<%-- Scriptlet: obtiene lista de clientes y mensajes --%>
<%
  List<Cliente> clientes = (List<Cliente>) request.getAttribute("clientes");
  String mensaje = (String) request.getAttribute("mensaje");
  String error = (String) request.getAttribute("error");
%>

<%-- Muestra mensaje de éxito si existe --%>
<% if (mensaje != null) { %>
  <div style="color:#27ae60;background:rgba(39,174,96,0.1);padding:12px 16px;border-radius:8px;margin-bottom:16px;font-size:13px;font-weight:500;"><i class="material-icons" style="font-size:16px;vertical-align:middle;">check_circle</i> <%= mensaje %></div>
<% } %>
<%-- Muestra error si existe --%>
<% if (error != null) { %>
  <div style="color:#e74c3c;background:rgba(231,76,60,0.1);padding:12px 16px;border-radius:8px;margin-bottom:16px;font-size:13px;font-weight:500;"><i class="material-icons" style="font-size:16px;vertical-align:middle;">error_outline</i> <%= error %></div>
<% } %>

<div style="display:flex;align-items:center;justify-content:space-between;gap:12px;margin-bottom:20px;flex-wrap:wrap;">
  <div class="input-field" style="margin:0;flex:1;max-width:340px;">
    <i class="material-icons prefix" style="color:#90a4ae;">search</i>
    <input id="bCliente" type="text" oninput="filtrarClientes(this.value)" placeholder="Buscar ID o Nombre...">
    <label for="bCliente">B&uacute;squeda</label>
  </div>
  <%-- Botón para crear un nuevo cliente --%>
  <a class="btn btn-dark waves-effect waves-light" href="<%= ctx %>/clientes?action=nuevo">
    <i class="material-icons left">person_add</i>NUEVO CLIENTE
  </a>
</div>

<%-- Estado vacío si no hay clientes --%>
<% if (clientes == null || clientes.isEmpty()) { %>
  <div class="content-card" style="text-align:center;padding:40px;">
    <i class="material-icons" style="font-size:48px;color:var(--text-muted);">group</i>
    <p style="color:var(--text-muted);margin:16px 0;">No hay clientes registrados</p>
    <a href="<%= ctx %>/clientes?action=nuevo" class="btn btn-teal waves-effect waves-light"><i class="material-icons left">person_add</i>Registrar primer cliente</a>
  </div>
<% } else { %>
  <%-- Grid de tarjetas de clientes --%>
  <div class="row" id="clientesGrid">
    <%-- Iteración sobre la lista de clientes --%>
    <% for (Cliente c : clientes) { %>
      <div class="col s12 m6 l4" style="margin-bottom:18px;" data-nombre="<%= c.getNombre().toLowerCase() %>" data-id="<%= c.getId() %>">
        <div class="client-card">
          <div style="display:flex;justify-content:space-between;align-items:flex-start;">
            <i class="material-icons" style="font-size:34px;color:#90a4ae;">account_circle</i>
            <span class="client-id-tag">ID: <%= c.getId() %></span>
          </div>
          <div class="client-name"><%= c.getNombre() %></div>
          <div class="client-email"><%= c.getEmail() != null ? c.getEmail() : "Sin email" %></div>
          <div style="font-size:11px;color:var(--text-muted);margin-bottom:14px;">
            <%= c.getTelefono() != null ? c.getTelefono() : "" %><%= c.getDireccion() != null ? " &middot; " + c.getDireccion() : "" %>
          </div>
          <div class="client-actions">
            <a class="btn-flat" style="color:#1a2535;font-weight:700;padding:0;" href="<%= ctx %>/clientes?action=editar&id=<%= c.getId() %>">Editar</a>
            <a class="btn-flat" style="color:#e74c3c;font-weight:700;padding:0;" href="<%= ctx %>/clientes?action=eliminar&id=<%= c.getId() %>" onclick="return confirm('¿Confirma que desea eliminar este cliente?')">Eliminar</a>
          </div>
        </div>
      </div>
    <% } %>
  </div>
<% } %>

<%-- JS: filtro en vivo de clientes por nombre o ID --%>
<script>
function filtrarClientes(valor) {
  var termino = valor.toLowerCase();
  document.querySelectorAll('#clientesGrid > .col').forEach(function(col) {
    var nombre = col.getAttribute('data-nombre') || '';
    var id = col.getAttribute('data-id') || '';
    col.style.display = (nombre.includes(termino) || id.includes(termino)) ? '' : 'none';
  });
}
</script>

<script src="<%= ctx %>/js/clientes.js"></script>
<%@ include file="/WEB-INF/jsp/layout/footer.jsp" %>
