/*
    Chỉ xóa thủ tục, khung nhìn, hàm và trigger trong schema dbo.
    Không xóa bảng, dữ liệu, khóa/ràng buộc, index, sequence, role hay user.
    Chạy trên database hiện có trước khi tạo lại các module SQL.
*/
USE DB_ChuyenTienNganHang;
GO
SET NOCOUNT ON;

DECLARE @Sql NVARCHAR(500);

-- Xóa khung nhìn trước vì chúng có thể tham chiếu lẫn nhau.
WHILE EXISTS (
    SELECT 1 FROM sys.objects
    WHERE type = 'V' AND is_ms_shipped = 0 AND schema_id = SCHEMA_ID(N'dbo')
)
BEGIN
    SELECT TOP (1) @Sql = N'DROP VIEW ' + QUOTENAME(SCHEMA_NAME(schema_id))
                              + N'.' + QUOTENAME(name) + N';'
    FROM sys.objects
    WHERE type = 'V' AND is_ms_shipped = 0 AND schema_id = SCHEMA_ID(N'dbo');
    EXEC sys.sp_executesql @Sql;
END;

-- Xóa stored procedure của schema dbo.
WHILE EXISTS (
    SELECT 1 FROM sys.objects
    WHERE type = 'P' AND is_ms_shipped = 0 AND schema_id = SCHEMA_ID(N'dbo')
)
BEGIN
    SELECT TOP (1) @Sql = N'DROP PROCEDURE ' + QUOTENAME(SCHEMA_NAME(schema_id))
                                    + N'.' + QUOTENAME(name) + N';'
    FROM sys.objects
    WHERE type = 'P' AND is_ms_shipped = 0 AND schema_id = SCHEMA_ID(N'dbo');
    EXEC sys.sp_executesql @Sql;
END;

-- Xóa function vô hướng và function trả về bảng trong schema dbo.
WHILE EXISTS (
    SELECT 1 FROM sys.objects
    WHERE is_ms_shipped = 0 AND schema_id = SCHEMA_ID(N'dbo')
      AND type IN ('FN', 'IF', 'TF')
)
BEGIN
    SELECT TOP (1) @Sql = N'DROP FUNCTION ' + QUOTENAME(SCHEMA_NAME(schema_id))
                                    + N'.' + QUOTENAME(name) + N';'
    FROM sys.objects
    WHERE is_ms_shipped = 0 AND schema_id = SCHEMA_ID(N'dbo')
      AND type IN ('FN', 'IF', 'TF');
    EXEC sys.sp_executesql @Sql;
END;

-- Xóa trigger DML do người dùng tạo trên các bảng trong schema dbo.
WHILE EXISTS (
    SELECT 1
    FROM sys.triggers AS tr
    JOIN sys.objects AS ob ON ob.object_id = tr.object_id
    WHERE tr.is_ms_shipped = 0 AND tr.parent_class = 1
      AND ob.schema_id = SCHEMA_ID(N'dbo')
)
BEGIN
    SELECT TOP (1) @Sql = N'DROP TRIGGER ' + QUOTENAME(SCHEMA_NAME(ob.schema_id))
                                     + N'.' + QUOTENAME(ob.name) + N';'
    FROM sys.triggers AS tr
    JOIN sys.objects AS ob ON ob.object_id = tr.object_id
    WHERE tr.is_ms_shipped = 0 AND tr.parent_class = 1
      AND ob.schema_id = SCHEMA_ID(N'dbo');
    EXEC sys.sp_executesql @Sql;
END;
GO
