package org.example.appnganhang.controller.nhanvien;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import org.example.appnganhang.model.entity.LichSuGiaoDich;
import org.example.appnganhang.controller.service.Iml.GiaoDichServiceImpl;
import org.example.appnganhang.controller.service.intf.GiaoDichService;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import static org.example.appnganhang.controller.util.Navigation.moView;

public class LichSuGiaoDichController {
    @FXML private TextField txtMaTaiKhoan;

    @FXML private Button btnTimKiem;
    @FXML private Button btnLamMoi;

    @FXML private TableView<LichSuGiaoDich> tblLichSu;
    @FXML private TableColumn<LichSuGiaoDich, Number> colMaLog;
    @FXML private TableColumn<LichSuGiaoDich, String> colMaGD;
    @FXML private TableColumn<LichSuGiaoDich, String> colSoTaiKhoan;
    @FXML private TableColumn<LichSuGiaoDich, String> colHanhDong;
    @FXML private TableColumn<LichSuGiaoDich, BigDecimal> colSoTienThayDoi;
    @FXML private TableColumn<LichSuGiaoDich, BigDecimal> colSoDuSauThayDoi;
    @FXML private TableColumn<LichSuGiaoDich, String> colTrangThai;
    @FXML private TableColumn<LichSuGiaoDich, LocalDateTime> colThoiGianGhiLog;

    private final ObservableList<LichSuGiaoDich> danhSachGiaoDich = FXCollections.observableArrayList();

    private final GiaoDichService giaoDichService;

    public LichSuGiaoDichController() {
        this.giaoDichService = new GiaoDichServiceImpl();
    }


    @FXML
    private void initialize() {
        cauHinhBang();
        tblLichSu.setItems(danhSachGiaoDich);
        taiDanhSachLichSuGiaoDich();
    }

    private void cauHinhBang() {
        colMaLog.setCellValueFactory(new PropertyValueFactory<>("maLog"));
        colMaGD.setCellValueFactory(new PropertyValueFactory<>("maGiaoDich"));
        colSoTaiKhoan.setCellValueFactory(new PropertyValueFactory<>("soTaiKhoanLienQuan"));
        colHanhDong.setCellValueFactory(new PropertyValueFactory<>("hanhDong"));
        colSoTienThayDoi.setCellValueFactory(new PropertyValueFactory<>("soTienThayDoi"));
        colSoDuSauThayDoi.setCellValueFactory(new PropertyValueFactory<>("soDuSauThayDoi"));
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        colThoiGianGhiLog.setCellValueFactory(new PropertyValueFactory<>("thoiGianGhiLog"));

        DecimalFormat dinhDangTien = new DecimalFormat(
                "#,##0", DecimalFormatSymbols.getInstance(Locale.GERMANY));
        colSoTienThayDoi.setCellFactory(column -> taoOFormattedTien(dinhDangTien));
        colSoDuSauThayDoi.setCellFactory(column -> taoOFormattedTien(dinhDangTien));
    }

    private TableCell<LichSuGiaoDich, BigDecimal> taoOFormattedTien(DecimalFormat dinhDangTien) {
        return new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal soTien, boolean empty) {
                super.updateItem(soTien, empty);
                setText(empty || soTien == null ? null : dinhDangTien.format(soTien));
            }
        };
    }

    private void taiDanhSachLichSuGiaoDich() {
        try {
            danhSachGiaoDich.setAll(giaoDichService.xemTatCaLichSuGiaoDich());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Không thể tải danh sách lịch sử giao dịch.");
        }
    }

    @FXML
    private void onTimKiem() {

        String maTaiKhoan = txtMaTaiKhoan.getText().trim();

        if (maTaiKhoan.isEmpty()) {

            danhSachGiaoDich.clear();
            tblLichSu.setItems(danhSachGiaoDich);

            return;
        }

        try {

            java.util.List<LichSuGiaoDich> ketQua = giaoDichService.xemLichSuGiaoDich(maTaiKhoan);

            danhSachGiaoDich.setAll(ketQua);
            tblLichSu.setItems(danhSachGiaoDich);

            System.out.println("Đã tìm lịch sử giao dịch của tài khoản: " + maTaiKhoan);

        } catch (Exception e) {

            e.printStackTrace();

            System.out.println("Không thể tìm lịch sử giao dịch.");
        }
    }

    @FXML
    private void onLamMoi() {

        txtMaTaiKhoan.clear();
        danhSachGiaoDich.clear();

        tblLichSu.setItems(danhSachGiaoDich);

        tblLichSu.getSelectionModel()
                .clearSelection();
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
}
