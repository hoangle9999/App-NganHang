package org.example.appnganhang.model.dao.Impl;

import org.example.appnganhang.model.dao.intf.TellerTransferDAO;
import org.example.appnganhang.model.entity.LoaiGiaoDich;
import org.example.appnganhang.model.entity.NganHang;
import org.example.appnganhang.model.entity.TellerTransferRecipientInfo;
import org.example.appnganhang.model.entity.TellerTransferResult;
import org.example.appnganhang.model.entity.TellerTransferSourceInfo;
import org.example.appnganhang.model.util.DBConnection;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TellerTransferDAOImpl implements TellerTransferDAO {
    @Override
    public List<NganHang> layDanhSachNganHangHoatDong() {
        List<NganHang> rows = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{call dbo.SP_GDV_DanhSachNganHang}");
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) rows.add(new NganHang(rs.getString("MaNganHang"), rs.getString("TenNganHang"), "Hoạt động"));
            return rows;
        } catch (SQLException e) { throw new RuntimeException("Không thể tải danh sách ngân hàng: " + e.getMessage(), e); }
    }

    @Override
    public List<LoaiGiaoDich> layDanhSachLoaiGiaoDich() {
        String sql = "SELECT MaLoaiGD, TenLoaiGD FROM dbo.LoaiGiaoDich ORDER BY MaLoaiGD";
        List<LoaiGiaoDich> rows = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                LoaiGiaoDich loai = new LoaiGiaoDich();
                loai.setMaLoaiGD(rs.getString("MaLoaiGD"));
                loai.setTenLoaiGD(rs.getString("TenLoaiGD"));
                rows.add(loai);
            }
            return rows;
        } catch (SQLException e) { throw new RuntimeException("Không thể tải danh sách loại giao dịch: " + e.getMessage(), e); }
    }

    @Override
    public Optional<TellerTransferSourceInfo> traCuuTaiKhoanNguon(String soTaiKhoan) {
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{call dbo.SP_GDV_KiemTraTaiKhoanNguon(?)}")) {
            cs.setString(1, soTaiKhoan);
            try (ResultSet rs = cs.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                TellerTransferSourceInfo source = new TellerTransferSourceInfo();
                source.setMaTaiKhoan(rs.getString("MaTaiKhoan"));
                source.setSoTaiKhoan(rs.getString("SoTaiKhoan"));
                source.setSoDu(rs.getBigDecimal("SoDu"));
                source.setTrangThai(rs.getString("TrangThai"));
                source.setHanMucNgay(rs.getBigDecimal("HanMucNgay"));
                source.setDaChuyenHomNay(rs.getBigDecimal("DaChuyenHomNay"));
                source.setHanMucConLai(rs.getBigDecimal("HanMucConLai"));
                source.setTenChuTaiKhoan(rs.getString("HoTen"));
                source.setSoDienThoai(rs.getString("SoDienThoai"));
                return Optional.of(source);
            }
        } catch (SQLException e) { throw new RuntimeException("Không thể tra cứu tài khoản nguồn: " + e.getMessage(), e); }
    }

    @Override
    public TellerTransferRecipientInfo traCuuTaiKhoanDich(String maNganHang, String soTaiKhoan) {
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{call dbo.SP_GDV_TimNguoiNhan(?, ?)}")) {
            cs.setString(1, maNganHang);
            cs.setString(2, soTaiKhoan);
            try (ResultSet rs = cs.executeQuery()) {
                if (!rs.next()) throw new IllegalArgumentException("Không tìm thấy thông tin người nhận.");
                TellerTransferRecipientInfo recipient = new TellerTransferRecipientInfo();
                recipient.setMaNganHang(rs.getString("MaNganHang"));
                recipient.setTenNganHang(rs.getString("TenNganHang"));
                recipient.setSoTaiKhoan(rs.getString("SoTaiKhoan"));
                recipient.setTenNguoiNhan(rs.getString("TenNguoiNhan"));
                recipient.setTrangThaiTaiKhoan(rs.getString("TrangThaiTaiKhoan"));
                recipient.setTaiKhoanNoiBo(rs.getBoolean("DaXacDinhNoiBo"));
                recipient.setThongBao(rs.getString("ThongBao"));
                return recipient;
            }
        } catch (SQLException e) { throw new RuntimeException("Không thể tra cứu người nhận: " + e.getMessage(), e); }
    }

    @Override
    public TellerTransferResult thucHienChuyenTien(String maGiaoDich, String soTaiKhoanNguon, String maNganHangDich,
                                                   String soTaiKhoanDich, String tenNguoiNhan, String maLoaiGD,
                                                   BigDecimal soTien, String noiDung) {
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{call dbo.SP_GDV_ThucHienChuyenTien(?, ?, ?, ?, ?, ?, ?, ?)}")) {
            cs.setString(1, maGiaoDich);
            cs.setString(2, soTaiKhoanNguon);
            cs.setString(3, maNganHangDich);
            cs.setString(4, soTaiKhoanDich);
            cs.setBigDecimal(5, soTien);
            cs.setString(6, maLoaiGD);
            if (tenNguoiNhan == null) cs.setNull(7, Types.NVARCHAR); else cs.setNString(7, tenNguoiNhan);
            if (noiDung == null) cs.setNull(8, Types.NVARCHAR); else cs.setNString(8, noiDung);
            try (ResultSet rs = cs.executeQuery()) {
                if (!rs.next()) throw new RuntimeException("Stored procedure không trả về kết quả giao dịch.");
                TellerTransferResult result = new TellerTransferResult();
                result.setMaGiaoDich(rs.getString("MaGiaoDich"));
                result.setTrangThai(rs.getString("TrangThai"));
                result.setSoDuNguonSauGiaoDich(rs.getBigDecimal("SoDuNguonSauGiaoDich"));
                result.setHanMucConLai(rs.getBigDecimal("HanMucConLai"));
                return result;
            }
        } catch (SQLException e) { throw new RuntimeException("Không thể thực hiện giao dịch: " + e.getMessage(), e); }
    }
}
