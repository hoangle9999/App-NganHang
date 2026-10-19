package org.example.appnganhang.controller.khachhang;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.appnganhang.controller.service.Iml.KhachHangPortalServiceImpl;
import org.example.appnganhang.controller.service.intf.KhachHangPortalService;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import static org.example.appnganhang.controller.util.Navigation.moView;

public class CustomerHomeController {
    private final KhachHangPortalService portalService = new KhachHangPortalServiceImpl();

    @FXML private Label lblSidebarTenKH;
    @FXML private Label lblHeaderTenKH;

    @FXML private ComboBox<String> cboChonTaiKhoan;
    @FXML private Label lblTenNganHang;
    @FXML private Label lblCardTrangThai;
    @FXML private Label lblSoTaiKhoan;
    @FXML private Button btnSaoChepSTK;
    @FXML private Label lblSoDuKhachHang;
    @FXML private Button btnToggleSoDu;

    @FXML private Label lblCardTenChuTK;
    @FXML private Label lblCardChiNhanh;
    @FXML private Label lblCardHanMuc;
    @FXML private Label lblCardTrangThaiText;
    @FXML private Label lblToastSaoChep;

    // Bảng danh sách tài khoản
    @FXML private TableView<CustomerAccountItem> tblDanhSachTK;
    @FXML private TableColumn<CustomerAccountItem, String> colSoTK;
    @FXML private TableColumn<CustomerAccountItem, String> colSoDu;
    @FXML private TableColumn<CustomerAccountItem, String> colNganHang;
    @FXML private TableColumn<CustomerAccountItem, String> colChiNhanh;
    @FXML private TableColumn<CustomerAccountItem, String> colHanMuc;
    @FXML private TableColumn<CustomerAccountItem, String> colTrangThai;

    // Modal Chi tiết
    @FXML private VBox modalChiTietTaiKhoan;
    @FXML private Label lblModalDetailSoTK;
    @FXML private Label lblModalDetailChuTK;
    @FXML private Label lblModalDetailCCCD;
    @FXML private Label lblModalDetailSDT;
    @FXML private Label lblModalDetailChiNhanh;
    @FXML private Label lblModalDetailTenHanMuc;
    @FXML private Label lblModalDetailHanMucNgay;
    @FXML private Label lblModalDetailTrangThai;

    // Modal Đăng xuất
    @FXML private VBox modalXacNhanDangXuat;

    private final ObservableList<CustomerAccountItem> danhSachTaiKhoan = FXCollections.observableArrayList();
    private final DecimalFormat dfTien = new DecimalFormat("#,### VNĐ");

    private boolean isSoDuAn = false;
    private CustomerAccountItem currentAccount = null;

    @FXML
    public void initialize() {
        lblSidebarTenKH.setText(CustomerSession.getCurrentHoTen());
        lblHeaderTenKH.setText(CustomerSession.getCurrentHoTen());

        cauHinhBangTaiKhoan();
        taiDanhSachTaiKhoanKhachHang();
    }

    private void cauHinhBangTaiKhoan() {
        colSoTK.setCellValueFactory(new PropertyValueFactory<>("soTaiKhoan"));
        colNganHang.setCellValueFactory(new PropertyValueFactory<>("tenNganHang"));
        colChiNhanh.setCellValueFactory(new PropertyValueFactory<>("tenChiNhanh"));
        colHanMuc.setCellValueFactory(new PropertyValueFactory<>("tenHanMuc"));

        colSoDu.setCellValueFactory(cellData -> {
            BigDecimal sd = cellData.getValue().getSoDu();
            return new SimpleStringProperty(sd != null ? dfTien.format(sd) : "0 VNĐ");
        });

        colTrangThai.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    CustomerAccountItem tk = getTableRow().getItem();
                    String tt = tk.getTrangThai() != null ? tk.getTrangThai() : "Hoạt động";
                    Label badge = new Label(tt);
                    if ("Hoạt động".equalsIgnoreCase(tt)) {
                        badge.setStyle("-fx-background-color: #E8F8F0; -fx-text-fill: #10B981; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 8; -fx-font-size: 11px;");
                    } else {
                        badge.setStyle("-fx-background-color: #FDECEC; -fx-text-fill: #D9534F; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 8; -fx-font-size: 11px;");
                    }
                    HBox box = new HBox(badge);
                    box.setAlignment(Pos.CENTER);
                    setGraphic(box);
                }
            }
        });

        tblDanhSachTK.setItems(danhSachTaiKhoan);

        // Khi click vào dòng trong bảng, cập nhật thẻ chính
        tblDanhSachTK.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                hienThiTheTaiKhoan(newVal);
                cboChonTaiKhoan.setValue(newVal.getSoTaiKhoan());
            }
        });
    }

    private void taiDanhSachTaiKhoanKhachHang() {
        danhSachTaiKhoan.clear();
        ObservableList<String> listSTK = FXCollections.observableArrayList();

        try {
            for (java.util.Map<String, Object> rs : portalService.layTaiKhoanKhachHang(
                    CustomerSession.getCurrentMaKhachHang(), CustomerSession.getCurrentTenDangNhap())) {
                CustomerAccountItem item = new CustomerAccountItem();
                item.setMaTaiKhoan(value(rs, "MaTaiKhoan"));
                item.setSoTaiKhoan(value(rs, "SoTaiKhoan"));
                item.setSoDu(decimal(rs.get("SoDu")));
                item.setTrangThai(value(rs, "TrangThai"));
                item.setTenNganHang(value(rs, "TenNganHang"));
                item.setTenChiNhanh(value(rs, "TenChiNhanh"));
                item.setTenHanMuc(value(rs, "TenHanMuc"));
                item.setHanMucNgay(decimal(rs.get("HanMucNgay")));
                danhSachTaiKhoan.add(item);
                listSTK.add(item.getSoTaiKhoan());
            }
        } catch (RuntimeException e) {
            System.err.println("Lỗi nạp tài khoản khách hàng: " + e.getMessage());
        }

        cboChonTaiKhoan.setItems(listSTK);

        if (danhSachTaiKhoan.isEmpty()) {
            currentAccount = null;
            lblTenNganHang.setText("—");
            lblCardTrangThai.setText("—");
            lblCardTrangThaiText.setText("—");
            lblSoTaiKhoan.setText("—");
            lblSoDuKhachHang.setText("—");
            lblCardTenChuTK.setText(CustomerSession.getCurrentHoTen());
            lblCardChiNhanh.setText("—");
            lblCardHanMuc.setText("—");
            return;
        }

        // Chọn tài khoản đầu tiên hoặc tài khoản đã lưu
        String savedSTK = CustomerSession.getSelectedSoTaiKhoan();
        if (savedSTK != null && !savedSTK.isEmpty() && listSTK.contains(savedSTK)) {
            cboChonTaiKhoan.setValue(savedSTK);
            chonTheoSoTaiKhoan(savedSTK);
        } else if (!danhSachTaiKhoan.isEmpty()) {
            cboChonTaiKhoan.getSelectionModel().selectFirst();
            hienThiTheTaiKhoan(danhSachTaiKhoan.get(0));
        }
    }

    private static String value(java.util.Map<String, Object> row, String key) {
        Object value = row.get(key); return value == null ? "" : value.toString();
    }
    private static BigDecimal decimal(Object value) {
        if (value == null) return BigDecimal.ZERO;
        return value instanceof BigDecimal ? (BigDecimal) value : new BigDecimal(value.toString());
    }


    private void chonTheoSoTaiKhoan(String stk) {
        for (CustomerAccountItem it : danhSachTaiKhoan) {
            if (it.getSoTaiKhoan().equals(stk)) {
                hienThiTheTaiKhoan(it);
                break;
            }
        }
    }

    private void hienThiTheTaiKhoan(CustomerAccountItem item) {
        this.currentAccount = item;
        CustomerSession.setSelectedSoTaiKhoan(item.getSoTaiKhoan());

        lblTenNganHang.setText(item.getTenNganHang());
        lblCardTrangThai.setText(item.getTrangThai());
        lblCardTrangThaiText.setText(item.getTrangThai());

        lblSoTaiKhoan.setText(dinhDangSoTaiKhoan(item.getSoTaiKhoan()));
        capNhatHienThiSoDu();

        lblCardTenChuTK.setText(CustomerSession.getCurrentHoTen());
        lblCardChiNhanh.setText(item.getTenChiNhanh());
        lblCardHanMuc.setText(item.getTenHanMuc() + " (" + dfTien.format(item.getHanMucNgay()) + "/ngày)");
    }

    private String dinhDangSoTaiKhoan(String stk) {
        if (stk == null) return "---";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < stk.length(); i++) {
            if (i > 0 && i % 4 == 0) sb.append(" ");
            sb.append(stk.charAt(i));
        }
        return sb.toString();
    }

    private void capNhatHienThiSoDu() {
        if (currentAccount == null) return;
        if (isSoDuAn) {
            lblSoDuKhachHang.setText("****** VNĐ");
            btnToggleSoDu.setText("👁 Hiện số dư");
        } else {
            lblSoDuKhachHang.setText(dfTien.format(currentAccount.getSoDu()));
            btnToggleSoDu.setText("👁 Ẩn số dư");
        }
    }

    @FXML
    public void onChonTaiKhoan() {
        String stk = cboChonTaiKhoan.getValue();
        if (stk != null) {
            chonTheoSoTaiKhoan(stk);
        }
    }

    @FXML
    public void onToggleSoDu() {
        isSoDuAn = !isSoDuAn;
        capNhatHienThiSoDu();
    }

    @FXML
    public void onSaoChepSoTaiKhoan() {
        if (currentAccount != null) {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(currentAccount.getSoTaiKhoan());
            clipboard.setContent(content);

            lblToastSaoChep.setText("✔ Đã sao chép số tài khoản [" + currentAccount.getSoTaiKhoan() + "] vào bộ nhớ tạm!");
            lblToastSaoChep.setVisible(true);
            lblToastSaoChep.setManaged(true);

            // Tự ẩn sau 3 giây hoặc click khác
            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                    javafx.application.Platform.runLater(() -> {
                        lblToastSaoChep.setVisible(false);
                        lblToastSaoChep.setManaged(false);
                    });
                } catch (InterruptedException ignored) {}
            }).start();
        }
    }

    @FXML
    public void onMoModalChiTietTaiKhoan() {
        if (currentAccount == null) return;

        lblModalDetailSoTK.setText(currentAccount.getSoTaiKhoan());
        lblModalDetailChuTK.setText(CustomerSession.getCurrentHoTen());
        lblModalDetailCCCD.setText(CustomerSession.getCurrentSoCCCD().isEmpty() ? "Chưa cập nhật" : CustomerSession.getCurrentSoCCCD());
        lblModalDetailSDT.setText(CustomerSession.getCurrentSoDienThoai().isEmpty() ? "Chưa cập nhật" : CustomerSession.getCurrentSoDienThoai());
        lblModalDetailChiNhanh.setText(currentAccount.getTenChiNhanh());
        lblModalDetailTenHanMuc.setText(currentAccount.getTenHanMuc() != null ? currentAccount.getTenHanMuc() : "Mặc định");
        lblModalDetailHanMucNgay.setText(dfTien.format(currentAccount.getHanMucNgay()));
        lblModalDetailTrangThai.setText(currentAccount.getTrangThai());

        hienModal(modalChiTietTaiKhoan);
    }

    @FXML
    public void onDongModalChiTiet() {
        anModal(modalChiTietTaiKhoan);
    }

    @FXML
    public void onMoModalDangXuat() {
        hienModal(modalXacNhanDangXuat);
    }

    @FXML
    public void onDongModalDangXuat() {
        anModal(modalXacNhanDangXuat);
    }

    @FXML
    public void onXacNhanDangXuatThucTe(ActionEvent event) {
        anModal(modalXacNhanDangXuat);
        CustomerSession.clear();
        moView("login-view.fxml", event);
    }

    @FXML
    public void onLichSuGiaoDich(ActionEvent event) {
        moView("customer-transactions-view.fxml", event);
    }

    @FXML
    public void onBienDongSoDu(ActionEvent event) {
        moView("customer-balance-history-view.fxml", event);
    }

    private void hienModal(VBox modal) {
        if (modal != null) {
            modal.setVisible(true);
            modal.setManaged(true);
        }
    }

    private void anModal(VBox modal) {
        if (modal != null) {
            modal.setVisible(false);
            modal.setManaged(false);
        }
    }

    // =========================================================
    // DỮ LIỆU TÀI KHOẢN KHÁCH HÀNG (MODEL ITEM)
    // =========================================================
    public static class CustomerAccountItem {
        private String maTaiKhoan;
        private String soTaiKhoan;
        private BigDecimal soDu;
        private String trangThai;
        private String tenNganHang;
        private String tenChiNhanh;
        private String tenHanMuc;
        private BigDecimal hanMucNgay;

        public CustomerAccountItem() {}

        public CustomerAccountItem(String maTaiKhoan, String soTaiKhoan, BigDecimal soDu, String trangThai, String tenNganHang, String tenChiNhanh, String tenHanMuc, BigDecimal hanMucNgay) {
            this.maTaiKhoan = maTaiKhoan;
            this.soTaiKhoan = soTaiKhoan;
            this.soDu = soDu;
            this.trangThai = trangThai;
            this.tenNganHang = tenNganHang;
            this.tenChiNhanh = tenChiNhanh;
            this.tenHanMuc = tenHanMuc;
            this.hanMucNgay = hanMucNgay;
        }

        public String getMaTaiKhoan() { return maTaiKhoan; }
        public void setMaTaiKhoan(String maTaiKhoan) { this.maTaiKhoan = maTaiKhoan; }
        public String getSoTaiKhoan() { return soTaiKhoan; }
        public void setSoTaiKhoan(String soTaiKhoan) { this.soTaiKhoan = soTaiKhoan; }
        public BigDecimal getSoDu() { return soDu; }
        public void setSoDu(BigDecimal soDu) { this.soDu = soDu; }
        public String getTrangThai() { return trangThai; }
        public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
        public String getTenNganHang() { return tenNganHang; }
        public void setTenNganHang(String tenNganHang) { this.tenNganHang = tenNganHang; }
        public String getTenChiNhanh() { return tenChiNhanh; }
        public void setTenChiNhanh(String tenChiNhanh) { this.tenChiNhanh = tenChiNhanh; }
        public String getTenHanMuc() { return tenHanMuc; }
        public void setTenHanMuc(String tenHanMuc) { this.tenHanMuc = tenHanMuc; }
        public BigDecimal getHanMucNgay() { return hanMucNgay; }
        public void setHanMucNgay(BigDecimal hanMucNgay) { this.hanMucNgay = hanMucNgay; }
    }
}
