package org.example.appnganhang.model.dao.Impl;

import org.example.appnganhang.model.dao.intf.CSKHCustomerDAO;
import org.example.appnganhang.model.entity.CSKHCustomerAccountItem;
import org.example.appnganhang.model.entity.KhachHang;
import org.example.appnganhang.model.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CSKHCustomerDAOImpl implements CSKHCustomerDAO {
    private static final String SQL_DANH_SACH =
            "SELECT MaKhachHang, HoTen, SoCCCD, SoDienThoai, Email, DiaChi, TenDangNhap FROM KhachHang";
    private static final String SQL_TON_TAI_CCCD =
            "SELECT COUNT(*) FROM KhachHang WHERE SoCCCD = ?";
    private static final String SQL_TON_TAI_SDT =
            "SELECT COUNT(*) FROM KhachHang WHERE SoDienThoai = ? AND (? IS NULL OR MaKhachHang <> ?)";
    private static final String SQL_THEM_KHACH_HANG =
            "INSERT INTO KhachHang (MaKhachHang, HoTen, SoCCCD, SoDienThoai, Email, DiaChi, TenDangNhap) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_THEM_TAI_KHOAN_DANG_NHAP =
            "INSERT INTO TaiKhoanDangNhap_VaiTro (TenDangNhap, MatKhau, RoleName, MaNguoiDung) VALUES (?, ?, N'Khách hàng', ?)";
    private static final String SQL_CAP_NHAT =
            "UPDATE KhachHang SET HoTen = ?, SoDienThoai = ?, Email = ?, DiaChi = ? WHERE MaKhachHang = ?";
    private static final String SQL_TAI_KHOAN_KHACH_HANG =
            "SELECT tk.SoTaiKhoan, tk.SoDu, tk.TrangThai, cn.TenChiNhanh " +
                    "FROM TaiKhoanNganHang tk LEFT JOIN ChiNhanh cn ON tk.MaChiNhanh = cn.MaChiNhanh " +
                    "WHERE tk.MaKhachHang = ?";

    @Override
    public List<KhachHang> layDanhSachKhachHang() throws SQLException {
        List<KhachHang> danhSach = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_DANH_SACH);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                KhachHang khachHang = new KhachHang();
                khachHang.setMaKhachHang(resultSet.getString("MaKhachHang"));
                khachHang.setHoTen(resultSet.getString("HoTen"));
                khachHang.setSoCCCD(resultSet.getString("SoCCCD"));
                khachHang.setSoDienThoai(resultSet.getString("SoDienThoai"));
                khachHang.setEmail(resultSet.getString("Email"));
                khachHang.setDiaChi(resultSet.getString("DiaChi"));
                khachHang.setTenDangNhap(resultSet.getString("TenDangNhap"));
                danhSach.add(khachHang);
            }
        }
        return danhSach;
    }

    @Override
    public boolean tonTaiCCCD(String soCCCD) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_TON_TAI_CCCD)) {
            statement.setString(1, soCCCD);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        }
    }

    @Override
    public boolean tonTaiSoDienThoai(String soDienThoai, String maKhachHangBoQua) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_TON_TAI_SDT)) {
            statement.setString(1, soDienThoai);
            statement.setString(2, maKhachHangBoQua);
            statement.setString(3, maKhachHangBoQua);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        }
    }

    @Override
    public void themKhachHang(KhachHang khachHang, String matKhau) throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            boolean autoCommitBanDau = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement statement = connection.prepareStatement(SQL_THEM_KHACH_HANG)) {
                    statement.setString(1, khachHang.getMaKhachHang());
                    statement.setString(2, khachHang.getHoTen());
                    statement.setString(3, khachHang.getSoCCCD());
                    statement.setString(4, khachHang.getSoDienThoai());
                    statement.setString(5, khachHang.getEmail());
                    statement.setString(6, khachHang.getDiaChi());
                    statement.setString(7, khachHang.getTenDangNhap());
                    statement.executeUpdate();
                }
                try (PreparedStatement statement = connection.prepareStatement(SQL_THEM_TAI_KHOAN_DANG_NHAP)) {
                    statement.setString(1, khachHang.getTenDangNhap());
                    statement.setString(2, matKhau);
                    statement.setString(3, khachHang.getMaKhachHang());
                    statement.executeUpdate();
                }
                connection.commit();
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(autoCommitBanDau);
            }
        }
    }

    @Override
    public void capNhatKhachHang(KhachHang khachHang) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_CAP_NHAT)) {
            statement.setString(1, khachHang.getHoTen());
            statement.setString(2, khachHang.getSoDienThoai());
            statement.setString(3, khachHang.getEmail());
            statement.setString(4, khachHang.getDiaChi());
            statement.setString(5, khachHang.getMaKhachHang());
            statement.executeUpdate();
        }
    }

    @Override
    public List<CSKHCustomerAccountItem> layTaiKhoanTheoKhachHang(String maKhachHang) throws SQLException {
        List<CSKHCustomerAccountItem> danhSach = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_TAI_KHOAN_KHACH_HANG)) {
            statement.setString(1, maKhachHang);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    danhSach.add(new CSKHCustomerAccountItem(
                            resultSet.getString("SoTaiKhoan"),
                            resultSet.getBigDecimal("SoDu"),
                            resultSet.getString("TrangThai"),
                            resultSet.getString("TenChiNhanh")
                    ));
                }
            }
        }
        return danhSach;
    }
}