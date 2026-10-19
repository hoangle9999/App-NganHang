package org.example.appnganhang.controller.giaodichvien;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.appnganhang.controller.service.Iml.TellerStatusServiceImpl;
import org.example.appnganhang.controller.service.intf.TellerStatusService;
import org.example.appnganhang.model.entity.GiaoDichChuyenTien;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.example.appnganhang.controller.util.Navigation.moView;

public class TellerStatusController {

    @FXML private Label lblTellerName;
    @FXML private Label lblTellerBranch;

    // Tabs chuyển đổi trạng thái nhanh
    @FXML private Button btnTabTatCa;
    @FXML private Button btnTabChoXuLy;
    @FXML private Button btnTabThanhCong;
    @FXML private Button btnTabThatBai;
    @FXML private Button btnTabDaHuy;

    // Thanh tìm kiếm
    @FXML private TextField txtTimKiem;
    @FXML private Label lblThongTinLoc;

    // Bảng danh sách giao dịch
    @FXML private TableView<GiaoDichChuyenTien> tblGiaoDich;
    @FXML private TableColumn<GiaoDichChuyenTien, String> colMaGD;
    @FXML private TableColumn<GiaoDichChuyenTien, String> colTKNguon;
    @FXML private TableColumn<GiaoDichChuyenTien, String> colTKDich;
    @FXML private TableColumn<GiaoDichChuyenTien, String> colSoTien;
    @FXML private TableColumn<GiaoDichChuyenTien, String> colNgayGD;
    @FXML private TableColumn<GiaoDichChuyenTien, String> colLoaiGD;
    @FXML private TableColumn<GiaoDichChuyenTien, String> colTrangThai;
    @FXML private TableColumn<GiaoDichChuyenTien, Void> colThaoTac;

    // Modal 1: Duyệt giao dịch
    @FXML private VBox modalDuyetGD;
    @FXML private Label lblDuyetMaGD;
    @FXML private Label lblDuyetTKNguon;
    @FXML private Label lblDuyetTKDich;
    @FXML private Label lblDuyetSoTien;
    @FXML private Label lblDuyetThoiGian;
    @FXML private Label lblDuyetNoiDung;

    // Modal 2: Hủy giao dịch
    @FXML private VBox modalHuyGD;
    @FXML private Label lblHuyThongTinGD;
    @FXML private TextArea txtLyDoHuy;

    private final ObservableList<GiaoDichChuyenTien> danhSachTatCaGD = FXCollections.observableArrayList();
    private final ObservableList<GiaoDichChuyenTien> danhSachHienThi = FXCollections.observableArrayList();

    private final DecimalFormat dfTien = new DecimalFormat("#,### VNĐ");
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private final TellerStatusService tellerStatusService = new TellerStatusServiceImpl();

    private String tabDangChon = "TAT_CA";
    private GiaoDichChuyenTien gdDangChon = null;

    @FXML
    public void initialize() {
        cauHinhBangGiaoDich();
        taiDuLieuGiaoDich();
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

        // Màu sắc trạng thái:
        // Chờ xử lý: màu vàng
        // Thành công: màu xanh
        // Thất bại: màu đỏ
        // Đã hủy: màu xám
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

                    if (tt.toLowerCase().contains("chờ") || tt.equalsIgnoreCase("CHO_XU_LY")) {
                        badge.setStyle("-fx-background-color: #FEF3C7; -fx-text-fill: #D97706; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 8; -fx-font-size: 11px;");
                    } else if (tt.toLowerCase().contains("thành công") || tt.equalsIgnoreCase("HOAN_THANH") || tt.equalsIgnoreCase("Hoàn thành")) {
                        badge.setStyle("-fx-background-color: #E8F8F0; -fx-text-fill: #10B981; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 8; -fx-font-size: 11px;");
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

        // Cột Thao tác: Nút [Duyệt] và [Hủy] - CHỈ xuất hiện ở những dòng có trạng thái "Chờ xử lý"!
        colThaoTac.setCellFactory(param -> new TableCell<>() {
            private final Button btnDuyet = new Button("✔ Duyệt");
            private final Button btnHuy = new Button("✕ Hủy");
            private final HBox box = new HBox(8, btnDuyet, btnHuy);

            {
                box.setAlignment(Pos.CENTER);
                btnDuyet.setStyle("-fx-background-color: #10B981; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 11px; -fx-padding: 4 10;");
                btnHuy.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 11px; -fx-padding: 4 10;");

                btnDuyet.setOnAction(e -> {
                    GiaoDichChuyenTien gd = getTableView().getItems().get(getIndex());
                    moModalDuyet(gd);
                });

                btnHuy.setOnAction(e -> {
                    GiaoDichChuyenTien gd = getTableView().getItems().get(getIndex());
                    moModalHuy(gd);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    GiaoDichChuyenTien gd = getTableRow().getItem();
                    String tt = gd.getTrangThai();
                    // Hai nút này chỉ xuất hiện ở những dòng có trạng thái Chờ xử lý
                    if (tt != null && (tt.toLowerCase().contains("chờ") || tt.equalsIgnoreCase("CHO_XU_LY"))) {
                        setGraphic(box);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });

        tblGiaoDich.setItems(danhSachHienThi);
    }

    private void taiDuLieuGiaoDich() {
        danhSachTatCaGD.clear();
        try {
            danhSachTatCaGD.setAll(tellerStatusService.layDanhSachGiaoDich());
        } catch (RuntimeException e) {
            hienCanhBao("Không thể tải danh sách giao dịch: " + e.getMessage());
        }
        capNhatBadgeTabs();
        onTimKiem();
    }

    private void capNhatBadgeTabs() {
        int countAll = danhSachTatCaGD.size();
        int countCho = 0;
        int countThanhCong = 0;
        int countThatBai = 0;
        int countHuy = 0;

        for (GiaoDichChuyenTien g : danhSachTatCaGD) {
            String tt = g.getTrangThai() != null ? g.getTrangThai().toLowerCase() : "";
            if (tt.contains("chờ") || tt.contains("cho_xu_ly")) countCho++;
            else if (tt.contains("thành công") || tt.contains("hoan_thanh") || tt.contains("hoàn thành")) countThanhCong++;
            else if (tt.contains("hủy") || tt.contains("da_huy")) countHuy++;
            else countThatBai++;
        }

        btnTabTatCa.setText("Tất cả (" + countAll + ")");
        btnTabChoXuLy.setText("⏳ Chờ xử lý (" + countCho + ")");
        btnTabThanhCong.setText("✔ Thành công (" + countThanhCong + ")");
        btnTabThatBai.setText("✕ Thất bại (" + countThatBai + ")");
        btnTabDaHuy.setText("⊘ Đã hủy (" + countHuy + ")");
    }

    // =========================================================
    // XỬ LÝ CHUYỂN TABS TRẠNG THÁI
    // =========================================================
    @FXML
    public void onChonTabTatCa() {
        tabDangChon = "TAT_CA";
        datActiveTab(btnTabTatCa);
        onTimKiem();
    }

    @FXML
    public void onChonTabChoXuLy() {
        tabDangChon = "CHO_XU_LY";
        datActiveTab(btnTabChoXuLy);
        onTimKiem();
    }

    @FXML
    public void onChonTabThanhCong() {
        tabDangChon = "THANH_CONG";
        datActiveTab(btnTabThanhCong);
        onTimKiem();
    }

    @FXML
    public void onChonTabThatBai() {
        tabDangChon = "THAT_BAI";
        datActiveTab(btnTabThatBai);
        onTimKiem();
    }

    @FXML
    public void onChonTabDaHuy() {
        tabDangChon = "DA_HUY";
        datActiveTab(btnTabDaHuy);
        onTimKiem();
    }

    private void datActiveTab(Button activeBtn) {
        // Cập nhật lại màu cơ bản cho các tabs
        btnTabTatCa.setStyle("-fx-background-color: #F1F2F6; -fx-background-radius: 10; -fx-text-fill: #4B5563; -fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 6 16;");
        btnTabChoXuLy.setStyle("-fx-background-color: #FEF3C7; -fx-background-radius: 10; -fx-text-fill: #D97706; -fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 6 16;");
        btnTabThanhCong.setStyle("-fx-background-color: #E8F8F0; -fx-background-radius: 10; -fx-text-fill: #10B981; -fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 6 16;");
        btnTabThatBai.setStyle("-fx-background-color: #FDECEC; -fx-background-radius: 10; -fx-text-fill: #D9534F; -fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 6 16;");
        btnTabDaHuy.setStyle("-fx-background-color: #F3F4F6; -fx-background-radius: 10; -fx-text-fill: #6B7280; -fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 6 16;");

        // Làm nổi bật tab đang chọn
        activeBtn.setStyle("-fx-background-color: #5148C9; -fx-background-radius: 10; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 6 16;");
    }

    @FXML
    public void onTimKiem() {
        String key = txtTimKiem != null ? txtTimKiem.getText().trim().toLowerCase() : "";
        danhSachHienThi.clear();

        for (GiaoDichChuyenTien g : danhSachTatCaGD) {
            boolean matchKey = key.isEmpty()
                    || (g.getMaGiaoDich() != null && g.getMaGiaoDich().toLowerCase().contains(key))
                    || (g.getMaTaiKhoanNguon() != null && g.getMaTaiKhoanNguon().toLowerCase().contains(key))
                    || (g.getMaTaiKhoanDich() != null && g.getMaTaiKhoanDich().toLowerCase().contains(key));

            boolean matchTab = true;
            String tt = g.getTrangThai() != null ? g.getTrangThai().toLowerCase() : "";

            if ("CHO_XU_LY".equals(tabDangChon)) {
                matchTab = tt.contains("chờ") || tt.contains("cho_xu_ly");
            } else if ("THANH_CONG".equals(tabDangChon)) {
                matchTab = tt.contains("thành công") || tt.contains("hoan_thanh") || tt.contains("hoàn thành");
            } else if ("THAT_BAI".equals(tabDangChon)) {
                matchTab = tt.contains("thất bại") || tt.contains("that_bai");
            } else if ("DA_HUY".equals(tabDangChon)) {
                matchTab = tt.contains("hủy") || tt.contains("da_huy");
            }

            if (matchKey && matchTab) {
                danhSachHienThi.add(g);
            }
        }

        if (lblThongTinLoc != null) {
            lblThongTinLoc.setText("Hiển thị: " + danhSachHienThi.size() + " giao dịch");
        }
    }

    @FXML
    public void onLamMoi() {
        if (txtTimKiem != null) txtTimKiem.clear();
        taiDuLieuGiaoDich();
    }

    // =========================================================
    // XỬ LÝ PHÊ DUYỆT GIAO DỊCH
    // =========================================================
    private void moModalDuyet(GiaoDichChuyenTien gd) {
        gdDangChon = gd;
        lblDuyetMaGD.setText(gd.getMaGiaoDich());
        lblDuyetTKNguon.setText(gd.getMaTaiKhoanNguon());
        lblDuyetTKDich.setText(gd.getMaTaiKhoanDich());
        lblDuyetSoTien.setText(dfTien.format(gd.getSoTien()));
        lblDuyetThoiGian.setText(gd.getNgayGiaoDich() != null ? gd.getNgayGiaoDich().format(dtf) : "---");
        lblDuyetNoiDung.setText(gd.getNoiDung() != null ? gd.getNoiDung() : "Chuyen tien tai quay SMART BANK");

        hienModal(modalDuyetGD);
    }

    @FXML
    public void onDongModalDuyet() {
        anModal(modalDuyetGD);
    }

    @FXML
    public void onXacNhanDuyetGiaoDich() {
        if (gdDangChon == null) return;
        String maGiaoDich = gdDangChon.getMaGiaoDich();
        try {
            tellerStatusService.duyetGiaoDich(maGiaoDich);
            gdDangChon.setTrangThai("Thành công");
            anModal(modalDuyetGD);
            capNhatBadgeTabs();
            onTimKiem();
            tblGiaoDich.refresh();
            hienThongBao("Duyệt thành công", "Giao dịch [" + maGiaoDich + "] đã chuyển sang trạng thái 'Thành công'.");
        } catch (RuntimeException e) {
            hienCanhBao("Không thể duyệt giao dịch: " + e.getMessage());
        }
    }

    // =========================================================
    // XỬ LÝ HỦY GIAO DỊCH
    // =========================================================
    private void moModalHuy(GiaoDichChuyenTien gd) {
        gdDangChon = gd;
        lblHuyThongTinGD.setText(gd.getMaGiaoDich() + " - " + dfTien.format(gd.getSoTien()) + " (Nguồn: " + gd.getMaTaiKhoanNguon() + ")");
        txtLyDoHuy.clear();
        hienModal(modalHuyGD);
    }

    @FXML
    public void onDongModalHuy() {
        anModal(modalHuyGD);
    }

    @FXML
    public void onXacNhanHuyGiaoDich() {
        if (gdDangChon == null) return;
        String lyDo = txtLyDoHuy.getText().trim();
        if (lyDo.isEmpty()) {
            hienCanhBao("Vui lòng nhập lý do từ chối/hủy giao dịch!");
            return;
        }
        String maGiaoDich = gdDangChon.getMaGiaoDich();
        try {
            tellerStatusService.huyGiaoDich(maGiaoDich, lyDo);
            gdDangChon.setTrangThai("Đã hủy");
            anModal(modalHuyGD);
            capNhatBadgeTabs();
            onTimKiem();
            tblGiaoDich.refresh();
            hienThongBao("Đã hủy giao dịch", "Giao dịch [" + maGiaoDich + "] đã chuyển sang trạng thái 'Đã hủy'.");
        } catch (RuntimeException e) {
            hienCanhBao("Không thể hủy giao dịch: " + e.getMessage());
        }
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

    private void hienCanhBao(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void hienThongBao(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // =========================================================
    // SIDEBAR NAVIGATION
    // =========================================================
    @FXML private void onTrangChu(ActionEvent event) { moView("teller-dashboard-view.fxml", event); }
    @FXML private void onChuyenTien(ActionEvent event) { moView("teller-transfer-view.fxml", event); }
    @FXML private void onNapRutThanhToan(ActionEvent event) { moView("teller-cash-view.fxml", event); }
    @FXML private void onQuanLyTaiKhoan(ActionEvent event) { moView("teller-account-view.fxml", event); }
    @FXML private void onTraCuuSaoKe(ActionEvent event) { moView("teller-statement-view.fxml", event); }
    @FXML private void onDangXuat(ActionEvent event) { moView("login-view.fxml", event); }
}
