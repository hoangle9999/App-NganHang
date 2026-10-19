package org.example.appnganhang.controller.cskh;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import org.example.appnganhang.model.entity.KhachHang;
import org.example.appnganhang.controller.service.Impl.CSKHCustomerServiceImpl;
import org.example.appnganhang.controller.service.intf.CSKHCustomerService;
import org.example.appnganhang.model.entity.CSKHCustomerAccountItem;

import java.text.DecimalFormat;
import java.util.stream.Collectors;

import static org.example.appnganhang.controller.util.Navigation.moView;

public class CSKHCustomerController {

    // ====== Header ======
    @FXML
    private Label lblStaffName;

    @FXML
    private Label lblTongKhachHang;

    @FXML
    private TextField txtTimKiem;

    // ====== Table ======
    @FXML
    private TableView<KhachHang> tblKhachHang;

    @FXML
    private TableColumn<KhachHang, String> colMaKH;

    @FXML
    private TableColumn<KhachHang, String> colHoTen;

    @FXML
    private TableColumn<KhachHang, String> colCCCD;

    @FXML
    private TableColumn<KhachHang, String> colSDT;

    @FXML
    private TableColumn<KhachHang, String> colEmail;

    @FXML
    private TableColumn<KhachHang, String> colDiaChi;

    @FXML
    private TableColumn<KhachHang, String> colTenDangNhap;

    @FXML
    private TableColumn<KhachHang, Void> colThaoTac;

    // ====== Modal Thêm KH ======
    @FXML
    private StackPane modalThemKhachHang;
    @FXML
    private TextField txtThemHoTen;

    @FXML
    private TextField txtThemSoCCCD;

    @FXML
    private TextField txtThemSoDienThoai;

    @FXML
    private TextField txtThemEmail;

    @FXML
    private TextField txtThemDiaChi;

    @FXML
    private TextField txtThemTenDangNhap;

    @FXML
    private PasswordField txtThemMatKhau;

    @FXML
    private Label lblThemError;

    // ====== Modal Sửa KH ======
    @FXML
    private StackPane modalSuaKhachHang;

    @FXML
    private TextField txtSuaMaKH;

    @FXML
    private TextField txtSuaHoTen;

    @FXML
    private TextField txtSuaSoCCCD;

    @FXML
    private TextField txtSuaSoDienThoai;

    @FXML
    private TextField txtSuaEmail;

    @FXML
    private TextField txtSuaDiaChi;

    @FXML
    private TextField txtSuaTenDangNhap;

    @FXML
    private Label lblSuaError;

    // ====== Modal Chi tiết KH ======
    @FXML
    private StackPane modalChiTietKhachHang;

    @FXML
    private Label lblDetailMaKHBadge;

    @FXML
    private Label lblDetailHoTen;

    @FXML
    private Label lblDetailCCCD;

    @FXML
    private Label lblDetailSDT;

    @FXML
    private Label lblDetailEmail;

    @FXML
    private Label lblDetailTenDangNhap;

    @FXML
    private Label lblDetailDiaChi;

    @FXML
    private TableView<DetailAccountItem> tblDetailTaiKhoan;

    @FXML
    private TableColumn<DetailAccountItem, String> colDetailSoTK;

    @FXML
    private TableColumn<DetailAccountItem, String> colDetailSoDu;

    @FXML
    private TableColumn<DetailAccountItem, String> colDetailTrangThai;

    @FXML
    private TableColumn<DetailAccountItem, String> colDetailChiNhanh;

    // ====== Data ======
    private ObservableList<KhachHang> danhSachKH = FXCollections.observableArrayList();
    private KhachHang khachHangDangChon;

    private final CSKHCustomerService customerService = new CSKHCustomerServiceImpl();
    private final DecimalFormat currencyFormat = new DecimalFormat("#,### VNĐ");

    // ====== Inner class for detail account table ======
    public static class DetailAccountItem {
        private final String soTaiKhoan;
        private final String soDu;
        private final String trangThai;
        private final String chiNhanh;

        public DetailAccountItem(String soTaiKhoan, String soDu, String trangThai, String chiNhanh) {
            this.soTaiKhoan = soTaiKhoan;
            this.soDu = soDu;
            this.trangThai = trangThai;
            this.chiNhanh = chiNhanh;
        }

        public String getSoTaiKhoan() {
            return soTaiKhoan;
        }

        public String getSoDu() {
            return soDu;
        }

        public String getTrangThai() {
            return trangThai;
        }

        public String getChiNhanh() {
            return chiNhanh;
        }
    }

    // ======================== INITIALIZE ========================

    @FXML
    public void initialize() {
        // Setup main table columns
        colMaKH.setCellValueFactory(new PropertyValueFactory<>("maKhachHang"));
        colHoTen.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        colCCCD.setCellValueFactory(new PropertyValueFactory<>("soCCCD"));
        colSDT.setCellValueFactory(new PropertyValueFactory<>("soDienThoai"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colDiaChi.setCellValueFactory(new PropertyValueFactory<>("diaChi"));
        colTenDangNhap.setCellValueFactory(new PropertyValueFactory<>("tenDangNhap"));

        // Setup action column
        colThaoTac.setCellFactory(param -> new TableCell<>() {
            private final Button btnXem = new Button("Xem");
            private final Button btnSua = new Button("Sửa");
            private final HBox hBox = new HBox(8, btnXem, btnSua);

            {
                // Style button Xem
                btnXem.setStyle("-fx-background-color: #ECEBFA; -fx-text-fill: #5148C9; "
                        + "-fx-cursor: hand; -fx-background-radius: 5; -fx-font-weight: bold;");
                // Style button Sửa
                btnSua.setStyle("-fx-background-color: #FEF3C7; -fx-text-fill: #D97706; "
                        + "-fx-cursor: hand; -fx-background-radius: 5; -fx-font-weight: bold;");

                hBox.setAlignment(Pos.CENTER);

                btnXem.setOnAction(event -> {
                    KhachHang kh = getTableView().getItems().get(getIndex());
                    moPopupChiTiet(kh);
                });

                btnSua.setOnAction(event -> {
                    KhachHang kh = getTableView().getItems().get(getIndex());
                    moPopupSua(kh);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hBox);
            }
        });

        // Setup detail account table columns
        colDetailSoTK.setCellValueFactory(new PropertyValueFactory<>("soTaiKhoan"));
        colDetailSoDu.setCellValueFactory(new PropertyValueFactory<>("soDu"));
        colDetailTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        colDetailChiNhanh.setCellValueFactory(new PropertyValueFactory<>("chiNhanh"));

        // Load data
        taiDanhSachKhachHang();
    }

    // ======================== LOAD DATA ========================

    private void taiDanhSachKhachHang() {
        danhSachKH.clear();
        try {
            danhSachKH.setAll(customerService.layDanhSachKhachHang());
        } catch (Exception e) {
            e.printStackTrace();
            hienThiThongBaoLoi("Không thể tải danh sách khách hàng: " + e.getMessage());
        }
        tblKhachHang.setItems(danhSachKH);
        lblTongKhachHang.setText(String.valueOf(danhSachKH.size()));
    }

    private void hienThiThongBaoLoi(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi dữ liệu");
        alert.setHeaderText("Không thể thực hiện thao tác");
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ======================== SEARCH / REFRESH ========================

    @FXML
    public void onTimKiem(ActionEvent event) {
        String keyword = txtTimKiem.getText();
        if (keyword == null || keyword.trim().isEmpty()) {
            tblKhachHang.setItems(danhSachKH);
            lblTongKhachHang.setText(String.valueOf(danhSachKH.size()));
            return;
        }

        String lowerKeyword = keyword.trim().toLowerCase();
        ObservableList<KhachHang> filtered = danhSachKH.stream()
                .filter(kh ->
                        (kh.getMaKhachHang() != null && kh.getMaKhachHang().toLowerCase().contains(lowerKeyword))
                                || (kh.getHoTen() != null && kh.getHoTen().toLowerCase().contains(lowerKeyword))
                                || (kh.getSoCCCD() != null && kh.getSoCCCD().toLowerCase().contains(lowerKeyword))
                                || (kh.getSoDienThoai() != null && kh.getSoDienThoai().toLowerCase().contains(lowerKeyword))
                )
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        tblKhachHang.setItems(filtered);
        lblTongKhachHang.setText(String.valueOf(filtered.size()));
    }

    @FXML
    public void onLamMoi(ActionEvent event) {
        txtTimKiem.clear();
        taiDanhSachKhachHang();
    }

    // ======================== MODAL THÊM KH ========================

    @FXML
    public void onMoPopupThemKhachHang(ActionEvent event) {
        txtThemHoTen.clear();
        txtThemSoCCCD.clear();
        txtThemSoDienThoai.clear();
        txtThemEmail.clear();
        txtThemDiaChi.clear();
        txtThemTenDangNhap.clear();
        txtThemMatKhau.clear();
        lblThemError.setVisible(false);
        lblThemError.setManaged(false);

        modalThemKhachHang.setVisible(true);
        modalThemKhachHang.setManaged(true);
    }

    @FXML
    public void onLuuThemKhachHang(ActionEvent event) {
        String hoTen = txtThemHoTen.getText() != null ? txtThemHoTen.getText().trim() : "";
        String soCCCD = txtThemSoCCCD.getText() != null ? txtThemSoCCCD.getText().trim() : "";
        String soDienThoai = txtThemSoDienThoai.getText() != null ? txtThemSoDienThoai.getText().trim() : "";
        String email = txtThemEmail.getText() != null ? txtThemEmail.getText().trim() : "";
        String diaChi = txtThemDiaChi.getText() != null ? txtThemDiaChi.getText().trim() : "";
        String tenDangNhap = txtThemTenDangNhap.getText() != null ? txtThemTenDangNhap.getText().trim() : "";
        String matKhau = txtThemMatKhau.getText() != null ? txtThemMatKhau.getText().trim() : "";

        // Validate required fields
        if (hoTen.isEmpty() || soCCCD.isEmpty()
                || soDienThoai.isEmpty() || tenDangNhap.isEmpty() || matKhau.isEmpty()) {
            hienThiLoiThem("Vui lòng điền đầy đủ các trường bắt buộc.");
            return;
        }

        try {
            if (customerService.tonTaiCCCD(soCCCD)) {
                hienThiLoiThem("Số CCCD đã tồn tại trong hệ thống.");
                return;
            }
            if (customerService.tonTaiSoDienThoai(soDienThoai, null)) {
                hienThiLoiThem("Số điện thoại đã tồn tại trong hệ thống.");
                return;
            }

            KhachHang khachHang = new KhachHang();
            khachHang.setHoTen(hoTen);
            khachHang.setSoCCCD(soCCCD);
            khachHang.setSoDienThoai(soDienThoai);
            khachHang.setEmail(email);
            khachHang.setDiaChi(diaChi);
            khachHang.setTenDangNhap(tenDangNhap);

            customerService.themKhachHang(khachHang, matKhau);
            onDongPopupThem(event);
            taiDanhSachKhachHang();

        } catch (Exception e) {
            e.printStackTrace();
            hienThiLoiThem("Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void hienThiLoiThem(String message) {
        lblThemError.setText(message);
        lblThemError.setStyle("-fx-text-fill: red; -fx-border-color: red; -fx-border-radius: 5; -fx-padding: 5;");
        lblThemError.setVisible(true);
        lblThemError.setManaged(true);
    }

    @FXML
    public void onDongPopupThem(ActionEvent event) {
        modalThemKhachHang.setVisible(false);
        modalThemKhachHang.setManaged(false);
    }

    // ======================== MODAL SỬA KH ========================

    private void moPopupSua(KhachHang kh) {
        khachHangDangChon = kh;

        txtSuaMaKH.setText(kh.getMaKhachHang());
        txtSuaMaKH.setEditable(false);
        txtSuaSoCCCD.setText(kh.getSoCCCD());
        txtSuaSoCCCD.setEditable(false);
        txtSuaTenDangNhap.setText(kh.getTenDangNhap());
        txtSuaTenDangNhap.setEditable(false);

        txtSuaHoTen.setText(kh.getHoTen());
        txtSuaSoDienThoai.setText(kh.getSoDienThoai());
        txtSuaEmail.setText(kh.getEmail());
        txtSuaDiaChi.setText(kh.getDiaChi());

        lblSuaError.setVisible(false);
        lblSuaError.setManaged(false);

        modalSuaKhachHang.setVisible(true);
        modalSuaKhachHang.setManaged(true);
    }

    @FXML
    public void onMoPopupSuaKhachHang(ActionEvent event) {
        KhachHang selected = tblKhachHang.getSelectionModel().getSelectedItem();
        if (selected != null) {
            moPopupSua(selected);
        }
    }

    @FXML
    public void onCapNhatKhachHang(ActionEvent event) {
        if (khachHangDangChon == null) return;

        String maKH = khachHangDangChon.getMaKhachHang();
        String hoTen = txtSuaHoTen.getText() != null ? txtSuaHoTen.getText().trim() : "";
        String soDienThoai = txtSuaSoDienThoai.getText() != null ? txtSuaSoDienThoai.getText().trim() : "";
        String email = txtSuaEmail.getText() != null ? txtSuaEmail.getText().trim() : "";
        String diaChi = txtSuaDiaChi.getText() != null ? txtSuaDiaChi.getText().trim() : "";

        if (hoTen.isEmpty() || soDienThoai.isEmpty()) {
            hienThiLoiSua("Họ tên và số điện thoại không được để trống.");
            return;
        }

        try {
            if (customerService.tonTaiSoDienThoai(soDienThoai, maKH)) {
                hienThiLoiSua("Số điện thoại đã được sử dụng bởi khách hàng khác.");
                return;
            }

            KhachHang khachHang = new KhachHang();
            khachHang.setMaKhachHang(maKH);
            khachHang.setHoTen(hoTen);
            khachHang.setSoDienThoai(soDienThoai);
            khachHang.setEmail(email);
            khachHang.setDiaChi(diaChi);

            customerService.capNhatKhachHang(khachHang);
            onDongPopupSua(event);
            taiDanhSachKhachHang();

        } catch (Exception e) {
            e.printStackTrace();
            hienThiLoiSua("Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void hienThiLoiSua(String message) {
        lblSuaError.setText(message);
        lblSuaError.setStyle("-fx-text-fill: red; -fx-border-color: red; -fx-border-radius: 5; -fx-padding: 5;");
        lblSuaError.setVisible(true);
        lblSuaError.setManaged(true);
    }

    @FXML
    public void onDongPopupSua(ActionEvent event) {
        modalSuaKhachHang.setVisible(false);
        modalSuaKhachHang.setManaged(false);
    }

    // ======================== MODAL CHI TIẾT KH ========================

    private void moPopupChiTiet(KhachHang kh) {
        khachHangDangChon = kh;

        lblDetailMaKHBadge.setText(kh.getMaKhachHang() != null ? kh.getMaKhachHang() : "");
        lblDetailHoTen.setText(kh.getHoTen() != null ? kh.getHoTen() : "");
        lblDetailCCCD.setText(kh.getSoCCCD() != null ? kh.getSoCCCD() : "");
        lblDetailSDT.setText(kh.getSoDienThoai() != null ? kh.getSoDienThoai() : "");
        lblDetailEmail.setText(kh.getEmail() != null ? kh.getEmail() : "");
        lblDetailTenDangNhap.setText(kh.getTenDangNhap() != null ? kh.getTenDangNhap() : "");
        lblDetailDiaChi.setText(kh.getDiaChi() != null ? kh.getDiaChi() : "");

        // Lấy danh sách tài khoản liên kết qua Service/DAO.
        ObservableList<DetailAccountItem> detailAccounts = FXCollections.observableArrayList();
        try {
            for (CSKHCustomerAccountItem account :
                    customerService.layTaiKhoanTheoKhachHang(kh.getMaKhachHang())) {
                String soDuFormatted = account.getSoDu() != null
                        ? currencyFormat.format(account.getSoDu()) : "0 VNĐ";
                detailAccounts.add(new DetailAccountItem(
                        account.getSoTaiKhoan() != null ? account.getSoTaiKhoan() : "",
                        soDuFormatted,
                        account.getTrangThai() != null ? account.getTrangThai() : "",
                        account.getTenChiNhanh() != null ? account.getTenChiNhanh() : ""
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
            hienThiThongBaoLoi("Không thể tải tài khoản của khách hàng: " + e.getMessage());
        }

        tblDetailTaiKhoan.setItems(detailAccounts);

        modalChiTietKhachHang.setVisible(true);
        modalChiTietKhachHang.setManaged(true);
    }

    @FXML
    public void onMoPopupChiTietKhachHang(ActionEvent event) {
        KhachHang selected = tblKhachHang.getSelectionModel().getSelectedItem();
        if (selected != null) {
            moPopupChiTiet(selected);
        }
    }

    @FXML
    public void onDongPopupChiTiet(ActionEvent event) {
        modalChiTietKhachHang.setVisible(false);
        modalChiTietKhachHang.setManaged(false);
    }

    // ======================== NAVIGATION ========================

    @FXML
    public void onDashboard(ActionEvent event) {
        moView("cskh-dashboard-view.fxml", event);
    }

    @FXML
    public void onKhachHang(ActionEvent event) {
        moView("cskh-customer-view.fxml", event);
    }

    @FXML
    public void onTraCuuTaiKhoan(ActionEvent event) {
        moView("cskh-account-lookup-view.fxml", event);
    }

    @FXML
    public void onTraCuuGiaoDich(ActionEvent event) {
        moView("cskh-transaction-lookup-view.fxml", event);
    }

    @FXML
    public void onDoiSoatBienDong(ActionEvent event) {
        moView("cskh-balance-history-view.fxml", event);
    }

    @FXML
    public void onDangXuat(ActionEvent event) {
        moView("login-view.fxml", event);
    }
}
