package org.example.appnganhang.model.dao.Impl;

import org.example.appnganhang.model.dao.intf.KhachHangPortalDAO;
import org.example.appnganhang.model.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

/** Tập trung toàn bộ SQL truy vấn dữ liệu màn hình khách hàng tại tầng DAO. */
public class KhachHangPortalDAOImpl implements KhachHangPortalDAO {
    private Map<String,Object> row(ResultSet rs, String... columns) throws SQLException {
        Map<String,Object> data = new LinkedHashMap<>();
        for (String column : columns) data.put(column, rs.getObject(column));
        return data;
    }
    private List<Map<String,Object>> query(String sql, Binder binder, String... columns) {
        List<Map<String,Object>> result = new ArrayList<>();
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            binder.bind(ps); try (ResultSet rs = ps.executeQuery()) { while (rs.next()) result.add(row(rs, columns)); }
            return result;
        } catch (SQLException e) { throw new RuntimeException("Lỗi truy vấn dữ liệu khách hàng: " + e.getMessage(), e); }
    }
    @FunctionalInterface private interface Binder { void bind(PreparedStatement ps) throws SQLException; }
    @Override public Optional<Map<String,Object>> timHoSo(String ma, String ten) {
        List<Map<String,Object>> rows = query("SELECT TOP 1 MaKhachHang, HoTen, SoCCCD, SoDienThoai, Email, DiaChi, TenDangNhap FROM KhachHang WHERE MaKhachHang = ? OR TenDangNhap = ?", ps -> { ps.setString(1, ma == null ? "" : ma); ps.setString(2, ten == null ? "" : ten); }, "MaKhachHang","HoTen","SoCCCD","SoDienThoai","Email","DiaChi","TenDangNhap");
        return rows.stream().findFirst();
    }
    @Override public Optional<Map<String,Object>> timHoSoCoTaiKhoanDauTien() {
        List<Map<String,Object>> rows = query("SELECT TOP 1 kh.MaKhachHang, kh.HoTen, kh.SoCCCD, kh.SoDienThoai, kh.Email, kh.DiaChi, kh.TenDangNhap FROM KhachHang kh JOIN TaiKhoanNganHang tk ON kh.MaKhachHang = tk.MaKhachHang ORDER BY kh.MaKhachHang", ps -> {}, "MaKhachHang","HoTen","SoCCCD","SoDienThoai","Email","DiaChi","TenDangNhap");
        return rows.stream().findFirst();
    }
    @Override public List<Map<String,Object>> layTaiKhoanKhachHang(String ma, String ten) {
        return query("SELECT tk.MaTaiKhoan, tk.SoTaiKhoan, tk.SoDu, tk.TrangThai, ISNULL(nh.TenNganHang, 'SMART BANK') AS TenNganHang, ISNULL(cn.TenChiNhanh, N'Hội sở SMART BANK') AS TenChiNhanh, ISNULL(hm.TenHanMuc, N'Mặc định') AS TenHanMuc, ISNULL(hm.HanMucNgay, 50000000) AS HanMucNgay FROM TaiKhoanNganHang tk LEFT JOIN NganHang nh ON tk.MaNganHang = nh.MaNganHang LEFT JOIN ChiNhanh cn ON tk.MaChiNhanh = cn.MaChiNhanh LEFT JOIN [HanMucGiaoDịch] hm ON tk.MaHanMuc = hm.MaHanMuc WHERE tk.MaKhachHang = ? OR tk.MaKhachHang IN (SELECT MaKhachHang FROM KhachHang WHERE TenDangNhap = ?)", ps -> { ps.setString(1, ma == null ? "" : ma); ps.setString(2, ten == null ? "" : ten); }, "MaTaiKhoan","SoTaiKhoan","SoDu","TrangThai","TenNganHang","TenChiNhanh","TenHanMuc","HanMucNgay");
    }
    @Override public List<String> laySoTaiKhoanKhachHang(String ma, String ten) {
        List<Map<String,Object>> rows = query("SELECT SoTaiKhoan FROM TaiKhoanNganHang WHERE MaKhachHang = ? OR MaKhachHang IN (SELECT MaKhachHang FROM KhachHang WHERE TenDangNhap = ?) ORDER BY SoTaiKhoan", ps -> { ps.setString(1, ma == null ? "" : ma); ps.setString(2, ten == null ? "" : ten); }, "SoTaiKhoan");
        List<String> result = new ArrayList<>(); for (Map<String,Object> r : rows) if (r.get("SoTaiKhoan") != null) result.add(r.get("SoTaiKhoan").toString()); return result;
    }
    @Override public BigDecimal laySoDu(String stk) {
        List<Map<String,Object>> rows = query("SELECT SoDu FROM TaiKhoanNganHang WHERE SoTaiKhoan = ?", ps -> ps.setString(1, stk), "SoDu");
        if (rows.isEmpty() || rows.get(0).get("SoDu") == null) return null; Object v=rows.get(0).get("SoDu"); return v instanceof BigDecimal ? (BigDecimal)v : new BigDecimal(v.toString());
    }
    @Override public List<Map<String,Object>> layBienDongSoDu(String stk) {
        return query("SELECT MaLog, MaGiaoDich, SoTaiKhoanLienQuan, HanhDong, SoTienThayDoi, SoDuSauThayDoi, ThoiGianGhiLog FROM LichSuGiaoDich WHERE SoTaiKhoanLienQuan = ? ORDER BY ThoiGianGhiLog DESC", ps -> ps.setString(1, stk), "MaLog","MaGiaoDich","SoTaiKhoanLienQuan","HanhDong","SoTienThayDoi","SoDuSauThayDoi","ThoiGianGhiLog");
    }
    @Override public List<Map<String,Object>> layGiaoDich(String stk) {
        return query("SELECT gd.MaGiaoDich, gd.MaTaiKhoanNguon, gd.MaTaiKhoanDich, gd.SoTien, gd.NgayGiaoDich, gd.NoiDung, gd.MaLoaiGD, gd.TrangThai, ISNULL(lg.TenLoaiGD, N'Chuyển khoản') AS TenLoaiGD FROM GiaoDichChuyenTien gd LEFT JOIN LoaiGiaoDich lg ON gd.MaLoaiGD = lg.MaLoaiGD WHERE gd.MaTaiKhoanNguon = ? OR gd.MaTaiKhoanDich = ? ORDER BY gd.NgayGiaoDich DESC", ps -> { ps.setString(1, stk); ps.setString(2, stk); }, "MaGiaoDich","MaTaiKhoanNguon","MaTaiKhoanDich","SoTien","NgayGiaoDich","NoiDung","MaLoaiGD","TrangThai","TenLoaiGD");
    }
}