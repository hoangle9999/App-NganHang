package org.example.appnganhang.controller.service;

import org.example.appnganhang.model.LoaiGiaoDich;

import java.util.List;

public interface LoaiGiaoDichService {
    boolean themLoaiGiaoDich(LoaiGiaoDich loaiGiaoDich);
    boolean suaLoaiGiaoDich(LoaiGiaoDich loaiGiaoDich);
    boolean xoaLoaiGiaoDich(String maLoaiGD);
    List<LoaiGiaoDich> getDanhSachLoaiGiaoDich();
}
