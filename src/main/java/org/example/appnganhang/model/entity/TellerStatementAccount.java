package org.example.appnganhang.model.entity;

import java.math.BigDecimal;

public class TellerStatementAccount {
    private String soTaiKhoan;
    private String tenKhachHang;
    private BigDecimal soDuHienTai;

    public String getSoTaiKhoan() { return soTaiKhoan; }
    public void setSoTaiKhoan(String value) { soTaiKhoan = value; }
    public String getTenKhachHang() { return tenKhachHang; }
    public void setTenKhachHang(String value) { tenKhachHang = value; }
    public BigDecimal getSoDuHienTai() { return soDuHienTai; }
    public void setSoDuHienTai(BigDecimal value) { soDuHienTai = value; }
}
