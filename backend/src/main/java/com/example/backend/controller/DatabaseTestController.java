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

    private final DataSource sqlServer1DataSource;
    private final DataSource sqlServer2DataSource;

    public DatabaseTestController(
            @Qualifier("sqlServer1DataSource") DataSource sqlServer1DataSource,
            @Qualifier("sqlServer2DataSource") DataSource sqlServer2DataSource) {
        this.sqlServer1DataSource = sqlServer1DataSource;
        this.sqlServer2DataSource = sqlServer2DataSource;
    }

    @GetMapping("/db-connections")
    public Map<String, Object> testDatabaseConnections() {
        Map<String, Object> result = new HashMap<>();
        
        // Test SQL Server 1 connection
        result.put("sqlserver1", testConnection(sqlServer1DataSource, "SQL Server 1"));
        
        // Test SQL Server 2 connection
        result.put("sqlserver2", testConnection(sqlServer2DataSource, "SQL Server 2"));
        
        return result;
    }

    @GetMapping("/sqlserver1")
    public Map<String, Object> testSQLServer1Connection() {
        return testConnection(sqlServer1DataSource, "SQL Server 1");
    }

    @GetMapping("/sqlserver2")
    public Map<String, Object> testSQLServer2Connection() {
        return testConnection(sqlServer2DataSource, "SQL Server 2");
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
        
        Map<String, Object> sqlServer1Result = testConnection(sqlServer1DataSource, "SQL Server 1");
        Map<String, Object> sqlServer2Result = testConnection(sqlServer2DataSource, "SQL Server 2");
        
        boolean sqlServer1Healthy = "SUCCESS".equals(sqlServer1Result.get("status"));
        boolean sqlServer2Healthy = "SUCCESS".equals(sqlServer2Result.get("status"));
        
        health.put("overall_status", (sqlServer1Healthy && sqlServer2Healthy) ? "HEALTHY" : "UNHEALTHY");
        health.put("sqlserver1_status", sqlServer1Healthy ? "UP" : "DOWN");
        health.put("sqlserver2_status", sqlServer2Healthy ? "UP" : "DOWN");
        health.put("timestamp", java.time.LocalDateTime.now());
        
        return health;
    }
}
