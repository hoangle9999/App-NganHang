package org.example.appnganhang.model.entity;

import java.math.BigDecimal;

public class TellerTransferResult {
    private String maGiaoDich;
    private String trangThai;
    private BigDecimal soDuNguonSauGiaoDich;
    private BigDecimal hanMucConLai;

    public String getMaGiaoDich() { return maGiaoDich; }
    public void setMaGiaoDich(String value) { maGiaoDich = value; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String value) { trangThai = value; }
    public BigDecimal getSoDuNguonSauGiaoDich() { return soDuNguonSauGiaoDich; }
    public void setSoDuNguonSauGiaoDich(BigDecimal value) { soDuNguonSauGiaoDich = value; }
    public BigDecimal getHanMucConLai() { return hanMucConLai; }
    public void setHanMucConLai(BigDecimal value) { hanMucConLai = value; }
}
