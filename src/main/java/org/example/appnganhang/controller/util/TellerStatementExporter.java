package org.example.appnganhang.controller.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.appnganhang.controller.giaodichvien.TellerStatementController.StatementRow;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public final class TellerStatementExporter {
    private static final DecimalFormat MONEY = new DecimalFormat("#,##0.##");

    private TellerStatementExporter() {}

    public static void exportExcel(Path destination, String accountName, String accountNumber,
                                   String period, String opening, String closing, String credit,
                                   String debit, List<StatementRow> entries) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Sao ke");
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 15);
            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setFont(titleFont);

            Row title = sheet.createRow(0);
            title.createCell(0).setCellValue("SAO KÊ TÀI KHOẢN SMART BANK");
            title.getCell(0).setCellStyle(titleStyle);
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 6));
            addTextRow(sheet, 1, "Chủ tài khoản", accountName);
            addTextRow(sheet, 2, "Số tài khoản", accountNumber);
            addTextRow(sheet, 3, "Kỳ sao kê", period);
            addTextRow(sheet, 4, "Số dư đầu kỳ", opening);
            addTextRow(sheet, 5, "Số dư cuối kỳ", closing);
            addTextRow(sheet, 6, "Tổng phát sinh có", credit);
            addTextRow(sheet, 7, "Tổng phát sinh nợ", debit);

            String[] headers = {"Mã giao dịch", "Thời gian", "Loại giao dịch", "Tài khoản liên quan",
                    "Số tiền tăng / giảm (VND)", "Số dư sau giao dịch (VND)", "Nội dung"};
            Row header = sheet.createRow(9);
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setWrapText(true);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            DataFormat formats = workbook.createDataFormat();
            CellStyle moneyStyle = workbook.createCellStyle();
            moneyStyle.setDataFormat(formats.getFormat("#,##0.##;[Red]-#,##0.##"));
            for (int i = 0; i < entries.size(); i++) {
                StatementRow entry = entries.get(i);
                Row row = sheet.createRow(i + 10);
                row.createCell(0).setCellValue(safe(entry.getMaGiaoDich()));
                row.createCell(1).setCellValue(safe(entry.getThoiGianStr()));
                row.createCell(2).setCellValue(safe(entry.getLoaiGD()));
                row.createCell(3).setCellValue(safe(entry.getSoTKLienQuan()));
                Cell amount = row.createCell(4);
                amount.setCellValue(entry.getSoTienThayDoi() == null ? 0 : entry.getSoTienThayDoi().doubleValue());
                amount.setCellStyle(moneyStyle);
                Cell balance = row.createCell(5);
                balance.setCellValue(entry.getSoDuSauThayDoi() == null ? 0 : entry.getSoDuSauThayDoi().doubleValue());
                balance.setCellStyle(moneyStyle);
                row.createCell(6).setCellValue(safe(entry.getNoiDung()));
            }
            int[] widths = {20, 22, 28, 23, 27, 29, 48};
            for (int i = 0; i < widths.length; i++) sheet.setColumnWidth(i, widths[i] * 256);
            sheet.createFreezePane(0, 10);
            sheet.setAutoFilter(new org.apache.poi.ss.util.CellRangeAddress(9, 9 + entries.size(), 0, 6));
            try (var output = Files.newOutputStream(destination)) { workbook.write(output); }
        }
    }

    public static void exportPdf(Path destination, String accountName, String accountNumber,
                                 String period, String opening, String closing, String credit,
                                 String debit, List<StatementRow> entries) throws IOException {
        Path fontPath = locateVietnameseFont();
        try (PDDocument document = new PDDocument()) {
            PDType0Font font = PDType0Font.load(document, fontPath.toFile());
            PDRectangle pageSize = new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth());
            float[] widths = {78, 88, 104, 90, 112, 115, 175};
            float margin = 30;
            float bottom = 28;
            float width = pageSize.getWidth();
            float y;
            PDPageContentStream stream = null;
            try {
                stream = newPage(document, pageSize);
                y = drawPdfHeader(stream, font, width, margin, accountName, accountNumber,
                        period, opening, closing, credit, debit, widths);
                for (StatementRow entry : entries) {
                    String[] values = {
                            safe(entry.getMaGiaoDich()), safe(entry.getThoiGianStr()), safe(entry.getLoaiGD()),
                            safe(entry.getSoTKLienQuan()), signedMoney(entry.getSoTienThayDoi()),
                            money(entry.getSoDuSauThayDoi()), safe(entry.getNoiDung())
                    };
                    List<List<String>> wrapped = new ArrayList<>();
                    int maxLines = 1;
                    for (int i = 0; i < values.length; i++) {
                        List<String> lines = wrap(values[i], font, 7.2f, widths[i] - 8);
                        wrapped.add(lines);
                        maxLines = Math.max(maxLines, lines.size());
                    }
                    float rowHeight = maxLines * 9.5f + 7;
                    if (y - rowHeight < bottom) {
                        stream.close();
                        stream = newPage(document, pageSize);
                        y = drawTableHeader(stream, font, width, margin, widths, yTop(pageSize, margin));
                    }
                    float x = margin;
                    for (int i = 0; i < widths.length; i++) {
                        stream.addRect(x, y - rowHeight, widths[i], rowHeight);
                        stream.stroke();
                        for (int line = 0; line < wrapped.get(i).size(); line++) {
                            writeText(stream, font, 7.2f, Color.BLACK, x + 4,
                                    y - 12 - line * 9.5f, wrapped.get(i).get(line));
                        }
                        x += widths[i];
                    }
                    y -= rowHeight;
                }
                stream.close();
                stream = null;
            } finally {
                if (stream != null) stream.close();
            }
            document.save(destination.toFile());
        }
    }

    private static float drawPdfHeader(PDPageContentStream stream, PDType0Font font, float pageWidth,
                                       float margin, String accountName, String accountNumber, String period,
                                       String opening, String closing, String credit, String debit,
                                       float[] widths) throws IOException {
        writeText(stream, font, 15, new Color(36, 45, 90), margin, 555, "SAO KÊ TÀI KHOẢN SMART BANK");
        writeText(stream, font, 8.5f, Color.BLACK, margin, 535, "Chủ tài khoản: " + safe(accountName));
        writeText(stream, font, 8.5f, Color.BLACK, margin + 265, 535, "Số tài khoản: " + safe(accountNumber));
        writeText(stream, font, 8.5f, Color.BLACK, margin, 520, "Kỳ sao kê: " + safe(period));
        writeText(stream, font, 8.5f, Color.BLACK, margin, 505,
                "Số dư đầu kỳ: " + safe(opening) + "    Số dư cuối kỳ: " + safe(closing));
        writeText(stream, font, 8.5f, Color.BLACK, margin, 490,
                "Tổng phát sinh có: " + safe(credit) + "    Tổng phát sinh nợ: " + safe(debit));
        return drawTableHeader(stream, font, pageWidth, margin, widths, 474);
    }

    private static float drawTableHeader(PDPageContentStream stream, PDType0Font font, float pageWidth,
                                         float margin, float[] widths, float top) throws IOException {
        String[] headers = {"Mã GD", "Thời gian", "Loại GD", "TK liên quan", "Số tiền tăng/giảm",
                "Số dư sau GD", "Nội dung"};
        float rowHeight = 22;
        float x = margin;
        for (int i = 0; i < widths.length; i++) {
            stream.setNonStrokingColor(new Color(235, 238, 250));
            stream.addRect(x, top - rowHeight, widths[i], rowHeight);
            stream.fill();
            stream.setStrokingColor(new Color(150, 155, 170));
            stream.addRect(x, top - rowHeight, widths[i], rowHeight);
            stream.stroke();
            writeText(stream, font, 7.1f, Color.BLACK, x + 3, top - 14, headers[i]);
            x += widths[i];
        }
        return top - rowHeight;
    }

    private static PDPageContentStream newPage(PDDocument document, PDRectangle pageSize) throws IOException {
        PDPage page = new PDPage(pageSize);
        document.addPage(page);
        return new PDPageContentStream(document, page);
    }

    private static float yTop(PDRectangle pageSize, float margin) { return pageSize.getHeight() - margin; }

    private static void writeText(PDPageContentStream stream, PDType0Font font, float size,
                                  Color color, float x, float y, String text) throws IOException {
        stream.beginText();
        stream.setFont(font, size);
        stream.setNonStrokingColor(color);
        stream.newLineAtOffset(x, y);
        stream.showText(safe(text));
        stream.endText();
    }

    private static List<String> wrap(String value, PDType0Font font, float size, float maxWidth) throws IOException {
        List<String> lines = new ArrayList<>();
        StringBuilder line = new StringBuilder();
        for (String word : safe(value).split("\\s+")) {
            String candidate = line.isEmpty() ? word : line + " " + word;
            if (font.getStringWidth(candidate) * size / 1000f <= maxWidth) {
                line.setLength(0);
                line.append(candidate);
            } else {
                if (!line.isEmpty()) lines.add(line.toString());
                line.setLength(0);
                line.append(word);
            }
        }
        if (!line.isEmpty()) lines.add(line.toString());
        return lines.isEmpty() ? List.of("") : lines;
    }

    private static Path locateVietnameseFont() throws IOException {
        String windows = System.getenv("WINDIR");
        List<Path> candidates = new ArrayList<>();
        if (windows != null) {
            candidates.add(Path.of(windows, "Fonts", "arial.ttf"));
            candidates.add(Path.of(windows, "Fonts", "segoeui.ttf"));
        }
        candidates.add(Path.of("C:/Windows/Fonts/arial.ttf"));
        candidates.add(Path.of("/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf"));
        candidates.add(Path.of("/usr/share/fonts/truetype/noto/NotoSans-Regular.ttf"));
        return candidates.stream().filter(Files::isRegularFile).findFirst()
                .orElseThrow(() -> new IOException("Không tìm thấy font Unicode để xuất tiếng Việt ra PDF."));
    }

    private static void addTextRow(XSSFSheet sheet, int rowIndex, String label, String value) {
        Row row = sheet.createRow(rowIndex);
        row.createCell(0).setCellValue(label);
        row.createCell(1).setCellValue(safe(value));
    }

    private static String money(java.math.BigDecimal amount) {
        return amount == null ? "0 VNĐ" : MONEY.format(amount) + " VNĐ";
    }

    private static String signedMoney(java.math.BigDecimal amount) {
        if (amount == null) return "0 VNĐ";
        return (amount.signum() > 0 ? "+" : "") + MONEY.format(amount) + " VNĐ";
    }

    private static String safe(String value) { return value == null ? "" : value; }
}
