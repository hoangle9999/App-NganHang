package org.example.appnganhang.controller.cskh;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.example.appnganhang.model.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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

    private void loadStats() {
        try (Connection conn = DBConnection.getConnection()) {
            // Tổng khách hàng
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM KhachHang");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    lblTongKH.setText(String.valueOf(rs.getInt(1)));
                }
            }

            // Tổng tài khoản hoạt động
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM TaiKhoanNganHang WHERE TrangThai = N'Hoạt động'");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    lblTongTKHoatDong.setText(String.valueOf(rs.getInt(1)));
                }
            }

            // Giao dịch hôm nay
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM GiaoDichChuyenTien WHERE CAST(NgayGiaoDich AS DATE) = CAST(GETDATE() AS DATE)");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    lblGDHomNay.setText(String.valueOf(rs.getInt(1)));
                }
            }

            // Khiếu nại chờ (placeholder)
            lblKhieuNaiCho.setText("0");

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
