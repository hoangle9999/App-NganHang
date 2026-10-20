package org.example.appnganhang.controller.service.Impl;

import org.example.appnganhang.controller.service.intf.KhachHangPortalService;
import org.example.appnganhang.model.dao.Impl.KhachHangPortalDAOImpl;
import org.example.appnganhang.model.dao.intf.KhachHangPortalDAO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class KhachHangPortalServiceImpl implements KhachHangPortalService {
    private final KhachHangPortalDAO dao;

    public KhachHangPortalServiceImpl() {
        this.dao = new KhachHangPortalDAOImpl();
    }

    public Optional<Map<String, Object>> timHoSo(String ma, String ten) { return dao.timHoSo(ma, ten); }
    public List<Map<String, Object>> layTaiKhoanKhachHang(String ma, String ten) { return dao.layTaiKhoanKhachHang(ma, ten); }
    public List<String> laySoTaiKhoanKhachHang(String ma, String ten) { return dao.laySoTaiKhoanKhachHang(ma, ten); }
    public BigDecimal laySoDu(String stk, String ma, String ten) { return dao.laySoDu(stk, ma, ten); }
    public List<Map<String, Object>> layBienDongSoDu(String stk, String ma, String ten) { return dao.layBienDongSoDu(stk, ma, ten); }
    public List<Map<String, Object>> layGiaoDich(String stk, String ma, String ten) { return dao.layGiaoDich(stk, ma, ten); }
}
