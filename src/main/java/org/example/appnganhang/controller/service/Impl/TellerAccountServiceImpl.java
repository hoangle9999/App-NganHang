package org.example.appnganhang.controller.service.Impl;

import org.example.appnganhang.controller.service.intf.TellerAccountService;
import org.example.appnganhang.model.dao.Impl.TellerAccountDAOImpl;
import org.example.appnganhang.model.dao.intf.TellerAccountDAO;
import org.example.appnganhang.model.entity.ChiNhanh;
import org.example.appnganhang.model.entity.HanMucGiaoDich;
import org.example.appnganhang.model.entity.KhachHang;
import org.example.appnganhang.model.entity.TaiKhoanNganHang;
import org.example.appnganhang.model.entity.TellerAccountInfo;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class TellerAccountServiceImpl implements TellerAccountService {
    private static final Set<String> TRANG_THAI_HOP_LE = Set.of("Hoạt động", "Bị khóa", "Đóng");
    private final TellerAccountDAO dao = new TellerAccountDAOImpl();

    @Override public List<TellerAccountInfo> layDanhSachTaiKhoan() { return dao.layDanhSachTaiKhoan(); }
    @Override public List<ChiNhanh> layDanhSachChiNhanh() { return dao.layDanhSachChiNhanh(); }
    @Override public List<HanMucGiaoDich> layDanhSachHanMuc() { return dao.layDanhSachHanMuc(); }
    @Override public Optional<KhachHang> timKhachHangTheoCCCD(String cccd) {
        if (cccd == null || cccd.isBlank()) throw new IllegalArgumentException("Vui lòng nhập CCCD.");
        return dao.timKhachHangTheoCCCD(cccd.trim());
    }
    @Override public String taoHoSoKhachHang(String username, String password, String name, String cccd,
                                              String phone, String email, String address) {
        if (username == null || username.isBlank() || password == null || password.isBlank()
                || name == null || name.isBlank() || cccd == null || phone == null || phone.isBlank())
            throw new IllegalArgumentException("Vui lòng nhập đủ tên đăng nhập, mật khẩu, họ tên, CCCD và số điện thoại.");
        return dao.taoHoSoKhachHang(username.trim(), password, name.trim(), cccd.trim(),
                phone.trim(), email, address);
    }
    @Override public String taoMaTaiKhoan() { return dao.taoMaTaiKhoan(); }
    @Override public boolean moTaiKhoan(TaiKhoanNganHang account) {
        if (account == null || account.getMaKhachHang() == null || account.getSoTaiKhoan() == null
                || account.getMaChiNhanh() == null || account.getMaHanMuc() == null)
            throw new IllegalArgumentException("Thông tin mở tài khoản chưa đầy đủ.");
        return dao.moTaiKhoan(account);
    }
    @Override public void doiTrangThai(String maTaiKhoan, String status) {
        if (maTaiKhoan == null || maTaiKhoan.isBlank() || !TRANG_THAI_HOP_LE.contains(status))
            throw new IllegalArgumentException("Trạng thái tài khoản không hợp lệ.");
        dao.doiTrangThai(maTaiKhoan, status);
    }
    @Override public void ganHanMuc(String maTaiKhoan, String maHanMuc) {
        if (maTaiKhoan == null || maTaiKhoan.isBlank() || maHanMuc == null || maHanMuc.isBlank())
            throw new IllegalArgumentException("Vui lòng chọn tài khoản và hạn mức.");
        dao.ganHanMuc(maTaiKhoan, maHanMuc);
    }
}
