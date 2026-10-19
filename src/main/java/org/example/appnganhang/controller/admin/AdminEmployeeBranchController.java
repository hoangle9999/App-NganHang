package org.example.appnganhang.controller.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.example.appnganhang.controller.service.Iml.AdminEmployBranchServiceImpl;
import org.example.appnganhang.controller.service.intf.AdminEmployBranchService;
import org.example.appnganhang.model.entity.ChiNhanh;
import org.example.appnganhang.model.entity.NhanVien;

import static org.example.appnganhang.controller.util.Navigation.moView;

public class AdminEmployeeBranchController {
    private static final String ALL_BRANCHES = "Tất cả chi nhánh";
    private static final ObservableList<String> ROLES = FXCollections.observableArrayList("Admin", "Nhân viên CSKH", "Giao dịch viên");

    @FXML private TextField txtTimKiemNV, txtTimKiemCN;
    @FXML private ComboBox<String> cboLocChiNhanhNV;
    @FXML private Label lblTongNV, lblTongCN;
    @FXML private TableView<NhanVien> tblNhanVien;
    @FXML private TableColumn<NhanVien, String> colMaNV, colHoTenNV, colSoDienThoaiNV, colChucVuNV, colChiNhanhNV;
    @FXML private StackPane modalThemNV, modalLienKetTK, modalSuaNV, modalXoaNV, modalThemSuaCN, modalXoaCN;
    @FXML private TextField txtModalThemHoTenNV, txtModalThemSDTNV, txtModalThemDiaChiNV;
    @FXML private ComboBox<String> cboModalThemChucVuNV, cboModalThemChiNhanhNV;
    @FXML private Label lblModalThemNVLoi;
    @FXML private Label lblModalLienKetMaNV, lblModalLienKetHoTen, lblModalLienKetLoi;
    @FXML private ComboBox<String> cboModalChonTaiKhoan;
    @FXML private Label lblModalSuaMaNV;
    @FXML private TextField txtModalSuaHoTenNV, txtModalSuaSDTNV, txtModalSuaDiaChiNV;
    @FXML private ComboBox<String> cboModalSuaChucVuNV, cboModalSuaChiNhanhNV;
    @FXML private Label lblModalXoaTenNV;
    @FXML private TableView<ChiNhanh> tblChiNhanh;
    @FXML private TableColumn<ChiNhanh, String> colMaCN, colTenCN, colDiaChiCN;
    @FXML private Label lblModalCNTitle, lblModalMaCN;
    @FXML private VBox boxModalMaCN;
    @FXML private TextField txtModalTenCN, txtModalDiaChiCN;
    @FXML private Label lblModalXoaTenCN;

    private final ObservableList<NhanVien> danhSachNhanVien = FXCollections.observableArrayList();
    private final ObservableList<NhanVien> danhSachHienThiNV = FXCollections.observableArrayList();
    private final ObservableList<ChiNhanh> danhSachChiNhanh = FXCollections.observableArrayList();
    private final ObservableList<ChiNhanh> danhSachHienThiCN = FXCollections.observableArrayList();
    private final AdminEmployBranchService service = new AdminEmployBranchServiceImpl();
    private NhanVien nhanVienDangChon;
    private ChiNhanh chiNhanhDangChon;
    private boolean isSuaChiNhanh;

    @FXML public void initialize() {
        colMaNV.setCellValueFactory(new PropertyValueFactory<>("maNhanVien"));
        colHoTenNV.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        colSoDienThoaiNV.setCellValueFactory(new PropertyValueFactory<>("soDienThoai"));
        colChucVuNV.setCellValueFactory(new PropertyValueFactory<>("chucVu"));
        colChiNhanhNV.setCellValueFactory(new PropertyValueFactory<>("tenChiNhanh"));
        colMaCN.setCellValueFactory(new PropertyValueFactory<>("maChiNhanh"));
        colTenCN.setCellValueFactory(new PropertyValueFactory<>("tenChiNhanh"));
        colDiaChiCN.setCellValueFactory(new PropertyValueFactory<>("diaChi"));
        tblNhanVien.setItems(danhSachHienThiNV);
        tblChiNhanh.setItems(danhSachHienThiCN);
        cboModalThemChucVuNV.setItems(FXCollections.observableArrayList(ROLES));
        cboModalSuaChucVuNV.setItems(FXCollections.observableArrayList(ROLES));
        taiDuLieuChiNhanh();
        taiDuLieuNhanVien();
    }

    private void taiDuLieuChiNhanh() {
        try {
            danhSachChiNhanh.setAll(service.layDanhSachChiNhanh());
            danhSachHienThiCN.setAll(danhSachChiNhanh);
            cboLocChiNhanhNV.setItems(FXCollections.observableArrayList(ALL_BRANCHES));
            danhSachChiNhanh.forEach(cn -> cboLocChiNhanhNV.getItems().add(cn.getTenChiNhanh()));
            cboLocChiNhanhNV.setValue(ALL_BRANCHES);
            cboModalThemChiNhanhNV.setItems(FXCollections.observableArrayList(danhSachChiNhanh.stream().map(ChiNhanh::getTenChiNhanh).toList()));
            cboModalSuaChiNhanhNV.setItems(FXCollections.observableArrayList(danhSachChiNhanh.stream().map(ChiNhanh::getTenChiNhanh).toList()));
            capNhatTongChiNhanh();
        } catch (RuntimeException e) { hienCanhBao("Lỗi tải chi nhánh", e.getMessage()); }
    }

    private void taiDuLieuNhanVien() {
        try { danhSachNhanVien.setAll(service.layDanhSachNhanVien()); locNhanVien(); }
        catch (RuntimeException e) { danhSachNhanVien.clear(); locNhanVien(); hienCanhBao("Lỗi tải nhân viên", e.getMessage()); }
    }

    @FXML private void onTimKiemNV() { locNhanVien(); }
    @FXML private void onLamMoiNV() { txtTimKiemNV.clear(); taiDuLieuChiNhanh(); taiDuLieuNhanVien(); }
    private void locNhanVien() {
        String keyword = txtTimKiemNV.getText() == null ? "" : txtTimKiemNV.getText().trim().toLowerCase();
        String branch = cboLocChiNhanhNV.getValue();
        danhSachHienThiNV.setAll(danhSachNhanVien.stream().filter(nv ->
                (keyword.isEmpty() || safe(nv.getHoTen()).toLowerCase().contains(keyword)
                        || safe(nv.getSoDienThoai()).toLowerCase().contains(keyword)
                        || safe(nv.getMaNhanVien()).toLowerCase().contains(keyword))
                        && (branch == null || ALL_BRANCHES.equals(branch) || branch.equalsIgnoreCase(nv.getTenChiNhanh()))).toList());
        lblTongNV.setText("Tổng số: " + danhSachHienThiNV.size() + " nhân viên");
    }

    @FXML public void onMoPopupThemNV() {
        txtModalThemHoTenNV.clear(); txtModalThemSDTNV.clear(); txtModalThemDiaChiNV.clear(); lblModalThemNVLoi.setText("");
        cboModalThemChucVuNV.getSelectionModel().clearSelection();
        if (!cboModalThemChiNhanhNV.getItems().isEmpty()) cboModalThemChiNhanhNV.getSelectionModel().selectFirst();
        hienModal(modalThemNV);
    }
    @FXML public void onDongPopupThemNV() { anModal(modalThemNV); }
    @FXML public void onLuuThemNV() {
        try {
            ChiNhanh branch = chiNhanhTheoTen(cboModalThemChiNhanhNV.getValue());
            if (branch == null) throw new IllegalArgumentException("Vui lòng chọn chi nhánh.");
            NhanVien nv = new NhanVien();
            nv.setHoTen(txtModalThemHoTenNV.getText().trim()); nv.setSoDienThoai(txtModalThemSDTNV.getText().trim());
            nv.setDiaChi(txtModalThemDiaChiNV.getText().trim()); nv.setChucVu(cboModalThemChucVuNV.getValue()); nv.setMaChiNhanh(branch.getMaChiNhanh());
            String ma = service.themNhanVien(nv);
            anModal(modalThemNV); taiDuLieuNhanVien(); hienThongTin("Thêm nhân viên", "Đã tạo nhân viên với mã " + ma + ".");
        } catch (RuntimeException e) { lblModalThemNVLoi.setText(e.getMessage()); }
    }

    @FXML public void onMoPopupLienKetTK() {
        NhanVien selected = tblNhanVien.getSelectionModel().getSelectedItem();
        if (selected == null) { hienThongBaoChuaChonDong(); return; }
        nhanVienDangChon = selected; lblModalLienKetMaNV.setText(selected.getMaNhanVien()); lblModalLienKetHoTen.setText(selected.getHoTen()); lblModalLienKetLoi.setText("");
        try {
            cboModalChonTaiKhoan.setItems(FXCollections.observableArrayList(service.layTaiKhoanNhanVienChuaGan(selected.getMaNhanVien())));
            if (selected.getTenDangNhap() != null) cboModalChonTaiKhoan.setValue(selected.getTenDangNhap());
            else cboModalChonTaiKhoan.getSelectionModel().clearSelection();
            hienModal(modalLienKetTK);
        } catch (RuntimeException e) { lblModalLienKetLoi.setText(e.getMessage()); }
    }
    @FXML public void onDongPopupLienKetTK() { anModal(modalLienKetTK); }
    @FXML public void onXacNhanLienKetTK() {
        if (nhanVienDangChon == null) return;
        try { service.lienKetTaiKhoanNhanVien(nhanVienDangChon.getMaNhanVien(), cboModalChonTaiKhoan.getValue()); anModal(modalLienKetTK); taiDuLieuNhanVien(); }
        catch (RuntimeException e) { lblModalLienKetLoi.setText(e.getMessage()); }
    }

    @FXML public void onMoPopupSuaNV() {
        NhanVien selected = tblNhanVien.getSelectionModel().getSelectedItem();
        if (selected == null) { hienThongBaoChuaChonDong(); return; }
        nhanVienDangChon = selected; lblModalSuaMaNV.setText("Mã NV: " + selected.getMaNhanVien());
        txtModalSuaHoTenNV.setText(selected.getHoTen()); txtModalSuaSDTNV.setText(selected.getSoDienThoai()); txtModalSuaDiaChiNV.setText(selected.getDiaChi());
        String chucVu = selected.getChucVu();
        if ("CSKH".equalsIgnoreCase(chucVu)) chucVu = "Nhân viên CSKH";
        if ("Quản trị viên".equalsIgnoreCase(chucVu)) chucVu = "Admin";
        cboModalSuaChucVuNV.setValue(chucVu); cboModalSuaChiNhanhNV.setValue(selected.getTenChiNhanh()); hienModal(modalSuaNV);
    }
    @FXML public void onDongPopupSuaNV() { anModal(modalSuaNV); }
    @FXML public void onCapNhatNV() {
        if (nhanVienDangChon == null) return;
        try {
            ChiNhanh branch = chiNhanhTheoTen(cboModalSuaChiNhanhNV.getValue());
            if (branch == null) throw new IllegalArgumentException("Vui lòng chọn chi nhánh.");
            nhanVienDangChon.setHoTen(txtModalSuaHoTenNV.getText().trim()); nhanVienDangChon.setSoDienThoai(txtModalSuaSDTNV.getText().trim());
            nhanVienDangChon.setDiaChi(txtModalSuaDiaChiNV.getText().trim()); nhanVienDangChon.setChucVu(cboModalSuaChucVuNV.getValue()); nhanVienDangChon.setMaChiNhanh(branch.getMaChiNhanh());
            service.capNhatNhanVien(nhanVienDangChon); anModal(modalSuaNV); taiDuLieuNhanVien();
        } catch (RuntimeException e) { hienCanhBao("Không thể cập nhật nhân viên", e.getMessage()); }
    }
    @FXML public void onMoPopupXoaNV() {
        NhanVien selected = tblNhanVien.getSelectionModel().getSelectedItem();
        if (selected == null) { hienThongBaoChuaChonDong(); return; }
        nhanVienDangChon = selected; lblModalXoaTenNV.setText("Nhân viên: [" + selected.getMaNhanVien() + "] " + selected.getHoTen()); hienModal(modalXoaNV);
    }
    @FXML public void onDongPopupXoaNV() { anModal(modalXoaNV); }
    @FXML public void onDongYXoaNV() {
        if (nhanVienDangChon == null) return;
        try { service.xoaNhanVien(nhanVienDangChon.getMaNhanVien()); anModal(modalXoaNV); taiDuLieuNhanVien(); }
        catch (RuntimeException e) { hienCanhBao("Không thể xóa nhân viên", e.getMessage()); }
    }

    @FXML private void onTimKiemCN() {
        String keyword = txtTimKiemCN.getText() == null ? "" : txtTimKiemCN.getText().trim().toLowerCase();
        danhSachHienThiCN.setAll(danhSachChiNhanh.stream().filter(cn -> keyword.isEmpty()
                || safe(cn.getMaChiNhanh()).toLowerCase().contains(keyword) || safe(cn.getTenChiNhanh()).toLowerCase().contains(keyword)
                || safe(cn.getDiaChi()).toLowerCase().contains(keyword)).toList()); capNhatTongChiNhanh();
    }
    @FXML private void onLamMoiCN() { txtTimKiemCN.clear(); taiDuLieuChiNhanh(); }
    private void capNhatTongChiNhanh() { lblTongCN.setText("Tổng số: " + danhSachHienThiCN.size() + " chi nhánh"); }

    @FXML public void onMoPopupThemCN() {
        isSuaChiNhanh = false; chiNhanhDangChon = null; lblModalCNTitle.setText("Thêm chi nhánh mới");
        boxModalMaCN.setVisible(false); boxModalMaCN.setManaged(false); txtModalTenCN.clear(); txtModalDiaChiCN.clear(); hienModal(modalThemSuaCN);
    }
    @FXML public void onMoPopupSuaCN() {
        ChiNhanh selected = tblChiNhanh.getSelectionModel().getSelectedItem();
        if (selected == null) { hienThongBaoChuaChonDong(); return; }
        chiNhanhDangChon = selected; isSuaChiNhanh = true; lblModalCNTitle.setText("Chỉnh sửa chi nhánh");
        lblModalMaCN.setText(selected.getMaChiNhanh()); boxModalMaCN.setVisible(true); boxModalMaCN.setManaged(true);
        txtModalTenCN.setText(selected.getTenChiNhanh()); txtModalDiaChiCN.setText(selected.getDiaChi()); hienModal(modalThemSuaCN);
    }
    @FXML public void onDongPopupThemSuaCN() { anModal(modalThemSuaCN); }
    @FXML public void onLuuThemSuaCN() {
        try {
            ChiNhanh cn = new ChiNhanh(); cn.setTenChiNhanh(txtModalTenCN.getText().trim()); cn.setDiaChi(txtModalDiaChiCN.getText().trim());
            if (isSuaChiNhanh && chiNhanhDangChon != null) {
                cn.setMaChiNhanh(chiNhanhDangChon.getMaChiNhanh()); service.capNhatChiNhanh(cn);
            } else {
                String ma = service.themChiNhanh(cn); hienThongTin("Thêm chi nhánh", "Đã tạo chi nhánh với mã " + ma + ".");
            }
            anModal(modalThemSuaCN); taiDuLieuChiNhanh(); taiDuLieuNhanVien();
        } catch (RuntimeException e) { hienCanhBao("Không thể lưu chi nhánh", e.getMessage()); }
    }
    @FXML public void onMoPopupXoaCN() {
        ChiNhanh selected = tblChiNhanh.getSelectionModel().getSelectedItem();
        if (selected == null) { hienThongBaoChuaChonDong(); return; }
        chiNhanhDangChon = selected; lblModalXoaTenCN.setText("Chi nhánh: [" + selected.getMaChiNhanh() + "] " + selected.getTenChiNhanh()); hienModal(modalXoaCN);
    }
    @FXML public void onDongPopupXoaCN() { anModal(modalXoaCN); }
    @FXML public void onDongYXoaCN() {
        if (chiNhanhDangChon == null) return;
        try { service.xoaChiNhanh(chiNhanhDangChon.getMaChiNhanh()); anModal(modalXoaCN); taiDuLieuChiNhanh(); taiDuLieuNhanVien(); }
        catch (RuntimeException e) { hienCanhBao("Không thể xóa chi nhánh", e.getMessage()); }
    }

    private ChiNhanh chiNhanhTheoTen(String ten) { return danhSachChiNhanh.stream().filter(cn -> cn.getTenChiNhanh().equals(ten)).findFirst().orElse(null); }
    private String safe(String value) { return value == null ? "" : value; }
    private void hienModal(StackPane modal) { modal.setVisible(true); modal.setManaged(true); }
    private void anModal(StackPane modal) { modal.setVisible(false); modal.setManaged(false); }
    private void hienThongBaoChuaChonDong() { hienCanhBao("Thông báo", "Vui lòng chọn một dòng trong bảng để thực hiện."); }
    private void hienCanhBao(String title, String message) { Alert a = new Alert(Alert.AlertType.WARNING); a.setTitle(title); a.setHeaderText(null); a.setContentText(message); a.showAndWait(); }
    private void hienThongTin(String title, String message) { Alert a = new Alert(Alert.AlertType.INFORMATION); a.setTitle(title); a.setHeaderText(null); a.setContentText(message); a.showAndWait(); }

    @FXML private void onTrangChu(ActionEvent event) { moView("admin-dashboard-view.fxml", event); }
    @FXML private void onTaiKhoan(ActionEvent event) { moView("admin-account-view.fxml", event); }
    @FXML private void onNhanVienChiNhanh(ActionEvent event) { }
    @FXML private void onCauHinhDanhMuc(ActionEvent event) { moView("admin-system-config-view.fxml", event); }
    @FXML private void onThongKeBaoCao(ActionEvent event) { moView("admin-report-view.fxml", event); }
    @FXML private void onDangXuat(ActionEvent event) { moView("login-view.fxml", event); }
}
