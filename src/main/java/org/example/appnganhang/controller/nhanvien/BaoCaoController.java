package org.example.appnganhang.controller.nhanvien;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import org.example.appnganhang.model.entity.BaoCao;
import org.example.appnganhang.controller.service.Iml.BaoCaoServiceImpl;
import org.example.appnganhang.controller.service.intf.BaoCaoService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

import static org.example.appnganhang.controller.util.Navigation.moView;

public class BaoCaoController {
    @FXML private TextField txtTuNgay;
    @FXML private TextField txtDenNgay;

    @FXML private ComboBox<String> cboLoaiBaoCao;

    @FXML private Button btnThongKe;
    @FXML private Button btnLamMoi;
    @FXML private Button btnXuatBaoCao;

    @FXML private Label lblTongSoGiaoDich;
    @FXML private Label lblTongTien;
    @FXML private Label lblGiaoDichThanhCong;
    @FXML private Label lblGiaoDichThatBai;

    @FXML private TableView<BaoCao> tblBaoCao;
    @FXML private TableColumn<BaoCao, String> colMaGiaoDich;
    @FXML private TableColumn<BaoCao, String> colTaiKhoanNguon;
    @FXML private TableColumn<BaoCao, String> colTaiKhoanDich;
    @FXML private TableColumn<BaoCao, BigDecimal> colSoTien;
    @FXML private TableColumn<BaoCao, LocalDateTime> colNgayGiaoDich;
    @FXML private TableColumn<BaoCao, String> colTrangThai;

    private final ObservableList<BaoCao> danhSach = FXCollections.observableArrayList();
    private final BaoCaoService baoCaoService = new BaoCaoServiceImpl();

    private static final DateTimeFormatter FORMAT_NGAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    private void initialize() {
        cauHinhBang();
        khoiTaoLoaiBaoCao();
        taiDuLieu();
    }

    private void cauHinhBang() {
        colMaGiaoDich.setCellValueFactory(new PropertyValueFactory<>("maGiaoDich"));
        colTaiKhoanNguon.setCellValueFactory(new PropertyValueFactory<>("taiKhoanNguon"));
        colTaiKhoanDich.setCellValueFactory(new PropertyValueFactory<>("taiKhoanDich"));
        colSoTien.setCellValueFactory(new PropertyValueFactory<>("soTien"));
        colNgayGiaoDich.setCellValueFactory(new PropertyValueFactory<>("ngayGiaoDich"));
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
    }

    private void khoiTaoLoaiBaoCao() {

        cboLoaiBaoCao.setItems(
                FXCollections.observableArrayList(
                        "Tất cả giao dịch",
                        "Giao dịch thành công",
                        "Giao dịch thất bại",
                        "Chuyển tiền",
                        "Thanh toán"
                )
        );

        cboLoaiBaoCao.setValue("Tất cả giao dịch");
    }

    private void taiDuLieu() {

        try {

            List<Map<String, Object>> duLieu = baoCaoService.baoCaoGiaoDich();
            danhSach.clear();

            for (Map<String, Object> row : duLieu) {

                BaoCao baoCao = chuyenMapThanhEntity(row);

                if (baoCao != null) {
                    danhSach.add(baoCao);
                }
            }

            tblBaoCao.setItems(danhSach);
            capNhatThongKeTongQuat();

        } catch (Exception e) {

            e.printStackTrace();

            danhSach.clear();
            tblBaoCao.setItems(danhSach);

            lblTongSoGiaoDich.setText("0");
            lblTongTien.setText("0 VNĐ");
            lblGiaoDichThanhCong.setText("0");
            lblGiaoDichThatBai.setText("0");
        }
    }

    private BaoCao chuyenMapThanhEntity(
            Map<String, Object> row
    ) {

        if (row == null) {
            return null;
        }

        BaoCao baoCao = new BaoCao();

        baoCao.setMaGiaoDich(layString(row, "MaGiaoDich"));
        baoCao.setTaiKhoanNguon(layString(row, "SoTaiKhoanNguon"));
        baoCao.setTaiKhoanDich(layString(row, "SoTaiKhoanDich"));
        baoCao.setSoTien(layBigDecimal(row, "SoTien"));
        baoCao.setNgayGiaoDich(layLocalDateTime(row, "NgayGiaoDich"));
        baoCao.setLoaiGiaoDich(layString(row, "LoaiGiaoDich"));
        baoCao.setTrangThai(layString(row, "TrangThai"));

        return baoCao;
    }

    private String layString(
            Map<String, Object> row,
            String key
    ) {

        Object value = row.get(key);

        if (value == null) {
            return "";
        }

        return value.toString();
    }

    private BigDecimal layBigDecimal(
            Map<String, Object> row,
            String key
    ) {

        Object value = row.get(key);

        if (value == null) {
            return BigDecimal.ZERO;
        }

        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }

        if (value instanceof Number) {
            return BigDecimal.valueOf(
                    ((Number) value).doubleValue()
            );
        }

        try {

            return new BigDecimal(
                    value.toString().trim()
            );

        } catch (NumberFormatException e) {

            return BigDecimal.ZERO;
        }
    }

    private LocalDateTime layLocalDateTime(
            Map<String, Object> row,
            String key
    ) {

        Object value = row.get(key);

        if (value == null) {
            return null;
        }

        if (value instanceof LocalDateTime) {
            return (LocalDateTime) value;
        }

        if (value instanceof java.sql.Timestamp) {
            return ((java.sql.Timestamp) value)
                    .toLocalDateTime();
        }

        return null;
    }

    @FXML
    private void onThongKe() {

        LocalDate tuNgay = layNgay(txtTuNgay.getText());
        LocalDate denNgay = layNgay(txtDenNgay.getText());

        if (!txtTuNgay.getText().trim().isEmpty()
                && tuNgay == null) {

            System.out.println("Ngày bắt đầu không đúng định dạng dd/MM/yyyy.");

            return;
        }

        if (!txtDenNgay.getText().trim().isEmpty()
                && denNgay == null) {

            System.out.println("Ngày kết thúc không đúng định dạng dd/MM/yyyy.");

            return;
        }

        if (tuNgay != null
                && denNgay != null
                && tuNgay.isAfter(denNgay)) {

            System.out.println("Từ ngày không được lớn hơn đến ngày.");

            return;
        }

        String loaiBaoCao = cboLoaiBaoCao.getValue();
        ObservableList<BaoCao> ketQua = FXCollections.observableArrayList();

        for (BaoCao baoCao : danhSach) {

            if (!phuHopNgay(
                    baoCao,
                    tuNgay,
                    denNgay
            )) {
                continue;
            }

            if (!phuHopLoai(
                    baoCao,
                    loaiBaoCao
            )) {
                continue;
            }

            ketQua.add(baoCao);
        }

        tblBaoCao.setItems(ketQua);
        capNhatThongKe(ketQua);
    }

    private boolean phuHopNgay(
            BaoCao baoCao,
            LocalDate tuNgay,
            LocalDate denNgay
    ) {

        if (tuNgay == null && denNgay == null) {
            return true;
        }

        if (baoCao.getNgayGiaoDich() == null) {
            return false;
        }

        LocalDate ngayGiaoDich =
                baoCao.getNgayGiaoDich().toLocalDate();

        if (tuNgay != null
                && ngayGiaoDich.isBefore(tuNgay)) {

            return false;
        }

        if (denNgay != null
                && ngayGiaoDich.isAfter(denNgay)) {

            return false;
        }

        return true;
    }

    private boolean phuHopLoai(
            BaoCao baoCao,
            String loaiBaoCao
    ) {

        if (loaiBaoCao == null
                || loaiBaoCao.equals("Tất cả giao dịch")) {

            return true;
        }

        String trangThai = baoCao.getTrangThai();
        String loaiGiaoDich = baoCao.getLoaiGiaoDich();

        switch (loaiBaoCao) {

            case "Giao dịch thành công":
                return "Thành công".equalsIgnoreCase(trangThai);

            case "Giao dịch thất bại":
                return "Thất bại".equalsIgnoreCase(trangThai);

            case "Chuyển tiền":
                return "Chuyển tiền".equalsIgnoreCase(loaiGiaoDich);

            case "Thanh toán":
                return "Thanh toán".equalsIgnoreCase(loaiGiaoDich);

            default:
                return true;
        }
    }

    private LocalDate layNgay(String text) {

        if (text == null || text.trim().isEmpty()) {
            return null;
        }

        try {

            return LocalDate.parse(
                    text.trim(),
                    FORMAT_NGAY
            );

        } catch (DateTimeParseException e) {

            return null;
        }
    }

    private void capNhatThongKe(ObservableList<BaoCao> danhSachThongKe) {

        int tongSoGiaoDich = danhSachThongKe.size();
        int giaoDichThanhCong = 0;
        int giaoDichThatBai = 0;

        BigDecimal tongTien = BigDecimal.ZERO;

        for (BaoCao baoCao : danhSachThongKe) {

            if ("Thành công".equalsIgnoreCase(baoCao.getTrangThai())) {
                giaoDichThanhCong++;
            }

            if ("Thất bại".equalsIgnoreCase(baoCao.getTrangThai())) {
                giaoDichThatBai++;
            }

            if (baoCao.getSoTien() != null) {

                tongTien = tongTien.add(baoCao.getSoTien());
            }
        }

        lblTongSoGiaoDich.setText(String.valueOf(tongSoGiaoDich));

        lblTongTien.setText(
                String.format(
                        "%,.0f VNĐ",
                        tongTien
                )
        );

        lblGiaoDichThanhCong.setText(String.valueOf(giaoDichThanhCong));
        lblGiaoDichThatBai.setText(String.valueOf(giaoDichThatBai));
    }

    private void capNhatThongKeTongQuat() {

        int tongSoGiaoDich = baoCaoService.demTongSoGiaoDich();
        BigDecimal tongTien = baoCaoService.tinhTongTienChuyen();
        int giaoDichThanhCong = baoCaoService.demGiaoDichThanhCong();
        int giaoDichThatBai = baoCaoService.demGiaoDichThatBai();

        lblTongSoGiaoDich.setText(String.valueOf(tongSoGiaoDich));

        lblTongTien.setText(
                String.format(
                        "%,.0f VNĐ",
                        tongTien
                )
        );

        lblGiaoDichThanhCong.setText(String.valueOf(giaoDichThanhCong));
        lblGiaoDichThatBai.setText(String.valueOf(giaoDichThatBai));
    }

    @FXML
    private void onLamMoi() {

        txtTuNgay.clear();
        txtDenNgay.clear();

        cboLoaiBaoCao.setValue("Tất cả giao dịch");
        taiDuLieu();

        tblBaoCao.getSelectionModel()
                .clearSelection();
    }

    @FXML
    private void onXuatBaoCao() {

        System.out.println("===== XUẤT BÁO CÁO =====");
        System.out.println("Từ ngày: " + txtTuNgay.getText());
        System.out.println("Đến ngày: " + txtDenNgay.getText());
        System.out.println("Loại báo cáo: " + cboLoaiBaoCao.getValue());System.out.println("Tổng số giao dịch: " + lblTongSoGiaoDich.getText());
        System.out.println("Tổng tiền: " + lblTongTien.getText());
        System.out.println("Giao dịch thành công: " + lblGiaoDichThanhCong.getText());
        System.out.println("Giao dịch thất bại: " + lblGiaoDichThatBai.getText());
    }

    // =========================================================
    // ĐIỀU HƯỚNG SIDEBAR
    // =========================================================
    @FXML
    private void onTrangChu(ActionEvent event){moView("dashboard-view.fxml", event);}

    @FXML
    private void onKhachHang(ActionEvent event) {
        moView("customer-view.fxml", event);
    }

    @FXML
    private void onChuyenTien(ActionEvent event) {
        moView("transfer-view.fxml", event);
    }

    @FXML
    private void onTraCuuGiaoDich(ActionEvent event) {
        moView("transaction-view.fxml", event);
    }

    @FXML
    private void onLichSuGiaoDich(ActionEvent event) {
        moView("history-view.fxml", event);
    }

    @FXML
    private void onHanMuc(ActionEvent event) {
        moView("limit-view.fxml", event);
    }

    @FXML
    private void onLoaiGiaoDich(ActionEvent event) {
        moView(
                "transaction-type-view.fxml",
                event
        );
    }

    @FXML
    private void onChiNhanh(ActionEvent event) {
        moView("branch-view.fxml", event);
    }

    @FXML
    private void onNhanVien(ActionEvent event) {
        moView("employee-view.fxml", event);
    }

    @FXML
    private void onBaoCao(ActionEvent event) {
        // Đang ở trang Báo cáo
    }

    @FXML
    private void onDoiMatKhau(ActionEvent event) {
        moView(
                "change-password-view.fxml",
                event
        );
    }

    @FXML
    private void onDangXuat(ActionEvent event) {
        moView("login-view.fxml", event);
    }
}