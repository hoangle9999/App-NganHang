package org.example.appnganhang.controller.cskh;

public final class CSKHSession {
    private static String tenDangNhap;

    private CSKHSession() {}

    public static void setTenDangNhap(String username) { tenDangNhap = username; }
    public static String getTenDangNhap() { return tenDangNhap; }
    public static void clear() { tenDangNhap = null; }
}
