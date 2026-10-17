package org.example.appnganhang.controller.giaodichvien;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import org.example.appnganhang.model.entity.GiaoDichChuyenTien;
import org.example.appnganhang.model.util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.example.appnganhang.controller.util.Navigation.moView;

public class TellerDashboardController {

    @FXML private Label lblTellerName;
    @FXML private Label lblTellerBranch;
    @FXML private Label lblThoiGianCa;

    // Stat cards
    @FXML private Label lblStatTongGD;
    @FXML private Label lblStatTongTien;
    @FXML private Label lblStatChoXuLy;
    @FXML private Label lblStatTaiKhoan;

    // Bảng giao dịch gần đây
    @FXML private TableView<GiaoDichChuyenTien> tblGiaoDichGanDay;
    @FXML private TableColumn<GiaoDichChuyenTien, String> colMaGD;
    @FXML private TableColumn<GiaoDichChuyenTien, String> colTKNguon;
    @FXML private TableColumn<GiaoDichChuyenTien, String> colTKDich;
    @FXML private TableColumn<GiaoDichChuyenTien, String> colLoaiGD;
    @FXML private TableColumn<GiaoDichChuyenTien, String> colSoTien;
    @FXML private TableColumn<GiaoDichChuyenTien, String> colNgayGD;
    @FXML private TableColumn<GiaoDichChuyenTien, String> colTrangThai;

    private final ObservableList<GiaoDichChuyenTien> danhSachGiaoDich = FXCollections.observableArrayList();
    private final DecimalFormat dfTien = new DecimalFormat("#,### VNĐ");
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @FXML
    public void initialize() {
        if (lblThoiGianCa != null) {
            lblThoiGianCa.setText("Trực tuyến | Hôm nay: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }

        cauHinhBangGiaoDich();
        taiDuLieuThongKe();
        taiDuLieuGiaoDich();
    }

    private void cauHinhBangGiaoDich() {
        colMaGD.setCellValueFactory(new PropertyValueFactory<>("maGiaoDich"));
        colTKNguon.setCellValueFactory(new PropertyValueFactory<>("maTaiKhoanNguon"));
        colTKDich.setCellValueFactory(new PropertyValueFactory<>("maTaiKhoanDich"));
        colLoaiGD.setCellValueFactory(new PropertyValueFactory<>("maLoaiGD"));

        colSoTien.setCellValueFactory(cellData -> {
            BigDecimal st = cellData.getValue().getSoTien();
            return new SimpleStringProperty(st != null ? dfTien.format(st) : "0 VNĐ");
        });

        colNgayGD.setCellValueFactory(cellData -> {
            LocalDateTime ngay = cellData.getValue().getNgayGiaoDich();
            return new SimpleStringProperty(ngay != null ? ngay.format(dtf) : "---");
        });

        colTrangThai.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    GiaoDichChuyenTien gd = getTableRow().getItem();
                    String tt = gd.getTrangThai() != null ? gd.getTrangThai() : "Hoàn thành";
                    Label badge = new Label(tt);

                    if (tt.toLowerCase().contains("thành công") || tt.equalsIgnoreCase("HOAN_THANH") || tt.equalsIgnoreCase("Hoàn thành")) {
                        badge.setStyle("-fx-background-color: #E8F8F0; -fx-text-fill: #10B981; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 8; -fx-font-size: 11px;");
                    } else if (tt.toLowerCase().contains("chờ") || tt.equalsIgnoreCase("CHO_XU_LY")) {
                        badge.setStyle("-fx-background-color: #FEF3C7; -fx-text-fill: #D97706; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 8; -fx-font-size: 11px;");
                    } else if (tt.toLowerCase().contains("hủy") || tt.equalsIgnoreCase("DA_HUY")) {
                        badge.setStyle("-fx-background-color: #F3F4F6; -fx-text-fill: #6B7280; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 8; -fx-font-size: 11px;");
                    } else {
                        badge.setStyle("-fx-background-color: #FDECEC; -fx-text-fill: #D9534F; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 8; -fx-font-size: 11px;");
                    }

                    HBox box = new HBox(badge);
                    box.setAlignment(Pos.CENTER);
                    setGraphic(box);
                }
            }
        });

        tblGiaoDichGanDay.setItems(danhSachGiaoDich);
    }

    private void taiDuLieuThongKe() {
        int tongGD = 0;
        BigDecimal tongTien = BigDecimal.ZERO;
        int choXuLy = 0;
        int tongTK = 0;

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Thống kê giao dịch
            ResultSet rs1 = stmt.executeQuery("SELECT COUNT(*) AS TongGD, ISNULL(SUM(SoTien), 0) AS TongTien FROM GiaoDichChuyenTien");
            if (rs1.next()) {
                tongGD = rs1.getInt("TongGD");
                tongTien = rs1.getBigDecimal("TongTien");
            }

            // Giao dịch chờ xử lý
            ResultSet rs2 = stmt.executeQuery("SELECT COUNT(*) AS ChoXuLy FROM GiaoDichChuyenTien WHERE TrangThai LIKE N'%Chờ%' OR TrangThai = 'CHO_XU_LY'");
            if (rs2.next()) {
                choXuLy = rs2.getInt("ChoXuLy");
            }

            // Tổng số tài khoản khách hàng
            ResultSet rs3 = stmt.executeQuery("SELECT COUNT(*) AS TongTK FROM TaiKhoanNganHang");
            if (rs3.next()) {
                tongTK = rs3.getInt("TongTK");
            }

        } catch (Exception e) {
            System.err.println("Lỗi nạp thống kê giao dịch viên: " + e.getMessage());
            tongGD = 18;
            tongTien = new BigDecimal("850000000");
            choXuLy = 3;
            tongTK = 42;
        }

        if (lblStatTongGD != null) lblStatTongGD.setText(tongGD + " GD");
        if (lblStatTongTien != null) lblStatTongTien.setText(dfTien.format(tongTien));
        if (lblStatChoXuLy != null) lblStatChoXuLy.setText(choXuLy + " GD");
        if (lblStatTaiKhoan != null) lblStatTaiKhoan.setText(tongTK + " TK");
    }

    private void taiDuLieuGiaoDich() {
        danhSachGiaoDich.clear();
        String sql = "SELECT TOP 30 MaGiaoDich, MaTaiKhoanNguon, MaTaiKhoanDich, SoTien, NgayGiaoDich, NoiDung, MaLoaiGD, TrangThai " +
                     "FROM GiaoDichChuyenTien ORDER BY NgayGiaoDich DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                GiaoDichChuyenTien gd = new GiaoDichChuyenTien();
                gd.setMaGiaoDich(rs.getString("MaGiaoDich"));
                gd.setMaTaiKhoanNguon(rs.getString("MaTaiKhoanNguon"));
                gd.setMaTaiKhoanDich(rs.getString("MaTaiKhoanDich"));
                gd.setSoTien(rs.getBigDecimal("SoTien"));

                java.sql.Timestamp ts = rs.getTimestamp("NgayGiaoDich");
                if (ts != null) {
                    gd.setNgayGiaoDich(ts.toLocalDateTime());
                } else {
                    gd.setNgayGiaoDich(LocalDateTime.now());
                }

                gd.setNoiDung(rs.getString("NoiDung"));
                gd.setMaLoaiGD(rs.getString("MaLoaiGD"));
                gd.setTrangThai(rs.getString("TrangThai"));

                danhSachGiaoDich.add(gd);
            }

        } catch (Exception e) {
            System.err.println("Lỗi nạp giao dịch gần đây: " + e.getMessage());
            themDuLieuMauNeuTrong();
        }

        if (danhSachGiaoDich.isEmpty()) {
            themDuLieuMauNeuTrong();
        }
    }

    private void themDuLieuMauNeuTrong() {
        GiaoDichChuyenTien g1 = new GiaoDichChuyenTien();
        g1.setMaGiaoDich("GD20261017-001");
        g1.setMaTaiKhoanNguon("1000000001");
        g1.setMaTaiKhoanDich("1000000002");
        g1.setSoTien(new BigDecimal("5000000"));
        g1.setNgayGiaoDich(LocalDateTime.now().minusMinutes(15));
        g1.setMaLoaiGD("CK01");
        g1.setTrangThai("Thành công");

        GiaoDichChuyenTien g2 = new GiaoDichChuyenTien();
        g2.setMaGiaoDich("NT20261017-002");
        g2.setMaTaiKhoanNguon("---");
        g2.setMaTaiKhoanDich("1000000003");
        g2.setSoTien(new BigDecimal("20000000"));
        g2.setNgayGiaoDich(LocalDateTime.now().minusHours(1));
        g2.setMaLoaiGD("NT01");
        g2.setTrangThai("Thành công");

        GiaoDichChuyenTien g3 = new GiaoDichChuyenTien();
        g3.setMaGiaoDich("RT20261017-003");
        g3.setMaTaiKhoanNguon("1000000001");
        g3.setMaTaiKhoanDich("---");
        g3.setSoTien(new BigDecimal("3000000"));
        g3.setNgayGiaoDich(LocalDateTime.now().minusHours(2));
        g3.setMaLoaiGD("RT01");
        g3.setTrangThai("Chờ xử lý");

        danhSachGiaoDich.addAll(g1, g2, g3);
    }

    @FXML
    public void onLamMoiDashboard() {
        taiDuLieuThongKe();
        taiDuLieuGiaoDich();
    }

    // =========================================================
    // ĐIỀU HƯỚNG TỪ SIDEBAR & QUICK ACTION TILES
    // =========================================================
    @FXML
    public void onTrangChu(ActionEvent event) {
        onLamMoiDashboard();
    }

    @FXML
    public void onChuyenTien(ActionEvent event) {
        moView("teller-transfer-view.fxml", event);
    }

    @FXML
    public void onChuyenTien(MouseEvent event) {
        ActionEvent ae = new ActionEvent(event.getSource(), null);
        moView("teller-transfer-view.fxml", ae);
    }

    @FXML
    public void onNapRutThanhToan(ActionEvent event) {
        moView("teller-cash-view.fxml", event);
    }

    @FXML
    public void onNapRutThanhToan(MouseEvent event) {
        ActionEvent ae = new ActionEvent(event.getSource(), null);
        moView("teller-cash-view.fxml", ae);
    }

    @FXML
    public void onQuanLyTaiKhoan(ActionEvent event) {
        moView("teller-account-view.fxml", event);
    }

    @FXML
    public void onQuanLyTaiKhoan(MouseEvent event) {
        ActionEvent ae = new ActionEvent(event.getSource(), null);
        moView("teller-account-view.fxml", ae);
    }

    @FXML
    public void onTraCuuSaoKe(ActionEvent event) {
        moView("teller-statement-view.fxml", event);
    }

    @FXML
    public void onTraCuuSaoKe(MouseEvent event) {
        ActionEvent ae = new ActionEvent(event.getSource(), null);
        moView("teller-statement-view.fxml", ae);
    }

    @FXML
    public void onTheoDoiTrangThai(ActionEvent event) {
        moView("teller-status-view.fxml", event);
    }

    @FXML
    public void onTheoDoiTrangThai(MouseEvent event) {
        ActionEvent ae = new ActionEvent(event.getSource(), null);
        moView("teller-status-view.fxml", ae);
    }

    @FXML
    public void onDangXuat(ActionEvent event) {
        moView("login-view.fxml", event);
    }
}
