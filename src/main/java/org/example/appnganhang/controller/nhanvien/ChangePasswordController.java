package org.example.appnganhang.controller.nhanvien;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import org.example.appnganhang.model.entity.TaiKhoanDangNhap_VaiTro;
import org.example.appnganhang.controller.service.Iml.AuthServiceImpl;
import org.example.appnganhang.controller.service.intf.AuthService;

import static org.example.appnganhang.controller.util.Navigation.moView;

public class ChangePasswordController {

    @FXML private TextField txtTenDangNhap;
    @FXML private PasswordField txtMatKhauCu;
    @FXML private PasswordField txtMatKhauMoi;
    @FXML private PasswordField txtXacNhanMatKhau;

    @FXML private Label lblThongBao;

    @FXML private Button btnLamMoi;
    @FXML private Button btnDoiMatKhau;

    private final AuthService authService;

    public ChangePasswordController() {
        this.authService = new AuthServiceImpl();
    }

    // =========================================================
    // KHỞI TẠO
    // =========================================================

    @FXML
    private void initialize() {

        lblThongBao.setText("");

        hienThiTenDangNhap();
    }

    // =========================================================
    // HIỂN THỊ TÊN ĐĂNG NHẬP HIỆN TẠI
    // =========================================================

    private void hienThiTenDangNhap() {

        TaiKhoanDangNhap_VaiTro currentUser =
                authService.getCurrentUser();

        if (currentUser != null) {

            txtTenDangNhap.setText(
                    currentUser.getTenDangNhap()
            );
        }
    }

    @FXML
    private void onDoiMatKhau() {

        String tenDangNhap = txtTenDangNhap.getText().trim();
        String matKhauCu = txtMatKhauCu.getText();
        String matKhauMoi = txtMatKhauMoi.getText();
        String xacNhanMatKhau = txtXacNhanMatKhau.getText();

        if (tenDangNhap.isBlank()
                || matKhauCu.isBlank()
                || matKhauMoi.isBlank()
                || xacNhanMatKhau.isBlank()) {

            hienThiThongBao("Vui lòng nhập đầy đủ thông tin!", false);
            return;
        }

        if (!matKhauMoi.equals(xacNhanMatKhau)) {

            hienThiThongBao("Mật khẩu xác nhận không khớp!", false);
            return;
        }

        if (matKhauCu.equals(matKhauMoi)) {

            hienThiThongBao("Mật khẩu mới phải khác mật khẩu hiện tại!", false);
            return;
        }

        boolean ketQua = authService.changePassword(tenDangNhap, matKhauCu, matKhauMoi);

        if (ketQua) {

            hienThiThongBao("Đổi mật khẩu thành công!", true);

            txtMatKhauCu.clear();
            txtMatKhauMoi.clear();
            txtXacNhanMatKhau.clear();

        } else {

            hienThiThongBao("Mật khẩu hiện tại không đúng hoặc không thể đổi mật khẩu!", false);
        }
    }

    @FXML
    private void onLamMoi() {

        txtMatKhauCu.clear();
        txtMatKhauMoi.clear();
        txtXacNhanMatKhau.clear();

        lblThongBao.setText("");

        hienThiTenDangNhap();
    }

    // =========================================================
    // HIỂN THỊ THÔNG BÁO
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

    // =========================================================
    // ĐIỀU HƯỚNG SIDEBAR
    // =========================================================
    @FXML
    private void onTrangChu(ActionEvent event){moView("dashboard-view.fxml", event);}

    @FXML
    private void onKhachHang(ActionEvent event) {
        moView("customer-view.fxml", event);
    }

    @FXML
    private void onChuyenTien(ActionEvent event) {
        moView("transfer-view.fxml", event);
    }

    @FXML
    private void onTraCuuGiaoDich(ActionEvent event) {
        moView("transaction-view.fxml", event);
    }

    @FXML
    private void onLichSuGiaoDich(ActionEvent event) {
        moView("history-view.fxml", event);
    }

    @FXML
    private void onHanMuc(ActionEvent event) {
        moView("limit-view.fxml", event);
    }

    @FXML
    private void onLoaiGiaoDich(ActionEvent event) {
        moView("transaction-type-view.fxml", event);
    }

    @FXML
    private void onChiNhanh(ActionEvent event) {
        moView("branch-view.fxml", event);
    }

    @FXML
    private void onNhanVien(ActionEvent event) {
        moView("employee-view.fxml", event);
    }

    @FXML
    private void onBaoCao(ActionEvent event) {
        moView("report-view.fxml", event);
    }

    @FXML
    private void onDoiMatKhau(ActionEvent event) {
        // Đang ở trang Đổi mật khẩu
    }

    @FXML
    private void onDangXuat(ActionEvent event) {
        moView("login-view.fxml", event);
    }
}