package org.example.appnganhang.service.Iml;

import org.example.appnganhang.dao.BaoCaoDAO;
import org.example.appnganhang.dao.Impl.BaoCaoDAOImpl;
import org.example.appnganhang.service.BaoCaoService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class BaoCaoServiceImpl implements BaoCaoService {
    private final BaoCaoDAO baoCaoDAO;

    public BaoCaoServiceImpl() {
        this.baoCaoDAO = new BaoCaoDAOImpl();
    }

    @Override
    public int demTongSoGiaoDich() {
        return baoCaoDAO.demTongSoGiaoDich();
    }

    @Override
    public BigDecimal tinhTongTienChuyen() {
        return baoCaoDAO.tinhTongTienChuyen();
    }

    @Override
    public int demGiaoDichThanhCong() {
        return baoCaoDAO.demGiaoDichThanhCong();
    }

    @Override
    public int demGiaoDichThatBai() {
        return baoCaoDAO.demGiaoDichThatBai();
    }

    @Override
    public Map<LocalDate, Integer> thongKeGiaoDichTheoNgay() {
        return baoCaoDAO.thongKeGiaoDichTheoNgay();
    }

    @Override
    public Map<String, Integer> thongKeGiaoDichTheoLoai() {
        return baoCaoDAO.thongKeGiaoDichTheoLoai();
    }

    @Override
    public Map<String, Integer> thongKeGiaoDichTheoChiNhanh() {
        return baoCaoDAO.thongKeGiaoDichTheoChiNhanh();
    }

    @Override
    public List<Map<String, Object>> baoCaoGiaoDich() {
        return baoCaoDAO.baoCaoGiaoDich();
    }
}