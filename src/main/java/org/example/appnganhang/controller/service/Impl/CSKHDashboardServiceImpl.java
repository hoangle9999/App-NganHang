package org.example.appnganhang.controller.service.Impl;
import org.example.appnganhang.controller.service.intf.CSKHDashboardService;
import org.example.appnganhang.model.dao.Impl.CSKHDashboardDAOImpl;
import org.example.appnganhang.model.dao.intf.CSKHDashboardDAO;
import org.example.appnganhang.model.entity.CSKHDashboardStats;
public class CSKHDashboardServiceImpl implements CSKHDashboardService {
    private final CSKHDashboardDAO dao;

    public CSKHDashboardServiceImpl() {
        this.dao = new CSKHDashboardDAOImpl();
    }

    public CSKHDashboardStats layThongKe() throws Exception {
        return dao.layThongKe();
    }
}
