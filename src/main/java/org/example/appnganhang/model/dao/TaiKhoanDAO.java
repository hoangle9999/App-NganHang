package org.example.appnganhang.model.dao;

import org.example.appnganhang.model.entity.TaiKhoanNganHang;

import java.util.List;
import java.util.Optional;

public interface TaiKhoanDAO {
    boolean themTaiKhoan(TaiKhoanNganHang taiKhoan);
    boolean capNhatTaiKhoan(TaiKhoanNganHang taiKhoan);
    boolean xoaTaiKhoan(String maTaiKhoan);
    Optional<TaiKhoanNganHang> timTheoMa(String maTaiKhoan);
    Optional<TaiKhoanNganHang> timTheoSoTaiKhoan(String soTaiKhoan);
    List<TaiKhoanNganHang> timTheoMaKhachHang(String maKhachHang);
    List<TaiKhoanNganHang> timTheoMaChiNhanh(String maChiNhanh);
    List<TaiKhoanNganHang> timTheoTrangThai(String trangThai);
    List<TaiKhoanNganHang> getDanhSachTaiKhoan();
    boolean capNhatTrangThai(String maTaiKhoan, String trangThai);
    boolean ganHanMuc(String maTaiKhoan, String maHanMuc);
}
