package org.example.appnganhang.controller.giaodichvien;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.example.appnganhang.controller.service.Iml.TellerTransferServiceImpl;
import org.example.appnganhang.controller.service.intf.TellerTransferService;
import org.example.appnganhang.model.entity.LoaiGiaoDich;
import org.example.appnganhang.model.entity.NganHang;
import org.example.appnganhang.model.entity.TellerTransferRecipientInfo;
import org.example.appnganhang.model.entity.TellerTransferResult;
import org.example.appnganhang.model.entity.TellerTransferSourceInfo;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static org.example.appnganhang.controller.util.Navigation.moView;

public class TellerTransferController {

    @FXML private Label lblTellerName;
    @FXML private Label lblTellerBranch;

    // Khung tài khoản nguồn
    @FXML private ComboBox<String> cboNganHangNguon;
    @FXML private TextField txtSoTaiKhoanNguon;
    @FXML private Button btnTraCuuNguon;
    @FXML private Label lblTenChuNguon;
    @FXML private Label lblSoDuNguon;
    @FXML private Label lblTrangThaiNguon;
    @FXML private Label lblHanMucNguon;

    // Khung tài khoản đích
    @FXML private ComboBox<String> cboNganHangDich;
    @FXML private TextField txtSoTaiKhoanDich;
    @FXML private Button btnTraCuuDich;
    @FXML private Label lblTenChuDich;
    @FXML private Label lblTrangThaiDich;
    @FXML private Label lblThongBaoNganHangDich;

    // Khung thông tin giao dịch
    @FXML private ComboBox<String> cboLoaiGiaoDich;
    @FXML private TextField txtSoTien;
    @FXML private Label lblTienBangChu;
    @FXML private TextField txtNoiDung;

    // 6 tiêu chí an toàn
    @FXML private Label lblScoreCriteria;
    @FXML private Label lblTrangThaiKiemTra;
    @FXML private Label iconChk1, lblChk1;
    @FXML private Label iconChk2, lblChk2;
    @FXML private Label iconChk3, lblChk3;
    @FXML private Label iconChk4, lblChk4;
    @FXML private Label iconChk5, lblChk5;
    @FXML private Label iconChk6, lblChk6;

    // Action buttons
    @FXML private Button btnKiemTraDieuKien;
    @FXML private Button btnXacNhanChuyenTien;
    @FXML private Button btnInBienNhan;

    // Modal Xác nhận giao dịch
    @FXML private VBox modalXacNhanGD;
    @FXML private Label lblModalTKNguon;
    @FXML private Label lblModalTenNguon;
    @FXML private Label lblModalTKDich;
    @FXML private Label lblModalTenDich;
    @FXML private Label lblModalNganHangDich;
    @FXML private Label lblModalSoTien;
    @FXML private Label lblModalNoiDung;

    // Modal In biên nhận
    @FXML private VBox modalInBienNhan;
    @FXML private Label lblReceiptMaGD;
    @FXML private Label lblReceiptThoiGian;
    @FXML private Label lblReceiptNguon;
    @FXML private Label lblReceiptDich;
    @FXML private Label lblReceiptNganHang;
    @FXML private Label lblReceiptSoTien;
    @FXML private Label lblReceiptBangChu;
    @FXML private Label lblReceiptNoiDung;
    @FXML private Label lblReceiptTeller;

    // Dữ liệu nội bộ
    private BigDecimal soDuHienTaiNguon = BigDecimal.ZERO;
    private BigDecimal hanMucNgayConLai = BigDecimal.ZERO;
    private String trangThaiTKnguon = "";
    private String trangThaiTKdich = "";
    private String trangThaiNHdich = "Hoạt động";
    private boolean taiKhoanDichNoiBo;
    private String tenNguoiNhanDich;
    private String maTaiKhoanNguonDB = "";
    private String maTaiKhoanDichDB = "";
    private String lastGeneratedMaGD = "";
    private boolean dangDinhDangSoTien;

    private final DecimalFormat dfTien = new DecimalFormat("#,### VNĐ");
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private final TellerTransferService tellerTransferService = new TellerTransferServiceImpl();
    private final Map<String, String> maNganHangTheoTen = new HashMap<>();

    @FXML
    public void initialize() {
        khoiTaoDanhSachNganHang();
        khoiTaoDanhSachLoaiGD();

        // Tự động chuyển đổi số tiền thành chữ
        txtSoTien.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!dangDinhDangSoTien) dinhDangSoTien(newVal);
            capNhatTienBangChu();
            // Reset trạng thái kiểm tra điều kiện khi sửa số tiền
            btnXacNhanChuyenTien.setDisable(true);
        });

        txtSoTaiKhoanNguon.textProperty().addListener((obs, oldVal, newVal) -> btnXacNhanChuyenTien.setDisable(true));
        txtSoTaiKhoanDich.textProperty().addListener((obs, oldVal, newVal) -> btnXacNhanChuyenTien.setDisable(true));
    }

    private void dinhDangSoTien(String giaTri) {
        if (giaTri == null) return;
        int viTriConTro = Math.min(txtSoTien.getCaretPosition(), giaTri.length());
        int soChuSoTruocConTro = (int) giaTri.substring(0, viTriConTro).chars().filter(Character::isDigit).count();
        String chuSo = giaTri.replaceAll("\\D", "");
        StringBuilder dinhDang = new StringBuilder(chuSo.length() + chuSo.length() / 3);
        for (int i = 0; i < chuSo.length(); i++) {
            if (i > 0 && (chuSo.length() - i) % 3 == 0) dinhDang.append(',');
            dinhDang.append(chuSo.charAt(i));
        }
        String ketQua = dinhDang.toString();
        if (!ketQua.equals(giaTri)) {
            int viTriMoi = 0;
            int daDem = 0;
            while (viTriMoi < ketQua.length() && daDem < soChuSoTruocConTro) {
                if (Character.isDigit(ketQua.charAt(viTriMoi))) daDem++;
                viTriMoi++;
            }
            dangDinhDangSoTien = true;
            txtSoTien.setText(ketQua);
            txtSoTien.positionCaret(viTriMoi);
            dangDinhDangSoTien = false;
        }
    }

    private void khoiTaoDanhSachNganHang() {
        try {
            ObservableList<String> listNganHangDich = FXCollections.observableArrayList();
            for (NganHang bank : tellerTransferService.layDanhSachNganHangHoatDong()) {
                if ("SMART".equalsIgnoreCase(bank.getMaNganHang())) {
                    String noiBo = bank.getTenNganHang() + " - Nội bộ hệ thống";
                    listNganHangDich.add(noiBo);
                    maNganHangTheoTen.put(noiBo, bank.getMaNganHang());
                    if (cboNganHangNguon != null) {
                        String nguon = bank.getTenNganHang() + " - Ngân hàng Hệ thống";
                        cboNganHangNguon.getItems().setAll(nguon);
                        cboNganHangNguon.getSelectionModel().selectFirst();
                    }
                } else {
                    listNganHangDich.add(bank.getTenNganHang());
                    maNganHangTheoTen.put(bank.getTenNganHang(), bank.getMaNganHang());
                }
            }
            if (cboNganHangDich != null) {
                cboNganHangDich.setItems(listNganHangDich);
                cboNganHangDich.getSelectionModel().selectFirst();
            }
            if (listNganHangDich.isEmpty()) hienCanhBao("Không có ngân hàng đang hoạt động.");
        } catch (RuntimeException e) {
            hienCanhBao("Lỗi tải danh sách ngân hàng: " + e.getMessage());
        }
    }

    private void khoiTaoDanhSachLoaiGD() {
        ObservableList<String> listLoaiGD = FXCollections.observableArrayList();
        try {
            for (LoaiGiaoDich loai : tellerTransferService.layDanhSachLoaiGiaoDich())
                listLoaiGD.add(loai.getMaLoaiGD() + " - " + loai.getTenLoaiGD());
            if (cboLoaiGiaoDich != null) {
                cboLoaiGiaoDich.setItems(listLoaiGD);
                cboLoaiGiaoDich.getSelectionModel().selectFirst();
            }
            if (listLoaiGD.isEmpty()) hienCanhBao("Chưa có loại giao dịch trong cơ sở dữ liệu.");
        } catch (RuntimeException e) {
            hienCanhBao("Lỗi tải danh sách loại giao dịch: " + e.getMessage());
        }
    }

    @FXML
    public void onChonNganHangDich() {
        trangThaiNHdich = maNganHangDichDangChon().isBlank() ? "Không tồn tại" : "Hoạt động";
        if (lblThongBaoNganHangDich != null) {
            lblThongBaoNganHangDich.setVisible(false);
            lblThongBaoNganHangDich.setManaged(false);
        }
        taiKhoanDichNoiBo = "SMART".equalsIgnoreCase(maNganHangDichDangChon());
        btnXacNhanChuyenTien.setDisable(true);
    }

    private String maNganHangDichDangChon() {
        return cboNganHangDich == null ? "" : maNganHangTheoTen.getOrDefault(cboNganHangDich.getValue(), "");
    }

    private String maLoaiGiaoDichDangChon() {
        String value = cboLoaiGiaoDich == null ? null : cboLoaiGiaoDich.getValue();
        if (value == null || value.isBlank()) return "";
        int separator = value.indexOf(" - ");
        return separator < 0 ? value.trim() : value.substring(0, separator).trim();
    }

    @FXML
    public void onTraCuuNguon() {
        String stk = txtSoTaiKhoanNguon.getText().trim();
        if (stk.isEmpty()) {
            hienCanhBao("Vui lòng nhập số tài khoản nguồn cần tra cứu!");
            return;
        }
        try {
            TellerTransferSourceInfo source = tellerTransferService.traCuuTaiKhoanNguon(stk)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản nguồn có số: " + stk));
            maTaiKhoanNguonDB = source.getMaTaiKhoan();
            soDuHienTaiNguon = source.getSoDu();
            hanMucNgayConLai = source.getHanMucConLai();
            trangThaiTKnguon = source.getTrangThai();
            lblTenChuNguon.setText(source.getTenChuTaiKhoan());
            lblSoDuNguon.setText(dfTien.format(soDuHienTaiNguon));
            lblTrangThaiNguon.setText(trangThaiTKnguon);
            lblHanMucNguon.setText(dfTien.format(hanMucNgayConLai));
            lblTrangThaiNguon.setStyle("Hoạt động".equalsIgnoreCase(trangThaiTKnguon)
                    ? "-fx-background-color: #E8F8F0; -fx-background-radius: 8; -fx-padding: 3 8; -fx-text-fill: #10B981; -fx-font-weight: bold; -fx-font-size: 11px;"
                    : "-fx-background-color: #FDECEC; -fx-background-radius: 8; -fx-padding: 3 8; -fx-text-fill: #D9534F; -fx-font-weight: bold; -fx-font-size: 11px;");
        } catch (RuntimeException e) {
            hienCanhBao(e.getMessage());
            maTaiKhoanNguonDB = "";
            soDuHienTaiNguon = BigDecimal.ZERO;
            hanMucNgayConLai = BigDecimal.ZERO;
            trangThaiTKnguon = "Không tồn tại";
            lblTenChuNguon.setText("Không tìm thấy");
            lblSoDuNguon.setText("0 VNĐ");
            lblTrangThaiNguon.setText(trangThaiTKnguon);
            lblHanMucNguon.setText("0 VNĐ");
        }

        btnXacNhanChuyenTien.setDisable(true);
    }

    @FXML
    public void onTraCuuDich() {
        String stkDich = txtSoTaiKhoanDich.getText().trim();
        if (stkDich.isEmpty()) {
            hienCanhBao("Vui lòng nhập số tài khoản đích cần tra cứu!");
            return;
        }

        try {
            String maNganHang = maNganHangDichDangChon();
            if (maNganHang.isBlank()) throw new IllegalArgumentException("Ngân hàng đích chưa được chọn hoặc không còn hoạt động.");
            TellerTransferRecipientInfo recipient = tellerTransferService.traCuuTaiKhoanDich(maNganHang, stkDich);
            maTaiKhoanDichDB = recipient.getSoTaiKhoan();
            tenNguoiNhanDich = recipient.getTenNguoiNhan();
            taiKhoanDichNoiBo = recipient.isTaiKhoanNoiBo();
            trangThaiTKdich = recipient.getTrangThaiTaiKhoan();
            trangThaiNHdich = "Hoạt động";
            lblTenChuDich.setText(tenNguoiNhanDich == null || tenNguoiNhanDich.isBlank()
                    ? "Người nhận ngân hàng ngoài" : tenNguoiNhanDich);
            lblTrangThaiDich.setText(taiKhoanDichNoiBo ? trangThaiTKdich : "Chưa xác minh tại ngân hàng ngoài");
            lblTrangThaiDich.setStyle(taiKhoanDichNoiBo && !"Hoạt động".equalsIgnoreCase(trangThaiTKdich)
                    ? "-fx-background-color: #FDECEC; -fx-text-fill: #D9534F; -fx-padding: 3 8; -fx-background-radius: 8;"
                    : "-fx-background-color: #E8F8F0; -fx-background-radius: 8; -fx-padding: 3 8; -fx-text-fill: #10B981; -fx-font-weight: bold; -fx-font-size: 11px;");
        } catch (RuntimeException e) {
            hienCanhBao(e.getMessage());
            maTaiKhoanDichDB = "";
            tenNguoiNhanDich = null;
            trangThaiTKdich = "Không tồn tại";
            taiKhoanDichNoiBo = false;
            lblTenChuDich.setText("Không tìm thấy");
            lblTrangThaiDich.setText(trangThaiTKdich);
            lblTrangThaiDich.setStyle("-fx-background-color: #FDECEC; -fx-text-fill: #D9534F; -fx-padding: 3 8; -fx-background-radius: 8;");
        }

        btnXacNhanChuyenTien.setDisable(true);
    }

    private void capNhatTienBangChu() {
        String strTien = txtSoTien.getText().replaceAll("[^0-9]", "");
        if (strTien.isEmpty()) {
            lblTienBangChu.setText("Bằng chữ: Không đồng");
            return;
        }
        try {
            long tien = Long.parseLong(strTien);
            lblTienBangChu.setText("Bằng chữ: " + docSoThanhChu(tien) + " đồng");
        } catch (Exception e) {
            lblTienBangChu.setText("Bằng chữ: Số tiền không hợp lệ");
        }
    }

    @FXML
    public void onKiemTraDieuKien() {
        String strSoTien = txtSoTien.getText().replaceAll("[^0-9]", "");
        BigDecimal soTienChuyen = BigDecimal.ZERO;
        if (!strSoTien.isEmpty()) {
            try {
                soTienChuyen = new BigDecimal(strSoTien);
            } catch (Exception ignored) {}
        }

        String stkNguon = txtSoTaiKhoanNguon.getText().trim();
        String stkDich = txtSoTaiKhoanDich.getText().trim();

        int score = 0;

        // 1. Số tiền lớn hơn 0
        boolean c1 = soTienChuyen.compareTo(BigDecimal.ZERO) > 0;
        setCheckItem(iconChk1, lblChk1, c1, "1. Số tiền lớn hơn 0 VNĐ");
        if (c1) score++;

        // 2. Số dư nguồn đủ thanh toán
        boolean c2 = soDuHienTaiNguon != null && soDuHienTaiNguon.compareTo(soTienChuyen) >= 0 && soTienChuyen.compareTo(BigDecimal.ZERO) > 0;
        setCheckItem(iconChk2, lblChk2, c2, "2. Số dư tài khoản nguồn đủ thanh toán (" + (soDuHienTaiNguon != null ? dfTien.format(soDuHienTaiNguon) : "0 VNĐ") + ")");
        if (c2) score++;

        // 3. Không vượt quá hạn mức ngày
        boolean c3 = hanMucNgayConLai != null && soTienChuyen.compareTo(hanMucNgayConLai) <= 0 && soTienChuyen.compareTo(BigDecimal.ZERO) > 0;
        setCheckItem(iconChk3, lblChk3, c3, "3. Không vượt quá hạn mức giao dịch theo ngày");
        if (c3) score++;

        // 4. Hai tài khoản khác nhau
        boolean c4 = !stkNguon.isEmpty() && !stkDich.isEmpty() && !stkNguon.equalsIgnoreCase(stkDich);
        setCheckItem(iconChk4, lblChk4, c4, "4. Hai tài khoản khác nhau (Nguồn ≠ Đích)");
        if (c4) score++;

        // 5. Cả hai tài khoản đang Hoạt động
        boolean c5 = "Hoạt động".equalsIgnoreCase(trangThaiTKnguon)
                && (!taiKhoanDichNoiBo || "Hoạt động".equalsIgnoreCase(trangThaiTKdich));
        setCheckItem(iconChk5, lblChk5, c5, taiKhoanDichNoiBo
                ? "5. Cả hai tài khoản đều ở trạng thái 'Hoạt động'"
                : "5. Tài khoản nguồn đang ở trạng thái 'Hoạt động'");
        if (c5) score++;

        // 6. Ngân hàng đích Hoạt động
        boolean c6 = "Hoạt động".equalsIgnoreCase(trangThaiNHdich);
        setCheckItem(iconChk6, lblChk6, c6, "6. Ngân hàng thụ hưởng đang 'Hoạt động'");
        if (c6) score++;

        lblScoreCriteria.setText(score + " / 6 ĐẠT");

        if (score == 6) {
            lblScoreCriteria.setStyle("-fx-background-color: #E8F8F0; -fx-background-radius: 8; -fx-padding: 3 8; -fx-text-fill: #10B981; -fx-font-weight: bold; -fx-font-size: 11px;");
            lblTrangThaiKiemTra.setText("✔ Tất cả 6 điều kiện an toàn đều đạt yêu cầu! Sẵn sàng xác nhận chuyển tiền.");
            lblTrangThaiKiemTra.setStyle("-fx-background-color: #E8F8F0; -fx-background-radius: 10; -fx-padding: 10; -fx-text-fill: #10B981; -fx-font-size: 12px; -fx-font-weight: bold;");
            btnXacNhanChuyenTien.setDisable(false);
        } else {
            lblScoreCriteria.setStyle("-fx-background-color: #FEF3C7; -fx-background-radius: 8; -fx-padding: 3 8; -fx-text-fill: #D97706; -fx-font-weight: bold; -fx-font-size: 11px;");
            lblTrangThaiKiemTra.setText("⚠ Có " + (6 - score) + " điều kiện chưa thỏa mãn. Vui lòng kiểm tra lại thông tin!");
            lblTrangThaiKiemTra.setStyle("-fx-background-color: #FEF3C7; -fx-background-radius: 10; -fx-padding: 10; -fx-text-fill: #D97706; -fx-font-size: 12px; -fx-font-weight: bold;");
            btnXacNhanChuyenTien.setDisable(true);
        }
    }

    private void setCheckItem(Label icon, Label text, boolean pass, String msg) {
        if (pass) {
            icon.setText("✔");
            icon.setStyle("-fx-font-size: 14px; -fx-text-fill: #10B981; -fx-font-weight: bold;");
            text.setText(msg);
            text.setStyle("-fx-font-size: 12px; -fx-text-fill: #10B981; -fx-font-weight: bold;");
        } else {
            icon.setText("✕");
            icon.setStyle("-fx-font-size: 14px; -fx-text-fill: #EF4444; -fx-font-weight: bold;");
            text.setText(msg);
            text.setStyle("-fx-font-size: 12px; -fx-text-fill: #6B7280;");
        }
    }

    @FXML
    public void onMoModalXacNhan() {
        lblModalTKNguon.setText(txtSoTaiKhoanNguon.getText().trim());
        lblModalTenNguon.setText(lblTenChuNguon.getText());
        lblModalTKDich.setText(txtSoTaiKhoanDich.getText().trim());
        lblModalTenDich.setText(lblTenChuDich.getText());
        lblModalNganHangDich.setText(cboNganHangDich.getValue());

        String strSoTien = txtSoTien.getText().replaceAll("[^0-9]", "");
        BigDecimal st = new BigDecimal(strSoTien);
        lblModalSoTien.setText(dfTien.format(st));

        String nd = txtNoiDung.getText().trim();
        lblModalNoiDung.setText(nd.isEmpty() ? "Chuyen tien tai quay SMART BANK" : nd);

        hienModal(modalXacNhanGD);
    }

    @FXML
    public void onDongModalXacNhan() {
        anModal(modalXacNhanGD);
    }

    @FXML
    public void onThucHienChuyenTien() {
        anModal(modalXacNhanGD);

        String strSoTien = txtSoTien.getText().replaceAll("[^0-9]", "");
        if (strSoTien.isEmpty()) {
            hienCanhBao("Vui lòng nhập số tiền chuyển hợp lệ.");
            return;
        }
        BigDecimal soTien = new BigDecimal(strSoTien);
        String stkNguon = txtSoTaiKhoanNguon.getText().trim();
        String stkDich = txtSoTaiKhoanDich.getText().trim();
        String noiDung = txtNoiDung.getText().trim();
        if (noiDung.isEmpty()) noiDung = "Chuyển tiền tại quầy SMART BANK";
        try {
            TellerTransferResult result = tellerTransferService.thucHienChuyenTien(
                    "GD" + System.currentTimeMillis(), stkNguon, maNganHangDichDangChon(), stkDich,
                    tenNguoiNhanDich, maLoaiGiaoDichDangChon(), soTien, noiDung);
            lastGeneratedMaGD = result.getMaGiaoDich();
            soDuHienTaiNguon = result.getSoDuNguonSauGiaoDich();
            hanMucNgayConLai = result.getHanMucConLai();
            lblSoDuNguon.setText(dfTien.format(soDuHienTaiNguon));
            lblHanMucNguon.setText(dfTien.format(hanMucNgayConLai));
            btnInBienNhan.setDisable(false);
            btnXacNhanChuyenTien.setDisable(true);

            boolean choXuLy = "Chờ xử lý".equalsIgnoreCase(result.getTrangThai());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(choXuLy ? "Giao dịch đang chờ xử lý" : "Giao dịch thành công");
            alert.setHeaderText(choXuLy ? "Đã ghi nhận yêu cầu chuyển tiền liên ngân hàng." : "Chuyển tiền thành công tại quầy!");
            alert.setContentText("Mã giao dịch: " + lastGeneratedMaGD + "\nSố tiền: " + dfTien.format(soTien)
                    + (choXuLy ? "\nGiao dịch đang chờ ngân hàng thụ hưởng xử lý." : "\nSố dư và hạn mức đã được cập nhật."));
            alert.showAndWait();
        } catch (Exception e) {
            hienCanhBao("Không thể thực hiện giao dịch: " + e.getMessage());
            btnInBienNhan.setDisable(true);
            return;
        }
    }

    @FXML
    public void onMoModalInBienNhan() {
        lblReceiptMaGD.setText(lastGeneratedMaGD);
        lblReceiptThoiGian.setText("Thời gian thực hiện: " + LocalDateTime.now().format(dtf));
        lblReceiptNguon.setText(lblTenChuNguon.getText() + " (STK: " + txtSoTaiKhoanNguon.getText().trim() + ")");
        lblReceiptDich.setText(lblTenChuDich.getText() + " (STK: " + txtSoTaiKhoanDich.getText().trim() + ")");
        lblReceiptNganHang.setText(cboNganHangDich.getValue());

        String strSoTien = txtSoTien.getText().replaceAll("[^0-9]", "");
        BigDecimal st = strSoTien.isEmpty() ? BigDecimal.ZERO : new BigDecimal(strSoTien);
        lblReceiptSoTien.setText(dfTien.format(st));
        lblReceiptBangChu.setText(lblTienBangChu.getText());

        String nd = txtNoiDung.getText().trim();
        lblReceiptNoiDung.setText(nd.isEmpty() ? "Chuyen tien tai quay SMART BANK" : nd);

        hienModal(modalInBienNhan);
    }

    @FXML
    public void onDongModalInBienNhan() {
        anModal(modalInBienNhan);
    }

    @FXML
    public void onInBienNhanThucTe() {
        anModal(modalInBienNhan);
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("In biên nhận");
        info.setHeaderText(null);
        info.setContentText("Lệnh in biên lai mã " + lastGeneratedMaGD + " đã được gửi đến máy in tại quầy!");
        info.showAndWait();
    }

    @FXML
    public void onLamMoi() {
        txtSoTaiKhoanNguon.clear();
        txtSoTaiKhoanDich.clear();
        txtSoTien.clear();
        txtNoiDung.clear();
        maTaiKhoanNguonDB = "";
        maTaiKhoanDichDB = "";
        tenNguoiNhanDich = null;
        lastGeneratedMaGD = "";
        trangThaiTKnguon = "";
        trangThaiTKdich = "";
        taiKhoanDichNoiBo = false;
        soDuHienTaiNguon = BigDecimal.ZERO;
        hanMucNgayConLai = BigDecimal.ZERO;

        lblTenChuNguon.setText("Chưa xác định");
        lblSoDuNguon.setText("0 VNĐ");
        lblTrangThaiNguon.setText("Chưa kiểm tra");
        lblTrangThaiNguon.setStyle("-fx-background-color: #F1F2F6; -fx-text-fill: #555A70; -fx-padding: 3 8; -fx-background-radius: 8;");
        lblHanMucNguon.setText("0 VNĐ");

        lblTenChuDich.setText("Chưa xác định");
        lblTrangThaiDich.setText("Chưa kiểm tra");
        lblTrangThaiDich.setStyle("-fx-background-color: #F1F2F6; -fx-text-fill: #555A70; -fx-padding: 3 8; -fx-background-radius: 8;");

        lblScoreCriteria.setText("0 / 6 ĐẠT");
        lblScoreCriteria.setStyle("-fx-background-color: #FEF3C7; -fx-text-fill: #D97706; -fx-padding: 3 8; -fx-background-radius: 8;");
        lblTrangThaiKiemTra.setText("Vui lòng bấm 'Kiểm tra điều kiện' trước khi xác nhận");
        lblTrangThaiKiemTra.setStyle("-fx-background-color: #F1F2F6; -fx-text-fill: #6B7280; -fx-padding: 10; -fx-background-radius: 10;");

        btnXacNhanChuyenTien.setDisable(true);
        btnInBienNhan.setDisable(true);
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

    private String docSoThanhChu(long n) {
        if (n == 0) return "Không";
        String[] donVi = {"", "nghìn", "triệu", "tỷ", "nghìn tỷ", "triệu tỷ"};
        String res = "";
        int i = 0;
        while (n > 0) {
            long phanDu = n % 1000;
            if (phanDu > 0) {
                res = phanDu + " " + donVi[i] + " " + res;
            }
            n /= 1000;
            i++;
        }
        return res.trim();
    }

    // =========================================================
    // SIDEBAR NAVIGATION
    // =========================================================
    @FXML private void onTrangChu(ActionEvent event) { moView("teller-dashboard-view.fxml", event); }
    @FXML private void onNapRutThanhToan(ActionEvent event) { moView("teller-cash-view.fxml", event); }
    @FXML private void onQuanLyTaiKhoan(ActionEvent event) { moView("teller-account-view.fxml", event); }
    @FXML private void onTraCuuSaoKe(ActionEvent event) { moView("teller-statement-view.fxml", event); }
    @FXML private void onTheoDoiTrangThai(ActionEvent event) { moView("teller-status-view.fxml", event); }
    @FXML private void onDangXuat(ActionEvent event) { moView("login-view.fxml", event); }
}
