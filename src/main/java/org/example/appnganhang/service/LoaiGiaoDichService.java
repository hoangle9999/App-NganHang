package org.example.appnganhang.service;

import org.example.appnganhang.modell.LoaiGiaoDich;

import java.util.List;

public interface LoaiGiaoDichService {
    boolean themLoaiGiaoDich(LoaiGiaoDich loaiGiaoDich);
    boolean suaLoaiGiaoDich(LoaiGiaoDich loaiGiaoDich);
    boolean xoaLoaiGiaoDich(String maLoaiGD);
    List<LoaiGiaoDich> getDanhSachLoaiGiaoDich();
}
