package org.example.appnganhang.controller.service.Iml;

import org.example.appnganhang.model.dao.intf.ChuyenTienDAO;
import org.example.appnganhang.model.dao.Impl.ChuyenTienDAOImpl;
import org.example.appnganhang.model.entity.GiaoDichChuyenTien;
import org.example.appnganhang.controller.service.intf.ChuyenTienService;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class ChuyenTienServiceImpl implements ChuyenTienService {
   private final ChuyenTienDAO chuyenTienDAO;

    public ChuyenTienServiceImpl() {
        this.chuyenTienDAO = new ChuyenTienDAOImpl();
    }


    @Override
    public boolean chuyenTien(
            String soTaiKhoanGui,
            String soTaiKhoanNhan,
            String maNganHangNhan,
            BigDecimal soTien,
            String noiDung) {

        // 1. Kiểm tra dữ liệu đầu vào.
        if (soTaiKhoanGui == null
                || soTaiKhoanNhan == null
                || maNganHangNhan == null
                || soTien == null) {
            return false;
        }

        if (soTaiKhoanGui.isBlank()
                || soTaiKhoanNhan.isBlank()
                || maNganHangNhan.isBlank()
                || soTien.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        // 2. Chỉ xử lý chuyển tiền nội bộ SMART BANK.
        if (!"SMART".equalsIgnoreCase(maNganHangNhan)) {
            return false;
        }

        // 3. Không cho phép chuyển tiền cho chính mình.
        if (soTaiKhoanGui.equals(soTaiKhoanNhan)) {
            return false;
        }

        // 4. Tạo thông tin giao dịch.
        GiaoDichChuyenTien giaoDich = new GiaoDichChuyenTien();

        giaoDich.setMaTaiKhoanNguon(soTaiKhoanGui);
        giaoDich.setMaTaiKhoanDich(soTaiKhoanNhan);
        giaoDich.setSoTien(soTien);
        giaoDich.setNoiDung(noiDung);
        giaoDich.setNgayGiaoDich(LocalDateTime.now());
        giaoDich.setMaLoaiGD("CK01");
        giaoDich.setTrangThai("Chờ xử lý");

        // 5. DAO thực hiện toàn bộ giao dịch trong transaction SQL.
        return chuyenTienDAO.thucHienChuyenTien(
                giaoDich,
                maNganHangNhan
        );
    }

    @Override
    public boolean kiemTraSoDu(String maTaiKhoan, BigDecimal soTien) {
        if (maTaiKhoan == null
                || soTien == null
                || soTien.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        BigDecimal soDu = chuyenTienDAO.laySoDu(maTaiKhoan);

        if (soDu == null) {
            return false;
        }

        return soDu.compareTo(soTien) >= 0;
    }

    @Override
    public boolean kiemTraHanMuc(String maTaiKhoan, BigDecimal soTien) {
        if (maTaiKhoan == null
                || soTien == null
                || soTien.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        BigDecimal hanMucNgay = chuyenTienDAO.layHanMucNgay(maTaiKhoan);

        if (hanMucNgay == null) {
            return false;
        }

        BigDecimal tongTienDaGiaoDich = chuyenTienDAO.layTongTienGiaoDichTrongNgay(maTaiKhoan);

        if (tongTienDaGiaoDich == null) {
            tongTienDaGiaoDich = BigDecimal.ZERO;
        }

        BigDecimal tongTienSauGiaoDich = tongTienDaGiaoDich.add(soTien);

        return tongTienSauGiaoDich.compareTo(hanMucNgay) <= 0;
    }

    @Override
    public boolean kiemTraTrangThaiTaiKhoan(String maTaiKhoan) {
        if (maTaiKhoan == null || maTaiKhoan.isBlank()) {
            return false;
        }

        String trangThai = chuyenTienDAO.layTrangThaiTaiKhoan(maTaiKhoan);

        if (trangThai == null) {
            return false;
        }

        return trangThai.equals("Hoạt động");
    }

    // cho phép hủy giao dịch khi đang chờ xử lý
    @Override
    public boolean huyGiaoDich(String maGiaoDich) {
        if (maGiaoDich == null || maGiaoDich.isBlank()) {
            return false;
        }

        Optional<GiaoDichChuyenTien> optional = chuyenTienDAO.timTheoMa(maGiaoDich);

        if (optional.isEmpty()) {
            return false;
        }

        GiaoDichChuyenTien giaoDich = optional.get();

        if (!"Chờ xử lý".equals(giaoDich.getTrangThai())) {
            return false;
        }

        return chuyenTienDAO.capNhatTrangThaiGiaoDich(
                maGiaoDich,
                "Đã hủy"
        );
    }
}
