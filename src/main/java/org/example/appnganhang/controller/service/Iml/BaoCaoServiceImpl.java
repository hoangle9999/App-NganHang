package org.example.appnganhang.controller.service.Iml;

import org.example.appnganhang.model.dao.intf.BaoCaoDAO;
import org.example.appnganhang.model.dao.Impl.BaoCaoDAOImpl;
import org.example.appnganhang.controller.service.intf.BaoCaoService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.example.appnganhang.model.entity.GiaoDichChuyenTien;
import org.example.appnganhang.model.entity.LichSuGiaoDich;

public class BaoCaoServiceImpl implements BaoCaoService {
    private final BaoCaoDAO baoCaoDAO;

    public BaoCaoServiceImpl() {
        this.baoCaoDAO = new BaoCaoDAOImpl();
    }

    @Override public List<GiaoDichChuyenTien> layDanhSachGiaoDich() { return baoCaoDAO.layDanhSachGiaoDich(); }
    @Override public List<LichSuGiaoDich> layDanhSachLichSuGiaoDich() { return baoCaoDAO.layDanhSachLichSuGiaoDich(); }
    @Override public List<String> layDanhSachLoaiGiaoDich() { return baoCaoDAO.layDanhSachLoaiGiaoDich(); }
    @Override public List<String> layDanhSachNganHang() { return baoCaoDAO.layDanhSachNganHang(); }
}
