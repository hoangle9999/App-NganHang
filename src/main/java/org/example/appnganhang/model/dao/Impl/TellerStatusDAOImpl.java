package org.example.appnganhang.model.dao.Impl;

import org.example.appnganhang.model.dao.intf.TellerStatusDAO;
import org.example.appnganhang.model.entity.GiaoDichChuyenTien;
import org.example.appnganhang.model.util.DBConnection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TellerStatusDAOImpl implements TellerStatusDAO {
    @Override
    public List<GiaoDichChuyenTien> layDanhSachGiaoDich() {
        String sql = "SELECT MaGiaoDich, SoTaiKhoanNguon, SoTaiKhoanDich, SoTien, NgayGiaoDich, "
                + "NoiDung, MaLoaiGD, TrangThai FROM dbo.vw_Teller_TrangThaiGiaoDich ORDER BY NgayGiaoDich DESC";
        List<GiaoDichChuyenTien> result = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             var statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                GiaoDichChuyenTien transaction = new GiaoDichChuyenTien();
                transaction.setMaGiaoDich(rs.getString("MaGiaoDich"));
                transaction.setMaTaiKhoanNguon(rs.getString("SoTaiKhoanNguon"));
                transaction.setMaTaiKhoanDich(rs.getString("SoTaiKhoanDich"));
                transaction.setSoTien(rs.getBigDecimal("SoTien"));
                Timestamp timestamp = rs.getTimestamp("NgayGiaoDich");
                transaction.setNgayGiaoDich(timestamp == null ? null : timestamp.toLocalDateTime());
                transaction.setNoiDung(rs.getString("NoiDung"));
                transaction.setMaLoaiGD(rs.getString("MaLoaiGD"));
                transaction.setTrangThai(rs.getString("TrangThai"));
                result.add(transaction);
            }
            return result;
        } catch (SQLException e) {
            throw new IllegalStateException("Không thể tải danh sách giao dịch: " + e.getMessage(), e);
        }
    }

    @Override
    public void duyetGiaoDich(String maGiaoDich) {
        execute("{CALL dbo.SP_GDV_DuyetGiaoDichChoXuLy(?)}", maGiaoDich, null);
    }

    @Override
    public void huyGiaoDich(String maGiaoDich, String lyDo) {
        execute("{CALL dbo.SP_GDV_HuyGiaoDichChoXuLy(?, ?)}", maGiaoDich, lyDo);
    }

    private void execute(String sql, String maGiaoDich, String secondParameter) {
        try (Connection connection = DBConnection.getConnection();
             CallableStatement statement = connection.prepareCall(sql)) {
            statement.setString(1, maGiaoDich);
            if (secondParameter != null) statement.setNString(2, secondParameter);
            statement.execute();
        } catch (SQLException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
