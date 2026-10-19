package org.example.appnganhang.controller.service.Impl;

import org.example.appnganhang.model.dao.Impl.DangKyDAOImpl;
import org.example.appnganhang.model.dao.Impl.KhachHangDAOImpl;
import org.example.appnganhang.model.dao.Impl.TaiKhoanDangNhapDAOImpl;
import org.example.appnganhang.model.dao.intf.DangKyDAO;
import org.example.appnganhang.model.dao.intf.KhachHangDAO;
import org.example.appnganhang.model.dao.intf.TaiKhoanDangNhapDAO;
import org.example.appnganhang.model.entity.KhachHang;
import org.example.appnganhang.model.entity.TaiKhoanDangNhap_VaiTro;
import org.example.appnganhang.controller.service.intf.AuthService;

import java.util.Optional;

public class AuthServiceImpl implements AuthService {
    private final TaiKhoanDangNhapDAO taiKhoanDangNhapDAO;
    private final KhachHangDAO khachHangDAO;
    private final DangKyDAO dangKyDAO;

    private TaiKhoanDangNhap_VaiTro currentUser;

    public AuthServiceImpl() {
        this.taiKhoanDangNhapDAO = new TaiKhoanDangNhapDAOImpl();
        this.khachHangDAO = new KhachHangDAOImpl();
        this.dangKyDAO = new DangKyDAOImpl();
    }

    @Override
    public boolean login(String tenDangNhap, String matKhau) {
        Optional<TaiKhoanDangNhap_VaiTro> result = taiKhoanDangNhapDAO.login(tenDangNhap, matKhau);

        if (result.isPresent()) {
            currentUser = result.get();
            return true;
        }

        currentUser = null;
        return false;
    }

    @Override
    public boolean register(KhachHang khachHang, String matKhau) {
        if (khachHang == null) {
            return false;
        }

        if (matKhau == null || matKhau.trim().isEmpty()) {
            return false;
        }

        if (khachHang.getHoTen() == null || khachHang.getHoTen().trim().isEmpty()) {
            return false;
        }

        if (khachHang.getSoCCCD() == null || khachHang.getSoCCCD().trim().isEmpty()) {
            return false;
        }

        if (khachHang.getSoDienThoai() == null || khachHang.getSoDienThoai().trim().isEmpty()) {
            return false;
        }

        if (khachHang.getTenDangNhap() == null || khachHang.getTenDangNhap().trim().isEmpty()) {
            return false;
        }

        String tenDangNhap = khachHang.getTenDangNhap().trim();

        if (taiKhoanDangNhapDAO.findByUsername(tenDangNhap).isPresent()) {
            return false;
        }

        if (khachHangDAO.findByCCCD(khachHang.getSoCCCD()).isPresent()) {
            return false;
        }

        if (khachHangDAO.findByPhone(khachHang.getSoDienThoai()).isPresent()) {
            return false;
        }


        khachHang.setTenDangNhap(tenDangNhap);

        return dangKyDAO.dangKyKhachHang(
                khachHang,
                matKhau
        );
    }

    @Override
    public void logout() {
        currentUser = null;
    }

    @Override
    public boolean changePassword(String tenDangNhap, String matKhauCu, String matKhauMoi) {
        Optional<TaiKhoanDangNhap_VaiTro> result = taiKhoanDangNhapDAO.login(tenDangNhap,matKhauCu);

        if (result.isEmpty()) {
            return false;
        }

        return taiKhoanDangNhapDAO.changePassword(tenDangNhap, matKhauMoi);
    }

    @Override
    public boolean hasPermission(String roleName) {

        if (currentUser == null) {
            return false;
        }

        String currentRole = currentUser.getRoleName();

        return currentRole != null &&
                currentRole.equalsIgnoreCase(roleName);
    }

    @Override
    public TaiKhoanDangNhap_VaiTro getCurrentUser() {
        return currentUser;
    }
}
