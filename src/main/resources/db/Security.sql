/*
    Tạo các vai trò và phân quyền theo nguyên tắc chỉ cấp quyền cần thiết.
    Chạy sau khi đã chạy schema, Admin.sql, CSKH.sql, Teller.sql, KH.sql
    và Trigger.sql.
*/
USE DB_ChuyenTienNganHang;
GO

IF DATABASE_PRINCIPAL_ID(N'role_BankAdmin') IS NULL CREATE ROLE role_BankAdmin AUTHORIZATION dbo;
IF DATABASE_PRINCIPAL_ID(N'role_Teller') IS NULL CREATE ROLE role_Teller AUTHORIZATION dbo;
IF DATABASE_PRINCIPAL_ID(N'role_CSKH') IS NULL CREATE ROLE role_CSKH AUTHORIZATION dbo;
IF DATABASE_PRINCIPAL_ID(N'role_Customer') IS NULL CREATE ROLE role_Customer AUTHORIZATION dbo;
GO

-- Quản trị viên: toàn quyền thao tác đối tượng trong schema dbo.
GRANT SELECT, INSERT, UPDATE, DELETE ON SCHEMA::dbo TO role_BankAdmin;
GRANT EXECUTE ON SCHEMA::dbo TO role_BankAdmin;
GRANT VIEW DEFINITION ON SCHEMA::dbo TO role_BankAdmin;
GO

-- Giao dịch viên: gọi thủ tục nghiệp vụ, đọc khung nhìn; không sửa bảng trực tiếp.
GRANT EXECUTE ON OBJECT::dbo.sp_Teller_Dashboard TO role_Teller;
GRANT EXECUTE ON OBJECT::dbo.SP_GDV_DanhSachNganHang TO role_Teller;
GRANT EXECUTE ON OBJECT::dbo.SP_GDV_TimNguoiNhan TO role_Teller;
GRANT EXECUTE ON OBJECT::dbo.SP_GDV_KiemTraTaiKhoanNguon TO role_Teller;
GRANT EXECUTE ON OBJECT::dbo.SP_GDV_ThucHienChuyenTien TO role_Teller;
GRANT EXECUTE ON OBJECT::dbo.SP_GDV_TraCuuTaiKhoan TO role_Teller;
GRANT EXECUTE ON OBJECT::dbo.SP_GDV_ThucHienGiaoDich TO role_Teller;
GRANT EXECUTE ON OBJECT::dbo.SP_GDV_DoiTrangThaiTaiKhoan TO role_Teller;
GRANT EXECUTE ON OBJECT::dbo.SP_GDV_GanHanMucTaiKhoan TO role_Teller;
GRANT EXECUTE ON OBJECT::dbo.SP_GDV_DuyetGiaoDichChoXuLy TO role_Teller;
GRANT EXECUTE ON OBJECT::dbo.SP_GDV_HuyGiaoDichChoXuLy TO role_Teller;
GRANT SELECT ON OBJECT::dbo.vw_Teller_GiaoDichGanDay TO role_Teller;
GRANT SELECT ON OBJECT::dbo.vw_GDV_DanhSachTaiKhoan TO role_Teller;
GRANT SELECT ON OBJECT::dbo.vw_GDV_SaoKe TO role_Teller;
GRANT SELECT ON OBJECT::dbo.vw_Teller_TrangThaiGiaoDich TO role_Teller;
GRANT EXECUTE ON OBJECT::dbo.fn_GDV_LayHanhDongTienMat TO role_Teller;
GRANT EXECUTE ON OBJECT::dbo.fn_GDV_TinhBienDongSoDu TO role_Teller;
GRANT EXECUTE ON OBJECT::dbo.fn_GDV_TinhHanMucConLai TO role_Teller;
DENY INSERT, UPDATE, DELETE ON SCHEMA::dbo TO role_Teller;
GO

-- Nhân viên CSKH: dùng thủ tục, khung nhìn CSKH và function tìm giao dịch.
GRANT EXECUTE ON OBJECT::dbo.sp_CSKH_DanhSachKhachHang TO role_CSKH;
GRANT EXECUTE ON OBJECT::dbo.sp_CSKH_TonTaiCCCD TO role_CSKH;
GRANT EXECUTE ON OBJECT::dbo.sp_CSKH_TonTaiSoDienThoai TO role_CSKH;
GRANT EXECUTE ON OBJECT::dbo.sp_CSKH_ThemKhachHang TO role_CSKH;
GRANT EXECUTE ON OBJECT::dbo.sp_CSKH_CapNhatKhachHang TO role_CSKH;
GRANT EXECUTE ON OBJECT::dbo.sp_CSKH_TaiKhoanTheoKhachHang TO role_CSKH;
GRANT EXECUTE ON OBJECT::dbo.sp_CSKH_TraCuuTaiKhoan TO role_CSKH;
GRANT EXECUTE ON OBJECT::dbo.sp_CSKH_Dashboard TO role_CSKH;
GRANT EXECUTE ON OBJECT::dbo.sp_CSKH_DanhSachGiaoDich TO role_CSKH;
GRANT EXECUTE ON OBJECT::dbo.sp_CSKH_TimGiaoDich TO role_CSKH;
GRANT EXECUTE ON OBJECT::dbo.sp_CSKH_TenLoaiGiaoDich TO role_CSKH;
GRANT EXECUTE ON OBJECT::dbo.sp_CSKH_MaLoaiGiaoDich TO role_CSKH;
GRANT EXECUTE ON OBJECT::dbo.sp_CSKH_LichSuGiaoDich TO role_CSKH;
GRANT EXECUTE ON OBJECT::dbo.sp_CSKH_TenChuTaiKhoan TO role_CSKH;
GRANT SELECT ON OBJECT::dbo.vw_CSKH_TaiKhoanKhachHang TO role_CSKH;
GRANT SELECT ON OBJECT::dbo.vw_CSKH_LichSuGiaoDich TO role_CSKH;
GRANT SELECT ON OBJECT::dbo.fn_CSKH_TimGiaoDich TO role_CSKH;
DENY INSERT, UPDATE, DELETE ON SCHEMA::dbo TO role_CSKH;
GO

-- Khách hàng: chỉ gọi thủ tục khách hàng; không đọc trực tiếp bảng hoặc view chung.
-- Chỉ gán role này khi danh tính khách hàng được xác thực riêng ở cấp cơ sở dữ liệu.
GRANT EXECUTE ON OBJECT::dbo.sp_KH_TimHoSo TO role_Customer;
GRANT EXECUTE ON OBJECT::dbo.sp_KH_DanhSachTaiKhoan TO role_Customer;
GRANT EXECUTE ON OBJECT::dbo.sp_KH_DanhSachSoTaiKhoan TO role_Customer;
GRANT EXECUTE ON OBJECT::dbo.sp_KH_SoDuTaiKhoan TO role_Customer;
GRANT EXECUTE ON OBJECT::dbo.sp_KH_LichSuBienDong TO role_Customer;
GRANT EXECUTE ON OBJECT::dbo.sp_KH_GiaoDichTaiKhoan TO role_Customer;
DENY SELECT, INSERT, UPDATE, DELETE ON SCHEMA::dbo TO role_Customer;
GO

-- Minh họa: GRANT cấp quyền, REVOKE thu hồi quyền đã cấp, DENY từ chối quyền.
-- Quyền sửa trực tiếp khách hàng được cấp rồi thu hồi; thao tác phải qua thủ tục.
GRANT UPDATE ON OBJECT::dbo.KhachHang TO role_CSKH;
REVOKE UPDATE ON OBJECT::dbo.KhachHang FROM role_CSKH;
DENY SELECT ON OBJECT::dbo.AuditNganHang TO role_Teller;
DENY SELECT ON OBJECT::dbo.AuditNganHang TO role_CSKH;
DENY SELECT ON OBJECT::dbo.AuditNganHang TO role_Customer;
GO

