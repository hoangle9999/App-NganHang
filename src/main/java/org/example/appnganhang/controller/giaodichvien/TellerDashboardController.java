package org.example.appnganhang.controller.giaodichvien;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import org.example.appnganhang.controller.util.StaffHeader;
import org.example.appnganhang.model.entity.GiaoDichChuyenTien;
import org.example.appnganhang.model.entity.TellerDashboardStats;
import org.example.appnganhang.controller.service.Impl.TellerDashboardServiceImpl;
import org.example.appnganhang.controller.service.intf.TellerDashboardService;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.example.appnganhang.controller.util.Navigation.moView;

public class TellerDashboardController {

    @FXML private Label lblTellerName;
    @FXML private Label lblTellerBranch;
    @FXML private Label lblThoiGianCa;

    // Stat cards
    @FXML private Label lblStatTongGD;
    @FXML private Label lblStatTongTien;
    @FXML private Label lblStatChoXuLy;
    @FXML private Label lblStatTaiKhoan;

    // Bảng giao dịch gần đây
    @FXML private TableView<GiaoDichChuyenTien> tblGiaoDichGanDay;
    @FXML private TableColumn<GiaoDichChuyenTien, String> colMaGD;
    @FXML private TableColumn<GiaoDichChuyenTien, String> colTKNguon;
    @FXML private TableColumn<GiaoDichChuyenTien, String> colTKDich;
    @FXML private TableColumn<GiaoDichChuyenTien, String> colLoaiGD;
    @FXML private TableColumn<GiaoDichChuyenTien, String> colSoTien;
    @FXML private TableColumn<GiaoDichChuyenTien, String> colNgayGD;
    @FXML private TableColumn<GiaoDichChuyenTien, String> colTrangThai;

    private final ObservableList<GiaoDichChuyenTien> danhSachGiaoDich = FXCollections.observableArrayList();
    private final TellerDashboardService dashboardService = new TellerDashboardServiceImpl();
    private final DecimalFormat dfTien = new DecimalFormat("#,### VNĐ");
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @FXML
    public void initialize() {
        lblTellerName.setText("Đang tải...");
        lblTellerBranch.setText("Đang tải...");
        taiDuLieuGiaoDichVien();
        if (lblThoiGianCa != null) {
            lblThoiGianCa.setText("Trực tuyến | Hôm nay: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }

        cauHinhBangGiaoDich();
        taiDuLieuThongKe();
        taiDuLieuGiaoDich();
    }

    private void taiDuLieuGiaoDichVien() {
        String username = TellerSession.getTenDangNhap();
        if (!StaffHeader.loadTeller(lblTellerName, lblTellerBranch)) {
            if (username == null || username.isBlank()) {
                hienCanhBao("Phiên đăng nhập không hợp lệ", "Vui lòng đăng nhập lại để tải thông tin giao dịch viên.");
            } else {
                hienCanhBao("Không tìm thấy hồ sơ", "Tài khoản đăng nhập chưa được liên kết với hồ sơ nhân viên.");
            }
        }
    }

    private void cauHinhBangGiaoDich() {
        colMaGD.setCellValueFactory(new PropertyValueFactory<>("maGiaoDich"));
        colTKNguon.setCellValueFactory(new PropertyValueFactory<>("maTaiKhoanNguon"));
        colTKDich.setCellValueFactory(new PropertyValueFactory<>("maTaiKhoanDich"));
        colLoaiGD.setCellValueFactory(new PropertyValueFactory<>("maLoaiGD"));

        colSoTien.setCellValueFactory(cellData -> {
            BigDecimal st = cellData.getValue().getSoTien();
            return new SimpleStringProperty(st != null ? dfTien.format(st) : "0 VNĐ");
        });

        colNgayGD.setCellValueFactory(cellData -> {
            LocalDateTime ngay = cellData.getValue().getNgayGiaoDich();
            return new SimpleStringProperty(ngay != null ? ngay.format(dtf) : "---");
        });

        colTrangThai.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    GiaoDichChuyenTien gd = getTableRow().getItem();
                    String tt = gd.getTrangThai() != null ? gd.getTrangThai() : "Hoàn thành";
                    Label badge = new Label(tt);

                    if (tt.toLowerCase().contains("thành công") || tt.equalsIgnoreCase("HOAN_THANH") || tt.equalsIgnoreCase("Hoàn thành")) {
                        badge.setStyle("-fx-background-color: #E8F8F0; -fx-text-fill: #10B981; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 8; -fx-font-size: 11px;");
                    } else if (tt.toLowerCase().contains("chờ") || tt.equalsIgnoreCase("CHO_XU_LY")) {
                        badge.setStyle("-fx-background-color: #FEF3C7; -fx-text-fill: #D97706; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 8; -fx-font-size: 11px;");
                    } else if (tt.toLowerCase().contains("hủy") || tt.equalsIgnoreCase("DA_HUY")) {
                        badge.setStyle("-fx-background-color: #F3F4F6; -fx-text-fill: #6B7280; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 8; -fx-font-size: 11px;");
                    } else {
                        badge.setStyle("-fx-background-color: #FDECEC; -fx-text-fill: #D9534F; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 8; -fx-font-size: 11px;");
                    }

                    HBox box = new HBox(badge);
                    box.setAlignment(Pos.CENTER);
                    setGraphic(box);
                }
            }
        });

        tblGiaoDichGanDay.setItems(danhSachGiaoDich);
    }

    private void taiDuLieuThongKe() {
        try {
            TellerDashboardStats stats = dashboardService.layThongKeDashboard();
            if (lblStatTongGD != null) lblStatTongGD.setText(stats.getSoGiaoDichHomNay() + " GD");
            if (lblStatTongTien != null) lblStatTongTien.setText(dfTien.format(stats.getTongTienHomNay()));
            if (lblStatChoXuLy != null) lblStatChoXuLy.setText(stats.getSoGiaoDichChoXuLy() + " GD");
            if (lblStatTaiKhoan != null) lblStatTaiKhoan.setText(stats.getSoTaiKhoanKhachHang() + " TK");
        } catch (RuntimeException e) {
            hienCanhBao("Lỗi tải số liệu dashboard", e.getMessage());
            if (lblStatTongGD != null) lblStatTongGD.setText("—");
            if (lblStatTongTien != null) lblStatTongTien.setText("—");
            if (lblStatChoXuLy != null) lblStatChoXuLy.setText("—");
            if (lblStatTaiKhoan != null) lblStatTaiKhoan.setText("—");
        }
    }

    private void taiDuLieuGiaoDich() {
        try {
            danhSachGiaoDich.setAll(dashboardService.layGiaoDichGanDay(30));
        } catch (RuntimeException e) {
            danhSachGiaoDich.clear();
            hienCanhBao("Lỗi tải giao dịch gần đây", e.getMessage());
        }
    }

    @FXML
    public void onLamMoiDashboard() {
        taiDuLieuGiaoDichVien();
        taiDuLieuThongKe();
        taiDuLieuGiaoDich();
    }

    // =========================================================
    // ĐIỀU HƯỚNG TỪ SIDEBAR & QUICK ACTION TILES
    // =========================================================
    @FXML
    public void onTrangChu(ActionEvent event) {
        onLamMoiDashboard();
    }

    @FXML
    public void onChuyenTien(ActionEvent event) {
        moView("teller-transfer-view.fxml", event);
    }

    @FXML
    public void onChuyenTienTile(MouseEvent event) {
        ActionEvent ae = new ActionEvent(event.getSource(), null);
        moView("teller-transfer-view.fxml", ae);
    }

    @FXML
    public void onNapRutThanhToan(ActionEvent event) {
        moView("teller-cash-view.fxml", event);
    }

    @FXML
    public void onNapRutThanhToanTile(MouseEvent event) {
        ActionEvent ae = new ActionEvent(event.getSource(), null);
        moView("teller-cash-view.fxml", ae);
    }

    @FXML
    public void onQuanLyTaiKhoan(ActionEvent event) {
        moView("teller-account-view.fxml", event);
    }

    @FXML
    public void onQuanLyTaiKhoanTile(MouseEvent event) {
        ActionEvent ae = new ActionEvent(event.getSource(), null);
        moView("teller-account-view.fxml", ae);
    }

    @FXML
    public void onTraCuuSaoKe(ActionEvent event) {
        moView("teller-statement-view.fxml", event);
    }

    @FXML
    public void onTraCuuSaoKeTile(MouseEvent event) {
        ActionEvent ae = new ActionEvent(event.getSource(), null);
        moView("teller-statement-view.fxml", ae);
    }

    @FXML
    public void onTheoDoiTrangThai(ActionEvent event) {
        moView("teller-status-view.fxml", event);
    }

    @FXML
    public void onTheoDoiTrangThaiTile(MouseEvent event) {
        ActionEvent ae = new ActionEvent(event.getSource(), null);
        moView("teller-status-view.fxml", ae);
    }

    @FXML
    public void onDangXuat(ActionEvent event) {
        TellerSession.clear();
        moView("login-view.fxml", event);
    }

    private void hienCanhBao(String tieuDe, String noiDung) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(tieuDe);
        alert.setHeaderText(null);
        alert.setContentText(noiDung == null ? "Không thể tải dữ liệu." : noiDung);
        alert.showAndWait();
    }
}
