package org.example.appnganhang.model.dao;

import org.example.appnganhang.model.ChiNhanh;

import java.util.List;
import java.util.Optional;

public interface ChiNhanhDAO {
    boolean themChiNhanh(ChiNhanh chiNhanh);
    boolean suaChiNhanh(ChiNhanh chiNhanh);
    boolean xoaChiNhanh(String maChiNhanh);
    Optional<ChiNhanh> timKiemChiNhanh(String maChiNhanh);
    List<ChiNhanh> getDanhSachChiNhanh();
}
