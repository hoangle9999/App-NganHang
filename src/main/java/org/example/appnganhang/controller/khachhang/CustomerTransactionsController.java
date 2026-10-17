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
import javafx.scene.layout.VBox;
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

import static org.example.appnganhang.controller.util.Navigation.moView;

public class CustomerTransactionsController {

    @FXML private Label lblSidebarTenKH;
    @FXML private Label lblSidebarMaKH;

    @FXML private ComboBox<String> cboChonTaiKhoan;
    @FXML private DatePicker dpTuNgay;
    @FXML private DatePicker dpDenNgay;
    @FXML private Button btnApDungLoc;

    @FXML private Button btnTabTatCa;
    @FXML private Button btnTabThanhCong;
    @FXML private Button btnTabChoXuLy;
    @FXML private Button btnTabThatBai;
    @FXML private Button btnTabDaHuy;

    @FXML private Label lblSoLuongGD;

    // Bảng giao dịch
    @FXML private TableView<CustomerTransactionRow> tblGiaoDich;
    @FXML private TableColumn<CustomerTransactionRow, Void> colPhanLoai;
    @FXML private TableColumn<CustomerTransactionRow, String> colTKLienQuan;
    @FXML private TableColumn<CustomerTransactionRow, Void> colSoTien;
    @FXML private TableColumn<CustomerTransactionRow, String> colNgayGD;
    @FXML private TableColumn<CustomerTransactionRow, String> colNoiDung;
    @FXML private TableColumn<CustomerTransactionRow, String> colTrangThai;
    @FXML private TableColumn<CustomerTransactionRow, Void> colThaoTac;

    // Modal chi tiết
    @FXML private VBox modalChiTietGD;
    @FXML private Label lblModalMaGD;
    @FXML private Label lblModalTKGui;
    @FXML private Label lblModalTKNhan;
    @FXML private Label lblModalLoaiGD;
    @FXML private Label lblModalSoTien;
    @FXML private Label lblModalThoiGian;
    @FXML private Label lblModalNoiDung;
    @FXML private Label lblModalTrangThai;

    private final ObservableList<CustomerTransactionRow> danhSachTatCaGD = FXCollections.observableArrayList();
    private final ObservableList<CustomerTransactionRow> danhSachHienThi = FXCollections.observableArrayList();

    private final DecimalFormat dfTien = new DecimalFormat("#,### VNĐ");
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private String tabDangChon = "TAT_CA";
    private String currentSTK = "";

    @FXML
    public void initialize() {
        lblSidebarTenKH.setText(CustomerSession.getCurrentHoTen());
        lblSidebarMaKH.setText("Mã KH: " + CustomerSession.getCurrentMaKhachHang());

        khoiTaoDanhSachTaiKhoan();
        cauHinhBangGiaoDich();
        taiDuLieuGiaoDich();
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
    }

    private void cauHinhBangGiaoDich() {
        // Biểu tượng phân loại: Chuyển tiền đi -> Mũi tên đỏ hướng lên (↑); Nhận tiền -> Mũi tên xanh hướng xuống (↓)
        colPhanLoai.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    CustomerTransactionRow r = getTableRow().getItem();
                    Label icon = new Label();
                    if (r.isChuyenDi()) {
                        icon.setText("↑ Đi");
                        icon.setStyle("-fx-background-color: #FDECEC; -fx-text-fill: #D9534F; -fx-font-weight: bold; -fx-padding: 3 8; -fx-background-radius: 8; -fx-font-size: 11px;");
                    } else {
                        icon.setText("↓ Đến");
                        icon.setStyle("-fx-background-color: #E8F8F0; -fx-text-fill: #10B981; -fx-font-weight: bold; -fx-padding: 3 8; -fx-background-radius: 8; -fx-font-size: 11px;");
                    }
                    HBox box = new HBox(icon);
                    box.setAlignment(Pos.CENTER);
                    setGraphic(box);
                }
            }
        });

        colTKLienQuan.setCellValueFactory(new PropertyValueFactory<>("tkDoiUng"));

        // Số tiền tăng (+) hoặc giảm (-)
        colSoTien.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    CustomerTransactionRow r = getTableRow().getItem();
                    Label lbl = new Label();
                    if (r.isChuyenDi()) {
                        lbl.setText("-" + dfTien.format(r.getSoTien()));
                        lbl.setStyle("-fx-text-fill: #EF4444; -fx-font-weight: bold; -fx-font-size: 12px;");
                    } else {
                        lbl.setText("+" + dfTien.format(r.getSoTien()));
                        lbl.setStyle("-fx-text-fill: #10B981; -fx-font-weight: bold; -fx-font-size: 12px;");
                    }
                    HBox box = new HBox(lbl);
                    box.setAlignment(Pos.CENTER_RIGHT);
                    setGraphic(box);
                }
            }
        });

        colNgayGD.setCellValueFactory(cellData -> {
            LocalDateTime dt = cellData.getValue().getNgayGiaoDich();
            return new SimpleStringProperty(dt != null ? dt.format(dtf) : "---");
        });

        colNoiDung.setCellValueFactory(new PropertyValueFactory<>("noiDung"));

        // Trạng thái badge
        colTrangThai.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    CustomerTransactionRow r = getTableRow().getItem();
                    String tt = r.getTrangThai() != null ? r.getTrangThai() : "Hoàn thành";
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

        // Nút Xem chi tiết
        colThaoTac.setCellFactory(param -> new TableCell<>() {
            private final Button btnChiTiet = new Button("Chi tiết");
            {
                btnChiTiet.setStyle("-fx-background-color: #ECEBFA; -fx-text-fill: #5148C9; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 11px; -fx-padding: 4 10;");
                btnChiTiet.setOnAction(e -> {
                    CustomerTransactionRow r = getTableView().getItems().get(getIndex());
                    moModalChiTiet(r);
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

        tblGiaoDich.setItems(danhSachHienThi);
    }

    private void taiDuLieuGiaoDich() {
        danhSachTatCaGD.clear();
        currentSTK = cboChonTaiKhoan.getValue();
        if (currentSTK == null || currentSTK.isEmpty()) return;

        String sql = "SELECT gd.MaGiaoDich, gd.MaTaiKhoanNguon, gd.MaTaiKhoanDich, gd.SoTien, gd.NgayGiaoDich, " +
                     "gd.NoiDung, gd.MaLoaiGD, gd.TrangThai, " +
                     "ISNULL(lg.TenLoaiGD, N'Chuyển khoản') AS TenLoaiGD " +
                     "FROM GiaoDichChuyenTien gd " +
                     "LEFT JOIN LoaiGiaoDich lg ON gd.MaLoaiGD = lg.MaLoaiGD " +
                     "WHERE gd.MaTaiKhoanNguon = ? OR gd.MaTaiKhoanDich = ? " +
                     "ORDER BY gd.NgayGiaoDich DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, currentSTK);
            ps.setString(2, currentSTK);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                CustomerTransactionRow row = new CustomerTransactionRow();
                row.setMaGiaoDich(rs.getString("MaGiaoDich"));
                row.setMaTaiKhoanNguon(rs.getString("MaTaiKhoanNguon"));
                row.setMaTaiKhoanDich(rs.getString("MaTaiKhoanDich"));
                row.setSoTien(rs.getBigDecimal("SoTien"));

                java.sql.Timestamp ts = rs.getTimestamp("NgayGiaoDich");
                row.setNgayGiaoDich(ts != null ? ts.toLocalDateTime() : LocalDateTime.now());

                row.setNoiDung(rs.getString("NoiDung"));
                row.setMaLoaiGD(rs.getString("MaLoaiGD"));
                row.setTenLoaiGD(rs.getString("TenLoaiGD"));
                row.setTrangThai(rs.getString("TrangThai"));

                // Xác định chiều giao dịch
                boolean chuyenDi = currentSTK.equalsIgnoreCase(row.getMaTaiKhoanNguon());
                row.setChuyenDi(chuyenDi);
                row.setTkDoiUng(chuyenDi ? row.getMaTaiKhoanDich() : row.getMaTaiKhoanNguon());

                danhSachTatCaGD.add(row);
            }

        } catch (Exception e) {
            System.err.println("Lỗi nạp giao dịch khách hàng: " + e.getMessage());
            themDuLieuMauNeuTrong();
        }

        if (danhSachTatCaGD.isEmpty()) {
            themDuLieuMauNeuTrong();
        }

        capNhatBadgeTabs();
        locDuLieu();
    }

    private void themDuLieuMauNeuTrong() {
        CustomerTransactionRow r1 = new CustomerTransactionRow("GD20261017-001", currentSTK, "1000000008", new BigDecimal("5000000"), LocalDateTime.now().minusMinutes(30), "Chuyen tien mua hang hoa", "CK01", "Chuyển khoản", "Thành công", true, "1000000008");
        CustomerTransactionRow r2 = new CustomerTransactionRow("GD20261016-002", "1000000099", currentSTK, new BigDecimal("25000000"), LocalDateTime.now().minusDays(1), "Nhan luong thang 10", "CK01", "Chuyển khoản", "Thành công", false, "1000000099");
        CustomerTransactionRow r3 = new CustomerTransactionRow("GD20261015-003", currentSTK, "1000000055", new BigDecimal("12000000"), LocalDateTime.now().minusDays(2), "Chuyen tien du an", "CK01", "Chuyển khoản", "Chờ xử lý", true, "1000000055");
        CustomerTransactionRow r4 = new CustomerTransactionRow("GD20261014-004", currentSTK, "1000000033", new BigDecimal("80000000"), LocalDateTime.now().minusDays(3), "Vuot han muc ngay", "CK01", "Chuyển khoản", "Thất bại", true, "1000000033");
        CustomerTransactionRow r5 = new CustomerTransactionRow("GD20261012-005", currentSTK, "1000000012", new BigDecimal("1000000"), LocalDateTime.now().minusDays(5), "Yeu cau huy giao dich", "CK01", "Chuyển khoản", "Đã hủy", true, "1000000012");
        danhSachTatCaGD.addAll(r1, r2, r3, r4, r5);
    }

    private void capNhatBadgeTabs() {
        int countAll = danhSachTatCaGD.size();
        int countThanhCong = 0;
        int countCho = 0;
        int countThatBai = 0;
        int countHuy = 0;

        for (CustomerTransactionRow r : danhSachTatCaGD) {
            String tt = r.getTrangThai() != null ? r.getTrangThai().toLowerCase() : "";
            if (tt.contains("thành công") || tt.contains("hoan_thanh") || tt.contains("hoàn thành")) countThanhCong++;
            else if (tt.contains("chờ") || tt.contains("cho_xu_ly")) countCho++;
            else if (tt.contains("hủy") || tt.contains("da_huy")) countHuy++;
            else countThatBai++;
        }

        btnTabTatCa.setText("Tất cả (" + countAll + ")");
        btnTabThanhCong.setText("✔ Thành công (" + countThanhCong + ")");
        btnTabChoXuLy.setText("⏳ Chờ xử lý (" + countCho + ")");
        btnTabThatBai.setText("✕ Thất bại (" + countThatBai + ")");
        btnTabDaHuy.setText("⊘ Đã hủy (" + countHuy + ")");
    }

    @FXML
    public void onChonTabTatCa() {
        tabDangChon = "TAT_CA";
        datActiveTab(btnTabTatCa);
        locDuLieu();
    }

    @FXML
    public void onChonTabThanhCong() {
        tabDangChon = "THANH_CONG";
        datActiveTab(btnTabThanhCong);
        locDuLieu();
    }

    @FXML
    public void onChonTabChoXuLy() {
        tabDangChon = "CHO_XU_LY";
        datActiveTab(btnTabChoXuLy);
        locDuLieu();
    }

    @FXML
    public void onChonTabThatBai() {
        tabDangChon = "THAT_BAI";
        datActiveTab(btnTabThatBai);
        locDuLieu();
    }

    @FXML
    public void onChonTabDaHuy() {
        tabDangChon = "DA_HUY";
        datActiveTab(btnTabDaHuy);
        locDuLieu();
    }

    private void datActiveTab(Button activeBtn) {
        btnTabTatCa.setStyle("-fx-background-color: #F1F2F6; -fx-background-radius: 10; -fx-text-fill: #4B5563; -fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 6 16;");
        btnTabThanhCong.setStyle("-fx-background-color: #E8F8F0; -fx-background-radius: 10; -fx-text-fill: #10B981; -fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 6 16;");
        btnTabChoXuLy.setStyle("-fx-background-color: #FEF3C7; -fx-background-radius: 10; -fx-text-fill: #D97706; -fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 6 16;");
        btnTabThatBai.setStyle("-fx-background-color: #FDECEC; -fx-background-radius: 10; -fx-text-fill: #D9534F; -fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 6 16;");
        btnTabDaHuy.setStyle("-fx-background-color: #F3F4F6; -fx-background-radius: 10; -fx-text-fill: #6B7280; -fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 6 16;");

        activeBtn.setStyle("-fx-background-color: #5148C9; -fx-background-radius: 10; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 6 16;");
    }

    @FXML
    public void onApDungLoc() {
        taiDuLieuGiaoDich();
    }

    private void locDuLieu() {
        LocalDate tuNgay = dpTuNgay.getValue();
        LocalDate denNgay = dpDenNgay.getValue();

        danhSachHienThi.clear();

        for (CustomerTransactionRow r : danhSachTatCaGD) {
            // Lọc theo ngày
            boolean matchDate = true;
            if (r.getNgayGiaoDich() != null) {
                LocalDate d = r.getNgayGiaoDich().toLocalDate();
                if (tuNgay != null && d.isBefore(tuNgay)) matchDate = false;
                if (denNgay != null && d.isAfter(denNgay)) matchDate = false;
            }

            // Lọc theo Tab trạng thái
            boolean matchTab = true;
            String tt = r.getTrangThai() != null ? r.getTrangThai().toLowerCase() : "";

            if ("THANH_CONG".equals(tabDangChon)) {
                matchTab = tt.contains("thành công") || tt.contains("hoan_thanh") || tt.contains("hoàn thành");
            } else if ("CHO_XU_LY".equals(tabDangChon)) {
                matchTab = tt.contains("chờ") || tt.contains("cho_xu_ly");
            } else if ("THAT_BAI".equals(tabDangChon)) {
                matchTab = tt.contains("thất bại") || tt.contains("that_bai");
            } else if ("DA_HUY".equals(tabDangChon)) {
                matchTab = tt.contains("hủy") || tt.contains("da_huy");
            }

            if (matchDate && matchTab) {
                danhSachHienThi.add(r);
            }
        }

        lblSoLuongGD.setText("Tổng số: " + danhSachHienThi.size() + " giao dịch");
    }

    @FXML
    public void onLamMoi() {
        dpTuNgay.setValue(null);
        dpDenNgay.setValue(null);
        taiDuLieuGiaoDich();
    }

    private void moModalChiTiet(CustomerTransactionRow r) {
        lblModalMaGD.setText(r.getMaGiaoDich());
        lblModalTKGui.setText(r.getMaTaiKhoanNguon());
        lblModalTKNhan.setText(r.getMaTaiKhoanDich());
        lblModalLoaiGD.setText(r.getTenLoaiGD() + " (" + r.getMaLoaiGD() + ")");
        lblModalSoTien.setText((r.isChuyenDi() ? "-" : "+") + dfTien.format(r.getSoTien()));
        lblModalThoiGian.setText(r.getNgayGiaoDich() != null ? r.getNgayGiaoDich().format(dtf) : "---");
        lblModalNoiDung.setText(r.getNoiDung() != null ? r.getNoiDung() : "---");
        lblModalTrangThai.setText(r.getTrangThai());

        hienModal(modalChiTietGD);
    }

    @FXML
    public void onDongModalChiTiet() {
        anModal(modalChiTietGD);
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
    @FXML private void onTrangChu(ActionEvent event) { moView("customer-home-view.fxml", event); }
    @FXML private void onBienDongSoDu(ActionEvent event) { moView("customer-balance-history-view.fxml", event); }
    @FXML private void onDangXuat(ActionEvent event) { CustomerSession.clear(); moView("login-view.fxml", event); }

    // =========================================================
    // LỚP DÒNG DỮ LIỆU GIAO DỊCH KHÁCH HÀNG
    // =========================================================
    public static class CustomerTransactionRow {
        private String maGiaoDich;
        private String maTaiKhoanNguon;
        private String maTaiKhoanDich;
        private BigDecimal soTien;
        private LocalDateTime ngayGiaoDich;
        private String noiDung;
        private String maLoaiGD;
        private String tenLoaiGD;
        private String trangThai;
        private boolean chuyenDi;
        private String tkDoiUng;

        public CustomerTransactionRow() {}

        public CustomerTransactionRow(String maGiaoDich, String maTaiKhoanNguon, String maTaiKhoanDich, BigDecimal soTien, LocalDateTime ngayGiaoDich, String noiDung, String maLoaiGD, String tenLoaiGD, String trangThai, boolean chuyenDi, String tkDoiUng) {
            this.maGiaoDich = maGiaoDich;
            this.maTaiKhoanNguon = maTaiKhoanNguon;
            this.maTaiKhoanDich = maTaiKhoanDich;
            this.soTien = soTien;
            this.ngayGiaoDich = ngayGiaoDich;
            this.noiDung = noiDung;
            this.maLoaiGD = maLoaiGD;
            this.tenLoaiGD = tenLoaiGD;
            this.trangThai = trangThai;
            this.chuyenDi = chuyenDi;
            this.tkDoiUng = tkDoiUng;
        }

        public String getMaGiaoDich() { return maGiaoDich; }
        public void setMaGiaoDich(String maGiaoDich) { this.maGiaoDich = maGiaoDich; }
        public String getMaTaiKhoanNguon() { return maTaiKhoanNguon; }
        public void setMaTaiKhoanNguon(String maTaiKhoanNguon) { this.maTaiKhoanNguon = maTaiKhoanNguon; }
        public String getMaTaiKhoanDich() { return maTaiKhoanDich; }
        public void setMaTaiKhoanDich(String maTaiKhoanDich) { this.maTaiKhoanDich = maTaiKhoanDich; }
        public BigDecimal getSoTien() { return soTien; }
        public void setSoTien(BigDecimal soTien) { this.soTien = soTien; }
        public LocalDateTime getNgayGiaoDich() { return ngayGiaoDich; }
        public void setNgayGiaoDich(LocalDateTime ngayGiaoDich) { this.ngayGiaoDich = ngayGiaoDich; }
        public String getNoiDung() { return noiDung; }
        public void setNoiDung(String noiDung) { this.noiDung = noiDung; }
        public String getMaLoaiGD() { return maLoaiGD; }
        public void setMaLoaiGD(String maLoaiGD) { this.maLoaiGD = maLoaiGD; }
        public String getTenLoaiGD() { return tenLoaiGD; }
        public void setTenLoaiGD(String tenLoaiGD) { this.tenLoaiGD = tenLoaiGD; }
        public String getTrangThai() { return trangThai; }
        public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
        public boolean isChuyenDi() { return chuyenDi; }
        public void setChuyenDi(boolean chuyenDi) { this.chuyenDi = chuyenDi; }
        public String getTkDoiUng() { return tkDoiUng; }
        public void setTkDoiUng(String tkDoiUng) { this.tkDoiUng = tkDoiUng; }
    }
}
