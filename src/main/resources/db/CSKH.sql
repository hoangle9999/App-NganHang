/*
    Stored procedures and views for the CSKH screens.
    Prerequisites: application tables and dbo.Seq_MaKhachHang.
*/

CREATE OR ALTER PROCEDURE dbo.sp_TaoMaKhachHang
    @MaKhachHang VARCHAR(20) OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @SoThuTu BIGINT;
    DECLARE @MaUngVien VARCHAR(20);

    WHILE 1 = 1
    BEGIN
        SELECT @SoThuTu = NEXT VALUE FOR dbo.Seq_MaKhachHang;
        SET @MaUngVien = 'KH' + RIGHT('000000' + CONVERT(VARCHAR(20), @SoThuTu), 6);

        IF NOT EXISTS (
            SELECT 1 FROM dbo.KhachHang WHERE MaKhachHang = @MaUngVien
        )
        BEGIN
            SET @MaKhachHang = @MaUngVien;
            RETURN;
        END;
    END;
END;
GO

CREATE OR ALTER VIEW dbo.vw_CSKH_TaiKhoanKhachHang
AS
    SELECT tk.MaTaiKhoan,
           tk.SoTaiKhoan,
           kh.HoTen AS TenChuTK,
           tk.SoDu,
           ISNULL(nh.TenNganHang, N'SMART BANK') AS TenNganHang,
           ISNULL(cn.TenChiNhanh, N'N/A') AS ChiNhanh,
           ISNULL(hm.TenHanMuc, N'Hạn mức mặc định') AS HanMuc,
           tk.TrangThai,
           kh.SoCCCD,
           ISNULL(CONVERT(NVARCHAR(50), hm.HanMucNgay), N'Mặc định') AS HanMucNgay
    FROM dbo.TaiKhoanNganHang AS tk
    LEFT JOIN dbo.KhachHang AS kh ON kh.MaKhachHang = tk.MaKhachHang
    LEFT JOIN dbo.NganHang AS nh ON nh.MaNganHang = tk.MaNganHang
    LEFT JOIN dbo.ChiNhanh AS cn ON cn.MaChiNhanh = tk.MaChiNhanh
    LEFT JOIN dbo.[HanMucGiaoDịch] AS hm ON hm.MaHanMuc = tk.MaHanMuc;
GO

CREATE OR ALTER VIEW dbo.vw_CSKH_LichSuGiaoDich
AS
    SELECT ls.MaLog, ls.MaGiaoDich, ls.SoTaiKhoanLienQuan, ls.HanhDong,
           ls.SoTienThayDoi, ls.SoDuSauThayDoi,
           gd.TrangThai, ls.ThoiGianGhiLog
    FROM dbo.LichSuGiaoDich AS ls
    INNER JOIN dbo.GiaoDichChuyenTien AS gd ON gd.MaGiaoDich = ls.MaGiaoDich;
GO

CREATE OR ALTER PROCEDURE dbo.sp_CSKH_DanhSachKhachHang
AS
BEGIN
    SET NOCOUNT ON;
    SELECT MaKhachHang, HoTen, SoCCCD, SoDienThoai, Email, DiaChi, TenDangNhap
    FROM dbo.KhachHang
    ORDER BY HoTen, MaKhachHang;
END;
GO

CREATE OR ALTER PROCEDURE dbo.sp_CSKH_TonTaiCCCD @SoCCCD VARCHAR(20)
AS
BEGIN
    SET NOCOUNT ON;
    SELECT CONVERT(BIT, CASE WHEN EXISTS
        (SELECT 1 FROM dbo.KhachHang WHERE SoCCCD = @SoCCCD) THEN 1 ELSE 0 END) AS TonTai;
END;
GO

CREATE OR ALTER PROCEDURE dbo.sp_CSKH_TonTaiSoDienThoai
    @SoDienThoai VARCHAR(11), @MaKhachHangBoQua VARCHAR(20) = NULL
AS
BEGIN
    SET NOCOUNT ON;
    SELECT CONVERT(BIT, CASE WHEN EXISTS
        (SELECT 1 FROM dbo.KhachHang
         WHERE SoDienThoai = @SoDienThoai
           AND (@MaKhachHangBoQua IS NULL OR MaKhachHang <> @MaKhachHangBoQua))
        THEN 1 ELSE 0 END) AS TonTai;
END;
GO

CREATE OR ALTER PROCEDURE dbo.sp_CSKH_ThemKhachHang
    @HoTen NVARCHAR(100), @SoCCCD VARCHAR(20), @SoDienThoai VARCHAR(11),
    @Email VARCHAR(100), @DiaChi NVARCHAR(200),
    @TenDangNhap VARCHAR(50), @MatKhau VARCHAR(255)
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    DECLARE @MaKhachHang VARCHAR(20);
    EXEC dbo.sp_TaoMaKhachHang @MaKhachHang = @MaKhachHang OUTPUT;

    BEGIN TRY
        BEGIN TRANSACTION;

        INSERT INTO dbo.TaiKhoanDangNhap_VaiTro (TenDangNhap, MatKhau, RoleName, MaNguoiDung)
        VALUES (@TenDangNhap, @MatKhau, N'Khách hàng', @MaKhachHang);

        INSERT INTO dbo.KhachHang
            (MaKhachHang, HoTen, SoCCCD, SoDienThoai, Email, DiaChi, TenDangNhap)
        VALUES
            (@MaKhachHang, @HoTen, @SoCCCD, @SoDienThoai, @Email, @DiaChi, @TenDangNhap);

        COMMIT TRANSACTION;
        SELECT @MaKhachHang AS MaKhachHang;
    END TRY
    BEGIN CATCH
        IF XACT_STATE() <> 0 ROLLBACK TRANSACTION;
        THROW;
    END CATCH;
END;
GO

CREATE OR ALTER PROCEDURE dbo.sp_CSKH_CapNhatKhachHang
    @MaKhachHang VARCHAR(20), @HoTen NVARCHAR(100), @SoDienThoai VARCHAR(11),
    @Email VARCHAR(100) = NULL, @DiaChi NVARCHAR(200) = NULL
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE dbo.KhachHang
    SET HoTen = @HoTen, SoDienThoai = @SoDienThoai, Email = @Email, DiaChi = @DiaChi
    WHERE MaKhachHang = @MaKhachHang;
END;
GO

CREATE OR ALTER PROCEDURE dbo.sp_CSKH_TaiKhoanTheoKhachHang @MaKhachHang VARCHAR(20)
AS
BEGIN
    SET NOCOUNT ON;
    SELECT tk.SoTaiKhoan, tk.SoDu, tk.TrangThai, cn.TenChiNhanh
    FROM dbo.TaiKhoanNganHang AS tk
    LEFT JOIN dbo.ChiNhanh AS cn ON cn.MaChiNhanh = tk.MaChiNhanh
    WHERE tk.MaKhachHang = @MaKhachHang
    ORDER BY tk.SoTaiKhoan;
END;
GO

CREATE OR ALTER PROCEDURE dbo.sp_CSKH_TraCuuTaiKhoan @TuKhoa NVARCHAR(100)
AS
BEGIN
    SET NOCOUNT ON;
    DECLARE @Keyword NVARCHAR(100) = LTRIM(RTRIM(ISNULL(@TuKhoa, N'')));
    SELECT TOP (200) *
    FROM dbo.vw_CSKH_TaiKhoanKhachHang
    WHERE @Keyword = N'' OR SoTaiKhoan LIKE N'%' + @Keyword + N'%'
       OR SoCCCD LIKE N'%' + @Keyword + N'%'
    ORDER BY SoTaiKhoan;
END;
GO

CREATE OR ALTER PROCEDURE dbo.sp_CSKH_Dashboard
AS
BEGIN
    SET NOCOUNT ON;
    SELECT
        (SELECT COUNT(*) FROM dbo.KhachHang) AS TongKhachHang,
        (SELECT COUNT(*) FROM dbo.TaiKhoanNganHang WHERE TrangThai = N'Hoạt động') AS TongTaiKhoanHoatDong,
        (SELECT COUNT(*) FROM dbo.GiaoDichChuyenTien WHERE CAST(NgayGiaoDich AS DATE) = CAST(GETDATE() AS DATE)) AS GiaoDichHomNay,
        CONVERT(INT, 0) AS KhieuNaiCho;
END;
GO

CREATE OR ALTER PROCEDURE dbo.sp_CSKH_DanhSachGiaoDich
AS
BEGIN
    SET NOCOUNT ON;
    SELECT TOP (500) MaGiaoDich, MaTaiKhoanNguon, MaTaiKhoanDich, SoTien,
           NgayGiaoDich, NoiDung, MaLoaiGD, TrangThai
    FROM dbo.GiaoDichChuyenTien
    ORDER BY NgayGiaoDich DESC;
END;
GO

CREATE OR ALTER PROCEDURE dbo.sp_CSKH_TimGiaoDich
    @TuKhoa NVARCHAR(100) = NULL, @TuNgay DATE = NULL, @DenNgay DATE = NULL,
    @MaLoaiGD VARCHAR(10) = NULL, @TrangThai NVARCHAR(30) = NULL
AS
BEGIN
    SET NOCOUNT ON;
    DECLARE @Keyword NVARCHAR(100) = NULLIF(LTRIM(RTRIM(@TuKhoa)), N'');
    SELECT TOP (500) MaGiaoDich, MaTaiKhoanNguon, MaTaiKhoanDich, SoTien,
           NgayGiaoDich, NoiDung, MaLoaiGD, TrangThai
    FROM dbo.GiaoDichChuyenTien
    WHERE (@Keyword IS NULL OR MaGiaoDich LIKE N'%' + @Keyword + N'%'
           OR MaTaiKhoanNguon LIKE N'%' + @Keyword + N'%'
           OR MaTaiKhoanDich LIKE N'%' + @Keyword + N'%')
      AND (@TuNgay IS NULL OR NgayGiaoDich >= CONVERT(DATETIME, @TuNgay))
      AND (@DenNgay IS NULL OR NgayGiaoDich < DATEADD(DAY, 1, CONVERT(DATETIME, @DenNgay)))
      AND (@MaLoaiGD IS NULL OR MaLoaiGD = @MaLoaiGD)
      AND (@TrangThai IS NULL OR TrangThai = @TrangThai)
    ORDER BY NgayGiaoDich DESC;
END;
GO

CREATE OR ALTER PROCEDURE dbo.sp_CSKH_TenLoaiGiaoDich @MaLoaiGD VARCHAR(10)
AS
BEGIN
    SET NOCOUNT ON;
    SELECT TenLoaiGD FROM dbo.LoaiGiaoDich WHERE MaLoaiGD = @MaLoaiGD;
END;
GO

CREATE OR ALTER PROCEDURE dbo.sp_CSKH_MaLoaiGiaoDich @TenLoaiGD NVARCHAR(50)
AS
BEGIN
    SET NOCOUNT ON;
    SELECT MaLoaiGD FROM dbo.LoaiGiaoDich WHERE TenLoaiGD = @TenLoaiGD;
END;
GO

CREATE OR ALTER PROCEDURE dbo.sp_CSKH_LichSuGiaoDich
    @TuKhoa NVARCHAR(100) = NULL, @TuNgay DATE = NULL, @DenNgay DATE = NULL
AS
BEGIN
    SET NOCOUNT ON;
    DECLARE @Keyword NVARCHAR(100) = NULLIF(LTRIM(RTRIM(@TuKhoa)), N'');
    SELECT MaLog, MaGiaoDich, SoTaiKhoanLienQuan, HanhDong, SoTienThayDoi,
           SoDuSauThayDoi, TrangThai, ThoiGianGhiLog
    FROM dbo.vw_CSKH_LichSuGiaoDich
    WHERE (@Keyword IS NULL OR SoTaiKhoanLienQuan LIKE N'%' + @Keyword + N'%'
           OR MaGiaoDich LIKE N'%' + @Keyword + N'%')
      AND (@TuNgay IS NULL OR ThoiGianGhiLog >= CONVERT(DATETIME, @TuNgay))
      AND (@DenNgay IS NULL OR ThoiGianGhiLog < DATEADD(DAY, 1, CONVERT(DATETIME, @DenNgay)))
    ORDER BY ThoiGianGhiLog DESC;
END;
GO

CREATE OR ALTER PROCEDURE dbo.sp_CSKH_TenChuTaiKhoan @SoTaiKhoan VARCHAR(15)
AS
BEGIN
    SET NOCOUNT ON;
    SELECT TOP (1) kh.HoTen
    FROM dbo.TaiKhoanNganHang AS tk
    INNER JOIN dbo.KhachHang AS kh ON kh.MaKhachHang = tk.MaKhachHang
    WHERE tk.SoTaiKhoan = @SoTaiKhoan;
END;
GO
