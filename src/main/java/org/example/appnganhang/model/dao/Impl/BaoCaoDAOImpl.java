package org.example.appnganhang.model.dao.Impl;

import org.example.appnganhang.model.dao.intf.BaoCaoDAO;
import org.example.appnganhang.model.entity.GiaoDichChuyenTien;
import org.example.appnganhang.model.entity.LichSuGiaoDich;
import org.example.appnganhang.model.util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BaoCaoDAOImpl implements BaoCaoDAO {
    @Override
    public List<GiaoDichChuyenTien> layDanhSachGiaoDich() {
        String sql = """
                SELECT gd.MaGiaoDich, gd.MaTaiKhoanNguon, gd.MaTaiKhoanDich, gd.SoTien,
                       gd.NgayGiaoDich, gd.NoiDung, lgd.TenLoaiGD, gd.TrangThai,
                       nh.TenNganHang AS TenNganHangDich
                FROM dbo.GiaoDichChuyenTien gd
                LEFT JOIN dbo.LoaiGiaoDich lgd ON lgd.MaLoaiGD=gd.MaLoaiGD
                LEFT JOIN dbo.NganHang nh ON nh.MaNganHang=gd.MaNganHangDich
                ORDER BY gd.NgayGiaoDich DESC
                """;
        List<GiaoDichChuyenTien> rows = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                GiaoDichChuyenTien gd = new GiaoDichChuyenTien();
                gd.setMaGiaoDich(rs.getString("MaGiaoDich"));
                gd.setMaTaiKhoanNguon(rs.getString("MaTaiKhoanNguon"));
                gd.setMaTaiKhoanDich(rs.getString("MaTaiKhoanDich"));
                gd.setSoTien(rs.getBigDecimal("SoTien"));
                Timestamp ngay = rs.getTimestamp("NgayGiaoDich");
                if (ngay != null) gd.setNgayGiaoDich(ngay.toLocalDateTime());
                gd.setNoiDung(rs.getString("NoiDung"));
                gd.setMaLoaiGD(rs.getString("TenLoaiGD"));
                gd.setTrangThai(rs.getString("TrangThai"));
                gd.setTenNganHangDich(rs.getString("TenNganHangDich"));
                rows.add(gd);
            }
            return rows;
        } catch (SQLException e) {
            throw new RuntimeException("Không thể tải danh sách giao dịch: " + e.getMessage(), e);
        }
    }

    @Override
    public List<LichSuGiaoDich> layDanhSachLichSuGiaoDich() {
        String sql = """
                SELECT MaLog, MaGiaoDich, SoTaiKhoanLienQuan, HanhDong,
                       SoTienThayDoi, SoDuSauThayDoi, ThoiGianGhiLog
                FROM dbo.LichSuGiaoDich
                ORDER BY ThoiGianGhiLog DESC
                """;
        List<LichSuGiaoDich> rows = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                LichSuGiaoDich log = new LichSuGiaoDich();
                log.setMaLog(rs.getInt("MaLog")); log.setMaGiaoDich(rs.getString("MaGiaoDich"));
                log.setSoTaiKhoanLienQuan(rs.getString("SoTaiKhoanLienQuan")); log.setHanhDong(rs.getString("HanhDong"));
                log.setSoTienThayDoi(rs.getBigDecimal("SoTienThayDoi")); log.setSoDuSauThayDoi(rs.getBigDecimal("SoDuSauThayDoi"));
                Timestamp time = rs.getTimestamp("ThoiGianGhiLog");
                if (time != null) log.setThoiGianGhiLog(time.toLocalDateTime());
                rows.add(log);
            }
            return rows;
        } catch (SQLException e) {
            throw new RuntimeException("Không thể tải lịch sử kiểm toán: " + e.getMessage(), e);
        }
    }

    @Override
    public List<String> layDanhSachLoaiGiaoDich() {
        return layDanhSachChuoi("SELECT TenLoaiGD FROM dbo.LoaiGiaoDich ORDER BY TenLoaiGD", "TenLoaiGD");
    }

    @Override
    public List<String> layDanhSachNganHang() {
        return layDanhSachChuoi("SELECT TenNganHang FROM dbo.NganHang ORDER BY TenNganHang", "TenNganHang");
    }

    private List<String> layDanhSachChuoi(String sql, String column) {
        List<String> rows = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) rows.add(rs.getString(column));
            return rows;
        } catch (SQLException e) {
            throw new RuntimeException("Không thể tải danh sách bộ lọc báo cáo: " + e.getMessage(), e);
        }
    }
}
