package org.example.appnganhang.controller.service.intf;

import org.example.appnganhang.model.entity.GiaoDichChuyenTien;

import java.util.List;

public interface TellerStatusService {
    List<GiaoDichChuyenTien> layDanhSachGiaoDich();
    void duyetGiaoDich(String maGiaoDich);
    void huyGiaoDich(String maGiaoDich, String lyDo);
}
