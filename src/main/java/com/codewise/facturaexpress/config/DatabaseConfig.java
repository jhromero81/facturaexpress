package com.codewise.facturaexpress.config;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConfig {

    private static final Logger LOGGER = Logger.getLogger(DatabaseConfig.class.getName());
    private static DatabaseConfig instance;
    private final String url;
    private final String username;
    private final String password;

    private DatabaseConfig() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new RuntimeException("No se encontro application.properties");
            }
            props.load(input);
            this.url = props.getProperty("spring.datasource.url");
            this.username = props.getProperty("spring.datasource.username");
            this.password = props.getProperty("spring.datasource.password");
            LOGGER.info("DatabaseConfig inicializado correctamente");
        } catch (Exception e) {
            throw new RuntimeException("Error al cargar configuracion de base de datos", e);
        }
    }

    public static synchronized DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);
        LOGGER.log(Level.FINE, "Conexion JDBC establecida");
        return connection;
    }
}
