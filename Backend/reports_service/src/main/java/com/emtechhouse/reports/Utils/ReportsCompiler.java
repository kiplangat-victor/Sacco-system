package com.emtechhouse.reports.Utils;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

@Component
@Slf4j
public class ReportsCompiler {
    @Autowired
    private DatabaseConnection databaseConnection;

    @Value("${spring.datasource.url}")
    private String db;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;

    private byte[] data;

    public ReportsCompiler() {
    }

    public byte[] compilePDFReport(String jrxmlPath, Map<String, Object> parameters, String pdfName, String type) throws SQLException, JRException {
        System.out.println(this.db);
        System.out.println(this.username);
        System.out.println(this.password);

        Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
        //Connection connection = DriverManager.getConnection("jdbc:mariadb://3.13.214.62:3306/fortunecredit", "emtech", "emtech");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlPath);
        System.out.println("all available");
        System.out.println(connection);

        //System.out.println(databaseConnection.newConnection());
        JasperPrint printReport = JasperFillManager.fillReport(jasperReport, parameters, connection);

        System.out.println(jrxmlPath);
        System.out.println(parameters);
        System.out.println(pdfName);
        System.out.println(CONSTANTS.PDF);
        System.out.println("all available");

//        databaseConnection.closeConnection();
        HttpHeaders headers = new HttpHeaders();
        if (type.equalsIgnoreCase("xlsx")) {
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()
            ) {
                JRXlsxExporter exporter = new JRXlsxExporter();
                exporter.setExporterInput(new SimpleExporterInput(printReport));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(byteArrayOutputStream));
                SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
                configuration.setOnePagePerSheet(true);
                configuration.setIgnoreGraphics(false);
                exporter.setConfiguration(configuration);
                exporter.exportReport();
                //headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename="+fileName[1]+".xlsx");
                this.data = byteArrayOutputStream.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        } else {
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + pdfName);
            this.data = JasperExportManager.exportReportToPdf(printReport);
            return data;
        }
    }
}
