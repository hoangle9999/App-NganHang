package org.example.appnganhang.model.entity;

import java.math.BigDecimal;

/** Thông tin tài khoản ngân hàng liên kết với khách hàng trong màn hình CSKH. */
public class CSKHCustomerAccountItem {
    private final String soTaiKhoan;
    private final BigDecimal soDu;
    private final String trangThai;
    private final String tenChiNhanh;

    public CSKHCustomerAccountItem(String soTaiKhoan, BigDecimal soDu, String trangThai, String tenChiNhanh) {
        this.soTaiKhoan = soTaiKhoan;
        this.soDu = soDu;
        this.trangThai = trangThai;
        this.tenChiNhanh = tenChiNhanh;
    }

    public String getSoTaiKhoan() { return soTaiKhoan; }
    public BigDecimal getSoDu() { return soDu; }
    public String getTrangThai() { return trangThai; }
    public String getTenChiNhanh() { return tenChiNhanh; }
}