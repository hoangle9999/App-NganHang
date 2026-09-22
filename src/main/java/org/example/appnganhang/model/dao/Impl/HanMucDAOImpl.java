package org.example.appnganhang.model.dao.Impl;

import org.example.appnganhang.model.dao.HamMucDAO;
import org.example.appnganhang.model.HanMucGiaoDich;
import org.example.appnganhang.model.util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class HanMucDAOImpl implements HamMucDAO {
    private static final String SQL_THEM_HAN_MUC = """
            INSERT INTO HanMucGiaoDịch
            (MaHanMuc, TenHanMuc, HanMucNgay)
            VALUES (?, ?, ?)
            """;

    private static final String SQL_SUA_HAN_MUC = """
            UPDATE HanMucGiaoDịch
            SET TenHanMuc = ?,
                HanMucNgay = ?
            WHERE MaHanMuc = ?
            """;

    private static final String SQL_XOA_HAN_MUC = """
            DELETE FROM HanMucGiaoDịch
            WHERE MaHanMuc = ?
            """;

    private static final String SQL_GET_HAN_MUC = """
            SELECT MaHanMuc,
                   TenHanMuc,
                   HanMucNgay
            FROM HanMucGiaoDịch
            WHERE MaHanMuc = ?
            """;

    private static final String SQL_KIEM_TRA_HAN_MUC = """
            SELECT HMGD.HanMucNgay
            FROM TaiKhoanNganHang TKNH
            INNER JOIN HanMucGiaoDịch HMGD
                ON TKNH.MaHanMuc = HMGD.MaHanMuc
            WHERE TKNH.MaTaiKhoan = ?
            """;

    @Override
    public boolean themHanMuc(HanMucGiaoDich hanMuc) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_THEM_HAN_MUC)) {

            ps.setString(1, hanMuc.getMaHanMuc());
            ps.setString(2, hanMuc.getTenHanMuc());
            ps.setBigDecimal(3, hanMuc.getHanMucNgay());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean suaHanMuc(HanMucGiaoDich hanMuc) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SUA_HAN_MUC)) {

            ps.setString(1, hanMuc.getTenHanMuc());
            ps.setBigDecimal(2, hanMuc.getHanMucNgay());
            ps.setString(3, hanMuc.getMaHanMuc());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean xoaHanMuc(String maHanMuc) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_XOA_HAN_MUC)) {

            ps.setString(1, maHanMuc);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Optional<HanMucGiaoDich> getHanMuc(String maHanMuc) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_GET_HAN_MUC)) {

            ps.setString(1, maHanMuc);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    HanMucGiaoDich hanMuc = new HanMucGiaoDich();

                    hanMuc.setMaHanMuc(rs.getString("MaHanMuc"));
                    hanMuc.setTenHanMuc(rs.getString("TenHanMuc"));
                    hanMuc.setHanMucNgay(rs.getBigDecimal("HanMucNgay"));

                    return Optional.of(hanMuc);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public boolean kiemTraHanMuc(String maTaiKhoan, BigDecimal soTien) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_KIEM_TRA_HAN_MUC)) {

            ps.setString(1, maTaiKhoan);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    BigDecimal hanMucNgay =
                            rs.getBigDecimal("HanMucNgay");

                    return soTien.compareTo(hanMucNgay) <= 0;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
