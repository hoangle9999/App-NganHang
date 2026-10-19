package org.example.appnganhang.model.entity;

import java.math.BigDecimal;

public class TellerTransferSourceInfo {
    private String maTaiKhoan;
    private String soTaiKhoan;
    private String tenChuTaiKhoan;
    private String soDienThoai;
    private String trangThai;
    private BigDecimal soDu = BigDecimal.ZERO;
    private BigDecimal hanMucNgay = BigDecimal.ZERO;
    private BigDecimal daChuyenHomNay = BigDecimal.ZERO;
    private BigDecimal hanMucConLai = BigDecimal.ZERO;

    public String getMaTaiKhoan() { return maTaiKhoan; }
    public void setMaTaiKhoan(String value) { maTaiKhoan = value; }
    public String getSoTaiKhoan() { return soTaiKhoan; }
    public void setSoTaiKhoan(String value) { soTaiKhoan = value; }
    public String getTenChuTaiKhoan() { return tenChuTaiKhoan; }
    public void setTenChuTaiKhoan(String value) { tenChuTaiKhoan = value; }
    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String value) { soDienThoai = value; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String value) { trangThai = value; }
    public BigDecimal getSoDu() { return soDu; }
    public void setSoDu(BigDecimal value) { soDu = value == null ? BigDecimal.ZERO : value; }
    public BigDecimal getHanMucNgay() { return hanMucNgay; }
    public void setHanMucNgay(BigDecimal value) { hanMucNgay = value == null ? BigDecimal.ZERO : value; }
    public BigDecimal getDaChuyenHomNay() { return daChuyenHomNay; }
    public void setDaChuyenHomNay(BigDecimal value) { daChuyenHomNay = value == null ? BigDecimal.ZERO : value; }
    public BigDecimal getHanMucConLai() { return hanMucConLai; }
    public void setHanMucConLai(BigDecimal value) { hanMucConLai = value == null ? BigDecimal.ZERO : value; }
}
