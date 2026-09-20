package org.example.appnganhang.dao;

import org.example.appnganhang.modell.LoaiGiaoDich;

import java.util.List;

public interface LoaiGiaoDichDAO {
    boolean themLoaiGiaoDich(LoaiGiaoDich loaiGiaoDich);
    boolean suaLoaiGiaoDich(LoaiGiaoDich loaiGiaoDich);
    boolean xoaLoaiGiaoDich(String maLoaiGD);
    List<LoaiGiaoDich> getDanhSachLoaiGiaoDich();
}
