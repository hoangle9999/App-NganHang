package org.example.appnganhang.controller.nhanvien;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import org.example.appnganhang.controller.service.Iml.ChuyenTienServiceImpl;
import org.example.appnganhang.controller.service.intf.ChuyenTienService;

import java.math.BigDecimal;

import static org.example.appnganhang.controller.util.Navigation.moView;

public class TransferController {

    @FXML private TextField txtSoTaiKhoanGui;
    @FXML private TextField txtSoTaiKhoanNhan;
    @FXML private ComboBox<String> cbNganHang;
    @FXML private TextField txtSoTien;
    @FXML private TextArea txtNoiDung;

    private final ChuyenTienService chuyenTienService = new ChuyenTienServiceImpl();

    @FXML
    private void initialize() {

        cbNganHang.getItems().addAll(
                "SMART BANK",
                "Vietcombank",
                "BIDV",
                "VietinBank",
                "Agribank",
                "Techcombank",
                "MB Bank"
        );
    }

    @FXML
    private void onXacNhan() {
        String soTaiKhoanGui = txtSoTaiKhoanGui.getText().trim();
        String soTaiKhoanNhan = txtSoTaiKhoanNhan.getText().trim();
        String nganHang = cbNganHang.getValue();
        String soTienText = txtSoTien.getText().trim();
        String noiDung = txtNoiDung.getText().trim();

        if (soTaiKhoanGui.isBlank()
                || soTaiKhoanNhan.isBlank()
                || nganHang == null
                || soTienText.isBlank()) {
            System.out.println("Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        if (soTaiKhoanGui.equals(soTaiKhoanNhan)) {
            System.out.println("Tài khoản gửi và nhận không được trùng nhau.");
            return;
        }

        BigDecimal soTien;

        try {
            soTien = new BigDecimal(soTienText);
        } catch (NumberFormatException e) {
            System.out.println("Số tiền không hợp lệ.");
            return;
        }

        if (soTien.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Số tiền phải lớn hơn 0.");
            return;
        }

        boolean ketQua = chuyenTienService.chuyenTien(
                soTaiKhoanGui,
                soTaiKhoanNhan,
                nganHang,
                soTien,
                noiDung
        );

        if (ketQua) {
            System.out.println("Chuyển tiền thành công.");

            txtSoTaiKhoanGui.clear();
            txtSoTaiKhoanNhan.clear();
            txtSoTien.clear();
            txtNoiDung.clear();
            cbNganHang.setValue(null);
        } else {
            System.out.println("Chuyển tiền thất bại.");
        }
    }
    // =========================
    // SIDEBAR
    // =========================
    @FXML
    private void onTrangChu(ActionEvent event){moView("dashboard-view.fxml", event);}

    @FXML
    private void onKhachHang(ActionEvent event) {
        moView("customer-view.fxml", event);
    }

    @FXML
    private void onChuyenTien(ActionEvent event) {
        // Đang ở trang Chuyển tiền
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
}