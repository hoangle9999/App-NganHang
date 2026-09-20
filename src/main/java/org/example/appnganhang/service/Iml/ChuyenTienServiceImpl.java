package org.example.appnganhang.service.Iml;

import org.example.appnganhang.dao.ChuyenTienDAO;
import org.example.appnganhang.dao.Impl.ChuyenTienDAOImpl;
import org.example.appnganhang.modell.GiaoDichChuyenTien;
import org.example.appnganhang.service.ChuyenTienService;

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
    public boolean chuyenTien(String maTaiKhoanGui, String maTaiKhoanNhan, BigDecimal soTien) {
        if (maTaiKhoanGui == null
                || maTaiKhoanNhan == null
                || soTien == null) {
            return false;
        }

        if (maTaiKhoanGui.isBlank()
                || maTaiKhoanNhan.isBlank()
                || soTien.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        if (maTaiKhoanGui.equals(maTaiKhoanNhan)) {
            return false;
        }

        if (!kiemTraTrangThaiTaiKhoan(maTaiKhoanGui)) {
            return false;
        }

        if (!kiemTraTrangThaiTaiKhoan(maTaiKhoanNhan)) {
            return false;
        }

        if (!kiemTraSoDu(maTaiKhoanGui, soTien)) {
            return false;
        }

        if (!kiemTraHanMuc(maTaiKhoanGui, soTien)) {
            return false;
        }

        BigDecimal soDuGui = chuyenTienDAO.laySoDu(maTaiKhoanGui);
        BigDecimal soDuNhan = chuyenTienDAO.laySoDu(maTaiKhoanNhan);

        if (soDuGui == null || soDuNhan == null) {
            return false;
        }

        BigDecimal soDuGuiMoi = soDuGui.subtract(soTien);
        BigDecimal soDuNhanMoi = soDuNhan.add(soTien);

        if (!chuyenTienDAO.capNhatSoDu(maTaiKhoanGui, soDuGuiMoi)) {
            return false;
        }

        if (!chuyenTienDAO.capNhatSoDu(maTaiKhoanNhan, soDuNhanMoi)) {
            return false;
        }

        String maGiaoDich = "GD" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 20);

        GiaoDichChuyenTien giaoDich = new GiaoDichChuyenTien();

        giaoDich.setMaGiaoDich(maGiaoDich);
        giaoDich.setMaTaiKhoanNguon(maTaiKhoanGui);
        giaoDich.setMaTaiKhoanDich(maTaiKhoanNhan);
        giaoDich.setSoTien(soTien);
        giaoDich.setNgayGiaoDich(LocalDateTime.now());
        giaoDich.setMaLoaiGD("CHUYEN");
        giaoDich.setTrangThai("Thành công");

        if (!chuyenTienDAO.themGiaoDich(giaoDich)) {
            return false;
        }

        boolean lichSuGui = chuyenTienDAO.themLichSuGiaoDich(
                maGiaoDich,
                maTaiKhoanGui,
                "Chuyển tiền",
                soTien.negate(),
                soDuGuiMoi
        );

        boolean lichSuNhan = chuyenTienDAO.themLichSuGiaoDich(
                maGiaoDich,
                maTaiKhoanNhan,
                "Nhận tiền",
                soTien,
                soDuNhanMoi
        );

        return lichSuGui && lichSuNhan;
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
