package org.example.appnganhang.service;

import org.example.appnganhang.modell.HanMucGiaoDich;

import java.math.BigDecimal;
import java.util.Optional;

public interface HanMucService {
    boolean themHanMuc(HanMucGiaoDich hanMuc);
    boolean suaHanMuc(HanMucGiaoDich hanMuc);
    boolean xoaHanMuc(String maHanMuc);
    Optional<HanMucGiaoDich> getHanMuc(String maHanMuc);
    boolean kiemTraHanMuc(String maTaiKhoan, BigDecimal soTien);
}
