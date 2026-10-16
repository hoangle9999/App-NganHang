package org.example.appnganhang.controller.service.Iml;

import org.example.appnganhang.model.dao.Impl.DangKyDAOImpl;
import org.example.appnganhang.model.dao.Impl.KhachHangDAOImpl;
import org.example.appnganhang.model.dao.Impl.TaiKhoanDangNhapDAOImpl;
import org.example.appnganhang.model.dao.intf.DangKyDAO;
import org.example.appnganhang.model.dao.intf.KhachHangDAO;
import org.example.appnganhang.model.dao.intf.TaiKhoanDangNhapDAO;
import org.example.appnganhang.model.entity.KhachHang;
import org.example.appnganhang.controller.service.intf.KhachHangService;

import java.util.List;
import java.util.Optional;

public class KhachHangServiceImpl implements KhachHangService {
    private final KhachHangDAO khachHangDAO;
    private final DangKyDAO dangKyDAO;
    private final TaiKhoanDangNhapDAO taiKhoanDangNhapDAO;

    public KhachHangServiceImpl() {
        this.taiKhoanDangNhapDAO = new TaiKhoanDangNhapDAOImpl();
        this.khachHangDAO = new KhachHangDAOImpl();
        this.dangKyDAO = new DangKyDAOImpl();
    }

    @Override
    public String taoMaKhachHang() {
        return khachHangDAO.taoMaKhachHang();
    }

    @Override
    public boolean taoKhachHangTuNhanVien(
            String hoTen,
            String soCCCD,
            String soDienThoai,
            String email,
            String diaChi,
            String tenDangNhap,
            String matKhau,
            String maChiNhanh
    ) {
        if (hoTen == null || hoTen.isBlank()
                || soCCCD == null || soCCCD.isBlank()
                || soDienThoai == null || soDienThoai.isBlank()
                || tenDangNhap == null || tenDangNhap.isBlank()
                || matKhau == null || matKhau.isBlank()
                || maChiNhanh == null || maChiNhanh.isBlank()) {
            return false;
        }

        tenDangNhap = tenDangNhap.trim();
        soCCCD = soCCCD.trim();
        soDienThoai = soDienThoai.trim();
        maChiNhanh = maChiNhanh.trim();

        if (taiKhoanDangNhapDAO.findByUsername(tenDangNhap).isPresent()) {
            return false;
        }

        KhachHang khachHang = new KhachHang(
                null,
                hoTen.trim(),
                soCCCD,
                soDienThoai,
                email == null || email.isBlank() ? null : email.trim(),
                diaChi == null || diaChi.isBlank() ? null : diaChi.trim(),
                tenDangNhap
        );

        if (khachHangDAO.findByCCCD(khachHang.getSoCCCD()).isPresent()) {
            return false;
        }

        if (khachHangDAO.findByPhone(khachHang.getSoDienThoai()).isPresent()) {
            return false;
        }

        return dangKyDAO.taoKhachHangTuNhanVien(
                khachHang,
                matKhau,
                maChiNhanh
        );
    }

    @Override
    public boolean themKhachHang(KhachHang kh) {
        return khachHangDAO.insert(kh);
    }

    @Override
    public boolean capNhatKhachHang(KhachHang kh) {
        return khachHangDAO.update(kh);
    }

    @Override
    public boolean xoaKhachHang(String MaKH) {
        return khachHangDAO.delete(MaKH);
    }

    @Override
    public List<KhachHang> timKiemKhachHang(String maKhachHang, String hoTen, String soCCCD, String soDienThoai, String email, String diaChi, String tenDangNhap) {
        return khachHangDAO.timKiemKhachHang(
                maKhachHang,
                hoTen,
                soCCCD,
                soDienThoai,
                email,
                diaChi,
                tenDangNhap
        );
    }

    @Override
    public List<KhachHang> getDanhSachKhachHang() {
        return khachHangDAO.findAll();
    }
}
