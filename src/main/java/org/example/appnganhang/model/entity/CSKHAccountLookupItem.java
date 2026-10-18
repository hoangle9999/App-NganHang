package org.example.appnganhang.model.entity;

import java.math.BigDecimal;

/** Dữ liệu tài khoản hiển thị trên màn hình tra cứu CSKH. */
public class CSKHAccountLookupItem {
    private String maTaiKhoan;
    private String soTaiKhoan;
    private String tenChuTK;
    private BigDecimal soDu;
    private String tenNganHang;
    private String chiNhanh;
    private String hanMuc;
    private String trangThai;
    private String soCCCD;
    private String hanMucNgay;

    public CSKHAccountLookupItem(String maTaiKhoan, String soTaiKhoan, String tenChuTK,
                                 BigDecimal soDu, String tenNganHang, String chiNhanh, String hanMuc,
                                 String trangThai, String soCCCD, String hanMucNgay) {
        this.maTaiKhoan = maTaiKhoan;
        this.soTaiKhoan = soTaiKhoan;
        this.tenChuTK = tenChuTK;
        this.soDu = soDu;
        this.tenNganHang = tenNganHang;
        this.chiNhanh = chiNhanh;
        this.hanMuc = hanMuc;
        this.trangThai = trangThai;
        this.soCCCD = soCCCD;
        this.hanMucNgay = hanMucNgay;
    }

    public String getMaTaiKhoan() { return maTaiKhoan; }
    public void setMaTaiKhoan(String maTaiKhoan) { this.maTaiKhoan = maTaiKhoan; }
    public String getSoTaiKhoan() { return soTaiKhoan; }
    public void setSoTaiKhoan(String soTaiKhoan) { this.soTaiKhoan = soTaiKhoan; }
    public String getTenChuTK() { return tenChuTK; }
    public void setTenChuTK(String tenChuTK) { this.tenChuTK = tenChuTK; }
    public BigDecimal getSoDu() { return soDu; }
    public void setSoDu(BigDecimal soDu) { this.soDu = soDu; }
    public String getTenNganHang() { return tenNganHang; }
    public void setTenNganHang(String tenNganHang) { this.tenNganHang = tenNganHang; }
    public String getChiNhanh() { return chiNhanh; }
    public void setChiNhanh(String chiNhanh) { this.chiNhanh = chiNhanh; }
    public String getHanMuc() { return hanMuc; }
    public void setHanMuc(String hanMuc) { this.hanMuc = hanMuc; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public String getSoCCCD() { return soCCCD; }
    public void setSoCCCD(String soCCCD) { this.soCCCD = soCCCD; }
    public String getHanMucNgay() { return hanMucNgay; }
    public void setHanMucNgay(String hanMucNgay) { this.hanMucNgay = hanMucNgay; }
}
