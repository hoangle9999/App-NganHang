package org.example.appnganhang.model.entity;

import java.math.BigDecimal;

public class TellerCashResult {
    private String maGiaoDich;
    private BigDecimal soDuSauGiaoDich;
    private String trangThai;

    public String getMaGiaoDich() { return maGiaoDich; }
    public void setMaGiaoDich(String value) { maGiaoDich = value; }
    public BigDecimal getSoDuSauGiaoDich() { return soDuSauGiaoDich; }
    public void setSoDuSauGiaoDich(BigDecimal value) { soDuSauGiaoDich = value; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String value) { trangThai = value; }
}
