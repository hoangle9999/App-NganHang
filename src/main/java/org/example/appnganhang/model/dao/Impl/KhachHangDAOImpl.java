package org.example.appnganhang.model.dao.Impl;

import org.example.appnganhang.model.dao.intf.KhachHangDAO;
import org.example.appnganhang.model.entity.KhachHang;
import org.example.appnganhang.model.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class KhachHangDAOImpl implements KhachHangDAO {
    private static final String SQL_FIND_ALL = """
            SELECT MaKhachHang,
                   HoTen,
                   SoCCCD,
                   SoDienThoai,
                   Email,
                   DiaChi,
                   TenDangNhap
            FROM KhachHang
            """;

    private static final String SQL_FIND_BY_CCCD = """
            SELECT MaKhachHang,
                   HoTen,
                   SoCCCD,
                   SoDienThoai,
                   Email,
                   DiaChi,
                   TenDangNhap
            FROM KhachHang
            WHERE SoCCCD = ?
            """;

    private static final String SQL_FIND_BY_PHONE = """
            SELECT MaKhachHang,
                   HoTen,
                   SoCCCD,
                   SoDienThoai,
                   Email,
                   DiaChi,
                   TenDangNhap
            FROM KhachHang
            WHERE SoDienThoai = ?
            """;

    private static final String SQL_TIM_KIEM_KHACH_HANG = """
            SELECT MaKhachHang,
                   HoTen,
                   SoCCCD,
                   SoDienThoai,
                   Email,
                   DiaChi,
                   TenDangNhap
            FROM KhachHang
            WHERE (? = '' OR MaKhachHang LIKE ?)
              AND (? = '' OR HoTen LIKE ?)
              AND (? = '' OR SoCCCD LIKE ?)
              AND (? = '' OR SoDienThoai LIKE ?)
              AND (? = '' OR Email LIKE ?)
              AND (? = '' OR DiaChi LIKE ?)
              AND (? = '' OR TenDangNhap LIKE ?)
            """;

    private static final String SQL_INSERT = """
            INSERT INTO KhachHang
            (
                MaKhachHang,
                HoTen,
                SoCCCD,
                SoDienThoai,
                Email,
                DiaChi,
                TenDangNhap
            )
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

    private static final String SQL_UPDATE = """
            UPDATE KhachHang
            SET HoTen = ?,
                SoCCCD = ?,
                SoDienThoai = ?,
                Email = ?,
                DiaChi = ?,
                TenDangNhap = ?
            WHERE MaKhachHang = ?
            """;

    private static final String SQL_DELETE =
            "DELETE FROM KhachHang WHERE MaKhachHang = ?";


    @Override
    public String taoMaKhachHang() {
        String sql = "{CALL dbo.sp_TaoMaKhachHang(?)}";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.registerOutParameter(1, Types.VARCHAR);
            stmt.execute();

            return stmt.getString(1);

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Lỗi tạo mã khách hàng: " + e.getMessage(), e
            );
        }
    }

    @Override
    public boolean insert(KhachHang kh) {

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_INSERT)) {

            ps.setString(1, kh.getMaKhachHang());
            ps.setString(2, kh.getHoTen());
            ps.setString(3, kh.getSoCCCD());
            ps.setString(4, kh.getSoDienThoai());
            ps.setString(5, kh.getEmail());
            ps.setString(6, kh.getDiaChi());
            ps.setString(7, kh.getTenDangNhap());

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Lỗi insert KhachHang: " + e.getMessage(), e
            );
        }
    }

    @Override
    public boolean update(KhachHang kh) {

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_UPDATE)) {

            ps.setString(1, kh.getHoTen());
            ps.setString(2, kh.getSoCCCD());
            ps.setString(3, kh.getSoDienThoai());
            ps.setString(4, kh.getEmail());
            ps.setString(5, kh.getDiaChi());
            ps.setString(6, kh.getTenDangNhap());
            ps.setString(7, kh.getMaKhachHang());

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Lỗi update KhachHang: " + e.getMessage(), e
            );
        }
    }

    @Override
    public boolean delete(String maKhachHang) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_DELETE)) {

            ps.setString(1, maKhachHang);

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            if (e.getErrorCode() == 547) {
                throw new RuntimeException(
                        "Không thể xóa khách hàng vì khách hàng đang có "
                                + "tài khoản ngân hàng hoặc dữ liệu liên quan.",
                        e
                );
            }

            throw new RuntimeException(
                    "Lỗi khi xóa khách hàng: " + e.getMessage(), e
            );
        }
    }

    @Override
    public Optional<KhachHang> findByCCCD(String soCCCD) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_FIND_BY_CCCD)) {

            ps.setString(1, soCCCD);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return Optional.of(taoKhachHang(rs));
                }

                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Lỗi findByCCCD KhachHang: " + e.getMessage(), e
            );
        }
    }

    @Override
    public Optional<KhachHang> findByPhone(String soDienThoai) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_FIND_BY_PHONE)) {

            ps.setString(1, soDienThoai);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return Optional.of(taoKhachHang(rs));
                }

                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Lỗi findByPhone KhachHang: " + e.getMessage(), e
            );
        }
    }

    @Override
    public List<KhachHang> timKiemKhachHang(
            String maKhachHang,
            String hoTen,
            String soCCCD,
            String soDienThoai,
            String email,
            String diaChi,
            String tenDangNhap) {

        List<KhachHang> danhSach = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_TIM_KIEM_KHACH_HANG)) {

            ps.setString(1, maKhachHang);
            ps.setString(2, "%" + maKhachHang + "%");

            ps.setString(3, hoTen);
            ps.setString(4, "%" + hoTen + "%");

            ps.setString(5, soCCCD);
            ps.setString(6, "%" + soCCCD + "%");

            ps.setString(7, soDienThoai);
            ps.setString(8, "%" + soDienThoai + "%");

            ps.setString(9, email);
            ps.setString(10, "%" + email + "%");

            ps.setString(11, diaChi);
            ps.setString(12, "%" + diaChi + "%");

            ps.setString(13, tenDangNhap);
            ps.setString(14, "%" + tenDangNhap + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(taoKhachHang(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Lỗi tìm kiếm khách hàng: " + e.getMessage(), e
            );
        }

        return danhSach;
    }

    @Override
    public List<KhachHang> findAll() {
        List<KhachHang> danhSach = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                danhSach.add(taoKhachHang(rs));
            }

            return danhSach;

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi findAll KhachHang: " + e.getMessage(), e);
        }
    }

    private KhachHang taoKhachHang(ResultSet rs) throws SQLException {

        KhachHang kh = new KhachHang();

        kh.setMaKhachHang(rs.getString("MaKhachHang"));
        kh.setHoTen(rs.getString("HoTen"));
        kh.setSoCCCD(rs.getString("SoCCCD"));
        kh.setSoDienThoai(rs.getString("SoDienThoai"));
        kh.setEmail(rs.getString("Email"));
        kh.setDiaChi(rs.getString("DiaChi"));
        kh.setTenDangNhap(rs.getString("TenDangNhap"));

        return kh;
    }
}
