package org.example.appnganhang.model.dao.Impl;

import org.example.appnganhang.model.dao.intf.KhachHangPortalDAO;
import org.example.appnganhang.model.util.DBConnection;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Read-only database access for the customer portal. */
public class KhachHangPortalDAOImpl implements KhachHangPortalDAO {

    @FunctionalInterface
    private interface Binder {
        void bind(CallableStatement statement) throws SQLException;
    }

    private List<Map<String, Object>> query(String procedure, Binder binder, String... columns) {
        List<Map<String, Object>> rows = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             CallableStatement statement = connection.prepareCall(procedure)) {
            binder.bind(statement);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (String column : columns) row.put(column, resultSet.getObject(column));
                    rows.add(row);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Customer data query failed: " + e.getMessage(), e);
        }
        return rows;
    }

    @Override
    public Optional<Map<String, Object>> timHoSo(String maKhachHang, String tenDangNhap) {
        return query("{call dbo.sp_KH_TimHoSo(?, ?)}", statement -> {
            statement.setString(1, maKhachHang);
            statement.setString(2, tenDangNhap);
        }, "MaKhachHang", "HoTen", "SoCCCD", "SoDienThoai", "Email", "DiaChi", "TenDangNhap")
                .stream().findFirst();
    }

    @Override
    public List<Map<String, Object>> layTaiKhoanKhachHang(String maKhachHang, String tenDangNhap) {
        return query("{call dbo.sp_KH_DanhSachTaiKhoan(?, ?)}", statement -> {
            statement.setString(1, maKhachHang);
            statement.setString(2, tenDangNhap);
        }, "MaTaiKhoan", "SoTaiKhoan", "SoDu", "TrangThai", "TenNganHang",
                "TenChiNhanh", "TenHanMuc", "HanMucNgay");
    }

    @Override
    public List<String> laySoTaiKhoanKhachHang(String maKhachHang, String tenDangNhap) {
        List<String> accounts = new ArrayList<>();
        for (Map<String, Object> row : query("{call dbo.sp_KH_DanhSachSoTaiKhoan(?, ?)}", statement -> {
            statement.setString(1, maKhachHang);
            statement.setString(2, tenDangNhap);
        }, "SoTaiKhoan")) {
            Object value = row.get("SoTaiKhoan");
            if (value != null) accounts.add(value.toString());
        }
        return accounts;
    }

    @Override
    public BigDecimal laySoDu(String soTaiKhoan, String maKhachHang, String tenDangNhap) {
        List<Map<String, Object>> rows = query("{call dbo.sp_KH_SoDuTaiKhoan(?, ?, ?)}", statement -> {
            statement.setString(1, soTaiKhoan);
            statement.setString(2, maKhachHang);
            statement.setString(3, tenDangNhap);
        }, "SoDu");
        if (rows.isEmpty() || rows.get(0).get("SoDu") == null) return null;
        Object value = rows.get(0).get("SoDu");
        return value instanceof BigDecimal ? (BigDecimal) value : new BigDecimal(value.toString());
    }

    @Override
    public List<Map<String, Object>> layBienDongSoDu(String soTaiKhoan, String maKhachHang, String tenDangNhap) {
        return query("{call dbo.sp_KH_LichSuBienDong(?, ?, ?)}", statement -> {
            statement.setString(1, soTaiKhoan);
            statement.setString(2, maKhachHang);
            statement.setString(3, tenDangNhap);
        }, "MaLog", "MaGiaoDich", "SoTaiKhoanLienQuan", "HanhDong",
                "SoTienThayDoi", "SoDuSauThayDoi", "ThoiGianGhiLog");
    }

    @Override
    public List<Map<String, Object>> layGiaoDich(String soTaiKhoan, String maKhachHang, String tenDangNhap) {
        return query("{call dbo.sp_KH_GiaoDichTaiKhoan(?, ?, ?)}", statement -> {
            statement.setString(1, soTaiKhoan);
            statement.setString(2, maKhachHang);
            statement.setString(3, tenDangNhap);
        }, "MaGiaoDich", "MaTaiKhoanNguon", "MaTaiKhoanDich", "SoTien", "NgayGiaoDich",
                "NoiDung", "MaLoaiGD", "TrangThai", "TenLoaiGD");
    }
}
