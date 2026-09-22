package org.example.appnganhang.controller.service.Iml;

import org.example.appnganhang.model.dao.Impl.KhachHangDAOImpl;
import org.example.appnganhang.model.dao.KhachHangDAO;
import org.example.appnganhang.model.KhachHang;
import org.example.appnganhang.controller.service.KhachHangService;

import java.util.List;
import java.util.Optional;

public class KhachHangServiceImpl implements KhachHangService {
    private final KhachHangDAO khachHangDao;

    public KhachHangServiceImpl() {
        this.khachHangDao = new KhachHangDAOImpl();
    }

    @Override
    public boolean themKhachHang(KhachHang kh) {
        return khachHangDao.insert(kh);
    }

    @Override
    public boolean capNhatKhachHang(KhachHang kh) {
        return khachHangDao.update(kh);
    }

    @Override
    public boolean xoaKhachHang(String MaKH) {
        return khachHangDao.delete(MaKH);
    }

    @Override
    public Optional<KhachHang> timTheoMa(String maKh) {
        return khachHangDao.findById(maKh);
    }

    @Override
    public Optional<KhachHang> timTheoCCCD(String soCCCD) {
        return khachHangDao.findByCCCD(soCCCD);
    }

    @Override
    public Optional<KhachHang> timTheoSoDienThoai(String soDienThoai) {
        return khachHangDao.findByPhone(soDienThoai);
    }

    @Override
    public Optional<KhachHang> timTheoTenDangNhap(String tenDangNhap) {
        return khachHangDao.findByUsername(tenDangNhap);
    }

    @Override
    public List<KhachHang> getDanhSachKhachHang() {
        return khachHangDao.findAll();
    }
}
