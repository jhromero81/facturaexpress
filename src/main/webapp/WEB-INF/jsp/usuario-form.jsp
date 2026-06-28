<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.codewise.facturaexpress.model.Usuario" %>
<%@ include file="/WEB-INF/jsp/layout/header.jsp" %>
<%
  Usuario usuario = (Usuario) request.getAttribute("usuario");
  boolean editando = usuario != null && usuario.getId() != null;
  String error = (String) request.getAttribute("error");
%>
<% if (error != null) { %>
  <div style="color:#e74c3c;background:rgba(231,76,60,0.1);padding:12px 16px;border-radius:8px;margin-bottom:16px;font-size:13px;"><i class="material-icons" style="font-size:16px;vertical-align:middle;">error_outline</i> <%= error %></div>
<% } %>

<div class="form-card">
  <div style="font-family:'Space Mono',monospace;font-size:18px;font-weight:700;color:var(--text-dark);margin-bottom:24px;">
    <%= editando ? "Editar Usuario" : "Nuevo Usuario" %>
  </div>

  <form action="<%= ctx %>/usuarios" method="post">
    <input type="hidden" name="_csrf_token" value="<%= request.getAttribute("csrfToken") %>">
    <input type="hidden" name="action" value="guardar">
    <% if (editando) { %>
      <input type="hidden" name="id" value="<%= usuario.getId() %>">
    <% } %>

    <div class="input-field">
      <i class="material-icons prefix">person</i>
      <input id="username" name="username" type="text" class="validate" required value="<%= editando ? usuario.getUsername() : "" %>">
      <label for="username">Username *</label>
    </div>

    <div class="input-field">
      <i class="material-icons prefix">badge</i>
      <input id="nombre" name="nombre" type="text" class="validate" required value="<%= editando ? usuario.getNombre() : "" %>">
      <label for="nombre">Nombre Completo *</label>
    </div>

    <div class="input-field">
      <i class="material-icons prefix">email</i>
      <input id="email" name="email" type="email" class="validate" value="<%= editando && usuario.getEmail() != null ? usuario.getEmail() : "" %>">
      <label for="email">Email</label>
    </div>

    <div class="input-field">
      <i class="material-icons prefix">lock</i>
      <input id="password" name="password" type="password" class="validate" <%= editando ? "" : "required" %>>
      <label for="password"><%= editando ? "Nueva Contrase&ntilde;a (dejar vac&iacute;o para mantener)" : "Contrase&ntilde;a *" %></label>
    </div>

    <div class="input-field">
      <i class="material-icons prefix">admin_panel_settings</i>
      <select id="rol" name="rol" class="browser-default" style="display:block;width:100%;padding:10px;border:1px solid #ccc;border-radius:8px;">
        <option value="VENDEDOR" <%= editando && "VENDEDOR".equals(usuario.getRol()) ? "selected" : "" %>>Vendedor</option>
        <option value="ADMIN" <%= editando && "ADMIN".equals(usuario.getRol()) ? "selected" : "" %>>Administrador</option>
        <option value="CONTADOR" <%= editando && "CONTADOR".equals(usuario.getRol()) ? "selected" : "" %>>Contador</option>
      </select>
      <label for="rol" style="position:relative;top:-8px;font-size:12px;">Rol</label>
    </div>

    <div class="form-actions">
      <button type="submit" class="btn btn-teal waves-effect waves-light"><i class="material-icons left">save</i><%= editando ? "Actualizar Usuario" : "Crear Usuario" %></button>
      <a href="<%= ctx %>/usuarios" class="btn btn-cancel-compact waves-effect" style="display:inline-flex;align-items:center;gap:8px;height:38px;line-height:38px;padding:0 20px;border-radius:10px;text-decoration:none;font-size:13px;font-weight:600;">Cancelar</a>
    </div>
  </form>
</div>

<%@ include file="/WEB-INF/jsp/layout/footer.jsp" %>
