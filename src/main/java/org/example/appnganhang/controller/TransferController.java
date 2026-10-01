package org.example.appnganhang.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class TransferController {

    @FXML
    private TextField txtSoTaiKhoanNhan;

    @FXML
    private ComboBox<String> cbNganHang;

    @FXML
    private TextField txtSoTien;

    @FXML
    private TextArea txtNoiDung;

    @FXML
    private void initialize() {
        cbNganHang.getItems().addAll(
                "SMART BANK",
                "Vietcombank",
                "BIDV",
                "VietinBank",
                "Agribank",
                "Techcombank",
                "MB Bank"
        );
    }

    // =========================
    // MỞ VIEW
    // =========================

    private void moView(String tenFile, ActionEvent event) {
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
            stage.setTitle("Hệ Thống Chuyển Tiền Ngân Hàng - SMART BANK");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // =========================
    // SIDEBAR
    // =========================

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
        // Đang ở trang Chuyển tiền
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

    // =========================
    // XÁC NHẬN CHUYỂN KHOẢN
    // =========================

    @FXML
    private void onXacNhan() {

        String soTaiKhoanNhan = txtSoTaiKhoanNhan.getText();
        String nganHang = cbNganHang.getValue();
        String soTien = txtSoTien.getText();
        String noiDung = txtNoiDung.getText();

        System.out.println("===== XÁC NHẬN CHUYỂN KHOẢN =====");
        System.out.println("Số tài khoản nhận: " + soTaiKhoanNhan);
        System.out.println("Ngân hàng: " + nganHang);
        System.out.println("Số tiền: " + soTien);
        System.out.println("Nội dung: " + noiDung);
    }
}