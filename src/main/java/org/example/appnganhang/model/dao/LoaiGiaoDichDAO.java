package org.example.appnganhang.model.dao;

import org.example.appnganhang.model.entity.LoaiGiaoDich;

import java.util.List;

public interface LoaiGiaoDichDAO {
    boolean themLoaiGiaoDich(LoaiGiaoDich loaiGiaoDich);
    boolean suaLoaiGiaoDich(LoaiGiaoDich loaiGiaoDich);
    boolean xoaLoaiGiaoDich(String maLoaiGD);
    List<LoaiGiaoDich> getDanhSachLoaiGiaoDich();
}
