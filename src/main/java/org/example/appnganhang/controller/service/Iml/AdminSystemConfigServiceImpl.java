package org.example.appnganhang.controller.service.Iml;

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
    private final AdminSystemConfigDAO dao = new AdminSystemConfigDAOImpl();
    @Override public List<HanMucGiaoDich> layDanhSachHanMuc() { return dao.layDanhSachHanMuc(); }
    @Override public String taoHanMuc(String ten, BigDecimal amount) {
        batBuoc(ten, "Vui lòng nhập tên hạn mức.");
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Hạn mức ngày phải lớn hơn 0.");
        return dao.taoHanMuc(ten.trim(), amount);
    }
    @Override public void capNhatHanMuc(HanMucGiaoDich hm) {
        batBuoc(hm.getMaHanMuc(), "Thiếu mã hạn mức."); batBuoc(hm.getTenHanMuc(), "Vui lòng nhập tên hạn mức.");
        if (hm.getHanMucNgay() == null || hm.getHanMucNgay().compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Hạn mức ngày phải lớn hơn 0.");
        dao.capNhatHanMuc(hm);
    }
    @Override public void xoaHanMuc(String ma) { batBuoc(ma, "Thiếu mã hạn mức."); dao.xoaHanMuc(ma); }
    @Override public Optional<ThongTinTaiKhoanCauHinh> traCuuTaiKhoan(String keyword) { batBuoc(keyword, "Vui lòng nhập số tài khoản hoặc mã khách hàng."); return dao.traCuuTaiKhoan(keyword.trim()); }
    @Override public void capNhatHanMucTaiKhoan(String account, String limit) { batBuoc(account, "Chưa tra cứu tài khoản."); batBuoc(limit, "Vui lòng chọn gói hạn mức."); dao.capNhatHanMucTaiKhoan(account, limit); }
    @Override public String doiTrangThaiTaiKhoan(String account) { batBuoc(account, "Chưa tra cứu tài khoản."); return dao.doiTrangThaiTaiKhoan(account); }
    @Override public List<LoaiGiaoDich> layDanhSachLoaiGiaoDich() { return dao.layDanhSachLoaiGiaoDich(); }
    @Override public String taoLoaiGiaoDich(String prefix, String ten) {
        batBuoc(prefix, "Vui lòng nhập phần chữ của mã loại giao dịch."); batBuoc(ten, "Vui lòng nhập tên loại giao dịch.");
        String normalized = prefix.trim().toUpperCase();
        if (!normalized.matches("[A-Z]{1,6}")) throw new IllegalArgumentException("Phần mã tự nhập chỉ gồm 1 đến 6 chữ cái A-Z.");
        return dao.taoLoaiGiaoDich(normalized, ten.trim());
    }
    @Override public void capNhatLoaiGiaoDich(LoaiGiaoDich x) { batBuoc(x.getMaLoaiGD(), "Thiếu mã loại giao dịch."); batBuoc(x.getTenLoaiGD(), "Vui lòng nhập tên loại giao dịch."); dao.capNhatLoaiGiaoDich(x); }
    @Override public void xoaLoaiGiaoDich(String ma) { batBuoc(ma, "Thiếu mã loại giao dịch."); dao.xoaLoaiGiaoDich(ma); }
    @Override public List<NganHang> layDanhSachNganHang() { return dao.layDanhSachNganHang(); }
    @Override public String doiTrangThaiNganHang(String ma) { batBuoc(ma, "Thiếu mã ngân hàng."); return dao.doiTrangThaiNganHang(ma); }
    private void batBuoc(String value, String message) { if (value == null || value.isBlank()) throw new IllegalArgumentException(message); }
}
