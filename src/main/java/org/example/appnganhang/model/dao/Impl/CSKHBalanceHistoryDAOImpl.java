package org.example.appnganhang.model.dao.Impl;

import org.example.appnganhang.model.dao.intf.CSKHBalanceHistoryDAO;
import org.example.appnganhang.model.entity.LichSuGiaoDich;
import org.example.appnganhang.model.util.DBConnection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CSKHBalanceHistoryDAOImpl implements CSKHBalanceHistoryDAO {

    @Override
    public List<LichSuGiaoDich> layDanhSachLog(String tuKhoa, LocalDate tuNgay, LocalDate denNgay)
            throws SQLException {
        List<LichSuGiaoDich> danhSach = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             CallableStatement statement = connection.prepareCall("{call dbo.sp_CSKH_LichSuGiaoDich(?, ?, ?)}")) {
            if (tuKhoa == null || tuKhoa.isBlank()) statement.setNull(1, java.sql.Types.NVARCHAR);
            else statement.setString(1, tuKhoa.trim());
            setDateOrNull(statement, 2, tuNgay);
            setDateOrNull(statement, 3, denNgay);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    LichSuGiaoDich log = new LichSuGiaoDich();
                    log.setMaLog(result.getInt("MaLog"));
                    log.setMaGiaoDich(result.getString("MaGiaoDich"));
                    log.setSoTaiKhoanLienQuan(result.getString("SoTaiKhoanLienQuan"));
                    log.setHanhDong(result.getString("HanhDong"));
                    log.setSoTienThayDoi(result.getBigDecimal("SoTienThayDoi"));
                    log.setSoDuSauThayDoi(result.getBigDecimal("SoDuSauThayDoi"));
                    log.setTrangThai(result.getString("TrangThai"));
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
        try (Connection connection = DBConnection.getConnection();
             CallableStatement statement = connection.prepareCall("{call dbo.sp_CSKH_TenChuTaiKhoan(?)}")) {
            statement.setString(1, soTaiKhoan);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? result.getString("HoTen") : "";
            }
        }
    }

    private void setDateOrNull(CallableStatement statement, int index, LocalDate value) throws SQLException {
        if (value == null) statement.setNull(index, java.sql.Types.DATE);
        else statement.setDate(index, Date.valueOf(value));
    }
}
