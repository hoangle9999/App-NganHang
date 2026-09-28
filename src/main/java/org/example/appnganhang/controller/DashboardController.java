package org.example.appnganhang.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class DashboardController {

    @FXML
    private Label lblWelcome;

    @FXML
    private StackPane contentArea;

    @FXML
    private Button btnKhachHang;

    private String currentUser;

    public void initData(String username) {
        this.currentUser = username;

        if (lblWelcome != null) {
            lblWelcome.setText("Xin chào, " + username + "!");
        }
    }

    @FXML
    private void handleTrangChu(ActionEvent event) {
        System.out.println("Trang chủ");
    }

    @FXML
    private void handleChuyenTien(ActionEvent event) {
        System.out.println("Chuyển tiền");
    }

    @FXML
    private void handleLichSu(ActionEvent event) {
        System.out.println("Lịch sử giao dịch");
    }

    @FXML
    private void handleDangXuat(ActionEvent event) {
        dangXuat(event);
    }

    @FXML
    protected void onKhachHang() {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/org/example/appnganhang/khachhang-view.fxml"
                    )
            );

            Parent root = loader.load();

            // Chỉ thay phần bên phải
            contentArea.getChildren().setAll(root);

            // Đổi màu nút Khách hàng
            setActiveButton(btnKhachHang);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setActiveButton(Button activeButton) {

        if (activeButton == null) {
            return;
        }

        activeButton.setStyle(
                "-fx-background-color: rgba(255,255,255,0.16);" +
                        "-fx-background-radius: 11;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 12 15;" +
                        "-fx-cursor: hand;"
        );
    }

    @FXML
    private void onTaiKhoan() {
        System.out.println("Quản lý tài khoản");
    }

    @FXML
    private void onChuyenTien() {
        System.out.println("Chuyển tiền");
    }

    @FXML
    private void onTraCuuGiaoDich() {
        System.out.println("Tra cứu giao dịch");
    }

    @FXML
    private void onLichSuGiaoDich() {
        System.out.println("Lịch sử giao dịch");
    }

    @FXML
    private void onHanMuc() {
        System.out.println("Quản lý hạn mức");
    }

    @FXML
    private void onLoaiGiaoDich() {
        System.out.println("Loại giao dịch");
    }

    @FXML
    private void onChiNhanh() {
        System.out.println("Quản lý chi nhánh");
    }

    @FXML
    private void onNhanVien() {
        System.out.println("Quản lý nhân viên");
    }

    @FXML
    private void onBaoCao() {
        System.out.println("Báo cáo");
    }

    @FXML
    private void onDoiMatKhau() {
        System.out.println("Đổi mật khẩu");
    }

    @FXML
    private void onDangXuat() {
        dangXuat(null);
    }

    private void dangXuat(ActionEvent event) {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/org/example/appnganhang/login-view.fxml"
                    )
            );

            Parent root = loader.load();

            Node node;

            if (event != null) {
                node = (Node) event.getSource();
            } else {
                node = lblWelcome;
            }

            node.getScene().setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}