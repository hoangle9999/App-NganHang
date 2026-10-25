/*
    Stored procedures and views for the CSKH screens.
    Prerequisites: application tables and dbo.Seq_MaKhachHang.
*/

-- Trả danh sách giao dịch theo bộ lọc; thủ tục gọi hàm sẽ áp dụng giới hạn và sắp xếp.
CREATE OR ALTER FUNCTION dbo.fn_CSKH_TimGiaoDich
(
    @TuKhoa NVARCHAR(100),
    @TuNgay DATE,
    @DenNgay DATE,
    @MaLoaiGD VARCHAR(10),
    @TrangThai NVARCHAR(30)
)
RETURNS TABLE
AS
RETURN
(
    SELECT gd.MaGiaoDich, gd.MaTaiKhoanNguon, gd.MaTaiKhoanDich, gd.SoTien,
           gd.NgayGiaoDich, gd.NoiDung, gd.MaLoaiGD, gd.TrangThai
    FROM dbo.GiaoDichChuyenTien AS gd
    CROSS APPLY (VALUES (NULLIF(LTRIM(RTRIM(@TuKhoa)), N''))) AS kw(TuKhoaChuanHoa)
    WHERE (kw.TuKhoaChuanHoa IS NULL
           OR gd.MaGiaoDich LIKE N'%' + kw.TuKhoaChuanHoa + N'%'
           OR gd.MaTaiKhoanNguon LIKE N'%' + kw.TuKhoaChuanHoa + N'%'
           OR gd.MaTaiKhoanDich LIKE N'%' + kw.TuKhoaChuanHoa + N'%')
      AND (@TuNgay IS NULL OR gd.NgayGiaoDich >= CONVERT(DATETIME, @TuNgay))
      AND (@DenNgay IS NULL OR gd.NgayGiaoDich < DATEADD(DAY, 1, CONVERT(DATETIME, @DenNgay)))
      AND (@MaLoaiGD IS NULL OR gd.MaLoaiGD = @MaLoaiGD)
      AND (@TrangThai IS NULL OR gd.TrangThai = @TrangThai)
);
GO

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
    DECLARE @MaTaiKhoan VARCHAR(20), @SoTaiKhoan VARCHAR(15);
    DECLARE @MaChiNhanh VARCHAR(10), @MaHanMuc VARCHAR(10);

    SELECT TOP (1) @MaChiNhanh = MaChiNhanh
    FROM dbo.ChiNhanh
    ORDER BY MaChiNhanh;
    IF @MaChiNhanh IS NULL
        THROW 52401, N'Chưa có chi nhánh để mở tài khoản ngân hàng.', 1;

    SELECT @MaHanMuc = MaHanMuc
    FROM dbo.[HanMucGiaoDịch]
    WHERE MaHanMuc = 'HM001';
    IF @MaHanMuc IS NULL
        SELECT TOP (1) @MaHanMuc = MaHanMuc
        FROM dbo.[HanMucGiaoDịch]
        ORDER BY MaHanMuc;
    IF @MaHanMuc IS NULL
        THROW 52402, N'Chưa có gói hạn mức để mở tài khoản ngân hàng.', 1;

    EXEC dbo.sp_TaoMaKhachHang @MaKhachHang = @MaKhachHang OUTPUT;

    BEGIN TRY
        BEGIN TRANSACTION;

        INSERT INTO dbo.TaiKhoanDangNhap_VaiTro (TenDangNhap, MatKhau, RoleName, MaNguoiDung)
        VALUES (@TenDangNhap, @MatKhau, N'Khách hàng', @MaKhachHang);

        INSERT INTO dbo.KhachHang
            (MaKhachHang, HoTen, SoCCCD, SoDienThoai, Email, DiaChi, TenDangNhap)
        VALUES
            (@MaKhachHang, @HoTen, @SoCCCD, @SoDienThoai, @Email, @DiaChi, @TenDangNhap);

        EXEC dbo.sp_TaoMaTaiKhoan @Ma = @MaTaiKhoan OUTPUT;
        EXEC dbo.sp_TaoSoTaiKhoan @Ma = @SoTaiKhoan OUTPUT;

        INSERT INTO dbo.TaiKhoanNganHang
            (MaTaiKhoan, MaKhachHang, SoTaiKhoan, SoDu, TrangThai, MaChiNhanh, MaHanMuc, MaNganHang)
        VALUES
            (@MaTaiKhoan, @MaKhachHang, @SoTaiKhoan, 0, N'Hoạt động', @MaChiNhanh, @MaHanMuc, 'SMART');

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
    FROM dbo.fn_CSKH_TimGiaoDich(NULL, NULL, NULL, NULL, NULL)
    ORDER BY NgayGiaoDich DESC;
END;
GO

CREATE OR ALTER PROCEDURE dbo.sp_CSKH_TimGiaoDich
    @TuKhoa NVARCHAR(100) = NULL, @TuNgay DATE = NULL, @DenNgay DATE = NULL,
    @MaLoaiGD VARCHAR(10) = NULL, @TrangThai NVARCHAR(30) = NULL
AS
BEGIN
    SET NOCOUNT ON;
    SELECT TOP (500) MaGiaoDich, MaTaiKhoanNguon, MaTaiKhoanDich, SoTien,
           NgayGiaoDich, NoiDung, MaLoaiGD, TrangThai
    FROM dbo.fn_CSKH_TimGiaoDich(@TuKhoa, @TuNgay, @DenNgay, @MaLoaiGD, @TrangThai)
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

-- ===== Index va trigger nghiep vu CSKH =====
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE object_id=OBJECT_ID(N'dbo.GiaoDichChuyenTien') AND name=N'IX_GiaoDich_NgayTrangThai')
    CREATE INDEX IX_GiaoDich_NgayTrangThai ON dbo.GiaoDichChuyenTien(NgayGiaoDich DESC,TrangThai) INCLUDE (MaLoaiGD,SoTien,MaTaiKhoanNguon,MaTaiKhoanDich);
GO
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE object_id=OBJECT_ID(N'dbo.KhachHang') AND name=N'IX_KhachHang_HoTen')
    CREATE INDEX IX_KhachHang_HoTen ON dbo.KhachHang(HoTen) INCLUDE (SoDienThoai,Email);
GO

CREATE OR ALTER TRIGGER dbo.trg_CSKH_KhachHang_AuditInsert
ON dbo.KhachHang AFTER INSERT AS
BEGIN
 SET NOCOUNT ON;
 INSERT dbo.AuditNganHang(BangDuLieu,KhoaDuLieu,HanhDong,ChiTiet)
 SELECT N'KhachHang',i.MaKhachHang,N'Tao ho so khach hang',CONCAT(N'HoTen=',i.HoTen) FROM inserted i;
END;
GO

-- ===== Phân quyền cho role_CSKH =====
IF DATABASE_PRINCIPAL_ID(N'role_CSKH') IS NULL
    CREATE ROLE role_CSKH;
GO

-- Tra cứu & quản lý khách hàng
GRANT EXECUTE ON OBJECT::dbo.sp_CSKH_DanhSachKhachHang    TO role_CSKH;
GRANT EXECUTE ON OBJECT::dbo.sp_CSKH_TonTaiCCCD           TO role_CSKH;
GRANT EXECUTE ON OBJECT::dbo.sp_CSKH_TonTaiSoDienThoai    TO role_CSKH;
GRANT EXECUTE ON OBJECT::dbo.sp_CSKH_ThemKhachHang        TO role_CSKH;
GRANT EXECUTE ON OBJECT::dbo.sp_CSKH_CapNhatKhachHang     TO role_CSKH;
GRANT EXECUTE ON OBJECT::dbo.sp_CSKH_TaiKhoanTheoKhachHang TO role_CSKH;

-- Tra cứu tài khoản & giao dịch
GRANT EXECUTE ON OBJECT::dbo.sp_CSKH_TraCuuTaiKhoan       TO role_CSKH;
GRANT EXECUTE ON OBJECT::dbo.sp_CSKH_TenChuTaiKhoan       TO role_CSKH;
GRANT EXECUTE ON OBJECT::dbo.sp_CSKH_Dashboard            TO role_CSKH;
GRANT EXECUTE ON OBJECT::dbo.sp_CSKH_DanhSachGiaoDich     TO role_CSKH;
GRANT EXECUTE ON OBJECT::dbo.sp_CSKH_TimGiaoDich          TO role_CSKH;
GRANT EXECUTE ON OBJECT::dbo.sp_CSKH_TenLoaiGiaoDich      TO role_CSKH;
GRANT EXECUTE ON OBJECT::dbo.sp_CSKH_MaLoaiGiaoDich       TO role_CSKH;
GRANT EXECUTE ON OBJECT::dbo.sp_CSKH_LichSuGiaoDich       TO role_CSKH;

-- Function tra cứu giao dịch
GRANT SELECT ON dbo.fn_CSKH_TimGiaoDich                   TO role_CSKH;

-- Đọc view (cần cho các SP nội bộ)
GRANT SELECT ON OBJECT::dbo.vw_CSKH_TaiKhoanKhachHang    TO role_CSKH;
GRANT SELECT ON OBJECT::dbo.vw_CSKH_LichSuGiaoDich        TO role_CSKH;

-- CSKH chỉ đọc, không được sửa/xóa dữ liệu tài chính
DENY INSERT, UPDATE, DELETE ON OBJECT::dbo.GiaoDichChuyenTien TO role_CSKH;
DENY INSERT, UPDATE, DELETE ON OBJECT::dbo.LichSuGiaoDich     TO role_CSKH;
DENY INSERT, UPDATE, DELETE ON OBJECT::dbo.TaiKhoanNganHang   TO role_CSKH;
GO
