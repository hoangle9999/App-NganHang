package org.example.appnganhang.controller.nhanvien;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import org.example.appnganhang.model.entity.LoaiGiaoDich;
import org.example.appnganhang.controller.service.Iml.LoaiGiaoDichServiceImpl;
import org.example.appnganhang.controller.service.intf.LoaiGiaoDichService;

import java.util.List;

import static org.example.appnganhang.controller.util.Navigation.moView;

public class LoaiGiaoDichController {

    @FXML private TextField txtMaLoai;
    @FXML private TextField txtTenLoai;


    @FXML private TableView<LoaiGiaoDich> tblLoaiGiaoDich;
    @FXML private TableColumn<LoaiGiaoDich, String> colMaLoai;
    @FXML private TableColumn<LoaiGiaoDich, String> colTenLoai;
    @FXML private TableColumn<LoaiGiaoDich, String> colMaGiaoDich;
    @FXML private TableColumn<LoaiGiaoDich, String> colNoiDung;
    @FXML private TableColumn<LoaiGiaoDich, String> colTrangThai;

    private final ObservableList<LoaiGiaoDich> danhSach = FXCollections.observableArrayList();

    private final LoaiGiaoDichService loaiGiaoDichService = new LoaiGiaoDichServiceImpl();

    @FXML
    private void initialize() {

        cauHinhBang();

        taiDanhSach();

    }

    private void cauHinhBang() {
        colMaLoai.setCellValueFactory(new PropertyValueFactory<>("maLoaiGD"));
        colTenLoai.setCellValueFactory(new PropertyValueFactory<>("tenLoaiGD"));
        colMaGiaoDich.setCellValueFactory(new PropertyValueFactory<>("maGiaoDich"));
        colNoiDung.setCellValueFactory(new PropertyValueFactory<>("noiDung"));
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
    }

    private void taiDanhSach() {

        danhSach.clear();

        danhSach.addAll(loaiGiaoDichService.getDanhSachLoaiGiaoDich());

        tblLoaiGiaoDich.setItems(danhSach);
    }

    @FXML
    private void onTimKiemTheoMa() {
        String maLoai = txtMaLoai.getText().trim();

        if (maLoai.isEmpty()) {
            taiDanhSach();
            return;
        }

        List<LoaiGiaoDich> ketQua =
                loaiGiaoDichService.getDanhSachLoaiGiaoDich()
                        .stream()
                        .filter(item -> item.getMaLoaiGD() != null
                                && item.getMaLoaiGD()
                                .toLowerCase()
                                .contains(maLoai.toLowerCase()))
                        .toList();

        danhSach.setAll(ketQua);
        tblLoaiGiaoDich.setItems(danhSach);
    }

    @FXML
    private void onTimKiemTheoTen() {
        String tenLoai = txtTenLoai.getText().trim();

        if (tenLoai.isEmpty()) {
            taiDanhSach();
            return;
        }

        List<LoaiGiaoDich> ketQua =
                loaiGiaoDichService.getDanhSachLoaiGiaoDich()
                        .stream()
                        .filter(item -> item.getTenLoaiGD() != null
                                && item.getTenLoaiGD()
                                .toLowerCase()
                                .contains(tenLoai.toLowerCase()))
                        .toList();

        danhSach.setAll(ketQua);
        tblLoaiGiaoDich.setItems(danhSach);
    }

    @FXML
    private void onTimKiem() {
        String maLoai = txtMaLoai.getText().trim().toLowerCase();
        String tenLoai = txtTenLoai.getText().trim().toLowerCase();

        if (maLoai.isEmpty() && tenLoai.isEmpty()) {
            taiDanhSach();
            return;
        }

        List<LoaiGiaoDich> ketQua =
                loaiGiaoDichService.getDanhSachLoaiGiaoDich()
                        .stream()
                        .filter(item -> {
                            String ma = item.getMaLoaiGD() == null ? "" : item.getMaLoaiGD().toLowerCase();

                            String ten = item.getTenLoaiGD() == null ? "" : item.getTenLoaiGD().toLowerCase();

                            return ma.contains(maLoai) && ten.contains(tenLoai);
                        })
                        .toList();

        danhSach.setAll(ketQua);
        tblLoaiGiaoDich.setItems(danhSach);
    }

    @FXML
    private void onLamMoi() {

        txtMaLoai.clear();
        txtTenLoai.clear();

        tblLoaiGiaoDich.getSelectionModel()
                .clearSelection();

        taiDanhSach();

        System.out.println("Đã làm mới danh sách loại giao dịch.");
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
}