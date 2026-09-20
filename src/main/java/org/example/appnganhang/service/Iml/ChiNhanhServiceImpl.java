package org.example.appnganhang.service.Iml;

import org.example.appnganhang.dao.ChiNhanhDAO;
import org.example.appnganhang.dao.Impl.ChiNhanhDAOImpl;
import org.example.appnganhang.modell.ChiNhanh;
import org.example.appnganhang.service.ChiNhanhService;

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
    public Optional<ChiNhanh> timKiemChiNhanh(String maChiNhanh) {
        if (maChiNhanh == null || maChiNhanh.trim().isEmpty()) {
            return Optional.empty();
        }

        return chiNhanhDAO.timKiemChiNhanh(maChiNhanh);
    }

    @Override
    public List<ChiNhanh> getDanhSachChiNhanh() {
        return chiNhanhDAO.getDanhSachChiNhanh();
    }
}
