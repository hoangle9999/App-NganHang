package org.example.appnganhang.model.dao.Impl;

import org.example.appnganhang.model.dao.KhachHangDAO;
import org.example.appnganhang.model.KhachHang;
import org.example.appnganhang.model.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class KhachHangDAOImpl implements KhachHangDAO {
    private static final String SQL_FIND_ALL =
            "SELECT MaKhachHang, HoTen, SoCCCD, SoDienThoai, Email, TenDangNhap FROM KhachHang";
    private static final String SQL_FIND_BY_ID =
            "SELECT MaKhachHang, HoTen, SoCCCD, SoDienThoai, Email, TenDangNhap FROM KhachHang WHERE MaKhachHang = ?";
    private static final String SQL_FIND_BY_CCCD =
            "SELECT MaKhachHang, HoTen, SoCCCD, SoDienThoai, Email, TenDangNhap FROM KhachHang WHERE SoCCCD = ?";
    private static final String SQL_FIND_BY_PHONE =
            "SELECT MaKhachHang, HoTen, SoCCCD, SoDienThoai, Email, TenDangNhap FROM KhachHang WHERE SoDienThoai = ?";
    private static final String SQL_FIND_BY_USERNAME =
            "SELECT MaKhachHang, HoTen, SoCCCD, SoDienThoai, Email, TenDangNhap FROM KhachHang WHERE TenDangNhap = ?";
    private static final String SQL_INSERT =
            "INSERT INTO KhachHang (MaKhachHang, HoTen, SoCCCD, SoDienThoai, Email, TenDangNhap) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE =
            "UPDATE KhachHang SET HoTen = ?, SoCCCD = ?, SoDienThoai = ?, TenDangNhap = ? " + "WHERE MaKhachHang = ?";
    private static final String SQL_DELETE =
            "DELETE FROM KhachHang WHERE MaKhachHang = ?";

    @Override
    public boolean insert(KhachHang kh) {
        try(Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(SQL_INSERT)){
            ps.setString(1, kh.getMaKhachHang());
            ps.setString(2, kh.getHoTen());
            ps.setString(3, kh.getSoCCCD());
            ps.setString(4, kh.getSoDienThoai());
            ps.setString(5, kh.getEmail());
            ps.setString(6, kh.getTenDangNhap());

            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi insert KhachHang: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(KhachHang kh) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_UPDATE)) {

            ps.setString(1, kh.getHoTen());
            ps.setString(2, kh.getSoCCCD());
            ps.setString(3, kh.getSoDienThoai());
            ps.setString(4, kh.getEmail());
            ps.setString(5, kh.getTenDangNhap());
            ps.setString(6, kh.getMaKhachHang());

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Lỗi update KhachHang: " + e.getMessage(), e
            );
        }
    }

    @Override
    public boolean delete(String MaKh) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_DELETE)) {

            ps.setString(1, MaKh);

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Lỗi delete KhachHang: " + e.getMessage(), e
            );
        }
    }

    @Override
    public Optional<KhachHang> findById(String MaKh) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_FIND_BY_ID)) {

            ps.setString(1, MaKh);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    KhachHang kh = new KhachHang();
                    kh.setMaKhachHang(rs.getString("MaKhachHang"));
                    kh.setHoTen(rs.getString("hoTen"));
                    kh.setSoCCCD(rs.getString("SoCCCD"));
                    kh.setSoDienThoai(rs.getString("SoDienThoai"));
                    kh.setEmail(rs.getString("Email"));
                    kh.setTenDangNhap(rs.getString("TenDangNhap"));

                    return Optional.of(kh);
                }

                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Lỗi findById KhachHang: " + e.getMessage(), e
            );
        }
    }

    @Override
    public Optional<KhachHang> findByCCCD(String soCCCD) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_FIND_BY_CCCD)) {

            ps.setString(1, soCCCD);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    KhachHang kh = new KhachHang();

                    kh.setMaKhachHang(rs.getString("MaKhachHang"));
                    kh.setHoTen(rs.getString("HoTen"));
                    kh.setSoCCCD(rs.getString("SoCCCD"));
                    kh.setSoDienThoai(rs.getString("SoDienThoai"));
                    kh.setEmail(rs.getString("Email"));
                    kh.setTenDangNhap(rs.getString("TenDangNhap"));

                    return Optional.of(kh);
                }

                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Lỗi findByCCCD KhachHang: " + e.getMessage(), e
            );
        }
    }

    @Override
    public Optional<KhachHang> findByPhone(String soDienThoai) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_FIND_BY_PHONE)) {

            ps.setString(1, soDienThoai);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    KhachHang kh = new KhachHang();

                    kh.setMaKhachHang(rs.getString("MaKhachHang"));
                    kh.setHoTen(rs.getString("HoTen"));
                    kh.setSoCCCD(rs.getString("SoCCCD"));
                    kh.setSoDienThoai(rs.getString("SoDienThoai"));
                    kh.setEmail(rs.getString("Email"));
                    kh.setTenDangNhap(rs.getString("TenDangNhap"));

                    return Optional.of(kh);
                }

                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Lỗi findByPhone KhachHang: " + e.getMessage(), e
            );
        }
    }

    @Override
    public Optional<KhachHang> findByUsername(String tenDangNhap) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_FIND_BY_USERNAME)) {

            ps.setString(1, tenDangNhap);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    KhachHang kh = new KhachHang();

                    kh.setMaKhachHang(rs.getString("MaKhachHang"));
                    kh.setHoTen(rs.getString("HoTen"));
                    kh.setSoCCCD(rs.getString("SoCCCD"));
                    kh.setSoDienThoai(rs.getString("SoDienThoai"));
                    kh.setEmail(rs.getString("Email"));
                    kh.setTenDangNhap(rs.getString("TenDangNhap"));

                    return Optional.of(kh);
                }

                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Lỗi findByUsername KhachHang: " + e.getMessage(), e
            );
        }
    }

    @Override
    public List<KhachHang> findAll() {
        List<KhachHang> danhSach = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                KhachHang kh = new KhachHang();

                kh.setMaKhachHang(rs.getString("MaKhachHang"));
                kh.setHoTen(rs.getString("HoTen"));
                kh.setSoCCCD(rs.getString("SoCCCD"));
                kh.setSoDienThoai(rs.getString("SoDienThoai"));
                kh.setEmail(rs.getString("Email"));
                kh.setTenDangNhap(rs.getString("TenDangNhap"));

                danhSach.add(kh);
            }

            return danhSach;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Lỗi findAll KhachHang: " + e.getMessage(), e
            );
        }
    }
}
