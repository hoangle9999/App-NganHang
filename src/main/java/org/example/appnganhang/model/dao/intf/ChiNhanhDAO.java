package org.example.appnganhang.model.dao.intf;

import org.example.appnganhang.model.entity.ChiNhanh;

import java.util.List;
import java.util.Optional;

public interface ChiNhanhDAO {
    boolean themChiNhanh(ChiNhanh chiNhanh);
    boolean suaChiNhanh(ChiNhanh chiNhanh);
    boolean xoaChiNhanh(String maChiNhanh);
    Optional<ChiNhanh> timKiemChiNhanhTheoMa(String maChiNhanh);
    List<ChiNhanh> timKiemChiNhanhTheoTen(String tenChiNhanh);
    List<ChiNhanh> timKiemChiNhanhTheoDiaChi(String diaChi);
    List<ChiNhanh> timKiemChiNhanh(
            String maChiNhanh,
            String tenChiNhanh,
            String diaChi
    );
    List<ChiNhanh> getDanhSachChiNhanh();

}
