package org.example.appnganhang.controller.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.example.appnganhang.controller.service.Iml.AdminSystemConfigServiceImpl;
import org.example.appnganhang.controller.service.intf.AdminSystemConfigService;
import org.example.appnganhang.model.entity.HanMucGiaoDich;
import org.example.appnganhang.model.entity.LoaiGiaoDich;
import org.example.appnganhang.model.entity.NganHang;
import org.example.appnganhang.model.entity.ThongTinTaiKhoanCauHinh;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Optional;

import static org.example.appnganhang.controller.util.Navigation.moView;

public class AdminSystemConfigController {
    @FXML private TextField txtTimKiemHanMuc;
    @FXML private Label lblTongGoiHanMuc;
    @FXML private TableView<HanMucGiaoDich> tblHanMuc;
    @FXML private TableColumn<HanMucGiaoDich, String> colMaHanMuc, colTenHanMuc;
    @FXML private TableColumn<HanMucGiaoDich, BigDecimal> colHanMucNgay;
    @FXML private StackPane modalThemSuaHanMuc;
    @FXML private Label lblModalHanMucTitle, lblModalMaHanMuc, lblModalHanMucLoi;
    @FXML private VBox boxModalMaHanMuc;
    @FXML private TextField txtModalTenHanMuc, txtModalHanMucNgay;

    @FXML private TextField txtTraCuuTK;
    @FXML private HBox boxLoadingTK;
    @FXML private VBox cardDetailTK;
    @FXML private Label lblBadgeTrangThaiTK, lblDetailSoTK, lblDetailTenKH, lblDetailSoDu,
            lblDetailChiNhanh, lblDetailGoiHanMucHienTai, lblThongBaoCapNhatTK;
    @FXML private ComboBox<HanMucGiaoDich> cboChonGoiHanMucMoi;
    @FXML private StackPane modalXacNhanDoiHanMuc, modalDoiTrangThaiTK;
    @FXML private Label lblNoiDungXacNhanDoiHanMuc, lblNoiDungDoiTrangThaiTK;

    @FXML private TableView<LoaiGiaoDich> tblLoaiGD;
    @FXML private TableColumn<LoaiGiaoDich, String> colMaLoaiGD, colTenLoaiGD;
    @FXML private StackPane modalThemSuaLoaiGD;
    @FXML private Label lblModalLoaiGDTitle, lblModalLoaiGDLoi;
    @FXML private TextField txtModalMaLoaiGD, txtModalTenLoaiGD;

    @FXML private TableView<NganHang> tblNganHang;
    @FXML private TableColumn<NganHang, String> colMaNganHang, colTenNganHang, colTrangThaiNganHang;

    private final AdminSystemConfigService service = new AdminSystemConfigServiceImpl();
    private final ObservableList<HanMucGiaoDich> danhSachHanMuc = FXCollections.observableArrayList();
    private final ObservableList<HanMucGiaoDich> danhSachHienThiHanMuc = FXCollections.observableArrayList();
    private final ObservableList<LoaiGiaoDich> danhSachLoaiGD = FXCollections.observableArrayList();
    private final ObservableList<NganHang> danhSachNganHang = FXCollections.observableArrayList();
    private final DecimalFormat dfTien = new DecimalFormat("#,##0.## VNĐ");
    private HanMucGiaoDich hanMucDangChon;
    private LoaiGiaoDich loaiGDDangChon;
    private ThongTinTaiKhoanCauHinh taiKhoanDangTraCuu;
    private boolean isSuaHanMuc;
    private boolean isSuaLoaiGD;

    @FXML public void initialize() {
        cauHinhBangHanMuc();
        cauHinhBangLoaiGiaoDich();
        cauHinhBangNganHang();
        cauHinhComboBoxHanMuc();

        cardDetailTK.setVisible(false); cardDetailTK.setManaged(false);
        taiDuLieuHanMuc();
        taiDuLieuLoaiGiaoDich();
        taiDuLieuNganHang();
    }

    private void cauHinhBangHanMuc() {
        colMaHanMuc.setCellValueFactory(new PropertyValueFactory<>("maHanMuc"));
        colTenHanMuc.setCellValueFactory(new PropertyValueFactory<>("tenHanMuc"));
        colHanMucNgay.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getHanMucNgay()));
        colHanMucNgay.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(BigDecimal amount, boolean empty) {
                super.updateItem(amount, empty); setText(empty || amount == null ? null : dfTien.format(amount));
            }
        });
        tblHanMuc.setItems(danhSachHienThiHanMuc);
    }

    private void cauHinhComboBoxHanMuc() {
        cboChonGoiHanMucMoi.setConverter(new StringConverter<>() {
            @Override public String toString(HanMucGiaoDich hm) { return hm == null ? "" : hm.getTenHanMuc() + " (" + dfTien.format(hm.getHanMucNgay()) + "/ngày)"; }
            @Override public HanMucGiaoDich fromString(String value) { return null; }
        });
    }

    private void taiDuLieuHanMuc() {
        try {
            danhSachHanMuc.setAll(service.layDanhSachHanMuc()); danhSachHienThiHanMuc.setAll(danhSachHanMuc);
            lblTongGoiHanMuc.setText("Tổng số: " + danhSachHienThiHanMuc.size() + " gói");
            cboChonGoiHanMucMoi.setItems(FXCollections.observableArrayList(danhSachHanMuc));
        } catch (RuntimeException e) { danhSachHanMuc.clear(); danhSachHienThiHanMuc.clear(); cboChonGoiHanMucMoi.getItems().clear(); hienCanhBao("Lỗi tải hạn mức", e.getMessage()); }
    }

    @FXML private void onTimKiemHanMuc() {
        String keyword = txtTimKiemHanMuc.getText() == null ? "" : txtTimKiemHanMuc.getText().trim().toLowerCase();
        danhSachHienThiHanMuc.setAll(danhSachHanMuc.stream().filter(hm -> keyword.isEmpty()
                || safe(hm.getMaHanMuc()).toLowerCase().contains(keyword) || safe(hm.getTenHanMuc()).toLowerCase().contains(keyword)).toList());
        lblTongGoiHanMuc.setText("Tổng số: " + danhSachHienThiHanMuc.size() + " gói");
    }
    @FXML private void onLamMoiHanMuc() { txtTimKiemHanMuc.clear(); taiDuLieuHanMuc(); }

    @FXML public void onMoPopupTaoGoiHanMuc() {
        isSuaHanMuc = false; hanMucDangChon = null; lblModalHanMucTitle.setText("Tạo gói hạn mức giao dịch");
        boxModalMaHanMuc.setVisible(false); boxModalMaHanMuc.setManaged(false);
        txtModalTenHanMuc.clear(); txtModalHanMucNgay.clear(); lblModalHanMucLoi.setText(""); hienModal(modalThemSuaHanMuc);
    }
    @FXML public void onMoPopupSuaHanMuc() {
        HanMucGiaoDich selected = tblHanMuc.getSelectionModel().getSelectedItem();
        if (selected == null) { hienThongBaoChuaChonDong(); return; } moPopupSuaHanMucCho(selected);
    }
    private void moPopupSuaHanMucCho(HanMucGiaoDich hm) {
        hanMucDangChon = hm; isSuaHanMuc = true; lblModalHanMucTitle.setText("Chỉnh sửa gói hạn mức");
        lblModalMaHanMuc.setText(hm.getMaHanMuc()); boxModalMaHanMuc.setVisible(true); boxModalMaHanMuc.setManaged(true);
        txtModalTenHanMuc.setText(hm.getTenHanMuc()); txtModalHanMucNgay.setText(hm.getHanMucNgay().toPlainString()); lblModalHanMucLoi.setText(""); hienModal(modalThemSuaHanMuc);
    }
    @FXML public void onDongPopupThemSuaHanMuc() { anModal(modalThemSuaHanMuc); }
    @FXML public void onLuuThemSuaHanMuc() {
        try {
            BigDecimal amount = new BigDecimal(txtModalHanMucNgay.getText().trim());
            if (isSuaHanMuc && hanMucDangChon != null) {
                hanMucDangChon.setTenHanMuc(txtModalTenHanMuc.getText().trim()); hanMucDangChon.setHanMucNgay(amount); service.capNhatHanMuc(hanMucDangChon);
            } else {
                String code = service.taoHanMuc(txtModalTenHanMuc.getText(), amount);
                hienThongTin("Tạo gói hạn mức", "Đã tạo gói hạn mức với mã " + code + ".");
            }
            anModal(modalThemSuaHanMuc); taiDuLieuHanMuc(); onTimKiemHanMuc();
        } catch (NumberFormatException e) { lblModalHanMucLoi.setText("Hạn mức ngày phải là số hợp lệ."); }
        catch (RuntimeException e) { lblModalHanMucLoi.setText(e.getMessage()); }
    }
    @FXML public void onMoPopupXoaHanMuc() {
        HanMucGiaoDich selected = tblHanMuc.getSelectionModel().getSelectedItem();
        if (selected == null) { hienThongBaoChuaChonDong(); return; } xoaHanMuc(selected);
    }
    private void xoaHanMuc(HanMucGiaoDich hm) {
        if (!xacNhan("Xác nhận xóa gói hạn mức", "Bạn có chắc chắn muốn xóa gói hạn mức [" + hm.getMaHanMuc() + "] " + hm.getTenHanMuc() + "?")) return;
        try { service.xoaHanMuc(hm.getMaHanMuc()); taiDuLieuHanMuc(); onTimKiemHanMuc(); }
        catch (RuntimeException e) { hienCanhBao("Không thể xóa hạn mức", e.getMessage()); }
    }

    private void taiDuLieuLoaiGiaoDich() {
        try { danhSachLoaiGD.setAll(service.layDanhSachLoaiGiaoDich()); tblLoaiGD.setItems(danhSachLoaiGD); }
        catch (RuntimeException e) { danhSachLoaiGD.clear(); hienCanhBao("Lỗi tải loại giao dịch", e.getMessage()); }
    }
    private void cauHinhBangLoaiGiaoDich() {
        colMaLoaiGD.setCellValueFactory(new PropertyValueFactory<>("maLoaiGD")); colTenLoaiGD.setCellValueFactory(new PropertyValueFactory<>("tenLoaiGD"));
        tblLoaiGD.setItems(danhSachLoaiGD);
    }
    @FXML public void onMoPopupThemLoaiGD() {
        isSuaLoaiGD = false; loaiGDDangChon = null; lblModalLoaiGDTitle.setText("Thêm loại giao dịch mới");
        txtModalMaLoaiGD.setDisable(false); txtModalMaLoaiGD.clear(); txtModalTenLoaiGD.clear(); lblModalLoaiGDLoi.setText(""); hienModal(modalThemSuaLoaiGD);
    }
    @FXML public void onMoPopupSuaLoaiGD() {
        LoaiGiaoDich selected = tblLoaiGD.getSelectionModel().getSelectedItem();
        if (selected == null) { hienThongBaoChuaChonDong(); return; } moPopupSuaLoaiGDCho(selected);
    }
    private void moPopupSuaLoaiGDCho(LoaiGiaoDich x) {
        loaiGDDangChon = x; isSuaLoaiGD = true; lblModalLoaiGDTitle.setText("Chỉnh sửa loại giao dịch");
        txtModalMaLoaiGD.setText(x.getMaLoaiGD()); txtModalMaLoaiGD.setDisable(true); txtModalTenLoaiGD.setText(x.getTenLoaiGD()); lblModalLoaiGDLoi.setText(""); hienModal(modalThemSuaLoaiGD);
    }
    @FXML public void onDongPopupThemSuaLoaiGD() { anModal(modalThemSuaLoaiGD); }
    @FXML public void onLuuThemSuaLoaiGD() {
        try {
            if (isSuaLoaiGD && loaiGDDangChon != null) { loaiGDDangChon.setTenLoaiGD(txtModalTenLoaiGD.getText().trim()); service.capNhatLoaiGiaoDich(loaiGDDangChon); }
            else { String code = service.taoLoaiGiaoDich(txtModalMaLoaiGD.getText(), txtModalTenLoaiGD.getText()); hienThongTin("Thêm loại giao dịch", "Mã loại giao dịch được tạo: " + code); }
            anModal(modalThemSuaLoaiGD); taiDuLieuLoaiGiaoDich();
        } catch (RuntimeException e) { lblModalLoaiGDLoi.setText(e.getMessage()); }
    }
    @FXML public void onMoPopupXoaLoaiGD() {
        LoaiGiaoDich selected = tblLoaiGD.getSelectionModel().getSelectedItem();
        if (selected == null) { hienThongBaoChuaChonDong(); return; } xoaLoaiGD(selected);
    }
    private void xoaLoaiGD(LoaiGiaoDich x) {
        if (!xacNhan("Xác nhận xóa", "Bạn có muốn xóa loại giao dịch [" + x.getMaLoaiGD() + "] " + x.getTenLoaiGD() + "?")) return;
        try { service.xoaLoaiGiaoDich(x.getMaLoaiGD()); taiDuLieuLoaiGiaoDich(); }
        catch (RuntimeException e) { hienCanhBao("Không thể xóa loại giao dịch", e.getMessage()); }
    }

    private void cauHinhBangNganHang() {
        colMaNganHang.setCellValueFactory(new PropertyValueFactory<>("maNganHang")); colTenNganHang.setCellValueFactory(new PropertyValueFactory<>("tenNganHang"));
        colTrangThaiNganHang.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        colTrangThaiNganHang.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty); if (empty) { setText(null); return; }
                setText("Ngừng hoạt động".equals(status) ? "Tạm ngừng" : status);
            }
        });
        tblNganHang.setItems(danhSachNganHang);
    }
    private void taiDuLieuNganHang() {
        try { danhSachNganHang.setAll(service.layDanhSachNganHang()); }
        catch (RuntimeException e) { danhSachNganHang.clear(); hienCanhBao("Lỗi tải ngân hàng", e.getMessage()); }
    }
    @FXML public void onToggleTrangThaiNganHang() {
        NganHang selected = tblNganHang.getSelectionModel().getSelectedItem();
        if (selected == null) { hienThongBaoChuaChonDong(); return; } toggleTrangThaiNganHang(selected);
    }
    private void toggleTrangThaiNganHang(NganHang bank) {
        try { service.doiTrangThaiNganHang(bank.getMaNganHang()); taiDuLieuNganHang(); }
        catch (RuntimeException e) { hienCanhBao("Không thể đổi trạng thái ngân hàng", e.getMessage()); }
    }

    @FXML private void onTraCuuTaiKhoan() {
        String keyword = txtTraCuuTK.getText() == null ? "" : txtTraCuuTK.getText().trim();
        if (keyword.isEmpty()) { hienCanhBao("Tra cứu tài khoản", "Vui lòng nhập số tài khoản hoặc mã khách hàng."); return; }
        boxLoadingTK.setVisible(true); boxLoadingTK.setManaged(true); lblThongBaoCapNhatTK.setText("");
        Task<Optional<ThongTinTaiKhoanCauHinh>> task = new Task<>() {
            @Override protected Optional<ThongTinTaiKhoanCauHinh> call() { return service.traCuuTaiKhoan(keyword); }
        };
        task.setOnSucceeded(e -> {
            boxLoadingTK.setVisible(false); boxLoadingTK.setManaged(false);
            Optional<ThongTinTaiKhoanCauHinh> result = task.getValue();
            if (result.isEmpty()) { taiKhoanDangTraCuu = null; cardDetailTK.setVisible(false); cardDetailTK.setManaged(false); hienCanhBao("Tra cứu tài khoản", "Không tìm thấy tài khoản phù hợp."); return; }
            taiKhoanDangTraCuu = result.get(); hienThiTaiKhoan(taiKhoanDangTraCuu);
        });
        task.setOnFailed(e -> { boxLoadingTK.setVisible(false); boxLoadingTK.setManaged(false); hienCanhBao("Lỗi tra cứu tài khoản", task.getException().getMessage()); });
        Thread thread = new Thread(task, "admin-system-config-account-lookup"); thread.setDaemon(true); thread.start();
    }
    private void hienThiTaiKhoan(ThongTinTaiKhoanCauHinh info) {
        lblDetailSoTK.setText(info.getSoTaiKhoan()); lblDetailTenKH.setText(info.getTenKhachHang());
        lblDetailSoDu.setText(dfTien.format(info.getSoDu())); lblDetailChiNhanh.setText(info.getTenChiNhanh());
        lblBadgeTrangThaiTK.setText(info.getTrangThai());
        lblBadgeTrangThaiTK.setStyle("Hoạt động".equals(info.getTrangThai())
                ? "-fx-background-color: #E8F8F0; -fx-text-fill: #10B981; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 10; -fx-padding: 5 12;"
                : "-fx-background-color: #FDECEC; -fx-text-fill: #D9534F; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 10; -fx-padding: 5 12;");
        lblDetailGoiHanMucHienTai.setText(info.getTenHanMuc() == null ? "Chưa gán hạn mức" : info.getTenHanMuc() + " (" + dfTien.format(info.getHanMucNgay()) + "/ngày)");
        cboChonGoiHanMucMoi.getSelectionModel().clearSelection(); cardDetailTK.setVisible(true); cardDetailTK.setManaged(true);
    }
    @FXML public void onMoXacNhanCapNhatHanMuc() {
        if (taiKhoanDangTraCuu == null) { hienCanhBao("Cập nhật hạn mức", "Hãy tra cứu tài khoản trước."); return; }
        HanMucGiaoDich selected = cboChonGoiHanMucMoi.getValue();
        if (selected == null) { hienCanhBao("Cập nhật hạn mức", "Vui lòng chọn một gói hạn mức mới."); return; }
        lblNoiDungXacNhanDoiHanMuc.setText("Xác nhận đổi hạn mức tài khoản [" + taiKhoanDangTraCuu.getSoTaiKhoan() + "] sang [" + selected.getTenHanMuc() + "]?"); hienModal(modalXacNhanDoiHanMuc);
    }
    @FXML public void onDongPopupXacNhanDoiHanMuc() { anModal(modalXacNhanDoiHanMuc); }
    @FXML public void onDongYDoiHanMuc() {
        if (taiKhoanDangTraCuu == null || cboChonGoiHanMucMoi.getValue() == null) return;
        HanMucGiaoDich selected = cboChonGoiHanMucMoi.getValue();
        try {
            service.capNhatHanMucTaiKhoan(taiKhoanDangTraCuu.getSoTaiKhoan(), selected.getMaHanMuc());
            taiKhoanDangTraCuu.setMaHanMuc(selected.getMaHanMuc()); taiKhoanDangTraCuu.setTenHanMuc(selected.getTenHanMuc()); taiKhoanDangTraCuu.setHanMucNgay(selected.getHanMucNgay());
            hienThiTaiKhoan(taiKhoanDangTraCuu); lblThongBaoCapNhatTK.setText("Đã cập nhật hạn mức thành công."); anModal(modalXacNhanDoiHanMuc);
        } catch (RuntimeException e) { hienCanhBao("Không thể cập nhật hạn mức", e.getMessage()); }
    }
    @FXML public void onMoXacNhanDoiTrangThaiTK() {
        if (taiKhoanDangTraCuu == null) { hienCanhBao("Đổi trạng thái", "Hãy tra cứu tài khoản trước."); return; }
        String next = "Hoạt động".equals(taiKhoanDangTraCuu.getTrangThai()) ? "Bị khóa" : "Hoạt động";
        lblNoiDungDoiTrangThaiTK.setText("Xác nhận chuyển tài khoản [" + taiKhoanDangTraCuu.getSoTaiKhoan() + "] sang trạng thái '" + next + "'?"); hienModal(modalDoiTrangThaiTK);
    }
    @FXML public void onDongPopupDoiTrangThaiTK() { anModal(modalDoiTrangThaiTK); }
    @FXML public void onDongYDoiTrangThaiTK() {
        if (taiKhoanDangTraCuu == null) return;
        try { taiKhoanDangTraCuu.setTrangThai(service.doiTrangThaiTaiKhoan(taiKhoanDangTraCuu.getSoTaiKhoan())); hienThiTaiKhoan(taiKhoanDangTraCuu); lblThongBaoCapNhatTK.setText("Đã cập nhật trạng thái tài khoản."); anModal(modalDoiTrangThaiTK); }
        catch (RuntimeException e) { hienCanhBao("Không thể đổi trạng thái tài khoản", e.getMessage()); }
    }

    private String safe(String value) { return value == null ? "" : value; }
    private boolean xacNhan(String title, String message) { Alert a = new Alert(Alert.AlertType.CONFIRMATION); a.setTitle(title); a.setHeaderText(null); a.setContentText(message); return a.showAndWait().filter(ButtonType.OK::equals).isPresent(); }
    private void hienModal(StackPane modal) { modal.setVisible(true); modal.setManaged(true); }
    private void anModal(StackPane modal) { modal.setVisible(false); modal.setManaged(false); }
    private void hienThongBaoChuaChonDong() { hienCanhBao("Thông báo", "Vui lòng chọn một dòng trong bảng."); }
    private void hienCanhBao(String title, String message) { Alert a = new Alert(Alert.AlertType.WARNING); a.setTitle(title); a.setHeaderText(null); a.setContentText(message); a.showAndWait(); }
    private void hienThongTin(String title, String message) { Alert a = new Alert(Alert.AlertType.INFORMATION); a.setTitle(title); a.setHeaderText(null); a.setContentText(message); a.showAndWait(); }

    @FXML private void onTrangChu(ActionEvent event) { moView("admin-dashboard-view.fxml", event); }
    @FXML private void onTaiKhoan(ActionEvent event) { moView("admin-account-view.fxml", event); }
    @FXML private void onNhanVienChiNhanh(ActionEvent event) { moView("admin-employee-branch-view.fxml", event); }
    @FXML private void onCauHinhDanhMuc(ActionEvent event) { }
    @FXML private void onThongKeBaoCao(ActionEvent event) { moView("admin-report-view.fxml", event); }
    @FXML private void onDangXuat(ActionEvent event) { moView("login-view.fxml", event); }
}
