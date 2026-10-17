
-- DỰ ÁN CUỐI KỲ: ỨNG DỤNG CHUYỂN TIỀN NGÂN HÀNG
-- Thành viên : ĐỖ LÊ GIA HÂN
-- Vai trò    : Thiết kế dữ liệu
-- Hệ QTCSDL  : Microsoft SQL Server

-- 0. LÀM SẠCH DATABASE CŨ (NẾU TỒN TẠI)
USE master;
GO

IF EXISTS (SELECT 1 FROM sys.databases WHERE name = 'DB_ChuyenTienNganHang')
BEGIN
    ALTER DATABASE DB_ChuyenTienNganHang
        SET SINGLE_USER WITH ROLLBACK IMMEDIATE;

    DROP DATABASE DB_ChuyenTienNganHang;
END;
GO


-- 1. KHỞI TẠO DATABASE
CREATE DATABASE DB_ChuyenTienNganHang;
GO

USE DB_ChuyenTienNganHang;
GO


-- 2. TẠO CÁC BẢNG DỮ LIỆU VÀ RÀNG BUỘC
-- 2.1. Bảng tài khoản đăng nhập và vai trò
CREATE TABLE dbo.TaiKhoanDangNhap_VaiTro (
    TenDangNhap VARCHAR(50) PRIMARY KEY,
    MatKhau VARCHAR(255) NOT NULL,

    RoleName NVARCHAR(30) NOT NULL
        CHECK (
            RoleName IN (
                N'Admin',
                N'Nhân viên CSKH',
                N'Giao dịch viên',
                N'Khách hàng'
            )
        ),

    MaNguoiDung VARCHAR(20) NOT NULL
);
GO

-- 2.2. Bảng chi nhánh
CREATE TABLE dbo.ChiNhanh (
    MaChiNhanh VARCHAR(10) PRIMARY KEY,
    TenChiNhanh NVARCHAR(100) NOT NULL,
    DiaChi NVARCHAR(200) NOT NULL
);
GO

-- 2.3. Bảng khách hàng
CREATE TABLE dbo.KhachHang (
    MaKhachHang VARCHAR(20) PRIMARY KEY,
    HoTen NVARCHAR(100) NOT NULL,
    SoCCCD VARCHAR(12) NOT NULL,
    SoDienThoai VARCHAR(11) NOT NULL,
    Email VARCHAR(100) NULL,

    -- Thông tin địa chỉ khách hàng
    DiaChi NVARCHAR(200) NULL,

    TenDangNhap VARCHAR(50) NOT NULL,

    CONSTRAINT UQ_KhachHang_CCCD
        UNIQUE (SoCCCD),

    CONSTRAINT UQ_KhachHang_SDT
        UNIQUE (SoDienThoai),

    CONSTRAINT FK_KhachHang_DangNhap
        FOREIGN KEY (TenDangNhap)
        REFERENCES dbo.TaiKhoanDangNhap_VaiTro(TenDangNhap)
);
GO

-- 2.4. Bảng hạn mức giao dịch
CREATE TABLE dbo.HanMucGiaoDịch (
    MaHanMuc VARCHAR(10) PRIMARY KEY,
    TenHanMuc NVARCHAR(50) NOT NULL,

    HanMucNgay DECIMAL(18, 2) NOT NULL
        CONSTRAINT CK_HanMuc_GiaTri
        CHECK (HanMucNgay > 0)
);
GO

-- 2.5. Bảng tài khoản ngân hàng
CREATE TABLE dbo.TaiKhoanNganHang (
    MaTaiKhoan VARCHAR(20) PRIMARY KEY,
    MaKhachHang VARCHAR(20) NOT NULL,
    SoTaiKhoan VARCHAR(15) NOT NULL,

    SoDu DECIMAL(18, 2) NOT NULL
        CONSTRAINT DF_TaiKhoan_SoDu DEFAULT 0
        CONSTRAINT CK_TaiKhoan_SoDu CHECK (SoDu >= 0),

    TrangThai NVARCHAR(20) NOT NULL
        CONSTRAINT DF_TaiKhoan_TrangThai
        DEFAULT N'Hoạt động',

    MaChiNhanh VARCHAR(10) NOT NULL,
    MaHanMuc VARCHAR(10) NULL,

    CONSTRAINT UQ_TaiKhoan_STK
        UNIQUE (SoTaiKhoan),

    CONSTRAINT FK_TaiKhoan_KhachHang
        FOREIGN KEY (MaKhachHang)
        REFERENCES dbo.KhachHang(MaKhachHang),

    CONSTRAINT FK_TaiKhoan_ChiNhanh
        FOREIGN KEY (MaChiNhanh)
        REFERENCES dbo.ChiNhanh(MaChiNhanh),

    CONSTRAINT FK_TaiKhoan_HanMuc
        FOREIGN KEY (MaHanMuc)
        REFERENCES dbo.HanMucGiaoDịch(MaHanMuc)
);
GO

-- 2.6. Bảng loại giao dịch
CREATE TABLE dbo.LoaiGiaoDich (
    MaLoaiGD VARCHAR(10) PRIMARY KEY,
    TenLoaiGD NVARCHAR(50) NOT NULL
);
GO

-- Dữ liệu loại giao dịch ban đầu
INSERT INTO dbo.LoaiGiaoDich (MaLoaiGD, TenLoaiGD)
VALUES
    ('CK01', N'Chuyển khoản'),
    ('NT01', N'Nạp tiền'),
    ('RT01', N'Rút tiền'),
    ('TT01', N'Thanh toán');
GO

-- 2.7. Bảng giao dịch chuyển tiền
CREATE TABLE dbo.GiaoDichChuyenTien (
    MaGiaoDich VARCHAR(30) PRIMARY KEY,
    MaTaiKhoanNguon VARCHAR(20) NOT NULL,
    MaTaiKhoanDich VARCHAR(20) NOT NULL,

    SoTien DECIMAL(18, 2) NOT NULL
        CONSTRAINT CK_GiaoDich_SoTien
        CHECK (SoTien > 0),

    NgayGiaoDich DATETIME NOT NULL
        CONSTRAINT DF_GiaoDich_NgayGD
        DEFAULT GETDATE(),

    NoiDung NVARCHAR(255) NULL,
    MaLoaiGD VARCHAR(10) NOT NULL,

    TrangThai NVARCHAR(30) NOT NULL
        CONSTRAINT CK_GiaoDich_TrangThai
        CHECK (
            TrangThai IN (
                N'Chờ xử lý',
                N'Thành công',
                N'Thất bại',
                N'Đã hủy'
            )
        ),

    -- Không cho phép chuyển tiền từ tài khoản sang chính nó
    CONSTRAINT CK_GiaoDich_TaiKhoanKhac
        CHECK (MaTaiKhoanNguon <> MaTaiKhoanDich),

    CONSTRAINT FK_GiaoDich_TaiKhoanNguon
        FOREIGN KEY (MaTaiKhoanNguon)
        REFERENCES dbo.TaiKhoanNganHang(MaTaiKhoan),

    CONSTRAINT FK_GiaoDich_TaiKhoanDich
        FOREIGN KEY (MaTaiKhoanDich)
        REFERENCES dbo.TaiKhoanNganHang(MaTaiKhoan),

    CONSTRAINT FK_GiaoDich_LoaiGD
        FOREIGN KEY (MaLoaiGD)
        REFERENCES dbo.LoaiGiaoDich(MaLoaiGD)
);
GO

-- 2.8. Bảng nhân viên
CREATE TABLE dbo.NhanVien (
    MaNhanVien VARCHAR(20) PRIMARY KEY,
    HoTen NVARCHAR(100) NOT NULL,

    -- Thông tin số điện thoại nhân viên
    SoDienThoai VARCHAR(11) NULL,

    -- Thông tin địa chỉ nhân viên
    DiaChi NVARCHAR(200) NULL,

    ChucVu NVARCHAR(50) NOT NULL,
    MaChiNhanh VARCHAR(10) NOT NULL,
    TenDangNhap VARCHAR(50) NULL,

    CONSTRAINT FK_NhanVien_ChiNhanh
        FOREIGN KEY (MaChiNhanh)
        REFERENCES dbo.ChiNhanh(MaChiNhanh),

    CONSTRAINT FK_NhanVien_DangNhap
        FOREIGN KEY (TenDangNhap)
        REFERENCES dbo.TaiKhoanDangNhap_VaiTro(TenDangNhap)
);
GO

-- 2.9. Bảng lịch sử giao dịch (Audit Trail)
CREATE TABLE dbo.LichSuGiaoDich (
    MaLog INT IDENTITY(1, 1) PRIMARY KEY,
    MaGiaoDich VARCHAR(30) NOT NULL,
    SoTaiKhoanLienQuan VARCHAR(15) NOT NULL,
    HanhDong NVARCHAR(50) NOT NULL,
    SoTienThayDoi DECIMAL(18, 2) NOT NULL,
    SoDuSauThayDoi DECIMAL(18, 2) NOT NULL,

    ThoiGianGhiLog DATETIME NOT NULL
        CONSTRAINT DF_LichSuGD_ThoiGian
        DEFAULT GETDATE(),

    CONSTRAINT FK_LichSuGD_GiaoDich
        FOREIGN KEY (MaGiaoDich)
        REFERENCES dbo.GiaoDichChuyenTien(MaGiaoDich)
);
GO


CREATE TABLE NganHang (
    MaNganHang VARCHAR(20) PRIMARY KEY,
    TenNganHang NVARCHAR(100) NOT NULL,
    TrangThai NVARCHAR(30) NOT NULL
);

INSERT INTO NganHang (MaNganHang, TenNganHang, TrangThai)
VALUES
    ('SMART', N'SMART BANK', N'Hoạt động'),
    ('VCB', N'Vietcombank', N'Hoạt động'),
    ('BIDV', N'BIDV', N'Hoạt động'),
    ('CTG', N'VietinBank', N'Hoạt động'),
    ('AGR', N'Agribank', N'Hoạt động'),
    ('TCB', N'Techcombank', N'Hoạt động'),
    ('MB', N'MB Bank', N'Hoạt động');

	
ALTER TABLE TaiKhoanNganHang
ADD MaNganHang VARCHAR(20) NULL;

ALTER TABLE TaiKhoanNganHang
ADD CONSTRAINT FK_TaiKhoan_NganHang
FOREIGN KEY (MaNganHang)
REFERENCES NganHang(MaNganHang);


USE DB_ChuyenTienNganHang;
GO

-- Sinh mã khách hàng
CREATE SEQUENCE dbo.Seq_MaKhachHang
    AS BIGINT
    START WITH 11
    INCREMENT BY 1;

-- Sinh mã nhân viên
CREATE SEQUENCE dbo.Seq_MaNhanVien
    AS BIGINT
    START WITH 11
    INCREMENT BY 1;

-- Sinh mã tài khoản ngân hàng
CREATE SEQUENCE dbo.Seq_MaTaiKhoan
    AS BIGINT
    START WITH 11
    INCREMENT BY 1;

-- Sinh mã giao dịch
CREATE SEQUENCE dbo.Seq_MaGiaoDich
    AS BIGINT
    START WITH 11
    INCREMENT BY 1;

-- Sinh mã chi nhánh
CREATE SEQUENCE dbo.Seq_MaChiNhanh
    AS BIGINT
    START WITH 11
    INCREMENT BY 1;

-- Sinh mã hạn mức
CREATE SEQUENCE dbo.Seq_MaHanMuc
    AS BIGINT
    START WITH 11
    INCREMENT BY 1;
GO

-- sinh số tài khoản
CREATE SEQUENCE dbo.Seq_SoTaiKhoan
    AS BIGINT
    START WITH 100000000000011
    INCREMENT BY 1;
GO

CREATE OR ALTER PROCEDURE dbo.sp_TaoMaKhachHang
    @MaKhachHang VARCHAR(20) OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    SET @MaKhachHang =
        'KH' + RIGHT(
            '000000' + CAST(
                NEXT VALUE FOR dbo.Seq_MaKhachHang AS VARCHAR(20)
            ),
            6
        );
END;
GO

CREATE OR ALTER PROCEDURE dbo.sp_TaoMaTaiKhoan
    @MaTaiKhoan VARCHAR(20) OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    SET @MaTaiKhoan =
        'TK' + RIGHT(
            '000000' + CAST(
                NEXT VALUE FOR dbo.Seq_MaTaiKhoan AS VARCHAR(20)
            ),
            6
        );
END;
GO

CREATE OR ALTER PROCEDURE dbo.sp_TaoSoTaiKhoan
    @SoTaiKhoan VARCHAR(15) OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    SET @SoTaiKhoan =
        CAST(
            NEXT VALUE FOR dbo.Seq_SoTaiKhoan
            AS VARCHAR(15)
        );
END;
GO

-- lấy dữ liệu lên dashboard
CREATE PROC sp_GetDashboardDB
	@MaChiNhanh VARCHAR(20) = NULL 
AS
BEGIN
	SELECT
		(
			SELECT COUNT(DISTINCT KH.MaKhachHang)
			FROM KhachHang KH
			JOIN TaiKhoanNganHang TKDN ON TKDN.MaKhachHang = KH.MaKhachHang
			WHERE (@MaChiNhanh IS NULL OR TKDN.MaChiNhanh = @MaChiNhanh)
		) AS TongKhachHang,

		(
			SELECT COUNT(TKNH.MaTaiKhoan)
			FROM TaiKhoanNganHang TKNH
			WHERE ((@MaChiNhanh IS NULL OR TKNH.MaChiNhanh = @MaChiNhanh) AND TrangThai = N'Hoạt động')
		) AS TongTaiKhoanActive,

		(
			SELECT ISNULL(SUM(TKNH.SoDu), 0)
			FROM TaiKhoanNganHang TKNH
			WHERE (@MaChiNhanh IS NULL OR TKNH.MaChiNhanh = @MaChiNhanh)
		) AS TongSoDu,

		(
			SELECT COUNT(GDCT.MaGiaoDich)
			FROM GiaoDichChuyenTien GDCT
			JOIN TaiKhoanNganHang TKNH ON TKNH.MaTaiKhoan = GDCT.MaTaiKhoanNguon
			WHERE ((@MaChiNhanh IS NULL OR TKNH.MaChiNhanh = @MaChiNhanh) AND CAST(GDCT.NgayGiaoDich AS DATE) = CAST(GETDATE() AS DATE) )
		) AS SoGiaoDichHomNay,

		(
			SELECT ISNULL(SUM(GDCT.SoTien), 0)
			FROM GiaoDichChuyenTien GDCT
			JOIN TaiKhoanNganHang TKNH ON TKNH.MaTaiKhoan = GDCT.MaTaiKhoanNguon
			WHERE ((@MaChiNhanh IS NULL OR TKNH.MaChiNhanh = @MaChiNhanh) AND CAST(GDCT.NgayGiaoDich AS DATE) = CAST(GETDATE() AS DATE) AND GDCT.TrangThai = N'Thành công' )
		) AS TongTienGiaoDichHomNay
END
