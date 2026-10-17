package org.example.appnganhang.controller.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import org.example.appnganhang.model.entity.ChiNhanh;
import org.example.appnganhang.model.entity.NhanVien;
import org.example.appnganhang.model.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import static org.example.appnganhang.controller.util.Navigation.moView;

public class AdminEmployeeBranchController {

    @FXML private TabPane tabPaneMain;

    // TAB 1: NHÂN VIÊN
    @FXML private TextField txtTimKiemNV;
    @FXML private ComboBox<String> cboLocChiNhanhNV;
    @FXML private Button btnTimKiemNV;
    @FXML private Button btnLamMoiNV;
    @FXML private Button btnThemNV;
    @FXML private Label lblTongNV;

    @FXML private TableView<NhanVien> tblNhanVien;
    @FXML private TableColumn<NhanVien, String> colMaNV;
    @FXML private TableColumn<NhanVien, String> colHoTenNV;
    @FXML private TableColumn<NhanVien, String> colSoDienThoaiNV;
    @FXML private TableColumn<NhanVien, String> colChucVuNV;
    @FXML private TableColumn<NhanVien, String> colChiNhanhNV;
    @FXML private TableColumn<NhanVien, String> colTaiKhoanLienKet;
    @FXML private TableColumn<NhanVien, Void> colThaoTacNV;

    // Modal 1: Thêm NV
    @FXML private StackPane modalThemNV;
    @FXML private TextField txtModalThemMaNV;
    @FXML private TextField txtModalThemHoTenNV;
    @FXML private TextField txtModalThemSDTNV;
    @FXML private TextField txtModalThemDiaChiNV;
    @FXML private TextField txtModalThemChucVuNV;
    @FXML private ComboBox<String> cboModalThemChiNhanhNV;
    @FXML private Label lblModalThemNVLoi;

    // Modal 2: Liên kết tài khoản
    @FXML private StackPane modalLienKetTK;
    @FXML private Label lblModalLienKetMaNV;
    @FXML private Label lblModalLienKetHoTen;
    @FXML private ComboBox<String> cboModalChonTaiKhoan;
    @FXML private Label lblModalLienKetLoi;

    // Modal 3: Sửa NV
    @FXML private StackPane modalSuaNV;
    @FXML private Label lblModalSuaMaNV;
    @FXML private TextField txtModalSuaHoTenNV;
    @FXML private TextField txtModalSuaSDTNV;
    @FXML private TextField txtModalSuaDiaChiNV;
    @FXML private TextField txtModalSuaChucVuNV;
    @FXML private ComboBox<String> cboModalSuaChiNhanhNV;

    // Modal 4: Xóa NV
    @FXML private StackPane modalXoaNV;
    @FXML private Label lblModalXoaTenNV;

    // TAB 2: CHI NHÁNH
    @FXML private TextField txtTimKiemCN;
    @FXML private Label lblTongCN;

    @FXML private TableView<ChiNhanh> tblChiNhanh;
    @FXML private TableColumn<ChiNhanh, String> colMaCN;
    @FXML private TableColumn<ChiNhanh, String> colTenCN;
    @FXML private TableColumn<ChiNhanh, String> colDiaChiCN;
    @FXML private TableColumn<ChiNhanh, Void> colThaoTacCN;

    // Modal 5: Thêm/Sửa Chi nhánh
    @FXML private StackPane modalThemSuaCN;
    @FXML private Label lblModalCNTitle;
    @FXML private TextField txtModalMaCN;
    @FXML private TextField txtModalTenCN;
    @FXML private TextField txtModalDiaChiCN;

    // Modal 6: Xóa Chi nhánh
    @FXML private StackPane modalXoaCN;
    @FXML private Label lblModalXoaTenCN;

    // Collections
    private final ObservableList<NhanVien> danhSachNhanVien = FXCollections.observableArrayList();
    private final ObservableList<NhanVien> danhSachHienThiNV = FXCollections.observableArrayList();

    private final ObservableList<ChiNhanh> danhSachChiNhanh = FXCollections.observableArrayList();
    private final ObservableList<ChiNhanh> danhSachHienThiCN = FXCollections.observableArrayList();

    private NhanVien nhanVienDangChon;
    private ChiNhanh chiNhanhDangChon;
    private boolean isSuaChiNhanh = false;

    @FXML
    public void initialize() {
        cauHinhBangNhanVien();
        cauHinhBangChiNhanh();

        taiDuLieuChiNhanh();
        taiDuLieuNhanVien();
    }

    // =========================================================
    // CẤU HÌNH BẢNG NHÂN VIÊN
    // =========================================================
    private void cauHinhBangNhanVien() {
        colMaNV.setCellValueFactory(new PropertyValueFactory<>("maNhanVien"));
        colHoTenNV.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        colSoDienThoaiNV.setCellValueFactory(new PropertyValueFactory<>("soDienThoai"));
        colChucVuNV.setCellValueFactory(new PropertyValueFactory<>("chucVu"));
        colChiNhanhNV.setCellValueFactory(new PropertyValueFactory<>("tenChiNhanh"));

        // Cột tài khoản liên kết: Badge xám nếu chưa gán, badge xanh nếu đã gán
        colTaiKhoanLienKet.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    NhanVien nv = getTableRow().getItem();
                    Label badge = new Label();
                    if (nv.getTenDangNhap() == null || nv.getTenDangNhap().trim().isEmpty()) {
                        badge.setText("Chưa gán account");
                        badge.setStyle("-fx-background-color: #F1F2F6; -fx-text-fill: #85879A; -fx-padding: 4 10; -fx-background-radius: 8; -fx-font-size: 11px;");
                    } else {
                        badge.setText("🔗 " + nv.getTenDangNhap());
                        badge.setStyle("-fx-background-color: #ECEBFA; -fx-text-fill: #5148C9; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 8; -fx-font-size: 11px;");
                    }
                    HBox box = new HBox(badge);
                    box.setAlignment(Pos.CENTER);
                    setGraphic(box);
                }
            }
        });

        // Cột Thao tác: Cấp/Liên kết tài khoản, Chỉnh sửa, Xóa
        colThaoTacNV.setCellFactory(param -> new TableCell<>() {
            private final Button btnLienKet = new Button("Liên kết TK");
            private final Button btnSua = new Button("Sửa");
            private final Button btnXoa = new Button("Xóa");
            private final HBox box = new HBox(6, btnLienKet, btnSua, btnXoa);

            {
                box.setAlignment(Pos.CENTER);
                btnLienKet.setStyle("-fx-background-color: #ECEBFA; -fx-text-fill: #5148C9; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 11px; -fx-padding: 4 8;");
                btnSua.setStyle("-fx-background-color: #F1F2F6; -fx-text-fill: #171A35; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 11px; -fx-padding: 4 8;");
                btnXoa.setStyle("-fx-background-color: #FDECEC; -fx-text-fill: #D9534F; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 11px; -fx-padding: 4 8;");

                btnLienKet.setOnAction(e -> {
                    NhanVien item = getTableView().getItems().get(getIndex());
                    moPopupLienKetTKCho(item);
                });

                btnSua.setOnAction(e -> {
                    NhanVien item = getTableView().getItems().get(getIndex());
                    moPopupSuaNVCho(item);
                });

                btnXoa.setOnAction(e -> {
                    NhanVien item = getTableView().getItems().get(getIndex());
                    moPopupXoaNVCho(item);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else setGraphic(box);
            }
        });

        tblNhanVien.setItems(danhSachHienThiNV);
    }

    // =========================================================
    // CẤU HÌNH BẢNG CHI NHÁNH
    // =========================================================
    private void cauHinhBangChiNhanh() {
        colMaCN.setCellValueFactory(new PropertyValueFactory<>("maChiNhanh"));
        colTenCN.setCellValueFactory(new PropertyValueFactory<>("tenChiNhanh"));
        colDiaChiCN.setCellValueFactory(new PropertyValueFactory<>("diaChi"));

        colThaoTacCN.setCellFactory(param -> new TableCell<>() {
            private final Button btnSua = new Button("Sửa");
            private final Button btnXoa = new Button("Xóa");
            private final HBox box = new HBox(8, btnSua, btnXoa);

            {
                box.setAlignment(Pos.CENTER);
                btnSua.setStyle("-fx-background-color: #ECEBFA; -fx-text-fill: #5148C9; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 11px; -fx-padding: 5 12;");
                btnXoa.setStyle("-fx-background-color: #FDECEC; -fx-text-fill: #D9534F; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 11px; -fx-padding: 5 12;");

                btnSua.setOnAction(e -> {
                    ChiNhanh item = getTableView().getItems().get(getIndex());
                    moPopupSuaCNCho(item);
                });

                btnXoa.setOnAction(e -> {
                    ChiNhanh item = getTableView().getItems().get(getIndex());
                    moPopupXoaCNCho(item);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else setGraphic(box);
            }
        });

        tblChiNhanh.setItems(danhSachHienThiCN);
    }

    // =========================================================
    // NẠP DỮ LIỆU TỪ DB
    // =========================================================
    private void taiDuLieuChiNhanh() {
        danhSachChiNhanh.clear();
        String sql = "SELECT MaChiNhanh, TenChiNhanh, DiaChi FROM ChiNhanh";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ChiNhanh cn = new ChiNhanh(
                        rs.getString("MaChiNhanh"),
                        rs.getString("TenChiNhanh"),
                        rs.getString("DiaChi")
                );
                danhSachChiNhanh.add(cn);
            }
        } catch (Exception e) {
            System.err.println("Lỗi nạp ChiNhanh: " + e.getMessage());
            danhSachChiNhanh.addAll(
                    new ChiNhanh("CN01", "Chi nhánh Hà Nội", "123 Hoàng Mai, Hà Nội"),
                    new ChiNhanh("CN02", "Chi nhánh TP.HCM", "456 Nguyễn Huệ, Quận 1, TP.HCM"),
                    new ChiNhanh("CN03", "Chi nhánh Đà Nẵng", "789 Nguyễn Văn Linh, Đà Nẵng")
            );
        }

        danhSachHienThiCN.setAll(danhSachChiNhanh);
        if (lblTongCN != null) lblTongCN.setText("Tổng số: " + danhSachHienThiCN.size() + " chi nhánh");

        // Cập nhật combobox chi nhánh
        ObservableList<String> listTenCN = FXCollections.observableArrayList();
        listTenCN.add("Tất cả chi nhánh");
        for (ChiNhanh cn : danhSachChiNhanh) {
            listTenCN.add(cn.getTenChiNhanh());
        }
        if (cboLocChiNhanhNV != null) {
            cboLocChiNhanhNV.setItems(listTenCN);
            cboLocChiNhanhNV.setValue("Tất cả chi nhánh");
        }
        if (cboModalThemChiNhanhNV != null) {
            ObservableList<String> tenCNChon = FXCollections.observableArrayList();
            for (ChiNhanh cn : danhSachChiNhanh) tenCNChon.add(cn.getTenChiNhanh());
            cboModalThemChiNhanhNV.setItems(tenCNChon);
        }
        if (cboModalSuaChiNhanhNV != null) {
            ObservableList<String> tenCNChon = FXCollections.observableArrayList();
            for (ChiNhanh cn : danhSachChiNhanh) tenCNChon.add(cn.getTenChiNhanh());
            cboModalSuaChiNhanhNV.setItems(tenCNChon);
        }
    }

    private void taiDuLieuNhanVien() {
        danhSachNhanVien.clear();
        String sql = """
                SELECT nv.MaNhanVien, nv.HoTen, nv.SoDienThoai, nv.DiaChi, nv.ChucVu, nv.MaChiNhanh, cn.TenChiNhanh, nv.TenDangNhap
                FROM NhanVien nv
                LEFT JOIN ChiNhanh cn ON nv.MaChiNhanh = cn.MaChiNhanh
                """;

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                NhanVien nv = new NhanVien();
                nv.setMaNhanVien(rs.getString("MaNhanVien"));
                nv.setHoTen(rs.getString("HoTen"));
                nv.setSoDienThoai(rs.getString("SoDienThoai"));
                nv.setDiaChi(rs.getString("DiaChi"));
                nv.setChucVu(rs.getString("ChucVu"));
                nv.setMaChiNhanh(rs.getString("MaChiNhanh"));
                nv.setTenChiNhanh(rs.getString("TenChiNhanh"));
                nv.setTenDangNhap(rs.getString("TenDangNhap"));
                danhSachNhanVien.add(nv);
            }
        } catch (Exception e) {
            System.err.println("Lỗi nạp NhanVien: " + e.getMessage());
            napNhanVienMau();
        }

        if (danhSachNhanVien.isEmpty()) {
            napNhanVienMau();
        }

        locNhanVien();
    }

    private void napNhanVienMau() {
        NhanVien nv1 = new NhanVien();
        nv1.setMaNhanVien("NV001");
        nv1.setHoTen("Nguyễn Văn A");
        nv1.setSoDienThoai("0901234567");
        nv1.setDiaChi("Hoàn Kiếm, Hà Nội");
        nv1.setChucVu("CSKH");
        nv1.setTenChiNhanh("Chi nhánh Hà Nội");
        nv1.setTenDangNhap("cskh_hoanglan");

        NhanVien nv2 = new NhanVien();
        nv2.setMaNhanVien("NV002");
        nv2.setHoTen("Trần Thị B");
        nv2.setSoDienThoai("0912345678");
        nv2.setDiaChi("Quận 1, TP.HCM");
        nv2.setChucVu("Giao dịch viên");
        nv2.setTenChiNhanh("Chi nhánh TP.HCM");
        nv2.setTenDangNhap(null); // Chưa gán

        danhSachNhanVien.addAll(nv1, nv2);
    }

    // =========================================================
    // TAB 1 ACTIONS: TÌM KIẾM & BỘ LỌC
    // =========================================================
    @FXML
    private void onTimKiemNV() {
        locNhanVien();
    }

    @FXML
    private void onLamMoiNV() {
        if (txtTimKiemNV != null) txtTimKiemNV.clear();
        if (cboLocChiNhanhNV != null) cboLocChiNhanhNV.setValue("Tất cả chi nhánh");
        taiDuLieuChiNhanh();
        taiDuLieuNhanVien();
    }

    private void locNhanVien() {
        String keyword = txtTimKiemNV != null && txtTimKiemNV.getText() != null
                ? txtTimKiemNV.getText().trim().toLowerCase() : "";
        String chiNhanh = cboLocChiNhanhNV != null ? cboLocChiNhanhNV.getValue() : "Tất cả chi nhánh";

        danhSachHienThiNV.clear();
        for (NhanVien nv : danhSachNhanVien) {
            boolean matchKeyword = keyword.isEmpty()
                    || (nv.getHoTen() != null && nv.getHoTen().toLowerCase().contains(keyword))
                    || (nv.getSoDienThoai() != null && nv.getSoDienThoai().toLowerCase().contains(keyword))
                    || (nv.getMaNhanVien() != null && nv.getMaNhanVien().toLowerCase().contains(keyword));

            boolean matchBranch = chiNhanh == null
                    || chiNhanh.equals("Tất cả chi nhánh")
                    || (nv.getTenChiNhanh() != null && nv.getTenChiNhanh().equalsIgnoreCase(chiNhanh));

            if (matchKeyword && matchBranch) {
                danhSachHienThiNV.add(nv);
            }
        }

        if (lblTongNV != null) lblTongNV.setText("Tổng số: " + danhSachHienThiNV.size() + " nhân viên");
    }

    // =========================================================
    // MODAL 1: THÊM NHÂN VIÊN MỚI
    // =========================================================
    @FXML
    public void onMoPopupThemNV() {
        if (txtModalThemMaNV != null) txtModalThemMaNV.clear();
        if (txtModalThemHoTenNV != null) txtModalThemHoTenNV.clear();
        if (txtModalThemSDTNV != null) txtModalThemSDTNV.clear();
        if (txtModalThemDiaChiNV != null) txtModalThemDiaChiNV.clear();
        if (txtModalThemChucVuNV != null) txtModalThemChucVuNV.clear();
        if (lblModalThemNVLoi != null) lblModalThemNVLoi.setText("");

        if (cboModalThemChiNhanhNV != null && !danhSachChiNhanh.isEmpty()) {
            cboModalThemChiNhanhNV.setValue(danhSachChiNhanh.get(0).getTenChiNhanh());
        }

        hienModal(modalThemNV);
    }

    @FXML
    public void onDongPopupThemNV() {
        anModal(modalThemNV);
    }

    @FXML
    public void onLuuThemNV() {
        String maNV = txtModalThemMaNV.getText() != null ? txtModalThemMaNV.getText().trim() : "";
        String hoTen = txtModalThemHoTenNV.getText() != null ? txtModalThemHoTenNV.getText().trim() : "";
        String sdt = txtModalThemSDTNV.getText() != null ? txtModalThemSDTNV.getText().trim() : "";
        String diaChi = txtModalThemDiaChiNV.getText() != null ? txtModalThemDiaChiNV.getText().trim() : "";
        String chucVu = txtModalThemChucVuNV.getText() != null ? txtModalThemChucVuNV.getText().trim() : "";
        String tenCN = cboModalThemChiNhanhNV.getValue();

        if (maNV.isEmpty() || hoTen.isEmpty() || sdt.isEmpty() || chucVu.isEmpty() || tenCN == null) {
            lblModalThemNVLoi.setText("Vui lòng nhập đầy đủ các trường thông tin bắt buộc!");
            return;
        }

        // Tìm mã chi nhánh tương ứng
        String maCN = "";
        for (ChiNhanh cn : danhSachChiNhanh) {
            if (cn.getTenChiNhanh().equalsIgnoreCase(tenCN)) {
                maCN = cn.getMaChiNhanh();
                break;
            }
        }

        NhanVien nvMoi = new NhanVien();
        nvMoi.setMaNhanVien(maNV);
        nvMoi.setHoTen(hoTen);
        nvMoi.setSoDienThoai(sdt);
        nvMoi.setDiaChi(diaChi);
        nvMoi.setChucVu(chucVu);
        nvMoi.setMaChiNhanh(maCN);
        nvMoi.setTenChiNhanh(tenCN);
        nvMoi.setTenDangNhap(null);

        // Lưu DB
        String sql = "INSERT INTO NhanVien (MaNhanVien, HoTen, SoDienThoai, DiaChi, ChucVu, MaChiNhanh, TenDangNhap) VALUES (?, ?, ?, ?, ?, ?, NULL)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNV);
            ps.setString(2, hoTen);
            ps.setString(3, sdt);
            ps.setString(4, diaChi);
            ps.setString(5, chucVu);
            ps.setString(6, maCN);
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Lưu NV vào DB lỗi: " + e.getMessage());
        }

        danhSachNhanVien.add(0, nvMoi);
        locNhanVien();
        anModal(modalThemNV);
    }

    // =========================================================
    // MODAL 2: LIÊN KẾT TÀI KHOẢN ĐĂNG NHẬP
    // =========================================================
    @FXML
    public void onMoPopupLienKetTK() {
        NhanVien chon = tblNhanVien.getSelectionModel().getSelectedItem();
        if (chon != null) {
            moPopupLienKetTKCho(chon);
        } else {
            hienThongBaoChuaChonDong();
        }
    }

    private void moPopupLienKetTKCho(NhanVien nv) {
        this.nhanVienDangChon = nv;
        lblModalLienKetMaNV.setText(nv.getMaNhanVien());
        lblModalLienKetHoTen.setText(nv.getHoTen());
        lblModalLienKetLoi.setText("");

        // Chỉ nạp các tài khoản CHƯA được gán cho nhân viên nào khác
        Set<String> cacTKDaGan = new HashSet<>();
        for (NhanVien n : danhSachNhanVien) {
            if (n.getTenDangNhap() != null && !n.getTenDangNhap().isEmpty()) {
                cacTKDaGan.add(n.getTenDangNhap().toLowerCase());
            }
        }

        ObservableList<String> listTKChuaGan = FXCollections.observableArrayList();
        String sql = "SELECT TenDangNhap FROM TaiKhoanDangNhap_VaiTro WHERE RoleName <> N'Khách hàng'";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String tk = rs.getString("TenDangNhap");
                if (!cacTKDaGan.contains(tk.toLowerCase()) || (nv.getTenDangNhap() != null && nv.getTenDangNhap().equalsIgnoreCase(tk))) {
                    listTKChuaGan.add(tk);
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi nạp danh sách tài khoản chưa gán: " + e.getMessage());
            listTKChuaGan.addAll("nv_nguyenvana", "nv_tranthib", "gdv_lehoang", "cskh_phamminh");
        }

        cboModalChonTaiKhoan.setItems(listTKChuaGan);
        if (nv.getTenDangNhap() != null && !nv.getTenDangNhap().isEmpty()) {
            cboModalChonTaiKhoan.setValue(nv.getTenDangNhap());
        } else if (!listTKChuaGan.isEmpty()) {
            cboModalChonTaiKhoan.setValue(listTKChuaGan.get(0));
        }

        hienModal(modalLienKetTK);
    }

    @FXML
    public void onDongPopupLienKetTK() {
        anModal(modalLienKetTK);
    }

    @FXML
    public void onXacNhanLienKetTK() {
        if (nhanVienDangChon == null) return;
        String tkChon = cboModalChonTaiKhoan.getValue();
        if (tkChon == null || tkChon.isEmpty()) {
            lblModalLienKetLoi.setText("Vui lòng chọn một tài khoản!");
            return;
        }

        nhanVienDangChon.setTenDangNhap(tkChon);

        String sql = "UPDATE NhanVien SET TenDangNhap = ? WHERE MaNhanVien = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tkChon);
            ps.setString(2, nhanVienDangChon.getMaNhanVien());
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Cập nhật liên kết DB lỗi: " + e.getMessage());
        }

        tblNhanVien.refresh();
        anModal(modalLienKetTK);
    }

    // =========================================================
    // MODAL 3: CHỈNH SỬA NHÂN VIÊN
    // =========================================================
    @FXML
    public void onMoPopupSuaNV() {
        NhanVien chon = tblNhanVien.getSelectionModel().getSelectedItem();
        if (chon != null) {
            moPopupSuaNVCho(chon);
        } else {
            hienThongBaoChuaChonDong();
        }
    }

    private void moPopupSuaNVCho(NhanVien nv) {
        this.nhanVienDangChon = nv;
        lblModalSuaMaNV.setText("Mã NV: " + nv.getMaNhanVien());
        txtModalSuaHoTenNV.setText(nv.getHoTen());
        txtModalSuaSDTNV.setText(nv.getSoDienThoai());
        txtModalSuaDiaChiNV.setText(nv.getDiaChi());
        txtModalSuaChucVuNV.setText(nv.getChucVu());
        cboModalSuaChiNhanhNV.setValue(nv.getTenChiNhanh());
        hienModal(modalSuaNV);
    }

    @FXML
    public void onDongPopupSuaNV() {
        anModal(modalSuaNV);
    }

    @FXML
    public void onCapNhatNV() {
        if (nhanVienDangChon == null) return;

        String hoTen = txtModalSuaHoTenNV.getText().trim();
        String sdt = txtModalSuaSDTNV.getText().trim();
        String diaChi = txtModalSuaDiaChiNV.getText().trim();
        String chucVu = txtModalSuaChucVuNV.getText().trim();
        String tenCN = cboModalSuaChiNhanhNV.getValue();

        nhanVienDangChon.setHoTen(hoTen);
        nhanVienDangChon.setSoDienThoai(sdt);
        nhanVienDangChon.setDiaChi(diaChi);
        nhanVienDangChon.setChucVu(chucVu);
        nhanVienDangChon.setTenChiNhanh(tenCN);

        String maCN = "";
        for (ChiNhanh cn : danhSachChiNhanh) {
            if (cn.getTenChiNhanh().equalsIgnoreCase(tenCN)) {
                maCN = cn.getMaChiNhanh();
                break;
            }
        }

        String sql = "UPDATE NhanVien SET HoTen = ?, SoDienThoai = ?, DiaChi = ?, ChucVu = ?, MaChiNhanh = ? WHERE MaNhanVien = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hoTen);
            ps.setString(2, sdt);
            ps.setString(3, diaChi);
            ps.setString(4, chucVu);
            ps.setString(5, maCN);
            ps.setString(6, nhanVienDangChon.getMaNhanVien());
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Cập nhật NV vào DB lỗi: " + e.getMessage());
        }

        tblNhanVien.refresh();
        locNhanVien();
        anModal(modalSuaNV);
    }

    // =========================================================
    // MODAL 4: XÓA NHÂN VIÊN
    // =========================================================
    @FXML
    public void onMoPopupXoaNV() {
        NhanVien chon = tblNhanVien.getSelectionModel().getSelectedItem();
        if (chon != null) {
            moPopupXoaNVCho(chon);
        } else {
            hienThongBaoChuaChonDong();
        }
    }

    private void moPopupXoaNVCho(NhanVien nv) {
        this.nhanVienDangChon = nv;
        lblModalXoaTenNV.setText("Nhân viên: [" + nv.getMaNhanVien() + "] " + nv.getHoTen());
        hienModal(modalXoaNV);
    }

    @FXML
    public void onDongPopupXoaNV() {
        anModal(modalXoaNV);
    }

    @FXML
    public void onDongYXoaNV() {
        if (nhanVienDangChon == null) return;

        String sql = "DELETE FROM NhanVien WHERE MaNhanVien = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nhanVienDangChon.getMaNhanVien());
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Xóa NV khỏi DB lỗi: " + e.getMessage());
        }

        danhSachNhanVien.remove(nhanVienDangChon);
        locNhanVien();
        anModal(modalXoaNV);
    }

    // =========================================================
    // TAB 2 ACTIONS: CHI NHÁNH
    // =========================================================
    @FXML
    private void onTimKiemCN() {
        String keyword = txtTimKiemCN != null && txtTimKiemCN.getText() != null
                ? txtTimKiemCN.getText().trim().toLowerCase() : "";

        danhSachHienThiCN.clear();
        for (ChiNhanh cn : danhSachChiNhanh) {
            if (keyword.isEmpty()
                    || (cn.getMaChiNhanh() != null && cn.getMaChiNhanh().toLowerCase().contains(keyword))
                    || (cn.getTenChiNhanh() != null && cn.getTenChiNhanh().toLowerCase().contains(keyword))) {
                danhSachHienThiCN.add(cn);
            }
        }
        if (lblTongCN != null) lblTongCN.setText("Tổng số: " + danhSachHienThiCN.size() + " chi nhánh");
    }

    @FXML
    private void onLamMoiCN() {
        if (txtTimKiemCN != null) txtTimKiemCN.clear();
        taiDuLieuChiNhanh();
    }

    @FXML
    public void onMoPopupThemCN() {
        isSuaChiNhanh = false;
        lblModalCNTitle.setText("Thêm chi nhánh mới");
        txtModalMaCN.setDisable(false);
        txtModalMaCN.clear();
        txtModalTenCN.clear();
        txtModalDiaChiCN.clear();
        hienModal(modalThemSuaCN);
    }

    @FXML
    public void onMoPopupSuaCN() {
        ChiNhanh chon = tblChiNhanh.getSelectionModel().getSelectedItem();
        if (chon != null) {
            moPopupSuaCNCho(chon);
        } else {
            hienThongBaoChuaChonDong();
        }
    }

    private void moPopupSuaCNCho(ChiNhanh cn) {
        this.chiNhanhDangChon = cn;
        isSuaChiNhanh = true;
        lblModalCNTitle.setText("Chỉnh sửa chi nhánh");
        txtModalMaCN.setText(cn.getMaChiNhanh());
        txtModalMaCN.setDisable(true);
        txtModalTenCN.setText(cn.getTenChiNhanh());
        txtModalDiaChiCN.setText(cn.getDiaChi());
        hienModal(modalThemSuaCN);
    }

    @FXML
    public void onDongPopupThemSuaCN() {
        anModal(modalThemSuaCN);
    }

    @FXML
    public void onLuuThemSuaCN() {
        String maCN = txtModalMaCN.getText().trim();
        String tenCN = txtModalTenCN.getText().trim();
        String diaChi = txtModalDiaChiCN.getText().trim();

        if (maCN.isEmpty() || tenCN.isEmpty() || diaChi.isEmpty()) {
            return;
        }

        if (isSuaChiNhanh && chiNhanhDangChon != null) {
            chiNhanhDangChon.setTenChiNhanh(tenCN);
            chiNhanhDangChon.setDiaChi(diaChi);

            String sql = "UPDATE ChiNhanh SET TenChiNhanh = ?, DiaChi = ? WHERE MaChiNhanh = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, tenCN);
                ps.setString(2, diaChi);
                ps.setString(3, maCN);
                ps.executeUpdate();
            } catch (Exception e) {
                System.err.println("Cập nhật CN vào DB lỗi: " + e.getMessage());
            }

        } else {
            ChiNhanh cnMoi = new ChiNhanh(maCN, tenCN, diaChi);
            String sql = "INSERT INTO ChiNhanh (MaChiNhanh, TenChiNhanh, DiaChi) VALUES (?, ?, ?)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, maCN);
                ps.setString(2, tenCN);
                ps.setString(3, diaChi);
                ps.executeUpdate();
            } catch (Exception e) {
                System.err.println("Thêm CN vào DB lỗi: " + e.getMessage());
            }
            danhSachChiNhanh.add(0, cnMoi);
        }

        tblChiNhanh.refresh();
        onTimKiemCN();
        anModal(modalThemSuaCN);
    }

    @FXML
    public void onMoPopupXoaCN() {
        ChiNhanh chon = tblChiNhanh.getSelectionModel().getSelectedItem();
        if (chon != null) {
            moPopupXoaCNCho(chon);
        } else {
            hienThongBaoChuaChonDong();
        }
    }

    private void moPopupXoaCNCho(ChiNhanh cn) {
        this.chiNhanhDangChon = cn;
        lblModalXoaTenCN.setText("Chi nhánh: [" + cn.getMaChiNhanh() + "] " + cn.getTenChiNhanh());
        hienModal(modalXoaCN);
    }

    @FXML
    public void onDongPopupXoaCN() {
        anModal(modalXoaCN);
    }

    @FXML
    public void onDongYXoaCN() {
        if (chiNhanhDangChon == null) return;

        String sql = "DELETE FROM ChiNhanh WHERE MaChiNhanh = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, chiNhanhDangChon.getMaChiNhanh());
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Xóa CN DB lỗi: " + e.getMessage());
        }

        danhSachChiNhanh.remove(chiNhanhDangChon);
        onTimKiemCN();
        anModal(modalXoaCN);
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
        alert.setContentText("Vui lòng chọn một dòng trong bảng để thực hiện!");
        alert.showAndWait();
    }

    // =========================================================
    // SIDEBAR NAVIGATION
    // =========================================================
    @FXML
    private void onTaiKhoan(ActionEvent event) {
        moView("admin-account-view.fxml", event);
    }

    @FXML
    private void onNhanVienChiNhanh(ActionEvent event) {
        // Đang ở view này
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
