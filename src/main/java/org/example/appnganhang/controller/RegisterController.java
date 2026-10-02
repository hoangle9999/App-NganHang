package org.example.appnganhang.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class RegisterController {

    @FXML
    private TextField txtHoTen;

    @FXML
    private TextField txtCCCD;

    @FXML
    private TextField txtSoDienThoai;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtTenDangNhap;

    @FXML
    private PasswordField txtMatKhau;

    @FXML
    private Label lblThongBao;

    @FXML
    private Button btnDangKy;

    @FXML
    private void initialize() {
        lblThongBao.setText("");
    }

    // =========================================================
    // ĐĂNG KÝ
    // =========================================================

    @FXML
    private void onRegisterButtonClick() {

        String hoTen = txtHoTen.getText().trim();
        String cccd = txtCCCD.getText().trim();
        String soDienThoai = txtSoDienThoai.getText().trim();
        String email = txtEmail.getText().trim();
        String tenDangNhap = txtTenDangNhap.getText().trim();
        String matKhau = txtMatKhau.getText();

        if (hoTen.isEmpty()
                || cccd.isEmpty()
                || soDienThoai.isEmpty()
                || email.isEmpty()
                || tenDangNhap.isEmpty()
                || matKhau.isEmpty()) {

            hienThiThongBao(
                    "Vui lòng nhập đầy đủ thông tin!",
                    false
            );

            return;
        }

        if (cccd.length() != 12) {

            hienThiThongBao(
                    "CCCD phải gồm 12 số!",
                    false
            );

            return;
        }

        if (!soDienThoai.matches("\\d+")) {

            hienThiThongBao(
                    "Số điện thoại chỉ được chứa chữ số!",
                    false
            );

            return;
        }

        if (!email.contains("@")) {

            hienThiThongBao(
                    "Email không hợp lệ!",
                    false
            );

            return;
        }

        if (matKhau.length() < 6) {

            hienThiThongBao(
                    "Mật khẩu phải có ít nhất 6 ký tự!",
                    false
            );

            return;
        }

        /*
         * Hiện tại chưa kết nối Database.
         * Khi làm phần Service/DAO sẽ xử lý đăng ký
         * tài khoản thật tại đây.
         */

        hienThiThongBao(
                "Đăng ký tài khoản thành công!",
                true
        );

        lamSachForm();
    }

    // =========================================================
    // QUAY LẠI ĐĂNG NHẬP
    // =========================================================

    @FXML
    private void onBackToLogin(ActionEvent event) {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/org/example/appnganhang/util-view/login-view.fxml"
                    )
            );

            Parent root = loader.load();

            Stage stage =
                    (Stage) ((Node) event.getSource())
                            .getScene()
                            .getWindow();

            // Tạo lại Scene Login
            Scene scene = new Scene(root, 500, 850);

            // Quan trọng:
            // Register đã đặt minWidth = 1100,
            // nên phải trả giới hạn về kích thước của Login.
            stage.setMinWidth(500);
            stage.setMinHeight(850);

            stage.setScene(scene);

            stage.setWidth(500);
            stage.setHeight(850);

            stage.setResizable(false);

            stage.setTitle("SMART BANK");

            stage.centerOnScreen();

            stage.show();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    // =========================================================
    // XÓA FORM
    // =========================================================

    private void lamSachForm() {

        txtHoTen.clear();
        txtCCCD.clear();
        txtSoDienThoai.clear();
        txtEmail.clear();
        txtTenDangNhap.clear();
        txtMatKhau.clear();
    }

    // =========================================================
    // THÔNG BÁO
    // =========================================================

    private void hienThiThongBao(
            String noiDung,
            boolean thanhCong
    ) {

        lblThongBao.setText(noiDung);

        if (thanhCong) {

            lblThongBao.setStyle(
                    "-fx-font-size: 12px;" +
                            "-fx-text-fill: #2E8B57;"
            );

        } else {

            lblThongBao.setStyle(
                    "-fx-font-size: 12px;" +
                            "-fx-text-fill: #D9534F;"
            );
        }
    }
}