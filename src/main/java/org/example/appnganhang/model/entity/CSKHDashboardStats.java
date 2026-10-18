package org.example.appnganhang.model.entity;
public class CSKHDashboardStats {
    private final int tongKhachHang, tongTaiKhoanHoatDong, giaoDichHomNay;
    public CSKHDashboardStats(int kh, int tk, int gd) {
        tongKhachHang=kh; tongTaiKhoanHoatDong=tk; giaoDichHomNay=gd;
    }
    public int getTongKhachHang(){
        return tongKhachHang;
    }
    public int getTongTaiKhoanHoatDong(){
        return tongTaiKhoanHoatDong;
    }
    public int getGiaoDichHomNay(){
        return giaoDichHomNay;
    }
}