<%-- Página de confirmación con redirección automática tras operación exitosa --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/jsp/layout/header.jsp" %>
<%-- Scriptlet: mensaje de éxito y URL de redirección (por defecto al home) --%>
<%
  String mensaje = (String) request.getAttribute("mensaje");
  String redirectUrl = (String) request.getAttribute("redirectUrl");
  if (redirectUrl == null) redirectUrl = ctx + "/";
%>

<%-- Mensaje de confirmación con icono de éxito --%>
<div class="content-card confirm-box">
  <i class="material-icons confirm-icon">check_circle</i>
  <h3><%= mensaje != null ? mensaje : "Operaci&oacute;n exitosa" %></h3>
  <p>Ser&aacute; redirigido autom&aacute;ticamente en 3 segundos...</p>
  <div style="margin-top:24px;">
    <a href="<%= redirectUrl %>" class="btn btn-teal waves-effect waves-light">Ir ahora</a>
  </div>
</div>

<%-- Meta refresh para redirección automática después de 3 segundos --%>
<meta http-equiv="refresh" content="3;url=<%= redirectUrl %>">

<%@ include file="/WEB-INF/jsp/layout/footer.jsp" %>
