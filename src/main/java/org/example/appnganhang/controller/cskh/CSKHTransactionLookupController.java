package org.example.appnganhang.controller.cskh;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import org.example.appnganhang.model.entity.GiaoDichChuyenTien;
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

import static org.example.appnganhang.controller.util.Navigation.moView;

public class CSKHTransactionLookupController {

    // ===== Sidebar & Header =====
    @FXML
    private Label lblStaffName;

    @FXML
    private Label lblTongGiaoDich;

    // ===== Search & Filter =====
    @FXML
    private TextField txtTimKiemGD;

    @FXML
    private DatePicker dpTuNgay;

    @FXML
    private DatePicker dpDenNgay;

    @FXML
    private ComboBox<String> cboLoaiGiaoDich;

    @FXML
    private ComboBox<String> cboTrangThai;

    // ===== Table =====
    @FXML
    private TableView<GiaoDichChuyenTien> tblGiaoDich;

    @FXML
    private TableColumn<GiaoDichChuyenTien, String> colMaGD;

    @FXML
    private TableColumn<GiaoDichChuyenTien, String> colTKSource;

    @FXML
    private TableColumn<GiaoDichChuyenTien, String> colTKDest;

    @FXML
    private TableColumn<GiaoDichChuyenTien, String> colSoTien;

    @FXML
    private TableColumn<GiaoDichChuyenTien, String> colNgayGD;

    @FXML
    private TableColumn<GiaoDichChuyenTien, String> colLoaiGD;

    @FXML
    private TableColumn<GiaoDichChuyenTien, String> colTrangThai;

    @FXML
    private TableColumn<GiaoDichChuyenTien, Void> colThaoTac;

    // ===== Modal Chi tiết GD =====
    @FXML
    private StackPane modalChiTietGiaoDich;

    @FXML
    private Label lblModalTrangThaiGD;

    @FXML
    private Label lblModalSoTien;

    @FXML
    private Label lblModalMaGD;

    @FXML
    private Label lblModalTKSource;

    @FXML
    private Label lblModalTKDest;

    @FXML
    private Label lblModalLoaiGD;

    @FXML
    private Label lblModalNgayGD;

    @FXML
    private Label lblModalNoiDung;

    // ===== Modal In Phiếu =====
    @FXML
    private StackPane modalInPhieuGiaoDich;

    @FXML
    private Label lblPrintNgayIn;

    @FXML
    private Label lblPrintMaGD;

    @FXML
    private Label lblPrintThoiGian;

    @FXML
    private Label lblPrintLoaiGD;

    @FXML
    private Label lblPrintTKSource;

    @FXML
    private Label lblPrintTKDest;

    @FXML
    private Label lblPrintSoTien;

    @FXML
    private Label lblPrintNoiDung;

    @FXML
    private Label lblPrintTrangThai;

    @FXML
    private Label lblPrintNguoiLap;

    // ===== Data =====
    private ObservableList<GiaoDichChuyenTien> danhSachGD;
    private GiaoDichChuyenTien giaoDichDangChon;
    private final DecimalFormat dfTien = new DecimalFormat("#,### VNĐ");
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @FXML
    public void initialize() {
        // Setup columns
        colMaGD.setCellValueFactory(new PropertyValueFactory<>("maGiaoDich"));
        colTKSource.setCellValueFactory(new PropertyValueFactory<>("maTaiKhoanNguon"));
        colTKDest.setCellValueFactory(new PropertyValueFactory<>("maTaiKhoanDich"));

        // Custom: format tiền
        colSoTien.setCellValueFactory(cellData -> {
            BigDecimal soTien = cellData.getValue().getSoTien();
            String formatted = soTien != null ? dfTien.format(soTien) : "0 VNĐ";
            return new javafx.beans.property.SimpleStringProperty(formatted);
        });

        // Custom: format ngày giao dịch
        colNgayGD.setCellValueFactory(cellData -> {
            LocalDateTime ngayGD = cellData.getValue().getNgayGiaoDich();
            String formatted = ngayGD != null ? dtf.format(ngayGD) : "";
            return new javafx.beans.property.SimpleStringProperty(formatted);
        });

        // Custom: lookup loại giao dịch
        colLoaiGD.setCellValueFactory(cellData -> {
            String maLoaiGD = cellData.getValue().getMaLoaiGD();
            String tenLoai = lookupTenLoaiGD(maLoaiGD);
            return new javafx.beans.property.SimpleStringProperty(tenLoai);
        });

        // Custom: trạng thái with colored labels
        colTrangThai.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }
                GiaoDichChuyenTien gd = getTableRow().getItem();
                String trangThai = gd.getTrangThai();
                Label label = new Label(trangThai != null ? trangThai : "");
                label.setPadding(new Insets(4, 12, 4, 12));
                label.setStyle(getStyleForTrangThai(trangThai));
                setGraphic(label);
                setText(null);
                setAlignment(Pos.CENTER);
            }
        });
        colTrangThai.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTrangThai()));

        // Action column: button 'Chi tiết'
        colThaoTac.setCellFactory(column -> new TableCell<>() {
            private final Button btnChiTiet = new Button("Chi tiết");
            private final HBox hBox = new HBox(btnChiTiet);

            {
                btnChiTiet.setStyle("-fx-background-color: #ECEBFA; -fx-text-fill: #5148C9; " +
                        "-fx-cursor: hand; -fx-background-radius: 5; -fx-font-weight: bold;");
                btnChiTiet.setOnAction(event -> {
                    GiaoDichChuyenTien gd = getTableView().getItems().get(getIndex());
                    moPopupChiTietGiaoDich(gd);
                });
                hBox.setAlignment(Pos.CENTER);
                hBox.setSpacing(5);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(hBox);
                }
            }
        });

        // Load all transactions
        taiDanhSachGiaoDich();
    }

    /**
     * Load toàn bộ giao dịch từ database
     */
    private void taiDanhSachGiaoDich() {
        danhSachGD = FXCollections.observableArrayList();
        String sql = "SELECT gd.MaGiaoDich, gd.MaTaiKhoanNguon, gd.MaTaiKhoanDich, gd.SoTien, " +
                "gd.NgayGiaoDich, gd.NoiDung, gd.MaLoaiGD, gd.TrangThai " +
                "FROM GiaoDichChuyenTien gd ORDER BY gd.NgayGiaoDich DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                GiaoDichChuyenTien gd = new GiaoDichChuyenTien();
                gd.setMaGiaoDich(rs.getString("MaGiaoDich"));
                gd.setMaTaiKhoanNguon(rs.getString("MaTaiKhoanNguon"));
                gd.setMaTaiKhoanDich(rs.getString("MaTaiKhoanDich"));
                gd.setSoTien(rs.getBigDecimal("SoTien"));
                gd.setNgayGiaoDich(rs.getTimestamp("NgayGiaoDich") != null
                        ? rs.getTimestamp("NgayGiaoDich").toLocalDateTime() : null);
                gd.setNoiDung(rs.getString("NoiDung"));
                gd.setMaLoaiGD(rs.getString("MaLoaiGD"));
                gd.setTrangThai(rs.getString("TrangThai"));
                danhSachGD.add(gd);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        tblGiaoDich.setItems(danhSachGD);
        lblTongGiaoDich.setText(String.valueOf(danhSachGD.size()));
    }

    /**
     * Tìm kiếm giao dịch theo bộ lọc
     */
    @FXML
    private void onTimKiemGiaoDich(ActionEvent event) {
        danhSachGD = FXCollections.observableArrayList();

        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT gd.MaGiaoDich, gd.MaTaiKhoanNguon, gd.MaTaiKhoanDich, gd.SoTien, ");
        sqlBuilder.append("gd.NgayGiaoDich, gd.NoiDung, gd.MaLoaiGD, gd.TrangThai ");
        sqlBuilder.append("FROM GiaoDichChuyenTien gd WHERE 1=1 ");

        ArrayList<Object> params = new ArrayList<>();

        // Filter by keyword
        String keyword = txtTimKiemGD.getText();
        if (keyword != null && !keyword.trim().isEmpty()) {
            sqlBuilder.append("AND (gd.MaGiaoDich LIKE ? OR gd.MaTaiKhoanNguon LIKE ? OR gd.MaTaiKhoanDich LIKE ?) ");
            String likeParam = "%" + keyword.trim() + "%";
            params.add(likeParam);
            params.add(likeParam);
            params.add(likeParam);
        }

        // Filter by start date
        LocalDate tuNgay = dpTuNgay.getValue();
        if (tuNgay != null) {
            sqlBuilder.append("AND gd.NgayGiaoDich >= ? ");
            params.add(java.sql.Timestamp.valueOf(tuNgay.atStartOfDay()));
        }

        // Filter by end date (inclusive, add 1 day)
        LocalDate denNgay = dpDenNgay.getValue();
        if (denNgay != null) {
            sqlBuilder.append("AND gd.NgayGiaoDich <= ? ");
            params.add(java.sql.Timestamp.valueOf(denNgay.plusDays(1).atStartOfDay()));
        }

        // Filter by loại giao dịch
        String loaiGD = cboLoaiGiaoDich.getValue();
        if (loaiGD != null && !loaiGD.equals("Tất cả loại GD")) {
            String maLoaiGD = lookupMaLoaiGD(loaiGD);
            if (maLoaiGD != null) {
                sqlBuilder.append("AND gd.MaLoaiGD = ? ");
                params.add(maLoaiGD);
            }
        }

        // Filter by trạng thái
        String trangThai = cboTrangThai.getValue();
        if (trangThai != null && !trangThai.equals("Tất cả trạng thái")) {
            sqlBuilder.append("AND gd.TrangThai = ? ");
            params.add(trangThai);
        }

        sqlBuilder.append("ORDER BY gd.NgayGiaoDich DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlBuilder.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    GiaoDichChuyenTien gd = new GiaoDichChuyenTien();
                    gd.setMaGiaoDich(rs.getString("MaGiaoDich"));
                    gd.setMaTaiKhoanNguon(rs.getString("MaTaiKhoanNguon"));
                    gd.setMaTaiKhoanDich(rs.getString("MaTaiKhoanDich"));
                    gd.setSoTien(rs.getBigDecimal("SoTien"));
                    gd.setNgayGiaoDich(rs.getTimestamp("NgayGiaoDich") != null
                            ? rs.getTimestamp("NgayGiaoDich").toLocalDateTime() : null);
                    gd.setNoiDung(rs.getString("NoiDung"));
                    gd.setMaLoaiGD(rs.getString("MaLoaiGD"));
                    gd.setTrangThai(rs.getString("TrangThai"));
                    danhSachGD.add(gd);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        tblGiaoDich.setItems(danhSachGD);
        lblTongGiaoDich.setText(String.valueOf(danhSachGD.size()));
    }

    /**
     * Đặt lại tất cả bộ lọc và tải lại danh sách
     */
    @FXML
    private void onDatLaiBoLoc(ActionEvent event) {
        txtTimKiemGD.clear();
        dpTuNgay.setValue(null);
        dpDenNgay.setValue(null);
        cboLoaiGiaoDich.getSelectionModel().clearSelection();
        cboTrangThai.getSelectionModel().clearSelection();
        taiDanhSachGiaoDich();
    }

    // ===== Modal Chi tiết Giao dịch =====

    /**
     * Mở popup chi tiết từ nút trên bảng
     */
    private void moPopupChiTietGiaoDich(GiaoDichChuyenTien gd) {
        giaoDichDangChon = gd;

        lblModalMaGD.setText(gd.getMaGiaoDich() != null ? gd.getMaGiaoDich() : "");
        lblModalTKSource.setText(gd.getMaTaiKhoanNguon() != null ? gd.getMaTaiKhoanNguon() : "");
        lblModalTKDest.setText(gd.getMaTaiKhoanDich() != null ? gd.getMaTaiKhoanDich() : "");
        lblModalSoTien.setText(gd.getSoTien() != null ? dfTien.format(gd.getSoTien()) : "0 VNĐ");
        lblModalNgayGD.setText(gd.getNgayGiaoDich() != null ? dtf.format(gd.getNgayGiaoDich()) : "");
        lblModalLoaiGD.setText(lookupTenLoaiGD(gd.getMaLoaiGD()));
        lblModalNoiDung.setText(gd.getNoiDung() != null ? gd.getNoiDung() : "");

        // Trạng thái with color
        String trangThai = gd.getTrangThai();
        lblModalTrangThaiGD.setText(trangThai != null ? trangThai : "");
        lblModalTrangThaiGD.setStyle(getStyleForTrangThai(trangThai));

        modalChiTietGiaoDich.setVisible(true);
        modalChiTietGiaoDich.setManaged(true);
    }

    /**
     * Mở popup chi tiết từ ActionEvent (nếu có selected row)
     */
    @FXML
    private void onMoPopupChiTietGiaoDich(ActionEvent event) {
        GiaoDichChuyenTien selected = tblGiaoDich.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Vui lòng chọn giao dịch cần xem chi tiết.");
            return;
        }
        moPopupChiTietGiaoDich(selected);
    }

    @FXML
    private void onDongPopupChiTiet(ActionEvent event) {
        modalChiTietGiaoDich.setVisible(false);
        modalChiTietGiaoDich.setManaged(false);
    }

    // ===== Modal In Phiếu =====

    @FXML
    private void onMoModalInPhieu(ActionEvent event) {
        if (giaoDichDangChon == null) {
            return;
        }

        lblPrintMaGD.setText(giaoDichDangChon.getMaGiaoDich() != null ? giaoDichDangChon.getMaGiaoDich() : "");
        lblPrintThoiGian.setText(giaoDichDangChon.getNgayGiaoDich() != null
                ? dtf.format(giaoDichDangChon.getNgayGiaoDich()) : "");
        lblPrintLoaiGD.setText(lookupTenLoaiGD(giaoDichDangChon.getMaLoaiGD()));
        lblPrintTKSource.setText(giaoDichDangChon.getMaTaiKhoanNguon() != null
                ? giaoDichDangChon.getMaTaiKhoanNguon() : "");
        lblPrintTKDest.setText(giaoDichDangChon.getMaTaiKhoanDich() != null
                ? giaoDichDangChon.getMaTaiKhoanDich() : "");
        lblPrintSoTien.setText(giaoDichDangChon.getSoTien() != null
                ? dfTien.format(giaoDichDangChon.getSoTien()) : "0 VNĐ");
        lblPrintNoiDung.setText(giaoDichDangChon.getNoiDung() != null ? giaoDichDangChon.getNoiDung() : "");
        lblPrintTrangThai.setText(giaoDichDangChon.getTrangThai() != null ? giaoDichDangChon.getTrangThai() : "");
        lblPrintNgayIn.setText(dtf.format(LocalDateTime.now()));
        lblPrintNguoiLap.setText(lblStaffName.getText() != null ? lblStaffName.getText() : "");

        modalInPhieuGiaoDich.setVisible(true);
        modalInPhieuGiaoDich.setManaged(true);
    }

    @FXML
    private void onXacNhanInPhieu(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION,
                "Đã tạo phiếu xác nhận giao dịch. Tính năng xuất PDF sẽ được cập nhật.");
    }

    @FXML
    private void onDongModalInPhieu(ActionEvent event) {
        modalInPhieuGiaoDich.setVisible(false);
        modalInPhieuGiaoDich.setManaged(false);
    }

    // ===== Navigation =====

    @FXML
    private void onDashboard(ActionEvent event) {
        moView("cskh-dashboard-view.fxml", event);
    }

    @FXML
    private void onKhachHang(ActionEvent event) {
        moView("cskh-customer-view.fxml", event);
    }

    @FXML
    private void onTraCuuTaiKhoan(ActionEvent event) {
        moView("cskh-account-lookup-view.fxml", event);
    }

    @FXML
    private void onTraCuuGiaoDich(ActionEvent event) {
        moView("cskh-transaction-lookup-view.fxml", event);
    }

    @FXML
    private void onDoiSoatBienDong(ActionEvent event) {
        moView("cskh-balance-history-view.fxml", event);
    }

    @FXML
    private void onDangXuat(ActionEvent event) {
        moView("login-view.fxml", event);
    }

    // ===== Helper Methods =====

    /**
     * Lookup tên loại giao dịch từ mã
     */
    private String lookupTenLoaiGD(String maLoaiGD) {
        if (maLoaiGD == null || maLoaiGD.isEmpty()) {
            return "";
        }
        String tenLoai = maLoaiGD;
        String sql = "SELECT TenLoaiGD FROM LoaiGiaoDich WHERE MaLoaiGD = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maLoaiGD);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    tenLoai = rs.getString("TenLoaiGD");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tenLoai;
    }

    /**
     * Lookup mã loại giao dịch từ tên
     */
    private String lookupMaLoaiGD(String tenLoaiGD) {
        if (tenLoaiGD == null || tenLoaiGD.isEmpty()) {
            return null;
        }
        String sql = "SELECT MaLoaiGD FROM LoaiGiaoDich WHERE TenLoaiGD = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenLoaiGD);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("MaLoaiGD");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Trả về style CSS cho trạng thái giao dịch
     */
    private String getStyleForTrangThai(String trangThai) {
        if (trangThai == null) {
            return "";
        }
        return switch (trangThai) {
            case "Thành công" -> "-fx-text-fill: #22C55E; -fx-background-color: #E8F8F0; " +
                    "-fx-background-radius: 5; -fx-padding: 4 12;";
            case "Chờ xử lý" -> "-fx-text-fill: #D97706; -fx-background-color: #FEF3C7; " +
                    "-fx-background-radius: 5; -fx-padding: 4 12;";
            case "Thất bại" -> "-fx-text-fill: #EF4444; -fx-background-color: #FDECEC; " +
                    "-fx-background-radius: 5; -fx-padding: 4 12;";
            case "Đã hủy" -> "-fx-text-fill: #64748B; -fx-background-color: #F1F5F9; " +
                    "-fx-background-radius: 5; -fx-padding: 4 12;";
            default -> "";
        };
    }

    /**
     * Hiển thị thông báo
     */
    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
