package org.example.appnganhang.controller.khachhang;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.appnganhang.controller.service.Iml.AuthServiceImpl;
import org.example.appnganhang.controller.service.intf.AuthService;
import org.example.appnganhang.model.entity.TaiKhoanDangNhap_VaiTro;
import org.example.appnganhang.model.util.DBConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.example.appnganhang.controller.util.Navigation.moView;

public class CustomerLoginController {

    @FXML private TextField txtTenDangNhap;
    @FXML private PasswordField txtMatKhau;
    @FXML private TextField txtMatKhauHien;
    @FXML private Button btnToggleMatKhau;
    @FXML private Label lblThongBaoLoi;
    @FXML private Button btnDangNhap;

    @FXML private VBox modalQuenMatKhau;

    private boolean isMatKhauHien = false;
    private final AuthService authService = new AuthServiceImpl();

    @FXML
    public void initialize() {
        // Đồng bộ dữ liệu 2 trường mật khẩu
        txtMatKhau.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!isMatKhauHien) {
                txtMatKhauHien.setText(newVal);
            }
        });

        txtMatKhauHien.textProperty().addListener((obs, oldVal, newVal) -> {
            if (isMatKhauHien) {
                txtMatKhau.setText(newVal);
            }
        });
    }

    @FXML
    public void onFocusMatKhau() {
        if (isMatKhauHien) {
            txtMatKhauHien.requestFocus();
        } else {
            txtMatKhau.requestFocus();
        }
    }

    @FXML
    public void onToggleMatKhau() {
        isMatKhauHien = !isMatKhauHien;
        if (isMatKhauHien) {
            txtMatKhauHien.setText(txtMatKhau.getText());
            txtMatKhauHien.setVisible(true);
            txtMatKhauHien.setManaged(true);

            txtMatKhau.setVisible(false);
            txtMatKhau.setManaged(false);

            btnToggleMatKhau.setText("🙈");
            txtMatKhauHien.requestFocus();
            txtMatKhauHien.positionCaret(txtMatKhauHien.getText().length());
        } else {
            txtMatKhau.setText(txtMatKhauHien.getText());
            txtMatKhau.setVisible(true);
            txtMatKhau.setManaged(true);

            txtMatKhauHien.setVisible(false);
            txtMatKhauHien.setManaged(false);

            btnToggleMatKhau.setText("👁");
            txtMatKhau.requestFocus();
            txtMatKhau.positionCaret(txtMatKhau.getText().length());
        }
    }

    @FXML
    public void onDangNhap(ActionEvent event) {
        String tenDN = txtTenDangNhap.getText().trim();
        String matKhau = isMatKhauHien ? txtMatKhauHien.getText().trim() : txtMatKhau.getText().trim();

        if (tenDN.isEmpty() || matKhau.isEmpty()) {
            hienLoi("Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu!");
            return;
        }

        // Kiểm tra đăng nhập và vai trò
        boolean hopLe = false;
        String maNguoiDung = "";
        String roleName = "";

        // Cách 1: Qua authService
        if (authService.login(tenDN, matKhau)) {
            TaiKhoanDangNhap_VaiTro user = authService.getCurrentUser();
            if (user != null) {
                roleName = user.getRoleName();
                maNguoiDung = user.getMaNguoiDung();
                // Phải đúng vai trò Khách hàng
                if ("Khách hàng".equalsIgnoreCase(roleName) || "KhachHang".equalsIgnoreCase(roleName) || "Customer".equalsIgnoreCase(roleName)) {
                    hopLe = true;
                }
            }
        }

        // Cách 2: Truy vấn trực tiếp nếu authService chưa hỗ trợ hoặc fallback
        if (!hopLe) {
            String sql = "SELECT TenDangNhap, MatKhau, RoleName, MaNguoiDung FROM TaiKhoanDangNhap_VaiTro WHERE TenDangNhap = ? AND MatKhau = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, tenDN);
                ps.setString(2, matKhau);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    roleName = rs.getString("RoleName");
                    maNguoiDung = rs.getString("MaNguoiDung");
                    if ("Khách hàng".equalsIgnoreCase(roleName) || "KhachHang".equalsIgnoreCase(roleName) || "Customer".equalsIgnoreCase(roleName)) {
                        hopLe = true;
                    }
                }
            } catch (Exception e) {
                System.err.println("Lỗi xác thực khách hàng: " + e.getMessage());
            }
        }

        if (hopLe) {
            anLoi();
            // Nạp session khách hàng theo MaNguoiDung liên kết MaKhachHang
            CustomerSession.loadSessionFromDB(maNguoiDung, tenDN);

            // Chuyển sang màn hình Trang chủ khách hàng
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/appnganhang/khachHang-view/customer-home-view.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Hệ Thống Chuyển Tiền Ngân Hàng - Khách Hàng");
                stage.centerOnScreen();
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            hienLoi("Tên đăng nhập hoặc mật khẩu không chính xác.");
        }
    }

    private void hienLoi(String msg) {
        lblThongBaoLoi.setText(msg);
        lblThongBaoLoi.setVisible(true);
        lblThongBaoLoi.setManaged(true);
    }

    private void anLoi() {
        lblThongBaoLoi.setVisible(false);
        lblThongBaoLoi.setManaged(false);
    }

    @FXML
    public void onMoModalQuenMatKhau() {
        if (modalQuenMatKhau != null) {
            modalQuenMatKhau.setVisible(true);
            modalQuenMatKhau.setManaged(true);
        }
    }

    @FXML
    public void onDongModalQuenMatKhau() {
        if (modalQuenMatKhau != null) {
            modalQuenMatKhau.setVisible(false);
            modalQuenMatKhau.setManaged(false);
        }
    }

    @FXML
    public void onChuyenSangLoginChung(ActionEvent event) {
        moView("login-view.fxml", event);
    }
}
