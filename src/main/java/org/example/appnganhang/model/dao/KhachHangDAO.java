package org.example.appnganhang.model.dao;

import org.example.appnganhang.model.KhachHang;

import java.util.List;
import java.util.Optional;

public interface KhachHangDAO {
    boolean insert(KhachHang kh);
    boolean update(KhachHang kh);
    boolean delete(String MaKh);
    Optional <KhachHang> findById(String MaKh);
    Optional <KhachHang> findByCCCD(String soCCCD);
    Optional <KhachHang> findByPhone(String soDienThoai);
    Optional <KhachHang> findByUsername(String tenDangNhap);
    List <KhachHang> findAll();
}
