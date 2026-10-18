package org.example.appnganhang.controller.service.Iml;

import org.example.appnganhang.controller.service.intf.CSKHAccountLookupService;
import org.example.appnganhang.model.dao.Impl.CSKHAccountLookupDAOImpl;
import org.example.appnganhang.model.dao.intf.CSKHAccountLookupDAO;
import org.example.appnganhang.model.entity.CSKHAccountLookupItem;

import java.util.List;

public class CSKHAccountLookupServiceImpl implements CSKHAccountLookupService {
    private final CSKHAccountLookupDAO accountLookupDAO = new CSKHAccountLookupDAOImpl();

    @Override
    public List<CSKHAccountLookupItem> timKiemTaiKhoan(String tuKhoa) throws Exception {
        return accountLookupDAO.timKiemTaiKhoan(tuKhoa);
    }
}