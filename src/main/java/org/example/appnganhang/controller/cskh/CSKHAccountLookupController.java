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
import org.example.appnganhang.model.util.DBConnection;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;

import static org.example.appnganhang.controller.util.Navigation.moView;

public class CSKHAccountLookupController {

    // ======================== FXML Fields ========================
    @FXML
    private Label lblStaffName;
    @FXML
    private Label lblTongTaiKhoan;
    @FXML
    private TextField txtTimKiemTK;
    @FXML
    private ProgressIndicator piLoading;

    @FXML
    private TableView<AccountLookupItem> tblTaiKhoan;
    @FXML
    private TableColumn<AccountLookupItem, String> colMaTK;
    @FXML
    private TableColumn<AccountLookupItem, String> colSoTK;
    @FXML
    private TableColumn<AccountLookupItem, String> colTenChuTK;
    @FXML
    private TableColumn<AccountLookupItem, String> colSoDu;
    @FXML
    private TableColumn<AccountLookupItem, String> colTenNganHang;
    @FXML
    private TableColumn<AccountLookupItem, String> colChiNhanh;
    @FXML
    private TableColumn<AccountLookupItem, String> colHanMuc;
    @FXML
    private TableColumn<AccountLookupItem, String> colTrangThai;
    @FXML
    private TableColumn<AccountLookupItem, Void> colThaoTac;

    // ======================== Modal Chi tiết ========================
    @FXML
    private StackPane modalChiTietTaiKhoan;
    @FXML
    private Label lblModalTrangThaiBadge;
    @FXML
    private Label lblModalSoDu;
    @FXML
    private Label lblModalSoTK;
    @FXML
    private Label lblModalTenChuTK;
    @FXML
    private Label lblModalCCCD;
    @FXML
    private Label lblModalNganHang;
    @FXML
    private Label lblModalChiNhanh;
    @FXML
    private Label lblModalTenHanMuc;
    @FXML
    private Label lblModalHanMucNgay;

    // ======================== Data ========================
    private final ObservableList<AccountLookupItem> danhSachTK = FXCollections.observableArrayList();
    private final DecimalFormat currencyFormat = new DecimalFormat("#,### VNĐ");

    // ======================== Initialize ========================
    @FXML
    public void initialize() {
        // Simple string columns
        colMaTK.setCellValueFactory(new PropertyValueFactory<>("maTaiKhoan"));
        colSoTK.setCellValueFactory(new PropertyValueFactory<>("soTaiKhoan"));
        colTenChuTK.setCellValueFactory(new PropertyValueFactory<>("tenChuTK"));
        colTenNganHang.setCellValueFactory(new PropertyValueFactory<>("tenNganHang"));
        colChiNhanh.setCellValueFactory(new PropertyValueFactory<>("chiNhanh"));
        colHanMuc.setCellValueFactory(new PropertyValueFactory<>("hanMuc"));

        // Số dư – formatted currency
        colSoDu.setCellValueFactory(cellData -> {
            BigDecimal soDu = cellData.getValue().getSoDu();
            String formatted = (soDu != null) ? currencyFormat.format(soDu) : "0 VNĐ";
            return new javafx.beans.property.SimpleStringProperty(formatted);
        });

        // Trạng thái – colored label
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        colTrangThai.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Label label = new Label(item);
                    label.setStyle("-fx-font-weight: bold;");
                    if ("Hoạt động".equals(item)) {
                        label.setStyle(label.getStyle() + "-fx-text-fill: #22C55E;");
                    } else if ("Tạm khóa".equals(item)) {
                        label.setStyle(label.getStyle() + "-fx-text-fill: #EF4444;");
                    }
                    setGraphic(label);
                    setText(null);
                }
            }
        });

        // Thao tác – "Xem chi tiết" button
        colThaoTac.setCellFactory(column -> new TableCell<>() {
            private final Button btnChiTiet = new Button("Xem chi tiết");
            private final HBox hbox = new HBox(btnChiTiet);

            {
                btnChiTiet.setStyle("-fx-background-color: #ECEBFA; -fx-text-fill: #5148C9; "
                        + "-fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 5;");
                hbox.setAlignment(Pos.CENTER);
                btnChiTiet.setOnAction(event -> {
                    AccountLookupItem item = getTableView().getItems().get(getIndex());
                    if (item != null) {
                        moPopupChiTiet(item);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });

        tblTaiKhoan.setItems(danhSachTK);

        // Load all accounts initially
        taiDanhSach();
    }

    // ======================== Load Data ========================
    private void taiDanhSach() {
        danhSachTK.clear();
        String keyword = (txtTimKiemTK != null) ? txtTimKiemTK.getText().trim() : "";

        String sql = "SELECT tk.MaTaiKhoan, tk.SoTaiKhoan, kh.HoTen AS TenChuTK, tk.SoDu, "
                + "ISNULL(nh.TenNganHang, 'SMART BANK') AS TenNganHang, "
                + "ISNULL(cn.TenChiNhanh, 'N/A') AS ChiNhanh, "
                + "ISNULL(hm.TenHanMuc, N'Hạn mức mặc định') AS HanMuc, "
                + "tk.TrangThai, kh.SoCCCD, "
                + "ISNULL(CAST(hm.HanMucNgay AS NVARCHAR), N'Mặc định') AS HanMucNgay "
                + "FROM TaiKhoanNganHang tk "
                + "LEFT JOIN KhachHang kh ON tk.MaKhachHang = kh.MaKhachHang "
                + "LEFT JOIN NganHang nh ON tk.MaNganHang = nh.MaNganHang "
                + "LEFT JOIN ChiNhanh cn ON tk.MaChiNhanh = cn.MaChiNhanh "
                + "LEFT JOIN HanMucGiaoDich hm ON tk.MaHanMuc = hm.MaHanMuc";

        if (!keyword.isEmpty()) {
            sql += " WHERE tk.SoTaiKhoan LIKE ? OR kh.SoCCCD LIKE ?";
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (!keyword.isEmpty()) {
                String wildcard = "%" + keyword + "%";
                ps.setString(1, wildcard);
                ps.setString(2, wildcard);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AccountLookupItem item = new AccountLookupItem(
                            rs.getString("MaTaiKhoan"),
                            rs.getString("SoTaiKhoan"),
                            rs.getString("TenChuTK"),
                            rs.getBigDecimal("SoDu"),
                            rs.getString("TenNganHang"),
                            rs.getString("ChiNhanh"),
                            rs.getString("HanMuc"),
                            rs.getString("TrangThai"),
                            rs.getString("SoCCCD"),
                            rs.getString("HanMucNgay")
                    );
                    danhSachTK.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (lblTongTaiKhoan != null) {
            lblTongTaiKhoan.setText(String.valueOf(danhSachTK.size()));
        }
    }

    // ======================== Search Action ========================
    @FXML
    void onTraCuuTaiKhoanAction(ActionEvent event) {
        taiDanhSach();
    }

    // ======================== Modal Chi tiết ========================
    private void moPopupChiTiet(AccountLookupItem item) {
        if (item == null) return;

        lblModalSoTK.setText(item.getSoTaiKhoan() != null ? item.getSoTaiKhoan() : "");
        lblModalTenChuTK.setText(item.getTenChuTK() != null ? item.getTenChuTK() : "");
        lblModalCCCD.setText(item.getSoCCCD() != null ? item.getSoCCCD() : "");
        lblModalSoDu.setText(item.getSoDu() != null ? currencyFormat.format(item.getSoDu()) : "0 VNĐ");
        lblModalNganHang.setText(item.getTenNganHang() != null ? item.getTenNganHang() : "");
        lblModalChiNhanh.setText(item.getChiNhanh() != null ? item.getChiNhanh() : "");
        lblModalTenHanMuc.setText(item.getHanMuc() != null ? item.getHanMuc() : "Hạn mức mặc định");
        lblModalHanMucNgay.setText(item.getHanMucNgay() != null ? item.getHanMucNgay() : "Mặc định");

        // Trạng thái badge
        String trangThai = item.getTrangThai();
        if (trangThai != null) {
            lblModalTrangThaiBadge.setText(trangThai);
            if ("Hoạt động".equals(trangThai)) {
                lblModalTrangThaiBadge.setStyle("-fx-text-fill: #22C55E; -fx-font-weight: bold;");
            } else if ("Tạm khóa".equals(trangThai)) {
                lblModalTrangThaiBadge.setStyle("-fx-text-fill: #EF4444; -fx-font-weight: bold;");
            } else {
                lblModalTrangThaiBadge.setStyle("-fx-font-weight: bold;");
            }
        } else {
            lblModalTrangThaiBadge.setText("");
        }

        modalChiTietTaiKhoan.setVisible(true);
        modalChiTietTaiKhoan.setManaged(true);
    }

    @FXML
    void onMoPopupChiTietTaiKhoan(ActionEvent event) {
        AccountLookupItem selected = tblTaiKhoan.getSelectionModel().getSelectedItem();
        moPopupChiTiet(selected);
    }

    @FXML
    void onDongPopupChiTiet(ActionEvent event) {
        modalChiTietTaiKhoan.setVisible(false);
        modalChiTietTaiKhoan.setManaged(false);
    }

    // ======================== Làm mới ========================
    @FXML
    void onLamMoi(ActionEvent event) {
        txtTimKiemTK.clear();
        taiDanhSach();
    }

    // ======================== Navigation ========================
    @FXML
    void onDashboard(ActionEvent event) throws IOException {
        moView("cskh-dashboard-view.fxml", event);
    }

    @FXML
    void onKhachHang(ActionEvent event) throws IOException {
        moView("cskh-customer-view.fxml", event);
    }

    @FXML
    void onTraCuuTaiKhoan(ActionEvent event) throws IOException {
        moView("cskh-account-lookup-view.fxml", event);
    }

    @FXML
    void onTraCuuGiaoDich(ActionEvent event) throws IOException {
        moView("cskh-transaction-lookup-view.fxml", event);
    }

    @FXML
    void onDoiSoatBienDong(ActionEvent event) throws IOException {
        moView("cskh-balance-history-view.fxml", event);
    }

    @FXML
    void onDangXuat(ActionEvent event) throws IOException {
        moView("login-view.fxml", event);
    }

    // ======================== Inner Class ========================
    public static class AccountLookupItem {
        private String maTaiKhoan;
        private String soTaiKhoan;
        private String tenChuTK;
        private BigDecimal soDu;
        private String tenNganHang;
        private String chiNhanh;
        private String hanMuc;
        private String trangThai;
        private String soCCCD;
        private String hanMucNgay;

        public AccountLookupItem(String maTaiKhoan, String soTaiKhoan, String tenChuTK,
                                 BigDecimal soDu, String tenNganHang, String chiNhanh,
                                 String hanMuc, String trangThai, String soCCCD, String hanMucNgay) {
            this.maTaiKhoan = maTaiKhoan;
            this.soTaiKhoan = soTaiKhoan;
            this.tenChuTK = tenChuTK;
            this.soDu = soDu;
            this.tenNganHang = tenNganHang;
            this.chiNhanh = chiNhanh;
            this.hanMuc = hanMuc;
            this.trangThai = trangThai;
            this.soCCCD = soCCCD;
            this.hanMucNgay = hanMucNgay;
        }

        public String getMaTaiKhoan() { return maTaiKhoan; }
        public void setMaTaiKhoan(String maTaiKhoan) { this.maTaiKhoan = maTaiKhoan; }

        public String getSoTaiKhoan() { return soTaiKhoan; }
        public void setSoTaiKhoan(String soTaiKhoan) { this.soTaiKhoan = soTaiKhoan; }

        public String getTenChuTK() { return tenChuTK; }
        public void setTenChuTK(String tenChuTK) { this.tenChuTK = tenChuTK; }

        public BigDecimal getSoDu() { return soDu; }
        public void setSoDu(BigDecimal soDu) { this.soDu = soDu; }

        public String getTenNganHang() { return tenNganHang; }
        public void setTenNganHang(String tenNganHang) { this.tenNganHang = tenNganHang; }

        public String getChiNhanh() { return chiNhanh; }
        public void setChiNhanh(String chiNhanh) { this.chiNhanh = chiNhanh; }

        public String getHanMuc() { return hanMuc; }
        public void setHanMuc(String hanMuc) { this.hanMuc = hanMuc; }

        public String getTrangThai() { return trangThai; }
        public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

        public String getSoCCCD() { return soCCCD; }
        public void setSoCCCD(String soCCCD) { this.soCCCD = soCCCD; }

        public String getHanMucNgay() { return hanMucNgay; }
        public void setHanMucNgay(String hanMucNgay) { this.hanMucNgay = hanMucNgay; }
    }
}
