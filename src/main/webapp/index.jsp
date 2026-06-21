<%--
  Página de bienvenida. Redirige al dashboard si hay sesión activa,
  o al formulario de login en caso contrario.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  String ctx = request.getContextPath();
  if (session.getAttribute("usuario") != null) {
    response.sendRedirect(ctx + "/dashboard");
  } else {
    response.sendRedirect(ctx + "/login");
  }
%>
