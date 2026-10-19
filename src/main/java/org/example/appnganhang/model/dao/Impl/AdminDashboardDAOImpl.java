package org.example.appnganhang.model.dao.Impl;

import org.example.appnganhang.model.dao.intf.AdminDashboardDAO;
import org.example.appnganhang.model.entity.AdminDashboardStats;
import org.example.appnganhang.model.util.DBConnection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDashboardDAOImpl implements AdminDashboardDAO {

    @Override
    public AdminDashboardStats layThongKeDashboard() {

        String sql = "{CALL dbo.SP_Admin_Dashboard}";

        try (
                Connection conn = DBConnection.getConnection();
                CallableStatement stmt = conn.prepareCall(sql);
                ResultSet rs = stmt.executeQuery()
        ) {
            if (rs.next()) {
                return new AdminDashboardStats(
                        rs.getInt("TongTaiKhoan"),
                        rs.getInt("TongNhanVien"),
                        rs.getInt("TongChiNhanh"),
                        rs.getInt("TongGiaoDich"),
                        rs.getBigDecimal("TongTienGiaoDich"),
                        rs.getInt("TongCanhBao")
                );
            }

            throw new SQLException(
                    "Stored Procedure không trả về dữ liệu Dashboard."
            );

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Lỗi lấy thống kê Dashboard Admin: " + e.getMessage(),
                    e
            );
        }
    }
}