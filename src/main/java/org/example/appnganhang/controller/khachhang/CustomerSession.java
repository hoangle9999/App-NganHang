package org.example.appnganhang.controller.khachhang;

import org.example.appnganhang.model.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CustomerSession {

    private static String currentMaKhachHang = "";
    private static String currentTenDangNhap = "";
    private static String currentHoTen = "Khách Hàng";
    private static String currentSoCCCD = "";
    private static String currentSoDienThoai = "";
    private static String currentEmail = "";
    private static String currentDiaChi = "";
    private static String selectedSoTaiKhoan = "";

    public static void setSession(String maKhachHang, String tenDangNhap, String hoTen, String cccd, String sdt, String email, String diaChi) {
        currentMaKhachHang = maKhachHang;
        currentTenDangNhap = tenDangNhap;
        currentHoTen = hoTen != null && !hoTen.isEmpty() ? hoTen : "Khách Hàng";
        currentSoCCCD = cccd != null ? cccd : "";
        currentSoDienThoai = sdt != null ? sdt : "";
        currentEmail = email != null ? email : "";
        currentDiaChi = diaChi != null ? diaChi : "";
    }

    public static void loadSessionFromDB(String maNguoiDung, String tenDangNhap) {
        currentTenDangNhap = tenDangNhap != null ? tenDangNhap : "";
        currentMaKhachHang = maNguoiDung != null ? maNguoiDung : "";

        String sql = "SELECT MaKhachHang, HoTen, SoCCCD, SoDienThoai, Email, DiaChi, TenDangNhap FROM KhachHang " +
                     "WHERE MaKhachHang = ? OR TenDangNhap = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, currentMaKhachHang);
            ps.setString(2, currentTenDangNhap);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                currentMaKhachHang = rs.getString("MaKhachHang");
                currentHoTen = rs.getString("HoTen");
                currentSoCCCD = rs.getString("SoCCCD");
                currentSoDienThoai = rs.getString("SoDienThoai");
                currentEmail = rs.getString("Email");
                currentDiaChi = rs.getString("DiaChi");
                if (rs.getString("TenDangNhap") != null) {
                    currentTenDangNhap = rs.getString("TenDangNhap");
                }
            } else {
                // Nếu chưa có hồ sơ KhachHang, tìm theo tài khoản đầu tiên hoặc gán mặc định
                timTheoTaiKhoanDauTien();
            }

        } catch (Exception e) {
            System.err.println("Lỗi nạp session khách hàng: " + e.getMessage());
            timTheoTaiKhoanDauTien();
        }
    }

    private static void timTheoTaiKhoanDauTien() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT TOP 1 kh.MaKhachHang, kh.HoTen, kh.SoCCCD, kh.SoDienThoai, kh.Email, kh.DiaChi, kh.TenDangNhap " +
                     "FROM KhachHang kh JOIN TaiKhoanNganHang tk ON kh.MaKhachHang = tk.MaKhachHang")) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                currentMaKhachHang = rs.getString("MaKhachHang");
                currentHoTen = rs.getString("HoTen");
                currentSoCCCD = rs.getString("SoCCCD");
                currentSoDienThoai = rs.getString("SoDienThoai");
                currentEmail = rs.getString("Email");
                currentDiaChi = rs.getString("DiaChi");
            } else {
                currentMaKhachHang = "KH0001";
                currentHoTen = "Nguyễn Văn Khách Hàng";
                currentSoCCCD = "001200001234";
                currentSoDienThoai = "0987654321";
                currentEmail = "khachhang@gmail.com";
                currentDiaChi = "Hà Nội, Việt Nam";
            }
        } catch (Exception ex) {
            currentMaKhachHang = "KH0001";
            currentHoTen = "Nguyễn Văn Khách Hàng";
            currentSoCCCD = "001200001234";
            currentSoDienThoai = "0987654321";
            currentEmail = "khachhang@gmail.com";
            currentDiaChi = "Hà Nội, Việt Nam";
        }
    }

    public static void clear() {
        currentMaKhachHang = "";
        currentTenDangNhap = "";
        currentHoTen = "Khách Hàng";
        currentSoCCCD = "";
        currentSoDienThoai = "";
        currentEmail = "";
        currentDiaChi = "";
        selectedSoTaiKhoan = "";
    }

    public static String getCurrentMaKhachHang() { return currentMaKhachHang; }
    public static String getCurrentTenDangNhap() { return currentTenDangNhap; }
    public static String getCurrentHoTen() { return currentHoTen; }
    public static String getCurrentSoCCCD() { return currentSoCCCD; }
    public static String getCurrentSoDienThoai() { return currentSoDienThoai; }
    public static String getCurrentEmail() { return currentEmail; }
    public static String getCurrentDiaChi() { return currentDiaChi; }
    public static String getSelectedSoTaiKhoan() { return selectedSoTaiKhoan; }
    public static void setSelectedSoTaiKhoan(String stk) { selectedSoTaiKhoan = stk; }
}
