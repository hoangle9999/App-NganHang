package org.example.appnganhang.model.entity;

public class LoaiGiaoDich {
    private String maLoaiGD;
    private String tenLoaiGD;
    private String maGiaoDich;
    private String noiDung;
    private String trangThai;

    public String getMaLoaiGD() {
        return maLoaiGD;
    }

    public void setMaLoaiGD(String maLoaiGD) {
        this.maLoaiGD = maLoaiGD;
    }

    public String getTenLoaiGD() {
        return tenLoaiGD;
    }

    public void setTenLoaiGD(String tenLoaiGD) {
        this.tenLoaiGD = tenLoaiGD;
    }

    public void setMaGiaoDich(String maGiaoDich) {
        this.maGiaoDich = maGiaoDich;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getMaGiaoDich() {
        return maGiaoDich;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public String getTrangThai() {
        return trangThai;
    }
}
