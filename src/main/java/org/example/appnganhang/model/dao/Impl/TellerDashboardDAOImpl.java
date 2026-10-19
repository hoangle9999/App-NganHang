package org.example.appnganhang.model.dao.Impl;

import org.example.appnganhang.model.dao.intf.TellerDashboardDAO;
import org.example.appnganhang.model.entity.GiaoDichChuyenTien;
import org.example.appnganhang.model.entity.NhanVien;
import org.example.appnganhang.model.entity.TellerDashboardStats;
import org.example.appnganhang.model.util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TellerDashboardDAOImpl implements TellerDashboardDAO {
    @Override
    public Optional<NhanVien> timNhanVienTheoTenDangNhap(String tenDangNhap) {
        String sql = """
                SELECT nv.MaNhanVien, nv.HoTen, nv.TenDangNhap, nv.MaChiNhanh, cn.TenChiNhanh
                FROM dbo.NhanVien nv
                LEFT JOIN dbo.ChiNhanh cn ON cn.MaChiNhanh = nv.MaChiNhanh
                WHERE nv.TenDangNhap = ?
                """;
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenDangNhap);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                NhanVien nv = new NhanVien();
                nv.setMaNhanVien(rs.getString("MaNhanVien"));
                nv.setHoTen(rs.getString("HoTen"));
                nv.setTenDangNhap(rs.getString("TenDangNhap"));
                nv.setMaChiNhanh(rs.getString("MaChiNhanh"));
                nv.setTenChiNhanh(rs.getString("TenChiNhanh"));
                return Optional.of(nv);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Không thể tải thông tin giao dịch viên: " + e.getMessage(), e);
        }
    }

    @Override public TellerDashboardStats layThongKeDashboard() {
        String sql = "{call dbo.sp_Teller_Dashboard}";
        try (Connection conn = DBConnection.getConnection(); CallableStatement cs = conn.prepareCall(sql); ResultSet rs = cs.executeQuery()) {
            if (!rs.next()) return new TellerDashboardStats();
            TellerDashboardStats stats = new TellerDashboardStats();
            stats.setSoGiaoDichHomNay(rs.getInt("SoGiaoDichHomNay"));
            stats.setTongTienHomNay(rs.getBigDecimal("TongTienHomNay"));
            stats.setSoGiaoDichChoXuLy(rs.getInt("SoGiaoDichChoXuLy"));
            stats.setSoTaiKhoanKhachHang(rs.getInt("SoTaiKhoanKhachHang"));
            return stats;
        } catch (SQLException e) {
            throw new RuntimeException("Không thể tải số liệu dashboard: " + e.getMessage(), e);
        }
    }

    @Override
    public List<GiaoDichChuyenTien> layGiaoDichGanDay(int gioiHan) {
        String sql = """
                SELECT TOP (?) MaGiaoDich, MaTaiKhoanNguon AS TaiKhoanNguon, MaTaiKhoanDich AS TaiKhoanDich,
                       SoTien, NgayGiaoDich, MaLoaiGD AS TenLoaiGD, TrangThai
                FROM dbo.vw_Teller_GiaoDichGanDay
                ORDER BY NgayGiaoDich DESC
                """;
        List<GiaoDichChuyenTien> rows = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Math.max(1, gioiHan));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    GiaoDichChuyenTien gd = new GiaoDichChuyenTien();
                    gd.setMaGiaoDich(rs.getString("MaGiaoDich"));
                    gd.setMaTaiKhoanNguon(rs.getString("TaiKhoanNguon"));
                    gd.setMaTaiKhoanDich(rs.getString("TaiKhoanDich"));
                    gd.setSoTien(rs.getBigDecimal("SoTien"));
                    Timestamp at = rs.getTimestamp("NgayGiaoDich");
                    if (at != null) gd.setNgayGiaoDich(at.toLocalDateTime());
                    gd.setMaLoaiGD(rs.getString("TenLoaiGD"));
                    gd.setTrangThai(rs.getString("TrangThai"));
                    rows.add(gd);
                }
            }
            return rows;
        } catch (SQLException e) { throw new RuntimeException("Không thể tải giao dịch gần đây: " + e.getMessage(), e); }
    }
}
