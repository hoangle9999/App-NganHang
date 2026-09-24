package org.example.appnganhang.model.dao.Impl;

import org.example.appnganhang.model.dao.TaiKhoanDAO;
import org.example.appnganhang.model.entity.TaiKhoanNganHang;
import org.example.appnganhang.model.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TaiKhoanDAOImpl implements TaiKhoanDAO {

    private static final String SQL_THEM_TAI_KHOAN =
            "INSERT INTO TaiKhoanNganHang " +
                    "(MaTaiKhoan, MaKhachHang, SoTaiKhoan, SoDu, TrangThai, MaChiNhanh, MaHanMuc) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_CAP_NHAT_TAI_KHOAN =
            "UPDATE TaiKhoanNganHang " +
                    "SET MaKhachHang = ?, SoTaiKhoan = ?, SoDu = ?, TrangThai = ?, " +
                    "MaChiNhanh = ?, MaHanMuc = ? " +
                    "WHERE MaTaiKhoan = ?";

    private static final String SQL_XOA_TAI_KHOAN =
            "DELETE FROM TaiKhoanNganHang " +
                    "WHERE MaTaiKhoan = ?";

    private static final String SQL_TIM_THEO_MA =
            "SELECT MaTaiKhoan, MaKhachHang, SoTaiKhoan, SoDu, TrangThai, MaChiNhanh, MaHanMuc " +
                    "FROM TaiKhoanNganHang " +
                    "WHERE MaTaiKhoan = ?";

    private static final String SQL_TIM_THEO_SO_TAI_KHOAN =
            "SELECT MaTaiKhoan, MaKhachHang, SoTaiKhoan, SoDu, TrangThai, MaChiNhanh, MaHanMuc " +
                    "FROM TaiKhoanNganHang " +
                    "WHERE SoTaiKhoan = ?";

    private static final String SQL_TIM_THEO_MA_KHACH_HANG =
            "SELECT MaTaiKhoan, MaKhachHang, SoTaiKhoan, SoDu, TrangThai, MaChiNhanh, MaHanMuc " +
                    "FROM TaiKhoanNganHang " +
                    "WHERE MaKhachHang = ?";

    private static final String SQL_TIM_THEO_MA_CHI_NHANH =
            "SELECT MaTaiKhoan, MaKhachHang, SoTaiKhoan, SoDu, TrangThai, MaChiNhanh, MaHanMuc " +
                    "FROM TaiKhoanNganHang " +
                    "WHERE MaChiNhanh = ?";

    private static final String SQL_TIM_THEO_TRANG_THAI =
            "SELECT MaTaiKhoan, MaKhachHang, SoTaiKhoan, SoDu, TrangThai, MaChiNhanh, MaHanMuc " +
                    "FROM TaiKhoanNganHang " +
                    "WHERE TrangThai = ?";

    private static final String SQL_GET_DANH_SACH_TAI_KHOAN =
            "SELECT MaTaiKhoan, MaKhachHang, SoTaiKhoan, SoDu, TrangThai, MaChiNhanh, MaHanMuc " +
                    "FROM TaiKhoanNganHang";

    private static final String SQL_CAP_NHAT_TRANG_THAI =
            "UPDATE TaiKhoanNganHang " +
                    "SET TrangThai = ? " +
                    "WHERE MaTaiKhoan = ?";

    private static final String SQL_GAN_HAN_MUC =
            "UPDATE TaiKhoanNganHang " +
                    "SET MaHanMuc = ? " +
                    "WHERE MaTaiKhoan = ?";

    @Override
    public boolean themTaiKhoan(TaiKhoanNganHang taiKhoan) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_THEM_TAI_KHOAN)) {

            ps.setString(1, taiKhoan.getMaTaiKhoan());
            ps.setString(2, taiKhoan.getMaKhachHang());
            ps.setString(3, taiKhoan.getSoTaiKhoan());
            ps.setBigDecimal(4, taiKhoan.getSoDu());
            ps.setString(5, taiKhoan.getTrangThai());
            ps.setString(6, taiKhoan.getMaChiNhanh());
            ps.setString(7, taiKhoan.getMaHanMuc());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean capNhatTaiKhoan(TaiKhoanNganHang taiKhoan) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_CAP_NHAT_TAI_KHOAN)) {

            ps.setString(1, taiKhoan.getMaKhachHang());
            ps.setString(2, taiKhoan.getSoTaiKhoan());
            ps.setBigDecimal(3, taiKhoan.getSoDu());
            ps.setString(4, taiKhoan.getTrangThai());
            ps.setString(5, taiKhoan.getMaChiNhanh());
            ps.setString(6, taiKhoan.getMaHanMuc());
            ps.setString(7, taiKhoan.getMaTaiKhoan());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean xoaTaiKhoan(String maTaiKhoan) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_XOA_TAI_KHOAN)) {

            ps.setString(1, maTaiKhoan);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Optional<TaiKhoanNganHang> timTheoMa(String maTaiKhoan) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_TIM_THEO_MA)) {

            ps.setString(1, maTaiKhoan);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTaiKhoan(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public Optional<TaiKhoanNganHang> timTheoSoTaiKhoan(String soTaiKhoan) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_TIM_THEO_SO_TAI_KHOAN)) {

            ps.setString(1, soTaiKhoan);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTaiKhoan(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public List<TaiKhoanNganHang> timTheoMaKhachHang(String maKhachHang) {
        List<TaiKhoanNganHang> danhSach = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_TIM_THEO_MA_KHACH_HANG)) {

            ps.setString(1, maKhachHang);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(mapResultSetToTaiKhoan(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return danhSach;
    }

    @Override
    public List<TaiKhoanNganHang> timTheoMaChiNhanh(String maChiNhanh) {
        List<TaiKhoanNganHang> danhSach = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_TIM_THEO_MA_CHI_NHANH)) {

            ps.setString(1, maChiNhanh);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(mapResultSetToTaiKhoan(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return danhSach;
    }

    @Override
    public List<TaiKhoanNganHang> timTheoTrangThai(String trangThai) {
        List<TaiKhoanNganHang> danhSach = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_TIM_THEO_TRANG_THAI)) {

            ps.setString(1, trangThai);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(mapResultSetToTaiKhoan(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return danhSach;
    }

    @Override
    public List<TaiKhoanNganHang> getDanhSachTaiKhoan() {
        List<TaiKhoanNganHang> danhSach = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_GET_DANH_SACH_TAI_KHOAN);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                danhSach.add(mapResultSetToTaiKhoan(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return danhSach;
    }

    @Override
    public boolean capNhatTrangThai(String maTaiKhoan, String trangThai) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_CAP_NHAT_TRANG_THAI)) {

            ps.setString(1, trangThai);
            ps.setString(2, maTaiKhoan);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean ganHanMuc(String maTaiKhoan, String maHanMuc) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_GAN_HAN_MUC)) {

            ps.setString(1, maHanMuc);
            ps.setString(2, maTaiKhoan);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private TaiKhoanNganHang mapResultSetToTaiKhoan(ResultSet rs) throws SQLException {
        TaiKhoanNganHang taiKhoan = new TaiKhoanNganHang();

        taiKhoan.setMaTaiKhoan(rs.getString("MaTaiKhoan"));
        taiKhoan.setMaKhachHang(rs.getString("MaKhachHang"));
        taiKhoan.setSoTaiKhoan(rs.getString("SoTaiKhoan"));
        taiKhoan.setSoDu(rs.getBigDecimal("SoDu"));
        taiKhoan.setTrangThai(rs.getString("TrangThai"));
        taiKhoan.setMaChiNhanh(rs.getString("MaChiNhanh"));
        taiKhoan.setMaHanMuc(rs.getString("MaHanMuc"));

        return taiKhoan;
    }
}
