/**
 * accessibility-bootstrap.js
 * Script de arranque que se ejecuta inmediatamente (IIFE) para aplicar
 * las preferencias de accesibilidad (modo oscuro, alto contraste, tamaño
 * de texto) ANTES de que el DOM termine de cargar, evitando parpadeos.
 */
(function() {
    'use strict';

    // Lee localStorage de forma segura (entornos restringidos pueden lanzar excepción)
    let prefs = null;
    try {
        const rawPrefs = localStorage.getItem('facturaexpress_prefs');
        const rawConfig = localStorage.getItem('facturaexpress_config');

        const safeParse = (value) => {
            try {
                return value ? JSON.parse(value) : null;
            } catch (e) {
                return null;
            }
        };

        prefs = safeParse(rawPrefs);
        if (!prefs) {
            const configData = safeParse(rawConfig);
            if (configData && typeof configData === 'object') {
                prefs = {
                    modoOscuro: configData.modoOscuro || false,
                    altoContraste: configData.altoContraste || false,
                    tamanoTexto: configData.tamanoTexto || 'medium'
                };
            }
        }
    } catch (e) {
        prefs = null;
    }

    if (!prefs) {
        prefs = {
            modoOscuro: false,
            altoContraste: false,
            tamanoTexto: 'medium'
        };
    }

    /**
     * Aplica clase en <body> y un atributo data- en <html> (disponible
     * inmediatamente incluso si body aún no existe). Esto elimina el flash
     * porque <html> ya está presente cuando el script se ejecuta en <head>.
     */
    const applyTheme = (className, dataAttr, enabled) => {
        document.documentElement.setAttribute(dataAttr, enabled ? 'true' : '');
        const setClass = () => {
            if (!document.body) return;
            document.body.classList.toggle(className, enabled);
        };
        if (document.body) {
            setClass();
        } else {
            document.addEventListener('DOMContentLoaded', setClass);
        }
    };

    // Aplica modo oscuro si está habilitado
    applyTheme('fx-dark-mode', 'data-fx-dark-mode', prefs.modoOscuro === true);

    // Aplica alto contraste si está habilitado
    applyTheme('fx-high-contrast', 'data-fx-high-contrast', prefs.altoContraste === true);

    // Aplica el tamaño de fuente guardado
    const sizes = { small: '12px', medium: '15px', large: '18px' };
    const fontSize = sizes[prefs.tamanoTexto] || '15px';
    document.documentElement.style.fontSize = fontSize;

    // Inyecta un estilo global para transiciones suaves al cambiar de tema
    const style = document.createElement('style');
    style.id = 'fx-accessibility-bootstrap';
    style.textContent = `
        * { transition: background-color 0.3s ease, color 0.3s ease, border-color 0.3s ease !important; }
    `;
    if (document.head) {
        document.head.insertBefore(style, document.head.firstChild);
    } else {
        document.addEventListener('DOMContentLoaded', function() {
            if (!document.getElementById('fx-accessibility-bootstrap')) {
                document.head.insertBefore(style, document.head.firstChild);
            }
        });
    }

    // Expone las preferencias globalmente para que otros scripts puedan leerlas
    window.FX_ACCESSIBILITY_PREFS = prefs;
})();
