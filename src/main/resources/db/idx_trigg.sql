-- 3. TẠO CHỈ MỤC (INDEX) TỐI ƯU TRUY VẤN
-- 3.1. Index theo ngày giao dịch
CREATE NONCLUSTERED INDEX IX_GiaoDich_NgayGiaoDich
ON dbo.GiaoDichChuyenTien(NgayGiaoDich);
GO

-- 3.2. Index theo tài khoản nguồn
CREATE NONCLUSTERED INDEX IX_GiaoDich_STK_Nguon
ON dbo.GiaoDichChuyenTien(MaTaiKhoanNguon);
GO

-- 3.3. Index theo tài khoản đích
CREATE NONCLUSTERED INDEX IX_GiaoDich_STK_Dich
ON dbo.GiaoDichChuyenTien(MaTaiKhoanDich);
GO

-- 3.4. Index theo khách hàng
CREATE NONCLUSTERED INDEX IX_TaiKhoan_MaKhachHang
ON dbo.TaiKhoanNganHang(MaKhachHang);
GO

-- 3.5. Index theo số tài khoản liên quan trong lịch sử
CREATE NONCLUSTERED INDEX IX_LichSuGD_STK
ON dbo.LichSuGiaoDich(SoTaiKhoanLienQuan);
GO

-- Lưu ý:
-- SoDienThoai của KhachHang đã có UNIQUE constraint.
-- SQL Server đã tạo chỉ mục duy nhất cho cột này,
-- nên không cần tạo thêm một index trùng lặp.


-- 4. TẠO CÁC TRIGGER


-- 4.1. Trigger kiểm tra hạn mức chuyển tiền trong ngày
--
-- INSTEAD OF INSERT cho phép kiểm tra hạn mức trước khi
-- ghi giao dịch thực tế vào bảng.
--
-- Hỗ trợ nhiều dòng INSERT và chỉ tính các giao dịch
-- có trạng thái Thành công.

CREATE TRIGGER dbo.TRG_KiemTra_HanMucNgay
ON dbo.GiaoDichChuyenTien
INSTEAD OF INSERT
AS
BEGIN
    SET NOCOUNT ON;

    -- Kiểm tra tổng số tiền của các giao dịch mới theo
    -- từng tài khoản nguồn, bao gồm các dòng thành công.
    IF EXISTS (
        SELECT 1
        FROM (
            SELECT
                i.MaTaiKhoanNguon,
                SUM(
                    CASE
                        WHEN i.TrangThai = N'Thành công'
                            THEN i.SoTien
                        ELSE 0
                    END
                ) AS TongTienMoi
            FROM inserted AS i
            GROUP BY i.MaTaiKhoanNguon
        ) AS gdMoi

        INNER JOIN dbo.TaiKhoanNganHang AS tk
            ON tk.MaTaiKhoan = gdMoi.MaTaiKhoanNguon

        INNER JOIN dbo.HanMucGiaoDịch AS hm
            ON hm.MaHanMuc = tk.MaHanMuc

        OUTER APPLY (
            SELECT
                SUM(gd.SoTien) AS TongTienDaChuyen
            FROM dbo.GiaoDichChuyenTien AS gd
            WHERE gd.MaTaiKhoanNguon = gdMoi.MaTaiKhoanNguon
              AND gd.TrangThai = N'Thành công'
              AND gd.NgayGiaoDich >= CONVERT(DATE, GETDATE())
              AND gd.NgayGiaoDich < DATEADD(
                    DAY,
                    1,
                    CONVERT(DATE, GETDATE())
                )
        ) AS gdNgay

        WHERE
            ISNULL(gdNgay.TongTienDaChuyen, 0)
            + gdMoi.TongTienMoi > hm.HanMucNgay
    )
    BEGIN
        ;THROW 51001,
            N'Giao dịch thất bại: Vượt quá hạn mức chuyển tiền trong ngày!',
            1;
    END;

    -- Nếu hợp lệ, thực hiện INSERT thực tế.
    INSERT INTO dbo.GiaoDichChuyenTien (
        MaGiaoDich,
        MaTaiKhoanNguon,
        MaTaiKhoanDich,
        SoTien,
        NgayGiaoDich,
        NoiDung,
        MaLoaiGD,
        TrangThai
    )
    SELECT
        MaGiaoDich,
        MaTaiKhoanNguon,
        MaTaiKhoanDich,
        SoTien,
        NgayGiaoDich,
        NoiDung,
        MaLoaiGD,
        TrangThai
    FROM inserted;
END;
GO

-- 4.2. Trigger tự động ghi nhận lịch sử giao dịch
--
-- Ghi hai dòng log cho mỗi giao dịch thành công:
-- 1. Trừ tiền ở tài khoản nguồn.
-- 2. Cộng tiền ở tài khoản đích.
--
-- Sử dụng truy vấn theo tập hợp để hỗ trợ nhiều dòng.

CREATE TRIGGER dbo.TRG_GhiNhan_LichSuGiaoDich
ON dbo.GiaoDichChuyenTien
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO dbo.LichSuGiaoDich (
        MaGiaoDich,
        SoTaiKhoanLienQuan,
        HanhDong,
        SoTienThayDoi,
        SoDuSauThayDoi
    )
    SELECT
        i.MaGiaoDich,
        tk.SoTaiKhoan,
        N'Trừ tiền chuyển khoản',
        -i.SoTien,
        tk.SoDu
    FROM inserted AS i
    INNER JOIN dbo.TaiKhoanNganHang AS tk
        ON tk.MaTaiKhoan = i.MaTaiKhoanNguon
    WHERE i.TrangThai = N'Thành công'

    UNION ALL

    SELECT
        i.MaGiaoDich,
        tk.SoTaiKhoan,
        N'Cộng tiền nhận khoản',
        i.SoTien,
        tk.SoDu
    FROM inserted AS i
    INNER JOIN dbo.TaiKhoanNganHang AS tk
        ON tk.MaTaiKhoan = i.MaTaiKhoanDich
    WHERE i.TrangThai = N'Thành công';
END;
GO


-- 5. TẠO STORED PROCEDURE
-- 5.1. Stored Procedure thực hiện chuyển tiền an toàn
--
-- Kiểm tra:
-- - Mã giao dịch và tài khoản hợp lệ.
-- - Số tiền lớn hơn 0.
-- - Tài khoản nguồn và đích khác nhau.
-- - Loại giao dịch tồn tại.
-- - Hai tài khoản đều hoạt động.
-- - Tài khoản nguồn đủ số dư.
--
-- Transaction bảo đảm nếu một bước thất bại thì việc
-- trừ tiền, cộng tiền và ghi giao dịch đều được hoàn tác.


CREATE OR ALTER PROCEDURE dbo.sp_ChuyenTienNoiBo
    @SoTaiKhoanNguon VARCHAR(15),
    @SoTaiKhoanDich VARCHAR(15),
    @MaNganHangNhan VARCHAR(20),
    @SoTien DECIMAL(18, 2),
    @NoiDung NVARCHAR(255) = NULL,
    @KetQua BIT OUTPUT,
    @ThongBao NVARCHAR(255) OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    SET @KetQua = 0;
    SET @ThongBao = N'Giao dịch thất bại.';

    -- 1. Kiểm tra dữ liệu đầu vào
    IF @SoTaiKhoanNguon IS NULL
       OR @SoTaiKhoanDich IS NULL
       OR @MaNganHangNhan IS NULL
       OR @SoTien IS NULL
       OR @SoTien <= 0
    BEGIN
        SET @ThongBao = N'Dữ liệu đầu vào không hợp lệ.';
        RETURN;
    END;

    IF @SoTaiKhoanNguon = @SoTaiKhoanDich
    BEGIN
        SET @ThongBao = N'Không thể chuyển tiền cho chính mình.';
        RETURN;
    END;

    IF @MaNganHangNhan <> 'SMART'
    BEGIN
        SET @ThongBao = N'Hiện chỉ hỗ trợ chuyển tiền nội bộ SMART BANK.';
        RETURN;
    END;

    DECLARE
        @MaTaiKhoanNguon VARCHAR(20),
        @MaTaiKhoanDich VARCHAR(20),
        @MaTaiKhoanDau VARCHAR(20),
        @MaTaiKhoanSau VARCHAR(20),

        @SoDuNguon DECIMAL(18, 2),
        @SoDuDich DECIMAL(18, 2),
        @SoDuNguonMoi DECIMAL(18, 2),
        @SoDuDichMoi DECIMAL(18, 2),

        @MaHanMuc VARCHAR(10),
        @HanMucNgay DECIMAL(18, 2),
        @TongTienTrongNgay DECIMAL(18, 2),

        @TrangThaiNguon NVARCHAR(20),
        @TrangThaiDich NVARCHAR(20),
        @NganHangDich VARCHAR(20),

        @MaGiaoDich VARCHAR(30),
        @NgayGiaoDich DATETIME = GETDATE();

    BEGIN TRY
        BEGIN TRANSACTION;

        -- 2. Tìm mã nội bộ của hai tài khoản
        SELECT @MaTaiKhoanNguon = MaTaiKhoan
        FROM dbo.TaiKhoanNganHang
        WHERE SoTaiKhoan = @SoTaiKhoanNguon;

        SELECT @MaTaiKhoanDich = MaTaiKhoan
        FROM dbo.TaiKhoanNganHang
        WHERE SoTaiKhoan = @SoTaiKhoanDich;

        IF @MaTaiKhoanNguon IS NULL
           OR @MaTaiKhoanDich IS NULL
        BEGIN
            THROW 50001, N'Số tài khoản nguồn hoặc đích không tồn tại.', 1;
        END;

        -- 3. Xác định thứ tự khóa cố định
        SET @MaTaiKhoanDau =
            CASE
                WHEN @MaTaiKhoanNguon < @MaTaiKhoanDich
                    THEN @MaTaiKhoanNguon
                ELSE @MaTaiKhoanDich
            END;

        SET @MaTaiKhoanSau =
            CASE
                WHEN @MaTaiKhoanNguon < @MaTaiKhoanDich
                    THEN @MaTaiKhoanDich
                ELSE @MaTaiKhoanNguon
            END;

        -- Khóa lần lượt theo thứ tự cố định và giữ khóa đến cuối transaction
        SELECT
            @SoDuNguon = CASE
                WHEN MaTaiKhoan = @MaTaiKhoanNguon THEN SoDu
                ELSE @SoDuNguon
            END,
            @TrangThaiNguon = CASE
                WHEN MaTaiKhoan = @MaTaiKhoanNguon THEN TrangThai
                ELSE @TrangThaiNguon
            END,
            @MaHanMuc = CASE
                WHEN MaTaiKhoan = @MaTaiKhoanNguon THEN MaHanMuc
                ELSE @MaHanMuc
            END,
            @SoDuDich = CASE
                WHEN MaTaiKhoan = @MaTaiKhoanDich THEN SoDu
                ELSE @SoDuDich
            END,
            @TrangThaiDich = CASE
                WHEN MaTaiKhoan = @MaTaiKhoanDich THEN TrangThai
                ELSE @TrangThaiDich
            END,
            @NganHangDich = CASE
                WHEN MaTaiKhoan = @MaTaiKhoanDich THEN MaNganHang
                ELSE @NganHangDich
            END
        FROM dbo.TaiKhoanNganHang WITH (UPDLOCK, HOLDLOCK)
        WHERE MaTaiKhoan IN (@MaTaiKhoanDau, @MaTaiKhoanSau)
        ORDER BY MaTaiKhoan;

        IF @SoDuNguon IS NULL OR @SoDuDich IS NULL
        BEGIN
            THROW 50002, N'Không thể đọc thông tin tài khoản.', 1;
        END;

        IF @TrangThaiNguon <> N'Hoạt động'
           OR @TrangThaiDich <> N'Hoạt động'
        BEGIN
            THROW 50003, N'Tài khoản nguồn hoặc đích không hoạt động.', 1;
        END;

        IF @NganHangDich IS NULL OR @NganHangDich <> 'SMART'
        BEGIN
            THROW 50004, N'Tài khoản nhận không thuộc SMART BANK.', 1;
        END;

        -- 4. Kiểm tra số dư
        IF @SoDuNguon < @SoTien
        BEGIN
            THROW 50005, N'Số dư không đủ.', 1;
        END;

        -- 5. Kiểm tra hạn mức ngày
        IF @MaHanMuc IS NULL
        BEGIN
            THROW 50006, N'Tài khoản nguồn chưa được gán hạn mức.', 1;
        END;

        SELECT @HanMucNgay = HanMucNgay
        FROM dbo.[HanMucGiaoDịch]
        WHERE MaHanMuc = @MaHanMuc;

        IF @HanMucNgay IS NULL
        BEGIN
            THROW 50007, N'Không tìm thấy hạn mức giao dịch.', 1;
        END;

        SELECT @TongTienTrongNgay =
            COALESCE(SUM(SoTien), 0)
        FROM dbo.GiaoDichChuyenTien
        WHERE MaTaiKhoanNguon = @MaTaiKhoanNguon
          AND NgayGiaoDich >= CONVERT(DATE, @NgayGiaoDich)
          AND NgayGiaoDich < DATEADD(
                DAY, 1, CONVERT(DATE, @NgayGiaoDich)
              )
          AND TrangThai = N'Thành công';

        IF @TongTienTrongNgay + @SoTien > @HanMucNgay
        BEGIN
            THROW 50008, N'Vượt hạn mức giao dịch trong ngày.', 1;
        END;

        -- 6. Tính số dư mới
        SET @SoDuNguonMoi = @SoDuNguon - @SoTien;
        SET @SoDuDichMoi = @SoDuDich + @SoTien;

        -- 7. Cập nhật số dư
        UPDATE dbo.TaiKhoanNganHang
        SET SoDu = @SoDuNguonMoi
        WHERE MaTaiKhoan = @MaTaiKhoanNguon;

        IF @@ROWCOUNT <> 1
        BEGIN
            THROW 50009, N'Không cập nhật được số dư nguồn.', 1;
        END;

        UPDATE dbo.TaiKhoanNganHang
        SET SoDu = @SoDuDichMoi
        WHERE MaTaiKhoan = @MaTaiKhoanDich;

        IF @@ROWCOUNT <> 1
        BEGIN
            THROW 50010, N'Không cập nhật được số dư đích.', 1;
        END;

        -- 8. Tạo mã giao dịch
        SET @MaGiaoDich =
            'GD' + LEFT(
                REPLACE(CONVERT(VARCHAR(36), NEWID()), '-', ''),
                26
            );

        -- 9. Ghi giao dịch thành công
        INSERT INTO dbo.GiaoDichChuyenTien
        (
            MaGiaoDich,
            MaTaiKhoanNguon,
            MaTaiKhoanDich,
            SoTien,
            NgayGiaoDich,
            NoiDung,
            MaLoaiGD,
            TrangThai
        )
        VALUES
        (
            @MaGiaoDich,
            @MaTaiKhoanNguon,
            @MaTaiKhoanDich,
            @SoTien,
            @NgayGiaoDich,
            @NoiDung,
            'CK01',
            N'Thành công'
        );

        -- 10. Ghi lịch sử tài khoản gửi
        INSERT INTO dbo.LichSuGiaoDich
        (
            MaGiaoDich,
            SoTaiKhoanLienQuan,
            HanhDong,
            SoTienThayDoi,
            SoDuSauThayDoi
        )
        VALUES
        (
            @MaGiaoDich,
            @SoTaiKhoanNguon,
            N'Chuyển tiền',
            -@SoTien,
            @SoDuNguonMoi
        );

        -- 11. Ghi lịch sử tài khoản nhận
        INSERT INTO dbo.LichSuGiaoDich
        (
            MaGiaoDich,
            SoTaiKhoanLienQuan,
            HanhDong,
            SoTienThayDoi,
            SoDuSauThayDoi
        )
        VALUES
        (
            @MaGiaoDich,
            @SoTaiKhoanDich,
            N'Nhận tiền',
            @SoTien,
            @SoDuDichMoi
        );

        COMMIT TRANSACTION;

        SET @KetQua = 1;
        SET @ThongBao = N'Chuyển tiền thành công.';
    END TRY
    BEGIN CATCH
        IF XACT_STATE() <> 0
            ROLLBACK TRANSACTION;

        SET @KetQua = 0;
        SET @ThongBao = ERROR_MESSAGE();
    END CATCH;
END;
GO


-- 5.2. Stored Procedure tra cứu lịch sử giao dịch
--
-- Trả về cả giao dịch chuyển đi và giao dịch nhận vào.

CREATE PROCEDURE dbo.SP_TraCuuLichSuGiaoDich
    @SoTaiKhoan VARCHAR(15)
AS
BEGIN
    SET NOCOUNT ON;

    SELECT
        gd.MaGiaoDich,
        gd.SoTien,
        gd.NgayGiaoDich,
        gd.NoiDung,
        gd.TrangThai,
        gd.MaTaiKhoanNguon,
        gd.MaTaiKhoanDich,
        tkn.SoTaiKhoan AS SoTaiKhoanNguon,
        tkd.SoTaiKhoan AS SoTaiKhoanDich
    FROM dbo.GiaoDichChuyenTien AS gd

    INNER JOIN dbo.TaiKhoanNganHang AS tkn
        ON gd.MaTaiKhoanNguon = tkn.MaTaiKhoan

    INNER JOIN dbo.TaiKhoanNganHang AS tkd
        ON gd.MaTaiKhoanDich = tkd.MaTaiKhoan

    WHERE tkn.SoTaiKhoan = @SoTaiKhoan
       OR tkd.SoTaiKhoan = @SoTaiKhoan

    ORDER BY gd.NgayGiaoDich DESC;
END;
GO

-- 6. KIỂM TRA SAU KHI KHỞI TẠO DATABASE
-- 6.1. Kiểm tra số lượng bảng
SELECT
    COUNT(*) AS SoLuongBang
FROM INFORMATION_SCHEMA.TABLES
WHERE TABLE_TYPE = 'BASE TABLE';
GO

-- 6.2. Kiểm tra các cột địa chỉ và số điện thoại
SELECT
    TABLE_NAME,
    COLUMN_NAME,
    DATA_TYPE,
    IS_NULLABLE
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME IN ('KhachHang', 'NhanVien')
  AND COLUMN_NAME IN ('DiaChi', 'SoDienThoai')
ORDER BY TABLE_NAME, COLUMN_NAME;
GO

-- 6.3. Kiểm tra loại giao dịch
SELECT
    MaLoaiGD,
    TenLoaiGD
FROM dbo.LoaiGiaoDich;
GO

-- 6.4. Kiểm tra triggers
SELECT
    name AS TenTrigger,
    OBJECT_NAME(parent_id) AS BangApDung,
    is_disabled AS DaVoHieuHoa
FROM sys.triggers
WHERE parent_id = OBJECT_ID('dbo.GiaoDichChuyenTien');
GO

-- 6.5. Kiểm tra stored procedures
SELECT
    name AS TenProcedure
FROM sys.procedures
WHERE name IN (
    'SP_ThucHienChuyenTien',
    'SP_TraCuuLichSuGiaoDich'
);
GO