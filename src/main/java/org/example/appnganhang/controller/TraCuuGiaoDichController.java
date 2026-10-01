package org.example.appnganhang.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;

public class TraCuuGiaoDichController {

    @FXML
    private TextField txtMaGiaoDich;

    @FXML
    private TextField txtMaTaiKhoan;

    @FXML
    private ComboBox<String> cboLoaiGiaoDich;

    @FXML
    private ComboBox<String> cboTrangThai;

    @FXML
    private TableView<GiaoDichData> tblGiaoDich;

    @FXML
    private TableColumn<GiaoDichData, String> colMaGiaoDich;

    @FXML
    private TableColumn<GiaoDichData, String> colTaiKhoanNguon;

    @FXML
    private TableColumn<GiaoDichData, String> colTaiKhoanDich;

    @FXML
    private TableColumn<GiaoDichData, String> colSoTien;

    @FXML
    private TableColumn<GiaoDichData, String> colNgayGiaoDich;

    @FXML
    private TableColumn<GiaoDichData, String> colNoiDung;

    @FXML
    private TableColumn<GiaoDichData, String> colMaLoaiGD;

    @FXML
    private TableColumn<GiaoDichData, String> colTrangThai;

    private final ObservableList<GiaoDichData> danhSachGiaoDich =
            FXCollections.observableArrayList();

    @FXML
    private void initialize() {

        cboLoaiGiaoDich.getItems().addAll(
                "Chuyển tiền",
                "Nạp tiền",
                "Rút tiền",
                "Thanh toán"
        );

        cboTrangThai.getItems().addAll(
                "Thành công",
                "Đang xử lý",
                "Thất bại"
        );

        cauHinhBang();
        taoDuLieuMau();
    }

    private void cauHinhBang() {

        colMaGiaoDich.setCellValueFactory(
                new PropertyValueFactory<>("maGiaoDich")
        );

        colTaiKhoanNguon.setCellValueFactory(
                new PropertyValueFactory<>("taiKhoanNguon")
        );

        colTaiKhoanDich.setCellValueFactory(
                new PropertyValueFactory<>("taiKhoanDich")
        );

        colSoTien.setCellValueFactory(
                new PropertyValueFactory<>("soTien")
        );

        colNgayGiaoDich.setCellValueFactory(
                new PropertyValueFactory<>("ngayGiaoDich")
        );

        colNoiDung.setCellValueFactory(
                new PropertyValueFactory<>("noiDung")
        );

        colMaLoaiGD.setCellValueFactory(
                new PropertyValueFactory<>("maLoaiGD")
        );

        colTrangThai.setCellValueFactory(
                new PropertyValueFactory<>("trangThai")
        );
    }

    private void taoDuLieuMau() {

        danhSachGiaoDich.addAll(
                new GiaoDichData(
                        "GD001",
                        "190388889999",
                        "190377776666",
                        "2,000,000",
                        "01/10/2026 08:30",
                        "Chuyển tiền cho Nguyễn Văn B",
                        "CK",
                        "Thành công"
                ),

                new GiaoDichData(
                        "GD002",
                        "190355554444",
                        "190322221111",
                        "500,000",
                        "01/10/2026 09:15",
                        "Thanh toán hóa đơn",
                        "TT",
                        "Thành công"
                ),

                new GiaoDichData(
                        "GD003",
                        "190311112222",
                        "190333334444",
                        "1,500,000",
                        "01/10/2026 10:20",
                        "Chuyển tiền",
                        "CK",
                        "Đang xử lý"
                )
        );

        tblGiaoDich.setItems(danhSachGiaoDich);
    }

    @FXML
    private void onTimKiem() {

        String maGiaoDich = txtMaGiaoDich.getText().trim();
        String maTaiKhoan = txtMaTaiKhoan.getText().trim();
        String loai = cboLoaiGiaoDich.getValue();
        String trangThai = cboTrangThai.getValue();

        ObservableList<GiaoDichData> ketQua =
                FXCollections.observableArrayList();

        for (GiaoDichData giaoDich : danhSachGiaoDich) {

            boolean dungMaGD =
                    maGiaoDich.isEmpty()
                            || giaoDich.getMaGiaoDich()
                            .toLowerCase()
                            .contains(maGiaoDich.toLowerCase());

            boolean dungTaiKhoan =
                    maTaiKhoan.isEmpty()
                            || giaoDich.getTaiKhoanNguon()
                            .contains(maTaiKhoan)
                            || giaoDich.getTaiKhoanDich()
                            .contains(maTaiKhoan);

            boolean dungLoai =
                    loai == null
                            || giaoDich.getLoaiGiaoDich()
                            .equals(loai);

            boolean dungTrangThai =
                    trangThai == null
                            || giaoDich.getTrangThai()
                            .equals(trangThai);

            if (dungMaGD
                    && dungTaiKhoan
                    && dungLoai
                    && dungTrangThai) {

                ketQua.add(giaoDich);
            }
        }

        tblGiaoDich.setItems(ketQua);
    }

    @FXML
    private void onTheoMa() {
        String ma = txtMaGiaoDich.getText().trim();

        if (ma.isEmpty()) {
            tblGiaoDich.setItems(danhSachGiaoDich);
            return;
        }

        ObservableList<GiaoDichData> ketQua =
                FXCollections.observableArrayList();

        for (GiaoDichData giaoDich : danhSachGiaoDich) {

            if (giaoDich.getMaGiaoDich()
                    .toLowerCase()
                    .contains(ma.toLowerCase())) {

                ketQua.add(giaoDich);
            }
        }

        tblGiaoDich.setItems(ketQua);
    }

    @FXML
    private void onTheoTaiKhoan() {
        String maTaiKhoan = txtMaTaiKhoan.getText().trim();

        if (maTaiKhoan.isEmpty()) {
            tblGiaoDich.setItems(danhSachGiaoDich);
            return;
        }

        ObservableList<GiaoDichData> ketQua =
                FXCollections.observableArrayList();

        for (GiaoDichData giaoDich : danhSachGiaoDich) {

            if (giaoDich.getTaiKhoanNguon().contains(maTaiKhoan)
                    || giaoDich.getTaiKhoanDich().contains(maTaiKhoan)) {

                ketQua.add(giaoDich);
            }
        }

        tblGiaoDich.setItems(ketQua);
    }

    @FXML
    private void onTheoLoai() {

        String loai = cboLoaiGiaoDich.getValue();

        if (loai == null) {
            tblGiaoDich.setItems(danhSachGiaoDich);
            return;
        }

        ObservableList<GiaoDichData> ketQua =
                FXCollections.observableArrayList();

        for (GiaoDichData giaoDich : danhSachGiaoDich) {

            if (giaoDich.getLoaiGiaoDich().equals(loai)) {
                ketQua.add(giaoDich);
            }
        }

        tblGiaoDich.setItems(ketQua);
    }

    @FXML
    private void onTheoTrangThai() {

        String trangThai = cboTrangThai.getValue();

        if (trangThai == null) {
            tblGiaoDich.setItems(danhSachGiaoDich);
            return;
        }

        ObservableList<GiaoDichData> ketQua =
                FXCollections.observableArrayList();

        for (GiaoDichData giaoDich : danhSachGiaoDich) {

            if (giaoDich.getTrangThai().equals(trangThai)) {
                ketQua.add(giaoDich);
            }
        }

        tblGiaoDich.setItems(ketQua);
    }

    @FXML
    private void onLamMoi() {

        txtMaGiaoDich.clear();
        txtMaTaiKhoan.clear();

        cboLoaiGiaoDich.setValue(null);
        cboTrangThai.setValue(null);

        tblGiaoDich.setItems(danhSachGiaoDich);
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
        // Đang ở trang Tra cứu giao dịch
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
    // CLASS DỮ LIỆU
    // =========================

    public static class GiaoDichData {

        private final String maGiaoDich;
        private final String taiKhoanNguon;
        private final String taiKhoanDich;
        private final String soTien;
        private final String ngayGiaoDich;
        private final String noiDung;
        private final String maLoaiGD;
        private final String trangThai;

        public GiaoDichData(
                String maGiaoDich,
                String taiKhoanNguon,
                String taiKhoanDich,
                String soTien,
                String ngayGiaoDich,
                String noiDung,
                String maLoaiGD,
                String trangThai) {

            this.maGiaoDich = maGiaoDich;
            this.taiKhoanNguon = taiKhoanNguon;
            this.taiKhoanDich = taiKhoanDich;
            this.soTien = soTien;
            this.ngayGiaoDich = ngayGiaoDich;
            this.noiDung = noiDung;
            this.maLoaiGD = maLoaiGD;
            this.trangThai = trangThai;
        }

        public String getMaGiaoDich() {
            return maGiaoDich;
        }

        public String getTaiKhoanNguon() {
            return taiKhoanNguon;
        }

        public String getTaiKhoanDich() {
            return taiKhoanDich;
        }

        public String getSoTien() {
            return soTien;
        }

        public String getNgayGiaoDich() {
            return ngayGiaoDich;
        }

        public String getNoiDung() {
            return noiDung;
        }

        public String getMaLoaiGD() {
            return maLoaiGD;
        }

        public String getTrangThai() {
            return trangThai;
        }

        public String getLoaiGiaoDich() {

            return switch (maLoaiGD) {
                case "CK" -> "Chuyển tiền";
                case "TT" -> "Thanh toán";
                case "NT" -> "Nạp tiền";
                case "RT" -> "Rút tiền";
                default -> "";
            };
        }
    }
}