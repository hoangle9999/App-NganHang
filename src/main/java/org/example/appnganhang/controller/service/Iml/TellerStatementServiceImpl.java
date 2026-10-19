package org.example.appnganhang.controller.service.Iml;

import org.example.appnganhang.controller.service.intf.TellerStatementService;
import org.example.appnganhang.model.dao.Impl.TellerStatementDAOImpl;
import org.example.appnganhang.model.dao.intf.TellerStatementDAO;
import org.example.appnganhang.model.entity.LoaiGiaoDich;
import org.example.appnganhang.model.entity.TellerStatementAccount;
import org.example.appnganhang.model.entity.TellerStatementData;
import org.example.appnganhang.model.entity.TellerStatementEntry;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TellerStatementServiceImpl implements TellerStatementService {
    private final TellerStatementDAO dao = new TellerStatementDAOImpl();

    @Override public List<LoaiGiaoDich> layDanhSachLoaiGiaoDich() { return dao.layDanhSachLoaiGiaoDich(); }

    @Override
    public TellerStatementData traCuuSaoKe(String soTaiKhoan, LocalDate tuNgay, LocalDate denNgay, String maLoaiGD) {
        if (soTaiKhoan == null || soTaiKhoan.isBlank())
            throw new IllegalArgumentException("Vui lòng nhập số tài khoản cần sao kê.");
        if (tuNgay != null && denNgay != null && tuNgay.isAfter(denNgay))
            throw new IllegalArgumentException("Ngày bắt đầu không được sau ngày kết thúc.");

        TellerStatementAccount account = dao.traCuuTaiKhoan(soTaiKhoan.trim())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản [" + soTaiKhoan + "] trong hệ thống."));
        List<TellerStatementEntry> allEntries = dao.layLichSu(account.getSoTaiKhoan());
        BigDecimal currentBalance = account.getSoDuHienTai() == null ? BigDecimal.ZERO : account.getSoDuHienTai();
        LocalDateTime start = tuNgay == null ? null : tuNgay.atStartOfDay();
        LocalDateTime endExclusive = denNgay == null ? null : denNgay.plusDays(1).atStartOfDay();

        BigDecimal openingBalance = openingBalance(allEntries, start, currentBalance);
        BigDecimal closingBalance = closingBalance(allEntries, endExclusive, openingBalance, currentBalance, denNgay);

        List<TellerStatementEntry> filtered = new ArrayList<>();
        BigDecimal credit = BigDecimal.ZERO;
        BigDecimal debit = BigDecimal.ZERO;
        for (TellerStatementEntry entry : allEntries) {
            LocalDateTime time = entry.getThoiGian();
            if (time == null || (start != null && time.isBefore(start)) || (endExclusive != null && !time.isBefore(endExclusive))) continue;
            if (maLoaiGD != null && !maLoaiGD.isBlank() && !maLoaiGD.equalsIgnoreCase(entry.getMaLoaiGD())) continue;
            filtered.add(entry);
            BigDecimal amount = entry.getSoTienThayDoi();
            if (amount != null && amount.signum() > 0) credit = credit.add(amount);
            else if (amount != null && amount.signum() < 0) debit = debit.add(amount.abs());
        }
        filtered.sort(Comparator.comparing(TellerStatementEntry::getThoiGian).reversed());

        TellerStatementData data = new TellerStatementData();
        data.setTaiKhoan(account);
        data.setGiaoDich(filtered);
        data.setSoDuDauKy(openingBalance);
        data.setSoDuCuoiKy(closingBalance);
        data.setTongPhatSinhCo(credit);
        data.setTongPhatSinhNo(debit);
        return data;
    }

    private BigDecimal openingBalance(List<TellerStatementEntry> entries, LocalDateTime start, BigDecimal current) {
        if (entries.isEmpty()) return current;
        if (start == null) return balanceBefore(entries.get(0));
        BigDecimal before = null;
        for (TellerStatementEntry entry : entries) {
            if (entry.getThoiGian() != null && entry.getThoiGian().isBefore(start)) before = entry.getSoDuSauThayDoi();
            else break;
        }
        return before != null ? before : balanceBefore(entries.get(0));
    }

    private BigDecimal closingBalance(List<TellerStatementEntry> entries, LocalDateTime endExclusive,
                                      BigDecimal opening, BigDecimal current, LocalDate selectedEnd) {
        if (endExclusive == null || selectedEnd == null || !selectedEnd.isBefore(LocalDate.now())) return current;
        BigDecimal closing = null;
        for (TellerStatementEntry entry : entries) {
            if (entry.getThoiGian() != null && entry.getThoiGian().isBefore(endExclusive)) closing = entry.getSoDuSauThayDoi();
            else break;
        }
        return closing == null ? opening : closing;
    }

    private BigDecimal balanceBefore(TellerStatementEntry first) {
        BigDecimal after = first.getSoDuSauThayDoi() == null ? BigDecimal.ZERO : first.getSoDuSauThayDoi();
        BigDecimal change = first.getSoTienThayDoi() == null ? BigDecimal.ZERO : first.getSoTienThayDoi();
        return after.subtract(change);
    }
}
