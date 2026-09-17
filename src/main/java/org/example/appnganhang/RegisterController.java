package org.example.appnganhang;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.appnganhang.util.DatabaseConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class RegisterController {

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
        String maKhachHang = "KH" + System.currentTimeMillis() % 100000;

        String sqlAccount = "INSERT INTO TaiKhoanDangNhap_VaiTro (TenDangNhap, MatKhau, RoleName, MaNguoiDung) VALUES (?, ?, N'Khách hàng', ?)";
        String sqlCustomer = "INSERT INTO KhachHang (MaKhachHang, HoTen, SoCCCD, SoDienThoai, Email, TenDangNhap) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Sử dụng Transaction để đảm bảo tính toàn vẹn (thêm cả 2 bảng thành công hoặc hủy bỏ cả hai)
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtAcc = conn.prepareStatement(sqlAccount);
                 PreparedStatement pstmtCus = conn.prepareStatement(sqlCustomer)) {

                // 1. Thêm vào bảng tài khoản đăng nhập[cite: 1]
                pstmtAcc.setString(1, tenDN);
                pstmtAcc.setString(2, matKhau);
                pstmtAcc.setString(3, maKhachHang);
                pstmtAcc.executeUpdate();

                // 2. Thêm vào bảng khách hàng[cite: 1]
                pstmtCus.setString(1, maKhachHang);
                pstmtCus.setString(2, hoTen);
                pstmtCus.setString(3, cccd);
                pstmtCus.setString(4, sdt);
                pstmtCus.setString(5, email.isEmpty() ? null : email);
                pstmtCus.setString(6, tenDN);
                pstmtCus.executeUpdate();

                conn.commit(); // Xác nhận lưu dữ liệu thành công

                lblThongBao.setStyle("-fx-text-fill: green;");
                lblThongBao.setText("Đăng ký thành công! Bạn có thể quay lại để đăng nhập.");

            } catch (Exception e) {
                conn.rollback(); // Hoàn tác nếu vi phạm ràng buộc (trùng CCCD, SĐT hoặc Tên đăng nhập)
                e.printStackTrace();
                lblThongBao.setStyle("-fx-text-fill: red;");
                lblThongBao.setText("Lỗi: Tên đăng nhập hoặc CCCD/SĐT đã tồn tại!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            lblThongBao.setStyle("-fx-text-fill: red;");
            lblThongBao.setText("Không thể kết nối cơ sở dữ liệu!");
        }
    }

    // Xử lý sự kiện nút Quay lại Đăng nhập
    @FXML
    protected void handleBackToLogin() {
        try {
            Stage stage = (Stage) txtHoTen.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("login-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 450, 500);
            stage.setTitle("Hệ Thống Chuyển Tiền Ngân Hàng - Đăng Nhập");
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}