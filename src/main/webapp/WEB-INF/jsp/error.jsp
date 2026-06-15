<%-- Página de error genérica (puede mostrarse con o sin sesión) --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.codewise.facturaexpress.model.Usuario" %>
<%-- Scriptlet: mensaje de error, usuario actual (puede ser null si no hay sesión) y contextPath --%>
<%
  String errorMsg = (String) request.getAttribute("error");
  if (errorMsg == null) errorMsg = "Ha ocurrido un error inesperado";
  Usuario usr = (Usuario) session.getAttribute("usuario");
  String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Error - FacturaExpress</title>
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/css/materialize.min.css">
  <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
  <link rel="stylesheet" href="<%= ctx %>/css/styles.css">
  <script src="<%= ctx %>/js/accessibility-bootstrap.js"></script>
</head>
<body>

<%-- Si hay sesión activa, muestra el sidebar y topbar (layout inline) --%>
<% if (usr != null) { %>
<%-- Sidebar izquierdo con navegación --%>
<div class="fx-sidebar">
  <a class="fx-sidebar-logo" href="<%= ctx %>/dashboard">
    <div class="fx-logo-box">F</div>
    <span class="fx-logo-text">FacturaExpress</span>
  </a>
  <div class="fx-sidebar-nav">
    <a class="fx-nav-item" href="<%= ctx %>/dashboard"><i class="material-icons">dashboard</i> Panel Principal</a>
    <a class="fx-nav-item" href="<%= ctx %>/ventas"><i class="material-icons">shopping_cart</i> Gesti&oacute;n de Ventas</a>
    <a class="fx-nav-item" href="<%= ctx %>/facturas"><i class="material-icons">description</i> Facturaci&oacute;n Electr&oacute;nica</a>
    <a class="fx-nav-item" href="<%= ctx %>/clientes"><i class="material-icons">group</i> Clientes</a>
    <a class="fx-nav-item" href="<%= ctx %>/reportes"><i class="material-icons">bar_chart</i> Reportes y Estad&iacute;sticas</a>
  </div>
</div>
<%-- Topbar y área de contenido --%>
<div class="fx-main-area">
  <div class="fx-topbar">
    <span class="fx-topbar-title">Error</span>
    <div class="fx-topbar-dian"><span class="pulse-dot"></span> DIAN SINCRONIZADO</div>
    <div class="fx-topbar-user">
      <div class="fx-topbar-user-info">
        <div class="fx-topbar-user-name"><%= usr.getNombre() %></div>
        <div class="fx-topbar-user-role"><%= usr.getRol() %></div>
      </div>
      <i class="material-icons" style="color:#1a2535;font-size:30px;">account_circle</i>
    </div>
  </div>
  <div class="fx-content">
<% } %>

    <%-- Contenedor del mensaje de error con acciones --%>
    <div class="error-container">
      <div class="error-code">!</div>
      <h3>Error</h3>
      <p><%= errorMsg %></p>
      <div style="display:flex;gap:12px;justify-content:center;">
        <a href="javascript:history.back()" class="btn btn-cancel-compact waves-effect" style="display:inline-flex;align-items:center;gap:8px;height:38px;line-height:38px;padding:0 20px;border-radius:10px;text-decoration:none;font-size:13px;font-weight:600;">Volver atr&aacute;s</a>
        <a href="<%= ctx %>/<%= usr != null ? "dashboard" : "login" %>" class="btn btn-teal waves-effect waves-light"><i class="material-icons left">home</i>Ir al inicio</a>
      </div>
    </div>

<%-- Cierre del layout si hay sesión activa --%>
<% if (usr != null) { %>
  </div>
  <div class="fx-footer">
    <span>&copy; 2026 FACTURAEXPRESS &ndash; FACTURACI&Oacute;N SEGURA</span>
    <div class="fx-footer-statuses">
      <div class="fx-f-status"><span class="pulse-dot"></span> MOTOR FISCAL ACTIVO</div>
      <div class="fx-f-status"><span class="pulse-dot"></span> BACKUPS AL D&Iacute;A</div>
    </div>
  </div>
</div>
<% } %>

<script src="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/js/materialize.min.js"></script>
<script src="<%= ctx %>/js/core.js"></script>
</body>
</html>
