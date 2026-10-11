package org.example.appnganhang.controller.service.Iml;

import org.example.appnganhang.model.dao.intf.ChiNhanhDAO;
import org.example.appnganhang.model.dao.Impl.ChiNhanhDAOImpl;
import org.example.appnganhang.model.entity.ChiNhanh;
import org.example.appnganhang.controller.service.intf.ChiNhanhService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChiNhanhServiceImpl implements ChiNhanhService {
    private final ChiNhanhDAO chiNhanhDAO;

    public ChiNhanhServiceImpl() {
        this.chiNhanhDAO = new ChiNhanhDAOImpl();
    }

    @Override
    public boolean themChiNhanh(ChiNhanh chiNhanh) {
        if (chiNhanh == null) {
            return false;
        }

        if (chiNhanh.getMaChiNhanh() == null || chiNhanh.getMaChiNhanh().trim().isEmpty()) {
            return false;
        }

        if (chiNhanh.getTenChiNhanh() == null || chiNhanh.getTenChiNhanh().trim().isEmpty()) {
            return false;
        }

        if (chiNhanh.getDiaChi() == null || chiNhanh.getDiaChi().trim().isEmpty()) {
            return false;
        }

        return chiNhanhDAO.themChiNhanh(chiNhanh);
    }

    @Override
    public boolean suaChiNhanh(ChiNhanh chiNhanh) {
        if (chiNhanh == null) {
            return false;
        }

        if (chiNhanh.getMaChiNhanh() == null || chiNhanh.getMaChiNhanh().trim().isEmpty()) {
            return false;
        }

        if (chiNhanh.getTenChiNhanh() == null || chiNhanh.getTenChiNhanh().trim().isEmpty()) {
            return false;
        }

        if (chiNhanh.getDiaChi() == null || chiNhanh.getDiaChi().trim().isEmpty()) {
            return false;
        }

        return chiNhanhDAO.suaChiNhanh(chiNhanh);
    }

    @Override
    public boolean xoaChiNhanh(String maChiNhanh) {
        if (maChiNhanh == null || maChiNhanh.trim().isEmpty()) {
            return false;
        }

        return chiNhanhDAO.xoaChiNhanh(maChiNhanh);
    }

    @Override
    public Optional<ChiNhanh> timKiemChiNhanhTheoMa(String maChiNhanh) {
        if (maChiNhanh == null || maChiNhanh.trim().isEmpty()) {
            return Optional.empty();
        }

        return chiNhanhDAO.timKiemChiNhanhTheoMa(maChiNhanh.trim());
    }

    @Override
    public List<ChiNhanh> timKiemChiNhanhTheoTen(String tenChiNhanh) {
        if (tenChiNhanh == null || tenChiNhanh.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return chiNhanhDAO.timKiemChiNhanhTheoTen(tenChiNhanh.trim());
    }

    @Override
    public List<ChiNhanh> timKiemChiNhanhTheoDiaChi(String diaChi) {
        if (diaChi == null || diaChi.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return chiNhanhDAO.timKiemChiNhanhTheoDiaChi(diaChi.trim());
    }

    @Override
    public List<ChiNhanh> timKiemChiNhanh(
            String maChiNhanh,
            String tenChiNhanh,
            String diaChi) {

        maChiNhanh = maChiNhanh == null ? "" : maChiNhanh.trim();
        tenChiNhanh = tenChiNhanh == null ? "" : tenChiNhanh.trim();
        diaChi = diaChi == null ? "" : diaChi.trim();

        return chiNhanhDAO.timKiemChiNhanh(
                maChiNhanh,
                tenChiNhanh,
                diaChi
        );
    }

    @Override
    public List<ChiNhanh> getDanhSachChiNhanh() {
        return chiNhanhDAO.getDanhSachChiNhanh();
    }
}
