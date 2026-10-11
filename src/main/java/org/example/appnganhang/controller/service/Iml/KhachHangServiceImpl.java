package org.example.appnganhang.controller.service.Iml;

import org.example.appnganhang.model.dao.Impl.KhachHangDAOImpl;
import org.example.appnganhang.model.dao.intf.KhachHangDAO;
import org.example.appnganhang.model.entity.KhachHang;
import org.example.appnganhang.controller.service.intf.KhachHangService;

import java.util.List;
import java.util.Optional;

public class KhachHangServiceImpl implements KhachHangService {
    private final KhachHangDAO khachHangDao;

    public KhachHangServiceImpl() {
        this.khachHangDao = new KhachHangDAOImpl();
    }

    @Override
    public String taoMaKhachHang() {
        return khachHangDao.taoMaKhachHang();
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
    public List<KhachHang> timTheoHoTen(String hoTen) {
        return khachHangDao.findByHoTen(hoTen.trim());
    }

    @Override
    public List<KhachHang> timTheoEmail(String email) {
        return khachHangDao.findByEmail(email.trim());
    }

    @Override
    public List<KhachHang> timTheoDiaChi(String diaChi) {
        return khachHangDao.findByDiaChi(diaChi.trim());
    }

    @Override
    public List<KhachHang> timKiemKhachHang(String maKhachHang, String hoTen, String soCCCD, String soDienThoai, String email, String diaChi, String tenDangNhap) {
        return khachHangDao.timKiemKhachHang(
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
        return khachHangDao.findAll();
    }
}
