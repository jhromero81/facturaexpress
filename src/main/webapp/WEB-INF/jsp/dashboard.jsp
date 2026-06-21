<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.math.BigDecimal, java.util.List, java.util.Map" %>
<%@ include file="/WEB-INF/jsp/layout/header.jsp" %>
<%
  Integer facturasDia = (Integer) request.getAttribute("facturasDia");
  BigDecimal ventasDia = (BigDecimal) request.getAttribute("ventasDia");
  BigDecimal ticketPromedio = (BigDecimal) request.getAttribute("ticketPromedio");
  Integer facturasMes = (Integer) request.getAttribute("facturasMes");
  BigDecimal ventasMes = (BigDecimal) request.getAttribute("ventasMes");
  List<Map<String, Object>> ventasSemana = (List<Map<String, Object>>) request.getAttribute("ventasSemana");
  String error = (String) request.getAttribute("error");
  List<Map<String, Object>> topProductos = (List<Map<String, Object>>) request.getAttribute("topProductos");
  List<Map<String, Object>> ultimasTransacciones = (List<Map<String, Object>>) request.getAttribute("ultimasTransacciones");
%>

<% if (error != null) { %>
  <div style="color:#e74c3c;background:rgba(231,76,60,0.1);padding:12px 16px;border-radius:8px;margin-bottom:16px;font-size:13px;font-weight:500;"><%= error %></div>
<% } %>

<div class="row" style="margin-bottom:0;">
  <div class="col s12 m6 l3" style="padding-bottom:18px;">
    <div class="stat-card">
      <div class="stat-label">Ventas del D&iacute;a</div>
      <div class="stat-value">$<%= String.format("%,.0f", ventasDia != null ? ventasDia : BigDecimal.ZERO) %></div>
      <div class="stat-change change-up"><i class="material-icons" style="font-size:14px;">arrow_upward</i> +12% vs ayer</div>
    </div>
  </div>
  <div class="col s12 m6 l3" style="padding-bottom:18px;">
    <div class="stat-card">
      <div class="stat-label">Facturas Emitidas</div>
      <div class="stat-value"><%= facturasDia != null ? facturasDia : 0 %></div>
      <div class="stat-change change-up"><i class="material-icons" style="font-size:14px;">arrow_upward</i> +5 resp.</div>
    </div>
  </div>
  <div class="col s12 m6 l3" style="padding-bottom:18px;">
    <div class="stat-card">
      <div class="stat-label">Pendientes DIAN</div>
      <div class="stat-value">0</div>
      <div class="stat-change change-neutral"><i class="material-icons" style="font-size:14px;">check_circle</i> Ok respecto ayer</div>
    </div>
  </div>
  <div class="col s12 m6 l3" style="padding-bottom:18px;">
    <div class="stat-card">
      <div class="stat-label">Ticket Promedio</div>
      <div class="stat-value">$<%= String.format("%,.0f", ticketPromedio != null ? ticketPromedio : BigDecimal.ZERO) %></div>
      <div class="stat-change change-down"><i class="material-icons" style="font-size:14px;">arrow_downward</i> -2% respecto ayer</div>
    </div>
  </div>
</div>

<div class="row">
  <div class="col s12 l8" style="padding-bottom:18px;">
    <div class="content-card">
      <div style="height:200px;width:100%;position:relative;">
        <canvas id="dashboardChart"></canvas>
      </div>
      <p class="chart-label">Gr&aacute;fico de Ventas Semanales</p>
      <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
      <script>
      document.addEventListener('DOMContentLoaded', function() {
        var ctx = document.getElementById('dashboardChart').getContext('2d');
        var gradient = ctx.createLinearGradient(0, 0, 0, 200);
        gradient.addColorStop(0, 'rgba(26,188,156,0.35)');
        gradient.addColorStop(1, 'rgba(26,188,156,0.01)');
        new Chart(ctx, {
          type: 'line',
          data: {
            labels: [<% if (ventasSemana != null) { for (Map<String, Object> m : ventasSemana) { %>'<%= m.get("dia") %>',<% } } %>],
            datasets: [{
              label: 'Ventas ($)',
              data: [<% if (ventasSemana != null) { for (Map<String, Object> m : ventasSemana) { %><%= m.get("total") %>,<% } } %>],
              borderColor: '#1abc9c',
              backgroundColor: gradient,
              fill: true,
              tension: 0.4,
              pointBackgroundColor: '#fff',
              pointBorderColor: '#1abc9c',
              pointBorderWidth: 2,
              pointRadius: 4,
              pointHoverRadius: 6,
              borderWidth: 2
            }]
          },
          options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
              legend: { display: false },
              tooltip: {
                backgroundColor: 'rgba(26,35,53,0.9)',
                titleFont: { size: 12 },
                bodyFont: { size: 13, weight: 'bold' },
                padding: 10,
                cornerRadius: 8,
                displayColors: false,
                callbacks: {
                  label: function(ctx) { return '$ ' + ctx.parsed.y.toLocaleString('es-CO'); }
                }
              }
            },
            scales: {
              y: {
                beginAtZero: true,
                grid: { color: 'rgba(0,0,0,0.04)', drawBorder: false },
                ticks: {
                  font: { size: 11 },
                  color: '#90a4ae',
                  callback: function(v) { return '$' + v.toLocaleString('es-CO'); }
                }
              },
              x: {
                grid: { display: false },
                ticks: { font: { size: 11 }, color: '#90a4ae' }
              }
            },
            interaction: { intersect: false, mode: 'index' }
          }
        });
      });
      </script>
    </div>
  </div>
  <div class="col s12 l4" style="padding-bottom:18px;">
    <div class="content-card" style="height:100%;">
      <div class="card-title-sm">&Uacute;ltimas Transacciones</div>
      <ul class="txn-list">
<%
  if (ultimasTransacciones != null) {
    for (Map<String, Object> txn : ultimasTransacciones) {
      BigDecimal ttl = (BigDecimal) txn.get("total");
      String num = "FAC-" + String.format("%05d", txn.get("id"));
      String cli = (String) txn.get("cliente_nombre");
      if (cli == null) cli = "";
%>
        <li>
          <div class="txn-icon-box"><i class="material-icons" style="font-size:18px;">receipt</i></div>
          <div><div class="txn-num"><%= num %></div><div class="txn-time"><%= cli %></div></div>
          <div class="txn-amount">$<%= String.format("%,.0f", ttl != null ? ttl : BigDecimal.ZERO) %></div>
        </li>
<%
    }
  }
%>
      </ul>
    </div>
  </div>
</div>

<%@ include file="/WEB-INF/jsp/layout/footer.jsp" %>
