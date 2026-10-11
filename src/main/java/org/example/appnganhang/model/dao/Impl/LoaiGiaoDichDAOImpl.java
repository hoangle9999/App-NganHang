package org.example.appnganhang.model.dao.Impl;

import org.example.appnganhang.model.dao.intf.LoaiGiaoDichDAO;
import org.example.appnganhang.model.entity.LoaiGiaoDich;
import org.example.appnganhang.model.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LoaiGiaoDichDAOImpl implements LoaiGiaoDichDAO {

    private static final String SQL_THEM_LOAI_GIAO_DICH = """
            INSERT INTO LoaiGiaoDich
            (MaLoaiGD, TenLoaiGD)
            VALUES (?, ?)
            """;

    private static final String SQL_SUA_LOAI_GIAO_DICH = """
            UPDATE LoaiGiaoDich
            SET TenLoaiGD = ?
            WHERE MaLoaiGD = ?
            """;

    private static final String SQL_XOA_LOAI_GIAO_DICH = """
            DELETE FROM LoaiGiaoDich
            WHERE MaLoaiGD = ?
            """;

    private static final String SQL_GET_DANH_SACH_LOAI_GIAO_DICH = """
        SELECT
            l.MaLoaiGD,
            l.TenLoaiGD,
            g.MaGiaoDich,
            g.NoiDung,
            g.TrangThai
        FROM LoaiGiaoDich l
        LEFT JOIN GiaoDichChuyenTien g
            ON l.MaLoaiGD = g.MaLoaiGD
        ORDER BY l.MaLoaiGD, g.MaGiaoDich
        """;

    @Override
    public boolean themLoaiGiaoDich(LoaiGiaoDich loaiGiaoDich) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_THEM_LOAI_GIAO_DICH)) {

            ps.setString(1, loaiGiaoDich.getMaLoaiGD());
            ps.setString(2, loaiGiaoDich.getTenLoaiGD());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean suaLoaiGiaoDich(LoaiGiaoDich loaiGiaoDich) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SUA_LOAI_GIAO_DICH)) {

            ps.setString(1, loaiGiaoDich.getTenLoaiGD());
            ps.setString(2, loaiGiaoDich.getMaLoaiGD());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean xoaLoaiGiaoDich(String maLoaiGD) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_XOA_LOAI_GIAO_DICH)) {

            ps.setString(1, maLoaiGD);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<LoaiGiaoDich> getDanhSachLoaiGiaoDich() {
        List<LoaiGiaoDich> danhSach = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_GET_DANH_SACH_LOAI_GIAO_DICH);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                LoaiGiaoDich loaiGiaoDich = new LoaiGiaoDich();

                loaiGiaoDich.setMaLoaiGD(rs.getString("MaLoaiGD"));
                loaiGiaoDich.setTenLoaiGD(rs.getString("TenLoaiGD"));
                loaiGiaoDich.setMaGiaoDich(rs.getString("MaGiaoDich"));
                loaiGiaoDich.setNoiDung(rs.getString("NoiDung"));
                loaiGiaoDich.setTrangThai(rs.getString("TrangThai"));

                danhSach.add(loaiGiaoDich);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return danhSach;
    }
}