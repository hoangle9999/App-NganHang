package org.example.appnganhang.controller.service.Iml;

import org.example.appnganhang.controller.service.TaiKhoanService;
import org.example.appnganhang.model.dao.Impl.TaiKhoanDAOImpl;
import org.example.appnganhang.model.dao.TaiKhoanDAO;
import org.example.appnganhang.model.entity.TaiKhoanNganHang;

import java.util.List;
import java.util.Optional;

public class TaiKhoanServiceImpl implements TaiKhoanService {

    private final TaiKhoanDAO taiKhoanDAO;

    public TaiKhoanServiceImpl() {
        this.taiKhoanDAO = new TaiKhoanDAOImpl();
    }

    @Override
    public boolean themTaiKhoan(TaiKhoanNganHang taiKhoan) {
        if (taiKhoan == null) {
            return false;
        }

        if (taiKhoan.getMaTaiKhoan() == null || taiKhoan.getMaTaiKhoan().trim().isEmpty()) {
            return false;
        }

        if (taiKhoan.getMaKhachHang() == null || taiKhoan.getMaKhachHang().trim().isEmpty()) {
            return false;
        }

        if (taiKhoan.getSoTaiKhoan() == null || taiKhoan.getSoTaiKhoan().trim().isEmpty()) {
            return false;
        }

        if (taiKhoan.getMaChiNhanh() == null || taiKhoan.getMaChiNhanh().trim().isEmpty()) {
            return false;
        }

        if (taiKhoan.getSoDu() == null || taiKhoan.getSoDu().signum() < 0) {
            return false;
        }

        if (taiKhoanDAO.timTheoMa(taiKhoan.getMaTaiKhoan()).isPresent()) {
            return false;
        }

        if (taiKhoanDAO.timTheoSoTaiKhoan(taiKhoan.getSoTaiKhoan()).isPresent()) {
            return false;
        }

        return taiKhoanDAO.themTaiKhoan(taiKhoan);
    }

    @Override
    public boolean capNhatTaiKhoan(TaiKhoanNganHang taiKhoan) {
        if (taiKhoan == null) {
            return false;
        }

        if (taiKhoan.getMaTaiKhoan() == null || taiKhoan.getMaTaiKhoan().trim().isEmpty()) {
            return false;
        }

        if (taiKhoan.getMaKhachHang() == null || taiKhoan.getMaKhachHang().trim().isEmpty()) {
            return false;
        }

        if (taiKhoan.getSoTaiKhoan() == null || taiKhoan.getSoTaiKhoan().trim().isEmpty()) {
            return false;
        }

        if (taiKhoan.getMaChiNhanh() == null || taiKhoan.getMaChiNhanh().trim().isEmpty()) {
            return false;
        }

        if (taiKhoan.getSoDu() == null || taiKhoan.getSoDu().signum() < 0) {
            return false;
        }

        if (taiKhoanDAO.timTheoMa(taiKhoan.getMaTaiKhoan()).isEmpty()) {
            return false;
        }

        Optional<TaiKhoanNganHang> taiKhoanTonTai =
                taiKhoanDAO.timTheoSoTaiKhoan(taiKhoan.getSoTaiKhoan());

        if (taiKhoanTonTai.isPresent()
                && !taiKhoanTonTai.get().getMaTaiKhoan().equals(taiKhoan.getMaTaiKhoan())) {
            return false;
        }

        return taiKhoanDAO.capNhatTaiKhoan(taiKhoan);
    }

    @Override
    public boolean xoaTaiKhoan(String maTaiKhoan) {
        if (maTaiKhoan == null || maTaiKhoan.trim().isEmpty()) {
            return false;
        }

        if (taiKhoanDAO.timTheoMa(maTaiKhoan).isEmpty()) {
            return false;
        }

        return taiKhoanDAO.xoaTaiKhoan(maTaiKhoan);
    }

    @Override
    public Optional<TaiKhoanNganHang> timTheoMa(String maTaiKhoan) {
        if (maTaiKhoan == null || maTaiKhoan.trim().isEmpty()) {
            return Optional.empty();
        }

        return taiKhoanDAO.timTheoMa(maTaiKhoan);
    }

    @Override
    public Optional<TaiKhoanNganHang> timTheoSoTaiKhoan(String soTaiKhoan) {
        if (soTaiKhoan == null || soTaiKhoan.trim().isEmpty()) {
            return Optional.empty();
        }

        return taiKhoanDAO.timTheoSoTaiKhoan(soTaiKhoan);
    }

    @Override
    public List<TaiKhoanNganHang> timTheoMaKhachHang(String maKhachHang) {
        if (maKhachHang == null || maKhachHang.trim().isEmpty()) {
            return List.of();
        }

        return taiKhoanDAO.timTheoMaKhachHang(maKhachHang);
    }

    @Override
    public List<TaiKhoanNganHang> timTheoMaChiNhanh(String maChiNhanh) {
        if (maChiNhanh == null || maChiNhanh.trim().isEmpty()) {
            return List.of();
        }

        return taiKhoanDAO.timTheoMaChiNhanh(maChiNhanh);
    }

    @Override
    public List<TaiKhoanNganHang> timTheoTrangThai(String trangThai) {
        if (trangThai == null || trangThai.trim().isEmpty()) {
            return List.of();
        }

        return taiKhoanDAO.timTheoTrangThai(trangThai);
    }

    @Override
    public List<TaiKhoanNganHang> getDanhSachTaiKhoan() {
        return taiKhoanDAO.getDanhSachTaiKhoan();
    }

    @Override
    public boolean capNhatTrangThai(String maTaiKhoan, String trangThai) {
        if (maTaiKhoan == null || maTaiKhoan.trim().isEmpty()) {
            return false;
        }

        if (trangThai == null || trangThai.trim().isEmpty()) {
            return false;
        }

        if (taiKhoanDAO.timTheoMa(maTaiKhoan).isEmpty()) {
            return false;
        }

        return taiKhoanDAO.capNhatTrangThai(maTaiKhoan, trangThai);
    }

    @Override
    public boolean ganHanMuc(String maTaiKhoan, String maHanMuc) {
        if (maTaiKhoan == null || maTaiKhoan.trim().isEmpty()) {
            return false;
        }

        if (maHanMuc == null || maHanMuc.trim().isEmpty()) {
            return false;
        }

        if (taiKhoanDAO.timTheoMa(maTaiKhoan).isEmpty()) {
            return false;
        }

        return taiKhoanDAO.ganHanMuc(maTaiKhoan, maHanMuc);
    }
}
