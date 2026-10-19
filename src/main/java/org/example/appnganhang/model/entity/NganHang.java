package org.example.appnganhang.model.entity;

public class NganHang {
    private String maNganHang;
    private String tenNganHang;
    private String trangThai;

    public NganHang() {}
    public NganHang(String maNganHang, String tenNganHang, String trangThai) {
        this.maNganHang = maNganHang;
        this.tenNganHang = tenNganHang;
        this.trangThai = trangThai;
    }
    public String getMaNganHang() { return maNganHang; }
    public void setMaNganHang(String maNganHang) { this.maNganHang = maNganHang; }
    public String getTenNganHang() { return tenNganHang; }
    public void setTenNganHang(String tenNganHang) { this.tenNganHang = tenNganHang; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}
