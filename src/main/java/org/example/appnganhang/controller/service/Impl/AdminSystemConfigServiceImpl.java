package org.example.appnganhang.controller.service.Impl;

import org.example.appnganhang.controller.service.intf.AdminSystemConfigService;
import org.example.appnganhang.model.dao.Impl.AdminSystemConfigDAOImpl;
import org.example.appnganhang.model.dao.intf.AdminSystemConfigDAO;
import org.example.appnganhang.model.entity.HanMucGiaoDich;
import org.example.appnganhang.model.entity.LoaiGiaoDich;
import org.example.appnganhang.model.entity.NganHang;
import org.example.appnganhang.model.entity.ThongTinTaiKhoanCauHinh;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class AdminSystemConfigServiceImpl implements AdminSystemConfigService {
    private final AdminSystemConfigDAO dao;

    public AdminSystemConfigServiceImpl() {
        this.dao = new AdminSystemConfigDAOImpl();
    }

    @Override
    public List<HanMucGiaoDich> layDanhSachHanMuc() {
        return dao.layDanhSachHanMuc();
    }

    @Override
    public String taoHanMuc(String ten, BigDecimal amount) {
        batBuoc(ten, "Vui lòng nhập tên hạn mức.");
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Hạn mức ngày phải lớn hơn 0.");
        }
        return dao.taoHanMuc(ten.trim(), amount);
    }

    @Override
    public void capNhatHanMuc(HanMucGiaoDich hanMuc) {
        batBuoc(hanMuc.getMaHanMuc(), "Thiếu mã hạn mức.");
        batBuoc(hanMuc.getTenHanMuc(), "Vui lòng nhập tên hạn mức.");
        if (hanMuc.getHanMucNgay() == null
                || hanMuc.getHanMucNgay().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Hạn mức ngày phải lớn hơn 0.");
        }
        dao.capNhatHanMuc(hanMuc);
    }

    @Override
    public void xoaHanMuc(String maHanMuc) {
        batBuoc(maHanMuc, "Thiếu mã hạn mức.");
        dao.xoaHanMuc(maHanMuc);
    }

    @Override
    public Optional<ThongTinTaiKhoanCauHinh> traCuuTaiKhoan(String keyword) {
        batBuoc(keyword, "Vui lòng nhập số tài khoản hoặc mã khách hàng.");
        return dao.traCuuTaiKhoan(keyword.trim());
    }

    @Override
    public void capNhatHanMucTaiKhoan(String soTaiKhoan, String maHanMuc) {
        batBuoc(soTaiKhoan, "Chưa tra cứu tài khoản.");
        batBuoc(maHanMuc, "Vui lòng chọn gói hạn mức.");
        dao.capNhatHanMucTaiKhoan(soTaiKhoan, maHanMuc);
    }

    @Override
    public String doiTrangThaiTaiKhoan(String soTaiKhoan) {
        batBuoc(soTaiKhoan, "Chưa tra cứu tài khoản.");
        return dao.doiTrangThaiTaiKhoan(soTaiKhoan);
    }

    @Override
    public List<LoaiGiaoDich> layDanhSachLoaiGiaoDich() {
        return dao.layDanhSachLoaiGiaoDich();
    }

    @Override
    public String taoLoaiGiaoDich(String prefix, String ten) {
        batBuoc(prefix, "Vui lòng nhập phần chữ của mã loại giao dịch.");
        batBuoc(ten, "Vui lòng nhập tên loại giao dịch.");

        String normalizedPrefix = prefix.trim().toUpperCase();
        if (!normalizedPrefix.matches("[A-Z]{1,6}")) {
            throw new IllegalArgumentException("Phần mã tự nhập chỉ gồm 1 đến 6 chữ cái A-Z.");
        }
        return dao.taoLoaiGiaoDich(normalizedPrefix, ten.trim());
    }

    @Override
    public void capNhatLoaiGiaoDich(LoaiGiaoDich loaiGiaoDich) {
        batBuoc(loaiGiaoDich.getMaLoaiGD(), "Thiếu mã loại giao dịch.");
        batBuoc(loaiGiaoDich.getTenLoaiGD(), "Vui lòng nhập tên loại giao dịch.");
        dao.capNhatLoaiGiaoDich(loaiGiaoDich);
    }

    @Override
    public void xoaLoaiGiaoDich(String maLoaiGiaoDich) {
        batBuoc(maLoaiGiaoDich, "Thiếu mã loại giao dịch.");
        dao.xoaLoaiGiaoDich(maLoaiGiaoDich);
    }

    @Override
    public List<NganHang> layDanhSachNganHang() {
        return dao.layDanhSachNganHang();
    }

    @Override
    public String doiTrangThaiNganHang(String maNganHang) {
        batBuoc(maNganHang, "Thiếu mã ngân hàng.");
        return dao.doiTrangThaiNganHang(maNganHang);
    }

    private void batBuoc(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
}
