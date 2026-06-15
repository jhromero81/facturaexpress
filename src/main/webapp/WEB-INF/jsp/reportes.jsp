<%-- Página de reportes y estadísticas con gráficos Chart.js --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.math.BigDecimal, java.util.List, java.util.Map" %>
<%@ include file="/WEB-INF/jsp/layout/header.jsp" %>
<%-- Scriptlet: obtiene datos de ventas mensuales, top productos, KPI --%>
<%
  List<Map<String, Object>> ventasMensuales = (List<Map<String, Object>>) request.getAttribute("ventasMensuales");
  List<Map<String, Object>> topProductos = (List<Map<String, Object>>) request.getAttribute("topProductos");
  BigDecimal ventasMes = (BigDecimal) request.getAttribute("ventasMes");
  Integer facturasMes = (Integer) request.getAttribute("facturasMes");
  String error = (String) request.getAttribute("error");
%>

<%-- Muestra error si existe --%>
<% if (error != null) { %>
  <div style="color:#e74c3c;background:rgba(231,76,60,0.1);padding:12px 16px;border-radius:8px;margin-bottom:16px;font-size:13px;"><%= error %></div>
<% } %>

<%-- KPIs de reportes: ventas del mes y facturas emitidas --%>
<div class="row" style="margin-bottom:0;">
  <div class="col s12 m6 l3" style="padding-bottom:18px;">
    <div class="report-kpi">
      <i class="material-icons rk-icon">trending_up</i>
      <div>
        <div class="rk-val">$ <%= String.format("%,.0f", ventasMes != null ? ventasMes : BigDecimal.ZERO) %></div>
        <div class="rk-lbl">Ventas del Mes</div>
      </div>
    </div>
  </div>
  <div class="col s12 m6 l3" style="padding-bottom:18px;">
    <div class="report-kpi">
      <i class="material-icons rk-icon">receipt_long</i>
      <div>
        <div class="rk-val"><%= facturasMes != null ? facturasMes : 0 %></div>
        <div class="rk-lbl">Facturas Emitidas</div>
      </div>
    </div>
  </div>
</div>

<div class="row">
<%-- Gráfico de ventas mensuales (línea) --%>
  <div class="col s12 l8" style="padding-bottom:18px;">
    <div class="content-card">
      <canvas id="ventasMensualesChart" height="180"></canvas>
      <p class="chart-label">Crecimiento de Ventas (&uacute;ltimos 6 meses)</p>
    </div>
  </div>
  <%-- Gráfico de top 10 productos más vendidos (barras horizontales) --%>
  <div class="col s12 l4" style="padding-bottom:18px;">
    <div class="content-card" style="height:100%;">
      <div class="card-title-sm">Top 10 Productos M&aacute;s Vendidos</div>
      <canvas id="topProductosChart" height="200"></canvas>
    </div>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<%-- JS: inicializa los gráficos con Chart.js --%>
<script>
document.addEventListener('DOMContentLoaded', function() {
  // Gráfico de línea: ventas de los últimos 6 meses
  <% if (ventasMensuales != null && !ventasMensuales.isEmpty()) { %>
    new Chart(document.getElementById('ventasMensualesChart'), {
      type: 'line',
      data: {
        labels: [<% for (Map<String, Object> m : ventasMensuales) { %>'<%= m.get("mes") %>',<% } %>],
        datasets: [{
          label: 'Ventas ($)',
          data: [<% for (Map<String, Object> m : ventasMensuales) { %><%= m.get("total") %>,<% } %>],
          borderColor: '#1abc9c',
          backgroundColor: 'rgba(26,188,156,0.1)',
          fill: true,
          tension: 0.4,
          pointBackgroundColor: '#1abc9c',
          pointRadius: 4
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
  <% } %>

  // Gráfico de barras horizontales: top 10 productos más vendidos
  <% if (topProductos != null && !topProductos.isEmpty()) { %>
    new Chart(document.getElementById('topProductosChart'), {
      type: 'bar',
      data: {
        labels: [<% for (Map<String, Object> p : topProductos) { %>'<%= p.get("nombre") %>',<% } %>],
        datasets: [{
          label: 'Unidades vendidas',
          data: [<% for (Map<String, Object> p : topProductos) { %><%= p.get("cantidad") %>,<% } %>],
          backgroundColor: ['#1abc9c','#b0bec5','#1abc9c','#b0bec5','#1abc9c','#b0bec5','#1abc9c','#b0bec5','#1abc9c','#b0bec5']
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        indexAxis: 'y',
        plugins: { legend: { display: false } },
        scales: {
          x: { beginAtZero: true, ticks: { stepSize: 1, font: { size: 11 } }, grid: { color: 'rgba(0,0,0,0.05)' } },
          y: { grid: { display: false }, ticks: { font: { size: 10 } } }
        }
      }
    });
  <% } %>
});
</script>

<%@ include file="/WEB-INF/jsp/layout/footer.jsp" %>
