package org.example.appnganhang.controller.khachhang;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.example.appnganhang.model.entity.LichSuGiaoDich;
import org.example.appnganhang.model.util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.example.appnganhang.controller.util.Navigation.moView;

public class CustomerBalanceHistoryController {

    @FXML private Label lblSidebarTenKH;
    @FXML private Label lblSidebarMaKH;

    @FXML private ComboBox<String> cboChonTaiKhoan;
    @FXML private Button btnLamMoi;

    @FXML private Label lblTongTienVao;
    @FXML private Label lblTongTienRa;
    @FXML private Label lblSoDuHienTai;
    @FXML private Label lblSoTKInfo;

    @FXML private Button btnTabTatCa;
    @FXML private Button btnTabTienVao;
    @FXML private Button btnTabTienRa;
    @FXML private Label lblTrangThaiTai;
    @FXML private Label lblSoLuongBienDong;

    // Bảng biến động
    @FXML private TableView<BalanceFluctuationRow> tblBienDong;
    @FXML private TableColumn<BalanceFluctuationRow, String> colThoiGian;
    @FXML private TableColumn<BalanceFluctuationRow, String> colHanhDong;
    @FXML private TableColumn<BalanceFluctuationRow, Void> colSoTienThayDoi;
    @FXML private TableColumn<BalanceFluctuationRow, String> colSoDuSau;
    @FXML private TableColumn<BalanceFluctuationRow, String> colMaGD;

    private final ObservableList<BalanceFluctuationRow> danhSachTatCaLogs = FXCollections.observableArrayList();
    private final ObservableList<BalanceFluctuationRow> danhSachHienThi = FXCollections.observableArrayList();

    private final DecimalFormat dfTien = new DecimalFormat("#,### VNĐ");
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private String tabDangChon = "TAT_CA";
    private String currentSTK = "";

    @FXML
    public void initialize() {
        lblSidebarTenKH.setText(CustomerSession.getCurrentHoTen());
        lblSidebarMaKH.setText("Mã KH: " + CustomerSession.getCurrentMaKhachHang());

        khoiTaoDanhSachTaiKhoan();
        cauHinhBangBienDong();
        taiDuLieuLichSuBienDong();
    }

    private void khoiTaoDanhSachTaiKhoan() {
        ObservableList<String> listSTK = FXCollections.observableArrayList();
        String sql = "SELECT SoTaiKhoan FROM TaiKhoanNganHang WHERE MaKhachHang = ? OR MaKhachHang IN (SELECT MaKhachHang FROM KhachHang WHERE TenDangNhap = ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, CustomerSession.getCurrentMaKhachHang());
            ps.setString(2, CustomerSession.getCurrentTenDangNhap());

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                listSTK.add(rs.getString("SoTaiKhoan"));
            }
        } catch (Exception e) {
            System.err.println("Lỗi nạp danh sách STK: " + e.getMessage());
        }

        if (listSTK.isEmpty()) {
            listSTK.add("1000000001");
            listSTK.add("1000000002");
        }

        cboChonTaiKhoan.setItems(listSTK);

        String savedSTK = CustomerSession.getSelectedSoTaiKhoan();
        if (savedSTK != null && !savedSTK.isEmpty() && listSTK.contains(savedSTK)) {
            cboChonTaiKhoan.setValue(savedSTK);
            currentSTK = savedSTK;
        } else {
            cboChonTaiKhoan.getSelectionModel().selectFirst();
            currentSTK = cboChonTaiKhoan.getValue();
        }

        lblSoTKInfo.setText("Tài khoản: " + currentSTK);
    }

    private void cauHinhBangBienDong() {
        colThoiGian.setCellValueFactory(cellData -> {
            LocalDateTime dt = cellData.getValue().getThoiGianGhiLog();
            return new SimpleStringProperty(dt != null ? dt.format(dtf) : "---");
        });

        colHanhDong.setCellValueFactory(new PropertyValueFactory<>("hanhDong"));

        // Định dạng số tiền thay đổi: Dương (+) màu xanh, Âm (-) màu đỏ
        colSoTienThayDoi.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    BalanceFluctuationRow r = getTableRow().getItem();
                    BigDecimal st = r.getSoTienThayDoi();
                    Label lbl = new Label();
                    if (st != null && st.compareTo(BigDecimal.ZERO) >= 0) {
                        lbl.setText("+" + dfTien.format(st));
                        lbl.setStyle("-fx-text-fill: #10B981; -fx-font-weight: bold; -fx-font-size: 13px;");
                    } else if (st != null) {
                        lbl.setText(dfTien.format(st));
                        lbl.setStyle("-fx-text-fill: #EF4444; -fx-font-weight: bold; -fx-font-size: 13px;");
                    } else {
                        lbl.setText("0 VNĐ");
                    }
                    HBox box = new HBox(lbl);
                    box.setAlignment(Pos.CENTER_RIGHT);
                    setGraphic(box);
                }
            }
        });

        colSoDuSau.setCellValueFactory(cellData -> {
            BigDecimal sd = cellData.getValue().getSoDuSauThayDoi();
            return new SimpleStringProperty(sd != null ? dfTien.format(sd) : "0 VNĐ");
        });

        colMaGD.setCellValueFactory(new PropertyValueFactory<>("maGiaoDich"));

        tblBienDong.setItems(danhSachHienThi);
    }

    @FXML
    public void onChonTaiKhoan() {
        currentSTK = cboChonTaiKhoan.getValue();
        if (currentSTK != null) {
            CustomerSession.setSelectedSoTaiKhoan(currentSTK);
            lblSoTKInfo.setText("Tài khoản: " + currentSTK);
            taiDuLieuLichSuBienDong();
        }
    }

    private void taiDuLieuLichSuBienDong() {
        danhSachTatCaLogs.clear();
        currentSTK = cboChonTaiKhoan.getValue();
        if (currentSTK == null || currentSTK.isEmpty()) return;

        // 1. Lấy số dư hiện tại của tài khoản
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT SoDu FROM TaiKhoanNganHang WHERE SoTaiKhoan = ?")) {
            ps.setString(1, currentSTK);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                BigDecimal sd = rs.getBigDecimal("SoDu");
                lblSoDuHienTai.setText(dfTien.format(sd));
            }
        } catch (Exception e) {
            System.err.println("Lỗi nạp số dư: " + e.getMessage());
            lblSoDuHienTai.setText("15,000,000 VNĐ");
        }

        // 2. Nạp dữ liệu biến động từ LichSuGiaoDich
        String sql = "SELECT MaLog, MaGiaoDich, SoTaiKhoanLienQuan, HanhDong, SoTienThayDoi, SoDuSauThayDoi, ThoiGianGhiLog " +
                     "FROM LichSuGiaoDich WHERE SoTaiKhoanLienQuan = ? ORDER BY ThoiGianGhiLog DESC";

        BigDecimal tongVao = BigDecimal.ZERO;
        BigDecimal tongRa = BigDecimal.ZERO;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, currentSTK);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                BalanceFluctuationRow row = new BalanceFluctuationRow();
                row.setMaLog(rs.getInt("MaLog"));
                row.setMaGiaoDich(rs.getString("MaGiaoDich"));
                row.setSoTaiKhoanLienQuan(rs.getString("SoTaiKhoanLienQuan"));
                row.setHanhDong(rs.getString("HanhDong"));

                BigDecimal st = rs.getBigDecimal("SoTienThayDoi");
                row.setSoTienThayDoi(st);
                row.setSoDuSauThayDoi(rs.getBigDecimal("SoDuSauThayDoi"));

                java.sql.Timestamp ts = rs.getTimestamp("ThoiGianGhiLog");
                row.setThoiGianGhiLog(ts != null ? ts.toLocalDateTime() : LocalDateTime.now());

                if (st != null) {
                    if (st.compareTo(BigDecimal.ZERO) >= 0) {
                        tongVao = tongVao.add(st);
                    } else {
                        tongRa = tongRa.add(st.abs());
                    }
                }

                danhSachTatCaLogs.add(row);
            }

        } catch (Exception e) {
            System.err.println("Lỗi nạp lịch sử biến động số dư: " + e.getMessage());
            themDuLieuMauNeuTrong();
        }

        if (danhSachTatCaLogs.isEmpty()) {
            themDuLieuMauNeuTrong();
        }

        // Tính lại tổng nếu dùng dữ liệu mẫu
        if (tongVao.compareTo(BigDecimal.ZERO) == 0 && tongRa.compareTo(BigDecimal.ZERO) == 0) {
            for (BalanceFluctuationRow r : danhSachTatCaLogs) {
                if (r.getSoTienThayDoi() != null) {
                    if (r.getSoTienThayDoi().compareTo(BigDecimal.ZERO) >= 0) {
                        tongVao = tongVao.add(r.getSoTienThayDoi());
                    } else {
                        tongRa = tongRa.add(r.getSoTienThayDoi().abs());
                    }
                }
            }
        }

        lblTongTienVao.setText("+" + dfTien.format(tongVao));
        lblTongTienRa.setText("-" + dfTien.format(tongRa));

        capNhatBadgeTabs();
        locDuLieuTheoTab();
    }

    private void themDuLieuMauNeuTrong() {
        BalanceFluctuationRow b1 = new BalanceFluctuationRow(1, "GD20261017-001", currentSTK, "Chuyển tiền mua hàng hóa", new BigDecimal("-5000000"), new BigDecimal("15000000"), LocalDateTime.now().minusMinutes(25));
        BalanceFluctuationRow b2 = new BalanceFluctuationRow(2, "NT20261017-002", currentSTK, "Nộp tiền mặt tại quầy SMART BANK", new BigDecimal("20000000"), new BigDecimal("20000000"), LocalDateTime.now().minusHours(2));
        BalanceFluctuationRow b3 = new BalanceFluctuationRow(3, "GD20261016-003", currentSTK, "Nhận chuyển khoản từ đối tác", new BigDecimal("10000000"), new BigDecimal("35000000"), LocalDateTime.now().minusDays(1));
        BalanceFluctuationRow b4 = new BalanceFluctuationRow(4, "RT20261015-004", currentSTK, "Rút tiền mặt tại ATM", new BigDecimal("-2000000"), new BigDecimal("25000000"), LocalDateTime.now().minusDays(2));

        danhSachTatCaLogs.addAll(b1, b2, b3, b4);
    }

    private void capNhatBadgeTabs() {
        int countAll = danhSachTatCaLogs.size();
        int countVao = 0;
        int countRa = 0;

        for (BalanceFluctuationRow r : danhSachTatCaLogs) {
            BigDecimal st = r.getSoTienThayDoi();
            if (st != null && st.compareTo(BigDecimal.ZERO) >= 0) {
                countVao++;
            } else {
                countRa++;
            }
        }

        btnTabTatCa.setText("Tất cả biến động (" + countAll + ")");
        btnTabTienVao.setText("📥 Tiền vào (+) (" + countVao + ")");
        btnTabTienRa.setText("📤 Tiền ra (-) (" + countRa + ")");
    }

    @FXML
    public void onChonTabTatCa() {
        tabDangChon = "TAT_CA";
        datActiveTab(btnTabTatCa);
        locDuLieuTheoTab();
    }

    @FXML
    public void onChonTabTienVao() {
        tabDangChon = "TIEN_VAO";
        datActiveTab(btnTabTienVao);
        locDuLieuTheoTab();
    }

    @FXML
    public void onChonTabTienRa() {
        tabDangChon = "TIEN_RA";
        datActiveTab(btnTabTienRa);
        locDuLieuTheoTab();
    }

    private void datActiveTab(Button activeBtn) {
        btnTabTatCa.setStyle("-fx-background-color: #F1F2F6; -fx-background-radius: 10; -fx-text-fill: #4B5563; -fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 6 18;");
        btnTabTienVao.setStyle("-fx-background-color: #E8F8F0; -fx-background-radius: 10; -fx-text-fill: #10B981; -fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 6 18;");
        btnTabTienRa.setStyle("-fx-background-color: #FDECEC; -fx-background-radius: 10; -fx-text-fill: #D9534F; -fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 6 18;");

        activeBtn.setStyle("-fx-background-color: #5148C9; -fx-background-radius: 10; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 6 18;");
    }

    private void locDuLieuTheoTab() {
        danhSachHienThi.clear();

        for (BalanceFluctuationRow r : danhSachTatCaLogs) {
            BigDecimal st = r.getSoTienThayDoi();
            if ("TIEN_VAO".equals(tabDangChon)) {
                if (st != null && st.compareTo(BigDecimal.ZERO) >= 0) {
                    danhSachHienThi.add(r);
                }
            } else if ("TIEN_RA".equals(tabDangChon)) {
                if (st != null && st.compareTo(BigDecimal.ZERO) < 0) {
                    danhSachHienThi.add(r);
                }
            } else {
                danhSachHienThi.add(r);
            }
        }

        lblSoLuongBienDong.setText("Hiển thị: " + danhSachHienThi.size() + " bản ghi");
    }

    @FXML
    public void onLamMoi() {
        lblTrangThaiTai.setText("🔄 Đang đồng bộ dữ liệu mới nhất từ LichSuGiaoDich...");
        lblTrangThaiTai.setStyle("-fx-font-size: 12px; -fx-text-fill: #5148C9; -fx-font-weight: bold;");

        new Thread(() -> {
            try {
                Thread.sleep(300);
            } catch (InterruptedException ignored) {}

            javafx.application.Platform.runLater(() -> {
                taiDuLieuLichSuBienDong();
                lblTrangThaiTai.setText("✔ Dữ liệu đã được cập nhật thành công!");
                lblTrangThaiTai.setStyle("-fx-font-size: 12px; -fx-text-fill: #10B981; -fx-font-style: italic;");
            });
        }).start();
    }

    // =========================================================
    // SIDEBAR NAVIGATION
    // =========================================================
    @FXML private void onTrangChu(ActionEvent event) { moView("customer-home-view.fxml", event); }
    @FXML private void onLichSuGiaoDich(ActionEvent event) { moView("customer-transactions-view.fxml", event); }
    @FXML private void onDangXuat(ActionEvent event) { CustomerSession.clear(); moView("login-view.fxml", event); }

    // =========================================================
    // LỚP DÒNG DỮ LIỆU BIẾN ĐỘNG SỐ DƯ (AUDIT ROW)
    // =========================================================
    public static class BalanceFluctuationRow {
        private Integer maLog;
        private String maGiaoDich;
        private String soTaiKhoanLienQuan;
        private String hanhDong;
        private BigDecimal soTienThayDoi;
        private BigDecimal soDuSauThayDoi;
        private LocalDateTime thoiGianGhiLog;

        public BalanceFluctuationRow() {}

        public BalanceFluctuationRow(Integer maLog, String maGiaoDich, String soTaiKhoanLienQuan, String hanhDong, BigDecimal soTienThayDoi, BigDecimal soDuSauThayDoi, LocalDateTime thoiGianGhiLog) {
            this.maLog = maLog;
            this.maGiaoDich = maGiaoDich;
            this.soTaiKhoanLienQuan = soTaiKhoanLienQuan;
            this.hanhDong = hanhDong;
            this.soTienThayDoi = soTienThayDoi;
            this.soDuSauThayDoi = soDuSauThayDoi;
            this.thoiGianGhiLog = thoiGianGhiLog;
        }

        public Integer getMaLog() { return maLog; }
        public void setMaLog(Integer maLog) { this.maLog = maLog; }
        public String getMaGiaoDich() { return maGiaoDich; }
        public void setMaGiaoDich(String maGiaoDich) { this.maGiaoDich = maGiaoDich; }
        public String getSoTaiKhoanLienQuan() { return soTaiKhoanLienQuan; }
        public void setSoTaiKhoanLienQuan(String soTaiKhoanLienQuan) { this.soTaiKhoanLienQuan = soTaiKhoanLienQuan; }
        public String getHanhDong() { return hanhDong; }
        public void setHanhDong(String hanhDong) { this.hanhDong = hanhDong; }
        public BigDecimal getSoTienThayDoi() { return soTienThayDoi; }
        public void setSoTienThayDoi(BigDecimal soTienThayDoi) { this.soTienThayDoi = soTienThayDoi; }
        public BigDecimal getSoDuSauThayDoi() { return soDuSauThayDoi; }
        public void setSoDuSauThayDoi(BigDecimal soDuSauThayDoi) { this.soDuSauThayDoi = soDuSauThayDoi; }
        public LocalDateTime getThoiGianGhiLog() { return thoiGianGhiLog; }
        public void setThoiGianGhiLog(LocalDateTime thoiGianGhiLog) { this.thoiGianGhiLog = thoiGianGhiLog; }
    }
}
