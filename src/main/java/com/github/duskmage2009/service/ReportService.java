package com.github.duskmage2009.service;

import com.github.duskmage2009.entity.Card;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public byte[] generateCardsExcelReport(List<Card> cards) throws IOException {
        log.debug("Generating Excel report for {} cards", cards.size());

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Cards Report");

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);

            createHeaderRow(sheet, headerStyle);

            int rowNum = 1;
            for (Card card : cards) {
                Row row = sheet.createRow(rowNum++);
                fillCardRow(row, card, dataStyle);
            }

            for (int i = 0; i < 10; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            log.info("Generated Excel report with {} rows", cards.size());

            return out.toByteArray();
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();

        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        font.setColor(IndexedColors.WHITE.getIndex());

        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);

        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();

        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);

        return style;
    }

    private void createHeaderRow(Sheet sheet, CellStyle headerStyle) {
        Row headerRow = sheet.createRow(0);

        String[] headers = {
                "ID",
                "Name",
                "Deck",
                "Provision",
                "Power",
                "Type",
                "Faction",
                "Description",
                "Created At",
                "Updated At"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        headerRow.setHeightInPoints(25);
    }

    private void fillCardRow(Row row, Card card, CellStyle dataStyle) {
        int colNum = 0;

        Cell cell = row.createCell(colNum++);
        cell.setCellValue(card.getId());
        cell.setCellStyle(dataStyle);

        cell = row.createCell(colNum++);
        cell.setCellValue(card.getName());
        cell.setCellStyle(dataStyle);

        cell = row.createCell(colNum++);
        cell.setCellValue(card.getDeck().getName());
        cell.setCellStyle(dataStyle);

        cell = row.createCell(colNum++);
        cell.setCellValue(card.getProvision());
        cell.setCellStyle(dataStyle);

        cell = row.createCell(colNum++);
        cell.setCellValue(card.getPower());
        cell.setCellStyle(dataStyle);

        cell = row.createCell(colNum++);
        cell.setCellValue(card.getType().getDisplayName());
        cell.setCellStyle(dataStyle);

        cell = row.createCell(colNum++);
        cell.setCellValue(card.getFaction().getDisplayName());
        cell.setCellStyle(dataStyle);

        cell = row.createCell(colNum++);
        cell.setCellValue(card.getDescription() != null ? card.getDescription() : "");
        cell.setCellStyle(dataStyle);

        cell = row.createCell(colNum++);
        cell.setCellValue(card.getCreatedAt().format(DATE_FORMATTER));
        cell.setCellStyle(dataStyle);

        cell = row.createCell(colNum++);
        cell.setCellValue(card.getUpdatedAt().format(DATE_FORMATTER));
        cell.setCellStyle(dataStyle);
    }
}