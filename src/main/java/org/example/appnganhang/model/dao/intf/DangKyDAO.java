package org.example.appnganhang.model.dao.intf;

import org.example.appnganhang.model.entity.KhachHang;

public interface DangKyDAO {
    boolean dangKyKhachHang(
            KhachHang khachHang,
            String matKhau
    );
    boolean taoKhachHangTuNhanVien(
            KhachHang khachHang,
            String matKhau,
            String maChiNhanh
    );
}
