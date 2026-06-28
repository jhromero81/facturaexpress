package com.codewise.facturaexpress.controller;

import com.codewise.facturaexpress.config.AuthUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BackupRestoreServlet extends HttpServlet {

    private static final String BACKUP_DIR = System.getProperty("java.io.tmpdir") + "/facturaexpress_backups";

    @Override
    public void init() {
        new java.io.File(BACKUP_DIR).mkdirs();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (AuthUtil.getUsuario(req) == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
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
        req.setAttribute("csrfToken", AuthUtil.getCsrfToken(req.getSession()));

        File[] files = new File(BACKUP_DIR).listFiles((dir, name) -> name.endsWith(".sql"));
        req.setAttribute("backups", files != null ? files : new File[0]);
        req.getRequestDispatcher("/WEB-INF/jsp/backup.jsp").forward(req, resp);
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
        String action = req.getParameter("action");

        if ("crear".equals(action)) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = "backup_" + timestamp + ".sql";
            File output = new File(BACKUP_DIR, filename);

            String dbUrl = getServletContext().getInitParameter("DB_URL");
            if (dbUrl == null) dbUrl = "jdbc:mysql://localhost:3306/factura_express_db";
            String dbName = dbUrl.contains("factura_express_db") ? "factura_express_db" : "factura_express_Db";
            String dbUser = System.getenv().getOrDefault("DB_USER", "root");
            String dbPass = System.getenv().getOrDefault("DB_PASSWORD", "JlJ020912#*");

            try {
                ProcessBuilder pb = new ProcessBuilder(
                        "mysqldump",
                        "-u" + dbUser,
                        "-p" + dbPass,
                        "--single-transaction",
                        "--routines",
                        "--triggers",
                        dbName
                );
                pb.redirectErrorStream(true);
                pb.redirectOutput(output);
                Process p = pb.start();
                int exit = p.waitFor();

                if (exit == 0) {
                    resp.sendRedirect(req.getContextPath() + "/backup?success=ok&file=" + filename);
                } else {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                        String line;
                        StringBuilder err = new StringBuilder();
                        while ((line = reader.readLine()) != null) {
                            err.append(line).append("\n");
                        }
                        req.setAttribute("error", "Error mysqldump (exit=" + exit + "): " + err);
                    }
                    req.setAttribute("backups", new File(BACKUP_DIR).listFiles((d, n) -> n.endsWith(".sql")));
                    req.getRequestDispatcher("/WEB-INF/jsp/backup.jsp").forward(req, resp);
                }
            } catch (Exception e) {
                req.setAttribute("error", "Error al crear respaldo: " + e.getMessage());
                req.setAttribute("backups", new File(BACKUP_DIR).listFiles((d, n) -> n.endsWith(".sql")));
                req.getRequestDispatcher("/WEB-INF/jsp/backup.jsp").forward(req, resp);
            }
        } else if ("restaurar".equals(action)) {
            String filename = req.getParameter("file");
            if (filename == null || filename.contains("..")) {
                req.setAttribute("error", "Nombre de archivo invalido");
                req.setAttribute("backups", new File(BACKUP_DIR).listFiles((d, n) -> n.endsWith(".sql")));
                req.getRequestDispatcher("/WEB-INF/jsp/backup.jsp").forward(req, resp);
                return;
            }
            File input = new File(BACKUP_DIR, filename);
            if (!input.exists()) {
                req.setAttribute("error", "Archivo no encontrado");
                req.setAttribute("backups", new File(BACKUP_DIR).listFiles((d, n) -> n.endsWith(".sql")));
                req.getRequestDispatcher("/WEB-INF/jsp/backup.jsp").forward(req, resp);
                return;
            }

            String dbUrl = getServletContext().getInitParameter("DB_URL");
            if (dbUrl == null) dbUrl = "jdbc:mysql://localhost:3306/factura_express_db";
            String dbName = dbUrl.contains("factura_express_db") ? "factura_express_db" : "factura_express_Db";
            String dbUser = System.getenv().getOrDefault("DB_USER", "root");
            String dbPass = System.getenv().getOrDefault("DB_PASSWORD", "JlJ020912#*");

            try {
                ProcessBuilder pb = new ProcessBuilder(
                        "mysql",
                        "-u" + dbUser,
                        "-p" + dbPass,
                        dbName
                );
                pb.redirectInput(input);
                pb.redirectErrorStream(true);
                Process p = pb.start();
                int exit = p.waitFor();

                if (exit == 0) {
                    resp.sendRedirect(req.getContextPath() + "/backup?success=restore");
                } else {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                        String line;
                        StringBuilder err = new StringBuilder();
                        while ((line = reader.readLine()) != null) {
                            err.append(line).append("\n");
                        }
                        req.setAttribute("error", "Error mysql restore (exit=" + exit + "): " + err);
                    }
                    req.setAttribute("backups", new File(BACKUP_DIR).listFiles((d, n) -> n.endsWith(".sql")));
                    req.getRequestDispatcher("/WEB-INF/jsp/backup.jsp").forward(req, resp);
                }
            } catch (Exception e) {
                req.setAttribute("error", "Error al restaurar respaldo: " + e.getMessage());
                req.setAttribute("backups", new File(BACKUP_DIR).listFiles((d, n) -> n.endsWith(".sql")));
                req.getRequestDispatcher("/WEB-INF/jsp/backup.jsp").forward(req, resp);
            }
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
}
