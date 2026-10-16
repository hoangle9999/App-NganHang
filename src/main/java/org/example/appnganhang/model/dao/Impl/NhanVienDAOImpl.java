package org.example.appnganhang.model.dao.Impl;

import org.example.appnganhang.model.dao.intf.NhanVienDAO;
import org.example.appnganhang.model.entity.NhanVien;
import org.example.appnganhang.model.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NhanVienDAOImpl implements NhanVienDAO {

    private static final String SQL_THEM_NHAN_VIEN = """
            INSERT INTO dbo.NhanVien (MaNhanVien, HoTen, SoDienThoai, DiaChi, ChucVu, MaChiNhanh, TenDangNhap)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

    private static final String SQL_SUA_NHAN_VIEN = """
            UPDATE NhanVien
            SET HoTen = ?, SoDienThoai = ?, DiaChi = ?, ChucVu = ?, MaChiNhanh = ?, TenDangNhap = ?
            WHERE MaNhanVien = ?
            """;

    private static final String SQL_XOA_NHAN_VIEN = """
            DELETE FROM NhanVien
            WHERE MaNhanVien = ?
            """;

    private static final String SQL_TIM_NHAN_VIEN = """
            SELECT nv.MaNhanVien, nv.HoTen, nv.SoDienThoai, nv.DiaChi,
                   nv.ChucVu, nv.MaChiNhanh, cn.TenChiNhanh, nv.TenDangNhap
            FROM dbo.NhanVien nv
            LEFT JOIN dbo.ChiNhanh cn ON cn.MaChiNhanh = nv.MaChiNhanh
            WHERE nv.MaNhanVien = ?
            """;

    private static final String SQL_GET_DANH_SACH_NHAN_VIEN = """
            SELECT nv.MaNhanVien, nv.HoTen, nv.SoDienThoai, nv.DiaChi,
                   nv.ChucVu, nv.MaChiNhanh, cn.TenChiNhanh, nv.TenDangNhap
            FROM dbo.NhanVien nv
            LEFT JOIN dbo.ChiNhanh cn ON cn.MaChiNhanh = nv.MaChiNhanh
            """;

    private static final String SQL_GET_NHAN_VIEN_THEO_CHI_NHANH = """
            SELECT nv.MaNhanVien, nv.HoTen, nv.SoDienThoai, nv.DiaChi,
                   nv.ChucVu, nv.MaChiNhanh, cn.TenChiNhanh, nv.TenDangNhap
            FROM dbo.NhanVien nv
            LEFT JOIN dbo.ChiNhanh cn ON cn.MaChiNhanh = nv.MaChiNhanh
            WHERE nv.MaChiNhanh = ?
            """;

    private static final String SQL_TIM_MA_CHI_NHANH_THEO_TEN = """
            SELECT MaChiNhanh
            FROM ChiNhanh
            WHERE TenChiNhanh = ?
            """;

    private static final String SQL_TIM_THEO_HO_TEN = """
            SELECT nv.MaNhanVien, nv.HoTen, nv.SoDienThoai, nv.DiaChi,
                   nv.ChucVu, nv.MaChiNhanh, cn.TenChiNhanh, nv.TenDangNhap
            FROM dbo.NhanVien nv
            LEFT JOIN dbo.ChiNhanh cn ON cn.MaChiNhanh = nv.MaChiNhanh
            WHERE nv.HoTen LIKE ?
            """;

    private static final String SQL_TIM_THEO_SO_DIEN_THOAI = """
            SELECT nv.MaNhanVien, nv.HoTen, nv.SoDienThoai, nv.DiaChi,
                   nv.ChucVu, nv.MaChiNhanh, cn.TenChiNhanh, nv.TenDangNhap
            FROM dbo.NhanVien nv
            LEFT JOIN dbo.ChiNhanh cn ON cn.MaChiNhanh = nv.MaChiNhanh
            WHERE nv.SoDienThoai LIKE ?
            """;

    private static final String SQL_TIM_THEO_CHUC_VU = """
            SELECT nv.MaNhanVien, nv.HoTen, nv.SoDienThoai, nv.DiaChi,
                   nv.ChucVu, nv.MaChiNhanh, cn.TenChiNhanh, nv.TenDangNhap
            FROM dbo.NhanVien nv
            LEFT JOIN dbo.ChiNhanh cn ON cn.MaChiNhanh = nv.MaChiNhanh
            WHERE nv.ChucVu LIKE ?
            """;

    private static final String SQL_TIM_THEO_TEN_CHI_NHANH = """
            SELECT nv.MaNhanVien, nv.HoTen, nv.SoDienThoai, nv.DiaChi,
                   nv.ChucVu, nv.MaChiNhanh, cn.TenChiNhanh, nv.TenDangNhap
            FROM dbo.NhanVien nv
            INNER JOIN dbo.ChiNhanh cn ON nv.MaChiNhanh = cn.MaChiNhanh
            WHERE cn.TenChiNhanh LIKE ?
            """;

    private static final String SQL_TIM_THEO_TEN_DANG_NHAP = """
            SELECT nv.MaNhanVien, nv.HoTen, nv.SoDienThoai, nv.DiaChi,
                   nv.ChucVu, nv.MaChiNhanh, cn.TenChiNhanh, nv.TenDangNhap
            FROM dbo.NhanVien nv
            LEFT JOIN dbo.ChiNhanh cn ON cn.MaChiNhanh = nv.MaChiNhanh
            WHERE nv.TenDangNhap LIKE ?
            """;

    private static final String SQL_TIM_KIEM_NHAN_VIEN = """
            SELECT nv.MaNhanVien, nv.HoTen, nv.SoDienThoai, nv.DiaChi,
                   nv.ChucVu, nv.MaChiNhanh, cn.TenChiNhanh, nv.TenDangNhap
            FROM dbo.NhanVien nv
            LEFT JOIN dbo.ChiNhanh cn ON nv.MaChiNhanh = cn.MaChiNhanh
            WHERE (? = '' OR nv.MaNhanVien LIKE ?)
              AND (? = '' OR nv.HoTen LIKE ?)
              AND (? = '' OR nv.SoDienThoai LIKE ?)
              AND (? = '' OR nv.ChucVu LIKE ?)
              AND (? = '' OR cn.TenChiNhanh LIKE ?)
              AND (? = '' OR nv.TenDangNhap LIKE ?)
              AND (? = '' OR nv.DiaChi LIKE ?)
            """;

    @Override
    public boolean themNhanVien(NhanVien nhanVien) {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(SQL_THEM_NHAN_VIEN)) {
            ps.setString(1, nhanVien.getMaNhanVien());
            ps.setString(2, nhanVien.getHoTen());
            ps.setString(3, nhanVien.getSoDienThoai());
            ps.setString(4, nhanVien.getDiaChi());
            ps.setString(5, nhanVien.getChucVu());
            ps.setString(6, nhanVien.getMaChiNhanh());
            ps.setString(7, nhanVien.getTenDangNhap());

            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi thêm nhân viên: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean suaNhanVien(NhanVien nhanVien) {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(SQL_SUA_NHAN_VIEN)) {
            ps.setString(1, nhanVien.getHoTen());
            ps.setString(2, nhanVien.getSoDienThoai());
            ps.setString(3, nhanVien.getDiaChi());
            ps.setString(4, nhanVien.getChucVu());
            ps.setString(5, nhanVien.getMaChiNhanh());
            ps.setString(6, nhanVien.getTenDangNhap());
            ps.setString(7, nhanVien.getMaNhanVien());

            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi sửa nhân viên: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean xoaNhanVien(String maNhanVien) {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(SQL_XOA_NHAN_VIEN)) {
            ps.setString(1, maNhanVien);

            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi xóa nhân viên: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<NhanVien> timNhanVien(String maNhanVien) {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(SQL_TIM_NHAN_VIEN)) {
            ps.setString(1, maNhanVien);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(taoNhanVien(rs));
                }

                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tìm nhân viên: " + e.getMessage(), e);
        }
    }

    @Override
    public List<NhanVien> getDanhSachNhanVien() {
        List<NhanVien> danhSach = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(SQL_GET_DANH_SACH_NHAN_VIEN); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                danhSach.add(taoNhanVien(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy danh sách nhân viên: " + e.getMessage(), e);
        }

        return danhSach;
    }

    @Override
    public List<NhanVien> getNhanVienTheoChiNhanh(String maChiNhanh) {
        List<NhanVien> danhSach = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(SQL_GET_NHAN_VIEN_THEO_CHI_NHANH)) {
            ps.setString(1, maChiNhanh);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(taoNhanVien(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy nhân viên theo chi nhánh: " + e.getMessage(), e);
        }

        return danhSach;
    }

    @Override
    public Optional<String> timMaChiNhanhTheoTen(String tenChiNhanh) {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(SQL_TIM_MA_CHI_NHANH_THEO_TEN)) {
            ps.setString(1, tenChiNhanh);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getString("MaChiNhanh"));
                }

                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tìm mã chi nhánh theo tên: " + e.getMessage(), e);
        }
    }

    @Override
    public List<NhanVien> timTheoHoTen(String hoTen) {
        return timDanhSach(SQL_TIM_THEO_HO_TEN, "%" + hoTen + "%");
    }

    @Override
    public List<NhanVien> timTheoSoDienThoai(String soDienThoai) {
        return timDanhSach(SQL_TIM_THEO_SO_DIEN_THOAI, "%" + soDienThoai + "%");
    }

    @Override
    public List<NhanVien> timTheoChucVu(String chucVu) {
        return timDanhSach(SQL_TIM_THEO_CHUC_VU, "%" + chucVu + "%");
    }

    @Override
    public List<NhanVien> timTheoTenChiNhanh(String tenChiNhanh) {
        return timDanhSach(SQL_TIM_THEO_TEN_CHI_NHANH, "%" + tenChiNhanh + "%");
    }

    @Override
    public List<NhanVien> timTheoTenDangNhap(String tenDangNhap) {
        return timDanhSach(SQL_TIM_THEO_TEN_DANG_NHAP, "%" + tenDangNhap + "%");
    }

    @Override
    public List<NhanVien> timKiemNhanVien(
            String maNhanVien,
            String hoTen,
            String soDienThoai,
            String chucVu,
            String tenChiNhanh,
            String tenDangNhap,
            String diaChi
    ) {
        List<NhanVien> danhSach = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(SQL_TIM_KIEM_NHAN_VIEN)) {
            ps.setString(1, maNhanVien);
            ps.setString(2, "%" + maNhanVien + "%");
            ps.setString(3, hoTen);
            ps.setString(4, "%" + hoTen + "%");
            ps.setString(5, soDienThoai);
            ps.setString(6, "%" + soDienThoai + "%");
            ps.setString(7, chucVu);
            ps.setString(8, "%" + chucVu + "%");
            ps.setString(9, tenChiNhanh);
            ps.setString(10, "%" + tenChiNhanh + "%");
            ps.setString(11, tenDangNhap);
            ps.setString(12, "%" + tenDangNhap + "%");
            ps.setString(13, diaChi);
            ps.setString(14, "%" + diaChi + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(taoNhanVien(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tìm kiếm nhân viên: " + e.getMessage(), e);
        }

        return danhSach;
    }

    private List<NhanVien> timDanhSach(String sql, String giaTri) {
        List<NhanVien> danhSach = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, giaTri);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(taoNhanVien(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tìm kiếm nhân viên: " + e.getMessage(), e);
        }

        return danhSach;
    }

    private NhanVien taoNhanVien(ResultSet rs) throws SQLException {
        NhanVien nhanVien = new NhanVien();
        nhanVien.setMaNhanVien(rs.getString("MaNhanVien"));
        nhanVien.setHoTen(rs.getString("HoTen"));
        nhanVien.setSoDienThoai(rs.getString("SoDienThoai"));
        nhanVien.setDiaChi(rs.getString("DiaChi"));
        nhanVien.setChucVu(rs.getString("ChucVu"));
        nhanVien.setMaChiNhanh(rs.getString("MaChiNhanh"));
        nhanVien.setTenChiNhanh(rs.getString("TenChiNhanh"));
        nhanVien.setTenDangNhap(rs.getString("TenDangNhap"));
        return nhanVien;
    }
}
