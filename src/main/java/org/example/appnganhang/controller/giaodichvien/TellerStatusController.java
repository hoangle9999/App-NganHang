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
import org.example.appnganhang.model.dao.Impl.ChuyenTienDAOImpl;
import org.example.appnganhang.model.dao.intf.ChuyenTienDAO;
import org.example.appnganhang.model.entity.GiaoDichChuyenTien;
import org.example.appnganhang.model.util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
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
    private final ChuyenTienDAO chuyenTienDAO = new ChuyenTienDAOImpl();

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

        String sql = "SELECT MaGiaoDich, MaTaiKhoanNguon, MaTaiKhoanDich, SoTien, NgayGiaoDich, NoiDung, MaLoaiGD, TrangThai " +
                     "FROM GiaoDichChuyenTien ORDER BY NgayGiaoDich DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                GiaoDichChuyenTien gd = new GiaoDichChuyenTien();
                gd.setMaGiaoDich(rs.getString("MaGiaoDich"));
                gd.setMaTaiKhoanNguon(rs.getString("MaTaiKhoanNguon"));
                gd.setMaTaiKhoanDich(rs.getString("MaTaiKhoanDich"));
                gd.setSoTien(rs.getBigDecimal("SoTien"));

                java.sql.Timestamp ts = rs.getTimestamp("NgayGiaoDich");
                if (ts != null) {
                    gd.setNgayGiaoDich(ts.toLocalDateTime());
                } else {
                    gd.setNgayGiaoDich(LocalDateTime.now());
                }

                gd.setNoiDung(rs.getString("NoiDung"));
                gd.setMaLoaiGD(rs.getString("MaLoaiGD"));
                gd.setTrangThai(rs.getString("TrangThai"));

                danhSachTatCaGD.add(gd);
            }

        } catch (Exception e) {
            System.err.println("Lỗi nạp danh sách giao dịch: " + e.getMessage());
            themDuLieuMauNeuTrong();
        }

        if (danhSachTatCaGD.isEmpty()) {
            themDuLieuMauNeuTrong();
        }

        capNhatBadgeTabs();
        onTimKiem();
    }

    private void themDuLieuMauNeuTrong() {
        GiaoDichChuyenTien g1 = new GiaoDichChuyenTien();
        g1.setMaGiaoDich("GD20261017-001");
        g1.setMaTaiKhoanNguon("1000000001");
        g1.setMaTaiKhoanDich("1000000002");
        g1.setSoTien(new BigDecimal("15000000"));
        g1.setNgayGiaoDich(LocalDateTime.now().minusMinutes(10));
        g1.setMaLoaiGD("CK01");
        g1.setNoiDung("Chuyen tien hop dong kinh te");
        g1.setTrangThai("Chờ xử lý");

        GiaoDichChuyenTien g2 = new GiaoDichChuyenTien();
        g2.setMaGiaoDich("GD20261017-002");
        g2.setMaTaiKhoanNguon("1000000003");
        g2.setMaTaiKhoanDich("1000000004");
        g2.setSoTien(new BigDecimal("50000000"));
        g2.setNgayGiaoDich(LocalDateTime.now().minusHours(1));
        g2.setMaLoaiGD("CK01");
        g2.setNoiDung("Chuyen tien mua xe o to");
        g2.setTrangThai("Chờ xử lý");

        GiaoDichChuyenTien g3 = new GiaoDichChuyenTien();
        g3.setMaGiaoDich("GD20261017-003");
        g3.setMaTaiKhoanNguon("1000000001");
        g3.setMaTaiKhoanDich("1000000005");
        g3.setSoTien(new BigDecimal("3500000"));
        g3.setNgayGiaoDich(LocalDateTime.now().minusHours(2));
        g3.setMaLoaiGD("CK01");
        g3.setNoiDung("Thanh toan tien nha");
        g3.setTrangThai("Thành công");

        GiaoDichChuyenTien g4 = new GiaoDichChuyenTien();
        g4.setMaGiaoDich("GD20261017-004");
        g4.setMaTaiKhoanNguon("1000000002");
        g4.setMaTaiKhoanDich("1000000009");
        g4.setSoTien(new BigDecimal("100000000"));
        g4.setNgayGiaoDich(LocalDateTime.now().minusDays(1));
        g4.setMaLoaiGD("CK01");
        g4.setNoiDung("Chuyen khoan vuot han muc");
        g4.setTrangThai("Thất bại");

        GiaoDichChuyenTien g5 = new GiaoDichChuyenTien();
        g5.setMaGiaoDich("GD20261017-005");
        g5.setMaTaiKhoanNguon("1000000003");
        g5.setMaTaiKhoanDich("1000000001");
        g5.setSoTien(new BigDecimal("2000000"));
        g5.setNgayGiaoDich(LocalDateTime.now().minusDays(1));
        g5.setMaLoaiGD("CK01");
        g5.setNoiDung("Khach hang yeu cau huy tai quay");
        g5.setTrangThai("Đã hủy");

        danhSachTatCaGD.addAll(g1, g2, g3, g4, g5);
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

        // Cập nhật Database
        String sql = "UPDATE GiaoDichChuyenTien SET TrangThai = N'Thành công' WHERE MaGiaoDich = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, gdDangChon.getMaGiaoDich());
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Lỗi duyệt GD: " + e.getMessage());
        }

        // Cập nhật trạng thái và màu sắc
        gdDangChon.setTrangThai("Thành công");
        anModal(modalDuyetGD);

        capNhatBadgeTabs();
        onTimKiem();
        tblGiaoDich.refresh();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Duyệt thành công");
        alert.setHeaderText("Phê duyệt giao dịch thành công!");
        alert.setContentText("Giao dịch [" + gdDangChon.getMaGiaoDich() + "] đã chuyển sang trạng thái 'Thành công'!");
        alert.showAndWait();
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
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Thông báo");
            a.setHeaderText(null);
            a.setContentText("Vui lòng nhập lý do từ chối/hủy giao dịch!");
            a.showAndWait();
            return;
        }

        // Cập nhật Database
        String sql = "UPDATE GiaoDichChuyenTien SET TrangThai = N'Đã hủy', NoiDung = NoiDung + N' [Lý do hủy: ' + ? + ']' WHERE MaGiaoDich = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, lyDo);
            ps.setString(2, gdDangChon.getMaGiaoDich());
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Lỗi hủy GD: " + e.getMessage());
        }

        // Cập nhật trạng thái và màu sắc
        gdDangChon.setTrangThai("Đã hủy");
        anModal(modalHuyGD);

        capNhatBadgeTabs();
        onTimKiem();
        tblGiaoDich.refresh();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Đã hủy giao dịch");
        alert.setHeaderText("Giao dịch đã được hủy bỏ!");
        alert.setContentText("Giao dịch [" + gdDangChon.getMaGiaoDich() + "] đã chuyển sang trạng thái 'Đã hủy'!");
        alert.showAndWait();
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
    // SIDEBAR NAVIGATION
    // =========================================================
    @FXML private void onTrangChu(ActionEvent event) { moView("teller-dashboard-view.fxml", event); }
    @FXML private void onChuyenTien(ActionEvent event) { moView("teller-transfer-view.fxml", event); }
    @FXML private void onNapRutThanhToan(ActionEvent event) { moView("teller-cash-view.fxml", event); }
    @FXML private void onQuanLyTaiKhoan(ActionEvent event) { moView("teller-account-view.fxml", event); }
    @FXML private void onTraCuuSaoKe(ActionEvent event) { moView("teller-statement-view.fxml", event); }
    @FXML private void onDangXuat(ActionEvent event) { moView("login-view.fxml", event); }
}
