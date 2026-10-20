/*
    Read-only SQL API for the customer portal.
    Run after the base schema and seed data have been created.
*/

-- Generates a bank account number from the sequence in the base schema.
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
    SELECT ls.MaLog, ls.MaGiaoDich, ls.SoTaiKhoanLienQuan,
           ls.HanhDong, ls.SoTienThayDoi, ls.SoDuSauThayDoi, ls.ThoiGianGhiLog
    FROM dbo.vw_KH_BienDongSoDu AS ls
    INNER JOIN dbo.vw_KH_TaiKhoan AS tk ON tk.SoTaiKhoan = ls.SoTaiKhoanLienQuan
    INNER JOIN dbo.KhachHang AS kh
        ON kh.MaKhachHang = tk.MaKhachHang
       AND kh.TenDangNhap = @TenDangNhap
    WHERE tk.MaKhachHang = @MaKhachHang
      AND tk.SoTaiKhoan = @SoTaiKhoan
    ORDER BY ls.ThoiGianGhiLog DESC;
END;
GO

CREATE OR ALTER PROCEDURE dbo.sp_KH_GiaoDichTaiKhoan
    @SoTaiKhoan VARCHAR(15),
    @MaKhachHang VARCHAR(20),
    @TenDangNhap VARCHAR(50)
AS
BEGIN
    SET NOCOUNT ON;
    SELECT gd.MaGiaoDich, gd.MaTaiKhoanNguon, gd.MaTaiKhoanDich,
           gd.SoTien, gd.NgayGiaoDich, gd.NoiDung, gd.MaLoaiGD,
           gd.TrangThai, gd.TenLoaiGD
    FROM dbo.vw_KH_GiaoDich AS gd
    INNER JOIN dbo.KhachHang AS kh
        ON kh.MaKhachHang = @MaKhachHang
       AND kh.TenDangNhap = @TenDangNhap
    WHERE (gd.MaKhachHangNguon = @MaKhachHang AND gd.MaTaiKhoanNguon = @SoTaiKhoan)
       OR (gd.MaKhachHangDich = @MaKhachHang AND gd.MaTaiKhoanDich = @SoTaiKhoan)
    ORDER BY gd.NgayGiaoDich DESC;
END;
GO
