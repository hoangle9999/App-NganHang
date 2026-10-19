package org.example.appnganhang.model.entity;
public class CSKHDashboardStats {
    private final int tongKhachHang, tongTaiKhoanHoatDong, giaoDichHomNay, khieuNaiCho;
    public CSKHDashboardStats(int kh, int tk, int gd, int khieuNaiCho) {
        tongKhachHang=kh; tongTaiKhoanHoatDong=tk; giaoDichHomNay=gd; this.khieuNaiCho=khieuNaiCho;
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
    public int getKhieuNaiCho(){
        return khieuNaiCho;
    }
}
