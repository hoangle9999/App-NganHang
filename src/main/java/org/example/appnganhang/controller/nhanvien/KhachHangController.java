package org.example.appnganhang.controller.nhanvien;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import org.example.appnganhang.controller.service.Iml.ChiNhanhServiceImpl;
import org.example.appnganhang.model.entity.ChiNhanh;
import org.example.appnganhang.model.entity.KhachHang;
import org.example.appnganhang.controller.service.Iml.KhachHangServiceImpl;
import org.example.appnganhang.controller.service.intf.KhachHangService;

import java.awt.*;
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
        SUA
    }

    private MODE mode = MODE.XEM;
    private KhachHang khachHangDangSua;
    private boolean dangNhapDieuKienTimKiem = false;

    private final KhachHangService khachHangService;
    private final ChiNhanhServiceImpl chiNhanhService;
    private final ObservableList<KhachHang> danhSach = FXCollections.observableArrayList();

    public KhachHangController() {
        this.khachHangService = new KhachHangServiceImpl();
        this.chiNhanhService = new ChiNhanhServiceImpl();
    }

    @FXML
    private void initialize() {
        cauHinhBang();
        setFocus();
        selectRow();

        khoaForm(true);
        tblKhachHang.setItems(danhSach);
        taiDanhSachKhachHang();
    }

    private void khoaForm(boolean khoa) {
        txtHoTen.setEditable(!khoa);
        txtCCCD.setEditable(!khoa);
        txtSoDienThoai.setEditable(!khoa);
        txtEmail.setEditable(!khoa);
        txtDiaChi.setEditable(!khoa);
        txtTenDangNhap.setEditable(!khoa);
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

    private void moCuaSoThemKhachHang() {
        Dialog<ButtonType> dialog = new Dialog<>();

        dialog.setTitle("Thêm khách hàng");
        dialog.setHeaderText("Nhập thông tin khách hàng mới");

        ButtonType nutLuu = new ButtonType("Tạo khách hàng", ButtonBar.ButtonData.OK_DONE);

        dialog.getDialogPane().getButtonTypes().addAll(
                nutLuu,
                ButtonType.CANCEL
        );

        TextField txtHoTenMoi = new TextField();
        TextField txtCCCDMoi = new TextField();
        TextField txtSoDienThoaiMoi = new TextField();
        TextField txtEmailMoi = new TextField();
        TextField txtDiaChiMoi = new TextField();
        TextField txtTenDangNhapMoi = new TextField();
        PasswordField txtMatKhauMoi = new PasswordField();
        ComboBox<ChiNhanh> cboChiNhanh = new ComboBox<>();
        cboChiNhanh.setPromptText("Chọn chi nhánh");
        cboChiNhanh.setMaxWidth(Double.MAX_VALUE);

        setFocusCuaSoThemKhachHang(
                txtHoTenMoi,
                txtCCCDMoi,
                txtSoDienThoaiMoi,
                txtEmailMoi,
                txtDiaChiMoi,
                txtTenDangNhapMoi,
                txtMatKhauMoi,
                cboChiNhanh
        );

        try {
            cboChiNhanh.setItems(
                    FXCollections.observableArrayList(chiNhanhService.getDanhSachChiNhanh())
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        cboChiNhanh.setConverter(new StringConverter<>() {
            @Override
            public String toString(ChiNhanh chiNhanh) {
                return chiNhanh == null ? "" : chiNhanh.getTenChiNhanh();
            }

            @Override
            public ChiNhanh fromString(String tenChiNhanh) {
                return null;
            }
        });

        Label lblThongBao = new Label();
        lblThongBao.setWrapText(true);

        txtHoTenMoi.setPromptText("Nhập họ và tên");
        txtCCCDMoi.setPromptText("Nhập CCCD");
        txtSoDienThoaiMoi.setPromptText("Nhập số điện thoại");
        txtEmailMoi.setPromptText("Nhập email");
        txtDiaChiMoi.setPromptText("Nhập địa chỉ");
        txtTenDangNhapMoi.setPromptText("Nhập tên đăng nhập");
        txtMatKhauMoi.setPromptText("Nhập mật khẩu");

        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(10);
        form.setPadding(new Insets(15));

        form.addRow(0, new Label("Họ và tên:"), txtHoTenMoi);
        form.addRow(1, new Label("CCCD:"), txtCCCDMoi);
        form.addRow(2, new Label("Số điện thoại:"), txtSoDienThoaiMoi);
        form.addRow(3, new Label("Email:"), txtEmailMoi);
        form.addRow(4, new Label("Địa chỉ:"), txtDiaChiMoi);
        form.addRow(5, new Label("Tên đăng nhập:"), txtTenDangNhapMoi);
        form.addRow(6, new Label("Mật khẩu:"), txtMatKhauMoi);
        form.addRow(7, new Label("Chi nhánh:"), cboChiNhanh);
        form.add(lblThongBao, 0, 8, 2, 1);

        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().setPrefWidth(520);

        Button btnTao = (Button) dialog.getDialogPane()
                .lookupButton(nutLuu);

        btnTao.addEventFilter(ActionEvent.ACTION, event -> {
            String hoTen = txtHoTenMoi.getText().trim();
            String cccd = txtCCCDMoi.getText().trim();
            String soDienThoai = txtSoDienThoaiMoi.getText().trim();
            String email = txtEmailMoi.getText().trim();
            String diaChi = txtDiaChiMoi.getText().trim();
            String tenDangNhap = txtTenDangNhapMoi.getText().trim();
            String matKhau = txtMatKhauMoi.getText();
            ChiNhanh chiNhanhDuocChon = cboChiNhanh.getValue();
            String maChiNhanh = chiNhanhDuocChon == null ? null : chiNhanhDuocChon.getMaChiNhanh();

            if (hoTen.isEmpty()
                    || cccd.isEmpty()
                    || soDienThoai.isEmpty()
                    || tenDangNhap.isEmpty()
                    || matKhau.trim().isEmpty()
                    || maChiNhanh == null
                    || maChiNhanh.isBlank()) {

                lblThongBao.setText(
                        "Vui lòng nhập họ tên, CCCD, số điện thoại, "
                                + "tên đăng nhập, mật khẩu và mã chi nhánh."
                );

                event.consume();
                return;
            }

            boolean ketQua;

            try {
                ketQua = taoKhachHangTuNhanVien(
                        hoTen,
                        cccd,
                        soDienThoai,
                        email,
                        diaChi,
                        tenDangNhap,
                        matKhau,
                        maChiNhanh
                );
            } catch (Exception e) {
                e.printStackTrace();
                ketQua = false;
            }

            if (!ketQua) {
                lblThongBao.setText(
                        "Tạo khách hàng thất bại. "
                                + "Kiểm tra dữ liệu và thông tin chi nhánh."
                );

                event.consume();
                return;
            }

            taiDanhSachKhachHang();
            lblThongBao.setText("Tạo khách hàng thành công.");
        });

        dialog.showAndWait();
    }

    private void selectRow(){
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

    private void setFocusCuaSoThemKhachHang(
            TextField txtHoTenMoi,
            TextField txtCCCDMoi,
            TextField txtSoDienThoaiMoi,
            TextField txtEmailMoi,
            TextField txtDiaChiMoi,
            TextField txtTenDangNhapMoi,
            PasswordField txtMatKhauMoi,
            ComboBox<ChiNhanh> cboChiNhanh
    ) {
        txtHoTenMoi.setOnAction(event -> txtCCCDMoi.requestFocus());
        txtCCCDMoi.setOnAction(event -> txtSoDienThoaiMoi.requestFocus());
        txtSoDienThoaiMoi.setOnAction(event -> txtEmailMoi.requestFocus());
        txtEmailMoi.setOnAction(event -> txtDiaChiMoi.requestFocus());
        txtDiaChiMoi.setOnAction(event -> txtTenDangNhapMoi.requestFocus());
        txtTenDangNhapMoi.setOnAction(event -> txtMatKhauMoi.requestFocus());
        txtMatKhauMoi.setOnAction(event -> cboChiNhanh.requestFocus());
    }

    private boolean taoKhachHangTuNhanVien(
            String hoTen,
            String cccd,
            String soDienThoai,
            String email,
            String diaChi,
            String tenDangNhap,
            String matKhau,
            String maChiNhanh
    ) {
        return khachHangService.taoKhachHangTuNhanVien(
                hoTen,
                cccd,
                soDienThoai,
                email,
                diaChi,
                tenDangNhap,
                matKhau,
                maChiNhanh
        );
    }

    @FXML
    private void onTimKiem() {
        if (!dangNhapDieuKienTimKiem) {
            // Lần nhấn thứ nhất: mở các ô nhập điều kiện
            dangNhapDieuKienTimKiem = true;

            tblKhachHang.getSelectionModel().clearSelection();
            lamSachForm();
            khoaForm(false);

            btnTimKiem.setText("Thực hiện");
            txtHoTen.requestFocus();

            return;
        }

        // Lần nhấn thứ hai: thực hiện tìm kiếm
        String hoTen = txtHoTen.getText().trim();
        String cccd = txtCCCD.getText().trim();
        String soDienThoai = txtSoDienThoai.getText().trim();
        String email = txtEmail.getText().trim();
        String diaChi = txtDiaChi.getText().trim();
        String tenDangNhap = txtTenDangNhap.getText().trim();

        try {
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

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Tìm kiếm khách hàng thất bại.");
        } finally {
            dangNhapDieuKienTimKiem = false;
            btnTimKiem.setText("Tìm kiếm");
            khoaForm(true);
        }
    }

    @FXML
    private void onThem() {
        moCuaSoThemKhachHang();
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
        khoaForm(false);

        txtTenDangNhap.setEditable(false);

        capNhatTrangThaiNut();

        txtHoTen.requestFocus();

        System.out.println("Đang sửa khách hàng: " + selected.getMaKhachHang());
    }

    @FXML
    private void onXoa() {
        KhachHang selected = tblKhachHang
                .getSelectionModel()
                .getSelectedItem();

        String maKhachHang = selected.getMaKhachHang();

        Alert xacNhan = new Alert(Alert.AlertType.CONFIRMATION);
        xacNhan.setTitle("Xác nhận xóa");
        xacNhan.setHeaderText("Xóa khách hàng " + maKhachHang + "?");
        xacNhan.setContentText(
                "Thao tác này không thể thực hiện nếu khách hàng "
                        + "đang có tài khoản ngân hàng hoặc dữ liệu liên quan."
        );

        Optional<ButtonType> luaChon = xacNhan.showAndWait();

        if (luaChon.isEmpty()
                || luaChon.get() != ButtonType.OK) {
            return;
        }

        try {
            boolean ketQua = khachHangService.xoaKhachHang(maKhachHang);

            if (ketQua) {
                danhSach.remove(selected);
                lamSachForm();
                tblKhachHang.getSelectionModel().clearSelection();

                hienThongBao(
                        Alert.AlertType.INFORMATION,
                        "Thành công",
                        "Đã xóa khách hàng " + maKhachHang + "."
                );
            } else {
                hienThongBao(
                        Alert.AlertType.WARNING,
                        "Không thể xóa",
                        "Không tìm thấy khách hàng hoặc xóa không thành công."
                );
            }

        } catch (RuntimeException e) {
            hienThongBao(
                    Alert.AlertType.ERROR,
                    "Không thể xóa khách hàng",
                    e.getMessage()
            );
        }
    }

    @FXML
    private void onLuu() {
        if (mode != MODE.SUA || khachHangDangSua == null) {
            System.out.println("Vui lòng chọn khách hàng và nhấn Sửa trước.");
            return;
        }

        String hoTen = txtHoTen.getText().trim();
        String cccd = txtCCCD.getText().trim();
        String soDienThoai = txtSoDienThoai.getText().trim();
        String email = txtEmail.getText().trim();
        String diaChi = txtDiaChi.getText().trim();

        System.out.println("Đã nhận thông tin khách hàng: " + email);

        if (hoTen.isEmpty()
                || cccd.isEmpty()
                || soDienThoai.isEmpty()
                || email.isEmpty()
                || diaChi.isEmpty()) {
            System.out.println("Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        String maKhachHang = khachHangDangSua.getMaKhachHang();
        String tenDangNhap = khachHangDangSua.getTenDangNhap();

        KhachHang khachHang = new KhachHang(
                maKhachHang,
                hoTen,
                cccd,
                soDienThoai,
                diaChi,
                email,
                tenDangNhap
        );

        System.out.println("Đã tạo khách hàng mới: " + email);

        boolean ketQua;

        try {
            ketQua = khachHangService.capNhatKhachHang(khachHang);
            System.out.println("Đã cập khách hàng: " + khachHang.getEmail());
        } catch (Exception e) {
            e.printStackTrace();
            ketQua = false;
        }

        if (ketQua) {
            int index = danhSach.indexOf(khachHangDangSua);

            if (index >= 0) {
                danhSach.set(index, khachHang);
            }

            System.out.println("Đã cập nhật khách hàng: " + maKhachHang);

            ketThucChinhSua();
        } else {
            System.out.println("Cập nhật khách hàng thất bại.");
        }
    }

    @FXML
    private void onLamMoi() {

        dangNhapDieuKienTimKiem = false;
        btnTimKiem.setText("Tìm kiếm");
        khoaForm(true);

        mode = MODE.XEM;
        khachHangDangSua = null;

        lamSachForm();

        tblKhachHang.getSelectionModel().clearSelection();

        taiDanhSachKhachHang();

        capNhatTrangThaiNut();

        System.out.println("Đã làm mới danh sách khách hàng.");
    }

    private void ketThucChinhSua() {
        mode = MODE.XEM;
        khachHangDangSua = null;
        dangNhapDieuKienTimKiem = false;

        lamSachForm();
        khoaForm(true);

        btnTimKiem.setText("Tìm kiếm");

        tblKhachHang.getSelectionModel().clearSelection();

        capNhatTrangThaiNut();
    }

    private void hienThiLenForm(KhachHang khachHang) {

        txtHoTen.setText(khachHang.getHoTen());
        txtCCCD.setText(khachHang.getSoCCCD());
        txtSoDienThoai.setText(khachHang.getSoDienThoai());
        txtEmail.setText(khachHang.getEmail());
        txtDiaChi.setText(khachHang.getDiaChi());
        txtTenDangNhap.setText(khachHang.getTenDangNhap());
    }

    private void hienThongBao(Alert.AlertType loai, String tieuDe, String noiDung) {
        Alert alert = new Alert(loai);
        alert.setTitle(tieuDe);
        alert.setHeaderText(null);
        alert.setContentText(noiDung);
        alert.showAndWait();
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