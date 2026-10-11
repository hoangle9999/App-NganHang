package org.example.appnganhang.controller.nhanvien;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import org.example.appnganhang.controller.service.Iml.HanMucServiceImpl;
import org.example.appnganhang.controller.service.intf.HanMucService;
import org.example.appnganhang.model.entity.HanMucGiaoDich;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.example.appnganhang.controller.util.Navigation.moView;

public class HanMucController {
    @FXML private TextField txtMaHanMuc;
    @FXML private TextField txtTenHanMuc;
    @FXML private TextField txtHanMucNgay;

    @FXML private TableView<HanMucGiaoDich> tblHanMuc;
    @FXML private TableColumn<HanMucGiaoDich, String> colMaHanMuc;
    @FXML private TableColumn<HanMucGiaoDich, String> colTenHanMuc;
    @FXML private TableColumn<HanMucGiaoDich, BigDecimal> colHanMucNgay;

    @FXML private Button btnSua;
    @FXML private Button btnXoa;
    @FXML private Button btnTimKiem;
    @FXML private Button btnLamMoi;

    private final ObservableList<HanMucGiaoDich> danhSach = FXCollections.observableArrayList();

    private final HanMucService hanMucService = new HanMucServiceImpl();

    @FXML
    private void initialize() {
        cauHinhBang();
        tblHanMuc.setItems(danhSach);

        tblHanMuc.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldValue, newValue) -> {
                    if (newValue != null) {
                        hienThiDuLieu(newValue);
                    }
                });

        taiDanhSachHanMuc();
    }

    private void cauHinhBang() {
        colMaHanMuc.setCellValueFactory(new PropertyValueFactory<>("maHanMuc"));
        colTenHanMuc.setCellValueFactory(new PropertyValueFactory<>("tenHanMuc"));
        colHanMucNgay.setCellValueFactory(new PropertyValueFactory<>("hanMucNgay"));
    }

    private void taiDanhSachHanMuc() {
        danhSach.setAll(hanMucService.getDanhSachHanMuc());
    }

    private void hienThiDuLieu(HanMucGiaoDich hanMuc) {
        txtMaHanMuc.setText(
                hanMuc.getMaHanMuc() == null
                        ? ""
                        : hanMuc.getMaHanMuc()
        );

        txtTenHanMuc.setText(
                hanMuc.getTenHanMuc() == null
                        ? ""
                        : hanMuc.getTenHanMuc()
        );

        txtHanMucNgay.setText(
                hanMuc.getHanMucNgay() == null
                        ? ""
                        : hanMuc.getHanMucNgay().toPlainString()
        );
    }

    @FXML
    private void onTimKiemTheoMa() {
        String maHanMuc = txtMaHanMuc.getText().trim();

        if (maHanMuc.isEmpty()) {
            onTimKiem();
            return;
        }

        Optional<HanMucGiaoDich> ketQua =
                hanMucService.getHanMuc(maHanMuc);

        danhSach.clear();
        ketQua.ifPresent(danhSach::add);

        tblHanMuc.getSelectionModel().clearSelection();
    }

    @FXML
    private void onTimKiemTheoTen() {
        String tenHanMuc = txtTenHanMuc.getText()
                .trim()
                .toLowerCase();

        if (tenHanMuc.isEmpty()) {
            onTimKiem();
            return;
        }

        danhSach.setAll(
                hanMucService.getDanhSachHanMuc()
                        .stream()
                        .filter(hanMuc ->
                                hanMuc.getTenHanMuc() != null
                                        && hanMuc.getTenHanMuc()
                                        .toLowerCase()
                                        .contains(tenHanMuc)
                        )
                        .toList()
        );

        tblHanMuc.getSelectionModel().clearSelection();
    }

    @FXML
    private void onTimKiemTheoNgay() {
        String hanMucNgayText = txtHanMucNgay.getText().trim();

        if (hanMucNgayText.isEmpty()) {
            onTimKiem();
            return;
        }

        BigDecimal hanMucNgay;

        try {
            hanMucNgay = new BigDecimal(hanMucNgayText);
        } catch (NumberFormatException e) {
            hienThongBao(
                    Alert.AlertType.WARNING,
                    "Hạn mức ngày phải là số hợp lệ."
            );
            return;
        }

        danhSach.setAll(
                hanMucService.getDanhSachHanMuc()
                        .stream()
                        .filter(hanMuc ->
                                hanMuc.getHanMucNgay() != null
                                        && hanMuc.getHanMucNgay()
                                        .compareTo(hanMucNgay) == 0
                        )
                        .toList()
        );

        tblHanMuc.getSelectionModel().clearSelection();
    }

    @FXML
    private void onTimKiem() {
        String maHanMuc = txtMaHanMuc.getText().trim();
        String tenHanMuc = txtTenHanMuc.getText()
                .trim()
                .toLowerCase();
        String hanMucNgayText = txtHanMucNgay.getText().trim();

        BigDecimal hanMucNgay = null;

        if (!hanMucNgayText.isEmpty()) {
            try {
                hanMucNgay = new BigDecimal(hanMucNgayText);
            } catch (NumberFormatException e) {
                hienThongBao(
                        Alert.AlertType.WARNING,
                        "Hạn mức ngày phải là số hợp lệ."
                );
                return;
            }
        }

        final BigDecimal giaTriHanMucNgay = hanMucNgay;

        List<HanMucGiaoDich> ketQua =
                hanMucService.getDanhSachHanMuc()
                        .stream()
                        .filter(hanMuc ->
                                maHanMuc.isEmpty()
                                        || (hanMuc.getMaHanMuc() != null
                                        && hanMuc.getMaHanMuc()
                                        .equalsIgnoreCase(maHanMuc))
                        )
                        .filter(hanMuc ->
                                tenHanMuc.isEmpty()
                                        || (hanMuc.getTenHanMuc() != null
                                        && hanMuc.getTenHanMuc()
                                        .toLowerCase()
                                        .contains(tenHanMuc))
                        )
                        .filter(hanMuc ->
                                giaTriHanMucNgay == null
                                        || (hanMuc.getHanMucNgay() != null
                                        && hanMuc.getHanMucNgay()
                                        .compareTo(giaTriHanMucNgay) == 0)
                        )
                        .collect(Collectors.toList());

        danhSach.setAll(ketQua);

        tblHanMuc.getSelectionModel().clearSelection();

        if (ketQua.isEmpty()) {
            hienThongBao(
                    Alert.AlertType.INFORMATION,
                    "Không tìm thấy hạn mức phù hợp."
            );
        }
    }

    @FXML
    private void onLamMoi() {
        txtMaHanMuc.clear();
        txtTenHanMuc.clear();
        txtHanMucNgay.clear();

        tblHanMuc.getSelectionModel().clearSelection();
        taiDanhSachHanMuc();
    }

    @FXML
    private void onSua() {
        HanMucGiaoDich selected =
                tblHanMuc.getSelectionModel().getSelectedItem();

        if (selected == null) {
            hienThongBao(
                    Alert.AlertType.WARNING,
                    "Vui lòng chọn hạn mức cần sửa."
            );
            return;
        }

        String tenHanMuc = txtTenHanMuc.getText().trim();
        String hanMucNgayText = txtHanMucNgay.getText().trim();

        if (tenHanMuc.isEmpty() || hanMucNgayText.isEmpty()) {
            hienThongBao(
                    Alert.AlertType.WARNING,
                    "Vui lòng nhập tên hạn mức và hạn mức ngày."
            );
            return;
        }

        BigDecimal hanMucNgay;

        try {
            hanMucNgay = new BigDecimal(hanMucNgayText);
        } catch (NumberFormatException e) {
            hienThongBao(
                    Alert.AlertType.WARNING,
                    "Hạn mức ngày phải là số hợp lệ."
            );
            return;
        }

        if (hanMucNgay.compareTo(BigDecimal.ZERO) <= 0) {
            hienThongBao(
                    Alert.AlertType.WARNING,
                    "Hạn mức ngày phải lớn hơn 0."
            );
            return;
        }

        HanMucGiaoDich hanMuc = new HanMucGiaoDich(
                selected.getMaHanMuc(),
                tenHanMuc,
                hanMucNgay
        );

        try {
            hanMucService.suaHanMuc(hanMuc);

            hienThongBao(
                    Alert.AlertType.INFORMATION,
                    "Cập nhật hạn mức thành công."
            );

            onLamMoi();
        } catch (Exception e) {
            hienThongBao(
                    Alert.AlertType.ERROR,
                    "Không thể sửa hạn mức: " + e.getMessage()
            );
        }
    }

    @FXML
    private void onXoa() {
        HanMucGiaoDich selected =
                tblHanMuc.getSelectionModel().getSelectedItem();

        if (selected == null) {
            hienThongBao(
                    Alert.AlertType.WARNING,
                    "Vui lòng chọn hạn mức cần xóa."
            );
            return;
        }

        try {
            boolean thanhCong =
                    hanMucService.xoaHanMuc(selected.getMaHanMuc());

            if (thanhCong) {
                hienThongBao(
                        Alert.AlertType.INFORMATION,
                        "Xóa hạn mức thành công."
                );
                onLamMoi();
            } else {
                hienThongBao(
                        Alert.AlertType.ERROR,
                        "Xóa hạn mức thất bại."
                );
            }
        } catch (Exception e) {
            hienThongBao(
                    Alert.AlertType.ERROR,
                    "Không thể xóa hạn mức: " + e.getMessage()
            );
        }
    }

    private void hienThongBao(
            Alert.AlertType loai,
            String noiDung
    ) {
        Alert alert = new Alert(loai);
        alert.setTitle("Quản lý hạn mức");
        alert.setHeaderText(null);
        alert.setContentText(noiDung);
        alert.showAndWait();
    }

    @FXML
    private void onTrangChu(ActionEvent event) {
        moView("dashboard-view.fxml", event);
    }

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
        moView("history-view.fxml", event);
    }

    @FXML
    private void onHanMuc(ActionEvent event) {
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