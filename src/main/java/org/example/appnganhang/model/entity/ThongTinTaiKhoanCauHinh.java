package org.example.appnganhang.model.entity;

import java.math.BigDecimal;

public class ThongTinTaiKhoanCauHinh {
    private String soTaiKhoan;
    private String tenKhachHang;
    private BigDecimal soDu;
    private String tenChiNhanh;
    private String trangThai;
    private String maHanMuc;
    private String tenHanMuc;
    private BigDecimal hanMucNgay;

    public String getSoTaiKhoan() { return soTaiKhoan; }
    public void setSoTaiKhoan(String soTaiKhoan) { this.soTaiKhoan = soTaiKhoan; }
    public String getTenKhachHang() { return tenKhachHang; }
    public void setTenKhachHang(String tenKhachHang) { this.tenKhachHang = tenKhachHang; }
    public BigDecimal getSoDu() { return soDu; }
    public void setSoDu(BigDecimal soDu) { this.soDu = soDu; }
    public String getTenChiNhanh() { return tenChiNhanh; }
    public void setTenChiNhanh(String tenChiNhanh) { this.tenChiNhanh = tenChiNhanh; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public String getMaHanMuc() { return maHanMuc; }
    public void setMaHanMuc(String maHanMuc) { this.maHanMuc = maHanMuc; }
    public String getTenHanMuc() { return tenHanMuc; }
    public void setTenHanMuc(String tenHanMuc) { this.tenHanMuc = tenHanMuc; }
    public BigDecimal getHanMucNgay() { return hanMucNgay; }
    public void setHanMucNgay(BigDecimal hanMucNgay) { this.hanMucNgay = hanMucNgay; }
}
