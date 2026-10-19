package org.example.appnganhang.model.entity;

import java.math.BigDecimal;

public class TellerAccountInfo {
    private String maTaiKhoan, soTaiKhoan, tenKhachHang, soCCCD, soDienThoai, email, diaChi;
    private BigDecimal soDu;
    private String tenNganHang, tenChiNhanh, tenHanMuc, trangThai;

    public String getMaTaiKhoan() { return maTaiKhoan; }
    public void setMaTaiKhoan(String v) { maTaiKhoan = v; }
    public String getSoTaiKhoan() { return soTaiKhoan; }
    public void setSoTaiKhoan(String v) { soTaiKhoan = v; }
    public String getTenKhachHang() { return tenKhachHang; }
    public void setTenKhachHang(String v) { tenKhachHang = v; }
    public String getSoCCCD() { return soCCCD; }
    public void setSoCCCD(String v) { soCCCD = v; }
    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String v) { soDienThoai = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { email = v; }
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String v) { diaChi = v; }
    public BigDecimal getSoDu() { return soDu; }
    public void setSoDu(BigDecimal v) { soDu = v; }
    public String getTenNganHang() { return tenNganHang; }
    public void setTenNganHang(String v) { tenNganHang = v; }
    public String getTenChiNhanh() { return tenChiNhanh; }
    public void setTenChiNhanh(String v) { tenChiNhanh = v; }
    public String getTenHanMuc() { return tenHanMuc; }
    public void setTenHanMuc(String v) { tenHanMuc = v; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String v) { trangThai = v; }
}
