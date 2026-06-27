<%-- Listado de facturas electrÃ³nicas - fiel al prototipo facturacion.html --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, com.codewise.facturaexpress.model.Factura, java.time.format.DateTimeFormatter, java.math.BigDecimal" %>
<%@ include file="/WEB-INF/jsp/layout/header.jsp" %>
<%
  List<Factura> facturas = (List<Factura>) request.getAttribute("facturas");
  String mensaje = (String) request.getAttribute("mensaje");
  String error = (String) request.getAttribute("error");
%>

<% if (mensaje != null) { %>
  <div style="color:#27ae60;background:rgba(39,174,96,0.1);padding:12px 16px;border-radius:8px;margin-bottom:16px;font-size:13px;font-weight:500;"><i class="material-icons" style="font-size:16px;vertical-align:middle;">check_circle</i> <%= mensaje %></div>
<% } %>
<% if (error != null) { %>
  <div style="color:#e74c3c;background:rgba(231,76,60,0.1);padding:12px 16px;border-radius:8px;margin-bottom:16px;font-size:13px;font-weight:500;"><i class="material-icons" style="font-size:16px;vertical-align:middle;">error_outline</i> <%= error %></div>
<% } %>

<div class="content-card">
  <div style="display:flex;align-items:center;gap:12px;flex-wrap:wrap;margin-bottom:16px;">
    <div class="input-field" style="margin:0;flex:1;min-width:220px;">
      <i class="material-icons prefix" style="color:#90a4ae;">search</i>
      <input id="bFac" type="text" placeholder="Buscar factura o cliente...">
      <label for="bFac">B&uacute;squeda</label>
    </div>
    <a class="btn-flat waves-effect" style="border:1px solid #cfd8dc;border-radius:6px;color:#546e7a;font-weight:600;font-size:13px;height:36px;line-height:36px;padding:0 16px;" onclick="mostrarFiltros()">
      <i class="material-icons left" style="font-size:18px;">filter_list</i>Filtros
    </a>
    <a class="btn btn-dark waves-effect waves-light" style="height:36px;line-height:36px;padding:0 16px;font-size:13px;border-radius:8px;" onclick="exportarReporte()">
      <i class="material-icons left" style="font-size:18px;">download</i>Exportar Reporte
    </a>
    <a class="btn btn-teal waves-effect waves-light" style="height:36px;line-height:36px;padding:0 16px;font-size:13px;border-radius:8px;" href="<%= ctx %>/facturas?action=nuevo">
      <i class="material-icons left" style="font-size:18px;">add</i>Nueva Factura
    </a>
  </div>

<% if (facturas == null || facturas.isEmpty()) { %>
  <div style="text-align:center;padding:40px;">
    <i class="material-icons" style="font-size:48px;color:var(--text-muted);">receipt_long</i>
    <p style="color:var(--text-muted);margin:16px 0;font-size:14px;">No hay facturas registradas</p>
    <a href="<%= ctx %>/facturas?action=nuevo" class="btn btn-teal waves-effect waves-light"><i class="material-icons left">add</i>Crear primera factura</a>
  </div>
<% } else { %>
  <table class="striped highlight responsive-table">
    <thead>
      <tr>
        <th>No. Factura</th>
        <th>Fecha de Emisi&oacute;n</th>
        <th>Cliente</th>
        <th>Estado DIAN</th>
        <th>Monto Total</th>
        <th>Documentos</th>
      </tr>
    </thead>
    <tbody id="facturasBody">
<% for (Factura f : facturas) {
  String est = f.getEstado() != null ? f.getEstado() : "PENDIENTE";
  String estadoColor, estadoIcono, estadoTexto;
  switch (est) {
    case "PAGADA":    estadoColor = "#1abc9c"; estadoIcono = "check_circle"; estadoTexto = "Enviado";    break;
    case "ANULADA":   estadoColor = "#e74c3c"; estadoIcono = "error";        estadoTexto = "Rechazado";  break;
    case "PENDIENTE":
    default:          estadoColor = "#f39c12"; estadoIcono = "schedule";     estadoTexto = "Pendiente";  break;
  }
  String fechaStr = f.getFecha() != null ? f.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yy")) : "-";
  String clienteStr = f.getClienteNombre() != null ? f.getClienteNombre() : "Cliente #" + f.getClienteId();
  BigDecimal totalVal = f.getTotal();
  String totalStr = totalVal != null ? "$ " + String.format("%,.0f", totalVal) : "$ 0";
  String numFac = "FAC-" + String.format("%06d", f.getId());
%>
      <tr data-numero="<%= numFac %>" data-cliente="<%= clienteStr.toLowerCase() %>" data-estado="<%= est %>">
        <td><strong><%= numFac %></strong></td>
        <td><%= fechaStr %></td>
        <td>
          <%= clienteStr %><br>
          <small style="color:#90a4ae;">ID: <%= f.getClienteId() %></small>
        </td>
        <td>
          <span class="badge-status inline-flex" style="background:<%= estadoColor %>15;color:<%= estadoColor %>;display:inline-flex;align-items:center;gap:4px;padding:3px 10px;border-radius:20px;font-size:11px;font-weight:700;">
            <i class="material-icons" style="font-size:12px;"><%= estadoIcono %></i> <%= estadoTexto %>
          </span>
        </td>
        <td style="font-family:'Space Mono',monospace;font-weight:700;"><%= totalStr %></td>
        <td>
          <a class="btn-flat btn-small" style="color:#546e7a;padding:0 4px;" href="<%= ctx %>/facturas?action=detalle&id=<%= f.getId() %>">
            <i class="material-icons tiny">picture_as_pdf</i> PDF
          </a>
          <a class="btn-flat btn-small" style="color:#1abc9c;padding:0 4px;" onclick="descargarXML('<%= numFac %>')">
            <i class="material-icons tiny">download</i> XML
          </a>
        </td>
      </tr>
<% } %>
    </tbody>
  </table>

  <div id="paginacion" style="display:flex;justify-content:flex-end;align-items:center;gap:8px;margin-top:16px;">
    <a class="btn-flat waves-effect" id="prevPageBtn" style="color:#90a4ae;"><i class="material-icons">chevron_left</i></a>
    <span style="font-size:13px;color:#546e7a;">P&aacute;gina <strong id="currentPage">1</strong> de <span id="totalPages">1</span></span>
    <a class="btn-flat waves-effect" id="nextPageBtn" style="color:#1abc9c;"><i class="material-icons">chevron_right</i></a>
  </div>
<% } %>
</div>

<script>
var ITEMS_PER_PAGE = 10;
var currentPage = 1;
var filteredRows = [];

function getDataRows() {
  return Array.from(document.querySelectorAll('#facturasBody tr'));
}

function filterRows(termino, estado) {
  var rows = getDataRows();
  filteredRows = rows.filter(function(row) {
    var texto = ((row.getAttribute('data-cliente') || '') + ' ' + (row.getAttribute('data-numero') || '')).toLowerCase();
    var matchTermino = !termino || texto.indexOf(termino.toLowerCase()) !== -1;
    var matchEstado = !estado || estado === 'todos' || row.getAttribute('data-estado') === estado;
    return matchTermino && matchEstado;
  });
  return filteredRows;
}

function renderPage(page, rows) {
  var total = rows.length;
  var totalPages = Math.ceil(total / ITEMS_PER_PAGE) || 1;
  if (page < 1) page = 1;
  if (page > totalPages) page = totalPages;
  currentPage = page;

  var start = (page - 1) * ITEMS_PER_PAGE;
  var end = start + ITEMS_PER_PAGE;
  var pageRows = rows.slice(start, end);

  var allRows = getDataRows();
  allRows.forEach(function(r) { r.style.display = 'none'; });
  pageRows.forEach(function(r) { r.style.display = ''; });

  document.getElementById('currentPage').textContent = page;
  document.getElementById('totalPages').textContent = totalPages;

  var prevBtn = document.getElementById('prevPageBtn');
  var nextBtn = document.getElementById('nextPageBtn');
  if (prevBtn) { prevBtn.style.color = page === 1 ? '#90a4ae' : '#1abc9c'; prevBtn.style.cursor = page === 1 ? 'not-allowed' : 'pointer'; }
  if (nextBtn) { nextBtn.style.color = page === totalPages ? '#90a4ae' : '#1abc9c'; nextBtn.style.cursor = page === totalPages ? 'not-allowed' : 'pointer'; }
}

document.addEventListener('DOMContentLoaded', function() {
  filteredRows = filterRows('', 'todos');
  renderPage(1, filteredRows);

  var searchInput = document.getElementById('bFac');
  var searchTimer;
  searchInput.addEventListener('input', function() {
    clearTimeout(searchTimer);
    searchTimer = setTimeout(function() {
      filteredRows = filterRows(searchInput.value, window._filtroEstado || 'todos');
      renderPage(1, filteredRows);
    }, 300);
  });

  document.getElementById('prevPageBtn').addEventListener('click', function(e) {
    e.preventDefault();
    if (currentPage > 1) renderPage(currentPage - 1, filteredRows);
  });
  document.getElementById('nextPageBtn').addEventListener('click', function(e) {
    e.preventDefault();
    if (currentPage < Math.ceil(filteredRows.length / ITEMS_PER_PAGE)) renderPage(currentPage + 1, filteredRows);
  });
});

window._filtroEstado = 'todos';
function mostrarFiltros() {
  var opcion = prompt('Filtrar por estado:\n1 - Todos\n2 - Enviados\n3 - Pendientes\n4 - Rechazados');
  if (!opcion) return;
  var map = { '1': 'todos', '2': 'PAGADA', '3': 'PENDIENTE', '4': 'ANULADA' };
  if (map[opcion]) {
    window._filtroEstado = map[opcion];
    filteredRows = filterRows(document.getElementById('bFac').value, window._filtroEstado);
    renderPage(1, filteredRows);
    if (typeof M !== 'undefined' && M.toast) {
      M.toast({html: '<i class="material-icons left" style="font-size:16px;">check</i>Filtro aplicado', classes: 'rounded', displayLength: 2000});
    }
  } else {
    alert('Opcion invalida (ingrese 1-4)');
  }
}

function exportarReporte() {
  var visible = getDataRows().filter(function(r) { return r.style.display !== 'none'; });
  if (visible.length === 0) {
    if (typeof M !== 'undefined' && M.toast) {
      M.toast({html: '<i class="material-icons left" style="font-size:16px;">info</i>No hay datos para exportar', classes: 'rounded', displayLength: 2000});
    }
    return;
  }
  var csv = '\uFEFFN\u00famero Factura,Fecha,Cliente,Estado,Total\n';
  visible.forEach(function(r) {
    var cells = r.querySelectorAll('td');
    var num  = cells[0] ? cells[0].textContent.trim() : '';
    var fecha = cells[1] ? cells[1].textContent.trim() : '';
    var cli  = cells[2] ? cells[2].textContent.trim().replace(/\s*ID:.*$/, '') : '';
    var est  = cells[3] ? cells[3].textContent.trim() : '';
    var tot  = cells[4] ? cells[4].textContent.trim().replace(/[^0-9.,]/g, '') : '';
    csv += '"' + num + '","' + fecha + '","' + cli + '","' + est + '","' + tot + '"\n';
  });
  var blob = new Blob([csv], { type: 'text/csv;charset=utf-8' });
  var a = document.createElement('a');
  a.href = URL.createObjectURL(blob);
  a.download = 'reporte_facturas_' + new Date().toISOString().slice(0, 10) + '.csv';
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  URL.revokeObjectURL(a.href);
  if (typeof M !== 'undefined' && M.toast) {
    M.toast({html: '<i class="material-icons left" style="font-size:16px;">check</i>Reporte exportado', classes: 'rounded', displayLength: 2000});
  }
}

function descargarXML(numero) {
  if (typeof M !== 'undefined' && M.toast) {
    M.toast({html: '<i class="material-icons left" style="font-size:16px;">info</i>Generaci\u00f3n XML pr\u00f3ximamente', classes: 'rounded', displayLength: 2000});
  }
}
</script>

<script src="<%= ctx %>/js/facturacion.js"></script>
<%@ include file="/WEB-INF/jsp/layout/footer.jsp" %>
