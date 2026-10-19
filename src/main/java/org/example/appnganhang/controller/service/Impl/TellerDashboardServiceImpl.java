package org.example.appnganhang.controller.service.Impl;

import org.example.appnganhang.controller.service.intf.TellerDashboardService;
import org.example.appnganhang.model.dao.Impl.TellerDashboardDAOImpl;
import org.example.appnganhang.model.dao.intf.TellerDashboardDAO;
import org.example.appnganhang.model.entity.GiaoDichChuyenTien;
import org.example.appnganhang.model.entity.NhanVien;

import java.util.List;
import java.util.Optional;
import org.example.appnganhang.model.entity.TellerDashboardStats;

public class TellerDashboardServiceImpl implements TellerDashboardService {
    private final TellerDashboardDAO dao = new TellerDashboardDAOImpl();
    @Override public Optional<NhanVien> timNhanVienTheoTenDangNhap(String username) { return dao.timNhanVienTheoTenDangNhap(username); }
    @Override public TellerDashboardStats layThongKeDashboard() { return dao.layThongKeDashboard(); }
    @Override public List<GiaoDichChuyenTien> layGiaoDichGanDay(int gioiHan) { return dao.layGiaoDichGanDay(gioiHan); }
}
