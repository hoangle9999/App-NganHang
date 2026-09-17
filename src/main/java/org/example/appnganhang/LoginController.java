package org.example.appnganhang;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.appnganhang.util.DatabaseConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {

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

        // Kiểm tra đăng nhập với bảng TaiKhoanDangNhap_VaiTro trong SQL Server
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM TaiKhoanDangNhap_VaiTro WHERE TenDangNhap = ? AND MatKhau = ?";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, tenDangNhap);
                pstmt.setString(2, matKhau);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        String vaiTro = rs.getString("RoleName");

                        // Đăng nhập thành công -> Tiến hành chuyển sang Dashboard
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard-view.fxml"));
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

                    } else {
                        lblThongBao.setStyle("-fx-text-fill: red;");
                        lblThongBao.setText("Sai tên đăng nhập hoặc mật khẩu!");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            lblThongBao.setStyle("-fx-text-fill: red;");
            lblThongBao.setText("Lỗi kết nối cơ sở dữ liệu!");
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