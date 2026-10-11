package org.example.appnganhang.model.dao.intf;

import org.example.appnganhang.model.entity.HanMucGiaoDich;

import java.util.List;
import java.util.Optional;

public interface HanMucDAO {
    boolean suaHanMuc(HanMucGiaoDich hanMuc);
    boolean xoaHanMuc(String maHanMuc);
    Optional<HanMucGiaoDich> getHanMuc(String maHanMuc);
    List<HanMucGiaoDich> getDanhSachHanMuc();
}
