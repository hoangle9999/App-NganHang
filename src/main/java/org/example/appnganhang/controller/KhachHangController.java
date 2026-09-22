package org.example.appnganhang.controller;

import org.example.appnganhang.model.KhachHang;
import org.example.appnganhang.controller.service.KhachHangService;
import org.example.appnganhang.controller.service.Iml.KhachHangServiceImpl;

import java.util.List;
import java.util.Optional;

public class KhachHangController {
    private final KhachHangService service;

    public KhachHangController() {
        this.service = new KhachHangServiceImpl();
    }

    public boolean themKhachHang(KhachHang kh) {
        return service.themKhachHang(kh);
    }

    public boolean capNhatKhachHang(KhachHang kh) {
        return service.capNhatKhachHang(kh);
    }

    public boolean xoaKhachHang(String maKh) {
        return service.xoaKhachHang(maKh);
    }

    public Optional<KhachHang> timTheoCCCD(String soCCCD) {
        return service.timTheoCCCD(soCCCD);
    }

    public Optional<KhachHang> timTheoSoDienThoai(String soDienThoai) {
        return service.timTheoSoDienThoai(soDienThoai);
    }

    public Optional<KhachHang> timTheoTenDangNhap(String tenDangNhap) {
        return service.timTheoTenDangNhap(tenDangNhap);
    }

    public List<KhachHang> getDanhSachKhachHang() {
        return service.getDanhSachKhachHang();
    }
}
