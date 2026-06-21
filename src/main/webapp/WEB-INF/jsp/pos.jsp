<%-- Punto de venta (POS): catálogo + carrito de compras --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, com.codewise.facturaexpress.model.*" %>
<%@ include file="/WEB-INF/jsp/layout/header.jsp" %>
<%-- Scriptlet: listas de clientes y productos desde el request --%>
<%
  List<Cliente> clientes = (List<Cliente>) request.getAttribute("clientes");
  List<Producto> productos = (List<Producto>) request.getAttribute("productos");
  String error = (String) request.getAttribute("error");
%>

<% if (error != null) { %>
  <div style="color:#e74c3c;background:rgba(231,76,60,0.1);padding:12px 16px;border-radius:8px;margin-bottom:16px;font-size:13px;"><%= error %></div>
<% } %>

<%-- Formulario de venta: envía el carrito a /ventas con acción "finalizar" --%>
<form id="ventaForm" action="<%= ctx %>/ventas" method="post">
  <input type="hidden" name="_csrf_token" value="<%= request.getAttribute("csrfToken") %>">
  <input type="hidden" name="action" value="finalizar">

  <div class="row" style="margin-bottom:0;">
    <div class="col s12 l8" style="padding-bottom:18px;">
      <div class="content-card">

        <div class="row" style="margin-bottom:16px;">
          <div class="col s6">
            <a class="btn btn-small btn-dark waves-effect waves-light" onclick="limpiarCarrito()"><i class="material-icons left">add</i>Nueva Venta</a>
          </div>
          <div class="col s6 right-align">
            <div class="input-field" style="margin:0;display:inline-block;width:auto;min-width:200px;">
              <select id="clienteId" name="clienteId" class="browser-default" style="display:inline;width:auto;padding:6px 12px;border:1px solid #cfd8dc;border-radius:6px;font-size:13px;">
                <option value="">Seleccionar cliente...</option>
                <% if (clientes != null) { for (Cliente c : clientes) { %>
                  <option value="<%= c.getId() %>"><%= c.getNombre() %></option>
                <% } } %>
              </select>
            </div>
          </div>
        </div>

<%-- Sección: buscador de productos en vivo --%>
        <div class="section-heading">AGREGAR PRODUCTO</div>
        <div class="row valign-wrapper" style="margin-bottom:8px;">
          <div class="input-field col s9" style="margin-bottom:0;">
            <i class="material-icons prefix" style="color:#90a4ae;">qr_code_scanner</i>
            <input id="buscarProducto" type="text" placeholder="Buscar producto...">
            <label for="buscarProducto">Producto</label>
          </div>
        </div>

        <%-- Grid de tarjetas de productos: cada una con datos id, nombre, precio. Al hacer clic se agrega al carrito --%>
        <div class="row" id="productosGrid" style="margin-bottom:0;">
          <%-- Iteración sobre todos los productos disponibles --%>
          <% if (productos != null) { for (Producto p : productos) { %>
            <div class="col s6 m4 l3" style="margin-bottom:12px;">
              <div class="producto-card content-card" style="padding:16px;cursor:pointer;text-align:center;border:2px solid var(--border);margin:0;height:100%;"
                   data-id="<%= p.getId() %>" data-nombre="<%= p.getNombre() %>" data-precio="<%= p.getPrecio() %>"
                   onclick="agregarAlCarrito(this)">
                <i class="material-icons" style="font-size:32px;color:var(--accent);margin-bottom:4px;">inventory_2</i>
                <div style="font-weight:600;font-size:0.85rem;"><%= p.getNombre() %></div>
                <div style="color:var(--accent);font-weight:700;font-family:'Space Mono',monospace;font-size:14px;">$ <%= String.format("%,.0f", p.getPrecio()) %></div>
                <div style="font-size:0.75rem;color:var(--text-muted);">Stock: <%= p.getStock() %></div>
              </div>
            </div>
          <% } } %>
        </div>

<%-- Tabla del carrito de compras con controles de cantidad y eliminación --%>
        <div class="section-heading" style="margin-top:16px;">CARRITO DE VENTAS</div>
        <table class="striped highlight responsive-table">
          <thead>
            <tr><th>Producto</th><th>Cantidad</th><th>Precio Unit.</th><th>Subtotal</th><th></th></tr>
          </thead>
          <tbody id="carritoBody">
          </tbody>
        </table>
      </div>
    </div>

<%-- Panel lateral de pago: subtotal, IVA, total y botón finalizar --%>
    <div class="col s12 l4" style="padding-bottom:18px;">
      <div class="payment-panel">
        <h6>Detalle de Pago</h6>
        <div class="pay-row"><span class="pay-lbl">Subtotal:</span><span class="pay-val" id="subtotalCarrito">$0</span></div>
        <div class="pay-row"><span class="pay-lbl">IVA (19%):</span><span class="pay-val" id="ivaCarrito">$0</span></div>
        <div class="pay-row"><span class="pay-lbl">Descuento (0%):</span><span class="pay-val">$0</span></div>
        <div class="pay-divider"></div>
        <div class="total-lbl">Total a pagar:</div>
        <div class="total-val" id="totalCarrito">$0</div>
        <button type="submit" class="btn btn-finalizar waves-effect waves-light" onclick="return validarCarrito()"><i class="material-icons left">check</i> FINALIZAR VENTA</button>
        <div class="nota-genera">Genera XML y PDF autom&aacute;ticamente</div>
      </div>
    </div>
  </div>
</form>

<%-- JS del carrito: array global, funciones de agregar, renderizar, modificar cantidad, eliminar, limpiar y validar --%>
<script>
var carrito = [];

// Agrega un producto al carrito o incrementa su cantidad si ya existe
function agregarAlCarrito(el) {
  var id = el.dataset.id;
  var nombre = el.dataset.nombre;
  var precio = parseFloat(el.dataset.precio);
  var existente = carrito.find(function(item) { return item.id === id; });
  if (existente) {
    existente.cantidad++;
  } else {
    carrito.push({ id: id, nombre: nombre, precio: precio, cantidad: 1 });
  }
  renderizarCarrito();
  el.style.borderColor = 'var(--accent)';
  setTimeout(function() { el.style.borderColor = 'var(--border)'; }, 500);
}

// Renderiza la tabla del carrito y actualiza subtotal, IVA y total
function renderizarCarrito() {
  var tbody = document.getElementById('carritoBody');
  tbody.innerHTML = '';
  var subtotal = 0;
  // Itera sobre cada ítem del carrito para generar las filas de la tabla
  carrito.forEach(function(item, index) {
    var sub = item.precio * item.cantidad;
    subtotal += sub;
    var tr = document.createElement('tr');
    tr.innerHTML = '<td><strong>' + item.nombre + '</strong></td>' +
      '<td><div class="qty-ctrl"><button type="button" class="qty-btn" onclick="cambiarCantidad(' + index + ',-1)">−</button><span class="qty-val">' + item.cantidad + '</span><button type="button" class="qty-btn" onclick="cambiarCantidad(' + index + ',1)">+</button></div></td>' +
      '<td style="font-family:\'Space Mono\',monospace;">$' + item.precio.toLocaleString('es-CO') + '</td>' +
      '<td style="font-family:\'Space Mono\',monospace;font-weight:700;">$' + sub.toLocaleString('es-CO') + '</td>' +
      '<td><a class="btn-flat" style="color:#e74c3c;padding:0 4px;" onclick="eliminarDelCarrito(' + index + ')"><i class="material-icons">delete_outline</i></a></td>' +
      '<input type="hidden" name="productoId" value="' + item.id + '">' +
      '<input type="hidden" name="cantidad" value="' + item.cantidad + '">' +
      '<input type="hidden" name="precioUnitario" value="' + item.precio + '">';
    tbody.appendChild(tr);
  });
  var iva = subtotal * 0.19;
  var total = subtotal + iva;
  document.getElementById('subtotalCarrito').textContent = '$' + subtotal.toLocaleString('es-CO');
  document.getElementById('ivaCarrito').textContent = '$' + iva.toLocaleString('es-CO');
  document.getElementById('totalCarrito').textContent = '$' + total.toLocaleString('es-CO');
}

// Cambia la cantidad de un ítem (mínimo 1)
function cambiarCantidad(index, delta) {
  carrito[index].cantidad = Math.max(1, carrito[index].cantidad + delta);
  renderizarCarrito();
}

// Elimina un ítem del carrito por índice
function eliminarDelCarrito(index) {
  carrito.splice(index, 1);
  renderizarCarrito();
}

// Vacía el carrito por completo
function limpiarCarrito() {
  carrito = [];
  renderizarCarrito();
}

// Valida que el carrito tenga productos y que se haya seleccionado un cliente
function validarCarrito() {
  // Verifica que haya al menos un producto
  if (carrito.length === 0) {
    if (typeof M !== 'undefined' && M.toast) {
      M.toast({html: '<i class="material-icons left" style="font-size:16px;">warning</i>Debe agregar al menos un producto', classes: 'rounded', displayLength: 3000});
    } else {
      alert('Debe agregar al menos un producto al carrito');
    }
    return false;
  }
  // Verifica que se haya seleccionado un cliente
  var clienteSelect = document.getElementById('clienteId');
  if (!clienteSelect.value) {
    if (typeof M !== 'undefined' && M.toast) {
      M.toast({html: '<i class="material-icons left" style="font-size:16px;">warning</i>Seleccione un cliente', classes: 'rounded', displayLength: 3000});
    } else {
      alert('Seleccione un cliente');
    }
    return false;
  }
  return true;
}

// Filtro en vivo del catálogo de productos por nombre
document.getElementById('buscarProducto').addEventListener('input', function() {
  var termino = this.value.toLowerCase();
  document.querySelectorAll('.producto-card').forEach(function(card) {
    var nombre = card.dataset.nombre.toLowerCase();
    card.closest('.col').style.display = nombre.includes(termino) ? '' : 'none';
  });
});
</script>

<style>
.producto-card:hover { border-color: var(--accent) !important; transform: translateY(-2px); box-shadow: 0 6px 20px rgba(26,188,156,0.1) !important; }
.producto-card:active { transform: scale(0.98); }
</style>

<script src="<%= ctx %>/js/ventas.js"></script>
<%@ include file="/WEB-INF/jsp/layout/footer.jsp" %>
