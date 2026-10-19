package org.example.appnganhang.controller.service.intf;

import org.example.appnganhang.model.entity.CSKHAccountLookupItem;
import java.util.List;

public interface CSKHAccountLookupService {
    List<CSKHAccountLookupItem> timKiemTaiKhoan(String tuKhoa) throws Exception;
}