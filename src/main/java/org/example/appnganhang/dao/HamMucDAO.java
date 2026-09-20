package org.example.appnganhang.dao;

import org.example.appnganhang.modell.HanMucGiaoDich;

import java.math.BigDecimal;
import java.util.Optional;

public interface HamMucDAO {
    boolean themHanMuc(HanMucGiaoDich hanMuc);
    boolean suaHanMuc(HanMucGiaoDich hanMuc);
    boolean xoaHanMuc(String maHanMuc);
    Optional<HanMucGiaoDich> getHanMuc(String maHanMuc);
    boolean kiemTraHanMuc(String maTaiKhoan, BigDecimal soTien);
}
