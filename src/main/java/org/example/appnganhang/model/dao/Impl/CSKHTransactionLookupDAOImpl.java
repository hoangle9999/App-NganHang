package org.example.appnganhang.model.dao.Impl;

import org.example.appnganhang.model.dao.intf.CSKHTransactionLookupDAO;
import org.example.appnganhang.model.entity.GiaoDichChuyenTien;
import org.example.appnganhang.model.util.DBConnection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CSKHTransactionLookupDAOImpl implements CSKHTransactionLookupDAO {

    @Override
    public List<GiaoDichChuyenTien> layDanhSachGiaoDich() throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             CallableStatement statement = connection.prepareCall("{call dbo.sp_CSKH_DanhSachGiaoDich}");
             ResultSet resultSet = statement.executeQuery()) {
            return docDanhSach(resultSet);
        }
    }

    @Override
    public List<GiaoDichChuyenTien> timKiemGiaoDich(String tuKhoa, LocalDate tuNgay,
                                                    LocalDate denNgay, String maLoaiGiaoDich,
                                                    String trangThai) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             CallableStatement statement = connection.prepareCall("{call dbo.sp_CSKH_TimGiaoDich(?, ?, ?, ?, ?)}")) {
            setStringOrNull(statement, 1, tuKhoa);
            setDateOrNull(statement, 2, tuNgay);
            setDateOrNull(statement, 3, denNgay);
            setStringOrNull(statement, 4, maLoaiGiaoDich);
            setStringOrNull(statement, 5, trangThai);
            try (ResultSet resultSet = statement.executeQuery()) {
                return docDanhSach(resultSet);
            }
        }
    }

    @Override
    public String layTenLoaiGiaoDich(String maLoaiGiaoDich) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             CallableStatement statement = connection.prepareCall("{call dbo.sp_CSKH_TenLoaiGiaoDich(?)}")) {
            statement.setString(1, maLoaiGiaoDich);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getString("TenLoaiGD") : maLoaiGiaoDich;
            }
        }
    }

    @Override
    public String layMaLoaiGiaoDich(String tenLoaiGiaoDich) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             CallableStatement statement = connection.prepareCall("{call dbo.sp_CSKH_MaLoaiGiaoDich(?)}")) {
            statement.setString(1, tenLoaiGiaoDich);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getString("MaLoaiGD") : null;
            }
        }
    }

    private void setStringOrNull(CallableStatement statement, int index, String value) throws SQLException {
        if (value == null || value.isBlank()) statement.setNull(index, java.sql.Types.NVARCHAR);
        else statement.setString(index, value.trim());
    }

    private void setDateOrNull(CallableStatement statement, int index, LocalDate value) throws SQLException {
        if (value == null) statement.setNull(index, java.sql.Types.DATE);
        else statement.setDate(index, Date.valueOf(value));
    }

    private List<GiaoDichChuyenTien> docDanhSach(ResultSet resultSet) throws SQLException {
        List<GiaoDichChuyenTien> danhSach = new ArrayList<>();
        while (resultSet.next()) {
            GiaoDichChuyenTien giaoDich = new GiaoDichChuyenTien();
            giaoDich.setMaGiaoDich(resultSet.getString("MaGiaoDich"));
            giaoDich.setMaTaiKhoanNguon(resultSet.getString("MaTaiKhoanNguon"));
            giaoDich.setMaTaiKhoanDich(resultSet.getString("MaTaiKhoanDich"));
            giaoDich.setSoTien(resultSet.getBigDecimal("SoTien"));
            Timestamp ngayGiaoDich = resultSet.getTimestamp("NgayGiaoDich");
            giaoDich.setNgayGiaoDich(ngayGiaoDich == null ? null : ngayGiaoDich.toLocalDateTime());
            giaoDich.setNoiDung(resultSet.getString("NoiDung"));
            giaoDich.setMaLoaiGD(resultSet.getString("MaLoaiGD"));
            giaoDich.setTrangThai(resultSet.getString("TrangThai"));
            danhSach.add(giaoDich);
        }
        return danhSach;
    }
}
