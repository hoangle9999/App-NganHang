package org.example.appnganhang.controller.giaodichvien;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.example.appnganhang.controller.service.Impl.TellerCashServiceImpl;
import org.example.appnganhang.controller.service.intf.TellerCashService;
import org.example.appnganhang.model.entity.NganHang;
import org.example.appnganhang.model.entity.TellerCashAccountInfo;
import org.example.appnganhang.model.entity.TellerCashResult;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.example.appnganhang.controller.util.Navigation.moView;

public class TellerCashController {

    @FXML private Label lblTellerName;
    @FXML private Label lblTellerBranch;

    // Tabs
    @FXML private Button btnTabNapTien;
    @FXML private Button btnTabRutTien;
    @FXML private Button btnTabThanhToan;

    // Khung khách hàng
    @FXML private ComboBox<String> cboNganHang;
    @FXML private TextField txtSoTaiKhoan;
    @FXML private Button btnTraCuu;
    @FXML private Label lblTenKhachHang;
    @FXML private Label lblSoDuHienTai;
    @FXML private Label lblTrangThaiTaiKhoan;
    @FXML private Label lblChiNhanhMo;

    // Khung thao tác
    @FXML private Label lblTieuDeThaoTac;
    @FXML private Label lblBadgeMaLoaiGD;
    @FXML private TextField txtSoTien;
    @FXML private Label lblTienBangChu;
    @FXML private TextField txtNoiDung;
    @FXML private Label lblCanhBaoRutTien;

    // Buttons
    @FXML private Button btnThucHien;
    @FXML private Button btnInPhieu;

    // Modal 1: Xác nhận
    @FXML private VBox modalXacNhanTienMat;
    @FXML private Label lblModalXacNhanTieuDe;
    @FXML private Label lblModalLoaiGD;
    @FXML private Label lblModalSoTK;
    @FXML private Label lblModalTenKH;
    @FXML private Label lblModalSoTien;
    @FXML private Label lblModalNoiDung;

    // Modal 2: In phiếu thu chi
    @FXML private VBox modalInPhieuThuChi;
    @FXML private Label lblPhieuMaGD;
    @FXML private Label lblTieuDePhieu;
    @FXML private Label lblPhieuThoiGian;
    @FXML private Label lblPhieuTenKH;
    @FXML private Label lblPhieuSoTK;
    @FXML private Label lblPhieuSoTien;
    @FXML private Label lblPhieuBangChu;
    @FXML private Label lblPhieuSoDuSau;
    @FXML private Label lblPhieuNoiDung;
    @FXML private Label lblPhieuTeller;

    // Trạng thái nội bộ
    private String maLoaiGDDangChon = "NT01"; // NT01, RT01, TT01
    private BigDecimal soDuHienTai = BigDecimal.ZERO;
    private String trangThaiTK = "";
    private String maTaiKhoanDB = "";
    private String soTaiKhoanDaTraCuu = "";
    private String lastGeneratedMaGD = "";
    private BigDecimal soDuSauGD = BigDecimal.ZERO;
    private boolean dangDinhDangSoTien;

    private final DecimalFormat dfTien = new DecimalFormat("#,### VNĐ");
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private final TellerCashService tellerCashService = new TellerCashServiceImpl();

    @FXML
    public void initialize() {
        khoiTaoDanhSachNganHang();

        // Lắng nghe thay đổi số tiền để chuyển sang chữ và kiểm tra số dư rút tiền
        txtSoTien.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!dangDinhDangSoTien) dinhDangSoTien(newVal);
            capNhatTienBangChu();
            kiemTraSoDuRutTien();
        });

        // Mặc định chọn Tab Nạp tiền
        onChonTabNapTien();
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
        ObservableList<String> listNH = FXCollections.observableArrayList();
        try {
            for (NganHang bank : tellerCashService.layDanhSachNganHangNoiBo()) {
                listNH.add(bank.getTenNganHang() + " - Ngân hàng Hệ thống");
            }
        } catch (RuntimeException e) {
            hienCanhBao("Lỗi tải ngân hàng nội bộ: " + e.getMessage());
        }

        if (cboNganHang != null) {
            cboNganHang.setItems(listNH);
            cboNganHang.getSelectionModel().selectFirst();
        }
        if (listNH.isEmpty()) hienCanhBao("Không tìm thấy ngân hàng SMART BANK đang hoạt động trong cơ sở dữ liệu.");
    }

    // =========================================================
    // XỬ LÝ CHUYỂN ĐỔI 3 TABS: NT01, RT01, TT01
    // =========================================================
    @FXML
    public void onChonTabNapTien() {
        maLoaiGDDangChon = "NT01";
        datTrangThaiNutTab(btnTabNapTien, true);
        datTrangThaiNutTab(btnTabRutTien, false);
        datTrangThaiNutTab(btnTabThanhToan, false);

        lblTieuDeThaoTac.setText("NẠP TIỀN MẶT VÀO TÀI KHOẢN");
        lblBadgeMaLoaiGD.setText("MÃ GD: NT01");
        lblBadgeMaLoaiGD.setStyle("-fx-background-color: #E8F8F0; -fx-background-radius: 8; -fx-padding: 4 10; -fx-text-fill: #10B981; -fx-font-weight: bold; -fx-font-size: 12px;");

        lblCanhBaoRutTien.setVisible(false);
        lblCanhBaoRutTien.setManaged(false);

        if (txtNoiDung.getText().isEmpty() || txtNoiDung.getText().contains("Rut tien") || txtNoiDung.getText().contains("Thanh toan")) {
            txtNoiDung.setText("Nop tien mat vao tai khoan tai quay SMART BANK");
        }
    }

    @FXML
    public void onChonTabRutTien() {
        maLoaiGDDangChon = "RT01";
        datTrangThaiNutTab(btnTabNapTien, false);
        datTrangThaiNutTab(btnTabRutTien, true);
        datTrangThaiNutTab(btnTabThanhToan, false);

        lblTieuDeThaoTac.setText("RÚT TIỀN MẶT TẠI QUẦY");
        lblBadgeMaLoaiGD.setText("MÃ GD: RT01");
        lblBadgeMaLoaiGD.setStyle("-fx-background-color: #FEF3C7; -fx-background-radius: 8; -fx-padding: 4 10; -fx-text-fill: #D97706; -fx-font-weight: bold; -fx-font-size: 12px;");

        kiemTraSoDuRutTien();

        if (txtNoiDung.getText().isEmpty() || txtNoiDung.getText().contains("Nop tien") || txtNoiDung.getText().contains("Thanh toan")) {
            txtNoiDung.setText("Rut tien mat tai quay giao dich SMART BANK");
        }
    }

    @FXML
    public void onChonTabThanhToan() {
        maLoaiGDDangChon = "TT01";
        datTrangThaiNutTab(btnTabNapTien, false);
        datTrangThaiNutTab(btnTabRutTien, false);
        datTrangThaiNutTab(btnTabThanhToan, true);

        lblTieuDeThaoTac.setText("THANH TOÁN HÓA ĐƠN (TẠI QUẦY)");
        lblBadgeMaLoaiGD.setText("MÃ GD: TT01");
        lblBadgeMaLoaiGD.setStyle("-fx-background-color: #ECEBFA; -fx-background-radius: 8; -fx-padding: 4 10; -fx-text-fill: #5148C9; -fx-font-weight: bold; -fx-font-size: 12px;");

        kiemTraSoDuRutTien();

        if (txtNoiDung.getText().isEmpty() || txtNoiDung.getText().contains("Nop tien") || txtNoiDung.getText().contains("Rut tien")) {
            txtNoiDung.setText("Thanh toan hoa don dich vu tai quay SMART BANK");
        }
    }

    private void datTrangThaiNutTab(Button btn, boolean active) {
        if (active) {
            btn.setStyle("-fx-background-color: #5148C9; -fx-background-radius: 12; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; -fx-cursor: hand; -fx-padding: 8 20;");
        } else {
            btn.setStyle("-fx-background-color: #F1F2F6; -fx-background-radius: 12; -fx-text-fill: #4B5563; -fx-font-weight: bold; -fx-font-size: 13px; -fx-cursor: hand; -fx-padding: 8 20;");
        }
    }

    private void kiemTraSoDuRutTien() {
        if ("RT01".equals(maLoaiGDDangChon) || "TT01".equals(maLoaiGDDangChon)) {
            String strSoTien = txtSoTien.getText().replaceAll("[^0-9]", "");
            if (!strSoTien.isEmpty()) {
                BigDecimal st = new BigDecimal(strSoTien);
                if (st.compareTo(soDuHienTai) > 0) {
                    lblCanhBaoRutTien.setText("⚠ Số dư tài khoản không đủ để thực hiện rút tiền! (Khả dụng: " + dfTien.format(soDuHienTai) + ")");
                    lblCanhBaoRutTien.setVisible(true);
                    lblCanhBaoRutTien.setManaged(true);
                    btnThucHien.setDisable(true);
                    return;
                }
            }
        }
        lblCanhBaoRutTien.setVisible(false);
        lblCanhBaoRutTien.setManaged(false);
        btnThucHien.setDisable(false);
    }

    @FXML
    public void onTraCuuTaiKhoan() {
        String stk = txtSoTaiKhoan.getText().trim();
        if (stk.isEmpty()) {
            hienCanhBao("Vui lòng nhập số tài khoản khách hàng cần tra cứu!");
            return;
        }

        try {
            TellerCashAccountInfo account = tellerCashService.traCuuTaiKhoan(stk)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thông tin tài khoản: " + stk));
            maTaiKhoanDB = account.getMaTaiKhoan();
            soTaiKhoanDaTraCuu = account.getSoTaiKhoan();
            soDuHienTai = account.getSoDu();
            trangThaiTK = account.getTrangThai();
            lblTenKhachHang.setText(account.getTenKhachHang());
            lblSoDuHienTai.setText(dfTien.format(soDuHienTai));
            lblTrangThaiTaiKhoan.setText(trangThaiTK);
            lblChiNhanhMo.setText(account.getTenChiNhanh() != null ? account.getTenChiNhanh() : "Hội sở chính SMART BANK");

            if ("Hoạt động".equalsIgnoreCase(trangThaiTK)) {
                lblTrangThaiTaiKhoan.setStyle("-fx-background-color: #E8F8F0; -fx-background-radius: 8; -fx-padding: 3 8; -fx-text-fill: #10B981; -fx-font-weight: bold; -fx-font-size: 11px;");
            } else {
                lblTrangThaiTaiKhoan.setStyle("-fx-background-color: #FDECEC; -fx-background-radius: 8; -fx-padding: 3 8; -fx-text-fill: #D9534F; -fx-font-weight: bold; -fx-font-size: 11px;");
            }
            kiemTraSoDuRutTien();
        } catch (RuntimeException e) {
            hienCanhBao(e.getMessage());
            maTaiKhoanDB = "";
            soTaiKhoanDaTraCuu = "";
            soDuHienTai = BigDecimal.ZERO;
            trangThaiTK = "Không tồn tại";
            lblTenKhachHang.setText("Không tìm thấy");
            lblSoDuHienTai.setText(dfTien.format(soDuHienTai));
            lblTrangThaiTaiKhoan.setText(trangThaiTK);
            lblTrangThaiTaiKhoan.setStyle("-fx-background-color: #FDECEC; -fx-text-fill: #D9534F; -fx-padding: 3 8; -fx-background-radius: 8;");
            lblChiNhanhMo.setText("Hội sở SMART BANK");
        }
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
    public void onMoModalXacNhan() {
        if (maTaiKhoanDB.isBlank() || !txtSoTaiKhoan.getText().trim().equals(soTaiKhoanDaTraCuu)) {
            hienCanhBao("Vui lòng tra cứu lại tài khoản trước khi tạo giao dịch.");
            return;
        }
        if (!"Hoạt động".equalsIgnoreCase(trangThaiTK)) {
            hienCanhBao("Tài khoản không ở trạng thái hoạt động.");
            return;
        }
        String strSoTien = txtSoTien.getText().replaceAll("[^0-9]", "");
        if (strSoTien.isEmpty() || new BigDecimal(strSoTien).compareTo(BigDecimal.ZERO) <= 0) {
            hienCanhBao("Vui lòng nhập số tiền giao dịch hợp lệ (> 0 VNĐ)!");
            return;
        }

        BigDecimal st = new BigDecimal(strSoTien);

        if (("RT01".equals(maLoaiGDDangChon) || "TT01".equals(maLoaiGDDangChon)) && st.compareTo(soDuHienTai) > 0) {
            hienCanhBao("Số dư tài khoản không đủ để thực hiện giao dịch này!");
            return;
        }

        String tenLoai = "NẠP TIỀN MẶT";
        if ("RT01".equals(maLoaiGDDangChon)) tenLoai = "RÚT TIỀN MẶT";
        else if ("TT01".equals(maLoaiGDDangChon)) tenLoai = "THANH TOÁN HÓA ĐƠN";

        lblModalXacNhanTieuDe.setText("XÁC NHẬN " + tenLoai);
        lblModalLoaiGD.setText(maLoaiGDDangChon + " - " + tenLoai);
        lblModalSoTK.setText(txtSoTaiKhoan.getText().trim());
        lblModalTenKH.setText(lblTenKhachHang.getText());
        lblModalSoTien.setText(dfTien.format(st));
        lblModalNoiDung.setText(txtNoiDung.getText().trim());

        hienModal(modalXacNhanTienMat);
    }

    @FXML
    public void onDongModalXacNhan() {
        anModal(modalXacNhanTienMat);
    }

    @FXML
    public void onThucHienGiaoDichThucTe() {
        anModal(modalXacNhanTienMat);

        String strSoTien = txtSoTien.getText().replaceAll("[^0-9]", "");
        if (strSoTien.isEmpty()) {
            hienCanhBao("Vui lòng nhập số tiền giao dịch hợp lệ.");
            return;
        }
        BigDecimal soTien = new BigDecimal(strSoTien);
        String stk = txtSoTaiKhoan.getText().trim();
        String noiDung = txtNoiDung.getText().trim();
        try {
            TellerCashResult result = tellerCashService.thucHienGiaoDich(
                    maLoaiGDDangChon + "-" + System.currentTimeMillis(), stk, maLoaiGDDangChon, soTien, noiDung);
            lastGeneratedMaGD = result.getMaGiaoDich();
            soDuSauGD = result.getSoDuSauGiaoDich();
            soDuHienTai = soDuSauGD;
        } catch (RuntimeException e) {
            hienCanhBao(e.getMessage());
            btnInPhieu.setDisable(true);
            return;
        }
        lblSoDuHienTai.setText(dfTien.format(soDuHienTai));

        // Kích hoạt nút In phiếu
        btnInPhieu.setDisable(false);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thành công");
        alert.setHeaderText("Giao dịch " + ("NT01".equals(maLoaiGDDangChon) ? "Nạp tiền" : ("RT01".equals(maLoaiGDDangChon) ? "Rút tiền" : "Thanh toán")) + " thành công!");
        alert.setContentText("Mã giao dịch: " + lastGeneratedMaGD + "\nSố tiền: " + dfTien.format(soTien) + "\nSố dư mới: " + dfTien.format(soDuSauGD));
        alert.showAndWait();
    }

    @FXML
    public void onMoModalInPhieu() {
        lblPhieuMaGD.setText(lastGeneratedMaGD);
        lblPhieuThoiGian.setText("Thời gian: " + LocalDateTime.now().format(dtf));

        if ("NT01".equals(maLoaiGDDangChon)) {
            lblTieuDePhieu.setText("PHIẾU THU TIỀN MẶT (CASH RECEIPT)");
        } else if ("RT01".equals(maLoaiGDDangChon)) {
            lblTieuDePhieu.setText("PHIẾU CHI TIỀN MẶT (PAYMENT VOUCHER)");
        } else {
            lblTieuDePhieu.setText("HÓA ĐƠN THANH TOÁN DỊCH VỤ");
        }

        lblPhieuTenKH.setText(lblTenKhachHang.getText());
        lblPhieuSoTK.setText(txtSoTaiKhoan.getText().trim());

        String strSoTien = txtSoTien.getText().replaceAll("[^0-9]", "");
        BigDecimal st = strSoTien.isEmpty() ? BigDecimal.ZERO : new BigDecimal(strSoTien);
        lblPhieuSoTien.setText(dfTien.format(st));
        lblPhieuBangChu.setText(lblTienBangChu.getText());
        lblPhieuSoDuSau.setText(dfTien.format(soDuSauGD));
        lblPhieuNoiDung.setText(txtNoiDung.getText().trim());

        hienModal(modalInPhieuThuChi);
    }

    @FXML
    public void onDongModalInPhieu() {
        anModal(modalInPhieuThuChi);
    }

    @FXML
    public void onInPhieuThucTe() {
        anModal(modalInPhieuThuChi);
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("In phiếu");
        info.setHeaderText(null);
        info.setContentText("Lệnh in phiếu " + lastGeneratedMaGD + " đã được gửi tới máy in tại quầy!");
        info.showAndWait();
    }

    @FXML
    public void onHuyBo() {
        txtSoTaiKhoan.clear();
        txtSoTien.clear();
        txtNoiDung.clear();

        lblTenKhachHang.setText("Chưa xác định");
        lblSoDuHienTai.setText("0 VNĐ");
        lblTrangThaiTaiKhoan.setText("Chưa kiểm tra");
        lblTrangThaiTaiKhoan.setStyle("-fx-background-color: #F1F2F6; -fx-text-fill: #555A70; -fx-padding: 3 8; -fx-background-radius: 8;");
        lblChiNhanhMo.setText("Hội sở SMART BANK");

        soDuHienTai = BigDecimal.ZERO;
        soDuSauGD = BigDecimal.ZERO;
        maTaiKhoanDB = "";
        soTaiKhoanDaTraCuu = "";
        trangThaiTK = "";
        lastGeneratedMaGD = "";
        btnInPhieu.setDisable(true);
        lblCanhBaoRutTien.setVisible(false);
        lblCanhBaoRutTien.setManaged(false);

        onChonTabNapTien();
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
    @FXML private void onChuyenTien(ActionEvent event) { moView("teller-transfer-view.fxml", event); }
    @FXML private void onQuanLyTaiKhoan(ActionEvent event) { moView("teller-account-view.fxml", event); }
    @FXML private void onTraCuuSaoKe(ActionEvent event) { moView("teller-statement-view.fxml", event); }
    @FXML private void onTheoDoiTrangThai(ActionEvent event) { moView("teller-status-view.fxml", event); }
    @FXML private void onDangXuat(ActionEvent event) { moView("login-view.fxml", event); }
}
