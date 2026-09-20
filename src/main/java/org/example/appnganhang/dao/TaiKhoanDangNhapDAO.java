package org.example.appnganhang.dao;

import org.example.appnganhang.modell.TaiKhoanDangNhap_VaiTro;

import java.util.Optional;

public interface TaiKhoanDangNhapDAO {
    boolean insert(TaiKhoanDangNhap_VaiTro taiKhoan);
    Optional<TaiKhoanDangNhap_VaiTro> findByUsername(String tenDangNhap);
    Optional<TaiKhoanDangNhap_VaiTro> login(String tenDangNhap, String matKhau);
    boolean changePassword(String tenDangNhap, String matKhauMoi);
}

