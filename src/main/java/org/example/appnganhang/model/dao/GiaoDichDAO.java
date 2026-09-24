package org.example.appnganhang.model.dao;

import org.example.appnganhang.model.entity.GiaoDichChuyenTien;
import org.example.appnganhang.model.entity.LichSuGiaoDich;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface GiaoDichDAO {
    Optional<GiaoDichChuyenTien> traCuuTheoMa(String maGiaoDich);
    List<GiaoDichChuyenTien> traCuuTheoTaiKhoan(String maTaiKhoan);
    List<GiaoDichChuyenTien> traCuuTheoKhoangThoiGian(LocalDateTime tuThoiGian, LocalDateTime denThoiGian);
    List<GiaoDichChuyenTien> traCuuTheoTrangThai(String trangThai);
    List<GiaoDichChuyenTien> traCuuTheoLoai(String maLoaiGD);
    List<LichSuGiaoDich> xemLichSuGiaoDich(String maTaiKhoan);
}
