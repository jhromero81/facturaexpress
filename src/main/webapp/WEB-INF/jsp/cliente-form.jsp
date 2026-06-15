<%-- Formulario para crear o editar un cliente --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.codewise.facturaexpress.model.Cliente" %>
<%@ include file="/WEB-INF/jsp/layout/header.jsp" %>
<%-- Scriptlet: determina si estamos en modo edición o creación --%>
<%
  Cliente cliente = (Cliente) request.getAttribute("cliente");
  boolean editando = cliente != null && cliente.getId() != null;
  String error = (String) request.getAttribute("error");
%>

<%-- Muestra error si existe --%>
<% if (error != null) { %>
  <div style="color:#e74c3c;background:rgba(231,76,60,0.1);padding:12px 16px;border-radius:8px;margin-bottom:16px;font-size:13px;"><i class="material-icons" style="font-size:16px;vertical-align:middle;">error_outline</i> <%= error %></div>
<% } %>

<div class="form-card">
  <div style="font-family:'Space Mono',monospace;font-size:18px;font-weight:700;color:var(--text-dark);margin-bottom:24px;">
    <%= editando ? "Editar Cliente" : "Nuevo Cliente" %>
  </div>

  <%-- Formulario: envía vía POST a /clientes con acción "guardar" o "actualizar" --%>
  <form action="<%= ctx %>/clientes" method="post">
    <input type="hidden" name="action" value="<%= editando ? "actualizar" : "guardar" %>">
    <% if (editando) { %>
      <%-- Campo oculto con el ID del cliente a editar --%>
      <input type="hidden" name="id" value="<%= cliente.getId() %>">
    <% } %>

    <div class="input-field">
      <i class="material-icons prefix">person</i>
      <input id="nombre" name="nombre" type="text" class="validate" required value="<%= editando ? cliente.getNombre() : "" %>">
      <label for="nombre">Nombre *</label>
    </div>

    <div class="input-field">
      <i class="material-icons prefix">email</i>
      <input id="email" name="email" type="email" class="validate" value="<%= editando && cliente.getEmail() != null ? cliente.getEmail() : "" %>">
      <label for="email">Email</label>
    </div>

    <div class="input-field">
      <i class="material-icons prefix">phone</i>
      <input id="telefono" name="telefono" type="text" class="validate" value="<%= editando && cliente.getTelefono() != null ? cliente.getTelefono() : "" %>">
      <label for="telefono">Tel&eacute;fono</label>
    </div>

    <div class="input-field">
      <i class="material-icons prefix">location_on</i>
      <textarea id="direccion" name="direccion" class="materialize-textarea"><%= editando && cliente.getDireccion() != null ? cliente.getDireccion() : "" %></textarea>
      <label for="direccion">Direcci&oacute;n</label>
    </div>

    <div class="form-actions">
      <button type="submit" class="btn btn-teal waves-effect waves-light"><i class="material-icons left">save</i><%= editando ? "Actualizar Cliente" : "Guardar Cliente" %></button>
      <a href="<%= ctx %>/clientes" class="btn btn-cancel-compact waves-effect" style="display:inline-flex;align-items:center;gap:8px;height:38px;line-height:38px;padding:0 20px;border-radius:10px;text-decoration:none;font-size:13px;font-weight:600;">Cancelar</a>
    </div>
  </form>
</div>

<%@ include file="/WEB-INF/jsp/layout/footer.jsp" %>
