/*
    Tạo bảng nhật ký và trigger cho DB_ChuyenTienNganHang.
    Chạy sau Database_AppNganhang_DoLeGiaHan.sql để các bảng nghiệp vụ đã tồn tại.
*/
USE DB_ChuyenTienNganHang;
GO

IF OBJECT_ID(N'dbo.AuditNganHang', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.AuditNganHang (
        MaAudit BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_AuditNganHang PRIMARY KEY,
        BangDuLieu SYSNAME NOT NULL,
        KhoaDuLieu NVARCHAR(100) NOT NULL,
        HanhDong NVARCHAR(30) NOT NULL,
        ChiTiet NVARCHAR(1000) NULL,
        NguoiThucHien SYSNAME NOT NULL
            CONSTRAINT DF_AuditNganHang_Nguoi DEFAULT ORIGINAL_LOGIN(),
        ThoiGian DATETIME2(0) NOT NULL
            CONSTRAINT DF_AuditNganHang_ThoiGian DEFAULT SYSUTCDATETIME()
    );
END;
GO

-- Ghi nhận thay đổi số dư để có thể tra soát từ mọi nguồn cập nhật.
CREATE OR ALTER TRIGGER dbo.trg_TaiKhoan_AuditSoDu
ON dbo.TaiKhoanNganHang
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    INSERT dbo.AuditNganHang (BangDuLieu, KhoaDuLieu, HanhDong, ChiTiet)
    SELECT N'TaiKhoanNganHang', i.MaTaiKhoan, N'Thay đổi số dư',
           CONCAT(N'Số dư: ', CONVERT(NVARCHAR(50), d.SoDu), N' -> ',
                  CONVERT(NVARCHAR(50), i.SoDu))
    FROM inserted AS i
    JOIN deleted AS d ON d.MaTaiKhoan = i.MaTaiKhoan
    WHERE i.SoDu <> d.SoDu;
END;
GO

-- Ghi nhận thay đổi trạng thái tài khoản.
CREATE OR ALTER TRIGGER dbo.trg_TaiKhoan_AuditTrangThai
ON dbo.TaiKhoanNganHang
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    INSERT dbo.AuditNganHang (BangDuLieu, KhoaDuLieu, HanhDong, ChiTiet)
    SELECT N'TaiKhoanNganHang', i.MaTaiKhoan, N'Thay đổi trạng thái',
           CONCAT(N'Trạng thái: ', d.TrangThai, N' -> ', i.TrangThai)
    FROM inserted AS i
    JOIN deleted AS d ON d.MaTaiKhoan = i.MaTaiKhoan
    WHERE i.TrangThai <> d.TrangThai;
END;
GO

-- Ghi nhận cập nhật hồ sơ khách hàng, không lưu lại thông tin định danh nhạy cảm.
CREATE OR ALTER TRIGGER dbo.trg_KhachHang_AuditCapNhat
ON dbo.KhachHang
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    INSERT dbo.AuditNganHang (BangDuLieu, KhoaDuLieu, HanhDong, ChiTiet)
    SELECT N'KhachHang', i.MaKhachHang, N'Cập nhật hồ sơ',
           CONCAT(N'Trường thay đổi: ',
               CASE WHEN i.HoTen <> d.HoTen THEN N'Họ tên ' ELSE N'' END,
               CASE WHEN i.SoDienThoai <> d.SoDienThoai THEN N'Số điện thoại ' ELSE N'' END,
               CASE WHEN ISNULL(i.Email, '') <> ISNULL(d.Email, '') THEN N'Email ' ELSE N'' END,
               CASE WHEN ISNULL(i.DiaChi, N'') <> ISNULL(d.DiaChi, N'') THEN N'Địa chỉ ' ELSE N'' END,
               CASE WHEN ISNULL(i.TenDangNhap, '') <> ISNULL(d.TenDangNhap, '') THEN N'Tên đăng nhập ' ELSE N'' END)
    FROM inserted AS i
    JOIN deleted AS d ON d.MaKhachHang = i.MaKhachHang
    WHERE i.HoTen <> d.HoTen
       OR i.SoDienThoai <> d.SoDienThoai
       OR ISNULL(i.Email, '') <> ISNULL(d.Email, '')
       OR ISNULL(i.DiaChi, N'') <> ISNULL(d.DiaChi, N'')
       OR ISNULL(i.TenDangNhap, '') <> ISNULL(d.TenDangNhap, '');
END;
GO

-- Ghi nhận giao dịch mới, bao gồm giao dịch liên ngân hàng chờ xử lý.
CREATE OR ALTER TRIGGER dbo.trg_GiaoDich_AuditInsert
ON dbo.GiaoDichChuyenTien
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;
    INSERT dbo.AuditNganHang (BangDuLieu, KhoaDuLieu, HanhDong, ChiTiet)
    SELECT N'GiaoDichChuyenTien', i.MaGiaoDich, N'Tạo giao dịch',
           CONCAT(N'Số tiền=', CONVERT(NVARCHAR(50), i.SoTien),
                  N'; Trạng thái=', i.TrangThai,
                  N'; Tài khoản nguồn=', i.MaTaiKhoanNguon)
    FROM inserted AS i;
END;
GO

-- Giao dịch thành công là bản ghi tài chính không được sửa hoặc xóa.
CREATE OR ALTER TRIGGER dbo.trg_GiaoDich_ChanSuaXoaThanhCong
ON dbo.GiaoDichChuyenTien
AFTER UPDATE, DELETE
AS
BEGIN
    SET NOCOUNT ON;
    IF EXISTS (SELECT 1 FROM deleted WHERE TrangThai = N'Thành công')
        THROW 52401, N'Không được xóa hoặc sửa giao dịch đã thành công.', 1;
END;
GO
