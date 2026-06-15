package com.codewise.facturaexpress.config;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConfig {

    private static DatabaseConfig instance;
    private final DataSource dataSource;

    private DatabaseConfig() {
        this.dataSource = SpringContextHolder.getBean(DataSource.class);
    }

    public static synchronized DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
