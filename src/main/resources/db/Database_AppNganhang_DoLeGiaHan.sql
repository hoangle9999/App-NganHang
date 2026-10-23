
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

CREATE DATABASE DB_ChuyenTienNganHang;
GO

USE DB_ChuyenTienNganHang;
GO


-- 1. KHỞI TẠO DATABASE
-- ----------------------------------------------------------
-- 1.1  Tài khoản đăng nhập & vai trò ứng dụng
--      MatKhau phải là HASH được tạo ở Java, không lưu rõ.
--      RoleName phải thuộc đúng 4 vai trò ứng dụng.
-- ----------------------------------------------------------
CREATE TABLE dbo.TaiKhoanDangNhap_VaiTro (
    TenDangNhap  VARCHAR(50)   NOT NULL
        CONSTRAINT PK_TaiKhoanDangNhap PRIMARY KEY,
    MatKhau      VARCHAR(255)  NOT NULL,
    RoleName     NVARCHAR(30)  NOT NULL,
    MaNguoiDung  VARCHAR(20)   NOT NULL,
    QuyenHoatDong BIT NOT NULL CONSTRAINT DF_TaiKhoan_QuyenHoatDong DEFAULT (1),

    CONSTRAINT CK_TaiKhoanDangNhap_Role
        CHECK (RoleName IN (
            N'Admin',
            N'Nhân viên CSKH',
            N'Giao dịch viên',
            N'Khách hàng'
        )),

    CONSTRAINT UQ_TaiKhoanDangNhap_MaNguoiDung
        UNIQUE (MaNguoiDung)
);
GO

-- ----------------------------------------------------------
-- 1.2  Chi nhánh
-- ----------------------------------------------------------
CREATE TABLE dbo.ChiNhanh (
    MaChiNhanh  VARCHAR(10)    NOT NULL
        CONSTRAINT PK_ChiNhanh PRIMARY KEY,
    TenChiNhanh NVARCHAR(100)  NOT NULL,
    DiaChi      NVARCHAR(200)  NOT NULL
);
GO

-- ----------------------------------------------------------
-- 1.3  Danh mục ngân hàng
--      Chỉ chứa danh sách tổ chức, KHÔNG lưu tài khoản ngoài.
-- ----------------------------------------------------------
CREATE TABLE dbo.NganHang (
    MaNganHang  VARCHAR(20)    NOT NULL
        CONSTRAINT PK_NganHang PRIMARY KEY,
    TenNganHang NVARCHAR(100)  NOT NULL,
    TrangThai   NVARCHAR(30)   NOT NULL
        CONSTRAINT DF_NganHang_TrangThai DEFAULT N'Hoạt động',

    CONSTRAINT CK_NganHang_TrangThai
        CHECK (TrangThai IN (N'Hoạt động', N'Ngừng hoạt động'))
);
GO

-- ----------------------------------------------------------
-- 1.4  Khách hàng
-- ----------------------------------------------------------
CREATE TABLE dbo.KhachHang (
    MaKhachHang  VARCHAR(20)   NOT NULL
        CONSTRAINT PK_KhachHang PRIMARY KEY,
    HoTen        NVARCHAR(100) NOT NULL,
    SoCCCD       VARCHAR(12)   NOT NULL,
    SoDienThoai  VARCHAR(11)   NOT NULL,
    Email        VARCHAR(100)  NULL,
    DiaChi       NVARCHAR(200) NULL,
    TenDangNhap  VARCHAR(50)   NULL,

    CONSTRAINT UQ_KhachHang_CCCD   UNIQUE (SoCCCD),
    CONSTRAINT UQ_KhachHang_SDT    UNIQUE (SoDienThoai),

    CONSTRAINT CK_KhachHang_CCCD
        CHECK (LEN(SoCCCD) = 12 AND SoCCCD NOT LIKE '%[^0-9]%'),

    CONSTRAINT CK_KhachHang_SDT
        CHECK (
            LEN(SoDienThoai) BETWEEN 10 AND 11
            AND SoDienThoai NOT LIKE '%[^0-9]%'
        ),

    CONSTRAINT FK_KhachHang_DangNhap
        FOREIGN KEY (TenDangNhap)
        REFERENCES dbo.TaiKhoanDangNhap_VaiTro(TenDangNhap)
);
GO

-- ----------------------------------------------------------
-- 1.5  Hạn mức giao dịch
--      Lưu ý: tên bảng có ký tự Unicode để giữ nguyên tên gốc.
--      Code Java truy vấn bằng: dbo.[HanMucGiaoDịch]
-- ----------------------------------------------------------
CREATE TABLE dbo.[HanMucGiaoDịch] (
    MaHanMuc    VARCHAR(10)    NOT NULL
        CONSTRAINT PK_HanMuc PRIMARY KEY,
    TenHanMuc   NVARCHAR(50)   NOT NULL,
    HanMucNgay  DECIMAL(18,2)  NOT NULL,

    CONSTRAINT CK_HanMuc_GiaTri CHECK (HanMucNgay > 0)
);
GO

-- ----------------------------------------------------------
-- 1.6  Tài khoản ngân hàng
--      Chỉ lưu tài khoản nội bộ SMART BANK.
--      SoDu >= 0, TrangThai phải hợp lệ.
-- ----------------------------------------------------------
CREATE TABLE dbo.TaiKhoanNganHang (
    MaTaiKhoan   VARCHAR(20)    NOT NULL
        CONSTRAINT PK_TaiKhoanNganHang PRIMARY KEY,
    MaKhachHang  VARCHAR(20)    NOT NULL,
    SoTaiKhoan   VARCHAR(15)    NOT NULL,

    SoDu         DECIMAL(18,2)  NOT NULL
        CONSTRAINT DF_TaiKhoan_SoDu DEFAULT (0),

    TrangThai    NVARCHAR(20)   NOT NULL
        CONSTRAINT DF_TaiKhoan_TrangThai DEFAULT N'Hoạt động',

    MaChiNhanh   VARCHAR(10)    NOT NULL,

    MaHanMuc     VARCHAR(10)    NOT NULL
        CONSTRAINT DF_TaiKhoan_HanMuc DEFAULT ('HM001'),

    MaNganHang   VARCHAR(20)    NOT NULL
        CONSTRAINT DF_TaiKhoan_NganHang DEFAULT ('SMART'),

    CONSTRAINT UQ_TaiKhoan_STK  UNIQUE (SoTaiKhoan),
    CONSTRAINT CK_TaiKhoan_SoDu CHECK (SoDu >= 0),

    CONSTRAINT CK_TaiKhoan_TrangThai
        CHECK (TrangThai IN (N'Hoạt động', N'Bị khóa', N'Đóng')),

    CONSTRAINT CK_TaiKhoan_NganHangNoiBo
        CHECK (MaNganHang = 'SMART'),

    CONSTRAINT FK_TaiKhoan_KhachHang
        FOREIGN KEY (MaKhachHang)
        REFERENCES dbo.KhachHang(MaKhachHang),

    CONSTRAINT FK_TaiKhoan_ChiNhanh
        FOREIGN KEY (MaChiNhanh)
        REFERENCES dbo.ChiNhanh(MaChiNhanh),

    CONSTRAINT FK_TaiKhoan_HanMuc
        FOREIGN KEY (MaHanMuc)
        REFERENCES dbo.[HanMucGiaoDịch](MaHanMuc),

    CONSTRAINT FK_TaiKhoan_NganHang
        FOREIGN KEY (MaNganHang)
        REFERENCES dbo.NganHang(MaNganHang)
);
GO

-- ----------------------------------------------------------
-- 1.7  Nhân viên
-- ----------------------------------------------------------
CREATE TABLE dbo.NhanVien (
    MaNhanVien   VARCHAR(20)    NOT NULL
        CONSTRAINT PK_NhanVien PRIMARY KEY,
    HoTen        NVARCHAR(100)  NOT NULL,
    SoDienThoai  VARCHAR(11)    NULL,
    DiaChi       NVARCHAR(200)  NULL,
    ChucVu       NVARCHAR(50)   NOT NULL,
    MaChiNhanh   VARCHAR(10)    NOT NULL,
    TenDangNhap  VARCHAR(50)    NULL,

    CONSTRAINT FK_NhanVien_ChiNhanh
        FOREIGN KEY (MaChiNhanh)
        REFERENCES dbo.ChiNhanh(MaChiNhanh),

    CONSTRAINT FK_NhanVien_DangNhap
        FOREIGN KEY (TenDangNhap)
        REFERENCES dbo.TaiKhoanDangNhap_VaiTro(TenDangNhap)
);
GO

-- ----------------------------------------------------------
-- 1.8  Loại giao dịch
-- ----------------------------------------------------------
CREATE TABLE dbo.LoaiGiaoDich (
    MaLoaiGD   VARCHAR(10)    NOT NULL
        CONSTRAINT PK_LoaiGiaoDich PRIMARY KEY,
    TenLoaiGD  NVARCHAR(50)   NOT NULL
        CONSTRAINT UQ_LoaiGiaoDich_Ten UNIQUE
);
GO

-- ----------------------------------------------------------
-- 1.9  Giao dịch chuyển tiền
--      MaTaiKhoanDich NULL nếu người nhận ở ngân hàng khác.
--      MaNganHangDich DEFAULT 'SMART' vì TellerCashController
--      INSERT chỉ 8 cột (không truyền MaNganHangDich, SoTaiKhoanDich).
--      SoTaiKhoanDich DEFAULT 'N/A' để không vi phạm NOT NULL
--      khi GDV nạp/rút tiền mặt tại quầy.
-- ----------------------------------------------------------
CREATE TABLE dbo.GiaoDichChuyenTien (
    MaGiaoDich              VARCHAR(30)    NOT NULL
        CONSTRAINT PK_GiaoDichChuyenTien PRIMARY KEY,
    MaTaiKhoanNguon         VARCHAR(20)    NOT NULL,
    MaTaiKhoanDich          VARCHAR(20)    NULL,
    MaNganHangDich          VARCHAR(20)    NOT NULL
        CONSTRAINT DF_GiaoDich_NganHangDich DEFAULT ('SMART'),
    SoTaiKhoanDich          VARCHAR(30)    NOT NULL
        CONSTRAINT DF_GiaoDich_SoTKDich DEFAULT ('N/A'),
    TenNguoiNhan            NVARCHAR(100)  NULL,
    SoTien                  DECIMAL(18,2)  NOT NULL,

    NgayGiaoDich            DATETIME       NOT NULL
        CONSTRAINT DF_GiaoDich_NgayGD DEFAULT GETDATE(),

    NoiDung                 NVARCHAR(255)  NULL,
    MaLoaiGD                VARCHAR(10)    NOT NULL,
    TrangThai               NVARCHAR(30)   NOT NULL,
    MaThamChieuLienNganHang VARCHAR(50)    NULL,
    PhanHoiLienNganHang     NVARCHAR(255)  NULL,

    CONSTRAINT CK_GiaoDich_SoTien
        CHECK (SoTien > 0),

    CONSTRAINT CK_GiaoDich_TrangThai
        CHECK (TrangThai IN (
            N'Chờ xử lý', N'Thành công', N'Thất bại', N'Đã hủy'
        )),

    CONSTRAINT CK_GiaoDich_TaiKhoanKhac
        CHECK (
            MaTaiKhoanDich IS NULL
            OR MaTaiKhoanNguon <> MaTaiKhoanDich
        ),

    -- Nội bộ SMART: phải có MaTaiKhoanDich
    -- Ngoài ngân hàng: MaTaiKhoanDich phải NULL
    -- Tiền mặt quầy (NT01/RT01/TT01): bỏ qua rule này khi SoTaiKhoanDich='N/A'
    CONSTRAINT CK_GiaoDich_DichTheoNganHang
        CHECK (
            SoTaiKhoanDich = 'N/A'                                          -- giao dịch tiền mặt quầy
            OR (MaNganHangDich = 'SMART' AND MaTaiKhoanDich IS NOT NULL)    -- chuyển nội bộ
            OR (MaNganHangDich <> 'SMART' AND MaTaiKhoanDich IS NULL)       -- chuyển liên ngân hàng
        ),

    -- FK MaTaiKhoanNguon/Dich không khai báo ở đây vì
    -- TellerCashController (RT01/TT01) truyền SoTaiKhoan thay vì
    -- MaTaiKhoan vào 2 cột này — bỏ FK tránh lỗi 547.
    -- Tính toàn vẹn cho chuyển tiền thông thường được đảm bảo
    -- bởi SP_ThucHienChuyenTien (khóa UPDLOCK, kiểm tra tồn tại).

    CONSTRAINT FK_GiaoDich_NganHangDich
        FOREIGN KEY (MaNganHangDich)
        REFERENCES dbo.NganHang(MaNganHang),

    CONSTRAINT FK_GiaoDich_LoaiGD
        FOREIGN KEY (MaLoaiGD)
        REFERENCES dbo.LoaiGiaoDich(MaLoaiGD)
);
GO

-- ----------------------------------------------------------
-- 1.10 Lịch sử giao dịch / audit trail
--      TrangThai đọc từ GiaoDichChuyenTien qua JOIN (không
--      lưu trùng), SoTienThayDoi âm = trừ, dương = cộng.
-- ----------------------------------------------------------
CREATE TABLE dbo.LichSuGiaoDich (
    MaLog               BIGINT          IDENTITY(1,1) NOT NULL
        CONSTRAINT PK_LichSuGiaoDich PRIMARY KEY,
    MaGiaoDich          VARCHAR(30)     NOT NULL,
    SoTaiKhoanLienQuan  VARCHAR(30)     NOT NULL,
    HanhDong            NVARCHAR(100)   NOT NULL,
    SoTienThayDoi       DECIMAL(18,2)   NOT NULL,
    SoDuSauThayDoi      DECIMAL(18,2)   NOT NULL,

    ThoiGianGhiLog      DATETIME        NOT NULL
        CONSTRAINT DF_LichSuGD_ThoiGian DEFAULT GETDATE(),

    CONSTRAINT FK_LichSuGD_GiaoDich
        FOREIGN KEY (MaGiaoDich)
        REFERENCES dbo.GiaoDichChuyenTien(MaGiaoDich)
);
GO


-- ============================================================
-- BƯỚC 2: SEQUENCE – TỰ SINH MÃ
-- ============================================================

CREATE SEQUENCE dbo.Seq_MaKhachHang
    AS BIGINT START WITH 1 INCREMENT BY 1;
GO

CREATE SEQUENCE dbo.Seq_MaTaiKhoan
    AS BIGINT START WITH 1 INCREMENT BY 1;
GO

CREATE SEQUENCE dbo.Seq_SoTaiKhoan
    AS BIGINT
    START WITH 100000000001
    INCREMENT BY 1
    MAXVALUE 999999999999999;
GO

CREATE SEQUENCE dbo.Seq_MaNhanVien
    AS BIGINT START WITH 1 INCREMENT BY 1;
GO

CREATE SEQUENCE dbo.Seq_MaChiNhanh
    AS BIGINT START WITH 1 INCREMENT BY 1;
GO

CREATE SEQUENCE dbo.Seq_MaHanMuc
    AS BIGINT START WITH 1 INCREMENT BY 1;
GO

-- ============================================================
-- BƯỚC 3: INDEX – TỐI ƯU TRUY VẤN
-- ============================================================

-- Tìm kiếm giao dịch theo ngày (báo cáo, lọc)
CREATE NONCLUSTERED INDEX IX_GiaoDich_NgayGiaoDich
    ON dbo.GiaoDichChuyenTien(NgayGiaoDich);
GO

-- Kiểm tra hạn mức ngày, sao kê giao dịch theo tài khoản nguồn
CREATE NONCLUSTERED INDEX IX_GiaoDich_Nguon_Ngay
    ON dbo.GiaoDichChuyenTien(MaTaiKhoanNguon, NgayGiaoDich DESC)
    INCLUDE (SoTien, TrangThai);
GO

-- Tìm giao dịch nhận (chuyển đến tài khoản nội bộ)
CREATE NONCLUSTERED INDEX IX_GiaoDich_TaiKhoanDich
    ON dbo.GiaoDichChuyenTien(MaTaiKhoanDich)
    WHERE MaTaiKhoanDich IS NOT NULL;
GO

-- Liên kết giao dịch liên ngân hàng theo ngân hàng + số tài khoản đích
CREATE NONCLUSTERED INDEX IX_GiaoDich_NganHang_SoTKDich
    ON dbo.GiaoDichChuyenTien(MaNganHangDich, SoTaiKhoanDich);
GO

-- Lấy danh sách tài khoản theo khách hàng (trang chủ khách hàng, CSKH)
CREATE NONCLUSTERED INDEX IX_TaiKhoan_MaKhachHang
    ON dbo.TaiKhoanNganHang(MaKhachHang)
    INCLUDE (SoTaiKhoan, SoDu, TrangThai, MaChiNhanh);
GO

-- Lấy danh sách tài khoản theo chi nhánh (Admin, GDV)
CREATE NONCLUSTERED INDEX IX_TaiKhoan_MaChiNhanh
    ON dbo.TaiKhoanNganHang(MaChiNhanh, TrangThai);
GO

-- Sao kê theo số tài khoản + thời gian (khách hàng, GDV, CSKH)
CREATE NONCLUSTERED INDEX IX_LichSuGD_STK_ThoiGian
    ON dbo.LichSuGiaoDich(SoTaiKhoanLienQuan, ThoiGianGhiLog DESC)
    INCLUDE (MaGiaoDich, HanhDong, SoTienThayDoi, SoDuSauThayDoi);
GO

-- Đảm bảo idempotent callback liên ngân hàng
CREATE UNIQUE NONCLUSTERED INDEX UX_GiaoDich_MaThamChieuLienNganHang
    ON dbo.GiaoDichChuyenTien(MaThamChieuLienNganHang)
    WHERE MaThamChieuLienNganHang IS NOT NULL;
GO
