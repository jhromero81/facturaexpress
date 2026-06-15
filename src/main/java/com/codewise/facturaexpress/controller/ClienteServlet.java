package com.codewise.facturaexpress.controller;

import com.codewise.facturaexpress.model.Cliente;
import com.codewise.facturaexpress.service.ClienteService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Servlet para el CRUD de clientes.
 * Responde a GET /clientes (listar, nuevo, editar, eliminar) y
 * POST /clientes (guardar, actualizar).
 */
public class ClienteServlet extends HttpServlet {

    private ClienteService clienteService;

    @Override
    public void init() {
        clienteService = new ClienteService();
    }

    /**
     * Maneja las acciones de lectura sobre clientes:
     * listar (default), mostrar formulario nuevo, mostrar formulario edición, o eliminar.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (req.getSession().getAttribute("usuario") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        req.setAttribute("activeNav", "clientes");
        req.setAttribute("pageTitle", "Clientes");
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

    /**
     * Maneja las acciones de escritura sobre clientes: guardar (crear) o actualizar.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (req.getSession().getAttribute("usuario") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
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

    /**
     * Obtiene todos los clientes y los envía a la vista de listado.
     */
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

    /**
     * Busca un cliente por ID y carga sus datos en el formulario de edición.
     */
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

    /**
     * Crea un nuevo cliente con los datos del formulario y lo persiste.
     */
    private void guardarCliente(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Cliente cliente = new Cliente();
            cliente.setNombre(req.getParameter("nombre"));
            cliente.setEmail(req.getParameter("email"));
            cliente.setTelefono(req.getParameter("telefono"));
            cliente.setDireccion(req.getParameter("direccion"));

            clienteService.guardarCliente(cliente);
            req.setAttribute("mensaje", "Cliente registrado exitosamente");
            req.setAttribute("redirectUrl", req.getContextPath() + "/clientes");
            req.getRequestDispatcher("/WEB-INF/jsp/confirmacion.jsp").forward(req, resp);
        } catch (IllegalArgumentException e) {
            // Error de validación: regresa al formulario con el mensaje
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/cliente-form.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "Error al guardar cliente: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, resp);
        }
    }

    /**
     * Actualiza un cliente existente identificado por su ID.
     */
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
            resp.sendRedirect(req.getContextPath() + "/clientes");
        } catch (IllegalArgumentException e) {
            // Error de validación: reconstruye el objeto y regresa al formulario
            req.setAttribute("error", e.getMessage());
            req.setAttribute("cliente", construirClienteDesdeRequest(req));
            req.getRequestDispatcher("/WEB-INF/jsp/cliente-form.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "Error al actualizar cliente: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, resp);
        }
    }

    /**
     * Elimina un cliente por ID y redirige al listado.
     */
    private void eliminarCliente(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        try {
            Long id = Long.parseLong(req.getParameter("id"));
            clienteService.eliminarCliente(id);
            resp.sendRedirect(req.getContextPath() + "/clientes");
        } catch (Exception e) {
            req.setAttribute("error", "Error al eliminar cliente: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, resp);
        }
    }

    /**
     * Reconstruye un objeto Cliente con los parámetros HTTP recibidos,
     * útil para re-poblar el formulario cuando ocurre un error de validación.
     */
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
