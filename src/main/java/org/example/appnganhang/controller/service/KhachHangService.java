package org.example.appnganhang.controller.service;

import org.example.appnganhang.model.entity.KhachHang;

import java.util.List;
import java.util.Optional;

public interface KhachHangService {
    boolean themKhachHang(KhachHang kh);
    boolean capNhatKhachHang(KhachHang kh);
    boolean xoaKhachHang(String kh);
    Optional<KhachHang> timTheoMa(String maKh);
    Optional<KhachHang> timTheoCCCD(String soCCCD);
    Optional<KhachHang> timTheoSoDienThoai(String soDienThoai);
    Optional<KhachHang> timTheoTenDangNhap(String tenDangNhap);
    List <KhachHang> getDanhSachKhachHang();

}
