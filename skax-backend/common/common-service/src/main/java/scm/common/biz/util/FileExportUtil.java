package scm.common.biz.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Component
public class FileExportUtil {

    /**
     * 객체 리스트를 CSV Resource로 변환
     */
    public <T> Resource exportToCsv(List<T> data, Class<T> clazz) throws IOException {
        ByteArrayInputStream csvStream = generateCsv(data, clazz);
        return new InputStreamResource(csvStream);
    }

    /**
     * 객체 리스트를 Excel Resource로 변환
     */
    public <T> Resource exportToExcel(List<T> data, Class<T> clazz) throws IOException {
        ByteArrayInputStream excelStream = generateExcel(data, clazz);
        return new InputStreamResource(excelStream);
    }

    /**
     * 객체 리스트를 Excel Resource로 변환 (시트명 지정)
     */
    public <T> Resource exportToExcel(List<T> data, Class<T> clazz, String sheetName) throws IOException {
        ByteArrayInputStream excelStream = generateExcel(data, clazz, sheetName);
        return new InputStreamResource(excelStream);
    }

    /**
     * 파일 타입에 따라 자동으로 적절한 Export 메서드 호출
     */
    public <T> Resource exportToFile(List<T> data, Class<T> clazz, FileType fileType) throws IOException {
        return switch (fileType) {
            case CSV -> exportToCsv(data, clazz);
            case XLSX -> exportToExcel(data, clazz);
        };
    }

    /**
     * 객체 리스트를 CSV ByteArrayInputStream으로 변환
     */
    public <T> ByteArrayInputStream generateCsv(List<T> data, Class<T> clazz) throws IOException {
        Map<String, Field> fieldMap = buildOrderedFieldMap(clazz);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8)) {

            // UTF-8 BOM 추가 (Excel에서 한글 깨짐 방지)
            out.write(0xEF);
            out.write(0xBB);
            out.write(0xBF);

            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);

            // 헤더 작성
            List<String> headers = new ArrayList<>(fieldMap.keySet());
            csvPrinter.printRecord(headers);

            // 데이터 작성
            for (T item : data) {
                List<String> record = new ArrayList<>();
                for (Field field : fieldMap.values()) {
                    field.setAccessible(true);
                    try {
                        Object value = field.get(item);
                        record.add(formatValue(value, field));
                    } catch (IllegalAccessException e) {
                        record.add("");
                    }
                }
                csvPrinter.printRecord(record);
            }

            csvPrinter.flush();
            writer.flush();

            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    /**
     * 객체 리스트를 Excel ByteArrayInputStream으로 변환
     */
    public <T> ByteArrayInputStream generateExcel(List<T> data, Class<T> clazz) throws IOException {
        return generateExcel(data, clazz, "Sheet1");
    }

    /**
     * 객체 리스트를 Excel ByteArrayInputStream으로 변환 (시트명 지정)
     */
    public <T> ByteArrayInputStream generateExcel(List<T> data, Class<T> clazz, String sheetName) throws IOException {
        Map<String, Field> fieldMap = buildOrderedFieldMap(clazz);

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet(sheetName);

            // 헤더 스타일
            CellStyle headerStyle = createHeaderStyle(workbook);

            // 헤더 행 생성
            Row headerRow = sheet.createRow(0);
            List<String> headers = new ArrayList<>(fieldMap.keySet());
            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
                cell.setCellStyle(headerStyle);
            }

            // 데이터 행 생성
            int rowNum = 1;
            for (T item : data) {
                Row row = sheet.createRow(rowNum++);
                int colNum = 0;

                for (Field field : fieldMap.values()) {
                    field.setAccessible(true);
                    Cell cell = row.createCell(colNum++);

                    try {
                        Object value = field.get(item);
                        setCellValue(cell, value, field);
                    } catch (IllegalAccessException e) {
                        cell.setCellValue("");
                    }
                }
            }

            // 컬럼 너비 자동 조정
            for (int i = 0; i < headers.size(); i++) {
                sheet.autoSizeColumn(i);
                // 최대 너비 제한 (너무 넓어지는 것 방지)
                int currentWidth = sheet.getColumnWidth(i);
                if (currentWidth > 15000) {
                    sheet.setColumnWidth(i, 15000);
                }
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    /**
     * byte[] 형태로 Excel 데이터 반환 (기존 코드 호환용)
     */
    public <T> byte[] generateExcelBytes(List<T> data, Class<T> clazz) throws IOException {
        return generateExcel(data, clazz).readAllBytes();
    }

    /**
     * CsvColumn 어노테이션 순서대로 정렬된 필드 맵 생성
     */
    private <T> Map<String, Field> buildOrderedFieldMap(Class<T> clazz) {
        Map<String, Field> fieldMap = new LinkedHashMap<>();
        List<FieldOrder> fieldOrders = new ArrayList<>();

        for (Field field : clazz.getDeclaredFields()) {
            FileExportColumn annotation = field.getAnnotation(FileExportColumn.class);
            if (annotation != null) {
                field.setAccessible(true);
                fieldOrders.add(new FieldOrder(annotation.value(), field, annotation.order()));
            }
        }

        // order 값으로 정렬 (order가 -1이면 선언 순서)
        fieldOrders.sort(Comparator.comparingInt(FieldOrder::order));

        for (FieldOrder fo : fieldOrders) {
            fieldMap.put(fo.name(), fo.field());
        }

        return fieldMap;
    }

    /**
     * 필드 값을 문자열로 포맷팅
     */
    private String formatValue(Object value, Field field) {
        if (value == null) {
            return "";
        }

        FileExportColumn annotation = field.getAnnotation(FileExportColumn.class);

        if (value instanceof LocalDate) {
            String format = annotation != null ? annotation.dateFormat() : "yyyy-MM-dd";
            return ((LocalDate) value).format(DateTimeFormatter.ofPattern(format));
        } else if (value instanceof LocalDateTime) {
            String format = annotation != null ? annotation.dateFormat() : "yyyy-MM-dd HH:mm:ss";
            return ((LocalDateTime) value).format(DateTimeFormatter.ofPattern(format));
        } else if (value instanceof Date) {
            String format = annotation != null ? annotation.dateFormat() : "yyyy-MM-dd";
            return new SimpleDateFormat(format).format((Date) value);
        } else if (value instanceof List) {
            return String.join(", ", ((List<?>) value).stream()
                    .map(Object::toString)
                    .toArray(String[]::new));
        }

        return value.toString();
    }

    /**
     * Excel Cell에 값 설정
     */
    private void setCellValue(Cell cell, Object value, Field field) {
        if (value == null) {
            cell.setCellValue("");
            return;
        }

        FileExportColumn annotation = field.getAnnotation(FileExportColumn.class);

        if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof LocalDate) {
            String format = annotation != null ? annotation.dateFormat() : "yyyy-MM-dd";
            cell.setCellValue(((LocalDate) value).format(DateTimeFormatter.ofPattern(format)));
        } else if (value instanceof LocalDateTime) {
            String format = annotation != null ? annotation.dateFormat() : "yyyy-MM-dd HH:mm:ss";
            cell.setCellValue(((LocalDateTime) value).format(DateTimeFormatter.ofPattern(format)));
        } else if (value instanceof Date) {
            cell.setCellValue((Date) value);
        } else if (value instanceof List) {
            String joined = String.join(", ", ((List<?>) value).stream()
                    .map(Object::toString)
                    .toArray(String[]::new));
            cell.setCellValue(joined);
        } else {
            cell.setCellValue(value.toString());
        }
    }

    /**
     * Excel 헤더 스타일 생성
     */
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    /**
     * 파일 타입 Enum
     */
    public enum FileType {
        CSV, XLSX
    }

    /**
     * 필드 순서 관리를 위한 내부 클래스
     */
    private record FieldOrder(String name, Field field, int order) {
    }
}