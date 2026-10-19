package org.example.appnganhang.controller.service.intf;

import org.example.appnganhang.model.entity.ChiNhanh;
import org.example.appnganhang.model.entity.HanMucGiaoDich;
import org.example.appnganhang.model.entity.KhachHang;
import org.example.appnganhang.model.entity.TaiKhoanNganHang;
import org.example.appnganhang.model.entity.TellerAccountInfo;

import java.util.List;
import java.util.Optional;

public interface TellerAccountService {
    List<TellerAccountInfo> layDanhSachTaiKhoan();
    List<ChiNhanh> layDanhSachChiNhanh();
    List<HanMucGiaoDich> layDanhSachHanMuc();
    Optional<KhachHang> timKhachHangTheoCCCD(String soCCCD);
    String taoHoSoKhachHang(String tenDangNhap, String matKhau, String hoTen, String soCCCD,
                            String soDienThoai, String email, String diaChi);
    String taoMaTaiKhoan();
    boolean moTaiKhoan(TaiKhoanNganHang taiKhoan);
    void doiTrangThai(String maTaiKhoan, String trangThai);
    void ganHanMuc(String maTaiKhoan, String maHanMuc);
}
