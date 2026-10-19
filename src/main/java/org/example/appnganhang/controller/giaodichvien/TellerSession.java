package org.example.appnganhang.controller.giaodichvien;

public final class TellerSession {
    private static String tenDangNhap;

    private TellerSession() {}

    public static void setCurrentUsername(String username) {
        tenDangNhap = username;
    }

    public static String getTenDangNhap() { return tenDangNhap; }

    public static void clear() {
        tenDangNhap = null;
    }
}
