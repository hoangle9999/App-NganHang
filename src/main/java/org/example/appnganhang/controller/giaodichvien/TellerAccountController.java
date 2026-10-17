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
import org.example.appnganhang.model.dao.Impl.KhachHangDAOImpl;
import org.example.appnganhang.model.dao.Impl.TaiKhoanDAOImpl;
import org.example.appnganhang.model.dao.intf.KhachHangDAO;
import org.example.appnganhang.model.dao.intf.TaiKhoanDAO;
import org.example.appnganhang.model.entity.KhachHang;
import org.example.appnganhang.model.entity.TaiKhoanNganHang;
import org.example.appnganhang.model.util.DBConnection;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.Optional;

import static org.example.appnganhang.controller.util.Navigation.moView;

public class TellerAccountController {

    @FXML private Label lblTellerName;
    @FXML private Label lblTellerBranch;
    @FXML private Label lblTongTaiKhoan;

    // Bộ lọc
    @FXML private TextField txtTimKiem;
    @FXML private ComboBox<String> cboLocTrangThai;
    @FXML private ComboBox<String> cboLocChiNhanh;

    // Bảng tài khoản
    @FXML private TableView<TaiKhoanRow> tblTaiKhoan;
    @FXML private TableColumn<TaiKhoanRow, String> colMaTK;
    @FXML private TableColumn<TaiKhoanRow, String> colSoTK;
    @FXML private TableColumn<TaiKhoanRow, String> colTenKH;
    @FXML private TableColumn<TaiKhoanRow, String> colCCCD;
    @FXML private TableColumn<TaiKhoanRow, String> colSoDu;
    @FXML private TableColumn<TaiKhoanRow, String> colNganHang;
    @FXML private TableColumn<TaiKhoanRow, String> colChiNhanh;
    @FXML private TableColumn<TaiKhoanRow, String> colHanMuc;
    @FXML private TableColumn<TaiKhoanRow, String> colTrangThai;
    @FXML private TableColumn<TaiKhoanRow, Void> colThaoTac;

    // Modal 1: Mở tài khoản 3 bước
    @FXML private VBox modalTaoTaiKhoan;
    @FXML private Label lblStep1, lblStep2, lblStep3;
    @FXML private VBox panelStep1, panelStep2, panelStep3;
    @FXML private TextField txtStep1CCCD;
    @FXML private VBox boxStep1KetQua;
    @FXML private Label lblStep1ThongBao, lblStep1MaKH, lblStep1HoTen, lblStep1TenDangNhap;

    @FXML private TextField txtStep2TenDangNhap;
    @FXML private PasswordField txtStep2MatKhau;
    @FXML private TextField txtStep2HoTen;
    @FXML private TextField txtStep2SDT;
    @FXML private TextField txtStep2Email;
    @FXML private TextField txtStep2DiaChi;

    @FXML private TextField txtStep3SoTK;
    @FXML private ComboBox<String> cboStep3NganHang;
    @FXML private ComboBox<String> cboStep3ChiNhanh;
    @FXML private ComboBox<String> cboStep3HanMuc;
    @FXML private Button btnXacNhanMoTK;

    // Modal 2: Đổi trạng thái
    @FXML private VBox modalDoiTrangThai;
    @FXML private Label lblModalStatusSoTK, lblModalStatusTenKH, lblModalStatusHienTai;
    @FXML private ComboBox<String> cboModalTrangThaiMoi;
    @FXML private TextField txtModalLyDoDoiTrangThai;

    // Modal 3: Gán hạn mức
    @FXML private VBox modalGanHanMuc;
    @FXML private Label lblModalHanMucSoTK, lblModalHanMucTenKH, lblModalHanMucHienTai;
    @FXML private ComboBox<String> cboModalHanMucMoi;

    // Modal 4: Chi tiết tài khoản
    @FXML private VBox modalChiTietTK;
    @FXML private Label lblDetailMaTK, lblDetailSoTK, lblDetailTenKH, lblDetailCCCD, lblDetailSoDu, lblDetailChiNhanh, lblDetailHanMuc, lblDetailTrangThai;

    private final ObservableList<TaiKhoanRow> danhSachTaiKhoan = FXCollections.observableArrayList();
    private final ObservableList<TaiKhoanRow> danhSachHienThi = FXCollections.observableArrayList();
    private final DecimalFormat dfTien = new DecimalFormat("#,### VNĐ");

    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAOImpl();
    private final KhachHangDAO khachHangDAO = new KhachHangDAOImpl();

    // Biến lưu tạm khi thực hiện wizard 3 bước
    private String currentMaKhachHang = "";
    private String currentHoTen = "";
    private TaiKhoanRow currentSelectedTK = null;

    @FXML
    public void initialize() {
        khoiTaoBoLoc();
        cauHinhBangTaiKhoan();
        taiDuLieuTaiKhoan();
        khoiTaoDuLieuWizard();
    }

    private void khoiTaoBoLoc() {
        if (cboLocTrangThai != null) {
            cboLocTrangThai.getItems().setAll("Tất cả trạng thái", "Hoạt động", "Tạm khóa", "Đã khóa", "Chờ kích hoạt");
            cboLocTrangThai.getSelectionModel().selectFirst();
        }

        ObservableList<String> listCN = FXCollections.observableArrayList();
        listCN.add("Tất cả chi nhánh");
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT TenChiNhanh FROM ChiNhanh")) {
            while (rs.next()) {
                listCN.add(rs.getString("TenChiNhanh"));
            }
        } catch (Exception e) {
            listCN.addAll("Hội sở chính", "Chi nhánh Ba Đình", "Chi nhánh Quận 1", "Chi nhánh Đà Nẵng");
        }

        if (cboLocChiNhanh != null) {
            cboLocChiNhanh.setItems(listCN);
            cboLocChiNhanh.getSelectionModel().selectFirst();
        }
    }

    private void khoiTaoDuLieuWizard() {
        if (cboStep3NganHang != null) {
            cboStep3NganHang.getItems().setAll("SMART BANK (Hệ thống)");
            cboStep3NganHang.getSelectionModel().selectFirst();
        }

        ObservableList<String> listCN = FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT MaChiNhanh, TenChiNhanh FROM ChiNhanh")) {
            while (rs.next()) {
                listCN.add(rs.getString("MaChiNhanh") + " - " + rs.getString("TenChiNhanh"));
            }
        } catch (Exception e) {
            listCN.addAll("CN01 - Trụ sở chính", "CN02 - Chi nhánh Ba Đình", "CN03 - Chi nhánh Quận 1");
        }
        if (cboStep3ChiNhanh != null) {
            cboStep3ChiNhanh.setItems(listCN);
            cboStep3ChiNhanh.getSelectionModel().selectFirst();
        }

        ObservableList<String> listHM = FXCollections.observableArrayList();
        listHM.add("Mặc định - 50.000.000 VNĐ/ngày");
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT MaHanMuc, TenHanMuc, HanMucNgay FROM [HanMucGiaoDịch]")) {
            while (rs.next()) {
                String ma = rs.getString("MaHanMuc");
                String ten = rs.getString("TenHanMuc");
                BigDecimal tien = rs.getBigDecimal("HanMucNgay");
                listHM.add(ma + " - " + ten + " (" + dfTien.format(tien) + "/ngày)");
            }
        } catch (Exception e) {
            listHM.addAll("HM01 - Tiêu chuẩn (50,000,000 VNĐ)", "HM02 - VIP (200,000,000 VNĐ)", "HM03 - Doanh nghiệp (1,000,000,000 VNĐ)");
        }
        if (cboStep3HanMuc != null) {
            cboStep3HanMuc.setItems(listHM);
            cboStep3HanMuc.getSelectionModel().selectFirst();
        }

        if (cboModalTrangThaiMoi != null) {
            cboModalTrangThaiMoi.getItems().setAll("Hoạt động", "Tạm khóa", "Đã khóa");
            cboModalTrangThaiMoi.getSelectionModel().selectFirst();
        }

        if (cboModalHanMucMoi != null) {
            cboModalHanMucMoi.setItems(listHM);
            cboModalHanMucMoi.getSelectionModel().selectFirst();
        }
    }

    private void cauHinhBangTaiKhoan() {
        colMaTK.setCellValueFactory(new PropertyValueFactory<>("maTaiKhoan"));
        colSoTK.setCellValueFactory(new PropertyValueFactory<>("soTaiKhoan"));
        colTenKH.setCellValueFactory(new PropertyValueFactory<>("tenKhachHang"));
        colCCCD.setCellValueFactory(new PropertyValueFactory<>("soCCCD"));

        colSoDu.setCellValueFactory(cellData -> {
            BigDecimal sd = cellData.getValue().getSoDu();
            return new SimpleStringProperty(sd != null ? dfTien.format(sd) : "0 VNĐ");
        });

        colNganHang.setCellValueFactory(new PropertyValueFactory<>("tenNganHang"));
        colChiNhanh.setCellValueFactory(new PropertyValueFactory<>("tenChiNhanh"));
        colHanMuc.setCellValueFactory(new PropertyValueFactory<>("tenHanMuc"));

        colTrangThai.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    TaiKhoanRow tk = getTableRow().getItem();
                    String tt = tk.getTrangThai() != null ? tk.getTrangThai() : "Hoạt động";
                    Label badge = new Label(tt);

                    if ("Hoạt động".equalsIgnoreCase(tt)) {
                        badge.setStyle("-fx-background-color: #E8F8F0; -fx-text-fill: #10B981; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 8; -fx-font-size: 11px;");
                    } else if (tt.toLowerCase().contains("tạm") || tt.toLowerCase().contains("chờ")) {
                        badge.setStyle("-fx-background-color: #FEF3C7; -fx-text-fill: #D97706; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 8; -fx-font-size: 11px;");
                    } else {
                        badge.setStyle("-fx-background-color: #FDECEC; -fx-text-fill: #D9534F; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 8; -fx-font-size: 11px;");
                    }

                    HBox box = new HBox(badge);
                    box.setAlignment(Pos.CENTER);
                    setGraphic(box);
                }
            }
        });

        // Cột Thao tác: [Xem chi tiết], [Đổi trạng thái], [Gán hạn mức]
        colThaoTac.setCellFactory(param -> new TableCell<>() {
            private final Button btnChiTiet = new Button("Xem");
            private final Button btnTrangThai = new Button("Đổi TT");
            private final Button btnHanMuc = new Button("Gán HM");
            private final HBox box = new HBox(6, btnChiTiet, btnTrangThai, btnHanMuc);

            {
                box.setAlignment(Pos.CENTER);
                btnChiTiet.setStyle("-fx-background-color: #ECEBFA; -fx-text-fill: #5148C9; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand; -fx-font-size: 11px; -fx-padding: 4 8;");
                btnTrangThai.setStyle("-fx-background-color: #FEF3C7; -fx-text-fill: #D97706; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand; -fx-font-size: 11px; -fx-padding: 4 8;");
                btnHanMuc.setStyle("-fx-background-color: #E8F8F0; -fx-text-fill: #10B981; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand; -fx-font-size: 11px; -fx-padding: 4 8;");

                btnChiTiet.setOnAction(e -> {
                    TaiKhoanRow row = getTableView().getItems().get(getIndex());
                    moModalChiTiet(row);
                });

                btnTrangThai.setOnAction(e -> {
                    TaiKhoanRow row = getTableView().getItems().get(getIndex());
                    moModalDoiTrangThai(row);
                });

                btnHanMuc.setOnAction(e -> {
                    TaiKhoanRow row = getTableView().getItems().get(getIndex());
                    moModalGanHanMuc(row);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else setGraphic(box);
            }
        });

        tblTaiKhoan.setItems(danhSachHienThi);
    }

    private void taiDuLieuTaiKhoan() {
        danhSachTaiKhoan.clear();

        String sql = "SELECT tk.MaTaiKhoan, tk.SoTaiKhoan, tk.SoDu, tk.TrangThai, " +
                     "kh.HoTen, kh.SoCCCD, kh.SoDienThoai, kh.Email, kh.DiaChi, " +
                     "ISNULL(cn.TenChiNhanh, N'Hội sở SMART BANK') AS TenChiNhanh, " +
                     "ISNULL(hm.TenHanMuc, N'Tiêu chuẩn') AS TenHanMuc, " +
                     "ISNULL(nh.TenNganHang, N'SMART BANK') AS TenNganHang " +
                     "FROM TaiKhoanNganHang tk " +
                     "JOIN KhachHang kh ON tk.MaKhachHang = kh.MaKhachHang " +
                     "LEFT JOIN ChiNhanh cn ON tk.MaChiNhanh = cn.MaChiNhanh " +
                     "LEFT JOIN [HanMucGiaoDịch] hm ON tk.MaHanMuc = hm.MaHanMuc " +
                     "LEFT JOIN NganHang nh ON tk.MaNganHang = nh.MaNganHang " +
                     "ORDER BY tk.MaTaiKhoan DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                TaiKhoanRow r = new TaiKhoanRow();
                r.setMaTaiKhoan(rs.getString("MaTaiKhoan"));
                r.setSoTaiKhoan(rs.getString("SoTaiKhoan"));
                r.setSoDu(rs.getBigDecimal("SoDu"));
                r.setTrangThai(rs.getString("TrangThai"));
                r.setTenKhachHang(rs.getString("HoTen"));
                r.setSoCCCD(rs.getString("SoCCCD"));
                r.setSoDienThoai(rs.getString("SoDienThoai"));
                r.setEmail(rs.getString("Email"));
                r.setDiaChi(rs.getString("DiaChi"));
                r.setTenChiNhanh(rs.getString("TenChiNhanh"));
                r.setTenHanMuc(rs.getString("TenHanMuc"));
                r.setTenNganHang(rs.getString("TenNganHang"));

                danhSachTaiKhoan.add(r);
            }

        } catch (Exception e) {
            System.err.println("Lỗi nạp danh sách tài khoản: " + e.getMessage());
            themDuLieuMauNeuTrong();
        }

        if (danhSachTaiKhoan.isEmpty()) {
            themDuLieuMauNeuTrong();
        }

        onTimKiem();
    }

    private void themDuLieuMauNeuTrong() {
        TaiKhoanRow r1 = new TaiKhoanRow("TK001", "1000000001", "Nguyễn Văn An", "001200001234", new BigDecimal("45000000"), "SMART BANK", "Hội sở chính", "Tiêu chuẩn (50tr)", "Hoạt động");
        TaiKhoanRow r2 = new TaiKhoanRow("TK002", "1000000002", "Trần Thị Bích", "001200005678", new BigDecimal("125000000"), "SMART BANK", "Chi nhánh Ba Đình", "VIP (200tr)", "Hoạt động");
        TaiKhoanRow r3 = new TaiKhoanRow("TK003", "1000000003", "Lê Hoàng Long", "079200009999", new BigDecimal("1500000"), "SMART BANK", "Chi nhánh Quận 1", "Tiêu chuẩn (50tr)", "Tạm khóa");
        TaiKhoanRow r4 = new TaiKhoanRow("TK004", "1000000004", "Công ty TNHH Ánh Dương", "001099887766", new BigDecimal("850000000"), "SMART BANK", "Hội sở chính", "Doanh nghiệp (1 tỷ)", "Hoạt động");
        danhSachTaiKhoan.addAll(r1, r2, r3, r4);
    }

    @FXML
    public void onTimKiem() {
        String keyword = txtTimKiem != null ? txtTimKiem.getText().trim().toLowerCase() : "";
        String filterTT = cboLocTrangThai != null ? cboLocTrangThai.getValue() : "Tất cả trạng thái";
        String filterCN = cboLocChiNhanh != null ? cboLocChiNhanh.getValue() : "Tất cả chi nhánh";

        danhSachHienThi.clear();

        for (TaiKhoanRow r : danhSachTaiKhoan) {
            boolean matchKey = keyword.isEmpty()
                    || (r.getSoTaiKhoan() != null && r.getSoTaiKhoan().toLowerCase().contains(keyword))
                    || (r.getTenKhachHang() != null && r.getTenKhachHang().toLowerCase().contains(keyword))
                    || (r.getSoCCCD() != null && r.getSoCCCD().toLowerCase().contains(keyword))
                    || (r.getSoDienThoai() != null && r.getSoDienThoai().toLowerCase().contains(keyword));

            boolean matchTT = filterTT == null || filterTT.contains("Tất cả")
                    || (r.getTrangThai() != null && r.getTrangThai().equalsIgnoreCase(filterTT));

            boolean matchCN = filterCN == null || filterCN.contains("Tất cả")
                    || (r.getTenChiNhanh() != null && r.getTenChiNhanh().equalsIgnoreCase(filterCN));

            if (matchKey && matchTT && matchCN) {
                danhSachHienThi.add(r);
            }
        }

        if (lblTongTaiKhoan != null) {
            lblTongTaiKhoan.setText("Tổng số: " + danhSachHienThi.size() + " tài khoản");
        }
    }

    @FXML
    public void onLamMoi() {
        if (txtTimKiem != null) txtTimKiem.clear();
        if (cboLocTrangThai != null) cboLocTrangThai.getSelectionModel().selectFirst();
        if (cboLocChiNhanh != null) cboLocChiNhanh.getSelectionModel().selectFirst();
        taiDuLieuTaiKhoan();
    }

    // =========================================================
    // XỬ LÝ WIZARD 3 BƯỚC MỞ TÀI KHOẢN MỚI
    // =========================================================
    @FXML
    public void onMoModalTaoTaiKhoan() {
        // Reset wizard sang Bước 1
        datActiveStep(1);
        txtStep1CCCD.clear();
        boxStep1KetQua.setVisible(false);
        boxStep1KetQua.setManaged(false);

        txtStep2TenDangNhap.clear();
        txtStep2MatKhau.clear();
        txtStep2HoTen.clear();
        txtStep2SDT.clear();
        txtStep2Email.clear();
        txtStep2DiaChi.clear();

        onSinhSoTKNgauNhien();
        btnXacNhanMoTK.setDisable(true);

        hienModal(modalTaoTaiKhoan);
    }

    @FXML
    public void onDongModalTaoTK() {
        anModal(modalTaoTaiKhoan);
    }

    @FXML
    public void onKiemTraCCCD() {
        String cccd = txtStep1CCCD.getText().trim();
        if (cccd.isEmpty()) {
            hienCanhBao("Vui lòng nhập số CCCD để tra cứu!");
            return;
        }

        Optional<KhachHang> khOpt = khachHangDAO.findByCCCD(cccd);

        if (khOpt.isPresent()) {
            KhachHang kh = khOpt.get();
            currentMaKhachHang = kh.getMaKhachHang();
            currentHoTen = kh.getHoTen();

            boxStep1KetQua.setVisible(true);
            boxStep1KetQua.setManaged(true);
            lblStep1ThongBao.setText("✔ Khách hàng đã có hồ sơ trong hệ thống! Chuyển thẳng sang bước 3.");
            lblStep1ThongBao.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #10B981;");
            lblStep1MaKH.setText(kh.getMaKhachHang());
            lblStep1HoTen.setText(kh.getHoTen());
            lblStep1TenDangNhap.setText(kh.getTenDangNhap() != null ? kh.getTenDangNhap() : "Chưa liên kết");

            // Nhảy thẳng sang Bước 3
            datActiveStep(3);
            btnXacNhanMoTK.setDisable(false);

        } else {
            boxStep1KetQua.setVisible(true);
            boxStep1KetQua.setManaged(true);
            lblStep1ThongBao.setText("Chưa có thông tin khách hàng. Vui lòng tạo tài khoản đăng nhập và hồ sơ trước.");
            lblStep1ThongBao.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #D97706;");
            lblStep1MaKH.setText("Chưa có");
            lblStep1HoTen.setText("Chưa có");
            lblStep1TenDangNhap.setText("Chưa có");

            // Kích hoạt Bước 2
            datActiveStep(2);
        }
    }

    @FXML
    public void onLuuHoSoBuoc2() {
        String tenDN = txtStep2TenDangNhap.getText().trim();
        String matKhau = txtStep2MatKhau.getText().trim();
        String hoTen = txtStep2HoTen.getText().trim();
        String sdt = txtStep2SDT.getText().trim();
        String cccd = txtStep1CCCD.getText().trim();
        String email = txtStep2Email.getText().trim();
        String diaChi = txtStep2DiaChi.getText().trim();

        if (tenDN.isEmpty() || matKhau.isEmpty() || hoTen.isEmpty() || sdt.isEmpty()) {
            hienCanhBao("Vui lòng nhập đầy đủ các trường bắt buộc (*): Tên đăng nhập, Mật khẩu, Họ tên, SĐT!");
            return;
        }

        String maKH = khachHangDAO.taoMaKhachHang();
        currentMaKhachHang = maKH;
        currentHoTen = hoTen;

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            // 1. Tạo tài khoản đăng nhập với RoleName = 'Khách hàng'
            String sqlLogin = "INSERT INTO TaiKhoanDangNhap_VaiTro (TenDangNhap, MatKhau, RoleName, MaNguoiDung) VALUES (?, ?, N'Khách hàng', ?)";
            try (PreparedStatement ps1 = conn.prepareStatement(sqlLogin)) {
                ps1.setString(1, tenDN);
                ps1.setString(2, matKhau);
                ps1.setString(3, maKH);
                ps1.executeUpdate();
            }

            // 2. Tạo hồ sơ KhachHang
            KhachHang kh = new KhachHang(maKH, hoTen, cccd, sdt, diaChi, email, tenDN);
            khachHangDAO.insert(kh);

            conn.commit();
        } catch (Exception e) {
            System.err.println("Lỗi lưu hồ sơ khách hàng mới: " + e.getMessage());
        }

        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("Tạo hồ sơ thành công");
        info.setHeaderText(null);
        info.setContentText("Đã tạo hồ sơ cho khách hàng [" + hoTen + "] mã: " + maKH + ". Chuyển sang bước 3 tạo số tài khoản ngân hàng!");
        info.showAndWait();

        // Chuyển sang Bước 3
        datActiveStep(3);
        btnXacNhanMoTK.setDisable(false);
    }

    @FXML
    public void onSinhSoTKNgauNhien() {
        SecureRandom rng = new SecureRandom();
        long num = 1000000000L + (long)(rng.nextDouble() * 8999999999L);
        txtStep3SoTK.setText(String.valueOf(num));
    }

    @FXML
    public void onXacNhanMoTaiKhoan() {
        String soTK = txtStep3SoTK.getText().trim();
        if (soTK.isEmpty()) {
            hienCanhBao("Vui lòng nhập số tài khoản hoặc bấm 'Tự sinh'!");
            return;
        }

        String maTK = taiKhoanDAO.taoMaTaiKhoan();
        String chiNhanhStr = cboStep3ChiNhanh.getValue();
        String maCN = chiNhanhStr != null && chiNhanhStr.contains(" - ") ? chiNhanhStr.split(" - ")[0] : "CN01";

        String hanMucStr = cboStep3HanMuc.getValue();
        String maHM = hanMucStr != null && hanMucStr.contains(" - ") ? hanMucStr.split(" - ")[0] : "HM01";

        TaiKhoanNganHang tk = new TaiKhoanNganHang();
        tk.setMaTaiKhoan(maTK);
        tk.setMaKhachHang(currentMaKhachHang);
        tk.setSoTaiKhoan(soTK);
        tk.setSoDu(BigDecimal.ZERO);
        tk.setTrangThai("Hoạt động");
        tk.setMaChiNhanh(maCN);
        tk.setMaHanMuc(maHM);
        tk.setMaNganHang("NH01");

        boolean thanhCong = taiKhoanDAO.themTaiKhoan(tk);

        anModal(modalTaoTaiKhoan);

        if (thanhCong) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thành công");
            alert.setHeaderText("Mở tài khoản ngân hàng thành công!");
            alert.setContentText("Số tài khoản: " + soTK + "\nChủ tài khoản: " + currentHoTen + "\nTrạng thái: Hoạt động\nSố dư ban đầu: 0 VNĐ");
            alert.showAndWait();
        }

        taiDuLieuTaiKhoan();
    }

    private void datActiveStep(int step) {
        panelStep1.setVisible(step == 1);
        panelStep1.setManaged(step == 1);

        panelStep2.setVisible(step == 2);
        panelStep2.setManaged(step == 2);

        panelStep3.setVisible(step == 3);
        panelStep3.setManaged(step == 3);

        String active = "-fx-background-color: #5148C9; -fx-background-radius: 20; -fx-padding: 4 12; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px;";
        String inactive = "-fx-background-color: #E2E8F0; -fx-background-radius: 20; -fx-padding: 4 12; -fx-text-fill: #64748B; -fx-font-weight: bold; -fx-font-size: 11px;";

        lblStep1.setStyle(step >= 1 ? active : inactive);
        lblStep2.setStyle(step >= 2 ? active : inactive);
        lblStep3.setStyle(step >= 3 ? active : inactive);
    }

    // =========================================================
    // MODALS: CHI TIẾT, ĐỔI TRẠNG THÁI, GÁN HẠN MỨC
    // =========================================================
    private void moModalChiTiet(TaiKhoanRow row) {
        lblDetailMaTK.setText(row.getMaTaiKhoan());
        lblDetailSoTK.setText(row.getSoTaiKhoan());
        lblDetailTenKH.setText(row.getTenKhachHang());
        lblDetailCCCD.setText(row.getSoCCCD());
        lblDetailSoDu.setText(dfTien.format(row.getSoDu()));
        lblDetailChiNhanh.setText(row.getTenChiNhanh());
        lblDetailHanMuc.setText(row.getTenHanMuc());
        lblDetailTrangThai.setText(row.getTrangThai());
        hienModal(modalChiTietTK);
    }

    @FXML
    public void onDongModalChiTiet() {
        anModal(modalChiTietTK);
    }

    private void moModalDoiTrangThai(TaiKhoanRow row) {
        currentSelectedTK = row;
        lblModalStatusSoTK.setText(row.getSoTaiKhoan());
        lblModalStatusTenKH.setText(row.getTenKhachHang());
        lblModalStatusHienTai.setText(row.getTrangThai());
        cboModalTrangThaiMoi.setValue("Hoạt động".equalsIgnoreCase(row.getTrangThai()) ? "Tạm khóa" : "Hoạt động");
        txtModalLyDoDoiTrangThai.clear();
        hienModal(modalDoiTrangThai);
    }

    @FXML
    public void onDongModalDoiTrangThai() {
        anModal(modalDoiTrangThai);
    }

    @FXML
    public void onLuuDoiTrangThai() {
        if (currentSelectedTK == null) return;
        String trangThaiMoi = cboModalTrangThaiMoi.getValue();
        taiKhoanDAO.capNhatTrangThai(currentSelectedTK.getMaTaiKhoan(), trangThaiMoi);
        currentSelectedTK.setTrangThai(trangThaiMoi);
        anModal(modalDoiTrangThai);
        tblTaiKhoan.refresh();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Cập nhật thành công");
        alert.setHeaderText(null);
        alert.setContentText("Tài khoản [" + currentSelectedTK.getSoTaiKhoan() + "] đã được đổi trạng thái thành: " + trangThaiMoi);
        alert.showAndWait();
    }

    private void moModalGanHanMuc(TaiKhoanRow row) {
        currentSelectedTK = row;
        lblModalHanMucSoTK.setText(row.getSoTaiKhoan());
        lblModalHanMucTenKH.setText(row.getTenKhachHang());
        lblModalHanMucHienTai.setText(row.getTenHanMuc());
        hienModal(modalGanHanMuc);
    }

    @FXML
    public void onDongModalGanHanMuc() {
        anModal(modalGanHanMuc);
    }

    @FXML
    public void onLuuGanHanMuc() {
        if (currentSelectedTK == null) return;
        String hmStr = cboModalHanMucMoi.getValue();
        String maHM = hmStr != null && hmStr.contains(" - ") ? hmStr.split(" - ")[0] : "HM01";

        taiKhoanDAO.ganHanMuc(currentSelectedTK.getMaTaiKhoan(), maHM);
        currentSelectedTK.setTenHanMuc(hmStr);
        anModal(modalGanHanMuc);
        tblTaiKhoan.refresh();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Cập nhật thành công");
        alert.setHeaderText(null);
        alert.setContentText("Đã cập nhật hạn mức mới cho tài khoản [" + currentSelectedTK.getSoTaiKhoan() + "]!");
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

    private void hienCanhBao(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle("Thông báo");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    // =========================================================
    // LỚP DỮ LIỆU BẢNG TÀI KHOẢN (TABLE MODEL ROW)
    // =========================================================
    public static class TaiKhoanRow {
        private String maTaiKhoan;
        private String soTaiKhoan;
        private String tenKhachHang;
        private String soCCCD;
        private String soDienThoai;
        private String email;
        private String diaChi;
        private BigDecimal soDu;
        private String tenNganHang;
        private String tenChiNhanh;
        private String tenHanMuc;
        private String trangThai;

        public TaiKhoanRow() {}

        public TaiKhoanRow(String maTaiKhoan, String soTaiKhoan, String tenKhachHang, String soCCCD, BigDecimal soDu, String tenNganHang, String tenChiNhanh, String tenHanMuc, String trangThai) {
            this.maTaiKhoan = maTaiKhoan;
            this.soTaiKhoan = soTaiKhoan;
            this.tenKhachHang = tenKhachHang;
            this.soCCCD = soCCCD;
            this.soDu = soDu;
            this.tenNganHang = tenNganHang;
            this.tenChiNhanh = tenChiNhanh;
            this.tenHanMuc = tenHanMuc;
            this.trangThai = trangThai;
        }

        public String getMaTaiKhoan() { return maTaiKhoan; }
        public void setMaTaiKhoan(String maTaiKhoan) { this.maTaiKhoan = maTaiKhoan; }
        public String getSoTaiKhoan() { return soTaiKhoan; }
        public void setSoTaiKhoan(String soTaiKhoan) { this.soTaiKhoan = soTaiKhoan; }
        public String getTenKhachHang() { return tenKhachHang; }
        public void setTenKhachHang(String tenKhachHang) { this.tenKhachHang = tenKhachHang; }
        public String getSoCCCD() { return soCCCD; }
        public void setSoCCCD(String soCCCD) { this.soCCCD = soCCCD; }
        public String getSoDienThoai() { return soDienThoai; }
        public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getDiaChi() { return diaChi; }
        public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
        public BigDecimal getSoDu() { return soDu; }
        public void setSoDu(BigDecimal soDu) { this.soDu = soDu; }
        public String getTenNganHang() { return tenNganHang; }
        public void setTenNganHang(String tenNganHang) { this.tenNganHang = tenNganHang; }
        public String getTenChiNhanh() { return tenChiNhanh; }
        public void setTenChiNhanh(String tenChiNhanh) { this.tenChiNhanh = tenChiNhanh; }
        public String getTenHanMuc() { return tenHanMuc; }
        public void setTenHanMuc(String tenHanMuc) { this.tenHanMuc = tenHanMuc; }
        public String getTrangThai() { return trangThai; }
        public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    }

    // =========================================================
    // SIDEBAR NAVIGATION
    // =========================================================
    @FXML private void onTrangChu(ActionEvent event) { moView("teller-dashboard-view.fxml", event); }
    @FXML private void onChuyenTien(ActionEvent event) { moView("teller-transfer-view.fxml", event); }
    @FXML private void onNapRutThanhToan(ActionEvent event) { moView("teller-cash-view.fxml", event); }
    @FXML private void onTraCuuSaoKe(ActionEvent event) { moView("teller-statement-view.fxml", event); }
    @FXML private void onTheoDoiTrangThai(ActionEvent event) { moView("teller-status-view.fxml", event); }
    @FXML private void onDangXuat(ActionEvent event) { moView("login-view.fxml", event); }
}
