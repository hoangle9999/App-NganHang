package org.example.appnganhang.model.dao;

import org.example.appnganhang.model.entity.TaiKhoanDangNhap_VaiTro;

import java.util.Optional;

public interface TaiKhoanDangNhapDAO {
    boolean insert(TaiKhoanDangNhap_VaiTro taiKhoan);
    Optional<TaiKhoanDangNhap_VaiTro> findByUsername(String tenDangNhap);
    Optional<TaiKhoanDangNhap_VaiTro> login(String tenDangNhap, String matKhau);
    boolean changePassword(String tenDangNhap, String matKhauMoi);
}

