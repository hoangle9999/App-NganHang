-- DỰ ÁN CUỐI KỲ: ỨNG DỤNG CHUYỂN TIỀN NGÂN HÀNG 
-- THÀNH VIÊN THỰC HIỆN: ĐỖ LÊ GIA HÂN (VAI TRÒ: THIẾT KẾ DỮ LIỆU)

-- XÓA DATABASE CŨ ĐỂ LÀM SẠCH HỆ THỐNG
USE master;
GO
IF EXISTS (SELECT * FROM sys.databases WHERE name = 'DB_ChuyenTienNganHang')
BEGIN
    ALTER DATABASE DB_ChuyenTienNganHang SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE DB_ChuyenTienNganHang;
END
GO

-- 1. KHỞI TẠO CƠ SỞ DỮ LIỆU
CREATE DATABASE DB_ChuyenTienNganHang;
GO
USE DB_ChuyenTienNganHang;
GO
-- 2. TẠO CÁC BẢNG DỮ LIỆU & RÀNG BUỘC (CONSTRAINTS)

-- Bảng: Tài Khoản Đăng Nhập / Vai Trò
CREATE TABLE TaiKhoanDangNhap_VaiTro (
    TenDangNhap VARCHAR(50) PRIMARY KEY,
    MatKhau VARCHAR(255) NOT NULL,
    RoleName NVARCHAR(30) NOT NULL CHECK (RoleName IN (N'Admin', N'Nhân viên CSKH', N'Giao dịch viên', N'Khách hàng')),
    MaNguoiDung VARCHAR(20) NOT NULL 
);

-- Bảng: Chi Nhánh Ngân Hàng
CREATE TABLE ChiNhanh (
    MaChiNhanh VARCHAR(10) PRIMARY KEY,
    TenChiNhanh NVARCHAR(100) NOT NULL,
    DiaChi NVARCHAR(200) NOT NULL
);

-- Bảng: Khách Hàng
CREATE TABLE KhachHang (
    MaKhachHang VARCHAR(20) PRIMARY KEY,
    HoTen NVARCHAR(100) NOT NULL,
    SoCCCD VARCHAR(12) NOT NULL,
    SoDienThoai VARCHAR(11) NOT NULL,
    Email VARCHAR(100) NULL,
    TenDangNhap VARCHAR(50) NULL,
        
    -- [RÀNG BUỘC]: Duy nhất CCCD, SĐT và Khóa ngoại tài khoản đăng nhập
    CONSTRAINT UQ_KhachHang_CCCD UNIQUE (SoCCCD),
    CONSTRAINT UQ_KhachHang_SDT UNIQUE (SoDienThoai),
    CONSTRAINT FK_KhachHang_DangNhap FOREIGN KEY (TenDangNhap) REFERENCES TaiKhoanDangNhap_VaiTro(TenDangNhap)
);

-- Bảng: Hạn Mức Giao Dịch
CREATE TABLE HanMucGiaoDịch (
    MaHanMuc VARCHAR(10) PRIMARY KEY,
    TenHanMuc NVARCHAR(50) NOT NULL,
    HanMucNgay DECIMAL(18, 2) NOT NULL CONSTRAINT CK_HanMuc_GiaTri CHECK (HanMucNgay > 0)
);

-- Bảng: Tài Khoản Ngân Hàng
CREATE TABLE TaiKhoanNganHang (
    MaTaiKhoan VARCHAR(20) PRIMARY KEY,
    MaKhachHang VARCHAR(20) NOT NULL,
    SoTaiKhoan VARCHAR(15) NOT NULL,
    
    -- [RÀNG BUỘC]: Số dư mặc định = 0 và luôn >= 0
    SoDu DECIMAL(18, 2) NOT NULL CONSTRAINT DF_TaiKhoan_SoDu DEFAULT 0 
                         CONSTRAINT CK_TaiKhoan_SoDu CHECK (SoDu >= 0),
                         
    TrangThai NVARCHAR(20) NOT NULL CONSTRAINT DF_TaiKhoan_TrangThai DEFAULT N'Hoạt động',
    MaChiNhanh VARCHAR(10) NOT NULL,
    MaHanMuc VARCHAR(10) NULL,

    CONSTRAINT UQ_TaiKhoan_STK UNIQUE (SoTaiKhoan),
    CONSTRAINT FK_TaiKhoan_KhachHang FOREIGN KEY (MaKhachHang) REFERENCES KhachHang(MaKhachHang),
    CONSTRAINT FK_TaiKhoan_ChiNhanh FOREIGN KEY (MaChiNhanh) REFERENCES ChiNhanh(MaChiNhanh),
    CONSTRAINT FK_TaiKhoan_HanMuc FOREIGN KEY (MaHanMuc) REFERENCES HanMucGiaoDịch(MaHanMuc)
);

-- Bảng: Loại Giao Dịch
CREATE TABLE LoaiGiaoDich (
    MaLoaiGD VARCHAR(10) PRIMARY KEY,
    TenLoaiGD NVARCHAR(50) NOT NULL 
);

-- Bảng: Giao Dịch Chuyển Tiền
CREATE TABLE GiaoDichChuyenTien (
    MaGiaoDich VARCHAR(30) PRIMARY KEY,
    MaTaiKhoanNguon VARCHAR(20) NOT NULL, 
    MaTaiKhoanDich VARCHAR(20) NOT NULL, 
    SoTien DECIMAL(18, 2) NOT NULL CONSTRAINT CK_GiaoDich_SoTien CHECK (SoTien > 0),
    
    NgayGiaoDich DATETIME NOT NULL CONSTRAINT DF_GiaoDich_NgayGD DEFAULT GETDATE(),
    NoiDung NVARCHAR(255) NULL,
    MaLoaiGD VARCHAR(10) NOT NULL,
    
    TrangThai NVARCHAR(30) NOT NULL CONSTRAINT CK_GiaoDich_TrangThai 
                         CHECK (TrangThai IN (N'Chờ xử lý', N'Thành công', N'Thất bại', N'Đã hủy')),

    CONSTRAINT FK_GiaoDich_TaiKhoanNguon FOREIGN KEY (MaTaiKhoanNguon) REFERENCES TaiKhoanNganHang(MaTaiKhoan),
    CONSTRAINT FK_GiaoDich_TaiKhoanDich FOREIGN KEY (MaTaiKhoanDich) REFERENCES TaiKhoanNganHang(MaTaiKhoan),
    CONSTRAINT FK_GiaoDich_LoaiGD FOREIGN KEY (MaLoaiGD) REFERENCES LoaiGiaoDich(MaLoaiGD)
);

-- Bảng: Nhân Viên Ngân Hàng
CREATE TABLE NhanVien (
    MaNhanVien VARCHAR(20) PRIMARY KEY,
    HoTen NVARCHAR(100) NOT NULL,
    ChucVu NVARCHAR(50) NOT NULL,
    MaChiNhanh VARCHAR(10) NOT NULL,
    TenDangNhap VARCHAR(50) NULL,
    
    CONSTRAINT FK_NhanVien_ChiNhanh FOREIGN KEY (MaChiNhanh) REFERENCES ChiNhanh(MaChiNhanh),
    CONSTRAINT FK_NhanVien_DangNhap FOREIGN KEY (TenDangNhap) REFERENCES TaiKhoanDangNhap_VaiTro(TenDangNhap)
);

-- Bảng: Lịch Sử Giao Dịch (Audit Trail)
CREATE TABLE LichSuGiaoDich (
    MaLog INT IDENTITY(1,1) PRIMARY KEY,
    MaGiaoDich VARCHAR(30) NOT NULL,
    SoTaiKhoanLienQuan VARCHAR(15) NOT NULL,
    HanhDong NVARCHAR(50) NOT NULL, 
    SoTienThayDoi DECIMAL(18, 2) NOT NULL,
    SoDuSauThayDoi DECIMAL(18, 2) NOT NULL,
    ThoiGianGhiLog DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_LichSuGD_GiaoDich FOREIGN KEY (MaGiaoDich) REFERENCES GiaoDichChuyenTien(MaGiaoDich)
);
GO

-- 3. TẠO CÁC CHỈ MỤC (INDEXES) TỐI ƯU TRUY VẤN
CREATE NONCLUSTERED INDEX IX_GiaoDich_NgayGiaoDich ON GiaoDichChuyenTien(NgayGiaoDich);
CREATE NONCLUSTERED INDEX IX_GiaoDich_STK_Nguon ON GiaoDichChuyenTien(MaTaiKhoanNguon);
CREATE NONCLUSTERED INDEX IX_TaiKhoan_MaKhachHang ON TaiKhoanNganHang(MaKhachHang);
CREATE NONCLUSTERED INDEX IX_LichSuGD_STK ON LichSuGiaoDich(SoTaiKhoanLienQuan);
CREATE NONCLUSTERED INDEX IX_KhachHang_SoDienThoai ON KhachHang(SoDienThoai);
GO


-- 4. TẠO CÁC BỘ KÍCH HOẠT (TRIGGERS)
-- Trigger 1: Kiểm tra hạn mức giao dịch trong ngày
CREATE TRIGGER TRG_KiemTra_HanMucNgay
ON GiaoDichChuyenTien
INSTEAD OF INSERT
AS
BEGIN
    DECLARE @MaTaiKhoanNguon VARCHAR(20), @SoTienGiaoDich DECIMAL(18, 2);
    DECLARE @HanMucNgay DECIMAL(18, 2), @TongTienDaChuyenTrongNgay DECIMAL(18, 2);

    SELECT @MaTaiKhoanNguon = MaTaiKhoanNguon, @SoTienGiaoDich = SoTien FROM inserted;

    SELECT @HanMucNgay = hm.HanMucNgay
    FROM TaiKhoanNganHang tk
    JOIN HanMucGiaoDịch hm ON tk.MaHanMuc = hm.MaHanMuc
    WHERE tk.MaTaiKhoan = @MaTaiKhoanNguon;

    IF @HanMucNgay IS NOT NULL
    BEGIN
        SELECT @TongTienDaChuyenTrongNgay = ISNULL(SUM(SoTien), 0)
        FROM GiaoDichChuyenTien
        WHERE MaTaiKhoanNguon = @MaTaiKhoanNguon 
          AND TrangThai = N'Thành công'
          AND CAST(NgayGiaoDich AS DATE) = CAST(GETDATE() AS DATE);

        IF (@TongTienDaChuyenTrongNgay + @SoTienGiaoDich > @HanMucNgay)
        BEGIN
            RAISERROR(N'Giao dịch thất bại: Vượt quá hạn mức chuyển tiền cho phép trong ngày!', 16, 1);
            ROLLBACK TRANSACTION;
            RETURN;
        END
    END

    INSERT INTO GiaoDichChuyenTien (MaGiaoDich, MaTaiKhoanNguon, MaTaiKhoanDich, SoTien, NgayGiaoDich, NoiDung, MaLoaiGD, TrangThai)
    SELECT MaGiaoDich, MaTaiKhoanNguon, MaTaiKhoanDich, SoTien, NgayGiaoDich, NoiDung, MaLoaiGD, TrangThai
    FROM inserted;
END;
GO

-- Trigger 2: Tự động ghi nhận lịch sử kiểm toán (Audit Trail)
CREATE TRIGGER TRG_GhiNhan_LichSuGiaoDich
ON GiaoDichChuyenTien
AFTER INSERT
AS
BEGIN
    DECLARE @MaGiaoDich VARCHAR(30), @MaTK_Nguon VARCHAR(20), @MaTK_Dich VARCHAR(20), @SoTien DECIMAL(18,2), @TrangThai NVARCHAR(30);
    DECLARE @SoDuNguonMoi DECIMAL(18,2), @SoDuDichMoi DECIMAL(18,2);
    DECLARE @SoTK_Nguon VARCHAR(15), @SoTK_Dich VARCHAR(15);

    SELECT @MaGiaoDich = MaGiaoDich, @MaTK_Nguon = MaTaiKhoanNguon, @MaTK_Dich = MaTaiKhoanDich, @SoTien = SoTien, @TrangThai = TrangThai
    FROM inserted;

    IF @TrangThai = N'Thành công'
    BEGIN
        SELECT @SoTK_Nguon = SoTaiKhoan, @SoDuNguonMoi = SoDu FROM TaiKhoanNganHang WHERE MaTaiKhoan = @MaTK_Nguon;
        SELECT @SoTK_Dich = SoTaiKhoan, @SoDuDichMoi = SoDu FROM TaiKhoanNganHang WHERE MaTaiKhoan = @MaTK_Dich;

        INSERT INTO LichSuGiaoDich (MaGiaoDich, SoTaiKhoanLienQuan, HanhDong, SoTienThayDoi, SoDuSauThayDoi)
        VALUES (@MaGiaoDich, @SoTK_Nguon, N'Trừ tiền chuyển khoản', -@SoTien, @SoDuNguonMoi);

        INSERT INTO LichSuGiaoDich (MaGiaoDich, SoTaiKhoanLienQuan, HanhDong, SoTienThayDoi, SoDuSauThayDoi)
        VALUES (@MaGiaoDich, @SoTK_Dich, N'Cộng tiền nhận khoản', @SoTien, @SoDuDichMoi);
    END
END;
GO


-- 5. TẠO CÁC STORED PROCEDURES (THỦ TỤC LƯU TRỮ) KÈM TRANSACTION
-- SP 1: Chuyển tiền an toàn sử dụng Transaction (ACID)
CREATE PROCEDURE SP_ThucHienChuyenTien
    @MaGiaoDich VARCHAR(30),
    @MaTaiKhoanNguon VARCHAR(20),
    @MaTaiKhoanDich VARCHAR(20),
    @SoTien DECIMAL(18, 2),
    @NoiDung NVARCHAR(255),
    @MaLoaiGD VARCHAR(10)
AS
BEGIN
    BEGIN TRANSACTION;
    
    BEGIN TRY
        -- Kiểm tra tài khoản nguồn
        IF NOT EXISTS (SELECT 1 FROM TaiKhoanNganHang WHERE MaTaiKhoan = @MaTaiKhoanNguon AND TrangThai = N'Hoạt động')
        BEGIN
            RAISERROR(N'Tài khoản nguồn không tồn tại hoặc đã bị khóa!', 16, 1);
            ROLLBACK TRANSACTION;
            RETURN;
        END

        -- Kiểm tra tài khoản đích
        IF NOT EXISTS (SELECT 1 FROM TaiKhoanNganHang WHERE MaTaiKhoan = @MaTaiKhoanDich AND TrangThai = N'Hoạt động')
        BEGIN
            RAISERROR(N'Tài khoản đích không tồn tại hoặc đã bị khóa!', 16, 1);
            ROLLBACK TRANSACTION;
            RETURN;
        END

        -- Kiểm tra số dư tài khoản nguồn
        DECLARE @SoDuHienTai DECIMAL(18, 2);
        SELECT @SoDuHienTai = SoDu FROM TaiKhoanNganHang WHERE MaTaiKhoan = @MaTaiKhoanNguon;

        IF @SoDuHienTai < @SoTien
        BEGIN
            RAISERROR(N'Số dư trong tài khoản nguồn không đủ để thực hiện giao dịch!', 16, 1);
            ROLLBACK TRANSACTION;
            RETURN;
        END

        -- Trừ tiền nguồn & Cộng tiền đích
        UPDATE TaiKhoanNganHang SET SoDu = SoDu - @SoTien WHERE MaTaiKhoan = @MaTaiKhoanNguon;
        UPDATE TaiKhoanNganHang SET SoDu = SoDu + @SoTien WHERE MaTaiKhoan = @MaTaiKhoanDich;

        -- Thêm giao dịch (Trigger hạn mức sẽ kích hoạt tại đây)
        INSERT INTO GiaoDichChuyenTien (MaGiaoDich, MaTaiKhoanNguon, MaTaiKhoanDich, SoTien, NoiDung, MaLoaiGD, TrangThai)
        VALUES (@MaGiaoDich, @MaTaiKhoanNguon, @MaTaiKhoanDich, @SoTien, @NoiDung, @MaLoaiGD, N'Thành công');

        COMMIT TRANSACTION;
        PRINT N'Giao dịch chuyển tiền thành công!';
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0
            ROLLBACK TRANSACTION;
            
        DECLARE @ErrorMessage NVARCHAR(4000) = ERROR_MESSAGE();
        RAISERROR(@ErrorMessage, 16, 1);
    END CATCH
END;
GO

-- SP 2: Tra cứu lịch sử giao dịch
CREATE PROCEDURE SP_TraCuuLichSuGiaoDich
    @SoTaiKhoan VARCHAR(15)
AS
BEGIN
    SELECT 
        gd.MaGiaoDich,
        gd.SoTien,
        gd.NgayGiaoDich,
        gd.NoiDung,
        gd.TrangThai,
        gd.MaTaiKhoanNguon,
        gd.MaTaiKhoanDich
    FROM GiaoDichChuyenTien gd
    JOIN TaiKhoanNganHang tk ON gd.MaTaiKhoanNguon = tk.MaTaiKhoan
    WHERE tk.SoTaiKhoan = @SoTaiKhoan
    ORDER BY gd.NgayGiaoDich DESC;
END;
GO