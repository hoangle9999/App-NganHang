package org.example.appnganhang.model.dao.Impl;

import org.example.appnganhang.model.dao.intf.CSKHAccountLookupDAO;
import org.example.appnganhang.model.entity.CSKHAccountLookupItem;
import org.example.appnganhang.model.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CSKHAccountLookupDAOImpl implements CSKHAccountLookupDAO {


    private static final String SQL_DANH_SACH_TAI_KHOAN = """
        SELECT tk.MaTaiKhoan,
               tk.SoTaiKhoan,
               kh.HoTen AS TenChuTK,
               tk.SoDu,
               ISNULL(nh.TenNganHang, 'SMART BANK') AS TenNganHang,
               ISNULL(cn.TenChiNhanh, 'N/A') AS ChiNhanh,
               ISNULL(hm.TenHanMuc, N'Hạn mức mặc định') AS HanMuc,
               tk.TrangThai,
               kh.SoCCCD,
               ISNULL(CAST(hm.HanMucNgay AS NVARCHAR), N'Mặc định') AS HanMucNgay
        FROM dbo.TaiKhoanNganHang tk
        LEFT JOIN dbo.KhachHang kh
               ON tk.MaKhachHang = kh.MaKhachHang
        LEFT JOIN dbo.NganHang nh
               ON tk.MaNganHang = nh.MaNganHang
        LEFT JOIN dbo.ChiNhanh cn
               ON tk.MaChiNhanh = cn.MaChiNhanh
        LEFT JOIN dbo.[HanMucGiaoDịch] hm
               ON tk.MaHanMuc = hm.MaHanMuc
        WHERE (? = '' OR tk.SoTaiKhoan LIKE ? OR kh.SoCCCD LIKE ?)
        ORDER BY tk.SoTaiKhoan
        """;


    @Override
    public List<CSKHAccountLookupItem> timKiemTaiKhoan(String tuKhoa) throws SQLException {
        String keyword = tuKhoa == null ? "" : tuKhoa.trim();
        String wildcard = "%" + keyword + "%";
        List<CSKHAccountLookupItem> danhSach = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_DANH_SACH_TAI_KHOAN)) {
            statement.setString(1, keyword);
            statement.setString(2, wildcard);
            statement.setString(3, wildcard);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    danhSach.add(new CSKHAccountLookupItem(
                            resultSet.getString("MaTaiKhoan"),
                            resultSet.getString("SoTaiKhoan"),
                            resultSet.getString("TenChuTK"),
                            resultSet.getBigDecimal("SoDu"),
                            resultSet.getString("TenNganHang"),
                            resultSet.getString("ChiNhanh"),
                            resultSet.getString("HanMuc"),
                            resultSet.getString("TrangThai"),
                            resultSet.getString("SoCCCD"),
                            resultSet.getString("HanMucNgay")
                    ));
                }
            }
        }
        return danhSach;
    }
}
