package org.example.appnganhang.model.dao.intf;

import org.example.appnganhang.model.entity.KhachHang;

import java.util.List;
import java.util.Optional;

public interface KhachHangDAO {
    String taoMaKhachHang();
    boolean insert(KhachHang kh);
    boolean update(KhachHang kh);
    boolean delete(String MaKh);
    Optional <KhachHang> findById(String MaKh);
    Optional <KhachHang> findByCCCD(String soCCCD);
    Optional <KhachHang> findByPhone(String soDienThoai);
    Optional <KhachHang> findByUsername(String tenDangNhap);
    List<KhachHang> findByHoTen(String hoTen);
    List<KhachHang> findByEmail(String email);
    List<KhachHang> findByDiaChi(String diaChi);
    List<KhachHang> timKiemKhachHang(
            String maKhachHang,
            String hoTen,
            String soCCCD,
            String soDienThoai,
            String email,
            String diaChi,
            String tenDangNhap
    );
    List <KhachHang> findAll();
}
