package org.example.appnganhang.controller.service.Impl;

import org.example.appnganhang.controller.service.intf.CSKHBalanceHistoryService;
import org.example.appnganhang.model.dao.Impl.CSKHBalanceHistoryDAOImpl;
import org.example.appnganhang.model.dao.intf.CSKHBalanceHistoryDAO;
import org.example.appnganhang.model.entity.LichSuGiaoDich;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class CSKHBalanceHistoryServiceImpl implements CSKHBalanceHistoryService {
    private final CSKHBalanceHistoryDAO dao;

    public CSKHBalanceHistoryServiceImpl() {
        this.dao = new CSKHBalanceHistoryDAOImpl();
    }

    @Override
    public List<LichSuGiaoDich> layDanhSachLog(String tuKhoa, LocalDate tuNgay, LocalDate denNgay) throws SQLException {
        return dao.layDanhSachLog(tuKhoa, tuNgay, denNgay);
    }

    @Override
    public String layTenChuTaiKhoan(String soTaiKhoan) throws SQLException {
        return dao.layTenChuTaiKhoan(soTaiKhoan);
    }
}
