package org.example.appnganhang.model.dao.intf;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Truy vấn dữ liệu chỉ đọc dành cho các màn hình khách hàng. */
public interface KhachHangPortalDAO {
    Optional<Map<String, Object>> timHoSo(String maKhachHang, String tenDangNhap);
    List<Map<String, Object>> layTaiKhoanKhachHang(String maKhachHang, String tenDangNhap);
    List<String> laySoTaiKhoanKhachHang(String maKhachHang, String tenDangNhap);
    BigDecimal laySoDu(String soTaiKhoan);
    List<Map<String, Object>> layBienDongSoDu(String soTaiKhoan);
    List<Map<String, Object>> layGiaoDich(String soTaiKhoan);
}
