package org.example.appnganhang.controller.nhanvien;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import org.example.appnganhang.model.entity.GiaoDichChuyenTien;
import org.example.appnganhang.controller.service.Iml.GiaoDichServiceImpl;
import org.example.appnganhang.controller.service.intf.GiaoDichService;

import java.util.List;

import static org.example.appnganhang.controller.util.Navigation.moView;

public class TraCuuGiaoDichController {

    @FXML private TextField txtMaGiaoDich;
    @FXML private TextField txtMaTaiKhoan;

    @FXML private ComboBox<String> cboLoaiGiaoDich;
    @FXML private ComboBox<String> cboTrangThai;

    @FXML private TableView<GiaoDichChuyenTien> tblGiaoDich;
    @FXML private TableColumn<GiaoDichChuyenTien, String> colMaGiaoDich;
    @FXML private TableColumn<GiaoDichChuyenTien, String> colTaiKhoanNguon;
    @FXML private TableColumn<GiaoDichChuyenTien, String> colTaiKhoanDich;
    @FXML private TableColumn<GiaoDichChuyenTien, String> colSoTien;
    @FXML private TableColumn<GiaoDichChuyenTien, String> colNgayGiaoDich;
    @FXML private TableColumn<GiaoDichChuyenTien, String> colNoiDung;
    @FXML private TableColumn<GiaoDichChuyenTien, String> colMaLoaiGD;
    @FXML private TableColumn<GiaoDichChuyenTien, String> colTrangThai;

    private final ObservableList<GiaoDichChuyenTien> danhSachGiaoDich = FXCollections.observableArrayList();
    private final GiaoDichService giaoDichService = new GiaoDichServiceImpl();

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
        taiTatCaGiaoDich();
    }


    private void cauHinhBang() {

        colMaGiaoDich.setCellValueFactory(new PropertyValueFactory<>("maGiaoDich"));
        colTaiKhoanNguon.setCellValueFactory(new PropertyValueFactory<>("maTaiKhoanNguon"));
        colTaiKhoanDich.setCellValueFactory(new PropertyValueFactory<>("maTaiKhoanDich"));
        colSoTien.setCellValueFactory(new PropertyValueFactory<>("soTien"));
        colNgayGiaoDich.setCellValueFactory(new PropertyValueFactory<>("ngayGiaoDich"));
        colNoiDung.setCellValueFactory(new PropertyValueFactory<>("noiDung"));
        colMaLoaiGD.setCellValueFactory(new PropertyValueFactory<>("maLoaiGD"));
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
    }

    private void taiTatCaGiaoDich() {
        danhSachGiaoDich.setAll(giaoDichService.getTatCaGiaoDich());
        tblGiaoDich.setItems(danhSachGiaoDich);
    }

    @FXML
    private void onTimKiem() {

        String maGiaoDich = txtMaGiaoDich.getText().trim();
        String maTaiKhoan = txtMaTaiKhoan.getText().trim(); // tài khoản nguồn
        String loai = cboLoaiGiaoDich.getValue();
        String trangThai = cboTrangThai.getValue();

        if (!maGiaoDich.isEmpty()) {
            onTimTheoMa(maGiaoDich);
            return;
        }

        if (!maTaiKhoan.isEmpty()) {
            onTimTheoTaiKhoan(maTaiKhoan);
            return;
        }

        if (loai != null) {
            onTimTheoLoai(loai);
            return;
        }

        if (trangThai != null) {
            onTimTheoTrangThai(trangThai);
            return;
        }

        tblGiaoDich.setItems(danhSachGiaoDich);
    }


    @FXML
    private void onTheoMa() {

        String ma = txtMaGiaoDich.getText().trim();

        if (ma.isEmpty()) {
            tblGiaoDich.setItems(danhSachGiaoDich);
            return;
        }

        onTimTheoMa(ma);
    }

    private void onTimTheoMa(String maGiaoDich) {

        giaoDichService.traCuuTheoMa(maGiaoDich)
                .ifPresentOrElse(
                        giaoDich -> {
                            ObservableList<GiaoDichChuyenTien> ketQua =
                                    FXCollections.observableArrayList();

                            ketQua.add(giaoDich);

                            tblGiaoDich.setItems(ketQua);
                        },
                        () -> {
                            tblGiaoDich.setItems(
                                    FXCollections.observableArrayList()
                            );
                        }
                );
    }

    @FXML
    private void onTheoTaiKhoan() {

        String maTaiKhoan = txtMaTaiKhoan.getText().trim();

        if (maTaiKhoan.isEmpty()) {
            tblGiaoDich.setItems(danhSachGiaoDich);
            return;
        }

        onTimTheoTaiKhoan(maTaiKhoan);
    }

    private void onTimTheoTaiKhoan(String maTaiKhoan) {

        List<GiaoDichChuyenTien> ketQua = giaoDichService.traCuuTheoTaiKhoan(maTaiKhoan);
        tblGiaoDich.setItems(FXCollections.observableArrayList(ketQua));
    }

    @FXML
    private void onTheoLoai() {

        String loai = cboLoaiGiaoDich.getValue();

        if (loai == null) {
            tblGiaoDich.setItems(danhSachGiaoDich);
            return;
        }

        onTimTheoLoai(loai);
    }

    private void onTimTheoLoai(String tenLoai) {

        String maLoaiGD = chuyenTenLoaiSangMa(tenLoai);

        if (maLoaiGD.isEmpty()) {
            tblGiaoDich.setItems(FXCollections.observableArrayList());
            return;
        }

        List<GiaoDichChuyenTien> ketQua = giaoDichService.traCuuTheoLoai(maLoaiGD);
        tblGiaoDich.setItems(FXCollections.observableArrayList(ketQua));
    }

    @FXML
    private void onTheoTrangThai() {

        String trangThai = cboTrangThai.getValue();

        if (trangThai == null) {
            tblGiaoDich.setItems(danhSachGiaoDich);
            return;
        }

        onTimTheoTrangThai(trangThai);
    }

    private void onTimTheoTrangThai(String trangThai) {

        List<GiaoDichChuyenTien> ketQua = giaoDichService.traCuuTheoTrangThai(trangThai);
        tblGiaoDich.setItems(FXCollections.observableArrayList(ketQua));
    }

    private String chuyenTenLoaiSangMa(String tenLoai) {

        return switch (tenLoai) {
            case "Chuyển tiền" -> "CK";
            case "Nạp tiền" -> "NT";
            case "Rút tiền" -> "RT";
            case "Thanh toán" -> "TT";
            default -> "";
        };
    }

    @FXML
    private void onLamMoi() {

        txtMaGiaoDich.clear();
        txtMaTaiKhoan.clear();

        cboLoaiGiaoDich.setValue(null);
        cboTrangThai.setValue(null);

        taiTatCaGiaoDich();
    }

    // =========================================================
    // ĐIỀU HƯỚNG SIDEBAR
    // =========================================================
    @FXML
    private void onTrangChu(ActionEvent event){moView("dashboard-view.fxml", event);}

    @FXML
    private void onKhachHang(ActionEvent event) {
        moView("customer-view.fxml", event);
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
}