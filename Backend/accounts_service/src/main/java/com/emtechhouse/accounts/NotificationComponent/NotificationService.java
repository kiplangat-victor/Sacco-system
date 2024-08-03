package com.emtechhouse.accounts.NotificationComponent;

import com.emtechhouse.accounts.DTO.ContactInfo;
import com.emtechhouse.accounts.Models.Accounts.AccountClosure.AccountClosureService;
import com.emtechhouse.accounts.Models.Accounts.AccountDtos.SmsDto;
import com.emtechhouse.accounts.Models.Accounts.AccountRepository;
import com.emtechhouse.accounts.Notification.ServiceNotification;
import com.emtechhouse.accounts.NotificationComponent.EmailService.MailService;
import com.emtechhouse.accounts.NotificationComponent.SmsService.SMSService;
import com.emtechhouse.accounts.NotificationComponent.TransactionNotificationDetails.Transactionnotification;
import com.emtechhouse.accounts.NotificationComponent.TransactionNotificationDetails.TransactionnotificationRepo;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.PartTrans.PartTran;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TranHeaderRepository;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TransactionHeader;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.emtechhouse.accounts.Utils.DatesCalculator;
import com.emtechhouse.accounts.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.accounts.Utils.HttpInterceptor.UserRequestContext;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import com.emtechhouse.accounts.Utils.ServiceCaller;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class NotificationService {


    private final MailService mailService;
    private final SMSService smsService;
    private final ServiceCaller  serviceCaller;
    @Autowired
    private DatesCalculator datesCalculator;

    private final TranHeaderRepository tranHeaderRepository;
    private final AccountRepository accountRepository;
    private final TransactionnotificationRepo transactionnotificationRepo;

    @Autowired
    ServiceNotification serviceNotification;

//    private AccountClosureService accountClosureService;
//
////    @Autowired
//    public void setAccountClosureService(AccountClosureService accountClosureService) {
//        this.accountClosureService = accountClosureService;
//    }


    public NotificationService(MailService mailService, SMSService smsService, ServiceCaller serviceCaller, TranHeaderRepository tranHeaderRepository, AccountRepository accountRepository, TransactionnotificationRepo transactionnotificationRepo) {
        this.mailService = mailService;
        this.smsService = smsService;
        this.serviceCaller = serviceCaller;
        this.tranHeaderRepository = tranHeaderRepository;
        this.accountRepository = accountRepository;
        this.transactionnotificationRepo = transactionnotificationRepo;

    }


    public void sendEmailNotification(TransactionHeader transactionHeader) throws MessagingException, IOException {
        try {
            System.out.println("Sending email One");
            List<PartTran> partTrans = transactionHeader.getPartTrans();
            List<PartTran> debits = partTrans.stream().filter(d->d.getPartTranType().equalsIgnoreCase(CONSTANTS.Debit)).collect(Collectors.toList());
            Map<String, Double> minifiedDebits  = debits.stream().collect(Collectors.groupingBy(d->d.getAcid(),Collectors.summingDouble(d->d.getTransactionAmount())));
            for (String key : minifiedDebits.keySet()) {
                Double debitBal = minifiedDebits.get(key);
                String acid = key;
//                String to, String subject, String message
                Optional<AccountRepository.CustomerCode> customerCodeCheck = accountRepository.getCustomerCodeByAcidAndDeletedFlag(acid);
                if (customerCodeCheck.isPresent()) {
                    String customerCode = customerCodeCheck.get().getCustomer_code(); String userName = UserRequestContext.getCurrentUser();
                    String entityId = EntityRequestContext.getCurrentEntityId();
//                    EntityResponse response = serviceCaller.getCustomerPhoneAndEmail(customerCode,userName,entityId);
//                    if (response.getStatusCode().equals(HttpStatus.)) String to, String subject, String message)
                    String to = "ckibet@emtechhouse.co.ke";
                    String subject = "Transaction Information";
                    String message = "Greetings. Your account has been debited "+debitBal;
                    System.out.println("--------------ending email-------------");
                    mailService.sendEmail(to,subject,message);
                }
            }
            List<PartTran> credits = partTrans.stream().filter(d->d.getPartTranType().equalsIgnoreCase(CONSTANTS.Credit)).collect(Collectors.toList());
            Map<String, Double> minifiedCredits  = credits.stream().collect(Collectors.groupingBy(d->d.getAcid(),Collectors.summingDouble(d->d.getTransactionAmount())));
            for (String key : minifiedCredits.keySet()) {
                Double creditBal = minifiedCredits.get(key);
                String acid = key;
//                String to, String subject, String message
                Optional<AccountRepository.CustomerCode> customerCodeCheck = accountRepository.getCustomerCodeByAcidAndDeletedFlag(acid);
                if (customerCodeCheck.isPresent()){
                    String customerCode = customerCodeCheck.get().getCustomer_code(); String userName = UserRequestContext.getCurrentUser();
                    String entityId = EntityRequestContext.getCurrentEntityId();
//                    EntityResponse response = serviceCaller.getCustomerPhoneAndEmail(customerCode,userName,entityId);
//                    if (response.getStatusCode().equals(HttpStatus.)) String to, String subject, String message)
                    String to = "ckibet@emtechhouse.co.ke";
                    String subject = "Transaction Information";
                    String message = "Greetings. Your account has been credited "+creditBal;
                    System.out.println("--------------sending email-------------");
                    mailService.sendEmail(to,subject,message);
                }
            }
        } catch (Exception e) {

        }
    }

    public void sendSMSNotification(TransactionHeader transactionHeader) throws MessagingException, IOException {
        try {
            System.out.println("sendSMSNotification(TransactionHeader transactionHeader)");
            String transactionCode = transactionHeader.getTransactionCode();

            List<PartTran> partTrans = transactionHeader.getPartTrans();
            //            TODO: Send Debit SMS
            List<PartTran> debits = partTrans.stream().filter(d->d.getPartTranType().equalsIgnoreCase(CONSTANTS.Debit)).collect(Collectors.toList());
            for (int i =0; i<debits.size(); i++) {
                PartTran partTran = debits.get(i);
                String acid = partTran.getAcid();
///                Send sms to only customer accounts
                Optional<AccountRepository.AcTransactionDetails> acDetails = accountRepository.getAccountTransactionDetails(acid);
                if (acDetails.isPresent() && !acDetails.get().getAccountType().equalsIgnoreCase(CONSTANTS.OFFICE_ACCOUNT)){
                    Optional<AccountRepository.CustomerCode> customerCodeCheck = accountRepository.getCustomerCodeByAcidAndDeletedFlag(acid);
                    if (customerCodeCheck.isPresent()){
                        String customerCode = customerCodeCheck.get().getCustomer_code();
                        String userName = UserRequestContext.getCurrentUser();
                        String entityId = EntityRequestContext.getCurrentEntityId();
                        EntityResponse response = serviceCaller.getCustomerPhone_and_Email(customerCode,userName,entityId);
                        if (response.getStatusCode() == HttpStatus.OK.value()){
                            JSONObject jo = new JSONObject(response);
                            JSONObject entity = jo.getJSONObject("entity");
                            String phoneNumber = entity.getString("phoneNumber");
                            if (phoneNumber!=null && phoneNumber.length() == 12){
                                log.info("--------------Valid Phone No.  Sending sms to "+phoneNumber+" -------------");
                                String name = acDetails.get().getAccountName();
                                Double tranAmount = partTran.getTransactionAmount();
                                String tranDate = transactionHeader.getTransactionDate().toString();
                                String tranCode = transactionHeader.getTransactionCode();
                                String message = "Hello "+name+", your account has been debited Ksh."+tranAmount+" Ref code:" +tranCode+" Date "+tranDate;
                                smsService.SMSNotification(message,phoneNumber);
                            }else {
                                log.info("--------------Invalid Phone No.  Not Sending sms to "+phoneNumber+" -------------");
                            }
                        }
                    }
                }

            }
            //            TODO: Send Cr SMS
            List<PartTran> credits = partTrans.stream().filter(d->d.getPartTranType().equalsIgnoreCase(CONSTANTS.Credit)).collect(Collectors.toList());
            for (int i =0; i<credits.size(); i++){
                PartTran partTran = credits.get(i);
                String acid = partTran.getAcid();
///                Send sms to only customer accounts
                Optional<AccountRepository.AcTransactionDetails> acDetails = accountRepository.getAccountTransactionDetails(acid);
                if (acDetails.isPresent() && !acDetails.get().getAccountType().equalsIgnoreCase(CONSTANTS.OFFICE_ACCOUNT)){
                    Optional<AccountRepository.CustomerCode> customerCodeCheck = accountRepository.getCustomerCodeByAcidAndDeletedFlag(acid);
                    if (customerCodeCheck.isPresent()){
                        String customerCode = customerCodeCheck.get().getCustomer_code();
                        String userName = UserRequestContext.getCurrentUser();
                        String entityId = EntityRequestContext.getCurrentEntityId();
                        EntityResponse response = serviceCaller.getCustomerPhone_and_Email(customerCode,userName,entityId);
                        if (response.getStatusCode() == HttpStatus.OK.value()){
                            JSONObject jo = new JSONObject(response);
                            JSONObject entity = jo.getJSONObject("entity");
                            String phoneNumber = entity.getString("phoneNumber");
                            if (phoneNumber!=null && phoneNumber.length() == 12){
                                log.info("--------------Valid Phone No.  Sending sms to "+phoneNumber+" -------------");
                                String name = acDetails.get().getAccountName();
                                Double tranAmount = partTran.getTransactionAmount();
                                String tranDate = transactionHeader.getTransactionDate().toString();
                                String tranCode = transactionHeader.getTransactionCode();
                                String message = "Hello "+name+", your account has been debited Ksh."+tranAmount+" Ref code:" +tranCode+" Date "+tranDate;
                                smsService.SMSNotification(message,phoneNumber);
                            }else {
                                log.info("--------------Invalid Phone No.  Not Sending sms to "+phoneNumber+" -------------");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {

        }
    }

    public void sendSMSNotification1(TransactionHeader transactionHeader) throws MessagingException, IOException {
        try {
            System.out.println("sendSMSNotification1(TransactionHeader transactionHeader)");
            String transactionCode = transactionHeader.getTransactionCode();

            List<PartTran> partTrans = transactionHeader.getPartTrans();
            //            TODO: Send Debit SMS
            List<PartTran> debits = partTrans.stream().filter(d->d.getPartTranType().equalsIgnoreCase(CONSTANTS.Debit)).collect(Collectors.toList());
            System.out.println("Found debit "+ debits);
            for (int i =0; i<debits.size(); i++) {
                System.out.println("In Dedit");
                PartTran partTran = debits.get(i);

                if(partTran.getParttranIdentity().equalsIgnoreCase("Normal")) {
                    String acid = partTran.getAcid();
                    System.out.println("Found normal");
///                Send sms to only customer accounts
                    Optional<AccountRepository.AcTransactionDetails> acDetails = accountRepository.getAccountTransactionDetails(acid);
                    if (acDetails.isPresent() &&  acDetails.get().getAccountType().equalsIgnoreCase(CONSTANTS.SAVINGS_ACCOUNT)) {
                        log.info("account is not an office account");
                        Optional<AccountRepository.CustomerCode> customerCodeCheck = accountRepository.getCustomerCodeByAcidAndDeletedFlag(acid);
                        if (customerCodeCheck.isPresent()) {
                            log.info("customer code is present");
                            String customerCode = customerCodeCheck.get().getCustomer_code();
                            String userName = UserRequestContext.getCurrentUser();
                            String entityId = EntityRequestContext.getCurrentEntityId();
                            EntityResponse response = serviceCaller.getCustomerPhone_and_Email(customerCode,userName,entityId);
                            if (response.getStatusCode() == HttpStatus.OK.value()) {
                                System.out.println("Found phone");
                                JSONObject jo = new JSONObject(response);
                                JSONObject entity = jo.getJSONObject("entity");
                                String phoneNumber = entity.getString("phoneNumber");
                                if (phoneNumber!=null && phoneNumber.length() == 12) {
                                    log.info("--------------Debit Valid Phone No.  Sending sms to "+phoneNumber+" -------------");
                                    String name = acDetails.get().getAccountName();
                                    name = name.contains(" ") ? name.split(" ")[0] : name;
                                    Double tranAmount = partTran.getTransactionAmount();
                                    Date tranDate = transactionHeader.getTransactionDate();
                                    String tranCode = transactionHeader.getTransactionCode();
                                    String debitCredit = partTran.getPartTranType();

                                    String message=null;
                                    if(transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.CASH_WITHDRAWAL)) {
//                                        message="Dear "+name+", We are pleased to inform you that your recent WITHDRAWAL request of Ksh "+tranAmount+" from your  "+acid+" account has been successfully processed on "+tranDate+". Ref code: "+tranCode+". Thank you";
                                        message="Dear "+name+",you have withdrawn Ksh."+tranAmount+" Ref code:"+tranCode+" Date "+datesCalculator.fullDateFormat(new Date());
                                    } else if(transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.PAYBILL_WITHDRAWAL)) {
//                                        message="Dear "+name+", We are pleased to inform you that your recent WITHDRAWAL request of Ksh "+tranAmount+" from your  "+acid+" account has been successfully processed on "+tranDate+". Ref code: "+tranCode+". Thank you";
                                        message="Dear "+name+",you have withdrawn Ksh."+tranAmount+" via paybill. Ref code:"+tranCode+" Date "+datesCalculator.fullDateFormat(new Date());
                                    } else if(transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.TRANSFER)) {
                                        String creditAccount = getCorrespondingCrAcid(transactionHeader);
                                        message= "Dear "+name+" You have transfered ksh "+tranAmount+" from your account "+acid+" to "+creditAccount+" on "+datesCalculator.fullDateFormat(new Date());
                                    } else if (transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.BATCH_UPLOAD)) {
                                        String creditAccount = getCorrespondingCrAcid(transactionHeader);
//                                        message = "Dear "+name+", your account has been debited Ksh."+tranAmount+" Ref code:" +tranCode+" Date "+tranDate;
                                        message="Dear "+name+", your account has been withdrawn via batch Ksh."+tranAmount+" Ref code:"+tranCode+" Date "+datesCalculator.fullDateFormat(new Date());
                                    } else if (!transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.REVERSE_TRANSACTIONS)
                                    && !transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.SYSTEM_REVERSAL)
                                            && !transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.SYSTEM)) {
                                        message = "Dear "+name+", your account has been debited Ksh."+tranAmount+" Ref code:" +tranCode+" Date "+tranDate;
                                    }
                                    System.out.println("Debit, SMS Message: "+message);


                                    assert (message !=null);

                                    SmsDto smsDto= new SmsDto();
                                    smsDto.setMsisdn(phoneNumber);
                                    smsDto.setText(message);

                                    EntityResponse response1 = serviceCaller.sendSmsEmt(smsDto);

                                    if (response1.getStatusCode() == HttpStatus.OK.value()) {
                                        System.out.println("Initiated Notification Charges on Debit");
                                        // initiate notification charges
                                        String TransactionHeaderId = String.valueOf(transactionHeader.getSn());
                                        System.out.println("Tran Header ID: "+TransactionHeaderId);
                                        String Acid = tranHeaderRepository.getAcid(TransactionHeaderId);
                                        System.out.println("Acid: "+Acid);

                                        serviceNotification.smsCharges(Acid);


                                    } else {
                                        System.out.println("Failed to initiate notification charges. Status code: " + response1.getStatusCode());
                                    }

                                } else {
                                    log.info("--------------Invalid Phone No.  Not Sending sms to "+phoneNumber+" -------------");
                                }
                            }
                        }
                    }
                }

            }
            //            TODO: Send Cr SMS
            List<PartTran> credits = partTrans.stream().filter(d->d.getPartTranType().equalsIgnoreCase(CONSTANTS.Credit)).collect(Collectors.toList());
            for (int i =0; i<credits.size(); i++) {
                System.out.println("Found credit");
                PartTran partTran = credits.get(i);
                String acid = partTran.getAcid();


///                Send sms to only customer accounts
                Optional<AccountRepository.AcTransactionDetails> acDetails = accountRepository.getAccountTransactionDetails(acid);
                if (acDetails.isPresent() && acDetails.get().getAccountType().equalsIgnoreCase(CONSTANTS.SAVINGS_ACCOUNT)) {
                    System.out.println("Not office account");
                    Optional<AccountRepository.CustomerCode> customerCodeCheck = accountRepository.getCustomerCodeByAcidAndDeletedFlag(acid);
                    if (customerCodeCheck.isPresent()) {
                        System.out.println("Customer check success");
                        String customerCode = customerCodeCheck.get().getCustomer_code();
                        String userName = UserRequestContext.getCurrentUser();
                        String entityId = EntityRequestContext.getCurrentEntityId();
                        EntityResponse response = serviceCaller.getCustomerPhone_and_Email(customerCode,userName,entityId);
                        if (response.getStatusCode() == HttpStatus.OK.value()) {
                            System.out.println("Found customer");
                            JSONObject jo = new JSONObject(response);
                            JSONObject entity = jo.getJSONObject("entity");
                            String phoneNumber = entity.getString("phoneNumber");
                            if (phoneNumber!=null && phoneNumber.length() == 12) {
                                log.info("--------------Credit Valid Phone No.  Sending sms to "+phoneNumber+" -------------");
                                String name = acDetails.get().getAccountName();
                                name = name.contains(" ") ? name.split(" ")[0] : name;
                                System.out.println("name = name.contains(\" \") ? name.split(\" \")[0] : name;");
                                Double tranAmount = partTran.getTransactionAmount();
                                System.out.println("Double tranAmount = partTran.getTransactionAmount();");
                                String tranDate = transactionHeader.getTransactionDate().toString();
                                System.out.println("String tranDate = transactionHeader.getTransactionDate().toString();");
                                String tranCode = transactionHeader.getTransactionCode();
                                System.out.println("String tranCode = transactionHeader.getTransactionCode();");
                                String message=null;
                                System.out.println("Tran Type: "+transactionHeader.getTransactionType());
                                if(transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.CASH_DEPOSIT)) {
                                    System.out.println("cash depo");
                                    String date = datesCalculator.fullDateFormat(new Date());
                                    System.out.println(date);
                                    message="Dear "+name+", Ksh."+tranAmount+" has been deposited into your account. Ref code:"+tranCode+" Date "+date;
                                    System.out.println(message);
                                } else if(transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.BATCH_UPLOAD)) {
                                    System.out.println("batch deposit");
                                    String date = datesCalculator.fullDateFormat(new Date());
                                    System.out.println(date);
                                    message="Dear "+name+", Ksh."+tranAmount+" has been deposited into your account. Batch Ref code:"+tranCode+" Date "+date;
                                    System.out.println(message);
                                }   else if(transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.TRANSFER)) {
                                    String debitAcid=getCorrespondingDrAcid(transactionHeader);
                                     message= "Dear "+name+",  You have received ksh "+tranAmount+" from account "+debitAcid+" on "+datesCalculator.fullDateFormat(new Date());


                                    List<PartTran> debitss = partTrans.stream().filter(d->d.getPartTranType().equalsIgnoreCase(CONSTANTS.Debit)).collect(Collectors.toList());
                                    for (int l =0; l<debitss.size(); l++) {
                                        log.info("the size is :"+debitss.size());
                                        PartTran partTranss = debitss.get(i);
                                        String CreditAcid = partTranss.getAcid();
                                        Optional<AccountRepository.AcTransactionDetails> AcDetails = accountRepository.getAccountTransactionDetails(CreditAcid);
                                        if (acDetails.isPresent() && !AcDetails.get().getAccountType().equalsIgnoreCase(CONSTANTS.OFFICE_ACCOUNT)){
                                            Optional<AccountRepository.CustomerCode> CustomerCodeCheck = accountRepository.getCustomerCodeByAcidAndDeletedFlag(CreditAcid);
                                            if (CustomerCodeCheck.isPresent()){
                                                String CustomerCode = CustomerCodeCheck.get().getCustomer_code();
                                                String UserName = UserRequestContext.getCurrentUser();
                                                String EntityId = EntityRequestContext.getCurrentEntityId();
                                                log.info("EntityId " + EntityId);
                                                EntityResponse Response = serviceCaller.getCustomerPhone_and_Email(CustomerCode,UserName,EntityId);
                                                if (Response.getStatusCode() == HttpStatus.OK.value()){
                                                    JSONObject Jo = new JSONObject(Response);
                                                    JSONObject Entity = Jo.getJSONObject("entity");
                                                    String PhoneNumber = Entity.getString("phoneNumber");
                                                    if (PhoneNumber!=null && PhoneNumber.length() == 12){
                                                        log.info("--------------Valid Phone No.  Sending sms to "+PhoneNumber+" -------------");
                                                        String Name = AcDetails.get().getAccountName();
                                                        Double TranAmount = partTran.getTransactionAmount();
                                                        String TranDate = transactionHeader.getTransactionDate().toString();
                                                        String TranCode = transactionHeader.getTransactionCode();
                                                        String Message= "Dear "+Name+" You have transfered ksh "+TranAmount+" from your account "+CreditAcid+" to "+acid+" on "+datesCalculator.fullDateFormat(new Date());

                                                        SmsDto smsDto= new SmsDto();
                                                        smsDto.setMsisdn(PhoneNumber);
                                                        smsDto.setText(Message);
                                                        EntityResponse response2 = serviceCaller.sendSmsEmt(smsDto);

                                                        if (response2.getStatusCode() == HttpStatus.OK.value()) {
                                                            System.out.println("Initiated Notification Charges on Debit");
                                                            // initiate notification charges
                                                            String TransactionHeaderId = String.valueOf(transactionHeader.getSn());
                                                            System.out.println("Tran Header ID: "+TransactionHeaderId);
                                                            String Acid = tranHeaderRepository.getAcid(TransactionHeaderId);
                                                            System.out.println("Acid1: "+Acid);

                                                            serviceNotification.smsCharges(Acid);


                                                        } else {
                                                            System.out.println("Failed to initiate 2 notification charges. Status code: " + response2.getStatusCode());
                                                        }
                                                    }else {
                                                        log.info("--------------Invalid Phone No.  Not Sending sms to "+PhoneNumber+" -------------");
                                                    }
                                                }
                                            }
                                        }
                                    }


                                } else if (transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.SALARY_UPLOAD)) {
                                    String debitAcid=getCorrespondingDrAcid(transactionHeader);
                                     message=  "Dear "+name+",  You have received ksh "+tranAmount+" being salary payment on "+datesCalculator.fullDateFormat(new Date());
                                } else if (!transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.REVERSE_TRANSACTIONS)
                                        && !transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.SYSTEM_REVERSAL)
                                        && !transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.SYSTEM)) {
                                    message = "Dear "+name+", your account has been credited Ksh."+tranAmount+" Ref code:" +tranCode+" Date "+tranDate;
                                }

                                System.out.println("Credit, SMS Message: "+message);
//                                String message = "Hello "+name+", your account has been credited Ksh."+tranAmount+" Ref code:" +tranCode+" Date "+tranDate;
//                                smsService.SMSNotification(message,phoneNumber);

                                assert (message !=null);
                                SmsDto smsDto= new SmsDto();
                                smsDto.setMsisdn(phoneNumber);
                                smsDto.setText(message);

                                EntityResponse response1 = serviceCaller.sendSmsEmt(smsDto);

                                if (response1.getStatusCode() == HttpStatus.OK.value()) {
                                    System.out.println("Initiated Notification Charges on Credit");
                                    // initiate notification charges
                                    String TransactionHeaderId = String.valueOf(transactionHeader.getSn());
                                    System.out.println("Tran Header ID: "+TransactionHeaderId);
                                    String Acid = tranHeaderRepository.getAcidCredit(TransactionHeaderId);
                                    System.out.println("Acid 2: "+Acid);

                                    serviceNotification.smsCharges(Acid);


                                } else {
                                    System.out.println("Failed to initiate notification charges. Status code: " + response1.getStatusCode());
                                }

                            } else {
                                log.info("--------------Invalid Phone No.  Not Sending sms to "+phoneNumber+" -------------");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getCorrespondingDrAcid(TransactionHeader transactionHeader){
        return getCorresponding(transactionHeader, "Debit");
    }

    public String getCorrespondingCrAcid(TransactionHeader transactionHeader){
        return getCorresponding(transactionHeader, "Credit");
    }

    public String getCorresponding(TransactionHeader transactionHeader, String tranType) {
        try{
            System.out.println(tranType);
            PartTran p = transactionHeader.getPartTrans().stream().filter(partTran -> partTran.getPartTranType().equalsIgnoreCase(tranType)).findAny().orElse(null);
            if (p == null)
                System.out.println("");
            return p.getAcid();
        } catch (Exception e) {
            System.out.println("Error in getCorresponding(TransactionHeader transactionHeader, String tranType){");
            System.out.println(e.getStackTrace());
            return null;
        }
    }

//    public void sendSMSNotification(TransactionHeader transactionHeader) throws MessagingException, IOException {
//        try {
//            System.out.println("Sending sms");
//            String transactionCode = transactionHeader.getTransactionCode();
//
//            List<PartTran> partTrans = transactionHeader.getPartTrans();
//            //            TODO: Send Debit SMS
//            List<PartTran> debits = partTrans.stream().filter(d->d.getPartTranType().equalsIgnoreCase(CONSTANTS.Debit)).collect(Collectors.toList());
//            Map<String, Double> minifiedDebits  = debits.stream().collect(Collectors.groupingBy(d->d.getAcid(),Collectors.summingDouble(d->d.getTransactionAmount())));
//
//            for (String key : minifiedDebits.keySet()) {
//                Double debitBal = minifiedDebits.get(key);
//                String acid = key;
////                Send sms to only customer accounts
//                Optional<AccountRepository.AcTransactionDetails> acDetails = accountRepository.getAccountTransactionDetails(acid);
//                if (acDetails.isPresent() && !acDetails.get().getAccountType().equalsIgnoreCase(CONSTANTS.OFFICE_ACCOUNT)){
//                    Optional<AccountRepository.CustomerCode> customerCodeCheck = accountRepository.getCustomerCodeByAcidAndDeletedFlag(acid);
//                    if (customerCodeCheck.isPresent()){
//                        String customerCode = customerCodeCheck.get().getCustomer_code();
//                        String userName = UserRequestContext.getCurrentUser();
//                        String entityId = EntityRequestContext.getCurrentEntityId();
////                    EntityResponse response = serviceCaller.getCustomerPhoneAndEmail(customerCode,userName,entityId);
////                    if (response.getStatusCode().equals(HttpStatus.)) String to, String subject, String message)
//                        String toPhone = "";
//                        String debitName = acDetails.get().getAccountName();
//                        Double tranAmount = debitBal;
//                        String creditAccount = "";
//                        String nameOfCredited = "";
//                        Date tranDate =
////                        String tranParticulars = transactionHeader.getTra
//                        String tranCode = transactionCode;
//                        String message =
//
////                                "Hello "+debitName+", your account has been debited Ksh."+tranAmount+" Ref code: MAKSAKSAKSA Date 23-23-2020
////
//                                "Your transaction of Ksh." +tranAmount+ " has been credited to "+creditAccount+" Name" +nameOfCredited+
//                                        " ON "+tranDate+
//                                        " Trancode: "+tranCode+
//                                        " Thank you for working with us.";
//
////                        String message =
////                                "Your transaction of Ksh." +tranAmount+ " has been credited to "+creditAccount+" Name" +nameOfCredited+
////                                        " ON "+tranDate+
////                                        " Trancode: "+tranCode+
////                                " Thank you for working with us.";
//                        smsService.sendSMS(message,toPhone);
//
//                        System.out.println(toPhone);
//                        System.out.println(message);
//                        System.out.println("--------------Sending sms to "+debitName+" -------------");
//                    }
//                }
//            }
//
//            //            TODO: Send Credit SMS
//            List<PartTran> credits = partTrans.stream().filter(d->d.getPartTranType().equalsIgnoreCase(CONSTANTS.Credit)).collect(Collectors.toList());
//            Map<String, Double> minifiedCredits  = credits.stream().collect(Collectors.groupingBy(d->d.getAcid(),Collectors.summingDouble(d->d.getTransactionAmount())));
//
//            for (String key : minifiedCredits.keySet()) {
//                Double creditBal = minifiedCredits.get(key);
//                String acid = key;
////                Send sms to only customer accounts
//                Optional<AccountRepository.AcTransactionDetails> acDetails = accountRepository.getAccountTransactionDetails(acid);
//                if (acDetails.isPresent() && !acDetails.get().getAccountType().equalsIgnoreCase(CONSTANTS.OFFICE_ACCOUNT)){
//                    Optional<AccountRepository.CustomerCode> customerCodeCheck = accountRepository.getCustomerCodeByAcidAndDeletedFlag(acid);
//                    if (customerCodeCheck.isPresent()){
//                        String customerCode = customerCodeCheck.get().getCustomer_code();
//                        String userName = UserRequestContext.getCurrentUser();
//                        String entityId = EntityRequestContext.getCurrentEntityId();
////                    EntityResponse response = serviceCaller.getCustomerPhoneAndEmail(customerCode,userName,entityId);
////                    if (response.getStatusCode().equals(HttpStatus.)) String to, String subject, String message)
//                        String toPhone = "254726634786";
//                        String debitName = "";
//                        Double tranAmount = creditBal;
//                        String creditAccount = acid;
//                        String nameOfCredited = "";
//                        Date tranDate = null;
//                        String tranCode = null;
//                        String message =
//                                "Hello " + debitName +
//                                        "Your transaction of Ksh." +tranAmount+ "has been credited to "+creditAccount+" Name" +nameOfCredited+
//                                        " ON "+tranDate+
//                                        " Trancode: "+tranCode+
//                                        " Thank you for working with us.";
//                        smsService.SMSNotification(message,toPhone);
//                        System.out.println("--------------Sending sms to "+debitName+" -------------");
//                    }
//                }
//            }
//
//
//
//        } catch (Exception e) {
//        }
//    }
    public void sendNotification(TransactionHeader transactionHeader) throws MessagingException, IOException {
//        TODO: GET TRANSACTION TYPE
//        SEND CASH DEPOSIT
        if(transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.CASH_DEPOSIT)){
            cashDepostDebitNotification(transactionHeader.getTransactionCode(), transactionHeader.getTransactionDate(), transactionHeader.getPartTrans());
        }
//        SEND CASH WITHDRAWAL
        if(transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.CASH_WITHDRAWAL)){
            cashWithdrawalNotification(transactionHeader.getTransactionCode(), transactionHeader.getTransactionDate(), transactionHeader.getPartTrans());
        }
//        TRANSFER
        if(transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.TRANSFER)){
            transferNotification(transactionHeader.getTransactionCode(), transactionHeader.getTransactionDate(), transactionHeader.getPartTrans());
        }
//        SEND TRANSFER
    }

    ////    TODO: GET CUSTOMERCODE
    public void cashDepostDebitNotification(String tranId, Date tranDate, List<PartTran> partTrans) throws IOException, MessagingException {
        String debitAc = null;
        List<PartTran> debits = (List<PartTran>) partTrans.stream().filter(f -> f.getPartTranType().equalsIgnoreCase(CONSTANTS.Debit));
        for (int j = 0; j<debits.size(); j++){
            debitAc = debits.get(0).getAcid();
        }
        List<PartTran> credits = (List<PartTran>) partTrans.stream().filter(f -> f.getPartTranType().equalsIgnoreCase(CONSTANTS.Credit));
        log.info("----------------------- Found "+credits.size() +" accounts credited ---------------------------");
        for (int i=0; i<credits.size(); i++){
//            check each credit account if it accepts email notification
            Optional<AccountRepository.ReceiveEmailNotifications> checkAcEmail = accountRepository.getReceiveEmailNotificationsByAcidAndDeletedFlag(credits.get(i).getAcid());
//            if account accept email get customer code
            if (checkAcEmail.isPresent()){
//                TODO:Send Email
                if (checkAcEmail.get().getReceive_email_notifications() == true){
//                TODO: SEND email
                    EntityResponse response = serviceCaller.getCustomerPhone_and_Email(checkAcEmail.get().getCustomer_code(),"System", "001");
                    if (response.getStatusCode() == 200){
                        EntityResponse<ContactInfo> contactInfo = (EntityResponse<ContactInfo>) response.getEntity();
                        if (contactInfo.getEntity().getEmailAddress()==null){
                        }else{
                            /*
                             * Debited Ac Name | Debited Ac No | Amount | Credited Ac Name | Credited Ac No | Amount | TranID | TranDate |
                             * */
                            String debitAcName = accountRepository.findAccountNameByAcid(debitAc).orElse("No A/c Name");
                            String creditAcName = accountRepository.findAccountNameByAcid(credits.get(i).getAcid()).orElse("No A/c Name");
                            List<String> strRowData = new ArrayList<>();
                                String tr =
                                        "  <tr>\n" +
                                                "    <td>"+ debitAcName +"</td>\n" +
                                                "    <td>"+ debitAc +"</td>\n" +
                                                "    <td>"+ credits.get(i).getTransactionAmount() +"</td>\n" +
                                                "    <td>"+ creditAcName +"</td>\n" +
                                                "    <td>"+ credits.get(i).getAcid() +"</td>\n" +
                                                "    <td>"+ credits.get(i).getTransactionAmount() +"</td>\n" +
                                                "    <td>"+ tranId +"</td>\n" +
                                                "    <td>"+ tranDate +"</td>\n" +
                                                "  </tr>\n";
                                strRowData.add(tr);
                            String message = "<table>\n" +
                                    "  <tr>\n" +
                                    "    <th>Debit from A/c Name</th>\n" +
                                    "    <th>Debit from A/c No</th>\n" +
                                    "    <th>Debit Amount</th>\n" +
                                    "    <th>Credit to A/c Name</th>\n" +
                                    "    <th>Credit to A/c No</th>\n" +
                                    "    <th>Credited Amount</th>\n" +
                                    "    <th>TranID</th>\n" +
                                    "    <th>TranDate</th>\n" +
                                    "  </tr>\n" +
                                    strRowData +
                                    "</table>";
                            String toEmail = contactInfo.getEntity().getEmailAddress();
                            String subject = "Transaction Min Statement";
//                            Save to db
                            Transactionnotification transactionnotification = new Transactionnotification();
                            transactionnotification.setDebitedAcName(debitAcName);
                            transactionnotification.setDebitedAcNo(debitAc);
                            transactionnotification.setCreditedAcName(creditAcName);
                            transactionnotification.setCreditedAcNo(credits.get(i).getAcid());
                            transactionnotification.setAmount(credits.get(i).getTransactionAmount());
                            transactionnotification.setTransactionId(tranId);
                            transactionnotification.setTransactionDate(tranDate);
                            transactionnotification.setStatus("Pending");
                            transactionnotification.setSentFlag('N');
                            transactionnotification.setSentTime(null);
                            mailService.sendEmail("coullence@gmail.com",subject,message);
                            boolean result = true;
                            try {
                                InternetAddress emailAddr = new InternetAddress(toEmail);
                                emailAddr.validate();
                            } catch (AddressException ex) {
                                result = false;
                            }
                            if (result = true){
                                transactionnotification.setStatus("Successful");
                                transactionnotification.setSentFlag('Y');
                                transactionnotification.setSentTime(new Date());
                            }
                            transactionnotificationRepo.save(transactionnotification);
                        }
                    }
                }
            }
        }
    }

    ////    TODO: GET CUSTOMERCODE
    public void cashWithdrawalNotification(String tranId, Date tranDate, List<PartTran> partTrans) throws IOException, MessagingException {
        String debitAc = null;
        Double debitAmount = 0.00;
        List<PartTran> debits = (List<PartTran>) partTrans.stream().filter(f -> f.getPartTranType().equalsIgnoreCase(CONSTANTS.Debit));
        for (int j = 0; j<debits.size(); j++){
            debitAc = debits.get(0).getAcid();
            debitAmount = debits.get(0).getTransactionAmount();
        }
        String creditAc = null;
        Double creditAmount = 0.00;
        List<PartTran> credits = (List<PartTran>) partTrans.stream().filter(f -> f.getPartTranType().equalsIgnoreCase(CONSTANTS.Credit));
        for (int j = 0; j<credits.size(); j++){
            creditAc = credits.get(0).getAcid();
            creditAmount = credits.get(0).getTransactionAmount();
        }
        //            check each credit account if it accepts email notification
            Optional<AccountRepository.ReceiveEmailNotifications> checkAcEmail = accountRepository.getReceiveEmailNotificationsByAcidAndDeletedFlag(debitAc);
//            if account accept email get customer code
            if (checkAcEmail.isPresent()){
//                TODO:Send Email
                if (checkAcEmail.get().getReceive_email_notifications() == true){
//                TODO: SEND email
                    EntityResponse response = serviceCaller.getCustomerPhone_and_Email(checkAcEmail.get().getCustomer_code(),"System", "001");
                    if (response.getStatusCode() == 200){
                        EntityResponse<ContactInfo> contactInfo = (EntityResponse<ContactInfo>) response.getEntity();
                        if (contactInfo.getEntity().getEmailAddress()==null){
                        }else{
                            /*
                             *
                             * Debited Ac Name | Debited Ac No | Amount | Credited Ac Name | Credited Ac No | Amount | TranID | TranDate |
                             *
                             * */
                            List<String> strRowData = new ArrayList<>();
                            String tr =
                                    "  <tr>\n" +
                                            "    <td>"+ accountRepository.findAccountNameByAcid(debitAc).orElse("No A/c Name")+"</td>\n" +
                                            "    <td>"+ debitAc +"</td>\n" +
                                            "    <td>"+ debitAmount +"</td>\n" +
                                            "    <td>"+ accountRepository.findAccountNameByAcid(creditAc).orElse("No A/c Name") +"</td>\n" +
                                            "    <td>"+ creditAc +"</td>\n" +
                                            "    <td>"+ creditAmount +"</td>\n" +
                                            "    <td>"+ tranId +"</td>\n" +
                                            "    <td>"+ tranDate +"</td>\n" +
                                            "  </tr>\n";
                            strRowData.add(tr);
                            String message = "<table>\n" +
                                    "  <tr>\n" +
                                    "    <th>Debit from A/c Name</th>\n" +
                                    "    <th>Debit from A/c No</th>\n" +
                                    "    <th>Debit Amount</th>\n" +
                                    "    <th>Credit to A/c Name</th>\n" +
                                    "    <th>Credit to A/c No</th>\n" +
                                    "    <th>Credited Amount</th>\n" +
                                    "    <th>TranID</th>\n" +
                                    "    <th>TranDate</th>\n" +
                                    "  </tr>\n" +
                                    strRowData +
                                    "</table>";
                            String toEmail = contactInfo.getEntity().getEmailAddress();
                            String subject = "Transaction Min Statement";
//                            mailService.sendEmail(toEmail,subject,message);
                            mailService.sendEmail("coullence@gmail.com",subject,message);

                        }
                    }
                }
            }
    }
    public void transferNotification(String tranId, Date tranDate, List<PartTran> partTrans) throws IOException, MessagingException {
        System.out.println("Trying to send mail ------------------------------------------");
//        Get Credits
//        For each of credits , send email if accept
//        You have been credited Ksh. 2000 for Debit office account on 27/02/2323.
//        Transaction ID:
//        Transaction Date:
//        Account balance:
//        Available balance:
//        Debit A credit B credit C
//        String creditAc = null;
//        Double creditAmount = 0.00;
        Stream<PartTran> credits =  partTrans.stream().filter(f -> f.getPartTranType().equalsIgnoreCase(CONSTANTS.Credit));
        System.out.println("After filter");
        System.out.println(credits);
        credits.forEach(partTran -> {
            String creditAc = partTran.getAcid();
            System.out.println("******************in Loop");
            //            check each credit account if it accepts email notification
            Optional<AccountRepository.ReceiveEmailNotifications> checkAcEmail = accountRepository.getReceiveEmailNotificationsByAcidAndDeletedFlag(creditAc);
            System.out.println("*******************after check mail");
            if (checkAcEmail.isPresent()){
//                TODO:Send Email
                if (checkAcEmail.get().getReceive_email_notifications() == true){
//                TODO: SEND email
                    EntityResponse response = null;
                    try {
                        System.out.println("Data for request ,+ "  + checkAcEmail.get().getCustomer_code());
                        response = serviceCaller.getCustomerPhone_and_Email(checkAcEmail.get().getCustomer_code(),"System", "001");
                        System.out.println("Response from customer");
                        System.out.println(response);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (response.getStatusCode() == 200){
                        EntityResponse<ContactInfo> contactInfo = (EntityResponse<ContactInfo>) response.getEntity();
                        if (contactInfo.getEntity().getEmailAddress()==null){
                            System.out.println("Null mail");
                        }else{
                            /*
                             * Debited Ac Name | Debited Ac No | Amount | Credited Ac Name | Credited Ac No | Amount | TranID | TranDate |
                             * */
                            Optional<AccountRepository.AcBalanceAndLien> accountBal = accountRepository.getAccountBalanceAndLien(creditAc);
                            Double availableBalance = accountBal.get().getAccountBalance() - accountBal.get().getLienAmount();
                            List<String> strRowData = new ArrayList<>();
                            String tr =
                                    "  <tr>\n" +
                                            "    <td>"+"Transaction Date: "+"</td>\n" +
                                            "    <td>"+ tranDate +"</td>\n" +
                                            "  </tr>\n"+
                                            "  <tr>\n" +
                                            "    <td>"+ "Transaction Code: " +"</td>\n" +
                                            "    <td>"+ tranId +"</td>\n" +
                                            "  </tr>\n" +
                                            "  <tr>\n" +
                                            "    <td>"+ "Account Balance: " +"</td>\n" +
                                            "    <td>"+ accountBal.get().getAccountBalance() +"</td>\n" +
                                            "  </tr>\n"+
                                            "  <tr>\n" +
                                            "    <td>"+ "Available Balance: " +"</td>\n" +
                                            "    <td>"+ availableBalance +"</td>\n" +
                                            "  </tr>\n";

                            strRowData.add(tr);
                            String message = "<table>\n" +
                                    "  Your account has been credited "+partTran.getTransactionAmount()+ "\n"+
                                    strRowData +
                                    "</table>";
                            String toEmail = contactInfo.getEntity().getEmailAddress();
                            String subject = "Transaction Min Statement";
//                            mailService.sendEmail(toEmail,subject,message);
                            try {
                                mailService.sendEmail("coullence@gmail.com",subject,message);
                            } catch (MessagingException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }
            }else {
                System.out.println("NOt present");
            }
        });
//        for (int j = 0; j<credits.size(); j++){
//            creditAc = credits.get(j).getAcid();
//
//            creditAmount = credits.get(j).getTransactionAmount();
//            //            check each credit account if it accepts email notification
//            Optional<AccountRepository.ReceiveEmailNotifications> checkAcEmail = accountRepository.getReceiveEmailNotificationsByAcidAndDeletedFlag(creditAc);
//            if (checkAcEmail.isPresent()){
////                TODO:Send Email
//                if (checkAcEmail.get().getReceive_email_notifications() == true){
////                TODO: SEND email
//                    EntityResponse response = serviceCaller.getCustomerPhoneAndEmail(checkAcEmail.get().getCustomer_code(),"System", "001");
//                    if (response.getStatusCode() == 200){
//                        EntityResponse<ContactInfo> contactInfo = (EntityResponse<ContactInfo>) response.getEntity();
//                        if (contactInfo.getEntity().getEmailAddress()==null){
//                        }else{
//                            /*
//                             * Debited Ac Name | Debited Ac No | Amount | Credited Ac Name | Credited Ac No | Amount | TranID | TranDate |
//                             * */
//                            Optional<AccountRepository.AcBalanceAndLien> accountBal = accountRepository.getAccountBalanceAndLien(creditAc);
//                            Double availableBalance = accountBal.get().getAccountBalance() - accountBal.get().getLienAmount();
//                            List<String> strRowData = new ArrayList<>();
//
//                            String tr =
//                                            "  <tr>\n" +
//                                            "    <td>"+"Transaction Date: "+"</td>\n" +
//                                            "    <td>"+ tranDate +"</td>\n" +
//                                            "  </tr>\n"+
//                                            "  <tr>\n" +
//                                            "    <td>"+ "Transaction Code: " +"</td>\n" +
//                                            "    <td>"+ tranId +"</td>\n" +
//                                            "  </tr>\n" +
//                                            "  <tr>\n" +
//                                            "    <td>"+ "Account Balance: " +"</td>\n" +
//                                            "    <td>"+ accountBal.get().getAccountBalance() +"</td>\n" +
//                                            "  </tr>\n"+
//                                            "  <tr>\n" +
//                                            "    <td>"+ "Available Balance: " +"</td>\n" +
//                                            "    <td>"+ availableBalance +"</td>\n" +
//                                            "  </tr>\n";
//
//                            strRowData.add(tr);
//                            String message = "<table>\n" +
//                                    "  Your account has been credited "+credits.get(j).getTransactionAmount()+ "\n"+
//                                    strRowData +
//                                    "</table>";
//                            String toEmail = contactInfo.getEntity().getEmailAddress();
//                            String subject = "Transaction Min Statement";
////                            mailService.sendEmail(toEmail,subject,message);
//                            mailService.sendEmail("coullence@gmail.com",subject,message);
//
//                        }
//                    }
//                }
//            }
//
//        }
//




        String debitAc = null;
        Double debitAmount = 0.00;
        List<PartTran> debits = (List<PartTran>) partTrans.stream().filter(f -> f.getPartTranType().equalsIgnoreCase(CONSTANTS.Debit));
        for (int j = 0; j<debits.size(); j++){
            debitAc = debits.get(0).getAcid();
            debitAmount = debits.get(0).getTransactionAmount();
        }
//        String creditAc = null;
//        Double creditAmount = 0.00;
//        List<PartTran> credits = (List<PartTran>) partTrans.stream().filter(f -> f.getPartTranType().equalsIgnoreCase(CONSTANTS.Credit));
//        for (int j = 0; j<credits.size(); j++){
//            creditAc = credits.get(0).getAcid();
//            creditAmount = credits.get(0).getTransactionAmount();
//        }
//        //            check each credit account if it accepts email notification
//        Optional<AccountRepository.ReceiveEmailNotifications> checkAcEmail = accountRepository.getReceiveEmailNotificationsByAcidAndDeletedFlag(debitAc);
////            if account accept email get customer code
//        if (checkAcEmail.isPresent()){
////                TODO:Send Email
//            if (checkAcEmail.get().getReceive_email_notifications() == true){
////                TODO: SEND email
//                EntityResponse response = serviceCaller.getCustomerPhoneAndEmail(checkAcEmail.get().getCustomer_code(),"System", "001");
//                if (response.getStatusCode() == 200){
//                    EntityResponse<ContactInfo> contactInfo = (EntityResponse<ContactInfo>) response.getEntity();
//                    if (contactInfo.getEntity().getEmailAddress()==null){
//                    }else{
//                        /*
//                         * Debited Ac Name | Debited Ac No | Amount | Credited Ac Name | Credited Ac No | Amount | TranID | TranDate |
//                         * */
//                        List<String> strRowData = new ArrayList<>();
//                        String tr =
//                                "  <tr>\n" +
//                                        "    <td>"+ accountRepository.findAccountNameByAcid(debitAc).orElse("No A/c Name")+"</td>\n" +
//                                        "    <td>"+ debitAc +"</td>\n" +
//                                        "    <td>"+ debitAmount +"</td>\n" +
//                                        "    <td>"+ accountRepository.findAccountNameByAcid(creditAc).orElse("No A/c Name") +"</td>\n" +
//                                        "    <td>"+ creditAc +"</td>\n" +
//                                        "    <td>"+ creditAmount +"</td>\n" +
//                                        "    <td>"+ tranId +"</td>\n" +
//                                        "    <td>"+ tranDate +"</td>\n" +
//                                        "  </tr>\n";
//                        strRowData.add(tr);
//                        String message = "<table>\n" +
//                                "  <tr>\n" +
//                                "    <th>Debit from A/c Name</th>\n" +
//                                "    <th>Debit from A/c No</th>\n" +
//                                "    <th>Debit Amount</th>\n" +
//                                "    <th>Credit to A/c Name</th>\n" +
//                                "    <th>Credit to A/c No</th>\n" +
//                                "    <th>Credited Amount</th>\n" +
//                                "    <th>TranID</th>\n" +
//                                "    <th>TranDate</th>\n" +
//                                "  </tr>\n" +
//                                strRowData +
//                                "</table>";
//                        String toEmail = contactInfo.getEntity().getEmailAddress();
//                        String subject = "Transaction Min Statement";
////                            mailService.sendEmail(toEmail,subject,message);
//                        mailService.sendEmail("coullence@gmail.com",subject,message);
//
//                    }
//                }
//            }
//        }
    }


////    TODO: GET CUSTOMERCODE
//    public void sendDebitNotification(String tranId, Date tranDate, List<PartTran> credits) throws IOException, MessagingException {
//        Optional<AccountRepository.CustomerCode>  accountCustCode = accountRepository.getCustomerCodeByAcidAndDeletedFlag(debitAc);
//        if (accountCustCode.isPresent()){
//            String customerCode = accountCustCode.get().getCustomer_code();
//            Optional<AccountRepository.ReceiveEmailNotifications> checkAcEmail = accountRepository.getReceiveEmailNotificationsByAcidAndDeletedFlag(debitAc);
////               TODO:Send Email
//            if (checkAcEmail.get().getReceive_email_notifications() == true){
////                TODO: SEND email
//                EntityResponse response = serviceCaller.getCustomerPhoneAndEmail(customerCode,"System", "001");
//                    if (response.getStatusCode() == 200){
//                        EntityResponse<ContactInfo> contactInfo = (EntityResponse<ContactInfo>) response.getEntity();
//                        if (contactInfo.getEntity().getEmailAddress()==null){
//                        }else{
//                            /*
//                             * Debited Ac Name | Debited Ac No | Amount | Credited Ac Name | Credited Ac No | Amount | TranID | TranDate |
//                             * */
//                            List<String> strRowData = new ArrayList<>();
//                            for (int i =0; i<credits.size(); i++){
//                                String tr =
//                                        "  <tr>\n" +
//                                                "    <td>"+ accountRepository.findAccountNameByAcid(debitAc).orElse("No A/c Name")+"</td>\n" +
//                                                "    <td>"+ debitAc +"</td>\n" +
//                                                "    <td>"+ debitAmount +"</td>\n" +
//                                                "    <td>"+ accountRepository.findAccountNameByAcid(credits.get(i).getAcid()).orElse("No A/c Name") +"</td>\n" +
//                                                "    <td>"+ credits.get(i).getAcid() +"</td>\n" +
//                                                "    <td>"+ credits.get(i).getTransactionAmount() +"</td>\n" +
//                                                "    <td>"+ tranId +"</td>\n" +
//                                                "    <td>"+ tranDate +"</td>\n" +
//                                                "  </tr>\n";
//                                strRowData.add(tr);
//                            }
//                            String message = "<table>\n" +
//                                    "  <tr>\n" +
//                                    "    <th>Debit from A/c Name</th>\n" +
//                                    "    <th>Debit from A/c No</th>\n" +
//                                    "    <th>Debit Amount</th>\n" +
//                                    "    <th>Credit to A/c Name</th>\n" +
//                                    "    <th>Credit to A/c No</th>\n" +
//                                    "    <th>Credited Amount</th>\n" +
//                                    "    <th>TranID</th>\n" +
//                                    "    <th>TranDate</th>\n" +
//                                    "  </tr>\n" +
//                                    strRowData +
//                                    "</table>";
//                            String toEmail = contactInfo.getEntity().getEmailAddress();
//                            String subject = "Transaction Min Statement";
////                            mailService.sendEmail(toEmail,subject,message);
//                            mailService.sendEmail("coullence@gmail.com",subject,message);
//
//                        }
//                    }
//                }
//        }
//    }
    public void sendCreditNotification(String debitAc, Double debitAmount, String tranId, String tranDate, String creditAc, Double creditAmount) throws IOException, MessagingException {
        Optional<AccountRepository.CustomerCode>  accountCustCode = accountRepository.getCustomerCodeByAcidAndDeletedFlag(debitAc);
        if (accountCustCode.isPresent()){
            String customerCode = accountCustCode.get().getCustomer_code();
            Optional<AccountRepository.ReceiveEmailNotifications> checkAcEmail = accountRepository.getReceiveEmailNotificationsByAcidAndDeletedFlag(debitAc);
//               TODO:Send Email
            if (checkAcEmail.get().getReceive_email_notifications() == true){
//                TODO: SEND email
                EntityResponse response = serviceCaller.getCustomerPhone_and_Email(customerCode,"System", "001");
                if (response.getStatusCode() == 200){
                    EntityResponse<ContactInfo> contactInfo = (EntityResponse<ContactInfo>) response.getEntity();
                    if (contactInfo.getEntity().getEmailAddress()==null){
                    }else{
                        /*
                         * Debited Ac | Amount | Credited Ac | Amount | TranID | TranDate |
                         * */
                        List<String> strRowData = new ArrayList<>();
//                        for (int i =0; i<1; i++){
                            String tr =
                                    "  <tr>\n" +
                                            "    <td>"+ debitAc +"</td>\n" +
                                            "    <td>"+ debitAmount +"</td>\n" +
                                            "    <td>"+ creditAc +"</td>\n" +
                                            "    <td>"+ creditAmount +"</td>\n" +
                                            "    <td>"+ tranId +"</td>\n" +
                                            "    <td>"+ tranDate +"</td>\n" +
                                            "  </tr>\n";
                            strRowData.add(tr);
//                        }
                        String message = "<table>\n" +
                                "  <tr>\n" +
                                "    <th>Debit from A/c</th>\n" +
                                "    <th>Debit Amount</th>\n" +
                                "    <th>Credit to A/c</th>\n" +
                                "    <th>Credited Amount</th>\n" +
                                "    <th>TranID</th>\n" +
                                "    <th>TranDate</th>\n" +
                                "  </tr>\n" +
                                strRowData +
                                "</table>";
                        String toEmail = contactInfo.getEntity().getEmailAddress();
                        String subject = "Transaction Min Statement";
                        mailService.sendEmail(toEmail,subject,message);
                    }
                }
            }
        }
    }
    public void sendEmail(){
    }
    public void sendSms(){
    }
}
