package org.example.appnganhang.controller.cskh;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.example.appnganhang.model.entity.LichSuGiaoDich;
import org.example.appnganhang.model.util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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
    private final DecimalFormat dfTien = new DecimalFormat("#,### VNĐ");
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @FXML
    public void initialize() {
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
        danhSachLog = FXCollections.observableArrayList();
        String sql = "SELECT MaLog, MaGiaoDich, SoTaiKhoanLienQuan, HanhDong, SoTienThayDoi, SoDuSauThayDoi, TrangThai, ThoiGianGhiLog " +
                "FROM LichSuGiaoDich ORDER BY ThoiGianGhiLog DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                LichSuGiaoDich log = new LichSuGiaoDich();
                log.setMaLog(rs.getInt("MaLog"));
                log.setMaGiaoDich(rs.getString("MaGiaoDich"));
                log.setSoTaiKhoanLienQuan(rs.getString("SoTaiKhoanLienQuan"));
                log.setHanhDong(rs.getString("HanhDong"));
                log.setSoTienThayDoi(rs.getBigDecimal("SoTienThayDoi"));
                log.setSoDuSauThayDoi(rs.getBigDecimal("SoDuSauThayDoi"));
                log.setTrangThai(rs.getString("TrangThai"));
                log.setThoiGianGhiLog(rs.getTimestamp("ThoiGianGhiLog") != null
                        ? rs.getTimestamp("ThoiGianGhiLog").toLocalDateTime() : null);
                danhSachLog.add(log);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        tblBienDong.setItems(danhSachLog);
        lblTongLog.setText(String.valueOf(danhSachLog.size()));
    }

    @FXML
    public void onTraCuuLog(ActionEvent event) {
        danhSachLog = FXCollections.observableArrayList();
        StringBuilder sqlBuilder = new StringBuilder(
                "SELECT MaLog, MaGiaoDich, SoTaiKhoanLienQuan, HanhDong, SoTienThayDoi, SoDuSauThayDoi, TrangThai, ThoiGianGhiLog FROM LichSuGiaoDich");

        List<Object> params = new ArrayList<>();
        boolean hasWhere = false;

        String keyword = txtTimKiemLog.getText() != null ? txtTimKiemLog.getText().trim() : "";
        if (!keyword.isEmpty()) {
            sqlBuilder.append(" WHERE (SoTaiKhoanLienQuan LIKE ? OR MaGiaoDich LIKE ?)");
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");
            hasWhere = true;
        }

        LocalDate tuNgay = dpLogTuNgay.getValue();
        if (tuNgay != null) {
            sqlBuilder.append(hasWhere ? " AND" : " WHERE");
            sqlBuilder.append(" ThoiGianGhiLog >= ?");
            params.add(java.sql.Timestamp.valueOf(tuNgay.atStartOfDay()));
            hasWhere = true;
        }

        LocalDate denNgay = dpLogDenNgay.getValue();
        if (denNgay != null) {
            sqlBuilder.append(hasWhere ? " AND" : " WHERE");
            sqlBuilder.append(" ThoiGianGhiLog <= ?");
            params.add(java.sql.Timestamp.valueOf(denNgay.atTime(23, 59, 59)));
        }

        sqlBuilder.append(" ORDER BY ThoiGianGhiLog DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlBuilder.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LichSuGiaoDich log = new LichSuGiaoDich();
                    log.setMaLog(rs.getInt("MaLog"));
                    log.setMaGiaoDich(rs.getString("MaGiaoDich"));
                    log.setSoTaiKhoanLienQuan(rs.getString("SoTaiKhoanLienQuan"));
                    log.setHanhDong(rs.getString("HanhDong"));
                    log.setSoTienThayDoi(rs.getBigDecimal("SoTienThayDoi"));
                    log.setSoDuSauThayDoi(rs.getBigDecimal("SoDuSauThayDoi"));
                    log.setTrangThai(rs.getString("TrangThai"));
                    log.setThoiGianGhiLog(rs.getTimestamp("ThoiGianGhiLog") != null
                            ? rs.getTimestamp("ThoiGianGhiLog").toLocalDateTime() : null);
                    danhSachLog.add(log);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        tblBienDong.setItems(danhSachLog);
        lblTongLog.setText(String.valueOf(danhSachLog.size()));
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

            String sqlLookup = "SELECT TOP 1 kh.HoTen FROM TaiKhoanNganHang tk " +
                    "JOIN KhachHang kh ON tk.MaKhachHang = kh.MaKhachHang " +
                    "WHERE tk.SoTaiKhoan = ?";

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sqlLookup)) {

                ps.setString(1, keyword);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        lblPrintTenChuTK.setText(rs.getString("HoTen"));
                    } else {
                        lblPrintTenChuTK.setText("");
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                lblPrintTenChuTK.setText("");
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
