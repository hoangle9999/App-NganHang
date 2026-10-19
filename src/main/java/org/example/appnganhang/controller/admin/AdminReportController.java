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
import org.example.appnganhang.controller.service.Impl.BaoCaoServiceImpl;
import org.example.appnganhang.controller.service.intf.BaoCaoService;

import java.io.File;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.example.appnganhang.controller.util.Navigation.moView;

public class AdminReportController {

    @FXML private TabPane tabPaneReport;

    // Filter Bar
    @FXML private DatePicker dpTuNgay;
    @FXML private DatePicker dpDenNgay;
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
    private final BaoCaoService baoCaoService = new BaoCaoServiceImpl();

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
        try {
            cboLoaiGiaoDich.setItems(FXCollections.observableArrayList("Tất cả loại GD"));
            cboLoaiGiaoDich.getItems().addAll(baoCaoService.layDanhSachLoaiGiaoDich());
            cboLoaiGiaoDich.setValue("Tất cả loại GD");
            cboNganHang.setItems(FXCollections.observableArrayList("Tất cả ngân hàng"));
            cboNganHang.getItems().addAll(baoCaoService.layDanhSachNganHang());
            cboNganHang.setValue("Tất cả ngân hàng");
        } catch (RuntimeException e) {
            hienCanhBao("Lỗi tải bộ lọc báo cáo", e.getMessage());
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
        try {
            danhSachGiaoDich.setAll(baoCaoService.layDanhSachGiaoDich());
            danhSachHienThiGD.setAll(danhSachGiaoDich);
        } catch (RuntimeException e) {
            danhSachGiaoDich.clear();
            danhSachHienThiGD.clear();
            hienCanhBao("Lỗi tải giao dịch", e.getMessage());
        }
    }

    private void taiDuLieuAuditLog() {
        try {
            danhSachAuditLog.setAll(baoCaoService.layDanhSachLichSuGiaoDich());
            danhSachHienThiAudit.setAll(danhSachAuditLog);
        } catch (RuntimeException e) {
            danhSachAuditLog.clear();
            danhSachHienThiAudit.clear();
            hienCanhBao("Lỗi tải lịch sử kiểm toán", e.getMessage());
        }
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
        String nganHang = cboNganHang != null ? cboNganHang.getValue() : "Tất cả ngân hàng";
        LocalDate tuNgay = dpTuNgay.getValue();
        LocalDate denNgay = dpDenNgay.getValue();
        if (tuNgay != null && denNgay != null && tuNgay.isAfter(denNgay)) {
            hienCanhBao("Khoảng ngày không hợp lệ", "Ngày bắt đầu phải nhỏ hơn hoặc bằng ngày kết thúc.");
            return;
        }

        danhSachHienThiGD.clear();
        for (GiaoDichChuyenTien gd : danhSachGiaoDich) {
            boolean matchKw = keyword.isEmpty()
                    || (gd.getMaGiaoDich() != null && gd.getMaGiaoDich().toLowerCase().contains(keyword))
                    || (gd.getMaTaiKhoanNguon() != null && gd.getMaTaiKhoanNguon().toLowerCase().contains(keyword))
                    || (gd.getMaTaiKhoanDich() != null && gd.getMaTaiKhoanDich().toLowerCase().contains(keyword));

            boolean matchLoai = loaiGD == null || loaiGD.equals("Tất cả loại GD")
                    || (gd.getMaLoaiGD() != null && gd.getMaLoaiGD().equalsIgnoreCase(loaiGD));
            boolean matchNganHang = nganHang == null || nganHang.equals("Tất cả ngân hàng")
                    || nganHang.equalsIgnoreCase(gd.getTenNganHangDich());
            LocalDateTime ngayGiaoDich = gd.getNgayGiaoDich();
            boolean matchNgay = ngayGiaoDich == null ? tuNgay == null && denNgay == null
                    : (tuNgay == null || !ngayGiaoDich.toLocalDate().isBefore(tuNgay))
                    && (denNgay == null || !ngayGiaoDich.toLocalDate().isAfter(denNgay));

            if (matchKw && matchLoai && matchNganHang && matchNgay) {
                danhSachHienThiGD.add(gd);
            }
        }

        danhSachHienThiAudit.clear();
        for (LichSuGiaoDich ls : danhSachAuditLog) {
            LocalDateTime thoiGian = ls.getThoiGianGhiLog();
            boolean matchNgay = thoiGian == null ? tuNgay == null && denNgay == null
                    : (tuNgay == null || !thoiGian.toLocalDate().isBefore(tuNgay))
                    && (denNgay == null || !thoiGian.toLocalDate().isAfter(denNgay));
            if (matchNgay && (keyword.isEmpty()
                    || (ls.getMaGiaoDich() != null && ls.getMaGiaoDich().toLowerCase().contains(keyword))
                    || (ls.getSoTaiKhoanLienQuan() != null && ls.getSoTaiKhoanLienQuan().toLowerCase().contains(keyword)))) {
                danhSachHienThiAudit.add(ls);
            }
        }

        capNhatTheChiSo();
    }

    @FXML
    private void onLamMoi() {
        if (dpTuNgay != null) dpTuNgay.setValue(null);
        if (dpDenNgay != null) dpDenNgay.setValue(null);
        if (txtTimKiemGD != null) txtTimKiemGD.clear();
        if (cboLoaiGiaoDich != null) cboLoaiGiaoDich.setValue("Tất cả loại GD");
        if (cboNganHang != null) cboNganHang.setValue("Tất cả ngân hàng");

        taiDuLieuGiaoDich();
        taiDuLieuAuditLog();
        cauHinhBoLoc();
        onLocDuLieu();
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

    private static String safe(String value) {
        return value == null ? "" : value;
    }

    private void hienCanhBao(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message == null ? "Đã xảy ra lỗi khi tải dữ liệu." : message);
        alert.showAndWait();
    }

    // =========================================================
    // SIDEBAR NAVIGATION
    // =========================================================
    @FXML
    private void onTrangChu(ActionEvent event) {
        moView("admin-dashboard-view.fxml", event);
    }

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
