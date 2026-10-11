package org.example.appnganhang.controller.service.intf;

import java.math.BigDecimal;

public interface ChuyenTienService {
    boolean chuyenTien(
            String soTaiKhoanGui,
            String soTaiKhoanNhan,
            String maNganHangNhan,
            BigDecimal soTien,
            String noiDung
    );
    boolean kiemTraSoDu(String maTaiKhoan, BigDecimal soTien);
    boolean kiemTraHanMuc(String maTaiKhoan, BigDecimal soTien);
    boolean kiemTraTrangThaiTaiKhoan(String maTaiKhoan);
    boolean huyGiaoDich(String maGiaoDich);
}
