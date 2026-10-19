package org.example.appnganhang.controller.service.Impl;

import org.example.appnganhang.controller.service.intf.TellerStatusService;
import org.example.appnganhang.model.dao.Impl.TellerStatusDAOImpl;
import org.example.appnganhang.model.dao.intf.TellerStatusDAO;
import org.example.appnganhang.model.entity.GiaoDichChuyenTien;

import java.util.List;

public class TellerStatusServiceImpl implements TellerStatusService {
    private final TellerStatusDAO dao = new TellerStatusDAOImpl();

    @Override public List<GiaoDichChuyenTien> layDanhSachGiaoDich() {
        return dao.layDanhSachGiaoDich();
    }

    @Override public void duyetGiaoDich(String maGiaoDich) {
        if (maGiaoDich == null || maGiaoDich.isBlank())
            throw new IllegalArgumentException("Mã giao dịch không hợp lệ.");
        dao.duyetGiaoDich(maGiaoDich.trim());
    }

    @Override public void huyGiaoDich(String maGiaoDich, String lyDo) {
        if (maGiaoDich == null || maGiaoDich.isBlank())
            throw new IllegalArgumentException("Mã giao dịch không hợp lệ.");
        if (lyDo == null || lyDo.isBlank())
            throw new IllegalArgumentException("Vui lòng nhập lý do từ chối/hủy giao dịch.");
        dao.huyGiaoDich(maGiaoDich.trim(), lyDo.trim());
    }
}
