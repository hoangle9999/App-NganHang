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
import org.example.appnganhang.controller.service.Impl.CSKHAccountLookupServiceImpl;
import org.example.appnganhang.controller.service.intf.CSKHAccountLookupService;
import org.example.appnganhang.model.entity.CSKHAccountLookupItem;

import java.io.IOException;
import java.math.BigDecimal;
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
    private TableView<CSKHAccountLookupItem> tblTaiKhoan;
    @FXML
    private TableColumn<CSKHAccountLookupItem, String> colMaTK;
    @FXML
    private TableColumn<CSKHAccountLookupItem, String> colSoTK;
    @FXML
    private TableColumn<CSKHAccountLookupItem, String> colTenChuTK;
    @FXML
    private TableColumn<CSKHAccountLookupItem, String> colSoDu;
    @FXML
    private TableColumn<CSKHAccountLookupItem, String> colTenNganHang;
    @FXML
    private TableColumn<CSKHAccountLookupItem, String> colChiNhanh;
    @FXML
    private TableColumn<CSKHAccountLookupItem, String> colHanMuc;
    @FXML
    private TableColumn<CSKHAccountLookupItem, String> colTrangThai;
    @FXML
    private TableColumn<CSKHAccountLookupItem, Void> colThaoTac;

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
    private final ObservableList<CSKHAccountLookupItem> danhSachTK = FXCollections.observableArrayList();
    private final DecimalFormat currencyFormat = new DecimalFormat("#,### VNĐ");
    private final CSKHAccountLookupService accountLookupService = new CSKHAccountLookupServiceImpl();

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
                    CSKHAccountLookupItem item = getTableView().getItems().get(getIndex());
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
        String keyword = txtTimKiemTK != null ? txtTimKiemTK.getText().trim() : "";

        try {
            danhSachTK.setAll(accountLookupService.timKiemTaiKhoan(keyword));
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi tra cứu tài khoản");
            alert.setHeaderText("Không thể tải danh sách tài khoản");
            alert.setContentText("Vui lòng kiểm tra kết nối cơ sở dữ liệu và thử lại.");
            alert.showAndWait();
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
    private void moPopupChiTiet(CSKHAccountLookupItem item) {
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
        CSKHAccountLookupItem selected = tblTaiKhoan.getSelectionModel().getSelectedItem();
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

}