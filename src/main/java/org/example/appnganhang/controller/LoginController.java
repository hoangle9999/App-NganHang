package org.example.appnganhang.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.appnganhang.MainApp;
import org.example.appnganhang.controller.service.AuthService;
import org.example.appnganhang.controller.service.Iml.AuthServiceImpl;

import java.io.IOException;

public class LoginController {
    private AuthService authService;

    public LoginController() {
        this.authService = new AuthServiceImpl();
    }

    @FXML
    private TextField txtTenDangNhap;

    @FXML
    private PasswordField txtMatKhau;

    @FXML
    private Label lblThongBao;

    // Sự kiện nút ĐĂNG NHẬP (gắn qua Scene Builder: onAction="#onLoginButtonClick")
    @FXML
    protected void onLoginButtonClick() {
        String tenDangNhap = txtTenDangNhap.getText().trim();
        String matKhau = txtMatKhau.getText().trim();

        if (tenDangNhap.isEmpty() || matKhau.isEmpty()) {
            lblThongBao.setStyle("-fx-text-fill: red;");
            lblThongBao.setText("Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        // áp dụng service vừa tạo
        if (authService.login(tenDangNhap, matKhau)){
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/appnganhang/dashboard-view.fxml"));
                Parent root = loader.load();

                // Truyền tên đăng nhập sang DashboardController
                DashboardController dashboardController = loader.getController();
                dashboardController.initData(tenDangNhap);

                // Lấy stage hiện tại và chuyển scene
                Stage stage = (Stage) txtTenDangNhap.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Hệ Thống Chuyển Tiền Ngân Hàng - Trang Chủ");
                stage.centerOnScreen();
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            lblThongBao.setStyle("-fx-text-fill: red;");
            lblThongBao.setText("Sai tên đăng nhập hoặc mật khẩu!");
        }
    }

    // Sự kiện khi bấm vào nút chuyển sang trang Đăng ký
    @FXML
    protected void onRegisterLinkClick() {
        try {
            Stage stage = (Stage) txtTenDangNhap.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("register-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 450, 550);
            stage.setTitle("Hệ Thống Chuyển Tiền Ngân Hàng - Đăng Ký");
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Sự kiện nút THOÁT
    @FXML
    protected void onExitButtonClick() {
        Stage stage = (Stage) txtTenDangNhap.getScene().getWindow();
        stage.close();
    }
}