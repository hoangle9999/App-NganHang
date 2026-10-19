package org.example.appnganhang.controller.service.intf;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.example.appnganhang.model.entity.GiaoDichChuyenTien;
import org.example.appnganhang.model.entity.LichSuGiaoDich;

public interface BaoCaoService {
    List<GiaoDichChuyenTien> layDanhSachGiaoDich();
    List<LichSuGiaoDich> layDanhSachLichSuGiaoDich();
    List<String> layDanhSachLoaiGiaoDich();
    List<String> layDanhSachNganHang();
}
