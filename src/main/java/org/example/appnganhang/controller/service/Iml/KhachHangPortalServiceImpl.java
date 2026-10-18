package org.example.appnganhang.controller.service.Iml;

import org.example.appnganhang.controller.service.intf.KhachHangPortalService;
import org.example.appnganhang.controller.service.intf.KhachHangPortalService;
import org.example.appnganhang.model.dao.intf.KhachHangPortalDAO;
import org.example.appnganhang.model.dao.Impl.KhachHangPortalDAOImpl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Service cho cổng khách hàng; controller không thao tác JDBC trực tiếp. */
public class KhachHangPortalServiceImpl implements KhachHangPortalService {
    private final KhachHangPortalDAO dao = new KhachHangPortalDAOImpl();
    public Optional<Map<String,Object>> timHoSo(String ma, String ten) { return dao.timHoSo(ma, ten); }
    public Optional<Map<String,Object>> timHoSoCoTaiKhoanDauTien() { return dao.timHoSoCoTaiKhoanDauTien(); }
    public List<Map<String,Object>> layTaiKhoanKhachHang(String ma, String ten) { return dao.layTaiKhoanKhachHang(ma, ten); }
    public List<String> laySoTaiKhoanKhachHang(String ma, String ten) { return dao.laySoTaiKhoanKhachHang(ma, ten); }
    public BigDecimal laySoDu(String stk) { return dao.laySoDu(stk); }
    public List<Map<String,Object>> layBienDongSoDu(String stk) { return dao.layBienDongSoDu(stk); }
    public List<Map<String,Object>> layGiaoDich(String stk) { return dao.layGiaoDich(stk); }
}