package org.example.appnganhang.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
            "jdbc:sqlserver://LETHAIHOANG:1433;" +
                    "databaseName=DB_ChuyenTienNganHang;" +
                    "encrypt=true;" +
                    "trustServerCertificate=true;";

    private static final String USER = "sa";
    private static final String PASSWORD = "123456"; // Mật khẩu bạn vừa đặt ở bước 1

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}