package com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader;

import com.emtechhouse.accounts.Models.Accounts.AccountRepository;
import com.emtechhouse.accounts.NotificationComponent.SmsService.SMSService;
import com.emtechhouse.accounts.TransactionService.ChequeProcessing.ChequeProcessing;
import com.emtechhouse.accounts.TransactionService.ChequeProcessing.ChequeProcessingRepo;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.PartTrans.PartTran;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.emtechhouse.accounts.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.accounts.Utils.HttpInterceptor.UserRequestContext;
import com.emtechhouse.accounts.Utils.MoneyWordConverter;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import com.emtechhouse.accounts.Utils.ServiceCaller;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequestMapping("api/v1/transaction/receipts")
@RestController
@CrossOrigin
public class TransactionReceipts {
    @Autowired
    ServiceCaller serviceCaller;

    @Value("${spring.organisation.reports_absolute_path}")
    private String files_path;
    //    @Value("${logolink}")
    private String logolink;
    @Value("${spring.datasource.url}")
    private String db;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;
    //    spring.organisation.
    @Value("${spring.organisation.organizationName}")
    private String organizationName;
    @Value("${spring.organisation.organizationAddress}")
    private String organizationAddress;
    @Value("${spring.organisation.organizationPhone}")
    private String organizationPhone;
    @Value("${spring.organisation.organizationDescription}")
    private String organizationDescription;
    @Value("${spring.organisation.company_logo_path}")
    private String organizationLogo;

    @Autowired
    private MoneyWordConverter moneyWordConverter;

    @Autowired
    SMSService smsService;
    @Autowired
    TranHeaderRepository tranHeaderRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    ChequeProcessingRepo chequeProcessingRepo;
    @Autowired
    TransactionProcessing transactionProcessing;

    @GetMapping("/withdrawal")
    public ResponseEntity<?> getReport(@RequestParam String transactionCode) {
        try {
            Optional<TransactionHeader> checkTrans = tranHeaderRepository.findByTransactionCode(transactionCode);
            if (checkTrans.isPresent()) {
                if (checkTrans.get().getPostedFlag() == 'Y'){
                    if (checkTrans.get().getTransactionType().equalsIgnoreCase(CONSTANTS.CASH_WITHDRAWAL)) {
                        String customerCode = "";
                        String accountNo= "";
                        String accountName = "";
                        String transactionRefCode = "";
                        String transactionDate = "";
                        String transactionParticulars = "";
                        String transactionAmount = "";
                        String tranAmountInWords = "";
                        Double withdrawalFee = 0.0;
                        Double availableBalance = 0.0;
                        Double exciseDutyAmount = 0.0;
                        String servedBy = "";
                        String conductedBy = "";
                        String timestamp = "";
                        TransactionHeader transactionHeader = checkTrans.get();
                        List<PartTran> partTrans = transactionHeader.getPartTrans();
                        List<PartTran> debits = partTrans.stream().filter(d->d.getPartTranType().equalsIgnoreCase(CONSTANTS.Debit)).collect(Collectors.toList());
                        for (int i=0; i<debits.size(); i++){
                            PartTran partTran = debits.get(i);
                            transactionRefCode = transactionHeader.getTransactionCode();
                            transactionDate = transactionHeader.getTransactionDate().toString();
                            servedBy = transactionHeader.getPostedBy();
                            timestamp = transactionHeader.getPostedTime().toString();
                            if (partTran.getPartTranType().equalsIgnoreCase(CONSTANTS.Debit)){
//                    DEBIT FROM CUSTOMER
                                if (partTran.getParttranIdentity().equalsIgnoreCase(CONSTANTS.Normal)){
                                    transactionAmount = String.valueOf(partTran.getTransactionAmount());
                                    tranAmountInWords = moneyWordConverter.doubleConvert(partTran.getTransactionAmount());
                                    transactionParticulars = partTran.getTransactionParticulars();
                                    conductedBy = partTran.getConductedBy();

                                    accountNo = partTran.getAcid();
                                    availableBalance = accountRepository.getAvailableBalance(accountNo);
                                    if (availableBalance == null)
                                        availableBalance = 0.0;
                                    Optional<AccountRepository.AcTransactionDetails> acDetails = accountRepository.getAccountTransactionDetails(accountNo);
                                    customerCode = acDetails.get().getCustomercode();
                                    accountName = acDetails.get().getAccountName();
                                }
                                if (partTran.getParttranIdentity().equalsIgnoreCase(CONSTANTS.Tax)){
                                    exciseDutyAmount = partTran.getTransactionAmount();
                                }
                                if (partTran.getParttranIdentity().equalsIgnoreCase(CONSTANTS.Fee)){
                                    withdrawalFee = partTran.getTransactionAmount();
                                }
                            }
                        }
                        double chargesSum = exciseDutyAmount + withdrawalFee;
                        String charges = String.valueOf(chargesSum);
                        Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
                        JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(files_path + "/Withdrawal.jrxml"));
                        Map<String, Object> parameter = new HashMap<String, Object>();
                        parameter.put("customerCode", customerCode);
                        parameter.put("accountNo", accountNo);
                        parameter.put("accountName", accountName);
                        parameter.put("transactionRefCode", transactionRefCode);
                        parameter.put("transactionDate", transactionDate);
                        parameter.put("transactionParticulars", transactionParticulars);
                        parameter.put("transactionAmount", transactionAmount);
                        parameter.put("tranAmountInWords", tranAmountInWords);
                        parameter.put("conductedBy", conductedBy);
                        parameter.put("charges", charges);
                        parameter.put("availbal", String.format("%.2f", availableBalance));
                        parameter.put("available",String.format("%.2f", availableBalance));
                        parameter.put("servedBy", servedBy);
                        parameter.put("timestamp", timestamp);
                        parameter.put("organizationName", organizationName);
                        parameter.put("organizationAddress", organizationAddress);
                        parameter.put("organizationPhone", organizationPhone);
                        parameter.put("organizationDescription", organizationDescription);
                        parameter.put("organizationLogo",organizationLogo);
                        System.out.println(parameter);
                        JasperPrint report = JasperFillManager.fillReport(compileReport, parameter, connection);
                        byte[] data = JasperExportManager.exportReportToPdf(report);
                        HttpHeaders headers = new HttpHeaders();
                        headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=withdrawal.pdf");
                        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);
                    }else {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("Not a withdrawal transaction!");
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                    }
                } else {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("Transaction not Posted!");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }
            } else {
                EntityResponse response = new EntityResponse();
                response.setMessage("Not Found!");
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception exc) {
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }
    @GetMapping("/cashdeposit")
    public ResponseEntity<?> getCashDeposit(@RequestParam String transactionCode) {
        try {
            Optional<TransactionHeader> checkTrans = tranHeaderRepository.findByTransactionCode(transactionCode);
            if (checkTrans.isPresent()){
                if (checkTrans.get().getPostedFlag() == 'Y'){
                    System.out.println(checkTrans.get());
                    if (checkTrans.get().getTransactionType().equalsIgnoreCase(CONSTANTS.CASH_DEPOSIT)){
                        String customerCode = "";
                        String accountNo= "";
                        String accountName = "";
                        String conductedBy = "";
                        String transactionRefCode = "";
                        String transactionDate = "";
                        String transactionParticulars = "";
                        String transactionAmount = "";
                        String tranAmountInWords = "";
                        Double withdrawalFee = 0.0;
                        Double exciseDutyAmount = 0.0;
                        String servedBy = "";
                        String timestamp = "";
                        String accountType = "";
                        String memberName = "";
                        TransactionHeader transactionHeader = checkTrans.get();
                        List<PartTran> partTrans = transactionHeader.getPartTrans();
                        List<PartTran> credits = partTrans.stream().filter(d->d.getPartTranType().equalsIgnoreCase(CONSTANTS.Credit)).collect(Collectors.toList());
                        for (int i=0; i<credits.size(); i++){
                            PartTran partTran = credits.get(i);
                            transactionRefCode = transactionHeader.getTransactionCode();
                            transactionDate = transactionHeader.getTransactionDate().toString();
                            servedBy = transactionHeader.getPostedBy();
                            timestamp = transactionHeader.getPostedTime().toString();
                            if (partTran.getPartTranType().equalsIgnoreCase(CONSTANTS.Credit)){
//                    DEBIT FROM CUSTOMER
                                if (partTran.getParttranIdentity().equalsIgnoreCase(CONSTANTS.Normal)){
                                    transactionAmount = String.valueOf(partTran.getTransactionAmount());
                                    tranAmountInWords = moneyWordConverter.doubleConvert(partTran.getTransactionAmount());
                                    transactionParticulars = partTran.getTransactionParticulars();
                                    conductedBy = partTran.getConductedBy();
                                    accountNo = partTran.getAcid();
                                    Optional<AccountRepository.AcTransactionDetails> acDetails = accountRepository.getAccountTransactionDetails(accountNo);
                                    customerCode = acDetails.get().getCustomercode();
                                    accountName = acDetails.get().getAccountName();
                                    memberName = accountName;
                                    accountType = acDetails.get().getAccountType();
                                }
                                if (partTran.getParttranIdentity().equalsIgnoreCase(CONSTANTS.Tax)){
                                    exciseDutyAmount = partTran.getTransactionAmount();
                                }
                                if (partTran.getParttranIdentity().equalsIgnoreCase(CONSTANTS.Fee)){
                                    withdrawalFee = partTran.getTransactionAmount();
                                }
                            }
                        }
                        double chargesSum = exciseDutyAmount + withdrawalFee;
                        String charges = String.valueOf(chargesSum);
                        Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
                        JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(files_path + "/CashDeposit.jrxml"));
                        Map<String, Object> parameter = new HashMap<String, Object>();
                        parameter.put("customerCode",customerCode);
                        parameter.put("accountNo",accountNo);
                        parameter.put("accountName",accountName);
                        parameter.put("conductedBy",conductedBy);
                        parameter.put("transactionRefCode",transactionRefCode);
                        parameter.put("transactionDate",transactionDate);
                        parameter.put("transactionParticulars",transactionParticulars);
                        parameter.put("transactionAmount",transactionAmount);
                        parameter.put("tranAmountInWords",tranAmountInWords);
                        parameter.put("servedBy",servedBy);
                        parameter.put("timestamp",timestamp);
                        parameter.put("organizationName",organizationName);
                        parameter.put("organizationAddress",organizationAddress);
                        parameter.put("organizationPhone",organizationPhone);
                        parameter.put("organizationDescription",organizationDescription);
                        parameter.put("organizationLogo",organizationLogo);
                        parameter.put("charges",charges);
                        parameter.put("memberName",memberName);
                        parameter.put("accountType",accountType);
                        System.out.println(parameter);
                        JasperPrint report = JasperFillManager.fillReport(compileReport, parameter, connection);
                        byte[] data = JasperExportManager.exportReportToPdf(report);
                        HttpHeaders headers = new HttpHeaders();
                        headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=withdrawal.pdf");
                        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);
                    }else {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("Not a cash deposit transaction!");
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                    }
                }else {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("Transaction not Posted!");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }

            }else {
                EntityResponse response = new EntityResponse();
                response.setMessage("Not Found!");
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                System.out.println(response);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

        } catch (Exception exc) {
            System.out.println("Error error");
            exc.printStackTrace();
            System.out.println(exc.getLocalizedMessage());
            return null;
        }
    }
    @GetMapping("/chequedeposit")
    public ResponseEntity<?> getDocument(@RequestParam String chequeRandCode) {
        try {
        EntityResponse response = new EntityResponse();
        Optional<ChequeProcessing> checkTrans = chequeProcessingRepo.findByChequeRandCode(chequeRandCode);
        if (checkTrans.isPresent()) {
            ChequeProcessing chequeProcessing = checkTrans.get();
//            if (chequeProcessing.getPostedFlag() == 'Y'){
                Optional<AccountRepository.AcTransactionDetails> acDetails = accountRepository.getAccountTransactionDetails(chequeProcessing.getCreditCustOperativeAccount());
                String No = chequeProcessing.getChequeRandCode();
                String creditCustomerCodeName = acDetails.get().getCustomercode()+ " - " + acDetails.get().getAccountName();
                String creditAccountName = acDetails.get().getAccountName();
                String customerCode = acDetails.get().getCustomercode();
                String Account = chequeProcessing.getCreditCustOperativeAccount();
                String chequeInfo = chequeProcessing.getChequeNumber();
                String maturityDate = chequeProcessing.getMaturityDate().toString();
                String Amount = chequeProcessing.getAmount().toString();
                String AmountInWords = moneyWordConverter.doubleConvert(chequeProcessing.getAmount());
                String Charges  = chequeProcessing.getChargeAmount().toString();
                String transactionCode  = chequeProcessing.getChequeRandCode();
                String transactionDate  = chequeProcessing.getEnteredTime().toString();
                String chequeRefCode  = chequeProcessing.getChequeRandCode();
                String servedBy=chequeProcessing.getEnteredBy();
                String postedDate =chequeProcessing.getEnteredTime().toString();
                Connection connection = DriverManager.getConnection(this.db, this.username, this.password);
                JasperReport compileReport = JasperCompileManager.compileReport(new FileInputStream(files_path + "/ChequeDepositReceipt.jrxml"));
                Map<String, Object> parameter = new HashMap<String, Object>();
                parameter.put("No", No);
                parameter.put("customerCode", customerCode);
                parameter.put("creditCustomerCodeName", creditCustomerCodeName);
                parameter.put("creditAccountName", creditAccountName);
                parameter.put("Account", Account);
                parameter.put("ChequeInfo", chequeInfo);
                parameter.put("maturityDate", maturityDate);
                parameter.put("Amount", Amount);
                parameter.put("AmountInWords", AmountInWords);
                parameter.put("Charges", Charges);
                parameter.put("servedBy", servedBy);
                parameter.put("postedDate", postedDate);
                parameter.put("organizationName", organizationName );
                parameter.put("organizationAddress", organizationAddress);
                parameter.put("organizationPhone", organizationPhone);
                parameter.put("organizationDescription", organizationDescription);
                parameter.put("organizationLogo",organizationLogo);
                parameter.put("transactionCode",transactionCode);
                parameter.put("transactionDate",transactionDate);
                parameter.put("chequeRefCode",chequeRefCode);
                JasperPrint report = JasperFillManager.fillReport(compileReport, parameter, connection);
                byte[] data = JasperExportManager.exportReportToPdf(report);
                HttpHeaders headers = new HttpHeaders();
                headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=chequedeposit.pdf");
                return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);
//            }else {
//                response.setMessage("Cheque has not been posted!");
//                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                return new ResponseEntity<>(response, HttpStatus.OK);
//            }
        }else {
            response.setMessage("Not Found!");
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    } catch (Exception exc) {
        System.out.println(exc.getLocalizedMessage());
        return null;
    }
    }

//    public String getCustomerName(String customerCode, String acName) throws IOException {
//        String customerName = "";
//        String userName = UserRequestContext.getCurrentUser();
//        String entityId = EntityRequestContext.getCurrentEntityId();
//        EntityResponse response = serviceCaller.getCustomerInfo(customerCode,userName,entityId);
//        if (response.getStatusCode() == HttpStatus.OK.value()){
//            JSONObject jo = new JSONObject(response);
//            JSONObject entity = jo.getJSONObject("entity");
//            String customerNameStr = entity.getString("customerName");
//            if (customerNameStr==null || customerNameStr.trim().isEmpty()){
//                customerName = acName;
//                log.info("--------------Customer Not Found -------------");
//            }else {
//                customerName = customerNameStr;
//            }
//        }else {
//            customerName = acName;
//        }
//        return customerName;
//    }

}
