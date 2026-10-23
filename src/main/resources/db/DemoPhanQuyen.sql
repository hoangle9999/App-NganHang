/*
    SQL Server RBAC demo for Admin, CSKH, Teller, and Customer.
    Run after Admin.sql, CSKH.sql, Teller.sql, and KH.sql.

    The demo principals have no SQL Server login. EXECUTE AS USER is used
    inside SSMS to test database roles independently of Windows Authentication.
*/
USE DB_ChuyenTienNganHang;
GO

-- Compatibility cleanup for the old demo procedure formerly kept in Admin.sql.
DROP PROCEDURE IF EXISTS dbo.SP_Admin_DemoCapThuHoiExecute;
GO

-- Demo-specific database roles belong in this file, not in feature SQL files.
IF DATABASE_PRINCIPAL_ID(N'role_CSKH') IS NULL CREATE ROLE role_CSKH AUTHORIZATION dbo;
IF DATABASE_PRINCIPAL_ID(N'role_Teller') IS NULL CREATE ROLE role_Teller AUTHORIZATION dbo;
IF DATABASE_PRINCIPAL_ID(N'role_Customer') IS NULL CREATE ROLE role_Customer AUTHORIZATION dbo;
GO

IF DATABASE_PRINCIPAL_ID(N'demo_admin') IS NULL CREATE USER demo_admin WITHOUT LOGIN;
IF DATABASE_PRINCIPAL_ID(N'demo_cskh') IS NULL CREATE USER demo_cskh WITHOUT LOGIN;
IF DATABASE_PRINCIPAL_ID(N'demo_gdv') IS NULL CREATE USER demo_gdv WITHOUT LOGIN;
IF DATABASE_PRINCIPAL_ID(N'demo_kh') IS NULL CREATE USER demo_kh WITHOUT LOGIN;
GO

IF NOT EXISTS (SELECT 1 FROM sys.database_role_members WHERE role_principal_id=DATABASE_PRINCIPAL_ID(N'role_BankAdmin') AND member_principal_id=DATABASE_PRINCIPAL_ID(N'demo_admin'))
    ALTER ROLE role_BankAdmin ADD MEMBER demo_admin;
IF NOT EXISTS (SELECT 1 FROM sys.database_role_members WHERE role_principal_id=DATABASE_PRINCIPAL_ID(N'role_CSKH') AND member_principal_id=DATABASE_PRINCIPAL_ID(N'demo_cskh'))
    ALTER ROLE role_CSKH ADD MEMBER demo_cskh;
IF NOT EXISTS (SELECT 1 FROM sys.database_role_members WHERE role_principal_id=DATABASE_PRINCIPAL_ID(N'role_Teller') AND member_principal_id=DATABASE_PRINCIPAL_ID(N'demo_gdv'))
    ALTER ROLE role_Teller ADD MEMBER demo_gdv;
IF NOT EXISTS (SELECT 1 FROM sys.database_role_members WHERE role_principal_id=DATABASE_PRINCIPAL_ID(N'role_Customer') AND member_principal_id=DATABASE_PRINCIPAL_ID(N'demo_kh'))
    ALTER ROLE role_Customer ADD MEMBER demo_kh;
GO

/*
    Admin-only permission manager for a fixed allowlist of role-owned objects.
    Views and table-valued functions use SELECT; procedures and scalar
    functions use EXECUTE. Customer direct view access stays denied by design;
    Customer receives its KH stored procedures only.
*/
CREATE OR ALTER PROCEDURE dbo.SP_Admin_DemoQuanLyQuyenRole
    @RoleName SYSNAME,
    @HanhDong VARCHAR(10),
    @ObjectName SYSNAME = NULL,
    @PermissionName VARCHAR(10) = NULL
WITH EXECUTE AS OWNER
AS
BEGIN
    SET NOCOUNT ON;
    SET @HanhDong = UPPER(LTRIM(RTRIM(@HanhDong)));

    IF @RoleName NOT IN (N'role_CSKH',N'role_Teller',N'role_Customer')
        THROW 52601,N'Chi duoc quan ly role CSKH, Teller hoac Customer.',1;

    IF @HanhDong NOT IN ('GRANT_ALL','GRANT','REVOKE')
        THROW 52602,N'Hanh dong chi duoc la GRANT_ALL, GRANT hoac REVOKE.',1;

    DECLARE @Targets TABLE(ObjectName SYSNAME PRIMARY KEY,ObjectType CHAR(2) NOT NULL);

    INSERT @Targets(ObjectName,ObjectType)
    SELECT o.name,o.type
    FROM sys.objects AS o
    WHERE o.schema_id=SCHEMA_ID(N'dbo')
      AND (
        (@RoleName=N'role_CSKH' AND (
             (o.name LIKE N'sp_CSKH[_]%' AND o.type IN ('P','PC'))
          OR (o.name LIKE N'vw_CSKH[_]%' AND o.type='V')
          OR (o.name LIKE N'fn_CSKH[_]%' AND o.type IN ('IF','TF'))
        ))
        OR (@RoleName=N'role_Teller' AND (
             ((o.name LIKE N'SP_GDV[_]%' OR o.name LIKE N'sp_Teller[_]%') AND o.type IN ('P','PC'))
          OR ((o.name LIKE N'vw_GDV[_]%' OR o.name LIKE N'vw_Teller[_]%') AND o.type='V')
          OR (o.name LIKE N'fn_GDV[_]%' AND o.type IN ('FN','FS'))
        ))
        OR (@RoleName=N'role_Customer' AND o.name LIKE N'sp_KH[_]%' AND o.type IN ('P','PC'))
      );

    IF @HanhDong='GRANT_ALL'
    BEGIN
        IF @ObjectName IS NOT NULL OR @PermissionName IS NOT NULL
            THROW 52603,N'GRANT_ALL khong nhan ObjectName hoac PermissionName.',1;

        DECLARE @Name SYSNAME,@Type CHAR(2),@Permission VARCHAR(10),@Sql NVARCHAR(MAX);
        DECLARE object_cursor CURSOR LOCAL FAST_FORWARD FOR
            SELECT ObjectName,ObjectType FROM @Targets ORDER BY ObjectName;
        OPEN object_cursor;
        FETCH NEXT FROM object_cursor INTO @Name,@Type;
        WHILE @@FETCH_STATUS=0
        BEGIN
            SET @Permission=CASE WHEN @Type IN ('P','PC','FN','FS') THEN 'EXECUTE' ELSE 'SELECT' END;
            SET @Sql=N'GRANT '+@Permission+N' ON OBJECT::dbo.'+QUOTENAME(@Name)+N' TO '+QUOTENAME(@RoleName)+N';';
            EXEC sys.sp_executesql @Sql;
            FETCH NEXT FROM object_cursor INTO @Name,@Type;
        END;
        CLOSE object_cursor;
        DEALLOCATE object_cursor;

        SELECT @RoleName AS RoleDuocCap,COUNT(*) AS SoObjectDuocCap FROM @Targets;
        RETURN;
    END;

    IF @ObjectName IS NULL OR @PermissionName IS NULL
        THROW 52604,N'GRANT/REVOKE can ObjectName va PermissionName.',1;

    SELECT @Type=ObjectType FROM @Targets WHERE ObjectName=@ObjectName;
    IF @Type IS NULL
        THROW 52605,N'Object khong thuoc allowlist cua role nay.',1;

    SET @Permission=CASE WHEN @Type IN ('P','PC','FN','FS') THEN 'EXECUTE' ELSE 'SELECT' END;
    IF UPPER(@PermissionName)<>@Permission
        THROW 52606,N'Permission khong dung voi loai object (SP/scalar function=EXECUTE; view/table function=SELECT).',1;

    SET @Sql=CASE WHEN @HanhDong='GRANT'
        THEN N'GRANT '+@Permission+N' ON OBJECT::dbo.'+QUOTENAME(@ObjectName)+N' TO '+QUOTENAME(@RoleName)+N';'
        ELSE N'REVOKE '+@Permission+N' ON OBJECT::dbo.'+QUOTENAME(@ObjectName)+N' FROM '+QUOTENAME(@RoleName)+N';'
    END;
    EXEC sys.sp_executesql @Sql;
    SELECT @RoleName AS RoleDuocCapThuHoi,@ObjectName AS ObjectName,@Permission AS PermissionName,@HanhDong AS HanhDong;
END;
GO

-- Admin grants every role-appropriate function/view/procedure permission.
EXECUTE AS USER=N'demo_admin';
EXEC dbo.SP_Admin_DemoQuanLyQuyenRole N'role_CSKH','GRANT_ALL';
EXEC dbo.SP_Admin_DemoQuanLyQuyenRole N'role_Teller','GRANT_ALL';
EXEC dbo.SP_Admin_DemoQuanLyQuyenRole N'role_Customer','GRANT_ALL';
REVERT;
GO

/*
    For each business subject: Admin revokes one concrete object permission,
    then that subject directly calls the same object. The expected result is
    SQL Server error 229 captured below. These are actual SELECT/EXEC calls,
    not just permission metadata checks.
*/

-- CSKH: revoke SELECT on its view, then call that exact view as demo_cskh.
EXECUTE AS USER=N'demo_admin';
EXEC dbo.SP_Admin_DemoQuanLyQuyenRole N'role_CSKH','REVOKE',N'vw_CSKH_TaiKhoanKhachHang','SELECT';
REVERT;
GO
EXECUTE AS USER=N'demo_cskh';
BEGIN TRY
    EXEC sys.sp_executesql N'SELECT TOP (1) * FROM dbo.vw_CSKH_TaiKhoanKhachHang;';
    PRINT N'LOI DEMO CSKH: SELECT tren view van thanh cong sau khi thu hoi.';
END TRY
BEGIN CATCH
    SELECT N'CSKH bi chan khi goi dbo.vw_CSKH_TaiKhoanKhachHang' AS KetQua,
           ERROR_NUMBER() AS MaLoi, ERROR_MESSAGE() AS ThongBaoSQL;
END CATCH;
REVERT;
GO

-- Teller/GDV: revoke SELECT on its view, then call that exact view as demo_gdv.
EXECUTE AS USER=N'demo_admin';
EXEC dbo.SP_Admin_DemoQuanLyQuyenRole N'role_Teller','REVOKE',N'vw_GDV_DanhSachTaiKhoan','SELECT';
REVERT;
GO
EXECUTE AS USER=N'demo_gdv';
BEGIN TRY
    EXEC sys.sp_executesql N'SELECT TOP (1) * FROM dbo.vw_GDV_DanhSachTaiKhoan;';
    PRINT N'LOI DEMO GDV: SELECT tren view van thanh cong sau khi thu hoi.';
END TRY
BEGIN CATCH
    SELECT N'GDV bi chan khi goi dbo.vw_GDV_DanhSachTaiKhoan' AS KetQua,
           ERROR_NUMBER() AS MaLoi, ERROR_MESSAGE() AS ThongBaoSQL;
END CATCH;
REVERT;
GO

-- Customer: revoke EXECUTE on its account-list procedure, then call that SP.
EXECUTE AS USER=N'demo_admin';
EXEC dbo.SP_Admin_DemoQuanLyQuyenRole N'role_Customer','REVOKE',N'sp_KH_DanhSachTaiKhoan','EXECUTE';
REVERT;
GO
EXECUTE AS USER=N'demo_kh';
BEGIN TRY
    EXEC sys.sp_executesql N'EXEC dbo.sp_KH_DanhSachTaiKhoan @MaKhachHang=@MaKH, @TenDangNhap=@TenDN;',
        N'@MaKH VARCHAR(20), @TenDN VARCHAR(50)', N'__DEMO_KH__', N'__DEMO_KH__';
    PRINT N'LOI DEMO KH: EXEC procedure van thanh cong sau khi thu hoi.';
END TRY
BEGIN CATCH
    SELECT N'KH bi chan khi goi dbo.sp_KH_DanhSachTaiKhoan' AS KetQua,
           ERROR_NUMBER() AS MaLoi, ERROR_MESSAGE() AS ThongBaoSQL;
END CATCH;
REVERT;
GO
