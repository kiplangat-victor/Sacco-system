
package com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader;

import com.emtechhouse.accounts.KraStampedTransactions.Kraservice;
import com.emtechhouse.accounts.Models.Accounts.AccountClosure.AccountClosureService;
import com.emtechhouse.accounts.Models.Accounts.AccountRepository;
import com.emtechhouse.accounts.NotificationComponent.NotificationService;
import com.emtechhouse.accounts.NotificationComponent.SmsService.SMSService;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.PartTrans.PartTran;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.emtechhouse.accounts.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.accounts.Utils.HttpInterceptor.UserRequestContext;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

//import static com.emtechhouse.accounts.Utils.HttpInterceptor.EntityRequestContext.entityId;

@Service
@Slf4j
public class NewTransactionService {
    private final TranHeaderRepository tranHeaderRepository;
    private final TransactionProcessing transactionProcessing;
    private final AccountRepository accountRepository;
    private final NotificationService notificationService;
    private final SMSService smsService;
    @Autowired
    private Kraservice kraservice;

    @Autowired
    AccountClosureService accountClosureService;

    @Autowired
    private TransactionsController transactionsController;

    public NewTransactionService(TranHeaderRepository tranHeaderRepository, TransactionProcessing transactionProcessing, AccountRepository accountRepository, NotificationService notificationService, SMSService smsService) {
        this.tranHeaderRepository = tranHeaderRepository;
        this.transactionProcessing = transactionProcessing;
        this.accountRepository = accountRepository;
        this.notificationService = notificationService;
        this.smsService = smsService;
    }

    public static String generateRandomCode(int len) {
        String chars = "0123456789MI";
        Random rnd = new Random();
        String M = "M";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < 10; i++)
            sb.append(chars.charAt(rnd.nextInt(chars.length()))).toString();
        return M + sb;
    }

    public static String generatecSystemCode(int len) {
        String chars = "01234567890GOODWAY";
        Random rnd = new Random();
        String S = "S";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < 10; i++)
            sb.append(chars.charAt(rnd.nextInt(chars.length()))).toString();
        return S + sb;
    }

//    public TransactionHeader chargeFees(TransactionHeader transactionHeader) {
//
//    }


    public List<PartTran> setDates(List<PartTran> partTrans) {
        for (PartTran partTran: partTrans){
            partTran.setTransactionDate(new Date());
        }
        return partTrans;
    }
    //Enter
    //Enter
    public EntityResponse enter(TransactionHeader transactionHeader) {
        try {
            EntityResponse response = new EntityResponse();
            String user = UserRequestContext.getCurrentUser();
            String entityId = EntityRequestContext.getCurrentEntityId();
            response.setStatusCode(HttpStatus.OK.value());
            if (user.isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return response;
            } else if (entityId.isEmpty()) {
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return response;
            } else {
                transactionHeader.setTransactionDate(new Date());
                System.out.println(Arrays.deepToString(transactionHeader.getPartTrans().toArray()));
                setDates(transactionHeader.getPartTrans());
                System.out.println(Arrays.deepToString(transactionHeader.getPartTrans().toArray()));
                transactionHeader.setCurrency("KES");
                boolean chargeFee = false;
                if (transactionHeader.getPartTrans().get(0).getChargeFee() != null) {
                    chargeFee = transactionHeader.getPartTrans().get(0).getChargeFee() == 'Y';
                }
                response = transactionProcessing.validateParttrans(transactionHeader);
                System.out.println(response);
                if (response.getStatusCode() == HttpStatus.OK.value()) {
                    if (transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.CASH_WITHDRAWAL)
                            || transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.PAYBILL_WITHDRAWAL)
                            || transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.TRANSFER)
                    ) {
                        if (chargeFee) {
                            if (transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.CASH_WITHDRAWAL)) {
                                response = transactionProcessing.collectWithdrawalCharges(transactionHeader);
                            }else if (transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.PAYBILL_WITHDRAWAL)) {
                                response = transactionProcessing.collectPaybillWithdrawalCharges(transactionHeader);
                            }else if (transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.TRANSFER)) {
                                response = transactionProcessing.collectMoneyTransferCharges(transactionHeader);
                            }
                            List<PartTran> partTranList = transactionHeader.getPartTrans();
                            if (response.getStatusCode() == HttpStatus.FOUND.value()) {
                                System.out.println("Charges found");
                                JSONObject obj = new JSONObject(response);
                                JSONArray ja = obj.getJSONArray("entity");
                                for (Object o : ja) {
                                    JSONObject ob = new JSONObject(o.toString());
                                    String acid = ob.get("acid").toString();
                                    PartTran partTran1 = new PartTran();
                                    AccountRepository.AcTransactionDetails acDetailsDb = accountRepository.getAccountTransactionDetails(acid).get();
                                    partTran1.setPartTranType(ob.getString("partTranType"));
                                    partTran1.setTransactionAmount(ob.getDouble("transactionAmount"));
                                    partTran1.setAcid(acid);
                                    partTran1.setParttranIdentity(ob.getString("parttranIdentity"));
                                    partTran1.setCurrency(acDetailsDb.getCurrency());
                                    partTran1.setTransactionParticulars(ob.getString("transactionParticulars"));
                                    partTran1.setIsoFlag('N');
                                    partTran1.setExchangeRate(partTranList.get(0).getExchangeRate());
                                    partTran1.setTransactionDate(partTranList.get(0).getTransactionDate());
                                    partTran1.setAccountBalance(acDetailsDb.getAccountBalance());
                                    partTran1.setAccountType(acDetailsDb.getAccountType());
                                    partTran1.setIsWelfare(false);
                                    System.out.println(partTran1);
                                    partTranList.add(partTran1);
                                }
                            } else {
                                return response;
                            }
                            System.out.println("adding charges");
                            System.out.println(Arrays.deepToString(partTranList.toArray()));
                            transactionHeader.setPartTrans(partTranList);
                            System.out.println(Arrays.deepToString(transactionHeader.getPartTrans().toArray()));
                        }
                    }
                    //                        TODO: collect cheque charges
                    if (transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.CHEQUE_CLEARENCE)
                            || transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.CHEQUE_BOUNCE)) {

                        List<PartTran> partTranList = transactionHeader.getPartTrans();

                        if (transactionHeader.getChargeEventId() != null && !transactionHeader.getChargeEventId().trim().isEmpty()) {
                            response = transactionProcessing.collectChequeCharges(transactionHeader);

                            if (response.getStatusCode() == HttpStatus.FOUND.value()) {
                                JSONObject obj = new JSONObject(response);
                                JSONArray ja = obj.getJSONArray("entity");
                                for (Object o : ja) {
                                    JSONObject ob = new JSONObject(o.toString());
                                    String acid = ob.get("acid").toString();
                                    PartTran partTran1 = new PartTran();
                                    AccountRepository.AcTransactionDetails acDetailsDb = accountRepository.getAccountTransactionDetails(acid).get();
                                    partTran1.setPartTranType(ob.getString("partTranType"));
                                    partTran1.setTransactionAmount(ob.getDouble("transactionAmount"));
                                    partTran1.setAcid(acid);
                                    partTran1.setParttranIdentity(ob.getString("parttranIdentity"));
                                    partTran1.setCurrency(acDetailsDb.getCurrency());
                                    partTran1.setTransactionParticulars(ob.getString("transactionParticulars"));
                                    partTran1.setIsoFlag('N');
                                    partTran1.setExchangeRate(partTranList.get(0).getExchangeRate());
                                    partTran1.setTransactionDate(partTranList.get(0).getTransactionDate());
                                    partTran1.setAccountBalance(acDetailsDb.getAccountBalance());
                                    partTran1.setAccountType(acDetailsDb.getAccountType());
                                    partTran1.setIsWelfare(false);
                                    partTranList.add(partTran1);
                                }
                            } else {
                                return response;
                            }
                        }
                        transactionHeader.setPartTrans(partTranList);
                    }
                    System.out.println("Before attachTellerPartrans");
                    System.out.println(Arrays.deepToString(transactionHeader.getPartTrans().toArray()));
                    transactionHeader = transactionProcessing.attachTellerPartrans(transactionHeader);
                    System.out.println("After attachTellerPartrans");
                    System.out.println(Arrays.deepToString(transactionHeader.getPartTrans().toArray()));
                    transactionHeader.setEntityId(entityId);
                    transactionHeader.setEnteredBy(user);
                    transactionHeader.setEnteredFlag('Y');
                    transactionHeader.setEnteredTime(new Date());
                    System.out.println("Before validation");
                    System.out.println(Arrays.deepToString(transactionHeader.getPartTrans().toArray()));
                    response = transactionProcessing.validateTransaction(transactionHeader);
                    System.out.println("After validation");
                    System.out.println(Arrays.deepToString(transactionHeader.getPartTrans().toArray()));
                    if (response.getStatusCode() == HttpStatus.ACCEPTED.value()) {
                        System.out.println("Is within teller limits");
                        System.out.println(Arrays.deepToString(transactionHeader.getPartTrans().toArray()));
                        EntityResponse checkAcLimits = transactionProcessing.isTransactionWithinTellerLimits(transactionHeader);
                        if (checkAcLimits.getStatusCode() == HttpStatus.OK.value()) {
                            transactionHeader.setVerifiedFlag('Y');
                            transactionHeader.setVerifiedBy(user);
                            transactionHeader.setVerifiedTime(new Date());
                            transactionHeader.setVerifiedFlag_2('Y');
                            transactionHeader.setVerifiedBy_2(user);
                            transactionHeader.setVerifiedTime_2(new Date());
                        }
                        List<PartTran> newParttran = transactionProcessing.attachCurrencies(transactionHeader.getPartTrans());
                        System.out.println("Before currency attachements ");
                        System.out.println(Arrays.deepToString(newParttran.toArray()));
                        transactionHeader.setPartTrans(newParttran);
                        transactionHeader.setTransactionCode(generateRandomCode(6).toUpperCase());
                        response = transactionProcessing.accountDebitBalanceValidation(transactionHeader);
                        if (response.getStatusCode() == HttpStatus.ACCEPTED.value()) {
                            response.setMessage("Transaction Entered Successfully. Transaction code is " + transactionHeader.getTransactionCode());
                            response.setStatusCode(HttpStatus.CREATED.value());
                            transactionHeader.setStatus(CONSTANTS.ENTERED);
                            response.setEntity(tranHeaderRepository.save(transactionHeader));
                        } else {
                            response = response;
                        }
                    } else {
                        return response;
                    }
                    return response;
                } else {
                    response = response;
                }
            }
            return response;
        } catch (Exception e) {
            EntityResponse res = new EntityResponse<>();
            e.printStackTrace();
            res.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            res.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return res;
        }
    }


    public EntityResponse enterAccountActivationFee(TransactionHeader transactionHeader) {
        try {
            System.out.println("Inside enter transaction fuction, tranheader is: "+transactionHeader);
            EntityResponse response = new EntityResponse();
            String user = UserRequestContext.getCurrentUser();
            String entityId = EntityRequestContext.getCurrentEntityId();
            response.setStatusCode(HttpStatus.OK.value());
            if (user.isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return response;
            } else if (entityId.isEmpty()) {
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return response;
            } else {
                System.out.println("====================================");
                transactionHeader.setTransactionDate(new Date());
                //System.out.println(Arrays.deepToString(transactionHeader.getPartTrans().toArray()));
                //setDates(transactionHeader.getPartTrans());
                //System.out.println(Arrays.deepToString(transactionHeader.getPartTrans().toArray()));
                transactionHeader.setCurrency("KSH");
                boolean chargeFee = true;

//                if (transactionHeader.getPartTrans().get(0).getChargeFee() != null) {
//                    System.out.println("====================================1 ");
//                    chargeFee = transactionHeader.getPartTrans().get(0).getChargeFee() == 'Y';
//                }
                //chargeFee = transactionHeader.getPartTrans().get(0).getChargeFee() == 'Y';
//                response = transactionProcessing.validateParttrans(transactionHeader);
//                System.out.println(response);
                System.out.println("skipped validation");
                if (response.getStatusCode() == HttpStatus.OK.value()) {
                    if (transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.ACCOUNT_ACTIVATION)
                    ) {
                        if (chargeFee) {
                            if (transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.ACCOUNT_ACTIVATION)) {
                                response = transactionProcessing.collectAccountActivationCharges(transactionHeader);
                            }
                            List<PartTran> partTranList = transactionHeader.getPartTrans();
                            if (response.getStatusCode() == HttpStatus.FOUND.value()) {
                                System.out.println("Charges found, charge json is "+response);
                                JSONObject obj = new JSONObject(response);
                                JSONArray ja = obj.getJSONArray("entity");
                                for (Object o : ja) {
                                    JSONObject ob = new JSONObject(o.toString());
                                    String acid = ob.get("acid").toString();
                                    PartTran partTran1 = new PartTran();
                                    AccountRepository.AcTransactionDetails acDetailsDb = accountRepository.getAccountTransactionDetails(acid).get();
                                    partTran1.setPartTranType(ob.getString("partTranType"));
                                    partTran1.setTransactionAmount(ob.getDouble("transactionAmount"));
                                    partTran1.setAcid(acid);
                                    partTran1.setTransactionDate(new Date());
//                                    partTran1.setDeletedFlag('N');
//                                    partTran1.setPostedBy(UserRequestContext.getCurrentUser());
                                    partTran1.setParttranIdentity(ob.getString("parttranIdentity"));
                                    partTran1.setCurrency(acDetailsDb.getCurrency());
                                    partTran1.setTransactionParticulars(ob.getString("transactionParticulars"));
                                    partTran1.setIsoFlag('N');
                                    partTran1.setCurrency("KSH");
                                    partTran1.setExchangeRate(partTranList.get(0).getExchangeRate());
                                    partTran1.setTransactionDate(partTranList.get(0).getTransactionDate());
                                    partTran1.setAccountBalance(acDetailsDb.getAccountBalance());
                                    partTran1.setAccountType(acDetailsDb.getAccountType());
                                    partTran1.setIsWelfare(false);
                                    System.out.println("Partran header: "+partTran1);
                                    partTranList.add(partTran1);
                                }
                            } else {
                                return response;
                            }
                            System.out.println("adding charges");
                            System.out.println(Arrays.deepToString(partTranList.toArray()));
                            transactionHeader.setPartTrans(partTranList);
                            System.out.println(Arrays.deepToString(transactionHeader.getPartTrans().toArray()));
                        }
                    }

                    System.out.println("Before attachTellerPartrans");
                    System.out.println(Arrays.deepToString(transactionHeader.getPartTrans().toArray()));
                    transactionHeader = transactionProcessing.attachTellerPartrans(transactionHeader);
                    System.out.println("After attachTellerPartrans");
                    System.out.println(Arrays.deepToString(transactionHeader.getPartTrans().toArray()));
                    transactionHeader.setEntityId(entityId);
                    transactionHeader.setEnteredBy(user);
                    transactionHeader.setEnteredFlag('Y');
                    transactionHeader.setEnteredTime(new Date());
                    System.out.println("Before validation");
                    System.out.println(Arrays.deepToString(transactionHeader.getPartTrans().toArray()));
                    response = transactionProcessing.validateAccountActivationTransaction(transactionHeader);
                    System.out.println("After validation");
                    System.out.println(Arrays.deepToString(transactionHeader.getPartTrans().toArray()));
                    if (response.getStatusCode() == HttpStatus.ACCEPTED.value()) {
                        System.out.println("Is within teller limits");
                        System.out.println(Arrays.deepToString(transactionHeader.getPartTrans().toArray()));
//                        EntityResponse checkAcLimits = transactionProcessing.isTransactionWithinTellerLimits(transactionHeader);
//                        if (checkAcLimits.getStatusCode() == HttpStatus.OK.value()) {
                            transactionHeader.setVerifiedFlag('Y');
                            transactionHeader.setVerifiedBy(user);
                            transactionHeader.setVerifiedTime(new Date());
                            transactionHeader.setVerifiedFlag_2('Y');
                            transactionHeader.setVerifiedBy_2(user);
                            transactionHeader.setVerifiedTime_2(new Date());

                        List<PartTran> newParttran = transactionProcessing.attachCurrencies(transactionHeader.getPartTrans());
                        System.out.println("Before currency attachements ");
                        System.out.println(Arrays.deepToString(newParttran.toArray()));
                        transactionHeader.setPartTrans(newParttran);
                        transactionHeader.setTransactionCode(generateRandomCode(6).toUpperCase());
                        response = transactionProcessing.accountDebitBalanceValidation(transactionHeader);
                        if (response.getStatusCode() == HttpStatus.ACCEPTED.value()) {
                            response.setMessage("Transaction Entered Successfully. Transaction code is " + transactionHeader.getTransactionCode());
                            response.setStatusCode(HttpStatus.CREATED.value());
                            transactionHeader.setStatus(CONSTANTS.ENTERED);
                            response.setEntity(transactionHeader.getTransactionCode());
                            tranHeaderRepository.save(transactionHeader);
                        } else {
                            response = response;
                        }
                    } else {
                        return response;
                    }
                    return response;
                } else {
                    response = response;
                }
            }
            return response;
        } catch (Exception e) {
            EntityResponse res = new EntityResponse<>();
            e.printStackTrace();
            res.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            res.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return res;
        }
    }


    public EntityResponse enterBalanceEnquiryFee(TransactionHeader transactionHeader) {
        try {
            System.out.println("Inside enter transaction fuction, tranheader is: "+transactionHeader);
            EntityResponse response = new EntityResponse();
            String user = UserRequestContext.getCurrentUser();
            String entityId = EntityRequestContext.getCurrentEntityId();
            response.setStatusCode(HttpStatus.OK.value());
            if (user.isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return response;
            } else if (entityId.isEmpty()) {
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return response;
            } else {
                System.out.println("====================================");
                transactionHeader.setTransactionDate(new Date());
                transactionHeader.setCurrency("KSH");
                boolean chargeFee = true;

                System.out.println("skipped validation");
                if (response.getStatusCode() == HttpStatus.OK.value()) {
                    if (transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.BALANCE_ENQUIRY)
                    ) {
                        if (chargeFee) {
                            if (transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.BALANCE_ENQUIRY)) {
                                response = transactionProcessing.collectBalanceEnquiryCharges(transactionHeader);
                            }
                            List<PartTran> partTranList = transactionHeader.getPartTrans();
                            if (response.getStatusCode() == HttpStatus.FOUND.value()) {
                                System.out.println("Charges found, charge json is "+response);
                                JSONObject obj = new JSONObject(response);
                                JSONArray ja = obj.getJSONArray("entity");
                                for (Object o : ja) {
                                    JSONObject ob = new JSONObject(o.toString());
                                    String acid = ob.get("acid").toString();
                                    PartTran partTran1 = new PartTran();
                                    AccountRepository.AcTransactionDetails acDetailsDb = accountRepository.getAccountTransactionDetails(acid).get();
                                    partTran1.setPartTranType(ob.getString("partTranType"));
                                    partTran1.setTransactionAmount(ob.getDouble("transactionAmount"));
                                    partTran1.setAcid(acid);
                                    partTran1.setTransactionDate(new Date());
                                    partTran1.setParttranIdentity(ob.getString("parttranIdentity"));
                                    partTran1.setCurrency(acDetailsDb.getCurrency());
                                    partTran1.setTransactionParticulars(ob.getString("transactionParticulars"));
                                    partTran1.setIsoFlag('N');
                                    partTran1.setCurrency("KSH");
                                    partTran1.setExchangeRate(partTranList.get(0).getExchangeRate());
                                    partTran1.setTransactionDate(partTranList.get(0).getTransactionDate());
                                    partTran1.setAccountBalance(acDetailsDb.getAccountBalance());
                                    partTran1.setAccountType(acDetailsDb.getAccountType());
                                    partTran1.setIsWelfare(false);
                                    System.out.println("Partran header: "+partTran1);
                                    partTranList.add(partTran1);
                                }
                            } else {
                                return response;
                            }
                            System.out.println("adding charges in balance");
                            System.out.println(Arrays.deepToString(partTranList.toArray()));
                            transactionHeader.setPartTrans(partTranList);
                            System.out.println(Arrays.deepToString(transactionHeader.getPartTrans().toArray()));
                        }
                    }

                    System.out.println("Before attachTellerPartrans balance");
                    System.out.println(Arrays.deepToString(transactionHeader.getPartTrans().toArray()));
                    transactionHeader = transactionProcessing.attachTellerPartrans(transactionHeader);
                    System.out.println("After attachTellerPartrans in balance");
                    System.out.println(Arrays.deepToString(transactionHeader.getPartTrans().toArray()));
                    transactionHeader.setEntityId(entityId);
                    transactionHeader.setEnteredBy(user);
                    transactionHeader.setEnteredFlag('Y');
                    transactionHeader.setEnteredTime(new Date());
                    System.out.println("Before validation in balance");
                    System.out.println(Arrays.deepToString(transactionHeader.getPartTrans().toArray()));
                    response = transactionProcessing.validateAccountActivationTransaction(transactionHeader);
                    System.out.println("After validation in balance");
                    System.out.println(Arrays.deepToString(transactionHeader.getPartTrans().toArray()));
                    if (response.getStatusCode() == HttpStatus.ACCEPTED.value()) {
                        System.out.println("Is within teller limits");
                        System.out.println(Arrays.deepToString(transactionHeader.getPartTrans().toArray()));
                        transactionHeader.setVerifiedFlag('Y');
                        transactionHeader.setVerifiedBy(user);
                        transactionHeader.setVerifiedTime(new Date());
                        transactionHeader.setVerifiedFlag_2('Y');
                        transactionHeader.setVerifiedBy_2(user);
                        transactionHeader.setVerifiedTime_2(new Date());

                        List<PartTran> newParttran = transactionProcessing.attachCurrencies(transactionHeader.getPartTrans());
                        System.out.println("Before currency attachements ");
                        System.out.println(Arrays.deepToString(newParttran.toArray()));
                        transactionHeader.setPartTrans(newParttran);
                        transactionHeader.setTransactionCode(generateRandomCode(6).toUpperCase());
                        response = transactionProcessing.accountDebitBalanceValidation(transactionHeader);
                        if (response.getStatusCode() == HttpStatus.ACCEPTED.value()) {
                            response.setMessage("Transaction Entered Successfully. Transaction code is " + transactionHeader.getTransactionCode());
                            response.setStatusCode(HttpStatus.CREATED.value());
                            transactionHeader.setStatus(CONSTANTS.ENTERED);
                            response.setEntity(transactionHeader.getTransactionCode());
                            tranHeaderRepository.save(transactionHeader);
                        } else {
                            response = response;
                        }
                    } else {
                        return response;
                    }
                    return response;
                } else {
                    response = response;
                }
            }
            return response;
        } catch (Exception e) {
            EntityResponse res = new EntityResponse<>();
            e.printStackTrace();
            res.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            res.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return res;
        }
    }




    public EntityResponse enterSMSFee(TransactionHeader transactionHeader) {
        try {
            System.out.println("Inside enter transaction fuction, tranheader is: "+transactionHeader);
            EntityResponse response = new EntityResponse();
            String user = UserRequestContext.getCurrentUser();
            String entityId = EntityRequestContext.getCurrentEntityId();
            response.setStatusCode(HttpStatus.OK.value());
            if (user.isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return response;
            } else if (entityId.isEmpty()) {
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return response;
            } else {
                System.out.println("====================================");
                transactionHeader.setTransactionDate(new Date());
                transactionHeader.setCurrency("KSH");
                boolean chargeFee = true;

                System.out.println("skipped validation");
                if (response.getStatusCode() == HttpStatus.OK.value()) {
                    if (transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.SMS_CHARGES)
                    ) {
                        if (chargeFee) {
                            if (transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.SMS_CHARGES)) {
                                response = transactionProcessing.collectSMSCharges(transactionHeader);
                            }
                            List<PartTran> partTranList = transactionHeader.getPartTrans();
                            if (response.getStatusCode() == HttpStatus.FOUND.value()) {
                                System.out.println("Charges found, charge json is "+response);
                                JSONObject obj = new JSONObject(response);
                                JSONArray ja = obj.getJSONArray("entity");
                                for (Object o : ja) {
                                    JSONObject ob = new JSONObject(o.toString());
                                    String acid = ob.get("acid").toString();
                                    PartTran partTran1 = new PartTran();
                                    AccountRepository.AcTransactionDetails acDetailsDb = accountRepository.getAccountTransactionDetails(acid).get();
                                    partTran1.setPartTranType(ob.getString("partTranType"));
                                    partTran1.setTransactionAmount(ob.getDouble("transactionAmount"));
                                    partTran1.setAcid(acid);
                                    partTran1.setTransactionDate(new Date());
                                    partTran1.setParttranIdentity(ob.getString("parttranIdentity"));
                                    partTran1.setCurrency(acDetailsDb.getCurrency());
                                    partTran1.setTransactionParticulars(ob.getString("transactionParticulars"));
                                    partTran1.setIsoFlag('N');
                                    partTran1.setCurrency("KSH");
                                    partTran1.setExchangeRate(partTranList.get(0).getExchangeRate());
                                    partTran1.setTransactionDate(partTranList.get(0).getTransactionDate());
                                    partTran1.setAccountBalance(acDetailsDb.getAccountBalance());
                                    partTran1.setAccountType(acDetailsDb.getAccountType());
                                    partTran1.setIsWelfare(false);
                                    System.out.println("Partran header: "+partTran1);
                                    partTranList.add(partTran1);
                                }
                            } else {
                                return response;
                            }
                            System.out.println("adding charges in balance");
                            System.out.println(Arrays.deepToString(partTranList.toArray()));
                            transactionHeader.setPartTrans(partTranList);
                            System.out.println(Arrays.deepToString(transactionHeader.getPartTrans().toArray()));
                        }
                    }

                    System.out.println("Before attachTellerPartrans balance");
                    System.out.println(Arrays.deepToString(transactionHeader.getPartTrans().toArray()));
                    transactionHeader = transactionProcessing.attachTellerPartrans(transactionHeader);
                    System.out.println("After attachTellerPartrans in balance");
                    System.out.println(Arrays.deepToString(transactionHeader.getPartTrans().toArray()));
                    transactionHeader.setEntityId(entityId);
                    transactionHeader.setEnteredBy(user);
                    transactionHeader.setEnteredFlag('Y');
                    transactionHeader.setEnteredTime(new Date());
                    System.out.println("Before validation in balance");
                    System.out.println(Arrays.deepToString(transactionHeader.getPartTrans().toArray()));
                    response = transactionProcessing.validateAccountActivationTransaction(transactionHeader);
                    System.out.println("After validation in balance");
                    System.out.println(Arrays.deepToString(transactionHeader.getPartTrans().toArray()));
                    if (response.getStatusCode() == HttpStatus.ACCEPTED.value()) {
                        System.out.println("Is within teller limits");
                        System.out.println(Arrays.deepToString(transactionHeader.getPartTrans().toArray()));
                        transactionHeader.setVerifiedFlag('Y');
                        transactionHeader.setVerifiedBy(user);
                        transactionHeader.setVerifiedTime(new Date());
                        transactionHeader.setVerifiedFlag_2('Y');
                        transactionHeader.setVerifiedBy_2(user);
                        transactionHeader.setVerifiedTime_2(new Date());

                        List<PartTran> newParttran = transactionProcessing.attachCurrencies(transactionHeader.getPartTrans());
                        System.out.println("Before currency attachements ");
                        System.out.println(Arrays.deepToString(newParttran.toArray()));
                        transactionHeader.setPartTrans(newParttran);
                        transactionHeader.setTransactionCode(generateRandomCode(6).toUpperCase());
                        response = transactionProcessing.accountDebitBalanceValidation(transactionHeader);
                        if (response.getStatusCode() == HttpStatus.ACCEPTED.value()) {
                            response.setMessage("Transaction Entered Successfully. Transaction code is " + transactionHeader.getTransactionCode());
                            response.setStatusCode(HttpStatus.CREATED.value());
                            transactionHeader.setStatus(CONSTANTS.ENTERED);
                            response.setEntity(transactionHeader.getTransactionCode());
                            tranHeaderRepository.save(transactionHeader);
                        } else {
                            response = response;
                        }
                    } else {
                        return response;
                    }
                    return response;
                } else {
                    response = response;
                }
            }
            return response;
        } catch (Exception e) {
            EntityResponse res = new EntityResponse<>();
            e.printStackTrace();
            res.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            res.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return res;
        }
    }



    public EntityResponse enterMobileTransaction(TransactionHeader transactionHeader) {
        try {
            EntityResponse response = new EntityResponse();
            String user = UserRequestContext.getCurrentUser();
            String entityId = EntityRequestContext.getCurrentEntityId();
            response.setStatusCode(HttpStatus.OK.value());
            if (user.isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return response;
            } else if (entityId.isEmpty()) {
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return response;
            } else {
                transactionHeader.setTransactionDate(new Date());
                transactionHeader.setCurrency("KES");
                boolean chargeFee = transactionHeader.getPartTrans().get(0).getChargeFee() == 'Y';
                response = transactionProcessing.validateParttrans(transactionHeader);
                if (response.getStatusCode() == HttpStatus.OK.value()) {
                    // TODO: collect withdrawal fee
                    if (transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.CASH_WITHDRAWAL)) {
                        if (chargeFee) {
                            response = transactionProcessing.collectWithdrawalCharges(transactionHeader);
                            List<PartTran> partTranList = transactionHeader.getPartTrans();
                            if (response.getStatusCode() == HttpStatus.FOUND.value()) {
                                JSONObject obj = new JSONObject(response);
                                JSONArray ja = obj.getJSONArray("entity");
                                for (Object o : ja) {
                                    JSONObject ob = new JSONObject(o.toString());
                                    String acid = ob.get("acid").toString();
                                    PartTran partTran1 = new PartTran();
                                    AccountRepository.AcTransactionDetails acDetailsDb = accountRepository.getAccountTransactionDetails(acid).get();
                                    partTran1.setPartTranType(ob.getString("partTranType"));
                                    partTran1.setTransactionAmount(ob.getDouble("transactionAmount"));
                                    partTran1.setAcid(acid);
                                    partTran1.setParttranIdentity(ob.getString("parttranIdentity"));
                                    partTran1.setCurrency(acDetailsDb.getCurrency());
                                    partTran1.setTransactionParticulars(ob.getString("transactionParticulars"));
                                    partTran1.setIsoFlag('N');
                                    partTran1.setExchangeRate(partTranList.get(0).getExchangeRate());
                                    partTran1.setTransactionDate(partTranList.get(0).getTransactionDate());
                                    partTran1.setAccountBalance(acDetailsDb.getAccountBalance());
                                    partTran1.setAccountType(acDetailsDb.getAccountType());
                                    partTran1.setIsWelfare(false);
                                    partTranList.add(partTran1);
                                }
                            } else {
                                return response;
                            }
                            transactionHeader.setPartTrans(partTranList);
                        }
                    }
                    //TODO: collect cheque charges
                    if (transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.CHEQUE_CLEARENCE) || transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.CHEQUE_BOUNCE)) {
                        List<PartTran> partTranList = transactionHeader.getPartTrans();
                        if (transactionHeader.getChargeEventId() != null && !transactionHeader.getChargeEventId().trim().isEmpty()) {
                            response = transactionProcessing.collectChequeCharges(transactionHeader);
                            if (response.getStatusCode() == HttpStatus.FOUND.value()) {
                                JSONObject obj = new JSONObject(response);
                                JSONArray ja = obj.getJSONArray("entity");
                                for (Object o : ja) {
                                    JSONObject ob = new JSONObject(o.toString());
                                    String acid = ob.get("acid").toString();
                                    PartTran partTran1 = new PartTran();
                                    AccountRepository.AcTransactionDetails acDetailsDb = accountRepository.getAccountTransactionDetails(acid).get();
                                    partTran1.setPartTranType(ob.getString("partTranType"));
                                    partTran1.setTransactionAmount(ob.getDouble("transactionAmount"));
                                    partTran1.setAcid(acid);
                                    partTran1.setParttranIdentity(ob.getString("parttranIdentity"));
                                    partTran1.setCurrency(acDetailsDb.getCurrency());
                                    partTran1.setTransactionParticulars(ob.getString("transactionParticulars"));
                                    partTran1.setIsoFlag('N');
                                    partTran1.setExchangeRate(partTranList.get(0).getExchangeRate());
                                    partTran1.setTransactionDate(partTranList.get(0).getTransactionDate());
                                    partTran1.setAccountBalance(acDetailsDb.getAccountBalance());
                                    partTran1.setAccountType(acDetailsDb.getAccountType());
                                    partTran1.setIsWelfare(false);
                                    partTranList.add(partTran1);
                                }
                            } else {
                                return response;
                            }
                        }
                        transactionHeader.setPartTrans(partTranList);
                    }
                    transactionHeader = transactionProcessing.attachTellerPartrans(transactionHeader);
                    transactionHeader.setEntityId(entityId);
                    transactionHeader.setEnteredBy(user);
                    transactionHeader.setEnteredFlag('Y');
                    transactionHeader.setEnteredTime(new Date());
                    transactionHeader.setVerifiedFlag('Y');
                    transactionHeader.setVerifiedBy(user);
                    transactionHeader.setVerifiedTime(new Date());
                    transactionHeader.setVerifiedFlag_2('Y');
                    transactionHeader.setVerifiedBy_2(user);
                    transactionHeader.setVerifiedTime_2(new Date());
                    response = transactionProcessing.validateTransaction(transactionHeader);
                    if (response.getStatusCode() == HttpStatus.ACCEPTED.value()) {
                        EntityResponse checkAcLimits = transactionProcessing.isTransactionWithinTellerLimits(transactionHeader);
                        if (checkAcLimits.getStatusCode() == HttpStatus.OK.value()) {
                            transactionHeader.setVerifiedFlag('Y');
                            transactionHeader.setVerifiedBy(user);
                            transactionHeader.setVerifiedTime(new Date());
                            transactionHeader.setVerifiedFlag_2('Y');
                            transactionHeader.setVerifiedBy_2(user);
                            transactionHeader.setVerifiedTime_2(new Date());
                        }
                        List<PartTran> newParttran = transactionProcessing.attachCurrencies(transactionHeader.getPartTrans());
                        transactionHeader.setPartTrans(newParttran);
                        transactionHeader.setTransactionCode(generateRandomCode(6).toUpperCase());
                        response = transactionProcessing.accountDebitBalanceValidation(transactionHeader);
                        if (response.getStatusCode() == HttpStatus.ACCEPTED.value()) {

                            transactionHeader.setPostedFlag('Y');
                            transactionHeader.setPostedBy(user);
                            transactionHeader.setStatus(CONSTANTS.POSTED);
                            transactionHeader.setPostedTime(new Date());
                            // Update account balance
                            transactionProcessing.updateAccountBalances(transactionHeader.getPartTrans());
                            // Send notifications
                            System.out.println("About to send MAIL 1");
                            notificationService.sendEmailNotification(transactionHeader);
                            // Send SMS
                            notificationService.sendSMSNotification1(transactionHeader);
                            response.setMessage("Transaction With Transaction Code " + transactionHeader.getTransactionCode() + " Posted Successfully and Account Balance Updated Successfully At " + transactionHeader.getPostedTime());
                            response.setStatusCode(HttpStatus.CREATED.value());
                            transactionHeader.setStatus(CONSTANTS.ENTERED);
                            response.setEntity(tranHeaderRepository.save(transactionHeader));
                        } else {
                            response.setMessage("Error: !! Failed to Post Transaction As At " + new Date());
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            transactionHeader.setStatus("NOT-POSTED");
                            response.setEntity("");
                        }
                    } else {
                        return response;
                    }
                    return response;
                } else {
                    response.setMessage("Error: !! Failed to Collect Transaction Charges As At " + new Date());
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    transactionHeader.setStatus("NOT-CHARGES-COLLECTED");
                    response.setEntity("");
                }
            }
            return response;
        } catch (Exception e) {
            EntityResponse res = new EntityResponse<>();
            e.printStackTrace();
            res.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            res.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return res;
        }
    }



    //    Enter Mobile Transactions
    public EntityResponse enterMobileTransactions(TransactionHeader transactionHeader) {
        try {
            EntityResponse response = new EntityResponse();
            String user = UserRequestContext.getCurrentUser();
            String entityId = EntityRequestContext.getCurrentEntityId();
            response.setStatusCode(HttpStatus.OK.value());
            if (user.isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return response;
            } else if (entityId.isEmpty()) {
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return response;
            } else {

                transactionHeader.setTransactionDate(new Date());
                transactionHeader.setCurrency("KES");
                boolean chargeFee = transactionHeader.getPartTrans().get(0).getChargeFee() == 'Y';
                response = transactionProcessing.validateParttrans(transactionHeader);
                if (response.getStatusCode() == HttpStatus.OK.value()) {
//                        TODO: collect withdrawal fee
                    if (transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.CASH_WITHDRAWAL)) {
                        if (chargeFee) {
                            response = transactionProcessing.collectWithdrawalCharges(transactionHeader);
                            List<PartTran> partTranList = transactionHeader.getPartTrans();
                            if (response.getStatusCode() == HttpStatus.FOUND.value()) {
                                JSONObject obj = new JSONObject(response);
                                JSONArray ja = obj.getJSONArray("entity");
                                for (Object o : ja) {
                                    JSONObject ob = new JSONObject(o.toString());
                                    String acid = ob.get("acid").toString();
                                    PartTran partTran1 = new PartTran();
                                    AccountRepository.AcTransactionDetails acDetailsDb = accountRepository.getAccountTransactionDetails(acid).get();
                                    partTran1.setPartTranType(ob.getString("partTranType"));
                                    partTran1.setTransactionAmount(ob.getDouble("transactionAmount"));
                                    partTran1.setAcid(acid);
                                    partTran1.setParttranIdentity(ob.getString("parttranIdentity"));
                                    partTran1.setCurrency(acDetailsDb.getCurrency());
                                    partTran1.setTransactionParticulars(ob.getString("transactionParticulars"));
                                    partTran1.setIsoFlag('N');
                                    partTran1.setExchangeRate(partTranList.get(0).getExchangeRate());
                                    partTran1.setTransactionDate(partTranList.get(0).getTransactionDate());
                                    partTran1.setAccountBalance(acDetailsDb.getAccountBalance());
                                    partTran1.setAccountType(acDetailsDb.getAccountType());
                                    partTran1.setIsWelfare(false);
                                    partTranList.add(partTran1);
                                }
                            } else {
                                return response;
                            }
                            transactionHeader.setPartTrans(partTranList);
                        }
                    }
                    //  TODO: collect cheque charges
                    if (transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.CHEQUE_CLEARENCE) || transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.CHEQUE_BOUNCE)) {

                        List<PartTran> partTranList = transactionHeader.getPartTrans();

                        if (transactionHeader.getChargeEventId() != null && !transactionHeader.getChargeEventId().trim().isEmpty()) {
                            response = transactionProcessing.collectChequeCharges(transactionHeader);

                            if (response.getStatusCode() == HttpStatus.FOUND.value()) {
                                JSONObject obj = new JSONObject(response);
                                JSONArray ja = obj.getJSONArray("entity");
                                for (Object o : ja) {
                                    JSONObject ob = new JSONObject(o.toString());
                                    String acid = ob.get("acid").toString();
                                    PartTran partTran1 = new PartTran();
                                    AccountRepository.AcTransactionDetails acDetailsDb = accountRepository.getAccountTransactionDetails(acid).get();
                                    partTran1.setPartTranType(ob.getString("partTranType"));
                                    partTran1.setTransactionAmount(ob.getDouble("transactionAmount"));
                                    partTran1.setAcid(acid);
                                    partTran1.setParttranIdentity(ob.getString("parttranIdentity"));
                                    partTran1.setCurrency(acDetailsDb.getCurrency());
                                    partTran1.setTransactionParticulars(ob.getString("transactionParticulars"));
                                    partTran1.setIsoFlag('N');
                                    partTran1.setExchangeRate(partTranList.get(0).getExchangeRate());
                                    partTran1.setTransactionDate(partTranList.get(0).getTransactionDate());
                                    partTran1.setAccountBalance(acDetailsDb.getAccountBalance());
                                    partTran1.setAccountType(acDetailsDb.getAccountType());
                                    partTran1.setIsWelfare(false);
                                    partTranList.add(partTran1);
                                }
                            } else {
                                return response;
                            }
                        }
                        transactionHeader.setPartTrans(partTranList);
                    }
                    transactionHeader = transactionProcessing.attachTellerPartrans(transactionHeader);
                    transactionHeader.setEntityId(entityId);
                    transactionHeader.setEnteredBy(user);
                    transactionHeader.setEnteredFlag('Y');
                    transactionHeader.setEnteredTime(new Date());
                    transactionHeader.setVerifiedFlag('Y');
                    transactionHeader.setVerifiedBy(user);
                    transactionHeader.setVerifiedTime(new Date());
                    transactionHeader.setVerifiedFlag_2('Y');
                    transactionHeader.setVerifiedBy_2(user);
                    transactionHeader.setVerifiedTime_2(new Date());

                    List<PartTran> newParttran = transactionProcessing.attachCurrencies(transactionHeader.getPartTrans());
                    transactionHeader.setPartTrans(newParttran);
                    transactionHeader.setTransactionCode(generateRandomCode(6).toUpperCase());
                    response.setStatusCode(HttpStatus.CREATED.value());
                    transactionHeader.setStatus(CONSTANTS.ENTERED);
                    TransactionHeader saveTransaction = tranHeaderRepository.save(transactionHeader);
                    if (response.getStatusCode() == HttpStatus.CREATED.value()) {
                        System.out.println("MY RES   ==> " + response.getEntity());
                        Optional<TransactionHeader> transactionHeaderFound = tranHeaderRepository.findByTransactionCode(saveTransaction.getTransactionCode());
                        if (transactionHeaderFound.isPresent()) {
                            // Check if transaction is already posted
                            if (transactionHeaderFound.get().getPostedFlag() == 'N') {
                                if (transactionHeaderFound.get().getVerifiedFlag() == 'Y' && transactionHeaderFound.get().getVerifiedFlag_2() == 'Y') {
                                    //Check if transaction is verified
                                    TransactionHeader transactionHeader1 = transactionHeaderFound.get();
                                    transactionHeader1.setPostedFlag('Y');
                                    transactionHeader1.setPostedBy(user);
                                    transactionHeader1.setStatus(CONSTANTS.POSTED);
                                    transactionHeader1.setPostedTime(new Date());
                                    tranHeaderRepository.save(transactionHeader1);
                                    //Update account balance
                                    transactionProcessing.updateAccountBalances(transactionHeader1.getPartTrans());
                                    //Send notifications
                                    System.out.println("About to send MAIL 2");
                                    notificationService.sendEmailNotification(transactionHeader1);
                                    //Send SMS
                                    notificationService.sendSMSNotification(transactionHeader1);
                                    response.setMessage("Transaction With Transaction Code " + transactionHeaderFound.get().getTransactionCode() + " Posted Successfully and Account Balance Updated Successfully At " + transactionHeaderFound.get().getPostedTime());
                                    response.setStatusCode(HttpStatus.OK.value());
                                    response.setEntity(transactionHeader1);
                                } else {
                                    response.setMessage("Transaction with Transaction Code " + transactionHeaderFound.get().getTransactionCode() + " MUST Be Fully Verified: !!");
                                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                }
                            } else {
                                response.setMessage("Transaction with Transaction Code " + transactionHeaderFound.get().getTransactionCode() + " Already Posted On " + transactionHeaderFound.get().getPostedTime());
                                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            }
                        } else {
                            response.setMessage("Transaction with Transaction Code " + transactionHeaderFound.get().getTransactionCode() + " Not found");
                            response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        }
                    }
                }
            }
            return response;
        } catch (Exception e) {
            EntityResponse res = new EntityResponse<>();
            e.printStackTrace();
            res.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            res.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return res;
        }
    }

    // TODO: 4/6/2023 salary processing
    public EntityResponse enterSalaryProcessing(TransactionHeader transactionHeader,
                                                String chargeCode,
                                                String chargeAccount) {
        try {
            EntityResponse response = new EntityResponse();
            String user = UserRequestContext.getCurrentUser();
            String entityId = EntityRequestContext.getCurrentEntityId();
            if (user.isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return response;
            } else if (entityId.isEmpty()) {
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return response;
            } else {
                transactionHeader.setTransactionDate(new Date());
                transactionHeader.setCurrency("KES");
//
                //                        TODO: collect  charges

                log.info("transaction type is :: " + transactionHeader.getTransactionType());
                if (transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.BATCH_UPLOAD) ||
                        transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.SALARY_UPLOAD)
                        || transactionHeader.getTransactionType().equalsIgnoreCase(CONSTANTS.CHEQUE_BOUNCE)) {

                    List<PartTran> partTranList = transactionHeader.getPartTrans();
                    if (chargeCode != null && !chargeCode.trim().isEmpty()) {
                        response = transactionProcessing.collectSalaryCharges(transactionHeader, chargeCode, chargeAccount);
                        if (response.getStatusCode() == HttpStatus.FOUND.value()) {
                            JSONObject obj = new JSONObject(response);
                            JSONArray ja = obj.getJSONArray("entity");
                            for (Object o : ja) {
                                JSONObject ob = new JSONObject(o.toString());
                                String acid = ob.get("acid").toString();
                                PartTran partTran1 = new PartTran();
                                AccountRepository.AcTransactionDetails acDetailsDb = accountRepository.getAccountTransactionDetails(acid).get();
                                partTran1.setPartTranType(ob.getString("partTranType"));
                                partTran1.setTransactionAmount(ob.getDouble("transactionAmount"));
                                partTran1.setAcid(acid);
                                partTran1.setParttranIdentity(ob.getString("parttranIdentity"));
                                partTran1.setCurrency(acDetailsDb.getCurrency());
                                partTran1.setTransactionParticulars(ob.getString("transactionParticulars"));
                                partTran1.setIsoFlag('N');
                                partTran1.setExchangeRate(partTranList.get(0).getExchangeRate());
                                partTran1.setTransactionDate(partTranList.get(0).getTransactionDate());
                                partTran1.setAccountBalance(acDetailsDb.getAccountBalance());
                                partTran1.setAccountType(acDetailsDb.getAccountType());
                                partTran1.setIsWelfare(false);
                                partTranList.add(partTran1);
                            }
                        } else {
                            return response;
                        }
                    }
                    transactionHeader.setPartTrans(partTranList);
                }
                transactionHeader = transactionProcessing.attachTellerPartrans(transactionHeader);
                transactionHeader.setEntityId(entityId);
                transactionHeader.setEnteredBy(user);
                transactionHeader.setEnteredFlag('Y');
                transactionHeader.setEnteredTime(new Date());
                response = transactionProcessing.validateTransaction(transactionHeader);
                if (response.getStatusCode() == HttpStatus.ACCEPTED.value()) {
                    List<PartTran> newParttran = transactionProcessing.attachCurrencies(transactionHeader.getPartTrans());
                    transactionHeader.setPartTrans(newParttran);
                    transactionHeader.setTransactionCode(generateRandomCode(6).toUpperCase());
                    response = transactionProcessing.accountDebitBalanceValidation(transactionHeader);
                    if (response.getStatusCode() == HttpStatus.ACCEPTED.value()) {
                        response.setMessage("Transaction Entered Successfully. Transaction code is " + transactionHeader.getTransactionCode());
                        response.setStatusCode(HttpStatus.CREATED.value());
                        transactionHeader.setStatus(CONSTANTS.ENTERED);
                        response.setEntity(tranHeaderRepository.save(transactionHeader));
                    } else {
                        response = response;
                    }
                } else {
                    return response;
                }
                return response;
            }
        } catch (Exception e) {
            EntityResponse res = new EntityResponse<>();
            log.error("error " + e);
            res.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            res.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return res;
        }
    }

    public EntityResponse systemReverseTransaction(String transactionCode) throws IOException {
        try {
            String user = UserRequestContext.getCurrentUser();
            String entityId = EntityRequestContext.getCurrentEntityId();
            return systemReverseTransaction(transactionCode, user, entityId, true);
        } catch (Exception e) {
            EntityResponse response = new EntityResponse();
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return response;
        }
    }

    public EntityResponse systemReverseTransaction(String transactionCode, Boolean checkIfSystem) throws IOException {
        try {
            String user = UserRequestContext.getCurrentUser();
            String entityId = EntityRequestContext.getCurrentEntityId();
            return systemReverseTransaction(transactionCode, user, entityId, checkIfSystem);
        } catch (Exception e) {
            EntityResponse response = new EntityResponse();
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return response;
        }
    }


    public EntityResponse systemReverseTransaction(String transactionCode, String user,
                                                   String entityId, Boolean mustBeSystemUser) {
        try {
            EntityResponse response = new EntityResponse();
            if (user.isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return response;
            } else if (entityId.isEmpty()) {
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return response;
            } else {
                Optional<TransactionHeader> transactionHeaderCheck = tranHeaderRepository.findByTransactionCode(transactionCode);
                if (transactionHeaderCheck.isPresent()) {
                    TransactionHeader transactionHeader = transactionHeaderCheck.get();

                    if (mustBeSystemUser) {
                        if (!transactionHeader.getEnteredBy().equalsIgnoreCase("SYSTEM")) {
                            response.setMessage("This is not a system generated transaction");
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            return response;
                        }
                    }

                    if (transactionHeader.getReversedFlag() == 'N') {
                        String reverseTransactionCode = generateRandomCode(6).toUpperCase();
                        Gson g = new Gson();
                        String jsonObjStr = g.toJson(transactionHeader);

                        JSONObject obj = new JSONObject(jsonObjStr);
                        obj.remove("sn");
//                    save new Transaction
                        TransactionHeader newTransaction = g.fromJson(String.valueOf(obj), TransactionHeader.class);
                        List<PartTran> newPartTrans = new ArrayList<>();
                        List<PartTran> partTransList = newTransaction.getPartTrans();
                        for (int i = 0; i < partTransList.size(); i++) {
                            PartTran partTran = partTransList.get(i);
                            partTran.setSn(null);
                            if (partTran.getPartTranType().equalsIgnoreCase(CONSTANTS.Debit)) {
                                partTran.setPartTranType(CONSTANTS.Credit);
                            } else {
                                partTran.setPartTranType(CONSTANTS.Debit);
                            }
                            partTran.setTransactionDate(new Date());
                            partTran.setTransactionParticulars("REVERSAL for " + transactionCode);
                            newPartTrans.add(partTran);
                        }
                        newTransaction.setPartTrans(newPartTrans);
                        newTransaction.setTransactionDate(new Date());
                        newTransaction.setReversalTransactionCode(transactionCode);
                        newTransaction.setReversedBy(user);
                        newTransaction.setTransactionCode(reverseTransactionCode);
                        newTransaction.setEntityId(entityId);
                        newTransaction.setTransactionType(CONSTANTS.SYSTEM_REVERSAL);
                        newTransaction.setEnteredBy(user);
                        newTransaction.setEnteredFlag('Y');
                        newTransaction.setEnteredTime(new Date());
                        newTransaction.setPostedFlag('N');
                        newTransaction.setPostedBy("");
                        newTransaction.setVerifiedFlag('N');
                        newTransaction.setVerifiedBy("");
                        newTransaction.setReversedFlag('Y');
                        newTransaction.setReversalPostedFlag(CONSTANTS.YES);
                        newTransaction.setVerifiedTime(null);
                        newTransaction.setVerifiedFlag_2('N');
                        newTransaction.setVerifiedBy_2("");
                        newTransaction.setVerifiedTime_2(null);
                        response.setMessage("Transaction Reversed Successfully. Transaction code is " + newTransaction.getTransactionCode());
                        response.setStatusCode(HttpStatus.CREATED.value());
                        newTransaction.setStatus(CONSTANTS.ENTERED);
//                        response.setEntity(tranHeaderRepository.save(newTransaction));

                        EntityResponse transactionRes= transactionsController.systemTransaction1(newTransaction).getBody();
                        if (!transactionRes.getStatusCode().equals(HttpStatus.OK.value())) {
                            response.setMessage("TRANSACTION ERROR! COULD NOT PERFORM SYSTEM REVERSAL");
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            return response;
                        } else {
                            transactionHeader.setReversedBy(user);
                            transactionHeader.setReversedTime(new Date());
                            transactionHeader.setStatus(CONSTANTS.REVERSED);
                            transactionHeader.setReversedFlag('Y');
                            transactionHeader.setReversalPostedFlag(CONSTANTS.YES);
                            tranHeaderRepository.save(transactionHeader);

                            return transactionRes;
                        }
                    } else {
                        response.setMessage("This transaction with transaction code " + transactionCode + " has already been reversed!");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    }
                } else {
                    response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                }
            }
            return response;
        } catch (Exception e) {
            EntityResponse res = new EntityResponse<>();
            e.printStackTrace();
            res.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            res.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return res;
        }
    }


    public EntityResponse reverseTransaction(String transactionCode) {
        try {
            EntityResponse response = new EntityResponse();
            String user = UserRequestContext.getCurrentUser();
            String entityId = EntityRequestContext.getCurrentEntityId();
            if (user.isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return response;
            } else if (entityId.isEmpty()) {
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return response;
            } else {
                Optional<TransactionHeader> transactionHeaderCheck = tranHeaderRepository.findByTransactionCode(transactionCode);
                if (transactionHeaderCheck.isPresent()) {
                    TransactionHeader transactionHeader = transactionHeaderCheck.get();
                    if (transactionHeader.getReversedFlag() == 'N') {
                        String reverseTransactionCode = generateRandomCode(6).toUpperCase();
                        transactionHeader.setReversedBy(user);
                        transactionHeader.setReversedTime(new Date());
                        transactionHeader.setStatus(CONSTANTS.REVERSED);
                        transactionHeader.setReversedFlag('Y');
//                        System.out.println(transactionHeader);
                        tranHeaderRepository.save(transactionHeader);
//                    Saved old transaction


                        Gson g = new Gson();
                        String jsonObjStr = g.toJson(transactionHeader);
                        transactionHeader = null;
                        JSONObject obj = new JSONObject(jsonObjStr);
                        obj.remove("sn");
//                    save new Transaction
                        TransactionHeader newTransaction = g.fromJson(String.valueOf(obj), TransactionHeader.class);
                        List<PartTran> newPartTrans = new ArrayList<>();
                        List<PartTran> partTransList = newTransaction.getPartTrans();
                        for (int i = 0; i < partTransList.size(); i++) {
                            PartTran partTran = partTransList.get(i);
                            partTran.setSn(null);
                            if (partTran.getPartTranType().equalsIgnoreCase(CONSTANTS.Debit)) {
                                partTran.setPartTranType(CONSTANTS.Credit);
                            } else {
                                partTran.setPartTranType(CONSTANTS.Debit);
                            }
                            partTran.setTransactionDate(new Date());
                            partTran.setTransactionParticulars("REVERSAL for " + transactionCode);
                            newPartTrans.add(partTran);
                        }
                        newTransaction.setPartTrans(newPartTrans);
                        newTransaction.setTransactionDate(new Date());
                        newTransaction.setReversalTransactionCode(transactionCode);
                        newTransaction.setTransactionCode(reverseTransactionCode);
                        newTransaction.setEntityId(entityId);
                        newTransaction.setTransactionType(CONSTANTS.REVERSE_TRANSACTIONS);
                        newTransaction.setEnteredBy(user);
                        newTransaction.setEnteredFlag('Y');
                        newTransaction.setEnteredTime(new Date());
                        newTransaction.setPostedFlag('N');
                        newTransaction.setPostedBy("");
                        newTransaction.setVerifiedFlag('N');
                        newTransaction.setVerifiedBy("");
                        newTransaction.setReversedFlag('Y');
                        newTransaction.setVerifiedTime(null);
                        newTransaction.setVerifiedFlag_2('N');
                        newTransaction.setVerifiedBy_2("");
                        newTransaction.setVerifiedTime_2(null);
                        response.setMessage("Transaction Reversed Successfully. Transaction code is " + newTransaction.getTransactionCode());
                        response.setStatusCode(HttpStatus.CREATED.value());
                        newTransaction.setStatus(CONSTANTS.ENTERED);
                        response.setEntity(tranHeaderRepository.save(newTransaction));
                    } else {
                        response.setMessage("This transaction with transaction code " + transactionCode + " has already been reversed!");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    }
                } else {
                    response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                }
            }
            return response;
        } catch (Exception e) {
            EntityResponse res = new EntityResponse<>();
            e.printStackTrace();
            res.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            res.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return res;
        }
    }

    //    Verify
    public EntityResponse acknowledge(String transactionCode) {
        try {
            EntityResponse response = new EntityResponse();
            String user = UserRequestContext.getCurrentUser();
            String entityId = EntityRequestContext.getCurrentEntityId();
            if (user.isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return response;
            } else if (entityId.isEmpty()) {
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return response;
            } else {
                Optional<TransactionHeader> transactionHeader = tranHeaderRepository.findByTransactionCode(transactionCode);
                if (transactionHeader.isPresent()) {
                    if (transactionHeader.get().getAcknowledgedFlag() == null || transactionHeader.get().getAcknowledgedFlag() == 'N') {
                        TransactionHeader transactionHeader1 = transactionHeader.get();
                        PartTran debitPartTran = transactionHeader1.getPartTrans().stream().filter(partTran -> partTran.getPartTranType().equalsIgnoreCase("Debit")).findAny().orElse(null);
                        Optional<TranHeaderRepository.TellerDetails> tellerDetails = tranHeaderRepository.getTellerDetails(user);
                        if (!tellerDetails.isPresent() || !tellerDetails.get().getAccount().equalsIgnoreCase(debitPartTran.getAcid())) {
                            response.setMessage("Acknowledge should be done by the funded Teller");
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            return response;
                        }
                        transactionHeader1.setAcknowledgedFlag('Y');
                        transactionHeader1.setAcknowledgedBy(user);
                        transactionHeader1.setAcknowledgedTime(new Date());
//                transactionHeader1.setStatus(CONSTANTS.VERIFIED);
                        tranHeaderRepository.save(transactionHeader1);
                        //TellerDetails
                        response.setMessage("Acknowledgement is successful.");
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(transactionHeader1);
                    } else {
//                Second Verifier

                        response.setMessage("Transaction with transaction code " + transactionCode + " is already verified twice");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    }
                } else {
                    response.setMessage("Transaction with transaction code " + transactionCode + " Not found");
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                }
            }
            return response;
        } catch (Exception e) {
            EntityResponse res = new EntityResponse<>();
            e.printStackTrace();
            res.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            res.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return res;
        }
    }

    //    Verify
    public EntityResponse verify(String transactionCode) {
        try {
            EntityResponse response = new EntityResponse();
            String user = UserRequestContext.getCurrentUser();
            String entityId = EntityRequestContext.getCurrentEntityId();
            if (user.isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return response;
            } else if (entityId.isEmpty()) {
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return response;
            } else {
                Optional<TransactionHeader> transactionHeader = tranHeaderRepository.findByTransactionCode(transactionCode);
                if (transactionHeader.isPresent()) {
                    if (transactionHeader.get().getVerifiedFlag() == 'N') {
                        TransactionHeader transactionHeader1 = transactionHeader.get();
                        transactionHeader1.setVerifiedFlag('Y');
                        transactionHeader1.setVerifiedBy(user);
                        transactionHeader1.setApprovalSentFlag('N');
                        transactionHeader1.setVerifiedTime(new Date());
                        transactionHeader1.setStatus(CONSTANTS.VERIFIED);
                        tranHeaderRepository.save(transactionHeader1);
                        response.setMessage("First Verification is successful.");
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(transactionHeader1);
                    } else {
//                Second Verifier
                        if (transactionHeader.get().getVerifiedFlag_2() == 'N') {
                            if (transactionHeader.get().getVerifiedBy().equalsIgnoreCase(user)) {
                                response.setMessage("You can not do both verification! hint: The system support two verification first and second. Kindly check  with the second verifier if you verify first");
                                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            } else {
                                TransactionHeader transactionHeader1 = transactionHeader.get();
                                transactionHeader1.setVerifiedFlag_2('Y');
                                transactionHeader1.setVerifiedBy_2(user);
                                transactionHeader1.setApprovalSentFlag('N');
                                transactionHeader1.setStatus(CONSTANTS.VERIFIED);
                                transactionHeader1.setVerifiedTime_2(new Date());
                                tranHeaderRepository.save(transactionHeader1);
                                response.setMessage("Second Verification is successful.");
                                response.setStatusCode(HttpStatus.OK.value());
                                response.setEntity(transactionHeader1);
                            }
                        } else {
                            response.setMessage("Transaction with transaction code " + transactionCode + " is already verified twice");
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        }
                    }
                } else {
                    response.setMessage("Transaction with transaction code " + transactionCode + " Not found");
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                }
            }
            return response;
        } catch (Exception e) {
            EntityResponse res = new EntityResponse<>();
            e.printStackTrace();
            res.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            res.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return res;
        }
    }



    public EntityResponse post(String transactionCode) {
        try {
            EntityResponse response = new EntityResponse();
            String user = UserRequestContext.getCurrentUser();
            String entityId = EntityRequestContext.getCurrentEntityId();
            if (user.isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return response;
            } else if (entityId.isEmpty()) {
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return response;
            } else {
                Optional<TransactionHeader> transactionHeader = tranHeaderRepository.findByTransactionCode(transactionCode);
                if (transactionHeader.isPresent()) {
//                check if transaction is already posted
                    if (transactionHeader.get().getPostedFlag() == 'N') {
//                    if (transactionHeader.get().getAcknowledgedFlag() == null){
//                        transactionHeader.get().setAcknowledgedFlag('N');
//                    }
                        if ((transactionHeader.get().getAcknowledgedFlag() == null || transactionHeader.get().getAcknowledgedFlag() != 'Y')
                                && transactionHeader.get().getTransactionType().equalsIgnoreCase("Fund Teller")) {

                            response.setMessage("The receiving teller must acknowledge receipt first before posting");
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        } else if (transactionHeader.get().getVerifiedFlag() == 'Y' && transactionHeader.get().getVerifiedFlag_2() == 'Y') {
                            //                check if transaction is verified
                            TransactionHeader transactionHeader1 = transactionHeader.get();
                            transactionHeader1.setPostedFlag('Y');
                            transactionHeader1.setPostedBy(user);
                            transactionHeader1.setStatus(CONSTANTS.POSTED);
                            transactionHeader1.setPostedTime(new Date());
                            if (transactionHeader1.getReversedFlag() == 'Y') {
                                Optional<TransactionHeader> reversedHeaderOptional = tranHeaderRepository.findByTransactionCode(transactionHeader1.getReversalTransactionCode());
                                if (reversedHeaderOptional.isPresent()) {
                                    TransactionHeader reversedHeader = reversedHeaderOptional.get();
                                    reversedHeader.setReversalPostedFlag('Y');
                                    transactionHeader1.setReversalPostedFlag('Y');
                                    tranHeaderRepository.save(reversedHeader);
                                }
                            }
                            tranHeaderRepository.save(transactionHeader1);
//                Update account balance
                            transactionProcessing.updateAccountBalances(transactionHeader1.getPartTrans());
//                Send notifications
                            System.out.println("About to send MAIL 3");
                            String TransactionType = transactionHeader1.getTransactionType();
                            System.out.println("Transaction Type: "+TransactionType);
                            if (!transactionHeader1.getTransactionType().equalsIgnoreCase(CONSTANTS.BALANCE_ENQUIRY)
                                && !transactionHeader1.getTransactionType().equalsIgnoreCase(CONSTANTS.SMS_CHARGES)) {
                                notificationService.sendEmailNotification(transactionHeader1);
                            }

//                Send SMS
//                            TODO: post kra
                            kraservice.sendTransactionToKra(transactionHeader1);

                            if(transactionHeader1.getTransactionType().equalsIgnoreCase(CONSTANTS.CASH_WITHDRAWAL)
                                    || transactionHeader1.getTransactionType().equalsIgnoreCase(CONSTANTS.CASH_DEPOSIT)
                                    || transactionHeader1.getTransactionType().equalsIgnoreCase(CONSTANTS.TRANSFER)
                                    || transactionHeader1.getTransactionType().equalsIgnoreCase(CONSTANTS.SALARY_UPLOAD) || transactionHeader1.getTransactionType().equalsIgnoreCase(CONSTANTS.BATCH_UPLOAD) ){
                                notificationService.sendSMSNotification1(transactionHeader1);
                            }

                            response.setMessage("Transaction posted successfully and account balance updated successfully.");
                            response.setStatusCode(HttpStatus.OK.value());
                            response.setEntity(transactionHeader1);
                        } else {
                            response.setMessage("This Transaction with transaction code " + transactionCode + " Not fully verified! Verification 1: " + transactionHeader.get().getVerifiedFlag() + " Verification 2: " + transactionHeader.get().getVerifiedFlag_2());
                            response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        }
                    } else {
                        response.setMessage("Transaction with transaction code " + transactionCode + " is already posted!");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    }
                } else {
                    response.setMessage("Transaction with transaction code " + transactionCode + " Not found");
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                }
            }
            return response;
        } catch (Exception e) {
            EntityResponse res = new EntityResponse<>();
            e.printStackTrace();
            res.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            res.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return res;
        }
    }



    public EntityResponse post1(String transactionCode) {
        try {
            EntityResponse response = new EntityResponse();
            String user = UserRequestContext.getCurrentUser();
            String entityId = EntityRequestContext.getCurrentEntityId();
            if (user.isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return response;
            } else if (entityId.isEmpty()) {
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return response;
            } else {
                Optional<TransactionHeader> transactionHeader = tranHeaderRepository.findByTransactionCode(transactionCode);
                if (transactionHeader.isPresent()) {
//                check if transaction is already posted
                    if (transactionHeader.get().getPostedFlag() == 'N') {
//                    if (transactionHeader.get().getAcknowledgedFlag() == null){
//                        transactionHeader.get().setAcknowledgedFlag('N');
//                    }
                        if ((transactionHeader.get().getAcknowledgedFlag() == null || transactionHeader.get().getAcknowledgedFlag() != 'Y')
                                && transactionHeader.get().getTransactionType().equalsIgnoreCase("Fund Teller")) {

                            response.setMessage("The receiving teller must acknowledge receipt first before posting");
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        }
//                        else if (transactionHeader.get().getVerifiedFlag() == 'Y' && transactionHeader.get().getVerifiedFlag_2() == 'Y') {
                            //                check if transaction is verified
                            TransactionHeader transactionHeader1 = transactionHeader.get();
                            transactionHeader1.setPostedFlag('Y');
                            transactionHeader1.setPostedBy(user);
                            transactionHeader1.setStatus(CONSTANTS.POSTED);
                            transactionHeader1.setPostedTime(new Date());
                            if (transactionHeader1.getReversedFlag() == 'Y') {
                                Optional<TransactionHeader> reversedHeaderOptional = tranHeaderRepository.findByTransactionCode(transactionHeader1.getReversalTransactionCode());
                                if (reversedHeaderOptional.isPresent()) {
                                    TransactionHeader reversedHeader = reversedHeaderOptional.get();
                                    reversedHeader.setReversalPostedFlag('Y');
                                    transactionHeader1.setReversalPostedFlag('Y');
                                    tranHeaderRepository.save(reversedHeader);
                                }
                            }
                            tranHeaderRepository.save(transactionHeader1);
//                Update account balance
                            transactionProcessing.updateAccountBalances(transactionHeader1.getPartTrans());
//                Send notifications
                        System.out.println("About to send MAIL 4");
                            notificationService.sendEmailNotification(transactionHeader1);
//                Send SMS
//                            TODO: post kra
                            kraservice.sendTransactionToKra(transactionHeader1);

                            if(transactionHeader1.getTransactionType().equalsIgnoreCase(CONSTANTS.CASH_WITHDRAWAL)
                                    || transactionHeader1.getTransactionType().equalsIgnoreCase(CONSTANTS.CASH_DEPOSIT)
                                    || transactionHeader1.getTransactionType().equalsIgnoreCase(CONSTANTS.TRANSFER)
                                    || transactionHeader1.getTransactionType().equalsIgnoreCase(CONSTANTS.SALARY_UPLOAD) || transactionHeader1.getTransactionType().equalsIgnoreCase(CONSTANTS.BATCH_UPLOAD) ){
                                notificationService.sendSMSNotification1(transactionHeader1);
                            }
                            response.setMessage("Transaction posted successfully and account balance updated successfully.");
                            response.setStatusCode(HttpStatus.OK.value());
                            response.setEntity(transactionHeader1);
//                        }
//                        else {
//                            response.setMessage("Transaction with transaction code " + transactionCode + " Not fully verified! Verification 1: " + transactionHeader.get().getVerifiedFlag() + " Verification 2: " + transactionHeader.get().getVerifiedFlag_2());
//                            response.setStatusCode(HttpStatus.NOT_FOUND.value());
//                        }
                    }
                    else {
                        response.setMessage("Transaction with transaction code " + transactionCode + " is already posted!");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    }
                } else {
                    response.setMessage("Transaction with transaction code " + transactionCode + " Not found");
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                }
            }
            return response;
        } catch (Exception e) {
            EntityResponse res = new EntityResponse<>();
            e.printStackTrace();
            res.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            res.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return res;
        }
    }


    public Page<TransactionHeader> findTransactions(int offset, int pageSize, String field) {
        Page<TransactionHeader> transactionHeaders = tranHeaderRepository.findAll(PageRequest.of(offset, pageSize).withSort(Sort.by(field)));
        return transactionHeaders;
    }

    public EntityResponse fectAll() {
        try {
            EntityResponse response = new EntityResponse();
            response.setStatusCode(HttpStatus.OK.value());
            response.setMessage(HttpStatus.OK.getReasonPhrase());
            response.setEntity(tranHeaderRepository.findAll());
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse<?> transactionsCount() {
        try {
            EntityResponse response = new EntityResponse();
            String user = UserRequestContext.getCurrentUser();
            String entityId = EntityRequestContext.getCurrentEntityId();
            if (user.isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
            } else if (entityId.isEmpty()) {
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            } else {
                Integer totalTransactions = tranHeaderRepository.countAllTransactios();
                if (totalTransactions > 0) {
                    response.setMessage("Total Unverified Transactions is " + totalTransactions);
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(totalTransactions);
                } else {
                    response.setMessage("Total Unverified Transactions is " + totalTransactions);
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setEntity("");
                }
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse requestApproval(String transactionCode, String approver) {
        try {
            EntityResponse response = new EntityResponse();
            String user = UserRequestContext.getCurrentUser();
            String entityId = EntityRequestContext.getCurrentEntityId();
            if (user.isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return response;
            } else if (entityId.isEmpty()) {
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return response;
            } else if (user.equalsIgnoreCase(approver)) {
                response.setMessage("You cannot send approval request to yourself");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return response;
            } else {
                Optional<TransactionHeader> transactionHeader = tranHeaderRepository.findByTransactionCode(transactionCode);
                if (transactionHeader.isPresent()) {
                    if (transactionHeader.get().getVerifiedFlag() == 'Y' && transactionHeader.get().getVerifiedFlag_2() == 'Y') {
                        response.setMessage("Already approved");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        return response;
                    } else {
                        TransactionHeader transactionHeader1 = transactionHeader.get();
                        transactionHeader1.setApprovalSentBy(user);
                        transactionHeader1.setApprovalSentTo(approver);
                        transactionHeader1.setApprovalSentFlag('Y');
                        transactionHeader1.setApprovalSentTime(new Date());
                        tranHeaderRepository.save(transactionHeader1);
                        response.setMessage("Approval request sent successfully.");
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(transactionHeader1);
                    }
                } else {
                    response.setMessage("Transaction with transaction code " + transactionCode + " Not found");
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                }
            }
            return response;
        } catch (Exception e) {
            EntityResponse res = new EntityResponse<>();
            e.printStackTrace();
            res.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            res.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return res;
        }
    }

    public EntityResponse rejectTransaction(String transactionCode, String rejectionReason) {
        try {
            EntityResponse response = new EntityResponse();
            String user = UserRequestContext.getCurrentUser();
            String entityId = EntityRequestContext.getCurrentEntityId();
            if (user.isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return response;
            } else if (entityId.isEmpty()) {
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return response;
            } else {
                Optional<TransactionHeader> transactionHeader = tranHeaderRepository.findByTransactionCode(transactionCode);
                if (transactionHeader.isPresent()) {
                    TransactionHeader transactionHeader1 = transactionHeader.get();
                    if (transactionHeader1.getRejectedFlag().equals('N')) {
                        if (transactionHeader1.getPostedFlag().equals('Y')) {
                            response.setMessage("Failed! Transaction with transaction code " + transactionCode + " was posted on " + transactionHeader1.getPostedTime());
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        } else {
                            transactionHeader1.setRejectedBy(user);
                            transactionHeader1.setRejectedFlag('Y');
                            transactionHeader1.setRejectedTime(new Date());
                            transactionHeader1.setRejectedReason(rejectionReason);
                            tranHeaderRepository.save(transactionHeader1);

                            response.setMessage("Transaction rejected successfully.");
                            response.setStatusCode(HttpStatus.OK.value());
                            response.setEntity(transactionHeader1);
                        }
                    } else {
                        response.setMessage("Failed! Transaction with transaction code " + transactionCode + " was rejected on " + transactionHeader1.getRejectedTime());
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    }
                } else {
                    response.setMessage("Failed! Transaction with transaction code " + transactionCode + " Not found");
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                }
            }

            return response;
        } catch (Exception e) {
            EntityResponse res = new EntityResponse<>();
            e.printStackTrace();
            res.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            res.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return res;
        }
    }



    public EntityResponse feeCharges(String acid){
        try {
            System.out.println("In Tran service 4 to collect sms charges");
            EntityResponse res= new EntityResponse<>();

            accountClosureService.smsCharges(acid);

        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
        return null;
    }


}
