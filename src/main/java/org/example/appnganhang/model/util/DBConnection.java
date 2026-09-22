package org.example.appnganhang.model.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
            "jdbc:sqlserver://localhost:1433;" +
                    "databaseName=DB_ChuyenTienNganHang;" +
                    "encrypt=true;" +
                    "trustServerCertificate=true;" +
                    "integratedSecurity=true";

    public static Connection getConnection() throws SQLException {
        Connection databaseLink = DriverManager.getConnection(URL);
        System.out.println("Connected SQL server");
        return  databaseLink;
    }
}