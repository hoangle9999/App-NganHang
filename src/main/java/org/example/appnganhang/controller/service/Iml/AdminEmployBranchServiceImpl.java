package org.example.appnganhang.controller.service.Iml;

import org.example.appnganhang.controller.service.intf.AdminEmployBranchService;
import org.example.appnganhang.model.dao.Impl.AdminEmployBranchDAOImpl;
import org.example.appnganhang.model.dao.intf.AdminEmployBranchDAO;
import org.example.appnganhang.model.entity.ChiNhanh;
import org.example.appnganhang.model.entity.NhanVien;

import java.util.List;

public class AdminEmployBranchServiceImpl implements AdminEmployBranchService {
    private final AdminEmployBranchDAO dao = new AdminEmployBranchDAOImpl();

    @Override public List<ChiNhanh> layDanhSachChiNhanh() { return dao.layDanhSachChiNhanh(); }
    @Override public List<NhanVien> layDanhSachNhanVien() { return dao.layDanhSachNhanVien(); }
    @Override public List<String> layTaiKhoanNhanVienChuaGan(String ma) { return dao.layTaiKhoanNhanVienChuaGan(ma); }
    @Override public String themNhanVien(NhanVien nv) {
        batBuoc(nv.getHoTen(), "Vui lòng nhập họ tên nhân viên.");
        batBuoc(nv.getSoDienThoai(), "Vui lòng nhập số điện thoại.");
        if (!nv.getSoDienThoai().matches("[0-9]{10,11}")) throw new IllegalArgumentException("Số điện thoại phải gồm 10 hoặc 11 chữ số.");
        batBuoc(nv.getChucVu(), "Vui lòng chọn chức vụ."); batBuoc(nv.getMaChiNhanh(), "Vui lòng chọn chi nhánh.");
        return dao.themNhanVien(nv);
    }
    @Override public void capNhatNhanVien(NhanVien nv) {
        batBuoc(nv.getMaNhanVien(), "Thiếu mã nhân viên."); batBuoc(nv.getHoTen(), "Vui lòng nhập họ tên.");
        batBuoc(nv.getSoDienThoai(), "Vui lòng nhập số điện thoại.");
        if (!nv.getSoDienThoai().matches("[0-9]{10,11}")) throw new IllegalArgumentException("Số điện thoại phải gồm 10 hoặc 11 chữ số.");
        batBuoc(nv.getChucVu(), "Vui lòng chọn chức vụ."); batBuoc(nv.getMaChiNhanh(), "Vui lòng chọn chi nhánh.");
        dao.capNhatNhanVien(nv);
    }
    @Override public void xoaNhanVien(String ma) { batBuoc(ma, "Thiếu mã nhân viên."); dao.xoaNhanVien(ma); }
    @Override public void lienKetTaiKhoanNhanVien(String ma, String tk) { batBuoc(ma, "Thiếu mã nhân viên."); batBuoc(tk, "Vui lòng chọn tài khoản."); dao.lienKetTaiKhoanNhanVien(ma, tk); }
    @Override public String themChiNhanh(ChiNhanh cn) { batBuoc(cn.getTenChiNhanh(), "Vui lòng nhập tên chi nhánh."); batBuoc(cn.getDiaChi(), "Vui lòng nhập địa chỉ chi nhánh."); return dao.themChiNhanh(cn); }
    @Override public void capNhatChiNhanh(ChiNhanh cn) { batBuoc(cn.getMaChiNhanh(), "Thiếu mã chi nhánh."); batBuoc(cn.getTenChiNhanh(), "Vui lòng nhập tên chi nhánh."); batBuoc(cn.getDiaChi(), "Vui lòng nhập địa chỉ chi nhánh."); dao.capNhatChiNhanh(cn); }
    @Override public void xoaChiNhanh(String ma) { batBuoc(ma, "Thiếu mã chi nhánh."); dao.xoaChiNhanh(ma); }

    private void batBuoc(String value, String message) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(message);
    }
}
