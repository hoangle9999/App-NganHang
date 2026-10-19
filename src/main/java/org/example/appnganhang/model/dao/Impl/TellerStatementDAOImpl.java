package org.example.appnganhang.model.dao.Impl;

import org.example.appnganhang.model.dao.intf.TellerStatementDAO;
import org.example.appnganhang.model.entity.LoaiGiaoDich;
import org.example.appnganhang.model.entity.TellerStatementAccount;
import org.example.appnganhang.model.entity.TellerStatementEntry;
import org.example.appnganhang.model.util.DBConnection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TellerStatementDAOImpl implements TellerStatementDAO {
    @Override
    public Optional<TellerStatementAccount> traCuuTaiKhoan(String soTaiKhoan) {
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{call dbo.SP_GDV_TraCuuTaiKhoan(?)}")) {
            cs.setString(1, soTaiKhoan);
            try (ResultSet rs = cs.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                TellerStatementAccount account = new TellerStatementAccount();
                account.setSoTaiKhoan(rs.getString("SoTaiKhoan"));
                account.setTenKhachHang(rs.getString("HoTen"));
                account.setSoDuHienTai(rs.getBigDecimal("SoDu"));
                return Optional.of(account);
            }
        } catch (SQLException e) { throw new RuntimeException("Không thể tra cứu tài khoản sao kê: " + e.getMessage(), e); }
    }

    @Override
    public List<TellerStatementEntry> layLichSu(String soTaiKhoan) {
        List<TellerStatementEntry> entries = new ArrayList<>();
        String sql = "SELECT MaGiaoDich, SoTaiKhoanLienQuan, MaLoaiGD, TenLoaiGD, SoTienThayDoi, " +
                "SoDuSauThayDoi, NoiDung, ThoiGianGhiLog FROM dbo.vw_GDV_SaoKe " +
                "WHERE SoTaiKhoanLienQuan = ? ORDER BY ThoiGianGhiLog, MaLog";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, soTaiKhoan);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TellerStatementEntry entry = new TellerStatementEntry();
                    entry.setMaGiaoDich(rs.getString("MaGiaoDich"));
                    entry.setSoTaiKhoanLienQuan(rs.getString("SoTaiKhoanLienQuan"));
                    entry.setMaLoaiGD(rs.getString("MaLoaiGD"));
                    entry.setTenLoaiGD(rs.getString("TenLoaiGD"));
                    entry.setSoTienThayDoi(rs.getBigDecimal("SoTienThayDoi"));
                    entry.setSoDuSauThayDoi(rs.getBigDecimal("SoDuSauThayDoi"));
                    entry.setNoiDung(rs.getString("NoiDung"));
                    java.sql.Timestamp timestamp = rs.getTimestamp("ThoiGianGhiLog");
                    entry.setThoiGian(timestamp == null ? null : timestamp.toLocalDateTime());
                    entries.add(entry);
                }
            }
            return entries;
        } catch (SQLException e) { throw new RuntimeException("Không thể tải lịch sử giao dịch: " + e.getMessage(), e); }
    }

    @Override
    public List<LoaiGiaoDich> layDanhSachLoaiGiaoDich() {
        List<LoaiGiaoDich> types = new ArrayList<>();
        String sql = "SELECT MaLoaiGD, TenLoaiGD FROM dbo.LoaiGiaoDich ORDER BY MaLoaiGD";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                LoaiGiaoDich type = new LoaiGiaoDich();
                type.setMaLoaiGD(rs.getString("MaLoaiGD"));
                type.setTenLoaiGD(rs.getString("TenLoaiGD"));
                types.add(type);
            }
            return types;
        } catch (SQLException e) { throw new RuntimeException("Không thể tải loại giao dịch: " + e.getMessage(), e); }
    }
}
