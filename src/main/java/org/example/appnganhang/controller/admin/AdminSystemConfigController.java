package org.example.appnganhang.controller.admin;

import javafx.application.Platform;
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
import org.example.appnganhang.model.entity.HanMucGiaoDich;
import org.example.appnganhang.model.entity.LoaiGiaoDich;
import org.example.appnganhang.model.util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.Optional;

import static org.example.appnganhang.controller.util.Navigation.moView;

public class AdminSystemConfigController {

    @FXML private TabPane tabPaneConfig;

    // ==========================================
    // PHÂN ĐOẠN 1: GÓI HẠN MỨC GIAO DỊCH
    // ==========================================
    @FXML private TextField txtTimKiemHanMuc;
    @FXML private Button btnTaoGoiHanMuc;
    @FXML private Label lblTongGoiHanMuc;

    @FXML private TableView<HanMucGiaoDich> tblHanMuc;
    @FXML private TableColumn<HanMucGiaoDich, String> colMaHanMuc;
    @FXML private TableColumn<HanMucGiaoDich, String> colTenHanMuc;
    @FXML private TableColumn<HanMucGiaoDich, String> colHanMucNgay;
    @FXML private TableColumn<HanMucGiaoDich, Void> colThaoTacHanMuc;

    // Modal Phân đoạn 1
    @FXML private StackPane modalThemSuaHanMuc;
    @FXML private Label lblModalHanMucTitle;
    @FXML private TextField txtModalMaHanMuc;
    @FXML private TextField txtModalTenHanMuc;
    @FXML private TextField txtModalHanMucNgay;
    @FXML private Label lblModalHanMucLoi;

    // ==========================================
    // PHÂN ĐOẠN 2: GÁN HẠN MỨC CHO TÀI KHOẢN
    // ==========================================
    @FXML private TextField txtTraCuuTK;
    @FXML private Button btnTraCuuTK;
    @FXML private HBox boxLoadingTK;

    @FXML private VBox cardDetailTK;
    @FXML private Label lblBadgeTrangThaiTK;
    @FXML private Label lblDetailSoTK;
    @FXML private Label lblDetailTenKH;
    @FXML private Label lblDetailSoDu;
    @FXML private Label lblDetailChiNhanh;
    @FXML private Label lblDetailGoiHanMucHienTai;
    @FXML private ComboBox<String> cboChonGoiHanMucMoi;
    @FXML private Button btnCapNhatHanMuc;
    @FXML private Button btnDoiTrangThaiTK;
    @FXML private Label lblThongBaoCapNhatTK;

    // Modal Phân đoạn 2
    @FXML private StackPane modalXacNhanDoiHanMuc;
    @FXML private Label lblNoiDungXacNhanDoiHanMuc;
    @FXML private StackPane modalDoiTrangThaiTK;
    @FXML private Label lblNoiDungDoiTrangThaiTK;

    // ==========================================
    // PHÂN ĐOẠN 3: LOẠI GIAO DỊCH & NGÂN HÀNG
    // ==========================================
    @FXML private TableView<LoaiGiaoDich> tblLoaiGD;
    @FXML private TableColumn<LoaiGiaoDich, String> colMaLoaiGD;
    @FXML private TableColumn<LoaiGiaoDich, String> colTenLoaiGD;
    @FXML private TableColumn<LoaiGiaoDich, Void> colThaoTacLoaiGD;

    @FXML private StackPane modalThemSuaLoaiGD;
    @FXML private Label lblModalLoaiGDTitle;
    @FXML private TextField txtModalMaLoaiGD;
    @FXML private TextField txtModalTenLoaiGD;

    // Bảng ngân hàng
    public static class NganHangItem {
        private String maNganHang;
        private String tenNganHang;
        private String trangThai;

        public NganHangItem(String ma, String ten, String tt) {
            this.maNganHang = ma;
            this.tenNganHang = ten;
            this.trangThai = tt;
        }

        public String getMaNganHang() { return maNganHang; }
        public void setMaNganHang(String maNganHang) { this.maNganHang = maNganHang; }
        public String getTenNganHang() { return tenNganHang; }
        public void setTenNganHang(String tenNganHang) { this.tenNganHang = tenNganHang; }
        public String getTrangThai() { return trangThai; }
        public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    }

    @FXML private TableView<NganHangItem> tblNganHang;
    @FXML private TableColumn<NganHangItem, String> colMaNganHang;
    @FXML private TableColumn<NganHangItem, String> colTenNganHang;
    @FXML private TableColumn<NganHangItem, String> colTrangThaiNganHang;
    @FXML private TableColumn<NganHangItem, Void> colThaoTacNganHang;
    @FXML private Button btnKichHoatTamNgung;

    // Collections
    private final ObservableList<HanMucGiaoDich> danhSachHanMuc = FXCollections.observableArrayList();
    private final ObservableList<HanMucGiaoDich> danhSachHienThiHanMuc = FXCollections.observableArrayList();

    private final ObservableList<LoaiGiaoDich> danhSachLoaiGD = FXCollections.observableArrayList();
    private final ObservableList<NganHangItem> danhSachNganHang = FXCollections.observableArrayList();

    private final DecimalFormat dfTien = new DecimalFormat("#,### VNĐ");
    private boolean isSuaHanMuc = false;
    private HanMucGiaoDich hanMucDangChon;
    private LoaiGiaoDich loaiGDDangChon;
    private boolean isSuaLoaiGD = false;

    // Trạng thái tài khoản hiện tra cứu
    private String traCuuSTK = "1000000001";
    private String traCuuGoiMoiDuocChon = "";
    private String trangThaiHienTaiTK = "Hoạt động";

    @FXML
    public void initialize() {
        cauHinhPhanDoan1();
        cauHinhPhanDoan2();
        cauHinhPhanDoan3();

        taiDuLieuHanMuc();
        taiDuLieuLoaiGD();
        taiDuLieuNganHang();
    }

    // =========================================================
    // CẤU HÌNH & XỬ LÝ PHÂN ĐOẠN 1: GÓI HẠN MỨC
    // =========================================================
    private void cauHinhPhanDoan1() {
        colMaHanMuc.setCellValueFactory(new PropertyValueFactory<>("maHanMuc"));
        colTenHanMuc.setCellValueFactory(new PropertyValueFactory<>("tenHanMuc"));

        colHanMucNgay.setCellValueFactory(cellData -> {
            BigDecimal hm = cellData.getValue().getHanMucNgay();
            String formatted = hm != null ? dfTien.format(hm) : "0 VNĐ";
            return new javafx.beans.property.SimpleStringProperty(formatted);
        });

        colThaoTacHanMuc.setCellFactory(param -> new TableCell<>() {
            private final Button btnSua = new Button("Sửa");
            private final Button btnXoa = new Button("Xóa");
            private final HBox box = new HBox(8, btnSua, btnXoa);

            {
                box.setAlignment(Pos.CENTER);
                btnSua.setStyle("-fx-background-color: #ECEBFA; -fx-text-fill: #5148C9; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 11px; -fx-padding: 5 12;");
                btnXoa.setStyle("-fx-background-color: #FDECEC; -fx-text-fill: #D9534F; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 11px; -fx-padding: 5 12;");

                btnSua.setOnAction(e -> {
                    HanMucGiaoDich item = getTableView().getItems().get(getIndex());
                    moPopupSuaHanMucCho(item);
                });

                btnXoa.setOnAction(e -> {
                    HanMucGiaoDich item = getTableView().getItems().get(getIndex());
                    xoaHanMuc(item);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else setGraphic(box);
            }
        });

        tblHanMuc.setItems(danhSachHienThiHanMuc);
    }

    private void taiDuLieuHanMuc() {
        danhSachHanMuc.clear();
        String sql = "SELECT MaHanMuc, TenHanMuc, HanMucNgay FROM dbo.[HanMucGiaoDịch]";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                HanMucGiaoDich hm = new HanMucGiaoDich(
                        rs.getString("MaHanMuc"),
                        rs.getString("TenHanMuc"),
                        rs.getBigDecimal("HanMucNgay")
                );
                danhSachHanMuc.add(hm);
            }
        } catch (Exception e) {
            System.err.println("Lỗi nạp HanMuc: " + e.getMessage());
            danhSachHanMuc.addAll(
                    new HanMucGiaoDich("HM01", "Gói Cơ Bản", new BigDecimal("50000000")),
                    new HanMucGiaoDich("HM02", "Gói Tiêu Chuẩn", new BigDecimal("100000000")),
                    new HanMucGiaoDich("HM03", "Gói Nâng Cao", new BigDecimal("200000000")),
                    new HanMucGiaoDich("HM04", "Gói VIP Doanh Nghiệp", new BigDecimal("500000000"))
            );
        }

        danhSachHienThiHanMuc.setAll(danhSachHanMuc);
        if (lblTongGoiHanMuc != null) lblTongGoiHanMuc.setText("Tổng số: " + danhSachHienThiHanMuc.size() + " gói");

        // Cập nhật danh sách gói hạn mức sang Dropdown Phân đoạn 2
        capNhatDropdownGoiHanMuc();
    }

    private void capNhatDropdownGoiHanMuc() {
        if (cboChonGoiHanMucMoi == null) return;
        ObservableList<String> listTenGoi = FXCollections.observableArrayList();
        for (HanMucGiaoDich hm : danhSachHanMuc) {
            listTenGoi.add(hm.getTenHanMuc() + " (" + dfTien.format(hm.getHanMucNgay()) + "/ngày)");
        }
        cboChonGoiHanMucMoi.setItems(listTenGoi);
        if (!listTenGoi.isEmpty()) {
            cboChonGoiHanMucMoi.setValue(listTenGoi.get(0));
        }
    }

    @FXML
    private void onTimKiemHanMuc() {
        String kw = txtTimKiemHanMuc != null && txtTimKiemHanMuc.getText() != null
                ? txtTimKiemHanMuc.getText().trim().toLowerCase() : "";

        danhSachHienThiHanMuc.clear();
        for (HanMucGiaoDich hm : danhSachHanMuc) {
            if (kw.isEmpty()
                    || (hm.getMaHanMuc() != null && hm.getMaHanMuc().toLowerCase().contains(kw))
                    || (hm.getTenHanMuc() != null && hm.getTenHanMuc().toLowerCase().contains(kw))) {
                danhSachHienThiHanMuc.add(hm);
            }
        }
        if (lblTongGoiHanMuc != null) lblTongGoiHanMuc.setText("Tổng số: " + danhSachHienThiHanMuc.size() + " gói");
    }

    @FXML
    private void onLamMoiHanMuc() {
        if (txtTimKiemHanMuc != null) txtTimKiemHanMuc.clear();
        taiDuLieuHanMuc();
    }

    @FXML
    public void onMoPopupTaoGoiHanMuc() {
        isSuaHanMuc = false;
        lblModalHanMucTitle.setText("Tạo gói hạn mức giao dịch");
        txtModalMaHanMuc.setDisable(false);
        txtModalMaHanMuc.clear();
        txtModalTenHanMuc.clear();
        txtModalHanMucNgay.clear();
        lblModalHanMucLoi.setText("");
        hienModal(modalThemSuaHanMuc);
    }

    @FXML
    public void onMoPopupSuaHanMuc() {
        HanMucGiaoDich chon = tblHanMuc.getSelectionModel().getSelectedItem();
        if (chon != null) {
            moPopupSuaHanMucCho(chon);
        } else {
            hienThongBaoChuaChonDong();
        }
    }

    private void moPopupSuaHanMucCho(HanMucGiaoDich hm) {
        this.hanMucDangChon = hm;
        isSuaHanMuc = true;
        lblModalHanMucTitle.setText("Chỉnh sửa gói hạn mức");
        txtModalMaHanMuc.setText(hm.getMaHanMuc());
        txtModalMaHanMuc.setDisable(true);
        txtModalTenHanMuc.setText(hm.getTenHanMuc());
        txtModalHanMucNgay.setText(hm.getHanMucNgay() != null ? hm.getHanMucNgay().toPlainString() : "");
        lblModalHanMucLoi.setText("");
        hienModal(modalThemSuaHanMuc);
    }

    @FXML
    public void onDongPopupThemSuaHanMuc() {
        anModal(modalThemSuaHanMuc);
    }

    @FXML
    public void onLuuThemSuaHanMuc() {
        String maHM = txtModalMaHanMuc.getText().trim();
        String tenHM = txtModalTenHanMuc.getText().trim();
        String strTien = txtModalHanMucNgay.getText().trim();

        if (maHM.isEmpty() || tenHM.isEmpty() || strTien.isEmpty()) {
            lblModalHanMucLoi.setText("Vui lòng điền đầy đủ các thông tin!");
            return;
        }

        BigDecimal tien;
        try {
            tien = new BigDecimal(strTien);
            if (tien.compareTo(BigDecimal.ZERO) <= 0) {
                lblModalHanMucLoi.setText("Hạn mức ngày phải lớn hơn 0!");
                return;
            }
        } catch (NumberFormatException e) {
            lblModalHanMucLoi.setText("Hạn mức ngày phải là số hợp lệ!");
            return;
        }

        if (isSuaHanMuc && hanMucDangChon != null) {
            hanMucDangChon.setTenHanMuc(tenHM);
            hanMucDangChon.setHanMucNgay(tien);

            String sql = "UPDATE dbo.[HanMucGiaoDịch] SET TenHanMuc = ?, HanMucNgay = ? WHERE MaHanMuc = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, tenHM);
                ps.setBigDecimal(2, tien);
                ps.setString(3, maHM);
                ps.executeUpdate();
            } catch (Exception e) {
                System.err.println("Lỗi cập nhật HanMuc DB: " + e.getMessage());
            }

        } else {
            HanMucGiaoDich hmMoi = new HanMucGiaoDich(maHM, tenHM, tien);
            String sql = "INSERT INTO dbo.[HanMucGiaoDịch] (MaHanMuc, TenHanMuc, HanMucNgay) VALUES (?, ?, ?)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, maHM);
                ps.setString(2, tenHM);
                ps.setBigDecimal(3, tien);
                ps.executeUpdate();
            } catch (Exception e) {
                System.err.println("Lỗi thêm HanMuc DB: " + e.getMessage());
            }
            danhSachHanMuc.add(0, hmMoi);
        }

        tblHanMuc.refresh();
        onTimKiemHanMuc();
        capNhatDropdownGoiHanMuc();
        anModal(modalThemSuaHanMuc);
    }

    @FXML
    public void onMoPopupXoaHanMuc() {
        HanMucGiaoDich chon = tblHanMuc.getSelectionModel().getSelectedItem();
        if (chon != null) {
            xoaHanMuc(chon);
        } else {
            hienThongBaoChuaChonDong();
        }
    }

    private void xoaHanMuc(HanMucGiaoDich hm) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận xóa gói hạn mức");
        confirm.setHeaderText(null);
        confirm.setContentText("Bạn có chắc chắn muốn xóa gói hạn mức [" + hm.getMaHanMuc() + "] " + hm.getTenHanMuc() + "?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String sql = "DELETE FROM dbo.[HanMucGiaoDịch] WHERE MaHanMuc = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, hm.getMaHanMuc());
                ps.executeUpdate();
            } catch (Exception e) {
                System.err.println("Lỗi xóa HanMuc DB: " + e.getMessage());
            }

            danhSachHanMuc.remove(hm);
            onTimKiemHanMuc();
            capNhatDropdownGoiHanMuc();
        }
    }

    // =========================================================
    // CẤU HÌNH & XỬ LÝ PHÂN ĐOẠN 2: GÁN HẠN MỨC CHO TÀI KHOẢN
    // =========================================================
    private void cauHinhPhanDoan2() {
        if (cardDetailTK != null) {
            cardDetailTK.setVisible(true); // Hiển thị sẵn dữ liệu mẫu hoặc sau khi tìm
        }
    }

    @FXML
    private void onTraCuuTaiKhoan() {
        String keyword = txtTraCuuTK != null ? txtTraCuuTK.getText().trim() : "";
        if (keyword.isEmpty()) {
            keyword = "1000000001";
        }
        final String finalKeyword = keyword;

        // Hiệu ứng Loading xoay nhẹ
        if (boxLoadingTK != null) {
            boxLoadingTK.setVisible(true);
            boxLoadingTK.setManaged(true);
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(300); // Tạo hiệu ứng tải mượt mà
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            if (boxLoadingTK != null) {
                boxLoadingTK.setVisible(false);
                boxLoadingTK.setManaged(false);
            }

            // Truy vấn dữ liệu tài khoản từ DB
            boolean timThay = false;
            String sql = """
                    SELECT tk.SoTaiKhoan, kh.HoTen, tk.SoDu, cn.TenChiNhanh, tk.TrangThai, hm.TenHanMuc, hm.HanMucNgay
                    FROM TaiKhoanNganHang tk
                    LEFT JOIN KhachHang kh ON tk.MaKhachHang = kh.MaKhachHang
                    LEFT JOIN ChiNhanh cn ON tk.MaChiNhanh = cn.MaChiNhanh
                    LEFT JOIN dbo.[HanMucGiaoDịch] hm ON tk.MaHanMuc = hm.MaHanMuc
                    WHERE tk.SoTaiKhoan = ? OR tk.MaKhachHang = ?
                    """;

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, finalKeyword);
                ps.setString(2, finalKeyword);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    timThay = true;
                    traCuuSTK = rs.getString("SoTaiKhoan");
                    lblDetailSoTK.setText(traCuuSTK);
                    lblDetailTenKH.setText(rs.getString("HoTen") != null ? rs.getString("HoTen") : "Khách hàng");
                    lblDetailSoDu.setText(dfTien.format(rs.getBigDecimal("SoDu")));
                    lblDetailChiNhanh.setText(rs.getString("TenChiNhanh") != null ? rs.getString("TenChiNhanh") : "Trụ sở chính");

                    trangThaiHienTaiTK = rs.getString("TrangThai");
                    capNhatBadgeTrangThai(trangThaiHienTaiTK);

                    String tenHM = rs.getString("TenHanMuc");
                    BigDecimal hmNgay = rs.getBigDecimal("HanMucNgay");
                    if (tenHM != null && hmNgay != null) {
                        lblDetailGoiHanMucHienTai.setText(tenHM + " (" + dfTien.format(hmNgay) + "/ngày)");
                    } else {
                        lblDetailGoiHanMucHienTai.setText("Chưa gán hạn mức");
                    }
                }
            } catch (Exception ex) {
                System.err.println("Tra cứu DB lỗi: " + ex.getMessage());
            }

            if (!timThay) {
                // Fallback dữ liệu mẫu hiển thị
                traCuuSTK = finalKeyword;
                lblDetailSoTK.setText(traCuuSTK);
                lblDetailTenKH.setText("Nguyễn Văn A");
                lblDetailSoDu.setText("25,400,000 VNĐ");
                lblDetailChiNhanh.setText("Chi nhánh TP.HCM");
                trangThaiHienTaiTK = "Hoạt động";
                capNhatBadgeTrangThai(trangThaiHienTaiTK);
                lblDetailGoiHanMucHienTai.setText("Gói Cơ Bản (50,000,000 VNĐ / ngày)");
            }

            if (lblThongBaoCapNhatTK != null) lblThongBaoCapNhatTK.setText("");
            if (cardDetailTK != null) {
                cardDetailTK.setVisible(true);
                cardDetailTK.setManaged(true);
            }
        });

        new Thread(task).start();
    }

    private void capNhatBadgeTrangThai(String trangThai) {
        if (lblBadgeTrangThaiTK == null) return;
        lblBadgeTrangThaiTK.setText(trangThai);
        if ("Hoạt động".equalsIgnoreCase(trangThai)) {
            lblBadgeTrangThaiTK.setStyle("-fx-background-color: #E8F8F0; -fx-text-fill: #10B981; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 10; -fx-padding: 5 12;");
        } else {
            lblBadgeTrangThaiTK.setStyle("-fx-background-color: #FDECEC; -fx-text-fill: #D9534F; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 10; -fx-padding: 5 12;");
        }
    }

    @FXML
    public void onMoXacNhanCapNhatHanMuc() {
        traCuuGoiMoiDuocChon = cboChonGoiHanMucMoi.getValue();
        if (traCuuGoiMoiDuocChon == null || traCuuGoiMoiDuocChon.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Vui lòng chọn một gói hạn mức mới từ danh sách!");
            alert.showAndWait();
            return;
        }

        lblNoiDungXacNhanDoiHanMuc.setText("Xác nhận đổi hạn mức tài khoản [" + traCuuSTK + "] sang [" + traCuuGoiMoiDuocChon + "]?");
        hienModal(modalXacNhanDoiHanMuc);
    }

    @FXML
    public void onDongPopupXacNhanDoiHanMuc() {
        anModal(modalXacNhanDoiHanMuc);
    }

    @FXML
    public void onDongYDoiHanMuc() {
        lblDetailGoiHanMucHienTai.setText(traCuuGoiMoiDuocChon);
        if (lblThongBaoCapNhatTK != null) {
            lblThongBaoCapNhatTK.setStyle("-fx-font-size: 13px; -fx-text-fill: #10B981; -fx-font-weight: bold;");
            lblThongBaoCapNhatTK.setText("✓ Cập nhật gói hạn mức cho tài khoản [" + traCuuSTK + "] thành công!");
        }

        // Cập nhật DB
        String sql = "UPDATE TaiKhoanNganHang SET MaHanMuc = (SELECT TOP 1 MaHanMuc FROM dbo.[HanMucGiaoDịch] WHERE ? LIKE '%' + TenHanMuc + '%') WHERE SoTaiKhoan = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, traCuuGoiMoiDuocChon);
            ps.setString(2, traCuuSTK);
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Cập nhật hạn mức DB lỗi: " + e.getMessage());
        }

        anModal(modalXacNhanDoiHanMuc);
    }

    @FXML
    public void onMoXacNhanDoiTrangThaiTK() {
        String trangThaiMoi = "Hoạt động".equalsIgnoreCase(trangThaiHienTaiTK) ? "Khóa" : "Hoạt động";
        lblNoiDungDoiTrangThaiTK.setText("Xác nhận chuyển đổi trạng thái tài khoản [" + traCuuSTK + "] từ '" + trangThaiHienTaiTK + "' sang '" + trangThaiMoi + "'?");
        hienModal(modalDoiTrangThaiTK);
    }

    @FXML
    public void onDongPopupDoiTrangThaiTK() {
        anModal(modalDoiTrangThaiTK);
    }

    @FXML
    public void onDongYDoiTrangThaiTK() {
        trangThaiHienTaiTK = "Hoạt động".equalsIgnoreCase(trangThaiHienTaiTK) ? "Khóa" : "Hoạt động";
        capNhatBadgeTrangThai(trangThaiHienTaiTK);

        if (lblThongBaoCapNhatTK != null) {
            lblThongBaoCapNhatTK.setStyle("-fx-font-size: 13px; -fx-text-fill: #5148C9; -fx-font-weight: bold;");
            lblThongBaoCapNhatTK.setText("✓ Đã cập nhật trạng thái tài khoản thành '" + trangThaiHienTaiTK + "'!");
        }

        String sql = "UPDATE TaiKhoanNganHang SET TrangThai = ? WHERE SoTaiKhoan = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trangThaiHienTaiTK);
            ps.setString(2, traCuuSTK);
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Cập nhật trạng thái TK DB lỗi: " + e.getMessage());
        }

        anModal(modalDoiTrangThaiTK);
    }

    // =========================================================
    // CẤU HÌNH & XỬ LÝ PHÂN ĐOẠN 3: LOẠI GD & NGÂN HÀNG
    // =========================================================
    private void cauHinhPhanDoan3() {
        // Bảng Loại giao dịch
        colMaLoaiGD.setCellValueFactory(new PropertyValueFactory<>("maLoaiGD"));
        colTenLoaiGD.setCellValueFactory(new PropertyValueFactory<>("tenLoaiGD"));

        colThaoTacLoaiGD.setCellFactory(param -> new TableCell<>() {
            private final Button btnSua = new Button("Sửa");
            private final Button btnXoa = new Button("Xóa");
            private final HBox box = new HBox(6, btnSua, btnXoa);

            {
                box.setAlignment(Pos.CENTER);
                btnSua.setStyle("-fx-background-color: #ECEBFA; -fx-text-fill: #5148C9; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 11px; -fx-padding: 4 8;");
                btnXoa.setStyle("-fx-background-color: #FDECEC; -fx-text-fill: #D9534F; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 11px; -fx-padding: 4 8;");

                btnSua.setOnAction(e -> {
                    LoaiGiaoDich item = getTableView().getItems().get(getIndex());
                    moPopupSuaLoaiGDCho(item);
                });

                btnXoa.setOnAction(e -> {
                    LoaiGiaoDich item = getTableView().getItems().get(getIndex());
                    xoaLoaiGD(item);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else setGraphic(box);
            }
        });

        tblLoaiGD.setItems(danhSachLoaiGD);

        // Bảng Ngân hàng
        colMaNganHang.setCellValueFactory(new PropertyValueFactory<>("maNganHang"));
        colTenNganHang.setCellValueFactory(new PropertyValueFactory<>("tenNganHang"));

        colTrangThaiNganHang.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    NganHangItem nh = getTableRow().getItem();
                    Label badge = new Label(nh.getTrangThai());
                    if ("Hoạt động".equalsIgnoreCase(nh.getTrangThai())) {
                        badge.setStyle("-fx-background-color: #E8F8F0; -fx-text-fill: #10B981; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 8; -fx-font-size: 11px;");
                    } else {
                        badge.setStyle("-fx-background-color: #FDECEC; -fx-text-fill: #D9534F; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 8; -fx-font-size: 11px;");
                    }
                    HBox box = new HBox(badge);
                    box.setAlignment(Pos.CENTER);
                    setGraphic(box);
                }
            }
        });

        colThaoTacNganHang.setCellFactory(param -> new TableCell<>() {
            private final Button btnToggle = new Button("Đổi trạng thái");

            {
                btnToggle.setStyle("-fx-background-color: #F1F2F6; -fx-text-fill: #171A35; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 11px; -fx-padding: 5 10;");
                btnToggle.setOnAction(e -> {
                    NganHangItem item = getTableView().getItems().get(getIndex());
                    toggleTrangThaiNganHang(item);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else {
                    HBox box = new HBox(btnToggle);
                    box.setAlignment(Pos.CENTER);
                    setGraphic(box);
                }
            }
        });

        tblNganHang.setItems(danhSachNganHang);
    }

    private void taiDuLieuLoaiGD() {
        danhSachLoaiGD.clear();
        String sql = "SELECT MaLoaiGD, TenLoaiGD FROM LoaiGiaoDich";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                LoaiGiaoDich lgd = new LoaiGiaoDich();
                lgd.setMaLoaiGD(rs.getString("MaLoaiGD"));
                lgd.setTenLoaiGD(rs.getString("TenLoaiGD"));
                danhSachLoaiGD.add(lgd);
            }
        } catch (Exception e) {
            System.err.println("Lỗi nạp LoaiGD: " + e.getMessage());
            napLoaiGDMau();
        }

        if (danhSachLoaiGD.isEmpty()) {
            napLoaiGDMau();
        }
    }

    private void napLoaiGDMau() {
        LoaiGiaoDich l1 = new LoaiGiaoDich(); l1.setMaLoaiGD("CK01"); l1.setTenLoaiGD("Chuyển khoản");
        LoaiGiaoDich l2 = new LoaiGiaoDich(); l2.setMaLoaiGD("NT01"); l2.setTenLoaiGD("Nạp tiền");
        LoaiGiaoDich l3 = new LoaiGiaoDich(); l3.setMaLoaiGD("RT01"); l3.setTenLoaiGD("Rút tiền");
        LoaiGiaoDich l4 = new LoaiGiaoDich(); l4.setMaLoaiGD("TT01"); l4.setTenLoaiGD("Thanh toán");
        danhSachLoaiGD.addAll(l1, l2, l3, l4);
    }

    private void taiDuLieuNganHang() {
        danhSachNganHang.clear();
        String sql = "SELECT MaNganHang, TenNganHang, TrangThai FROM NganHang";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                danhSachNganHang.add(new NganHangItem(
                        rs.getString("MaNganHang"),
                        rs.getString("TenNganHang"),
                        rs.getString("TrangThai")
                ));
            }
        } catch (Exception e) {
            System.err.println("Lỗi nạp NganHang: " + e.getMessage());
            danhSachNganHang.addAll(
                    new NganHangItem("SMART", "SMART BANK", "Hoạt động"),
                    new NganHangItem("VCB", "Vietcombank", "Hoạt động"),
                    new NganHangItem("BIDV", "BIDV", "Hoạt động"),
                    new NganHangItem("TCB", "Techcombank", "Hoạt động"),
                    new NganHangItem("MB", "MB Bank", "Tạm ngừng")
            );
        }
    }

    @FXML
    public void onMoPopupThemLoaiGD() {
        isSuaLoaiGD = false;
        lblModalLoaiGDTitle.setText("Thêm loại giao dịch mới");
        txtModalMaLoaiGD.setDisable(false);
        txtModalMaLoaiGD.clear();
        txtModalTenLoaiGD.clear();
        hienModal(modalThemSuaLoaiGD);
    }

    @FXML
    public void onMoPopupSuaLoaiGD() {
        LoaiGiaoDich chon = tblLoaiGD.getSelectionModel().getSelectedItem();
        if (chon != null) {
            moPopupSuaLoaiGDCho(chon);
        } else {
            hienThongBaoChuaChonDong();
        }
    }

    private void moPopupSuaLoaiGDCho(LoaiGiaoDich lgd) {
        this.loaiGDDangChon = lgd;
        isSuaLoaiGD = true;
        lblModalLoaiGDTitle.setText("Chỉnh sửa loại giao dịch");
        txtModalMaLoaiGD.setText(lgd.getMaLoaiGD());
        txtModalMaLoaiGD.setDisable(true);
        txtModalTenLoaiGD.setText(lgd.getTenLoaiGD());
        hienModal(modalThemSuaLoaiGD);
    }

    @FXML
    public void onDongPopupThemSuaLoaiGD() {
        anModal(modalThemSuaLoaiGD);
    }

    @FXML
    public void onLuuThemSuaLoaiGD() {
        String ma = txtModalMaLoaiGD.getText().trim();
        String ten = txtModalTenLoaiGD.getText().trim();
        if (ma.isEmpty() || ten.isEmpty()) return;

        if (isSuaLoaiGD && loaiGDDangChon != null) {
            loaiGDDangChon.setTenLoaiGD(ten);
            String sql = "UPDATE LoaiGiaoDich SET TenLoaiGD = ? WHERE MaLoaiGD = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, ten);
                ps.setString(2, ma);
                ps.executeUpdate();
            } catch (Exception e) {
                System.err.println("Lỗi sửa LoaiGD DB: " + e.getMessage());
            }
        } else {
            LoaiGiaoDich moi = new LoaiGiaoDich();
            moi.setMaLoaiGD(ma);
            moi.setTenLoaiGD(ten);
            String sql = "INSERT INTO LoaiGiaoDich (MaLoaiGD, TenLoaiGD) VALUES (?, ?)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, ma);
                ps.setString(2, ten);
                ps.executeUpdate();
            } catch (Exception e) {
                System.err.println("Lỗi thêm LoaiGD DB: " + e.getMessage());
            }
            danhSachLoaiGD.add(0, moi);
        }

        tblLoaiGD.refresh();
        anModal(modalThemSuaLoaiGD);
    }

    @FXML
    public void onMoPopupXoaLoaiGD() {
        LoaiGiaoDich chon = tblLoaiGD.getSelectionModel().getSelectedItem();
        if (chon != null) {
            xoaLoaiGD(chon);
        } else {
            hienThongBaoChuaChonDong();
        }
    }

    private void xoaLoaiGD(LoaiGiaoDich lgd) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận xóa");
        confirm.setContentText("Bạn có muốn xóa loại giao dịch [" + lgd.getMaLoaiGD() + "] " + lgd.getTenLoaiGD() + "?");
        Optional<ButtonType> res = confirm.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            String sql = "DELETE FROM LoaiGiaoDich WHERE MaLoaiGD = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, lgd.getMaLoaiGD());
                ps.executeUpdate();
            } catch (Exception e) {
                System.err.println("Lỗi xóa LoaiGD DB: " + e.getMessage());
            }
            danhSachLoaiGD.remove(lgd);
        }
    }

    @FXML
    public void onToggleTrangThaiNganHang() {
        NganHangItem chon = tblNganHang.getSelectionModel().getSelectedItem();
        if (chon != null) {
            toggleTrangThaiNganHang(chon);
        } else {
            hienThongBaoChuaChonDong();
        }
    }

    private void toggleTrangThaiNganHang(NganHangItem nh) {
        String ttMoi = "Hoạt động".equalsIgnoreCase(nh.getTrangThai()) ? "Tạm ngừng" : "Hoạt động";
        nh.setTrangThai(ttMoi);

        String sql = "UPDATE NganHang SET TrangThai = ? WHERE MaNganHang = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ttMoi);
            ps.setString(2, nh.getMaNganHang());
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Lỗi cập nhật ngân hàng DB: " + e.getMessage());
        }

        tblNganHang.refresh();
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
        moView("admin-employee-branch-view.fxml", event);
    }

    @FXML
    private void onCauHinhDanhMuc(ActionEvent event) {
        // Đang ở view này
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
