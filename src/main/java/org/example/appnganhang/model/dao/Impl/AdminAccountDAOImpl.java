package org.example.appnganhang.model.dao.Impl;

import org.example.appnganhang.controller.admin.AdminAccountController;
import org.example.appnganhang.model.dao.intf.AdminAccountDAO;
import org.example.appnganhang.model.entity.ChiNhanh;
import org.example.appnganhang.model.entity.TaiKhoanDangNhap_VaiTro;
import org.example.appnganhang.model.entity.HanMucGiaoDich;
import org.example.appnganhang.model.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminAccountDAOImpl implements AdminAccountDAO {
    @Override
    public List<HanMucGiaoDich> layDanhSachHanMuc() {
        String sql = "SELECT MaHanMuc, TenHanMuc, HanMucNgay FROM dbo.HanMucGiaoDịch ORDER BY MaHanMuc";
        List<HanMucGiaoDich> result = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement st = c.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) result.add(new HanMucGiaoDich(
                    rs.getString("MaHanMuc"), rs.getString("TenHanMuc"), rs.getBigDecimal("HanMucNgay")));
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Không thể tải danh sách hạn mức: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ChiNhanh> layDanhSachChiNhanh() {
        // Giả định bảng dbo.ChiNhanh có hai cột MaChiNhanh và TenChiNhanh.
        String sql = "SELECT MaChiNhanh, TenChiNhanh FROM dbo.ChiNhanh ORDER BY TenChiNhanh";
        List<ChiNhanh> result = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement st = c.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) result.add(new ChiNhanh(rs.getString("MaChiNhanh"), rs.getString("TenChiNhanh")));
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Không thể tải danh sách chi nhánh: " + e.getMessage(), e);
        }
    }

    @Override
    public List<TaiKhoanDangNhap_VaiTro> layDanhSachTaiKhoan() {
        String sql = "{CALL dbo.SP_Admin_DanhSachTaiKhoanDangNhap}";
        List<TaiKhoanDangNhap_VaiTro> result = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             CallableStatement st = c.prepareCall(sql);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                TaiKhoanDangNhap_VaiTro tk = new TaiKhoanDangNhap_VaiTro();
                tk.setTenDangNhap(rs.getString("TenDangNhap"));
                // Không cần đọc mật khẩu để hiển thị trong bảng.
                tk.setRoleName(rs.getString("RoleName"));
                tk.setMaNguoiDung(rs.getString("MaNguoiDung"));
                tk.setQuyenHoatDong(rs.getBoolean("QuyenHoatDong"));
                result.add(tk);
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Không thể tải danh sách tài khoản: " + e.getMessage(), e);
        }
    }

    @Override
    public String taoTaiKhoan(String tenDangNhap, String matKhau, String roleName,
                              String hoTen, String soCCCD, String soDienThoai,
                              String email, String diaChi, String maChiNhanh, String maHanMuc) {
        String sql = "{CALL dbo.SP_Admin_TaoTaiKhoanHeThong(?,?,?,?,?,?,?,?,?,?)}";
        try (Connection c = DBConnection.getConnection(); CallableStatement st = c.prepareCall(sql)) {
            st.setString(1, tenDangNhap);
            st.setString(2, matKhau);
            st.setNString(3, roleName);
            st.setNString(4, hoTen);
            if (soCCCD == null || soCCCD.isBlank()) st.setNull(5, Types.VARCHAR); else st.setString(5, soCCCD);
            st.setString(6, soDienThoai);
            if (email == null || email.isBlank()) st.setNull(7, Types.VARCHAR); else st.setString(7, email);
            if (diaChi == null || diaChi.isBlank()) st.setNull(8, Types.NVARCHAR); else st.setNString(8, diaChi);
            st.setString(9, maChiNhanh);
            st.setString(10, (maHanMuc == null || maHanMuc.isBlank()) ? "HM001" : maHanMuc);
            boolean hasResult = st.execute();
            while (!hasResult && st.getUpdateCount() != -1) hasResult = st.getMoreResults();
            if (hasResult) try (ResultSet rs = st.getResultSet()) {
                if (rs.next()) return rs.getString("MaNguoiDungMoi");
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(layThongBaoSql(e), e);
        }
    }

    @Override
    public void doiVaiTro(String tenDangNhap, String roleNameMoi) {
        goiProcHaiThamSo("{CALL dbo.SP_Admin_DoiVaiTroTaiKhoan(?,?)}", tenDangNhap, roleNameMoi);
    }

    @Override
    public void datQuyenHoatDong(String tenDangNhap, boolean choPhep) {
        try (Connection c = DBConnection.getConnection();
             CallableStatement st = c.prepareCall("{CALL dbo.SP_Admin_DatTrangThaiQuyen(?,?)}")) {
            st.setString(1, tenDangNhap);
            st.setBoolean(2, choPhep);
            st.execute();
        } catch (SQLException e) {
            throw new RuntimeException(layThongBaoSql(e), e);
        }
    }

    @Override
    public void datLaiMatKhau(String tenDangNhap, String matKhauMoi) {
        goiProcHaiThamSo("{CALL dbo.SP_Admin_DatLaiMatKhau(?,?)}", tenDangNhap, matKhauMoi);
    }

    @Override
    public void xoaTaiKhoan(String tenDangNhap) {
        try (Connection c = DBConnection.getConnection(); CallableStatement st = c.prepareCall("{CALL dbo.SP_Admin_XoaTaiKhoanDangNhap(?)}")) {
            st.setString(1, tenDangNhap);
            st.execute();
        } catch (SQLException e) {
            throw new RuntimeException(layThongBaoSql(e), e);
        }
    }

    private void goiProcHaiThamSo(String sql, String a, String b) {
        try (Connection c = DBConnection.getConnection(); CallableStatement st = c.prepareCall(sql)) {
            st.setString(1, a);
            if (sql.contains("DoiVaiTro")) st.setNString(2, b); else st.setString(2, b);
            st.execute();
        } catch (SQLException e) {
            throw new RuntimeException(layThongBaoSql(e), e);
        }
    }

    private String layThongBaoSql(SQLException e) {
        String message = e.getMessage();
        return message == null ? "Lỗi SQL Server." : message;
    }
}

