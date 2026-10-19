package org.example.appnganhang.controller.service.intf;

import org.example.appnganhang.model.entity.LoaiGiaoDich;
import org.example.appnganhang.model.entity.NganHang;
import org.example.appnganhang.model.entity.TellerTransferRecipientInfo;
import org.example.appnganhang.model.entity.TellerTransferResult;
import org.example.appnganhang.model.entity.TellerTransferSourceInfo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface TellerTransferService {
    List<NganHang> layDanhSachNganHangHoatDong();
    List<LoaiGiaoDich> layDanhSachLoaiGiaoDich();
    Optional<TellerTransferSourceInfo> traCuuTaiKhoanNguon(String soTaiKhoan);
    TellerTransferRecipientInfo traCuuTaiKhoanDich(String maNganHang, String soTaiKhoan);
    TellerTransferResult thucHienChuyenTien(String maGiaoDich, String soTaiKhoanNguon, String maNganHangDich,
                                            String soTaiKhoanDich, String tenNguoiNhan, String maLoaiGD,
                                            BigDecimal soTien, String noiDung);
}
