package org.example.appnganhang.controller.giaodichvien;


import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.appnganhang.controller.util.StaffHeader;
import org.example.appnganhang.controller.service.Impl.TellerStatementServiceImpl;
import org.example.appnganhang.controller.service.intf.TellerStatementService;
import org.example.appnganhang.model.entity.LoaiGiaoDich;
import org.example.appnganhang.model.entity.TellerStatementData;
import org.example.appnganhang.model.entity.TellerStatementEntry;
import org.example.appnganhang.controller.util.TellerStatementExporter;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
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
    private final TellerStatementService tellerStatementService = new TellerStatementServiceImpl();

    private BigDecimal soDuDauKy = BigDecimal.ZERO;
    private BigDecimal soDuCuoiKy = BigDecimal.ZERO;
    private BigDecimal tongCo = BigDecimal.ZERO;
    private BigDecimal tongNo = BigDecimal.ZERO;

    @FXML
    public void initialize() {
        StaffHeader.loadTeller(lblTellerName, lblTellerBranch);
        cauHinhBangLichSu();
        khoiTaoLoaiGiaoDich();
    }

    private void khoiTaoLoaiGiaoDich() {
        if (cboLoaiGiaoDich == null) return;
        try {
            ObservableList<String> types = FXCollections.observableArrayList("Tất cả giao dịch");
            for (LoaiGiaoDich type : tellerStatementService.layDanhSachLoaiGiaoDich())
                types.add(type.getMaLoaiGD() + " - " + type.getTenLoaiGD());
            cboLoaiGiaoDich.setItems(types);
            cboLoaiGiaoDich.getSelectionModel().selectFirst();
        } catch (RuntimeException e) {
            hienCanhBao("Không thể tải danh sách loại giao dịch: " + e.getMessage());
        }
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

        LocalDate dTu = dpTuNgay.getValue();
        LocalDate dDen = dpDenNgay.getValue();
        String selectedType = cboLoaiGiaoDich.getValue();
        String maLoai = selectedType == null || selectedType.startsWith("Tất cả")
                ? "" : selectedType.split(" - ", 2)[0];
        TellerStatementData data;
        try {
            data = tellerStatementService.traCuuSaoKe(stk, dTu, dDen, maLoai);
        } catch (RuntimeException e) {
            hienCanhBao(e.getMessage());
            danhSachLichSu.clear();
            lblTenKhachHang.setText("Chưa xác định");
            lblSoTKHienThi.setText("STK: ---");
            lblKySaoKe.setText("Kỳ sao kê: ---");
            lblSoDuDauKy.setText("0 VNĐ");
            lblSoDuCuoiKy.setText("0 VNĐ");
            lblTongPhatSinhCo.setText("+0 VNĐ");
            lblTongPhatSinhNo.setText("-0 VNĐ");
            lblTongButToan.setText("0 giao dịch");
            btnInSaoKe.setDisable(true);
            btnXuatFile.setDisable(true);
            return;
        }

        lblTenKhachHang.setText(data.getTaiKhoan().getTenKhachHang());
        lblSoTKHienThi.setText("STK: " + data.getTaiKhoan().getSoTaiKhoan());
        if (dTu == null && dDen == null) lblKySaoKe.setText("Kỳ sao kê: Toàn bộ lịch sử phát sinh");
        else lblKySaoKe.setText("Kỳ sao kê: " + (dTu == null ? "Từ trước" : dTu.format(dfDate))
                + " đến " + (dDen == null ? "hiện tại" : dDen.format(dfDate)));

        danhSachLichSu.clear();
        for (TellerStatementEntry entry : data.getGiaoDich()) {
            StatementRow row = new StatementRow();
            row.setMaGiaoDich(entry.getMaGiaoDich());
            row.setThoiGian(entry.getThoiGian());
            row.setThoiGianStr(entry.getThoiGian() == null ? "---" : entry.getThoiGian().format(dtf));
            row.setLoaiGD(entry.getMaLoaiGD() == null ? entry.getTenLoaiGD()
                    : entry.getMaLoaiGD() + " - " + entry.getTenLoaiGD());
            row.setSoTKLienQuan(entry.getSoTaiKhoanLienQuan());
            row.setSoTienThayDoi(entry.getSoTienThayDoi());
            row.setSoDuSauThayDoi(entry.getSoDuSauThayDoi());
            row.setNoiDung(entry.getNoiDung());
            danhSachLichSu.add(row);
        }
        soDuDauKy = data.getSoDuDauKy();
        soDuCuoiKy = data.getSoDuCuoiKy();
        tongCo = data.getTongPhatSinhCo();
        tongNo = data.getTongPhatSinhNo();

        lblSoDuDauKy.setText(dfTien.format(soDuDauKy));
        lblSoDuCuoiKy.setText(dfTien.format(soDuCuoiKy));
        lblTongPhatSinhCo.setText("+" + dfTien.format(tongCo));
        lblTongPhatSinhNo.setText("-" + dfTien.format(tongNo));
        lblTongButToan.setText(danhSachLichSu.size() + " giao dịch");

        btnInSaoKe.setDisable(false);
        btnXuatFile.setDisable(false);
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
        info.setContentText("Bản sao kê tài khoản [" + txtSoTaiKhoan.getText().trim() + "] gồm " + danhSachLichSu.size() + " dòng giao dịch đã được gửi tới máy in.");
        info.showAndWait();
    }

    @FXML
    public void onXuatFilePDFExcel() {
        ChoiceDialog<String> formatDialog = new ChoiceDialog<>("Excel (.xlsx)", "Excel (.xlsx)", "PDF (.pdf)");
        formatDialog.setTitle("Xuất sao kê");
        formatDialog.setHeaderText("Chọn định dạng file cần xuất");
        formatDialog.setContentText("Định dạng:");
        String format = formatDialog.showAndWait().orElse(null);
        if (format == null) return;

        boolean excel = format.startsWith("Excel");
        String extension = excel ? ".xlsx" : ".pdf";
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Lưu sao kê");
        chooser.setInitialFileName("SaoKe_" + txtSoTaiKhoan.getText().trim() + extension);
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                excel ? "Excel Workbook (*.xlsx)" : "PDF (*.pdf)", "*" + extension));
        Window owner = btnXuatFile == null || btnXuatFile.getScene() == null
                ? null : btnXuatFile.getScene().getWindow();
        File selectedFile = chooser.showSaveDialog(owner);
        if (selectedFile == null) return;

        String path = selectedFile.getAbsolutePath();
        if (!path.toLowerCase(java.util.Locale.ROOT).endsWith(extension)) path += extension;
        try {
            if (excel) {
                TellerStatementExporter.exportExcel(Path.of(path), lblTenKhachHang.getText(),
                        txtSoTaiKhoan.getText().trim(), lblKySaoKe.getText(), lblSoDuDauKy.getText(),
                        lblSoDuCuoiKy.getText(), lblTongPhatSinhCo.getText(), lblTongPhatSinhNo.getText(),
                        danhSachLichSu);
            } else {
                TellerStatementExporter.exportPdf(Path.of(path), lblTenKhachHang.getText(),
                        txtSoTaiKhoan.getText().trim(), lblKySaoKe.getText(), lblSoDuDauKy.getText(),
                        lblSoDuCuoiKy.getText(), lblTongPhatSinhCo.getText(), lblTongPhatSinhNo.getText(),
                        danhSachLichSu);
            }
            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Xuất sao kê");
            success.setHeaderText("Đã xuất file sao kê thành công");
            success.setContentText(path);
            success.showAndWait();
        } catch (IOException | RuntimeException e) {
            hienCanhBao("Không thể xuất file sao kê: " + e.getMessage());
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
