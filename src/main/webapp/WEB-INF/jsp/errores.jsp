<%-- Página de errores del sistema --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.codewise.facturaexpress.model.ErrorSistema, java.util.List" %>
<%@ include file="/WEB-INF/jsp/layout/header.jsp" %>
<%
  List<ErrorSistema> errores = (List<ErrorSistema>) request.getAttribute("errores");
  List<ErrorSistema> noResueltos = (List<ErrorSistema>) request.getAttribute("noResueltos");
  String error = (String) request.getAttribute("error");
%>

<% if (error != null) { %>
  <div style="color:#e74c3c;background:rgba(231,76,60,0.1);padding:12px 16px;border-radius:8px;margin-bottom:16px;font-size:13px;"><%= error %></div>
<% } %>

<div class="row" style="margin-bottom:0;">
  <div class="col s12 m4" style="padding-bottom:18px;">
    <div class="report-kpi">
      <i class="material-icons rk-icon" style="color:#e74c3c;">error</i>
      <div>
        <div class="rk-val" style="color:#e74c3c;"><%= errores != null ? errores.size() : 0 %></div>
        <div class="rk-lbl">Total Errores</div>
      </div>
    </div>
  </div>
  <div class="col s12 m4" style="padding-bottom:18px;">
    <div class="report-kpi">
      <i class="material-icons rk-icon" style="color:#f39c12;">warning</i>
      <div>
        <div class="rk-val" style="color:#f39c12;"><%= noResueltos != null ? noResueltos.size() : 0 %></div>
        <div class="rk-lbl">No Resueltos</div>
      </div>
    </div>
  </div>
  <div class="col s12 m4" style="padding-bottom:18px;">
    <div class="report-kpi">
      <i class="material-icons rk-icon">check_circle</i>
      <div>
        <div class="rk-val"><%= (errores != null ? errores.size() : 0) - (noResueltos != null ? noResueltos.size() : 0) %></div>
        <div class="rk-lbl">Resueltos</div>
      </div>
    </div>
  </div>
</div>

<div class="content-card">
  <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px;flex-wrap:wrap;gap:8px;">
    <div class="card-title-sm" style="margin:0;">Listado de Errores</div>
    <div style="display:flex;gap:8px;flex-wrap:wrap;">
      <a href="<%= ctx %>/errores" class="btn btn-small btn-teal" style="border-radius:40px;height:30px;line-height:30px;font-size:11px;">Todos</a>
      <a href="<%= ctx %>/errores?tipo=dian" class="btn btn-small btn-outline-accent" style="border-radius:40px;height:30px;line-height:30px;font-size:11px;">DIAN</a>
      <a href="<%= ctx %>/errores?tipo=firma" class="btn btn-small btn-outline-accent" style="border-radius:40px;height:30px;line-height:30px;font-size:11px;">Firma</a>
      <a href="<%= ctx %>/errores?tipo=bd" class="btn btn-small btn-outline-accent" style="border-radius:40px;height:30px;line-height:30px;font-size:11px;">BD</a>
    </div>
  </div>

  <% if (errores == null || errores.isEmpty()) { %>
    <div class="empty-state"><i class="material-icons">check_circle</i><p>No hay errores registrados en el sistema</p></div>
  <% } else { %>
    <table class="highlight striped">
      <thead><tr><th>ID</th><th>Tipo</th><th>Mensaje</th><th>Factura</th><th>Fecha</th><th>Estado</th><th>Acci&oacute;n</th></tr></thead>
      <tbody>
        <% for (ErrorSistema e : errores) { %>
          <tr>
            <td class="table-mono"><%= e.getId() %></td>
            <td><span class="badge-status" style="background:rgba(52,152,219,0.12);color:#3498db;text-transform:uppercase;"><%= e.getTipo() %></span></td>
            <td style="max-width:300px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;"><%= e.getMensaje() %></td>
            <td class="table-mono"><%= e.getFactura() != null ? e.getFactura().getId() : "-" %></td>
            <td style="font-size:12px;"><%= e.getFechaCreacion() != null ? e.getFechaCreacion().toString().replace("T", " ") : "" %></td>
            <td>
              <% if (e.isResuelto()) { %>
                <span class="badge-status" style="background:rgba(39,174,96,0.12);color:#27ae60;">Resuelto</span>
              <% } else { %>
                <span class="badge-status" style="background:rgba(231,76,60,0.12);color:#e74c3c;">Pendiente</span>
              <% } %>
            </td>
            <td>
              <% if (!e.isResuelto()) { %>
                <a href="<%= ctx %>/errores?action=resolver&id=<%= e.getId() %>" class="btn btn-small btn-teal" style="border-radius:40px;height:28px;line-height:28px;font-size:11px;" onclick="return confirm('Marcar este error como resuelto?')">
                  <i class="material-icons left" style="font-size:14px;">check</i>Resolver
                </a>
              <% } else { %>
                <span style="color:var(--text-muted);font-size:12px;"><%= e.getFechaResolucion() != null ? e.getFechaResolucion().toString().replace("T", " ") : "" %></span>
              <% } %>
            </td>
          </tr>
        <% } %>
      </tbody>
    </table>
  <% } %>
</div>

<%@ include file="/WEB-INF/jsp/layout/footer.jsp" %>
