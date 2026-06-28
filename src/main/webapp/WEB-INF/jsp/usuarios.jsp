<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, com.codewise.facturaexpress.model.Usuario" %>
<%@ include file="/WEB-INF/jsp/layout/header.jsp" %>
<%
  List<Usuario> usuarios = (List<Usuario>) request.getAttribute("usuarios");
  String error = (String) request.getAttribute("error");
%>
<% if (error != null) { %>
  <div style="color:#e74c3c;background:rgba(231,76,60,0.1);padding:12px 16px;border-radius:8px;margin-bottom:16px;font-size:13px;"><i class="material-icons" style="font-size:16px;vertical-align:middle;">error_outline</i> <%= error %></div>
<% } %>

<div style="display:flex;align-items:center;justify-content:space-between;gap:12px;margin-bottom:20px;flex-wrap:wrap;">
  <div style="font-family:'Space Mono',monospace;font-size:18px;font-weight:700;color:var(--text-dark);">Usuarios del Sistema</div>
  <a class="btn btn-dark waves-effect waves-light" href="<%= ctx %>/usuarios?action=nuevo">
    <i class="material-icons left">person_add</i>NUEVO USUARIO
  </a>
</div>

<div class="content-card">
  <table class="highlight">
    <thead>
      <tr>
        <th>ID</th>
        <th>Username</th>
        <th>Nombre</th>
        <th>Email</th>
        <th>Rol</th>
        <th>Activo</th>
        <th style="width:180px;">Acciones</th>
      </tr>
    </thead>
    <tbody>
<%
  if (usuarios != null) {
    for (Usuario u : usuarios) {
%>
      <tr>
        <td><%= u.getId() %></td>
        <td><%= u.getUsername() %></td>
        <td><%= u.getNombre() != null ? u.getNombre() : "" %></td>
        <td><%= u.getEmail() != null ? u.getEmail() : "" %></td>
        <td><span class="badge" style="background:rgba(26,188,156,0.15);color:#1abc9c;border-radius:6px;padding:3px 10px;font-size:11px;font-weight:600;"><%= u.getRol() %></span></td>
        <td>
          <form action="<%= ctx %>/usuarios" method="post" style="display:inline;">
            <input type="hidden" name="_csrf_token" value="<%= request.getAttribute("csrfToken") %>">
            <input type="hidden" name="action" value="toggleActivo">
            <input type="hidden" name="id" value="<%= u.getId() %>">
            <input type="hidden" name="activo" value="<%= !u.isActivo() %>">
            <button type="submit" style="border:none;background:none;cursor:pointer;color:<%= u.isActivo() ? "#27ae60" : "#e74c3c" %>;font-weight:600;"><%= u.isActivo() ? "ACTIVO" : "INACTIVO" %></button>
          </form>
        </td>
        <td>
          <a class="btn btn-teal waves-effect waves-light" style="height:32px;line-height:32px;padding:0 12px;font-size:11px;" href="<%= ctx %>/usuarios?action=editar&id=<%= u.getId() %>">
            <i class="material-icons" style="font-size:14px;">edit</i> Editar
          </a>
          <form action="<%= ctx %>/usuarios" method="post" style="display:inline;" onsubmit="return confirm('Eliminar usuario <%= u.getUsername() %>?')">
            <input type="hidden" name="_csrf_token" value="<%= request.getAttribute("csrfToken") %>">
            <input type="hidden" name="action" value="eliminar">
            <input type="hidden" name="id" value="<%= u.getId() %>">
            <button type="submit" class="btn" style="height:32px;line-height:32px;padding:0 12px;font-size:11px;background:#e74c3c;color:white;border-radius:8px;border:none;cursor:pointer;">
              <i class="material-icons" style="font-size:14px;">delete</i>
            </button>
          </form>
        </td>
      </tr>
<%
    }
  }
%>
    </tbody>
  </table>
  <% if (usuarios == null || usuarios.isEmpty()) { %>
    <div style="text-align:center;padding:40px;color:var(--text-muted);">
      <i class="material-icons" style="font-size:48px;">group</i>
      <p style="margin-top:12px;">No hay usuarios registrados</p>
    </div>
  <% } %>
</div>

<%@ include file="/WEB-INF/jsp/layout/footer.jsp" %>
