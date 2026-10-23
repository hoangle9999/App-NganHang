USE DB_ChuyenTienNganHang;
GO

-- Migrare an toan cho database da ton tai: trang thai nay duoc app kiem tra moi lan truy cap DB.
IF COL_LENGTH(N'dbo.TaiKhoanDangNhap_VaiTro', N'QuyenHoatDong') IS NULL
    ALTER TABLE dbo.TaiKhoanDangNhap_VaiTro
        ADD QuyenHoatDong BIT NOT NULL
            CONSTRAINT DF_TaiKhoan_QuyenHoatDong DEFAULT (1) WITH VALUES;
GO


-- ============================================================
-- PHẦN 1: DATABASE ROLE CHO ADMIN
-- ============================================================

IF DATABASE_PRINCIPAL_ID(N'role_BankAdmin') IS NULL
    CREATE ROLE role_BankAdmin AUTHORIZATION dbo;
GO

-- Admin có quyền đầy đủ trên toàn schema dbo
GRANT SELECT, INSERT, UPDATE, DELETE ON SCHEMA::dbo TO role_BankAdmin;
GRANT EXECUTE                         ON SCHEMA::dbo TO role_BankAdmin;
GRANT VIEW DEFINITION                 ON SCHEMA::dbo TO role_BankAdmin;
GO

-- Trả về 1 nếu số điện thoại có 10 hoặc 11 chữ số; ngược lại trả về 0.
CREATE OR ALTER FUNCTION dbo.fn_Admin_SoDienThoaiHopLe
(
    @SoDienThoai VARCHAR(20)
)
RETURNS BIT
AS
BEGIN
    RETURN CASE
        WHEN @SoDienThoai IS NOT NULL
         AND LEN(@SoDienThoai) BETWEEN 10 AND 11
         AND @SoDienThoai NOT LIKE '%[^0-9]%'
        THEN 1 ELSE 0
    END;
END;
GO

-- ============================================================
-- PHẦN 3: STORED PROCEDURES CHO ADMIN
-- ============================================================

-- ----------------------------------------------------------
-- 3.1  Dashboard tổng quan toàn hệ thống hoặc theo chi nhánh
--      Giao diện: AdminViewController
--      Java gọi: {call sp_GetDashboardDB(?)}  (NULL = tất cả)
--      SP sp_GetDashboardDB đã tạo trong 1_TaoBang.sql.
--      SP dưới đây là alias rõ ràng cho Admin.
-- ----------------------------------------------------------
CREATE OR ALTER PROCEDURE dbo.sp_Admin_Dashboard
AS
BEGIN
    SET NOCOUNT ON;

    SELECT
        -- Tổng số tài khoản đăng nhập
        (
            SELECT COUNT(*)
            FROM dbo.TaiKhoanDangNhap_VaiTro
        ) AS TongTaiKhoan,

        -- Tổng số nhân viên
        (
            SELECT COUNT(*)
            FROM dbo.NhanVien
        ) AS TongNhanVien,

        -- Tổng số chi nhánh
        (
            SELECT COUNT(*)
            FROM dbo.ChiNhanh
        ) AS TongChiNhanh,

        -- Tổng số giao dịch
        (
            SELECT COUNT(*)
            FROM dbo.GiaoDichChuyenTien
        ) AS TongGiaoDich,

        -- Tổng số tiền giao dịch
        (
            SELECT COALESCE(SUM(SoTien), 0)
            FROM dbo.GiaoDichChuyenTien
            WHERE TrangThai = N'Thành công'
        ) AS TongTienGiaoDich,

        -- Số tài khoản ngân hàng đang bị khóa
        (
            SELECT COUNT(*)
            FROM dbo.TaiKhoanNganHang
            WHERE TrangThai = N'Bị khóa'
        ) AS TongCanhBao;
END;
GO

-- ----------------------------------------------------------
-- 3.2  Danh sách khách hàng, tìm kiếm đa tiêu chí
--      Giao diện: AdminAccountController
--      Java gọi trực tiếp qua KhachHangDAOImpl (SELECT + LIKE)
-- ----------------------------------------------------------

-- ----------------------------------------------------------
-- 3.3  Tạo khách hàng mới (Admin tạo thay cho KH)
--      Giao diện: AdminAccountController
--      Java gọi qua DangKyDAOImpl.taoKhachHangTuNhanVien()
--      SP này chuẩn hoá quy trình phía DB khi Admin tạo.
-- ----------------------------------------------------------
CREATE OR ALTER PROCEDURE dbo.SP_Admin_DanhSachTaiKhoanDangNhap
AS
BEGIN
    SET NOCOUNT ON;
    SELECT TenDangNhap, RoleName, MaNguoiDung, QuyenHoatDong
    FROM dbo.TaiKhoanDangNhap_VaiTro
    ORDER BY RoleName, TenDangNhap;
END;
GO

-- Thu hoi/khoi phuc quyen tai khoan app. Khong cho phep Admin khoa tai khoan Admin.
CREATE OR ALTER PROCEDURE dbo.SP_Admin_DatTrangThaiQuyen
    @TenDangNhap VARCHAR(50),
    @ChoPhep BIT
AS
BEGIN
    SET NOCOUNT ON;
    IF EXISTS (SELECT 1 FROM dbo.TaiKhoanDangNhap_VaiTro WHERE TenDangNhap=@TenDangNhap AND RoleName=N'Admin')
        THROW 52150, N'Khong the thu hoi quyen tai khoan Admin bang thao tac nay.', 1;

    UPDATE dbo.TaiKhoanDangNhap_VaiTro
    SET QuyenHoatDong=@ChoPhep
    WHERE TenDangNhap=@TenDangNhap;
    IF @@ROWCOUNT=0 THROW 52151, N'Khong tim thay tai khoan dang nhap.', 1;
END;
GO

CREATE OR ALTER PROCEDURE dbo.SP_Admin_TaoTaiKhoanHeThong
    @TenDangNhap VARCHAR(50),
    @MatKhau VARCHAR(255),
    @RoleName NVARCHAR(30),
    @HoTen NVARCHAR(100),
    @SoCCCD VARCHAR(12) = NULL,
    @SoDienThoai VARCHAR(11),
    @Email VARCHAR(100) = NULL,
    @DiaChi NVARCHAR(200) = NULL,
    @MaChiNhanh VARCHAR(10),
    @MaHanMuc VARCHAR(10) = 'HM001'
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    IF NULLIF(LTRIM(RTRIM(@TenDangNhap)), '') IS NULL THROW 52101, N'Tên đăng nhập không được để trống.', 1;
    IF NULLIF(LTRIM(RTRIM(@MatKhau)), '') IS NULL THROW 52102, N'Mật khẩu không được để trống.', 1;
    IF NULLIF(LTRIM(RTRIM(@HoTen)), N'') IS NULL THROW 52103, N'Họ tên không được để trống.', 1;
    IF dbo.fn_Admin_SoDienThoaiHopLe(@SoDienThoai) = 0
        THROW 52104, N'Số điện thoại phải gồm 10 hoặc 11 chữ số.', 1;
    IF @RoleName NOT IN (N'Admin', N'Nhân viên CSKH', N'Giao dịch viên', N'Khách hàng')
        THROW 52105, N'Vai trò không hợp lệ.', 1;
    IF NOT EXISTS (SELECT 1 FROM dbo.ChiNhanh WHERE MaChiNhanh = @MaChiNhanh)
        THROW 52106, N'Chi nhánh không tồn tại.', 1;
    IF EXISTS (SELECT 1 FROM dbo.TaiKhoanDangNhap_VaiTro WHERE TenDangNhap = @TenDangNhap)
        THROW 52107, N'Tên đăng nhập đã tồn tại.', 1;

    IF @RoleName = N'Khách hàng'
    BEGIN
        IF @SoCCCD IS NULL OR LEN(@SoCCCD) <> 12 OR @SoCCCD LIKE '%[^0-9]%'
            THROW 52108, N'CCCD khách hàng phải gồm đúng 12 chữ số.', 1;
        IF EXISTS (SELECT 1 FROM dbo.KhachHang WHERE SoCCCD = @SoCCCD)
            THROW 52109, N'CCCD đã được đăng ký.', 1;
        IF EXISTS (SELECT 1 FROM dbo.KhachHang WHERE SoDienThoai = @SoDienThoai)
            THROW 52110, N'Số điện thoại đã được đăng ký.', 1;
        IF NOT EXISTS (SELECT 1 FROM dbo.[HanMucGiaoDịch] WHERE MaHanMuc = @MaHanMuc)
            THROW 52111, N'Gói hạn mức không tồn tại.', 1;
    END
    ELSE
    BEGIN
        IF EXISTS (SELECT 1 FROM dbo.NhanVien WHERE SoDienThoai = @SoDienThoai)
            THROW 52112, N'Số điện thoại nhân viên đã tồn tại.', 1;
    END;

    DECLARE @MaNguoiDung VARCHAR(20), @MaTaiKhoan VARCHAR(20), @SoTaiKhoan VARCHAR(15);
    BEGIN TRY
        BEGIN TRANSACTION;

        IF @RoleName = N'Khách hàng'
        BEGIN
            /* Giữ tương thích với các bộ sinh mã hiện có trong database. */
            EXEC dbo.sp_TaoMaKhachHang @Ma = @MaNguoiDung OUTPUT;
            EXEC dbo.sp_TaoMaTaiKhoan @Ma = @MaTaiKhoan OUTPUT;
            EXEC dbo.sp_TaoSoTaiKhoan @Ma = @SoTaiKhoan OUTPUT;

            INSERT dbo.TaiKhoanDangNhap_VaiTro(TenDangNhap, MatKhau, RoleName, MaNguoiDung)
            VALUES (@TenDangNhap, @MatKhau, @RoleName, @MaNguoiDung);

            INSERT dbo.KhachHang(MaKhachHang, HoTen, SoCCCD, SoDienThoai, Email, DiaChi, TenDangNhap)
            VALUES (@MaNguoiDung, @HoTen, @SoCCCD, @SoDienThoai, @Email, @DiaChi, @TenDangNhap);

            INSERT dbo.TaiKhoanNganHang(MaTaiKhoan, MaKhachHang, SoTaiKhoan, SoDu, TrangThai, MaChiNhanh, MaHanMuc, MaNganHang)
            VALUES (@MaTaiKhoan, @MaNguoiDung, @SoTaiKhoan, 0, N'Hoạt động', @MaChiNhanh, @MaHanMuc, 'SMART');
        END
        ELSE
        BEGIN
            /* Sinh mã NVxxx có khóa giao dịch để tránh hai phiên lấy cùng mã. */
            DECLARE @SoNV INT;
            SELECT @SoNV = ISNULL(MAX(TRY_CONVERT(INT, SUBSTRING(MaNhanVien, 3, 18))), 0) + 1
            FROM dbo.NhanVien WITH (UPDLOCK, HOLDLOCK)
            WHERE MaNhanVien LIKE 'NV%';
            SET @MaNguoiDung = 'NV' + RIGHT(REPLICATE('0', 18) + CONVERT(VARCHAR(18), @SoNV), 18);

            INSERT dbo.TaiKhoanDangNhap_VaiTro(TenDangNhap, MatKhau, RoleName, MaNguoiDung)
            VALUES (@TenDangNhap, @MatKhau, @RoleName, @MaNguoiDung);

            INSERT dbo.NhanVien(MaNhanVien, HoTen, SoDienThoai, DiaChi, ChucVu, MaChiNhanh, TenDangNhap)
            VALUES (@MaNguoiDung, @HoTen, @SoDienThoai, @DiaChi,
                    CASE @RoleName WHEN N'Admin' THEN N'Quản trị viên'
                                   WHEN N'Nhân viên CSKH' THEN N'Nhân viên CSKH'
                                   ELSE N'Giao dịch viên' END,
                    @MaChiNhanh, @TenDangNhap);
        END;

        COMMIT TRANSACTION;
        SELECT @MaNguoiDung AS MaNguoiDungMoi, @MaTaiKhoan AS MaTaiKhoanMoi,
               @SoTaiKhoan AS SoTaiKhoanMoi, @TenDangNhap AS TenDangNhap;
    END TRY
    BEGIN CATCH
        IF XACT_STATE() <> 0 ROLLBACK TRANSACTION;
        THROW;
    END CATCH;
END;
GO

CREATE OR ALTER PROCEDURE dbo.SP_Admin_DoiVaiTroTaiKhoan
    @TenDangNhap VARCHAR(50), @RoleNameMoi NVARCHAR(30)
AS
BEGIN
    SET NOCOUNT ON;
    IF @RoleNameMoi NOT IN (N'Admin', N'Nhân viên CSKH', N'Giao dịch viên', N'Khách hàng')
        THROW 52120, N'Vai trò mới không hợp lệ.', 1;
    IF EXISTS (SELECT 1 FROM dbo.KhachHang kh JOIN dbo.TaiKhoanDangNhap_VaiTro t ON t.TenDangNhap=kh.TenDangNhap
               WHERE t.TenDangNhap=@TenDangNhap) AND @RoleNameMoi <> N'Khách hàng'
        THROW 52121, N'Không thể đổi vai trò khách hàng sang vai trò nhân viên/Admin. Hãy dùng quy trình chuyển đổi hồ sơ riêng.', 1;
    IF EXISTS (SELECT 1 FROM dbo.NhanVien nv WHERE nv.TenDangNhap=@TenDangNhap) AND @RoleNameMoi = N'Khách hàng'
        THROW 52122, N'Không thể đổi vai trò nhân viên sang khách hàng.', 1;
    UPDATE dbo.TaiKhoanDangNhap_VaiTro SET RoleName=@RoleNameMoi WHERE TenDangNhap=@TenDangNhap;
    IF @@ROWCOUNT=0 THROW 52123, N'Không tìm thấy tài khoản đăng nhập.', 1;
    UPDATE dbo.NhanVien SET ChucVu=CASE @RoleNameMoi WHEN N'Admin' THEN N'Quản trị viên'
                  WHEN N'Nhân viên CSKH' THEN N'Nhân viên CSKH' ELSE N'Giao dịch viên' END
    WHERE TenDangNhap=@TenDangNhap;
END;
GO

CREATE OR ALTER PROCEDURE dbo.SP_Admin_DatLaiMatKhau
    @TenDangNhap VARCHAR(50), @MatKhauMoi VARCHAR(255)
AS
BEGIN
    SET NOCOUNT ON;
    IF NULLIF(LTRIM(RTRIM(@MatKhauMoi)), '') IS NULL THROW 52130, N'Mật khẩu mới không được để trống.', 1;
    UPDATE dbo.TaiKhoanDangNhap_VaiTro SET MatKhau=@MatKhauMoi WHERE TenDangNhap=@TenDangNhap;
    IF @@ROWCOUNT=0 THROW 52131, N'Không tìm thấy tài khoản đăng nhập.', 1;
END;
GO

CREATE OR ALTER PROCEDURE dbo.SP_Admin_XoaTaiKhoanDangNhap
    @TenDangNhap VARCHAR(50)
AS
BEGIN
    SET NOCOUNT ON;
    /* Không xóa login đang được hồ sơ KH/NV tham chiếu; tránh hồ sơ mồ côi. */
    IF EXISTS (SELECT 1 FROM dbo.KhachHang WHERE TenDangNhap=@TenDangNhap)
       OR EXISTS (SELECT 1 FROM dbo.NhanVien WHERE TenDangNhap=@TenDangNhap)
        THROW 52140, N'Tài khoản đang gắn với hồ sơ khách hàng/nhân viên. Không thể xóa tại màn hình này; hãy khóa tài khoản hoặc dùng quy trình nghỉ việc/đóng hồ sơ.', 1;
    DELETE dbo.TaiKhoanDangNhap_VaiTro WHERE TenDangNhap=@TenDangNhap;
    IF @@ROWCOUNT=0 THROW 52141, N'Không tìm thấy tài khoản đăng nhập.', 1;
END;
GO

CREATE PROCEDURE dbo.sp_TaoMaKhachHang
    @Ma VARCHAR(20) OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    DECLARE @Seq BIGINT = NEXT VALUE FOR dbo.Seq_MaKhachHang;
    SET @Ma = 'KH' + RIGHT(REPLICATE('0', 8) + CONVERT(VARCHAR(20), @Seq), 8);
END;
GO

CREATE PROCEDURE dbo.sp_TaoMaTaiKhoan
    @Ma VARCHAR(20) OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    DECLARE @Seq BIGINT = NEXT VALUE FOR dbo.Seq_MaTaiKhoan;
    SET @Ma = 'TK' + RIGHT(REPLICATE('0', 8) + CONVERT(VARCHAR(20), @Seq), 8);
END;
GO

-- ============================================================
-- PHẦN 4: NHÂN VIÊN VÀ CHI NHÁNH
-- ============================================================
CREATE OR ALTER PROCEDURE dbo.sp_TaoMaNhanVien
    @Ma VARCHAR(20) OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    DECLARE @Seq BIGINT;
    WHILE 1=1
    BEGIN
        SET @Seq = NEXT VALUE FOR dbo.Seq_MaNhanVien;
        SET @Ma = 'NV' + RIGHT(REPLICATE('0', 8) + CONVERT(VARCHAR(20), @Seq), 8);
        IF NOT EXISTS (SELECT 1 FROM dbo.NhanVien WHERE MaNhanVien=@Ma)
           AND NOT EXISTS (SELECT 1 FROM dbo.TaiKhoanDangNhap_VaiTro WHERE MaNguoiDung=@Ma) BREAK;
    END;
END;
GO

CREATE OR ALTER PROCEDURE dbo.SP_Admin_DanhSachChiNhanh
AS
BEGIN
    SET NOCOUNT ON;
    SELECT MaChiNhanh, TenChiNhanh, DiaChi
    FROM dbo.ChiNhanh
    ORDER BY MaChiNhanh;
END;
GO

CREATE OR ALTER PROCEDURE dbo.SP_Admin_DanhSachNhanVien
AS
BEGIN
    SET NOCOUNT ON;
    SELECT nv.MaNhanVien, nv.HoTen, nv.SoDienThoai, nv.DiaChi, nv.ChucVu,
           nv.MaChiNhanh, cn.TenChiNhanh, nv.TenDangNhap
    FROM dbo.NhanVien nv
    LEFT JOIN dbo.ChiNhanh cn ON cn.MaChiNhanh = nv.MaChiNhanh
    ORDER BY nv.MaNhanVien;
END;
GO

CREATE OR ALTER PROCEDURE dbo.SP_Admin_TaiKhoanNhanVienChuaGan
    @MaNhanVien VARCHAR(20)
AS
BEGIN
    SET NOCOUNT ON;
    SELECT t.TenDangNhap
    FROM dbo.TaiKhoanDangNhap_VaiTro t
    WHERE t.RoleName <> N'Khách hàng'
      AND (NOT EXISTS (SELECT 1 FROM dbo.NhanVien nv WHERE nv.TenDangNhap = t.TenDangNhap)
           OR EXISTS (SELECT 1 FROM dbo.NhanVien nv WHERE nv.MaNhanVien = @MaNhanVien AND nv.TenDangNhap = t.TenDangNhap))
    ORDER BY t.TenDangNhap;
END;
GO

CREATE OR ALTER PROCEDURE dbo.SP_Admin_TaoNhanVien
    @HoTen NVARCHAR(100),
    @SoDienThoai VARCHAR(11),
    @DiaChi NVARCHAR(200) = NULL,
    @ChucVu NVARCHAR(50),
    @MaChiNhanh VARCHAR(10),
    @MaNhanVien VARCHAR(20) OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;
    IF NULLIF(LTRIM(RTRIM(@HoTen)), N'') IS NULL THROW 52201, N'Họ tên không được để trống.', 1;
    IF dbo.fn_Admin_SoDienThoaiHopLe(@SoDienThoai) = 0
        THROW 52202, N'Số điện thoại phải gồm 10 hoặc 11 chữ số.', 1;
    IF @ChucVu NOT IN (N'Admin', N'Nhân viên CSKH', N'Giao dịch viên')
        THROW 52203, N'Chức vụ không hợp lệ.', 1;
    IF NOT EXISTS (SELECT 1 FROM dbo.ChiNhanh WHERE MaChiNhanh = @MaChiNhanh)
        THROW 52204, N'Chi nhánh không tồn tại.', 1;
    IF EXISTS (SELECT 1 FROM dbo.NhanVien WHERE SoDienThoai = @SoDienThoai)
        THROW 52205, N'Số điện thoại nhân viên đã tồn tại.', 1;

    BEGIN TRY
        BEGIN TRANSACTION;
        EXEC dbo.sp_TaoMaNhanVien @Ma = @MaNhanVien OUTPUT;
        INSERT dbo.NhanVien(MaNhanVien, HoTen, SoDienThoai, DiaChi, ChucVu, MaChiNhanh, TenDangNhap)
        VALUES (@MaNhanVien, @HoTen, @SoDienThoai, @DiaChi, @ChucVu, @MaChiNhanh, NULL);
        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF XACT_STATE() <> 0 ROLLBACK TRANSACTION;
        THROW;
    END CATCH;
END;
GO

CREATE OR ALTER PROCEDURE dbo.SP_Admin_CapNhatNhanVien
    @MaNhanVien VARCHAR(20),
    @HoTen NVARCHAR(100),
    @SoDienThoai VARCHAR(11),
    @DiaChi NVARCHAR(200) = NULL,
    @ChucVu NVARCHAR(50),
    @MaChiNhanh VARCHAR(10)
AS
BEGIN
    SET NOCOUNT ON;
    IF NULLIF(LTRIM(RTRIM(@HoTen)), N'') IS NULL THROW 52211, N'Họ tên không được để trống.', 1;
    IF dbo.fn_Admin_SoDienThoaiHopLe(@SoDienThoai) = 0
        THROW 52212, N'Số điện thoại phải gồm 10 hoặc 11 chữ số.', 1;
    IF @ChucVu NOT IN (N'Admin', N'Nhân viên CSKH', N'Giao dịch viên')
        THROW 52213, N'Chức vụ không hợp lệ.', 1;
    IF NOT EXISTS (SELECT 1 FROM dbo.ChiNhanh WHERE MaChiNhanh = @MaChiNhanh)
        THROW 52214, N'Chi nhánh không tồn tại.', 1;
    IF EXISTS (SELECT 1 FROM dbo.NhanVien WHERE SoDienThoai = @SoDienThoai AND MaNhanVien <> @MaNhanVien)
        THROW 52215, N'Số điện thoại nhân viên đã tồn tại.', 1;
    UPDATE dbo.NhanVien SET HoTen=@HoTen, SoDienThoai=@SoDienThoai, DiaChi=@DiaChi,
           ChucVu=@ChucVu, MaChiNhanh=@MaChiNhanh WHERE MaNhanVien=@MaNhanVien;
    IF @@ROWCOUNT=0 THROW 52216, N'Không tìm thấy nhân viên.', 1;
END;
GO

CREATE OR ALTER PROCEDURE dbo.SP_Admin_LienKetTaiKhoanNhanVien
    @MaNhanVien VARCHAR(20),
    @TenDangNhap VARCHAR(50)
AS
BEGIN
    SET NOCOUNT ON;
    IF NOT EXISTS (SELECT 1 FROM dbo.NhanVien WHERE MaNhanVien=@MaNhanVien)
        THROW 52221, N'Không tìm thấy nhân viên.', 1;
    IF NOT EXISTS (SELECT 1 FROM dbo.TaiKhoanDangNhap_VaiTro WHERE TenDangNhap=@TenDangNhap AND RoleName<>N'Khách hàng')
        THROW 52222, N'Tài khoản không tồn tại hoặc không có vai trò nhân viên.', 1;
    IF EXISTS (SELECT 1 FROM dbo.NhanVien WHERE TenDangNhap=@TenDangNhap AND MaNhanVien<>@MaNhanVien)
        THROW 52223, N'Tài khoản đã được liên kết với nhân viên khác.', 1;
    UPDATE dbo.NhanVien SET TenDangNhap=@TenDangNhap WHERE MaNhanVien=@MaNhanVien;
END;
GO

CREATE OR ALTER PROCEDURE dbo.SP_Admin_XoaNhanVien
    @MaNhanVien VARCHAR(20)
AS
BEGIN
    SET NOCOUNT ON;
    DELETE dbo.NhanVien WHERE MaNhanVien=@MaNhanVien;
    IF @@ROWCOUNT=0 THROW 52231, N'Không tìm thấy nhân viên.', 1;
END;
GO

CREATE OR ALTER PROCEDURE dbo.SP_Admin_TaoChiNhanh
    @TenChiNhanh NVARCHAR(100),
    @DiaChi NVARCHAR(200),
    @MaChiNhanh VARCHAR(10) OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;
    IF NULLIF(LTRIM(RTRIM(@TenChiNhanh)), N'') IS NULL THROW 52241, N'Tên chi nhánh không được để trống.', 1;
    IF NULLIF(LTRIM(RTRIM(@DiaChi)), N'') IS NULL THROW 52242, N'Địa chỉ chi nhánh không được để trống.', 1;

    DECLARE @Seq BIGINT;
    WHILE 1=1
    BEGIN
        SET @Seq = NEXT VALUE FOR dbo.Seq_MaChiNhanh;
        SET @MaChiNhanh = 'CN' + RIGHT(REPLICATE('0', 8) + CONVERT(VARCHAR(20), @Seq), 8);
        IF NOT EXISTS (SELECT 1 FROM dbo.ChiNhanh WHERE MaChiNhanh=@MaChiNhanh) BREAK;
    END;
    INSERT dbo.ChiNhanh(MaChiNhanh, TenChiNhanh, DiaChi) VALUES (@MaChiNhanh, @TenChiNhanh, @DiaChi);
END;
GO

CREATE OR ALTER PROCEDURE dbo.SP_Admin_CapNhatChiNhanh
    @MaChiNhanh VARCHAR(10),
    @TenChiNhanh NVARCHAR(100),
    @DiaChi NVARCHAR(200)
AS
BEGIN
    SET NOCOUNT ON;
    IF NULLIF(LTRIM(RTRIM(@TenChiNhanh)), N'') IS NULL THROW 52251, N'Tên chi nhánh không được để trống.', 1;
    IF NULLIF(LTRIM(RTRIM(@DiaChi)), N'') IS NULL THROW 52252, N'Địa chỉ chi nhánh không được để trống.', 1;
    UPDATE dbo.ChiNhanh SET TenChiNhanh=@TenChiNhanh, DiaChi=@DiaChi WHERE MaChiNhanh=@MaChiNhanh;
    IF @@ROWCOUNT=0 THROW 52253, N'Không tìm thấy chi nhánh.', 1;
END;
GO

CREATE OR ALTER PROCEDURE dbo.SP_Admin_XoaChiNhanh
    @MaChiNhanh VARCHAR(10)
AS
BEGIN
    SET NOCOUNT ON;
    DELETE dbo.ChiNhanh WHERE MaChiNhanh=@MaChiNhanh;
    IF @@ROWCOUNT=0 THROW 52261, N'Không tìm thấy chi nhánh.', 1;
END;
GO

-- ============================================================
-- PHẦN 5: CẤU HÌNH DANH MỤC HỆ THỐNG
-- ============================================================
CREATE OR ALTER PROCEDURE dbo.sp_TaoMaHanMuc
    @Ma VARCHAR(10) OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    DECLARE @Seq BIGINT;
    WHILE 1=1
    BEGIN
        SET @Seq = NEXT VALUE FOR dbo.Seq_MaHanMuc;
        SET @Ma = 'HM' + RIGHT(REPLICATE('0', 8) + CONVERT(VARCHAR(20), @Seq), 8);
        IF NOT EXISTS (SELECT 1 FROM dbo.[HanMucGiaoDịch] WHERE MaHanMuc=@Ma) BREAK;
    END;
END;
GO

CREATE OR ALTER PROCEDURE dbo.SP_Admin_DanhSachHanMuc
AS
BEGIN
    SET NOCOUNT ON;
    SELECT MaHanMuc, TenHanMuc, HanMucNgay FROM dbo.[HanMucGiaoDịch] ORDER BY MaHanMuc;
END;
GO

CREATE OR ALTER PROCEDURE dbo.SP_Admin_TaoHanMuc
    @TenHanMuc NVARCHAR(50),
    @HanMucNgay DECIMAL(18,2),
    @MaHanMuc VARCHAR(10) OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    IF NULLIF(LTRIM(RTRIM(@TenHanMuc)), N'') IS NULL THROW 52301, N'Tên hạn mức không được để trống.', 1;
    IF @HanMucNgay IS NULL OR @HanMucNgay <= 0 THROW 52302, N'Hạn mức ngày phải lớn hơn 0.', 1;
    IF EXISTS (SELECT 1 FROM dbo.[HanMucGiaoDịch] WHERE TenHanMuc=@TenHanMuc)
        THROW 52303, N'Tên hạn mức đã tồn tại.', 1;
    EXEC dbo.sp_TaoMaHanMuc @Ma=@MaHanMuc OUTPUT;
    INSERT dbo.[HanMucGiaoDịch](MaHanMuc, TenHanMuc, HanMucNgay) VALUES (@MaHanMuc, @TenHanMuc, @HanMucNgay);
END;
GO

CREATE OR ALTER PROCEDURE dbo.SP_Admin_CapNhatHanMuc
    @MaHanMuc VARCHAR(10),
    @TenHanMuc NVARCHAR(50),
    @HanMucNgay DECIMAL(18,2)
AS
BEGIN
    SET NOCOUNT ON;
    IF NULLIF(LTRIM(RTRIM(@TenHanMuc)), N'') IS NULL THROW 52311, N'Tên hạn mức không được để trống.', 1;
    IF @HanMucNgay IS NULL OR @HanMucNgay <= 0 THROW 52312, N'Hạn mức ngày phải lớn hơn 0.', 1;
    IF EXISTS (SELECT 1 FROM dbo.[HanMucGiaoDịch] WHERE TenHanMuc=@TenHanMuc AND MaHanMuc<>@MaHanMuc)
        THROW 52313, N'Tên hạn mức đã tồn tại.', 1;
    UPDATE dbo.[HanMucGiaoDịch] SET TenHanMuc=@TenHanMuc, HanMucNgay=@HanMucNgay WHERE MaHanMuc=@MaHanMuc;
    IF @@ROWCOUNT=0 THROW 52314, N'Không tìm thấy gói hạn mức.', 1;
END;
GO

CREATE OR ALTER PROCEDURE dbo.SP_Admin_XoaHanMuc
    @MaHanMuc VARCHAR(10)
AS
BEGIN
    SET NOCOUNT ON;
    DELETE dbo.[HanMucGiaoDịch] WHERE MaHanMuc=@MaHanMuc;
    IF @@ROWCOUNT=0 THROW 52321, N'Không tìm thấy gói hạn mức.', 1;
END;
GO

CREATE OR ALTER PROCEDURE dbo.SP_Admin_TraCuuTaiKhoanHanMuc
    @TuKhoa VARCHAR(20)
AS
BEGIN
    SET NOCOUNT ON;
    SELECT TOP (1) tk.SoTaiKhoan, kh.HoTen, tk.SoDu, cn.TenChiNhanh, tk.TrangThai,
           tk.MaHanMuc, hm.TenHanMuc, hm.HanMucNgay
    FROM dbo.TaiKhoanNganHang tk
    INNER JOIN dbo.KhachHang kh ON kh.MaKhachHang=tk.MaKhachHang
    LEFT JOIN dbo.ChiNhanh cn ON cn.MaChiNhanh=tk.MaChiNhanh
    LEFT JOIN dbo.[HanMucGiaoDịch] hm ON hm.MaHanMuc=tk.MaHanMuc
    WHERE tk.SoTaiKhoan=@TuKhoa OR tk.MaKhachHang=@TuKhoa
    ORDER BY CASE WHEN tk.SoTaiKhoan=@TuKhoa THEN 0 ELSE 1 END;
END;
GO

CREATE OR ALTER PROCEDURE dbo.SP_Admin_GanHanMucTaiKhoan
    @SoTaiKhoan VARCHAR(15),
    @MaHanMuc VARCHAR(10)
AS
BEGIN
    SET NOCOUNT ON;
    IF NOT EXISTS (SELECT 1 FROM dbo.[HanMucGiaoDịch] WHERE MaHanMuc=@MaHanMuc)
        THROW 52331, N'Gói hạn mức không tồn tại.', 1;
    UPDATE dbo.TaiKhoanNganHang SET MaHanMuc=@MaHanMuc WHERE SoTaiKhoan=@SoTaiKhoan;
    IF @@ROWCOUNT=0 THROW 52332, N'Không tìm thấy tài khoản ngân hàng.', 1;
END;
GO

CREATE OR ALTER PROCEDURE dbo.SP_Admin_DoiTrangThaiTaiKhoan
    @SoTaiKhoan VARCHAR(15)
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE dbo.TaiKhoanNganHang
    SET TrangThai=CASE WHEN TrangThai=N'Hoạt động' THEN N'Bị khóa' ELSE N'Hoạt động' END
    WHERE SoTaiKhoan=@SoTaiKhoan;
    IF @@ROWCOUNT=0 THROW 52341, N'Không tìm thấy tài khoản ngân hàng.', 1;
    SELECT TrangThai AS TrangThaiMoi FROM dbo.TaiKhoanNganHang WHERE SoTaiKhoan=@SoTaiKhoan;
END;
GO

CREATE OR ALTER PROCEDURE dbo.SP_Admin_DanhSachLoaiGiaoDich
AS
BEGIN
    SET NOCOUNT ON;
    SELECT MaLoaiGD, TenLoaiGD FROM dbo.LoaiGiaoDich ORDER BY MaLoaiGD;
END;
GO

CREATE OR ALTER PROCEDURE dbo.SP_Admin_TaoLoaiGiaoDich
    @TienTo VARCHAR(6),
    @TenLoaiGD NVARCHAR(50),
    @MaLoaiGD VARCHAR(10) OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;
    IF NULLIF(LTRIM(RTRIM(@TienTo)), '') IS NULL OR @TienTo LIKE '%[^A-Za-z]%'
        THROW 52351, N'Tiền tố mã chỉ được chứa chữ cái A-Z.', 1;
    IF NULLIF(LTRIM(RTRIM(@TenLoaiGD)), N'') IS NULL THROW 52352, N'Tên loại giao dịch không được để trống.', 1;
    IF LEN(@TienTo)>6 THROW 52353, N'Tiền tố mã không được dài quá 6 ký tự.', 1;

    BEGIN TRY
        BEGIN TRANSACTION;
        DECLARE @LockResult INT, @NextNumber INT, @SuffixLength INT=10-LEN(@TienTo),
                @LockResource NVARCHAR(255)=N'AdminLoaiGD:'+@TienTo;
        EXEC @LockResult=sys.sp_getapplock @Resource=@LockResource,
             @LockMode='Exclusive', @LockOwner='Transaction', @LockTimeout=10000;
        IF @LockResult<0 THROW 52354, N'Không thể khóa bộ sinh mã loại giao dịch.', 1;
        SELECT @NextNumber=ISNULL(MAX(TRY_CONVERT(INT, SUBSTRING(MaLoaiGD, LEN(@TienTo)+1, 10))),0)+1
        FROM dbo.LoaiGiaoDich WITH (UPDLOCK,HOLDLOCK)
        WHERE MaLoaiGD LIKE @TienTo+'%' AND LEN(MaLoaiGD)>LEN(@TienTo)
          AND SUBSTRING(MaLoaiGD,1,LEN(@TienTo))=@TienTo;
        IF @NextNumber>=POWER(CONVERT(BIGINT,10),@SuffixLength) THROW 52355, N'Đã hết mã cho tiền tố này.', 1;
        SET @MaLoaiGD=@TienTo+RIGHT(REPLICATE('0',@SuffixLength)+CONVERT(VARCHAR(10),@NextNumber),@SuffixLength);
        IF EXISTS (SELECT 1 FROM dbo.LoaiGiaoDich WHERE TenLoaiGD=@TenLoaiGD)
            THROW 52356, N'Tên loại giao dịch đã tồn tại.', 1;
        INSERT dbo.LoaiGiaoDich(MaLoaiGD,TenLoaiGD) VALUES (@MaLoaiGD,@TenLoaiGD);
        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF XACT_STATE()<>0 ROLLBACK TRANSACTION;
        THROW;
    END CATCH;
END;
GO

CREATE OR ALTER PROCEDURE dbo.SP_Admin_CapNhatLoaiGiaoDich
    @MaLoaiGD VARCHAR(10),
    @TenLoaiGD NVARCHAR(50)
AS
BEGIN
    SET NOCOUNT ON;
    IF NULLIF(LTRIM(RTRIM(@TenLoaiGD)),N'') IS NULL THROW 52361, N'Tên loại giao dịch không được để trống.', 1;
    IF EXISTS (SELECT 1 FROM dbo.LoaiGiaoDich WHERE TenLoaiGD=@TenLoaiGD AND MaLoaiGD<>@MaLoaiGD)
        THROW 52362, N'Tên loại giao dịch đã tồn tại.', 1;
    UPDATE dbo.LoaiGiaoDich SET TenLoaiGD=@TenLoaiGD WHERE MaLoaiGD=@MaLoaiGD;
    IF @@ROWCOUNT=0 THROW 52363, N'Không tìm thấy loại giao dịch.', 1;
END;
GO

CREATE OR ALTER PROCEDURE dbo.SP_Admin_XoaLoaiGiaoDich
    @MaLoaiGD VARCHAR(10)
AS
BEGIN
    SET NOCOUNT ON;
    DELETE dbo.LoaiGiaoDich WHERE MaLoaiGD=@MaLoaiGD;
    IF @@ROWCOUNT=0 THROW 52371, N'Không tìm thấy loại giao dịch.', 1;
END;
GO

CREATE OR ALTER PROCEDURE dbo.SP_Admin_DanhSachNganHang
AS
BEGIN
    SET NOCOUNT ON;
    SELECT MaNganHang, TenNganHang, TrangThai FROM dbo.NganHang ORDER BY MaNganHang;
END;
GO

CREATE OR ALTER PROCEDURE dbo.SP_Admin_DoiTrangThaiNganHang
    @MaNganHang VARCHAR(20)
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE dbo.NganHang
    SET TrangThai=CASE WHEN TrangThai=N'Hoạt động' THEN N'Ngừng hoạt động' ELSE N'Hoạt động' END
    WHERE MaNganHang=@MaNganHang;
    IF @@ROWCOUNT=0 THROW 52381, N'Không tìm thấy ngân hàng.', 1;
    SELECT TrangThai AS TrangThaiMoi FROM dbo.NganHang WHERE MaNganHang=@MaNganHang;
END;
GO

-- ===== Index va trigger nghiep vu Admin =====
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE object_id=OBJECT_ID(N'dbo.NhanVien') AND name=N'IX_NhanVien_MaChiNhanh')
    CREATE INDEX IX_NhanVien_MaChiNhanh ON dbo.NhanVien(MaChiNhanh);
GO
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE object_id=OBJECT_ID(N'dbo.TaiKhoanDangNhap_VaiTro') AND name=N'IX_TaiKhoan_RoleName')
    CREATE INDEX IX_TaiKhoan_RoleName ON dbo.TaiKhoanDangNhap_VaiTro(RoleName, TenDangNhap) INCLUDE (MaNguoiDung);
GO
CREATE OR ALTER TRIGGER dbo.trg_Admin_NhanVien_Audit
ON dbo.NhanVien AFTER UPDATE, DELETE AS
BEGIN
 SET NOCOUNT ON;
 INSERT dbo.AuditNganHang(BangDuLieu,KhoaDuLieu,HanhDong,ChiTiet)
 SELECT N'NhanVien', d.MaNhanVien, CASE WHEN i.MaNhanVien IS NULL THEN N'Xoa nhan vien' ELSE N'Cap nhat nhan vien' END,
        CONCAT(N'HoTen: ',d.HoTen,CASE WHEN i.MaNhanVien IS NULL THEN N'' ELSE CONCAT(N' -> ',i.HoTen) END)
 FROM deleted d LEFT JOIN inserted i ON i.MaNhanVien=d.MaNhanVien;
END;
GO
