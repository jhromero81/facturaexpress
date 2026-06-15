<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%-- Página de inicio: redirige automáticamente al dashboard del usuario autenticado --%>
<%
  response.sendRedirect(request.getContextPath() + "/dashboard");
%>
