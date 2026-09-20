package org.example.appnganhang.service;

import org.example.appnganhang.modell.ChiNhanh;

import java.util.List;
import java.util.Optional;

public interface ChiNhanhService {
    boolean themChiNhanh(ChiNhanh chiNhanh);
    boolean suaChiNhanh(ChiNhanh chiNhanh);
    boolean xoaChiNhanh(String maChiNhanh);
    Optional<ChiNhanh> timKiemChiNhanh(String maChiNhanh);
    List<ChiNhanh> getDanhSachChiNhanh();
}
