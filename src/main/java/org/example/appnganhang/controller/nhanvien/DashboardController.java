package org.example.appnganhang.controller.nhanvien;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.example.appnganhang.model.dao.DashboardDAO;
import org.example.appnganhang.model.entity.DashboardDB;

import javafx.scene.control.Label;
import java.text.NumberFormat;
import java.util.Locale;

import static org.example.appnganhang.controller.util.Navigation.moView;

public class DashboardController {
    @FXML private Label lblTitleCard1;
    @FXML private Label lblValueCard1;
    @FXML private Label lblSubCard1;

    @FXML private Label lblTitleCard2;
    @FXML private Label lblValueCard2;
    @FXML private Label lblSubCard2;

    @FXML private Label lblTitleCard3;
    @FXML private Label lblValueCard3;
    @FXML private Label lblSubCard3;

    private String currentUser;
    private String roleName;
    private String maNguoiDung;

    private DashboardDAO dashboardDAO = new DashboardDAO();

    @FXML
    private void initialize() {
        loadDashboardData(null);
    }

    public void initData(String username) {
        this.currentUser = username;
    }

    public void initData(String username, String roleName, String maNguoiDung) {
        this.currentUser = username;
        this.roleName = roleName;
        this.maNguoiDung = maNguoiDung;
    }

    private void loadDashboardData(String maChiNhanh) {
        DashboardDB dashboardDB = dashboardDAO.getDashboardStats(maChiNhanh);

        if (dashboardDB != null) {
            // Định dạng tiền tệ VNĐ
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

            // --- Set Card 1 ---
            lblTitleCard1.setText("Tổng khách hàng");
            lblValueCard1.setText(String.valueOf(dashboardDB.getTongKhachHang()));
            lblSubCard1.setText(dashboardDB.getTongTaiKhoanActive() + " Tài khoản hoạt động");

            // --- Set Card 2 ---
            lblTitleCard2.setText("Tổng số dư hệ thống");
            lblValueCard2.setText(formatter.format(dashboardDB.getTongSoDu()));
            lblSubCard2.setText("Số dư toàn hệ thống");

            // --- Set Card 3 ---
            lblTitleCard3.setText("Giao dịch hôm nay");
            lblValueCard3.setText(dashboardDB.getSoGiaoDichHomNay() + " giao dịch");
            lblSubCard3.setText("Tổng: " + formatter.format(dashboardDB.getTongTienGiaoDichHomNay()));
        }
    }

    // =========================================================
    // SIDEBAR
    // =========================================================

    @FXML
    private void onTrangChu(ActionEvent event){
        // Đang ở trang chủ
    }

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
        moView("transaction-type-view.fxml", event);
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
        moView("report-view.fxml", event);
    }

    @FXML
    private void onDoiMatKhau(ActionEvent event) {
        moView("change-password-view.fxml", event);
    }

    @FXML
    private void onDangXuat(ActionEvent event) {
        moView("login-view.fxml", event);
    }
}