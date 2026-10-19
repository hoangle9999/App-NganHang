package org.example.appnganhang.controller.admin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.example.appnganhang.controller.service.Impl.AdminDashboardServiceImpl;
import org.example.appnganhang.controller.service.intf.AdminDashboardService;
import org.example.appnganhang.model.entity.AdminDashboardStats;

import java.text.DecimalFormat;

import static org.example.appnganhang.controller.util.Navigation.moView;

public class AdminViewController {

    @FXML private Label lblAdminName;
    @FXML private Label lblStatTaiKhoan;
    @FXML private Label lblStatNhanVien;
    @FXML private Label lblStatGiaoDich;
    @FXML private Label lblStatCanhBao;

    private final AdminDashboardService dashboardService = new AdminDashboardServiceImpl();

    private final DecimalFormat dfTien = new DecimalFormat("#,### VNĐ");

    @FXML
    public void initialize() {
        taiThongKeTongQuan();
    }

    private void taiThongKeTongQuan() {
        try {
            AdminDashboardStats stats = dashboardService.layThongKeDashboard();

            if (lblStatTaiKhoan != null) {
                lblStatTaiKhoan.setText(
                        stats.getTongTaiKhoan() + " tài khoản"
                );
            }

            if (lblStatNhanVien != null) {
                lblStatNhanVien.setText(
                        stats.getTongNhanVien() + " NV / "
                                + stats.getTongChiNhanh() + " CN"
                );
            }

            if (lblStatGiaoDich != null) {
                lblStatGiaoDich.setText(
                        dfTien.format(stats.getTongTienGiaoDich())
                );
            }

            if (lblStatCanhBao != null) {
                lblStatCanhBao.setText(
                        stats.getTongCanhBao() + " tài khoản bị khóa"
                );
            }

        } catch (RuntimeException e) {
            System.err.println(
                    "Lỗi nạp thống kê Dashboard: " + e.getMessage()
            );

            // Không làm ứng dụng bị dừng nếu database xảy ra lỗi.
            if (lblStatTaiKhoan != null) {
                lblStatTaiKhoan.setText("Không tải được");
            }

            if (lblStatNhanVien != null) {
                lblStatNhanVien.setText("Không tải được");
            }

            if (lblStatGiaoDich != null) {
                lblStatGiaoDich.setText("Không tải được");
            }

            if (lblStatCanhBao != null) {
                lblStatCanhBao.setText("Không tải được");
            }
        }
    }

    @FXML
    private void onTrangChu(ActionEvent event) {
        // Đang ở dashboard
    }

    @FXML
    private void onTaiKhoan(ActionEvent event) {
        moView("admin-account-view.fxml", event);
    }

    @FXML
    private void onNhanVienChiNhanh(ActionEvent event) {
        moView("admin-employee-branch-view.fxml", event);
    }

    @FXML
    private void onCauHinhDanhMuc(ActionEvent event) {
        moView("admin-system-config-view.fxml", event);
    }

    @FXML
    private void onThongKeBaoCao(ActionEvent event) {
        moView("admin-report-view.fxml", event);
    }

    @FXML
    private void onDangXuat(ActionEvent event) {
        moView("login-view.fxml", event);
    }
}
