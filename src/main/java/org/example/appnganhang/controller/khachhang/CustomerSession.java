package org.example.appnganhang.controller.khachhang;

import org.example.appnganhang.controller.service.Iml.KhachHangPortalServiceImpl;
import org.example.appnganhang.controller.service.intf.KhachHangPortalService;


public class CustomerSession {
    private static final KhachHangPortalService portalService = new KhachHangPortalServiceImpl();

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
        try {
            java.util.Optional<java.util.Map<String, Object>> profile =
                    portalService.timHoSo(currentMaKhachHang, currentTenDangNhap);
            if (profile.isEmpty()) profile = portalService.timHoSoCoTaiKhoanDauTien();
            if (profile.isPresent()) {
                java.util.Map<String, Object> row = profile.get();
                currentMaKhachHang = text(row, "MaKhachHang", currentMaKhachHang);
                currentHoTen = text(row, "HoTen", "Khách Hàng");
                currentSoCCCD = text(row, "SoCCCD", "");
                currentSoDienThoai = text(row, "SoDienThoai", "");
                currentEmail = text(row, "Email", "");
                currentDiaChi = text(row, "DiaChi", "");
                currentTenDangNhap = text(row, "TenDangNhap", currentTenDangNhap);
            }
        } catch (RuntimeException e) {
            System.err.println("Lỗi nạp session khách hàng: " + e.getMessage());
            // Không tự gán hồ sơ giả; giữ thông tin nhận được từ đăng nhập.
        }
    }

    private static String text(java.util.Map<String, Object> row, String key, String fallback) {
        Object value = row.get(key);
        return value == null || value.toString().isBlank() ? fallback : value.toString();
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
