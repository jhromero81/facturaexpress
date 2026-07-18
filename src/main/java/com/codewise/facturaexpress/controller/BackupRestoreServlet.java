package com.codewise.facturaexpress.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.*;
import java.nio.file.Files;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Servlet para gestionar respaldos y restauraciones de la base de datos.
// Usa JDBC puro (sin mysqldump) para generar INSERTs y ejecutar restores.
// Los respaldos se almacenan como archivos .sql en java.io.tmpdir.
public class BackupRestoreServlet extends HttpServlet {

    private static final String BACKUP_DIR = System.getProperty("java.io.tmpdir") + "/facturaexpress_backups";
    private DataSource dataSource;

    // Permite inyectar el DataSource desde el bean de Spring
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // Inicializa el directorio de respaldos
    @Override
    public void init() throws ServletException {
        new File(BACKUP_DIR).mkdirs();
    }

    // Obtiene el DataSource inyectado
    private DataSource getDataSource() {
        if (dataSource != null) return dataSource;
        try {
            org.springframework.web.context.WebApplicationContext ctx =
                    org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
            if (ctx != null) dataSource = ctx.getBean(DataSource.class);
        } catch (Exception e) {
            // ignore
        }
        return dataSource;
    }

    // GET: descarga un archivo o muestra la pagina de respaldos
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("download".equals(action)) {
            String filename = req.getParameter("file");
            if (filename == null || filename.contains("..")) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            File file = new File(BACKUP_DIR, filename);
            if (!file.exists()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            resp.setContentType("application/octet-stream");
            resp.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
            Files.copy(file.toPath(), resp.getOutputStream());
            return;
        }
        req.setAttribute("activeNav", "backup");
        req.setAttribute("pageTitle", "Respaldos");

        File[] files = new File(BACKUP_DIR).listFiles((dir, name) -> name.endsWith(".sql"));
        req.setAttribute("backups", files != null ? files : new File[0]);
        req.getRequestDispatcher("/WEB-INF/jsp/backup.jsp").forward(req, resp);
    }

    // POST: crea, restaura o elimina respaldos segun la accion solicitada
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");

        if ("crear".equals(action)) {
            crearRespaldo(req, resp);
        } else if ("restaurar".equals(action)) {
            restaurarRespaldo(req, resp);
        } else if ("eliminar".equals(action)) {
            String filename = req.getParameter("file");
            if (filename != null && !filename.contains("..")) {
                File f = new File(BACKUP_DIR, filename);
                if (f.exists()) f.delete();
            }
            resp.sendRedirect(req.getContextPath() + "/backup");
        } else {
            resp.sendRedirect(req.getContextPath() + "/backup");
        }
    }

    // Genera un respaldo SQL recorriendo todas las tablas con JDBC puro
    private void crearRespaldo(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = "backup_" + timestamp + ".sql";
        File output = new File(BACKUP_DIR, filename);

        DataSource ds = getDataSource();
        if (ds == null) {
            req.setAttribute("error", "No se pudo obtener la conexion a la base de datos");
            listarBackups(req);
            req.getRequestDispatcher("/WEB-INF/jsp/backup.jsp").forward(req, resp);
            return;
        }

        try (Connection conn = ds.getConnection();
             PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(output), "UTF-8"))) {

            out.println("-- Respaldo FacturaExpress");
            out.println("-- Fecha: " + LocalDateTime.now());
            out.println("-- Base de datos: " + conn.getCatalog());
            out.println();

            String catalog = conn.getCatalog();
            DatabaseMetaData meta = conn.getMetaData();
            String[] types = {"TABLE"};
            ResultSet tables = meta.getTables(catalog, null, "%", types);

            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                if ("flyway_schema_history".equalsIgnoreCase(tableName)) continue;

                ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM " + tableName);
                ResultSetMetaData rsMeta = rs.getMetaData();
                int colCount = rsMeta.getColumnCount();

                while (rs.next()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("INSERT INTO ").append(tableName).append(" VALUES (");
                    for (int i = 1; i <= colCount; i++) {
                        if (i > 1) sb.append(",");
                        Object val = rs.getObject(i);
                        if (val == null) {
                            sb.append("NULL");
                        } else if (val instanceof Number) {
                            sb.append(val.toString());
                        } else if (val instanceof Boolean) {
                            sb.append(((Boolean) val) ? "1" : "0");
                        } else if (val instanceof java.util.Date || val instanceof java.sql.Timestamp || val instanceof java.time.LocalDateTime) {
                            sb.append("'").append(val.toString().replace("'", "''")).append("'");
                        } else if (val instanceof byte[]) {
                            sb.append("NULL");
                        } else {
                            sb.append("'").append(val.toString().replace("'", "''")).append("'");
                        }
                    }
                    sb.append(");");
                    out.println(sb.toString());
                }
                rs.close();
            }
            tables.close();

            resp.sendRedirect(req.getContextPath() + "/backup?success=ok&file=" + filename);

        } catch (Exception e) {
            req.setAttribute("error", "Error al crear respaldo: " + e.getMessage());
            listarBackups(req);
            req.getRequestDispatcher("/WEB-INF/jsp/backup.jsp").forward(req, resp);
        }
    }

    // Restaura la BD ejecutando cada sentencia del archivo .sql
    private void restaurarRespaldo(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String filename = req.getParameter("file");
        if (filename == null || filename.contains("..")) {
            req.setAttribute("error", "Nombre de archivo invalido");
            listarBackups(req);
            req.getRequestDispatcher("/WEB-INF/jsp/backup.jsp").forward(req, resp);
            return;
        }
        File input = new File(BACKUP_DIR, filename);
        if (!input.exists()) {
            req.setAttribute("error", "Archivo no encontrado");
            listarBackups(req);
            req.getRequestDispatcher("/WEB-INF/jsp/backup.jsp").forward(req, resp);
            return;
        }

        DataSource ds = getDataSource();
        if (ds == null) {
            req.setAttribute("error", "No se pudo obtener la conexion a la base de datos");
            listarBackups(req);
            req.getRequestDispatcher("/WEB-INF/jsp/backup.jsp").forward(req, resp);
            return;
        }

        try (Connection conn = ds.getConnection();
             BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(input), "UTF-8"))) {

            conn.setAutoCommit(false);
            StringBuilder stmt = new StringBuilder();
            String line;
            int executed = 0;
            int errors = 0;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("--")) continue;
                stmt.append(line);
                if (line.endsWith(";")) {
                    try (Statement st = conn.createStatement()) {
                        st.execute(stmt.toString());
                        executed++;
                    } catch (SQLException e) {
                        errors++;
                    }
                    stmt.setLength(0);
                }
            }
            conn.commit();

            resp.sendRedirect(req.getContextPath() + "/backup?success=restore&executed=" + executed + "&errors=" + errors);

        } catch (Exception e) {
            req.setAttribute("error", "Error al restaurar respaldo: " + e.getMessage());
            listarBackups(req);
            req.getRequestDispatcher("/WEB-INF/jsp/backup.jsp").forward(req, resp);
        }
    }

    // Carga la lista de archivos de respaldo en el request
    private void listarBackups(HttpServletRequest req) {
        File[] files = new File(BACKUP_DIR).listFiles((d, n) -> n.endsWith(".sql"));
        req.setAttribute("backups", files != null ? files : new File[0]);
        req.setAttribute("activeNav", "backup");
        req.setAttribute("pageTitle", "Respaldos");
    }
}
