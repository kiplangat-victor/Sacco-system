package com.emtechhouse.reports.Resources;

import com.emtechhouse.reports.Conversions.ChargeRequest;
import com.emtechhouse.reports.Responses.EntityResponse;
import com.emtechhouse.reports.Responses.ReportRequestRepo;
import com.emtechhouse.reports.Utils.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.reports.Utils.Utils.HttpInterceptor.UserRequestContext;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import okhttp3.RequestBody;
import okhttp3.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("api/v1/dynamic")
@Slf4j
@CrossOrigin
public class DynamicReportGenerator {
    @Value("${spring.datasource.url}")
    private String db;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;

    @Value("${sacco.reports.path}")
    private String path;

    @Value("${sacco.reports.authimage}")
    private String authimage;
    @Value("${sacco.reports.chargelink}")
    private String chargeLink;
    @Value("${sacco.reports.chargeevent}")
    private String chargeEvent;
    @Value("${sacco.reports.icon}")
    private String report_icon;

    @Value("${sacco.reports.sacco_name}")
    private String sacco_name;

    @Autowired
    private ReportRequestRepo reportRequestRepo;

    @Autowired
    private ReportsService reportsService;


    private Map<String, Object> setParameters(ReportRequest reportRequestObject) {
        Map<String, Object> parameters = new HashMap<>();

        parameters.put("report_icon", report_icon);
        parameters.put("sacco_name", sacco_name);
        parameters.put("customer_code", reportRequestObject.customerCode != null? reportRequestObject.customerCode.trim(): null);
        parameters.put("guarantors_code", reportRequestObject.customerCode != null? reportRequestObject.customerCode.trim(): null);
        parameters.put("welfare_code", reportRequestObject.welfareCode);
        parameters.put("product_code", reportRequestObject.productCode);
        parameters.put("gl_code", reportRequestObject.glCode);
        parameters.put("acid", reportRequestObject.acid != null? reportRequestObject.acid.trim(): null);
        parameters.put("gl_subhead", reportRequestObject.glSubheadCode);
        parameters.put("classification", reportRequestObject.classification);
        parameters.put("account_type", reportRequestObject.accountType);
        parameters.put("member_type", reportRequestObject.memberType);
        parameters.put("transactionType", reportRequestObject.transactionType);
        parameters.put("employer_code", reportRequestObject.employerCode);
        parameters.put("account_type_name", getSchemeName(reportRequestObject.accountType));
        parameters.put("sol_code", reportRequestObject.solCode);
        parameters.put("to_date", reportRequestObject.todate);
        parameters.put("from_date", reportRequestObject.fromdate);
        parameters.put("todaydate", reportRequestObject.todaydate);
        parameters.put("username", reportRequestObject.username);
        parameters.put("check_account", reportRequestObject.checkNumber);
        parameters.put("status", reportRequestObject.accountStatus);
        parameters.put("year", reportRequestObject.year);

        if (reportRequestObject.memberType != null && reportRequestObject.memberType.trim().length() > 0){
            parameters.put("member_type_title", reportRequestRepo.category(reportRequestObject.memberType).get());
        }
        return parameters;
    }

    @GetMapping("/load")
    public ResponseEntity<?> loadPdf(HttpServletRequest request, @RequestParam String reportRequest) {
        String  userName = request.getHeader("userName");

        System.out.println("---------Test-----");
        System.out.println(userName);

        ReportRequest reportRequestObject = new Gson().fromJson(reportRequest, ReportRequest.class);
//        System.out.println(reportRequest);
        System.out.println(reportRequestObject);


        try {
            //List<ReportsDatabaseConn> loansList = loansRepo.findAllLoans();
//            if (!loansList.isEmpty()){
            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compilereport = JasperCompileManager.compileReport(new FileInputStream(path + "/"+reportRequestObject.fileName));

            System.out.println(reportRequestObject);
            Map<String, Object> parameters = setParameters(reportRequestObject);

            System.out.println(parameters);

            if (reportRequestObject.charge == 'Y') {
                String chargeAccount = "";
                if (reportRequestObject.customerCode != null && reportRequestObject.customerCode.length() > 0) {
                    chargeAccount = reportsService.chargeAccount(reportRequestObject.customerCode);
                    if (chargeAccount.isEmpty())
                        return null;
                }else
                if (reportRequestObject.acid != null && reportRequestObject.acid.length() > 0) {
                    chargeAccount = reportRequestObject.acid;
                }else{
                    return null;
                }
                chargeStatement(userName, new ChargeRequest(1, "Statement Charges",
                        chargeAccount, chargeEvent));
            }

            JasperPrint report = JasperFillManager.fillReport(compilereport, parameters, connection);
            System.out.println(report.getPages().size());
            byte[] data = JasperExportManager.exportReportToPdf(report);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=report.pdf");


            return ResponseEntity.ok().headers(headers).contentType(org.springframework.http.MediaType.APPLICATION_PDF).body(data);
//            }else {
//                return new ResponseEntity<>(new ResponseMessage("No Data Found", 404), HttpStatus.NOT_FOUND);
//            }
        } catch (Exception exc) {
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }
    @GetMapping("/load/xlsx2")
    public void loadXlsx2(@RequestParam String reportRequest, HttpServletResponse response) {
        ReportRequest reportRequestObject = new Gson().fromJson(reportRequest, ReportRequest.class);
        Map<String, Object> parameters = setParameters(reportRequestObject);

        String filename = reportRequestObject.fileName;

        if(filename.contains(".")) {
            filename =  filename.substring(0, filename.lastIndexOf('.'));
        }
        try {
            String query = reportsService.getQueryFromJRXML(path + "/"+reportRequestObject.fileName, parameters);
            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            reportsService.exportQueryResultsXLSX(connection, query, filename, response);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @GetMapping("/load/csv")
    public void loadCSV(@RequestParam String reportRequest, HttpServletResponse response) {
        ReportRequest reportRequestObject = new Gson().fromJson(reportRequest, ReportRequest.class);
        Map<String, Object> parameters = setParameters(reportRequestObject);

        String filename = reportRequestObject.fileName;

        if(filename.contains(".")) {
            filename =  filename.substring(0, filename.lastIndexOf('.'));
        }
        try {
            String query = reportsService.getQueryFromJRXML(path + "/"+reportRequestObject.fileName, parameters);
            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            reportsService.exportQueryResults(connection, query, filename, response);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/load/xlsx")
    public void loadXlsx(@RequestParam String reportRequest, HttpServletResponse response) {
        ReportRequest reportRequestObject = new Gson().fromJson(reportRequest, ReportRequest.class);
        try {
//            List<ReportsDatabaseConn> loansList = loansRepo.findAllLoans();
//            if (!loansList.isEmpty()){
            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compilereport = JasperCompileManager.compileReport(new FileInputStream(path + "/"+reportRequestObject.fileName));

            Map<String, Object> parameters = setParameters(reportRequestObject);


            String filename = reportRequestObject.fileName;

            if(filename.contains(".")) {
                filename =  filename.substring(0, filename.lastIndexOf('.'));
            }

            System.out.println(parameters);

            JasperPrint report = JasperFillManager.fillReport(compilereport, parameters, connection);

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=\""+filename+".xlsx" + "\"");
            JRXlsxExporter exporterXls = new JRXlsxExporter();
            exporterXls.setParameter(JRExporterParameter.JASPER_PRINT, report);
            exporterXls.setParameter(JRExporterParameter.OUTPUT_STREAM, response.getOutputStream());
            exporterXls.exportReport();
        } catch (Exception exc) {
            System.out.println(exc.getLocalizedMessage());
        }
    }


    @GetMapping("/sacconame")
    public ResponseEntity<?> getSaccoName() {
        EntityResponse<String> response = new EntityResponse<>();
        response.setMessage(HttpStatus.OK.getReasonPhrase());
        response.setStatusCode(HttpStatus.FOUND.value());
        response.setEntity(sacco_name);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/saccologo")
    public ResponseEntity<byte[]> getSaccoLogo() {
        Path path = Paths.get(report_icon);
        String imageName = path.getFileName().toString();
        try
        {
            File file = new File(report_icon);
            FileInputStream fl = new FileInputStream(report_icon);
            byte[] arr = new byte[(int)file.length()];
            fl.read(arr);
            fl.close();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.parseMediaType("image/png"));
            headers.setContentDispositionFormData("inline", imageName);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            ResponseEntity<byte[]> response = new ResponseEntity<>(arr,
                    headers, HttpStatus.OK);
            return response;
        }
        catch (IOException e)
        {
            System.out.println(e.getLocalizedMessage());
        }
        return null;
    }
    @GetMapping("/authimage")
    public ResponseEntity<byte[]> getAuthImage() {
        Path path = Paths.get(authimage);
        String imageName = path.getFileName().toString();
        try
        {
            File file = new File(authimage);
            FileInputStream fl = new FileInputStream(authimage);
            byte[] arr = new byte[(int)file.length()];
            fl.read(arr);
            fl.close();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.parseMediaType("image/png"));
            headers.setContentDispositionFormData("inline", imageName);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            ResponseEntity<byte[]> response = new ResponseEntity<>(arr,
                    headers, HttpStatus.OK);
            return response;
        }
        catch (IOException e)
        {
            System.out.println(e.getLocalizedMessage());
        }
        return null;
    }

    @GetMapping("/customcss.json")
    public String getCustomCss() {
        try {
            File file = ResourceUtils.getFile("customcss.json");
//            System.out.println(file.getAbsolutePath());
            InputStream in = new FileInputStream(file);
            String strbasicactions = reportsService.readFileToString(file.getAbsolutePath());
            String response = "{" +
                    "\t\"statusCode\":200,\n" +
                    "\t\"entity\":" +strbasicactions+
                    "}";
            return response;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @GetMapping("/query")
    public String getQuery(String path) {
        return reportsService.getQueryFromJRXML(path);
    }


    private String getSchemeName(String accountType) {
        if (accountType.equalsIgnoreCase("SBA")) {
            return "Savings";
        }
        else if(accountType.equalsIgnoreCase("OAB")) {
            return "Office";
        }
        else if (accountType.equalsIgnoreCase("LAA")) {
            return  "Loan";
        }
        else if(accountType.equalsIgnoreCase("TDA")){
            return "Term Deposits";
        } else  if (accountType.equalsIgnoreCase("CAA")) {
            return "Current";
        }
        return accountType;
    }

    public boolean chargeStatement(String username, ChargeRequest chargeRequest){
        try {
            log.info("getting withdrawal fee info");

            EntityResponse entityResponse= new EntityResponse<>();
            String url= chargeLink;
            log.info("url:="+url);
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(300, TimeUnit.SECONDS)
                    .build();
            Gson g = new Gson();
            String chargeCollectionReqsSTR = g.toJson(chargeRequest);
            System.out.println("----------------------------------");
            System.out.println(chargeCollectionReqsSTR);
            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"), chargeCollectionReqsSTR);
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("userName", username)
                    .addHeader("entityId", "001")
                    .put(body)
                    .build();
            Response response = client.newCall(request).execute();
            String res = response.body().string();
            Boolean isJSONValid= isJSONValid(res);
            System.out.println(res);
            if(isJSONValid){
                System.out.println("Got charge result");
            }else {
                log.info("------------------------>INVALID JSON");
                entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                entityResponse.setMessage("FAILED TO FETCH CHARGES: ");

            }
            System.out.println("hhhh");
            System.out.println(entityResponse);
            return false;
        }catch (Exception e) {
            log.info("Caught Error {}"+e);

            EntityResponse response = new EntityResponse();
            response.setMessage(e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity(e.getCause());
            return false;
        }
    }

    public boolean isJSONValid(String json) {
        try {
            new JSONObject(json);
        } catch (JSONException e) {
            try {
                new JSONArray(json);
            } catch (JSONException ne) {
                return false;
            }
        }
        return true;
    }
}