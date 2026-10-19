package org.example.appnganhang.model.dao.Impl;

import org.example.appnganhang.model.dao.intf.AdminEmployBranchDAO;
import org.example.appnganhang.model.entity.ChiNhanh;
import org.example.appnganhang.model.entity.NhanVien;
import org.example.appnganhang.model.util.DBConnection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class AdminEmployBranchDAOImpl implements AdminEmployBranchDAO {
    @Override
    public List<ChiNhanh> layDanhSachChiNhanh() {
        List<ChiNhanh> result = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             CallableStatement st = c.prepareCall("{CALL dbo.SP_Admin_DanhSachChiNhanh}");
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) result.add(new ChiNhanh(rs.getString("MaChiNhanh"),
                    rs.getString("TenChiNhanh"), rs.getString("DiaChi")));
            return result;
        } catch (SQLException e) { throw loiSql(e); }
    }

    @Override
    public List<NhanVien> layDanhSachNhanVien() {
        List<NhanVien> result = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             CallableStatement st = c.prepareCall("{CALL dbo.SP_Admin_DanhSachNhanVien}");
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                NhanVien nv = new NhanVien();
                nv.setMaNhanVien(rs.getString("MaNhanVien")); nv.setHoTen(rs.getString("HoTen"));
                nv.setSoDienThoai(rs.getString("SoDienThoai")); nv.setDiaChi(rs.getString("DiaChi"));
                nv.setChucVu(rs.getString("ChucVu")); nv.setMaChiNhanh(rs.getString("MaChiNhanh"));
                nv.setTenChiNhanh(rs.getString("TenChiNhanh")); nv.setTenDangNhap(rs.getString("TenDangNhap"));
                result.add(nv);
            }
            return result;
        } catch (SQLException e) { throw loiSql(e); }
    }

    @Override
    public List<String> layTaiKhoanNhanVienChuaGan(String maNhanVien) {
        List<String> result = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             CallableStatement st = c.prepareCall("{CALL dbo.SP_Admin_TaiKhoanNhanVienChuaGan(?)}")) {
            st.setString(1, maNhanVien);
            try (ResultSet rs = st.executeQuery()) { while (rs.next()) result.add(rs.getString("TenDangNhap")); }
            return result;
        } catch (SQLException e) { throw loiSql(e); }
    }

    @Override
    public String themNhanVien(NhanVien nv) {
        try (Connection c = DBConnection.getConnection();
             CallableStatement st = c.prepareCall("{CALL dbo.SP_Admin_TaoNhanVien(?,?,?,?,?,?)}")) {
            st.setNString(1, nv.getHoTen()); st.setString(2, nv.getSoDienThoai());
            if (nv.getDiaChi() == null || nv.getDiaChi().isBlank()) st.setNull(3, Types.NVARCHAR); else st.setNString(3, nv.getDiaChi());
            st.setNString(4, nv.getChucVu()); st.setString(5, nv.getMaChiNhanh()); st.registerOutParameter(6, Types.VARCHAR);
            st.execute(); return st.getString(6);
        } catch (SQLException e) { throw loiSql(e); }
    }

    @Override
    public void capNhatNhanVien(NhanVien nv) {
        try (Connection c = DBConnection.getConnection();
             CallableStatement st = c.prepareCall("{CALL dbo.SP_Admin_CapNhatNhanVien(?,?,?,?,?,?)}")) {
            st.setString(1, nv.getMaNhanVien()); st.setNString(2, nv.getHoTen()); st.setString(3, nv.getSoDienThoai());
            if (nv.getDiaChi() == null || nv.getDiaChi().isBlank()) st.setNull(4, Types.NVARCHAR); else st.setNString(4, nv.getDiaChi());
            st.setNString(5, nv.getChucVu()); st.setString(6, nv.getMaChiNhanh()); st.execute();
        } catch (SQLException e) { throw loiSql(e); }
    }

    @Override public void xoaNhanVien(String ma) { goiMotThamSo("{CALL dbo.SP_Admin_XoaNhanVien(?)}", ma); }
    @Override public void xoaChiNhanh(String ma) { goiMotThamSo("{CALL dbo.SP_Admin_XoaChiNhanh(?)}", ma); }

    @Override
    public void lienKetTaiKhoanNhanVien(String ma, String tenDangNhap) {
        try (Connection c = DBConnection.getConnection();
             CallableStatement st = c.prepareCall("{CALL dbo.SP_Admin_LienKetTaiKhoanNhanVien(?,?)}")) {
            st.setString(1, ma); st.setString(2, tenDangNhap); st.execute();
        } catch (SQLException e) { throw loiSql(e); }
    }

    @Override
    public String themChiNhanh(ChiNhanh cn) {
        try (Connection c = DBConnection.getConnection();
             CallableStatement st = c.prepareCall("{CALL dbo.SP_Admin_TaoChiNhanh(?,?,?)}")) {
            st.setNString(1, cn.getTenChiNhanh()); st.setNString(2, cn.getDiaChi()); st.registerOutParameter(3, Types.VARCHAR);
            st.execute(); return st.getString(3);
        } catch (SQLException e) { throw loiSql(e); }
    }

    @Override
    public void capNhatChiNhanh(ChiNhanh cn) {
        try (Connection c = DBConnection.getConnection();
             CallableStatement st = c.prepareCall("{CALL dbo.SP_Admin_CapNhatChiNhanh(?,?,?)}")) {
            st.setString(1, cn.getMaChiNhanh()); st.setNString(2, cn.getTenChiNhanh()); st.setNString(3, cn.getDiaChi()); st.execute();
        } catch (SQLException e) { throw loiSql(e); }
    }

    private void goiMotThamSo(String sql, String value) {
        try (Connection c = DBConnection.getConnection(); CallableStatement st = c.prepareCall(sql)) {
            st.setString(1, value); st.execute();
        } catch (SQLException e) { throw loiSql(e); }
    }

    private RuntimeException loiSql(SQLException e) {
        return new RuntimeException(e.getMessage() == null ? "Lỗi SQL Server." : e.getMessage(), e);
    }
}
