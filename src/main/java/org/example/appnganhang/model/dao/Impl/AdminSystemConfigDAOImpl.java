package org.example.appnganhang.model.dao.Impl;

import org.example.appnganhang.model.dao.intf.AdminSystemConfigDAO;
import org.example.appnganhang.model.entity.HanMucGiaoDich;
import org.example.appnganhang.model.entity.LoaiGiaoDich;
import org.example.appnganhang.model.entity.NganHang;
import org.example.appnganhang.model.entity.ThongTinTaiKhoanCauHinh;
import org.example.appnganhang.model.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AdminSystemConfigDAOImpl implements AdminSystemConfigDAO {
    @Override public List<HanMucGiaoDich> layDanhSachHanMuc() {
        List<HanMucGiaoDich> rows = new ArrayList<>();
        try (Connection c = DBConnection.getConnection(); CallableStatement st = c.prepareCall("{CALL dbo.SP_Admin_DanhSachHanMuc}"); ResultSet rs = st.executeQuery()) {
            while (rs.next()) rows.add(new HanMucGiaoDich(rs.getString("MaHanMuc"), rs.getString("TenHanMuc"), rs.getBigDecimal("HanMucNgay")));
            return rows;
        } catch (SQLException e) { throw loiSql(e); }
    }

    @Override public String taoHanMuc(String ten, BigDecimal amount) {
        try (Connection c = DBConnection.getConnection(); CallableStatement st = c.prepareCall("{CALL dbo.SP_Admin_TaoHanMuc(?,?,?)}")) {
            st.setNString(1, ten); st.setBigDecimal(2, amount); st.registerOutParameter(3, Types.VARCHAR); st.execute(); return st.getString(3);
        } catch (SQLException e) { throw loiSql(e); }
    }
    @Override public void capNhatHanMuc(HanMucGiaoDich hm) {
        try (Connection c = DBConnection.getConnection(); CallableStatement st = c.prepareCall("{CALL dbo.SP_Admin_CapNhatHanMuc(?,?,?)}")) {
            st.setString(1, hm.getMaHanMuc()); st.setNString(2, hm.getTenHanMuc()); st.setBigDecimal(3, hm.getHanMucNgay()); st.execute();
        } catch (SQLException e) { throw loiSql(e); }
    }
    @Override public void xoaHanMuc(String ma) { goiMotThamSo("{CALL dbo.SP_Admin_XoaHanMuc(?)}", ma); }

    @Override public Optional<ThongTinTaiKhoanCauHinh> traCuuTaiKhoan(String keyword) {
        try (Connection c = DBConnection.getConnection(); CallableStatement st = c.prepareCall("{CALL dbo.SP_Admin_TraCuuTaiKhoanHanMuc(?)}")) {
            st.setString(1, keyword);
            try (ResultSet rs = st.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                ThongTinTaiKhoanCauHinh info = new ThongTinTaiKhoanCauHinh();
                info.setSoTaiKhoan(rs.getString("SoTaiKhoan")); info.setTenKhachHang(rs.getString("HoTen"));
                info.setSoDu(rs.getBigDecimal("SoDu")); info.setTenChiNhanh(rs.getString("TenChiNhanh"));
                info.setTrangThai(rs.getString("TrangThai")); info.setMaHanMuc(rs.getString("MaHanMuc"));
                info.setTenHanMuc(rs.getString("TenHanMuc")); info.setHanMucNgay(rs.getBigDecimal("HanMucNgay"));
                return Optional.of(info);
            }
        } catch (SQLException e) { throw loiSql(e); }
    }
    @Override public void capNhatHanMucTaiKhoan(String account, String limit) {
        try (Connection c = DBConnection.getConnection(); CallableStatement st = c.prepareCall("{CALL dbo.SP_Admin_GanHanMucTaiKhoan(?,?)}")) {
            st.setString(1, account); st.setString(2, limit); st.execute();
        } catch (SQLException e) { throw loiSql(e); }
    }
    @Override public String doiTrangThaiTaiKhoan(String account) {
        try (Connection c = DBConnection.getConnection(); CallableStatement st = c.prepareCall("{CALL dbo.SP_Admin_DoiTrangThaiTaiKhoan(?)}")) {
            st.setString(1, account); try (ResultSet rs = st.executeQuery()) { return rs.next() ? rs.getString("TrangThaiMoi") : null; }
        } catch (SQLException e) { throw loiSql(e); }
    }

    @Override public List<LoaiGiaoDich> layDanhSachLoaiGiaoDich() {
        List<LoaiGiaoDich> rows = new ArrayList<>();
        try (Connection c = DBConnection.getConnection(); CallableStatement st = c.prepareCall("{CALL dbo.SP_Admin_DanhSachLoaiGiaoDich}"); ResultSet rs = st.executeQuery()) {
            while (rs.next()) { LoaiGiaoDich x = new LoaiGiaoDich(); x.setMaLoaiGD(rs.getString("MaLoaiGD")); x.setTenLoaiGD(rs.getString("TenLoaiGD")); rows.add(x); }
            return rows;
        } catch (SQLException e) { throw loiSql(e); }
    }
    @Override public String taoLoaiGiaoDich(String prefix, String name) {
        try (Connection c = DBConnection.getConnection(); CallableStatement st = c.prepareCall("{CALL dbo.SP_Admin_TaoLoaiGiaoDich(?,?,?)}")) {
            st.setString(1, prefix); st.setNString(2, name); st.registerOutParameter(3, Types.VARCHAR); st.execute(); return st.getString(3);
        } catch (SQLException e) { throw loiSql(e); }
    }
    @Override public void capNhatLoaiGiaoDich(LoaiGiaoDich x) {
        try (Connection c = DBConnection.getConnection(); CallableStatement st = c.prepareCall("{CALL dbo.SP_Admin_CapNhatLoaiGiaoDich(?,?)}")) {
            st.setString(1, x.getMaLoaiGD()); st.setNString(2, x.getTenLoaiGD()); st.execute();
        } catch (SQLException e) { throw loiSql(e); }
    }
    @Override public void xoaLoaiGiaoDich(String ma) { goiMotThamSo("{CALL dbo.SP_Admin_XoaLoaiGiaoDich(?)}", ma); }

    @Override public List<NganHang> layDanhSachNganHang() {
        List<NganHang> rows = new ArrayList<>();
        try (Connection c = DBConnection.getConnection(); CallableStatement st = c.prepareCall("{CALL dbo.SP_Admin_DanhSachNganHang}"); ResultSet rs = st.executeQuery()) {
            while (rs.next()) rows.add(new NganHang(rs.getString("MaNganHang"), rs.getString("TenNganHang"), rs.getString("TrangThai")));
            return rows;
        } catch (SQLException e) { throw loiSql(e); }
    }
    @Override public String doiTrangThaiNganHang(String code) {
        try (Connection c = DBConnection.getConnection(); CallableStatement st = c.prepareCall("{CALL dbo.SP_Admin_DoiTrangThaiNganHang(?)}")) {
            st.setString(1, code); try (ResultSet rs = st.executeQuery()) { return rs.next() ? rs.getString("TrangThaiMoi") : null; }
        } catch (SQLException e) { throw loiSql(e); }
    }

    private void goiMotThamSo(String sql, String value) {
        try (Connection c = DBConnection.getConnection(); CallableStatement st = c.prepareCall(sql)) { st.setString(1, value); st.execute(); }
        catch (SQLException e) { throw loiSql(e); }
    }
    private RuntimeException loiSql(SQLException e) { return new RuntimeException(e.getMessage() == null ? "Lỗi SQL Server." : e.getMessage(), e); }
}
