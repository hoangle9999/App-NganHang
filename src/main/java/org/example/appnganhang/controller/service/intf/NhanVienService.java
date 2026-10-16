package org.example.appnganhang.controller.service.intf;

import org.example.appnganhang.model.entity.NhanVien;

import java.util.List;
import java.util.Optional;

public interface NhanVienService {
    boolean themNhanVien(NhanVien nhanVien);
    boolean suaNhanVien(NhanVien nhanVien);
    boolean xoaNhanVien(String maNhanVien);
    Optional<NhanVien> timNhanVien(String maNhanVien);
    List<NhanVien> getNhanVienTheoChiNhanh(String maChiNhanh);
    List<NhanVien> getDanhSachNhanVien();
    Optional<String> timMaChiNhanhTheoTen(String tenChiNhanh);
    List<NhanVien> timTheoHoTen(String hoTen);
    List<NhanVien> timTheoSoDienThoai(String soDienThoai);
    List<NhanVien> timTheoChucVu(String chucVu);
    List<NhanVien> timTheoTenChiNhanh(String tenChiNhanh);
    List<NhanVien> timTheoTenDangNhap(String tenDangNhap);
    List<NhanVien> timKiemNhanVien(
            String maNhanVien,
            String hoTen,
            String soDienThoai,
            String chucVu,
            String tenChiNhanh,
            String tenDangNhap,
            String diaChi
    );
}
