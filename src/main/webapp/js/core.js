// Objeto global FacturaExpress con utilidades y control de accesibilidad
const FacturaExpress = {
    config: {
        version: '2.1.0',
        IVA_RATE: 0.19
    },

    utils: {
        formatMoney: (amount) => {
            if (amount == null) return '$0';
            return new Intl.NumberFormat('es-CO', {
                style: 'currency', currency: 'COP',
                minimumFractionDigits: 0, maximumFractionDigits: 0
            }).format(amount);
        },

        formatDate: (date, format = 'dd/mm/yyyy') => {
            const d = date instanceof Date ? date : new Date(date);
            if (isNaN(d.getTime())) return 'Fecha invalida';
            const day = d.getDate().toString().padStart(2, '0');
            const month = (d.getMonth() + 1).toString().padStart(2, '0');
            const year = d.getFullYear();
            if (format === 'dd/mm/yyyy hh:mm') {
                return `${day}/${month}/${year} ${d.getHours().toString().padStart(2,'0')}:${d.getMinutes().toString().padStart(2,'0')}`;
            }
            return `${day}/${month}/${year}`;
        },

        showToast: (message, type = 'success') => {
            if (typeof M !== 'undefined' && M.toast) {
                const colors = { success: '#1abc9c', error: '#e74c3c', warning: '#f39c12', info: '#3498db' };
                const icons = { success: 'check_circle', error: 'error', warning: 'warning', info: 'info' };
                M.toast({
                    html: '<i class="material-icons left" style="font-size:16px;">' + (icons[type] || 'info') + '</i>' + message,
                    classes: 'rounded ' + (colors[type] || '#3498db'),
                    displayLength: 3000
                });
            }
        },

        validateEmail: (email) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email),
        validatePhone: (phone) => /^[0-9+\-\s]{7,15}$/.test(phone),
        calcularIVA: (base) => base * FacturaExpress.config.IVA_RATE,

        showDialog: (options = {}) => {
            const {
                title = 'FacturaExpress', message = '', type = 'info',
                confirmText = 'Aceptar', cancelText = null,
                onConfirm = null, onCancel = null,
                input = false, inputType = 'text', inputLabel = '', inputValue = ''
            } = options;

            const existing = document.getElementById('fx-dialog-overlay');
            if (existing) existing.remove();

            const overlay = document.createElement('div');
            overlay.id = 'fx-dialog-overlay';
            overlay.className = 'fx-dialog-overlay';

            const icons = { success: 'check_circle', error: 'error', warning: 'warning', info: 'info' };
            const iconColors = { success: '#1abc9c', error: '#e74c3c', warning: '#f39c12', info: '#3498db' };

            overlay.innerHTML = '<div class="fx-dialog-box">'
                + '<div class="fx-dialog-header">'
                + '<i class="material-icons fx-dialog-icon" style="color:' + (iconColors[type] || '#3498db') + ';">' + (icons[type] || 'info') + '</i>'
                + '<h5 class="fx-dialog-title">' + title + '</h5></div>'
                + '<p class="fx-dialog-message">' + message + '</p>'
                + (input ? '<div class="input-field"><input id="fx-dialog-input" type="' + inputType + '" value="' + inputValue + '"><label for="fx-dialog-input" class="active">' + inputLabel + '</label></div>' : '')
                + '<div class="fx-dialog-actions">'
                + (cancelText ? '<button id="fx-dialog-cancel" class="btn btn-cancel-compact">' + cancelText + '</button>' : '')
                + '<button id="fx-dialog-confirm" class="btn btn-teal">' + confirmText + '</button></div></div>';

            document.body.appendChild(overlay);

            return new Promise((resolve) => {
                const confirmBtn = document.getElementById('fx-dialog-confirm');
                const cancelBtn = document.getElementById('fx-dialog-cancel');
                const inputEl = document.getElementById('fx-dialog-input');

                const close = (result) => {
                    overlay.remove();
                    resolve(result);
                    if (result && onConfirm) onConfirm(result);
                    else if (!result && onCancel) onCancel();
                };

                confirmBtn.addEventListener('click', () => close(inputEl ? inputEl.value : true));
                if (cancelBtn) cancelBtn.addEventListener('click', () => close(null));
                overlay.addEventListener('click', (e) => { if (e.target === overlay && cancelBtn) close(null); });
                document.addEventListener('keydown', function handler(e) {
                    if (e.key === 'Escape' && document.getElementById('fx-dialog-overlay')) {
                        close(null);
                        document.removeEventListener('keydown', handler);
                    }
                });
                if (inputEl) setTimeout(() => inputEl.focus(), 100);
            });
        },

        showConfirm: async (message, title = 'Confirmar') => {
            return await FacturaExpress.utils.showDialog({
                title, message, type: 'warning', confirmText: 'Si', cancelText: 'Cancelar'
            });
        },

        showAlert: async (message, title = 'FacturaExpress', type = 'info') => {
            return await FacturaExpress.utils.showDialog({
                title, message, type, confirmText: 'Aceptar'
            });
        },

        debounce: function(fn, delay) {
            let timer = null;
            return function() {
                const context = this;
                const args = arguments;
                clearTimeout(timer);
                timer = setTimeout(() => fn.apply(context, args), delay);
            };
        },

        calculatePercentage: function(value, total) {
            if (!total || total === 0) return 0;
            return Math.round((value / total) * 100);
        },

        validateNIT: function(nit) {
            return /^\d{6,15}-\d{1}$/.test(nit) || /^\d{6,15}$/.test(nit);
        },

        generateInvoiceNumber: function() {
            const prefix = 'FAC-';
            const num = String(Date.now()).slice(-5);
            const random = String(Math.floor(Math.random() * 100)).padStart(2, '0');
            return prefix + num + random;
        }
    },

    // Estado global de la aplicaci&oacute;n
    state: {
        configuracion: {
            modoOscuro: false,
            altoContraste: false,
            tamanoTexto: 'medium'
        }
    },

    // ============================================
    // CONTROL DE ACCESIBILIDAD
    // ============================================
    accessibility: {
        getPrefs() {
            try {
                const raw = localStorage.getItem('facturaexpress_prefs');
                return raw ? JSON.parse(raw) : { modoOscuro: false, altoContraste: false, tamanoTexto: 'medium' };
            } catch { return { modoOscuro: false, altoContraste: false, tamanoTexto: 'medium' }; }
        },
        savePrefs(prefs) {
            localStorage.setItem('facturaexpress_prefs', JSON.stringify(prefs));
        },
        applyAll() {
            const prefs = this.getPrefs();
            document.body.classList.toggle('fx-dark-mode', prefs.modoOscuro);
            document.body.classList.toggle('fx-high-contrast', prefs.altoContraste);
            const sizes = { small: '12px', medium: '15px', large: '18px' };
            document.documentElement.style.fontSize = sizes[prefs.tamanoTexto] || '15px';
        },
        toggleDarkMode() {
            const prefs = this.getPrefs();
            prefs.modoOscuro = !prefs.modoOscuro;
            document.body.classList.toggle('fx-dark-mode', prefs.modoOscuro);
            this.savePrefs(prefs);
            this.updateControlStyles();
            FacturaExpress.utils.showToast('Modo oscuro ' + (prefs.modoOscuro ? 'activado' : 'desactivado'), 'info');
        },
        toggleHighContrast() {
            const prefs = this.getPrefs();
            prefs.altoContraste = !prefs.altoContraste;
            document.body.classList.toggle('fx-high-contrast', prefs.altoContraste);
            this.savePrefs(prefs);
            this.updateControlStyles();
            FacturaExpress.utils.showToast('Alto contraste ' + (prefs.altoContraste ? 'activado' : 'desactivado'), 'info');
        },
        setFontSize(size) {
            if (!['small', 'medium', 'large'].includes(size)) return;
            const prefs = this.getPrefs();
            prefs.tamanoTexto = size;
            const sizes = { small: '12px', medium: '15px', large: '18px' };
            document.documentElement.style.fontSize = sizes[size] || '15px';
            this.savePrefs(prefs);
            const labels = { small: 'Pequeno', medium: 'Mediano', large: 'Grande' };
            FacturaExpress.utils.showToast('Tamano de texto: ' + (labels[size] || size), 'success');
        },
        setupControls() {
            const darkModeControl = document.getElementById('darkModeControl');
            const highContrastControl = document.getElementById('highContrastControl');
            const fontSizeSelect = document.getElementById('fontSizeControl');

            if (darkModeControl) {
                const newBtn = darkModeControl.cloneNode(true);
                darkModeControl.parentNode.replaceChild(newBtn, darkModeControl);
                newBtn.addEventListener('click', (e) => { e.preventDefault(); this.toggleDarkMode(); });
            }
            if (highContrastControl) {
                const newBtn = highContrastControl.cloneNode(true);
                highContrastControl.parentNode.replaceChild(newBtn, highContrastControl);
                newBtn.addEventListener('click', (e) => { e.preventDefault(); this.toggleHighContrast(); });
            }
            if (fontSizeSelect) {
                const newSelect = fontSizeSelect.cloneNode(true);
                fontSizeSelect.parentNode.replaceChild(newSelect, fontSizeSelect);
                newSelect.addEventListener('change', (e) => { this.setFontSize(e.target.value); });
            }
            this.updateControlStyles();
        },
        updateControlStyles() {
            const prefs = this.getPrefs();
            const darkCtrl = document.getElementById('darkModeControl');
            const highCtrl = document.getElementById('highContrastControl');
            const fontCtrl = document.getElementById('fontSizeControl');
            if (darkCtrl) {
                darkCtrl.style.background = prefs.modoOscuro ? '#1abc9c' : 'rgba(26,188,156,0.1)';
                darkCtrl.style.color = prefs.modoOscuro ? 'white' : '#1abc9c';
            }
            if (highCtrl) {
                if (prefs.altoContraste) {
                    highCtrl.style.background = '#000';
                    highCtrl.style.color = '#ff0';
                } else {
                    highCtrl.style.background = 'rgba(26,188,156,0.1)';
                    highCtrl.style.color = '#1abc9c';
                }
            }
            if (fontCtrl) fontCtrl.value = prefs.tamanoTexto;
            const darkToggle = document.getElementById('darkModeToggle');
            const hcToggle = document.getElementById('highContrastToggle');
            if (darkToggle) {
                if (darkToggle.type === 'checkbox') {
                    darkToggle.checked = prefs.modoOscuro;
                } else {
                    darkToggle.classList.toggle('active', prefs.modoOscuro);
                }
            }
            if (hcToggle) {
                if (hcToggle.type === 'checkbox') {
                    hcToggle.checked = prefs.altoContraste;
                } else {
                    hcToggle.classList.toggle('active', prefs.altoContraste);
                }
            }
        }
    },

    // ============================================
    // GESTION DE DATOS
    // ============================================
    DataManager: {
        loadOrGenerate: function(key, generatorFn) {
            const data = FacturaExpress.storage.get(key);
            if (data && data.length > 0) return data;
            const generated = generatorFn();
            FacturaExpress.storage.set(key, generated);
            return generated;
        }
    },

    // ============================================
    // DATOS DE EJEMPLO
    // ============================================
    SampleData: {
        generarFacturasEjemplo: function(cantidad) {
            const clientes = [
                { nombre: 'Maria Fernanda Gomez', identificacion: '101020201' },
                { nombre: 'Carlos Rodriguez', identificacion: '101020202' },
                { nombre: 'Juan Perez', identificacion: '101020203' },
                { nombre: 'Luis Hernandez', identificacion: '101020204' },
                { nombre: 'Ana Martinez', identificacion: '101020205' }
            ];
            const productos = [
                { nombre: 'Insumo Industrial X', precio: 85000 },
                { nombre: 'Insumo Industrial Y', precio: 120000 },
                { nombre: 'Material Premium Z', precio: 250000 },
                { nombre: 'Componente Electr&oacute;nico A', precio: 45000 },
                { nombre: 'Herramienta Especial B', precio: 189000 }
            ];
            const facturas = [];
            const ahora = new Date();

            for (let i = 0; i < cantidad; i++) {
                const cliente = clientes[Math.floor(Math.random() * clientes.length)];
                const itemsCount = Math.floor(Math.random() * 4) + 1;
                const items = [];
                let total = 0;

                for (let j = 0; j < itemsCount; j++) {
                    const producto = productos[Math.floor(Math.random() * productos.length)];
                    const cantidadItem = Math.floor(Math.random() * 5) + 1;
                    const precio = producto.precio;
                    items.push({
                        nombre: producto.nombre,
                        cantidad: cantidadItem,
                        precio: precio,
                        subtotal: precio * cantidadItem
                    });
                    total += precio * cantidadItem;
                }

                const fecha = new Date(ahora);
                fecha.setDate(fecha.getDate() - Math.floor(Math.random() * 30));
                fecha.setHours(Math.floor(Math.random() * 12) + 8);

                const estados = ['enviado', 'pendiente', 'procesando', 'rechazado'];
                const factura = {
                    numero: 'FAC-' + String(100000 + i).slice(1),
                    fecha: fecha.toISOString(),
                    cliente: cliente.nombre,
                    clienteIdentificacion: cliente.identificacion,
                    items: items,
                    subtotal: total,
                    iva: total * 0.19,
                    total: total * 1.19,
                    estado: estados[Math.floor(Math.random() * estados.length)]
                };
                facturas.push(factura);
            }
            return facturas;
        }
    },

    // ============================================
    // UTILIDADES DE CALCULO
    // ============================================
    CalculationUtils: {
        calculateDailySalesData: function(facturas, dias) {
            const data = [];
            for (let i = dias - 1; i >= 0; i--) {
                const fecha = new Date();
                fecha.setDate(fecha.getDate() - i);
                const inicioDia = new Date(fecha.getFullYear(), fecha.getMonth(), fecha.getDate());
                const finDia = new Date(inicioDia);
                finDia.setDate(finDia.getDate() + 1);

                const facturasDia = facturas.filter(f => {
                    const fFecha = new Date(f.fecha);
                    return fFecha >= inicioDia && fFecha < finDia;
                });
                const monto = facturasDia.reduce((sum, f) => sum + (f.total || 0), 0);
                const diasSemana = ['Dom', 'Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb'];
                data.push({
                    dia: diasSemana[inicioDia.getDay()],
                    monto: monto,
                    cantidad: facturasDia.length
                });
            }
            return data;
        }
    },

    // ============================================
    // GESTION DE EVENTOS (DELEGACION)
    // ============================================
    EventManager: {
        delegate: function(parentSelector, childSelector, eventType, handler, context) {
            const parent = document.querySelector(parentSelector);
            if (!parent) return;
            parent.addEventListener(eventType, function(e) {
                const target = e.target.closest(childSelector);
                if (target && parent.contains(target)) {
                    handler.call(context || FacturaExpress, e, target);
                }
            });
        }
    },

    // ============================================
    // GESTION DE MODULOS POR PAGINA
    // ============================================
    ModuleManager: {
        modules: {},
        createModule: function(name, moduleObj) {
            this.modules[name] = moduleObj;
        }
    },

    // ============================================
    // ALMACENAMIENTO LOCAL
    // ============================================
    storage: {
        set: (key, data) => {
            try { localStorage.setItem('facturaexpress_' + key, JSON.stringify(data)); return true; }
            catch { return false; }
        },
        get: (key, defaultValue = null) => {
            try { const d = localStorage.getItem('facturaexpress_' + key); return d ? JSON.parse(d) : defaultValue; }
            catch { return defaultValue; }
        },
        remove: (key) => localStorage.removeItem('facturaexpress_' + key)
    },

};

// Inicializacion global
document.addEventListener('DOMContentLoaded', function() {
    // Aplicar preferencias de accesibilidad
    FacturaExpress.accessibility.applyAll();

    // Inicializar componentes Materialize
    if (typeof M !== 'undefined') {
        const logoutModalEl = document.getElementById('logoutModal');
        if (logoutModalEl && M.Modal) {
            const logoutModal = M.Modal.init(logoutModalEl, { dismissible: false });
            const showLogoutBtn = document.getElementById('showLogoutBtn');
            if (showLogoutBtn) {
                showLogoutBtn.addEventListener('click', function(e) {
                    e.preventDefault();
                    logoutModal.open();
                });
            }
            const confirmLogoutBtn = document.getElementById('confirmLogoutBtn');
            if (confirmLogoutBtn) {
                confirmLogoutBtn.addEventListener('click', function(e) {
                    e.preventDefault();
                    const href = this.getAttribute('href');
                    const loadingOverlay = document.getElementById('loadingOverlay');
                    if (loadingOverlay) loadingOverlay.classList.add('active');
                    setTimeout(function() { window.location.href = href; }, 600);
                });
            }
        }
        const modals = document.querySelectorAll('.modal:not(.logout-modal)');
        if (modals.length) M.Modal.init(modals);
    }

    // Configurar controles de accesibilidad
    FacturaExpress.accessibility.setupControls();
});
