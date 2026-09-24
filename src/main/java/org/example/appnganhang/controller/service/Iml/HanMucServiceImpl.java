package org.example.appnganhang.controller.service.Iml;

import org.example.appnganhang.model.dao.HanMucDAO;
import org.example.appnganhang.model.dao.Impl.HanMucDAOImpl;
import org.example.appnganhang.model.entity.HanMucGiaoDich;
import org.example.appnganhang.controller.service.HanMucService;

import java.math.BigDecimal;
import java.util.Optional;

public class HanMucServiceImpl implements HanMucService {

    private final HanMucDAO hanMucDAO;

    public HanMucServiceImpl() {
        this.hanMucDAO = new HanMucDAOImpl();
    }

    @Override
    public boolean themHanMuc(HanMucGiaoDich hanMuc) {
        if (hanMuc == null) {
            return false;
        }

        if (hanMuc.getMaHanMuc() == null ||
                hanMuc.getMaHanMuc().trim().isEmpty()) {
            return false;
        }

        if (hanMuc.getTenHanMuc().trim().isEmpty()) {
            return false;
        }

        if (hanMuc.getHanMucNgay().compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        return hanMucDAO.themHanMuc(hanMuc);
    }

    @Override
    public boolean suaHanMuc(HanMucGiaoDich hanMuc) {
        if (hanMuc == null) {
            return false;
        }

        if (hanMuc.getMaHanMuc() == null ||
                hanMuc.getMaHanMuc().trim().isEmpty()) {
            return false;
        }

        if (hanMuc.getTenHanMuc().trim().isEmpty()) {
            return false;
        }

        if (hanMuc.getHanMucNgay().compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        return hanMucDAO.suaHanMuc(hanMuc);
    }

    @Override
    public boolean xoaHanMuc(String maHanMuc) {
        if (maHanMuc == null || maHanMuc.trim().isEmpty()) {
            return false;
        }
        return hanMucDAO.xoaHanMuc(maHanMuc);
    }

    @Override
    public Optional<HanMucGiaoDich> getHanMuc(String maHanMuc) {
        if (maHanMuc == null || maHanMuc.trim().isEmpty()) {
            return Optional.empty();
        }
        return hanMucDAO.getHanMuc(maHanMuc);
    }

}