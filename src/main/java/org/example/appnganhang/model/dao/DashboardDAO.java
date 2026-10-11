package org.example.appnganhang.model.dao;

import org.example.appnganhang.model.entity.DashboardDB;
import org.example.appnganhang.model.util.DBConnection;

import java.sql.*;

public class DashboardDAO {

    public DashboardDB getDashboardStats(String maChiNhanh) {
        String sql = "{call sp_GetDashboardDB(?)}";

        try (Connection conn = DBConnection.getConnection(); // Hàm lấy Connection của bạn
             CallableStatement stmt = conn.prepareCall(sql)) {

            // Nếu maChiNhanh null (Admin) thì truyền NULL, ngược lại truyền mã chi nhánh
            if (maChiNhanh == null || maChiNhanh.isEmpty()) {
                stmt.setNull(1, Types.VARCHAR);
            } else {
                stmt.setString(1, maChiNhanh);
            }

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new DashboardDB(
                        rs.getInt("TongKhachHang"),
                        rs.getInt("TongTaiKhoanActive"),
                        rs.getDouble("TongSoDu"),
                        rs.getInt("SoGiaoDichHomNay"),
                        rs.getDouble("TongTienGiaoDichHomNay")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
