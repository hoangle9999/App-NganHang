package org.example.appnganhang.model.dao.Impl;

import org.example.appnganhang.model.dao.NhanVienDAO;
import org.example.appnganhang.model.NhanVien;
import org.example.appnganhang.model.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NhanVienDAOImpl implements NhanVienDAO {
    private static final String SQL_THEM_NHAN_VIEN = """
            INSERT INTO NhanVien
            (MaNhanVien, HoTen, ChucVu, MaChiNhanh, TenDangNhap)
            VALUES (?, ?, ?, ?, ?)
            """;

    private static final String SQL_SUA_NHAN_VIEN = """
            UPDATE NhanVien
            SET HoTen = ?,
                ChucVu = ?,
                MaChiNhanh = ?,
                TenDangNhap = ?
            WHERE MaNhanVien = ?
            """;

    private static final String SQL_XOA_NHAN_VIEN = """
            DELETE FROM NhanVien
            WHERE MaNhanVien = ?
            """;

    private static final String SQL_TIM_NHAN_VIEN = """
            SELECT MaNhanVien,
                   HoTen,
                   ChucVu,
                   MaChiNhanh,
                   TenDangNhap
            FROM NhanVien
            WHERE MaNhanVien = ?
            """;

    private static final String SQL_GET_NHAN_VIEN_THEO_CHI_NHANH = """
            SELECT MaNhanVien,
                   HoTen,
                   ChucVu,
                   MaChiNhanh,
                   TenDangNhap
            FROM NhanVien
            WHERE MaChiNhanh = ?
            """;

    @Override
    public boolean themNhanVien(NhanVien nhanVien) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_THEM_NHAN_VIEN)) {

            ps.setString(1, nhanVien.getMaNhanVien());
            ps.setString(2, nhanVien.getHoTen());
            ps.setString(3, nhanVien.getChucVu());
            ps.setString(4, nhanVien.getMaChiNhanh());
            ps.setString(5, nhanVien.getTenDangNhap());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean suaNhanVien(NhanVien nhanVien) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SUA_NHAN_VIEN)) {

            ps.setString(1, nhanVien.getHoTen());
            ps.setString(2, nhanVien.getChucVu());
            ps.setString(3, nhanVien.getMaChiNhanh());
            ps.setString(4, nhanVien.getTenDangNhap());
            ps.setString(5, nhanVien.getMaNhanVien());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean xoaNhanVien(String maNhanVien) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_XOA_NHAN_VIEN)) {

            ps.setString(1, maNhanVien);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Optional<NhanVien> timNhanVien(String maNhanVien) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_TIM_NHAN_VIEN)) {

            ps.setString(1, maNhanVien);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    NhanVien nhanVien = new NhanVien();

                    nhanVien.setMaNhanVien(rs.getString("MaNhanVien"));
                    nhanVien.setHoTen(rs.getString("HoTen"));
                    nhanVien.setChucVu(rs.getString("ChucVu"));
                    nhanVien.setMaChiNhanh(rs.getString("MaChiNhanh"));
                    nhanVien.setTenDangNhap(rs.getString("TenDangNhap"));

                    return Optional.of(nhanVien);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public List<NhanVien> getNhanVienTheoChiNhanh(String maChiNhanh) {
        List<NhanVien> danhSach = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_GET_NHAN_VIEN_THEO_CHI_NHANH)) {

            ps.setString(1, maChiNhanh);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    NhanVien nhanVien = new NhanVien();

                    nhanVien.setMaNhanVien(rs.getString("MaNhanVien"));
                    nhanVien.setHoTen(rs.getString("HoTen"));
                    nhanVien.setChucVu(rs.getString("ChucVu"));
                    nhanVien.setMaChiNhanh(rs.getString("MaChiNhanh"));
                    nhanVien.setTenDangNhap(rs.getString("TenDangNhap"));

                    danhSach.add(nhanVien);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }
}