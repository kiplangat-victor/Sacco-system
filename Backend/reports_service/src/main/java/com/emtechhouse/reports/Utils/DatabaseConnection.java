package com.emtechhouse.reports.Utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
@Slf4j
public class DatabaseConnection {

    @Value("${spring.datasource.url}")
    private String db;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;


    private Connection connection;

    public DatabaseConnection() {
    }

    public Connection newConnection() throws SQLException {
        log.info("Url {}", this.db);
        System.out.println("chege testing");
        this.connection = DriverManager.getConnection(this.db, this.username, this.password);
        return this.connection;
    }
    public void closeConnection() throws SQLException {
        this.connection.close();
    }
}
