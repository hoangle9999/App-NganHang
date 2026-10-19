package org.example.appnganhang.model.dao.Impl;

import org.example.appnganhang.model.dao.intf.TellerCashDAO;
import org.example.appnganhang.model.entity.NganHang;
import org.example.appnganhang.model.entity.TellerCashAccountInfo;
import org.example.appnganhang.model.entity.TellerCashResult;
import org.example.appnganhang.model.util.DBConnection;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TellerCashDAOImpl implements TellerCashDAO {
    @Override
    public List<NganHang> layDanhSachNganHangNoiBo() {
        List<NganHang> banks = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{call dbo.SP_GDV_DanhSachNganHang}" );
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                if (!"SMART".equalsIgnoreCase(rs.getString("MaNganHang"))) continue;
                NganHang bank = new NganHang();
                bank.setMaNganHang(rs.getString("MaNganHang"));
                bank.setTenNganHang(rs.getString("TenNganHang"));
                bank.setTrangThai(rs.getString("TrangThai"));
                banks.add(bank);
            }
            return banks;
        } catch (SQLException e) { throw new RuntimeException("Không thể tải ngân hàng nội bộ: " + e.getMessage(), e); }
    }

    @Override
    public Optional<TellerCashAccountInfo> traCuuTaiKhoan(String soTaiKhoan) {
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{call dbo.SP_GDV_TraCuuTaiKhoan(?)}")) {
            cs.setString(1, soTaiKhoan);
            try (ResultSet rs = cs.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                TellerCashAccountInfo account = new TellerCashAccountInfo();
                account.setMaTaiKhoan(rs.getString("MaTaiKhoan"));
                account.setSoTaiKhoan(rs.getString("SoTaiKhoan"));
                account.setTenKhachHang(rs.getString("HoTen"));
                account.setSoDu(rs.getBigDecimal("SoDu"));
                account.setTrangThai(rs.getString("TrangThai"));
                account.setTenChiNhanh(rs.getString("TenChiNhanh"));
                return Optional.of(account);
            }
        } catch (SQLException e) { throw new RuntimeException("Không thể tra cứu tài khoản: " + e.getMessage(), e); }
    }

    @Override
    public TellerCashResult thucHienGiaoDich(String maGiaoDich, String soTaiKhoan, String maLoaiGD,
                                              BigDecimal soTien, String noiDung) {
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{call dbo.SP_GDV_ThucHienGiaoDich(?, ?, ?, ?, ?)}")) {
            cs.setString(1, maGiaoDich);
            cs.setString(2, soTaiKhoan);
            cs.setString(3, maLoaiGD);
            cs.setBigDecimal(4, soTien);
            if (noiDung == null || noiDung.isBlank()) cs.setNull(5, Types.NVARCHAR);
            else cs.setNString(5, noiDung);
            try (ResultSet rs = cs.executeQuery()) {
                if (!rs.next()) throw new RuntimeException("Stored procedure không trả về kết quả giao dịch.");
                TellerCashResult result = new TellerCashResult();
                result.setMaGiaoDich(rs.getString("MaGiaoDich"));
                result.setSoDuSauGiaoDich(rs.getBigDecimal("SoDuSauGiaoDich"));
                result.setTrangThai(rs.getString("TrangThai"));
                return result;
            }
        } catch (SQLException e) { throw new RuntimeException("Không thể thực hiện giao dịch tại quầy: " + e.getMessage(), e); }
    }
}
