package org.example.appnganhang.model.dao.intf;

import org.example.appnganhang.model.entity.CSKHAccountLookupItem;
import java.sql.SQLException;
import java.util.List;

public interface CSKHAccountLookupDAO {
    List<CSKHAccountLookupItem> timKiemTaiKhoan(String tuKhoa) throws SQLException;
}
