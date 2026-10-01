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

public class LoaiGiaoDichController {

    @FXML
    private TextField txtMaLoai;

    @FXML
    private TextField txtTenLoai;

    @FXML
    private Button btnThem;

    @FXML
    private Button btnSua;

    @FXML
    private Button btnXoa;

    @FXML
    private Button btnLamMoi;

    @FXML
    private TableView<LoaiGiaoDichData> tblLoaiGiaoDich;

    @FXML
    private TableColumn<LoaiGiaoDichData, String> colMaLoai;

    @FXML
    private TableColumn<LoaiGiaoDichData, String> colTenLoai;

    @FXML
    private TableColumn<LoaiGiaoDichData, String> colMoTa;

    private final ObservableList<LoaiGiaoDichData> danhSach =
            FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        cauHinhBang();
        taoDuLieuMau();
    }

    private void cauHinhBang() {
        colMaLoai.setCellValueFactory(
                new PropertyValueFactory<>("maLoaiGiaoDich")
        );

        colTenLoai.setCellValueFactory(
                new PropertyValueFactory<>("tenLoaiGiaoDich")
        );

        colMoTa.setCellValueFactory(
                new PropertyValueFactory<>("moTa")
        );
    }

    private void taoDuLieuMau() {
        danhSach.addAll(
                new LoaiGiaoDichData(
                        "CK",
                        "Chuyển tiền",
                        "Giao dịch chuyển tiền giữa các tài khoản"
                ),

                new LoaiGiaoDichData(
                        "NT",
                        "Nạp tiền",
                        "Nạp tiền vào tài khoản ngân hàng"
                ),

                new LoaiGiaoDichData(
                        "RT",
                        "Rút tiền",
                        "Rút tiền từ tài khoản ngân hàng"
                ),

                new LoaiGiaoDichData(
                        "TT",
                        "Thanh toán",
                        "Thanh toán hóa đơn và các dịch vụ"
                )
        );

        tblLoaiGiaoDich.setItems(danhSach);
    }

    @FXML
    private void onThem() {

        String maLoai = txtMaLoai.getText().trim();
        String tenLoai = txtTenLoai.getText().trim();

        if (maLoai.isEmpty() || tenLoai.isEmpty()) {
            System.out.println("Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        LoaiGiaoDichData loaiMoi =
                new LoaiGiaoDichData(
                        maLoai,
                        tenLoai,
                        "Loại giao dịch mới"
                );

        danhSach.add(loaiMoi);

        txtMaLoai.clear();
        txtTenLoai.clear();

        tblLoaiGiaoDich.setItems(danhSach);

        System.out.println("Đã thêm loại giao dịch: " + maLoai);
    }

    @FXML
    private void onSua() {

        LoaiGiaoDichData selected =
                tblLoaiGiaoDich.getSelectionModel().getSelectedItem();

        if (selected == null) {
            System.out.println("Vui lòng chọn loại giao dịch cần sửa.");
            return;
        }

        String maLoai = txtMaLoai.getText().trim();
        String tenLoai = txtTenLoai.getText().trim();

        if (maLoai.isEmpty() || tenLoai.isEmpty()) {
            System.out.println("Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        int index = danhSach.indexOf(selected);

        danhSach.set(
                index,
                new LoaiGiaoDichData(
                        maLoai,
                        tenLoai,
                        selected.getMoTa()
                )
        );

        txtMaLoai.clear();
        txtTenLoai.clear();

        tblLoaiGiaoDich.refresh();

        System.out.println("Đã cập nhật loại giao dịch: " + maLoai);
    }

    @FXML
    private void onXoa() {

        LoaiGiaoDichData selected =
                tblLoaiGiaoDich.getSelectionModel().getSelectedItem();

        if (selected == null) {
            System.out.println("Vui lòng chọn loại giao dịch cần xóa.");
            return;
        }

        danhSach.remove(selected);

        txtMaLoai.clear();
        txtTenLoai.clear();

        System.out.println(
                "Đã xóa loại giao dịch: "
                        + selected.getMaLoaiGiaoDich()
        );
    }

    @FXML
    private void onLamMoi() {

        txtMaLoai.clear();
        txtTenLoai.clear();

        tblLoaiGiaoDich.setItems(danhSach);
        tblLoaiGiaoDich.getSelectionModel().clearSelection();

        System.out.println("Đã làm mới.");
    }

    // =========================================================
    // ĐIỀU HƯỚNG SIDEBAR
    // =========================================================

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
        moView("limit-view.fxml", event);
    }

    @FXML
    private void onLoaiGiaoDich(ActionEvent event) {
        // Đang ở trang Loại giao dịch
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
    // MODEL DỮ LIỆU HIỂN THỊ
    // =========================================================

    public static class LoaiGiaoDichData {

        private final String maLoaiGiaoDich;
        private final String tenLoaiGiaoDich;
        private final String moTa;

        public LoaiGiaoDichData(
                String maLoaiGiaoDich,
                String tenLoaiGiaoDich,
                String moTa
        ) {
            this.maLoaiGiaoDich = maLoaiGiaoDich;
            this.tenLoaiGiaoDich = tenLoaiGiaoDich;
            this.moTa = moTa;
        }

        public String getMaLoaiGiaoDich() {
            return maLoaiGiaoDich;
        }

        public String getTenLoaiGiaoDich() {
            return tenLoaiGiaoDich;
        }

        public String getMoTa() {
            return moTa;
        }
    }
}