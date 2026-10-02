package org.example.appnganhang.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {

    private String currentUser;
    private String roleName;
    private String maNguoiDung;

    @FXML
    private void initialize() {
    }

    // =========================================================
    // NHẬN USERNAME
    // =========================================================

    public void initData(String username) {
        this.currentUser = username;
    }

    public void initData(String username, String roleName, String maNguoiDung) {
        this.currentUser = username;
        this.roleName = roleName;
        this.maNguoiDung = maNguoiDung;
    }

    // =========================================================
    // MỞ VIEW
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

            Stage stage =
                    (Stage) ((Node) event.getSource())
                            .getScene()
                            .getWindow();

            stage.setScene(new Scene(root));

            stage.setTitle(
                    "Hệ Thống Chuyển Tiền Ngân Hàng - SMART BANK"
            );

            stage.show();

        } catch (IOException e) {

            System.err.println(
                    "Không thể mở view: " + tenFile
            );

            e.printStackTrace();
        }
    }

    // =========================================================
    // SIDEBAR
    // =========================================================

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
        moView("change-password-view.fxml", event);
    }

    @FXML
    private void onDangXuat(ActionEvent event) {
        moView("login-view.fxml", event);
    }

    // =========================================================
    // QUICK ACTION
    // =========================================================

    @FXML
    private void handleTrangChu(ActionEvent event) {
        // Đang ở trang chủ
    }

    @FXML
    private void handleChuyenTien(ActionEvent event) {
        moView("transfer-view.fxml", event);
    }

    @FXML
    private void handleLichSu(ActionEvent event) {
        moView("history-view.fxml", event);
    }

    @FXML
    private void handleDangXuat(ActionEvent event) {
        moView("login-view.fxml", event);
    }
}