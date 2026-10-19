package org.example.appnganhang.model.dao.intf;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.example.appnganhang.model.entity.GiaoDichChuyenTien;
import org.example.appnganhang.model.entity.LichSuGiaoDich;

public interface BaoCaoDAO {
    List<GiaoDichChuyenTien> layDanhSachGiaoDich();
    List<LichSuGiaoDich> layDanhSachLichSuGiaoDich();
    List<String> layDanhSachLoaiGiaoDich();
    List<String> layDanhSachNganHang();
}
