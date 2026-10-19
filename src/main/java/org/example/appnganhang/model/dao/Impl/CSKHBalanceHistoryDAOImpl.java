
package org.example.appnganhang.model.dao.Impl;

import org.example.appnganhang.model.dao.intf.CSKHBalanceHistoryDAO;
import org.example.appnganhang.model.entity.LichSuGiaoDich;
import org.example.appnganhang.model.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CSKHBalanceHistoryDAOImpl implements CSKHBalanceHistoryDAO {

    // TrangThai được lấy từ GiaoDichChuyenTien,
    // không phải từ LichSuGiaoDich.
    private static final String SELECT_LOGS =
            "SELECT ls.MaLog, ls.MaGiaoDich, ls.SoTaiKhoanLienQuan, " +
                    "ls.HanhDong, ls.SoTienThayDoi, ls.SoDuSauThayDoi, " +
                    "gd.TrangThai AS TrangThai, ls.ThoiGianGhiLog " +
                    "FROM dbo.LichSuGiaoDich ls " +
                    "INNER JOIN dbo.GiaoDichChuyenTien gd " +
                    "ON ls.MaGiaoDich = gd.MaGiaoDich";

    @Override
    public List<LichSuGiaoDich> layDanhSachLog(
            String tuKhoa,
            LocalDate tuNgay,
            LocalDate denNgay
    ) throws SQLException {

        StringBuilder sql = new StringBuilder(SELECT_LOGS);
        List<Object> thamSo = new ArrayList<>();
        boolean coDieuKien = false;

        // Tìm theo số tài khoản hoặc mã giao dịch.
        if (tuKhoa != null && !tuKhoa.isBlank()) {
            sql.append(
                    " WHERE (ls.SoTaiKhoanLienQuan LIKE ? " +
                            "OR ls.MaGiaoDich LIKE ?)"
            );

            thamSo.add("%" + tuKhoa.trim() + "%");
            thamSo.add("%" + tuKhoa.trim() + "%");
            coDieuKien = true;
        }

        // Lọc từ ngày, tính cả thời điểm bắt đầu ngày.
        if (tuNgay != null) {
            sql.append(coDieuKien ? " AND" : " WHERE")
                    .append(" ls.ThoiGianGhiLog >= ?");

            thamSo.add(Timestamp.valueOf(tuNgay.atStartOfDay()));
            coDieuKien = true;
        }

        // Lọc đến hết ngày được chọn.
        // Dùng mốc đầu ngày kế tiếp để không bỏ sót giờ trong ngày.
        if (denNgay != null) {
            sql.append(coDieuKien ? " AND" : " WHERE")
                    .append(" ls.ThoiGianGhiLog < ?");

            thamSo.add(
                    Timestamp.valueOf(denNgay.plusDays(1).atStartOfDay())
            );
        }

        sql.append(" ORDER BY ls.ThoiGianGhiLog DESC");

        List<LichSuGiaoDich> danhSach = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql.toString())) {

            // Gán các tham số tìm kiếm.
            for (int i = 0; i < thamSo.size(); i++) {
                statement.setObject(i + 1, thamSo.get(i));
            }

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    LichSuGiaoDich log = new LichSuGiaoDich();

                    log.setMaLog(result.getInt("MaLog"));
                    log.setMaGiaoDich(
                            result.getString("MaGiaoDich")
                    );
                    log.setSoTaiKhoanLienQuan(
                            result.getString("SoTaiKhoanLienQuan")
                    );
                    log.setHanhDong(
                            result.getString("HanhDong")
                    );
                    log.setSoTienThayDoi(
                            result.getBigDecimal("SoTienThayDoi")
                    );
                    log.setSoDuSauThayDoi(
                            result.getBigDecimal("SoDuSauThayDoi")
                    );

                    // Trạng thái lấy từ bảng GiaoDichChuyenTien.
                    log.setTrangThai(
                            result.getString("TrangThai")
                    );

                    Timestamp timestamp =
                            result.getTimestamp("ThoiGianGhiLog");

                    log.setThoiGianGhiLog(
                            timestamp == null
                                    ? null
                                    : timestamp.toLocalDateTime()
                    );

                    danhSach.add(log);
                }
            }
        }

        return danhSach;
    }

    @Override
    public String layTenChuTaiKhoan(String soTaiKhoan)
            throws SQLException {

        String sql =
                "SELECT TOP 1 kh.HoTen " +
                        "FROM dbo.TaiKhoanNganHang tk " +
                        "JOIN dbo.KhachHang kh " +
                        "ON tk.MaKhachHang = kh.MaKhachHang " +
                        "WHERE tk.SoTaiKhoan = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, soTaiKhoan);

            try (ResultSet result = statement.executeQuery()) {
                return result.next()
                        ? result.getString("HoTen")
                        : "";
            }
        }
    }
}
