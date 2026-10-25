USE DB_ChuyenTienNganHang;
GO

-- =============================================
-- 1. XÓA DỮ LIỆU CŨ (THEO THỨ TỰ KHÓA NGOẠI)
-- =============================================
DELETE FROM dbo.LichSuGiaoDich;
DELETE FROM dbo.GiaoDichChuyenTien;
DELETE FROM dbo.TaiKhoanNganHang;
DELETE FROM dbo.NhanVien;
DELETE FROM dbo.KhachHang;
DELETE FROM dbo.TaiKhoanDangNhap_VaiTro;
DELETE FROM dbo.[HanMucGiaoDịch];
DELETE FROM dbo.ChiNhanh;
DELETE FROM dbo.LoaiGiaoDich;
DELETE FROM dbo.NganHang;
GO

-- =============================================
-- 2. CHI NHÁNH
-- =============================================
INSERT INTO dbo.ChiNhanh (MaChiNhanh, TenChiNhanh, DiaChi)
VALUES
('CN001', N'Chi nhánh TP. Hồ Chí Minh', N'123 Nguyễn Huệ, Quận 1, TP. Hồ Chí Minh'),
('CN002', N'Chi nhánh Hà Nội', N'25 Trần Hưng Đạo, Hoàn Kiếm, Hà Nội'),
('CN003', N'Chi nhánh Đà Nẵng', N'100 Nguyễn Văn Linh, Hải Châu, Đà Nẵng');
GO

-- =============================================
-- 3. HẠN MỨC GIAO DỊCH
-- =============================================
INSERT INTO dbo.[HanMucGiaoDịch] (MaHanMuc, TenHanMuc, HanMucNgay)
VALUES
('HM001', N'Hạn mức cơ bản', 50000000),
('HM002', N'Hạn mức tiêu chuẩn', 200000000),
('HM003', N'Hạn mức cao cấp', 1000000000);
GO

-- =============================================
-- 4. DANH MỤC NGÂN HÀNG (Bổ sung - Khắc phục lỗi FK)
-- =============================================
INSERT INTO dbo.NganHang (MaNganHang, TenNganHang, TrangThai)
VALUES
('SMART', N'Ngân hàng SMART BANK', N'Hoạt động'),
('VCB', N'Ngân hàng Vietcombank', N'Hoạt động'),
('CTG', N'Ngân hàng VietinBank', N'Hoạt động'),
('BIDV', N'Ngân hàng BIDV', N'Hoạt động');
GO

-- =============================================
-- 5. LOẠI GIAO DỊCH (Bổ sung - Khắc phục lỗi FK)
-- =============================================
INSERT INTO dbo.LoaiGiaoDich (MaLoaiGD, TenLoaiGD)
VALUES
('CK01', N'Chuyển khoản nội bộ / Liên ngân hàng'),
('TT01', N'Thanh toán hóa đơn / Dịch vụ'),
('NT01', N'Nạp tiền mặt tại quầy'),
('RT01', N'Rút tiền mặt tại quầy');
GO

-- =============================================
-- 6. TÀI KHOẢN ĐĂNG NHẬP
-- =============================================
INSERT INTO dbo.TaiKhoanDangNhap_VaiTro (TenDangNhap, MatKhau, RoleName, MaNguoiDung)
VALUES
('admin',      '123456', N'Admin',          'NV001'),
('cskh01',     '123456', N'Nhân viên CSKH', 'NV002'),
('gdv01',      '123456', N'Giao dịch viên', 'NV003'),
('nguyenvana', '123456', N'Khách hàng',     'KH000001'),
('tranthib',   '123456', N'Khách hàng',     'KH000002'),
('levanc',     '123456', N'Khách hàng',     'KH000003'),
('phamthid',   '123456', N'Khách hàng',     'KH000004'),
('hoangvane',  '123456', N'Khách hàng',     'KH000005'),
('vuthif',     '123456', N'Khách hàng',     'KH000006');
GO

-- =============================================
-- 7. KHÁCH HÀNG
-- =============================================
INSERT INTO dbo.KhachHang (MaKhachHang, HoTen, SoCCCD, SoDienThoai, Email, DiaChi, TenDangNhap)
VALUES
('KH000001', N'Nguyễn Văn An', '079201000001', '09123456781', 'an.nguyen@example.com', N'Quận 1, TP. Hồ Chí Minh', 'nguyenvana'),
('KH000002', N'Trần Thị Bình', '079201000002', '09123456782', 'binh.tran@example.com', N'Thủ Đức, TP. Hồ Chí Minh', 'tranthib'),
('KH000003', N'Lê Văn Cường', '001201000003', '09123456783', 'cuong.le@example.com', N'Cầu Giấy, Hà Nội', 'levanc'),
('KH000004', N'Phạm Thị Dung', '001201000004', '09123456784', 'dung.pham@example.com', N'Đống Đa, Hà Nội', 'phamthid'),
('KH000005', N'Hoàng Văn Em', '048201000005', '09123456785', 'em.hoang@example.com', N'Hải Châu, Đà Nẵng', 'hoangvane'),
('KH000006', N'Vũ Thị Phương', '048201000006', '09123456786', 'phuong.vu@example.com', N'Sơn Trà, Đà Nẵng', 'vuthif');
GO

-- =============================================
-- 8. NHÂN VIÊN
-- =============================================
INSERT INTO dbo.NhanVien (MaNhanVien, HoTen, SoDienThoai, DiaChi, ChucVu, MaChiNhanh, TenDangNhap)
VALUES
('NV001', N'Nguyễn Minh Tuấn', '09881234561', N'Quận 3, TP. Hồ Chí Minh', N'Quản trị viên', 'CN001', 'admin'),
('NV002', N'Trần Ngọc Mai', '09881234562', N'Quận 7, TP. Hồ Chí Minh', N'Nhân viên CSKH', 'CN001', 'cskh01'),
('NV003', N'Lê Quốc Huy', '09881234563', N'Hoàn Kiếm, Hà Nội', N'Giao dịch viên', 'CN002', 'gdv01');
GO

-- =============================================
-- 9. TÀI KHOẢN NGÂN HÀNG
-- =============================================
INSERT INTO dbo.TaiKhoanNganHang (MaTaiKhoan, MaKhachHang, SoTaiKhoan, SoDu, TrangThai, MaChiNhanh, MaHanMuc, MaNganHang)
VALUES
('TK001', 'KH000001', '100000000000001', 27650000, N'Hoạt động', 'CN001', 'HM002', 'SMART'),
('TK002', 'KH000002', '100000000000002', 19500000, N'Hoạt động', 'CN001', 'HM001', 'SMART'),
('TK003', 'KH000003', '100000000000003', 37500000, N'Hoạt động', 'CN002', 'HM002', 'SMART'),
('TK004', 'KH000004', '100000000000004', 8850000, N'Hoạt động', 'CN002', 'HM001', 'SMART'),
('TK005', 'KH000005', '100000000000005', 67000000, N'Hoạt động', 'CN003', 'HM003', 'SMART'),
('TK006', 'KH000006', '100000000000006', 12000000, N'Hoạt động', 'CN003', 'HM001', 'SMART');
GO

-- =============================================
-- 10. GIAO DỊCH CHUYỂN TIỀN
-- =============================================
INSERT INTO dbo.GiaoDichChuyenTien (MaGiaoDich, MaTaiKhoanNguon, MaTaiKhoanDich, SoTien, NgayGiaoDich, NoiDung, MaLoaiGD, TrangThai)
VALUES
('GD001', 'TK001', 'TK002', 2000000, '2026-10-01T09:15:00', N'Chuyển tiền cho bạn', 'CK01', N'Thành công'),
('GD002', 'TK002', 'TK003', 1500000, '2026-10-02T10:30:00', N'Thanh toán tiền hàng', 'CK01', N'Thành công'),
('GD003', 'TK003', 'TK001', 5000000, '2026-10-03T08:45:00', N'Chuyển tiền giữa hai tài khoản', 'CK01', N'Thành công'),
('GD004', 'TK003', 'TK002', 1000000, '2026-10-04T14:20:00', N'Chuyển tiền cá nhân', 'CK01', N'Thành công'),
('GD005', 'TK001', 'TK004', 350000,  '2026-10-05T16:10:00', N'Thanh toán hóa đơn', 'TT01', N'Thành công'),
('GD006', 'TK004', 'TK005', 3000000, '2026-10-06T11:00:00', N'Chuyển tiền cho người thân', 'CK01', N'Chờ xử lý'),
('GD007', 'TK005', 'TK006', 500000,  '2026-10-07T13:30:00', N'Giao dịch thử nghiệm thất bại', 'CK01', N'Thất bại'),
('GD008', 'TK006', 'TK001', 200000,  '2026-10-08T15:45:00', N'Giao dịch đã hủy', 'CK01', N'Đã hủy');
GO

-- =============================================
-- 11. LỊCH SỬ GIAO DỊCH
-- =============================================
INSERT INTO dbo.LichSuGiaoDich (MaGiaoDich, SoTaiKhoanLienQuan, HanhDong, SoTienThayDoi, SoDuSauThayDoi, ThoiGianGhiLog)
VALUES
-- GD001
('GD001', '100000000000001', N'Trừ tiền chuyển khoản', -2000000, 23000000, '2026-10-01T09:15:01'),
('GD001', '100000000000002', N'Cộng tiền nhận chuyển khoản', 2000000, 20000000, '2026-10-01T09:15:02'),
-- GD002
('GD002', '100000000000002', N'Trừ tiền chuyển khoản', -1500000, 18500000, '2026-10-02T10:30:01'),
('GD002', '100000000000003', N'Cộng tiền nhận chuyển khoản', 1500000, 43500000, '2026-10-02T10:30:02'),
-- GD003
('GD003', '100000000000003', N'Trừ tiền chuyển khoản', -5000000, 38500000, '2026-10-03T08:45:01'),
('GD003', '100000000000001', N'Cộng tiền nhận chuyển khoản', 5000000, 28000000, '2026-10-03T08:45:02'),
-- GD004
('GD004', '100000000000003', N'Trừ tiền chuyển khoản', -1000000, 37500000, '2026-10-04T14:20:01'),
('GD004', '100000000000002', N'Cộng tiền nhận chuyển khoản', 1000000, 19500000, '2026-10-04T14:20:02'),
-- GD005
('GD005', '100000000000001', N'Trừ tiền thanh toán', -350000, 27650000, '2026-10-05T16:10:01'),
('GD005', '100000000000004', N'Cộng tiền thanh toán', 350000, 8850000, '2026-10-05T16:10:02'),
-- GD006: Chờ xử lý
('GD006', '100000000000004', N'Yêu cầu chuyển khoản', 0, 8850000, '2026-10-06T11:00:01'),
-- GD007: Thất bại
('GD007', '100000000000005', N'Giao dịch thất bại', 0, 67000000, '2026-10-07T13:30:01'),
-- GD008: Đã hủy
('GD008', '100000000000006', N'Giao dịch đã hủy', 0, 12000000, '2026-10-08T15:45:01');
GO

-- =============================================
-- 12. KIỂM TRA DỮ LIỆU SAU KHỦNG
-- =============================================
SELECT * FROM dbo.TaiKhoanDangNhap_VaiTro;
SELECT * FROM dbo.ChiNhanh;
SELECT * FROM dbo.KhachHang;
SELECT * FROM dbo.[HanMucGiaoDịch];
SELECT * FROM dbo.NganHang;
SELECT * FROM dbo.TaiKhoanNganHang;
SELECT * FROM dbo.LoaiGiaoDich;
SELECT * FROM dbo.NhanVien;
SELECT * FROM dbo.GiaoDichChuyenTien;
SELECT * FROM dbo.LichSuGiaoDich;
GO
