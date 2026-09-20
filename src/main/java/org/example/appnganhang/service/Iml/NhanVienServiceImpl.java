package org.example.appnganhang.service.Iml;

import org.example.appnganhang.dao.NhanVienDAO;
import org.example.appnganhang.dao.Impl.NhanVienDAOImpl;
import org.example.appnganhang.modell.NhanVien;
import org.example.appnganhang.service.NhanVienService;

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

        if (nhanVien.getMaNhanVien() == null || nhanVien.getMaNhanVien().trim().isEmpty()) {
            return false;
        }

        if (nhanVien.getHoTen() == null || nhanVien.getHoTen().trim().isEmpty()) {
            return false;
        }

        if (nhanVien.getChucVu() == null || nhanVien.getChucVu().trim().isEmpty()) {
            return false;
        }

        if (nhanVien.getMaChiNhanh() == null || nhanVien.getMaChiNhanh().trim().isEmpty()) {
            return false;
        }

        return nhanVienDAO.themNhanVien(nhanVien);
    }

    @Override
    public boolean suaNhanVien(NhanVien nhanVien) {
        if (nhanVien == null) {
            return false;
        }

        if (nhanVien.getMaNhanVien() == null || nhanVien.getMaNhanVien().trim().isEmpty()) {
            return false;
        }

        if (nhanVien.getHoTen() == null || nhanVien.getHoTen().trim().isEmpty()) {
            return false;
        }

        if (nhanVien.getChucVu() == null || nhanVien.getChucVu().trim().isEmpty()) {
            return false;
        }

        if (nhanVien.getMaChiNhanh() == null || nhanVien.getMaChiNhanh().trim().isEmpty()) {
            return false;
        }

        return nhanVienDAO.suaNhanVien(nhanVien);
    }

    @Override
    public boolean xoaNhanVien(String maNhanVien) {
        if (maNhanVien == null || maNhanVien.trim().isEmpty()) {
            return false;
        }

        return nhanVienDAO.xoaNhanVien(maNhanVien);
    }

    @Override
    public Optional<NhanVien> timNhanVien(String maNhanVien) {
        if (maNhanVien == null || maNhanVien.trim().isEmpty()) {
            return Optional.empty();
        }

        return nhanVienDAO.timNhanVien(maNhanVien);
    }

    @Override
    public List<NhanVien> getNhanVienTheoChiNhanh(String maChiNhanh) {
        if (maChiNhanh == null || maChiNhanh.trim().isEmpty()) {
            return List.of();
        }

        return nhanVienDAO.getNhanVienTheoChiNhanh(maChiNhanh);
    }
}