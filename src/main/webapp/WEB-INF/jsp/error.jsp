<%-- Página de error genérica (puede mostrarse con o sin sesión) --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.codewise.facturaexpress.model.Usuario" %>
<%
  String errorMsg = (String) request.getAttribute("error");
  if (errorMsg == null) errorMsg = "Ha ocurrido un error inesperado";
  Usuario usr = (Usuario) session.getAttribute("usuario");
  String homeUrl = (usr != null ? "dashboard" : "login");
%>
<% if (usr != null) { %>
  <%@ include file="/WEB-INF/jsp/layout/header.jsp" %>
<% } else { %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Error - FacturaExpress</title>
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/css/materialize.min.css">
  <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
  <link rel="stylesheet" href="<%= request.getContextPath() %>/css/styles.css">
  <script src="<%= request.getContextPath() %>/js/accessibility-bootstrap.js"></script>
</head>
<body>
<% } %>

    <div class="error-container">
      <div class="error-code">!</div>
      <h3>Error</h3>
      <p><%= errorMsg %></p>
      <div style="display:flex;gap:12px;justify-content:center;">
        <a href="javascript:history.back()" class="btn btn-cancel-compact waves-effect" style="display:inline-flex;align-items:center;gap:8px;height:38px;line-height:38px;padding:0 20px;border-radius:10px;text-decoration:none;font-size:13px;font-weight:600;">Volver atr&aacute;s</a>
        <a href="<%= request.getContextPath() %>/<%= homeUrl %>" class="btn btn-teal waves-effect waves-light"><i class="material-icons left">home</i>Ir al inicio</a>
      </div>
    </div>

<% if (usr != null) { %>
  <%@ include file="/WEB-INF/jsp/layout/footer.jsp" %>
<% } else { %>
<script src="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/js/materialize.min.js"></script>
<script src="<%= request.getContextPath() %>/js/core.js"></script>
</body>
</html>
<% } %>
