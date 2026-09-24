package org.example.appnganhang.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LichSuGiaoDich {
    private Integer maLog;
    private String maGiaoDich;
    private String soTaiKhoanLienQuan;
    private String hanhDong;
    private BigDecimal soTienThayDoi;
    private BigDecimal soDuSauThayDoi;
    private LocalDateTime thoiGianGhiLog;

    public Integer getMaLog() {
        return maLog;
    }

    public void setMaLog(Integer maLog) {
        this.maLog = maLog;
    }

    public String getMaGiaoDich() {
        return maGiaoDich;
    }

    public void setMaGiaoDich(String maGiaoDich) {
        this.maGiaoDich = maGiaoDich;
    }

    public String getSoTaiKhoanLienQuan() {
        return soTaiKhoanLienQuan;
    }

    public void setSoTaiKhoanLienQuan(String soTaiKhoanLienQuan) {
        this.soTaiKhoanLienQuan = soTaiKhoanLienQuan;
    }

    public String getHanhDong() {
        return hanhDong;
    }

    public void setHanhDong(String hanhDong) {
        this.hanhDong = hanhDong;
    }

    public BigDecimal getSoTienThayDoi() {
        return soTienThayDoi;
    }

    public void setSoTienThayDoi(BigDecimal soTienThayDoi) {
        this.soTienThayDoi = soTienThayDoi;
    }

    public BigDecimal getSoDuSauThayDoi() {
        return soDuSauThayDoi;
    }

    public void setSoDuSauThayDoi(BigDecimal soDuSauThayDoi) {
        this.soDuSauThayDoi = soDuSauThayDoi;
    }

    public LocalDateTime getThoiGianGhiLog() {
        return thoiGianGhiLog;
    }

    public void setThoiGianGhiLog(LocalDateTime thoiGianGhiLog) {
        this.thoiGianGhiLog = thoiGianGhiLog;
    }
}
