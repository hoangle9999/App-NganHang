package org.example.appnganhang.model.entity;

public class DashboardDB {
    private int tongKhachHang;
    private int tongTaiKhoanActive;
    private double tongSoDu;
    private int soGiaoDichHomNay;
    private double tongTienGiaoDichHomNay;

    // Constructor, Getters & Setters
    public DashboardDB(int tongKhachHang, int tongTaiKhoanActive, double tongSoDu, int soGiaoDichHomNay, double tongTienGiaoDichHomNay) {
        this.tongKhachHang = tongKhachHang;
        this.tongTaiKhoanActive = tongTaiKhoanActive;
        this.tongSoDu = tongSoDu;
        this.soGiaoDichHomNay = soGiaoDichHomNay;
        this.tongTienGiaoDichHomNay = tongTienGiaoDichHomNay;
    }

    public int getTongKhachHang() { return tongKhachHang; }
    public int getTongTaiKhoanActive() { return tongTaiKhoanActive; }
    public double getTongSoDu() { return tongSoDu; }
    public int getSoGiaoDichHomNay() { return soGiaoDichHomNay; }
    public double getTongTienGiaoDichHomNay() { return tongTienGiaoDichHomNay; }
}
