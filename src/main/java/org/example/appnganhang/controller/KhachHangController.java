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

public class KhachHangController {

    // =========================
    // FORM
    // =========================

    @FXML
    private TextField txtMaKhachHang;

    @FXML
    private TextField txtHoTen;

    @FXML
    private TextField txtCCCD;

    @FXML
    private TextField txtSoDienThoai;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtDiaChi;

    @FXML
    private TextField txtTenDangNhap;

    // =========================
    // BUTTON
    // =========================

    @FXML
    private Button btnTimKiem;

    @FXML
    private Button btnLamMoi;

    @FXML
    private Button btnThem;

    @FXML
    private Button btnSua;

    @FXML
    private Button btnXoa;

    // =========================
    // TABLE
    // =========================

    @FXML
    private TableView<KhachHangData> tblKhachHang;

    @FXML
    private TableColumn<KhachHangData, String> colMaKhachHang;

    @FXML
    private TableColumn<KhachHangData, String> colHoTen;

    @FXML
    private TableColumn<KhachHangData, String> colCCCD;

    @FXML
    private TableColumn<KhachHangData, String> colSoDienThoai;

    @FXML
    private TableColumn<KhachHangData, String> colEmail;

    @FXML
    private TableColumn<KhachHangData, String> colDiaChi;

    @FXML
    private TableColumn<KhachHangData, String> colTenDangNhap;

    private final ObservableList<KhachHangData> danhSach =
            FXCollections.observableArrayList();

    // =========================
    // INITIALIZE
    // =========================

    @FXML
    private void initialize() {

        cauHinhBang();

        taoDuLieuMau();

        tblKhachHang.setItems(danhSach);

        tblKhachHang.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldValue, newValue) -> {

                    if (newValue != null) {
                        hienThiLenForm(newValue);
                    }

                });
    }

    // =========================
    // CẤU HÌNH TABLE
    // =========================

    private void cauHinhBang() {

        colMaKhachHang.setCellValueFactory(
                new PropertyValueFactory<>("maKhachHang")
        );

        colHoTen.setCellValueFactory(
                new PropertyValueFactory<>("hoTen")
        );

        colCCCD.setCellValueFactory(
                new PropertyValueFactory<>("cccd")
        );

        colSoDienThoai.setCellValueFactory(
                new PropertyValueFactory<>("soDienThoai")
        );

        colEmail.setCellValueFactory(
                new PropertyValueFactory<>("email")
        );

        colDiaChi.setCellValueFactory(
                new PropertyValueFactory<>("diaChi")
        );

        colTenDangNhap.setCellValueFactory(
                new PropertyValueFactory<>("tenDangNhap")
        );
    }

    // =========================
    // DỮ LIỆU MẪU
    // =========================

    private void taoDuLieuMau() {

        danhSach.addAll(

                new KhachHangData(
                        "KH001",
                        "Nguyễn Văn An",
                        "079123456789",
                        "0901234567",
                        "nguyenvanan@gmail.com",
                        "Quận 1, TP. Hồ Chí Minh",
                        "nguyenvanan"
                ),

                new KhachHangData(
                        "KH002",
                        "Trần Thị Bình",
                        "079234567890",
                        "0912345678",
                        "tranthibinh@gmail.com",
                        "Quận 3, TP. Hồ Chí Minh",
                        "tranthibinh"
                ),

                new KhachHangData(
                        "KH003",
                        "Lê Văn Cường",
                        "079345678901",
                        "0987654321",
                        "levancuong@gmail.com",
                        "TP. Thủ Đức, TP. Hồ Chí Minh",
                        "levancuong"
                )
        );
    }

    // =========================
    // TÌM KIẾM
    // =========================

    @FXML
    private void onTimKiem() {

        String maKhachHang =
                txtMaKhachHang.getText().trim();

        String hoTen =
                txtHoTen.getText().trim();

        String cccd =
                txtCCCD.getText().trim();

        String soDienThoai =
                txtSoDienThoai.getText().trim();

        String email =
                txtEmail.getText().trim();

        String diaChi =
                txtDiaChi.getText().trim();

        String tenDangNhap =
                txtTenDangNhap.getText().trim();

        if (maKhachHang.isEmpty()
                && hoTen.isEmpty()
                && cccd.isEmpty()
                && soDienThoai.isEmpty()
                && email.isEmpty()
                && diaChi.isEmpty()
                && tenDangNhap.isEmpty()) {

            tblKhachHang.setItems(danhSach);
            return;
        }

        ObservableList<KhachHangData> ketQua =
                FXCollections.observableArrayList();

        for (KhachHangData khachHang : danhSach) {

            boolean dungMa =
                    maKhachHang.isEmpty()
                            || khachHang.getMaKhachHang()
                            .toLowerCase()
                            .contains(maKhachHang.toLowerCase());

            boolean dungHoTen =
                    hoTen.isEmpty()
                            || khachHang.getHoTen()
                            .toLowerCase()
                            .contains(hoTen.toLowerCase());

            boolean dungCCCD =
                    cccd.isEmpty()
                            || khachHang.getCccd()
                            .contains(cccd);

            boolean dungSoDienThoai =
                    soDienThoai.isEmpty()
                            || khachHang.getSoDienThoai()
                            .contains(soDienThoai);

            boolean dungEmail =
                    email.isEmpty()
                            || khachHang.getEmail()
                            .toLowerCase()
                            .contains(email.toLowerCase());

            boolean dungDiaChi =
                    diaChi.isEmpty()
                            || khachHang.getDiaChi()
                            .toLowerCase()
                            .contains(diaChi.toLowerCase());

            boolean dungTenDangNhap =
                    tenDangNhap.isEmpty()
                            || khachHang.getTenDangNhap()
                            .toLowerCase()
                            .contains(tenDangNhap.toLowerCase());

            if (dungMa
                    && dungHoTen
                    && dungCCCD
                    && dungSoDienThoai
                    && dungEmail
                    && dungDiaChi
                    && dungTenDangNhap) {

                ketQua.add(khachHang);
            }
        }

        tblKhachHang.setItems(ketQua);
    }

    // =========================
    // THÊM
    // =========================

    @FXML
    private void onThem() {

        String maKhachHang =
                txtMaKhachHang.getText().trim();

        String hoTen =
                txtHoTen.getText().trim();

        String cccd =
                txtCCCD.getText().trim();

        String soDienThoai =
                txtSoDienThoai.getText().trim();

        String email =
                txtEmail.getText().trim();

        String diaChi =
                txtDiaChi.getText().trim();

        String tenDangNhap =
                txtTenDangNhap.getText().trim();

        if (maKhachHang.isEmpty()
                || hoTen.isEmpty()
                || cccd.isEmpty()
                || soDienThoai.isEmpty()
                || email.isEmpty()
                || diaChi.isEmpty()
                || tenDangNhap.isEmpty()) {

            System.out.println(
                    "Vui lòng nhập đầy đủ thông tin khách hàng."
            );

            return;
        }

        KhachHangData khachHangMoi =
                new KhachHangData(
                        maKhachHang,
                        hoTen,
                        cccd,
                        soDienThoai,
                        email,
                        diaChi,
                        tenDangNhap
                );

        danhSach.add(khachHangMoi);

        tblKhachHang.setItems(danhSach);

        lamSachForm();

        System.out.println(
                "Đã thêm khách hàng: " + maKhachHang
        );
    }

    // =========================
    // SỬA
    // =========================

    @FXML
    private void onSua() {

        KhachHangData selected =
                tblKhachHang
                        .getSelectionModel()
                        .getSelectedItem();

        if (selected == null) {

            System.out.println(
                    "Vui lòng chọn khách hàng cần sửa."
            );

            return;
        }

        String maKhachHang =
                txtMaKhachHang.getText().trim();

        String hoTen =
                txtHoTen.getText().trim();

        String cccd =
                txtCCCD.getText().trim();

        String soDienThoai =
                txtSoDienThoai.getText().trim();

        String email =
                txtEmail.getText().trim();

        String diaChi =
                txtDiaChi.getText().trim();

        String tenDangNhap =
                txtTenDangNhap.getText().trim();

        if (maKhachHang.isEmpty()
                || hoTen.isEmpty()
                || cccd.isEmpty()
                || soDienThoai.isEmpty()
                || email.isEmpty()
                || diaChi.isEmpty()
                || tenDangNhap.isEmpty()) {

            System.out.println(
                    "Vui lòng nhập đầy đủ thông tin."
            );

            return;
        }

        int index = danhSach.indexOf(selected);

        danhSach.set(
                index,
                new KhachHangData(
                        maKhachHang,
                        hoTen,
                        cccd,
                        soDienThoai,
                        email,
                        diaChi,
                        tenDangNhap
                )
        );

        tblKhachHang.refresh();

        lamSachForm();

        System.out.println(
                "Đã cập nhật khách hàng: " + maKhachHang
        );
    }

    // =========================
    // XÓA
    // =========================

    @FXML
    private void onXoa() {

        KhachHangData selected =
                tblKhachHang
                        .getSelectionModel()
                        .getSelectedItem();

        if (selected == null) {

            System.out.println(
                    "Vui lòng chọn khách hàng cần xóa."
            );

            return;
        }

        danhSach.remove(selected);

        lamSachForm();

        System.out.println(
                "Đã xóa khách hàng: "
                        + selected.getMaKhachHang()
        );
    }

    // =========================
    // LÀM MỚI
    // =========================

    @FXML
    private void onLamMoi() {

        lamSachForm();

        tblKhachHang.setItems(danhSach);

        tblKhachHang
                .getSelectionModel()
                .clearSelection();

        System.out.println("Đã làm mới.");
    }

    // =========================
    // HIỂN THỊ DỮ LIỆU LÊN FORM
    // =========================

    private void hienThiLenForm(
            KhachHangData khachHang
    ) {

        txtMaKhachHang.setText(
                khachHang.getMaKhachHang()
        );

        txtHoTen.setText(
                khachHang.getHoTen()
        );

        txtCCCD.setText(
                khachHang.getCccd()
        );

        txtSoDienThoai.setText(
                khachHang.getSoDienThoai()
        );

        txtEmail.setText(
                khachHang.getEmail()
        );

        txtDiaChi.setText(
                khachHang.getDiaChi()
        );

        txtTenDangNhap.setText(
                khachHang.getTenDangNhap()
        );
    }

    // =========================
    // XÓA FORM
    // =========================

    private void lamSachForm() {

        txtMaKhachHang.clear();
        txtHoTen.clear();
        txtCCCD.clear();
        txtSoDienThoai.clear();
        txtEmail.clear();
        txtDiaChi.clear();
        txtTenDangNhap.clear();
    }

    // =========================
    // ĐIỀU HƯỚNG
    // =========================

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

            e.printStackTrace();
        }
    }

    @FXML
    private void onKhachHang(ActionEvent event) {
        // Đang ở trang Khách hàng
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

    // =========================
    // DATA CLASS
    // =========================

    public static class KhachHangData {

        private final String maKhachHang;
        private final String hoTen;
        private final String cccd;
        private final String soDienThoai;
        private final String email;
        private final String diaChi;
        private final String tenDangNhap;

        public KhachHangData(
                String maKhachHang,
                String hoTen,
                String cccd,
                String soDienThoai,
                String email,
                String diaChi,
                String tenDangNhap
        ) {
            this.maKhachHang = maKhachHang;
            this.hoTen = hoTen;
            this.cccd = cccd;
            this.soDienThoai = soDienThoai;
            this.email = email;
            this.diaChi = diaChi;
            this.tenDangNhap = tenDangNhap;
        }

        public String getMaKhachHang() {
            return maKhachHang;
        }

        public String getHoTen() {
            return hoTen;
        }

        public String getCccd() {
            return cccd;
        }

        public String getSoDienThoai() {
            return soDienThoai;
        }

        public String getEmail() {
            return email;
        }

        public String getDiaChi() {
            return diaChi;
        }

        public String getTenDangNhap() {
            return tenDangNhap;
        }
    }
}