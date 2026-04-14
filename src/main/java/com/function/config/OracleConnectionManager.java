package com.function.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.UUID;

public class OracleConnectionManager {

    static {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Could not load oracle.jdbc.OracleDriver", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        String url = System.getenv("ORACLE_DB_URL");
        String user = System.getenv("ORACLE_DB_USER");
        String password = System.getenv("ORACLE_DB_PASSWORD");

        if (url == null || url.isBlank()) {
            throw new IllegalStateException("ORACLE_DB_URL is not configured");
        }
        if (user == null || user.isBlank()) {
            throw new IllegalStateException("ORACLE_DB_USER is not configured");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalStateException("ORACLE_DB_PASSWORD is not configured");
        }

        String walletPath = prepareWallet();

        Properties props = new Properties();
        props.setProperty("user", user);
        props.setProperty("password", password);
        props.setProperty("oracle.net.tns_admin", walletPath);

        return DriverManager.getConnection(url, props);
    }

    private static String prepareWallet() {
        try {
            Path walletDir = Files.createTempDirectory("oracle-wallet-" + UUID.randomUUID());

            String[] files = {
                    "tnsnames.ora",
                    "sqlnet.ora",
                    "cwallet.sso",
                    "ewallet.p12",
                    "keystore.jks",
                    "truststore.jks",
                    "ojdbc.properties"
            };

            for (String fileName : files) {
                copyResourceIfExists("wallet/" + fileName, walletDir.resolve(fileName));
            }

            logWalletFiles(walletDir);

            return walletDir.toAbsolutePath().toString();
        } catch (IOException e) {
            throw new RuntimeException("Error preparing wallet in runtime", e);
        }
    }

    private static void copyResourceIfExists(String resourcePath, Path targetPath) throws IOException {
        try (InputStream is = OracleConnectionManager.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                System.out.println("Resource not found, skipping: " + resourcePath);
                return;
            }
            Files.copy(is, targetPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Copied: " + resourcePath + " -> " + targetPath);
        }
    }

    private static void logWalletFiles(Path walletDir) {
        String[] expected = {
                "tnsnames.ora",
                "sqlnet.ora",
                "cwallet.sso",
                "ewallet.p12",
                "keystore.jks",
                "truststore.jks",
                "ojdbc.properties"
        };

        for (String name : expected) {
            File f = walletDir.resolve(name).toFile();
            System.out.println(name + " exists? " + f.exists());
        }
    }
}