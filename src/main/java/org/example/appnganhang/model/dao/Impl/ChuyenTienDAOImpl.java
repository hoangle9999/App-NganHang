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
                || giaoDich.getSoTien().compareTo(BigDecimal.ZERO) <= 0
                || giaoDich.getMaTaiKhoanNguon().equals(
                giaoDich.getMaTaiKhoanDich())) {
            return false;
        }

        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false);

            try {
                String maTaiKhoanGui = giaoDich.getMaTaiKhoanNguon();
                String maTaiKhoanNhan = giaoDich.getMaTaiKhoanDich();
                BigDecimal soTien = giaoDich.getSoTien();

                // Khóa tài khoản theo thứ tự cố định để giảm nguy cơ deadlock.
                String taiKhoanDau = maTaiKhoanGui.compareTo(maTaiKhoanNhan) < 0 ? maTaiKhoanGui : maTaiKhoanNhan;
                String taiKhoanSau = maTaiKhoanGui.compareTo(maTaiKhoanNhan) < 0 ? maTaiKhoanNhan : maTaiKhoanGui;

                BigDecimal soDuGui = null;
                BigDecimal soDuNhan = null;
                String maHanMucGui = null;

                for (String maTaiKhoan : new String[]{taiKhoanDau, taiKhoanSau}) {
                    try (PreparedStatement ps = conn.prepareStatement(SQL_LAY_TAI_KHOAN_KHOA)) {

                        ps.setString(1, maTaiKhoan);

                        try (ResultSet rs = ps.executeQuery()) {
                            if (!rs.next()
                                    || !"Hoạt động".equals(
                                    rs.getString("TrangThai"))) {
                                throw new SQLException("Tài khoản không tồn tại hoặc không hoạt động: " + maTaiKhoan);
                            }

                            BigDecimal soDu = rs.getBigDecimal("SoDu");

                            if (maTaiKhoan.equals(maTaiKhoanGui)) {
                                soDuGui = soDu;
                                maHanMucGui = rs.getString("MaHanMuc");
                            } else {
                                soDuNhan = soDu;
                            }
                        }
                    }
                }

                if (soDuGui == null || soDuNhan == null
                        || soDuGui.compareTo(soTien) < 0) {
                    throw new SQLException("Số dư không đủ.");
                }

                // kiểm tra hạn mức
                BigDecimal hanMucNgay;

                try (PreparedStatement ps = conn.prepareStatement(SQL_LAY_HAN_MUC_NGAY)) {

                    ps.setString(1, maTaiKhoanGui);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("Không tìm thấy hạn mức.");
                        }

                        hanMucNgay = rs.getBigDecimal("HanMucNgay");
                    }
                }

                BigDecimal tongTienTrongNgay;

                try (PreparedStatement ps = conn.prepareStatement(SQL_LAY_TONG_TIEN_GIAO_DICH_TRONG_NGAY)) {

                    ps.setString(1, maTaiKhoanGui);

                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        tongTienTrongNgay = rs.getBigDecimal(1);
                    }
                }

                if (hanMucNgay == null
                        || tongTienTrongNgay == null
                        || tongTienTrongNgay.add(soTien)
                        .compareTo(hanMucNgay) > 0) {
                    throw new SQLException(
                            "Vượt hạn mức giao dịch trong ngày.");
                }

                // cập nhật số dư
                BigDecimal soDuGuiMoi = soDuGui.subtract(soTien);
                BigDecimal soDuNhanMoi = soDuNhan.add(soTien);

                capNhatSoDuTrongTransaction(conn, maTaiKhoanGui, soDuGuiMoi);
                capNhatSoDuTrongTransaction(conn, maTaiKhoanNhan, soDuNhanMoi);

                // thêm lịch
                String maGiaoDich = "GD" + UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 20);

                giaoDich.setMaGiaoDich(maGiaoDich);
                giaoDich.setNgayGiaoDich(LocalDateTime.now());
                giaoDich.setTrangThai("Chờ xử lý");

                try (PreparedStatement ps = conn.prepareStatement(SQL_THEM_GIAO_DICH)) {

                    ganThongTinGiaoDich(ps, giaoDich);

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException("Không lưu được giao dịch.");
                    }
                }

                // Lưu giao dịch
                try (PreparedStatement ps = conn.prepareStatement(SQL_THEM_LICH_SU_GIAO_DICH)) {

                    ganThongTinLichSu(
                            ps, maGiaoDich, maTaiKhoanGui,
                            "Chuyển tiền", soTien.negate(), soDuGuiMoi);

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException("Không ghi được lịch sử gửi.");
                    }

                    ganThongTinLichSu(
                            ps, maGiaoDich, maTaiKhoanNhan,
                            "Nhận tiền", soTien, soDuNhanMoi);

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException("Không ghi được lịch sử nhận.");
                    }
                }

                // Chỉ chuyển trạng thái khi toàn bộ bước xử lý đã thành công
                try (PreparedStatement ps =
                             conn.prepareStatement(SQL_CAP_NHAT_TRANG_THAI)) {

                    ps.setString(1, "Thành công");
                    ps.setString(2, maGiaoDich);

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException(
                                "Không cập nhật được trạng thái giao dịch.");
                    }
                }

                conn.commit();
                return true;

            } catch (Exception e) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }

                e.printStackTrace();
                return false;

            } finally {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void capNhatSoDuTrongTransaction(
            Connection conn,
            String maTaiKhoan,
            BigDecimal soDuMoi
    ) throws SQLException {

        try (PreparedStatement ps =
                     conn.prepareStatement(SQL_CAP_NHAT_SO_DU_TAI_KHOAN)) {

            ps.setBigDecimal(1, soDuMoi);
            ps.setString(2, maTaiKhoan);

            if (ps.executeUpdate() != 1) {
                throw new SQLException(
                        "Không cập nhật được số dư tài khoản: " + maTaiKhoan);
            }
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
