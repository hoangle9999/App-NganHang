package org.example.appnganhang.controller.service.Impl;

import org.example.appnganhang.controller.service.intf.TellerTransferService;
import org.example.appnganhang.model.dao.Impl.TellerTransferDAOImpl;
import org.example.appnganhang.model.dao.intf.TellerTransferDAO;
import org.example.appnganhang.model.entity.LoaiGiaoDich;
import org.example.appnganhang.model.entity.NganHang;
import org.example.appnganhang.model.entity.TellerTransferRecipientInfo;
import org.example.appnganhang.model.entity.TellerTransferResult;
import org.example.appnganhang.model.entity.TellerTransferSourceInfo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class TellerTransferServiceImpl implements TellerTransferService {
    private final TellerTransferDAO dao;

    public TellerTransferServiceImpl() {
        this.dao = new TellerTransferDAOImpl();
    }

    @Override public List<NganHang> layDanhSachNganHangHoatDong() { return dao.layDanhSachNganHangHoatDong(); }
    @Override public List<LoaiGiaoDich> layDanhSachLoaiGiaoDich() { return dao.layDanhSachLoaiGiaoDich(); }
    @Override public Optional<TellerTransferSourceInfo> traCuuTaiKhoanNguon(String stk) {
        if (stk == null || stk.isBlank()) throw new IllegalArgumentException("Vui lòng nhập số tài khoản nguồn cần tra cứu.");
        return dao.traCuuTaiKhoanNguon(stk.trim());
    }
    @Override public TellerTransferRecipientInfo traCuuTaiKhoanDich(String maNganHang, String stk) {
        if (maNganHang == null || maNganHang.isBlank() || stk == null || stk.isBlank())
            throw new IllegalArgumentException("Vui lòng chọn ngân hàng và nhập số tài khoản đích.");
        return dao.traCuuTaiKhoanDich(maNganHang.trim(), stk.trim());
    }
    @Override public TellerTransferResult thucHienChuyenTien(String maGD, String stkNguon, String maNH, String stkDich,
                                                             String tenNguoiNhan, String maLoaiGD, BigDecimal soTien, String noiDung) {
        if (maGD == null || stkNguon == null || maNH == null || stkDich == null || maLoaiGD == null || soTien == null || soTien.signum() <= 0)
            throw new IllegalArgumentException("Thông tin giao dịch chưa đầy đủ hoặc số tiền không hợp lệ.");
        return dao.thucHienChuyenTien(maGD, stkNguon.trim(), maNH.trim(), stkDich.trim(), tenNguoiNhan, maLoaiGD, soTien, noiDung);
    }
}
