package org.example.appnganhang.model.entity;

import java.math.BigDecimal;
import java.util.List;

public class TellerStatementData {
    private TellerStatementAccount taiKhoan;
    private List<TellerStatementEntry> giaoDich;
    private BigDecimal soDuDauKy;
    private BigDecimal soDuCuoiKy;
    private BigDecimal tongPhatSinhCo;
    private BigDecimal tongPhatSinhNo;

    public TellerStatementAccount getTaiKhoan() { return taiKhoan; }
    public void setTaiKhoan(TellerStatementAccount value) { taiKhoan = value; }
    public List<TellerStatementEntry> getGiaoDich() { return giaoDich; }
    public void setGiaoDich(List<TellerStatementEntry> value) { giaoDich = value; }
    public BigDecimal getSoDuDauKy() { return soDuDauKy; }
    public void setSoDuDauKy(BigDecimal value) { soDuDauKy = value; }
    public BigDecimal getSoDuCuoiKy() { return soDuCuoiKy; }
    public void setSoDuCuoiKy(BigDecimal value) { soDuCuoiKy = value; }
    public BigDecimal getTongPhatSinhCo() { return tongPhatSinhCo; }
    public void setTongPhatSinhCo(BigDecimal value) { tongPhatSinhCo = value; }
    public BigDecimal getTongPhatSinhNo() { return tongPhatSinhNo; }
    public void setTongPhatSinhNo(BigDecimal value) { tongPhatSinhNo = value; }
}
