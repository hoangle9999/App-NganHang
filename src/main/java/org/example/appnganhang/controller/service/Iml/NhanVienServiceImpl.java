package org.example.appnganhang.controller.service.Iml;

import org.example.appnganhang.model.dao.intf.NhanVienDAO;
import org.example.appnganhang.model.dao.Impl.NhanVienDAOImpl;
import org.example.appnganhang.model.entity.NhanVien;
import org.example.appnganhang.controller.service.intf.NhanVienService;

import java.util.List;
import java.util.Optional;

public class NhanVienServiceImpl implements NhanVienService {

    private final NhanVienDAO nhanVienDAO;

    public NhanVienServiceImpl() {
        this.nhanVienDAO = new NhanVienDAOImpl();
    }


    @Override
    public boolean themNhanVien(NhanVien nhanVien) {

        if (nhanVien == null) {
            return false;
        }

        if (nhanVien.getMaNhanVien() == null
                || nhanVien.getMaNhanVien().trim().isEmpty()) {
            return false;
        }

        if (nhanVien.getHoTen() == null
                || nhanVien.getHoTen().trim().isEmpty()) {
            return false;
        }

        if (nhanVien.getSoDienThoai() == null
                || nhanVien.getSoDienThoai().trim().isEmpty()) {
            return false;
        }

        if (nhanVien.getDiaChi() == null
                || nhanVien.getDiaChi().trim().isEmpty()) {
            return false;
        }

        if (nhanVien.getChucVu() == null
                || nhanVien.getChucVu().trim().isEmpty()) {
            return false;
        }

        if (nhanVien.getMaChiNhanh() == null
                || nhanVien.getMaChiNhanh().trim().isEmpty()) {
            return false;
        }

        if (nhanVien.getTenDangNhap() == null
                || nhanVien.getTenDangNhap().trim().isEmpty()) {
            return false;
        }

        return nhanVienDAO.themNhanVien(nhanVien);
    }


    @Override
    public boolean suaNhanVien(NhanVien nhanVien) {

        if (nhanVien == null) {
            return false;
        }

        if (nhanVien.getMaNhanVien() == null
                || nhanVien.getMaNhanVien().trim().isEmpty()) {
            return false;
        }

        if (nhanVien.getHoTen() == null
                || nhanVien.getHoTen().trim().isEmpty()) {
            return false;
        }

        if (nhanVien.getSoDienThoai() == null
                || nhanVien.getSoDienThoai().trim().isEmpty()) {
            return false;
        }

        if (nhanVien.getDiaChi() == null
                || nhanVien.getDiaChi().trim().isEmpty()) {
            return false;
        }

        if (nhanVien.getChucVu() == null
                || nhanVien.getChucVu().trim().isEmpty()) {
            return false;
        }

        if (nhanVien.getMaChiNhanh() == null
                || nhanVien.getMaChiNhanh().trim().isEmpty()) {
            return false;
        }

        if (nhanVien.getTenDangNhap() == null
                || nhanVien.getTenDangNhap().trim().isEmpty()) {
            return false;
        }

        return nhanVienDAO.suaNhanVien(nhanVien);
    }


    @Override
    public boolean xoaNhanVien(String maNhanVien) {

        if (maNhanVien == null
                || maNhanVien.trim().isEmpty()) {
            return false;
        }

        return nhanVienDAO.xoaNhanVien(maNhanVien);
    }


    @Override
    public Optional<NhanVien> timNhanVien(String maNhanVien) {

        if (maNhanVien == null
                || maNhanVien.trim().isEmpty()) {
            return Optional.empty();
        }

        return nhanVienDAO.timNhanVien(maNhanVien);
    }


    @Override
    public List<NhanVien> getDanhSachNhanVien() {
        return nhanVienDAO.getDanhSachNhanVien();
    }


    @Override
    public List<NhanVien> getNhanVienTheoChiNhanh(String maChiNhanh) {

        if (maChiNhanh == null
                || maChiNhanh.trim().isEmpty()) {
            return List.of();
        }

        return nhanVienDAO.getNhanVienTheoChiNhanh(maChiNhanh);
    }


    @Override
    public Optional<String> timMaChiNhanhTheoTen(String tenChiNhanh) {

        if (tenChiNhanh == null
                || tenChiNhanh.trim().isEmpty()) {
            return Optional.empty();
        }

        return nhanVienDAO.timMaChiNhanhTheoTen(
                tenChiNhanh.trim()
        );
    }

    @Override
    public List<NhanVien> timTheoHoTen(String hoTen) {
        if (hoTen == null || hoTen.trim().isEmpty()) {
            return List.of();
        }

        return nhanVienDAO.timTheoHoTen(hoTen);
    }

    @Override
    public List<NhanVien> timTheoSoDienThoai(String soDienThoai) {
        if (soDienThoai == null || soDienThoai.trim().isEmpty()) {
            return List.of();
        }

        return nhanVienDAO.timTheoSoDienThoai(soDienThoai);
    }

    @Override
    public List<NhanVien> timTheoChucVu(String chucVu) {
        if (chucVu == null || chucVu.trim().isEmpty()) {
            return List.of();
        }

        return nhanVienDAO.timTheoChucVu(chucVu);
    }

    @Override
    public List<NhanVien> timTheoTenChiNhanh(String tenChiNhanh) {
        if (tenChiNhanh == null || tenChiNhanh.trim().isEmpty()) {
            return List.of();
        }

        return nhanVienDAO.timTheoTenChiNhanh(tenChiNhanh);
    }

    @Override
    public List<NhanVien> timTheoTenDangNhap(String tenDangNhap) {
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) {
            return List.of();
        }

        return nhanVienDAO.timTheoTenDangNhap(tenDangNhap);
    }

    @Override
    public List<NhanVien> timKiemNhanVien(
            String maNhanVien,
            String hoTen,
            String soDienThoai,
            String chucVu,
            String tenChiNhanh,
            String tenDangNhap,
            String diaChi) {

        return nhanVienDAO.timKiemNhanVien(
                maNhanVien,
                hoTen,
                soDienThoai,
                chucVu,
                tenChiNhanh,
                tenDangNhap,
                diaChi
        );
    }
}
