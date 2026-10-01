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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;

public class BaoCaoController {

    @FXML
    private TextField txtTuNgay;

    @FXML
    private TextField txtDenNgay;

    @FXML
    private ComboBox<String> cboLoaiBaoCao;

    @FXML
    private Button btnThongKe;

    @FXML
    private Button btnLamMoi;

    @FXML
    private Button btnXuatBaoCao;

    @FXML
    private Label lblTongSoGiaoDich;

    @FXML
    private Label lblTongTien;

    @FXML
    private Label lblGiaoDichThanhCong;

    @FXML
    private Label lblGiaoDichThatBai;

    @FXML
    private TableView<BaoCaoData> tblBaoCao;

    @FXML
    private TableColumn<BaoCaoData, String> colMaGiaoDich;

    @FXML
    private TableColumn<BaoCaoData, String> colTaiKhoanNguon;

    @FXML
    private TableColumn<BaoCaoData, String> colTaiKhoanDich;

    @FXML
    private TableColumn<BaoCaoData, String> colSoTien;

    @FXML
    private TableColumn<BaoCaoData, String> colNgayGiaoDich;

    @FXML
    private TableColumn<BaoCaoData, String> colLoaiGiaoDich;

    @FXML
    private TableColumn<BaoCaoData, String> colTrangThai;

    private final ObservableList<BaoCaoData> danhSach =
            FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        cauHinhBang();
        khoiTaoLoaiBaoCao();
        taoDuLieuMau();
        capNhatThongKe(danhSach);
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

        colLoaiGiaoDich.setCellValueFactory(
                new PropertyValueFactory<>("loaiGiaoDich")
        );

        colTrangThai.setCellValueFactory(
                new PropertyValueFactory<>("trangThai")
        );
    }

    private void khoiTaoLoaiBaoCao() {

        cboLoaiBaoCao.getItems().addAll(
                "Tất cả giao dịch",
                "Giao dịch thành công",
                "Giao dịch thất bại",
                "Chuyển tiền",
                "Thanh toán"
        );

        cboLoaiBaoCao.setValue("Tất cả giao dịch");
    }

    private void taoDuLieuMau() {

        danhSach.addAll(

                new BaoCaoData(
                        "GD001",
                        "190388889999",
                        "190377776666",
                        "2,000,000 VNĐ",
                        "01/10/2026 08:30",
                        "Chuyển tiền",
                        "Thành công"
                ),

                new BaoCaoData(
                        "GD002",
                        "190355554444",
                        "190322221111",
                        "500,000 VNĐ",
                        "01/10/2026 09:15",
                        "Thanh toán",
                        "Thành công"
                ),

                new BaoCaoData(
                        "GD003",
                        "190311112222",
                        "190333334444",
                        "1,500,000 VNĐ",
                        "01/10/2026 10:20",
                        "Chuyển tiền",
                        "Thất bại"
                )
        );

        tblBaoCao.setItems(danhSach);
    }

    // =========================================================
    // THỐNG KÊ
    // =========================================================

    @FXML
    private void onThongKe() {

        String loaiBaoCao = cboLoaiBaoCao.getValue();

        ObservableList<BaoCaoData> ketQua =
                FXCollections.observableArrayList();

        for (BaoCaoData data : danhSach) {

            boolean phuHop = true;

            if (loaiBaoCao != null) {

                switch (loaiBaoCao) {

                    case "Giao dịch thành công":
                        phuHop = data.getTrangThai()
                                .equals("Thành công");
                        break;

                    case "Giao dịch thất bại":
                        phuHop = data.getTrangThai()
                                .equals("Thất bại");
                        break;

                    case "Chuyển tiền":
                        phuHop = data.getLoaiGiaoDich()
                                .equals("Chuyển tiền");
                        break;

                    case "Thanh toán":
                        phuHop = data.getLoaiGiaoDich()
                                .equals("Thanh toán");
                        break;

                    default:
                        phuHop = true;
                        break;
                }
            }

            if (phuHop) {
                ketQua.add(data);
            }
        }

        tblBaoCao.setItems(ketQua);

        capNhatThongKe(ketQua);
    }

    private void capNhatThongKe(
            ObservableList<BaoCaoData> danhSachThongKe
    ) {

        int tongSoGiaoDich = danhSachThongKe.size();

        int giaoDichThanhCong = 0;
        int giaoDichThatBai = 0;

        long tongTien = 0;

        for (BaoCaoData data : danhSachThongKe) {

            if (data.getTrangThai().equals("Thành công")) {
                giaoDichThanhCong++;
            }

            if (data.getTrangThai().equals("Thất bại")) {
                giaoDichThatBai++;
            }

            tongTien += chuyenSoTien(data.getSoTien());
        }

        lblTongSoGiaoDich.setText(
                String.valueOf(tongSoGiaoDich)
        );

        lblTongTien.setText(
                String.format("%,d VNĐ", tongTien)
        );

        lblGiaoDichThanhCong.setText(
                String.valueOf(giaoDichThanhCong)
        );

        lblGiaoDichThatBai.setText(
                String.valueOf(giaoDichThatBai)
        );
    }

    private long chuyenSoTien(String soTien) {

        if (soTien == null || soTien.isBlank()) {
            return 0;
        }

        String giaTri = soTien
                .replace("VNĐ", "")
                .replace(",", "")
                .trim();

        try {
            return Long.parseLong(giaTri);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // =========================================================
    // LÀM MỚI
    // =========================================================

    @FXML
    private void onLamMoi() {

        txtTuNgay.clear();
        txtDenNgay.clear();

        cboLoaiBaoCao.setValue("Tất cả giao dịch");

        tblBaoCao.setItems(danhSach);

        capNhatThongKe(danhSach);

        tblBaoCao.getSelectionModel().clearSelection();
    }

    // =========================================================
    // XUẤT BÁO CÁO
    // =========================================================

    @FXML
    private void onXuatBaoCao() {

        System.out.println("===== XUẤT BÁO CÁO =====");

        System.out.println(
                "Từ ngày: " + txtTuNgay.getText()
        );

        System.out.println(
                "Đến ngày: " + txtDenNgay.getText()
        );

        System.out.println(
                "Loại báo cáo: " + cboLoaiBaoCao.getValue()
        );

        System.out.println(
                "Tổng số giao dịch: "
                        + lblTongSoGiaoDich.getText()
        );

        System.out.println(
                "Tổng tiền: "
                        + lblTongTien.getText()
        );

        System.out.println(
                "Giao dịch thành công: "
                        + lblGiaoDichThanhCong.getText()
        );

        System.out.println(
                "Giao dịch thất bại: "
                        + lblGiaoDichThatBai.getText()
        );
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
        moView("branch-view.fxml", event);
    }

    @FXML
    private void onNhanVien(ActionEvent event) {
        moView("employee-view.fxml", event);
    }

    @FXML
    private void onBaoCao(ActionEvent event) {
        // Đang ở trang Báo cáo
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

    public static class BaoCaoData {

        private final String maGiaoDich;
        private final String taiKhoanNguon;
        private final String taiKhoanDich;
        private final String soTien;
        private final String ngayGiaoDich;
        private final String loaiGiaoDich;
        private final String trangThai;

        public BaoCaoData(
                String maGiaoDich,
                String taiKhoanNguon,
                String taiKhoanDich,
                String soTien,
                String ngayGiaoDich,
                String loaiGiaoDich,
                String trangThai
        ) {
            this.maGiaoDich = maGiaoDich;
            this.taiKhoanNguon = taiKhoanNguon;
            this.taiKhoanDich = taiKhoanDich;
            this.soTien = soTien;
            this.ngayGiaoDich = ngayGiaoDich;
            this.loaiGiaoDich = loaiGiaoDich;
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

        public String getLoaiGiaoDich() {
            return loaiGiaoDich;
        }

        public String getTrangThai() {
            return trangThai;
        }
    }
}