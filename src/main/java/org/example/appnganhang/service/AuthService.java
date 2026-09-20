package org.example.appnganhang.service;

import org.example.appnganhang.modell.KhachHang;
import org.example.appnganhang.modell.TaiKhoanDangNhap_VaiTro;

public interface AuthService {
    boolean login(String tenDangNhap, String matKhau);
    boolean register(KhachHang khachHang, String matKhau);
    void logout();
    boolean changePassword(String tenDangNhap, String matKhauCu, String matKhauMoi);
    boolean hasPermission(String tenDangNhap, String roleName);
    TaiKhoanDangNhap_VaiTro getCurrentUser();
}
