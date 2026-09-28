package org.example.appnganhang.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Parent;
import org.example.appnganhang.MainApp;
import org.example.appnganhang.model.entity.KhachHang;
import org.example.appnganhang.controller.service.AuthService;
import org.example.appnganhang.controller.service.Iml.AuthServiceImpl;

import java.io.IOException;

public class RegisterController {
    private AuthService authService;

    public RegisterController() {
        this.authService = new AuthServiceImpl();
    }

    @FXML private TextField txtHoTen;
    @FXML private TextField txtCCCD;
    @FXML private TextField txtSoDienThoai;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTenDangNhap;
    @FXML private PasswordField txtMatKhau;
    @FXML private Label lblThongBao;

    // Xử lý sự kiện khi bấm nút XÁC NHẬN ĐĂNG KÝ
    @FXML
    protected void handleRegister() {
        String hoTen = txtHoTen.getText().trim();
        String cccd = txtCCCD.getText().trim();
        String sdt = txtSoDienThoai.getText().trim();
        String email = txtEmail.getText().trim();
        String tenDN = txtTenDangNhap.getText().trim();
        String matKhau = txtMatKhau.getText().trim();

        if (hoTen.isEmpty() || cccd.isEmpty() || sdt.isEmpty() || tenDN.isEmpty() || matKhau.isEmpty()) {
            lblThongBao.setStyle("-fx-text-fill: red;");
            lblThongBao.setText("Vui lòng điền đầy đủ các thông tin bắt buộc!");
            return;
        }

        // Tự động sinh mã khách hàng dựa trên thời gian
        String maKhachHang = "KH" + (System.currentTimeMillis() % 100000);

        KhachHang khachHang = new KhachHang();

        khachHang.setMaKhachHang(maKhachHang);
        khachHang.setHoTen(hoTen);
        khachHang.setSoCCCD(cccd);
        khachHang.setSoDienThoai(sdt);
        khachHang.setEmail(email.isEmpty() ? null : email);
        khachHang.setTenDangNhap(tenDN);

        boolean ketQua = authService.register(khachHang, matKhau);

        if (ketQua) {
            lblThongBao.setStyle("-fx-text-fill: green;");
            lblThongBao.setText("Đăng ký thành công! Bạn có thể quay lại để đăng nhập.");
        } else {
            lblThongBao.setStyle("-fx-text-fill: red;");
            lblThongBao.setText("Đăng ký thất bại! Tên đăng nhập, CCCD hoặc SĐT có thể đã tồn tại.");
        }
    }

    // Xử lý sự kiện nút Quay lại Đăng nhập

    @FXML
    protected void handleBackToLogin() {
        try {
            Stage stage = (Stage) txtHoTen.getScene().getWindow();

            FXMLLoader fxmlLoader =
                    new FXMLLoader(MainApp.class.getResource("login-view.fxml"));

            Parent root = fxmlLoader.load();

            Scene scene = new Scene(root, 500, 850);

            stage.setScene(scene);
            stage.setTitle("SMART BANK - Đăng Nhập");

            // Đưa kích thước Stage về đúng kích thước Login
            stage.setWidth(500);
            stage.setHeight(850);

            stage.setMinWidth(500);
            stage.setMinHeight(850);

            stage.setMaxWidth(500);
            stage.setMaxHeight(850);

            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}