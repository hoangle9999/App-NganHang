package org.example.appnganhang.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

public class HanMucController {

    @FXML
    private TextField txtMaHanMuc;

    @FXML
    private TextField txtMaTaiKhoan;

    @FXML
    private Button btnTimKiem;

    @FXML
    private Button btnLamMoi;

    @FXML
    private TableView<HanMucData> tblHanMuc;

    @FXML
    private TableColumn<HanMucData, String> colMaHanMuc;

    @FXML
    private TableColumn<HanMucData, String> colMaTaiKhoan;

    @FXML
    private TableColumn<HanMucData, String> colHanMucNgay;

    @FXML
    private TableColumn<HanMucData, String> colHanMucLan;

    @FXML
    private TableColumn<HanMucData, String> colSoTienDaDung;

    @FXML
    private TableColumn<HanMucData, String> colTrangThai;

    @FXML
    private Button btnThem;

    @FXML
    private Button btnSua;

    @FXML
    private Button btnXoa;

    private final ObservableList<HanMucData> danhSach =
            FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        cauHinhBang();
        taoDuLieuMau();
    }

    private void cauHinhBang() {

        colMaHanMuc.setCellValueFactory(
                new PropertyValueFactory<>("maHanMuc")
        );

        colMaTaiKhoan.setCellValueFactory(
                new PropertyValueFactory<>("maTaiKhoan")
        );

        colHanMucNgay.setCellValueFactory(
                new PropertyValueFactory<>("hanMucNgay")
        );

        colHanMucLan.setCellValueFactory(
                new PropertyValueFactory<>("hanMucLan")
        );

        colSoTienDaDung.setCellValueFactory(
                new PropertyValueFactory<>("soTienDaDung")
        );

        colTrangThai.setCellValueFactory(
                new PropertyValueFactory<>("trangThai")
        );
    }

    private void taoDuLieuMau() {

        danhSach.addAll(
                new HanMucData(
                        "HM001",
                        "190388889999",
                        "50,000,000 VNĐ",
                        "20,000,000 VNĐ",
                        "5,000,000 VNĐ",
                        "Hoạt động"
                ),

                new HanMucData(
                        "HM002",
                        "190355554444",
                        "30,000,000 VNĐ",
                        "10,000,000 VNĐ",
                        "2,500,000 VNĐ",
                        "Hoạt động"
                ),

                new HanMucData(
                        "HM003",
                        "190322221111",
                        "20,000,000 VNĐ",
                        "5,000,000 VNĐ",
                        "20,000,000 VNĐ",
                        "Đã đạt hạn mức"
                )
        );

        tblHanMuc.setItems(danhSach);
    }

    @FXML
    private void onTimKiem() {

        String maHanMuc = txtMaHanMuc.getText().trim();
        String maTaiKhoan = txtMaTaiKhoan.getText().trim();

        if (maHanMuc.isEmpty() && maTaiKhoan.isEmpty()) {
            tblHanMuc.setItems(danhSach);
            return;
        }

        ObservableList<HanMucData> ketQua =
                FXCollections.observableArrayList();

        for (HanMucData hanMuc : danhSach) {

            boolean dungMaHanMuc =
                    maHanMuc.isEmpty()
                            || hanMuc.getMaHanMuc()
                            .toLowerCase()
                            .contains(maHanMuc.toLowerCase());

            boolean dungMaTaiKhoan =
                    maTaiKhoan.isEmpty()
                            || hanMuc.getMaTaiKhoan()
                            .contains(maTaiKhoan);

            if (dungMaHanMuc && dungMaTaiKhoan) {
                ketQua.add(hanMuc);
            }
        }

        tblHanMuc.setItems(ketQua);
    }

    @FXML
    private void onLamMoi() {

        txtMaHanMuc.clear();
        txtMaTaiKhoan.clear();

        tblHanMuc.setItems(danhSach);

        tblHanMuc.getSelectionModel().clearSelection();
    }

    @FXML
    private void onThem() {
        System.out.println("Thêm hạn mức");
    }

    @FXML
    private void onSua() {

        HanMucData selected =
                tblHanMuc.getSelectionModel().getSelectedItem();

        if (selected == null) {
            System.out.println(
                    "Vui lòng chọn hạn mức cần sửa."
            );
            return;
        }

        System.out.println(
                "Sửa hạn mức: " + selected.getMaHanMuc()
        );
    }

    @FXML
    private void onXoa() {

        HanMucData selected =
                tblHanMuc.getSelectionModel().getSelectedItem();

        if (selected == null) {
            System.out.println(
                    "Vui lòng chọn hạn mức cần xóa."
            );
            return;
        }

        danhSach.remove(selected);

        tblHanMuc.setItems(danhSach);

        System.out.println(
                "Đã xóa hạn mức: " + selected.getMaHanMuc()
        );
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
        moView("history-view.fxml", event);
    }

    @FXML
    private void onHanMuc(ActionEvent event) {
        // Đang ở trang Hạn mức
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
    // CLASS DỮ LIỆU
    // =========================

    public static class HanMucData {

        private final String maHanMuc;
        private final String maTaiKhoan;
        private final String hanMucNgay;
        private final String hanMucLan;
        private final String soTienDaDung;
        private final String trangThai;

        public HanMucData(
                String maHanMuc,
                String maTaiKhoan,
                String hanMucNgay,
                String hanMucLan,
                String soTienDaDung,
                String trangThai) {

            this.maHanMuc = maHanMuc;
            this.maTaiKhoan = maTaiKhoan;
            this.hanMucNgay = hanMucNgay;
            this.hanMucLan = hanMucLan;
            this.soTienDaDung = soTienDaDung;
            this.trangThai = trangThai;
        }

        public String getMaHanMuc() {
            return maHanMuc;
        }

        public String getMaTaiKhoan() {
            return maTaiKhoan;
        }

        public String getHanMucNgay() {
            return hanMucNgay;
        }

        public String getHanMucLan() {
            return hanMucLan;
        }

        public String getSoTienDaDung() {
            return soTienDaDung;
        }

        public String getTrangThai() {
            return trangThai;
        }
    }
}