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

public class ChiNhanhController {

    @FXML
    private TextField txtMaChiNhanh;

    @FXML
    private TextField txtTenChiNhanh;

    @FXML
    private TextField txtDiaChi;

    @FXML
    private Button btnThem;

    @FXML
    private Button btnSua;

    @FXML
    private Button btnXoa;

    @FXML
    private Button btnLamMoi;

    @FXML
    private TableView<ChiNhanhData> tblChiNhanh;

    @FXML
    private TableColumn<ChiNhanhData, String> colMaChiNhanh;

    @FXML
    private TableColumn<ChiNhanhData, String> colTenChiNhanh;

    @FXML
    private TableColumn<ChiNhanhData, String> colDiaChi;

    private final ObservableList<ChiNhanhData> danhSach =
            FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        cauHinhBang();
        taoDuLieuMau();
    }

    private void cauHinhBang() {

        colMaChiNhanh.setCellValueFactory(
                new PropertyValueFactory<>("maChiNhanh")
        );

        colTenChiNhanh.setCellValueFactory(
                new PropertyValueFactory<>("tenChiNhanh")
        );

        colDiaChi.setCellValueFactory(
                new PropertyValueFactory<>("diaChi")
        );
    }

    private void taoDuLieuMau() {

        danhSach.addAll(

                new ChiNhanhData(
                        "CN001",
                        "Chi nhánh Trung tâm",
                        "01 Nguyễn Huệ, Quận 1, TP. Hồ Chí Minh"
                ),

                new ChiNhanhData(
                        "CN002",
                        "Chi nhánh Tân Bình",
                        "120 Trường Chinh, Tân Bình, TP. Hồ Chí Minh"
                ),

                new ChiNhanhData(
                        "CN003",
                        "Chi nhánh Thủ Đức",
                        "25 Võ Văn Ngân, TP. Thủ Đức, TP. Hồ Chí Minh"
                )
        );

        tblChiNhanh.setItems(danhSach);
    }

    // =========================================================
    // THÊM
    // =========================================================

    @FXML
    private void onThem() {

        String maChiNhanh = txtMaChiNhanh.getText().trim();
        String tenChiNhanh = txtTenChiNhanh.getText().trim();
        String diaChi = txtDiaChi.getText().trim();

        if (maChiNhanh.isEmpty()
                || tenChiNhanh.isEmpty()
                || diaChi.isEmpty()) {

            System.out.println("Vui lòng nhập đầy đủ thông tin chi nhánh.");
            return;
        }

        ChiNhanhData chiNhanhMoi =
                new ChiNhanhData(
                        maChiNhanh,
                        tenChiNhanh,
                        diaChi
                );

        danhSach.add(chiNhanhMoi);

        lamSachForm();

        tblChiNhanh.setItems(danhSach);

        System.out.println(
                "Đã thêm chi nhánh: " + maChiNhanh
        );
    }

    // =========================================================
    // SỬA
    // =========================================================

    @FXML
    private void onSua() {

        ChiNhanhData selected =
                tblChiNhanh.getSelectionModel().getSelectedItem();

        if (selected == null) {
            System.out.println(
                    "Vui lòng chọn chi nhánh cần sửa."
            );
            return;
        }

        String maChiNhanh = txtMaChiNhanh.getText().trim();
        String tenChiNhanh = txtTenChiNhanh.getText().trim();
        String diaChi = txtDiaChi.getText().trim();

        if (maChiNhanh.isEmpty()
                || tenChiNhanh.isEmpty()
                || diaChi.isEmpty()) {

            System.out.println(
                    "Vui lòng nhập đầy đủ thông tin."
            );
            return;
        }

        int index = danhSach.indexOf(selected);

        danhSach.set(
                index,
                new ChiNhanhData(
                        maChiNhanh,
                        tenChiNhanh,
                        diaChi
                )
        );

        tblChiNhanh.refresh();

        lamSachForm();

        System.out.println(
                "Đã cập nhật chi nhánh: " + maChiNhanh
        );
    }

    // =========================================================
    // XÓA
    // =========================================================

    @FXML
    private void onXoa() {

        ChiNhanhData selected =
                tblChiNhanh.getSelectionModel().getSelectedItem();

        if (selected == null) {
            System.out.println(
                    "Vui lòng chọn chi nhánh cần xóa."
            );
            return;
        }

        danhSach.remove(selected);

        lamSachForm();

        System.out.println(
                "Đã xóa chi nhánh: "
                        + selected.getMaChiNhanh()
        );
    }

    // =========================================================
    // LÀM MỚI
    // =========================================================

    @FXML
    private void onLamMoi() {

        lamSachForm();

        tblChiNhanh.setItems(danhSach);

        tblChiNhanh.getSelectionModel().clearSelection();

        System.out.println("Đã làm mới.");
    }

    private void lamSachForm() {

        txtMaChiNhanh.clear();
        txtTenChiNhanh.clear();
        txtDiaChi.clear();
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
        moView("transaction-type-view.fxml", event);
    }

    @FXML
    private void onChiNhanh(ActionEvent event) {
        // Đang ở trang Chi nhánh
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
    // DATA
    // =========================================================

    public static class ChiNhanhData {

        private final String maChiNhanh;
        private final String tenChiNhanh;
        private final String diaChi;

        public ChiNhanhData(
                String maChiNhanh,
                String tenChiNhanh,
                String diaChi
        ) {
            this.maChiNhanh = maChiNhanh;
            this.tenChiNhanh = tenChiNhanh;
            this.diaChi = diaChi;
        }

        public String getMaChiNhanh() {
            return maChiNhanh;
        }

        public String getTenChiNhanh() {
            return tenChiNhanh;
        }

        public String getDiaChi() {
            return diaChi;
        }
    }
}