package org.example.appnganhang.model.dao.intf;

import org.example.appnganhang.model.entity.LoaiGiaoDich;
import org.example.appnganhang.model.entity.TellerStatementAccount;
import org.example.appnganhang.model.entity.TellerStatementEntry;

import java.util.List;
import java.util.Optional;

public interface TellerStatementDAO {
    Optional<TellerStatementAccount> traCuuTaiKhoan(String soTaiKhoan);
    List<TellerStatementEntry> layLichSu(String soTaiKhoan);
    List<LoaiGiaoDich> layDanhSachLoaiGiaoDich();
}
