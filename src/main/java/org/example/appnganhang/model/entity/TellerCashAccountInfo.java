package org.example.appnganhang.model.entity;

import java.math.BigDecimal;

public class TellerCashAccountInfo {
    private String maTaiKhoan;
    private String soTaiKhoan;
    private String tenKhachHang;
    private BigDecimal soDu;
    private String trangThai;
    private String tenChiNhanh;

    public String getMaTaiKhoan() { return maTaiKhoan; }
    public void setMaTaiKhoan(String value) { maTaiKhoan = value; }
    public String getSoTaiKhoan() { return soTaiKhoan; }
    public void setSoTaiKhoan(String value) { soTaiKhoan = value; }
    public String getTenKhachHang() { return tenKhachHang; }
    public void setTenKhachHang(String value) { tenKhachHang = value; }
    public BigDecimal getSoDu() { return soDu; }
    public void setSoDu(BigDecimal value) { soDu = value; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String value) { trangThai = value; }
    public String getTenChiNhanh() { return tenChiNhanh; }
    public void setTenChiNhanh(String value) { tenChiNhanh = value; }
}
