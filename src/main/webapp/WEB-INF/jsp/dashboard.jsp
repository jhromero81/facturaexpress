<%-- Página principal del dashboard después del login --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.math.BigDecimal, java.util.List, java.util.Map" %>
<%@ include file="/WEB-INF/jsp/layout/header.jsp" %>
<%-- Scriptlet: obtiene del request los indicadores del dashboard --%>
<%
  Integer facturasDia = (Integer) request.getAttribute("facturasDia");
  BigDecimal ventasDia = (BigDecimal) request.getAttribute("ventasDia");
  BigDecimal ticketPromedio = (BigDecimal) request.getAttribute("ticketPromedio");
  Integer facturasMes = (Integer) request.getAttribute("facturasMes");
  BigDecimal ventasMes = (BigDecimal) request.getAttribute("ventasMes");
  List<Map<String, Object>> ventasSemana = (List<Map<String, Object>>) request.getAttribute("ventasSemana");
  List<Map<String, Object>> topProductos = (List<Map<String, Object>>) request.getAttribute("topProductos");
  String error = (String) request.getAttribute("error");
%>

<%-- Barra de error si algo falla al cargar los indicadores --%>
<% if (error != null) { %>
  <div style="color:#e74c3c;background:rgba(231,76,60,0.1);padding:12px 16px;border-radius:8px;margin-bottom:16px;font-size:13px;font-weight:500;"><%= error %></div>
<% } %>

<%-- Tarjetas de indicadores (KPI): ventas del día, ticket promedio, ventas del mes, estado DIAN --%>
<div class="row" style="margin-bottom:0;">
  <div class="col s12 m6 l3" style="padding-bottom:18px;">
    <div class="stat-card">
      <div class="stat-label">Ventas del D&iacute;a</div>
      <div class="stat-value">$<%= String.format("%,.0f", ventasDia != null ? ventasDia : BigDecimal.ZERO) %></div>
      <div class="stat-change change-up"><i class="material-icons" style="font-size:14px;">arrow_upward</i> <%= facturasDia != null ? facturasDia : 0 %> facturas</div>
    </div>
  </div>
  <div class="col s12 m6 l3" style="padding-bottom:18px;">
    <div class="stat-card">
      <div class="stat-label">Ticket Promedio</div>
      <div class="stat-value">$<%= String.format("%,.0f", ticketPromedio != null ? ticketPromedio : BigDecimal.ZERO) %></div>
      <div class="stat-change change-neutral"><i class="material-icons" style="font-size:14px;">trending_flat</i> Hoy</div>
    </div>
  </div>
  <div class="col s12 m6 l3" style="padding-bottom:18px;">
    <div class="stat-card">
      <div class="stat-label">Ventas del Mes</div>
      <div class="stat-value">$<%= String.format("%,.0f", ventasMes != null ? ventasMes : BigDecimal.ZERO) %></div>
      <div class="stat-change change-up"><i class="material-icons" style="font-size:14px;">arrow_upward</i> <%= facturasMes != null ? facturasMes : 0 %> facturas</div>
    </div>
  </div>
  <div class="col s12 m6 l3" style="padding-bottom:18px;">
    <div class="stat-card">
      <div class="stat-label">Estado del Sistema</div>
      <div class="stat-value" style="font-size:16px;letter-spacing:0.5px;">DIAN OK</div>
      <div class="stat-change change-up" style="gap:6px;"><span class="pulse-dot" style="width:6px;height:6px;"></span>Sincronizado</div>
    </div>
  </div>
</div>

<div class="row">
<%-- Gráfico de ventas semanales (Chart.js) --%>
  <div class="col s12 l8" style="padding-bottom:18px;">
    <div class="content-card">
      <canvas id="ventasSemanaChart" height="160"></canvas>
      <p class="chart-label">Ventas de los &uacute;ltimos 7 d&iacute;as</p>
    </div>
  </div>
  <%-- Top productos más vendidos --%>
  <div class="col s12 l4" style="padding-bottom:18px;">
    <div class="content-card" style="height:100%;">
      <div class="card-title-sm">Top Productos</div>
      <% if (topProductos != null && !topProductos.isEmpty()) { %>
        <ul class="txn-list">
          <%-- Iteración sobre la lista de productos más vendidos --%>
          <% for (Map<String, Object> p : topProductos) { %>
            <li>
              <div class="txn-icon-box"><i class="material-icons" style="font-size:18px;">inventory_2</i></div>
              <div><div class="txn-num"><%= p.get("nombre") %></div><div class="txn-time">vendidos</div></div>
              <div class="txn-amount"><%= p.get("cantidad") %></div>
            </li>
          <% } %>
        </ul>
      <% } else { %>
        <p style="color:var(--text-muted);padding:16px;text-align:center;">No hay datos de productos vendidos</p>
      <% } %>
    </div>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<%-- JS: Inicializa el gráfico de barras con datos de ventas de la semana --%>
<script>
document.addEventListener('DOMContentLoaded', function() {
  var ctx = document.getElementById('ventasSemanaChart');
  if (ctx) {
    new Chart(ctx, {
      type: 'bar',
      data: {
        labels: [<% if (ventasSemana != null) { for (Map<String, Object> d : ventasSemana) { %>'<%= d.get("dia") %>',<% } } %>],
        datasets: [{
          label: 'Ventas ($)',
          data: [<% if (ventasSemana != null) { for (Map<String, Object> d : ventasSemana) { %><%= d.get("total") %>,<% } } %>],
          backgroundColor: 'rgba(26,188,156,0.7)',
          borderColor: '#1abc9c',
          borderWidth: 1,
          borderRadius: 4,
          barPercentage: 0.6
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { display: false } },
        scales: {
          y: { beginAtZero: true, grid: { color: 'rgba(0,0,0,0.05)' }, ticks: { font: { size: 11 } } },
          x: { grid: { display: false }, ticks: { font: { size: 11 } } }
        }
      }
    });
  }
});
</script>

<script src="<%= ctx %>/js/dashboard.js"></script>
<%@ include file="/WEB-INF/jsp/layout/footer.jsp" %>
