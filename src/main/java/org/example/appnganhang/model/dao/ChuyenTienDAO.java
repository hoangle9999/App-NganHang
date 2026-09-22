package org.example.appnganhang.model.dao;

import org.example.appnganhang.model.GiaoDichChuyenTien;

import java.math.BigDecimal;
import java.util.Optional;

public interface ChuyenTienDAO {
    BigDecimal laySoDu(String maTaiKhoan);
    String layTrangThaiTaiKhoan(String maTaiKhoan);
    BigDecimal layHanMucNgay(String maTaiKhoan);
    BigDecimal layTongTienGiaoDichTrongNgay(String maTaiKhoan);
    boolean capNhatSoDu(String maTaiKhoan, BigDecimal soDu);
    boolean themGiaoDich(GiaoDichChuyenTien giaoDich);
    boolean capNhatTrangThaiGiaoDich(String maGiaoDich, String trangThai);
    Optional<GiaoDichChuyenTien> timTheoMa(String maGiaoDich);
    boolean themLichSuGiaoDich(String maGiaoDich, String soTaiKhoan, String hanhDong, BigDecimal soTienThayDoi, BigDecimal soDuSauThayDoi);
}
