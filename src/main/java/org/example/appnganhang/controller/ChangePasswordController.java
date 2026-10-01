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
import javafx.stage.Stage;

import java.io.IOException;

public class ChangePasswordController {

    @FXML
    private PasswordField txtMatKhauCu;

    @FXML
    private PasswordField txtMatKhauMoi;

    @FXML
    private PasswordField txtXacNhanMatKhau;

    @FXML
    private Label lblThongBao;

    @FXML
    private Button btnLamMoi;

    @FXML
    private Button btnDoiMatKhau;

    @FXML
    private void initialize() {
        lblThongBao.setText("");
    }

    // =========================================================
    // ĐỔI MẬT KHẨU
    // =========================================================

    @FXML
    private void onDoiMatKhau() {

        String matKhauCu = txtMatKhauCu.getText();
        String matKhauMoi = txtMatKhauMoi.getText();
        String xacNhanMatKhau = txtXacNhanMatKhau.getText();

        if (matKhauCu.isBlank()
                || matKhauMoi.isBlank()
                || xacNhanMatKhau.isBlank()) {

            hienThiThongBao(
                    "Vui lòng nhập đầy đủ thông tin!",
                    false
            );

            return;
        }

        if (!matKhauMoi.equals(xacNhanMatKhau)) {

            hienThiThongBao(
                    "Mật khẩu xác nhận không khớp!",
                    false
            );

            return;
        }

        if (matKhauCu.equals(matKhauMoi)) {

            hienThiThongBao(
                    "Mật khẩu mới phải khác mật khẩu hiện tại!",
                    false
            );

            return;
        }

        /*
         * Hiện tại chưa kết nối Database.
         * Khi làm phần Service/DAO sẽ xử lý đổi mật khẩu thật ở đây.
         */

        hienThiThongBao(
                "Đổi mật khẩu thành công!",
                true
        );

        txtMatKhauCu.clear();
        txtMatKhauMoi.clear();
        txtXacNhanMatKhau.clear();
    }

    // =========================================================
    // LÀM MỚI
    // =========================================================

    @FXML
    private void onLamMoi() {

        txtMatKhauCu.clear();
        txtMatKhauMoi.clear();
        txtXacNhanMatKhau.clear();

        lblThongBao.setText("");
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

    private void moView(
            String tenFile,
            ActionEvent event
    ) {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/org/example/appnganhang/" + tenFile
                    )
            );

            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene()
                    .getWindow();

            stage.setScene(new Scene(root));

            stage.setTitle(
                    "Hệ Thống Chuyển Tiền Ngân Hàng - SMART BANK"
            );

            stage.show();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    @FXML
    private void onKhachHang(ActionEvent event) {
        moView("customer-view.fxml", event);
    }

    @FXML
    private void onTaiKhoan(ActionEvent event) {
        moView("account-view.fxml", event);
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