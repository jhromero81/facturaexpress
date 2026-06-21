/* ============================================
   FACTURAEXPRESS - PRODUCTOS MODULE
   Gesti&oacute;n de productos e inventario
   ============================================ */

const ProductosModule = {
    // Estado del m&oacute;dulo
    state: {
        productos: [],
        filtro: ''
    },

    // Productos de ejemplo
    productosEjemplo: [
        { id: 1, codigo: 'PROD001', nombre: 'Insumo Industrial X', descripcion: 'Insumo de alta calidad para procesos industriales', precio: 85000, stock: 50, iva: 19 },
        { id: 2, codigo: 'PROD002', nombre: 'Insumo Industrial Y', descripcion: 'Componente esencial para l&iacute;nea de producci&oacute;n', precio: 120000, stock: 35, iva: 19 },
        { id: 3, codigo: 'PROD003', nombre: 'Material Premium Z', descripcion: 'Material de primera calidad con certificaci&oacute;n ISO', precio: 250000, stock: 20, iva: 19 },
        { id: 4, codigo: 'PROD004', nombre: 'Componente Electr&oacute;nico A', descripcion: 'Circuito integrado de &uacute;ltima generaci&oacute;n', precio: 45000, stock: 100, iva: 19 },
        { id: 5, codigo: 'PROD005', nombre: 'Herramienta Especial B', descripcion: 'Herramienta de precisi&oacute;n para uso especializado', precio: 189000, stock: 15, iva: 19 }
    ],

    // Inicializar m&oacute;dulo
    init: function() {
        this.loadData();
        this.renderProductos();
        this.setupEventListeners();
    },

    // Cargar productos desde localStorage
    loadData: function() {
        this.state.productos = FacturaExpress.DataManager.loadOrGenerate(
            'productos',
            () => [...this.productosEjemplo]
        );
    },

    // Guardar productos en localStorage
    saveProductos: function() {
        FacturaExpress.storage.set('productos', this.state.productos);
    },

    // Renderizar tabla de productos
    renderProductos: function() {
        const tbody = document.getElementById('productosBody');
        if (!tbody) return;

        const productosFiltrados = this.filtrarProductos();

        if (productosFiltrados.length === 0) {
            const container = document.querySelector('.content-card');
            if (container) {
                const emptyState = container.querySelector('.empty-state');
                if (!emptyState) {
                    tbody.innerHTML = `
                        <tr>
                            <td colspan="6" class="empty-state">
                                <i class="material-icons empty-table-icon">inventory_2</i>
                                <p class="empty-table-text">No se encontraron productos</p>
                            </td>
                        </tr>
                    `;
                }
            }
            return;
        }

        tbody.innerHTML = productosFiltrados.map(producto => `
            <tr data-search="${producto.id} ${producto.nombre.toLowerCase()}">
                <td>${producto.id}</td>
                <td><strong>${this.escapeHtml(producto.nombre)}</strong></td>
                <td>${this.escapeHtml(producto.descripcion || '-')}</td>
                <td class="mono fw-700">${FacturaExpress.utils.formatMoney(producto.precio)}</td>
                <td>
                    <span class="stock-badge ${producto.stock <= 10 ? 'stock-low' : 'stock-ok'}">
                        ${producto.stock}
                    </span>
                </td>
                <td>
                    <a class="btn-flat btn-small editar-producto" style="color:#1a2535;font-weight:700;padding:0 8px;" data-id="${producto.id}">Editar</a>
                    <a class="btn-flat btn-small eliminar-producto" style="color:#e74c3c;font-weight:700;padding:0 8px;" data-id="${producto.id}">Eliminar</a>
                </td>
            </tr>
        `).join('');

        this.setupProductButtons();
    },

    // Escapar HTML
    escapeHtml: function(text) {
        if (!text) return '';
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    },

    // Configurar botones de acci&oacute;n - Usa event delegation
    setupProductButtons: function() {
        FacturaExpress.EventManager.delegate('#productosBody', '.editar-producto', 'click', this.handleEditar, this);
        FacturaExpress.EventManager.delegate('#productosBody', '.eliminar-producto', 'click', this.handleEliminar, this);
    },

    handleEditar: function(e, target) {
        const id = parseInt(target.dataset.id);
        if (id) {
            window.location.href = `${window.location.pathname}?action=editar&id=${id}`;
        }
    },

    handleEliminar: async function(e, target) {
        const id = parseInt(target.dataset.id);
        if (!id) return;

        const producto = this.state.productos.find(p => p.id === id);
        if (!producto) return;

        const confirmed = await FacturaExpress.utils.showConfirm(
            `¿Eliminar producto <strong>${producto.nombre}</strong>?`
        );
        if (!confirmed) return;

        this.state.productos = this.state.productos.filter(p => p.id !== id);
        this.saveProductos();
        this.renderProductos();
        FacturaExpress.utils.showToast('Producto eliminado', 'info');
    },

    // Filtrar productos por b&uacute;squeda
    filtrarProductos: function() {
        if (!this.state.filtro) return this.state.productos;

        const termino = this.state.filtro.toLowerCase();
        return this.state.productos.filter(producto =>
            producto.nombre.toLowerCase().includes(termino) ||
            producto.codigo.toLowerCase().includes(termino) ||
            producto.id.toString().includes(termino)
        );
    },

    // Agregar nuevo producto
    agregarProducto: function(datos) {
        if (!datos.nombre || !datos.precio) {
            FacturaExpress.utils.showToast('Nombre y Precio son obligatorios', 'error');
            return false;
        }

        const precio = parseFloat(datos.precio);
        if (isNaN(precio) || precio <= 0) {
            FacturaExpress.utils.showToast('El precio debe ser un valor positivo', 'error');
            return false;
        }

        const stock = parseInt(datos.stock) || 0;
        if (stock < 0) {
            FacturaExpress.utils.showToast('El stock no puede ser negativo', 'error');
            return false;
        }

        const nuevoProducto = {
            id: Date.now(),
            codigo: datos.codigo || `PROD${String(this.state.productos.length + 1).padStart(3, '0')}`,
            nombre: datos.nombre,
            descripcion: datos.descripcion || '',
            precio: precio,
            stock: stock,
            iva: 19
        };

        this.state.productos.push(nuevoProducto);
        this.saveProductos();
        this.renderProductos();

        FacturaExpress.utils.showToast(`Producto ${nuevoProducto.nombre} agregado`, 'success');
        return true;
    },

    // Actualizar stock desde la interfaz POS
    actualizarStock: function(productoId, cantidadVendida) {
        const producto = this.state.productos.find(p => p.id === productoId);
        if (producto) {
            producto.stock = Math.max(0, producto.stock - cantidadVendida);
            this.saveProductos();
        }
    },

    // Configurar event listeners
    setupEventListeners: function() {
        // B&uacute;squeda de productos
        const searchInput = document.getElementById('bProd');
        if (searchInput) {
            const debouncedSearch = FacturaExpress.utils.debounce((value) => {
                this.state.filtro = value;
                this.renderProductos();
            }, 300);

            searchInput.addEventListener('input', (e) => {
                debouncedSearch(e.target.value);
            });
        }
    }
};

// Inicializar si estamos en la p&aacute;gina de productos
if (document.getElementById('productosBody')) {
    document.addEventListener('DOMContentLoaded', () => {
        ProductosModule.init();
    });
}
