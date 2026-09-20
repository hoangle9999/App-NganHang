package org.example.appnganhang.service.Iml;

import org.example.appnganhang.dao.LoaiGiaoDichDAO;
import org.example.appnganhang.dao.Impl.LoaiGiaoDichDAOImpl;
import org.example.appnganhang.modell.LoaiGiaoDich;
import org.example.appnganhang.service.LoaiGiaoDichService;

import java.util.List;

public class LoaiGiaoDichServiceImpl implements LoaiGiaoDichService {

    private final LoaiGiaoDichDAO loaiGiaoDichDAO;

    public LoaiGiaoDichServiceImpl() {
        this.loaiGiaoDichDAO = new LoaiGiaoDichDAOImpl();
    }

    @Override
    public boolean themLoaiGiaoDich(LoaiGiaoDich loaiGiaoDich) {
        if (loaiGiaoDich == null) {
            return false;
        }

        if (loaiGiaoDich.getMaLoaiGD() == null ||
                loaiGiaoDich.getMaLoaiGD().trim().isEmpty()) {
            return false;
        }

        if (loaiGiaoDich.getTenLoaiGD() == null ||
                loaiGiaoDich.getTenLoaiGD().trim().isEmpty()) {
            return false;
        }

        return loaiGiaoDichDAO.themLoaiGiaoDich(loaiGiaoDich);
    }

    @Override
    public boolean suaLoaiGiaoDich(LoaiGiaoDich loaiGiaoDich) {
        if (loaiGiaoDich == null) {
            return false;
        }

        if (loaiGiaoDich.getMaLoaiGD() == null ||
                loaiGiaoDich.getMaLoaiGD().trim().isEmpty()) {
            return false;
        }

        if (loaiGiaoDich.getTenLoaiGD() == null ||
                loaiGiaoDich.getTenLoaiGD().trim().isEmpty()) {
            return false;
        }

        return loaiGiaoDichDAO.suaLoaiGiaoDich(loaiGiaoDich);
    }

    @Override
    public boolean xoaLoaiGiaoDich(String maLoaiGD) {
        if (maLoaiGD == null || maLoaiGD.trim().isEmpty()) {
            return false;
        }

        return loaiGiaoDichDAO.xoaLoaiGiaoDich(maLoaiGD);
    }

    @Override
    public List<LoaiGiaoDich> getDanhSachLoaiGiaoDich() {
        return loaiGiaoDichDAO.getDanhSachLoaiGiaoDich();
    }
}