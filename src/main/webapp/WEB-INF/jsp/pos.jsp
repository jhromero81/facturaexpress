<%-- Punto de venta (POS) - DiseÃ±o fiel al prototipo ventas.html --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, com.codewise.facturaexpress.model.*" %>
<%@ include file="/WEB-INF/jsp/layout/header.jsp" %>
<%
  List<Cliente> clientes = (List<Cliente>) request.getAttribute("clientes");
  List<Producto> productos = (List<Producto>) request.getAttribute("productos");
  String error = (String) request.getAttribute("error");
%>

<% if (error != null) { %>
  <div style="color:#e74c3c;background:rgba(231,76,60,0.1);padding:12px 16px;border-radius:8px;margin-bottom:16px;font-size:13px;font-weight:500;"><%= error %></div>
<% } %>

<form id="ventaForm" action="<%= ctx %>/ventas" method="post">
  <input type="hidden" name="_csrf_token" value="<%= request.getAttribute("csrfToken") %>">
  <input type="hidden" name="action" value="finalizar">

  <div class="row" style="margin-bottom:0;">
    <div class="col s12 l8" style="padding-bottom:18px;">
      <div class="content-card">
        <div class="row" style="margin-bottom:16px;">
          <div class="col s6">
            <a class="btn btn-small btn-dark waves-effect waves-light" onclick="limpiarCarrito()">
              <i class="material-icons left">add</i>Nueva Venta
            </a>
          </div>
          <div class="col s6 right-align">
            <a class="btn btn-small btn-teal waves-effect waves-light" onclick="cambiarCliente()">
              <i class="material-icons left">group</i>Cambiar Cliente
            </a>
          </div>
        </div>
        <div class="row" style="margin-bottom:0;">
          <div class="input-field col s6">
            <i class="material-icons prefix" style="color:#1abc9c;">search</i>
            <input id="cId" type="text" value="">
            <label for="cId" class="active">ID Cliente</label>
          </div>
          <div class="input-field col s6">
            <input id="cNombre" type="text" value="" list="clienteList" autocomplete="off" placeholder="Escriba para buscar...">
            <datalist id="clienteList">
              <% if (clientes != null) { for (Cliente c : clientes) { %>
                <option value="<%= c.getNombre().replace("&", "&amp;").replace("\"", "&quot;") %>"><%= c.getId() %></option>
              <% } } %>
            </datalist>
            <label for="cNombre" class="active">Nombre Cliente</label>
          </div>
        </div>
        <input type="hidden" name="clienteId" id="hiddenClienteId">

        <div class="section-heading">AGREGAR PRODUCTO</div>
        <div class="row valign-wrapper" style="margin-bottom:8px;">
          <div class="input-field col s9" style="margin-bottom:0;">
            <i class="material-icons prefix" style="color:#90a4ae;">qr_code_scanner</i>
            <input id="prod" type="text" placeholder="Escriba nombre del producto..." list="productoList">
            <datalist id="productoList">
              <% if (productos != null) { for (Producto p : productos) { %>
                <option value="<%= p.getNombre().replace("&", "&amp;").replace("\"", "&quot;") %>"><%= p.getNombre() %></option>
              <% } } %>
            </datalist>
            <label for="prod">Producto</label>
          </div>
          <div class="col s3">
            <a class="btn btn-dark waves-effect waves-light" style="width:100%;" onclick="agregarDesdeBusqueda()">Agregar</a>
          </div>
        </div>

        <table id="productosTable" class="striped highlight responsive-table">
          <thead>
            <tr>
              <th>Producto</th><th>Cantidad</th><th>Precio Unit.</th><th>Subtotal</th><th></th>
            </tr>
          </thead>
          <tbody id="productosBody">
            <% if (productos != null) { for (Producto p : productos) { %>
              <tr data-id="<%= p.getId() %>" data-nombre="<%= p.getNombre().replace("\"", "&quot;") %>" data-precio="<%= p.getPrecio() %>" style="display:none;">
                <td><strong><%= p.getNombre() %></strong></td>
                <td>
                  <div class="qty-ctrl">
                    <button type="button" class="qty-btn" onclick="cambiarCantidad('<%= p.getId() %>', -1)">&minus;</button>
                    <span class="qty-val" id="qty-<%= p.getId() %>">0</span>
                    <button type="button" class="qty-btn" onclick="cambiarCantidad('<%= p.getId() %>', 1)">+</button>
                  </div>
                </td>
                <td style="font-family:'Space Mono',monospace;">$ <%= String.format("%,.0f", p.getPrecio()) %></td>
                <td style="font-family:'Space Mono',monospace;font-weight:700;" id="subtotal-<%= p.getId() %>">$0</td>
                <td><a class="btn-flat" style="color:#e74c3c;padding:0 4px;" onclick="eliminarProducto('<%= p.getId() %>')"><i class="material-icons">delete_outline</i></a></td>
              </tr>
            <% } } %>
          </tbody>
        </table>
        <div id="carritoVacio" style="text-align:center;padding:24px;color:#90a4ae;font-size:13px;">
          <i class="material-icons" style="font-size:36px;color:#cfd8dc;display:block;margin-bottom:8px;">shopping_cart</i>
          No hay productos en el carrito
        </div>
      </div>
    </div>

    <div class="col s12 l4" style="padding-bottom:18px;">
      <div class="payment-panel">
        <h6>Detalle de Pago</h6>
        <div class="pay-row"><span class="pay-lbl">Subtotal:</span><span class="pay-val" id="subtotalCarrito">$0</span></div>
        <div class="pay-row"><span class="pay-lbl">IVA (19%):</span><span class="pay-val" id="ivaCarrito">$0</span></div>
        <div class="pay-row"><span class="pay-lbl">Descuento:</span><span class="pay-val">
          <input type="number" id="descuentoInput" min="0" max="100" value="0" oninput="actualizarPago()" style="width:40px;text-align:center;font-size:12px;padding:1px 2px;background:rgba(255,255,255,0.08);color:#ecf0f1;border:1px solid rgba(255,255,255,0.12);border-radius:4px;outline:none;font-family:'Space Mono',monospace;">%
          <span id="descuentoMonto" style="margin-left:6px;color:#e74c3c;">-$0</span>
        </span></div>
        <div class="pay-divider"></div>
        <div class="total-lbl" style="font-size:14px;color:#546e7a;font-weight:500;margin-bottom:4px;">Total a pagar:</div>
        <div class="total-val" id="totalCarrito">$0</div>
        <button type="submit" class="btn btn-finalizar waves-effect waves-light" onclick="return validarCarrito()">
          <i class="material-icons left">check</i> FINALIZAR VENTA
        </button>
        <div class="nota-genera" style="text-align:center;font-size:11px;color:#90a4ae;margin-top:12px;text-transform:uppercase;letter-spacing:0.5px;">Genera XML y PDF autom&aacute;ticamente</div>
      </div>
    </div>
  </div>
</form>

<script>
var carrito = {};
var productosData = [];
<% if (productos != null) { for (Producto p : productos) {
  String nom = p.getNombre().replace("\\", "\\\\").replace("'", "\\'");
%>
  productosData.push({ id: '<%= p.getId() %>', nombre: '<%= nom %>', precio: <%= p.getPrecio() %>, stock: <%= p.getStock() %> });
<% } } %>

function agregarProducto(id) {
  var prod = productosData.find(function(p) { return String(p.id) === String(id); });
  if (!prod) return;

  if (carrito[id] && carrito[id] >= prod.stock) {
    if (typeof M !== 'undefined' && M.toast) {
      M.toast({html: '<i class="material-icons left" style="font-size:16px;">warning</i>Stock insuficiente', classes: 'rounded', displayLength: 3000});
    }
    return;
  }

  carrito[id] = (carrito[id] || 0) + 1;
  mostrarFila(id);
  actualizarUI(id);
  actualizarPago();
}

function cambiarCantidad(id, delta) {
  if (!carrito[id]) return;
  var nueva = carrito[id] + delta;
  if (nueva <= 0) {
    eliminarProducto(id);
    return;
  }
  var prod = productosData.find(function(p) { return String(p.id) === String(id); });
  if (prod && nueva > prod.stock) {
    if (typeof M !== 'undefined' && M.toast) {
      M.toast({html: '<i class="material-icons left" style="font-size:16px;">warning</i>Stock insuficiente', classes: 'rounded', displayLength: 3000});
    }
    return;
  }
  carrito[id] = nueva;
  actualizarUI(id);
  actualizarPago();
}

function eliminarProducto(id) {
  delete carrito[id];
  var row = document.querySelector('#productosBody tr[data-id="' + id + '"]');
  if (row) row.style.display = 'none';
  actualizarPago();
  verificarCarritoVacio();
}

function mostrarFila(id) {
  var row = document.querySelector('#productosBody tr[data-id="' + id + '"]');
  if (row) {
    row.style.display = '';
    document.getElementById('carritoVacio').style.display = 'none';
  }
}

function actualizarUI(id) {
  var qty = carrito[id] || 0;
  var prod = productosData.find(function(p) { return String(p.id) === String(id); });
  var qtySpan = document.getElementById('qty-' + id);
  var subSpan = document.getElementById('subtotal-' + id);
  if (qtySpan) qtySpan.textContent = qty;
  if (subSpan && prod) subSpan.textContent = '$' + (prod.precio * qty).toLocaleString('es-CO');
}

function actualizarPago() {
  var subtotal = 0;
  var ids = Object.keys(carrito);
  ids.forEach(function(id) {
    var prod = productosData.find(function(p) { return String(p.id) === String(id); });
    if (prod) subtotal += prod.precio * carrito[id];
  });
  var input = document.getElementById('descuentoInput');
  var pct = Math.min(100, Math.max(0, parseInt(input.value) || 0));
  input.value = pct;
  var descuento = subtotal * pct / 100;
  document.getElementById('descuentoMonto').textContent = '-$' + descuento.toLocaleString('es-CO');
  var base = subtotal - descuento;
  var iva = base * 0.19;
  var total = base + iva;
  document.getElementById('subtotalCarrito').textContent = '$' + subtotal.toLocaleString('es-CO');
  document.getElementById('ivaCarrito').textContent = '$' + iva.toLocaleString('es-CO');
  document.getElementById('totalCarrito').textContent = '$' + total.toLocaleString('es-CO');
}

function limpiarCarrito() {
  var ids = Object.keys(carrito);
  ids.forEach(function(id) {
    var row = document.querySelector('#productosBody tr[data-id="' + id + '"]');
    if (row) {
      row.style.display = 'none';
      var qtySpan = document.getElementById('qty-' + id);
      if (qtySpan) qtySpan.textContent = '0';
      var subSpan = document.getElementById('subtotal-' + id);
      if (subSpan) subSpan.textContent = '$0';
    }
  });
  carrito = {};
  actualizarPago();
  verificarCarritoVacio();
  document.getElementById('cId').value = '';
  document.getElementById('cNombre').value = '';
  document.getElementById('hiddenClienteId').value = '';
}

function verificarCarritoVacio() {
  var empty = document.getElementById('carritoVacio');
  if (empty) {
    empty.style.display = Object.keys(carrito).length === 0 ? '' : 'none';
  }
}

function validarCarrito() {
  if (Object.keys(carrito).length === 0) {
    if (typeof M !== 'undefined' && M.toast) {
      M.toast({html: '<i class="material-icons left" style="font-size:16px;">warning</i>Debe agregar al menos un producto', classes: 'rounded', displayLength: 3000});
    } else {
      alert('Debe agregar al menos un producto al carrito');
    }
    return false;
  }
  var clienteId = document.getElementById('hiddenClienteId').value;
  if (!clienteId) {
    if (typeof M !== 'undefined' && M.toast) {
      M.toast({html: '<i class="material-icons left" style="font-size:16px;">warning</i>Seleccione un cliente', classes: 'rounded', displayLength: 3000});
    } else {
      alert('Seleccione un cliente');
    }
    return false;
  }
  // Agregar descuento como input oculto
  var form = document.getElementById('ventaForm');
  var dtoInput = document.createElement('input');
  dtoInput.type = 'hidden'; dtoInput.name = 'descuentoPorcentaje';
  dtoInput.value = document.getElementById('descuentoInput').value;
  form.appendChild(dtoInput);

  var ids = Object.keys(carrito);
  ids.forEach(function(id) {
    var prod = productosData.find(function(p) { return String(p.id) === String(id); });
    if (!prod) return;
    var in1 = document.createElement('input');
    in1.type = 'hidden'; in1.name = 'productoId'; in1.value = id;
    form.appendChild(in1);
    var in2 = document.createElement('input');
    in2.type = 'hidden'; in2.name = 'cantidad'; in2.value = carrito[id];
    form.appendChild(in2);
    var in3 = document.createElement('input');
    in3.type = 'hidden'; in3.name = 'precioUnitario'; in3.value = prod.precio;
    form.appendChild(in3);
  });
  return true;
}

var clientesData = [];
<% if (clientes != null) { for (Cliente c : clientes) {
  String cnom = c.getNombre().replace("\\", "\\\\").replace("'", "\\'");
%>
  clientesData.push({ id: '<%= c.getId() %>', nombre: '<%= cnom %>' });
<% } } %>

function cambiarCliente() {
  var input = document.getElementById('cNombre');
  if (!input || !input.value.trim()) {
    if (typeof M !== 'undefined' && M.toast) {
      M.toast({html: '<i class="material-icons left" style="font-size:16px;">info</i>Escriba un nombre en el campo Nombre Cliente', classes: 'rounded', displayLength: 3000});
    }
    input.focus();
    return;
  }
  seleccionarCliente(input.value.trim());
}

function seleccionarCliente(termino) {
  if (!termino) return;
  var t = termino.toLowerCase();
  var found = clientesData.filter(function(c) { return c.nombre.toLowerCase().includes(t) || String(c.id) === t; });
  if (found.length === 0) {
    if (typeof M !== 'undefined' && M.toast) {
      M.toast({html: '<i class="material-icons left" style="font-size:16px;">error</i>Cliente no encontrado', classes: 'rounded', displayLength: 3000});
    }
    return;
  }
  if (found.length === 1) {
    document.getElementById('cId').value = found[0].id;
    document.getElementById('cNombre').value = found[0].nombre;
    document.getElementById('hiddenClienteId').value = found[0].id;
    if (typeof M !== 'undefined' && M.toast) {
      M.toast({html: '<i class="material-icons left" style="font-size:16px;">check</i>Cliente: ' + found[0].nombre, classes: 'rounded', displayLength: 2000});
    }
  } else {
    if (typeof M !== 'undefined' && M.toast) {
      M.toast({html: '<i class="material-icons left" style="font-size:16px;">info</i>Multiples resultados, sea m&aacute;s espec&iacute;fico', classes: 'rounded', displayLength: 3000});
    }
  }
}

document.addEventListener('DOMContentLoaded', function() {
  var prodInput = document.getElementById('prod');
  if (prodInput) {
    prodInput.addEventListener('keypress', function(e) {
      if (e.key === 'Enter') {
        e.preventDefault();
        agregarDesdeBusqueda();
      }
    });
  }
  var cNombreInput = document.getElementById('cNombre');
  if (cNombreInput) {
    cNombreInput.addEventListener('change', function() {
      seleccionarCliente(this.value.trim());
    });
  }
});

function agregarDesdeBusqueda() {
  var input = document.getElementById('prod');
  if (!input || !input.value.trim()) return;
  var termino = input.value.trim().toLowerCase();
  var found = productosData.filter(function(p) { return p.nombre.toLowerCase().includes(termino) || String(p.id) === termino; });
  if (found.length === 0) {
    if (typeof M !== 'undefined' && M.toast) {
      M.toast({html: '<i class="material-icons left" style="font-size:16px;">info</i>Producto no encontrado', classes: 'rounded', displayLength: 3000});
    }
    return;
  }
  if (found.length === 1) {
    agregarProducto(found[0].id);
    input.value = '';
    input.focus();
  } else {
    if (typeof M !== 'undefined' && M.toast) {
      M.toast({html: '<i class="material-icons left" style="font-size:16px;">info</i>Multiples resultados, sea m&aacute;s espec&iacute;fico', classes: 'rounded', displayLength: 3000});
    }
  }
}

</script>

<%@ include file="/WEB-INF/jsp/layout/footer.jsp" %>
