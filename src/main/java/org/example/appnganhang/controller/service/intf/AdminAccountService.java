package org.example.appnganhang.controller.service.intf;

import org.example.appnganhang.controller.admin.AdminAccountController;
import org.example.appnganhang.model.entity.ChiNhanh;
import org.example.appnganhang.model.entity.TaiKhoanDangNhap_VaiTro;
import org.example.appnganhang.model.entity.HanMucGiaoDich;

import java.util.List;

public interface AdminAccountService {
    List<ChiNhanh> layDanhSachChiNhanh();
    List<HanMucGiaoDich> layDanhSachHanMuc();
    List<TaiKhoanDangNhap_VaiTro> layDanhSachTaiKhoan();
    String taoTaiKhoan(String tenDangNhap, String matKhau, String roleName,
                       String hoTen, String soCCCD, String soDienThoai,
                       String email, String diaChi, String maChiNhanh, String maHanMuc);
    void doiVaiTro(String tenDangNhap, String roleNameMoi);
    void datQuyenHoatDong(String tenDangNhap, boolean choPhep);
    void datLaiMatKhau(String tenDangNhap, String matKhauMoi);
    void xoaTaiKhoan(String tenDangNhap);
}
