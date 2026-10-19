package org.example.appnganhang.model.dao.Impl;

import org.example.appnganhang.model.dao.intf.CSKHCustomerDAO;
import org.example.appnganhang.model.entity.CSKHCustomerAccountItem;
import org.example.appnganhang.model.entity.KhachHang;
import org.example.appnganhang.model.util.DBConnection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CSKHCustomerDAOImpl implements CSKHCustomerDAO {

    @Override
    public List<KhachHang> layDanhSachKhachHang() throws SQLException {
        List<KhachHang> danhSach = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             CallableStatement statement = connection.prepareCall("{call dbo.sp_CSKH_DanhSachKhachHang}");
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
             CallableStatement statement = connection.prepareCall("{call dbo.sp_CSKH_TonTaiCCCD(?)}")) {
            statement.setString(1, soCCCD);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() && resultSet.getBoolean("TonTai");
            }
        }
    }

    @Override
    public boolean tonTaiSoDienThoai(String soDienThoai, String maKhachHangBoQua) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             CallableStatement statement = connection.prepareCall("{call dbo.sp_CSKH_TonTaiSoDienThoai(?, ?)}")) {
            statement.setString(1, soDienThoai);
            statement.setString(2, maKhachHangBoQua);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() && resultSet.getBoolean("TonTai");
            }
        }
    }

    @Override
    public void themKhachHang(KhachHang khachHang, String matKhau) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             CallableStatement statement = connection.prepareCall("{call dbo.sp_CSKH_ThemKhachHang(?, ?, ?, ?, ?, ?, ?)}")) {
            statement.setString(1, khachHang.getHoTen());
            statement.setString(2, khachHang.getSoCCCD());
            statement.setString(3, khachHang.getSoDienThoai());
            statement.setString(4, khachHang.getEmail());
            statement.setString(5, khachHang.getDiaChi());
            statement.setString(6, khachHang.getTenDangNhap());
            statement.setString(7, matKhau);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    khachHang.setMaKhachHang(resultSet.getString("MaKhachHang"));
                }
            }
        }
    }

    @Override
    public void capNhatKhachHang(KhachHang khachHang) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             CallableStatement statement = connection.prepareCall("{call dbo.sp_CSKH_CapNhatKhachHang(?, ?, ?, ?, ?)}")) {
            statement.setString(1, khachHang.getMaKhachHang());
            statement.setString(2, khachHang.getHoTen());
            statement.setString(3, khachHang.getSoDienThoai());
            statement.setString(4, khachHang.getEmail());
            statement.setString(5, khachHang.getDiaChi());
            statement.executeUpdate();
        }
    }

    @Override
    public List<CSKHCustomerAccountItem> layTaiKhoanTheoKhachHang(String maKhachHang) throws SQLException {
        List<CSKHCustomerAccountItem> danhSach = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             CallableStatement statement = connection.prepareCall("{call dbo.sp_CSKH_TaiKhoanTheoKhachHang(?)}")) {
            statement.setString(1, maKhachHang);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    danhSach.add(new CSKHCustomerAccountItem(
                            resultSet.getString("SoTaiKhoan"),
                            resultSet.getBigDecimal("SoDu"),
                            resultSet.getString("TrangThai"),
                            resultSet.getString("TenChiNhanh")));
                }
            }
        }
        return danhSach;
    }
}
