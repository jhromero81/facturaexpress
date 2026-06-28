<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.codewise.facturaexpress.model.ConfiguracionEmpresa" %>
<%@ include file="/WEB-INF/jsp/layout/header.jsp" %>
<%
  ConfiguracionEmpresa configEmp = (ConfiguracionEmpresa) request.getAttribute("config");
  if (configEmp == null) configEmp = new ConfiguracionEmpresa();
  String csrf = (String) request.getAttribute("csrfToken");
%>

<form id="configForm">
  <input type="hidden" name="_csrf_token" value="<%= csrf %>">

  <div class="content-card" style="margin-bottom:18px;">
    <div class="config-card-title">
      <i class="material-icons">business</i>
      Datos de la empresa
    </div>
    <div class="row" style="margin-bottom:0;">
      <div class="input-field col s12 m6">
        <i class="material-icons prefix">credit_card</i>
        <input id="nit_empresa" name="nit" type="text" class="validate" value="<%= configEmp.getNit() != null ? configEmp.getNit() : "" %>">
        <label for="nit_empresa" class="<%= configEmp.getNit() != null ? "active" : "" %>">NIT / Identificaci&oacute;n</label>
      </div>
      <div class="input-field col s12 m6">
        <i class="material-icons prefix">storefront</i>
        <input id="razon_social" name="razonSocial" type="text" class="validate" value="<%= configEmp.getRazonSocial() != null ? configEmp.getRazonSocial() : "" %>">
        <label for="razon_social" class="<%= configEmp.getRazonSocial() != null ? "active" : "" %>">Raz&oacute;n Social</label>
      </div>
    </div>
    <div class="row" style="margin-bottom:0;">
      <div class="input-field col s12 m6">
        <i class="material-icons prefix">email</i>
        <input id="email_facturacion" name="emailFacturacion" type="email" class="validate" value="<%= configEmp.getEmailFacturacion() != null ? configEmp.getEmailFacturacion() : "" %>">
        <label for="email_facturacion" class="<%= configEmp.getEmailFacturacion() != null ? "active" : "" %>">Email de facturaci&oacute;n</label>
      </div>
      <div class="input-field col s12 m6">
        <i class="material-icons prefix">phone</i>
        <input id="telefono_empresa" name="telefono" type="text" class="validate" value="<%= configEmp.getTelefono() != null ? configEmp.getTelefono() : "" %>">
        <label for="telefono_empresa" class="<%= configEmp.getTelefono() != null ? "active" : "" %>">Tel&eacute;fono</label>
      </div>
    </div>
    <div class="input-field" style="margin-bottom:0;">
      <i class="material-icons prefix">location_on</i>
      <input id="direccion_empresa" name="direccion" type="text" class="validate" value="<%= configEmp.getDireccion() != null ? configEmp.getDireccion() : "" %>">
      <label for="direccion_empresa" class="<%= configEmp.getDireccion() != null ? "active" : "" %>">Direcci&oacute;n</label>
    </div>
    <div class="form-actions">
      <a class="btn btn-teal waves-effect waves-light" onclick="guardarConfig()"><i class="material-icons left">save</i>Guardar Cambios</a>
    </div>
  </div>

  <div class="content-card" style="margin-bottom:18px;">
    <div class="config-card-title">
      <i class="material-icons">verified_user</i>
      Configuraci&oacute;n Fiscal (DIAN)
    </div>
    <div class="input-field" style="margin-bottom:0;">
      <i class="material-icons prefix">receipt</i>
      <input id="resolucion_dian" name="resolucionDian" type="text" class="validate" value="<%= configEmp.getResolucionDian() != null ? configEmp.getResolucionDian() : "" %>">
      <label for="resolucion_dian" class="<%= configEmp.getResolucionDian() != null ? "active" : "" %>">Resoluci&oacute;n DIAN</label>
    </div>
    <div class="row" style="margin-bottom:0;">
      <div class="input-field col s12 m6">
        <i class="material-icons prefix">schedule</i>
        <input id="certificado_vence" name="certificadoVence" type="date" class="validate" value="<%= configEmp.getCertificadoVence() != null ? configEmp.getCertificadoVence().toString() : "" %>">
        <label for="certificado_vence" class="<%= configEmp.getCertificadoVence() != null ? "active" : "" %>">Vence certificado</label>
      </div>
    </div>
    <div class="form-actions">
      <a class="btn btn-teal waves-effect waves-light" onclick="guardarConfig()"><i class="material-icons left">sync</i>Guardar configuraci&oacute;n DIAN</a>
    </div>
  </div>

  <div class="content-card" style="margin-bottom:18px;">
    <div class="config-card-title">
      <i class="material-icons">notifications_active</i>
      Notificaciones
    </div>
    <div class="row" style="margin-bottom:0;">
      <div class="col s12 m6">
        <div class="toggle-switch-custom">
          <span><i class="material-icons tiny" style="vertical-align:middle;">email</i> Notificaciones por email</span>
          <div class="switch">
            <label> Off <input type="checkbox" name="notifEmail" value="true" <%= configEmp.isNotifEmail() ? "checked" : "" %>> <span class="lever"></span> On </label>
          </div>
        </div>
        <div class="toggle-switch-custom">
          <span><i class="material-icons tiny">notifications</i> Notificaciones push</span>
          <div class="switch"><label> Off <input type="checkbox" name="notifPush" value="true" <%= configEmp.isNotifPush() ? "checked" : "" %>> <span class="lever"></span> On </label></div>
        </div>
      </div>
      <div class="col s12 m6">
        <div class="toggle-switch-custom">
          <span><i class="material-icons tiny">receipt</i> Alertas de facturaci&oacute;n (DIAN)</span>
          <div class="switch"><label> Off <input type="checkbox" name="alertasDian" value="true" <%= configEmp.isAlertasDian() ? "checked" : "" %>> <span class="lever"></span> On </label></div>
        </div>
        <div class="toggle-switch-custom">
          <span><i class="material-icons tiny">event</i> Recordatorios autom&aacute;ticos</span>
          <div class="switch"><label> Off <input type="checkbox" name="recordatorios" value="true" <%= configEmp.isRecordatorios() ? "checked" : "" %>> <span class="lever"></span> On </label></div>
        </div>
      </div>
    </div>
    <div class="form-actions">
      <a class="btn btn-teal waves-effect waves-light" onclick="guardarConfig()"><i class="material-icons left">save</i>Guardar preferencias</a>
    </div>
  </div>

  <div class="content-card" style="margin-bottom:18px;">
    <div class="config-card-title">
      <i class="material-icons">group</i>
      Perfiles de usuario
    </div>
    <div class="row" style="margin-bottom:0;">
      <div class="col s12 m6 l4" style="margin-bottom:18px;">
        <div class="client-card">
          <i class="material-icons medium" style="color:var(--accent);">admin_panel_settings</i>
          <div class="client-name">Administradores</div>
          <div class="client-email">Acceso total al sistema</div>
        </div>
      </div>
      <div class="col s12 m6 l4" style="margin-bottom:18px;">
        <div class="client-card">
          <i class="material-icons medium" style="color:var(--accent);">point_of_sale</i>
          <div class="client-name">Vendedores</div>
          <div class="client-email">Ventas, clientes, facturaci&oacute;n</div>
        </div>
      </div>
      <div class="col s12 m6 l4" style="margin-bottom:18px;">
        <div class="client-card">
          <i class="material-icons medium" style="color:var(--accent);">fact_check</i>
          <div class="client-name">Contadores / Reportes</div>
          <div class="client-email">Acceso a reportes y libros</div>
        </div>
      </div>
    </div>
  </div>

  <div class="content-card" style="margin-bottom:18px;">
    <div class="config-card-title">
      <i class="material-icons">security</i>
      Seguridad
    </div>
    <div class="info-row">
      <span class="info-label"><i class="material-icons tiny">vpn_key</i> &Uacute;ltimo cambio contrase&ntilde;a</span>
      <span class="info-value">10 de febrero 2026</span>
    </div>
    <hr>
    <div class="info-row" style="border-bottom:none;">
      <span class="info-label">Registro de accesos recientes:</span>
      <span class="info-value">IP 190.84.45.12 - Bogot&aacute;, hace 12 min</span>
    </div>
  </div>
</form>

<div class="content-card" style="margin-bottom:18px;">
    <div class="config-card-title">
      <i class="material-icons">accessibility_new</i>
      Accesibilidad
    </div>
    <div class="toggle-switch-custom">
      <span><i class="material-icons tiny">dark_mode</i> Activar modo oscuro</span>
      <div class="switch">
        <label> Off <input type="checkbox" id="darkModeToggle" data-access="modoOscuro"> <span class="lever"></span> On </label>
      </div>
    </div>
    <div class="toggle-switch-custom">
      <span><i class="material-icons tiny">contrast</i> Modo alto contraste</span>
      <div class="switch">
        <label> Off <input type="checkbox" id="highContrastToggle" data-access="altoContraste"> <span class="lever"></span> On </label>
      </div>
    </div>
    <div class="info-row" style="border-bottom:none;">
      <span class="info-label"><i class="material-icons tiny">text_fields</i> Tama&ntilde;o de texto:</span>
      <div class="text-size-selector">
        <button type="button" class="size-btn" data-size="small">Peque&ntilde;o</button>
        <button type="button" class="size-btn active" data-size="medium">Mediano</button>
        <button type="button" class="size-btn" data-size="large">Grande</button>
      </div>
    </div>
  </div>

<script>
function setupAccessibility() {
  var prefs = FacturaExpress.accessibility.getPrefs();
  var hcToggle = document.getElementById('highContrastToggle');
  var dmToggle = document.getElementById('darkModeToggle');
  if (hcToggle) hcToggle.checked = prefs.altoContraste;
  if (dmToggle) dmToggle.checked = prefs.modoOscuro;

  var sizeBtns = document.querySelectorAll('.size-btn');
  if (sizeBtns.length) {
    sizeBtns.forEach(function(b) { b.classList.remove('active'); });
    var activeBtn = document.querySelector('.size-btn[data-size="' + prefs.tamanoTexto + '"]');
    if (activeBtn) activeBtn.classList.add('active');
  }

  if (hcToggle) {
    hcToggle.addEventListener('change', function() {
      FacturaExpress.accessibility.toggleHighContrast();
    });
  }
  if (dmToggle) {
    dmToggle.addEventListener('change', function() {
      FacturaExpress.accessibility.toggleDarkMode();
    });
  }
  sizeBtns.forEach(function(b) {
    b.addEventListener('click', function() {
      var size = this.getAttribute('data-size');
      FacturaExpress.accessibility.setFontSize(size);
      sizeBtns.forEach(function(x) { x.classList.remove('active'); });
      this.classList.add('active');
    });
  });
}
document.addEventListener('DOMContentLoaded', setupAccessibility);
function guardarConfig() {
  var form = document.getElementById('configForm');
  var data = new URLSearchParams(new FormData(form));
  var checkboxes = form.querySelectorAll('input[type="checkbox"][name]');
  checkboxes.forEach(function(cb) {
    if (!cb.checked) {
      data.set(cb.name, 'false');
    }
  });
  fetch('<%= ctx %>/configuracion', {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body: data.toString()
  })
  .then(function(r) { return r.json(); })
  .then(function(json) {
    if (json.success) {
      M.toast({html: '<i class="material-icons left">check</i>' + json.message, classes: 'green rounded'});
    } else {
      M.toast({html: '<i class="material-icons left">error</i>' + json.message, classes: 'red rounded'});
    }
  })
  .catch(function(err) {
    M.toast({html: '<i class="material-icons left">error</i>Error de conexi&oacute;n', classes: 'red rounded'});
  });
}
</script>

<%@ include file="/WEB-INF/jsp/layout/footer.jsp" %>
