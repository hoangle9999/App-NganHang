package org.example.appnganhang.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TellerStatementEntry {
    private String maGiaoDich;
    private String soTaiKhoanLienQuan;
    private String maLoaiGD;
    private String tenLoaiGD;
    private BigDecimal soTienThayDoi;
    private BigDecimal soDuSauThayDoi;
    private String noiDung;
    private LocalDateTime thoiGian;

    public String getMaGiaoDich() { return maGiaoDich; }
    public void setMaGiaoDich(String value) { maGiaoDich = value; }
    public String getSoTaiKhoanLienQuan() { return soTaiKhoanLienQuan; }
    public void setSoTaiKhoanLienQuan(String value) { soTaiKhoanLienQuan = value; }
    public String getMaLoaiGD() { return maLoaiGD; }
    public void setMaLoaiGD(String value) { maLoaiGD = value; }
    public String getTenLoaiGD() { return tenLoaiGD; }
    public void setTenLoaiGD(String value) { tenLoaiGD = value; }
    public BigDecimal getSoTienThayDoi() { return soTienThayDoi; }
    public void setSoTienThayDoi(BigDecimal value) { soTienThayDoi = value; }
    public BigDecimal getSoDuSauThayDoi() { return soDuSauThayDoi; }
    public void setSoDuSauThayDoi(BigDecimal value) { soDuSauThayDoi = value; }
    public String getNoiDung() { return noiDung; }
    public void setNoiDung(String value) { noiDung = value; }
    public LocalDateTime getThoiGian() { return thoiGian; }
    public void setThoiGian(LocalDateTime value) { thoiGian = value; }
}
