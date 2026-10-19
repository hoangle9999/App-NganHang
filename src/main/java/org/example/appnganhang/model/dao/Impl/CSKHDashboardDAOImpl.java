package org.example.appnganhang.model.dao.Impl;

import org.example.appnganhang.model.dao.intf.CSKHDashboardDAO;
import org.example.appnganhang.model.entity.CSKHDashboardStats;
import org.example.appnganhang.model.util.DBConnection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CSKHDashboardDAOImpl implements CSKHDashboardDAO {

    @Override
    public CSKHDashboardStats layThongKe() throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             CallableStatement statement = connection.prepareCall("{call dbo.sp_CSKH_Dashboard}");
             ResultSet resultSet = statement.executeQuery()) {
            if (!resultSet.next()) return new CSKHDashboardStats(0, 0, 0, 0);
            return new CSKHDashboardStats(
                    resultSet.getInt("TongKhachHang"),
                    resultSet.getInt("TongTaiKhoanHoatDong"),
                    resultSet.getInt("GiaoDichHomNay"),
                    resultSet.getInt("KhieuNaiCho"));
        }
    }
}
