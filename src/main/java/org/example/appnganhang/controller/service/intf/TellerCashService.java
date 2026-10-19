package org.example.appnganhang.controller.service.intf;

import org.example.appnganhang.model.entity.NganHang;
import org.example.appnganhang.model.entity.TellerCashAccountInfo;
import org.example.appnganhang.model.entity.TellerCashResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface TellerCashService {
    List<NganHang> layDanhSachNganHangNoiBo();
    Optional<TellerCashAccountInfo> traCuuTaiKhoan(String soTaiKhoan);
    TellerCashResult thucHienGiaoDich(String maGiaoDich, String soTaiKhoan, String maLoaiGD,
                                      BigDecimal soTien, String noiDung);
}
