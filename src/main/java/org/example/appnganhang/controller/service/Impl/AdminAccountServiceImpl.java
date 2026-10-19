package org.example.appnganhang.controller.service.Impl;

import org.example.appnganhang.controller.service.intf.AdminAccountService;
import org.example.appnganhang.model.dao.Impl.AdminAccountDAOImpl;
import org.example.appnganhang.model.dao.intf.AdminAccountDAO;
import org.example.appnganhang.model.entity.ChiNhanh;
import org.example.appnganhang.model.entity.TaiKhoanDangNhap_VaiTro;
import org.example.appnganhang.model.entity.HanMucGiaoDich;

import java.util.List;

public class AdminAccountServiceImpl implements AdminAccountService {
    private final AdminAccountDAO dao;

    public AdminAccountServiceImpl() { this.dao = new AdminAccountDAOImpl(); }

    @Override public List<ChiNhanh> layDanhSachChiNhanh() { return dao.layDanhSachChiNhanh(); }
    @Override public List<HanMucGiaoDich> layDanhSachHanMuc() { return dao.layDanhSachHanMuc(); }
    @Override public List<TaiKhoanDangNhap_VaiTro> layDanhSachTaiKhoan() { return dao.layDanhSachTaiKhoan(); }
    @Override public String taoTaiKhoan(String tenDangNhap, String matKhau, String roleName,
                                        String hoTen, String soCCCD, String soDienThoai,
                                        String email, String diaChi, String maChiNhanh, String maHanMuc) {
        if (tenDangNhap == null || tenDangNhap.isBlank()) throw new IllegalArgumentException("Tên đăng nhập không được để trống.");
        if (matKhau == null || matKhau.length() < 6) throw new IllegalArgumentException("Mật khẩu phải có ít nhất 6 ký tự.");
        if (hoTen == null || hoTen.isBlank()) throw new IllegalArgumentException("Họ tên không được để trống.");
        if (soDienThoai == null || !soDienThoai.matches("[0-9]{10,11}")) throw new IllegalArgumentException("Số điện thoại phải gồm 10 hoặc 11 chữ số.");
        if (roleName == null) throw new IllegalArgumentException("Vui lòng chọn vai trò.");
        if ("Khách hàng".equals(roleName) && (soCCCD == null || !soCCCD.matches("[0-9]{12}")))
            throw new IllegalArgumentException("CCCD khách hàng phải gồm đúng 12 chữ số.");
        if (maChiNhanh == null || maChiNhanh.isBlank()) throw new IllegalArgumentException("Vui lòng nhập mã chi nhánh hợp lệ.");
        return dao.taoTaiKhoan(tenDangNhap.trim(), matKhau, roleName, hoTen.trim(), soCCCD,
                soDienThoai, email, diaChi, maChiNhanh.trim(), maHanMuc);
    }
    @Override public void doiVaiTro(String tenDangNhap, String roleNameMoi) { dao.doiVaiTro(tenDangNhap, roleNameMoi); }
    @Override public void datLaiMatKhau(String tenDangNhap, String matKhauMoi) {
        if (matKhauMoi == null || matKhauMoi.length() < 6) throw new IllegalArgumentException("Mật khẩu mới phải có tối thiểu 6 ký tự.");
        dao.datLaiMatKhau(tenDangNhap, matKhauMoi);
    }
    @Override public void xoaTaiKhoan(String tenDangNhap) { dao.xoaTaiKhoan(tenDangNhap); }
}
