<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.math.BigDecimal, java.util.List, java.util.Map" %>
<%@ page import="com.codewise.facturaexpress.model.Reporte" %>
<%@ include file="/WEB-INF/jsp/layout/header.jsp" %>
<%
  List<Map<String, Object>> ventasSemana = (List<Map<String, Object>>) request.getAttribute("ventasSemana");
  List<Map<String, Object>> ventasMensuales = (List<Map<String, Object>>) request.getAttribute("ventasMensuales");
  List<Map<String, Object>> ventasTrimestrales = (List<Map<String, Object>>) request.getAttribute("ventasTrimestrales");
  List<Map<String, Object>> ventasAnuales = (List<Map<String, Object>>) request.getAttribute("ventasAnuales");
  List<Map<String, Object>> topProductos = (List<Map<String, Object>>) request.getAttribute("topProductos");
  BigDecimal ventasMes = (BigDecimal) request.getAttribute("ventasMes");
  BigDecimal ventasDia = (BigDecimal) request.getAttribute("ventasDia");
  BigDecimal ticketPromedio = (BigDecimal) request.getAttribute("ticketPromedio");
  Integer facturasMes = (Integer) request.getAttribute("facturasMes");
  Integer facturasDia = (Integer) request.getAttribute("facturasDia");
  List<Reporte> reportesGuardados = (List<Reporte>) request.getAttribute("reportesGuardados");
  String error = (String) request.getAttribute("error");
%>

<% if (error != null) { %>
  <div style="color:#e74c3c;background:rgba(231,76,60,0.1);padding:12px 16px;border-radius:8px;margin-bottom:16px;font-size:13px;"><%= error %></div>
<% } %>

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
  <div class="col s12 m6 l3" style="padding-bottom:18px;">
    <div class="report-kpi">
      <i class="material-icons rk-icon" style="color:#f39c12;">today</i>
      <div>
        <div class="rk-val" style="color:#f39c12;">$ <%= String.format("%,.0f", ventasDia != null ? ventasDia : BigDecimal.ZERO) %></div>
        <div class="rk-lbl">Ventas del D&iacute;a</div>
      </div>
    </div>
  </div>
  <div class="col s12 m6 l3" style="padding-bottom:18px;">
    <div class="report-kpi">
      <i class="material-icons rk-icon" style="color:#3498db;">confirmation_number</i>
      <div>
        <div class="rk-val" style="color:#3498db;">$ <%= String.format("%,.0f", ticketPromedio != null ? ticketPromedio : BigDecimal.ZERO) %></div>
        <div class="rk-lbl">Ticket Promedio</div>
      </div>
    </div>
  </div>
</div>

<div class="report-toolbar">
  <div class="report-toolbar-tabs" id="periodTabs">
    <button class="tab-btn active" data-period="semanal">Semanal</button>
    <button class="tab-btn" data-period="mensual">Mensual</button>
    <button class="tab-btn" data-period="trimestral">Trimestral</button>
    <button class="tab-btn" data-period="anual">Anual</button>
  </div>
  <button class="btn btn-dark waves-effect waves-light" id="exportPdfBtn"><i class="material-icons left">download</i>Exportar PDF</button>
</div>

<div class="row">
  <div class="col s12 l8" style="padding-bottom:18px;">
    <div class="content-card" style="padding:22px;">
      <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px;">
        <div class="card-title-sm" style="margin:0;">Crecimiento de Ventas</div>
        <span id="chartSubtitle" style="font-size:11px;color:var(--text-muted);">&Uacute;ltimos 7 d&iacute;as</span>
      </div>
      <div style="height:220px;width:100%;position:relative;">
        <canvas id="ventasChart"></canvas>
      </div>
    </div>
  </div>
  <div class="col s12 l4" style="padding-bottom:18px;">
    <div class="content-card" style="margin-bottom:18px;padding:18px;">
      <div style="font-size:12px;font-weight:700;color:#1abc9c;text-transform:uppercase;letter-spacing:.8px;margin-bottom:14px;"><i class="material-icons" style="font-size:16px;vertical-align:middle;">star</i> Top Productos</div>
      <% if (topProductos != null && !topProductos.isEmpty()) { %>
        <ul style="list-style:none;padding:0;margin:0;">
          <% for (Map<String, Object> p : topProductos) { %>
            <li style="display:flex;justify-content:space-between;padding:9px 0;border-bottom:1px solid #eceff1;">
              <span style="font-weight:600;font-size:13px;"><%= p.get("nombre") %></span>
              <span style="color:#1abc9c;font-family:'Space Mono',monospace;font-size:12px;"><%= p.get("cantidad") %> vendidos</span>
            </li>
          <% } %>
        </ul>
      <% } else { %>
        <p style="color:var(--text-muted);text-align:center;padding:8px;">Sin datos de productos</p>
      <% } %>
    </div>
    <div class="content-card" style="padding:18px;">
      <div style="font-size:11px;font-weight:700;color:#90a4ae;text-transform:uppercase;letter-spacing:.8px;margin-bottom:12px;">Meta de Ventas</div>
      <% double meta = 6400000; double actual = ventasMes != null ? ventasMes.doubleValue() : 0; int pct = (int) Math.min(100, (actual / meta) * 100); %>
      <div style="display:flex;justify-content:space-between;margin-bottom:6px;font-size:13px;">
        <span style="color:#546e7a;">$ <%= String.format("%,.0f", actual) %> / $ <%= String.format("%,.0f", meta) %></span>
        <span style="color:#1abc9c;font-weight:700;"><%= pct %>%</span>
      </div>
      <div class="progress"><div class="determinate" style="width:<%= pct %>%;"></div></div>
      <% double faltan = Math.max(0, meta - actual); %>
      <div style="font-size:12px;color:#90a4ae;margin-top:8px;">Faltan <strong style="color:#1a2535;">$ <%= String.format("%,.0f", faltan) %></strong> para la meta</div>
    </div>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script>
var chartData = {
  semanal: {
    labels: [<% if (ventasSemana != null) { for (Map<String, Object> m : ventasSemana) { %>'<%= m.get("dia") %>',<% } } %>],
    values: [<% if (ventasSemana != null) { for (Map<String, Object> m : ventasSemana) { %><%= m.get("total") %>,<% } } %>],
    subtitle: '&Uacute;ltimos 7 d&iacute;as'
  },
  mensual: {
    labels: [<% if (ventasMensuales != null) { for (Map<String, Object> m : ventasMensuales) { %>'<%= m.get("mes") %>',<% } } %>],
    values: [<% if (ventasMensuales != null) { for (Map<String, Object> m : ventasMensuales) { %><%= m.get("total") %>,<% } } %>],
    subtitle: '&Uacute;ltimos 6 meses'
  },
  trimestral: {
    labels: [<% if (ventasTrimestrales != null) { for (Map<String, Object> m : ventasTrimestrales) { %>'<%= m.get("periodo") %>',<% } } %>],
    values: [<% if (ventasTrimestrales != null) { for (Map<String, Object> m : ventasTrimestrales) { %><%= m.get("total") %>,<% } } %>],
    subtitle: '&Uacute;ltimos 4 trimestres'
  },
  anual: {
    labels: [<% if (ventasAnuales != null) { for (Map<String, Object> m : ventasAnuales) { %>'<%= m.get("periodo") %>',<% } } %>],
    values: [<% if (ventasAnuales != null) { for (Map<String, Object> m : ventasAnuales) { %><%= m.get("total") %>,<% } } %>],
    subtitle: '&Uacute;ltimos 5 a&ntilde;os'
  }
};

document.addEventListener('DOMContentLoaded', function() {
  var chartColors = {
    accent: '#1abc9c',
    accentLight: 'rgba(26,188,156,0.15)',
    accentGrad: 'rgba(26,188,156,0.08)',
    text: '#90a4ae',
    grid: 'rgba(0,0,0,0.04)'
  };

  var ctx = document.getElementById('ventasChart').getContext('2d');
  var chart = null;

  function actualizarGrafico(periodo) {
    var data = chartData[periodo];
    if (!data || data.labels.length === 0) return;

    document.getElementById('chartSubtitle').innerHTML = data.subtitle;

    var gradient = ctx.createLinearGradient(0, 0, 0, 220);
    gradient.addColorStop(0, 'rgba(26,188,156,0.35)');
    gradient.addColorStop(1, 'rgba(26,188,156,0.01)');

    if (chart) chart.destroy();

    chart = new Chart(ctx, {
      type: 'line',
      data: {
        labels: data.labels,
        datasets: [{
          label: 'Ventas ($)',
          data: data.values,
          borderColor: chartColors.accent,
          backgroundColor: gradient,
          fill: true,
          tension: 0.4,
          pointBackgroundColor: '#fff',
          pointBorderColor: chartColors.accent,
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
            grid: { color: chartColors.grid, drawBorder: false },
            ticks: {
              font: { size: 11 },
              color: chartColors.text,
              callback: function(v) { return '$' + v.toLocaleString('es-CO'); }
            }
          },
          x: {
            grid: { display: false },
            ticks: { font: { size: 11 }, color: chartColors.text }
          }
        },
        interaction: { intersect: false, mode: 'index' }
      }
    });
  }

  var tabs = document.querySelectorAll('#periodTabs .tab-btn');
  tabs.forEach(function(tab) {
    tab.addEventListener('click', function() {
      tabs.forEach(function(t) { t.classList.remove('active'); });
      this.classList.add('active');
      actualizarGrafico(this.getAttribute('data-period'));
    });
  });

  actualizarGrafico('semanal');

  document.getElementById('exportPdfBtn').addEventListener('click', function() {
    var btn = this;
    btn.disabled = true;
    btn.innerHTML = '<i class="material-icons left">hourglass_top</i>Generando PDF...';
    var canvas = document.getElementById('ventasChart');
    var dataUrl = canvas.toDataURL('image/png');
    var periodBtn = document.querySelector('#periodTabs .tab-btn.active');
    var periodLabel = periodBtn ? periodBtn.textContent.trim() : 'Semanal';
    var subtitle = document.getElementById('chartSubtitle').innerText || '';
    var xhr = new XMLHttpRequest();
    xhr.open('POST', window.location.href, true);
    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    xhr.responseType = 'blob';
    xhr.onload = function() {
      if (xhr.status === 200) {
        var blob = xhr.response;
        var link = document.createElement('a');
        link.href = URL.createObjectURL(blob);
        link.download = 'reporte_ventas_' + new Date().toISOString().slice(0,10) + '.pdf';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        URL.revokeObjectURL(link.href);
      } else {
        alert('Error al generar el PDF. Intente de nuevo.');
      }
      btn.disabled = false;
      btn.innerHTML = '<i class="material-icons left">download</i>Exportar PDF';
    };
    xhr.onerror = function() {
      alert('Error de conexion al generar el PDF.');
      btn.disabled = false;
      btn.innerHTML = '<i class="material-icons left">download</i>Exportar PDF';
    };
    xhr.send('action=exportarPdf&chartImage=' + encodeURIComponent(dataUrl) + '&periodo=' + encodeURIComponent(periodLabel + ' ' + subtitle));
  });
});
</script>

<div class="content-card" style="padding:22px;margin-top:18px;">
  <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px;">
    <div class="card-title-sm" style="margin:0;">Reportes Guardados</div>
    <div>
      <form method="post" action="${pageContext.request.contextPath}/reportes" style="display:flex;gap:8px;align-items:center;flex-wrap:wrap;">
        <input type="hidden" name="action" value="generar"/>
        <input type="hidden" name="_csrf_token" value="${csrfToken}"/>
        <select name="tipo" required style="height:36px;font-size:13px;padding:0 8px;border:1px solid #cfd8dc;border-radius:6px;background:#fff;">
          <option value="ventas_diarias">Ventas Diarias</option>
          <option value="impuestos">Impuestos</option>
          <option value="inventario">Inventario</option>
        </select>
        <input type="date" name="fechaInicio" required style="height:36px;font-size:13px;padding:0 8px;border:1px solid #cfd8dc;border-radius:6px;">
        <input type="date" name="fechaFin" required style="height:36px;font-size:13px;padding:0 8px;border:1px solid #cfd8dc;border-radius:6px;">
        <button type="submit" class="btn btn-sm waves-effect waves-light" style="height:36px;line-height:36px;padding:0 16px;font-size:12px;">Generar</button>
      </form>
    </div>
  </div>
  <% if (reportesGuardados != null && !reportesGuardados.isEmpty()) { %>
    <table class="highlight responsive-table" style="font-size:13px;">
      <thead><tr>
        <th>ID</th><th>Tipo</th><th>Inicio</th><th>Fin</th><th>Archivo</th><th>Usuario</th><th>Fecha</th>
      </tr></thead>
      <tbody>
        <% for (Reporte r : reportesGuardados) { %>
          <tr>
            <td><%= r.getId() %></td>
            <td><%= r.getTipo() %></td>
            <td><%= r.getFechaInicio() %></td>
            <td><%= r.getFechaFin() %></td>
            <td><%= r.getArchivo() != null ? r.getArchivo() : "-" %></td>
            <td><%= r.getUsuarioNombre() != null ? r.getUsuarioNombre() : "-" %></td>
            <td><%= r.getFechaCreacion() != null ? r.getFechaCreacion().toLocalDate() : "-" %></td>
          </tr>
        <% } %>
      </tbody>
    </table>
  <% } else { %>
    <p style="color:var(--text-muted);text-align:center;padding:8px;">No hay reportes guardados</p>
  <% } %>
</div>

<%@ include file="/WEB-INF/jsp/layout/footer.jsp" %>
