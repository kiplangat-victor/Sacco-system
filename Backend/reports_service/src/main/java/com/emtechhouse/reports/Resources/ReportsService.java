package com.emtechhouse.reports.Resources;

import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;

@Service
public class ReportsService {

    private Connection dbConnection;
    @Value("${spring.datasource.url}")
    private String db;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;

    ReportsService(){

    }

    public String getQueryFromJRXML(String path) {
        try {
            File file = ResourceUtils.getFile(path);
//            System.out.println(file.getAbsolutePath());
            InputStream in = new FileInputStream(file);
            String fileContents = readFileToString(file.getAbsolutePath());


            int initial =  fileContents.indexOf("<queryString language=\"SQL\">") ;
            int start =  fileContents.indexOf("<![CDATA[", initial) + "<![CDATA[".length();
            int end =  fileContents.indexOf("]]>", initial);

            return fileContents.substring(start, end);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public String readFileToString(String path)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, Charset.defaultCharset());
    }

    public String chargeAccount(String customerCode) {
        try (Connection sourceConnection = DriverManager.getConnection(db, username, password)
        ) {
            this.dbConnection = sourceConnection;
            ResultSet resultSet = query(dbConnection, "SELECT acid FROM accounts WHERE customer_code ='"+customerCode+"' " +
                    "AND product_code LIKE 'G0%' AND (account_balance - book_balance) > 100 AND account_status = 'ACTIVE' ORDER BY product_code ASC LIMIT 1 ");
            while (resultSet.next()) {
                return resultSet.getString("acid");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return "";
    }

    public ResultSet query(Connection targetConnection, String sql) {
        try {
            Statement statement =  targetConnection.createStatement();
            return statement.executeQuery(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getQueryFromJRXML(String s, Map<String, Object> parameters) {
        String query = getQueryFromJRXML(s);

        Set<String> keys = parameters.keySet();
        for (String key: keys) {
            String keyre = "$"+"P{"+key+"}";
            System.out.println(keyre);
            while (query.contains(keyre)) {
                query = query.replace(keyre, "'"+parameters.get(key).toString()+"'");
            }
        }
        return query;
    }

    public void exportQueryResults(Connection targetConnection, String query, String filename, HttpServletResponse response)
    {
        System.out.println(query);
//        appendToExecutionFile(query);
        List<String[]> data = new ArrayList<String[]>();
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
                    data.add(values);

                }
                values = new String[columnsNumber];
                for (int i = 1; i <= columnsNumber; i++){
                    values[i-1] = formatString(resultSet.getString(i));
                }
                data.add(values);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        try {
            response.setContentType("text/csv");
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + filename + ".csv" + "\"");
            // create CSVWriter with '|' as separator
            StringWriter sw = new StringWriter();
            CSVWriter writer = new CSVWriter(sw, ',',
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);

            writer.writeAll(data);
            // closing writer connection
            writer.close();
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(sw.toString());
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void exportQueryResultsXLSX(Connection targetConnection, String query, String name, HttpServletResponse response)
    {

//        appendToExecutionFile(query);
        List<String[]> data = new ArrayList<String[]>();
        try {
            ExcelGenerator excelGenerator = new ExcelGenerator();
            excelGenerator.generateExcelFile(targetConnection, query, name, response);
            System.out.println(query);

        } catch ( IOException e) {
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
}

