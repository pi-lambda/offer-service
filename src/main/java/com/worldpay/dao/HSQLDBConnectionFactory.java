package com.worldpay.dao;

import org.apache.commons.dbutils.DbUtils;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class HSQLDBConnectionFactory {

    private static final String JDBC_CONNECTION_URL = "jdbc:hsqldb:mem:worldpaydb";
    private static final String USER_NAME = "SA";
    private static final String PASSWORD = "";
    public static final String HSQLDB_JDBC_DRIVER = "org.hsqldb.jdbc.JDBCDriver";

    private static DataSource dataSource;

    static {
        Connection connection = null;
        Statement initStatement = null;
        try {
            initialiseDataSource();
            connection = getConnection();
            connection.setAutoCommit(false);
            initStatement = connection.createStatement();
            String dropOffersTable = "DROP TABLE Offers IF EXISTS;";
            String createOffersTable = "CREATE TABLE Offers (\n" +
                    "    id BIGINT,\n" +
                    "    description VARCHAR(256),\n" +
                    "    price NUMERIC,\n" +
                    "    currency VARCHAR(3),\n" +
                    "    created TIMESTAMP,\n" +
                    "    validity_period INTEGER,\n" +
                    "    valid_until TIMESTAMP\n" +
                    ");";
            initStatement.executeUpdate(dropOffersTable);
            initStatement.executeUpdate(createOffersTable);
            connection.commit();
        } catch (SQLException e) {
            DbUtils.rollbackAndCloseQuietly(connection);
        } finally {
            DbUtils.closeQuietly(initStatement);
            DbUtils.closeQuietly(connection);
        }
    }

    private static void initialiseDataSource() {
        PoolProperties poolProperties = new PoolProperties();
        poolProperties.setUrl(JDBC_CONNECTION_URL);
        poolProperties.setDriverClassName(HSQLDB_JDBC_DRIVER);
        poolProperties.setUsername(USER_NAME);
        poolProperties.setPassword(PASSWORD);
        dataSource = new DataSource(poolProperties);
    }

    public HSQLDBConnectionFactory() {
        try {
            Class.forName(HSQLDB_JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("HSQLDB driver class not found!");
        }
    }

    public static Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to get connection from dataSource " + dataSource.getName());
        }
    }

    public static void init() {}

}

