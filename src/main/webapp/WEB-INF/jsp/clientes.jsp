<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, com.codewise.facturaexpress.model.Cliente" %>
<%@ include file="/WEB-INF/jsp/layout/header.jsp" %>
<%
  List<Cliente> clientes = (List<Cliente>) request.getAttribute("clientes");
  String mensaje = (String) request.getAttribute("mensaje");
  String error = (String) request.getAttribute("error");
%>

<% if (mensaje != null) { %>
  <div style="color:#27ae60;background:rgba(39,174,96,0.1);padding:12px 16px;border-radius:8px;margin-bottom:16px;font-size:13px;font-weight:500;"><i class="material-icons" style="font-size:16px;vertical-align:middle;">check_circle</i> <%= mensaje %></div>
<% } %>
<% if (error != null) { %>
  <div style="color:#e74c3c;background:rgba(231,76,60,0.1);padding:12px 16px;border-radius:8px;margin-bottom:16px;font-size:13px;font-weight:500;"><i class="material-icons" style="font-size:16px;vertical-align:middle;">error_outline</i> <%= error %></div>
<% } %>

<div style="display:flex;align-items:center;justify-content:space-between;gap:12px;margin-bottom:20px;flex-wrap:wrap;">
  <div class="input-field" style="margin:0;flex:1;max-width:340px;">
    <i class="material-icons prefix" style="color:#90a4ae;">search</i>
    <input id="bCliente" type="text" oninput="filtrarClientes(this.value)" placeholder="Buscar ID o Nombre...">
    <label for="bCliente">B&uacute;squeda</label>
  </div>
  <a class="btn btn-dark waves-effect waves-light" onclick="document.getElementById('modalOverlay').classList.add('open')">
    <i class="material-icons left">person_add</i>NUEVO CLIENTE
  </a>
</div>

<% if (clientes == null || clientes.isEmpty()) { %>
  <div class="content-card" style="text-align:center;padding:40px;">
    <i class="material-icons" style="font-size:48px;color:var(--text-muted);">group</i>
    <p style="color:var(--text-muted);margin:16px 0;">No hay clientes registrados</p>
    <a onclick="document.getElementById('modalOverlay').classList.add('open')" class="btn btn-teal waves-effect waves-light"><i class="material-icons left">person_add</i>Registrar primer cliente</a>
  </div>
<% } else { %>
  <div class="row" id="clientesGrid">
    <% for (Cliente c : clientes) { %>
      <div class="col s12 m6 l4" style="margin-bottom:18px;" data-nombre="<%= c.getNombre().toLowerCase() %>" data-id="<%= String.valueOf(c.getId()) %>">
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
            <a class="btn-flat" style="color:#1a2535;font-weight:700;padding:0;" href="<%= ctx %>/clientes?action=editar&id=<%= c.getId() %>">Ver Perfil</a>
            <a class="btn-flat" style="color:#e74c3c;font-weight:700;padding:0;" href="<%= ctx %>/clientes?action=eliminar&id=<%= c.getId() %>" onclick="return confirm('&iquest;Confirma que desea eliminar este cliente?')">Eliminar</a>
          </div>
        </div>
      </div>
    <% } %>
  </div>
<% } %>

<div class="modal-overlay" id="modalOverlay">
  <form action="<%= ctx %>/clientes?action=guardar" method="post">
    <input type="hidden" name="_csrf_token" value="<%= request.getAttribute("csrfToken") %>">
    <div class="modal-box">
      <div class="modal-title">Nuevo Cliente
        <i class="material-icons modal-close" onclick="document.getElementById('modalOverlay').classList.remove('open')">close</i>
      </div>
      <div class="input-field"><input id="mId" name="id" type="text" class="validate"><label for="mId">N&uacute;mero de Identificaci&oacute;n</label></div>
      <div class="input-field"><input id="mNombre" name="nombre" type="text" class="validate"><label for="mNombre">Nombre Completo</label></div>
      <div class="input-field"><input id="mEmail" name="email" type="email" class="validate"><label for="mEmail">Correo Electr&oacute;nico</label></div>
      <div class="input-field"><input id="mTel" name="telefono" type="text" class="validate"><label for="mTel">Tel&eacute;fono</label></div>
      <div class="input-field"><input id="mDir" name="direccion" type="text" class="validate"><label for="mDir">Direcci&oacute;n</label></div>
      <div style="display:flex;justify-content:flex-end;gap:10px;margin-top:10px;">
        <a class="btn-flat" onclick="document.getElementById('modalOverlay').classList.remove('open')">Cancelar</a>
        <button type="submit" class="btn btn-teal waves-effect waves-light"><i class="material-icons left">save</i>Guardar</button>
      </div>
    </div>
  </form>
</div>

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
