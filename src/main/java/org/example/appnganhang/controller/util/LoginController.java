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
import org.example.appnganhang.controller.khachhang.CustomerSession;
//import org.example.appnganhang.controller.nhanvien.DashboardController;
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

        if (authService.login(tenDangNhap, matKhau)) {

            TaiKhoanDangNhap_VaiTro currentUser =
                    authService.getCurrentUser();

            if (currentUser == null) {
                lblThongBao.setStyle("-fx-text-fill: red;");
                lblThongBao.setText("Không lấy được thông tin tài khoản!");
                return;
            }

            String roleName = currentUser.getRoleName();

            String fxmlPath;

            switch (roleName) {
                case "Admin":
                    fxmlPath =
                            "/org/example/appnganhang/admin-view/admin-dashboard-view.fxml";
                    break;

                case "Nhân viên CSKH":
                    fxmlPath =
                            "/org/example/appnganhang/CSKH-view/cskh-dashboard-view.fxml";
                    break;

                case "Giao dịch viên":
                    fxmlPath =
                            "/org/example/appnganhang/giaoDichVien-view/teller-dashboard-view.fxml";
                    break;

                case "Khách hàng":
                    if (!CustomerSession.loadSessionFromDB(currentUser.getMaNguoiDung(), tenDangNhap)) {
                        lblThongBao.setStyle("-fx-text-fill: red;");
                        lblThongBao.setText("Không tìm thấy hồ sơ khách hàng tương ứng với tài khoản đăng nhập.");
                        return;
                    }
                    fxmlPath =
                            "/org/example/appnganhang/khachHang-view/customer-home-view.fxml";
                    break;

                default:
                    lblThongBao.setStyle("-fx-text-fill: red;");
                    lblThongBao.setText("Vai trò tài khoản không hợp lệ!");
                    return;
            }

            try {
                FXMLLoader loader = new FXMLLoader(
                        MainApp.class.getResource(fxmlPath)
                );

                if (loader.getLocation() == null) {
                    throw new IOException(
                            "Không tìm thấy file FXML: " + fxmlPath
                    );
                }

                Parent root = loader.load();

                Stage stage = (Stage) txtTenDangNhap.getScene().getWindow();

                stage.setScene(new Scene(root));
                stage.setTitle("SMART BANK - Trang Chủ");
                stage.centerOnScreen();
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();

                lblThongBao.setStyle("-fx-text-fill: red;");
                lblThongBao.setText("Không thể mở giao diện sau đăng nhập!");
            }

        } else {
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
