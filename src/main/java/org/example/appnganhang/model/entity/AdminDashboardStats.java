package org.example.appnganhang.model.entity;

import java.math.BigDecimal;

public class AdminDashboardStats {

    private int tongTaiKhoan;
    private int tongNhanVien;
    private int tongChiNhanh;
    private int tongGiaoDich;
    private BigDecimal tongTienGiaoDich;
    private int tongCanhBao;

    public AdminDashboardStats(
            int tongTaiKhoan,
            int tongNhanVien,
            int tongChiNhanh,
            int tongGiaoDich,
            BigDecimal tongTienGiaoDich,
            int tongCanhBao) {

        this.tongTaiKhoan = tongTaiKhoan;
        this.tongNhanVien = tongNhanVien;
        this.tongChiNhanh = tongChiNhanh;
        this.tongGiaoDich = tongGiaoDich;
        this.tongTienGiaoDich = tongTienGiaoDich;
        this.tongCanhBao = tongCanhBao;
    }

    public int getTongTaiKhoan() {
        return tongTaiKhoan;
    }

    public int getTongNhanVien() {
        return tongNhanVien;
    }

    public int getTongChiNhanh() {
        return tongChiNhanh;
    }

    public int getTongGiaoDich() {
        return tongGiaoDich;
    }

    public BigDecimal getTongTienGiaoDich() {
        return tongTienGiaoDich;
    }

    public int getTongCanhBao() {
        return tongCanhBao;
    }
}