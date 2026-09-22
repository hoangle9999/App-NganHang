package org.example.appnganhang.model.dao.Impl;

import org.example.appnganhang.model.dao.ChiNhanhDAO;
import org.example.appnganhang.model.ChiNhanh;
import org.example.appnganhang.model.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChiNhanhDAOImpl implements ChiNhanhDAO {
    private static final String SQL_THEM_CHI_NHANH = """
            INSERT INTO ChiNhanh
            (MaChiNhanh, TenChiNhanh, DiaChi)
            VALUES (?, ?, ?)
            """;

    private static final String SQL_SUA_CHI_NHANH = """
            UPDATE ChiNhanh
            SET TenChiNhanh = ?,
                DiaChi = ?
            WHERE MaChiNhanh = ?
            """;

    private static final String SQL_XOA_CHI_NHANH = """
            DELETE FROM ChiNhanh
            WHERE MaChiNhanh = ?
            """;

    private static final String SQL_TIM_KIEM_CHI_NHANH = """
            SELECT MaChiNhanh,
                   TenChiNhanh,
                   DiaChi
            FROM ChiNhanh
            WHERE MaChiNhanh = ?
            """;

    private static final String SQL_GET_DANH_SACH_CHI_NHANH = """
            SELECT MaChiNhanh,
                   TenChiNhanh,
                   DiaChi
            FROM ChiNhanh
            """;

    @Override
    public boolean themChiNhanh(ChiNhanh chiNhanh) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_THEM_CHI_NHANH)) {

            ps.setString(1, chiNhanh.getMaChiNhanh());
            ps.setString(2, chiNhanh.getTenChiNhanh());
            ps.setString(3, chiNhanh.getDiaChi());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean suaChiNhanh(ChiNhanh chiNhanh) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SUA_CHI_NHANH)) {

            ps.setString(1, chiNhanh.getTenChiNhanh());
            ps.setString(2, chiNhanh.getDiaChi());
            ps.setString(3, chiNhanh.getMaChiNhanh());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean xoaChiNhanh(String maChiNhanh) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_XOA_CHI_NHANH)) {

            ps.setString(1, maChiNhanh);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Optional<ChiNhanh> timKiemChiNhanh(String maChiNhanh) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_TIM_KIEM_CHI_NHANH)) {

            ps.setString(1, maChiNhanh);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ChiNhanh chiNhanh = new ChiNhanh();

                    chiNhanh.setMaChiNhanh(rs.getString("MaChiNhanh"));
                    chiNhanh.setTenChiNhanh(rs.getString("TenChiNhanh"));
                    chiNhanh.setDiaChi(rs.getString("DiaChi"));

                    return Optional.of(chiNhanh);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public List<ChiNhanh> getDanhSachChiNhanh() {
        List<ChiNhanh> danhSach = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_GET_DANH_SACH_CHI_NHANH);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ChiNhanh chiNhanh = new ChiNhanh();

                chiNhanh.setMaChiNhanh(rs.getString("MaChiNhanh"));
                chiNhanh.setTenChiNhanh(rs.getString("TenChiNhanh"));
                chiNhanh.setDiaChi(rs.getString("DiaChi"));

                danhSach.add(chiNhanh);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return danhSach;
    }
}
