package org.example.appnganhang.controller.service.Impl;

import org.example.appnganhang.controller.service.intf.CSKHTransactionLookupService;
import org.example.appnganhang.model.dao.Impl.CSKHTransactionLookupDAOImpl;
import org.example.appnganhang.model.dao.intf.CSKHTransactionLookupDAO;
import org.example.appnganhang.model.entity.GiaoDichChuyenTien;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/** Triển khai nghiệp vụ tra cứu giao dịch CSKH. */
public class CSKHTransactionLookupServiceImpl implements CSKHTransactionLookupService {

    private final CSKHTransactionLookupDAO transactionDAO;

    public CSKHTransactionLookupServiceImpl() {
        this(new CSKHTransactionLookupDAOImpl());
    }

    public CSKHTransactionLookupServiceImpl(CSKHTransactionLookupDAO transactionDAO) {
        this.transactionDAO = transactionDAO;
    }

    @Override
    public List<GiaoDichChuyenTien> layDanhSachGiaoDich() {
        try {
            return transactionDAO.layDanhSachGiaoDich();
        } catch (SQLException e) {
            throw new IllegalStateException("Không thể tải danh sách giao dịch.", e);
        }
    }

    @Override
    public List<GiaoDichChuyenTien> timKiemGiaoDich(
            String tuKhoa, LocalDate tuNgay, LocalDate denNgay,
            String loaiGiaoDich, String trangThai) {
        String maLoaiGiaoDich = null;
        try {
            if (loaiGiaoDich != null && !loaiGiaoDich.isBlank()
                    && !loaiGiaoDich.equals("Tất cả loại GD")) {
                maLoaiGiaoDich = transactionDAO.layMaLoaiGiaoDich(loaiGiaoDich);
            }
            String locTrangThai = (trangThai == null || trangThai.isBlank()
                    || trangThai.equals("Tất cả trạng thái")) ? null : trangThai;
            return transactionDAO.timKiemGiaoDich(
                    tuKhoa, tuNgay, denNgay, maLoaiGiaoDich, locTrangThai);
        } catch (SQLException e) {
            throw new IllegalStateException("Không thể tra cứu giao dịch.", e);
        }
    }

    @Override
    public String layTenLoaiGiaoDich(String maLoaiGiaoDich) {
        try {
            return transactionDAO.layTenLoaiGiaoDich(maLoaiGiaoDich);
        } catch (SQLException e) {
            throw new IllegalStateException("Không thể tra cứu tên loại giao dịch.", e);
        }
    }

    @Override
    public String layMaLoaiGiaoDich(String tenLoaiGiaoDich) {
        try {
            return transactionDAO.layMaLoaiGiaoDich(tenLoaiGiaoDich);
        } catch (SQLException e) {
            throw new IllegalStateException("Không thể tra cứu mã loại giao dịch.", e);
        }
    }
}
