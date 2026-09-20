package org.example.appnganhang.service.Iml;

import org.example.appnganhang.dao.Impl.KhachHangDAOImpl;
import org.example.appnganhang.dao.Impl.TaiKhoanDangNhapDAOImpl;
import org.example.appnganhang.dao.KhachHangDAO;
import org.example.appnganhang.dao.TaiKhoanDangNhapDAO;
import org.example.appnganhang.modell.KhachHang;
import org.example.appnganhang.modell.TaiKhoanDangNhap_VaiTro;
import org.example.appnganhang.service.AuthService;

import java.util.Optional;

public class AuthServiceImpl implements AuthService {
    private final TaiKhoanDangNhapDAO taiKhoanDangNhapDAO;
    final KhachHangDAO khachHangDAO;


    private TaiKhoanDangNhap_VaiTro currentUser;

    public AuthServiceImpl() {
        this.taiKhoanDangNhapDAO = new TaiKhoanDangNhapDAOImpl();
        this.khachHangDAO = new KhachHangDAOImpl();
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

        if (khachHang.getMaKhachHang() == null || khachHang.getMaKhachHang().trim().isEmpty()) {
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

        if (khachHangDAO.findById(khachHang.getMaKhachHang()).isPresent()) {
            return false;
        }

        TaiKhoanDangNhap_VaiTro taiKhoan = new TaiKhoanDangNhap_VaiTro();

        taiKhoan.setTenDangNhap(tenDangNhap);
        taiKhoan.setMatKhau(matKhau);
        taiKhoan.setMaNguoiDung(khachHang.getMaKhachHang());

        if (!taiKhoanDangNhapDAO.insert(taiKhoan)) {
            return false;
        }

        khachHang.setTenDangNhap(tenDangNhap);

        if (!khachHangDAO.insert(khachHang)) {
            return false;
        }

        return true;
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
    public boolean hasPermission(String tenDangNhap, String roleName) {
        Optional<TaiKhoanDangNhap_VaiTro> result = taiKhoanDangNhapDAO.findByUsername(tenDangNhap);

        if (result.isEmpty()) {
            return false;
        }

        String currentRole = result.get().getRoleName();

        return currentRole != null && currentRole.equalsIgnoreCase(roleName);
    }

    @Override
    public TaiKhoanDangNhap_VaiTro getCurrentUser() {
        return currentUser;
    }
}
