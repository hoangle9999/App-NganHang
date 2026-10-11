package org.example.appnganhang.controller.service.intf;

import org.example.appnganhang.model.entity.KhachHang;

import java.util.List;
import java.util.Optional;

public interface KhachHangService {
    String taoMaKhachHang();
    boolean themKhachHang(KhachHang kh);
    boolean capNhatKhachHang(KhachHang kh);
    boolean xoaKhachHang(String kh);
    Optional<KhachHang> timTheoMa(String maKh);
    Optional<KhachHang> timTheoCCCD(String soCCCD);
    Optional<KhachHang> timTheoSoDienThoai(String soDienThoai);
    Optional<KhachHang> timTheoTenDangNhap(String tenDangNhap);
    List<KhachHang> timTheoHoTen(String hoTen);
    List<KhachHang> timTheoEmail(String email);
    List<KhachHang> timTheoDiaChi(String diaChi);
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
