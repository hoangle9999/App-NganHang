package org.example.appnganhang.controller.admin;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.example.appnganhang.model.dao.Impl.TaiKhoanDangNhapDAOImpl;
import org.example.appnganhang.model.dao.intf.TaiKhoanDangNhapDAO;
import org.example.appnganhang.model.entity.TaiKhoanDangNhap_VaiTro;
import org.example.appnganhang.model.util.DBConnection;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Optional;

import static org.example.appnganhang.controller.util.Navigation.moView;

public class AdminAccountController {

    @FXML private Label lblAdminName;
    @FXML private Label lblTongTaiKhoan;

    // Filter bar
    @FXML private TextField txtTimKiem;
    @FXML private ComboBox<String> cboLocVaiTro;
    @FXML private Button btnTimKiem;
    @FXML private Button btnLamMoi;
    @FXML private Button btnTaoMoi;

    // Table
    @FXML private TableView<TaiKhoanDangNhap_VaiTro> tblTaiKhoan;
    @FXML private TableColumn<TaiKhoanDangNhap_VaiTro, String> colTenDangNhap;
    @FXML private TableColumn<TaiKhoanDangNhap_VaiTro, String> colMaNguoiDung;
    @FXML private TableColumn<TaiKhoanDangNhap_VaiTro, String> colRoleName;
    @FXML private TableColumn<TaiKhoanDangNhap_VaiTro, Void> colThaoTac;

    // Modal 1: Tạo tài khoản
    @FXML private StackPane modalTaoTaiKhoan;
    @FXML private TextField txtModalTenDangNhap;
    @FXML private PasswordField txtModalMatKhau;
    @FXML private TextField txtModalMaNguoiDung;
    @FXML private ComboBox<String> cboModalRoleName;
    @FXML private Label lblModalTaoLoi;

    // Modal 2: Cập nhật vai trò
    @FXML private StackPane modalSuaVaiTro;
    @FXML private TextField txtModalSuaTenDangNhap;
    @FXML private ComboBox<String> cboModalSuaRoleName;

    // Modal 3: Đặt lại mật khẩu
    @FXML private StackPane modalDatLaiMatKhau;
    @FXML private Label lblModalDatLaiTK;
    @FXML private RadioButton rbMatKhauNgauNhien;
    @FXML private RadioButton rbNhapMatKhauMoi;
    @FXML private VBox boxNhapMatKhauMoi;
    @FXML private PasswordField txtModalMatKhauMoi;
    @FXML private Label lblModalThongBaoMatKhau;

    // Modal 4: Xác nhận xóa
    @FXML private StackPane modalXacNhanXoa;
    @FXML private Label lblModalXoaTenTK;

    private final ObservableList<TaiKhoanDangNhap_VaiTro> danhSachTaiKhoan = FXCollections.observableArrayList();
    private final ObservableList<TaiKhoanDangNhap_VaiTro> danhSachHienThi = FXCollections.observableArrayList();

    private final TaiKhoanDangNhapDAO taiKhoanDAO = new TaiKhoanDangNhapDAOImpl();
    private TaiKhoanDangNhap_VaiTro taiKhoanDangChon;

    @FXML
    public void initialize() {
        cauHinhBang();
        taiDuLieuTaiKhoan();
        if (cboLocVaiTro != null && cboLocVaiTro.getValue() == null) {
            cboLocVaiTro.setValue("Tất cả vai trò");
        }
    }

    private void cauHinhBang() {
        colTenDangNhap.setCellValueFactory(new PropertyValueFactory<>("tenDangNhap"));
        colMaNguoiDung.setCellValueFactory(new PropertyValueFactory<>("maNguoiDung"));
        colRoleName.setCellValueFactory(new PropertyValueFactory<>("roleName"));

        // Cấu hình cột thao tác với 3 nút: Sửa vai trò, Đặt lại mật khẩu, Xóa
        colThaoTac.setCellFactory(param -> new TableCell<>() {
            private final Button btnSua = new Button("Sửa vai trò");
            private final Button btnReset = new Button("Đặt lại MK");
            private final Button btnXoa = new Button("Xóa");
            private final HBox container = new HBox(8, btnSua, btnReset, btnXoa);

            {
                container.setAlignment(Pos.CENTER);

                btnSua.setStyle("-fx-background-color: #ECEBFA; -fx-text-fill: #5148C9; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 11px; -fx-padding: 5 10;");
                btnReset.setStyle("-fx-background-color: #FEF3C7; -fx-text-fill: #D97706; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 11px; -fx-padding: 5 10;");
                btnXoa.setStyle("-fx-background-color: #FDECEC; -fx-text-fill: #D9534F; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 11px; -fx-padding: 5 10;");

                btnSua.setOnAction(e -> {
                    TaiKhoanDangNhap_VaiTro item = getTableView().getItems().get(getIndex());
                    moPopupSuaVaiTroCho(item);
                });

                btnReset.setOnAction(e -> {
                    TaiKhoanDangNhap_VaiTro item = getTableView().getItems().get(getIndex());
                    moPopupDatLaiMatKhauCho(item);
                });

                btnXoa.setOnAction(e -> {
                    TaiKhoanDangNhap_VaiTro item = getTableView().getItems().get(getIndex());
                    moPopupXacNhanXoaCho(item);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(container);
                }
            }
        });

        tblTaiKhoan.setItems(danhSachHienThi);
    }

    private void taiDuLieuTaiKhoan() {
        danhSachTaiKhoan.clear();
        String sql = "SELECT TenDangNhap, MatKhau, RoleName, MaNguoiDung FROM TaiKhoanDangNhap_VaiTro";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                TaiKhoanDangNhap_VaiTro tk = new TaiKhoanDangNhap_VaiTro();
                tk.setTenDangNhap(rs.getString("TenDangNhap"));
                tk.setMatKhau(rs.getString("MatKhau"));
                tk.setRoleName(rs.getString("RoleName"));
                tk.setMaNguoiDung(rs.getString("MaNguoiDung"));
                danhSachTaiKhoan.add(tk);
            }

        } catch (Exception e) {
            System.err.println("Lỗi nạp dữ liệu từ DB, dùng danh sách mẫu: " + e.getMessage());
            napDuLieuMauNeuTrong();
        }

        if (danhSachTaiKhoan.isEmpty()) {
            napDuLieuMauNeuTrong();
        }

        locDuLieu();
    }

    private void napDuLieuMauNeuTrong() {
        danhSachTaiKhoan.addAll(
                new TaiKhoanDangNhap_VaiTro("admin_super", "123456", "Admin") {{ setMaNguoiDung("ADM001"); }},
                new TaiKhoanDangNhap_VaiTro("cskh_hoanglan", "123456", "Nhân viên CSKH") {{ setMaNguoiDung("NV001"); }},
                new TaiKhoanDangNhap_VaiTro("gdv_minhquan", "123456", "Giao dịch viên") {{ setMaNguoiDung("NV002"); }},
                new TaiKhoanDangNhap_VaiTro("kh_nguyenvana", "123456", "Khách hàng") {{ setMaNguoiDung("KH001"); }},
                new TaiKhoanDangNhap_VaiTro("kh_tranthib", "123456", "Khách hàng") {{ setMaNguoiDung("KH002"); }}
        );
    }

    // =========================================================
    // TÌM KIẾM & BỘ LỌC
    // =========================================================
    @FXML
    private void onTimKiem() {
        locDuLieu();
    }

    @FXML
    private void onLamMoi() {
        if (txtTimKiem != null) txtTimKiem.clear();
        if (cboLocVaiTro != null) cboLocVaiTro.setValue("Tất cả vai trò");
        taiDuLieuTaiKhoan();
    }

    private void locDuLieu() {
        String keyword = txtTimKiem != null && txtTimKiem.getText() != null
                ? txtTimKiem.getText().trim().toLowerCase() : "";
        String vaiTro = cboLocVaiTro != null ? cboLocVaiTro.getValue() : "Tất cả vai trò";

        danhSachHienThi.clear();
        for (TaiKhoanDangNhap_VaiTro tk : danhSachTaiKhoan) {
            boolean matchKeyword = keyword.isEmpty()
                    || (tk.getTenDangNhap() != null && tk.getTenDangNhap().toLowerCase().contains(keyword))
                    || (tk.getMaNguoiDung() != null && tk.getMaNguoiDung().toLowerCase().contains(keyword));

            boolean matchRole = vaiTro == null
                    || vaiTro.equals("Tất cả vai trò")
                    || (tk.getRoleName() != null && tk.getRoleName().equalsIgnoreCase(vaiTro));

            if (matchKeyword && matchRole) {
                danhSachHienThi.add(tk);
            }
        }

        if (lblTongTaiKhoan != null) {
            lblTongTaiKhoan.setText("Tổng số: " + danhSachHienThi.size() + " tài khoản");
        }
    }

    // =========================================================
    // MODAL 1: TẠO TÀI KHOẢN MỚI
    // =========================================================
    @FXML
    public void onMoPopupTaoTaiKhoan() {
        if (txtModalTenDangNhap != null) txtModalTenDangNhap.clear();
        if (txtModalMatKhau != null) txtModalMatKhau.clear();
        if (txtModalMaNguoiDung != null) txtModalMaNguoiDung.clear();
        if (cboModalRoleName != null) cboModalRoleName.setValue("Giao dịch viên");
        if (lblModalTaoLoi != null) lblModalTaoLoi.setText("");

        hienModal(modalTaoTaiKhoan);
    }

    @FXML
    public void onDongPopupTaoTaiKhoan() {
        anModal(modalTaoTaiKhoan);
    }

    @FXML
    public void onLuuTaiKhoanMoi() {
        String tenDangNhap = txtModalTenDangNhap.getText() != null ? txtModalTenDangNhap.getText().trim() : "";
        String matKhau = txtModalMatKhau.getText() != null ? txtModalMatKhau.getText().trim() : "";
        String maNguoiDung = txtModalMaNguoiDung.getText() != null ? txtModalMaNguoiDung.getText().trim() : "";
        String roleName = cboModalRoleName.getValue();

        if (tenDangNhap.isEmpty() || matKhau.isEmpty() || maNguoiDung.isEmpty() || roleName == null) {
            lblModalTaoLoi.setText("Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        for (TaiKhoanDangNhap_VaiTro tk : danhSachTaiKhoan) {
            if (tk.getTenDangNhap().equalsIgnoreCase(tenDangNhap)) {
                lblModalTaoLoi.setText("Tên đăng nhập đã tồn tại trong hệ thống!");
                return;
            }
        }

        TaiKhoanDangNhap_VaiTro tkMoi = new TaiKhoanDangNhap_VaiTro();
        tkMoi.setTenDangNhap(tenDangNhap);
        tkMoi.setMatKhau(matKhau);
        tkMoi.setRoleName(roleName);
        tkMoi.setMaNguoiDung(maNguoiDung);

        // Lưu vào DB
        String sql = "INSERT INTO TaiKhoanDangNhap_VaiTro (TenDangNhap, MatKhau, RoleName, MaNguoiDung) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenDangNhap);
            ps.setString(2, matKhau);
            ps.setString(3, roleName);
            ps.setString(4, maNguoiDung);
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Lưu DB thất bại: " + e.getMessage());
        }

        danhSachTaiKhoan.add(0, tkMoi);
        locDuLieu();
        anModal(modalTaoTaiKhoan);
    }

    // =========================================================
    // MODAL 2: SỬA VAI TRÒ
    // =========================================================
    @FXML
    public void onMoPopupSuaVaiTro() {
        TaiKhoanDangNhap_VaiTro chon = tblTaiKhoan.getSelectionModel().getSelectedItem();
        if (chon != null) {
            moPopupSuaVaiTroCho(chon);
        } else {
            hienThongBaoChuaChonDong();
        }
    }

    private void moPopupSuaVaiTroCho(TaiKhoanDangNhap_VaiTro tk) {
        this.taiKhoanDangChon = tk;
        txtModalSuaTenDangNhap.setText(tk.getTenDangNhap());
        cboModalSuaRoleName.setValue(tk.getRoleName());
        hienModal(modalSuaVaiTro);
    }

    @FXML
    public void onDongPopupSuaVaiTro() {
        anModal(modalSuaVaiTro);
    }

    @FXML
    public void onLuuSuaVaiTro() {
        if (taiKhoanDangChon == null) return;
        String vaiTroMoi = cboModalSuaRoleName.getValue();
        if (vaiTroMoi == null) return;

        taiKhoanDangChon.setRoleName(vaiTroMoi);

        String sql = "UPDATE TaiKhoanDangNhap_VaiTro SET RoleName = ? WHERE TenDangNhap = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, vaiTroMoi);
            ps.setString(2, taiKhoanDangChon.getTenDangNhap());
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Cập nhật vai trò DB thất bại: " + e.getMessage());
        }

        tblTaiKhoan.refresh();
        locDuLieu();
        anModal(modalSuaVaiTro);
    }

    // =========================================================
    // MODAL 3: ĐẶT LẠI MẬT KHẨU
    // =========================================================
    @FXML
    public void onMoPopupDatLaiMatKhau() {
        TaiKhoanDangNhap_VaiTro chon = tblTaiKhoan.getSelectionModel().getSelectedItem();
        if (chon != null) {
            moPopupDatLaiMatKhauCho(chon);
        } else {
            hienThongBaoChuaChonDong();
        }
    }

    private void moPopupDatLaiMatKhauCho(TaiKhoanDangNhap_VaiTro tk) {
        this.taiKhoanDangChon = tk;
        lblModalDatLaiTK.setText("Tài khoản: " + tk.getTenDangNhap());
        rbMatKhauNgauNhien.setSelected(true);
        boxNhapMatKhauMoi.setDisable(true);
        txtModalMatKhauMoi.clear();
        lblModalThongBaoMatKhau.setText("");
        hienModal(modalDatLaiMatKhau);
    }

    @FXML
    public void onChonCheDoMatKhau() {
        if (rbNhapMatKhauMoi.isSelected()) {
            boxNhapMatKhauMoi.setDisable(false);
            txtModalMatKhauMoi.requestFocus();
        } else {
            boxNhapMatKhauMoi.setDisable(true);
        }
    }

    @FXML
    public void onDongPopupDatLaiMatKhau() {
        anModal(modalDatLaiMatKhau);
    }

    @FXML
    public void onXacNhanDatLaiMatKhau() {
        if (taiKhoanDangChon == null) return;

        String matKhauMoi;
        if (rbMatKhauNgauNhien.isSelected()) {
            matKhauMoi = sinhMatKhauNgauNhien(8);
        } else {
            matKhauMoi = txtModalMatKhauMoi.getText() != null ? txtModalMatKhauMoi.getText().trim() : "";
            if (matKhauMoi.length() < 6) {
                lblModalThongBaoMatKhau.setStyle("-fx-text-fill: #D9534F; -fx-font-weight: bold;");
                lblModalThongBaoMatKhau.setText("Mật khẩu mới phải có tối thiểu 6 ký tự!");
                return;
            }
        }

        taiKhoanDangChon.setMatKhau(matKhauMoi);

        // Update DB
        String sql = "UPDATE TaiKhoanDangNhap_VaiTro SET MatKhau = ? WHERE TenDangNhap = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, matKhauMoi);
            ps.setString(2, taiKhoanDangChon.getTenDangNhap());
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Cập nhật mật khẩu DB thất bại: " + e.getMessage());
        }

        lblModalThongBaoMatKhau.setStyle("-fx-text-fill: #10B981; -fx-font-weight: bold;");
        lblModalThongBaoMatKhau.setText("✓ Đặt lại thành công! Mật khẩu mới: " + matKhauMoi);
    }

    private String sinhMatKhauNgauNhien(int length) {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789!@#$";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    // =========================================================
    // MODAL 4: XÓA TÀI KHOẢN
    // =========================================================
    @FXML
    public void onMoPopupXacNhanXoa() {
        TaiKhoanDangNhap_VaiTro chon = tblTaiKhoan.getSelectionModel().getSelectedItem();
        if (chon != null) {
            moPopupXacNhanXoaCho(chon);
        } else {
            hienThongBaoChuaChonDong();
        }
    }

    private void moPopupXacNhanXoaCho(TaiKhoanDangNhap_VaiTro tk) {
        this.taiKhoanDangChon = tk;
        lblModalXoaTenTK.setText("Tài khoản: " + tk.getTenDangNhap() + " (" + tk.getRoleName() + ")");
        hienModal(modalXacNhanXoa);
    }

    @FXML
    public void onDongPopupXacNhanXoa() {
        anModal(modalXacNhanXoa);
    }

    @FXML
    public void onDongYXoaTaiKhoan() {
        if (taiKhoanDangChon == null) return;

        String sql = "DELETE FROM TaiKhoanDangNhap_VaiTro WHERE TenDangNhap = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, taiKhoanDangChon.getTenDangNhap());
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Xóa DB thất bại: " + e.getMessage());
        }

        danhSachTaiKhoan.remove(taiKhoanDangChon);
        locDuLieu();
        anModal(modalXacNhanXoa);
    }

    private void hienModal(StackPane modal) {
        if (modal != null) {
            modal.setVisible(true);
            modal.setManaged(true);
        }
    }

    private void anModal(StackPane modal) {
        if (modal != null) {
            modal.setVisible(false);
            modal.setManaged(false);
        }
    }

    private void hienThongBaoChuaChonDong() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText("Vui lòng chọn một dòng tài khoản trong bảng để thực hiện thao tác!");
        alert.showAndWait();
    }

    // =========================================================
    // ĐIỀU HƯỚNG SIDEBAR
    // =========================================================
    @FXML
    private void onTaiKhoan(ActionEvent event) {
        // Đang ở màn hình này
    }

    @FXML
    private void onNhanVienChiNhanh(ActionEvent event) {
        moView("admin-employee-branch-view.fxml", event);
    }

    @FXML
    private void onCauHinhDanhMuc(ActionEvent event) {
        moView("admin-system-config-view.fxml", event);
    }

    @FXML
    private void onThongKeBaoCao(ActionEvent event) {
        moView("admin-report-view.fxml", event);
    }

    @FXML
    private void onDangXuat(ActionEvent event) {
        moView("login-view.fxml", event);
    }
}
