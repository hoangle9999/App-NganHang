package org.example.appnganhang.controller.khachhang;

import org.example.appnganhang.controller.service.Impl.KhachHangPortalServiceImpl;
import org.example.appnganhang.controller.service.intf.KhachHangPortalService;

import java.util.Map;
import java.util.Optional;

public class CustomerSession {
    private static final KhachHangPortalService portalService = new KhachHangPortalServiceImpl();

    private static String currentMaKhachHang = "";
    private static String currentTenDangNhap = "";
    private static String currentHoTen = "Khách hàng";
    private static String currentSoCCCD = "";
    private static String currentSoDienThoai = "";
    private static String currentEmail = "";
    private static String currentDiaChi = "";
    private static String selectedSoTaiKhoan = "";

    /** Loads only the profile explicitly linked to this authenticated user. */
    public static boolean loadSessionFromDB(String maNguoiDung, String tenDangNhap) {
        clear();
        if (maNguoiDung == null || maNguoiDung.isBlank()
                || tenDangNhap == null || tenDangNhap.isBlank()) return false;

        try {
            Optional<Map<String, Object>> profile = portalService.timHoSo(maNguoiDung, tenDangNhap);
            if (profile.isEmpty()) return false;

            Map<String, Object> row = profile.get();
            String maKH = text(row, "MaKhachHang");
            String username = text(row, "TenDangNhap");
            String hoTen = text(row, "HoTen");
            if (maKH.isBlank() || username.isBlank() || hoTen.isBlank()
                    || !maNguoiDung.equalsIgnoreCase(maKH)
                    || !tenDangNhap.equalsIgnoreCase(username)) return false;

            currentMaKhachHang = maKH;
            currentTenDangNhap = username;
            currentHoTen = hoTen;
            currentSoCCCD = text(row, "SoCCCD");
            currentSoDienThoai = text(row, "SoDienThoai");
            currentEmail = text(row, "Email");
            currentDiaChi = text(row, "DiaChi");
            return true;
        } catch (RuntimeException e) {
            System.err.println("Không thể tải hồ sơ khách hàng: " + e.getMessage());
            clear();
            return false;
        }
    }

    private static String text(Map<String, Object> row, String key) {
        Object value = row.get(key);
        return value == null ? "" : value.toString().trim();
    }

    public static void clear() {
        currentMaKhachHang = "";
        currentTenDangNhap = "";
        currentHoTen = "Khách hàng";
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
    public static void setSelectedSoTaiKhoan(String stk) { selectedSoTaiKhoan = stk == null ? "" : stk; }
}
