package org.example.appnganhang.controller.service.intf;

import org.example.appnganhang.model.entity.CSKHCustomerAccountItem;
import org.example.appnganhang.model.entity.KhachHang;

import java.sql.SQLException;
import java.util.List;

public interface CSKHCustomerService {
    List<KhachHang> layDanhSachKhachHang() throws SQLException;
    boolean tonTaiCCCD(String soCCCD) throws SQLException;
    boolean tonTaiSoDienThoai(String soDienThoai, String maKhachHangBoQua) throws SQLException;
    void themKhachHang(KhachHang khachHang, String matKhau) throws SQLException;
    void capNhatKhachHang(KhachHang khachHang) throws SQLException;
    List<CSKHCustomerAccountItem> layTaiKhoanTheoKhachHang(String maKhachHang) throws SQLException;
}
