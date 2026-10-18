package org.example.appnganhang.model.dao.intf;

import org.example.appnganhang.model.entity.LichSuGiaoDich;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface CSKHBalanceHistoryDAO {
    List<LichSuGiaoDich> layDanhSachLog(String tuKhoa, LocalDate tuNgay, LocalDate denNgay) throws SQLException;
    String layTenChuTaiKhoan(String soTaiKhoan) throws SQLException;
}