package org.example.appnganhang.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {

    @FXML
    private Label lblWelcome;

    // Biến lưu thông tin tài khoản đăng nhập (có thể truyền từ LoginController sang)
    private String currentUser;

    public void initData(String username) {
        this.currentUser = username;
        if (lblWelcome != null) {
            lblWelcome.setText("Xin chào, " + username + "! Chào mừng bạn đến với hệ thống ngân hàng.");
        }
    }

    @FXML
    private void handleTrangChu(ActionEvent event) {
        lblWelcome.setText("Đang hiển thị: Trang chủ");
    }

    @FXML
    private void handleChuyenTien(ActionEvent event) {
        lblWelcome.setText("Đang hiển thị: Giao diện Chuyển tiền");
        // Sau này chúng ta có thể load một FXML chuyển tiền nhúng vào vùng Center tại đây
    }

    @FXML
    private void handleLichSu(ActionEvent event) {
        lblWelcome.setText("Đang hiển thị: Lịch sử giao dịch");
    }

    @FXML
    private void handleDangXuat(ActionEvent event) {
        try {
            // Load lại màn hình Đăng nhập
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Hệ Thống Chuyển Tiền Ngân Hàng - Đăng Nhập");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}