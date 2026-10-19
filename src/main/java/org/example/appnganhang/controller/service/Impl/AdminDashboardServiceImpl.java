package org.example.appnganhang.controller.service.Impl;

import org.example.appnganhang.controller.service.intf.AdminDashboardService;
import org.example.appnganhang.model.dao.Impl.AdminDashboardDAOImpl;
import org.example.appnganhang.model.dao.intf.AdminDashboardDAO;
import org.example.appnganhang.model.entity.AdminDashboardStats;

public class AdminDashboardServiceImpl
        implements AdminDashboardService {

    private final AdminDashboardDAO dashboardDAO;

    public AdminDashboardServiceImpl() {
        this.dashboardDAO = new AdminDashboardDAOImpl();
    }

    public AdminDashboardServiceImpl(AdminDashboardDAO dashboardDAO) {
        this.dashboardDAO = dashboardDAO;
    }

    @Override
    public AdminDashboardStats layThongKeDashboard() {
        return dashboardDAO.layThongKeDashboard();
    }
}