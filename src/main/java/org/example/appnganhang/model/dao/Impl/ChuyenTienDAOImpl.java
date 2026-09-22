package org.example.appnganhang.model.dao.Impl;

import org.example.appnganhang.model.dao.ChuyenTienDAO;
import org.example.appnganhang.model.GiaoDichChuyenTien;
import org.example.appnganhang.model.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;

public class ChuyenTienDAOImpl implements ChuyenTienDAO {
    private static final String SQL_THEM_GIAO_DICH = """
            INSERT INTO GiaoDichChuyenTien
            (
                MaGiaoDich,
                MaTaiKhoanNguon,
                MaTaiKhoanDich,
                SoTien,
                NgayGiaoDich,
                NoiDung,
                MaLoaiGD,
                TrangThai
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

    private static final String SQL_TIM_GIAO_DICH_THEO_MA = """
            SELECT MaGiaoDich,
                   MaTaiKhoanNguon,
                   MaTaiKhoanDich,
                   SoTien,
                   NgayGiaoDich,
                   NoiDung,
                   MaLoaiGD,
                   TrangThai
            FROM GiaoDichChuyenTien
            WHERE MaGiaoDich = ?
            """;

    private static final String SQL_LAY_SO_DU_TAI_KHOAN = """
            SELECT SoDu
            FROM TaiKhoanNganHang
            WHERE MaTaiKhoan = ?
            """;

    private static final String SQL_LAY_TRANG_THAI_TAI_KHOAN = """
            SELECT TrangThai
            FROM TaiKhoanNganHang
            WHERE MaTaiKhoan = ?
            """;

    private static final String SQL_CAP_NHAT_SO_DU_TAI_KHOAN = """
            UPDATE TaiKhoanNganHang
            SET SoDu = ?
            WHERE MaTaiKhoan = ?
            """;

    private static final String SQL_LAY_HAN_MUC_NGAY = """
            SELECT h.HanMucNgay
            FROM TaiKhoanNganHang t
            JOIN HanMucGiaoDịch h
                ON t.MaHanMuc = h.MaHanMuc
            WHERE t.MaTaiKhoan = ?
            """;

    private static final String SQL_LAY_TONG_TIEN_GIAO_DICH_TRONG_NGAY = """
            SELECT COALESCE(SUM(SoTien), 0)
            FROM GiaoDichChuyenTien
            WHERE MaTaiKhoanNguon = ?
              AND CAST(NgayGiaoDich AS DATE) = CAST(GETDATE() AS DATE)
              AND TrangThai = N'Thành công'
            """;

    private static final String SQL_THEM_LICH_SU_GIAO_DICH = """
            INSERT INTO LichSuGiaoDich
            (
                MaGiaoDich,
                SoTaiKhoanLienQuan,
                HanhDong,
                SoTienThayDoi,
                SoDuSauThayDoi
            )
            VALUES (?, ?, ?, ?, ?)
            """;
    private static final String SQL_CAP_NHAT_TRANG_THAI = """
                UPDATE GiaoDichChuyenTien
                SET TrangThai = ?
                WHERE MaGiaoDich = ?
                """;
    @Override
    public BigDecimal laySoDu(String maTaiKhoan) {
        try(Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_LAY_SO_DU_TAI_KHOAN)){

                ps.setString(1, maTaiKhoan);
                try(ResultSet rs = ps.executeQuery()) {
                    if (rs != null){
                        return rs.getBigDecimal("SoDu");
                    }
                }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String layTrangThaiTaiKhoan(String maTaiKhoan) {
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_LAY_TRANG_THAI_TAI_KHOAN)) {

            ps.setString(1, maTaiKhoan);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("TrangThai");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public BigDecimal layHanMucNgay(String maTaiKhoan) {
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_LAY_HAN_MUC_NGAY)) {

                    ps.setString(1, maTaiKhoan);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("HanMucNgay");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public BigDecimal layTongTienGiaoDichTrongNgay(String maTaiKhoan) {
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_LAY_TONG_TIEN_GIAO_DICH_TRONG_NGAY)) {

            ps.setString(1, maTaiKhoan);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    @Override
    public boolean capNhatSoDu(String maTaiKhoan, BigDecimal soDu) {
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_CAP_NHAT_SO_DU_TAI_KHOAN)) {

            ps.setBigDecimal(1, soDu);
            ps.setString(2, maTaiKhoan);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean themGiaoDich(GiaoDichChuyenTien giaoDich) {
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_THEM_GIAO_DICH)) {

            ps.setString(1, giaoDich.getMaGiaoDich());
            ps.setString(2, giaoDich.getMaTaiKhoanNguon());
            ps.setString(3, giaoDich.getMaTaiKhoanDich());
            ps.setBigDecimal(4, giaoDich.getSoTien());

            if (giaoDich.getNgayGiaoDich() != null) {
                ps.setTimestamp(5, Timestamp.valueOf(giaoDich.getNgayGiaoDich()));
            } else {
                ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            }

            ps.setString(6, giaoDich.getNoiDung());
            ps.setString(7, giaoDich.getMaLoaiGD());
            ps.setString(8, giaoDich.getTrangThai());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean capNhatTrangThaiGiaoDich(String maGiaoDich, String trangThai) {
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_CAP_NHAT_TRANG_THAI)) {
            ps.setString(1, trangThai);
            ps.setString(2, maGiaoDich);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Optional<GiaoDichChuyenTien> timTheoMa(String maGiaoDich) {
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_TIM_GIAO_DICH_THEO_MA)) {

            ps.setString(1, maGiaoDich);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    GiaoDichChuyenTien giaoDich = new GiaoDichChuyenTien();

                    giaoDich.setMaGiaoDich(rs.getString("MaGiaoDich"));
                    giaoDich.setMaTaiKhoanNguon(rs.getString("MaTaiKhoanNguon"));
                    giaoDich.setMaTaiKhoanDich(rs.getString("MaTaiKhoanDich"));
                    giaoDich.setSoTien(rs.getBigDecimal("SoTien"));
                    Timestamp timestamp = rs.getTimestamp("NgayGiaoDich");

                    if (timestamp != null) {
                        giaoDich.setNgayGiaoDich(timestamp.toLocalDateTime());
                    }

                    giaoDich.setNoiDung(rs.getString("NoiDung"));
                    giaoDich.setMaLoaiGD(rs.getString("MaLoaiGD"));
                    giaoDich.setTrangThai(rs.getString("TrangThai"));

                    return Optional.of(giaoDich);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public boolean themLichSuGiaoDich(String maGiaoDich, String soTaiKhoan, String hanhDong, BigDecimal soTienThayDoi, BigDecimal soDuSauThayDoi) {
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_THEM_LICH_SU_GIAO_DICH)) {

            ps.setString(1, maGiaoDich);
            ps.setString(2, soTaiKhoan);
            ps.setString(3, hanhDong);
            ps.setBigDecimal(4, soTienThayDoi);
            ps.setBigDecimal(5, soDuSauThayDoi);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
