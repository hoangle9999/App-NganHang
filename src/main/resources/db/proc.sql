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

-- Tạo mã khách hàng
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

-- Tạo mã tài khoản
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


-- Tạo số tài khoản
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
