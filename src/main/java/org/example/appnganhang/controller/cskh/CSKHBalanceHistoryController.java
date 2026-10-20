package org.example.appnganhang.controller.cskh;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.example.appnganhang.controller.util.StaffHeader;
import org.example.appnganhang.model.entity.LichSuGiaoDich;
import org.example.appnganhang.controller.service.Impl.CSKHBalanceHistoryServiceImpl;
import org.example.appnganhang.controller.service.intf.CSKHBalanceHistoryService;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.SQLException;

import static org.example.appnganhang.controller.util.Navigation.moView;

public class CSKHBalanceHistoryController {

    // ========== HEADER ==========
    @FXML
    private Label lblStaffName;

    @FXML
    private Label lblTongLog;

    // ========== SEARCH / FILTER ==========
    @FXML
    private TextField txtTimKiemLog;

    @FXML
    private DatePicker dpLogTuNgay;

    @FXML
    private DatePicker dpLogDenNgay;

    // ========== TABLE ==========
    @FXML
    private TableView<LichSuGiaoDich> tblBienDong;

    @FXML
    private TableColumn<LichSuGiaoDich, String> colMaLog;

    @FXML
    private TableColumn<LichSuGiaoDich, String> colMaGD;

    @FXML
    private TableColumn<LichSuGiaoDich, String> colSoTKLienQuan;

    @FXML
    private TableColumn<LichSuGiaoDich, String> colHanhDong;

    @FXML
    private TableColumn<LichSuGiaoDich, String> colSoTienThayDoi;

    @FXML
    private TableColumn<LichSuGiaoDich, String> colSoDuSau;

    @FXML
    private TableColumn<LichSuGiaoDich, String> colThoiGian;

    // ========== MODAL IN PHIẾU ĐỐI SOÁT ==========
    @FXML
    private StackPane modalInPhieuDoiSoat;

    @FXML
    private Label lblPrintNgayIn;

    @FXML
    private Label lblPrintSoTK;

    @FXML
    private Label lblPrintTenChuTK;

    @FXML
    private Label lblPrintMaGD;

    @FXML
    private Label lblPrintNgayTraCuu;

    @FXML
    private Label lblPrintNguoiLap;

    @FXML
    private TableView<LichSuGiaoDich> tblPrintBienDong;

    @FXML
    private TableColumn<LichSuGiaoDich, String> colPrintMaLog;

    @FXML
    private TableColumn<LichSuGiaoDich, String> colPrintHanhDong;

    @FXML
    private TableColumn<LichSuGiaoDich, String> colPrintSoTienThayDoi;

    @FXML
    private TableColumn<LichSuGiaoDich, String> colPrintSoDuSau;

    @FXML
    private TableColumn<LichSuGiaoDich, String> colPrintThoiGian;

    // ========== DATA ==========
    private ObservableList<LichSuGiaoDich> danhSachLog;
    private final CSKHBalanceHistoryService balanceHistoryService = new CSKHBalanceHistoryServiceImpl();
    private final DecimalFormat dfTien = new DecimalFormat("#,### VNĐ");
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @FXML
    public void initialize() {
        StaffHeader.loadCustomerService(lblStaffName);
        // ---- Main table columns ----
        colMaLog.setCellValueFactory(cellData -> {
            Integer maLog = cellData.getValue().getMaLog();
            return new javafx.beans.property.SimpleStringProperty(maLog != null ? maLog.toString() : "");
        });

        colMaGD.setCellValueFactory(new PropertyValueFactory<>("maGiaoDich"));
        colSoTKLienQuan.setCellValueFactory(new PropertyValueFactory<>("soTaiKhoanLienQuan"));
        colHanhDong.setCellValueFactory(new PropertyValueFactory<>("hanhDong"));

        colSoTienThayDoi.setCellValueFactory(cellData -> {
            BigDecimal soTien = cellData.getValue().getSoTienThayDoi();
            if (soTien == null) return new javafx.beans.property.SimpleStringProperty("");
            String formatted = dfTien.format(soTien);
            if (soTien.compareTo(BigDecimal.ZERO) > 0) {
                formatted = "+" + formatted;
            }
            return new javafx.beans.property.SimpleStringProperty(formatted);
        });

        colSoTienThayDoi.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setTextFill(Color.BLACK);
                } else {
                    setText(item);
                    if (item.startsWith("+")) {
                        setTextFill(Color.web("#22C55E"));
                    } else {
                        setTextFill(Color.web("#EF4444"));
                    }
                }
            }
        });

        colSoDuSau.setCellValueFactory(cellData -> {
            BigDecimal soDu = cellData.getValue().getSoDuSauThayDoi();
            if (soDu == null) return new javafx.beans.property.SimpleStringProperty("");
            return new javafx.beans.property.SimpleStringProperty(dfTien.format(soDu));
        });

        colThoiGian.setCellValueFactory(cellData -> {
            LocalDateTime thoiGian = cellData.getValue().getThoiGianGhiLog();
            if (thoiGian == null) return new javafx.beans.property.SimpleStringProperty("");
            return new javafx.beans.property.SimpleStringProperty(dtf.format(thoiGian));
        });

        // ---- Print table columns ----
        colPrintMaLog.setCellValueFactory(cellData -> {
            Integer maLog = cellData.getValue().getMaLog();
            return new javafx.beans.property.SimpleStringProperty(maLog != null ? maLog.toString() : "");
        });

        colPrintHanhDong.setCellValueFactory(new PropertyValueFactory<>("hanhDong"));

        colPrintSoTienThayDoi.setCellValueFactory(cellData -> {
            BigDecimal soTien = cellData.getValue().getSoTienThayDoi();
            if (soTien == null) return new javafx.beans.property.SimpleStringProperty("");
            String formatted = dfTien.format(soTien);
            if (soTien.compareTo(BigDecimal.ZERO) > 0) {
                formatted = "+" + formatted;
            }
            return new javafx.beans.property.SimpleStringProperty(formatted);
        });

        colPrintSoTienThayDoi.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setTextFill(Color.BLACK);
                } else {
                    setText(item);
                    if (item.startsWith("+")) {
                        setTextFill(Color.web("#22C55E"));
                    } else {
                        setTextFill(Color.web("#EF4444"));
                    }
                }
            }
        });

        colPrintSoDuSau.setCellValueFactory(cellData -> {
            BigDecimal soDu = cellData.getValue().getSoDuSauThayDoi();
            if (soDu == null) return new javafx.beans.property.SimpleStringProperty("");
            return new javafx.beans.property.SimpleStringProperty(dfTien.format(soDu));
        });

        colPrintThoiGian.setCellValueFactory(cellData -> {
            LocalDateTime thoiGian = cellData.getValue().getThoiGianGhiLog();
            if (thoiGian == null) return new javafx.beans.property.SimpleStringProperty("");
            return new javafx.beans.property.SimpleStringProperty(dtf.format(thoiGian));
        });

        // ---- Load data ----
        taiDanhSachLog();
    }

    private void taiDanhSachLog() {
        try {
            danhSachLog = FXCollections.observableArrayList(
                    balanceHistoryService.layDanhSachLog(null, null, null));
        } catch (SQLException e) {
            danhSachLog = FXCollections.observableArrayList();
            hienThongBaoLoi("Không thể tải lịch sử biến động số dư.", e);
        }
        tblBienDong.setItems(danhSachLog);
        lblTongLog.setText(String.valueOf(danhSachLog.size()));
    }

    @FXML
    public void onTraCuuLog(ActionEvent event) {
        String keyword = txtTimKiemLog.getText() == null ? "" : txtTimKiemLog.getText().trim();
        try {
            danhSachLog = FXCollections.observableArrayList(
                    balanceHistoryService.layDanhSachLog(
                            keyword, dpLogTuNgay.getValue(), dpLogDenNgay.getValue()));
        } catch (SQLException e) {
            danhSachLog = FXCollections.observableArrayList();
            hienThongBaoLoi("Không thể tra cứu lịch sử biến động số dư.", e);
        }
        tblBienDong.setItems(danhSachLog);
        lblTongLog.setText(String.valueOf(danhSachLog.size()));
    }

    private void hienThongBaoLoi(String noiDung, Exception exception) {
        exception.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi dữ liệu");
        alert.setHeaderText(null);
        alert.setContentText(noiDung + " Chi tiết: " + exception.getMessage());
        alert.showAndWait();
    }

    @FXML
    public void onLamMoi(ActionEvent event) {
        txtTimKiemLog.clear();
        dpLogTuNgay.setValue(null);
        dpLogDenNgay.setValue(null);
        taiDanhSachLog();
    }

    @FXML
    public void onMoModalInPhieuDoiSoat(ActionEvent event) {
        lblPrintNgayIn.setText(dtf.format(LocalDateTime.now()));

        String keyword = txtTimKiemLog.getText() != null ? txtTimKiemLog.getText().trim() : "";
        if (!keyword.isEmpty()) {
            lblPrintSoTK.setText(keyword);

            try {
                lblPrintTenChuTK.setText(
                        balanceHistoryService.layTenChuTaiKhoan(keyword));
            } catch (SQLException e) {
                lblPrintTenChuTK.setText("");
                hienThongBaoLoi("Không thể tra cứu chủ tài khoản.", e);
            }
        } else {
            lblPrintSoTK.setText("");
            lblPrintTenChuTK.setText("");
        }

        // Copy current table data to print table
        tblPrintBienDong.setItems(FXCollections.observableArrayList(tblBienDong.getItems()));

        lblPrintNgayTraCuu.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        modalInPhieuDoiSoat.setVisible(true);
        modalInPhieuDoiSoat.setManaged(true);
    }

    @FXML
    public void onXacNhanInPhieuDoiSoat(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText("Đã tạo phiếu đối soát biến động số dư. Tính năng xuất PDF sẽ được cập nhật.");
        alert.showAndWait();
    }

    @FXML
    public void onDongModalInPhieuDoiSoat(ActionEvent event) {
        modalInPhieuDoiSoat.setVisible(false);
        modalInPhieuDoiSoat.setManaged(false);
    }

    // ========== NAVIGATION ==========
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
