/*
    Read-only SQL API for the customer portal.
    Run after the base schema and seed data have been created.
*/


USE DB_ChuyenTienNganHang;
GO

-- ===== Index va trigger nghiep vu khach hang =====
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE object_id=OBJECT_ID(N'dbo.LichSuGiaoDich') AND name=N'IX_LichSu_SoTaiKhoanThoiGian')
    CREATE INDEX IX_LichSu_SoTaiKhoanThoiGian
        ON dbo.LichSuGiaoDich(SoTaiKhoanLienQuan,ThoiGianGhiLog DESC)
        INCLUDE (MaGiaoDich,HanhDong,SoTienThayDoi,SoDuSauThayDoi);
GO

-- Lich su tai khoan la bang chi ghi them de bao toan dau vet giao dich.
CREATE OR ALTER TRIGGER dbo.trg_KH_LichSu_AppendOnly
ON dbo.LichSuGiaoDich 
AFTER UPDATE, DELETE
AS
BEGIN
    SET NOCOUNT ON;
    THROW 52511,N'Lich su giao dich la du lieu chi ghi them, khong duoc sua hoac xoa.',1;
END;
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

-- == FUNCTION ==
CREATE OR ALTER FUNCTION dbo.fn_KH_KiemTraDuSoDu
(
    @SoDu DECIMAL(18,2),
    @SoTienChuyen DECIMAL(18,2)
)
RETURNS BIT
AS
BEGIN
    IF @SoDu IS NULL
       OR @SoTienChuyen IS NULL
       OR @SoDu < 0
       OR @SoTienChuyen <= 0
        RETURN 0;

    RETURN CASE
        WHEN @SoDu >= @SoTienChuyen THEN 1
        ELSE 0
    END;
END;
GO


-- === Generates a bank account number from the sequence in the base schema. ===
CREATE OR ALTER PROCEDURE dbo.sp_TaoSoTaiKhoan
    @Ma VARCHAR(15) OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    SELECT @Ma = CONVERT(VARCHAR(15), NEXT VALUE FOR dbo.Seq_SoTaiKhoan);
END;
GO
CREATE OR ALTER VIEW dbo.vw_KH_TaiKhoan
AS
    SELECT tk.MaTaiKhoan,
           tk.MaKhachHang,
           tk.SoTaiKhoan,
           tk.SoDu,
           tk.TrangThai,
           ISNULL(nh.TenNganHang, N'SMART BANK') AS TenNganHang,
           ISNULL(cn.TenChiNhanh, N'Hội sở SMART BANK') AS TenChiNhanh,
           ISNULL(hm.TenHanMuc, N'Mặc định') AS TenHanMuc,
           ISNULL(hm.HanMucNgay, CONVERT(DECIMAL(18,2), 50000000)) AS HanMucNgay
    FROM dbo.TaiKhoanNganHang AS tk
    LEFT JOIN dbo.NganHang AS nh ON nh.MaNganHang = tk.MaNganHang
    LEFT JOIN dbo.ChiNhanh AS cn ON cn.MaChiNhanh = tk.MaChiNhanh
    LEFT JOIN dbo.[HanMucGiaoDịch] AS hm ON hm.MaHanMuc = tk.MaHanMuc;
GO

CREATE OR ALTER VIEW dbo.vw_KH_BienDongSoDu
AS
    SELECT ls.MaLog, ls.MaGiaoDich, ls.SoTaiKhoanLienQuan,
           ls.HanhDong, ls.SoTienThayDoi, ls.SoDuSauThayDoi,
           ls.ThoiGianGhiLog
    FROM dbo.LichSuGiaoDich AS ls;
GO

CREATE OR ALTER VIEW dbo.vw_KH_GiaoDich
AS
    SELECT gd.MaGiaoDich,
           tkNguon.SoTaiKhoan AS MaTaiKhoanNguon,
           tkDich.SoTaiKhoan AS MaTaiKhoanDich,
           gd.SoTien, gd.NgayGiaoDich, gd.NoiDung, gd.MaLoaiGD,
           gd.TrangThai,
           ISNULL(lg.TenLoaiGD, N'Chuyển khoản') AS TenLoaiGD,
           tkNguon.MaKhachHang AS MaKhachHangNguon,
           tkDich.MaKhachHang AS MaKhachHangDich
    FROM dbo.GiaoDichChuyenTien AS gd
    INNER JOIN dbo.TaiKhoanNganHang AS tkNguon ON tkNguon.MaTaiKhoan = gd.MaTaiKhoanNguon
    INNER JOIN dbo.TaiKhoanNganHang AS tkDich ON tkDich.MaTaiKhoan = gd.MaTaiKhoanDich
    LEFT JOIN dbo.LoaiGiaoDich AS lg ON lg.MaLoaiGD = gd.MaLoaiGD;
GO

CREATE OR ALTER PROCEDURE dbo.sp_KH_TimHoSo
    @MaKhachHang VARCHAR(20),
    @TenDangNhap VARCHAR(50)
AS
BEGIN
    SET NOCOUNT ON;
    SELECT TOP (1) MaKhachHang, HoTen, SoCCCD, SoDienThoai, Email, DiaChi, TenDangNhap
    FROM dbo.KhachHang
    WHERE MaKhachHang = @MaKhachHang
      AND TenDangNhap = @TenDangNhap;
END;
GO

CREATE OR ALTER PROCEDURE dbo.sp_KH_DanhSachTaiKhoan
    @MaKhachHang VARCHAR(20),
    @TenDangNhap VARCHAR(50)
AS
BEGIN
    SET NOCOUNT ON;
    SELECT tk.MaTaiKhoan, tk.SoTaiKhoan, tk.SoDu, tk.TrangThai,
           tk.TenNganHang, tk.TenChiNhanh, tk.TenHanMuc, tk.HanMucNgay
    FROM dbo.vw_KH_TaiKhoan AS tk
    INNER JOIN dbo.KhachHang AS kh
        ON kh.MaKhachHang = tk.MaKhachHang
       AND kh.TenDangNhap = @TenDangNhap
    WHERE tk.MaKhachHang = @MaKhachHang
    ORDER BY tk.SoTaiKhoan;
END;
GO

CREATE OR ALTER PROCEDURE dbo.sp_KH_DanhSachSoTaiKhoan
    @MaKhachHang VARCHAR(20),
    @TenDangNhap VARCHAR(50)
AS
BEGIN
    SET NOCOUNT ON;
    SELECT tk.SoTaiKhoan
    FROM dbo.vw_KH_TaiKhoan AS tk
    INNER JOIN dbo.KhachHang AS kh
        ON kh.MaKhachHang = tk.MaKhachHang
       AND kh.TenDangNhap = @TenDangNhap
    WHERE tk.MaKhachHang = @MaKhachHang
    ORDER BY tk.SoTaiKhoan;
END;
GO

CREATE OR ALTER PROCEDURE dbo.sp_KH_SoDuTaiKhoan
    @SoTaiKhoan VARCHAR(15),
    @MaKhachHang VARCHAR(20),
    @TenDangNhap VARCHAR(50)
AS
BEGIN
    SET NOCOUNT ON;
    SELECT tk.SoDu
    FROM dbo.vw_KH_TaiKhoan AS tk
    INNER JOIN dbo.KhachHang AS kh
        ON kh.MaKhachHang = tk.MaKhachHang
       AND kh.TenDangNhap = @TenDangNhap
    WHERE tk.MaKhachHang = @MaKhachHang
      AND tk.SoTaiKhoan = @SoTaiKhoan;
END;
GO

CREATE OR ALTER PROCEDURE dbo.sp_KH_LichSuBienDong
    @SoTaiKhoan VARCHAR(15),
    @MaKhachHang VARCHAR(20),
    @TenDangNhap VARCHAR(50)
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    BEGIN TRY
        SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
        BEGIN TRANSACTION;

        SELECT
            ls.MaLog,
            ls.MaGiaoDich,
            ls.SoTaiKhoanLienQuan,
            ls.HanhDong,
            ls.SoTienThayDoi,
            ls.SoDuSauThayDoi,
            ls.ThoiGianGhiLog
        FROM dbo.vw_KH_BienDongSoDu AS ls
        INNER JOIN dbo.vw_KH_TaiKhoan AS tk
            ON tk.SoTaiKhoan = ls.SoTaiKhoanLienQuan
        INNER JOIN dbo.KhachHang AS kh
            ON kh.MaKhachHang = tk.MaKhachHang
           AND kh.TenDangNhap = @TenDangNhap
        WHERE tk.MaKhachHang = @MaKhachHang
          AND tk.SoTaiKhoan = @SoTaiKhoan
        ORDER BY ls.ThoiGianGhiLog DESC;

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0
            ROLLBACK TRANSACTION;

        THROW;
    END CATCH;
END;
GO

CREATE OR ALTER PROCEDURE dbo.sp_KH_GiaoDichTaiKhoan
    @SoTaiKhoan VARCHAR(15),
    @MaKhachHang VARCHAR(20),
    @TenDangNhap VARCHAR(50)
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    BEGIN TRY
        SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
        BEGIN TRANSACTION;

        SELECT
            gd.MaGiaoDich,
            gd.MaTaiKhoanNguon,
            gd.MaTaiKhoanDich,
            gd.SoTien,
            gd.NgayGiaoDich,
            gd.NoiDung,
            gd.MaLoaiGD,
            gd.TrangThai,
            gd.TenLoaiGD
        FROM dbo.vw_KH_GiaoDich AS gd
        INNER JOIN dbo.KhachHang AS kh
            ON kh.MaKhachHang = @MaKhachHang
           AND kh.TenDangNhap = @TenDangNhap
        WHERE
            (gd.MaKhachHangNguon = @MaKhachHang
             AND gd.MaTaiKhoanNguon = @SoTaiKhoan)
            OR
            (gd.MaKhachHangDich = @MaKhachHang
             AND gd.MaTaiKhoanDich = @SoTaiKhoan)
        ORDER BY gd.NgayGiaoDich DESC;

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0
            ROLLBACK TRANSACTION;

        THROW;
    END CATCH;
END;
GO

-- === PHÂN QUYỀN ===
-- 1. Tao role neu chua ton tai
IF DATABASE_PRINCIPAL_ID('role_Customer') IS NULL
    CREATE ROLE role_Customer;
GO

-- 2. Cap quyen tra cuu thong tin khach hang
GRANT EXECUTE ON OBJECT::dbo.sp_KH_TimHoSo
TO role_Customer;

GRANT EXECUTE ON OBJECT::dbo.sp_KH_DanhSachTaiKhoan
TO role_Customer;

GRANT EXECUTE ON OBJECT::dbo.sp_KH_DanhSachSoTaiKhoan
TO role_Customer;

GRANT EXECUTE ON OBJECT::dbo.sp_KH_SoDuTaiKhoan
TO role_Customer;

GRANT EXECUTE ON OBJECT::dbo.sp_KH_LichSuBienDong
TO role_Customer;

GRANT EXECUTE ON OBJECT::dbo.sp_KH_GiaoDichTaiKhoan
TO role_Customer;

GRANT EXECUTE ON OBJECT::dbo.fn_KH_KiemTraDuSoDu
TO role_Customer;
GO

-- 3. Cam khach hang them, sua, xoa giao dich
DENY INSERT, UPDATE, DELETE
ON OBJECT::dbo.GiaoDichChuyenTien
TO role_Customer;

DENY INSERT, UPDATE, DELETE
ON OBJECT::dbo.LichSuGiaoDich
TO role_Customer;

DENY INSERT, UPDATE, DELETE
ON OBJECT::dbo.TaiKhoanNganHang
TO role_Customer;

DENY INSERT, UPDATE, DELETE
ON OBJECT::dbo.KhachHang
TO role_Customer;
GO
