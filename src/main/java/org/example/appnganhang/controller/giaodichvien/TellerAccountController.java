package org.example.appnganhang.controller.giaodichvien;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import org.example.appnganhang.controller.service.Iml.TellerAccountServiceImpl;
import org.example.appnganhang.controller.service.intf.TellerAccountService;
import org.example.appnganhang.model.entity.ChiNhanh;
import org.example.appnganhang.model.entity.HanMucGiaoDich;
import org.example.appnganhang.model.entity.KhachHang;
import org.example.appnganhang.model.entity.TaiKhoanNganHang;
import org.example.appnganhang.model.entity.TellerAccountInfo;

import java.math.BigDecimal;
import java.security.SecureRandom;
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
    @FXML private Button btnXemChiTiet;
    @FXML private Button btnDoiTrangThai;
    @FXML private Button btnGanHanMuc;

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

    private final TellerAccountService tellerAccountService = new TellerAccountServiceImpl();

    // Biến lưu tạm khi thực hiện wizard 3 bước
    private String currentMaKhachHang = "";
    private String currentHoTen = "";
    private TaiKhoanRow currentSelectedTK = null;

    @FXML
    public void initialize() {
        khoiTaoBoLoc();
        cauHinhBangTaiKhoan();
        tblTaiKhoan.getSelectionModel().selectedItemProperty().addListener((obs, oldRow, row) -> {
            boolean chuaChon = row == null;
            btnXemChiTiet.setDisable(chuaChon);
            btnDoiTrangThai.setDisable(chuaChon);
            btnGanHanMuc.setDisable(chuaChon);
        });
        taiDuLieuTaiKhoan();
        khoiTaoDuLieuWizard();
    }

    private void khoiTaoBoLoc() {
        if (cboLocTrangThai != null) {
            cboLocTrangThai.getItems().setAll("Tất cả trạng thái", "Hoạt động", "Bị khóa", "Đóng");
            cboLocTrangThai.getSelectionModel().selectFirst();
        }

        ObservableList<String> listCN = FXCollections.observableArrayList();
        listCN.add("Tất cả chi nhánh");
        try {
            for (ChiNhanh branch : tellerAccountService.layDanhSachChiNhanh()) listCN.add(branch.getTenChiNhanh());
        } catch (RuntimeException e) {
            hienCanhBao("Lỗi tải danh sách chi nhánh: " + e.getMessage());
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
        try {
            for (ChiNhanh branch : tellerAccountService.layDanhSachChiNhanh())
                listCN.add(branch.getMaChiNhanh() + " - " + branch.getTenChiNhanh());
        } catch (RuntimeException e) { hienCanhBao("Lỗi tải chi nhánh mở tài khoản: " + e.getMessage()); }
        if (cboStep3ChiNhanh != null) {
            cboStep3ChiNhanh.setItems(listCN);
            cboStep3ChiNhanh.getSelectionModel().selectFirst();
        }

        ObservableList<String> listHM = FXCollections.observableArrayList();
        try {
            for (HanMucGiaoDich limit : tellerAccountService.layDanhSachHanMuc())
                listHM.add(limit.getMaHanMuc() + " - " + limit.getTenHanMuc() + " (" + dfTien.format(limit.getHanMucNgay()) + "/ngày)");
        } catch (RuntimeException e) { hienCanhBao("Lỗi tải hạn mức giao dịch: " + e.getMessage()); }
        if (cboStep3HanMuc != null) {
            cboStep3HanMuc.setItems(listHM);
            cboStep3HanMuc.getSelectionModel().selectFirst();
        }

        if (cboModalTrangThaiMoi != null) {
            cboModalTrangThaiMoi.getItems().setAll("Hoạt động", "Bị khóa", "Đóng");
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

        tblTaiKhoan.setItems(danhSachHienThi);
    }

    private void taiDuLieuTaiKhoan() {
        danhSachTaiKhoan.clear();
        try {
            for (TellerAccountInfo account : tellerAccountService.layDanhSachTaiKhoan()) {
                TaiKhoanRow row = new TaiKhoanRow();
                row.setMaTaiKhoan(account.getMaTaiKhoan());
                row.setSoTaiKhoan(account.getSoTaiKhoan());
                row.setTenKhachHang(account.getTenKhachHang());
                row.setSoCCCD(account.getSoCCCD());
                row.setSoDienThoai(account.getSoDienThoai());
                row.setEmail(account.getEmail());
                row.setDiaChi(account.getDiaChi());
                row.setSoDu(account.getSoDu());
                row.setTenNganHang(account.getTenNganHang());
                row.setTenChiNhanh(account.getTenChiNhanh());
                row.setTenHanMuc(account.getTenHanMuc());
                row.setTrangThai(account.getTrangThai());
                danhSachTaiKhoan.add(row);
            }
        } catch (RuntimeException e) {
            hienCanhBao("Lỗi tải danh sách tài khoản: " + e.getMessage());
        }

        onTimKiem();
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

        Optional<KhachHang> khOpt;
        try {
            khOpt = tellerAccountService.timKhachHangTheoCCCD(cccd);
        } catch (RuntimeException e) {
            hienCanhBao("Không thể tra cứu CCCD: " + e.getMessage());
            return;
        }

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

        String maKH;
        try {
            maKH = tellerAccountService.taoHoSoKhachHang(tenDN, matKhau, hoTen, cccd, sdt, email, diaChi);
        } catch (RuntimeException e) {
            hienCanhBao("Không thể tạo hồ sơ khách hàng: " + e.getMessage());
            return;
        }
        currentMaKhachHang = maKH;
        currentHoTen = hoTen;

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

        String chiNhanhStr = cboStep3ChiNhanh.getValue();
        if (chiNhanhStr == null || !chiNhanhStr.contains(" - ")) {
            hienCanhBao("Vui lòng chọn chi nhánh hợp lệ.");
            return;
        }
        String hanMucStr = cboStep3HanMuc.getValue();
        if (hanMucStr == null || !hanMucStr.contains(" - ")) {
            hienCanhBao("Vui lòng chọn hạn mức giao dịch.");
            return;
        }

        String maTK;
        try {
            maTK = tellerAccountService.taoMaTaiKhoan();
        } catch (RuntimeException e) {
            hienCanhBao("Không thể tạo mã tài khoản: " + e.getMessage());
            return;
        }
        String maCN = chiNhanhStr.split(" - ", 2)[0];
        String maHM = hanMucStr.split(" - ", 2)[0];

        TaiKhoanNganHang tk = new TaiKhoanNganHang();
        tk.setMaTaiKhoan(maTK);
        tk.setMaKhachHang(currentMaKhachHang);
        tk.setSoTaiKhoan(soTK);
        tk.setSoDu(BigDecimal.ZERO);
        tk.setTrangThai("Hoạt động");
        tk.setMaChiNhanh(maCN);
        tk.setMaHanMuc(maHM);
        tk.setMaNganHang("SMART");

        boolean thanhCong;
        try {
            thanhCong = tellerAccountService.moTaiKhoan(tk);
        } catch (RuntimeException e) {
            hienCanhBao("Không thể mở tài khoản: " + e.getMessage());
            return;
        }

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

    private TaiKhoanRow layDongDangChon() {
        TaiKhoanRow row = tblTaiKhoan.getSelectionModel().getSelectedItem();
        if (row == null) hienCanhBao("Vui lòng chọn một tài khoản trong bảng trước.");
        return row;
    }

    @FXML public void onXemChiTietDongDaChon() {
        TaiKhoanRow row = layDongDangChon();
        if (row != null) moModalChiTiet(row);
    }

    @FXML public void onDoiTrangThaiDongDaChon() {
        TaiKhoanRow row = layDongDangChon();
        if (row != null) moModalDoiTrangThai(row);
    }

    @FXML public void onGanHanMucDongDaChon() {
        TaiKhoanRow row = layDongDangChon();
        if (row != null) moModalGanHanMuc(row);
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
        cboModalTrangThaiMoi.setValue("Hoạt động".equalsIgnoreCase(row.getTrangThai()) ? "Bị khóa" : "Hoạt động");
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
        try {
            tellerAccountService.doiTrangThai(currentSelectedTK.getMaTaiKhoan(), trangThaiMoi);
        } catch (RuntimeException e) {
            hienCanhBao("Không thể cập nhật trạng thái: " + e.getMessage());
            return;
        }
        String soTaiKhoan = currentSelectedTK.getSoTaiKhoan();
        anModal(modalDoiTrangThai);
        taiDuLieuTaiKhoan();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Cập nhật thành công");
        alert.setHeaderText(null);
        alert.setContentText("Tài khoản [" + soTaiKhoan + "] đã được đổi trạng thái thành: " + trangThaiMoi);
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
        if (hmStr == null || !hmStr.contains(" - ")) {
            hienCanhBao("Vui lòng chọn hạn mức hợp lệ.");
            return;
        }
        String maHM = hmStr.split(" - ", 2)[0];
        try {
            tellerAccountService.ganHanMuc(currentSelectedTK.getMaTaiKhoan(), maHM);
        } catch (RuntimeException e) {
            hienCanhBao("Không thể cập nhật hạn mức: " + e.getMessage());
            return;
        }
        String soTaiKhoan = currentSelectedTK.getSoTaiKhoan();
        anModal(modalGanHanMuc);
        taiDuLieuTaiKhoan();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Cập nhật thành công");
        alert.setHeaderText(null);
        alert.setContentText("Đã cập nhật hạn mức mới cho tài khoản [" + soTaiKhoan + "]!");
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
