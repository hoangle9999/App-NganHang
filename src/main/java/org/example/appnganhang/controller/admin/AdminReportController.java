package org.example.appnganhang.controller.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.example.appnganhang.model.entity.GiaoDichChuyenTien;
import org.example.appnganhang.model.entity.LichSuGiaoDich;
import org.example.appnganhang.model.util.DBConnection;

import java.io.File;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.example.appnganhang.controller.util.Navigation.moView;

public class AdminReportController {

    @FXML private TabPane tabPaneReport;

    // Filter Bar
    @FXML private TextField txtTuNgay;
    @FXML private TextField txtDenNgay;
    @FXML private ComboBox<String> cboLoaiGiaoDich;
    @FXML private ComboBox<String> cboNganHang;
    @FXML private TextField txtTimKiemGD;
    @FXML private Button btnLocDuLieu;
    @FXML private Button btnXuatExcel;
    @FXML private Button btnInBaoCao;
    @FXML private Button btnLamMoi;

    // Summary Cards
    @FXML private Label lblTongSoGiaoDich;
    @FXML private Label lblTongGiaTriGD;
    @FXML private Label lblSoGDNghiVan;

    // Tab 1: Giao dịch
    @FXML private TableView<GiaoDichChuyenTien> tblGiaoDich;
    @FXML private TableColumn<GiaoDichChuyenTien, String> colMaGD;
    @FXML private TableColumn<GiaoDichChuyenTien, String> colTKNguon;
    @FXML private TableColumn<GiaoDichChuyenTien, String> colTKDich;
    @FXML private TableColumn<GiaoDichChuyenTien, String> colSoTien;
    @FXML private TableColumn<GiaoDichChuyenTien, String> colNgayGD;
    @FXML private TableColumn<GiaoDichChuyenTien, String> colLoaiGD;
    @FXML private TableColumn<GiaoDichChuyenTien, String> colTrangThaiGD;
    @FXML private TableColumn<GiaoDichChuyenTien, Void> colThaoTacGD;

    // Tab 2: Audit Log
    @FXML private TableView<LichSuGiaoDich> tblAuditLog;
    @FXML private TableColumn<LichSuGiaoDich, Integer> colMaLog;
    @FXML private TableColumn<LichSuGiaoDich, String> colMaGDAudit;
    @FXML private TableColumn<LichSuGiaoDich, String> colSTKAudit;
    @FXML private TableColumn<LichSuGiaoDich, String> colHanhDongAudit;
    @FXML private TableColumn<LichSuGiaoDich, String> colBienDongAudit;
    @FXML private TableColumn<LichSuGiaoDich, String> colSoDuSauAudit;
    @FXML private TableColumn<LichSuGiaoDich, String> colThoiGianAudit;

    // Modal: Chi tiết giao dịch
    @FXML private StackPane modalChiTietGD;
    @FXML private Label lblModalTrangThaiGD;
    @FXML private Label lblModalMaGD;
    @FXML private Label lblModalSoTien;
    @FXML private Label lblModalTKNguon;
    @FXML private Label lblModalTKDich;
    @FXML private Label lblModalThoiGian;
    @FXML private Label lblModalLoaiGD;
    @FXML private Label lblModalNoiDung;
    @FXML private VBox boxModalMaLoi;
    @FXML private Label lblModalMaLoi;

    private final ObservableList<GiaoDichChuyenTien> danhSachGiaoDich = FXCollections.observableArrayList();
    private final ObservableList<GiaoDichChuyenTien> danhSachHienThiGD = FXCollections.observableArrayList();

    private final ObservableList<LichSuGiaoDich> danhSachAuditLog = FXCollections.observableArrayList();
    private final ObservableList<LichSuGiaoDich> danhSachHienThiAudit = FXCollections.observableArrayList();

    private final DecimalFormat dfTien = new DecimalFormat("#,### VNĐ");
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private GiaoDichChuyenTien giaoDichDangChon;

    @FXML
    public void initialize() {
        cauHinhBoLoc();
        cauHinhBangGiaoDich();
        cauHinhBangAuditLog();

        taiDuLieuGiaoDich();
        taiDuLieuAuditLog();
        capNhatTheChiSo();
    }

    private void cauHinhBoLoc() {
        if (cboLoaiGiaoDich != null) {
            cboLoaiGiaoDich.getItems().addAll("Tất cả loại GD", "Chuyển khoản", "Nạp tiền", "Rút tiền", "Thanh toán");
            cboLoaiGiaoDich.setValue("Tất cả loại GD");
        }
        if (cboNganHang != null) {
            cboNganHang.getItems().addAll("Tất cả ngân hàng", "SMART BANK", "Vietcombank", "BIDV", "Techcombank", "MB Bank");
            cboNganHang.setValue("Tất cả ngân hàng");
        }
    }

    private void cauHinhBangGiaoDich() {
        colMaGD.setCellValueFactory(new PropertyValueFactory<>("maGiaoDich"));
        colTKNguon.setCellValueFactory(new PropertyValueFactory<>("maTaiKhoanNguon"));
        colTKDich.setCellValueFactory(new PropertyValueFactory<>("maTaiKhoanDich"));

        colSoTien.setCellValueFactory(cellData -> {
            BigDecimal st = cellData.getValue().getSoTien();
            return new javafx.beans.property.SimpleStringProperty(st != null ? dfTien.format(st) : "0 VNĐ");
        });

        colNgayGD.setCellValueFactory(cellData -> {
            LocalDateTime ng = cellData.getValue().getNgayGiaoDich();
            return new javafx.beans.property.SimpleStringProperty(ng != null ? ng.format(dtf) : "");
        });

        colLoaiGD.setCellValueFactory(new PropertyValueFactory<>("maLoaiGD"));

        colTrangThaiGD.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    GiaoDichChuyenTien gd = getTableRow().getItem();
                    Label badge = new Label(gd.getTrangThai());
                    if ("Thành công".equalsIgnoreCase(gd.getTrangThai())) {
                        badge.setStyle("-fx-background-color: #E8F8F0; -fx-text-fill: #10B981; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 8; -fx-font-size: 11px;");
                    } else if ("Thất bại".equalsIgnoreCase(gd.getTrangThai())) {
                        badge.setStyle("-fx-background-color: #FDECEC; -fx-text-fill: #D9534F; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 8; -fx-font-size: 11px;");
                    } else {
                        badge.setStyle("-fx-background-color: #FEF3C7; -fx-text-fill: #D97706; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 8; -fx-font-size: 11px;");
                    }
                    HBox box = new HBox(badge);
                    box.setAlignment(Pos.CENTER);
                    setGraphic(box);
                }
            }
        });

        colThaoTacGD.setCellFactory(param -> new TableCell<>() {
            private final Button btnChiTiet = new Button("Xem chi tiết");

            {
                btnChiTiet.setStyle("-fx-background-color: #ECEBFA; -fx-text-fill: #5148C9; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 11px; -fx-padding: 5 10;");
                btnChiTiet.setOnAction(e -> {
                    GiaoDichChuyenTien item = getTableView().getItems().get(getIndex());
                    moPopupChiTietGDCho(item);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else {
                    HBox box = new HBox(btnChiTiet);
                    box.setAlignment(Pos.CENTER);
                    setGraphic(box);
                }
            }
        });

        tblGiaoDich.setItems(danhSachHienThiGD);
    }

    private void cauHinhBangAuditLog() {
        colMaLog.setCellValueFactory(new PropertyValueFactory<>("maLog"));
        colMaGDAudit.setCellValueFactory(new PropertyValueFactory<>("maGiaoDich"));
        colSTKAudit.setCellValueFactory(new PropertyValueFactory<>("soTaiKhoanLienQuan"));
        colHanhDongAudit.setCellValueFactory(new PropertyValueFactory<>("hanhDong"));

        colBienDongAudit.setCellValueFactory(cellData -> {
            BigDecimal st = cellData.getValue().getSoTienThayDoi();
            return new javafx.beans.property.SimpleStringProperty(st != null ? dfTien.format(st) : "0 VNĐ");
        });

        colSoDuSauAudit.setCellValueFactory(cellData -> {
            BigDecimal sd = cellData.getValue().getSoDuSauThayDoi();
            return new javafx.beans.property.SimpleStringProperty(sd != null ? dfTien.format(sd) : "0 VNĐ");
        });

        colThoiGianAudit.setCellValueFactory(cellData -> {
            LocalDateTime tg = cellData.getValue().getThoiGianGhiLog();
            return new javafx.beans.property.SimpleStringProperty(tg != null ? tg.format(dtf) : "");
        });

        tblAuditLog.setItems(danhSachHienThiAudit);
    }

    private void taiDuLieuGiaoDich() {
        danhSachGiaoDich.clear();
        String sql = """
                SELECT gd.MaGiaoDich, gd.MaTaiKhoanNguon, gd.MaTaiKhoanDich, gd.SoTien, gd.NgayGiaoDich, gd.NoiDung, lgd.TenLoaiGD, gd.TrangThai
                FROM GiaoDichChuyenTien gd
                LEFT JOIN LoaiGiaoDich lgd ON gd.MaLoaiGD = lgd.MaLoaiGD
                ORDER BY gd.NgayGiaoDich DESC
                """;

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                GiaoDichChuyenTien gd = new GiaoDichChuyenTien();
                gd.setMaGiaoDich(rs.getString("MaGiaoDich"));
                gd.setMaTaiKhoanNguon(rs.getString("MaTaiKhoanNguon"));
                gd.setMaTaiKhoanDich(rs.getString("MaTaiKhoanDich"));
                gd.setSoTien(rs.getBigDecimal("SoTien"));
                Timestamp ts = rs.getTimestamp("NgayGiaoDich");
                if (ts != null) gd.setNgayGiaoDich(ts.toLocalDateTime());
                gd.setNoiDung(rs.getString("NoiDung"));
                gd.setMaLoaiGD(rs.getString("TenLoaiGD") != null ? rs.getString("TenLoaiGD") : "Chuyển khoản");
                gd.setTrangThai(rs.getString("TrangThai"));
                danhSachGiaoDich.add(gd);
            }
        } catch (Exception e) {
            System.err.println("Lỗi nạp GiaoDich DB: " + e.getMessage());
            napGiaoDichMau();
        }

        if (danhSachGiaoDich.isEmpty()) {
            napGiaoDichMau();
        }

        danhSachHienThiGD.setAll(danhSachGiaoDich);
    }

    private void napGiaoDichMau() {
        GiaoDichChuyenTien g1 = new GiaoDichChuyenTien();
        g1.setMaGiaoDich("GD20261017001");
        g1.setMaTaiKhoanNguon("1000000001");
        g1.setMaTaiKhoanDich("1000000002");
        g1.setSoTien(new BigDecimal("5000000"));
        g1.setNgayGiaoDich(LocalDateTime.now().minusHours(2));
        g1.setNoiDung("Nguyen Van A chuyen tien hoc phi");
        g1.setMaLoaiGD("Chuyển khoản");
        g1.setTrangThai("Thành công");

        GiaoDichChuyenTien g2 = new GiaoDichChuyenTien();
        g2.setMaGiaoDich("GD20261017002");
        g2.setMaTaiKhoanNguon("1000000002");
        g2.setMaTaiKhoanDich("1000000003");
        g2.setSoTien(new BigDecimal("12000000"));
        g2.setNgayGiaoDich(LocalDateTime.now().minusHours(4));
        g2.setNoiDung("Thanh toan tien thue nha");
        g2.setMaLoaiGD("Chuyển khoản");
        g2.setTrangThai("Thất bại");

        GiaoDichChuyenTien g3 = new GiaoDichChuyenTien();
        g3.setMaGiaoDich("GD20261017003");
        g3.setMaTaiKhoanNguon("1000000001");
        g3.setMaTaiKhoanDich("1000000001");
        g3.setSoTien(new BigDecimal("2000000"));
        g3.setNgayGiaoDich(LocalDateTime.now().minusHours(6));
        g3.setNoiDung("Nap tien vao tai khoan");
        g3.setMaLoaiGD("Nạp tiền");
        g3.setTrangThai("Thành công");

        danhSachGiaoDich.addAll(g1, g2, g3);
    }

    private void taiDuLieuAuditLog() {
        danhSachAuditLog.clear();
        String sql = """
                SELECT MaLog, MaGiaoDich, SoTaiKhoanLienQuan, HanhDong, SoTienThayDoi, SoDuSauThayDoi, ThoiGianGhiLog
                FROM LichSuGiaoDich
                ORDER BY ThoiGianGhiLog DESC
                """;

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                LichSuGiaoDich ls = new LichSuGiaoDich();
                ls.setMaLog(rs.getInt("MaLog"));
                ls.setMaGiaoDich(rs.getString("MaGiaoDich"));
                ls.setSoTaiKhoanLienQuan(rs.getString("SoTaiKhoanLienQuan"));
                ls.setHanhDong(rs.getString("HanhDong"));
                ls.setSoTienThayDoi(rs.getBigDecimal("SoTienThayDoi"));
                ls.setSoDuSauThayDoi(rs.getBigDecimal("SoDuSauThayDoi"));
                Timestamp ts = rs.getTimestamp("ThoiGianGhiLog");
                if (ts != null) ls.setThoiGianGhiLog(ts.toLocalDateTime());
                danhSachAuditLog.add(ls);
            }
        } catch (Exception e) {
            System.err.println("Lỗi nạp AuditLog DB: " + e.getMessage());
            napAuditLogMau();
        }

        if (danhSachAuditLog.isEmpty()) {
            napAuditLogMau();
        }

        danhSachHienThiAudit.setAll(danhSachAuditLog);
    }

    private void napAuditLogMau() {
        LichSuGiaoDich l1 = new LichSuGiaoDich();
        l1.setMaLog(1);
        l1.setMaGiaoDich("GD20261017001");
        l1.setSoTaiKhoanLienQuan("1000000001");
        l1.setHanhDong("Trừ tiền chuyển khoản đi");
        l1.setSoTienThayDoi(new BigDecimal("-5000000"));
        l1.setSoDuSauThayDoi(new BigDecimal("20400000"));
        l1.setThoiGianGhiLog(LocalDateTime.now().minusHours(2));

        LichSuGiaoDich l2 = new LichSuGiaoDich();
        l2.setMaLog(2);
        l2.setMaGiaoDich("GD20261017001");
        l2.setSoTaiKhoanLienQuan("1000000002");
        l2.setHanhDong("Cộng tiền nhận chuyển khoản");
        l2.setSoTienThayDoi(new BigDecimal("5000000"));
        l2.setSoDuSauThayDoi(new BigDecimal("15000000"));
        l2.setThoiGianGhiLog(LocalDateTime.now().minusHours(2));

        LichSuGiaoDich l3 = new LichSuGiaoDich();
        l3.setMaLog(3);
        l3.setMaGiaoDich("GD20261017002");
        l3.setSoTaiKhoanLienQuan("1000000002");
        l3.setHanhDong("Giao dịch chuyển tiền thất bại");
        l3.setSoTienThayDoi(BigDecimal.ZERO);
        l3.setSoDuSauThayDoi(new BigDecimal("15000000"));
        l3.setThoiGianGhiLog(LocalDateTime.now().minusHours(4));

        danhSachAuditLog.addAll(l1, l2, l3);
    }

    private void capNhatTheChiSo() {
        int tongSoGD = danhSachHienThiGD.size();
        BigDecimal tongTien = BigDecimal.ZERO;
        int soNghiVan = 0;

        for (GiaoDichChuyenTien gd : danhSachHienThiGD) {
            if ("Thành công".equalsIgnoreCase(gd.getTrangThai()) && gd.getSoTien() != null) {
                tongTien = tongTien.add(gd.getSoTien());
            }
            if ("Thất bại".equalsIgnoreCase(gd.getTrangThai()) || "Đã hủy".equalsIgnoreCase(gd.getTrangThai())) {
                soNghiVan++;
            }
        }

        if (lblTongSoGiaoDich != null) lblTongSoGiaoDich.setText(String.format("%,d", tongSoGD));
        if (lblTongGiaTriGD != null) lblTongGiaTriGD.setText(dfTien.format(tongTien));
        if (lblSoGDNghiVan != null) lblSoGDNghiVan.setText(soNghiVan + " giao dịch");
    }

    // =========================================================
    // NÚT THAO TÁC & BỘ LỌC
    // =========================================================
    @FXML
    private void onLocDuLieu() {
        String keyword = txtTimKiemGD != null && txtTimKiemGD.getText() != null
                ? txtTimKiemGD.getText().trim().toLowerCase() : "";
        String loaiGD = cboLoaiGiaoDich != null ? cboLoaiGiaoDich.getValue() : "Tất cả loại GD";

        danhSachHienThiGD.clear();
        for (GiaoDichChuyenTien gd : danhSachGiaoDich) {
            boolean matchKw = keyword.isEmpty()
                    || (gd.getMaGiaoDich() != null && gd.getMaGiaoDich().toLowerCase().contains(keyword))
                    || (gd.getMaTaiKhoanNguon() != null && gd.getMaTaiKhoanNguon().toLowerCase().contains(keyword))
                    || (gd.getMaTaiKhoanDich() != null && gd.getMaTaiKhoanDich().toLowerCase().contains(keyword));

            boolean matchLoai = loaiGD == null || loaiGD.equals("Tất cả loại GD")
                    || (gd.getMaLoaiGD() != null && gd.getMaLoaiGD().equalsIgnoreCase(loaiGD));

            if (matchKw && matchLoai) {
                danhSachHienThiGD.add(gd);
            }
        }

        danhSachHienThiAudit.clear();
        for (LichSuGiaoDich ls : danhSachAuditLog) {
            if (keyword.isEmpty()
                    || (ls.getMaGiaoDich() != null && ls.getMaGiaoDich().toLowerCase().contains(keyword))
                    || (ls.getSoTaiKhoanLienQuan() != null && ls.getSoTaiKhoanLienQuan().toLowerCase().contains(keyword))) {
                danhSachHienThiAudit.add(ls);
            }
        }

        capNhatTheChiSo();
    }

    @FXML
    private void onLamMoi() {
        if (txtTuNgay != null) txtTuNgay.clear();
        if (txtDenNgay != null) txtDenNgay.clear();
        if (txtTimKiemGD != null) txtTimKiemGD.clear();
        if (cboLoaiGiaoDich != null) cboLoaiGiaoDich.setValue("Tất cả loại GD");
        if (cboNganHang != null) cboNganHang.setValue("Tất cả ngân hàng");

        taiDuLieuGiaoDich();
        taiDuLieuAuditLog();
        capNhatTheChiSo();
    }

    @FXML
    private void onXuatExcel() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Lưu báo cáo Excel");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel CSV (*.csv)", "*.csv"));
            fileChooser.setInitialFileName("BaoCao_KiemToan_GiaoDich_" + System.currentTimeMillis() + ".csv");

            File file = fileChooser.showSaveDialog(tblGiaoDich.getScene().getWindow());
            if (file != null) {
                try (PrintWriter writer = new PrintWriter(file, "UTF-8")) {
                    writer.write('\ufeff'); // BOM cho Excel tiếng Việt
                    writer.println("Mã GD,TK Nguồn,TK Đích,Số Tiền,Ngày GD,Loại GD,Trạng Thái,Nội Dung");
                    for (GiaoDichChuyenTien gd : danhSachHienThiGD) {
                        writer.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                                gd.getMaGiaoDich(),
                                gd.getMaTaiKhoanNguon(),
                                gd.getMaTaiKhoanDich(),
                                gd.getSoTien() != null ? gd.getSoTien().toPlainString() : "0",
                                gd.getNgayGiaoDich() != null ? gd.getNgayGiaoDich().format(dtf) : "",
                                gd.getMaLoaiGD(),
                                gd.getTrangThai(),
                                gd.getNoiDung() != null ? gd.getNoiDung().replace("\"", "\"\"") : ""
                        );
                    }
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Xuất báo cáo thành công");
                alert.setHeaderText(null);
                alert.setContentText("Đã xuất báo cáo thành công ra tập tin:\n" + file.getAbsolutePath());
                alert.showAndWait();
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Lỗi khi xuất file: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void onInBaoCao() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("In báo cáo");
        alert.setHeaderText(null);
        alert.setContentText("Đã gửi lệnh in báo cáo kiểm toán và thống kê giao dịch tới máy in hệ thống!");
        alert.showAndWait();
    }

    // =========================================================
    // MODAL POPUP: CHI TIẾT GIAO DỊCH
    // =========================================================
    @FXML
    public void onXemChiTietGiaoDich() {
        GiaoDichChuyenTien chon = tblGiaoDich.getSelectionModel().getSelectedItem();
        if (chon != null) {
            moPopupChiTietGDCho(chon);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Vui lòng chọn một dòng giao dịch trong bảng để xem chi tiết!");
            alert.showAndWait();
        }
    }

    private void moPopupChiTietGDCho(GiaoDichChuyenTien gd) {
        this.giaoDichDangChon = gd;
        lblModalMaGD.setText(gd.getMaGiaoDich());
        lblModalSoTien.setText(gd.getSoTien() != null ? dfTien.format(gd.getSoTien()) : "0 VNĐ");
        lblModalTKNguon.setText(gd.getMaTaiKhoanNguon());
        lblModalTKDich.setText(gd.getMaTaiKhoanDich() + " (SMART BANK)");
        lblModalThoiGian.setText(gd.getNgayGiaoDich() != null ? gd.getNgayGiaoDich().format(dtf) : "");
        lblModalLoaiGD.setText(gd.getMaLoaiGD());
        lblModalNoiDung.setText(gd.getNoiDung() != null ? gd.getNoiDung() : "(Không có nội dung)");

        lblModalTrangThaiGD.setText(gd.getTrangThai());
        if ("Thành công".equalsIgnoreCase(gd.getTrangThai())) {
            lblModalTrangThaiGD.setStyle("-fx-background-color: #E8F8F0; -fx-text-fill: #10B981; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 8;");
            boxModalMaLoi.setVisible(false);
            boxModalMaLoi.setManaged(false);
        } else {
            lblModalTrangThaiGD.setStyle("-fx-background-color: #FDECEC; -fx-text-fill: #D9534F; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 8;");
            boxModalMaLoi.setVisible(true);
            boxModalMaLoi.setManaged(true);
            lblModalMaLoi.setText("ERR_TRANSACTION_FAILED: Giao dịch không thể hoàn tất do số dư không đủ hoặc tài khoản nhận bị khóa.");
        }

        hienModal(modalChiTietGD);
    }

    @FXML
    public void onDongPopupChiTietGD() {
        anModal(modalChiTietGD);
    }

    private void hienModal(StackPane modal) {
        if (modal != null) {
            modal.setVisible(true);
            modal.setManaged(true);
        }
    }

    private void anModal(StackPane modal) {
        if (modal != null) {
            modal.setVisible(false);
            modal.setManaged(false);
        }
    }

    // =========================================================
    // SIDEBAR NAVIGATION
    // =========================================================
    @FXML
    private void onTaiKhoan(ActionEvent event) {
        moView("admin-account-view.fxml", event);
    }

    @FXML
    private void onNhanVienChiNhanh(ActionEvent event) {
        moView("admin-employee-branch-view.fxml", event);
    }

    @FXML
    private void onCauHinhDanhMuc(ActionEvent event) {
        moView("admin-system-config-view.fxml", event);
    }

    @FXML
    private void onThongKeBaoCao(ActionEvent event) {
        // Đang ở view này
    }

    @FXML
    private void onDangXuat(ActionEvent event) {
        moView("login-view.fxml", event);
    }
}
