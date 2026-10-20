package org.example.appnganhang.controller.util;

import javafx.scene.control.Label;
import org.example.appnganhang.controller.cskh.CSKHSession;
import org.example.appnganhang.controller.giaodichvien.TellerSession;
import org.example.appnganhang.controller.service.Impl.TellerDashboardServiceImpl;
import org.example.appnganhang.model.entity.NhanVien;

public final class StaffHeader {
    private static final TellerDashboardServiceImpl SERVICE = new TellerDashboardServiceImpl();

    private StaffHeader() {}

    public static boolean loadCustomerService(Label nameLabel) {
        return load(nameLabel, null, CSKHSession.getTenDangNhap(), "Nhân viên CSKH");
    }

    public static boolean loadTeller(Label nameLabel, Label branchLabel) {
        return load(nameLabel, branchLabel, TellerSession.getTenDangNhap(), "Giao dịch viên");
    }

    private static boolean load(Label nameLabel, Label branchLabel, String username, String defaultName) {
        if (nameLabel == null) return false;
        if (username == null || username.isBlank()) {
            nameLabel.setText(defaultName);
            if (branchLabel != null) branchLabel.setText("Chưa xác định chi nhánh");
            return false;
        }

        try {
            NhanVien staff = SERVICE.timNhanVienTheoTenDangNhap(username).orElse(null);
            nameLabel.setText(staff == null || staff.getHoTen() == null || staff.getHoTen().isBlank()
                    ? username : staff.getHoTen());
            if (branchLabel != null) {
                branchLabel.setText(staff == null || staff.getTenChiNhanh() == null || staff.getTenChiNhanh().isBlank()
                        ? "Chưa phân công chi nhánh" : staff.getTenChiNhanh());
            }
            return staff != null;
        } catch (RuntimeException e) {
            nameLabel.setText(username);
            if (branchLabel != null) branchLabel.setText("Chưa tải được chi nhánh");
            System.err.println("Không thể tải thông tin nhân viên: " + e.getMessage());
            return false;
        }
    }
}
