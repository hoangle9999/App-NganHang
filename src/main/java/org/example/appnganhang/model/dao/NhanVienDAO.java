package org.example.appnganhang.model.dao;

import org.example.appnganhang.model.NhanVien;

import java.util.List;
import java.util.Optional;

public interface NhanVienDAO {
    boolean themNhanVien(NhanVien nhanVien);
    boolean suaNhanVien(NhanVien nhanVien);
    boolean xoaNhanVien(String maNhanVien);
    Optional<NhanVien> timNhanVien(String maNhanVien);
    List<NhanVien> getNhanVienTheoChiNhanh(String maChiNhanh);
}
