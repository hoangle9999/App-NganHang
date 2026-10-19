package org.example.appnganhang.model.dao.intf;

import org.example.appnganhang.model.entity.NganHang;
import org.example.appnganhang.model.entity.TellerCashAccountInfo;
import org.example.appnganhang.model.entity.TellerCashResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface TellerCashDAO {
    List<NganHang> layDanhSachNganHangNoiBo();
    Optional<TellerCashAccountInfo> traCuuTaiKhoan(String soTaiKhoan);
    TellerCashResult thucHienGiaoDich(String maGiaoDich, String soTaiKhoan, String maLoaiGD,
                                      BigDecimal soTien, String noiDung);
}
