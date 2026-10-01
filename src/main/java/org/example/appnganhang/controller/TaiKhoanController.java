package org.example.appnganhang.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class TaiKhoanController {

    @FXML
    private Label lblSoTaiKhoan;

    @FXML
    private Label lblSoDu;

    @FXML
    private Label lblTenChuThe;

    @FXML
    private Label lblTrangThaiThe;

    @FXML
    private Button btnLamMoi;


    @FXML
    private void initialize() {
        hienThiThongTin();
    }


    // =========================
    // THÔNG TIN TÀI KHOẢN
    // =========================

    private void hienThiThongTin() {

        lblSoTaiKhoan.setText("1903 8888 9999");

        lblSoDu.setText("15,450,000 VNĐ");

        lblTenChuThe.setText("Họ tên: NGUYỄN VĂN A");

        lblTrangThaiThe.setText(
                "Trạng thái tài khoản: Hoạt động"
        );
    }


    // =========================
    // LÀM MỚI
    // =========================

    @FXML
    private void onLamMoi() {

        hienThiThongTin();

        System.out.println("Đã làm mới thông tin tài khoản.");
    }


    // =========================
    // CHUYỂN VIEW
    // =========================

    private void moView(
            String tenFile,
            ActionEvent event
    ) {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/org/example/appnganhang/"
                                            + tenFile
                            )
                    );

            Parent root = loader.load();

            Stage stage =
                    (Stage)
                            ((Node) event.getSource())
                                    .getScene()
                                    .getWindow();

            Scene scene =
                    new Scene(root);

            stage.setScene(scene);

            stage.setTitle(
                    "Hệ Thống Chuyển Tiền Ngân Hàng - SMART BANK"
            );

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

        moView(
                "customer-view.fxml",
                event
        );
    }


    @FXML
    private void onTaiKhoan(ActionEvent event) {

        // Đang ở trang Tài khoản
    }


    @FXML
    private void onChuyenTien(ActionEvent event) {

        moView(
                "transfer-view.fxml",
                event
        );
    }


    @FXML
    private void onTraCuuGiaoDich(ActionEvent event) {

        moView(
                "transaction-view.fxml",
                event
        );
    }


    @FXML
    private void onLichSuGiaoDich(ActionEvent event) {

        moView(
                "history-view.fxml",
                event
        );
    }


    @FXML
    private void onHanMuc(ActionEvent event) {

        moView(
                "limit-view.fxml",
                event
        );
    }


    @FXML
    private void onLoaiGiaoDich(ActionEvent event) {

        moView(
                "transaction-type-view.fxml",
                event
        );
    }


    @FXML
    private void onChiNhanh(ActionEvent event) {

        moView(
                "branch-view.fxml",
                event
        );
    }


    @FXML
    private void onNhanVien(ActionEvent event) {

        moView(
                "employee-view.fxml",
                event
        );
    }


    @FXML
    private void onBaoCao(ActionEvent event) {

        moView(
                "report-view.fxml",
                event
        );
    }


    @FXML
    private void onDoiMatKhau(ActionEvent event) {

        moView(
                "change-password-view.fxml",
                event
        );
    }


    @FXML
    private void onDangXuat(ActionEvent event) {

        moView(
                "login-view.fxml",
                event
        );
    }
}