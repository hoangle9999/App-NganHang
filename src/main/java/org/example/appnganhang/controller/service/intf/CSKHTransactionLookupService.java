package org.example.appnganhang.controller.service.intf;

import org.example.appnganhang.model.entity.GiaoDichChuyenTien;

import java.time.LocalDate;
import java.util.List;

/** Nghiệp vụ tra cứu giao dịch dành cho nhân viên CSKH. */
public interface CSKHTransactionLookupService {

    List<GiaoDichChuyenTien> layDanhSachGiaoDich();

    List<GiaoDichChuyenTien> timKiemGiaoDich(
            String tuKhoa,
            LocalDate tuNgay,
            LocalDate denNgay,
            String loaiGiaoDich,
            String trangThai);

    String layTenLoaiGiaoDich(String maLoaiGiaoDich);

    String layMaLoaiGiaoDich(String tenLoaiGiaoDich);
}
