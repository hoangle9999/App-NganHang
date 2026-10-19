package org.example.appnganhang.model.entity;

import java.math.BigDecimal;

public class TellerDashboardStats {
    private int soGiaoDichHomNay;
    private BigDecimal tongTienHomNay = BigDecimal.ZERO;
    private int soGiaoDichChoXuLy;
    private int soTaiKhoanKhachHang;

    public int getSoGiaoDichHomNay() { return soGiaoDichHomNay; }
    public void setSoGiaoDichHomNay(int value) { soGiaoDichHomNay = value; }
    public BigDecimal getTongTienHomNay() { return tongTienHomNay; }
    public void setTongTienHomNay(BigDecimal value) { tongTienHomNay = value == null ? BigDecimal.ZERO : value; }
    public int getSoGiaoDichChoXuLy() { return soGiaoDichChoXuLy; }
    public void setSoGiaoDichChoXuLy(int value) { soGiaoDichChoXuLy = value; }
    public int getSoTaiKhoanKhachHang() { return soTaiKhoanKhachHang; }
    public void setSoTaiKhoanKhachHang(int value) { soTaiKhoanKhachHang = value; }
}
