package org.example.appnganhang.model.dao.Impl;

import org.example.appnganhang.model.dao.intf.ChuyenTienDAO;
import org.example.appnganhang.model.entity.GiaoDichChuyenTien;
import org.example.appnganhang.model.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

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
               SoTaiKhoanNhanNgoai,
               MaNganHangNhan,
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

    private static final String SQL_LAY_TAI_KHOAN_KHOA = """
            SELECT MaTaiKhoan,
                   SoDu,
                   TrangThai,
                   MaHanMuc
            FROM TaiKhoanNganHang WITH (UPDLOCK, HOLDLOCK)
            WHERE MaTaiKhoan = ?
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

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean themGiaoDich(GiaoDichChuyenTien giaoDich) {
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_THEM_GIAO_DICH)) {

            ganThongTinGiaoDich(ps, giaoDich);

            return ps.executeUpdate() == 1;

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
                    return Optional.of(taoGiaoDich(rs));
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

            ganThongTinLichSu(
                    ps,
                    maGiaoDich,
                    soTaiKhoan,
                    hanhDong,
                    soTienThayDoi,
                    soDuSauThayDoi
            );

            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public boolean thucHienChuyenTien(
            GiaoDichChuyenTien giaoDich,
            String maNganHangNhan
    ) {
        if (giaoDich == null
                || giaoDich.getMaTaiKhoanNguon() == null
                || giaoDich.getMaTaiKhoanDich() == null
                || giaoDich.getSoTien() == null
                || giaoDich.getSoTien().compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        String sql = "{CALL dbo.sp_ChuyenTienNoiBo(?, ?, ?, ?, ?, ?, ?)}";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            // Các giá trị này phải là SoTaiKhoan thực tế,
            // không phải MaTaiKhoan nội bộ.
            cs.setString(1, giaoDich.getMaTaiKhoanNguon());
            cs.setString(2, giaoDich.getMaTaiKhoanDich());
            cs.setString(3, maNganHangNhan);
            cs.setBigDecimal(4, giaoDich.getSoTien());
            cs.setNString(5, giaoDich.getNoiDung());

            cs.registerOutParameter(6, java.sql.Types.BIT);
            cs.registerOutParameter(7, java.sql.Types.NVARCHAR);

            cs.execute();

            boolean ketQua = cs.getBoolean(6);
            String thongBao = cs.getNString(7);

            if (!ketQua) {
                System.err.println("Chuyển tiền thất bại: " + thongBao);
                return false;
            }

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private GiaoDichChuyenTien taoGiaoDich(ResultSet rs)
            throws SQLException {

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

        return giaoDich;
    }

    private void ganThongTinLichSu(
            PreparedStatement ps,
            String maGiaoDich,
            String soTaiKhoan,
            String hanhDong,
            BigDecimal soTienThayDoi,
            BigDecimal soDuSauThayDoi
    ) throws SQLException {

        ps.setString(1, maGiaoDich);
        ps.setString(2, soTaiKhoan);
        ps.setString(3, hanhDong);
        ps.setBigDecimal(4, soTienThayDoi);
        ps.setBigDecimal(5, soDuSauThayDoi);
    }

    private void ganThongTinGiaoDich(
            PreparedStatement ps,
            GiaoDichChuyenTien giaoDich
    ) throws SQLException {

        ps.setString(1, giaoDich.getMaGiaoDich());
        ps.setString(2, giaoDich.getMaTaiKhoanNguon());
        ps.setString(3, giaoDich.getMaTaiKhoanDich());
        ps.setBigDecimal(4, giaoDich.getSoTien());

        LocalDateTime ngayGiaoDich =
                giaoDich.getNgayGiaoDich() != null
                        ? giaoDich.getNgayGiaoDich()
                        : LocalDateTime.now();

        ps.setTimestamp(5, Timestamp.valueOf(ngayGiaoDich));
        ps.setString(6, giaoDich.getNoiDung());
        ps.setString(7, giaoDich.getMaLoaiGD());
        ps.setString(8, giaoDich.getTrangThai());
    }

}
