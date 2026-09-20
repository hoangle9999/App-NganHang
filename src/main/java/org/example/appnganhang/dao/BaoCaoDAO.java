package org.example.appnganhang.dao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface BaoCaoDAO {
    int demTongSoGiaoDich();
    BigDecimal tinhTongTienChuyen();
    int demGiaoDichThanhCong();
    int demGiaoDichThatBai();
    Map<LocalDate, Integer> thongKeGiaoDichTheoNgay();
    Map<String, Integer> thongKeGiaoDichTheoLoai();
    Map<String, Integer> thongKeGiaoDichTheoChiNhanh();
    List<Map<String, Object>> baoCaoGiaoDich();
}
