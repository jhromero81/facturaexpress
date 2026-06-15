<%-- Formulario para crear una factura con tabla dinámica de productos --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, com.codewise.facturaexpress.model.*" %>
<%@ include file="/WEB-INF/jsp/layout/header.jsp" %>
<%-- Scriptlet: listas de clientes y productos para los selects del formulario --%>
<%
  List<Cliente> clientes = (List<Cliente>) request.getAttribute("clientes");
  List<Producto> productos = (List<Producto>) request.getAttribute("productos");
  String error = (String) request.getAttribute("error");
%>

<%-- Muestra error si existe --%>
<% if (error != null) { %>
  <div style="color:#e74c3c;background:rgba(231,76,60,0.1);padding:12px 16px;border-radius:8px;margin-bottom:16px;font-size:13px;"><i class="material-icons" style="font-size:16px;vertical-align:middle;">error_outline</i> <%= error %></div>
<% } %>

<div class="form-card" style="max-width:800px;">
  <div style="font-family:'Space Mono',monospace;font-size:18px;font-weight:700;color:var(--text-dark);margin-bottom:24px;">
    Nueva Factura
  </div>

  <%-- Formulario: envía vía POST a /facturas con acción "guardar" --%>
  <form action="<%= ctx %>/facturas" method="post">
    <input type="hidden" name="action" value="guardar">

    <%-- Selector de cliente --%>
    <div class="input-field">
      <select id="clienteId" name="clienteId" required>
        <option value="" disabled selected>Seleccione un cliente</option>
        <% if (clientes != null) { for (Cliente c : clientes) { %>
          <option value="<%= c.getId() %>"><%= c.getNombre() %></option>
        <% } } %>
      </select>
      <label>Cliente *</label>
    </div>

<%-- Tabla dinámica de productos: cada fila es un detalle de factura (producto, cantidad, precio, subtotal, eliminar) --%>
    <div class="section-heading" style="text-align:left;margin-top:24px;">DETALLES DE LA FACTURA</div>

    <table class="striped highlight responsive-table" id="lineItemsTable">
      <thead>
        <tr><th style="width:40%;">Producto</th><th style="width:15%;">Cantidad</th><th style="width:15%;">Precio Unit.</th><th style="width:15%;">Subtotal</th><th style="width:10%;"></th></tr>
      </thead>
      <tbody>
        <tr>
          <td>
            <%-- Select de producto con data-precio para autocompletar el precio unitario --%>
            <select name="productoId" class="browser-default" style="width:100%;padding:6px 8px;border:1px solid #cfd8dc;border-radius:6px;font-size:13px;" required>
              <option value="">Seleccione...</option>
              <%-- Iteración sobre productos para llenar el select --%>
              <% if (productos != null) { for (Producto p : productos) { %>
                <option value="<%= p.getId() %>" data-precio="<%= p.getPrecio() %>"><%= p.getNombre() %></option>
              <% } } %>
            </select>
          </td>
          <td><input type="number" name="cantidad" min="1" value="1" required style="width:70px;padding:6px 8px;border:1px solid #cfd8dc;border-radius:6px;font-size:13px;"></td>
          <td><input type="number" name="precioUnitario" step="0.01" min="0" required style="width:100px;padding:6px 8px;border:1px solid #cfd8dc;border-radius:6px;font-size:13px;"></td>
          <td class="subtotal-cell" style="font-family:'Space Mono',monospace;font-weight:600;">$0</td>
          <td style="text-align:center;"><a class="btn-flat remove-line-item" style="color:#e74c3c;padding:0 4px;"><i class="material-icons">delete_outline</i></a></td>
        </tr>
      </tbody>
    </table>

    <div style="margin-top:8px;">
      <a class="btn-flat" style="color:var(--accent);font-weight:600;padding:0;" id="addLineItem"><i class="material-icons left" style="font-size:18px;">add_circle</i>Agregar producto</a>
    </div>

    <div style="text-align:right;margin-top:16px;padding-top:16px;border-top:2px solid var(--border);">
      <span style="font-size:11px;color:var(--text-muted);text-transform:uppercase;letter-spacing:.5px;">Total:</span>
      <span class="total-amount" id="totalFactura" style="font-family:'Space Mono',monospace;font-size:22px;font-weight:700;color:var(--accent);margin-left:12px;">$0</span>
    </div>

    <%-- Select oculto usado como template para clonar filas de productos --%>
    <select id="productoSelectTemplate" style="display:none;">
      <%-- Iteración sobre productos para llenar el template --%>
      <% if (productos != null) { for (Producto p : productos) { %>
        <option value="<%= p.getId() %>" data-precio="<%= p.getPrecio() %>"><%= p.getNombre() %></option>
      <% } } %>
    </select>

    <div class="form-actions">
      <button type="submit" class="btn btn-teal waves-effect waves-light"><i class="material-icons left">check</i>Crear Factura</button>
      <a href="<%= ctx %>/facturas" class="btn btn-cancel-compact waves-effect" style="display:inline-flex;align-items:center;gap:8px;height:38px;line-height:38px;padding:0 20px;border-radius:10px;text-decoration:none;font-size:13px;font-weight:600;">Cancelar</a>
    </div>
  </form>
</div>

<%-- JS: cuando se selecciona un producto, autocompleta el precio unitario desde data-precio --%>
<script>
document.querySelector('#lineItemsTable tbody').addEventListener('change', function(e) {
  // Detecta cambio en cualquier select de producto dentro de la tabla dinámica
  if (e.target.matches('select[name="productoId"]')) {
    var sel = e.target, opt = sel.options[sel.selectedIndex];
    var precio = opt.getAttribute('data-precio');
    if (precio) {
      var row = e.target.closest('tr');
      if (row) row.querySelector('input[name="precioUnitario"]').value = precio;
    }
  }
});
</script>

<%@ include file="/WEB-INF/jsp/layout/footer.jsp" %>
