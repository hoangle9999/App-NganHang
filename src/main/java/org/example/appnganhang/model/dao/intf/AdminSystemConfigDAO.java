package org.example.appnganhang.model.dao.intf;

import org.example.appnganhang.model.entity.HanMucGiaoDich;
import org.example.appnganhang.model.entity.LoaiGiaoDich;
import org.example.appnganhang.model.entity.NganHang;
import org.example.appnganhang.model.entity.ThongTinTaiKhoanCauHinh;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AdminSystemConfigDAO {
    List<HanMucGiaoDich> layDanhSachHanMuc();
    String taoHanMuc(String ten, BigDecimal hanMucNgay);
    void capNhatHanMuc(HanMucGiaoDich hanMuc);
    void xoaHanMuc(String maHanMuc);
    Optional<ThongTinTaiKhoanCauHinh> traCuuTaiKhoan(String tuKhoa);
    void capNhatHanMucTaiKhoan(String soTaiKhoan, String maHanMuc);
    String doiTrangThaiTaiKhoan(String soTaiKhoan);
    List<LoaiGiaoDich> layDanhSachLoaiGiaoDich();
    String taoLoaiGiaoDich(String tienTo, String ten);
    void capNhatLoaiGiaoDich(LoaiGiaoDich loaiGiaoDich);
    void xoaLoaiGiaoDich(String maLoaiGD);
    List<NganHang> layDanhSachNganHang();
    String doiTrangThaiNganHang(String maNganHang);
}
