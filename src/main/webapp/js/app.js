// Al cargar el DOM, configura las funcionalidades de la aplicación
document.addEventListener('DOMContentLoaded', function () {
  setupLineItems();
  setupAutoSubtotals();
  setupConfirmDeletes();
  setupAutoHideAlerts();
});

/**
 * Configura el botón para agregar líneas de detalle a la factura
 * Escucha clic en #addLineItem, agrega una fila con select de producto,
 * cantidad, precio unitario, subtotal y botón eliminar.
 * También maneja la eliminación de cada fila via delegación de eventos.
 */
function setupLineItems() {
  const addBtn = document.getElementById('addLineItem');
  if (!addBtn) return;

  const tbody = document.querySelector('#lineItemsTable tbody');
  let index = tbody ? tbody.children.length : 1;

  // Al hacer clic en "Agregar producto", inserta una nueva fila en la tabla
  addBtn.addEventListener('click', function () {
    const row = document.createElement('tr');
    row.innerHTML = `
      <td>
        <select name="productoId" class="browser-default" style="width:100%;padding:6px 8px;border:1px solid #cfd8dc;border-radius:6px;font-size:13px;" required>
          ${productOptions()}
        </select>
      </td>
      <td><input type="number" name="cantidad" min="1" value="1" required style="width:70px;padding:6px 8px;border:1px solid #cfd8dc;border-radius:6px;font-size:13px;"></td>
      <td><input type="number" name="precioUnitario" step="0.01" min="0" required style="width:100px;padding:6px 8px;border:1px solid #cfd8dc;border-radius:6px;font-size:13px;"></td>
      <td class="subtotal-cell" style="font-family:'Space Mono',monospace;font-weight:600;">$0</td>
      <td style="text-align:center;"><a class="btn-flat remove-line-item" style="color:#e74c3c;padding:0 4px;"><i class="material-icons">delete_outline</i></a></td>
    `;
    tbody.appendChild(row);
    index++;
  });

  // Delegación de eventos: detecta clic en botón eliminar de cualquier fila
  document.addEventListener('click', function (e) {
    if (e.target.classList.contains('remove-line-item') || e.target.closest('.remove-line-item')) {
      const row = e.target.closest('tr');
      // Siempre debe quedar al menos un detalle en la factura
      if (document.querySelectorAll('#lineItemsTable tbody tr').length > 1) {
        row.remove();
        calcularTotal();
      } else {
        if (typeof M !== 'undefined' && M.toast) {
          M.toast({html: '<i class="material-icons left" style="font-size:16px;">warning</i>Debe haber al menos un detalle', classes: 'rounded', displayLength: 3000});
        } else {
          alert('Debe haber al menos un detalle en la factura.');
        }
      }
    }
  });
}

/**
 * Obtiene las opciones de productos desde un <select> oculto (plantilla)
 * @returns {string} HTML de las opciones del select
 */
function productOptions() {
  const select = document.getElementById('productoSelectTemplate');
  if (select) return select.innerHTML;
  return '<option value="">Seleccione...</option>';
}

/**
 * Escucha cambios en cantidad o precio unitario para recalcular subtotales
 * automáticamente (event delegation sobre el documento)
 */
function setupAutoSubtotals() {
  document.addEventListener('input', function (e) {
    if (e.target.matches('input[name="cantidad"], input[name="precioUnitario"]')) {
      const row = e.target.closest('tr');
      if (row) calcularSubtotal(row);
    }
  });
}

/**
 * Calcula el subtotal de una fila (cantidad * precioUnitario)
 * y actualiza el total general
 * @param {HTMLElement} row - Fila <tr> que contiene los inputs
 */
function calcularSubtotal(row) {
  const cantidad = parseFloat(row.querySelector('input[name="cantidad"]').value) || 0;
  const precio = parseFloat(row.querySelector('input[name="precioUnitario"]').value) || 0;
  const subtotal = cantidad * precio;
  row.querySelector('.subtotal-cell').textContent = '$' + subtotal.toLocaleString('es-CO');
  calcularTotal();
}

/**
 * Suma todos los subtotales visibles y actualiza el span #totalFactura
 */
function calcularTotal() {
  let total = 0;
  document.querySelectorAll('.subtotal-cell').forEach(function (cell) {
    total += parseFloat(cell.textContent.replace(/[^0-9.-]+/g, '')) || 0;
  });
  const totalSpan = document.getElementById('totalFactura');
  if (totalSpan) totalSpan.textContent = '$' + total.toLocaleString('es-CO');
}

/**
 * Agrega confirmación a todos los botones con clase .btn-delete
 * Muestra un confirm nativo y previene el envío si el usuario cancela
 */
function setupConfirmDeletes() {
  document.querySelectorAll('.btn-delete').forEach(function (btn) {
    btn.addEventListener('click', function (e) {
      if (!confirm('Confirma que desea eliminar este registro?')) {
        e.preventDefault();
      }
    });
  });
}

/**
 * Oculta automáticamente las alertas después de 4 segundos
 * con una transición de opacidad y luego las elimina del DOM
 */
function setupAutoHideAlerts() {
  document.querySelectorAll('.alert').forEach(function (alert) {
    setTimeout(function () {
      alert.style.transition = 'opacity 0.5s';
      alert.style.opacity = '0';
      setTimeout(function () { alert.remove(); }, 500);
    }, 4000);
  });
}
