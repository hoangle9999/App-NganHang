package org.example.appnganhang.controller.service;

import org.example.appnganhang.model.entity.NhanVien;

import java.util.List;
import java.util.Optional;

public interface NhanVienService {
    boolean themNhanVien(NhanVien nhanVien);
    boolean suaNhanVien(NhanVien nhanVien);
    boolean xoaNhanVien(String maNhanVien);
    Optional<NhanVien> timNhanVien(String maNhanVien);
    List<NhanVien> getNhanVienTheoChiNhanh(String maChiNhanh);
}
