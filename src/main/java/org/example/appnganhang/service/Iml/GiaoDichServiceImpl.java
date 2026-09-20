package org.example.appnganhang.service.Iml;

import org.example.appnganhang.dao.GiaoDichDAO;
import org.example.appnganhang.dao.Impl.GiaoDichDAOImpl;
import org.example.appnganhang.modell.GiaoDichChuyenTien;
import org.example.appnganhang.modell.LichSuGiaoDich;
import org.example.appnganhang.service.GiaoDichService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class GiaoDichServiceImpl implements GiaoDichService {
    private final GiaoDichDAO giaoDichDAO;

    public GiaoDichServiceImpl() {
        this.giaoDichDAO = new GiaoDichDAOImpl();
    }
    @Override
    public Optional<GiaoDichChuyenTien> traCuuTheoMa(String maGiaoDich) {
        return giaoDichDAO.traCuuTheoMa(maGiaoDich);
    }

    @Override
    public List<GiaoDichChuyenTien> traCuuTheoTaiKhoan(String maTaiKhoan) {
        return giaoDichDAO.traCuuTheoTaiKhoan(maTaiKhoan);
    }

    @Override
    public List<GiaoDichChuyenTien> traCuuTheoKhoangThoiGian(LocalDateTime tuThoiGian, LocalDateTime denThoiGian) {
        return giaoDichDAO.traCuuTheoKhoangThoiGian(tuThoiGian, denThoiGian);
    }

    @Override
    public List<GiaoDichChuyenTien> traCuuTheoTrangThai(String trangThai) {
        return giaoDichDAO.traCuuTheoTrangThai(trangThai);
    }

    @Override
    public List<GiaoDichChuyenTien> traCuuTheoLoai(String maLoaiGD) {
        return giaoDichDAO.traCuuTheoLoai(maLoaiGD);
    }

    @Override
    public List<LichSuGiaoDich> xemLichSuGiaoDich(String maTaiKhoan) {
        return giaoDichDAO.xemLichSuGiaoDich(maTaiKhoan);
    }
}
