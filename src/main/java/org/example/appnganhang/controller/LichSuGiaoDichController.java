package org.example.appnganhang.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;

public class LichSuGiaoDichController {

    @FXML
    private TextField txtMaTaiKhoan;

    @FXML
    private Button btnTimKiem;

    @FXML
    private Button btnLamMoi;

    @FXML
    private TableView<GiaoDichData> tblLichSu;

    @FXML
    private TableColumn<GiaoDichData, String> colMaGD;

    @FXML
    private TableColumn<GiaoDichData, String> colNgay;

    @FXML
    private TableColumn<GiaoDichData, String> colLoai;

    @FXML
    private TableColumn<GiaoDichData, String> colSoTien;

    @FXML
    private TableColumn<GiaoDichData, String> colTrangThai;

    private final javafx.collections.ObservableList<GiaoDichData>
            danhSachGiaoDich = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        cauHinhBang();
        taoDuLieuMau();
    }

    private void cauHinhBang() {

        colMaGD.setCellValueFactory(
                new PropertyValueFactory<>("maGiaoDich")
        );

        colNgay.setCellValueFactory(
                new PropertyValueFactory<>("ngayGiaoDich")
        );

        colLoai.setCellValueFactory(
                new PropertyValueFactory<>("loaiGiaoDich")
        );

        colSoTien.setCellValueFactory(
                new PropertyValueFactory<>("soTien")
        );

        colTrangThai.setCellValueFactory(
                new PropertyValueFactory<>("trangThai")
        );
    }

    private void taoDuLieuMau() {

        danhSachGiaoDich.addAll(
                new GiaoDichData(
                        "GD001",
                        "01/10/2026 08:30",
                        "Chuyển tiền",
                        "2,000,000 VNĐ",
                        "Đã ghi nhận",
                        "190388889999"
                ),

                new GiaoDichData(
                        "GD002",
                        "01/10/2026 09:15",
                        "Thanh toán",
                        "500,000 VNĐ",
                        "Đã ghi nhận",
                        "190388889999"
                ),

                new GiaoDichData(
                        "GD003",
                        "01/10/2026 10:20",
                        "Nạp tiền",
                        "1,500,000 VNĐ",
                        "Đã ghi nhận",
                        "190355554444"
                )
        );

        tblLichSu.setItems(danhSachGiaoDich);
    }

    @FXML
    private void onTimKiem() {

        String maTaiKhoan = txtMaTaiKhoan.getText().trim();

        if (maTaiKhoan.isEmpty()) {
            tblLichSu.setItems(danhSachGiaoDich);
            return;
        }

        javafx.collections.ObservableList<GiaoDichData> ketQua =
                FXCollections.observableArrayList();

        for (GiaoDichData giaoDich : danhSachGiaoDich) {

            if (giaoDich.getMaTaiKhoan()
                    .contains(maTaiKhoan)) {

                ketQua.add(giaoDich);
            }
        }

        tblLichSu.setItems(ketQua);
    }

    @FXML
    private void onLamMoi() {

        txtMaTaiKhoan.clear();

        tblLichSu.setItems(danhSachGiaoDich);

        tblLichSu.getSelectionModel().clearSelection();
    }

    // =========================
    // ĐIỀU HƯỚNG SIDEBAR
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
        // Đang ở trang Lịch sử giao dịch
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
    // DỮ LIỆU LỊCH SỬ
    // =========================

    public static class GiaoDichData {

        private final String maGiaoDich;
        private final String ngayGiaoDich;
        private final String loaiGiaoDich;
        private final String soTien;
        private final String trangThai;
        private final String maTaiKhoan;

        public GiaoDichData(
                String maGiaoDich,
                String ngayGiaoDich,
                String loaiGiaoDich,
                String soTien,
                String trangThai,
                String maTaiKhoan) {

            this.maGiaoDich = maGiaoDich;
            this.ngayGiaoDich = ngayGiaoDich;
            this.loaiGiaoDich = loaiGiaoDich;
            this.soTien = soTien;
            this.trangThai = trangThai;
            this.maTaiKhoan = maTaiKhoan;
        }

        public String getMaGiaoDich() {
            return maGiaoDich;
        }

        public String getNgayGiaoDich() {
            return ngayGiaoDich;
        }

        public String getLoaiGiaoDich() {
            return loaiGiaoDich;
        }

        public String getSoTien() {
            return soTien;
        }

        public String getTrangThai() {
            return trangThai;
        }

        public String getMaTaiKhoan() {
            return maTaiKhoan;
        }
    }
}