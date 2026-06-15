// Objeto global FacturaExpress con utilidades y control de accesibilidad
const FacturaExpress = {
    utils: {
        /**
         * Formatea un número como moneda COP (ej: $1,234,567)
         * @param {number} amount - Monto a formatear
         * @returns {string} Monto formateado en pesos colombianos
         */
        formatMoney: (amount) => {
            if (!amount && amount !== 0) return '$0';
            return new Intl.NumberFormat('es-CO', {
                style: 'currency',
                currency: 'COP',
                minimumFractionDigits: 0,
                maximumFractionDigits: 0
            }).format(amount);
        },
        /**
         * Formatea una fecha en formato dd/mm/yyyy (opcional con hora)
         * @param {Date|string} date - Fecha a formatear
         * @param {string} format - 'dd/mm/yyyy' o 'dd/mm/yyyy hh:mm'
         * @returns {string} Fecha formateada o 'Fecha inválida'
         */
        formatDate: (date, format = 'dd/mm/yyyy') => {
            const d = date instanceof Date ? date : new Date(date);
            if (isNaN(d.getTime())) return 'Fecha inválida';
            const day = d.getDate().toString().padStart(2, '0');
            const month = (d.getMonth() + 1).toString().padStart(2, '0');
            const year = d.getFullYear();
            switch(format) {
                case 'dd/mm/yyyy': return `${day}/${month}/${year}`;
                case 'dd/mm/yyyy hh:mm': return `${day}/${month}/${year} ${d.getHours().toString().padStart(2,'0')}:${d.getMinutes().toString().padStart(2,'0')}`;
                default: return `${day}/${month}/${year}`;
            }
        },
        /**
         * Muestra un toast de Materialize con ícono y color según el tipo
         * @param {string} message - Mensaje a mostrar
         * @param {string} type - 'success' | 'error' | 'warning' | 'info'
         */
        showToast: (message, type = 'success') => {
            if (typeof M !== 'undefined' && M.toast) {
                const colors = {
                    success: '#1abc9c',
                    error: '#e74c3c',
                    warning: '#f39c12',
                    info: '#3498db'
                };
                M.toast({
                    html: `<i class="material-icons left" style="font-size:16px;">${
                        type === 'success' ? 'check_circle' : 
                        type === 'error' ? 'error' : 
                        type === 'warning' ? 'warning' : 'info'
                    }</i>${message}`,
                    classes: `rounded ${colors[type]}`,
                    displayLength: 3000
                });
            }
        }
    },

    // Control de accesibilidad: modo oscuro, alto contraste y tamaño de texto
    accessibility: {
        /** Obtiene las preferencias guardadas en localStorage */
        getPrefs() {
            try {
                const raw = localStorage.getItem('facturaexpress_prefs');
                return raw ? JSON.parse(raw) : { modoOscuro: false, altoContraste: false, tamanoTexto: 'medium' };
            } catch { return { modoOscuro: false, altoContraste: false, tamanoTexto: 'medium' }; }
        },
        /** Guarda las preferencias en localStorage */
        savePrefs(prefs) {
            localStorage.setItem('facturaexpress_prefs', JSON.stringify(prefs));
        },
        /** Alterna el modo oscuro y persiste la preferencia */
        toggleDarkMode() {
            const prefs = this.getPrefs();
            prefs.modoOscuro = !prefs.modoOscuro;
            document.body.classList.toggle('fx-dark-mode', prefs.modoOscuro);
            this.savePrefs(prefs);
        },
        /** Alterna el modo alto contraste y persiste la preferencia */
        toggleHighContrast() {
            const prefs = this.getPrefs();
            prefs.altoContraste = !prefs.altoContraste;
            document.body.classList.toggle('fx-high-contrast', prefs.altoContraste);
            this.savePrefs(prefs);
        },
        /**
         * Cambia el tamaño de fuente base del documento
         * @param {string} size - 'small' | 'medium' | 'large'
         */
        setFontSize(size) {
            const prefs = this.getPrefs();
            prefs.tamanoTexto = size;
            const sizes = { small: '12px', medium: '15px', large: '18px' };
            document.documentElement.style.fontSize = sizes[size] || '15px';
            this.savePrefs(prefs);
        }
    }
};

// Al cargar el DOM, inicializa componentes de Materialize
document.addEventListener('DOMContentLoaded', function() {
    if (typeof M !== 'undefined') {
        // Inicializa el modal de cierre de sesión
        const logoutModalEl = document.getElementById('logoutModal');
        if (logoutModalEl && M.Modal) {
            const logoutModal = M.Modal.init(logoutModalEl, { dismissible: false });
            // Abre el modal al hacer clic en el botón de logout
            const showLogoutBtn = document.getElementById('showLogoutBtn');
            if (showLogoutBtn) {
                showLogoutBtn.addEventListener('click', function(e) {
                    e.preventDefault();
                    logoutModal.open();
                });
            }
            // Confirma el logout: muestra overlay de carga y redirige
            const confirmLogoutBtn = document.getElementById('confirmLogoutBtn');
            if (confirmLogoutBtn) {
                confirmLogoutBtn.addEventListener('click', function(e) {
                    e.preventDefault();
                    const href = this.getAttribute('href');
                    const loadingOverlay = document.getElementById('loadingOverlay');
                    if (loadingOverlay) loadingOverlay.classList.add('active');
                    setTimeout(function() {
                        window.location.href = href;
                    }, 600);
                });
            }
        }
        // Inicializa el resto de modales de Materialize
        const modals = document.querySelectorAll('.modal:not(.logout-modal)');
        if (modals.length) M.Modal.init(modals);
    }
});
