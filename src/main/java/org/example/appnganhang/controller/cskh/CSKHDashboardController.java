package org.example.appnganhang.controller.cskh;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.example.appnganhang.controller.service.Iml.CSKHDashboardServiceImpl;
import org.example.appnganhang.controller.service.intf.CSKHDashboardService;
import org.example.appnganhang.model.entity.CSKHDashboardStats;

import static org.example.appnganhang.controller.util.Navigation.moView;

public class CSKHDashboardController {

    @FXML
    private Label lblStaffName;

    @FXML
    private Label lblTongKH;

    @FXML
    private Label lblTongTKHoatDong;

    @FXML
    private Label lblGDHomNay;

    @FXML
    private Label lblKhieuNaiCho;

    @FXML
    public void initialize() {
        loadStats();
    }

    private final CSKHDashboardService dashboardService = new CSKHDashboardServiceImpl();

    private void loadStats() {
        try {
            CSKHDashboardStats stats = dashboardService.layThongKe();
            lblTongKH.setText(String.valueOf(stats.getTongKhachHang()));
            lblTongTKHoatDong.setText(String.valueOf(stats.getTongTaiKhoanHoatDong()));
            lblGDHomNay.setText(String.valueOf(stats.getGiaoDichHomNay()));
            lblKhieuNaiCho.setText(String.valueOf(stats.getKhieuNaiCho()));
        } catch (Exception e) {
            System.err.println("Lỗi khi tải thống kê dashboard CSKH: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void onDashboard(ActionEvent event) {
        try {
            moView("cskh-dashboard-view.fxml", event);
        } catch (Exception e) {
            System.err.println("Lỗi điều hướng đến Dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void onKhachHang(ActionEvent event) {
        try {
            moView("cskh-customer-view.fxml", event);
        } catch (Exception e) {
            System.err.println("Lỗi điều hướng đến Khách hàng: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void onTraCuuTaiKhoan(ActionEvent event) {
        try {
            moView("cskh-account-lookup-view.fxml", event);
        } catch (Exception e) {
            System.err.println("Lỗi điều hướng đến Tra cứu tài khoản: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void onTraCuuGiaoDich(ActionEvent event) {
        try {
            moView("cskh-transaction-lookup-view.fxml", event);
        } catch (Exception e) {
            System.err.println("Lỗi điều hướng đến Tra cứu giao dịch: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void onDoiSoatBienDong(ActionEvent event) {
        try {
            moView("cskh-balance-history-view.fxml", event);
        } catch (Exception e) {
            System.err.println("Lỗi điều hướng đến Đối soát biến động: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void onDangXuat(ActionEvent event) {
        try {
            moView("login-view.fxml", event);
        } catch (Exception e) {
            System.err.println("Lỗi điều hướng đến Đăng xuất: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
