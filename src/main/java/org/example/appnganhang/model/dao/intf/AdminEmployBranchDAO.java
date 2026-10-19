package org.example.appnganhang.model.dao.intf;

import org.example.appnganhang.model.entity.ChiNhanh;
import org.example.appnganhang.model.entity.NhanVien;

import java.util.List;

public interface AdminEmployBranchDAO {
    List<ChiNhanh> layDanhSachChiNhanh();
    List<NhanVien> layDanhSachNhanVien();
    List<String> layTaiKhoanNhanVienChuaGan(String maNhanVien);
    String themNhanVien(NhanVien nhanVien);
    void capNhatNhanVien(NhanVien nhanVien);
    void xoaNhanVien(String maNhanVien);
    void lienKetTaiKhoanNhanVien(String maNhanVien, String tenDangNhap);
    String themChiNhanh(ChiNhanh chiNhanh);
    void capNhatChiNhanh(ChiNhanh chiNhanh);
    void xoaChiNhanh(String maChiNhanh);
}
