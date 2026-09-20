package org.example.appnganhang.dao.Impl;

import org.example.appnganhang.dao.GiaoDichDAO;
import org.example.appnganhang.modell.GiaoDichChuyenTien;
import org.example.appnganhang.modell.LichSuGiaoDich;
import org.example.appnganhang.util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GiaoDichDAOImpl implements GiaoDichDAO {
    private static final String SQL_TRA_CUU_THEO_MA = """
            SELECT MaGiaoDich, MaTaiKhoanNguon, MaTaiKhoanDich,
                   SoTien, NgayGiaoDich, NoiDung, MaLoaiGD, TrangThai
            FROM GiaoDichChuyenTien
            WHERE MaGiaoDich = ?
            """;

    private static final String SQL_TRA_CUU_THEO_TAI_KHOAN = """
            SELECT MaGiaoDich, MaTaiKhoanNguon, MaTaiKhoanDich,
                   SoTien, NgayGiaoDich, NoiDung, MaLoaiGD, TrangThai
            FROM GiaoDichChuyenTien
            WHERE MaTaiKhoanNguon = ?
               OR MaTaiKhoanDich = ?
            ORDER BY NgayGiaoDich DESC
            """;

    private static final String SQL_TRA_CUU_THEO_KHOANG_THOI_GIAN = """
            SELECT MaGiaoDich, MaTaiKhoanNguon, MaTaiKhoanDich,
                   SoTien, NgayGiaoDich, NoiDung, MaLoaiGD, TrangThai
            FROM GiaoDichChuyenTien
            WHERE NgayGiaoDich BETWEEN ? AND ?
            ORDER BY NgayGiaoDich DESC
            """;

    private static final String SQL_TRA_CUU_THEO_TRANG_THAI = """
            SELECT MaGiaoDich, MaTaiKhoanNguon, MaTaiKhoanDich,
                   SoTien, NgayGiaoDich, NoiDung, MaLoaiGD, TrangThai
            FROM GiaoDichChuyenTien
            WHERE TrangThai = ?
            ORDER BY NgayGiaoDich DESC
            """;

    private static final String SQL_TRA_CUU_THEO_LOAI = """
            SELECT MaGiaoDich, MaTaiKhoanNguon, MaTaiKhoanDich,
                   SoTien, NgayGiaoDich, NoiDung, MaLoaiGD, TrangThai
            FROM GiaoDichChuyenTien
            WHERE MaLoaiGD = ?
            ORDER BY NgayGiaoDich DESC
            """;

    private static final String SQL_XEM_LICH_SU_GIAO_DICH = """
            SELECT MaLog, MaGiaoDich, SoTaiKhoanLienQuan,
                   HanhDong, SoTienThayDoi, SoDuSauThayDoi,
                   ThoiGianGhiLog
            FROM LichSuGiaoDich
            WHERE SoTaiKhoanLienQuan = ?
            ORDER BY ThoiGianGhiLog DESC
            """;

    @Override
    public Optional<GiaoDichChuyenTien> traCuuTheoMa(String maGiaoDich) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_TRA_CUU_THEO_MA)) {

            ps.setString(1, maGiaoDich);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    GiaoDichChuyenTien giaoDich = new GiaoDichChuyenTien();

                    giaoDich.setMaGiaoDich(rs.getString("MaGiaoDich"));
                    giaoDich.setMaTaiKhoanNguon(rs.getString("MaTaiKhoanNguon"));
                    giaoDich.setMaTaiKhoanDich(rs.getString("MaTaiKhoanDich"));
                    giaoDich.setSoTien(rs.getBigDecimal("SoTien"));
                    giaoDich.setNgayGiaoDich(rs.getTimestamp("NgayGiaoDich").toLocalDateTime());
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
    public List<GiaoDichChuyenTien> traCuuTheoTaiKhoan(String maTaiKhoan) {
        List<GiaoDichChuyenTien> danhSach = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_TRA_CUU_THEO_TAI_KHOAN)) {

            ps.setString(1, maTaiKhoan);
            ps.setString(2, maTaiKhoan);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    GiaoDichChuyenTien giaoDich = new GiaoDichChuyenTien();

                    giaoDich.setMaGiaoDich(rs.getString("MaGiaoDich"));
                    giaoDich.setMaTaiKhoanNguon(rs.getString("MaTaiKhoanNguon"));
                    giaoDich.setMaTaiKhoanDich(rs.getString("MaTaiKhoanDich"));
                    giaoDich.setSoTien(rs.getBigDecimal("SoTien"));
                    giaoDich.setNgayGiaoDich(rs.getTimestamp("NgayGiaoDich").toLocalDateTime());
                    giaoDich.setNoiDung(rs.getString("NoiDung"));
                    giaoDich.setMaLoaiGD(rs.getString("MaLoaiGD"));
                    giaoDich.setTrangThai(rs.getString("TrangThai"));

                    danhSach.add(giaoDich);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return danhSach;
    }

    @Override
    public List<GiaoDichChuyenTien> traCuuTheoKhoangThoiGian(LocalDateTime tuThoiGian, LocalDateTime denThoiGian) {
        List<GiaoDichChuyenTien> danhSach = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_TRA_CUU_THEO_KHOANG_THOI_GIAN)) {

            ps.setTimestamp(1, Timestamp.valueOf(tuThoiGian));
            ps.setTimestamp(2, Timestamp.valueOf(denThoiGian));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    GiaoDichChuyenTien giaoDich = new GiaoDichChuyenTien();

                    giaoDich.setMaGiaoDich(rs.getString("MaGiaoDich"));
                    giaoDich.setMaTaiKhoanNguon(rs.getString("MaTaiKhoanNguon"));
                    giaoDich.setMaTaiKhoanDich(rs.getString("MaTaiKhoanDich"));
                    giaoDich.setSoTien(rs.getBigDecimal("SoTien"));
                    giaoDich.setNgayGiaoDich(rs.getTimestamp("NgayGiaoDich").toLocalDateTime());
                    giaoDich.setNoiDung(rs.getString("NoiDung"));
                    giaoDich.setMaLoaiGD(rs.getString("MaLoaiGD"));
                    giaoDich.setTrangThai(rs.getString("TrangThai"));

                    danhSach.add(giaoDich);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return danhSach;
    }

    @Override
    public List<GiaoDichChuyenTien> traCuuTheoTrangThai(String trangThai) {
        List<GiaoDichChuyenTien> danhSach = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_TRA_CUU_THEO_TRANG_THAI)) {

            ps.setString(1, trangThai);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    GiaoDichChuyenTien giaoDich = new GiaoDichChuyenTien();

                    giaoDich.setMaGiaoDich(rs.getString("MaGiaoDich"));
                    giaoDich.setMaTaiKhoanNguon(rs.getString("MaTaiKhoanNguon"));
                    giaoDich.setMaTaiKhoanDich(rs.getString("MaTaiKhoanDich"));
                    giaoDich.setSoTien(rs.getBigDecimal("SoTien"));
                    giaoDich.setNgayGiaoDich(rs.getTimestamp("NgayGiaoDich").toLocalDateTime());
                    giaoDich.setNoiDung(rs.getString("NoiDung"));
                    giaoDich.setMaLoaiGD(rs.getString("MaLoaiGD"));
                    giaoDich.setTrangThai(rs.getString("TrangThai"));

                    danhSach.add(giaoDich);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return danhSach;
    }

    @Override
    public List<GiaoDichChuyenTien> traCuuTheoLoai(String maLoaiGD) {
        List<GiaoDichChuyenTien> danhSach = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_TRA_CUU_THEO_LOAI)) {

            ps.setString(1, maLoaiGD);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    GiaoDichChuyenTien giaoDich = new GiaoDichChuyenTien();

                    giaoDich.setMaGiaoDich(rs.getString("MaGiaoDich"));
                    giaoDich.setMaTaiKhoanNguon(rs.getString("MaTaiKhoanNguon"));
                    giaoDich.setMaTaiKhoanDich(rs.getString("MaTaiKhoanDich"));
                    giaoDich.setSoTien(rs.getBigDecimal("SoTien"));
                    giaoDich.setNgayGiaoDich(rs.getTimestamp("NgayGiaoDich").toLocalDateTime());
                    giaoDich.setNoiDung(rs.getString("NoiDung"));
                    giaoDich.setMaLoaiGD(rs.getString("MaLoaiGD"));
                    giaoDich.setTrangThai(rs.getString("TrangThai"));

                    danhSach.add(giaoDich);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return danhSach;
    }

    @Override
    public List<LichSuGiaoDich> xemLichSuGiaoDich(String maTaiKhoan) {
        List<LichSuGiaoDich> danhSach = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_XEM_LICH_SU_GIAO_DICH)) {

            ps.setString(1, maTaiKhoan);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LichSuGiaoDich lichSu = new LichSuGiaoDich();

                    lichSu.setMaLog(rs.getInt("MaLog"));
                    lichSu.setMaGiaoDich(rs.getString("MaGiaoDich"));
                    lichSu.setSoTaiKhoanLienQuan(rs.getString("SoTaiKhoanLienQuan"));
                    lichSu.setHanhDong(rs.getString("HanhDong"));
                    lichSu.setSoTienThayDoi(rs.getBigDecimal("SoTienThayDoi"));
                    lichSu.setSoDuSauThayDoi(rs.getBigDecimal("SoDuSauThayDoi"));
                    lichSu.setThoiGianGhiLog(
                            rs.getTimestamp("ThoiGianGhiLog").toLocalDateTime()
                    );

                    danhSach.add(lichSu);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return danhSach;
    }
}
