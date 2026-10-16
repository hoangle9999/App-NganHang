package org.example.appnganhang.controller.nhanvien;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import org.example.appnganhang.model.entity.ChiNhanh;
import org.example.appnganhang.controller.service.Iml.ChiNhanhServiceImpl;
import org.example.appnganhang.controller.service.intf.ChiNhanhService;

import java.util.List;
import java.util.Optional;

import static org.example.appnganhang.controller.util.Navigation.moView;

public class ChiNhanhController {

    @FXML private TextField txtMaChiNhanh;
    @FXML private TextField txtTenChiNhanh;
    @FXML private TextField txtDiaChi;

    @FXML private Button btnThem;
    @FXML private Button btnSua;
    @FXML private Button btnXoa;
    @FXML private Button btnLamMoi;
    @FXML private Button btnTimKiem;
    @FXML private Button btnClear;

    @FXML private TableView<ChiNhanh> tblChiNhanh;
    @FXML private TableColumn<ChiNhanh, String> colMaChiNhanh;
    @FXML private TableColumn<ChiNhanh, String> colTenChiNhanh;
    @FXML private TableColumn<ChiNhanh, String> colDiaChi;

    private final ObservableList<ChiNhanh> danhSach = FXCollections.observableArrayList();

    private final ChiNhanhService chiNhanhService;

    public ChiNhanhController() {
        this.chiNhanhService = new ChiNhanhServiceImpl();
    }

    @FXML
    private void initialize() {
        cauHinhBang();
        taiDanhSachChiNhanh();

        tblChiNhanh.setItems(danhSach);

        tblChiNhanh.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldValue, newValue) -> {
                    if (newValue != null) {
                        hienThiChiNhanh(newValue);
                    }
                });
    }

    private void cauHinhBang() {
        colMaChiNhanh.setCellValueFactory(new PropertyValueFactory<>("maChiNhanh"));
        colTenChiNhanh.setCellValueFactory(new PropertyValueFactory<>("tenChiNhanh"));
        colDiaChi.setCellValueFactory(new PropertyValueFactory<>("diaChi"));
    }

    private void taiDanhSachChiNhanh() {

        try {
            danhSach.setAll(chiNhanhService.getDanhSachChiNhanh());

        } catch (Exception e) {
            e.printStackTrace();

            System.out.println("Không thể tải danh sách chi nhánh.");
        }
    }

    private void hienThiChiNhanh(ChiNhanh chiNhanh) {
        txtMaChiNhanh.setText(chiNhanh.getMaChiNhanh());
        txtTenChiNhanh.setText(chiNhanh.getTenChiNhanh());
        txtDiaChi.setText(chiNhanh.getDiaChi());
        txtMaChiNhanh.setDisable(true);
    }

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

        ChiNhanh chiNhanhMoi = new ChiNhanh(maChiNhanh, tenChiNhanh, diaChi);

        boolean ketQua = chiNhanhService.themChiNhanh(chiNhanhMoi);

        if (ketQua) {

            danhSach.add(chiNhanhMoi);

            lamSachForm();

            tblChiNhanh.getSelectionModel().clearSelection();

            System.out.println("Đã thêm chi nhánh: " + maChiNhanh);

        } else {

            System.out.println("Không thể thêm chi nhánh.");
        }
    }

    @FXML
    private void onSua() {

        ChiNhanh selected = tblChiNhanh.getSelectionModel().getSelectedItem();

        if (selected == null) {

            System.out.println("Vui lòng chọn chi nhánh cần sửa.");
            return;
        }

        String maChiNhanh = txtMaChiNhanh.getText().trim();
        String tenChiNhanh = txtTenChiNhanh.getText().trim();
        String diaChi = txtDiaChi.getText().trim();

        if (maChiNhanh.isEmpty()
                || tenChiNhanh.isEmpty()
                || diaChi.isEmpty()) {

            System.out.println("Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        ChiNhanh chiNhanhCapNhat = new ChiNhanh(maChiNhanh, tenChiNhanh, diaChi);

        boolean ketQua = chiNhanhService.suaChiNhanh(chiNhanhCapNhat);

        if (ketQua) {

            int index = danhSach.indexOf(selected);

            if (index >= 0) {
                danhSach.set(index, chiNhanhCapNhat);
            }

            tblChiNhanh.refresh();
            lamSachForm();
            tblChiNhanh.getSelectionModel().clearSelection();

            System.out.println("Đã cập nhật chi nhánh: " + maChiNhanh);

        } else {
            System.out.println("Không thể cập nhật chi nhánh.");
        }
    }

    @FXML
    private void onXoa() {

        ChiNhanh selected = tblChiNhanh.getSelectionModel().getSelectedItem();

        if (selected == null) {

            System.out.println("Vui lòng chọn chi nhánh cần xóa.");
            return;
        }

        String maChiNhanh = selected.getMaChiNhanh();

        boolean ketQua = chiNhanhService.xoaChiNhanh(maChiNhanh);

        if (ketQua) {

            danhSach.remove(selected);
            lamSachForm();

            tblChiNhanh.getSelectionModel().clearSelection();

            System.out.println("Đã xóa chi nhánh: " + maChiNhanh);

        } else {

            System.out.println("Không thể xóa chi nhánh.");
        }
    }

    @FXML
    private void onLamMoi() {

        lamSachForm();

        tblChiNhanh.getSelectionModel().clearSelection();
        taiDanhSachChiNhanh();

        System.out.println("Đã làm mới danh sách chi nhánh.");
    }

    @FXML
    private void onClear() {
        lamSachForm();
        tblChiNhanh.getSelectionModel().clearSelection();
    }

    @FXML
     private void onTimKiemTheoMa(){
        String maChiNhanh = txtMaChiNhanh.getText();

        Optional<ChiNhanh> ketQua = chiNhanhService.timKiemChiNhanhTheoMa(maChiNhanh);
        danhSach.clear();

        ketQua.ifPresent(danhSach::add);
    }

    @FXML
    private void onTimKiemTheoTen(){
        String tenChiNhanh = txtTenChiNhanh.getText();

        List<ChiNhanh> ketQua = chiNhanhService.timKiemChiNhanhTheoTen(tenChiNhanh);
        danhSach.setAll(ketQua);
    }

    @FXML
    private void onTimKiemTheoDiaChi(){
        String diaChi = txtDiaChi.getText();

        List<ChiNhanh> ketQua = chiNhanhService.timKiemChiNhanhTheoDiaChi(diaChi);
        danhSach.setAll(ketQua);
    }

    @FXML
    private void onTimKiem() {
        String maChiNhanh = txtMaChiNhanh.getText();
        String tenChiNhanh = txtTenChiNhanh.getText();
        String diaChi = txtDiaChi.getText();

        List<ChiNhanh> ketQua = chiNhanhService.timKiemChiNhanh(
                maChiNhanh,
                tenChiNhanh,
                diaChi
        );

        danhSach.setAll(ketQua);
    }

    private void lamSachForm() {

        txtMaChiNhanh.clear();
        txtMaChiNhanh.setDisable(false);
        txtTenChiNhanh.clear();
        txtDiaChi.clear();
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
        moView("transaction-type-view.fxml", event);
    }

    @FXML
    private void onChiNhanh(ActionEvent event) {
        // Đang ở trang chi nhánh
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
