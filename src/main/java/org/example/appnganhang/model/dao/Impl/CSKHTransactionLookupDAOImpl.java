package org.example.appnganhang.model.dao.Impl;

import org.example.appnganhang.model.dao.intf.CSKHTransactionLookupDAO;
import org.example.appnganhang.model.entity.GiaoDichChuyenTien;
import org.example.appnganhang.model.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Cài đặt các truy vấn SQL cho tra cứu giao dịch CSKH. */
public class CSKHTransactionLookupDAOImpl implements CSKHTransactionLookupDAO {

    private static final String SQL_DANH_SACH = """
            SELECT MaGiaoDich, MaTaiKhoanNguon, MaTaiKhoanDich, SoTien,
                   NgayGiaoDich, NoiDung, MaLoaiGD, TrangThai
            FROM GiaoDichChuyenTien
            ORDER BY NgayGiaoDich DESC
            """;

    private static final String SQL_TIM_KIEM = """
            SELECT MaGiaoDich, MaTaiKhoanNguon, MaTaiKhoanDich, SoTien,
                   NgayGiaoDich, NoiDung, MaLoaiGD, TrangThai
            FROM GiaoDichChuyenTien
            WHERE 1 = 1
            """;

    @Override
    public List<GiaoDichChuyenTien> layDanhSachGiaoDich() throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_DANH_SACH);
             ResultSet resultSet = statement.executeQuery()) {
            return docDanhSach(resultSet);
        }
    }

    @Override
    public List<GiaoDichChuyenTien> timKiemGiaoDich(
            String tuKhoa,
            LocalDate tuNgay,
            LocalDate denNgay,
            String maLoaiGiaoDich,
            String trangThai) throws SQLException {

        StringBuilder sql = new StringBuilder(SQL_TIM_KIEM);
        List<Object> thamSo = new ArrayList<>();

        if (tuKhoa != null && !tuKhoa.isBlank()) {
            sql.append(" AND (MaGiaoDich LIKE ? OR MaTaiKhoanNguon LIKE ? OR MaTaiKhoanDich LIKE ?)");
            String tuKhoaLike = "%" + tuKhoa.trim() + "%";
            thamSo.add(tuKhoaLike);
            thamSo.add(tuKhoaLike);
            thamSo.add(tuKhoaLike);
        }

        if (tuNgay != null) {
            sql.append(" AND NgayGiaoDich >= ?");
            thamSo.add(Timestamp.valueOf(tuNgay.atStartOfDay()));
        }

        // Dùng mốc đầu ngày kế tiếp để bao gồm toàn bộ ngày kết thúc.
        if (denNgay != null) {
            sql.append(" AND NgayGiaoDich < ?");
            thamSo.add(Timestamp.valueOf(denNgay.plusDays(1).atStartOfDay()));
        }

        if (maLoaiGiaoDich != null && !maLoaiGiaoDich.isBlank()) {
            sql.append(" AND MaLoaiGD = ?");
            thamSo.add(maLoaiGiaoDich);
        }

        if (trangThai != null && !trangThai.isBlank()) {
            sql.append(" AND TrangThai = ?");
            thamSo.add(trangThai);
        }

        sql.append(" ORDER BY NgayGiaoDich DESC");

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < thamSo.size(); i++) {
                statement.setObject(i + 1, thamSo.get(i));
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                return docDanhSach(resultSet);
            }
        }
    }

    @Override
    public String layTenLoaiGiaoDich(String maLoaiGiaoDich) throws SQLException {
        String sql = "SELECT TenLoaiGD FROM LoaiGiaoDich WHERE MaLoaiGD = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, maLoaiGiaoDich);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getString("TenLoaiGD") : maLoaiGiaoDich;
            }
        }
    }

    @Override
    public String layMaLoaiGiaoDich(String tenLoaiGiaoDich) throws SQLException {
        String sql = "SELECT MaLoaiGD FROM LoaiGiaoDich WHERE TenLoaiGD = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, tenLoaiGiaoDich);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getString("MaLoaiGD") : null;
            }
        }
    }

    private List<GiaoDichChuyenTien> docDanhSach(ResultSet resultSet) throws SQLException {
        List<GiaoDichChuyenTien> danhSach = new ArrayList<>();
        while (resultSet.next()) {
            GiaoDichChuyenTien giaoDich = new GiaoDichChuyenTien();
            giaoDich.setMaGiaoDich(resultSet.getString("MaGiaoDich"));
            giaoDich.setMaTaiKhoanNguon(resultSet.getString("MaTaiKhoanNguon"));
            giaoDich.setMaTaiKhoanDich(resultSet.getString("MaTaiKhoanDich"));
            giaoDich.setSoTien(resultSet.getBigDecimal("SoTien"));
            Timestamp thoiGian = resultSet.getTimestamp("NgayGiaoDich");
            giaoDich.setNgayGiaoDich(thoiGian != null ? thoiGian.toLocalDateTime() : null);
            giaoDich.setNoiDung(resultSet.getString("NoiDung"));
            giaoDich.setMaLoaiGD(resultSet.getString("MaLoaiGD"));
            giaoDich.setTrangThai(resultSet.getString("TrangThai"));
            danhSach.add(giaoDich);
        }
        return danhSach;
    }
}
