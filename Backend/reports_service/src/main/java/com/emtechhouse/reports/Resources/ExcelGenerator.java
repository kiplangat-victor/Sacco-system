package com.emtechhouse.reports.Resources;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;


public class ExcelGenerator {
    int rowCount = 1;
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private String name;
    CellStyle commonStyle;

    public ExcelGenerator() {
        workbook = new XSSFWorkbook();
        commonStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        commonStyle.setFont(font);
    }

    public void exportQueryResults(Connection targetConnection, String query)
    {
        System.out.println(query);
//        appendToExecutionFile(query);
//        List<String[]> data = new ArrayList<String[]>();
        try {
            Statement targetStatement = targetConnection.createStatement();
            ResultSet resultSet = targetStatement.executeQuery(query);
            ResultSetMetaData rsmd = resultSet.getMetaData();

            int columnsNumber = rsmd.getColumnCount();
            System.out.println("Columns: "+columnsNumber);
            boolean first = true;
            while (resultSet.next()) {
                String[] values = new String[columnsNumber];
                if (first) {
                    first = false;
                    for (int i = 1; i <= columnsNumber; i++) {
                        values[i-1] = rsmd.getColumnName(i);
                    }
                    writeHeaders(values);
                }
                values = new String[columnsNumber];
                for (int i = 1; i <= columnsNumber; i++){
                    values[i-1] = formatString(resultSet.getString(i));
                }
                writeValues(values);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    String formatString(String value) {
        if (value == null)
            return "";
        if (value.contains("-") && value.contains(":") ) {
            int index = value.indexOf("-");
            if (value.indexOf("-", index) > 0) {
                index = value.indexOf(":");
                if (value.indexOf(":", index) > 0) {
                    String date = value.substring(0, 10);
//                    System.out.println(date);
                    return date;
                }
            }
        }
        return value;
    }

    private void writeHeaders(String[] values) {
        sheet = workbook.createSheet(name);
        Row row = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        int columnCount = 0;
        for (String string: values) {
            createCell(row, columnCount++, string, style);
        }
    }
    private void createCell(Row row, int columnCount, Object valueOfCell, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (valueOfCell instanceof Integer) {
            cell.setCellValue((Integer) valueOfCell);
        } else if (valueOfCell instanceof Long) {
            cell.setCellValue((Long) valueOfCell);
        } else if (valueOfCell instanceof String) {
            cell.setCellValue((String) valueOfCell);
        } else {
            cell.setCellValue((Boolean) valueOfCell);
        }
        cell.setCellStyle(style);
    }
    private void writeValues(String[] values) {
        System.out.println("Row "+rowCount);
        Row row = sheet.createRow(rowCount++);
        int columnCount = 0;
        for (String string: values) {
            createCell(row, columnCount++, string, commonStyle);
        }
    }

    public void generateExcelFile(Connection targetConnection, String query, String name, HttpServletResponse response) throws IOException {
        this.name = name;
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\""+name+".xlsx" + "\"");
        exportQueryResults(targetConnection, query);
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }
}



