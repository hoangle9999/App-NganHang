package org.example.appnganhang.model.dao.intf;

import org.example.appnganhang.model.entity.GiaoDichChuyenTien;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/** Các truy vấn dữ liệu phục vụ màn hình tra cứu giao dịch CSKH. */
public interface CSKHTransactionLookupDAO {

    List<GiaoDichChuyenTien> layDanhSachGiaoDich() throws SQLException;

    List<GiaoDichChuyenTien> timKiemGiaoDich(
            String tuKhoa,
            LocalDate tuNgay,
            LocalDate denNgay,
            String maLoaiGiaoDich,
            String trangThai) throws SQLException;

    String layTenLoaiGiaoDich(String maLoaiGiaoDich) throws SQLException;

    String layMaLoaiGiaoDich(String tenLoaiGiaoDich) throws SQLException;
}