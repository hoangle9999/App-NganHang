package org.example.appnganhang.controller.service;

import org.example.appnganhang.model.entity.KhachHang;
import org.example.appnganhang.model.entity.TaiKhoanDangNhap_VaiTro;

public interface AuthService {
    boolean login(String tenDangNhap, String matKhau);
    boolean register(KhachHang khachHang, String matKhau);
    void logout();
    boolean changePassword(String tenDangNhap, String matKhauCu, String matKhauMoi);
    boolean hasPermission(String tenDangNhap, String roleName);
    TaiKhoanDangNhap_VaiTro getCurrentUser();
}
