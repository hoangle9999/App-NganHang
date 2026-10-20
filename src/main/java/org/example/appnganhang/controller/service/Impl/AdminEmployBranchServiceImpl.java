package org.example.appnganhang.controller.service.Impl;

import org.example.appnganhang.controller.service.intf.AdminEmployBranchService;
import org.example.appnganhang.model.dao.Impl.AdminEmployBranchDAOImpl;
import org.example.appnganhang.model.dao.intf.AdminEmployBranchDAO;
import org.example.appnganhang.model.entity.ChiNhanh;
import org.example.appnganhang.model.entity.NhanVien;

import java.util.List;

public class AdminEmployBranchServiceImpl implements AdminEmployBranchService {
    private final AdminEmployBranchDAO dao;

    public AdminEmployBranchServiceImpl() {
        this.dao = new AdminEmployBranchDAOImpl();
    }

    @Override
    public List<ChiNhanh> layDanhSachChiNhanh() {
        return dao.layDanhSachChiNhanh();
    }

    @Override
    public List<NhanVien> layDanhSachNhanVien() {
        return dao.layDanhSachNhanVien();
    }

    @Override
    public List<String> layTaiKhoanNhanVienChuaGan(String maNhanVien) {
        return dao.layTaiKhoanNhanVienChuaGan(maNhanVien);
    }

    @Override
    public String themNhanVien(NhanVien nhanVien) {
        kiemTraThongTinNhanVien(nhanVien);
        return dao.themNhanVien(nhanVien);
    }

    @Override
    public void capNhatNhanVien(NhanVien nhanVien) {
        batBuoc(nhanVien.getMaNhanVien(), "Thiếu mã nhân viên.");
        kiemTraThongTinNhanVien(nhanVien);
        dao.capNhatNhanVien(nhanVien);
    }

    private void kiemTraThongTinNhanVien(NhanVien nhanVien) {
        batBuoc(nhanVien.getHoTen(), "Vui lòng nhập họ tên nhân viên.");
        batBuoc(nhanVien.getSoDienThoai(), "Vui lòng nhập số điện thoại.");
        if (!nhanVien.getSoDienThoai().matches("[0-9]{10,11}")) {
            throw new IllegalArgumentException("Số điện thoại phải gồm 10 hoặc 11 chữ số.");
        }
        batBuoc(nhanVien.getChucVu(), "Vui lòng chọn chức vụ.");
        batBuoc(nhanVien.getMaChiNhanh(), "Vui lòng chọn chi nhánh.");
    }

    @Override
    public void xoaNhanVien(String maNhanVien) {
        batBuoc(maNhanVien, "Thiếu mã nhân viên.");
        dao.xoaNhanVien(maNhanVien);
    }

    @Override
    public void lienKetTaiKhoanNhanVien(String maNhanVien, String tenDangNhap) {
        batBuoc(maNhanVien, "Thiếu mã nhân viên.");
        batBuoc(tenDangNhap, "Vui lòng chọn tài khoản.");
        dao.lienKetTaiKhoanNhanVien(maNhanVien, tenDangNhap);
    }

    @Override
    public String themChiNhanh(ChiNhanh chiNhanh) {
        batBuoc(chiNhanh.getTenChiNhanh(), "Vui lòng nhập tên chi nhánh.");
        batBuoc(chiNhanh.getDiaChi(), "Vui lòng nhập địa chỉ chi nhánh.");
        return dao.themChiNhanh(chiNhanh);
    }

    @Override
    public void capNhatChiNhanh(ChiNhanh chiNhanh) {
        batBuoc(chiNhanh.getMaChiNhanh(), "Thiếu mã chi nhánh.");
        batBuoc(chiNhanh.getTenChiNhanh(), "Vui lòng nhập tên chi nhánh.");
        batBuoc(chiNhanh.getDiaChi(), "Vui lòng nhập địa chỉ chi nhánh.");
        dao.capNhatChiNhanh(chiNhanh);
    }

    @Override
    public void xoaChiNhanh(String maChiNhanh) {
        batBuoc(maChiNhanh, "Thiếu mã chi nhánh.");
        dao.xoaChiNhanh(maChiNhanh);
    }

    private void batBuoc(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
}
