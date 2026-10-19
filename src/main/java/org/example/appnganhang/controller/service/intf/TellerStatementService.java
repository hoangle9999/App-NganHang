package org.example.appnganhang.controller.service.intf;

import org.example.appnganhang.model.entity.LoaiGiaoDich;
import org.example.appnganhang.model.entity.TellerStatementData;

import java.time.LocalDate;
import java.util.List;

public interface TellerStatementService {
    List<LoaiGiaoDich> layDanhSachLoaiGiaoDich();
    TellerStatementData traCuuSaoKe(String soTaiKhoan, LocalDate tuNgay, LocalDate denNgay, String maLoaiGD);
}
