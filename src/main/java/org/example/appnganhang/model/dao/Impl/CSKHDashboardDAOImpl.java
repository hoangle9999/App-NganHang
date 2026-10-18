
package org.example.appnganhang.model.dao.Impl;

import org.example.appnganhang.model.dao.intf.CSKHDashboardDAO;
import org.example.appnganhang.model.entity.CSKHDashboardStats;
import org.example.appnganhang.model.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CSKHDashboardDAOImpl implements CSKHDashboardDAO {

    private static final String SQL_DEM_KHACH_HANG =
            "SELECT COUNT(*) FROM KhachHang";

    private static final String SQL_DEM_TAI_KHOAN_HOAT_DONG =
            "SELECT COUNT(*) " +
                    "FROM TaiKhoanNganHang " +
                    "WHERE TrangThai = N'Hoạt động'";

    private static final String SQL_DEM_GIAO_DICH_HOM_NAY =
            "SELECT COUNT(*) " +
                    "FROM GiaoDichChuyenTien " +
                    "WHERE CAST(NgayGiaoDich AS DATE) = CAST(GETDATE() AS DATE)";

    /**
     * Đếm số bản ghi theo câu truy vấn SQL.
     *
     * @param connection kết nối đến SQL Server
     * @param sql câu truy vấn cần thực hiện
     * @return số lượng bản ghi
     * @throws SQLException nếu truy vấn thất bại
     */
    private int demSoLuong(Connection connection, String sql)
            throws SQLException {

        try (PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getInt(1);
            }

            return 0;
        }
    }

    /**
     * Lấy các số liệu thống kê trên Dashboard CSKH.
     *
     * @return đối tượng chứa số lượng khách hàng,
     *         tài khoản hoạt động và giao dịch hôm nay
     * @throws SQLException nếu kết nối hoặc truy vấn thất bại
     */
    @Override
    public CSKHDashboardStats layThongKe() throws SQLException {

        try (Connection connection = DBConnection.getConnection()) {

            int tongKhachHang = demSoLuong(
                    connection,
                    SQL_DEM_KHACH_HANG
            );

            int taiKhoanHoatDong = demSoLuong(
                    connection,
                    SQL_DEM_TAI_KHOAN_HOAT_DONG
            );

            int giaoDichHomNay = demSoLuong(
                    connection,
                    SQL_DEM_GIAO_DICH_HOM_NAY
            );

            return new CSKHDashboardStats(
                    tongKhachHang,
                    taiKhoanHoatDong,
                    giaoDichHomNay
            );
        }
    }
}
