package org.example.appnganhang.model.dao.intf;

import org.example.appnganhang.model.entity.GiaoDichChuyenTien;

import java.util.List;

public interface TellerStatusDAO {
    List<GiaoDichChuyenTien> layDanhSachGiaoDich();
    void duyetGiaoDich(String maGiaoDich);
    void huyGiaoDich(String maGiaoDich, String lyDo);
}
