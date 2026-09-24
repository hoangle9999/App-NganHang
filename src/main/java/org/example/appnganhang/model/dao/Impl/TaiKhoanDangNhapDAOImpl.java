package org.example.appnganhang.model.dao.Impl;

import org.example.appnganhang.model.dao.TaiKhoanDangNhapDAO;
import org.example.appnganhang.model.entity.TaiKhoanDangNhap_VaiTro;
import org.example.appnganhang.model.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class TaiKhoanDangNhapDAOImpl implements TaiKhoanDangNhapDAO {
    private static final String SQL_INSERT = """
            INSERT INTO TaiKhoanDangNhap_VaiTro
            (TenDangNhap, MatKhau, RoleName, MaNguoiDung)
            VALUES (?, ?, N'Khách hàng', ?)
            """;

    private static final String SQL_FIND_BY_USERNAME = """
            SELECT TenDangNhap, MatKhau, RoleName, MaNguoiDung
            FROM TaiKhoanDangNhap_VaiTro
            WHERE TenDangNhap = ?
            """;
    private static final String SQL_LOGIN = """
            SELECT TenDangNhap, MatKhau, RoleName, MaNguoiDung
            FROM TaiKhoanDangNhap_VaiTro
            WHERE TenDangNhap = ?
              AND MatKhau = ?
            """;

    private static final String SQL_CHANGE_PASSWORD = """
            UPDATE TaiKhoanDangNhap_VaiTro
            SET MatKhau = ?
            WHERE TenDangNhap = ?
            """;

    @Override
    public boolean insert(TaiKhoanDangNhap_VaiTro taiKhoan) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {

            ps.setString(1, taiKhoan.getTenDangNhap());
            ps.setString(2, taiKhoan.getMatKhau());
            ps.setString(3, taiKhoan.getMaNguoiDung());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Optional<TaiKhoanDangNhap_VaiTro> findByUsername(String tenDangNhap) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_FIND_BY_USERNAME)) {

            pstmt.setString(1, tenDangNhap);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {

                    TaiKhoanDangNhap_VaiTro tk = new TaiKhoanDangNhap_VaiTro();

                    tk.setTenDangNhap(rs.getString("TenDangNhap"));
                    tk.setMatKhau(rs.getString("MatKhau"));
                    tk.setRoleName(rs.getString("RoleName"));
                    tk.setMaNguoiDung(rs.getString("MaNguoiDung"));

                    return Optional.of(tk);
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<TaiKhoanDangNhap_VaiTro> login(String tenDangNhap, String matKhau) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_LOGIN)) {

            pstmt.setString(1, tenDangNhap);
            pstmt.setString(2, matKhau);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    TaiKhoanDangNhap_VaiTro tk = new TaiKhoanDangNhap_VaiTro();

                    tk.setTenDangNhap(rs.getString("TenDangNhap"));
                    tk.setMatKhau(rs.getString("MatKhau"));
                    tk.setRoleName(rs.getString("RoleName"));
                    tk.setMaNguoiDung(rs.getString("MaNguoiDung"));

                    return Optional.of(tk);
                }

                return Optional.empty();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public boolean changePassword(String tenDangNhap, String matKhauMoi) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt =
                     conn.prepareStatement(SQL_CHANGE_PASSWORD)) {

            pstmt.setString(1, matKhauMoi);
            pstmt.setString(2, tenDangNhap);

            int rowsAffected = pstmt.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
