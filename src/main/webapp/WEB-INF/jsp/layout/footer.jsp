<%-- Footer / layout de cierre: footer, modal de logout, loading overlay y scripts --%>
<%@ page import="com.codewise.facturaexpress.model.Usuario" %>
<%
  Usuario _usr = (Usuario) session.getAttribute("usuario");
  String _ctx = request.getContextPath();
%>
  </div><!-- /fx-content -->

  <%-- Footer con información legal y estado del sistema --%>
  <div class="fx-footer">
    <span>&copy; 2026 FACTURAEXPRESS &ndash; FACTURACI&Oacute;N SEGURA</span>
    <div class="fx-footer-statuses">
      <div class="fx-f-status"><span class="pulse-dot"></span> MOTOR FISCAL ACTIVO</div>
      <div class="fx-f-status"><span class="pulse-dot"></span> BACKUPS AL D&Iacute;A</div>
    </div>
  </div>
</div><!-- /fx-main-area -->

<%-- Modal de confirmación de cierre de sesión con información de usuario y advertencia --%>
<!-- LOGOUT MODAL -->
<div id="logoutModal" class="modal logout-modal">
  <div class="modal-content">
    <%-- Cabecera del modal de logout --%>
    <div class="logout-header">
      <div class="logout-icon"><i class="material-icons">exit_to_app</i></div>
      <h4>¿Cerrar sesi&oacute;n?</h4>
      <p>Est&aacute;s a punto de salir del sistema</p>
    </div>
    <div class="logout-body">
      <%-- Advertencia sobre cambios no guardados --%>
      <div class="warning-compact">
        <i class="material-icons">warning</i>
        <div><strong>&iexcl;Atenci&oacute;n!</strong><span> Los cambios no guardados se perder&aacute;n</span></div>
      </div>
      <%-- Información de la sesión actual --%>
      <div class="session-compact">
        <div class="session-title-compact"><i class="material-icons tiny">info</i>INFORMACI&Oacute;N DE SESI&Oacute;N</div>
        <div class="session-grid">
          <div class="session-item"><span class="session-label-compact">Usuario</span><span class="session-value-compact"><%= _usr != null ? _usr.getNombre() : "" %></span></div>
          <div class="session-item"><span class="session-label-compact">Rol</span><span class="session-value-compact"><%= _usr != null ? _usr.getRol() : "" %></span></div>
        </div>
      </div>
      <%-- Botones: cancelar o confirmar logout --%>
      <div class="modal-buttons-compact">
        <button class="btn btn-cancel-compact modal-close"><i class="material-icons left" style="font-size:16px;">close</i>Cancelar</button>
        <a href="<%= _ctx %>/logout" class="btn btn-logout-compact" id="confirmLogoutBtn"><i class="material-icons left" style="font-size:16px;">logout</i>Salir Ahora</a>
      </div>
      <div class="security-note-compact"><span class="pulse-dot"></span><span>Ser&aacute;s redirigido al inicio de sesi&oacute;n</span></div>
    </div>
  </div>
</div>

<%-- Overlay de carga que se muestra durante el cierre de sesión --%>
<div id="loadingOverlay" class="logout-loading">
  <div class="loading-content">
    <div class="preloader-wrapper small active">
      <div class="spinner-layer spinner-green-only">
        <div class="circle-clipper left"><div class="circle"></div></div>
        <div class="gap-patch"><div class="circle"></div></div>
        <div class="circle-clipper right"><div class="circle"></div></div>
      </div>
    </div>
    <p>Cerrando sesi&oacute;n...</p>
  </div>
</div>

<%-- Scripts globales: Materialize JS, core del sistema y app personalizada --%>
<script src="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/js/materialize.min.js"></script>
<script src="<%= _ctx %>/js/core.js"></script>
<script src="<%= _ctx %>/js/app.js"></script>
</body>
</html>
