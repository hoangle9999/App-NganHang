package org.example.appnganhang.model.dao.Impl;

import org.example.appnganhang.model.dao.intf.GiaoDichDAO;
import org.example.appnganhang.model.entity.GiaoDichChuyenTien;
import org.example.appnganhang.model.entity.LichSuGiaoDich;
import org.example.appnganhang.model.entity.LoaiGiaoDich;
import org.example.appnganhang.model.util.DBConnection;

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
            SELECT ls.MaLog, ls.MaGiaoDich, ls.SoTaiKhoanLienQuan,
                   ls.HanhDong, ls.SoTienThayDoi, ls.SoDuSauThayDoi,
                   gd.TrangThai, ls.ThoiGianGhiLog
            FROM dbo.LichSuGiaoDich ls
            INNER JOIN dbo.GiaoDichChuyenTien gd
                ON ls.MaGiaoDich = gd.MaGiaoDich
            WHERE ls.SoTaiKhoanLienQuan = ?
            ORDER BY ls.ThoiGianGhiLog DESC
            """;

    private static final String SQL_GET_TAT_CA_GIAO_DICH = """
        SELECT MaGiaoDich,
               MaTaiKhoanNguon,
               MaTaiKhoanDich,
               SoTien,
               NgayGiaoDich,
               NoiDung,
               MaLoaiGD,
               TrangThai
        FROM GiaoDichChuyenTien
        ORDER BY NgayGiaoDich DESC
        """;

    private static final String SQL_XEM_TAT_CA_LICH_SU =
            """
            SELECT ls.MaLog,
                   ls.MaGiaoDich,
                   ls.SoTaiKhoanLienQuan,
                   ls.HanhDong,
                   ls.SoTienThayDoi,
                   ls.SoDuSauThayDoi,
                   gd.TrangThai,
                   ls.ThoiGianGhiLog
            FROM dbo.LichSuGiaoDich ls
            INNER JOIN dbo.GiaoDichChuyenTien gd
                ON ls.MaGiaoDich = gd.MaGiaoDich
            ORDER BY ls.ThoiGianGhiLog DESC
            """;

    private static final String SQL_GET_TAT_CA_LOAI_GIAO_DICH = """
        SELECT MaLoaiGD, TenLoaiGD
        FROM dbo.LoaiGiaoDich
        ORDER BY MaLoaiGD
        """;

    private static final String SQL_GET_TAT_CA_TRANG_THAI = """
        SELECT DISTINCT TrangThai
        FROM dbo.GiaoDichChuyenTien
        ORDER BY TrangThai
        """;


    @Override
    public Optional<GiaoDichChuyenTien> traCuuTheoMa(String maGiaoDich) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_TRA_CUU_THEO_MA)) {

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
    public List<GiaoDichChuyenTien> traCuuTheoTaiKhoan(String maTaiKhoan) {
        List<GiaoDichChuyenTien> danhSach = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_TRA_CUU_THEO_TAI_KHOAN)) {

            ps.setString(1, maTaiKhoan);
            ps.setString(2, maTaiKhoan);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(taoGiaoDich(rs));
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
                    danhSach.add(taoGiaoDich(rs));
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
                    danhSach.add(taoGiaoDich(rs));
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
                    danhSach.add(taoGiaoDich(rs));
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
                    lichSu.setTrangThai(rs.getString("TrangThai"));
                    lichSu.setThoiGianGhiLog(rs.getTimestamp("ThoiGianGhiLog").toLocalDateTime());

                    danhSach.add(lichSu);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return danhSach;
    }

    @Override
    public List<LichSuGiaoDich> xemTatCaLichSuGiaoDich() {
        List<LichSuGiaoDich> danhSach = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_XEM_TAT_CA_LICH_SU);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                LichSuGiaoDich lichSu = new LichSuGiaoDich();

                lichSu.setMaLog(rs.getInt("MaLog"));
                lichSu.setMaGiaoDich(rs.getString("MaGiaoDich"));
                lichSu.setSoTaiKhoanLienQuan(
                        rs.getString("SoTaiKhoanLienQuan")
                );
                lichSu.setHanhDong(rs.getString("HanhDong"));
                lichSu.setSoTienThayDoi(
                        rs.getBigDecimal("SoTienThayDoi")
                );
                lichSu.setSoDuSauThayDoi(
                        rs.getBigDecimal("SoDuSauThayDoi")
                );
                lichSu.setTrangThai(rs.getString("TrangThai"));

                Timestamp timestamp = rs.getTimestamp("ThoiGianGhiLog");
                if (timestamp != null) {
                    lichSu.setThoiGianGhiLog(timestamp.toLocalDateTime());
                }

                danhSach.add(lichSu);
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Lỗi khi lấy toàn bộ lịch sử giao dịch.", e
            );
        }

        return danhSach;
    }

    @Override
    public List<GiaoDichChuyenTien> getTatCaGiaoDich() {
        List<GiaoDichChuyenTien> danhSach = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_GET_TAT_CA_GIAO_DICH);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                danhSach.add(taoGiaoDich(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return danhSach;
    }


    @Override
    public List<LoaiGiaoDich> getTatCaLoaiGiaoDich() {
        List<LoaiGiaoDich> danhSach = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     SQL_GET_TAT_CA_LOAI_GIAO_DICH
             );
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                LoaiGiaoDich loai = new LoaiGiaoDich();
                loai.setMaLoaiGD(rs.getString("MaLoaiGD"));
                loai.setTenLoaiGD(rs.getString("TenLoaiGD"));

                danhSach.add(loai);
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Lỗi khi lấy danh sách loại giao dịch.", e
            );
        }

        return danhSach;
    }

    @Override
    public List<String> getTatCaTrangThai() {
        List<String> danhSach = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     SQL_GET_TAT_CA_TRANG_THAI
             );
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                danhSach.add(rs.getString("TrangThai"));
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Lỗi khi lấy danh sách trạng thái giao dịch.", e
            );
        }

        return danhSach;
    }


    private GiaoDichChuyenTien taoGiaoDich(ResultSet rs) throws SQLException{
        GiaoDichChuyenTien giaoDich = new GiaoDichChuyenTien();

        giaoDich.setMaGiaoDich(rs.getString("MaGiaoDich"));
        giaoDich.setMaTaiKhoanNguon(rs.getString("MaTaiKhoanNguon"));
        giaoDich.setMaTaiKhoanDich(rs.getString("MaTaiKhoanDich"));
        giaoDich.setSoTien(rs.getBigDecimal("SoTien"));
        giaoDich.setNgayGiaoDich(rs.getTimestamp("NgayGiaoDich").toLocalDateTime());
        giaoDich.setNoiDung(rs.getString("NoiDung"));
        giaoDich.setMaLoaiGD(rs.getString("MaLoaiGD"));
        giaoDich.setTrangThai(rs.getString("TrangThai"));

        return giaoDich;
    }
}
