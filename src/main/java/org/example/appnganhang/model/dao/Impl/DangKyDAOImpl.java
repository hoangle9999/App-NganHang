package org.example.appnganhang.model.dao.Impl;

import org.example.appnganhang.model.dao.intf.DangKyDAO;
import org.example.appnganhang.model.entity.KhachHang;
import org.example.appnganhang.model.util.DBConnection;

import java.sql.*;

public class DangKyDAOImpl implements DangKyDAO {
    private static final String sqlTaiKhoanDangNhap =
            "INSERT INTO TaiKhoanDangNhap_VaiTro " +
                    "(TenDangNhap, MatKhau, RoleName, MaNguoiDung) " +
                    "VALUES (?, ?, ?, ?)";

    private static final String sqlKhachHang =
            "INSERT INTO KhachHang " +
                    "(MaKhachHang, HoTen, SoCCCD, SoDienThoai, " +
                    "Email, DiaChi, TenDangNhap) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String sqlTaiKhoanNganHang =
            "INSERT INTO TaiKhoanNganHang " +
                    "(MaTaiKhoan, MaKhachHang, SoTaiKhoan, SoDu, " +
                    "TrangThai, MaChiNhanh, MaHanMuc, MaNganHang) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    @Override
    public boolean dangKyKhachHang(
            KhachHang khachHang,
            String matKhau
    ) {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // 1. Sinh mã khách hàng
                String maKhachHang = taoMa(
                        conn,
                        "dbo.sp_TaoMaKhachHang"
                );

                // 2. Sinh mã tài khoản ngân hàng
                String maTaiKhoan = taoMa(
                        conn,
                        "dbo.sp_TaoMaTaiKhoan"
                );

                // 3. Sinh số tài khoản ngân hàng
                String soTaiKhoan = taoMa(
                        conn,
                        "dbo.sp_TaoSoTaiKhoan"
                );

                // 4. Tạo tài khoản đăng nhập
                try (PreparedStatement ps =
                             conn.prepareStatement(sqlTaiKhoanDangNhap)) {

                    ps.setString(1, khachHang.getTenDangNhap());
                    ps.setString(2, matKhau);
                    ps.setString(3, "Khách hàng");
                    ps.setString(4, maKhachHang);

                    ps.executeUpdate();
                }

                // 5. Tạo thông tin khách hàng
                try (PreparedStatement ps =
                             conn.prepareStatement(sqlKhachHang)) {

                    ps.setString(1, maKhachHang);
                    ps.setString(2, khachHang.getHoTen());
                    ps.setString(3, khachHang.getSoCCCD());
                    ps.setString(4, khachHang.getSoDienThoai());
                    ps.setString(5, khachHang.getEmail());
                    ps.setString(6, khachHang.getDiaChi());
                    ps.setString(7, khachHang.getTenDangNhap());

                    ps.executeUpdate();
                }

                // 6. Tạo tài khoản ngân hàng
                try (PreparedStatement ps =
                             conn.prepareStatement(sqlTaiKhoanNganHang)) {

                    ps.setString(1, maTaiKhoan);
                    ps.setString(2, maKhachHang);
                    ps.setString(3, soTaiKhoan);
                    ps.setBigDecimal(
                            4,
                            java.math.BigDecimal.ZERO
                    );
                    ps.setString(5, "Hoạt động");
                    ps.setString(6, "CN001");
                    ps.setString(7, "HM003");
                    ps.setString(8, "SMART");

                    ps.executeUpdate();
                }

                conn.commit();

                khachHang.setMaKhachHang(maKhachHang);
                return true;

            } catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
                return false;

            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean taoKhachHangTuNhanVien(KhachHang khachHang, String matKhau, String maChiNhanh) {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // 1. Sinh mã khách hàng
                String maKhachHang = taoMa(
                        conn,
                        "dbo.sp_TaoMaKhachHang"
                );

                // 2. Sinh mã tài khoản ngân hàng
                String maTaiKhoan = taoMa(
                        conn,
                        "dbo.sp_TaoMaTaiKhoan"
                );

                // 3. Sinh số tài khoản ngân hàng
                String soTaiKhoan = taoMa(
                        conn,
                        "dbo.sp_TaoSoTaiKhoan"
                );

                // 4. Tạo tài khoản đăng nhập
                try (PreparedStatement ps =
                             conn.prepareStatement(sqlTaiKhoanDangNhap)) {

                    ps.setString(1, khachHang.getTenDangNhap());
                    ps.setString(2, matKhau);
                    ps.setString(3, "Khách hàng");
                    ps.setString(4, maKhachHang);

                    ps.executeUpdate();
                }

                // 5. Tạo thông tin khách hàng
                try (PreparedStatement ps =
                             conn.prepareStatement(sqlKhachHang)) {

                    ps.setString(1, maKhachHang);
                    ps.setString(2, khachHang.getHoTen());
                    ps.setString(3, khachHang.getSoCCCD());
                    ps.setString(4, khachHang.getSoDienThoai());
                    ps.setString(5, khachHang.getEmail());
                    ps.setString(6, khachHang.getDiaChi());
                    ps.setString(7, khachHang.getTenDangNhap());

                    ps.executeUpdate();
                }

                // 6. Tạo tài khoản ngân hàng
                try (PreparedStatement ps =
                             conn.prepareStatement(sqlTaiKhoanNganHang)) {

                    ps.setString(1, maTaiKhoan);
                    ps.setString(2, maKhachHang);
                    ps.setString(3, soTaiKhoan);
                    ps.setBigDecimal(
                            4,
                            java.math.BigDecimal.ZERO
                    );
                    ps.setString(5, "Hoạt động");
                    ps.setString(6, maChiNhanh);
                    ps.setString(7, "HM003");
                    ps.setString(8, "SMART");

                    ps.executeUpdate();
                }

                conn.commit();

                khachHang.setMaKhachHang(maKhachHang);
                return true;

            } catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
                return false;

            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String taoMa(
            Connection conn,
            String tenProcedure
    ) throws SQLException {

        String sql = "{CALL " + tenProcedure + "(?)}";

        try (CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.registerOutParameter(1, Types.VARCHAR);
            stmt.execute();

            String ma = stmt.getString(1);

            if (ma == null || ma.isBlank()) {
                throw new SQLException(
                        "Không sinh được mã từ " + tenProcedure
                );
            }

            return ma;
        }
    }
}
