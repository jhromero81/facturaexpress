<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  String error = (String) request.getAttribute("error");
  String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>FacturaExpress &ndash; Iniciar Sesi&oacute;n</title>
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/css/materialize.min.css">
  <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
  <link href="https://fonts.googleapis.com/css2?family=Space+Mono:wght@400;700&family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="<%= ctx %>/css/styles.css">
  <script src="<%= ctx %>/js/accessibility-bootstrap.js"></script>
  <style>
    body.fx-login-page { position: relative; }
    body.fx-login-page::before {
      content: '';
      position: absolute;
      width: 500px; height: 500px;
      border-radius: 50%;
      background: radial-gradient(circle, rgba(26,188,156,.12) 0%, transparent 70%);
      top: -100px; left: -100px;
    }
    body.fx-login-page::after {
      content: '';
      position: absolute;
      width: 400px; height: 400px;
      border-radius: 50%;
      background: radial-gradient(circle, rgba(26,188,156,.08) 0%, transparent 70%);
      bottom: -80px; right: -80px;
    }
    .login-wrapper { width: 420px; max-width: 95%; position: relative; z-index: 1; }
    .login-card { background: #fff; border-radius: 16px; padding: 40px; box-shadow: 0 24px 80px rgba(0,0,0,0.35); }
    .login-logo { display: flex; align-items: center; gap: 12px; justify-content: center; margin-bottom: 8px; }
    .logo-box { width: 48px; height: 48px; background: var(--accent); border-radius: 10px; display: flex; align-items: center; justify-content: center; font-family: 'Space Mono', monospace; font-weight: 700; color: #fff; font-size: 22px; }
    .logo-text { font-family: 'Space Mono', monospace; font-size: 20px; font-weight: 700; color: #1a2535; }
    .login-subtitle { text-align: center; font-size: 13px; color: #90a4ae; margin-bottom: 32px; }
    .input-field input:focus { border-bottom-color: var(--accent) !important; box-shadow: 0 1px 0 0 var(--accent) !important; }
    .input-field input:focus + label, .input-field input:focus ~ label { color: var(--accent) !important; }
    .input-field .prefix { color: #90a4ae; }
    .input-field input:focus ~ .prefix { color: var(--accent); }
    .btn-login { width: 100%; background-color: var(--accent) !important; height: 46px; line-height: 46px; font-size: 15px; font-weight: 600; letter-spacing: 0.5px; border-radius: 8px !important; margin-top: 8px; }
    .btn-login:hover { background-color: var(--accent-dk) !important; }
    .login-footer { text-align: center; margin-top: 20px; font-size: 12px; color: #90a4ae; }
    .login-footer a { color: var(--accent); cursor: pointer; }
    .dian-badge { display: inline-flex; align-items: center; gap: 6px; background: rgba(26,188,156,0.1); color: var(--accent); padding: 5px 14px; border-radius: 20px; margin-bottom: 24px; justify-content: center; width: auto; }
    .error-msg { color: #e74c3c; font-size: 12px; margin-top: 8px; display: none; }
    .login-access-nav { position: absolute; top: 20px; right: 20px; display: flex; gap: 10px; }
    .login-access-nav .btn-floating { background-color: #ffffff !important; backdrop-filter: blur(5px) !important; -webkit-backdrop-filter: blur(5px) !important; box-shadow: 0 4px 12px rgba(0,0,0,0.15) !important; border: 1px solid #e0e0e0 !important; }
    .login-access-nav .btn-floating i { color: #546e7a !important; }
    .login-access-nav .btn-floating:hover { background-color: var(--accent) !important; }
  </style>
</head>
<body class="fx-login-page">
<script>!function(){try{var p=JSON.parse(localStorage.getItem('facturaexpress_prefs'));if(p){document.documentElement.setAttribute('data-fx-dark-mode',p.modoOscuro?'true':'');document.documentElement.setAttribute('data-fx-high-contrast',p.altoContraste?'true':'');if(p.modoOscuro)document.body.classList.add('fx-dark-mode');if(p.altoContraste)document.body.classList.add('fx-high-contrast');var s={small:'12px',medium:'15px',large:'18px'};document.documentElement.style.fontSize=s[p.tamanoTexto]||'15px'}}catch(e){}}();</script>

<div class="login-wrapper">
  <div class="login-card">

    <div class="login-access-nav">
      <a class="btn-floating btn-small waves-effect" id="loginDark" title="Modo Oscuro"><i class="material-icons">dark_mode</i></a>
      <a class="btn-floating btn-small waves-effect" id="loginContrast" title="Alto Contraste"><i class="material-icons">contrast</i></a>
    </div>

    <div class="login-logo">
      <div class="logo-box">F</div>
      <span class="logo-text">FacturaExpress</span>
    </div>
    <p class="login-subtitle">Sistema de Facturaci&oacute;n Electr&oacute;nica DIAN</p>

    <div class="dian-badge">
      <span class="pulse-dot"></span> Servidor DIAN Activo
    </div>

    <% if (error != null) { %>
      <p class="error-msg" role="alert" style="display:block;">
        <i class="material-icons" style="font-size:14px;vertical-align:middle;">error_outline</i> <%= error %>
      </p>
    <% } %>

    <form action="<%= ctx %>/login" method="post">
      <div class="input-field">
        <i class="material-icons prefix">person</i>
        <input id="loginUser" name="username" type="text" class="validate" required autofocus>
        <label for="loginUser">Usuario / NIT</label>
      </div>

      <div class="input-field">
        <i class="material-icons prefix">lock</i>
        <input id="loginPass" name="password" type="password" class="validate" required>
        <label for="loginPass">Contrase&ntilde;a</label>
        <i class="material-icons" id="togglePassword" style="position:absolute;right:10px;top:12px;cursor:pointer;color:#90a4ae;">visibility</i>
      </div>

      <button type="submit" class="btn btn-login waves-effect waves-light">
        <i class="material-icons left">login</i>Iniciar Sesi&oacute;n
      </button>
    </form>

    <div class="login-footer">
      &iquest;Olvidaste tu contrase&ntilde;a? <a>Recuperar acceso</a><br><br>
      &iquest;Problemas de acceso? <a>Contacte al administrador</a>
    </div>

  </div>
</div>

<script src="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/js/materialize.min.js"></script>
<script src="<%= ctx %>/js/core.js"></script>
<script>
document.addEventListener('DOMContentLoaded', function() {
  if (window.FacturaExpress && FacturaExpress.accessibility) {
    FacturaExpress.accessibility.applyAll();
  }
  var togglePass = document.getElementById('togglePassword');
  var passEl = document.getElementById('loginPass');
  if (togglePass && passEl) {
    togglePass.addEventListener('click', function() {
      var isPass = passEl.getAttribute('type') === 'password';
      passEl.setAttribute('type', isPass ? 'text' : 'password');
      togglePass.textContent = isPass ? 'visibility_off' : 'visibility';
    });
  }
  var loginDark = document.getElementById('loginDark');
  var loginContrast = document.getElementById('loginContrast');
  if (loginDark) {
    loginDark.addEventListener('click', function() { FacturaExpress.accessibility.toggleDarkMode(); });
  }
  if (loginContrast) {
    loginContrast.addEventListener('click', function() { FacturaExpress.accessibility.toggleHighContrast(); });
  }
});
</script>
</body>
</html>
