package org.example.appnganhang.controller.service;

import java.math.BigDecimal;

public interface ChuyenTienService {
    boolean chuyenTien(String maTaiKhoanGui, String maTaiKhoanNhan, BigDecimal soTien);
    boolean kiemTraSoDu(String maTaiKhoan, BigDecimal soTien);
    boolean kiemTraHanMuc(String maTaiKhoan, BigDecimal soTien);
    boolean kiemTraTrangThaiTaiKhoan(String maTaiKhoan);
    boolean huyGiaoDich(String maGiaoDich);
}
