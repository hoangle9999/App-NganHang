package org.example.appnganhang.model.dao.Impl;

import org.example.appnganhang.model.dao.intf.CSKHAccountLookupDAO;
import org.example.appnganhang.model.entity.CSKHAccountLookupItem;
import org.example.appnganhang.model.util.DBConnection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CSKHAccountLookupDAOImpl implements CSKHAccountLookupDAO {

    @Override
    public List<CSKHAccountLookupItem> timKiemTaiKhoan(String tuKhoa) throws SQLException {
        List<CSKHAccountLookupItem> danhSach = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             CallableStatement statement = connection.prepareCall("{call dbo.sp_CSKH_TraCuuTaiKhoan(?)}")) {
            statement.setString(1, tuKhoa == null ? "" : tuKhoa.trim());
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
                            resultSet.getString("HanMucNgay")));
                }
            }
        }
        return danhSach;
    }
}
