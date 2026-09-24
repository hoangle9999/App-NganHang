package org.example.appnganhang.model.entity;

import java.math.BigDecimal;

public class HanMucGiaoDich {
    private String maHanMuc;
    private String tenHanMuc;
    private BigDecimal hanMucNgay;

    public String getMaHanMuc() {
        return maHanMuc;
    }

    public void setMaHanMuc(String maHanMuc) {
        this.maHanMuc = maHanMuc;
    }

    public String getTenHanMuc() {
        return tenHanMuc;
    }

    public void setTenHanMuc(String tenHanMuc) {
        this.tenHanMuc = tenHanMuc;
    }

    public BigDecimal getHanMucNgay() {
        return hanMucNgay;
    }

    public void setHanMucNgay(BigDecimal hanMucNgay) {
        this.hanMucNgay = hanMucNgay;
    }
}
