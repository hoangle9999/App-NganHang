package org.example.appnganhang.model.dao.Impl;

import org.example.appnganhang.model.dao.intf.TellerAccountDAO;
import org.example.appnganhang.model.entity.ChiNhanh;
import org.example.appnganhang.model.entity.HanMucGiaoDich;
import org.example.appnganhang.model.entity.KhachHang;
import org.example.appnganhang.model.entity.TaiKhoanNganHang;
import org.example.appnganhang.model.entity.TellerAccountInfo;
import org.example.appnganhang.model.util.DBConnection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TellerAccountDAOImpl implements TellerAccountDAO {
    @Override
    public List<TellerAccountInfo> layDanhSachTaiKhoan() {
        List<TellerAccountInfo> accounts = new ArrayList<>();
        String sql = "SELECT * FROM dbo.vw_GDV_DanhSachTaiKhoan ORDER BY MaTaiKhoan DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                TellerAccountInfo a = new TellerAccountInfo();
                a.setMaTaiKhoan(rs.getString("MaTaiKhoan"));
                a.setSoTaiKhoan(rs.getString("SoTaiKhoan"));
                a.setTenKhachHang(rs.getString("HoTen"));
                a.setSoCCCD(rs.getString("SoCCCD"));
                a.setSoDienThoai(rs.getString("SoDienThoai"));
                a.setEmail(rs.getString("Email"));
                a.setDiaChi(rs.getString("DiaChi"));
                a.setSoDu(rs.getBigDecimal("SoDu"));
                a.setTenNganHang(rs.getString("TenNganHang"));
                a.setTenChiNhanh(rs.getString("TenChiNhanh"));
                a.setTenHanMuc(rs.getString("TenHanMuc"));
                a.setTrangThai(rs.getString("TrangThai"));
                accounts.add(a);
            }
            return accounts;
        } catch (SQLException e) { throw new RuntimeException("Không thể tải danh sách tài khoản: " + e.getMessage(), e); }
    }

    @Override
    public List<ChiNhanh> layDanhSachChiNhanh() {
        List<ChiNhanh> branches = new ArrayList<>();
        String sql = "SELECT MaChiNhanh, TenChiNhanh, DiaChi FROM dbo.ChiNhanh ORDER BY MaChiNhanh";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) branches.add(new ChiNhanh(rs.getString("MaChiNhanh"), rs.getString("TenChiNhanh"), rs.getString("DiaChi")));
            return branches;
        } catch (SQLException e) { throw new RuntimeException("Không thể tải chi nhánh: " + e.getMessage(), e); }
    }

    @Override
    public List<HanMucGiaoDich> layDanhSachHanMuc() {
        List<HanMucGiaoDich> limits = new ArrayList<>();
        String sql = "SELECT MaHanMuc, TenHanMuc, HanMucNgay FROM dbo.[HanMucGiaoDịch] ORDER BY HanMucNgay";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) limits.add(new HanMucGiaoDich(rs.getString("MaHanMuc"), rs.getString("TenHanMuc"), rs.getBigDecimal("HanMucNgay")));
            return limits;
        } catch (SQLException e) { throw new RuntimeException("Không thể tải hạn mức: " + e.getMessage(), e); }
    }

    @Override
    public Optional<KhachHang> timKhachHangTheoCCCD(String soCCCD) {
        String sql = "SELECT MaKhachHang, HoTen, SoCCCD, SoDienThoai, Email, DiaChi, TenDangNhap FROM dbo.KhachHang WHERE SoCCCD = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, soCCCD);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                KhachHang customer = new KhachHang();
                customer.setMaKhachHang(rs.getString("MaKhachHang"));
                customer.setHoTen(rs.getString("HoTen"));
                customer.setSoCCCD(rs.getString("SoCCCD"));
                customer.setSoDienThoai(rs.getString("SoDienThoai"));
                customer.setEmail(rs.getString("Email"));
                customer.setDiaChi(rs.getString("DiaChi"));
                customer.setTenDangNhap(rs.getString("TenDangNhap"));
                return Optional.of(customer);
            }
        } catch (SQLException e) { throw new RuntimeException("Không thể tra cứu CCCD: " + e.getMessage(), e); }
    }

    @Override
    public String taoHoSoKhachHang(String tenDangNhap, String matKhau, String hoTen, String soCCCD,
                                   String soDienThoai, String email, String diaChi) {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String maKhachHang;
                try (CallableStatement code = conn.prepareCall("{call dbo.sp_TaoMaKhachHang(?)}")) {
                    code.registerOutParameter(1, Types.VARCHAR);
                    code.execute();
                    maKhachHang = code.getString(1);
                }
                try (PreparedStatement login = conn.prepareStatement(
                        "INSERT dbo.TaiKhoanDangNhap_VaiTro(TenDangNhap, MatKhau, RoleName, MaNguoiDung) VALUES (?, ?, N'Khách hàng', ?)")) {
                    login.setString(1, tenDangNhap);
                    login.setString(2, matKhau);
                    login.setString(3, maKhachHang);
                    login.executeUpdate();
                }
                try (PreparedStatement customer = conn.prepareStatement(
                        "INSERT dbo.KhachHang(MaKhachHang, HoTen, SoCCCD, SoDienThoai, Email, DiaChi, TenDangNhap) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                    customer.setString(1, maKhachHang);
                    customer.setString(2, hoTen);
                    customer.setString(3, soCCCD);
                    customer.setString(4, soDienThoai);
                    customer.setString(5, email);
                    customer.setString(6, diaChi);
                    customer.setString(7, tenDangNhap);
                    customer.executeUpdate();
                }
                conn.commit();
                return maKhachHang;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally { conn.setAutoCommit(true); }
        } catch (SQLException e) { throw new RuntimeException("Không thể tạo hồ sơ khách hàng: " + e.getMessage(), e); }
    }

    @Override
    public String taoMaTaiKhoan() {
        try (Connection conn = DBConnection.getConnection(); CallableStatement cs = conn.prepareCall("{call dbo.sp_TaoMaTaiKhoan(?)}")) {
            cs.registerOutParameter(1, Types.VARCHAR);
            cs.execute();
            return cs.getString(1);
        } catch (SQLException e) { throw new RuntimeException("Không thể tạo mã tài khoản: " + e.getMessage(), e); }
    }

    @Override
    public boolean moTaiKhoan(TaiKhoanNganHang account) {
        String sql = "INSERT dbo.TaiKhoanNganHang(MaTaiKhoan, MaKhachHang, SoTaiKhoan, SoDu, TrangThai, MaChiNhanh, MaHanMuc, MaNganHang) VALUES (?, ?, ?, ?, ?, ?, ?, 'SMART')";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, account.getMaTaiKhoan());
            ps.setString(2, account.getMaKhachHang());
            ps.setString(3, account.getSoTaiKhoan());
            ps.setBigDecimal(4, account.getSoDu());
            ps.setString(5, account.getTrangThai());
            ps.setString(6, account.getMaChiNhanh());
            ps.setString(7, account.getMaHanMuc());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) { throw new RuntimeException("Không thể mở tài khoản: " + e.getMessage(), e); }
    }

    @Override
    public void doiTrangThai(String maTaiKhoan, String trangThai) {
        try (Connection conn = DBConnection.getConnection(); CallableStatement cs = conn.prepareCall("{call dbo.SP_GDV_DoiTrangThaiTaiKhoan(?, ?)}")) {
            cs.setString(1, maTaiKhoan);
            cs.setNString(2, trangThai);
            cs.execute();
        } catch (SQLException e) { throw new RuntimeException("Không thể đổi trạng thái tài khoản: " + e.getMessage(), e); }
    }

    @Override
    public void ganHanMuc(String maTaiKhoan, String maHanMuc) {
        try (Connection conn = DBConnection.getConnection(); CallableStatement cs = conn.prepareCall("{call dbo.SP_GDV_GanHanMucTaiKhoan(?, ?)}")) {
            cs.setString(1, maTaiKhoan);
            cs.setString(2, maHanMuc);
            cs.execute();
        } catch (SQLException e) { throw new RuntimeException("Không thể gán hạn mức: " + e.getMessage(), e); }
    }
}
