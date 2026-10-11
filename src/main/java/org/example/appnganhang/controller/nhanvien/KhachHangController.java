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
import org.example.appnganhang.model.entity.KhachHang;
import org.example.appnganhang.controller.service.Iml.KhachHangServiceImpl;
import org.example.appnganhang.controller.service.intf.KhachHangService;

import java.util.List;
import java.util.Optional;

import static org.example.appnganhang.controller.util.Navigation.moView;

public class KhachHangController {
    @FXML private TextField txtHoTen;
    @FXML private TextField txtCCCD;
    @FXML private TextField txtSoDienThoai;
    @FXML private TextField txtEmail;
    @FXML private TextField txtDiaChi;
    @FXML private TextField txtTenDangNhap;

    @FXML private Button btnTimKiem;
    @FXML private Button btnLamMoi;
    @FXML private Button btnThem;
    @FXML private Button btnSua;
    @FXML private Button btnXoa;
    @FXML private Button btnLuu;

    @FXML private TableView<KhachHang> tblKhachHang;
    @FXML private TableColumn<KhachHang, String> colMaKhachHang;
    @FXML private TableColumn<KhachHang, String> colHoTen;
    @FXML private TableColumn<KhachHang, String> colCCCD;
    @FXML private TableColumn<KhachHang, String> colSoDienThoai;
    @FXML private TableColumn<KhachHang, String> colEmail;
    @FXML private TableColumn<KhachHang, String> colDiaChi;
    @FXML private TableColumn<KhachHang, String> colTenDangNhap;

    private enum MODE {
        XEM,
        THEM,
        SUA
    }

    private MODE mode = MODE.XEM;
    private KhachHang khachHangDangSua;

    private final KhachHangService khachHangService;
    private final ObservableList<KhachHang> danhSach = FXCollections.observableArrayList();

    public KhachHangController() {
        this.khachHangService = new KhachHangServiceImpl();
    }

    @FXML
    private void initialize() {
        cauHinhBang();
        setFocus();

        tblKhachHang.setItems(danhSach);
        taiDanhSachKhachHang();

        tblKhachHang.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldValue, newValue) -> {

                    // Chỉ hiển thị thông tin khi đang ở chế độ xem
                    if (mode == MODE.XEM
                            && newValue != null) {
                        hienThiLenForm(newValue);
                    }

                    capNhatTrangThaiNut();
                });

        capNhatTrangThaiNut();
    }

    private void capNhatTrangThaiNut() {
        boolean dangChinhSua = mode != MODE.XEM;

        boolean daChonDong =
                tblKhachHang.getSelectionModel()
                        .getSelectedItem() != null;

        btnThem.setDisable(dangChinhSua);
        btnSua.setDisable(dangChinhSua || !daChonDong);
        btnXoa.setDisable(dangChinhSua || !daChonDong);
        btnLuu.setDisable(!dangChinhSua);
    }

    private void taiDanhSachKhachHang() {

        try {

            List<KhachHang> danhSachKhachHang = khachHangService.getDanhSachKhachHang();

            danhSach.setAll(danhSachKhachHang);

        } catch (Exception e) {

            e.printStackTrace();

            System.out.println("Không thể tải danh sách khách hàng.");
        }
    }


    private void cauHinhBang() {

        colMaKhachHang.setCellValueFactory(new PropertyValueFactory<>("maKhachHang"));
        colHoTen.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        colCCCD.setCellValueFactory(new PropertyValueFactory<>("soCCCD"));
        colSoDienThoai.setCellValueFactory(new PropertyValueFactory<>("soDienThoai"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colDiaChi.setCellValueFactory(new PropertyValueFactory<>("diaChi"));
        colTenDangNhap.setCellValueFactory(new PropertyValueFactory<>("tenDangNhap"));
    }

    private void setFocus(){
        txtHoTen.setOnAction(event -> txtTenDangNhap.requestFocus());
        txtTenDangNhap.setOnAction(event -> txtCCCD.requestFocus());
        txtCCCD.setOnAction(event -> txtSoDienThoai.requestFocus());
        txtSoDienThoai.setOnAction(event -> txtEmail.requestFocus());
        txtEmail.setOnAction(event -> txtDiaChi.requestFocus());
    }

    @FXML
    private void onTimKiem() {

        String hoTen = txtHoTen.getText().trim();
        String cccd = txtCCCD.getText().trim();
        String soDienThoai = txtSoDienThoai.getText().trim();
        String email = txtEmail.getText().trim();
        String diaChi = txtDiaChi.getText().trim();
        String tenDangNhap = txtTenDangNhap.getText().trim();

        List<KhachHang> ketQua =
                khachHangService.timKiemKhachHang(
                        "",
                        hoTen,
                        cccd,
                        soDienThoai,
                        email,
                        diaChi,
                        tenDangNhap
                );

        danhSach.setAll(ketQua);

        tblKhachHang.getSelectionModel().clearSelection();
        lamSachForm();

        System.out.println("Tìm thấy " + ketQua.size() + " khách hàng.");
    }

    @FXML
    private void onThem() {

        mode = MODE.THEM;
        khachHangDangSua = null;

        tblKhachHang.getSelectionModel().clearSelection();
        lamSachForm();

        capNhatTrangThaiNut();

        txtHoTen.requestFocus();

        System.out.println("Đang thêm khách hàng mới.");
    }

    @FXML
    private void onSua() {

        KhachHang selected = tblKhachHang
                .getSelectionModel()
                .getSelectedItem();

        if (selected == null) {
            System.out.println("Vui lòng chọn khách hàng cần sửa.");
            return;
        }

        khachHangDangSua = selected;
        mode = MODE.SUA;

        hienThiLenForm(selected);

        capNhatTrangThaiNut();

        txtHoTen.requestFocus();

        System.out.println("Đang sửa khách hàng: " + selected.getMaKhachHang());
    }

    @FXML
    private void onXoa() {

        // Lấy khách hàng được chọn trong bảng
        KhachHang selected = tblKhachHang
                .getSelectionModel()
                .getSelectedItem();

        if (selected == null) {
            System.out.println("Vui lòng chọn khách hàng cần xóa.");
            return;
        }

        String maKhachHang = selected.getMaKhachHang();
        boolean ketQua = khachHangService.xoaKhachHang(maKhachHang);

        if (ketQua) {

            danhSach.remove(selected);
            lamSachForm();
            tblKhachHang.getSelectionModel().clearSelection();

            System.out.println("Đã xóa khách hàng: " + maKhachHang);

        } else {
            System.out.println("Xóa khách hàng thất bại.");
        }
    }

    @FXML
    private void onLuu() {

        if (mode == MODE.XEM) {
            System.out.println("Vui lòng nhấn Thêm hoặc Sửa trước.");
            return;
        }

        String hoTen = txtHoTen.getText().trim();
        String cccd = txtCCCD.getText().trim();
        String soDienThoai = txtSoDienThoai.getText().trim();
        String email = txtEmail.getText().trim();
        String diaChi = txtDiaChi.getText().trim();
        String tenDangNhap = txtTenDangNhap.getText().trim();

        if (hoTen.isEmpty()
                || cccd.isEmpty()
                || soDienThoai.isEmpty()
                || email.isEmpty()
                || diaChi.isEmpty()
                || tenDangNhap.isEmpty()) {

            System.out.println("Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        boolean ketQua;

        if (mode == MODE.THEM) {

            String maKhachHang =
                    khachHangService.taoMaKhachHang();

            KhachHang khachHang = new KhachHang(
                    maKhachHang,
                    hoTen,
                    cccd,
                    soDienThoai,
                    email,
                    diaChi,
                    tenDangNhap
            );

            ketQua = khachHangService.themKhachHang(khachHang);

            if (ketQua) {
                danhSach.add(khachHang);

                System.out.println(
                        "Đã thêm khách hàng: " + maKhachHang
                );
            }

        } else {

            String maKhachHang = khachHangDangSua.getMaKhachHang();

            KhachHang khachHang = new KhachHang(
                    maKhachHang,
                    hoTen,
                    cccd,
                    soDienThoai,
                    email,
                    diaChi,
                    tenDangNhap
            );

            ketQua = khachHangService.capNhatKhachHang(khachHang);

            if (ketQua) {
                int index = danhSach.indexOf(khachHangDangSua);

                if (index >= 0) {
                    danhSach.set(index, khachHang);
                }

                System.out.println("Đã cập nhật khách hàng: " + maKhachHang);
            }
        }

        if (ketQua) {
            ketThucChinhSua();
        } else {
            System.out.println("Lưu khách hàng thất bại.");
        }
    }

    private void ketThucChinhSua() {

        mode = MODE.XEM;
        khachHangDangSua = null;

        lamSachForm();

        tblKhachHang.getSelectionModel().clearSelection();

        capNhatTrangThaiNut();
    }


    @FXML
    private void onLamMoi() {

        mode = MODE.XEM;
        khachHangDangSua = null;

        lamSachForm();

        tblKhachHang.getSelectionModel().clearSelection();

        taiDanhSachKhachHang();

        capNhatTrangThaiNut();

        System.out.println("Đã làm mới danh sách khách hàng.");
    }

    private void hienThiLenForm(KhachHang khachHang) {

        txtHoTen.setText(khachHang.getHoTen());
        txtCCCD.setText(khachHang.getSoCCCD());
        txtSoDienThoai.setText(khachHang.getSoDienThoai());
        txtEmail.setText(khachHang.getEmail());
        txtDiaChi.setText(khachHang.getDiaChi());
        txtTenDangNhap.setText(khachHang.getTenDangNhap());
    }

    private void lamSachForm() {

        if (txtHoTen != null) {
            txtHoTen.clear();
        }

        if (txtCCCD != null) {
            txtCCCD.clear();
        }

        if (txtSoDienThoai != null) {
            txtSoDienThoai.clear();
        }

        if (txtEmail != null) {
            txtEmail.clear();
        }

        if (txtDiaChi != null) {
            txtDiaChi.clear();
        }

        if (txtTenDangNhap != null) {
            txtTenDangNhap.clear();
        }
    }

    // =========================
    // ĐIỀU HƯỚNG
    // =========================
    @FXML
    private void onTrangChu(ActionEvent event){moView("dashboard-view.fxml", event);}

    @FXML
    private void onKhachHang(ActionEvent event) {
        // Đang ở trang Khách hàng
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
}