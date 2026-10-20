package org.example.appnganhang.controller.service.Impl;

import org.example.appnganhang.controller.service.intf.TellerCashService;
import org.example.appnganhang.model.dao.Impl.TellerCashDAOImpl;
import org.example.appnganhang.model.dao.intf.TellerCashDAO;
import org.example.appnganhang.model.entity.NganHang;
import org.example.appnganhang.model.entity.TellerCashAccountInfo;
import org.example.appnganhang.model.entity.TellerCashResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class TellerCashServiceImpl implements TellerCashService {
    private final TellerCashDAO dao;

    public TellerCashServiceImpl() {
        this.dao = new TellerCashDAOImpl();
    }

    @Override public List<NganHang> layDanhSachNganHangNoiBo() { return dao.layDanhSachNganHangNoiBo(); }

    @Override public Optional<TellerCashAccountInfo> traCuuTaiKhoan(String stk) {
        if (stk == null || stk.isBlank()) throw new IllegalArgumentException("Vui lòng nhập số tài khoản cần tra cứu.");
        return dao.traCuuTaiKhoan(stk.trim());
    }

    @Override public TellerCashResult thucHienGiaoDich(String maGD, String stk, String maLoaiGD,
                                                        BigDecimal soTien, String noiDung) {
        if (maGD == null || maGD.isBlank() || stk == null || stk.isBlank()
                || soTien == null || soTien.signum() <= 0
                || maLoaiGD == null || !List.of("NT01", "RT01", "TT01").contains(maLoaiGD)) {
            throw new IllegalArgumentException("Thông tin giao dịch chưa đầy đủ hoặc số tiền không hợp lệ.");
        }
        return dao.thucHienGiaoDich(maGD, stk.trim(), maLoaiGD, soTien, noiDung);
    }
}
