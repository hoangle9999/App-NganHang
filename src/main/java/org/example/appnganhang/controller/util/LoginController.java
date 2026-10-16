package org.example.appnganhang.controller.util;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.appnganhang.MainApp;
import org.example.appnganhang.controller.nhanvien.DashboardController;
import org.example.appnganhang.controller.service.intf.AuthService;
import org.example.appnganhang.controller.service.Iml.AuthServiceImpl;
import org.example.appnganhang.model.entity.TaiKhoanDangNhap_VaiTro;

import java.io.IOException;

public class LoginController {
    private AuthService authService;

    public LoginController() {
        this.authService = new AuthServiceImpl();
    }

    @FXML private TextField txtTenDangNhap;
    @FXML private PasswordField txtMatKhau;
    @FXML private Label lblThongBao;

    @FXML
    private void initialize(){
        txtTenDangNhap.setOnAction(event -> txtMatKhau.requestFocus());
    }

    @FXML
    protected void onLoginButtonClick() {
        String tenDangNhap = txtTenDangNhap.getText().trim();
        String matKhau = txtMatKhau.getText().trim();

        if (tenDangNhap.isEmpty() || matKhau.isEmpty()) {
            lblThongBao.setStyle("-fx-text-fill: red;");
            lblThongBao.setText("Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        if (authService.login(tenDangNhap, matKhau)){


            TaiKhoanDangNhap_VaiTro currentUser = authService.getCurrentUser();

            if (currentUser == null) {
                lblThongBao.setStyle("-fx-text-fill: red;");
                lblThongBao.setText("Không lấy được thông tin tài khoản!");
                return;
            }

            // Lấy RoleName và MaNguoiDung
            String roleName = currentUser.getRoleName();
            String maNguoiDung = currentUser.getMaNguoiDung();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/appnganhang/nhanVien-view/dashboard-view.fxml"));
                Parent root = loader.load();

                // Truyền tên đăng nhập sang DashboardController
                DashboardController dashboardController = loader.getController();
                dashboardController.initData(tenDangNhap, roleName, maNguoiDung);

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

    @FXML
    protected void onRegisterLinkClick() {
        try {
            Stage stage = (Stage) txtTenDangNhap.getScene().getWindow();

            FXMLLoader fxmlLoader =
                    new FXMLLoader(MainApp.class.getResource("util-view/register-view.fxml"));

            Parent root = fxmlLoader.load();

            Scene scene = new Scene(root, 1100, 750);

            stage.setScene(scene);
            stage.setTitle("SMART BANK - Đăng Ký");

            stage.setWidth(1100);
            stage.setHeight(750);

            stage.setMinWidth(1100);
            stage.setMinHeight(750);

            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onExitButtonClick() {
        Stage stage = (Stage) txtTenDangNhap.getScene().getWindow();
        stage.close();
    }
}