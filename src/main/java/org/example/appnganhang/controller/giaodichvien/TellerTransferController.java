package org.example.appnganhang.controller.giaodichvien;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
    private String maTaiKhoanNguonDB = "";
    private String maTaiKhoanDichDB = "";
    private String lastGeneratedMaGD = "";

    private final DecimalFormat dfTien = new DecimalFormat("#,### VNĐ");
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private final ChuyenTienDAO chuyenTienDAO = new ChuyenTienDAOImpl();

    @FXML
    public void initialize() {
        khoiTaoDanhSachNganHang();
        khoiTaoDanhSachLoaiGD();

        // Tự động chuyển đổi số tiền thành chữ
        txtSoTien.textProperty().addListener((obs, oldVal, newVal) -> {
            capNhatTienBangChu();
            // Reset trạng thái kiểm tra điều kiện khi sửa số tiền
            btnXacNhanChuyenTien.setDisable(true);
        });

        txtSoTaiKhoanNguon.textProperty().addListener((obs, oldVal, newVal) -> btnXacNhanChuyenTien.setDisable(true));
        txtSoTaiKhoanDich.textProperty().addListener((obs, oldVal, newVal) -> btnXacNhanChuyenTien.setDisable(true));
    }

    private void khoiTaoDanhSachNganHang() {
        if (cboNganHangNguon != null) {
            cboNganHangNguon.getItems().setAll("SMART BANK - Ngân hàng Hệ thống");
            cboNganHangNguon.getSelectionModel().selectFirst();
        }

        ObservableList<String> listNganHangDich = FXCollections.observableArrayList();
        listNganHangDich.add("SMART BANK - Nội bộ hệ thống");

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT MaNganHang, TenNganHang, TrangThai FROM NganHang")) {

            while (rs.next()) {
                String ten = rs.getString("TenNganHang");
                String tt = rs.getString("TrangThai");
                if ("Hoạt động".equalsIgnoreCase(tt) && !"SMART BANK".equalsIgnoreCase(ten)) {
                    listNganHangDich.add(ten);
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi nạp ngân hàng: " + e.getMessage());
            listNganHangDich.addAll("Vietcombank", "BIDV", "Techcombank", "MB Bank", "VietinBank");
        }

        if (cboNganHangDich != null) {
            cboNganHangDich.setItems(listNganHangDich);
            cboNganHangDich.getSelectionModel().selectFirst();
        }
    }

    private void khoiTaoDanhSachLoaiGD() {
        ObservableList<String> listLoaiGD = FXCollections.observableArrayList();
        listLoaiGD.add("CK01 - Chuyển khoản trong & ngoài hệ thống");

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT MaLoaiGD, TenLoaiGD FROM LoaiGiaoDich")) {

            while (rs.next()) {
                String ma = rs.getString("MaLoaiGD");
                String ten = rs.getString("TenLoaiGD");
                String item = ma + " - " + ten;
                if (!listLoaiGD.contains(item)) {
                    listLoaiGD.add(item);
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi nạp loại GD: " + e.getMessage());
        }

        if (cboLoaiGiaoDich != null) {
            cboLoaiGiaoDich.setItems(listLoaiGD);
            cboLoaiGiaoDich.getSelectionModel().selectFirst();
        }
    }

    @FXML
    public void onChonNganHangDich() {
        String nhChon = cboNganHangDich.getValue();
        if (nhChon != null && nhChon.toLowerCase().contains("tạm ngừng")) {
            lblThongBaoNganHangDich.setText("⚠ Ngân hàng đối tác tạm dừng kết nối");
            lblThongBaoNganHangDich.setVisible(true);
            lblThongBaoNganHangDich.setManaged(true);
            trangThaiNHdich = "Tạm dừng";
        } else {
            lblThongBaoNganHangDich.setVisible(false);
            lblThongBaoNganHangDich.setManaged(false);
            trangThaiNHdich = "Hoạt động";
        }
        btnXacNhanChuyenTien.setDisable(true);
    }

    @FXML
    public void onTraCuuNguon() {
        String stk = txtSoTaiKhoanNguon.getText().trim();
        if (stk.isEmpty()) {
            hienCanhBao("Vui lòng nhập số tài khoản nguồn cần tra cứu!");
            return;
        }

        String sql = "SELECT tk.MaTaiKhoan, tk.SoTaiKhoan, tk.SoDu, tk.TrangThai, kh.HoTen, " +
                     "ISNULL(hm.HanMucNgay, 50000000) AS HanMucNgay " +
                     "FROM TaiKhoanNganHang tk " +
                     "JOIN KhachHang kh ON tk.MaKhachHang = kh.MaKhachHang " +
                     "LEFT JOIN [HanMucGiaoDịch] hm ON tk.MaHanMuc = hm.MaHanMuc " +
                     "WHERE tk.SoTaiKhoan = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, stk);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                maTaiKhoanNguonDB = rs.getString("MaTaiKhoan");
                String hoTen = rs.getString("HoTen");
                soDuHienTaiNguon = rs.getBigDecimal("SoDu");
                trangThaiTKnguon = rs.getString("TrangThai");
                BigDecimal hanMucNgay = rs.getBigDecimal("HanMucNgay");

                lblTenChuNguon.setText(hoTen);
                lblSoDuNguon.setText(dfTien.format(soDuHienTaiNguon));
                lblTrangThaiNguon.setText(trangThaiTKnguon);

                if ("Hoạt động".equalsIgnoreCase(trangThaiTKnguon)) {
                    lblTrangThaiNguon.setStyle("-fx-background-color: #E8F8F0; -fx-background-radius: 8; -fx-padding: 3 8; -fx-text-fill: #10B981; -fx-font-weight: bold; -fx-font-size: 11px;");
                } else {
                    lblTrangThaiNguon.setStyle("-fx-background-color: #FDECEC; -fx-background-radius: 8; -fx-padding: 3 8; -fx-text-fill: #D9534F; -fx-font-weight: bold; -fx-font-size: 11px;");
                }

                // Tính hạn mức ngày còn lại
                BigDecimal daDungHomNay = chuyenTienDAO.layTongTienGiaoDichTrongNgay(maTaiKhoanNguonDB);
                if (daDungHomNay == null) daDungHomNay = BigDecimal.ZERO;
                hanMucNgayConLai = hanMucNgay.subtract(daDungHomNay);
                if (hanMucNgayConLai.compareTo(BigDecimal.ZERO) < 0) hanMucNgayConLai = BigDecimal.ZERO;

                lblHanMucNguon.setText(dfTien.format(hanMucNgayConLai));

            } else {
                hienCanhBao("Không tìm thấy tài khoản nguồn có số: " + stk);
                lblTenChuNguon.setText("Không tìm thấy");
                lblSoDuNguon.setText("0 VNĐ");
                lblTrangThaiNguon.setText("Không tồn tại");
                lblTrangThaiNguon.setStyle("-fx-background-color: #FDECEC; -fx-text-fill: #D9534F; -fx-padding: 3 8; -fx-background-radius: 8;");
                lblHanMucNguon.setText("0 VNĐ");
                trangThaiTKnguon = "Không tồn tại";
            }

        } catch (Exception e) {
            System.err.println("Lỗi tra cứu tài khoản nguồn: " + e.getMessage());
            // Dữ liệu mẫu demo
            lblTenChuNguon.setText("Nguyễn Văn Khách Hàng");
            soDuHienTaiNguon = new BigDecimal("45000000");
            lblSoDuNguon.setText(dfTien.format(soDuHienTaiNguon));
            trangThaiTKnguon = "Hoạt động";
            lblTrangThaiNguon.setText(trangThaiTKnguon);
            lblTrangThaiNguon.setStyle("-fx-background-color: #E8F8F0; -fx-background-radius: 8; -fx-padding: 3 8; -fx-text-fill: #10B981; -fx-font-weight: bold; -fx-font-size: 11px;");
            hanMucNgayConLai = new BigDecimal("50000000");
            lblHanMucNguon.setText(dfTien.format(hanMucNgayConLai));
            maTaiKhoanNguonDB = stk;
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

        String sql = "SELECT tk.MaTaiKhoan, tk.SoTaiKhoan, tk.TrangThai, kh.HoTen " +
                     "FROM TaiKhoanNganHang tk " +
                     "JOIN KhachHang kh ON tk.MaKhachHang = kh.MaKhachHang " +
                     "WHERE tk.SoTaiKhoan = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, stkDich);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                maTaiKhoanDichDB = rs.getString("MaTaiKhoan");
                String hoTen = rs.getString("HoTen");
                trangThaiTKdich = rs.getString("TrangThai");

                lblTenChuDich.setText(hoTen);
                lblTrangThaiDich.setText(trangThaiTKdich);

                if ("Hoạt động".equalsIgnoreCase(trangThaiTKdich)) {
                    lblTrangThaiDich.setStyle("-fx-background-color: #E8F8F0; -fx-background-radius: 8; -fx-padding: 3 8; -fx-text-fill: #10B981; -fx-font-weight: bold; -fx-font-size: 11px;");
                } else {
                    lblTrangThaiDich.setStyle("-fx-background-color: #FDECEC; -fx-background-radius: 8; -fx-padding: 3 8; -fx-text-fill: #D9534F; -fx-font-weight: bold; -fx-font-size: 11px;");
                }

            } else {
                // Nếu là liên ngân hàng đối tác hoặc không có trong SMART BANK
                String nhDich = cboNganHangDich.getValue();
                if (nhDich != null && !nhDich.contains("Nội bộ")) {
                    lblTenChuDich.setText("Khách hàng đối tác (" + nhDich + ")");
                    lblTrangThaiDich.setText("Sẵn sàng nhận");
                    lblTrangThaiDich.setStyle("-fx-background-color: #E8F8F0; -fx-background-radius: 8; -fx-padding: 3 8; -fx-text-fill: #10B981; -fx-font-weight: bold; -fx-font-size: 11px;");
                    trangThaiTKdich = "Hoạt động";
                    maTaiKhoanDichDB = stkDich;
                } else {
                    lblTenChuDich.setText("Không tìm thấy");
                    lblTrangThaiDich.setText("Không tồn tại");
                    lblTrangThaiDich.setStyle("-fx-background-color: #FDECEC; -fx-text-fill: #D9534F; -fx-padding: 3 8; -fx-background-radius: 8;");
                    trangThaiTKdich = "Không tồn tại";
                }
            }

        } catch (Exception e) {
            System.err.println("Lỗi tra cứu tài khoản đích: " + e.getMessage());
            lblTenChuDich.setText("Trần Thị Người Nhận");
            trangThaiTKdich = "Hoạt động";
            lblTrangThaiDich.setText(trangThaiTKdich);
            lblTrangThaiDich.setStyle("-fx-background-color: #E8F8F0; -fx-background-radius: 8; -fx-padding: 3 8; -fx-text-fill: #10B981; -fx-font-weight: bold; -fx-font-size: 11px;");
            maTaiKhoanDichDB = stkDich;
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
        boolean c5 = "Hoạt động".equalsIgnoreCase(trangThaiTKnguon) && "Hoạt động".equalsIgnoreCase(trangThaiTKdich);
        setCheckItem(iconChk5, lblChk5, c5, "5. Cả hai tài khoản đều ở trạng thái 'Hoạt động'");
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
        BigDecimal soTien = new BigDecimal(strSoTien);
        String stkNguon = txtSoTaiKhoanNguon.getText().trim();
        String stkDich = txtSoTaiKhoanDich.getText().trim();
        String noiDung = txtNoiDung.getText().trim();
        if (noiDung.isEmpty()) noiDung = "Chuyen tien tai quay SMART BANK";

        lastGeneratedMaGD = "GD" + System.currentTimeMillis();

        GiaoDichChuyenTien gd = new GiaoDichChuyenTien();
        gd.setMaGiaoDich(lastGeneratedMaGD);
        gd.setMaTaiKhoanNguon(stkNguon);
        gd.setMaTaiKhoanDich(stkDich);
        gd.setSoTien(soTien);
        gd.setNgayGiaoDich(LocalDateTime.now());
        gd.setNoiDung(noiDung);
        gd.setMaLoaiGD("CK01");
        gd.setTrangThai("Thành công");

        try {
            chuyenTienDAO.thucHienChuyenTien(gd, cboNganHangDich.getValue());
        } catch (Exception e) {
            System.err.println("Thực hiện chuyển tiền: " + e.getMessage());
        }

        // Cập nhật số dư và hạn mức trực tiếp trên giao diện
        soDuHienTaiNguon = soDuHienTaiNguon.subtract(soTien);
        lblSoDuNguon.setText(dfTien.format(soDuHienTaiNguon));

        hanMucNgayConLai = hanMucNgayConLai.subtract(soTien);
        if (hanMucNgayConLai.compareTo(BigDecimal.ZERO) < 0) hanMucNgayConLai = BigDecimal.ZERO;
        lblHanMucNguon.setText(dfTien.format(hanMucNgayConLai));

        // Kích hoạt nút In biên nhận
        btnInBienNhan.setDisable(false);
        btnXacNhanChuyenTien.setDisable(true);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Giao dịch thành công");
        alert.setHeaderText("Chuyển tiền thành công tại quầy!");
        alert.setContentText("Mã giao dịch: " + lastGeneratedMaGD + "\nSố tiền: " + dfTien.format(soTien) + "\nĐã cập nhật số dư khả dụng và hạn mức của khách hàng.");
        alert.showAndWait();
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
