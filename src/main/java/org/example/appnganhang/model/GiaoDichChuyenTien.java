package org.example.appnganhang.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class GiaoDichChuyenTien {
    private String maGiaoDich;
    private String maTaiKhoanNguon;
    private String maTaiKhoanDich;
    private BigDecimal soTien;
    private LocalDateTime ngayGiaoDich;
    private String noiDung;
    private String maLoaiGD;
    private String trangThai;

    public String getMaGiaoDich() {
        return maGiaoDich;
    }

    public void setMaGiaoDich(String maGiaoDich) {
        this.maGiaoDich = maGiaoDich;
    }

    public String getMaTaiKhoanNguon() {
        return maTaiKhoanNguon;
    }

    public void setMaTaiKhoanNguon(String maTaiKhoanNguon) {
        this.maTaiKhoanNguon = maTaiKhoanNguon;
    }

    public String getMaTaiKhoanDich() {
        return maTaiKhoanDich;
    }

    public void setMaTaiKhoanDich(String maTaiKhoanDich) {
        this.maTaiKhoanDich = maTaiKhoanDich;
    }

    public BigDecimal getSoTien() {
        return soTien;
    }

    public void setSoTien(BigDecimal soTien) {
        this.soTien = soTien;
    }

    public LocalDateTime getNgayGiaoDich() {
        return ngayGiaoDich;
    }

    public void setNgayGiaoDich(LocalDateTime ngayGiaoDich) {
        this.ngayGiaoDich = ngayGiaoDich;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public String getMaLoaiGD() {
        return maLoaiGD;
    }

    public void setMaLoaiGD(String maLoaiGD) {
        this.maLoaiGD = maLoaiGD;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}
