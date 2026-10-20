package org.example.appnganhang.controller.service.Impl;

import org.example.appnganhang.controller.service.intf.CSKHAccountLookupService;
import org.example.appnganhang.model.dao.Impl.CSKHAccountLookupDAOImpl;
import org.example.appnganhang.model.dao.intf.CSKHAccountLookupDAO;
import org.example.appnganhang.model.entity.CSKHAccountLookupItem;

import java.util.List;

public class CSKHAccountLookupServiceImpl implements CSKHAccountLookupService {
    private final CSKHAccountLookupDAO dao;

    public CSKHAccountLookupServiceImpl() {
        this.dao = new CSKHAccountLookupDAOImpl();
    }

    @Override
    public List<CSKHAccountLookupItem> timKiemTaiKhoan(String tuKhoa) throws Exception {
        return dao.timKiemTaiKhoan(tuKhoa);
    }
}
