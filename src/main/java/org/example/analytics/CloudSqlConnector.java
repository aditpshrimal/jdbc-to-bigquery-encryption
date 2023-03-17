package org.example.analytics;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.sql.Statement;

/**
 * A sample app that connects to a Cloud SQL instance and lists all available tables
 in a database.
 */

public class CloudSqlConnector {

    // Make these variables private to prevent direct access
    private static String driverClassName;
    private static String jdbcUrl;
    private static String username;
    private static String password;
    private static String sqlQuery;
    private static String bigqueryDataset;

    // Add a constructor to set the private variables
    public CloudSqlConnector(String driverClassName, String jdbcUrl, String username, String password, String sqlQuery, String bigqueryDataset) {
        this.driverClassName = driverClassName;
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
        this.sqlQuery = sqlQuery;
        this.bigqueryDataset = bigqueryDataset;
    }

    public void run() throws SQLNonTransientConnectionException, IOException, SQLException {
        Connection connection = DriverManager.getConnection(jdbcUrl, username, password);

        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(sqlQuery);
            while (resultSet.next()) {
                // Do something with the result set
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
