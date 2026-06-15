<%-- Formulario para crear o editar un producto --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.codewise.facturaexpress.model.Producto" %>
<%@ include file="/WEB-INF/jsp/layout/header.jsp" %>
<%-- Scriptlet: determina si estamos en modo edición o creación --%>
<%
  Producto producto = (Producto) request.getAttribute("producto");
  boolean editando = producto != null && producto.getId() != null;
  String error = (String) request.getAttribute("error");
%>

<%-- Muestra error si existe --%>
<% if (error != null) { %>
  <div style="color:#e74c3c;background:rgba(231,76,60,0.1);padding:12px 16px;border-radius:8px;margin-bottom:16px;font-size:13px;"><i class="material-icons" style="font-size:16px;vertical-align:middle;">error_outline</i> <%= error %></div>
<% } %>

<div class="form-card">
  <div style="font-family:'Space Mono',monospace;font-size:18px;font-weight:700;color:var(--text-dark);margin-bottom:24px;">
    <%= editando ? "Editar Producto" : "Nuevo Producto" %>
  </div>

  <%-- Formulario: envía vía POST a /productos con acción "guardar" o "actualizar" --%>
  <form action="<%= ctx %>/productos" method="post">
    <input type="hidden" name="action" value="<%= editando ? "actualizar" : "guardar" %>">
    <% if (editando) { %>
      <%-- Campo oculto con el ID del producto a editar --%>
      <input type="hidden" name="id" value="<%= producto.getId() %>">
    <% } %>

    <div class="input-field">
      <i class="material-icons prefix">inventory_2</i>
      <input id="nombre" name="nombre" type="text" class="validate" required value="<%= editando ? producto.getNombre() : "" %>">
      <label for="nombre">Nombre *</label>
    </div>

    <div class="input-field">
      <i class="material-icons prefix">description</i>
      <textarea id="descripcion" name="descripcion" class="materialize-textarea"><%= editando && producto.getDescripcion() != null ? producto.getDescripcion() : "" %></textarea>
      <label for="descripcion">Descripci&oacute;n</label>
    </div>

    <div class="row" style="margin-bottom:0;">
      <div class="col s6">
        <div class="input-field">
          <i class="material-icons prefix">attach_money</i>
          <input id="precio" name="precio" type="number" class="validate" step="0.01" min="0.01" required value="<%= editando ? producto.getPrecio() : "" %>">
          <label for="precio">Precio *</label>
        </div>
      </div>
      <div class="col s6">
        <div class="input-field">
          <i class="material-icons prefix">inventory</i>
          <input id="stock" name="stock" type="number" class="validate" min="0" required value="<%= editando ? producto.getStock() : "0" %>">
          <label for="stock">Stock *</label>
        </div>
      </div>
    </div>

    <div class="form-actions">
      <button type="submit" class="btn btn-teal waves-effect waves-light"><i class="material-icons left">save</i><%= editando ? "Actualizar Producto" : "Guardar Producto" %></button>
      <a href="<%= ctx %>/productos" class="btn btn-cancel-compact waves-effect" style="display:inline-flex;align-items:center;gap:8px;height:38px;line-height:38px;padding:0 20px;border-radius:10px;text-decoration:none;font-size:13px;font-weight:600;">Cancelar</a>
    </div>
  </form>
</div>

<%@ include file="/WEB-INF/jsp/layout/footer.jsp" %>
