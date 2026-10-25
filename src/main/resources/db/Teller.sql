-- Truy vấn dữ liệu cho TellerDashboard.
-- Chạy file này sau khi đã tạo Database_AppNganhang_DoLeGiaHan.sql.
GO

-- Trả về tên loại giao dịch tại quầy.
CREATE OR ALTER FUNCTION dbo.fn_GDV_LayHanhDongTienMat
(
    @MaLoaiGD VARCHAR(10)
)
RETURNS NVARCHAR(100)
AS
BEGIN
    RETURN CASE @MaLoaiGD
        WHEN 'NT01' THEN N'Nạp tiền mặt'
        WHEN 'RT01' THEN N'Rút tiền mặt'
        WHEN 'TT01' THEN N'Thanh toán hóa đơn tại quầy'
        ELSE NULL
    END;
END;
GO

-- Trả về số tiền có dấu: nạp là số dương, rút/thanh toán là số âm.
CREATE OR ALTER FUNCTION dbo.fn_GDV_TinhBienDongSoDu
(
    @MaLoaiGD VARCHAR(10),
    @SoTien DECIMAL(18,2)
)
RETURNS DECIMAL(18,2)
AS
BEGIN
    RETURN CASE
        WHEN @MaLoaiGD = 'NT01' THEN @SoTien
        WHEN @MaLoaiGD IN ('RT01', 'TT01') THEN -@SoTien
        ELSE NULL
    END;
END;
GO

-- Tính hạn mức còn lại; phần truy vấn tổng giao dịch và khóa dữ liệu vẫn ở procedure.
CREATE OR ALTER FUNCTION dbo.fn_GDV_TinhHanMucConLai
(
    @HanMucNgay DECIMAL(18,2),
    @DaGiaoDich DECIMAL(18,2),
    @SoTienSapGiaoDich DECIMAL(18,2)
)
RETURNS DECIMAL(18,2)
AS
BEGIN
    IF @HanMucNgay IS NULL OR @DaGiaoDich IS NULL OR @SoTienSapGiaoDich IS NULL
        RETURN NULL;

    RETURN CASE
        WHEN @HanMucNgay - @DaGiaoDich - @SoTienSapGiaoDich > 0
            THEN @HanMucNgay - @DaGiaoDich - @SoTienSapGiaoDich
        ELSE 0
    END;
END;
GO

CREATE OR ALTER PROCEDURE dbo.sp_Teller_Dashboard
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @HomNay DATE = CONVERT(DATE, GETDATE());

    SELECT
        (SELECT COUNT(*)
         FROM dbo.GiaoDichChuyenTien
         WHERE NgayGiaoDich >= @HomNay
           AND NgayGiaoDich < DATEADD(DAY, 1, @HomNay)) AS SoGiaoDichHomNay,
        (SELECT COALESCE(SUM(SoTien), 0)
         FROM dbo.GiaoDichChuyenTien
         WHERE TrangThai = N'Thành công'
           AND NgayGiaoDich >= @HomNay
           AND NgayGiaoDich < DATEADD(DAY, 1, @HomNay)) AS TongTienHomNay,
        (SELECT COUNT(*)
         FROM dbo.GiaoDichChuyenTien
         WHERE TrangThai = N'Chờ xử lý') AS SoGiaoDichChoXuLy,
        (SELECT COUNT(*)
         FROM dbo.TaiKhoanNganHang) AS SoTaiKhoanKhachHang;
END;
GO

CREATE OR ALTER VIEW dbo.vw_Teller_GiaoDichGanDay
AS
    SELECT
        gd.MaGiaoDich,
        COALESCE(tkNguon.SoTaiKhoan, gd.MaTaiKhoanNguon) AS MaTaiKhoanNguon,
        COALESCE(tkDich.SoTaiKhoan, gd.SoTaiKhoanDich) AS MaTaiKhoanDich,
        gd.SoTien,
        gd.NgayGiaoDich,
        COALESCE(lgd.TenLoaiGD, gd.MaLoaiGD) AS MaLoaiGD,
        gd.TrangThai
    FROM dbo.GiaoDichChuyenTien gd
    LEFT JOIN dbo.TaiKhoanNganHang tkNguon ON tkNguon.MaTaiKhoan = gd.MaTaiKhoanNguon
    LEFT JOIN dbo.TaiKhoanNganHang tkDich ON tkDich.MaTaiKhoan = gd.MaTaiKhoanDich
    LEFT JOIN dbo.LoaiGiaoDich lgd ON lgd.MaLoaiGD = gd.MaLoaiGD;
GO

-- Nguồn của các procedure dưới đây là 4_GiaoDichVien.sql.
-- Đã căn chỉnh tham số và cột theo Database_AppNganhang_DoLeGiaHan.sql.
CREATE OR ALTER PROCEDURE dbo.SP_GDV_DanhSachNganHang
AS
BEGIN
    SET NOCOUNT ON;
    SELECT MaNganHang, TenNganHang, TrangThai
    FROM dbo.NganHang
    WHERE TrangThai = N'Hoạt động'
    ORDER BY TenNganHang;
END;
GO

CREATE OR ALTER PROCEDURE dbo.SP_GDV_TimNguoiNhan
    @MaNganHang VARCHAR(20),
    @SoTaiKhoan VARCHAR(30)
AS
BEGIN
    SET NOCOUNT ON;
    IF NULLIF(LTRIM(RTRIM(@SoTaiKhoan)), '') IS NULL
        THROW 52201, N'Số tài khoản nhận không được để trống.', 1;

    IF NOT EXISTS (SELECT 1 FROM dbo.NganHang WHERE MaNganHang = @MaNganHang AND TrangThai = N'Hoạt động')
        THROW 52202, N'Ngân hàng không tồn tại hoặc đang ngừng hoạt động.', 1;

    IF @MaNganHang = 'SMART'
    BEGIN
        SELECT nh.MaNganHang, nh.TenNganHang, tk.SoTaiKhoan, kh.HoTen AS TenNguoiNhan,
               tk.TrangThai AS TrangThaiTaiKhoan, CAST(1 AS BIT) AS DaXacDinhNoiBo,
               N'Đã tra cứu tài khoản nội bộ.' AS ThongBao
        FROM dbo.TaiKhoanNganHang tk
        INNER JOIN dbo.KhachHang kh ON kh.MaKhachHang = tk.MaKhachHang
        INNER JOIN dbo.NganHang nh ON nh.MaNganHang = tk.MaNganHang
        WHERE tk.SoTaiKhoan = @SoTaiKhoan;
        IF @@ROWCOUNT = 0 THROW 52203, N'Không tìm thấy số tài khoản SMART BANK.', 1;
    END
    ELSE
    BEGIN
        SELECT nh.MaNganHang, nh.TenNganHang, @SoTaiKhoan AS SoTaiKhoan,
               CAST(NULL AS NVARCHAR(100)) AS TenNguoiNhan,
               CAST(NULL AS NVARCHAR(20)) AS TrangThaiTaiKhoan,
               CAST(0 AS BIT) AS DaXacDinhNoiBo,
               N'Ngân hàng ngoài chưa kết nối API xác minh người nhận.' AS ThongBao
        FROM dbo.NganHang nh
        WHERE nh.MaNganHang = @MaNganHang;
    END;
END;
GO

CREATE OR ALTER PROCEDURE dbo.SP_GDV_KiemTraTaiKhoanNguon
    @SoTaiKhoan VARCHAR(30)
AS
BEGIN
    SET NOCOUNT ON;
    SELECT tk.MaTaiKhoan, tk.SoTaiKhoan, tk.SoDu, tk.TrangThai, hm.HanMucNgay,
           COALESCE(used.DaChuyenHomNay, 0) AS DaChuyenHomNay,
           dbo.fn_GDV_TinhHanMucConLai(
               hm.HanMucNgay, COALESCE(used.DaChuyenHomNay, 0), 0
           ) AS HanMucConLai,
           kh.HoTen, kh.SoDienThoai
    FROM dbo.TaiKhoanNganHang tk
    INNER JOIN dbo.[HanMucGiaoDịch] hm ON hm.MaHanMuc = tk.MaHanMuc
    INNER JOIN dbo.KhachHang kh ON kh.MaKhachHang = tk.MaKhachHang
    OUTER APPLY (
        SELECT SUM(gd.SoTien) AS DaChuyenHomNay
        FROM dbo.GiaoDichChuyenTien gd
        WHERE gd.MaTaiKhoanNguon = tk.MaTaiKhoan
          AND gd.NgayGiaoDich >= CONVERT(DATE, GETDATE())
          AND gd.NgayGiaoDich < DATEADD(DAY, 1, CONVERT(DATE, GETDATE()))
          AND gd.TrangThai IN (N'Thành công', N'Chờ xử lý')
    ) used
    WHERE tk.SoTaiKhoan = @SoTaiKhoan AND tk.MaNganHang = 'SMART';

    IF @@ROWCOUNT = 0 THROW 52226, N'Không tìm thấy tài khoản nguồn SMART BANK.', 1;
END;
GO

CREATE OR ALTER PROCEDURE dbo.SP_GDV_ThucHienChuyenTien
    @MaGiaoDich VARCHAR(30),
    @SoTaiKhoanNguon VARCHAR(30),
    @MaNganHangDich VARCHAR(20),
    @SoTaiKhoanDich VARCHAR(30),
    @SoTien DECIMAL(18,2),
    @MaLoaiGD VARCHAR(10),
    @TenNguoiNhan NVARCHAR(100),
    @NoiDung NVARCHAR(255)
AS
BEGIN

    -- kiểm tra dữ liệu đầu vào
    SET NOCOUNT ON;
    SET XACT_ABORT ON;
    IF @SoTien IS NULL OR @SoTien <= 0 THROW 52230, N'Số tiền phải lớn hơn 0.', 1;
    IF NULLIF(LTRIM(RTRIM(@MaGiaoDich)), '') IS NULL THROW 52231, N'Mã giao dịch không hợp lệ.', 1;
    IF NULLIF(LTRIM(RTRIM(@SoTaiKhoanNguon)), '') IS NULL OR NULLIF(LTRIM(RTRIM(@SoTaiKhoanDich)), '') IS NULL
        THROW 52232, N'Tài khoản nguồn và đích không được để trống.', 1;

    BEGIN TRY
        BEGIN TRANSACTION;
        IF EXISTS (SELECT 1 FROM dbo.GiaoDichChuyenTien WHERE MaGiaoDich = @MaGiaoDich)
            THROW 52233, N'Mã giao dịch đã tồn tại.', 1;

        DECLARE @MaTaiKhoanNguon VARCHAR(20), @SoDuNguon DECIMAL(18,2), @TrangThaiNguon NVARCHAR(20),
                @HanMucNgay DECIMAL(18,2), @DaChuyenHomNay DECIMAL(18,2),
                @MaTaiKhoanDich VARCHAR(20) = NULL, @SoDuDich DECIMAL(18,2) = NULL,
                @TrangThaiDich NVARCHAR(20), @TenNguoiNhanDB NVARCHAR(100),
                @TrangThaiGD NVARCHAR(30), @SoDuSauGD DECIMAL(18,2), @HanMucConLai DECIMAL(18,2);

        -- kiểm tra tài khoản nguồn
        SELECT @MaTaiKhoanNguon = tk.MaTaiKhoan, @SoDuNguon = tk.SoDu,
               @TrangThaiNguon = tk.TrangThai, @HanMucNgay = hm.HanMucNgay
        FROM dbo.TaiKhoanNganHang tk WITH (UPDLOCK, HOLDLOCK)
        INNER JOIN dbo.[HanMucGiaoDịch] hm ON hm.MaHanMuc = tk.MaHanMuc
        WHERE tk.SoTaiKhoan = @SoTaiKhoanNguon AND tk.MaNganHang = 'SMART';
        IF @MaTaiKhoanNguon IS NULL THROW 52226, N'Không tìm thấy tài khoản nguồn SMART BANK.', 1;
        IF @TrangThaiNguon <> N'Hoạt động' THROW 52234, N'Tài khoản nguồn không ở trạng thái hoạt động.', 1;

        -- kiểm tra ngân hàng đích
        IF NOT EXISTS (SELECT 1 FROM dbo.NganHang WHERE MaNganHang = @MaNganHangDich AND TrangThai = N'Hoạt động')
            THROW 52202, N'Ngân hàng đích không tồn tại hoặc đang ngừng hoạt động.', 1;
        IF NOT EXISTS (SELECT 1 FROM dbo.LoaiGiaoDich WHERE MaLoaiGD = @MaLoaiGD)
            THROW 52235, N'Loại giao dịch không tồn tại.', 1;

        -- kiểm tra số dư và hạn mức ngày
        SELECT @DaChuyenHomNay = COALESCE(SUM(SoTien), 0)
        FROM dbo.GiaoDichChuyenTien WITH (UPDLOCK, HOLDLOCK)
        WHERE MaTaiKhoanNguon = @MaTaiKhoanNguon
          AND NgayGiaoDich >= CONVERT(DATE, GETDATE())
          AND NgayGiaoDich < DATEADD(DAY, 1, CONVERT(DATE, GETDATE()))
          AND TrangThai IN (N'Thành công', N'Chờ xử lý');
        IF @SoDuNguon < @SoTien THROW 52236, N'Số dư tài khoản nguồn không đủ.', 1;
        IF @DaChuyenHomNay + @SoTien > @HanMucNgay THROW 52237, N'Giao dịch vượt hạn mức ngày.', 1;

        IF @MaNganHangDich = 'SMART'
        BEGIN
            SELECT @MaTaiKhoanDich = MaTaiKhoan, @SoDuDich = SoDu,
                   @TrangThaiDich = TrangThai, @TenNguoiNhanDB = kh.HoTen
            FROM dbo.TaiKhoanNganHang tk WITH (UPDLOCK, HOLDLOCK)
            INNER JOIN dbo.KhachHang kh ON kh.MaKhachHang = tk.MaKhachHang
            WHERE tk.SoTaiKhoan = @SoTaiKhoanDich AND tk.MaNganHang = 'SMART';
            IF @MaTaiKhoanDich IS NULL THROW 52203, N'Không tìm thấy số tài khoản SMART BANK.', 1;
            IF @TrangThaiDich <> N'Hoạt động' THROW 52238, N'Tài khoản nhận không ở trạng thái hoạt động.', 1;
            IF @MaTaiKhoanDich = @MaTaiKhoanNguon THROW 52239, N'Tài khoản nguồn và đích không được trùng nhau.', 1;
            SET @TenNguoiNhan = COALESCE(@TenNguoiNhanDB, @TenNguoiNhan);
            SET @TrangThaiGD = N'Thành công';

            --  cập nhật số dư tài khoản nguồn và tài khoản đích
            UPDATE dbo.TaiKhoanNganHang SET SoDu = SoDu - @SoTien WHERE MaTaiKhoan = @MaTaiKhoanNguon;
            UPDATE dbo.TaiKhoanNganHang SET SoDu = SoDu + @SoTien WHERE MaTaiKhoan = @MaTaiKhoanDich;
            SET @SoDuSauGD = @SoDuNguon - @SoTien;
        END
        ELSE
        BEGIN
            -- Chuyển liên ngân hàng được ghi nhận chờ xử lý cho tới khi có tích hợp thanh toán.
            SET @TenNguoiNhan = COALESCE(@TenNguoiNhan, N'Người nhận ngân hàng ngoài');
            SET @TrangThaiGD = N'Chờ xử lý';
            SET @SoDuSauGD = @SoDuNguon;
        END;

        INSERT dbo.GiaoDichChuyenTien
            (MaGiaoDich, MaTaiKhoanNguon, MaTaiKhoanDich, MaNganHangDich, SoTaiKhoanDich,
             TenNguoiNhan, SoTien, NgayGiaoDich, NoiDung, MaLoaiGD, TrangThai)
        VALUES
            (@MaGiaoDich, @MaTaiKhoanNguon, @MaTaiKhoanDich, @MaNganHangDich, @SoTaiKhoanDich,
             @TenNguoiNhan, @SoTien, GETDATE(), @NoiDung, @MaLoaiGD, @TrangThaiGD);

        IF @TrangThaiGD = N'Thành công'
        BEGIN
            INSERT dbo.LichSuGiaoDich (MaGiaoDich, SoTaiKhoanLienQuan, HanhDong, SoTienThayDoi, SoDuSauThayDoi)
            VALUES (@MaGiaoDich, @SoTaiKhoanNguon, N'Trừ tiền chuyển khoản đi', -@SoTien, @SoDuSauGD),
                   (@MaGiaoDich, @SoTaiKhoanDich, N'Cộng tiền nhận chuyển khoản', @SoTien, @SoDuDich + @SoTien);
        END;

        SET @HanMucConLai = dbo.fn_GDV_TinhHanMucConLai(
            @HanMucNgay, @DaChuyenHomNay, @SoTien
        );
        COMMIT TRANSACTION;
        SELECT @MaGiaoDich AS MaGiaoDich, @TrangThaiGD AS TrangThai,
               @SoDuSauGD AS SoDuNguonSauGiaoDich, @HanMucConLai AS HanMucConLai;
    END TRY
    BEGIN CATCH
        IF XACT_STATE() <> 0 ROLLBACK TRANSACTION;
        THROW;
    END CATCH;
END;
GO

-- Tra cứu tài khoản SMART BANK dùng tại màn hình giao dịch tiền mặt.
CREATE OR ALTER PROCEDURE dbo.SP_GDV_TraCuuTaiKhoan
    @SoTaiKhoan VARCHAR(15)
AS
BEGIN
    SET NOCOUNT ON;
    SELECT tk.MaTaiKhoan, tk.SoTaiKhoan, tk.SoDu, tk.TrangThai,
           kh.HoTen, cn.TenChiNhanh
    FROM dbo.TaiKhoanNganHang tk
    INNER JOIN dbo.KhachHang kh ON kh.MaKhachHang = tk.MaKhachHang
    LEFT JOIN dbo.ChiNhanh cn ON cn.MaChiNhanh = tk.MaChiNhanh
    WHERE tk.SoTaiKhoan = @SoTaiKhoan AND tk.MaNganHang = 'SMART';
END;
GO

-- Nạp/rút tiền mặt và thanh toán tại quầy. Cập nhật số dư, giao dịch và
-- lịch sử trong cùng transaction; tiền mặt không tạo tài khoản đích giả.
CREATE OR ALTER PROCEDURE dbo.SP_GDV_ThucHienGiaoDich
    @MaGiaoDich VARCHAR(30),
    @SoTaiKhoan VARCHAR(15),
    @MaLoaiGD VARCHAR(10),
    @SoTien DECIMAL(18,2),
    @NoiDung NVARCHAR(255)
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    -- kiểm tra tham số đầu vào
    IF @SoTien IS NULL OR @SoTien <= 0
        THROW 52250, N'Số tiền giao dịch phải lớn hơn 0.', 1;
    IF @MaLoaiGD NOT IN ('NT01', 'RT01', 'TT01')
        THROW 52251, N'Loại giao dịch tại quầy không hợp lệ.', 1;
    IF NULLIF(LTRIM(RTRIM(@MaGiaoDich)), '') IS NULL
       OR NULLIF(LTRIM(RTRIM(@SoTaiKhoan)), '') IS NULL
        THROW 52252, N'Mã giao dịch và số tài khoản không được để trống.', 1;

    BEGIN TRY
        BEGIN TRANSACTION;

        IF EXISTS (SELECT 1 FROM dbo.GiaoDichChuyenTien WHERE MaGiaoDich = @MaGiaoDich)
            THROW 52253, N'Mã giao dịch đã tồn tại.', 1;

        DECLARE @MaTaiKhoan VARCHAR(20), @SoDu DECIMAL(18,2),
                @TrangThai NVARCHAR(20), @SoDuSau DECIMAL(18,2),
                @HanhDong NVARCHAR(100), @SoTienThayDoi DECIMAL(18,2);

        SELECT @MaTaiKhoan = MaTaiKhoan, @SoDu = SoDu, @TrangThai = TrangThai
        FROM dbo.TaiKhoanNganHang WITH (UPDLOCK, HOLDLOCK)
        WHERE SoTaiKhoan = @SoTaiKhoan AND MaNganHang = 'SMART';

        IF @MaTaiKhoan IS NULL
            THROW 52254, N'Không tìm thấy tài khoản SMART BANK.', 1;
        IF @TrangThai <> N'Hoạt động'
            THROW 52255, N'Tài khoản không ở trạng thái hoạt động.', 1;
        IF NOT EXISTS (SELECT 1 FROM dbo.LoaiGiaoDich WHERE MaLoaiGD = @MaLoaiGD)
            THROW 52256, N'Loại giao dịch chưa được khai báo trong cơ sở dữ liệu.', 1;

        IF @MaLoaiGD = 'NT01'
        BEGIN
            SET @SoTienThayDoi = dbo.fn_GDV_TinhBienDongSoDu(@MaLoaiGD, @SoTien);
        END
        ELSE
        BEGIN
            SET @SoTienThayDoi = dbo.fn_GDV_TinhBienDongSoDu(@MaLoaiGD, @SoTien);
            IF @SoDu + @SoTienThayDoi < 0
                THROW 52257, N'Số dư tài khoản không đủ.', 1;
        END;

        SET @SoDuSau = @SoDu + @SoTienThayDoi;
        SET @HanhDong = dbo.fn_GDV_LayHanhDongTienMat(@MaLoaiGD);

        --  cập nhật số dư
        UPDATE dbo.TaiKhoanNganHang
        SET SoDu = @SoDuSau
        WHERE MaTaiKhoan = @MaTaiKhoan;

        -- ghi giao dịch
        INSERT dbo.GiaoDichChuyenTien
            (MaGiaoDich, MaTaiKhoanNguon, MaTaiKhoanDich, MaNganHangDich,
             SoTaiKhoanDich, SoTien, NgayGiaoDich, NoiDung, MaLoaiGD, TrangThai)
        VALUES
            (@MaGiaoDich,
             CASE WHEN @MaLoaiGD = 'NT01' THEN 'TIEN_MAT_QUAY' ELSE @MaTaiKhoan END,
             CASE WHEN @MaLoaiGD = 'NT01' THEN @MaTaiKhoan ELSE 'TIEN_MAT_QUAY' END,
             'SMART', 'N/A', @SoTien,
             GETDATE(), @NoiDung, @MaLoaiGD, N'Thành công');

        INSERT dbo.LichSuGiaoDich
            (MaGiaoDich, SoTaiKhoanLienQuan, HanhDong, SoTienThayDoi, SoDuSauThayDoi)
        VALUES
            (@MaGiaoDich, @SoTaiKhoan, @HanhDong, @SoTienThayDoi, @SoDuSau);

        COMMIT TRANSACTION;
        SELECT @MaGiaoDich AS MaGiaoDich, @SoDuSau AS SoDuSauGiaoDich,
               N'Thành công' AS TrangThai;
    END TRY
    BEGIN CATCH
        IF XACT_STATE() <> 0 ROLLBACK TRANSACTION;
        THROW;
    END CATCH;
END;
GO

CREATE OR ALTER VIEW dbo.vw_GDV_DanhSachTaiKhoan
AS
    SELECT tk.MaTaiKhoan, tk.SoTaiKhoan, tk.SoDu, tk.TrangThai,
           kh.HoTen, kh.SoCCCD, kh.SoDienThoai, kh.Email, kh.DiaChi,
           nh.TenNganHang, cn.TenChiNhanh, hm.TenHanMuc
    FROM dbo.TaiKhoanNganHang tk
    INNER JOIN dbo.KhachHang kh ON kh.MaKhachHang = tk.MaKhachHang
    INNER JOIN dbo.NganHang nh ON nh.MaNganHang = tk.MaNganHang
    INNER JOIN dbo.ChiNhanh cn ON cn.MaChiNhanh = tk.MaChiNhanh
    INNER JOIN dbo.[HanMucGiaoDịch] hm ON hm.MaHanMuc = tk.MaHanMuc;
GO

CREATE OR ALTER PROCEDURE dbo.SP_GDV_DoiTrangThaiTaiKhoan
    @MaTaiKhoan VARCHAR(20),
    @TrangThai NVARCHAR(20)
AS
BEGIN
    SET NOCOUNT ON;
    IF @TrangThai NOT IN (N'Hoạt động', N'Bị khóa', N'Đóng')
        THROW 52260, N'Trạng thái tài khoản không hợp lệ.', 1;
    UPDATE dbo.TaiKhoanNganHang SET TrangThai = @TrangThai WHERE MaTaiKhoan = @MaTaiKhoan;
    IF @@ROWCOUNT = 0 THROW 52261, N'Không tìm thấy tài khoản.', 1;
END;
GO

CREATE OR ALTER PROCEDURE dbo.SP_GDV_GanHanMucTaiKhoan
    @MaTaiKhoan VARCHAR(20),
    @MaHanMuc VARCHAR(10)
AS
BEGIN
    SET NOCOUNT ON;
    IF NOT EXISTS (SELECT 1 FROM dbo.[HanMucGiaoDịch] WHERE MaHanMuc = @MaHanMuc)
        THROW 52262, N'Hạn mức giao dịch không tồn tại.', 1;
    UPDATE dbo.TaiKhoanNganHang SET MaHanMuc = @MaHanMuc WHERE MaTaiKhoan = @MaTaiKhoan;
    IF @@ROWCOUNT = 0 THROW 52263, N'Không tìm thấy tài khoản.', 1;
END;
GO

CREATE OR ALTER VIEW dbo.vw_GDV_SaoKe
AS
    SELECT ls.MaLog, ls.MaGiaoDich, ls.SoTaiKhoanLienQuan, ls.HanhDong,
           ls.SoTienThayDoi, ls.SoDuSauThayDoi, ls.ThoiGianGhiLog,
           gd.MaLoaiGD, COALESCE(lgd.TenLoaiGD, gd.MaLoaiGD, ls.HanhDong) AS TenLoaiGD,
           COALESCE(gd.NoiDung, ls.HanhDong) AS NoiDung
    FROM dbo.LichSuGiaoDich ls
    LEFT JOIN dbo.GiaoDichChuyenTien gd ON gd.MaGiaoDich = ls.MaGiaoDich
    LEFT JOIN dbo.LoaiGiaoDich lgd ON lgd.MaLoaiGD = gd.MaLoaiGD;
GO

-- Danh sách cho màn hình Theo dõi trạng thái giao dịch.
CREATE OR ALTER VIEW dbo.vw_Teller_TrangThaiGiaoDich
AS
    SELECT gd.MaGiaoDich,
           COALESCE(tkNguon.SoTaiKhoan, gd.MaTaiKhoanNguon) AS SoTaiKhoanNguon,
           COALESCE(tkDich.SoTaiKhoan, gd.SoTaiKhoanDich) AS SoTaiKhoanDich,
           gd.SoTien, gd.NgayGiaoDich, gd.NoiDung, gd.MaLoaiGD, gd.TrangThai
    FROM dbo.GiaoDichChuyenTien gd
    LEFT JOIN dbo.TaiKhoanNganHang tkNguon ON tkNguon.MaTaiKhoan = gd.MaTaiKhoanNguon
    LEFT JOIN dbo.TaiKhoanNganHang tkDich ON tkDich.MaTaiKhoan = gd.MaTaiKhoanDich;
GO

-- Duyệt giao dịch chờ: cập nhật số dư và ghi sổ trong cùng transaction.
CREATE OR ALTER PROCEDURE dbo.SP_GDV_DuyetGiaoDichChoXuLy
    @MaGiaoDich VARCHAR(30)
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    BEGIN TRY
        BEGIN TRANSACTION;
        DECLARE @MaTaiKhoanNguon VARCHAR(20), @MaTaiKhoanDich VARCHAR(20),
                @MaNganHangDich VARCHAR(20), @SoTaiKhoanDich VARCHAR(30),
                @SoTaiKhoanNguon VARCHAR(15), @SoTien DECIMAL(18,2),
                @SoDuNguon DECIMAL(18,2), @SoDuDich DECIMAL(18,2),
                @TrangThaiNguon NVARCHAR(20), @TrangThaiDich NVARCHAR(20);

        SELECT @MaTaiKhoanNguon = MaTaiKhoanNguon,
               @MaTaiKhoanDich = MaTaiKhoanDich,
               @MaNganHangDich = MaNganHangDich,
               @SoTaiKhoanDich = SoTaiKhoanDich,
               @SoTien = SoTien
        FROM dbo.GiaoDichChuyenTien WITH (UPDLOCK, HOLDLOCK)
        WHERE MaGiaoDich = @MaGiaoDich AND TrangThai = N'Chờ xử lý';

        IF @SoTien IS NULL
            THROW 52270, N'Không tìm thấy giao dịch đang chờ xử lý.', 1;

        SELECT @SoTaiKhoanNguon = SoTaiKhoan,
               @SoDuNguon = SoDu,
               @TrangThaiNguon = TrangThai
        FROM dbo.TaiKhoanNganHang WITH (UPDLOCK, HOLDLOCK)
        WHERE MaTaiKhoan = @MaTaiKhoanNguon;

        IF @SoTaiKhoanNguon IS NULL
            THROW 52271, N'Không tìm thấy tài khoản nguồn của giao dịch.', 1;
        IF @TrangThaiNguon <> N'Hoạt động'
            THROW 52272, N'Tài khoản nguồn không ở trạng thái hoạt động.', 1;
        IF @SoDuNguon < @SoTien
            THROW 52273, N'Số dư tài khoản nguồn không đủ để duyệt giao dịch.', 1;

        IF @MaNganHangDich = 'SMART' AND @MaTaiKhoanDich IS NOT NULL
        BEGIN
            SELECT @SoDuDich = SoDu, @TrangThaiDich = TrangThai
            FROM dbo.TaiKhoanNganHang WITH (UPDLOCK, HOLDLOCK)
            WHERE MaTaiKhoan = @MaTaiKhoanDich;
            IF @SoDuDich IS NULL
                THROW 52274, N'Không tìm thấy tài khoản nhận của giao dịch.', 1;
            IF @TrangThaiDich <> N'Hoạt động'
                THROW 52275, N'Tài khoản nhận không ở trạng thái hoạt động.', 1;

            UPDATE dbo.TaiKhoanNganHang SET SoDu = SoDu + @SoTien
            WHERE MaTaiKhoan = @MaTaiKhoanDich;
            INSERT dbo.LichSuGiaoDich
                (MaGiaoDich, SoTaiKhoanLienQuan, HanhDong, SoTienThayDoi, SoDuSauThayDoi)
            VALUES
                (@MaGiaoDich, @SoTaiKhoanDich, N'Cộng tiền nhận chuyển khoản', @SoTien, @SoDuDich + @SoTien);
        END;

        UPDATE dbo.TaiKhoanNganHang SET SoDu = SoDu - @SoTien
        WHERE MaTaiKhoan = @MaTaiKhoanNguon;

        INSERT dbo.LichSuGiaoDich
            (MaGiaoDich, SoTaiKhoanLienQuan, HanhDong, SoTienThayDoi, SoDuSauThayDoi)
        VALUES
            (@MaGiaoDich, @SoTaiKhoanNguon, N'Trừ tiền giao dịch được duyệt', -@SoTien, @SoDuNguon - @SoTien);

        UPDATE dbo.GiaoDichChuyenTien SET TrangThai = N'Thành công'
        WHERE MaGiaoDich = @MaGiaoDich;

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF XACT_STATE() <> 0 ROLLBACK TRANSACTION;
        THROW;
    END CATCH;
END;
GO

CREATE OR ALTER PROCEDURE dbo.SP_GDV_HuyGiaoDichChoXuLy
    @MaGiaoDich VARCHAR(30),
    @LyDo NVARCHAR(255)
AS
BEGIN
    SET NOCOUNT ON;
    IF NULLIF(LTRIM(RTRIM(@LyDo)), N'') IS NULL
        THROW 52276, N'Lý do hủy giao dịch không được để trống.', 1;

    UPDATE dbo.GiaoDichChuyenTien
    SET TrangThai = N'Đã hủy', PhanHoiLienNganHang = @LyDo
    WHERE MaGiaoDich = @MaGiaoDich AND TrangThai = N'Chờ xử lý';

    IF @@ROWCOUNT = 0
        THROW 52277, N'Không tìm thấy giao dịch đang chờ xử lý.', 1;
END;
GO


-- ===== Index va trigger nghiep vu Teller =====
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE object_id=OBJECT_ID(N'dbo.GiaoDichChuyenTien') AND name=N'IX_GiaoDich_TaiKhoanNgay')
    CREATE INDEX IX_GiaoDich_TaiKhoanNgay
    ON dbo.GiaoDichChuyenTien(MaTaiKhoanNguon,NgayGiaoDich DESC) INCLUDE (MaTaiKhoanDich,SoTien,TrangThai,MaLoaiGD);
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE object_id=OBJECT_ID(N'dbo.LichSuGiaoDich') AND name=N'IX_LichSu_TaiKhoanThoiGian')
    CREATE INDEX IX_LichSu_TaiKhoanThoiGian
    ON dbo.LichSuGiaoDich(SoTaiKhoanLienQuan,ThoiGianGhiLog DESC) INCLUDE (MaGiaoDich,HanhDong,SoTienThayDoi,SoDuSauThayDoi);
GO

CREATE OR ALTER TRIGGER dbo.trg_Teller_GiaoDich_AuditInsert
ON dbo.GiaoDichChuyenTien AFTER INSERT AS
BEGIN
    SET NOCOUNT ON;

    INSERT dbo.AuditNganHang (BangDuLieu, KhoaDuLieu, HanhDong, ChiTiet)
    SELECT
        N'GiaoDichChuyenTien',
        CONVERT(NVARCHAR(100), i.MaGiaoDich),
        N'Tạo giao dịch',
        CONCAT(
            N'Nguồn=', i.MaTaiKhoanNguon,
            N'; Đích=', COALESCE(i.SoTaiKhoanDich, i.MaTaiKhoanDich, N'N/A'),
            N'; Số tiền=', CONVERT(NVARCHAR(50), i.SoTien),
            N'; Loại=', i.MaLoaiGD,
            N'; Trạng thái=', i.TrangThai,
            N'; Thời gian=', CONVERT(NVARCHAR(30), i.NgayGiaoDich, 126)
        )
    FROM inserted AS i;
END;
GO

IF DATABASE_PRINCIPAL_ID('role_GDV') IS NULL
	CREATE ROLE role_GDV AUTHORIZATION dbo
IF DATABASE_PRINCIPAL_ID('user_GDV') IS NULL
	CREATE USER user_GDV WITHOUT LOGIN

ALTER ROLE role_GDV ADD MEMBER user_GDV

GRANT EXEC ON dbo.sp_Teller_Dashboard TO user_GDV
REVOKE EXEC ON dbo.sp_Teller_Dashboard FROM user_GDV
DENY EXEC ON dbo.sp_Teller_Dashboard TO user_GDV

EXEC AS USER = 'user_GDV'
GO

EXEC dbo.sp_Teller_Dashboard;
GO

REVERT;
GO
