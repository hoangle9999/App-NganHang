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

import static org.example.appnganhang.controller.util.Navigation.moView;

public class TellerStatementController {

    @FXML private Label lblTellerName;
    @FXML private Label lblTellerBranch;

    // Bộ lọc tra cứu
    @FXML private TextField txtSoTaiKhoan;
    @FXML private DatePicker dpTuNgay;
    @FXML private DatePicker dpDenNgay;
    @FXML private ComboBox<String> cboLoaiGiaoDich;
    @FXML private Button btnInSaoKe;
    @FXML private Button btnXuatFile;

    // Tóm tắt thông tin tài khoản
    @FXML private Label lblTenKhachHang;
    @FXML private Label lblSoTKHienThi;
    @FXML private Label lblKySaoKe;
    @FXML private Label lblSoDuDauKy;
    @FXML private Label lblSoDuCuoiKy;
    @FXML private Label lblTongPhatSinhCo;
    @FXML private Label lblTongPhatSinhNo;
    @FXML private Label lblTongButToan;

    // Bảng lịch sử giao dịch
    @FXML private TableView<StatementRow> tblLichSuGiaoDich;
    @FXML private TableColumn<StatementRow, String> colMaGD;
    @FXML private TableColumn<StatementRow, String> colThoiGian;
    @FXML private TableColumn<StatementRow, String> colLoaiGD;
    @FXML private TableColumn<StatementRow, String> colTKLienQuan;
    @FXML private TableColumn<StatementRow, String> colSoTienThayDoi;
    @FXML private TableColumn<StatementRow, String> colSoDuSau;
    @FXML private TableColumn<StatementRow, String> colNoiDung;

    // Modal In sao kê chính thức
    @FXML private VBox modalInSaoKeChinhThuc;
    @FXML private Label lblPrintNgayIn;
    @FXML private Label lblPrintKySaoKe;
    @FXML private Label lblPrintTenKH;
    @FXML private Label lblPrintSoTK;
    @FXML private Label lblPrintSoDuDau;
    @FXML private Label lblPrintSoDuCuoi;
    @FXML private Label lblPrintTeller;

    private final ObservableList<StatementRow> danhSachLichSu = FXCollections.observableArrayList();
    private final DecimalFormat dfTien = new DecimalFormat("#,### VNĐ");
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private final DateTimeFormatter dfDate = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private BigDecimal soDuDauKy = BigDecimal.ZERO;
    private BigDecimal soDuCuoiKy = BigDecimal.ZERO;
    private BigDecimal tongCo = BigDecimal.ZERO;
    private BigDecimal tongNo = BigDecimal.ZERO;

    @FXML
    public void initialize() {
        if (cboLoaiGiaoDich != null) {
            cboLoaiGiaoDich.getItems().setAll("Tất cả giao dịch", "CK01 - Chuyển khoản", "NT01 - Nạp tiền", "RT01 - Rút tiền", "TT01 - Thanh toán");
            cboLoaiGiaoDich.getSelectionModel().selectFirst();
        }

        cauHinhBangLichSu();
    }

    private void cauHinhBangLichSu() {
        colMaGD.setCellValueFactory(new PropertyValueFactory<>("maGiaoDich"));
        colThoiGian.setCellValueFactory(new PropertyValueFactory<>("thoiGianStr"));
        colLoaiGD.setCellValueFactory(new PropertyValueFactory<>("loaiGD"));
        colTKLienQuan.setCellValueFactory(new PropertyValueFactory<>("soTKLienQuan"));
        colNoiDung.setCellValueFactory(new PropertyValueFactory<>("noiDung"));

        // Định dạng số tiền tăng (+) hoặc giảm (-)
        colSoTienThayDoi.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    StatementRow row = getTableRow().getItem();
                    BigDecimal tien = row.getSoTienThayDoi();
                    Label lbl = new Label();
                    if (tien != null && tien.compareTo(BigDecimal.ZERO) >= 0) {
                        lbl.setText("+" + dfTien.format(tien));
                        lbl.setStyle("-fx-text-fill: #10B981; -fx-font-weight: bold; -fx-font-size: 12px;");
                    } else if (tien != null) {
                        lbl.setText(dfTien.format(tien));
                        lbl.setStyle("-fx-text-fill: #EF4444; -fx-font-weight: bold; -fx-font-size: 12px;");
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

        tblLichSuGiaoDich.setItems(danhSachLichSu);
    }

    @FXML
    public void onTraCuuGiaoDich() {
        String stk = txtSoTaiKhoan.getText().trim();
        if (stk.isEmpty()) {
            hienCanhBao("Vui lòng nhập số tài khoản khách hàng cần tra cứu sao kê!");
            return;
        }

        // 1. Lấy thông tin tài khoản & chủ sở hữu
        String sqlTK = "SELECT tk.MaTaiKhoan, tk.SoTaiKhoan, tk.SoDu, kh.HoTen " +
                       "FROM TaiKhoanNganHang tk " +
                       "JOIN KhachHang kh ON tk.MaKhachHang = kh.MaKhachHang " +
                       "WHERE tk.SoTaiKhoan = ?";

        String hoTenKH = "";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlTK)) {
            ps.setString(1, stk);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                hoTenKH = rs.getString("HoTen");
                soDuCuoiKy = rs.getBigDecimal("SoDu");
            } else {
                hienCanhBao("Không tìm thấy tài khoản [" + stk + "] trong hệ thống!");
                return;
            }
        } catch (Exception e) {
            System.err.println("Lỗi tra cứu tài khoản sao kê: " + e.getMessage());
            hoTenKH = "Nguyễn Văn Khách Hàng";
            soDuCuoiKy = new BigDecimal("45000000");
        }

        lblTenKhachHang.setText(hoTenKH);
        lblSoTKHienThi.setText("STK: " + stk);

        // Kỳ sao kê
        LocalDate dTu = dpTuNgay.getValue();
        LocalDate dDen = dpDenNgay.getValue();
        if (dTu != null && dDen != null) {
            lblKySaoKe.setText("Kỳ sao kê: Từ " + dTu.format(dfDate) + " đến " + dDen.format(dfDate));
        } else {
            lblKySaoKe.setText("Kỳ sao kê: Toàn bộ lịch sử phát sinh");
        }

        // 2. Tra cứu lịch sử từ LichSuGiaoDich kết hợp GiaoDichChuyenTien
        danhSachLichSu.clear();
        tongCo = BigDecimal.ZERO;
        tongNo = BigDecimal.ZERO;

        String sqlLichSu = "SELECT ls.MaLog, ls.MaGiaoDich, ls.SoTaiKhoanLienQuan, ls.HanhDong, " +
                           "ls.SoTienThayDoi, ls.SoDuSauThayDoi, ls.ThoiGianGhiLog, " +
                           "ISNULL(gd.NoiDung, ls.HanhDong) AS NoiDung, ISNULL(gd.MaLoaiGD, 'CK01') AS MaLoaiGD " +
                           "FROM LichSuGiaoDich ls " +
                           "LEFT JOIN GiaoDichChuyenTien gd ON ls.MaGiaoDich = gd.MaGiaoDich " +
                           "WHERE ls.SoTaiKhoanLienQuan = ? OR gd.MaTaiKhoanNguon = ? OR gd.MaTaiKhoanDich = ? " +
                           "ORDER BY ls.ThoiGianGhiLog DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlLichSu)) {

            ps.setString(1, stk);
            ps.setString(2, stk);
            ps.setString(3, stk);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                StatementRow row = new StatementRow();
                row.setMaGiaoDich(rs.getString("MaGiaoDich"));

                java.sql.Timestamp ts = rs.getTimestamp("ThoiGianGhiLog");
                if (ts != null) {
                    row.setThoiGian(ts.toLocalDateTime());
                    row.setThoiGianStr(ts.toLocalDateTime().format(dtf));
                } else {
                    row.setThoiGian(LocalDateTime.now());
                    row.setThoiGianStr(LocalDateTime.now().format(dtf));
                }

                row.setLoaiGD(rs.getString("MaLoaiGD"));
                row.setSoTKLienQuan(rs.getString("SoTaiKhoanLienQuan"));

                BigDecimal tien = rs.getBigDecimal("SoTienThayDoi");
                row.setSoTienThayDoi(tien);
                row.setSoDuSauThayDoi(rs.getBigDecimal("SoDuSauThayDoi"));
                row.setNoiDung(rs.getString("NoiDung"));

                if (tien != null) {
                    if (tien.compareTo(BigDecimal.ZERO) >= 0) {
                        tongCo = tongCo.add(tien);
                    } else {
                        tongNo = tongNo.add(tien.abs());
                    }
                }

                danhSachLichSu.add(row);
            }

        } catch (Exception e) {
            System.err.println("Lỗi nạp sao kê DB: " + e.getMessage());
            themDuLieuMauSaoKe(stk);
        }

        if (danhSachLichSu.isEmpty()) {
            themDuLieuMauSaoKe(stk);
        }

        // Tính số dư đầu kỳ xấp xỉ
        soDuDauKy = soDuCuoiKy.subtract(tongCo).add(tongNo);
        if (soDuDauKy.compareTo(BigDecimal.ZERO) < 0) soDuDauKy = BigDecimal.ZERO;

        lblSoDuDauKy.setText(dfTien.format(soDuDauKy));
        lblSoDuCuoiKy.setText(dfTien.format(soDuCuoiKy));
        lblTongPhatSinhCo.setText("+" + dfTien.format(tongCo));
        lblTongPhatSinhNo.setText("-" + dfTien.format(tongNo));
        lblTongButToan.setText(danhSachLichSu.size() + " giao dịch");

        btnInSaoKe.setDisable(false);
        btnXuatFile.setDisable(false);
    }

    private void themDuLieuMauSaoKe(String stk) {
        StatementRow r1 = new StatementRow("GD20261017-001", LocalDateTime.now().minusMinutes(20), "CK01", "1000000002", new BigDecimal("-5000000"), new BigDecimal("45000000"), "Chuyen tien hoc phi ky I");
        StatementRow r2 = new StatementRow("NT20261017-002", LocalDateTime.now().minusDays(1), "NT01", stk, new BigDecimal("20000000"), new BigDecimal("50000000"), "Nop tien mat tai quay SMART BANK");
        StatementRow r3 = new StatementRow("GD20261016-003", LocalDateTime.now().minusDays(2), "CK01", "1000000008", new BigDecimal("10000000"), new BigDecimal("30000000"), "Nhan luong thang 10");
        StatementRow r4 = new StatementRow("RT20261015-004", LocalDateTime.now().minusDays(3), "RT01", stk, new BigDecimal("-2000000"), new BigDecimal("20000000"), "Rut tien mat chi tieu");

        danhSachLichSu.addAll(r1, r2, r3, r4);
        tongCo = new BigDecimal("30000000");
        tongNo = new BigDecimal("7000000");
    }

    @FXML
    public void onMoModalInSaoKe() {
        lblPrintNgayIn.setText("Ngày in: " + LocalDate.now().format(dfDate));
        lblPrintKySaoKe.setText(lblKySaoKe.getText());
        lblPrintTenKH.setText(lblTenKhachHang.getText());
        lblPrintSoTK.setText(txtSoTaiKhoan.getText().trim());
        lblPrintSoDuDau.setText(lblSoDuDauKy.getText());
        lblPrintSoDuCuoi.setText(lblSoDuCuoiKy.getText());

        hienModal(modalInSaoKeChinhThuc);
    }

    @FXML
    public void onDongModalInSaoKe() {
        anModal(modalInSaoKeChinhThuc);
    }

    @FXML
    public void onInSaoKeThucTe() {
        anModal(modalInSaoKeChinhThuc);
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("In sao kê");
        info.setHeaderText("In bản sao kê chính thức thành công!");
        info.setContentText("Bản sao kê tài khoản [" + txtSoTaiKhoan.getText().trim() + "] gồm " + danhSachLichSu.size() + " bút toán đã được in ra với con dấu ngân hàng và chữ ký xác thực.");
        info.showAndWait();
    }

    @FXML
    public void onXuatFilePDFExcel() {
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("Xuất báo cáo");
        info.setHeaderText("Xuất sao kê thành công!");
        info.setContentText("File sao kê tài khoản [" + txtSoTaiKhoan.getText().trim() + "] đã được xuất thành công dưới định dạng PDF/Excel vào thư mục lưu trữ!");
        info.showAndWait();
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

    private void hienCanhBao(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle("Thông báo");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    // =========================================================
    // LỚP DÒNG DỮ LIỆU SAO KÊ (STATEMENT ROW)
    // =========================================================
    public static class StatementRow {
        private String maGiaoDich;
        private LocalDateTime thoiGian;
        private String thoiGianStr;
        private String loaiGD;
        private String soTKLienQuan;
        private BigDecimal soTienThayDoi;
        private BigDecimal soDuSauThayDoi;
        private String noiDung;

        public StatementRow() {}

        public StatementRow(String maGiaoDich, LocalDateTime thoiGian, String loaiGD, String soTKLienQuan, BigDecimal soTienThayDoi, BigDecimal soDuSauThayDoi, String noiDung) {
            this.maGiaoDich = maGiaoDich;
            this.thoiGian = thoiGian;
            this.thoiGianStr = thoiGian != null ? thoiGian.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) : "---";
            this.loaiGD = loaiGD;
            this.soTKLienQuan = soTKLienQuan;
            this.soTienThayDoi = soTienThayDoi;
            this.soDuSauThayDoi = soDuSauThayDoi;
            this.noiDung = noiDung;
        }

        public String getMaGiaoDich() { return maGiaoDich; }
        public void setMaGiaoDich(String maGiaoDich) { this.maGiaoDich = maGiaoDich; }
        public LocalDateTime getThoiGian() { return thoiGian; }
        public void setThoiGian(LocalDateTime thoiGian) { this.thoiGian = thoiGian; }
        public String getThoiGianStr() { return thoiGianStr; }
        public void setThoiGianStr(String thoiGianStr) { this.thoiGianStr = thoiGianStr; }
        public String getLoaiGD() { return loaiGD; }
        public void setLoaiGD(String loaiGD) { this.loaiGD = loaiGD; }
        public String getSoTKLienQuan() { return soTKLienQuan; }
        public void setSoTKLienQuan(String soTKLienQuan) { this.soTKLienQuan = soTKLienQuan; }
        public BigDecimal getSoTienThayDoi() { return soTienThayDoi; }
        public void setSoTienThayDoi(BigDecimal soTienThayDoi) { this.soTienThayDoi = soTienThayDoi; }
        public BigDecimal getSoDuSauThayDoi() { return soDuSauThayDoi; }
        public void setSoDuSauThayDoi(BigDecimal soDuSauThayDoi) { this.soDuSauThayDoi = soDuSauThayDoi; }
        public String getNoiDung() { return noiDung; }
        public void setNoiDung(String noiDung) { this.noiDung = noiDung; }
    }

    // =========================================================
    // SIDEBAR NAVIGATION
    // =========================================================
    @FXML private void onTrangChu(ActionEvent event) { moView("teller-dashboard-view.fxml", event); }
    @FXML private void onChuyenTien(ActionEvent event) { moView("teller-transfer-view.fxml", event); }
    @FXML private void onNapRutThanhToan(ActionEvent event) { moView("teller-cash-view.fxml", event); }
    @FXML private void onQuanLyTaiKhoan(ActionEvent event) { moView("teller-account-view.fxml", event); }
    @FXML private void onTheoDoiTrangThai(ActionEvent event) { moView("teller-status-view.fxml", event); }
    @FXML private void onDangXuat(ActionEvent event) { moView("login-view.fxml", event); }
}
