package com.codewise.facturaexpress.controller;

import com.codewise.facturaexpress.config.AuthUtil;
import com.codewise.facturaexpress.model.Cliente;
import com.codewise.facturaexpress.model.Usuario;
import com.codewise.facturaexpress.service.ClienteService;
import com.codewise.facturaexpress.service.LogAuditoriaService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ClienteServlet extends HttpServlet {

    private final ClienteService clienteService;
    private final LogAuditoriaService logService;

    public ClienteServlet(ClienteService clienteService, LogAuditoriaService logService) {
        this.clienteService = clienteService;
        this.logService = logService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (AuthUtil.getUsuario(req) == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        req.setAttribute("activeNav", "clientes");
        req.setAttribute("pageTitle", "Clientes");
        req.setAttribute("csrfToken", AuthUtil.getCsrfToken(req.getSession()));
        String action = req.getParameter("action");
        if (action == null) action = "listar";

        switch (action) {
            case "nuevo":
                req.getRequestDispatcher("/WEB-INF/jsp/cliente-form.jsp").forward(req, resp);
                break;
            case "editar":
                mostrarFormularioEdicion(req, resp);
                break;
            case "eliminar":
                eliminarCliente(req, resp);
                break;
            default:
                listarClientes(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (AuthUtil.getUsuario(req) == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        if (!AuthUtil.validarCsrfToken(req)) {
            req.setAttribute("error", "Token CSRF invalido");
            req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, resp);
            return;
        }
        req.setAttribute("activeNav", "clientes");
        req.setAttribute("pageTitle", "Clientes");
        String action = req.getParameter("action");
        if ("guardar".equals(action)) {
            guardarCliente(req, resp);
        } else if ("actualizar".equals(action)) {
            actualizarCliente(req, resp);
        }
    }

    private void listarClientes(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            List<Cliente> clientes = clienteService.listarClientes();
            req.setAttribute("clientes", clientes);
            req.getRequestDispatcher("/WEB-INF/jsp/clientes.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "Error al listar clientes: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, resp);
        }
    }

    private void mostrarFormularioEdicion(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Long id = Long.parseLong(req.getParameter("id"));
            Optional<Cliente> clienteOpt = clienteService.buscarPorId(id);
            if (clienteOpt.isPresent()) {
                req.setAttribute("cliente", clienteOpt.get());
                req.getRequestDispatcher("/WEB-INF/jsp/cliente-form.jsp").forward(req, resp);
            } else {
                req.setAttribute("error", "Cliente no encontrado con ID: " + id);
                req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, resp);
            }
        } catch (NumberFormatException e) {
            req.setAttribute("error", "ID de cliente invalido");
            req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, resp);
        }
    }

    private void guardarCliente(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Cliente cliente = new Cliente();
            cliente.setNombre(req.getParameter("nombre"));
            cliente.setEmail(req.getParameter("email"));
            cliente.setTelefono(req.getParameter("telefono"));
            cliente.setDireccion(req.getParameter("direccion"));

            Cliente guardado = clienteService.guardarCliente(cliente);
            Usuario usuario = AuthUtil.getUsuario(req);
            logService.registrar(usuario, "INSERT cliente id=" + guardado.getId(), "clientes", guardado.getId(), req);
            req.setAttribute("mensaje", "Cliente registrado exitosamente");
            req.setAttribute("redirectUrl", req.getContextPath() + "/clientes");
            req.getRequestDispatcher("/WEB-INF/jsp/confirmacion.jsp").forward(req, resp);
        } catch (IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("csrfToken", AuthUtil.getCsrfToken(req.getSession()));
            req.getRequestDispatcher("/WEB-INF/jsp/cliente-form.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "Error al guardar cliente: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, resp);
        }
    }

    private void actualizarCliente(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Long id = Long.parseLong(req.getParameter("id"));
            Cliente cliente = new Cliente();
            cliente.setId(id);
            cliente.setNombre(req.getParameter("nombre"));
            cliente.setEmail(req.getParameter("email"));
            cliente.setTelefono(req.getParameter("telefono"));
            cliente.setDireccion(req.getParameter("direccion"));

            clienteService.actualizarCliente(cliente);
            Usuario usuario = AuthUtil.getUsuario(req);
            logService.registrar(usuario, "UPDATE cliente id=" + id, "clientes", id, req);
            resp.sendRedirect(req.getContextPath() + "/clientes");
        } catch (IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("csrfToken", AuthUtil.getCsrfToken(req.getSession()));
            req.setAttribute("cliente", construirClienteDesdeRequest(req));
            req.getRequestDispatcher("/WEB-INF/jsp/cliente-form.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "Error al actualizar cliente: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, resp);
        }
    }

    private void eliminarCliente(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        try {
            Long id = Long.parseLong(req.getParameter("id"));
            clienteService.eliminarCliente(id);
            Usuario usuario = AuthUtil.getUsuario(req);
            logService.registrar(usuario, "DELETE cliente id=" + id, "clientes", id, req);
            resp.sendRedirect(req.getContextPath() + "/clientes");
        } catch (NumberFormatException e) {
            req.setAttribute("error", "ID de cliente invalido");
            req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "Error al eliminar cliente: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, resp);
        }
    }

    private Cliente construirClienteDesdeRequest(HttpServletRequest req) {
        Cliente c = new Cliente();
        if (req.getParameter("id") != null) {
            c.setId(Long.parseLong(req.getParameter("id")));
        }
        c.setNombre(req.getParameter("nombre"));
        c.setEmail(req.getParameter("email"));
        c.setTelefono(req.getParameter("telefono"));
        c.setDireccion(req.getParameter("direccion"));
        return c;
    }
}
