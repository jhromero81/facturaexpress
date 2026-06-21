<%-- Header / layout principal: sidebar izquierdo + topbar superior + contenido central --%>
<%-- Este archivo se incluye al inicio de cada página interna. Verifica sesión y pinta el sidebar y topbar. --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.codewise.facturaexpress.model.Usuario" %>
<%-- Scriptlet: valida que el usuario tenga sesión activa; si no, redirige al login --%>
<%
  Usuario usuario = (Usuario) session.getAttribute("usuario");
  if (usuario == null) {
    response.sendRedirect(request.getContextPath() + "/login");
    return;
  }
  String activeNav = (String) request.getAttribute("activeNav");
  String pageTitle = (String) request.getAttribute("pageTitle");
  if (pageTitle == null) pageTitle = "FacturaExpress";
  String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title><%= pageTitle %> - FacturaExpress</title>
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/css/materialize.min.css">
  <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
  <link rel="stylesheet" href="<%= ctx %>/css/styles.css">
  <script src="<%= ctx %>/js/accessibility-bootstrap.js"></script>
</head>
<body>

<%-- Sidebar izquierdo fijo con logo, navegación principal y botón de cierre de sesión --%>
<%-- La navegación resalta el ítem activo según activeNav --%>
<div class="fx-sidebar">
  <a class="fx-sidebar-logo" href="<%= ctx %>/dashboard">
    <div class="fx-logo-box">F</div>
    <span class="fx-logo-text">FacturaExpress</span>
  </a>
  <div class="fx-sidebar-nav">
    <a class="fx-nav-item <%= "dashboard".equals(activeNav) ? "fx-active" : "" %>" href="<%= ctx %>/dashboard">
      <i class="material-icons">dashboard</i> Panel Principal
    </a>
    <a class="fx-nav-item <%= "ventas".equals(activeNav) ? "fx-active" : "" %>" href="<%= ctx %>/ventas">
      <i class="material-icons">shopping_cart</i> Gesti&oacute;n de Ventas
    </a>
    <a class="fx-nav-item <%= "facturas".equals(activeNav) ? "fx-active" : "" %>" href="<%= ctx %>/facturas">
      <i class="material-icons">description</i> Facturaci&oacute;n Electr&oacute;nica
    </a>
    <a class="fx-nav-item <%= "clientes".equals(activeNav) ? "fx-active" : "" %>" href="<%= ctx %>/clientes">
      <i class="material-icons">group</i> Clientes
    </a>
    <a class="fx-nav-item <%= "productos".equals(activeNav) ? "fx-active" : "" %>" href="<%= ctx %>/productos">
      <i class="material-icons">inventory_2</i> Productos
    </a>
    <a class="fx-nav-item <%= "reportes".equals(activeNav) ? "fx-active" : "" %>" href="<%= ctx %>/reportes">
      <i class="material-icons">bar_chart</i> Reportes y Estad&iacute;sticas
    </a>
  </div>
  <div class="fx-sidebar-footer">
    <a class="fx-nav-item fx-logout" href="#" id="showLogoutBtn">
      <i class="material-icons">logout</i> Cerrar Sesi&oacute;n
    </a>
  </div>
</div>

<%-- Área principal: topbar con título, estado DIAN y usuario + contenido dinámico --%>
<div class="fx-main-area">
    <div class="fx-topbar">
      <span class="fx-topbar-title"><%= pageTitle %></span>
      <div class="fx-topbar-dian"><span class="pulse-dot"></span> DIAN SINCRONIZADO</div>
      <div class="fx-accessibility-controls" style="display:flex;align-items:center;gap:4px;">
        <button id="darkModeControl" class="accessibility-option" style="background:rgba(26,188,156,0.1);border:none;border-radius:20px;padding:5px 10px;cursor:pointer;display:flex;align-items:center;gap:4px;" title="Modo oscuro">
          <i class="material-icons" style="font-size:16px;color:#1abc9c;">dark_mode</i>
          <span style="font-size:11px;font-weight:600;color:#1abc9c;text-transform:uppercase;">Oscuro</span>
        </button>
        <button id="highContrastControl" class="accessibility-option" style="background:rgba(26,188,156,0.1);border:none;border-radius:20px;padding:5px 10px;cursor:pointer;display:flex;align-items:center;gap:4px;" title="Alto contraste">
          <i class="material-icons" style="font-size:16px;color:#1abc9c;">contrast</i>
          <span style="font-size:11px;font-weight:600;color:#1abc9c;text-transform:uppercase;">Contraste</span>
        </button>
        <select id="fontSizeControl" style="background:rgba(26,188,156,0.1);border:none;border-radius:20px;padding:5px 10px;font-size:11px;font-weight:600;color:#1abc9c;cursor:pointer;outline:none;font-family:inherit;">
          <option value="small">Peque&ntilde;o</option>
          <option value="medium" selected>Mediano</option>
          <option value="large">Grande</option>
        </select>
      </div>
      <div class="fx-topbar-user">
        <div class="fx-topbar-user-info">
          <div class="fx-topbar-user-name"><%= usuario.getNombre() %></div>
          <div class="fx-topbar-user-role"><%= usuario.getRol() %></div>
        </div>
        <i class="material-icons" style="color:#1a2535;font-size:30px;">account_circle</i>
      </div>
    </div>
  <div class="fx-content">
