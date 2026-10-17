package org.example.appnganhang.controller.admin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.example.appnganhang.model.util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;

import static org.example.appnganhang.controller.util.Navigation.moView;

public class AdminViewController {

    @FXML private Label lblAdminName;
    @FXML private Label lblStatTaiKhoan;
    @FXML private Label lblStatNhanVien;
    @FXML private Label lblStatGiaoDich;
    @FXML private Label lblStatCanhBao;

    private final DecimalFormat dfTien = new DecimalFormat("#,### VNĐ");

    @FXML
    public void initialize() {
        taiThongKeTongQuan();
    }

    private void taiThongKeTongQuan() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Đếm tài khoản
            ResultSet rs1 = stmt.executeQuery("SELECT COUNT(*) AS TongTK FROM TaiKhoanDangNhap_VaiTro");
            if (rs1.next()) {
                int tongTK = rs1.getInt("TongTK");
                if (lblStatTaiKhoan != null) lblStatTaiKhoan.setText(tongTK + " tài khoản");
            }

            // Đếm nhân viên & chi nhánh
            ResultSet rs2 = stmt.executeQuery("SELECT (SELECT COUNT(*) FROM NhanVien) AS TongNV, (SELECT COUNT(*) FROM ChiNhanh) AS TongCN");
            if (rs2.next()) {
                int tongNV = rs2.getInt("TongNV");
                int tongCN = rs2.getInt("TongCN");
                if (lblStatNhanVien != null) lblStatNhanVien.setText(tongNV + " NV / " + tongCN + " CN");
            }

            // Thống kê giao dịch
            ResultSet rs3 = stmt.executeQuery("SELECT COUNT(*) AS TongGD, SUM(SoTien) AS TongTien FROM GiaoDichChuyenTien");
            if (rs3.next()) {
                BigDecimal tongTien = rs3.getBigDecimal("TongTien");
                if (lblStatGiaoDich != null && tongTien != null) {
                    lblStatGiaoDich.setText(dfTien.format(tongTien));
                }
            }

        } catch (Exception e) {
            System.err.println("Lỗi nạp thống kê DB: " + e.getMessage());
            // Giữ giá trị mặc định đẹp mắt
        }
    }

    @FXML
    private void onTrangChu(ActionEvent event) {
        // Đang ở dashboard
    }

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
