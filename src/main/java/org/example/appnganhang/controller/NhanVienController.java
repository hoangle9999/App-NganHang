package org.example.appnganhang.controller;

import org.example.appnganhang.model.NhanVien;
import org.example.appnganhang.controller.service.Iml.NhanVienServiceImpl;
import org.example.appnganhang.controller.service.NhanVienService;

import java.util.List;
import java.util.Optional;

public class NhanVienController {
    private final NhanVienService nhanVienService;

    public NhanVienController() {
        this.nhanVienService = new NhanVienServiceImpl();
    }

    public boolean themNhanVien(NhanVien nhanVien) {
        return nhanVienService.themNhanVien(nhanVien);
    }

    public boolean suaNhanVien(NhanVien nhanVien) {
        return nhanVienService.suaNhanVien(nhanVien);
    }

    public boolean xoaNhanVien(String maNhanVien) {
        return nhanVienService.xoaNhanVien(maNhanVien);
    }

    public Optional<NhanVien> timNhanVien(String maNhanVien) {
        return nhanVienService.timNhanVien(maNhanVien);
    }

    public List<NhanVien> getNhanVienTheoChiNhanh(String maChiNhanh) {
        return nhanVienService.getNhanVienTheoChiNhanh(maChiNhanh);
    }
}
