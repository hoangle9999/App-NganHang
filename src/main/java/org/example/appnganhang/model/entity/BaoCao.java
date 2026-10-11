package org.example.appnganhang.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BaoCao {

    private String maGiaoDich;
    private String taiKhoanNguon;
    private String taiKhoanDich;
    private BigDecimal soTien;
    private LocalDateTime ngayGiaoDich;
    private String loaiGiaoDich;
    private String trangThai;

    public BaoCao() {
    }

    public String getMaGiaoDich() {
        return maGiaoDich;
    }

    public void setMaGiaoDich(String maGiaoDich) {
        this.maGiaoDich = maGiaoDich;
    }

    public String getTaiKhoanNguon() {
        return taiKhoanNguon;
    }

    public void setTaiKhoanNguon(String taiKhoanNguon) {
        this.taiKhoanNguon = taiKhoanNguon;
    }

    public String getTaiKhoanDich() {
        return taiKhoanDich;
    }

    public void setTaiKhoanDich(String taiKhoanDich) {
        this.taiKhoanDich = taiKhoanDich;
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

    public String getLoaiGiaoDich() {
        return loaiGiaoDich;
    }

    public void setLoaiGiaoDich(String loaiGiaoDich) {
        this.loaiGiaoDich = loaiGiaoDich;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}

