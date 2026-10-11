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

import org.example.appnganhang.controller.service.Iml.ChiNhanhServiceImpl;
import org.example.appnganhang.controller.service.intf.ChiNhanhService;
import org.example.appnganhang.model.entity.ChiNhanh;
import org.example.appnganhang.model.entity.NhanVien;
import org.example.appnganhang.controller.service.Iml.NhanVienServiceImpl;
import org.example.appnganhang.controller.service.intf.NhanVienService;

import java.util.List;
import java.util.Optional;

import static org.example.appnganhang.controller.util.Navigation.moView;

public class NhanVienController {
    @FXML private TextField txtMaNhanVien;
    @FXML private TextField txtHoTen;
    @FXML private TextField txtSoDienThoai;
    @FXML private TextField txtChucVu;
    @FXML private TextField txtTenChiNhanh;
    @FXML private TextField txtTenDangNhap;

    @FXML private Button btnThem;
    @FXML private Button btnSua;
    @FXML private Button btnXoa;
    @FXML private Button btnLamMoi;

    @FXML private TableView<NhanVien> tblNhanVien;
    @FXML private TableColumn<NhanVien, String> colMaNhanVien;
    @FXML private TableColumn<NhanVien, String> colHoTen;
    @FXML private TableColumn<NhanVien, String> colSoDienThoai;

    private final ObservableList<NhanVien> danhSach = FXCollections.observableArrayList();

    private final NhanVienService nhanVienService;
    private final ChiNhanhService chiNhanhService;

    public NhanVienController() {
        this.nhanVienService = new NhanVienServiceImpl();
        this.chiNhanhService = new ChiNhanhServiceImpl();
    }

    @FXML
    private void initialize() {

        cauHinhBang();

        taiDanhSachNhanVien();

        tblNhanVien.setItems(danhSach);

        tblNhanVien.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldValue, newValue) -> {

                    if (newValue != null) {
                        hienThiNhanVien(newValue);
                    }
                });
    }

    private void cauHinhBang() {

        colMaNhanVien.setCellValueFactory(new PropertyValueFactory<>("maNhanVien"));
        colHoTen.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        colSoDienThoai.setCellValueFactory(new PropertyValueFactory<>("soDienThoai"));
    }

    private void taiDanhSachNhanVien() {

        try {
            danhSach.setAll(nhanVienService.getDanhSachNhanVien());

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Không thể tải danh sách nhân viên.");
        }
    }

    private void hienThiNhanVien(NhanVien nhanVien) {

        txtMaNhanVien.setText(nhanVien.getMaNhanVien());
        txtHoTen.setText(nhanVien.getHoTen());
        txtSoDienThoai.setText(nhanVien.getSoDienThoai());
        txtChucVu.setText(nhanVien.getChucVu());
        txtTenDangNhap.setText(nhanVien.getTenDangNhap());

        if (nhanVien.getMaChiNhanh() != null
                && !nhanVien.getMaChiNhanh().isBlank()) {

            Optional<ChiNhanh> chiNhanh =
                    chiNhanhService.timKiemChiNhanhTheoMa(nhanVien.getMaChiNhanh());

            chiNhanh.ifPresent(
                    cn -> txtTenChiNhanh.setText(cn.getTenChiNhanh())
            );
        }
    }

    private NhanVien layNhanVienTuForm(String maChiNhanh) {

        NhanVien nhanVien = new NhanVien();

        nhanVien.setMaNhanVien(txtMaNhanVien.getText().trim());
        nhanVien.setHoTen(txtHoTen.getText().trim());
        nhanVien.setSoDienThoai(txtSoDienThoai.getText().trim());
        nhanVien.setChucVu(txtChucVu.getText().trim());
        nhanVien.setMaChiNhanh(maChiNhanh);
        nhanVien.setTenDangNhap(txtTenDangNhap.getText().trim());

        return nhanVien;
    }

    @FXML
    private void onThem() {
        String tenChiNhanh = txtTenChiNhanh.getText().trim();
        String tenDangNhap = txtTenDangNhap.getText().trim();

        if (tenChiNhanh.isEmpty()) {
            System.out.println("Vui lòng nhập tên chi nhánh.");
            return;
        }

        if (tenDangNhap.isEmpty()) {
            System.out.println("Vui lòng nhập tên đăng nhập.");
            return;
        }

        List<ChiNhanh> danhSachChiNhanh = chiNhanhService.timKiemChiNhanhTheoTen(tenChiNhanh);

        if (danhSachChiNhanh.isEmpty()) {
            System.out.println("Chi nhánh không tồn tại: " + tenChiNhanh);
            return;
        }

        String maChiNhanh = danhSachChiNhanh.get(0).getMaChiNhanh();
        NhanVien nhanVienMoi = layNhanVienTuForm(maChiNhanh);

        if (nhanVienMoi.getMaNhanVien().isEmpty()
                || nhanVienMoi.getHoTen().isEmpty()
                || nhanVienMoi.getSoDienThoai().isEmpty()
                || nhanVienMoi.getChucVu().isEmpty()) {
            System.out.println("Vui lòng nhập đầy đủ thông tin nhân viên.");
            return;
        }

        boolean ketQua = nhanVienService.themNhanVien(nhanVienMoi);

        if (ketQua) {
            danhSach.add(nhanVienMoi);
            lamSachForm();
            tblNhanVien.getSelectionModel().clearSelection();
            System.out.println("Đã thêm nhân viên: " + nhanVienMoi.getMaNhanVien());
        } else {
            System.out.println("Không thể thêm nhân viên.");
        }
    }

    @FXML
    private void onSua() {
        NhanVien selected = tblNhanVien.getSelectionModel().getSelectedItem();

        if (selected == null) {
            System.out.println("Vui lòng chọn nhân viên cần sửa.");
            return;
        }

        String tenChiNhanh = txtTenChiNhanh.getText().trim();
        String tenDangNhap = txtTenDangNhap.getText().trim();

        if (tenChiNhanh.isEmpty()) {
            System.out.println("Vui lòng nhập tên chi nhánh.");
            return;
        }

        if (tenDangNhap.isEmpty()) {
            System.out.println("Vui lòng nhập tên đăng nhập.");
            return;
        }

        List<ChiNhanh> danhSachChiNhanh = chiNhanhService.timKiemChiNhanhTheoTen(tenChiNhanh);

        if (danhSachChiNhanh.isEmpty()) {
            System.out.println("Chi nhánh không tồn tại: " + tenChiNhanh);
            return;
        }

        String maChiNhanh = danhSachChiNhanh.get(0).getMaChiNhanh();
        NhanVien nhanVienCapNhat = layNhanVienTuForm(maChiNhanh);

        if (nhanVienCapNhat.getMaNhanVien().isEmpty()
                || nhanVienCapNhat.getHoTen().isEmpty()
                || nhanVienCapNhat.getSoDienThoai().isEmpty()
                || nhanVienCapNhat.getChucVu().isEmpty()) {
            System.out.println("Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        boolean ketQua = nhanVienService.suaNhanVien(nhanVienCapNhat);

        if (ketQua) {
            int index = danhSach.indexOf(selected);

            if (index >= 0) {
                danhSach.set(index, nhanVienCapNhat);
            }

            tblNhanVien.refresh();
            lamSachForm();
            tblNhanVien.getSelectionModel().clearSelection();

            System.out.println("Đã cập nhật nhân viên: " + nhanVienCapNhat.getMaNhanVien());
        } else {
            System.out.println("Không thể cập nhật nhân viên.");
        }
    }

    @FXML
    private void onXoa() {

        NhanVien selected =
                tblNhanVien.getSelectionModel()
                        .getSelectedItem();

        if (selected == null) {

            System.out.println("Vui lòng chọn nhân viên cần xóa.");

            return;
        }

        String maNhanVien = selected.getMaNhanVien();

        boolean ketQua = nhanVienService.xoaNhanVien(maNhanVien);

        if (ketQua) {

            danhSach.remove(selected);

            lamSachForm();

            tblNhanVien.getSelectionModel()
                    .clearSelection();

            System.out.println("Đã xóa nhân viên: " + maNhanVien);

        } else {

            System.out.println("Không thể xóa nhân viên.");
        }
    }

    @FXML
    private void onTimKiemTheoMa() {
        String maNhanVien = txtMaNhanVien.getText().trim();

        if (maNhanVien.isEmpty()) {
            taiDanhSachNhanVien();
            return;
        }

        Optional<NhanVien> ketQua = nhanVienService.timNhanVien(maNhanVien);

        danhSach.clear();
        ketQua.ifPresent(danhSach::add);
    }

    @FXML
    private void onTimKiemTheoTen() {
        String hoTen = txtHoTen.getText().trim();

        if (hoTen.isEmpty()) {
            taiDanhSachNhanVien();
            return;
        }

        danhSach.setAll(nhanVienService.timTheoHoTen(hoTen));
    }

    @FXML
    private void onTimKiemTheoSDT() {
        String soDienThoai = txtSoDienThoai.getText().trim();

        if (soDienThoai.isEmpty()) {
            taiDanhSachNhanVien();
            return;
        }

        danhSach.setAll(nhanVienService.timTheoSoDienThoai(soDienThoai));
    }

    @FXML
    private void onTimKiemTheoChucVu() {
        String chucVu = txtChucVu.getText().trim();

        if (chucVu.isEmpty()) {
            taiDanhSachNhanVien();
            return;
        }

        danhSach.setAll(nhanVienService.timTheoChucVu(chucVu));
    }

    @FXML
    private void onTimKiemTheoTenChiNhanh() {
        String tenChiNhanh = txtTenChiNhanh.getText().trim();

        if (tenChiNhanh.isEmpty()) {
            taiDanhSachNhanVien();
            return;
        }

        danhSach.setAll(nhanVienService.timTheoTenChiNhanh(tenChiNhanh));
    }

    @FXML
    private void onTimKiemTheoTenDangNhap() {
        String tenDangNhap = txtTenDangNhap.getText().trim();

        if (tenDangNhap.isEmpty()) {
            taiDanhSachNhanVien();
            return;
        }

        danhSach.setAll(nhanVienService.timTheoTenDangNhap(tenDangNhap));
    }

    @FXML
    private void onTimKiem() {
        String maNhanVien = txtMaNhanVien.getText().trim();
        String hoTen = txtHoTen.getText().trim();
        String soDienThoai = txtSoDienThoai.getText().trim();
        String chucVu = txtChucVu.getText().trim();
        String tenChiNhanh = txtTenChiNhanh.getText().trim();
        String tenDangNhap = txtTenDangNhap.getText().trim();

        List<NhanVien> ketQua = nhanVienService.timKiemNhanVien(
                maNhanVien,
                hoTen,
                soDienThoai,
                chucVu,
                tenChiNhanh,
                tenDangNhap
        );

        danhSach.setAll(ketQua);
    }

    @FXML
    private void onLamMoi() {

        lamSachForm();

        tblNhanVien.getSelectionModel()
                .clearSelection();

        taiDanhSachNhanVien();

        System.out.println("Đã làm mới.");
    }

    private void lamSachForm() {

        txtMaNhanVien.clear();
        txtHoTen.clear();
        txtSoDienThoai.clear();
        txtChucVu.clear();
        txtTenChiNhanh.clear();
        txtTenDangNhap.clear();
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
        moView("branch-view.fxml", event);
    }

    @FXML
    private void onNhanVien(ActionEvent event) {
        // Đang ở trang Nhân viên
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