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
    private static final String SELECT_LOGS =
            "SELECT MaLog, MaGiaoDich, SoTaiKhoanLienQuan, HanhDong, " +
                    "SoTienThayDoi, SoDuSauThayDoi, ThoiGianGhiLog " +
                    "FROM LichSuGiaoDich";

    @Override
    public List<LichSuGiaoDich> layDanhSachLog(String tuKhoa, LocalDate tuNgay, LocalDate denNgay) throws SQLException {
        StringBuilder sql = new StringBuilder(SELECT_LOGS);
        List<Object> thamSo = new ArrayList<>();
        boolean coDieuKien = false;

        if (tuKhoa != null && !tuKhoa.isBlank()) {
            sql.append(" WHERE (SoTaiKhoanLienQuan LIKE ? OR MaGiaoDich LIKE ?)");
            thamSo.add("%" + tuKhoa.trim() + "%");
            thamSo.add("%" + tuKhoa.trim() + "%");
            coDieuKien = true;
        }
        if (tuNgay != null) {
            sql.append(coDieuKien ? " AND" : " WHERE").append(" ThoiGianGhiLog >= ?");
            thamSo.add(Timestamp.valueOf(tuNgay.atStartOfDay()));
            coDieuKien = true;
        }
        if (denNgay != null) {
            sql.append(coDieuKien ? " AND" : " WHERE").append(" ThoiGianGhiLog < ?");
            thamSo.add(Timestamp.valueOf(denNgay.plusDays(1).atStartOfDay()));
        }
        sql.append(" ORDER BY ThoiGianGhiLog DESC");

        List<LichSuGiaoDich> danhSach = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < thamSo.size(); i++) {
                statement.setObject(i + 1, thamSo.get(i));
            }
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    LichSuGiaoDich log = new LichSuGiaoDich();
                    log.setMaLog(result.getInt("MaLog"));
                    log.setMaGiaoDich(result.getString("MaGiaoDich"));
                    log.setSoTaiKhoanLienQuan(result.getString("SoTaiKhoanLienQuan"));
                    log.setHanhDong(result.getString("HanhDong"));
                    log.setSoTienThayDoi(result.getBigDecimal("SoTienThayDoi"));
                    log.setSoDuSauThayDoi(result.getBigDecimal("SoDuSauThayDoi"));
                    Timestamp timestamp = result.getTimestamp("ThoiGianGhiLog");
                    log.setThoiGianGhiLog(timestamp == null ? null : timestamp.toLocalDateTime());
                    danhSach.add(log);
                }
            }
        }
        return danhSach;
    }

    @Override
    public String layTenChuTaiKhoan(String soTaiKhoan) throws SQLException {
        String sql = "SELECT TOP 1 kh.HoTen FROM TaiKhoanNganHang tk " +
                "JOIN KhachHang kh ON tk.MaKhachHang = kh.MaKhachHang WHERE tk.SoTaiKhoan = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, soTaiKhoan);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? result.getString("HoTen") : "";
            }
        }
    }
}
