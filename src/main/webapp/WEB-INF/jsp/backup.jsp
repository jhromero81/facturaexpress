<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.io.File" %>
<%@ include file="/WEB-INF/jsp/layout/header.jsp" %>
<%
  String success = request.getParameter("success");
  String errorMsg = (String) request.getAttribute("error");
  File[] backups = (File[]) request.getAttribute("backups");
%>
<% if (errorMsg != null) { %>
  <div style="color:#e74c3c;background:rgba(231,76,60,0.1);padding:12px 16px;border-radius:8px;margin-bottom:16px;font-size:13px;"><i class="material-icons" style="font-size:16px;vertical-align:middle;">error_outline</i> <%= errorMsg %></div>
<% } %>
<% if ("ok".equals(success)) { %>
  <div style="color:#27ae60;background:rgba(39,174,96,0.1);padding:12px 16px;border-radius:8px;margin-bottom:16px;font-size:13px;"><i class="material-icons" style="font-size:16px;vertical-align:middle;">check_circle</i> Respaldo creado exitosamente</div>
<% } %>
<% if ("restore".equals(success)) { %>
  <div style="color:#27ae60;background:rgba(39,174,96,0.1);padding:12px 16px;border-radius:8px;margin-bottom:16px;font-size:13px;"><i class="material-icons" style="font-size:16px;vertical-align:middle;">check_circle</i> Base de datos restaurada exitosamente</div>
<% } %>

<div style="display:flex;align-items:center;justify-content:space-between;gap:12px;margin-bottom:20px;flex-wrap:wrap;">
  <div style="font-family:'Space Mono',monospace;font-size:18px;font-weight:700;color:var(--text-dark);">Respaldos de Base de Datos</div>
  <div style="display:flex;gap:8px;">
    <form action="<%= ctx %>/backup" method="post" style="display:inline;">
      <input type="hidden" name="_csrf_token" value="<%= request.getAttribute("csrfToken") %>">
      <input type="hidden" name="action" value="crear">
      <button type="submit" class="btn btn-dark waves-effect waves-light"><i class="material-icons left">backup</i>CREAR RESPALDO</button>
    </form>
  </div>
</div>

<div class="row">
  <div class="col s12 l6">
    <div class="content-card">
      <div class="card-title-sm">Respaldos Disponibles</div>
      <% if (backups != null && backups.length > 0) { %>
        <table class="highlight">
          <thead>
            <tr>
              <th>Archivo</th>
              <th>Tama&ntilde;o</th>
              <th style="width:160px;">Acciones</th>
            </tr>
          </thead>
          <tbody>
<%
  for (File f : backups) {
    String name = f.getName();
    long sizeKb = f.length() / 1024;
%>
            <tr>
              <td><%= name %></td>
              <td><%= sizeKb %> KB</td>
              <td>
                <a class="btn btn-teal waves-effect waves-light" style="height:30px;line-height:30px;padding:0 10px;font-size:10px;" href="<%= ctx %>/backup?action=download&file=<%= name %>"><i class="material-icons" style="font-size:13px;">download</i></a>
                <form action="<%= ctx %>/backup" method="post" style="display:inline;" onsubmit="return confirm('Restaurar <%= name %>? Se perderan los datos actuales.');">
                  <input type="hidden" name="_csrf_token" value="<%= request.getAttribute("csrfToken") %>">
                  <input type="hidden" name="action" value="restaurar">
                  <input type="hidden" name="file" value="<%= name %>">
                  <button type="submit" class="btn" style="height:30px;line-height:30px;padding:0 10px;font-size:10px;background:#f39c12;color:white;border-radius:6px;border:none;cursor:pointer;"><i class="material-icons" style="font-size:13px;">restore</i></button>
                </form>
                <form action="<%= ctx %>/backup" method="post" style="display:inline;" onsubmit="return confirm('Eliminar <%= name %>?');">
                  <input type="hidden" name="_csrf_token" value="<%= request.getAttribute("csrfToken") %>">
                  <input type="hidden" name="action" value="eliminar">
                  <input type="hidden" name="file" value="<%= name %>">
                  <button type="submit" class="btn" style="height:30px;line-height:30px;padding:0 10px;font-size:10px;background:#e74c3c;color:white;border-radius:6px;border:none;cursor:pointer;"><i class="material-icons" style="font-size:13px;">delete</i></button>
                </form>
              </td>
            </tr>
<%
  }
%>
          </tbody>
        </table>
      <% } else { %>
        <div style="text-align:center;padding:40px;color:var(--text-muted);">
          <i class="material-icons" style="font-size:48px;">storage</i>
          <p style="margin-top:12px;">No hay respaldos disponibles</p>
          <p style="font-size:12px;">Haga clic en "Crear Respaldo" para generar uno nuevo</p>
        </div>
      <% } %>
    </div>
  </div>
  <div class="col s12 l6">
    <div class="content-card">
      <div class="card-title-sm">Informaci&oacute;n</div>
      <p style="font-size:13px;color:var(--text-muted);line-height:1.6;">
        Los respaldos se almacenan en el servidor y contienen la estructura completa de la base de datos, incluyendo tablas, procedimientos y datos.
      </p>
      <ul style="font-size:13px;color:var(--text-muted);line-height:2;">
        <li><i class="material-icons" style="font-size:14px;vertical-align:middle;color:#1abc9c;">check_circle</i> Formato: SQL</li>
        <li><i class="material-icons" style="font-size:14px;vertical-align:middle;color:#1abc9c;">check_circle</i> Incluye todos los datos del sistema</li>
        <li><i class="material-icons" style="font-size:14px;vertical-align:middle;color:#1abc9c;">check_circle</i> Restauraci&oacute;n completa con un clic</li>
        <li><i class="material-icons" style="font-size:14px;vertical-align:middle;color:#1abc9c;">check_circle</i> Descargue respaldos para almacenamiento externo</li>
      </ul>
    </div>
  </div>
</div>

<%@ include file="/WEB-INF/jsp/layout/footer.jsp" %>
