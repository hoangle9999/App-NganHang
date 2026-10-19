package org.example.appnganhang.model.dao.intf;

import org.example.appnganhang.model.entity.GiaoDichChuyenTien;
import org.example.appnganhang.model.entity.NhanVien;

import java.util.List;
import java.util.Optional;
import org.example.appnganhang.model.entity.TellerDashboardStats;

public interface TellerDashboardDAO {
    Optional<NhanVien> timNhanVienTheoTenDangNhap(String tenDangNhap);
    TellerDashboardStats layThongKeDashboard();
    List<GiaoDichChuyenTien> layGiaoDichGanDay(int gioiHan);
}
