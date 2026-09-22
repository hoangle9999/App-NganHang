package org.example.appnganhang.model.dao.Impl;

import org.example.appnganhang.model.dao.BaoCaoDAO;
import org.example.appnganhang.model.util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BaoCaoDAOImpl implements BaoCaoDAO {
    private static final String SQL_DEM_TONG_SO_GIAO_DICH = """
            SELECT COUNT(*)
            FROM GiaoDichChuyenTien
            """;

    private static final String SQL_TINH_TONG_TIEN_CHUYEN = """
            SELECT COALESCE(SUM(SoTien), 0)
            FROM GiaoDichChuyenTien
            """;

    private static final String SQL_DEM_GIAO_DICH_THANH_CONG = """
            SELECT COUNT(*)
            FROM GiaoDichChuyenTien
            WHERE TrangThai = N'Thành công'
            """;

    private static final String SQL_DEM_GIAO_DICH_THAT_BAI = """
            SELECT COUNT(*)
            FROM GiaoDichChuyenTien
            WHERE TrangThai = N'Thất bại'
            """;

    private static final String SQL_THONG_KE_GIAO_DICH_THEO_NGAY = """
            SELECT CAST(NgayGiaoDich AS DATE) AS Ngay,
                   COUNT(*) AS SoLuong
            FROM GiaoDichChuyenTien
            GROUP BY CAST(NgayGiaoDich AS DATE)
            ORDER BY Ngay
            """;

    private static final String SQL_THONG_KE_GIAO_DICH_THEO_LOAI = """
            SELECT LGD.TenLoaiGD,
                   COUNT(GD.MaGiaoDich) AS SoLuong
            FROM GiaoDichChuyenTien GD
            INNER JOIN LoaiGiaoDich LGD
                ON GD.MaLoaiGD = LGD.MaLoaiGD
            GROUP BY LGD.TenLoaiGD
            ORDER BY LGD.TenLoaiGD
            """;

    private static final String SQL_THONG_KE_GIAO_DICH_THEO_CHI_NHANH = """
            SELECT CN.TenChiNhanh,
                   COUNT(GD.MaGiaoDich) AS SoLuong
            FROM GiaoDichChuyenTien GD
            INNER JOIN TaiKhoanNganHang TK
                ON GD.MaTaiKhoanNguon = TK.MaTaiKhoan
            INNER JOIN ChiNhanh CN
                ON TK.MaChiNhanh = CN.MaChiNhanh
            GROUP BY CN.TenChiNhanh
            ORDER BY CN.TenChiNhanh
            """;

    private static final String SQL_BAO_CAO_GIAO_DICH = """
            SELECT GD.MaGiaoDich,
                   GD.MaTaiKhoanNguon,
                   TK_Nguon.SoTaiKhoan AS SoTaiKhoanNguon,
                   GD.MaTaiKhoanDich,
                   TK_Dich.SoTaiKhoan AS SoTaiKhoanDich,
                   GD.SoTien,
                   GD.NgayGiaoDich,
                   GD.NoiDung,
                   LGD.TenLoaiGD,
                   GD.TrangThai,
                   CN.TenChiNhanh
            FROM GiaoDichChuyenTien GD
            INNER JOIN TaiKhoanNganHang TK_Nguon
                ON GD.MaTaiKhoanNguon = TK_Nguon.MaTaiKhoan
            INNER JOIN TaiKhoanNganHang TK_Dich
                ON GD.MaTaiKhoanDich = TK_Dich.MaTaiKhoan
            INNER JOIN LoaiGiaoDich LGD
                ON GD.MaLoaiGD = LGD.MaLoaiGD
            INNER JOIN ChiNhanh CN
                ON TK_Nguon.MaChiNhanh = CN.MaChiNhanh
            ORDER BY GD.NgayGiaoDich DESC
            """;

    @Override
    public int demTongSoGiaoDich() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_DEM_TONG_SO_GIAO_DICH);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public BigDecimal tinhTongTienChuyen() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_TINH_TONG_TIEN_CHUYEN);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getBigDecimal(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    @Override
    public int demGiaoDichThanhCong() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_DEM_GIAO_DICH_THANH_CONG);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int demGiaoDichThatBai() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_DEM_GIAO_DICH_THAT_BAI);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public Map<LocalDate, Integer> thongKeGiaoDichTheoNgay() {
        Map<LocalDate, Integer> ketQua = new LinkedHashMap<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_THONG_KE_GIAO_DICH_THEO_NGAY);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                LocalDate ngay = rs.getDate("Ngay").toLocalDate();
                int soLuong = rs.getInt("SoLuong");

                ketQua.put(ngay, soLuong);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ketQua;
    }

    @Override
    public Map<String, Integer> thongKeGiaoDichTheoLoai() {
        Map<String, Integer> ketQua = new LinkedHashMap<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_THONG_KE_GIAO_DICH_THEO_LOAI);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String tenLoaiGD = rs.getString("TenLoaiGD");
                int soLuong = rs.getInt("SoLuong");

                ketQua.put(tenLoaiGD, soLuong);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ketQua;
    }

    @Override
    public Map<String, Integer> thongKeGiaoDichTheoChiNhanh() {
        Map<String, Integer> ketQua = new LinkedHashMap<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_THONG_KE_GIAO_DICH_THEO_CHI_NHANH);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String tenChiNhanh = rs.getString("TenChiNhanh");
                int soLuong = rs.getInt("SoLuong");

                ketQua.put(tenChiNhanh, soLuong);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ketQua;
    }

    @Override
    public List<Map<String, Object>> baoCaoGiaoDich() {
        List<Map<String, Object>> danhSach = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_BAO_CAO_GIAO_DICH);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();

                row.put("MaGiaoDich", rs.getString("MaGiaoDich"));
                row.put("MaTaiKhoanNguon", rs.getString("MaTaiKhoanNguon"));
                row.put("SoTaiKhoanNguon", rs.getString("SoTaiKhoanNguon"));
                row.put("MaTaiKhoanDich", rs.getString("MaTaiKhoanDich"));
                row.put("SoTaiKhoanDich", rs.getString("SoTaiKhoanDich"));
                row.put("SoTien", rs.getBigDecimal("SoTien"));
                row.put("NgayGiaoDich", rs.getTimestamp("NgayGiaoDich"));
                row.put("NoiDung", rs.getString("NoiDung"));
                row.put("TenLoaiGD", rs.getString("TenLoaiGD"));
                row.put("TrangThai", rs.getString("TrangThai"));
                row.put("TenChiNhanh", rs.getString("TenChiNhanh"));

                danhSach.add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }
}
