package org.example.appnganhang.controller.service.Iml;

import org.example.appnganhang.controller.service.intf.CSKHCustomerService;
import org.example.appnganhang.model.dao.Impl.CSKHCustomerDAOImpl;
import org.example.appnganhang.model.dao.intf.CSKHCustomerDAO;
import org.example.appnganhang.model.entity.CSKHCustomerAccountItem;
import org.example.appnganhang.model.entity.KhachHang;

import java.sql.SQLException;
import java.util.List;

/** Lớp nghiệp vụ cho màn hình quản lý khách hàng CSKH. */
public class CSKHCustomerServiceImpl implements CSKHCustomerService {
    private final CSKHCustomerDAO customerDAO;

    public CSKHCustomerServiceImpl() {
        this(new CSKHCustomerDAOImpl());
    }

    public CSKHCustomerServiceImpl(CSKHCustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    @Override public List<KhachHang> layDanhSachKhachHang() throws SQLException {
        return customerDAO.layDanhSachKhachHang();
    }
    @Override public boolean tonTaiCCCD(String soCCCD) throws SQLException {
        return customerDAO.tonTaiCCCD(soCCCD);
    }
    @Override public boolean tonTaiSoDienThoai(String soDienThoai, String maKhachHangBoQua) throws SQLException {
        return customerDAO.tonTaiSoDienThoai(soDienThoai, maKhachHangBoQua);
    }
    @Override public void themKhachHang(KhachHang khachHang, String matKhau) throws SQLException {
        customerDAO.themKhachHang(khachHang, matKhau);
    }
    @Override public void capNhatKhachHang(KhachHang khachHang) throws SQLException {
        customerDAO.capNhatKhachHang(khachHang);
    }
    @Override public List<CSKHCustomerAccountItem> layTaiKhoanTheoKhachHang(String maKhachHang) throws SQLException {
        return customerDAO.layTaiKhoanTheoKhachHang(maKhachHang);
    }
}