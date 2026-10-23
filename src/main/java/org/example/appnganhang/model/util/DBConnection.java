package org.example.appnganhang.model.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.example.appnganhang.controller.util.AppSession;

public class DBConnection {

    private static final String URL =
            "jdbc:sqlserver://localhost:1433;" +
                    "databaseName=DB_ChuyenTienNganHang;" +
                    "encrypt=true;" +
                    "trustServerCertificate=true;" +
                    "integratedSecurity=true";

    public static Connection getConnection() throws SQLException {
        Connection databaseLink = DriverManager.getConnection(URL);
        String username = AppSession.getUsername();
        if (username != null) {
            try (var check = databaseLink.prepareStatement(
                    "SELECT RoleName, QuyenHoatDong FROM dbo.TaiKhoanDangNhap_VaiTro WHERE TenDangNhap = ?")) {
                check.setString(1, username);
                try (var rs = check.executeQuery()) {
                    if (!rs.next() || !rs.getBoolean("QuyenHoatDong")
                            || !AppSession.getRole().equalsIgnoreCase(rs.getString("RoleName"))) {
                        AppSession.returnToLogin();
                        throw new SQLException("Quyền truy cập đã bị thu hồi hoặc thay đổi. Vui lòng đăng nhập lại.");
                    }
                }
            } catch (SQLException e) {
                databaseLink.close();
                throw e;
            }
        }
        System.out.println("Connected SQL server");
        return  databaseLink;
    }
}
