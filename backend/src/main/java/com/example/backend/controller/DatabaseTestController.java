package com.example.backend.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class DatabaseTestController {

    private final DataSource mysqlDataSource;
    private final DataSource sqlServerDataSource;

    public DatabaseTestController(
            @Qualifier("mysqlDataSource") DataSource mysqlDataSource,
            @Qualifier("sqlServerDataSource") DataSource sqlServerDataSource) {
        this.mysqlDataSource = mysqlDataSource;
        this.sqlServerDataSource = sqlServerDataSource;
    }

    @GetMapping("/db-connections")
    public Map<String, Object> testDatabaseConnections() {
        Map<String, Object> result = new HashMap<>();
        
        // Test MySQL connection
        result.put("mysql", testConnection(mysqlDataSource, "MySQL"));
        
        // Test SQL Server connection
        result.put("sqlserver", testConnection(sqlServerDataSource, "SQL Server"));
        
        return result;
    }

    @GetMapping("/mysql")
    public Map<String, Object> testMySQLConnection() {
        return testConnection(mysqlDataSource, "MySQL");
    }

    @GetMapping("/sqlserver")
    public Map<String, Object> testSQLServerConnection() {
        return testConnection(sqlServerDataSource, "SQL Server");
    }

    private Map<String, Object> testConnection(DataSource dataSource, String dbType) {
        Map<String, Object> connectionInfo = new HashMap<>();
        
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            
            connectionInfo.put("status", "SUCCESS");
            connectionInfo.put("database_type", dbType);
            connectionInfo.put("database_product_name", metaData.getDatabaseProductName());
            connectionInfo.put("database_product_version", metaData.getDatabaseProductVersion());
            connectionInfo.put("driver_name", metaData.getDriverName());
            connectionInfo.put("driver_version", metaData.getDriverVersion());
            connectionInfo.put("url", metaData.getURL());
            connectionInfo.put("username", metaData.getUserName());
            connectionInfo.put("is_connection_valid", connection.isValid(5));
            
        } catch (SQLException e) {
            connectionInfo.put("status", "FAILED");
            connectionInfo.put("database_type", dbType);
            connectionInfo.put("error_message", e.getMessage());
            connectionInfo.put("error_code", e.getErrorCode());
            connectionInfo.put("sql_state", e.getSQLState());
        } catch (Exception e) {
            connectionInfo.put("status", "FAILED");
            connectionInfo.put("database_type", dbType);
            connectionInfo.put("error_message", e.getMessage());
            connectionInfo.put("error_type", e.getClass().getSimpleName());
        }
        
        return connectionInfo;
    }

    @GetMapping("/health")
    public Map<String, Object> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        
        Map<String, Object> mysqlResult = testConnection(mysqlDataSource, "MySQL");
        Map<String, Object> sqlServerResult = testConnection(sqlServerDataSource, "SQL Server");
        
        boolean mysqlHealthy = "SUCCESS".equals(mysqlResult.get("status"));
        boolean sqlServerHealthy = "SUCCESS".equals(sqlServerResult.get("status"));
        
        health.put("overall_status", (mysqlHealthy && sqlServerHealthy) ? "HEALTHY" : "UNHEALTHY");
        health.put("mysql_status", mysqlHealthy ? "UP" : "DOWN");
        health.put("sqlserver_status", sqlServerHealthy ? "UP" : "DOWN");
        health.put("timestamp", java.time.LocalDateTime.now());
        
        return health;
    }
}