package com.emtechhouse.reports.Resources;


import com.emtechhouse.reports.Responses.EntityResponse;
import com.itextpdf.text.pdf.PdfReader;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.swing.text.html.parser.Entity;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.text.DecimalFormat;
import java.util.*;

@RestController
@RequestMapping("api/v1/reports")
@Api(tags = "Basic Reports API")
@Slf4j
@CrossOrigin
public class GenerateBasicReportsResource {

    @Value("${spring.datasource.url}")
    private String db;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;

    @Value("${sacco.reports.path}")
    private String path;

    @Value("${sacco.reports.icon}")
    private String report_icon;

    @Value("${sacco.reports.sacco_name}")
    private String sacco_name;

    @GetMapping("/loan-portfolio/all")
    public ResponseEntity<?> getloanportfolios(@RequestParam String fromdate, @RequestParam String todate) {

        try {

//            List<ReportsDatabaseConn> loansList = loansRepo.findAllLoans();
//            if (!loansList.isEmpty()){
                Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
                JasperReport compilereport = JasperCompileManager.compileReport(new FileInputStream(path + "/loan_portfolio.jrxml"));

                Map<String, Object> parameters = new HashMap<String, Object>();

                parameters.put("from_date", fromdate);
                parameters.put("to_date", todate);
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);


                JasperPrint report = JasperFillManager.fillReport(compilereport, parameters, connection);
                byte[] data = JasperExportManager.exportReportToPdf(report);
                HttpHeaders headers = new HttpHeaders();
                headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=loanportfolio.pdf");

                return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);
//            }else {
//                return new ResponseEntity<>(new ResponseMessage("No Data Found", 404), HttpStatus.NOT_FOUND);
//            }

        } catch (Exception exc) {
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }

    @GetMapping("/loan-portfolio/branch")
    public ResponseEntity<?> getbranchloanportfolios(@RequestParam String fromdate, @RequestParam String todate, @RequestParam String branchCode) {

        try {
//
//            List<ReportsDatabaseConn> loansList = loansRepo.findAllBranchLoans(branchCode);
//            if (!loansList.isEmpty()){
                Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
                JasperReport compilereport = JasperCompileManager.compileReport(new FileInputStream(path + "/loan_portfolio_branch.jrxml"));

                Map<String, Object> parameters = new HashMap<String, Object>();

                parameters.put("from_date", fromdate);
                parameters.put("to_date", todate);
                parameters.put("sol_code", branchCode);
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);


                JasperPrint report = JasperFillManager.fillReport(compilereport, parameters, connection);
                byte[] data = JasperExportManager.exportReportToPdf(report);
                HttpHeaders headers = new HttpHeaders();
                headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=branch-loanportfolio.pdf");

                return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);
//            }else {
//                return new ResponseEntity<>(new ResponseMessage("No Data Found", 404), HttpStatus.NOT_FOUND);
//            }

        } catch (Exception exc) {
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }

    @GetMapping("/loan-portfolio/user")
    public ResponseEntity<?> getuserloanportfolios(@RequestParam String fromdate, @RequestParam String todate, @RequestParam String username) {

        try {

//            List<ReportsDatabaseConn> loansList = loansRepo.findAllManagerLoans(username);
//            if (!loansList.isEmpty()){
                Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
                JasperReport compilereport = JasperCompileManager.compileReport(new FileInputStream(path + "/loan_portfolio_user.jrxml"));

                Map<String, Object> parameters = new HashMap<String, Object>();

                parameters.put("from_date", fromdate);
                parameters.put("to_date", todate);
                parameters.put("posted_by", username);
                parameters.put("sacco_name", sacco_name);
                parameters.put("report_icon", report_icon);


                JasperPrint report = JasperFillManager.fillReport(compilereport, parameters, connection);
                byte[] data = JasperExportManager.exportReportToPdf(report);
                HttpHeaders headers = new HttpHeaders();
                headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=user-loanportfolio.pdf");

                return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);
//            }else {
//                return new ResponseEntity<>(new ResponseMessage("No Data Found", 404), HttpStatus.NOT_FOUND);
//            }

        } catch (Exception exc) {
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }


    @GetMapping("/account-statement")
    public ResponseEntity<?> generateAccountStatement(@RequestParam String acid, @RequestParam String fromdate, @RequestParam String todate) {
        try {

//            List<ReportsDatabaseConn> accountsList = reportsRepo.findAllCustomerAccounts(acid);
//            if (!accountsList.isEmpty()){
                Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
                JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(path + "/account_statement.jrxml"));

                Map<String, Object> parameters = new HashMap<>();
                parameters.put("to_date", todate);
                parameters.put("from_date", fromdate);
                parameters.put("acid", acid);
                parameters.put("sacco_name", sacco_name);
                parameters.put("report_icon", report_icon);


                JasperPrint report = JasperFillManager.fillReport(compileReport, parameters, connection);
                byte[] data = JasperExportManager.exportReportToPdf(report);


                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=account-statement-report.pdf");

                return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);
//            }else {
//                return new ResponseEntity<>(new ResponseMessage("No Data Found", 404), HttpStatus.NOT_FOUND);
//            }

        } catch (Exception exc) {
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }

    @GetMapping("/customer/account-statement")
    public ResponseEntity<?> getCustomerAccountStatement(@RequestParam String acid, @RequestParam String fromdate, @RequestParam String todate) {
        try {

//            List<ReportsDatabaseConn> accountsList = reportsRepo.findAllCustomerAccounts(acid);
//            if (!accountsList.isEmpty()){
            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(path + "/trial_account_statement_1.jrxml"));

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("to_date", todate);
            parameters.put("from_date", fromdate);
            parameters.put("acid", acid);
            parameters.put("sacco_name", sacco_name);
            parameters.put("report_icon", report_icon);


            JasperPrint report = JasperFillManager.fillReport(compileReport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);


            EntityResponse res = new EntityResponse<>();
            res.setMessage(HttpStatus.FOUND.getReasonPhrase());
            res.setEntity(data);
            res.setStatusCode(HttpStatus.FOUND.value());

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=account-statement-report.pdf");

            System.out.println(String.valueOf(res.getEntity()));
//            return ResponseEntity.ok().body(res);
            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(res.getEntity());
//            }else {
//                return new ResponseEntity<>(new ResponseMessage("No Data Found", 404), HttpStatus.NOT_FOUND);
//            }

        } catch (Exception exc) {
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }

    @GetMapping("/account-statement/type")
    public ResponseEntity<?> generateAccountStatementType(@RequestParam String acid, @RequestParam(required = false) String accountType, @RequestParam String fromdate, @RequestParam String todate) {
        try {

//            List<ReportsDatabaseConn> accountsList = reportsRepo.findAllCustomerTypeAccounts(acid, accountType);
//            if (!accountsList.isEmpty()){
                Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
                JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(path + "/account_statement.jrxml"));

                Map<String, Object> parameters = new HashMap<>();
                parameters.put("to_date", todate);
                parameters.put("from_date", fromdate);
                parameters.put("account_type", accountType);
                parameters.put("acid", acid);
                parameters.put("sacco_name", sacco_name);
                parameters.put("report_icon", report_icon);


                JasperPrint report = JasperFillManager.fillReport(compileReport, parameters, connection);
                byte[] data = JasperExportManager.exportReportToPdf(report);

                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=account-statement-report.pdf");

                return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);
//            }else {
//                return new ResponseEntity<>(new ResponseMessage("No Data Found", 404), HttpStatus.NOT_FOUND);
//            }


        } catch (Exception exc) {
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }


    @GetMapping("retail-customers/all")
    public ResponseEntity<?> getallretailcustomers(@RequestParam String fromdate, @RequestParam String todate){
        try {

//            List<ReportsDatabaseConn> retailCustomerList = retailCustomerRepo.findAllCustomers();
//            if (!retailCustomerList.isEmpty()){
                Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
                JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(path + "/retail_customer_all.jrxml"));

                Map<String, Object> parameters = new HashMap<>();
                parameters.put("to_date", todate);
                parameters.put("from_date", fromdate);
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);

                JasperPrint report = JasperFillManager.fillReport(compileReport, parameters, connection);
                byte[] data = JasperExportManager.exportReportToPdf(report);

                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=retail-customers-list");

                return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);
//            }else {
//                return new ResponseEntity<>(new ResponseMessage("No Data Found", 404), HttpStatus.NOT_FOUND);
//            }

        }catch (Exception exc){
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }

    @GetMapping("members/all")
    public ResponseEntity<?> getallMembers(@RequestParam String fromdate, @RequestParam String todate){
        try {

//            List<ReportsDatabaseConn> retailCustomerList = retailCustomerRepo.findAllCustomers();
//            if (!retailCustomerList.isEmpty()){
            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(path + "/all_members.jrxml"));

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("status", todate);
            parameters.put("to_date", todate);
            parameters.put("from_date", fromdate);
            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);

            JasperPrint report = JasperFillManager.fillReport(compileReport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=all-members-list");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);
//            }else {
//                return new ResponseEntity<>(new ResponseMessage("No Data Found", 404), HttpStatus.NOT_FOUND);
//            }

        }catch (Exception exc){
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }

//    @GetMapping("members/all")
//    public ResponseEntity<?> getAllMembers(@RequestParam String fromdate, @RequestParam String todate){
//        try {
//
////            List<ReportsDatabaseConn> retailCustomerList = retailCustomerRepo.findAllCustomers();
////            if (!retailCustomerList.isEmpty()){
//            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
//            JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(path + "/all_members.jrxml"));
//
//            Map<String, Object> parameters = new HashMap<>();
//            parameters.put("status", todate);
//            parameters.put("to_date", todate);
//            parameters.put("from_date", fromdate);
//            parameters.put("report_icon", report_icon);
//            parameters.put("sacco_name", sacco_name);
//
//            JasperPrint report = JasperFillManager.fillReport(compileReport, parameters, connection);
//            byte[] data = JasperExportManager.exportReportToPdf(report);
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=all-members-list");
//
//            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);
////            }else {
////                return new ResponseEntity<>(new ResponseMessage("No Data Found", 404), HttpStatus.NOT_FOUND);
////            }
//
//        }catch (Exception exc){
//            System.out.println(exc.getLocalizedMessage());
//            return null;
//        }
//    }

    @GetMapping("members/all/corporate")
    public ResponseEntity<?> getallCorporateMembers(@RequestParam String fromdate, @RequestParam String todate) {
        try {

//            List<ReportsDatabaseConn> retailCustomerList = retailCustomerRepo.findAllCustomers();
//            if (!retailCustomerList.isEmpty()){
            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(path + "/corporate_members.jrxml"));

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("to_date", todate);
            parameters.put("from_date", fromdate);
            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);

            JasperPrint report = JasperFillManager.fillReport(compileReport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=all-corporate-members-list");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);
//            }else {
//                return new ResponseEntity<>(new ResponseMessage("No Data Found", 404), HttpStatus.NOT_FOUND);
//            }

        }catch (Exception exc){
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }

    @GetMapping("members/all/groups")
    public ResponseEntity<?> getallGroupMembers(@RequestParam String fromdate, @RequestParam String todate) {
        try {

//            List<ReportsDatabaseConn> retailCustomerList = retailCustomerRepo.findAllCustomers();
//            if (!retailCustomerList.isEmpty()){
            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(path + "/houses_list.jrxml"));

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("to_date", todate);
            parameters.put("from_date", fromdate);
            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);

            JasperPrint report = JasperFillManager.fillReport(compileReport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=all-group-members-list");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);
//            }else {
//                return new ResponseEntity<>(new ResponseMessage("No Data Found", 404), HttpStatus.NOT_FOUND);
//            }

        }catch (Exception exc){
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }


    @GetMapping("members/all/in/group")
    public ResponseEntity<?> getallMembersByGroup(@RequestParam String groupCode){
        try {


            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(path + "/all_members_in_group.jrxml"));

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);
            parameters.put("group_code", groupCode);

            JasperPrint report = JasperFillManager.fillReport(compileReport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=all-group-members-list");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

        }catch (Exception exc){
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }

    @GetMapping("retail-customers/branch")
    public ResponseEntity<?> getbranchretailcustomers(@RequestParam String fromdate, @RequestParam String todate, @RequestParam String branchCode){
        try {

                Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
                JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(path + "/retail_customer_branch.jrxml"));

                Map<String, Object> parameters = new HashMap<>();
                parameters.put("to_date", todate);
                parameters.put("from_date", fromdate);
                parameters.put("sol_code", branchCode);
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);

                JasperPrint report = JasperFillManager.fillReport(compileReport, parameters, connection);
                byte[] data = JasperExportManager.exportReportToPdf(report);

                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=branch-retail-customers-list");

                return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);


        }catch (Exception exc){
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }

    @GetMapping("retail-customers/user")
    public ResponseEntity<?> getusersretailcustomers(@RequestParam String fromdate, @RequestParam String todate, @RequestParam String username){
        try {
                Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
                JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(path + "/retail_customer_user.jrxml"));

                Map<String, Object> parameters = new HashMap<>();
                parameters.put("to_date", todate);
                parameters.put("from_date", fromdate);
                parameters.put("posted_by", username);
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);

                JasperPrint report = JasperFillManager.fillReport(compileReport, parameters, connection);
                byte[] data = JasperExportManager.exportReportToPdf(report);

                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=users-retail-customers-list");

                return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);
        }catch (Exception exc){
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }


    @GetMapping("/disbursements/all")
    public ResponseEntity<?> getAllDisbursements(@RequestParam String fromdate, @RequestParam String todate){
        try {

                Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
                JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(path + "/disbursements_all.jrxml"));

                Map<String, Object> parameters = new HashMap<>();
                parameters.put("to_date", todate);
                parameters.put("from_date", fromdate);
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);


                JasperPrint report = JasperFillManager.fillReport(compileReport, parameters, connection);
                byte[] data = JasperExportManager.exportReportToPdf(report);

                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=all-disbursements-list");

                return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);


        }catch (Exception exc){
            System.out.println(exc.getLocalizedMessage());
            return null;
        }

    }

    @GetMapping("/disbursements/branch")
    public ResponseEntity<?> getBranchDisbursements(@RequestParam String fromdate, @RequestParam String todate, @RequestParam String branchCode){
        try {

                Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
                JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(path + "/disbursements_branch.jrxml"));

                Map<String, Object> parameters = new HashMap<>();
                parameters.put("to_date", todate);
                parameters.put("from_date", fromdate);
                parameters.put("sol_code", branchCode);
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);


                JasperPrint report = JasperFillManager.fillReport(compileReport, parameters, connection);
                byte[] data = JasperExportManager.exportReportToPdf(report);

                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=branch-disbursements-list");

                return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);


        }catch (Exception exc){
            System.out.println(exc.getLocalizedMessage());
            return null;
        }

    }

    @GetMapping("/disbursements/user")
    public ResponseEntity<?> getUserDisbursements(@RequestParam String fromdate, @RequestParam String todate, @RequestParam String disbursedBy){
        try {


                Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
                JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(path + "/disbursements_user.jrxml"));

                Map<String, Object> parameters = new HashMap<>();
                parameters.put("to_date", todate);
                parameters.put("from_date", fromdate);
                parameters.put("account_manager", disbursedBy);
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);


                JasperPrint report = JasperFillManager.fillReport(compileReport, parameters, connection);
                byte[] data = JasperExportManager.exportReportToPdf(report);

                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=manager-disbursements-list");

                return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

        }catch (Exception exc){
            System.out.println(exc.getLocalizedMessage());
            return null;
        }

    }

    @GetMapping("/arrears/all")
    public ResponseEntity<?> getAllArrears(@RequestParam String fromdate, @RequestParam String todate){
        try {
            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(path + "/arrears_all.jrxml"));

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("to_date", todate);
            parameters.put("from_date", fromdate);
            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);


            JasperPrint report = JasperFillManager.fillReport(compileReport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=arrears-list");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

        }catch (Exception exc){
            System.out.println(exc.getLocalizedMessage());
            return null;
        }

    }

    @GetMapping("/arrears/branch")
    public ResponseEntity<?> getBranchArrears(@RequestParam String fromdate, @RequestParam String todate, @RequestParam String branchCode){
        try {
            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(path + "/arrears_branch.jrxml"));

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("to_date", todate);
            parameters.put("from_date", fromdate);
            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);
            parameters.put("sol_code", branchCode);


            JasperPrint report = JasperFillManager.fillReport(compileReport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=branch-arrears-list");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

        }catch (Exception exc){
            System.out.println(exc.getLocalizedMessage());
            return null;
        }

    }

    @GetMapping("/arrears/user")
    public ResponseEntity<?> getManagerArrears(@RequestParam String fromdate, @RequestParam String todate, @RequestParam String username){
        try {
            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(path + "/arrears_user.jrxml"));

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("to_date", todate);
            parameters.put("from_date", fromdate);
            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);
            parameters.put("account_manager", username);


            JasperPrint report = JasperFillManager.fillReport(compileReport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=manager-arrears-list");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

        }catch (Exception exc){
            System.out.println(exc.getLocalizedMessage());
            return null;
        }

    }


    @GetMapping("/repayments/all")
    public ResponseEntity<?> getAllRepayments(@RequestParam String fromdate, @RequestParam String todate){
        try {
            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(path + "/repayments_all.jrxml"));

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("to_date", todate);
            parameters.put("from_date", fromdate);
            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);


            JasperPrint report = JasperFillManager.fillReport(compileReport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=repayments-list");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

        }catch (Exception exc){
            System.out.println(exc.getLocalizedMessage());
            return null;
        }

    }

    @GetMapping("/repayments/branch")
    public ResponseEntity<?> getBranchRepayments(@RequestParam String fromdate, @RequestParam String todate, @RequestParam String branchCode){
        try {
            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(path + "/repayments_branch.jrxml"));

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("to_date", todate);
            parameters.put("from_date", fromdate);
            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);
            parameters.put("sol_code", branchCode);


            JasperPrint report = JasperFillManager.fillReport(compileReport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=branch-repayments-list");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

        }catch (Exception exc){
            System.out.println(exc.getLocalizedMessage());
            return null;
        }

    }

    @GetMapping("/repayments/user")
    public ResponseEntity<?> getManagerRepayments(@RequestParam String fromdate, @RequestParam String todate, @RequestParam String username){
        try {
            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(path + "/repayments_user.jrxml"));

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("to_date", todate);
            parameters.put("from_date", fromdate);
            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);
            parameters.put("account_manager", username);


            JasperPrint report = JasperFillManager.fillReport(compileReport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=manager-repayments-list");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

        }catch (Exception exc){
            System.out.println(exc.getLocalizedMessage());
            return null;
        }

    }


    @GetMapping("/transactions/all")
    public ResponseEntity<?> getAllTransactions(@RequestParam String fromdate, @RequestParam String todate) {
        try {
            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(path + "/transactions_all.jrxml"));

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("to_date", todate);
            parameters.put("from_date", fromdate);
            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);


            JasperPrint report = JasperFillManager.fillReport(compileReport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=transactions-list");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

        }catch (Exception exc){
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }


    @GetMapping("/transactions/analyze-allcustomer-accounts")
    public ResponseEntity<?> analyzeAllAccounts(@RequestParam String fromdate, @RequestParam String todate) {
        try {
            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(path + "/sum_transactions_all.jrxml"));

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("to_date", todate);
            parameters.put("from_date", fromdate);
            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);


            JasperPrint report = JasperFillManager.fillReport(compileReport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=transactions-list");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

        } catch (Exception exc) {
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }
    @GetMapping("/transactions/analyze-accounts-by-scheme")
    public ResponseEntity<?> analyzeAllAccountsByScheme(@RequestParam String fromdate, @RequestParam String todate, @RequestParam String accountType) {
        try {
            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(path + "/sum_transactions_by_scheme.jrxml"));

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("to_date", todate);
            parameters.put("from_date", fromdate);
            parameters.put("report_icon", report_icon);
            parameters.put("account_type", accountType);
            parameters.put("account_type_name", getSchemeName(accountType));
            parameters.put("sacco_name", sacco_name);


            JasperPrint report = JasperFillManager.fillReport(compileReport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=transactions-list");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

        } catch (Exception exc) {
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }


    @GetMapping("/transactions/per-product")
    public ResponseEntity<?> getAllTransactions(@RequestParam String fromdate, @RequestParam String todate,  @RequestParam String product_code) {
        try {
            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(path + "/sum_transactions_by_product.jrxml"));

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("to_date", todate);
            parameters.put("from_date", fromdate);
            parameters.put("product_code", product_code);
            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);


            JasperPrint report = JasperFillManager.fillReport(compileReport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=transactions-list");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

        } catch (Exception exc) {
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }


    @GetMapping("/transactions/per-customer")
    public ResponseEntity<?> getCustomerTransactions(@RequestParam String fromdate, @RequestParam String todate,
                                                     @RequestParam String customerCode) {
        try {
            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(path + "/sum_customer_transactions_all.jrxml"));

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("to_date", todate);
            parameters.put("from_date", fromdate);
            parameters.put("customer_code", customerCode);
            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);

            JasperPrint report = JasperFillManager.fillReport(compileReport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=transactions-list");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

        } catch (Exception exc) {
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }



    @GetMapping("/transactions/user")
    public ResponseEntity<?> getUserTransactions(@RequestParam String fromdate, @RequestParam String todate, @RequestParam String user) {
        try {
            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(path + "/transactions_user.jrxml"));

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("to_date", todate);
            parameters.put("from_date", fromdate);
            parameters.put("posted_by", user);
            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);


            JasperPrint report = JasperFillManager.fillReport(compileReport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=transactions-list");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

        }catch (Exception exc){
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }

    @GetMapping("/transactions/branch")
    public ResponseEntity<?> getBranchTransactions(@RequestParam String fromdate, @RequestParam String todate, @RequestParam String branchCode){
        try {
            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(path + "/transactions_branch.jrxml"));

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("to_date", todate);
            parameters.put("from_date", fromdate);
            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);
            parameters.put("branch_code", branchCode);


            JasperPrint report = JasperFillManager.fillReport(compileReport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=branch-transactions-list");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

        }catch (Exception exc){
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }

    @GetMapping("/accounts/all")
    public ResponseEntity<?> getAllAccounts(@RequestParam String fromdate, @RequestParam String todate, @RequestParam String accountType) {
        try {
            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(path + "/all_accounts.jrxml"));

            Map<String, Object> parameters = new HashMap<>();
            if (accountType.equalsIgnoreCase("SBA")){
                parameters.put("to_date", todate);
                parameters.put("from_date", fromdate);
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", "Savings");
            }
            else if(accountType.equalsIgnoreCase("OAB")) {
                parameters.put("to_date", todate);
                parameters.put("from_date", fromdate);
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", "Office");
            }
            else if (accountType.equalsIgnoreCase("LAA")) {
                parameters.put("to_date", todate);
                parameters.put("from_date", fromdate);
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", "Loan");
            }
            else if(accountType.equalsIgnoreCase("TDA")){
                parameters.put("to_date", todate);
                parameters.put("from_date", fromdate);
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", "Term Deposits");
            }else  if (accountType.equalsIgnoreCase("CAA")) {
                parameters.put("to_date", todate);
                parameters.put("from_date", fromdate);
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", "Current");
            }
            else {
                parameters.put("to_date", todate);
                parameters.put("from_date", fromdate);
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", accountType);
            }


            JasperPrint report = JasperFillManager.fillReport(compileReport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename="+accountType+"-accounts-list");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

        }catch (Exception exc){
            System.out.println(exc.getLocalizedMessage());
            return null;
        }

    }


    @GetMapping("/accounts/all/by/member")
    public ResponseEntity<?> getMyAllAccounts( @RequestParam String customerCode) {
        try {
            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(path + "/my_all_accounts.jrxml"));

            Map<String, Object> parameters = new HashMap<>();

            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);
            parameters.put("customer_code", customerCode);


            JasperPrint report = JasperFillManager.fillReport(compileReport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename="+customerCode+"-accounts-list");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

        }catch (Exception exc){
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }

    @GetMapping("/accounts/by/product")
    public ResponseEntity<?> getAllAccountsByProduct(@RequestParam String fromdate, @RequestParam String todate, @RequestParam String productCode,  @RequestParam String solCode) {
        try {
            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(path + "/accounts_by_product_code.jrxml"));

            Map<String, Object> parameters = new HashMap<>();

            parameters.put("to_date", todate);
            parameters.put("from_date", fromdate);
            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);
            parameters.put("product_code", productCode);
            parameters.put("sol_code", solCode);


            JasperPrint report = JasperFillManager.fillReport(compileReport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename="+productCode+"-accounts-list");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

        }catch (Exception exc){
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }



    @GetMapping("/accounts/by/product-and-group")
    public ResponseEntity<?> getAllAccountsByProductAndGroup(@RequestParam String productCode,  @RequestParam String groupCode) {
        try {
            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(path + "/accounts_by_product_code_and_group_code.jrxml"));

            Map<String, Object> parameters = new HashMap<>();

            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);
            parameters.put("product_code", productCode);
            parameters.put("group_code", groupCode);


            JasperPrint report = JasperFillManager.fillReport(compileReport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=-"+productCode+"-"+groupCode+"-accounts-list");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

        } catch (Exception exc) {
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }


    @GetMapping("/accounts/retail/all")
    public ResponseEntity<?> getAllRetailAccountsByAccountType(@RequestParam String fromdate, @RequestParam String todate, @RequestParam String accountType){
        try {
            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(path + "/retail_accounts_all.jrxml"));

            Map<String, Object> parameters = new HashMap<>();
            if (accountType.equalsIgnoreCase("SBA")){
                parameters.put("to_date", todate);
                parameters.put("from_date", fromdate);
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", "Savings");
            }
            else if(accountType.equalsIgnoreCase("OAB")){
                parameters.put("to_date", todate);
                parameters.put("from_date", fromdate);
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", "Office");
            }
            else if (accountType.equalsIgnoreCase("LAA")){
                parameters.put("to_date", todate);
                parameters.put("from_date", fromdate);
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", "Loan");
            }
            else if(accountType.equalsIgnoreCase("TDA")){
                parameters.put("to_date", todate);
                parameters.put("from_date", fromdate);
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", "Term Deposits");
            }else  if (accountType.equalsIgnoreCase("CAA")) {
                parameters.put("to_date", todate);
                parameters.put("from_date", fromdate);
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", "Current");
            }
            else {
                parameters.put("to_date", todate);
                parameters.put("from_date", fromdate);
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", accountType);
            }


            JasperPrint report = JasperFillManager.fillReport(compileReport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=retail-"+accountType+"-accounts-list");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

        }catch (Exception exc){
            System.out.println(exc.getLocalizedMessage());
            return null;
        }

    }

    @GetMapping("/accounts/retail/branch")
    public ResponseEntity<?> getAllBranchRetailAccountsByAccountType(@RequestParam String fromdate, @RequestParam String todate, @RequestParam String branchCode, @RequestParam String accountType){
        try {
            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(path + "/retail_accounts_branch.jrxml"));

            Map<String, Object> parameters = new HashMap<>();
            if (accountType.equalsIgnoreCase("SBA")) {
                parameters.put("to_date", todate);
                parameters.put("from_date", fromdate);
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("sol_code", branchCode);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", "Savings");
            } else if (accountType.equalsIgnoreCase("OAB")){
                parameters.put("to_date", todate);
                parameters.put("from_date", fromdate);
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("sol_code", branchCode);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", "Office");
            }else if (accountType.equalsIgnoreCase("LAA")){
                parameters.put("to_date", todate);
                parameters.put("from_date", fromdate);
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("sol_code", branchCode);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", "Loan");
            }else if (accountType.equalsIgnoreCase("TDA")){
                parameters.put("to_date", todate);
                parameters.put("from_date", fromdate);
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("sol_code", branchCode);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", "Term Deposit");
                parameters.put("sol_code", branchCode);
            }else  if (accountType.equalsIgnoreCase("CAA")) {
                parameters.put("to_date", todate);
                parameters.put("from_date", fromdate);
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("sol_code", branchCode);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", "Current");
                parameters.put("sol_code", branchCode);
            }else {
                parameters.put("to_date", todate);
                parameters.put("from_date", fromdate);
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("sol_code", branchCode);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", accountType);
            }


            JasperPrint report = JasperFillManager.fillReport(compileReport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=branch-retail-"+accountType+"-accounts-list");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

        }catch (Exception exc){
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }

    @GetMapping("/accounts/corporate/all")
    public ResponseEntity<?> getAllCorporateAccountsByAccountType(@RequestParam String fromdate, @RequestParam String todate, @RequestParam String accountType){
        try {
            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(path + "/corporate_accounts_all.jrxml"));

            Map<String, Object> parameters = new HashMap<>();
            if (accountType.equalsIgnoreCase("SBA")) {
                parameters.put("to_date", todate);
                parameters.put("from_date", fromdate);
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", "Savings");
            }else  if (accountType.equalsIgnoreCase("OAB")) {
                parameters.put("to_date", todate);
                parameters.put("from_date", fromdate);
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", "Office");
            }else  if (accountType.equalsIgnoreCase("LAA")) {
                parameters.put("to_date", todate);
                parameters.put("from_date", fromdate);
                parameters.put("sacco_name", sacco_name);
                parameters.put("report_icon", report_icon);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", "Loan");
            }else  if (accountType.equalsIgnoreCase("TDA")) {
                parameters.put("to_date", todate);
                parameters.put("from_date", fromdate);
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", "Term Deposit");
            } else  if (accountType.equalsIgnoreCase("CAA")) {
                parameters.put("to_date", todate);
                parameters.put("from_date", fromdate);
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", "Current");
            }
            else {
                parameters.put("to_date", todate);
                parameters.put("from_date", fromdate);
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", accountType);
            }


            JasperPrint report = JasperFillManager.fillReport(compileReport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=corporate-"+accountType+"-accounts-list");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

        }catch (Exception exc){
            System.out.println(exc.getLocalizedMessage());
            return null;
        }

    }

    @GetMapping("/accounts/corporate/branch")
    public ResponseEntity<?> getAllBranchCorporateAccountsByAccountType(@RequestParam String fromdate, @RequestParam String todate, @RequestParam String branchCode, @RequestParam String accountType){
        try {
            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(path + "/retail_accounts_branch.jrxml"));

            Map<String, Object> parameters = new HashMap<>();
            if (accountType.equalsIgnoreCase("SBA")) {
                parameters.put("to_date", todate);
                parameters.put("from_date", fromdate);
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("sol_code", branchCode);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", "Savving");
            }else  if (accountType.equalsIgnoreCase("OAB")) {
                parameters.put("to_date", todate);
                parameters.put("from_date", fromdate);
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("sol_code", branchCode);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", "Office");
            }else  if (accountType.equalsIgnoreCase("LAA")) {
                parameters.put("to_date", todate);
                parameters.put("from_date", fromdate);
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("sol_code", branchCode);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", "Loan");
            }else  if (accountType.equalsIgnoreCase("TDA")) {
                parameters.put("to_date", todate);
                parameters.put("from_date", fromdate);
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("sol_code", branchCode);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", "Term Deposit");
            }else  if (accountType.equalsIgnoreCase("CAA")) {
                parameters.put("to_date", todate);
                parameters.put("from_date", fromdate);
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("sol_code", branchCode);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", "Current");
            }else {
                parameters.put("to_date", todate);
                parameters.put("from_date", fromdate);
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("sol_code", branchCode);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", accountType);
            }


            JasperPrint report = JasperFillManager.fillReport(compileReport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=branch-corporate-"+accountType+"-accounts-list");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

        }catch (Exception exc){
            System.out.println(exc.getLocalizedMessage());
            return null;
        }

    }


    @GetMapping("/loan-demands/all")
    public ResponseEntity<?> getAllLoanDemands(@RequestParam String fromdate, @RequestParam String todate) {

        try {


            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compilereport = JasperCompileManager.compileReport(new FileInputStream(path + "/loan_demands_all.jrxml"));

            Map<String, Object> parameters = new HashMap<String, Object>();

            parameters.put("from_date", fromdate);
            parameters.put("to_date", todate);
            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);


            JasperPrint report = JasperFillManager.fillReport(compilereport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=loan-demands-list.pdf");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);


        } catch (Exception exc) {
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }

    @GetMapping("/loan-demands/branch")
    public ResponseEntity<?> getBranchLoanDemands(@RequestParam String fromdate, @RequestParam String todate, @RequestParam String branchCode) {

        try {
            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compilereport = JasperCompileManager.compileReport(new FileInputStream(path + "/loan_demands_branch.jrxml"));

            Map<String, Object> parameters = new HashMap<>();

            parameters.put("from_date", fromdate);
            parameters.put("to_date", todate);
            parameters.put("sol_code", branchCode);
            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);

            JasperPrint report = JasperFillManager.fillReport(compilereport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=branch-loandemands-list.pdf");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

        } catch (Exception exc) {
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }

    @GetMapping("/loan-demands/user")
    public ResponseEntity<?> getUserLoanDemands(@RequestParam String fromdate, @RequestParam String todate, @RequestParam String username) {

        try {
            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compilereport = JasperCompileManager.compileReport(new FileInputStream(path + "/loan_demands_user.jrxml"));

            Map<String, Object> parameters = new HashMap<String, Object>();

            parameters.put("from_date", fromdate);
            parameters.put("to_date", todate);
            parameters.put("account_manager", username);
            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);


            JasperPrint report = JasperFillManager.fillReport(compilereport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=user-loandemands-list.pdf");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);


        } catch (Exception exc) {
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }

    @GetMapping("/loan-demands/product")
    public ResponseEntity<?> getProductLoanDemands(@RequestParam String fromdate, @RequestParam String todate, @RequestParam String productCode) {

        try {
            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compilereport = JasperCompileManager.compileReport(new FileInputStream(path + "/loan_demands_by_product.jrxml"));

            Map<String, Object> parameters = new HashMap<String, Object>();

            parameters.put("from_date", fromdate);
            parameters.put("to_date", todate);
            parameters.put("product_code", productCode);
            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);


            JasperPrint report = JasperFillManager.fillReport(compilereport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=user-loandemands-list.pdf");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);


        } catch (Exception exc) {
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }

    @GetMapping("/loan-demands/member")
    public ResponseEntity<?> getMemberLoanDemands(@RequestParam String fromdate, @RequestParam String todate, @RequestParam String customerCode) {

        try {
            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compilereport = JasperCompileManager.compileReport(new FileInputStream(path + "/loan_demands_by_customer.jrxml"));

            Map<String, Object> parameters = new HashMap<>();

            parameters.put("from_date", fromdate);
            parameters.put("to_date", todate);
            parameters.put("customer_code", customerCode);
            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);


            JasperPrint report = JasperFillManager.fillReport(compilereport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=user-loandemands-list.pdf");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);


        } catch (Exception exc) {
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }


    @GetMapping("/fees/all")
    public ResponseEntity<?> getFeesReport(@RequestParam String fromdate, @RequestParam String todate) {

        try {
            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compilereport = JasperCompileManager.compileReport(new FileInputStream(path + "/fees_all.jrxml"));

            Map<String, Object> parameters = new HashMap<String, Object>();

            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);
            parameters.put("from_date", fromdate);
            parameters.put("to_date", todate);


            JasperPrint report = JasperFillManager.fillReport(compilereport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=all-fees.pdf");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);


        } catch (Exception exc) {
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }

    @GetMapping("/fees/branch")
    public ResponseEntity<?> getBranchFeesReport(@RequestParam String branchCode, @RequestParam String fromdate, @RequestParam String todate) {



        try {
            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compilereport = JasperCompileManager.compileReport(new FileInputStream(path + "/fees_branch.jrxml"));

            Map<String, Object> parameters = new HashMap<String, Object>();

            parameters.put("sol_code", branchCode);
            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);
            parameters.put("from_date", fromdate);
            parameters.put("to_date", todate);


            JasperPrint report = JasperFillManager.fillReport(compilereport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=all-branch-fees-list.pdf");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);


        } catch (Exception exc) {
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }


    @GetMapping("/expenses/all")
    public ResponseEntity<?> getExpensesReport() {

        try {
            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compilereport = JasperCompileManager.compileReport(new FileInputStream(path + "/expenses_all.jrxml"));

            Map<String, Object> parameters = new HashMap<String, Object>();

            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);


            JasperPrint report = JasperFillManager.fillReport(compilereport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=all-expenses.pdf");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);


        } catch (Exception exc) {
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }

    @GetMapping("/expenses/branch")
    public ResponseEntity<?> getBranchExpensesReport(@RequestParam String branchCode, @RequestParam String fromdate, @RequestParam String todate) {
        try {
            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compilereport = JasperCompileManager.compileReport(new FileInputStream(path + "/expenses_all.jrxml"));

            Map<String, Object> parameters = new HashMap<String, Object>();

            parameters.put("sol_code", branchCode);
            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);
            parameters.put("from_date", fromdate);
            parameters.put("to_date", todate);
            parameters.put("expense_glcode", "100000");


            JasperPrint report = JasperFillManager.fillReport(compilereport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=all-branch-expenses-list.pdf");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);


        } catch (Exception exc) {
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }

    @GetMapping("/users")
    public  ResponseEntity<?> generateUsersReport(HttpServletResponse response) {
        try {
            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compilereport = JasperCompileManager.compileReport(new FileInputStream(path + "/users_list.jrxml"));

            Map<String, Object> parameters = new HashMap<String, Object>();

            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);

            JasperPrint report = JasperFillManager.fillReport(compilereport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=cash-receipt.pdf");

//            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//            response.setHeader("Content-Disposition", "attachment; filename=\"" + "Accounts.xlsx" + "\"");
//            JRXlsxExporter exporterXls = new JRXlsxExporter();
//            exporterXls.setParameter(JRExporterParameter.JASPER_PRINT, report);
//            exporterXls.setParameter(JRExporterParameter.OUTPUT_STREAM, response.getOutputStream());
//            exporterXls.exportReport();

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);
        } catch (Exception exc) {
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }


    @GetMapping("/roles")
    public ResponseEntity<?> generatRolesReport() {
        try {
            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compilereport = JasperCompileManager.compileReport(new FileInputStream(path + "/roles.jrxml"));

            Map<String, Object> parameters = new HashMap<String, Object>();

            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);

            JasperPrint report = JasperFillManager.fillReport(compilereport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=cash-receipt.pdf");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);
        } catch (Exception exc) {
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }


    @GetMapping("/tran-receipt")
    public ResponseEntity<?> generatTranReceipt(@RequestParam String transactionCode) {
        try {
            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compilereport = JasperCompileManager.compileReport(new FileInputStream(path + "/transaction_receipt_customer.jrxml"));

            Map<String, Object> parameters = new HashMap<String, Object>();

            parameters.put("transaction_code", transactionCode);
            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);

            JasperPrint report = JasperFillManager.fillReport(compilereport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=cash-receipt.pdf");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);
        } catch (Exception exc) {
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }

    @GetMapping("/cash-receipt")
    public ResponseEntity<?> generateCashReceipt(@RequestParam String transactionCode,@RequestParam String receiptType,
                                                 @RequestParam String servedBy, @RequestParam Double amount) {
        try {
            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compilereport = JasperCompileManager.compileReport(new FileInputStream(path + "/withdrawal_receipt3.jrxml"));

            Map<String, Object> parameters = new HashMap<String, Object>();

            String  myamount = new DecimalFormat("##0").format(amount);
            Long theamount = Long.parseLong(myamount);
            String amountinwords = AmountToWords(theamount);

            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);
            parameters.put("transaction_code", transactionCode);
            parameters.put("amount_in_words", amountinwords);
            parameters.put("receipt_type", receiptType);
            parameters.put("served_by", servedBy);



            JasperPrint report = JasperFillManager.fillReport(compilereport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=cash-receipt.pdf");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);


        } catch (Exception exc) {
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }

    private String AmountToWords(Long amount){
        Numbers2Words ntw = new Numbers2Words();
        return ntw.EnglishNumber(amount);
    }

    @GetMapping("/active-dormant-accounts/all")
    public ResponseEntity<?> getAllActiveDormantReport(@RequestParam String fromdate, @RequestParam String todate, @RequestParam String accountType, @RequestParam String accountStatus) {

        try {
            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compilereport = JasperCompileManager.compileReport(new FileInputStream(path + "/active_dormant_accounts_all.jrxml"));

            Map<String, Object> parameters = new HashMap<String, Object>();

            if (accountType.equalsIgnoreCase("SBA")) {
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("from_date", fromdate);
                parameters.put("to_date", todate);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", "Savings");
                parameters.put("account_status", accountStatus);

            }else  if (accountType.equalsIgnoreCase("LAA")) {
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("from_date", fromdate);
                parameters.put("to_date", todate);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", "Loan");
                parameters.put("account_status", accountStatus);
            } else  if (accountType.equalsIgnoreCase("OAB")) {
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("from_date", fromdate);
                parameters.put("to_date", todate);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", "Office");
                parameters.put("account_status", accountStatus);
            }else  if (accountType.equalsIgnoreCase("TDA")) {
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("from_date", fromdate);
                parameters.put("to_date", todate);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", "Term Deposit");
                parameters.put("account_status", accountStatus);
            } else  if (accountType.equalsIgnoreCase("CAA")) {
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("from_date", fromdate);
                parameters.put("to_date", todate);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", "Current");
                parameters.put("account_status", accountStatus);
            } else {
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("from_date", fromdate);
                parameters.put("to_date", todate);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", accountType);
                parameters.put("account_status", accountStatus);
            }

            JasperPrint report = JasperFillManager.fillReport(compilereport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=active-dormant-accounts.pdf");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);


        } catch (Exception exc) {
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }

    @GetMapping("/dormant-accounts/by/product/code")
    public ResponseEntity<?> getAllDormantAccountsByProductCodeReport(@RequestParam String product_code) {

        try {
            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compilereport = JasperCompileManager.compileReport(new FileInputStream(path + "/dormant_accounts_by_product_code.jrxml"));

            Map<String, Object> parameters = new HashMap<String, Object>();

                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("product_code", product_code);

            JasperPrint report = JasperFillManager.fillReport(compilereport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=dormant-accounts.pdf");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);


        } catch (Exception exc) {
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }

    @GetMapping("/accounts/with/no/shares")
    public ResponseEntity<?> getAllAccountsWithNoSharesReport() {

        try {
            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compilereport = JasperCompileManager.compileReport(new FileInputStream(path + "/accounts_with_no_shares.jrxml"));

            Map<String, Object> parameters = new HashMap<String, Object>();

            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);

            JasperPrint report = JasperFillManager.fillReport(compilereport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=accounts-with-no-shares.pdf");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);


        } catch (Exception exc) {
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }

    @GetMapping("/office/chart/of/accounts")
    public ResponseEntity<?> getChartOfAccountsReport() {

        try {
            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compilereport = JasperCompileManager.compileReport(new FileInputStream(path + "/chart_of_accounts.jrxml"));

            Map<String, Object> parameters = new HashMap<String, Object>();

            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);

            JasperPrint report = JasperFillManager.fillReport(compilereport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=office-chart-of-accounts.pdf");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);


        } catch (Exception exc) {
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }

    @GetMapping("/active-dormant-accounts/branch")
    public ResponseEntity<?> getBranchActiveDormantReport(@RequestParam String branchCode, @RequestParam String fromdate, @RequestParam String todate, @RequestParam String accountType, @RequestParam String accountStatus) {

        try {
            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compilereport = JasperCompileManager.compileReport(new FileInputStream(path + "/active_dormant_accounts_branch.jrxml"));

            Map<String, Object> parameters = new HashMap<String, Object>();


            if (accountType.equalsIgnoreCase("SBA")) {
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("from_date", fromdate);
                parameters.put("to_date", todate);
                parameters.put("sol_code", branchCode);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", "Savings");
                parameters.put("account_status", accountStatus);

            }else  if (accountType.equalsIgnoreCase("LAA")) {
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("from_date", fromdate);
                parameters.put("to_date", todate);
                parameters.put("sol_code", branchCode);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", "Loan");
                parameters.put("account_status", accountStatus);
            } else  if (accountType.equalsIgnoreCase("OAB")) {
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("from_date", fromdate);
                parameters.put("to_date", todate);
                parameters.put("sol_code", branchCode);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", "Office");
                parameters.put("account_status", accountStatus);
            }else  if (accountType.equalsIgnoreCase("TDA")) {
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("from_date", fromdate);
                parameters.put("to_date", todate);
                parameters.put("sol_code", branchCode);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", "Term Deposit");
                parameters.put("account_status", accountStatus);
            } else  if (accountType.equalsIgnoreCase("CAA")) {
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("from_date", fromdate);
                parameters.put("to_date", todate);
                parameters.put("sol_code", branchCode);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", "Current");
                parameters.put("account_status", accountStatus);
            } else {
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("from_date", fromdate);
                parameters.put("to_date", todate);
                parameters.put("sol_code", branchCode);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", accountType);
                parameters.put("account_status", accountStatus);
            }


            JasperPrint report = JasperFillManager.fillReport(compilereport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=branch-active-dormant-accounts.pdf");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);


        } catch (Exception exc) {
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }

    @GetMapping("/transactions/account-type/all")
    public ResponseEntity<?> getAllAccountTypesTransactionsReport(@RequestParam String fromdate, @RequestParam String todate, @RequestParam String accountType) {
        System.out.println("Request received");
        try {
            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compilereport = JasperCompileManager.compileReport(new FileInputStream(path + "/account_type_transactions_all.jrxml"));

            Map<String, Object> parameters = new HashMap<String, Object>();

            if (accountType.equalsIgnoreCase("SBA")) {
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("from_date", fromdate);
                parameters.put("to_date", todate);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", "Savings");

            }else  if (accountType.equalsIgnoreCase("LAA")) {
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("from_date", fromdate);
                parameters.put("to_date", todate);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", "Loan");
            } else  if (accountType.equalsIgnoreCase("OAB")) {
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("from_date", fromdate);
                parameters.put("to_date", todate);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", "Office");
            }else  if (accountType.equalsIgnoreCase("TDA")) {
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("from_date", fromdate);
                parameters.put("to_date", todate);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", "Term Deposit");
            } else  if (accountType.equalsIgnoreCase("CAA")) {
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("from_date", fromdate);
                parameters.put("to_date", todate);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", "Current");
            } else {
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("from_date", fromdate);
                parameters.put("to_date", todate);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", accountType);
            }

            System.out.println(parameters);

            JasperPrint report = JasperFillManager.fillReport(compilereport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename="+accountType+ "-account-transactions.pdf");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);


        } catch (Exception exc) {
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }

    @GetMapping("/transactions/account-type/branch")
    public ResponseEntity<?> getBranchAccountTypesTransactionsReport(@RequestParam String branchCode, @RequestParam String fromdate, @RequestParam String todate, @RequestParam String accountType) {

        try {
            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compilereport = JasperCompileManager.compileReport(new FileInputStream(path + "/account_type_transactions_branch.jrxml"));

            Map<String, Object> parameters = new HashMap<String, Object>();


            if (accountType.equalsIgnoreCase("SBA")) {
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("from_date", fromdate);
                parameters.put("to_date", todate);
                parameters.put("sol_code", branchCode);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", "Savings");


            }else  if (accountType.equalsIgnoreCase("LAA")) {
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("from_date", fromdate);
                parameters.put("to_date", todate);
                parameters.put("sol_code", branchCode);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", "Loan");

            } else  if (accountType.equalsIgnoreCase("OAB")) {
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("from_date", fromdate);
                parameters.put("to_date", todate);
                parameters.put("sol_code", branchCode);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", "Office");

            }else  if (accountType.equalsIgnoreCase("TDA")) {
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("from_date", fromdate);
                parameters.put("to_date", todate);
                parameters.put("sol_code", branchCode);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", "Term Deposit");

            } else  if (accountType.equalsIgnoreCase("CAA")) {
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("from_date", fromdate);
                parameters.put("to_date", todate);
                parameters.put("sol_code", branchCode);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", "Current");

            } else {
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("from_date", fromdate);
                parameters.put("to_date", todate);
                parameters.put("sol_code", branchCode);
                parameters.put("account_type", accountType);
                parameters.put("account_type_name", accountType);

            }


            JasperPrint report = JasperFillManager.fillReport(compilereport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=branch-"+accountType+"-accounts-transactions.pdf");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);


        } catch (Exception exc) {
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }


    @GetMapping("/transactions/customer-account-type/")
    public ResponseEntity<?> getAllCustomerAccountTypesTransactionsReport(@RequestParam String fromdate, @RequestParam String todate, @RequestParam String accountType, @RequestParam String customerCode) {

        try {
            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compilereport = JasperCompileManager.compileReport(new FileInputStream(path + "/customer_account_type_transactions.jrxml"));

            Map<String, Object> parameters = new HashMap<String, Object>();

            if (accountType.equalsIgnoreCase("SBA")) {
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("from_date", fromdate);
                parameters.put("to_date", todate);
                parameters.put("account_type", accountType);
                parameters.put("customer_code", customerCode);
                parameters.put("account_type_name", "Savings");

            }else  if (accountType.equalsIgnoreCase("LAA")) {
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("from_date", fromdate);
                parameters.put("to_date", todate);
                parameters.put("account_type", accountType);
                parameters.put("customer_code", customerCode);
                parameters.put("account_type_name", "Loan");
            } else  if (accountType.equalsIgnoreCase("OAB")) {
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("from_date", fromdate);
                parameters.put("to_date", todate);
                parameters.put("account_type", accountType);
                parameters.put("customer_code", customerCode);
                parameters.put("account_type_name", "Office");
            }else  if (accountType.equalsIgnoreCase("TDA")) {
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("from_date", fromdate);
                parameters.put("to_date", todate);
                parameters.put("account_type", accountType);
                parameters.put("customer_code", customerCode);
                parameters.put("account_type_name", "Term Deposit");
            } else  if (accountType.equalsIgnoreCase("CAA")) {
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("from_date", fromdate);
                parameters.put("to_date", todate);
                parameters.put("account_type", accountType);
                parameters.put("customer_code", customerCode);
                parameters.put("account_type_name", "Current");
            } else {
                parameters.put("report_icon", report_icon);
                parameters.put("sacco_name", sacco_name);
                parameters.put("from_date", fromdate);
                parameters.put("to_date", todate);
                parameters.put("account_type", accountType);
                parameters.put("customer_code", customerCode);
                parameters.put("account_type_name", accountType);
            }

            JasperPrint report = JasperFillManager.fillReport(compilereport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename="+customerCode+"-"+accountType+ "-account-transactions.pdf");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);


        } catch (Exception exc) {
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }


    @GetMapping("/trial_balance/all")
    public ResponseEntity<?> getTrialBalanceReport() {

        try {
            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compilereport = JasperCompileManager.compileReport(new FileInputStream(path + "/trial_balance.jrxml"));

            Map<String, Object> parameters = new HashMap<String, Object>();

            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);


            JasperPrint report = JasperFillManager.fillReport(compilereport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=trial-balance.pdf");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);


        } catch (Exception exc) {
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }

    @GetMapping("/balance-sheet/all")
    public ResponseEntity<?> getBalanceSheet() {

        try {


            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compilereport = JasperCompileManager.compileReport(new FileInputStream(path + "/balancesheet.jrxml"));

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);


            JasperPrint report = JasperFillManager.fillReport(compilereport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=balance-sheet.pdf");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);


        } catch (Exception exc) {
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }


    @GetMapping("profit_loss/all")
    public ResponseEntity<?> getProfitLossReport() {
        try {
            System.out.println("In profit and loss");
            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compilereport = JasperCompileManager.compileReport(new FileInputStream(path + "/profitloss.jrxml"));

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);


            JasperPrint report = JasperFillManager.fillReport(compilereport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=profit&loss.pdf");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

        }catch (Exception exc){
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }

    @GetMapping("guarantorship/all")
    public ResponseEntity<?>  getGuaranteedLoans(@RequestParam String fromdate, @RequestParam String todate,
                                                HttpServletResponse response) {
        try {

            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compilereport = JasperCompileManager.compileReport(new FileInputStream(path + "/all_guarantors.jrxml"));

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);
            parameters.put("from_date", fromdate);
            parameters.put("to_date", todate);


            JasperPrint report = JasperFillManager.fillReport(compilereport, parameters, connection);

//            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//            response.setHeader("Content-Disposition", "attachment; filename=\"" + "Accounts.xlsx" + "\"");
//            JRXlsxExporter exporterXls = new JRXlsxExporter();
//            exporterXls.setParameter(JRExporterParameter.JASPER_PRINT, report);
//            exporterXls.setParameter(JRExporterParameter.OUTPUT_STREAM, response.getOutputStream());
//            exporterXls.exportReport();


            byte[] data = JasperExportManager.exportReportToPdf(report);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=all_guarantors.pdf");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

        }catch (Exception exc) {
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }

    @GetMapping("guarantorship/for-one-loan")
    public ResponseEntity<?> getAllGuarantorsForLoan( @RequestParam String acid) {
        try {

            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compilereport = JasperCompileManager.compileReport(new FileInputStream(path + "/guarantors_for_one_loan.jrxml"));

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);
            parameters.put("acid", acid);


            JasperPrint report = JasperFillManager.fillReport(compilereport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=guarantors_guaranteed_loans.pdf");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

        }catch (Exception exc){
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }

    @GetMapping("guarantorship/forperson")
    public ResponseEntity<?> getGuaranteedLoans(@RequestParam String guarantorCode) {
        try {

            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compilereport = JasperCompileManager.compileReport(new FileInputStream(path + "/my_guaranteed_loans.jrxml"));

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);
            parameters.put("guarantors_code", guarantorCode);


            JasperPrint report = JasperFillManager.fillReport(compilereport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=my_guaranteed_loans.jrxml.pdf");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

        }catch (Exception exc){
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }


    @GetMapping("group_customers/all")
    public ResponseEntity<?> getAllGroupCustomersReport(@RequestParam String fromdate, @RequestParam String todate){
        try {

            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compilereport = JasperCompileManager.compileReport(new FileInputStream(path + "/group_customers_all.jrxml"));

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);
            parameters.put("from_date", fromdate);
            parameters.put("to_date", todate);


            JasperPrint report = JasperFillManager.fillReport(compilereport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=group_customers.pdf");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

        }catch (Exception exc){
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }

    @GetMapping("group_customers/branch")
    public ResponseEntity<?> getBranchGroupCustomersReport(@RequestParam String fromdate, @RequestParam String todate, @RequestParam String solcode){
        try {

            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compilereport = JasperCompileManager.compileReport(new FileInputStream(path + "/group_customers_branch.jrxml"));

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);
            parameters.put("from_date", fromdate);
            parameters.put("to_date", todate);
            parameters.put("sol_code", solcode);


            JasperPrint report = JasperFillManager.fillReport(compilereport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=group_customers_branch.pdf");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

        }catch (Exception exc){
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }

    @GetMapping("portfolio-at-risk/all")
    public ResponseEntity<?> getAllPortfoliotRiskReport(@RequestParam String fromdate, @RequestParam String todate){
        try {

            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compilereport = JasperCompileManager.compileReport(new FileInputStream(path + "/portfolio_at_risk_all.jrxml"));

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);
            parameters.put("from_date", fromdate);
            parameters.put("to_date", todate);


            JasperPrint report = JasperFillManager.fillReport(compilereport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=portfolio-at-risk.pdf");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

        }catch (Exception exc){
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }

    @GetMapping("portfolio-at-risk/branch")
    public ResponseEntity<?> getBranchPortfoliotRiskReport(@RequestParam String fromdate, @RequestParam String todate, @RequestParam String solcode){
        try {

            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compilereport = JasperCompileManager.compileReport(new FileInputStream(path + "/portfolio_at_risk_branch.jrxml"));

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);
            parameters.put("from_date", fromdate);
            parameters.put("to_date", todate);
            parameters.put("sol_code", solcode);


            JasperPrint report = JasperFillManager.fillReport(compilereport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=portfolio-at-risk_branch.pdf");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

        } catch (Exception exc) {
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }

    @GetMapping("loan/account/asset/classification")
    public ResponseEntity<?> loanAccountAssetClassification(@RequestParam String classification, @RequestParam String productCode) {
        try {

            Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
            JasperReport compilereport = JasperCompileManager.compileReport(new FileInputStream(path + "/loan_classification.jrxml"));

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("report_icon", report_icon);
            parameters.put("sacco_name", sacco_name);
            parameters.put("classification", classification);
            parameters.put("product_code", productCode);


            JasperPrint report = JasperFillManager.fillReport(compilereport, parameters, connection);
            byte[] data = JasperExportManager.exportReportToPdf(report);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=portfolio-at-risk_branch.pdf");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

        }catch (Exception exc){
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
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
        else if(accountType.equalsIgnoreCase("TDA")) {
            return "Term Deposits";
        } else  if (accountType.equalsIgnoreCase("CAA")) {
            return "Current";
        }
        return accountType;
    }
}