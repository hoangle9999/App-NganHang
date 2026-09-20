package org.example.appnganhang.modell;

public class TaiKhoanDangNhap_VaiTro {
    private String tenDangNhap;
    private String matKhau;
    private String roleName;
    private String maNguoiDung;

    public TaiKhoanDangNhap_VaiTro() {}

    public TaiKhoanDangNhap_VaiTro(String tenDangNhap, String matKhau, String roleName) {
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.roleName = roleName;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getMaNguoiDung() {
        return maNguoiDung;
    }

    public void setMaNguoiDung(String maNguoiDung) {
        this.maNguoiDung = maNguoiDung;
    }
}
