package com.function.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class OracleConnectionManager {
    private static final String URL = System.getenv("ORACLE_DB_URL");
    private static final String USER = System.getenv("ORACLE_DB_USER");
    private static final String PASSWORD = System.getenv("ORACLE_DB_PASSWORD");
    private static final String TNS_ADMIN = System.getenv("TNS_ADMIN");

    static {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        System.out.println("ORACLE_DB_URL=" + URL);
        System.out.println("ORACLE_DB_USER=" + USER);
        System.out.println("TNS_ADMIN=" + TNS_ADMIN);
        System.out.println("Exists wallet path? " + new java.io.File(TNS_ADMIN).exists());
        System.out.println("Exists tnsnames.ora? " + new java.io.File(TNS_ADMIN, "tnsnames.ora").exists());
        System.out.println("Exists sqlnet.ora? " + new java.io.File(TNS_ADMIN, "sqlnet.ora").exists());

        if (TNS_ADMIN != null && !TNS_ADMIN.isEmpty()) {
            System.setProperty("oracle.net.tns_admin", TNS_ADMIN);
        }

        Properties props = new Properties();
        props.setProperty("user", USER);
        props.setProperty("password", PASSWORD);

        return DriverManager.getConnection(URL, props);
    }
}
