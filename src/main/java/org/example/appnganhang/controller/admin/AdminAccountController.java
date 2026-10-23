package org.example.appnganhang.controller.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.example.appnganhang.controller.service.Impl.AdminAccountServiceImpl;
import org.example.appnganhang.controller.service.intf.AdminAccountService;
import org.example.appnganhang.model.entity.ChiNhanh;
import org.example.appnganhang.model.entity.TaiKhoanDangNhap_VaiTro;
import org.example.appnganhang.model.entity.HanMucGiaoDich;
import java.security.SecureRandom;
import java.text.NumberFormat;
import java.util.Locale;
import javafx.util.StringConverter;
import static org.example.appnganhang.controller.util.Navigation.moView;

public class AdminAccountController {
    @FXML private Label lblAdminName, lblTongTaiKhoan;
    @FXML private TextField txtTimKiem;
    @FXML private ComboBox<String> cboLocVaiTro;
    @FXML private TableView<TaiKhoanDangNhap_VaiTro> tblTaiKhoan;
    @FXML private TableColumn<TaiKhoanDangNhap_VaiTro, String> colTenDangNhap, colMaNguoiDung, colRoleName, colTrangThaiQuyen;

    @FXML private StackPane modalTaoTaiKhoan;
    @FXML private TextField txtModalTenDangNhap, txtModalHoTen, txtModalCCCD, txtModalSDT,
            txtModalEmail, txtModalDiaChi;
    @FXML private ComboBox<ChiNhanh> cboModalChiNhanh;
    @FXML private ComboBox<HanMucGiaoDich> cboModalHanMuc;
    @FXML private PasswordField txtModalMatKhau;
    @FXML private ComboBox<String> cboModalRoleName;
    @FXML private Label lblModalTaoLoi;

    @FXML private StackPane modalSuaVaiTro;
    @FXML private TextField txtModalSuaTenDangNhap;
    @FXML private ComboBox<String> cboModalSuaRoleName;

    @FXML private StackPane modalDatLaiMatKhau;
    @FXML private Label lblModalDatLaiTK, lblModalThongBaoMatKhau;
    @FXML private RadioButton rbMatKhauNgauNhien, rbNhapMatKhauMoi;
    @FXML private VBox boxNhapMatKhauMoi;
    @FXML private PasswordField txtModalMatKhauMoi;

    @FXML private StackPane modalXacNhanXoa;
    @FXML private Label lblModalXoaTenTK;

    private final ObservableList<TaiKhoanDangNhap_VaiTro> danhSachTaiKhoan = FXCollections.observableArrayList();
    private final ObservableList<TaiKhoanDangNhap_VaiTro> danhSachHienThi = FXCollections.observableArrayList();
    private final AdminAccountService service = new AdminAccountServiceImpl();
    private TaiKhoanDangNhap_VaiTro taiKhoanDangChon;

    @FXML public void initialize() {
        cauHinhBang();
        if (cboLocVaiTro != null) cboLocVaiTro.setValue("Tất cả vai trò");
        if (cboModalRoleName != null) {
            cboModalRoleName.setValue("Khách hàng");
            cboModalRoleName.valueProperty().addListener((obs, oldRole, newRole) -> capNhatTrangThaiTruongKhachHang(newRole));
        }
        capNhatTrangThaiTruongKhachHang(cboModalRoleName == null ? null : cboModalRoleName.getValue());
        taiDanhSachChiNhanh();
        taiDanhSachHanMuc();
        taiDuLieuTaiKhoan();
    }

    private void cauHinhBang() {
        colTenDangNhap.setCellValueFactory(new PropertyValueFactory<>("tenDangNhap"));
        colMaNguoiDung.setCellValueFactory(new PropertyValueFactory<>("maNguoiDung"));
        colRoleName.setCellValueFactory(new PropertyValueFactory<>("roleName"));
        if (colTrangThaiQuyen != null) colTrangThaiQuyen.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(cell.getValue().isQuyenHoatDong() ? "Đang hoạt động" : "Đã thu hồi"));

        // Ba nút thao tác đã được bố trí sẵn bên dưới bảng trong FXML.
        // Không tạo thêm nút trong từng dòng để tránh bị lặp giao diện.
        tblTaiKhoan.setItems(danhSachHienThi);
    }

    private void taiDuLieuTaiKhoan() {
        try {
            danhSachTaiKhoan.setAll(service.layDanhSachTaiKhoan());
            locDuLieu();
        } catch (RuntimeException e) {
            danhSachTaiKhoan.clear(); danhSachHienThi.clear();
            if (lblTongTaiKhoan != null) lblTongTaiKhoan.setText("Không tải được dữ liệu");
            hienCanhBao("Lỗi tải tài khoản", e.getMessage());
        }
    }

    @FXML private void onTimKiem() { locDuLieu(); }
    @FXML private void onLamMoi() {
        if (txtTimKiem != null) txtTimKiem.clear();
        if (cboLocVaiTro != null) cboLocVaiTro.setValue("Tất cả vai trò");
        taiDuLieuTaiKhoan();
    }
    private void locDuLieu() {
        String keyword = txtTimKiem == null || txtTimKiem.getText() == null ? "" : txtTimKiem.getText().trim().toLowerCase();
        String role = cboLocVaiTro == null ? "Tất cả vai trò" : cboLocVaiTro.getValue();
        danhSachHienThi.setAll(danhSachTaiKhoan.stream().filter(tk ->
                (keyword.isEmpty() || safe(tk.getTenDangNhap()).toLowerCase().contains(keyword)
                        || safe(tk.getMaNguoiDung()).toLowerCase().contains(keyword))
                        && (role == null || role.equals("Tất cả vai trò") || role.equals(tk.getRoleName()))).toList());
        if (lblTongTaiKhoan != null) lblTongTaiKhoan.setText("Tổng số: " + danhSachHienThi.size() + " tài khoản");
    }
    private String safe(String s) { return s == null ? "" : s; }

    // CCCD, email và hạn mức chỉ được nhập khi tạo tài khoản khách hàng.
    private void capNhatTrangThaiTruongKhachHang(String role) {
        boolean laKhachHang = "Khách hàng".equals(role);
        if (txtModalCCCD != null) {
            txtModalCCCD.setDisable(!laKhachHang);
            if (!laKhachHang) txtModalCCCD.clear();
        }
        if (txtModalEmail != null) {
            txtModalEmail.setDisable(!laKhachHang);
            if (!laKhachHang) txtModalEmail.clear();
        }
        if (cboModalHanMuc != null) {
            cboModalHanMuc.setDisable(!laKhachHang);
            if (!laKhachHang) cboModalHanMuc.getSelectionModel().clearSelection();
        }
    }

    private void taiDanhSachChiNhanh() {
        if (cboModalChiNhanh == null) return;
        cboModalChiNhanh.setConverter(new StringConverter<>() {
            @Override public String toString(ChiNhanh chiNhanh) {
                return chiNhanh == null ? "" : chiNhanh.getTenChiNhanh();
            }
            @Override public ChiNhanh fromString(String value) { return null; }
        });
        try {
            cboModalChiNhanh.setItems(FXCollections.observableArrayList(service.layDanhSachChiNhanh()));
            if (!cboModalChiNhanh.getItems().isEmpty()) cboModalChiNhanh.getSelectionModel().selectFirst();
        } catch (RuntimeException e) {
            cboModalChiNhanh.getItems().clear();
            hienCanhBao("Lỗi tải chi nhánh", e.getMessage());
        }
    }

    private void taiDanhSachHanMuc() {
        if (cboModalHanMuc == null) return;
        cboModalHanMuc.setConverter(new StringConverter<>() {
            @Override public String toString(HanMucGiaoDich hanMuc) {
                if (hanMuc == null) return "";
                String mucTien = hanMuc.getHanMucNgay() == null ? "" : NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN")).format(hanMuc.getHanMucNgay());
                return hanMuc.getTenHanMuc() + " (" + mucTien + " VNĐ/ngày)";
            }
            @Override public HanMucGiaoDich fromString(String value) { return null; }
        });
        try {
            cboModalHanMuc.setItems(FXCollections.observableArrayList(service.layDanhSachHanMuc()));
        } catch (RuntimeException e) {
            cboModalHanMuc.getItems().clear();
            hienCanhBao("Lỗi tải hạn mức", e.getMessage());
        }
    }

    @FXML public void onMoPopupTaoTaiKhoan() {
        txtModalTenDangNhap.clear(); txtModalMatKhau.clear(); txtModalHoTen.clear(); txtModalCCCD.clear();
        txtModalSDT.clear(); txtModalEmail.clear(); txtModalDiaChi.clear();
        cboModalRoleName.setValue("Khách hàng");
        capNhatTrangThaiTruongKhachHang("Khách hàng");
        if (cboModalChiNhanh != null && !cboModalChiNhanh.getItems().isEmpty()) cboModalChiNhanh.getSelectionModel().selectFirst();
        lblModalTaoLoi.setText("");
        hienModal(modalTaoTaiKhoan);
    }

    @FXML public void onDongPopupTaoTaiKhoan() { anModal(modalTaoTaiKhoan); }

    @FXML public void onLuuTaiKhoanMoi() {
        try {
            ChiNhanh chiNhanh = cboModalChiNhanh.getValue();
            if (chiNhanh == null) throw new IllegalArgumentException("Vui lòng chọn chi nhánh.");

            HanMucGiaoDich hanMuc = cboModalHanMuc.getValue();
            boolean laKhachHang = "Khách hàng".equals(cboModalRoleName.getValue());
            if (laKhachHang && hanMuc == null) throw new IllegalArgumentException("Vui lòng chọn hạn mức giao dịch.");
            String ma = service.taoTaiKhoan(txtModalTenDangNhap.getText(), txtModalMatKhau.getText(),
                    cboModalRoleName.getValue(), txtModalHoTen.getText(), txtModalCCCD.getText(),
                    txtModalSDT.getText(), txtModalEmail.getText(), txtModalDiaChi.getText(),
                    chiNhanh.getMaChiNhanh(), hanMuc == null ? null : hanMuc.getMaHanMuc());
            anModal(modalTaoTaiKhoan); taiDuLieuTaiKhoan();
            hienThongTin("Tạo tài khoản thành công", "Mã người dùng được hệ thống sinh tự động: " + ma);
        } catch (RuntimeException e) {
            lblModalTaoLoi.setText(e.getMessage());
        }
    }

    @FXML public void onMoPopupSuaVaiTro() {
        TaiKhoanDangNhap_VaiTro tk = tblTaiKhoan.getSelectionModel().getSelectedItem();
        if (tk == null) hienThongBaoChuaChonDong(); else moPopupSuaVaiTroCho(tk);
    }

    @FXML public void onThuHoiKhoiPhucQuyen() {
        TaiKhoanDangNhap_VaiTro tk = tblTaiKhoan.getSelectionModel().getSelectedItem();
        if (tk == null) { hienThongBaoChuaChonDong(); return; }
        if ("Admin".equals(tk.getRoleName())) {
            hienCanhBao("Không thể thu hồi", "Không thể thu hồi quyền của tài khoản Admin.");
            return;
        }
        boolean choPhep = !tk.isQuyenHoatDong();
        try {
            service.datQuyenHoatDong(tk.getTenDangNhap(), choPhep);
            taiDuLieuTaiKhoan();
            hienThongTin("Phân quyền", choPhep
                    ? "Đã khôi phục quyền truy cập. Người dùng cần đăng nhập lại."
                    : "Đã thu hồi quyền. Phiên ứng dụng của người dùng sẽ bị kết thúc trong tối đa 1 giây.");
        } catch (RuntimeException e) {
            hienCanhBao("Không thể cập nhật quyền", e.getMessage());
        }
    }
    private void moPopupSuaVaiTroCho(TaiKhoanDangNhap_VaiTro tk) {
        taiKhoanDangChon = tk; txtModalSuaTenDangNhap.setText(tk.getTenDangNhap());
        cboModalSuaRoleName.setValue(tk.getRoleName()); hienModal(modalSuaVaiTro);
    }

    @FXML public void onDongPopupSuaVaiTro() { anModal(modalSuaVaiTro); }

    @FXML public void onLuuSuaVaiTro() {
        if (taiKhoanDangChon == null || cboModalSuaRoleName.getValue() == null) return;
        try { service.doiVaiTro(taiKhoanDangChon.getTenDangNhap(), cboModalSuaRoleName.getValue());
            anModal(modalSuaVaiTro); taiDuLieuTaiKhoan();
        } catch (RuntimeException e) { hienCanhBao("Không thể đổi vai trò", e.getMessage()); }
    }

    @FXML public void onMoPopupDatLaiMatKhau() {
        TaiKhoanDangNhap_VaiTro tk = tblTaiKhoan.getSelectionModel().getSelectedItem();
        if (tk == null) hienThongBaoChuaChonDong(); else moPopupDatLaiMatKhauCho(tk);
    }

    private void moPopupDatLaiMatKhauCho(TaiKhoanDangNhap_VaiTro tk) {
        taiKhoanDangChon = tk; lblModalDatLaiTK.setText("Tài khoản: " + tk.getTenDangNhap());
        rbMatKhauNgauNhien.setSelected(true); boxNhapMatKhauMoi.setDisable(true);
        txtModalMatKhauMoi.clear(); lblModalThongBaoMatKhau.setText(""); hienModal(modalDatLaiMatKhau);
    }
    @FXML public void onChonCheDoMatKhau() { boxNhapMatKhauMoi.setDisable(!rbNhapMatKhauMoi.isSelected()); }

    @FXML public void onDongPopupDatLaiMatKhau() { anModal(modalDatLaiMatKhau); }

    @FXML public void onXacNhanDatLaiMatKhau() {
        if (taiKhoanDangChon == null) return;
        String password = rbMatKhauNgauNhien.isSelected() ? sinhMatKhauNgauNhien(10) : txtModalMatKhauMoi.getText();
        try {
            service.datLaiMatKhau(taiKhoanDangChon.getTenDangNhap(), password);
            lblModalThongBaoMatKhau.setStyle("-fx-text-fill: #10B981; -fx-font-weight: bold;");
            lblModalThongBaoMatKhau.setText("Đặt lại thành công. Mật khẩu mới: " + password);
        } catch (RuntimeException e) {
            lblModalThongBaoMatKhau.setStyle("-fx-text-fill: #D9534F; -fx-font-weight: bold;");
            lblModalThongBaoMatKhau.setText(e.getMessage());
        }
    }

    private String sinhMatKhauNgauNhien(int length) {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789!@#$";
        SecureRandom random = new SecureRandom(); StringBuilder sb = new StringBuilder(length);
        for (int i=0;i<length;i++) sb.append(chars.charAt(random.nextInt(chars.length()))); return sb.toString();
    }

    @FXML public void onMoPopupXacNhanXoa() {
        TaiKhoanDangNhap_VaiTro tk = tblTaiKhoan.getSelectionModel().getSelectedItem();
        if (tk == null) hienThongBaoChuaChonDong(); else moPopupXacNhanXoaCho(tk);
    }
    private void moPopupXacNhanXoaCho(TaiKhoanDangNhap_VaiTro tk) {
        taiKhoanDangChon = tk; lblModalXoaTenTK.setText("Tài khoản: " + tk.getTenDangNhap() + " (" + tk.getRoleName() + ")");
        hienModal(modalXacNhanXoa);
    }

    @FXML public void onDongPopupXacNhanXoa() { anModal(modalXacNhanXoa); }

    @FXML public void onDongYXoaTaiKhoan() {
        if (taiKhoanDangChon == null) return;
        try { service.xoaTaiKhoan(taiKhoanDangChon.getTenDangNhap()); anModal(modalXacNhanXoa); taiDuLieuTaiKhoan(); }
        catch (RuntimeException e) { hienCanhBao("Không thể xóa tài khoản", e.getMessage()); }
    }

    private void hienModal(StackPane modal) { if (modal != null) { modal.setVisible(true); modal.setManaged(true); } }
    private void anModal(StackPane modal) { if (modal != null) { modal.setVisible(false); modal.setManaged(false); } }
    private void hienThongBaoChuaChonDong() { hienCanhBao("Thông báo", "Vui lòng chọn một dòng tài khoản trong bảng."); }
    private void hienCanhBao(String title, String message) {
        Alert a = new Alert(Alert.AlertType.WARNING); a.setTitle(title); a.setHeaderText(null); a.setContentText(message); a.showAndWait();
    }
    private void hienThongTin(String title, String message) {
        Alert a = new Alert(Alert.AlertType.INFORMATION); a.setTitle(title); a.setHeaderText(null); a.setContentText(message); a.showAndWait();
    }

    @FXML private void onTrangChu(ActionEvent event) { moView("admin-dashboard-view.fxml", event); }
    @FXML private void onTaiKhoan(ActionEvent event) { }
    @FXML private void onNhanVienChiNhanh(ActionEvent event) { moView("admin-employee-branch-view.fxml", event); }
    @FXML private void onCauHinhDanhMuc(ActionEvent event) { moView("admin-system-config-view.fxml", event); }
    @FXML private void onThongKeBaoCao(ActionEvent event) { moView("admin-report-view.fxml", event); }
    @FXML private void onDangXuat(ActionEvent event) { moView("login-view.fxml", event); }
}
