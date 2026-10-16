package org.example.appnganhang.controller.service.intf;

import org.example.appnganhang.model.entity.KhachHang;

import java.util.List;
import java.util.Optional;

public interface KhachHangService {
    String taoMaKhachHang();
    boolean taoKhachHangTuNhanVien(
            String hoTen,
            String soCCCD,
            String soDienThoai,
            String email,
            String diaChi,
            String tenDangNhap,
            String matKhau,
            String maChiNhanh
    );
    boolean themKhachHang(KhachHang kh);
    boolean capNhatKhachHang(KhachHang kh);
    boolean xoaKhachHang(String kh);
    List<KhachHang> timKiemKhachHang(
            String maKhachHang,
            String hoTen,
            String soCCCD,
            String soDienThoai,
            String email,
            String diaChi,
            String tenDangNhap
    );
    List <KhachHang> getDanhSachKhachHang();

}
