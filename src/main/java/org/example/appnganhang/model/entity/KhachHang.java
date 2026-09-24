package org.example.appnganhang.model.entity;

public class KhachHang {
    private String maKhachHang;
    private String hoTen;
    private String soCCCD;
    private String soDienThoai;
    private String email;
    private String tenDangNhap;

    public KhachHang() {}

    public KhachHang(String maKhachHang, String hoTen, String soCCCD, String soDienThoai, String email, String tenDangNhap) {
        this.maKhachHang = maKhachHang;
        this.hoTen = hoTen;
        this.soCCCD = soCCCD;
        this.soDienThoai = soDienThoai;
        this.email = email;
        this.tenDangNhap = tenDangNhap;
    }

    public String getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getSoCCCD() {
        return soCCCD;
    }

    public void setSoCCCD(String soCCCD) {
        this.soCCCD = soCCCD;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }
}
