<%-- Página de logs de auditoría --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.codewise.facturaexpress.model.LogAuditoria, java.util.List" %>
<%@ include file="/WEB-INF/jsp/layout/header.jsp" %>
<%
  List<LogAuditoria> logs = (List<LogAuditoria>) request.getAttribute("logs");
  String error = (String) request.getAttribute("error");
%>

<% if (error != null) { %>
  <div style="color:#e74c3c;background:rgba(231,76,60,0.1);padding:12px 16px;border-radius:8px;margin-bottom:16px;font-size:13px;"><%= error %></div>
<% } %>

<div class="content-card">
  <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px;flex-wrap:wrap;gap:8px;">
    <div class="card-title-sm" style="margin:0;">Registro de Auditor&iacute;a</div>
    <div style="display:flex;gap:8px;flex-wrap:wrap;">
      <a href="<%= ctx %>/logs" class="btn btn-small btn-teal" style="border-radius:40px;height:30px;line-height:30px;font-size:11px;">Todos</a>
      <a href="<%= ctx %>/logs?tabla=clientes" class="btn btn-small btn-outline-accent" style="border-radius:40px;height:30px;line-height:30px;font-size:11px;">Clientes</a>
      <a href="<%= ctx %>/logs?tabla=facturas" class="btn btn-small btn-outline-accent" style="border-radius:40px;height:30px;line-height:30px;font-size:11px;">Facturas</a>
      <a href="<%= ctx %>/logs?tabla=productos" class="btn btn-small btn-outline-accent" style="border-radius:40px;height:30px;line-height:30px;font-size:11px;">Productos</a>
    </div>
  </div>

  <% if (logs == null || logs.isEmpty()) { %>
    <div class="empty-state"><i class="material-icons">receipt_long</i><p>No hay eventos de auditor&iacute;a registrados</p></div>
  <% } else { %>
    <table class="highlight striped">
      <thead><tr><th>ID</th><th>Usuario</th><th>Acci&oacute;n</th><th>Tabla</th><th>Registro</th><th>IP</th><th>Fecha</th></tr></thead>
      <tbody>
        <% for (LogAuditoria l : logs) { %>
          <tr>
            <td class="table-mono"><%= l.getId() %></td>
            <td><strong><%= l.getUsuarioNombre() != null ? l.getUsuarioNombre() : "ID:" + l.getUsuarioId() %></strong></td>
            <td style="max-width:250px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;"><%= l.getAccion() %></td>
            <td><span class="badge-status" style="background:rgba(52,152,219,0.12);color:#3498db;"><%= l.getTablaAfectada() != null ? l.getTablaAfectada() : "-" %></span></td>
            <td class="table-mono"><%= l.getRegistroId() != null ? l.getRegistroId() : "-" %></td>
            <td style="font-size:12px;"><%= l.getIpOrigen() != null ? l.getIpOrigen() : "-" %></td>
            <td style="font-size:12px;"><%= l.getFecha() != null ? l.getFecha().toString().replace("T", " ") : "" %></td>
          </tr>
        <% } %>
      </tbody>
    </table>
  <% } %>
</div>

<%@ include file="/WEB-INF/jsp/layout/footer.jsp" %>
