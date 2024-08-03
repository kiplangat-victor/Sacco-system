package com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader;


import com.emtechhouse.accounts.Models.Accounts.Account;
import com.emtechhouse.accounts.Models.Accounts.AccountRepository;
import com.emtechhouse.accounts.Models.Accounts.AccountServices.AccountService;
import com.emtechhouse.accounts.Models.Accounts.OfficeAccount.OfficeAccount;
import com.emtechhouse.accounts.TransactionService.ChequeComponent.Chequebook;
import com.emtechhouse.accounts.TransactionService.ChequeComponent.ChequebookRepo;
import com.emtechhouse.accounts.TransactionService.ChequeComponent.ChequebookService;
import com.emtechhouse.accounts.TransactionService.MpesaDeposits.MpesaDepositsAPIRequest;
import com.emtechhouse.accounts.TransactionService.MpesaDeposits.MpesaDepositsAPIRequestRepo;
import com.emtechhouse.accounts.TransactionService.MpesaWithdrawals.MpesaWithdrawalAPIRequest;
import com.emtechhouse.accounts.TransactionService.MpesaWithdrawals.MpesaWithdrawalAPIRequestRepo;
import com.emtechhouse.accounts.TransactionService.Requests.MinistatementInterface;
import com.emtechhouse.accounts.TransactionService.Requests.TransactionStatementInterface;
import com.emtechhouse.accounts.TransactionService.Responses.AccountBalanceInterface;
import com.emtechhouse.accounts.TransactionService.SalaryUploads.Salaryupload;
import com.emtechhouse.accounts.TransactionService.SalaryUploads.SalaryuploadRepo;
import com.emtechhouse.accounts.TransactionService.SalaryUploads.SalaryuploadService;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.Requests.USSDTransfer;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.ChargePartran.ChargePartran;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.PartTrans.PartTran;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.emtechhouse.accounts.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.accounts.Utils.HttpInterceptor.UserRequestContext;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

//@CrossOrigin
@Slf4j
@RequestMapping("transactions")
@RestController
public class TransactionsController{
    @Autowired
    private TranHeaderService transactionHeaderService;
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private MpesaDepositsAPIRequestRepo mpesaDepositsAPIRequestRepo;

    @Autowired
    private MpesaWithdrawalAPIRequestRepo mpesaWithdrawalAPIRequestRepo;

    @Autowired
    private TranHeaderRepository transactionRepo;
    @Autowired
    private TransactionServiceCaller transactionServiceCaller;
    @Autowired
    private SalaryuploadRepo salaryuploadRepo;

    @Autowired
    private SalaryuploadService salaryuploadService;

    @Autowired
    private ChequebookService chequebookService;
    @Autowired
    private ChequebookRepo chequebookRepo;

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    private String today = format.format(new Date());
    private static final DecimalFormat df = new DecimalFormat("0.00");

    @GetMapping("{transactionCode}")
    public ResponseEntity<?> retrieveTransaction(@PathVariable("transactionCode") String transactionCode) {

        EntityResponse response = new EntityResponse<>();
        if (UserRequestContext.getCurrentUser().isEmpty()) {

            response.setMessage("User Name not present in the Request Header");
            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            response.setEntity("");
            return new ResponseEntity<>(response, HttpStatus.OK);

        } else if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
            response.setMessage("Entity not present in the Request Header");
            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            response.setEntity("");
            return new ResponseEntity<>(response, HttpStatus.OK);

        } else {

            TransactionHeader transactionHeader = transactionHeaderService.retrieveTranHeader(transactionCode);

            if (Objects.isNull(transactionHeader)) {
                response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                response.setEntity("");
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            } else {
                response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                response.setEntity(transactionHeader);
                response.setStatusCode(HttpStatus.OK.value());
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }
    }

    @PostMapping("enter")
    public ResponseEntity<?> enterTransaction(@RequestBody TransactionHeader transactionHeader) throws IOException {
        log.info("Entering transaction...");
        try {
            EntityResponse response = new EntityResponse();
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                List<PartTran> orgpartTranList = transactionHeader.getPartTrans();
                List<ChargePartran> chargepartrans = new ArrayList<>();
                List<ChargePartran> partTransRes = new ArrayList<>();
                String transactionType = transactionHeader.getTransactionType();
                log.info("Transaction type :: << " + transactionType + " >>");

                if (transactionType.equalsIgnoreCase("Process Cheque")) {
                    //validate Cheque
                    String chequeNo = transactionHeader.getChequeInstruments().get(0).getInstrumentNo();
                    Integer leafNo = transactionHeader.getChequeInstruments().get(0).getLeafNo();
                    EntityResponse res = chequebookService.validateCheck(chequeNo, leafNo);
                    if (res.getStatusCode() == 200) {
                        log.info("Check is valid");
                    } else {
                        response.setMessage(res.getMessage());
                        response.setStatusCode(res.getStatusCode());
                        response.setEntity(res.getEntity());
                        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);
                    }

                }
                for (PartTran patran : orgpartTranList) {

                    //TODO:Check if acounts exists
                    Integer isExist = transactionRepo.checkAccountExistance(patran.getAcid());
                    String accountStatus = transactionRepo.checkAccountStatus(patran.getAcid());
                    Character verifiedStatus = transactionRepo.checkVerifiedStatus(patran.getAcid());
//                    patran.setCurrency(accountRepository.findByAccountId(patran.getAcid()).get().getCurrency());
                    System.out.println("------------------------------------");
                    System.out.println(patran);
                    patran.setCurrency("Ksh");

                    if (isExist == 1) {
                        if (accountStatus.equalsIgnoreCase("ACTIVE")) {
                            if (verifiedStatus == 'Y') {


                                log.info("Account " + patran.getAcid() + " Found");
                                //get account details,status,balance

                                String currency = transactionHeader.getCurrency();
                                String tranType = patran.getPartTranType();

                                log.info("Proceeding to collect charges...");
//

                                if (tranType.equalsIgnoreCase("Debit")) {

                                    boolean iswithdrawable = transactionRepo.checkDebitpermision(patran.getAcid());
                                    if (iswithdrawable) {
                                        log.info("Account allows Debiting.");
                                    } else {
                                        log.info("Account Debiting NOT ALLOWED");
                                        response.setMessage("Sorry, withdrawals is NOT allowed from account " + patran.getAcid());
                                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);
                                    }
//                                    log.info("Partran Type ::<< " + tranType + " >>");
                                    String acid = patran.getAcid();


                                    if (transactionType.equalsIgnoreCase("Cash Withdrawal")) {
                                        log.info("Cash withdrawal..");
                                        Double withdrawalAmount = patran.getTransactionAmount();
                                        String transType = "WithdrawalFee";
                                        partTransRes = transactionServiceCaller.getWithdrwalChargePartran(transType, withdrawalAmount, acid);
                                        System.out.println("charge partran " + partTransRes);
                                    } else if (transactionType.equalsIgnoreCase("Transfer")) {
                                        log.info("Cash Transfer...");
                                        partTransRes = new ArrayList<>();
                                    } else if (transactionType.equalsIgnoreCase("Cash Deposit")) {
                                        log.info("Cash Deposit...");
                                        partTransRes = new ArrayList<>();
                                    } else if (transactionType.equalsIgnoreCase("SALARY_PROCESSING")) {
                                        log.info("SALARY_PROCESSING...");
                                        partTransRes = new ArrayList<>();
                                    } else if (transactionType.equalsIgnoreCase("SYSTEM") || transactionType.equalsIgnoreCase("USSD")) {
                                        log.info("System induced transactions..");
                                        partTransRes = new ArrayList<>();
                                    } else if (transactionType.equalsIgnoreCase("SYSTEM") || transactionType.equalsIgnoreCase("USSD")) {
                                        log.info("System induced transactions..");
                                        partTransRes = new ArrayList<>();
                                    } else if (transactionType.equalsIgnoreCase("Process Cheque")) {
                                        log.info("Cheque ");



                                        partTransRes = new ArrayList<>();
//                                        continue;
                                    }
                                } else {
//                                    log.info("Trantype == " + tranType);
                                }


                            } else {
                                log.error("Account " + patran.getAcid() + " is  not verified");
                                response.setMessage("Account " + patran.getAcid() + " is not verified");
                                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);
                            }
                        } else {
                            log.error("Account " + patran.getAcid() + " is " + accountStatus);
                            response.setMessage("Account " + patran.getAcid() + " is " + accountStatus);
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);
                        }
                    } else {
                        log.error("Account " + patran.getAcid() + " does not Exist");
                        response.setMessage("Account " + patran.getAcid() + " does not Exist");
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

                    }
                }
                if (partTransRes.isEmpty()) {
                    log.info("Charge partrans is empty");
                    chargepartrans = new ArrayList<>();
                } else if (partTransRes.size() > 0) {
                    log.info("Charge Partrans size = " + chargepartrans.size());
                    for (ChargePartran p : partTransRes) {
                        log.info("Combining charge partrans");
                        chargepartrans.add(p);
                    }
                }
                log.info("Charge collection complete");
                if (chargepartrans.isEmpty()) {
                    log.warn("Charge partrans is empty");
                    transactionHeader.setChargePartran(chargepartrans);
                } else {
                    log.info("Charge partrans size is " + chargepartrans.size());
                    transactionHeader.setChargePartran(chargepartrans);
                }
                List<ChargePartran> chargePartranList = transactionHeader.getChargePartran();
                List<PartTran> partTranList = transactionHeader.getPartTrans();
                //Collecting all partrans
                List<PartTran> allPartran = new ArrayList<>();
                log.info("Charge list size " + chargePartranList.size());
                if (chargePartranList.size() > 0) {
                    List<PartTran> newPartransList = new ArrayList<>();
                    for (ChargePartran cp : chargePartranList) {
                        PartTran p = new PartTran();
                        p.setTransactionDate(cp.getTransactionDate());
                        p.setTransactionParticulars(cp.getTransactionParticulars());
                        p.setAcid(cp.getAcid());
                        p.setCurrency(cp.getCurrency());
                        p.setIsoFlag(cp.getIsoFlag());
                        p.setTransactionAmount(cp.getTransactionAmount());
                        p.setPartTranType(cp.getPartTranType());
                        p.setAccountBalance(cp.getAccountBalance());
                        p.setExchangeRate(cp.getExchangeRate());
                        newPartransList.add(p);

                    }
                    allPartran.addAll(newPartransList);
                    allPartran.addAll(partTranList);
                } else {
                    allPartran.addAll(partTranList);
                }


//                System.out.println("Balance checking....");
                Boolean bc = false;
                try {
                    bc = transactionHeaderService.balanceCheck(allPartran);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error(e.getMessage());
                }
                System.out.println("Balance check pass " + bc);
                if (bc) {
                    transactionHeader.setEnteredBy(UserRequestContext.getCurrentUser());
                    transactionHeader.setEnteredFlag(CONSTANTS.YES);
                    transactionHeader.setEnteredTime(new Date());
                    transactionHeader.setDeletedFlag(CONSTANTS.NO);
                    transactionHeader.setStatus("Pending");
                    transactionHeader.setEntityId(EntityRequestContext.getCurrentEntityId());
                    EntityResponse res = transactionHeaderService.saveTransactionHeader(transactionHeader);
                    response.setMessage(res.getMessage());
                    response.setStatusCode(res.getStatusCode());
                    response.setEntity(res.getEntity());
                } else {
                    response.setStatusCode(HttpStatus.EXPECTATION_FAILED.value());
                    response.setMessage("Insufficient Amount");
                    response.setEntity("");
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }

                transactionHeader.setEnteredBy(UserRequestContext.getCurrentUser());
                transactionHeader.setEnteredFlag(CONSTANTS.YES);
                transactionHeader.setEnteredTime(new Date());
                transactionHeader.setDeletedFlag(CONSTANTS.NO);
                transactionHeader.setStatus("Pending");
                transactionHeader.setEntityId(EntityRequestContext.getCurrentEntityId());
                EntityResponse res = transactionHeaderService.saveTransactionHeader(transactionHeader);
                response.setMessage(res.getMessage());
                response.setStatusCode(res.getStatusCode());
                response.setEntity(res.getEntity());

            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {

            EntityResponse response = new EntityResponse();
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping("post/{transactionCode}")
    public ResponseEntity<?> postTransaction(@PathVariable("transactionCode") String transactionCode) {
        try {
            EntityResponse response = new EntityResponse();
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                Optional<TransactionHeader> transactionCheck = transactionRepo.findByEntityIdAndTransactionCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), transactionCode, 'N');
                if (transactionCheck.isPresent()) {
                    TransactionHeader transaction = transactionCheck.get();
                    if (transaction.getPostedFlag() == 'Y') {
                        response.setMessage("Transaction is already Posted!");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        if(transaction.getVerifiedFlag()=='N'){
                            //check teller posting limit
                            String tellerAccount = transaction.getTellerAccount();
                            String transactionType = transaction.getTransactionType();
                            log.info("Checking teller posting limit...");
//                            Double limit = transactionServiceCaller.checkTsdsellerlimit(tellerAccount, transactionType);
//                            TODO: TO BE CHANGED

                            Double drLimit = 99999999999999999.0;
                            Double crLimit = 99999999999999999.0;
                            Double transferDrLimit = 99999999999999999.0;
                            Double transferCrLimit = 99999999999999999.0;

                            EntityResponse<OfficeAccount> account = accountService.getOfficeAccountSpecificDetails(tellerAccount);
                            if (account.getStatusCode() == HttpStatus.FOUND.value()){
                                drLimit = account.getEntity().getCashLimitDr();
                                crLimit = account.getEntity().getCashLimitCr();
                                transferDrLimit = account.getEntity().getTransferLimitCr();
                                transferCrLimit = account.getEntity().getTransferLimitCr();
                                if (transactionType.equalsIgnoreCase("Cash Withdrawal")) {
                                    log.info("Cash withdrawal..");

                                    PartTran p = transaction.getPartTrans().stream().filter(partTran -> partTran.getPartTranType().equalsIgnoreCase("Debit")).findAny().orElse(null);
                                    Double debitAmount = p.getTransactionAmount();
                                    log.info("debit amount = " + debitAmount + " :: " + drLimit);
                                    if (debitAmount > drLimit) {
                                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                        response.setMessage("You cannot withdraw amount exceeding your limit of " + drLimit);
                                        response.setEntity("");
                                        return new ResponseEntity<>(response, HttpStatus.OK);
                                    } else {
                                        transaction.setVerifiedBy(UserRequestContext.getCurrentUser());
                                        transaction.setVerifiedFlag(CONSTANTS.YES);
                                        transaction.setVerifiedTime(new Date());
                                        log.info("Posting Accepted...");
                                    }
                                } else if (transactionType.equalsIgnoreCase("Transfer")
                                        || transactionType.equalsIgnoreCase("SALARY_PROCESSING")) {
                                    log.info("Cash Transfer...");
                                    PartTran p = transaction.getPartTrans().stream().filter(partTran -> partTran.getPartTranType().equalsIgnoreCase("Debit")).findAny().orElse(null);
                                    Double debitAmount = p.getTransactionAmount();
                                    log.info("debit amount = " + debitAmount + " :: " + transferDrLimit);

                                    if (debitAmount > transferDrLimit) {

                                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                        response.setMessage("You cannot Transfer amount exceeding your limit of " + drLimit);
                                        response.setEntity("");
                                        return new ResponseEntity<>(response, HttpStatus.OK);

                                    } else {
                                        log.info("Posting Accepted...");
                                        transaction.setVerifiedBy(UserRequestContext.getCurrentUser());
                                        transaction.setVerifiedFlag(CONSTANTS.YES);
                                        transaction.setVerifiedTime(new Date());

                                    }
                                } else if (transactionType.equalsIgnoreCase("Cash Deposit")) {
                                    log.info("Cash deposit");
                                    PartTran p = transaction.getPartTrans().stream().filter(partTran -> partTran.getPartTranType().equalsIgnoreCase("Debit")).findAny().orElse(null);
                                    Double debitAmount = p.getTransactionAmount();
                                    if (debitAmount > drLimit) {
                                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                        response.setMessage("You cannot deposit amount exceeding your limit of " + drLimit);
                                        response.setEntity("");
                                        return new ResponseEntity<>(response, HttpStatus.OK);

                                    } else {
                                        log.info("Posting Accepted...");

                                        transaction.setVerifiedBy(UserRequestContext.getCurrentUser());
                                        transaction.setVerifiedFlag(CONSTANTS.YES);
                                        transaction.setVerifiedTime(new Date());

                                    }
                                } else if (transactionType.equalsIgnoreCase("Fund Teller")) {
                                    log.info("Fund Teller");
                                    if (transaction.getAcknowledgedFlag() != 'Y') {
                                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                        response.setMessage("You cannot deposit amount exceeding your limit of " + drLimit);
                                        response.setEntity("");
                                        return new ResponseEntity<>(response, HttpStatus.OK);

                                    } else {
                                        log.info("Posting Accepted...");

                                        transaction.setVerifiedBy(UserRequestContext.getCurrentUser());
                                        transaction.setVerifiedFlag(CONSTANTS.YES);
                                        transaction.setVerifiedTime(new Date());

                                    }
                                }

//                                } else if (transactionType.equalsIgnoreCase("Cheque")) {
//                                    PartTran p = transaction.getPartTrans().stream().filter(partTran -> partTran.getPartTranType().equalsIgnoreCase("Debit")).findAny().orElse(null);
//                                    Double debitAmount = p.getTransactionAmount();
//                                    if (debitAmount > drLimit) {
//                                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                                        response.setMessage("You cannot deposit amount exceeding your limit of " + drLimit);
//                                        response.setEntity("");
//                                        return new ResponseEntity<>(response, HttpStatus.OK);
//
//                                    } else {
//                                        log.info("Posting Accepted...");
//
//                                        transaction.setVerifiedBy(UserRequestContext.getCurrentUser());
//                                        transaction.setVerifiedFlag(CONSTANTS.YES);
//                                        transaction.setVerifiedTime(new Date());
//
//                                    }
//                                }

                            }else{
                                transaction.setVerifiedBy(UserRequestContext.getCurrentUser());
                                transaction.setVerifiedFlag(CONSTANTS.YES);
                                transaction.setVerifiedTime(new Date());
                            }

                        }else if (transaction.getVerifiedFlag()=='Y'){
                            log.info("Transaction verified by " +transaction.getVerifiedBy());
                            log.info("Continue...");
                        }
                        List<ChargePartran> chargePartranList = transaction.getChargePartran();
                        List<PartTran> partTranList = transaction.getPartTrans();
                        //Collecting all partrans
                        List<PartTran> allPartran = new ArrayList<>();

                        if (chargePartranList.size() > 0) {
                            List<PartTran> newPartransList = new ArrayList<>();
                            for (ChargePartran cp : chargePartranList) {
                                PartTran p = new PartTran();
                                p.setTransactionDate(cp.getTransactionDate());
                                p.setTransactionParticulars(cp.getTransactionParticulars());
                                p.setAcid(cp.getAcid());
                                p.setCurrency(cp.getCurrency());
                                p.setIsoFlag(cp.getIsoFlag());
                                p.setTransactionAmount(cp.getTransactionAmount());
                                p.setPartTranType(cp.getPartTranType());
                                p.setAccountBalance(cp.getAccountBalance());
                                p.setExchangeRate(cp.getExchangeRate());
                                newPartransList.add(p);
                            }
                            allPartran.addAll(newPartransList);
                            allPartran.addAll(partTranList);
                        } else {
                            allPartran.addAll(partTranList);
                        }
                        System.out.println("Balance checking....");
                        Boolean bc = false;
                        try {
                            bc = transactionHeaderService.balanceCheck(allPartran);
                        } catch (Exception e) {
                            e.printStackTrace();
                            log.error(e.getMessage());
                        }
                        System.out.println("Balance check pass :: " + bc);
                        if (bc == true) {
                            transaction.setPostedBy(UserRequestContext.getCurrentUser());
                            transaction.setPostedFlag(CONSTANTS.YES);
                            transaction.setPostedTime(new Date());
                            EntityResponse responseSer = transactionHeaderService.updateTransaction(allPartran);
                            System.out.println(responseSer);
                            Integer statuscode = responseSer.getStatusCode();
                            if (statuscode == 200) {
                                response.setMessage("Transaction Posted!. Transaction Code: " + transaction.getTransactionCode());
                                response.setStatusCode(HttpStatus.OK.value());
                                transaction.setPostedBy(UserRequestContext.getCurrentUser());
                                transaction.setPostedFlag(CONSTANTS.YES);
                                transaction.setPostedTime(new Date());
                                if (transaction.getSalaryuploadCode() != null) {
                                    String salaryuploadcode = transaction.getSalaryuploadCode();
                                    log.info("updating salary uploads...");
                                    Optional<Salaryupload> s_check = salaryuploadRepo.findByEntityIdAndSalaryUploadCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), salaryuploadcode, CONSTANTS.NO);
                                    if (s_check.isPresent()) {
                                        s_check.get().setPostedBy(UserRequestContext.getCurrentUser());
                                        s_check.get().setPostedFlag(CONSTANTS.YES);
                                        s_check.get().setPostedTime(new Date());
                                        salaryuploadRepo.save(s_check.get());
                                    }

                                }
                                //update cheque info
//                                if(!transaction.getChequeInstruments().isEmpty()){
//                                    String chequeNo =  transaction.getChequeInstruments().get(0).getInstrumentNo();
//                                    Integer leafNo =  transaction.getChequeInstruments().get(0).getLeafNo();
//
//                                    Chequebook chequebook= chequebookRepo.findByMicrCodeAndEntityIdAndDeletedFlag(chequeNo,EntityRequestContext.getCurrentEntityId(),CONSTANTS.NO);
//                                    String lvsStat=chequebook.getChqLvsStat();
//                                    Character c = lvsStat.charAt(leafNo);
//                                    String newlvStat= lvsStat.replace(c,'p');
//                                    chequebook.setChqLvsStat(newlvStat);
//                                    chequebookRepo.save(chequebook);
//                                }

                                transaction.setStatus("Success");
                                //update partrans


                                transactionRepo.save(transaction);


                                return new ResponseEntity<>(response, HttpStatus.OK);
                            } else {
                                response.setStatusCode(responseSer.getStatusCode());
                                response.setMessage(responseSer.getMessage());
                                response.setEntity(responseSer.getEntity());
                                return new ResponseEntity<>(response, HttpStatus.OK);
                            }

//                            transactionRepo.save(transaction);

                        } else {
                            response.setStatusCode(HttpStatus.EXPECTATION_FAILED.value());
                            response.setMessage("Insufficient Amount");
                            response.setEntity("");
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        }
                    }
//                    }
                } else {
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());

                }
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            EntityResponse response = new EntityResponse();
            response.setMessage(e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity("");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("verify/{transactionCode}")
    public ResponseEntity<?> approval(@PathVariable String transactionCode) {
        log.info("Verifying transaction transactioncode : "+ transactionCode);
        EntityResponse response = new EntityResponse();
        try {

            if (UserRequestContext.getCurrentUser().isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                Optional<TransactionHeader> transactionCheck = transactionRepo.findByEntityIdAndTransactionCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), transactionCode, 'N');
                if (transactionCheck.isPresent()) {
                    TransactionHeader transaction = transactionCheck.get();
                    if (transaction.getVerifiedFlag() == 'Y') {
                        response.setMessage("Transaction is already Verified!");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        response.setMessage("Transaction Verified!. Transaction Code: " + transaction.getTransactionCode());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(transaction);
                        transaction.setVerifiedBy(UserRequestContext.getCurrentUser());
                        transaction.setVerifiedFlag(CONSTANTS.YES);
                        transaction.setVerifiedTime(new Date());
                        transactionRepo.save(transaction);
                        log.info("Transaction verified.");
                        return new ResponseEntity<>(response, HttpStatus.OK);
//                        }
                    }
                } else {
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setMessage("Transaction not found with transactioncode " + transactionCode);

                }
            }
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error -", e.getMessage());
            response.setMessage(e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("acknowledge/{transactionCode}")
    public ResponseEntity<?> acknowledge(@PathVariable String transactionCode) {
        log.info("Verifying transaction transactioncode : "+ transactionCode);
        EntityResponse response = new EntityResponse();
        try {

            if (UserRequestContext.getCurrentUser().isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                Optional<TransactionHeader> transactionCheck = transactionRepo.findByEntityIdAndTransactionCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), transactionCode, 'N');
                if (transactionCheck.isPresent()) {
                    TransactionHeader transaction = transactionCheck.get();
                    if (transaction.getAcknowledgedFlag() == 'Y') {
                        response.setMessage("Transaction is already Verified!");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        PartTran debitPartTran = transaction.getPartTrans().stream().filter(partTran -> partTran.getPartTranType().equalsIgnoreCase("Debit")).findAny().orElse(null);
                        response.setMessage("Transaction Verified!. Transaction Code: " + transaction.getTransactionCode());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(transaction);
                        transaction.setVerifiedBy(UserRequestContext.getCurrentUser());
                        transaction.setVerifiedFlag(CONSTANTS.YES);
                        transaction.setVerifiedTime(new Date());
                        transactionRepo.save(transaction);
                        log.info("Transaction verified.");
                        return new ResponseEntity<>(response, HttpStatus.OK);
//                        }
                    }
                } else {
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setMessage("Transaction not found with transactioncode " + transactionCode);

                }
            }
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error -", e.getMessage());
            response.setMessage(e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("transactions")
    public ResponseEntity<?> fetchTransaction(@RequestParam String fromDate, @RequestParam String toDate, @RequestParam String action) {
        try {
            EntityResponse response = new EntityResponse<>();
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                log.info("User Name not present in the Request Header");
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return new ResponseEntity<>(response, HttpStatus.OK);

            } else if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                log.info("Entity not present in the Request Header");
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return new ResponseEntity<>(response, HttpStatus.OK);

            } else {

                log.info("fetching transacton  details...");
                List<TransactionHeader> transactions = transactionHeaderService.filterBydate(fromDate, toDate, EntityRequestContext.getCurrentEntityId(), action);
                if (transactions.size() > 0) {
                    response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(transactions);
                    return new ResponseEntity<>(response, HttpStatus.OK);

                } else if (transactions.isEmpty()) {
                    response.setEntity(transactions);
                    response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                    response.setStatusCode(HttpStatus.NO_CONTENT.value());
                }
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error ", e.getMessage());
            EntityResponse response = new EntityResponse<>();
            response.setMessage(e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity("");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("transactions/by/account/and/year")
    public ResponseEntity<?> fetchTransaction(@RequestParam String acid, @RequestParam Integer year) {
        try {
            EntityResponse response = new EntityResponse<>();
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                log.info("User Name not present in the Request Header");
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return new ResponseEntity<>(response, HttpStatus.OK);

            } else if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                log.info("Entity not present in the Request Header");
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                List<TranHeaderRepository.Sharecapital> transactions = transactionRepo.findTransactionPerAccountAndYear(acid, year);
                if (transactions.size() > 0) {
                    response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(transactions);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else if (transactions.isEmpty()) {
                    response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                    response.setStatusCode(HttpStatus.NO_CONTENT.value());
                }
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error ", e.getMessage());
            EntityResponse response = new EntityResponse<>();
            response.setMessage(e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity("");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping(value = "verified")
    public ResponseEntity<EntityResponse> getVerified() {
        EntityResponse response = new EntityResponse<>();
        try {
            List<TransactionHeader> verifiedTransactions = transactionHeaderService.verifiedTransactions();
            if (verifiedTransactions.isEmpty()) {
                response.setMessage(HttpStatus.NO_CONTENT.getReasonPhrase());
                response.setStatusCode(HttpStatus.NO_CONTENT.value());

            } else if (verifiedTransactions.size() > 0) {

                response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                response.setStatusCode(HttpStatus.FOUND.value());
                response.setEntity(verifiedTransactions);

            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.EXPECTATION_FAILED.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("delete")
    public ResponseEntity<EntityResponse> deleteTransaction(@RequestParam Long id) {
        EntityResponse response = new EntityResponse<>();
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                log.info("User Name not present in the Request Header");
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return new ResponseEntity<>(response, HttpStatus.OK);

            } else if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                log.info("Entity not present in the Request Header");
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return new ResponseEntity<>(response, HttpStatus.OK);

            } else {
                log.info("Deleting transacton  details...");
                Optional<TransactionHeader> transactionHeader = transactionRepo.findById(id);
                if (transactionHeader.isPresent()) {
                    if (transactionHeader.get().getVerifiedFlag() == 'Y') {
                        response.setMessage("You can not delete verified transaction");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else if (transactionHeader.get().getPostedFlag() == 'Y') {
                        response.setMessage("You can not delete posted transaction");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        transactionHeaderService.deleteTransactionByid(id);
                        response.setMessage(HttpStatus.OK.getReasonPhrase());
                        response.setStatusCode(HttpStatus.OK.value());
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }
                } else {
                    response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error ", e.getMessage());
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.EXPECTATION_FAILED.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }


    }

    @PutMapping("update")
    public ResponseEntity<?> updateTransaction(@RequestBody TransactionHeader transactionHeader) {
        EntityResponse response = new EntityResponse<>();
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                log.info("User Name not present in the Request Header");
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                log.info("Entity not present in the Request Header");
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                log.info("Updating transacton  details...");
                EntityResponse res = transactionHeaderService.updateTransaction(transactionHeader);
                response.setMessage(res.getMessage());
                response.setStatusCode(res.getStatusCode());
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error ", e.getMessage());
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.EXPECTATION_FAILED.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }


    }

    @GetMapping("acount_balance/{acid}")
    public ResponseEntity<?> getAccountBalance(@PathVariable String acid) {
        EntityResponse response = new EntityResponse<>();
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                log.info("User Name not present in the Request Header");
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return new ResponseEntity<>(response, HttpStatus.OK);

            } else if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                log.info("Entity not present in the Request Header");
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return new ResponseEntity<>(response, HttpStatus.OK);

            } else {

                Integer isExist = transactionRepo.checkAccountExistance(acid);
                if (isExist == 1) {
                    log.info("Account found in transfer ");
                    log.info("Fetching account balance...");
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    AccountBalanceInterface balance = transactionRepo.getbalance(acid);
                    response.setEntity(balance);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    log.info("Account not found...");
                    response.setMessage("Account does not exist");
                    response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error ", e.getMessage());
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.EXPECTATION_FAILED.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping("system")
    public ResponseEntity<EntityResponse> systemTransaction(@RequestBody TransactionHeader transactionHeader) {
        log.info("---System transaction initiated...----");
        try {
            EntityResponse response = new EntityResponse();
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);

            } else {
                try {


                    transactionHeader.setTransactionCode(transactionHeaderService.generatecSystemCode(6));
                    transactionHeader.setDeletedFlag(CONSTANTS.NO);


                    Optional<TransactionHeader> transactionCheck = transactionRepo.findByEntityIdAndTransactionCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), transactionHeader.getTransactionCode(), 'N');
                    if (transactionCheck.isPresent()) {
                        response.setMessage("Transaction Exist!");
                        response.setStatusCode(HttpStatus.CONFLICT.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);

                    } else {

                        TransactionHeader transaction = transactionHeader;
                        String transactionType = transaction.getTransactionType();
                        List<PartTran> orgpartTranList = transaction.getPartTrans();
                        List<ChargePartran> chargepartrans = new ArrayList<>();
                        List<ChargePartran> partTransRes = new ArrayList<>();
                        for (PartTran patran : orgpartTranList) {
                            //get account details,status,balance

                            Integer isExist = transactionRepo.checkAccountExistance(patran.getAcid());
                            String accountStatus = transactionRepo.checkAccountStatus(patran.getAcid());
                            Character verifiedStatus = transactionRepo.checkVerifiedStatus(patran.getAcid());
                            if (isExist == 1) {
                                if (accountStatus.equalsIgnoreCase("ACTIVE")) {
                                    if (verifiedStatus == 'Y') {


                                        log.info("Account " + patran.getAcid() + " Found");
                                        //get account details,status,balance

                                        String currency = transactionHeader.getCurrency();
                                        String tranType = patran.getPartTranType();

                                        log.info("Proceeding to collect charges...");
//

                                        if (tranType.equalsIgnoreCase("Debit")) {
//                                            log.info("Partran Type ::<< " + tranType + " >>");
                                            String acid = patran.getAcid();

                                            if (transactionType.equalsIgnoreCase("Cash Withdrawal")) {
                                                log.info("Cash withdrawal..");
                                                Double withdrawalAmount = patran.getTransactionAmount();
                                                String transType = "WithdrawalFee";
                                                partTransRes = transactionServiceCaller.getWithdrwalChargePartran(transType, withdrawalAmount, acid);
                                                System.out.println("charge partran " + partTransRes);
                                            } else if (transactionType.equalsIgnoreCase("Transfer")) {
                                                log.info("Cash Transfer...");
                                                partTransRes = new ArrayList<>();
                                            } else if (transactionType.equalsIgnoreCase("Cash Deposit")) {
                                                log.info("Cash Deposit...");
                                                partTransRes = new ArrayList<>();
                                            } else if (transactionType.equalsIgnoreCase("SYSTEM") || transactionType.equalsIgnoreCase("USSD")) {
                                                log.info("System induced transactions..");
                                                partTransRes = new ArrayList<>();
                                            } else if (transactionType.equalsIgnoreCase("SYSTEM") || transactionType.equalsIgnoreCase("USSD")) {
                                                log.info("System induced transactions..");
                                                partTransRes = new ArrayList<>();
                                            } else {
//                                                log.info("Unknown transaction type");
//                                        continue;
                                            }
                                        } else {
                                            log.info("Trantype == " + tranType);
                                        }


                                    } else {
//                                        log.error("Account " + patran.getAcid() + " is  not verified");
                                        response.setMessage("Account " + patran.getAcid() + " is not verified");
                                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);
                                    }
                                } else {
//                                    log.error("Account " + patran.getAcid() + " is " + accountStatus);
                                    response.setMessage("Account " + patran.getAcid() + " is " + accountStatus);
                                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);
                                }
                            } else {
//                                log.error("Account " + patran.getAcid() + " does not Exist");
                                response.setMessage("Account " + patran.getAcid() + " does not Exist");
                                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

                            }

                        }
                        if (partTransRes.isEmpty()) {
//                            log.info("Charge partrans is empty");
                            chargepartrans = new ArrayList<>();
                        } else if (partTransRes.size() > 0) {
//                            log.info("Charge Partrans size = " + chargepartrans.size());
                            for (ChargePartran p : partTransRes) {

//                                log.info("Combining charge partrans");
                                chargepartrans.add(p);

                            }

                        }

//                        log.info("Charge collection complete");
                        if (chargepartrans.isEmpty()) {
//                            log.warn("Charge partrans is empty");
                            transactionHeader.setChargePartran(chargepartrans);

                        } else {
//                            log.info("Charge partrans size is " + chargepartrans.size());
                            transactionHeader.setChargePartran(chargepartrans);

                        }

                        //Collecting all partrans
                        List<PartTran> allPartran = new ArrayList<>();
                        List<ChargePartran> chargePartranList = transaction.getChargePartran();
//                        List<PartTran> partTranList = transaction.getPartTrans();
                        //Collecting all partrans


                        if (chargePartranList.size() > 0) {

                            List<PartTran> newPartransList = new ArrayList<>();
                            for (ChargePartran cp : partTransRes) {
                                PartTran p = new PartTran();
                                p.setTransactionDate(cp.getTransactionDate());
                                p.setTransactionParticulars(cp.getTransactionParticulars());
                                p.setAcid(cp.getAcid());
                                p.setCurrency(cp.getCurrency());
                                p.setIsoFlag(cp.getIsoFlag());
                                p.setTransactionAmount(cp.getTransactionAmount());
                                p.setPartTranType(cp.getPartTranType());
                                p.setAccountBalance(cp.getAccountBalance());
                                p.setExchangeRate(cp.getExchangeRate());
                                newPartransList.add(p);

                            }
                            allPartran.addAll(newPartransList);
                            allPartran.addAll(orgpartTranList);
                        } else {
                            allPartran.addAll(orgpartTranList);
                        }
                        transaction.setChargePartran(chargepartrans);
                        transaction.setEnteredBy("system");
                        transaction.setEnteredFlag(CONSTANTS.YES);
                        transaction.setEnteredTime(new Date());
                        transaction.setEntityId(EntityRequestContext.getCurrentEntityId());
                        transaction.setPostedBy("system");
                        transaction.setPostedTime(new Date());
                        transaction.setPostedFlag(CONSTANTS.YES);

//                        System.out.println("Balance checking....");
                        Boolean bc = false;
                        try {
                            bc = transactionHeaderService.balanceCheck(allPartran);
                        } catch (Exception e) {
                            e.printStackTrace();
                            log.error(e.getMessage());
                        }
//                        System.out.println("Balance check pass " + bc);
                        if (bc == true) {

                            transaction.setPostedBy(UserRequestContext.getCurrentUser());
                            transaction.setPostedFlag(CONSTANTS.YES);
                            transaction.setPostedTime(new Date());

                            EntityResponse responseSer = transactionHeaderService.updateTransaction(allPartran);
                            Integer statuscode = responseSer.getStatusCode();
                            if (statuscode == 200) {
                                response.setMessage("Transaction Posted!. Transaction Code: " + transaction.getTransactionCode());
                                response.setStatusCode(HttpStatus.OK.value());
                                response.setEntity(transaction.getTransactionCode());
                                transaction.setPostedBy(UserRequestContext.getCurrentUser());
                                transaction.setPostedFlag(CONSTANTS.YES);
                                transaction.setPostedTime(new Date());
                                transaction.setStatus("Success");


                            } else {
                                response.setStatusCode(responseSer.getStatusCode());
                                response.setMessage(responseSer.getMessage());
                                response.setEntity(responseSer.getEntity());
                                transaction.setStatus("Failed");

                            }


                        } else {
                            response.setStatusCode(HttpStatus.EXPECTATION_FAILED.value());
                            response.setMessage("Transaction failed.Check your balance");
                            response.setEntity("");

                        }
                        transactionRepo.save(transaction);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
//                    log.error("Error " + e.getMessage());
                    response.setStatusCode(HttpStatus.EXPECTATION_FAILED.value());
                    response.setMessage("Transaction failed.Check your balance");
                    response.setEntity("");
                }
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } catch (Exception e) {
            EntityResponse response = new EntityResponse();
            response.setMessage(e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity("");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

    }
    @Transactional
    public ResponseEntity<EntityResponse> systemTransaction2(TransactionHeader transactionHeader) {
        log.info("---System transaction initiated...----");
        try {
            log.info("---Entered System transaction initiated...----");
            EntityResponse response = new EntityResponse();

            try {

                log.info("---Entered System transaction initiated check one...----");

                transactionHeader.setTransactionCode(transactionHeaderService.generatecSystemCode(6));
                transactionHeader.setDeletedFlag(CONSTANTS.NO);

                System.out.println(transactionHeader.getTransactionCode());

                Optional<TransactionHeader> transactionCheck = transactionRepo.findByEntityIdAndTransactionCodeAndDeletedFlag(CONSTANTS.SYSTEM_ENTITY, transactionHeader.getTransactionCode(), 'N');
                if (!transactionCheck.isPresent()) {
                    log.error("Transaction does not exist");
                    response.setMessage("Transaction Exist!");
                    response.setStatusCode(HttpStatus.CONFLICT.value());
                    response.setEntity("");
                    return new ResponseEntity<>(response, HttpStatus.OK);

                }
                else {
                    log.info("Transaction exists");

                    TransactionHeader transaction = transactionHeader;
                    String transactionType = transaction.getTransactionType();
                    List<PartTran> orgpartTranList = transaction.getPartTrans();
                    List<ChargePartran> chargepartrans = new ArrayList<>();
                    List<ChargePartran> partTransRes = new ArrayList<>();
                    for (PartTran patran : orgpartTranList) {
                        //get account details,status,balance

                        if (patran.getTransactionAmount() < 0.02) {
                            log.error("Transaction amount "+patran.getTransactionAmount()+" is less than allowable ");
                            response.setMessage("Transaction amount 1: "+patran.getTransactionAmount()+" is less than allowable");
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);
                        }

                        Integer isExist = transactionRepo.checkAccountExistance(patran.getAcid());
                        String accountStatus = transactionRepo.checkAccountStatus(patran.getAcid());
                        Character verifiedStatus = transactionRepo.checkVerifiedStatus(patran.getAcid());
                        if (isExist == 1) {
                            if (accountStatus.equalsIgnoreCase("ACTIVE")) {
                                if (verifiedStatus == 'Y') {


                                    log.info("Account " + patran.getAcid() + " Found");
                                    //get account details,status,balance

                                    String currency = transactionHeader.getCurrency();
                                    String tranType = patran.getPartTranType();

                                    log.info("Proceeding to collect charges...");
//

                                    if (tranType.equalsIgnoreCase("Debit")) {
                                        log.info("Partran Type ::<< " + tranType + " >>");
                                        String acid = patran.getAcid();

                                        if (transactionType.equalsIgnoreCase("Cash Withdrawal")) {
                                            log.info("Cash withdrawal..");
                                            Double withdrawalAmount = patran.getTransactionAmount();
                                            String transType = "WithdrawalFee";
                                            partTransRes = transactionServiceCaller.getWithdrwalChargePartran(transType, withdrawalAmount, acid);
                                            System.out.println("charge partran " + partTransRes);
                                        } else if (transactionType.equalsIgnoreCase("Transfer")) {
                                            log.info("Cash Transfer...");
                                            partTransRes = new ArrayList<>();
                                        } else if (transactionType.equalsIgnoreCase("Cash Deposit")) {
                                            log.info("Cash Deposit...");
                                            partTransRes = new ArrayList<>();
                                        } else if (transactionType.equalsIgnoreCase("SYSTEM")
                                                || transactionType.equalsIgnoreCase("USSD")
                                                || transactionType.equalsIgnoreCase(CONSTANTS.SYSTEM_REVERSAL)
                                        ) {
                                            log.info("System induced transactions..");
                                            partTransRes = new ArrayList<>();
                                        }  else {
                                            log.info("Unknown transaction type");
//                                        continue;
                                        }
                                    } else {
                                        log.info("Trantype == " + tranType);
                                    }


                                } else {
                                    log.error("Account " + patran.getAcid() + " is  not verified");
                                    response.setMessage("Account " + patran.getAcid() + " is not verified");
                                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);
                                }
                            } else {
                                log.error("Account " + patran.getAcid() + " is " + accountStatus);
                                response.setMessage("Account " + patran.getAcid() + " is " + accountStatus);
                                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);
                            }
                        } else {
                            log.error("Account " + patran.getAcid() + " does not Exist");
                            response.setMessage("Account " + patran.getAcid() + " does not Exist");
                            response.setStatusCode(HttpStatus.NOT_FOUND.value());
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

                        }

                    }
                    if (partTransRes.isEmpty()) {
                        log.info("Charge partrans is empty");
                        chargepartrans = new ArrayList<>();
                    } else if (partTransRes.size() > 0) {
                        log.info("Charge Partrans size = " + chargepartrans.size());
                        for (ChargePartran p : partTransRes) {
                            log.info("Combining charge partrans");
                            chargepartrans.add(p);
                        }
                    }

//                    log.info("Charge collection complete");
                    if (chargepartrans.isEmpty()) {
//                        log.warn("Charge partrans is empty");
                        transactionHeader.setChargePartran(chargepartrans);

                    } else {
                        log.info("Charge partrans size is " + chargepartrans.size());
                        transactionHeader.setChargePartran(chargepartrans);

                    }

                    //Collecting all partrans
                    List<PartTran> allPartran = new ArrayList<>();
                    List<ChargePartran> chargePartranList = transaction.getChargePartran();
//                        List<PartTran> partTranList = transaction.getPartTrans();
                    //Collecting all partrans


                    if (chargePartranList.size() > 0) {

                        List<PartTran> newPartransList = new ArrayList<>();
                        for (ChargePartran cp : partTransRes) {
                            PartTran p = new PartTran();
                            p.setTransactionDate(cp.getTransactionDate());
                            p.setTransactionParticulars(cp.getTransactionParticulars());
                            p.setAcid(cp.getAcid());
                            p.setCurrency(cp.getCurrency());
                            p.setIsoFlag(cp.getIsoFlag());
                            p.setTransactionAmount(cp.getTransactionAmount());
                            p.setPartTranType(cp.getPartTranType());
                            p.setAccountBalance(cp.getAccountBalance());
                            p.setExchangeRate(cp.getExchangeRate());
                            newPartransList.add(p);

                        }
                        allPartran.addAll(newPartransList);
                        allPartran.addAll(orgpartTranList);
                    } else {
                        allPartran.addAll(orgpartTranList);
                    }
                    transaction.setChargePartran(chargepartrans);
                    transaction.setEnteredBy("system");
                    transaction.setEnteredFlag(CONSTANTS.YES);
                    transaction.setEnteredTime(new Date());
                    transaction.setEntityId(CONSTANTS.SYSTEM_ENTITY);
                    transaction.setPostedBy("system");
                    transaction.setPostedTime(new Date());
                    transaction.setPostedFlag(CONSTANTS.YES);

                    System.out.println("Balance checking....");
                    Boolean bc = false;
                    try {
                        bc = transactionHeaderService.balanceCheck(transaction);
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error(e.getMessage());
                    }
                    System.out.println("Balance check pass " + bc);
                    if (bc) {
                        transaction.setPostedBy(CONSTANTS.SYSTEM_USERNAME);
                        transaction.setPostedFlag(CONSTANTS.YES);
                        transaction.setPostedTime(new Date());

                        EntityResponse responseSer = transactionHeaderService.updateTransaction(allPartran);
                        Integer statuscode = responseSer.getStatusCode();
                        if (statuscode == 200) {
                            response.setMessage("Transaction Posted!. Transaction Code: " + transaction.getTransactionCode());
                            response.setStatusCode(HttpStatus.OK.value());
                            response.setEntity(transaction.getTransactionCode());
                            transaction.setPostedBy(CONSTANTS.SYSTEM_USERNAME);
                            transaction.setPostedFlag(CONSTANTS.YES);
                            transaction.setPostedTime(new Date());
                            transaction.setStatus("Success");


                        } else {
                            response.setStatusCode(responseSer.getStatusCode());
                            response.setMessage(responseSer.getMessage());
                            response.setEntity(responseSer.getEntity());
                            transaction.setStatus("Failed");
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        }
                    } else {
                        response.setStatusCode(HttpStatus.EXPECTATION_FAILED.value());
                        response.setMessage("Transaction failed.Check your balance");
                        response.setEntity(null);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }
                    transactionRepo.save(transaction);
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("Error " + e.getMessage());
                response.setStatusCode(HttpStatus.EXPECTATION_FAILED.value());
                response.setMessage("Transaction failed.Check your balance");
                response.setEntity("");
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
//            }
        } catch (Exception e) {

            EntityResponse response = new EntityResponse();
            response.setMessage(e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity("");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

    }
    @Transactional
    public ResponseEntity<EntityResponse> systemTransaction1(TransactionHeader transactionHeader) {
        log.info("---System transaction initiated...----");
        try {
            log.info("---Entered System transaction initiated...----");
            EntityResponse response = new EntityResponse();

            try {

                log.info("---Entered System transaction initiated check one...----");

                log.info("Tran Amount: "+transactionHeader.getTotalAmount());

                transactionHeader.setTransactionCode(transactionHeaderService.generatecSystemCode(6));
                transactionHeader.setDeletedFlag(CONSTANTS.NO);

                log.info("Transaction Code: "+transactionHeader.getTransactionCode());

                Optional<TransactionHeader> transactionCheck = transactionRepo.findByEntityIdAndTransactionCodeAndDeletedFlag(CONSTANTS.SYSTEM_ENTITY, transactionHeader.getTransactionCode(), 'N');
                if (transactionCheck.isPresent()) {
                    log.error("Transction does exist");
                    response.setMessage("Transaction Exist!");
                    response.setStatusCode(HttpStatus.CONFLICT.value());
                    response.setEntity("");
                    return new ResponseEntity<>(response, HttpStatus.OK);

                } else {
//                    log.info("Transction does not exist");

                    TransactionHeader transaction = transactionHeader;
                    log.info("Total Amt: "+transaction.getTotalAmount());
                    String transactionType = transaction.getTransactionType();
                    List<PartTran> orgpartTranList = transaction.getPartTrans();
                    List<ChargePartran> chargepartrans = new ArrayList<>();
                    List<ChargePartran> partTransRes = new ArrayList<>();
                    for (PartTran patran : orgpartTranList) {
                        //get account details,status,balance
                        log.info("Here Amount1: "+patran.getTransactionAmount());

                        if (patran.getTransactionAmount() < 0.02) {
                            log.error("Transaction amount 2: "+patran.getTransactionAmount()+" is less than allowable ");
                            response.setMessage("Transaction amount  "+patran.getTransactionAmount()+" is less than allowable");
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);
                        }

                        Integer isExist = transactionRepo.checkAccountExistance(patran.getAcid());
                        String accountStatus = transactionRepo.checkAccountStatus(patran.getAcid());
                        Character verifiedStatus = transactionRepo.checkVerifiedStatus(patran.getAcid());
                        if (isExist == 1) {
                            if (accountStatus.equalsIgnoreCase("ACTIVE")) {
                                if (verifiedStatus == 'Y') {


                                    log.info("The Account: " + patran.getAcid() + " Found");
                                    //get account details,status,balance

                                    String currency = transactionHeader.getCurrency();
                                    String tranType = patran.getPartTranType();

                                    log.info("Proceeding to collect charges...");
//

                                    if (tranType.equalsIgnoreCase("Debit")) {
                                        log.info("Partran Type ::<< " + tranType + " >>");
                                        String acid = patran.getAcid();

                                        System.out.println("The Acida: "+acid);

                                        if (transactionType.equalsIgnoreCase("Cash Withdrawal")) {
                                            log.info("Cash withdrawal..");
                                            Double withdrawalAmount = patran.getTransactionAmount();
                                            String transType = "WithdrawalFee";
                                            partTransRes = transactionServiceCaller.getWithdrwalChargePartran(transType, withdrawalAmount, acid);
                                            System.out.println("charge partran " + partTransRes);
                                        } else if (transactionType.equalsIgnoreCase("Transfer")) {
                                            log.info("Cash Transfer...");
                                            partTransRes = new ArrayList<>();
                                        } else if (transactionType.equalsIgnoreCase("Cash Deposit")) {
                                            log.info("Cash Deposit...");
                                            partTransRes = new ArrayList<>();
                                        } else if (transactionType.equalsIgnoreCase("SYSTEM")
                                                || transactionType.equalsIgnoreCase("USSD")
                                                || transactionType.equalsIgnoreCase(CONSTANTS.SYSTEM_REVERSAL)
                                        ) {
                                            log.info("System induced transactions..up");
                                            partTransRes = new ArrayList<>();
                                        }  else {
                                            log.info("Unknown transaction type");
//                                        continue;
                                        }
                                    } else {
                                        log.info("Trantype == " + tranType);
                                    }


                                } else {
                                    log.error("Account " + patran.getAcid() + " is  not verified");
                                    response.setMessage("Account " + patran.getAcid() + " is not verified");
                                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);
                                }
                            } else {
                                log.error("Account " + patran.getAcid() + " is " + accountStatus);
                                response.setMessage("Account " + patran.getAcid() + " is " + accountStatus);
                                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);
                            }
                        } else {
                            log.error("Account " + patran.getAcid() + " does not Exist");
                            response.setMessage("Account " + patran.getAcid() + " does not Exist");
                            response.setStatusCode(HttpStatus.NOT_FOUND.value());
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

                        }

                    }
                    if (partTransRes.isEmpty()) {
                        log.info("Charge partrans is empty");
                        chargepartrans = new ArrayList<>();
                    } else if (partTransRes.size() > 0) {
                        log.info("Charge Partrans size = " + chargepartrans.size());
                        for (ChargePartran p : partTransRes) {
                            log.info("Combining charge partrans");
                            chargepartrans.add(p);
                        }
                    }

//                    log.info("Charge collection complete");
                    if (chargepartrans.isEmpty()) {
//                        log.warn("Charge partrans is empty");
                        transactionHeader.setChargePartran(chargepartrans);

                    } else {
                        log.info("Charge partrans size is " + chargepartrans.size());
                        transactionHeader.setChargePartran(chargepartrans);

                    }

                    //Collecting all partrans
                    List<PartTran> allPartran = new ArrayList<>();
                    List<ChargePartran> chargePartranList = transaction.getChargePartran();
//                        List<PartTran> partTranList = transaction.getPartTrans();
                    //Collecting all partrans


                    if (chargePartranList.size() > 0) {

                        List<PartTran> newPartransList = new ArrayList<>();
                        for (ChargePartran cp : partTransRes) {
                            PartTran p = new PartTran();
                            p.setTransactionDate(cp.getTransactionDate());
                            p.setTransactionParticulars(cp.getTransactionParticulars());
                            p.setAcid(cp.getAcid());
                            p.setCurrency(cp.getCurrency());
                            p.setIsoFlag(cp.getIsoFlag());
                            p.setTransactionAmount(cp.getTransactionAmount());
                            p.setPartTranType(cp.getPartTranType());
                            p.setAccountBalance(cp.getAccountBalance());
                            p.setExchangeRate(cp.getExchangeRate());
                            newPartransList.add(p);

                        }
                        allPartran.addAll(newPartransList);
                        allPartran.addAll(orgpartTranList);
                    } else {
                        allPartran.addAll(orgpartTranList);
                    }
                    transaction.setChargePartran(chargepartrans);
                    transaction.setEnteredBy("system");
                    transaction.setEnteredFlag(CONSTANTS.YES);
                    transaction.setEnteredTime(new Date());
                    transaction.setEntityId(CONSTANTS.SYSTEM_ENTITY);
                    transaction.setPostedBy("system");
                    transaction.setPostedTime(new Date());
                    transaction.setPostedFlag(CONSTANTS.YES);

                    System.out.println("Balance checking....");
                    Boolean bc = false;
                    try {
                        bc = transactionHeaderService.balanceCheck(transaction);
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error(e.getMessage());
                    }
                    System.out.println("Balance check pass " + bc);
                    if (bc) {
                        transaction.setPostedBy(CONSTANTS.SYSTEM_USERNAME);
                        transaction.setPostedFlag(CONSTANTS.YES);
                        transaction.setPostedTime(new Date());

                        EntityResponse responseSer = transactionHeaderService.updateTransaction(allPartran);
                        Integer statuscode = responseSer.getStatusCode();
                        if (statuscode == 200) {
                            response.setMessage("Transaction Posted!. Transaction Code: " + transaction.getTransactionCode());
                            response.setStatusCode(HttpStatus.OK.value());
                            response.setEntity(transaction.getTransactionCode());
                            transaction.setPostedBy(CONSTANTS.SYSTEM_USERNAME);
                            transaction.setPostedFlag(CONSTANTS.YES);
                            transaction.setPostedTime(new Date());
                            transaction.setStatus("Success");


                        } else {
                            response.setStatusCode(responseSer.getStatusCode());
                            response.setMessage(responseSer.getMessage());
                            response.setEntity(responseSer.getEntity());
                            transaction.setStatus("Failed");
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        }
                    } else {
                        response.setStatusCode(HttpStatus.EXPECTATION_FAILED.value());
                        response.setMessage("Transaction failed.Check your balance");
                        response.setEntity(null);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }
                    transactionRepo.save(transaction);
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("Error " + e.getMessage());
                response.setStatusCode(HttpStatus.EXPECTATION_FAILED.value());
                response.setMessage("Transaction failed.Check your balance");
                response.setEntity("");
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
//            }
        } catch (Exception e) {

            EntityResponse response = new EntityResponse();
            response.setMessage(e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity("");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

    }

    public ResponseEntity<EntityResponse> systemLienTransaction(TransactionHeader transactionHeader) {
        log.info("---System transaction initiated...----");
        try {
            log.info("---Entered System transaction initiated...----");
            EntityResponse response = new EntityResponse();
//            if (UserRequestContext.getCurrentUser().isEmpty()) {
//                response.setMessage("User Name not present in the Request Header");
//                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                response.setEntity("");
//                return new ResponseEntity<>(response, HttpStatus.OK);
//            } else if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
//                response.setMessage("Entity not present in the Request Header");
//                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                response.setEntity("");
//                return new ResponseEntity<>(response, HttpStatus.OK);
//
//            } else {
            try {

                log.info("---Entered System transaction initiated check one...----");

                transactionHeader.setTransactionCode(transactionHeaderService.generatecSystemCode(6));
                transactionHeader.setDeletedFlag(CONSTANTS.NO);


                Optional<TransactionHeader> transactionCheck = transactionRepo.findByEntityIdAndTransactionCodeAndDeletedFlag(CONSTANTS.SYSTEM_ENTITY, transactionHeader.getTransactionCode(), 'N');
                if (transactionCheck.isPresent()) {
                    log.error("Transction does not exist");
                    response.setMessage("Transaction Exist!");
                    response.setStatusCode(HttpStatus.CONFLICT.value());
                    response.setEntity("");
                    return new ResponseEntity<>(response, HttpStatus.OK);

                } else {
                    log.info("Transction does not exist");

                    TransactionHeader transaction = transactionHeader;
                    String transactionType = transaction.getTransactionType();
                    List<PartTran> orgpartTranList = transaction.getPartTrans();
                    List<ChargePartran> chargepartrans = new ArrayList<>();
                    List<ChargePartran> partTransRes = new ArrayList<>();
                    for (PartTran patran : orgpartTranList) {
                        //get account details,status,balance

                        Integer isExist = transactionRepo.checkAccountExistance(patran.getAcid());
                        String accountStatus = transactionRepo.checkAccountStatus(patran.getAcid());
                        Character verifiedStatus = transactionRepo.checkVerifiedStatus(patran.getAcid());
                        if (isExist == 1) {
                            if (accountStatus.equalsIgnoreCase("ACTIVE")) {
                                if (verifiedStatus == 'Y') {


                                    log.info("Account " + patran.getAcid() + " Found");
                                    //get account details,status,balance

                                    String currency = transactionHeader.getCurrency();
                                    String tranType = patran.getPartTranType();

//                                    log.info("Proceeding to collect charges...");
//

                                    if (tranType.equalsIgnoreCase("Debit")) {
                                        log.info("Partran Type ::<< " + tranType + " >>");
                                        String acid = patran.getAcid();

                                        if (transactionType.equalsIgnoreCase("Cash Withdrawal")) {
                                            log.info("Cash withdrawal..");
                                            Double withdrawalAmount = patran.getTransactionAmount();
                                            String transType = "WithdrawalFee";
                                            partTransRes = transactionServiceCaller.getWithdrwalChargePartran(transType, withdrawalAmount, acid);
                                            System.out.println("charge partran " + partTransRes);
                                        } else if (transactionType.equalsIgnoreCase("Transfer")) {
                                            log.info("Cash Transfer...");
                                            partTransRes = new ArrayList<>();
                                        } else if (transactionType.equalsIgnoreCase("Cash Deposit")) {
                                            log.info("Cash Deposit...");
                                            partTransRes = new ArrayList<>();
                                        } else if (transactionType.equalsIgnoreCase("SYSTEM") || transactionType.equalsIgnoreCase("USSD")) {
                                            log.info("System induced transactions..");
                                            partTransRes = new ArrayList<>();
                                        } else if (transactionType.equalsIgnoreCase("SYSTEM") || transactionType.equalsIgnoreCase("USSD")) {
                                            log.info("System induced transactions..");
                                            partTransRes = new ArrayList<>();
                                        } else {
                                            log.info("Unknown transaction type");
//                                        continue;
                                        }
                                    } else {
                                        log.info("Trantype == " + tranType);
                                    }


                                } else {
                                    log.error("Account " + patran.getAcid() + " is  not verified");
                                    response.setMessage("Account " + patran.getAcid() + " is not verified");
                                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);
                                }
                            } else {
                                log.error("Account " + patran.getAcid() + " is " + accountStatus);
                                response.setMessage("Account " + patran.getAcid() + " is " + accountStatus);
                                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);
                            }
                        } else {
                            log.error("Account " + patran.getAcid() + " does not Exist");
                            response.setMessage("Account " + patran.getAcid() + " does not Exist");
                            response.setStatusCode(HttpStatus.NOT_FOUND.value());
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

                        }

                    }
                    if (partTransRes.isEmpty()) {
                        log.info("Charge partrans is empty");
                        chargepartrans = new ArrayList<>();
                    } else if (partTransRes.size() > 0) {
                        log.info("Charge Partrans size = " + chargepartrans.size());
                        for (ChargePartran p : partTransRes) {

                            log.info("Combining charge partrans");
                            chargepartrans.add(p);

                        }

                    }

                    log.info("Charge collection complete");
                    if (chargepartrans.isEmpty()) {
                        log.warn("Charge partrans is empty");
                        transactionHeader.setChargePartran(chargepartrans);

                    } else {
                        log.info("Charge partrans size is " + chargepartrans.size());
                        transactionHeader.setChargePartran(chargepartrans);

                    }

                    //Collecting all partrans
                    List<PartTran> allPartran = new ArrayList<>();
                    List<ChargePartran> chargePartranList = transaction.getChargePartran();
//                        List<PartTran> partTranList = transaction.getPartTrans();
                    //Collecting all partrans


                    if (chargePartranList.size() > 0) {

                        List<PartTran> newPartransList = new ArrayList<>();
                        for (ChargePartran cp : partTransRes) {
                            PartTran p = new PartTran();
                            p.setTransactionDate(cp.getTransactionDate());
                            p.setTransactionParticulars(cp.getTransactionParticulars());
                            p.setAcid(cp.getAcid());
                            p.setCurrency(cp.getCurrency());
                            p.setIsoFlag(cp.getIsoFlag());
                            p.setTransactionAmount(cp.getTransactionAmount());
                            p.setPartTranType(cp.getPartTranType());
                            p.setAccountBalance(cp.getAccountBalance());
                            p.setExchangeRate(cp.getExchangeRate());
                            newPartransList.add(p);

                        }
                        allPartran.addAll(newPartransList);
                        allPartran.addAll(orgpartTranList);
                    } else {
                        allPartran.addAll(orgpartTranList);
                    }
                    transaction.setChargePartran(chargepartrans);
                    transaction.setEnteredBy("system");
                    transaction.setEnteredFlag(CONSTANTS.YES);
                    transaction.setEnteredTime(new Date());
                    transaction.setEntityId(CONSTANTS.SYSTEM_ENTITY);
                    transaction.setPostedBy("system");
                    transaction.setPostedTime(new Date());
                    transaction.setPostedFlag(CONSTANTS.YES);

                    System.out.println("Balance checking....");
                    Boolean bc = false;
                    try {
                        bc = transactionHeaderService.balanceCheck(transaction);
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error(e.getMessage());
                    }
                    System.out.println("Balance check pass " + bc);
                    if (bc == true) {

                        transaction.setPostedBy(CONSTANTS.SYSTEM_USERNAME);
                        transaction.setPostedFlag(CONSTANTS.YES);
                        transaction.setPostedTime(new Date());

                        EntityResponse responseSer = transactionHeaderService.updateLienTransaction(allPartran);
                        Integer statuscode = responseSer.getStatusCode();
                        if (statuscode == 200) {
                            response.setMessage("Transaction Posted!. Transaction Code: " + transaction.getTransactionCode());
                            response.setStatusCode(HttpStatus.OK.value());
                            response.setEntity(transaction.getTransactionCode());
                            transaction.setPostedBy(CONSTANTS.SYSTEM_USERNAME);
                            transaction.setPostedFlag(CONSTANTS.YES);
                            transaction.setPostedTime(new Date());
                            transaction.setStatus("Success");


                        } else {
                            response.setStatusCode(responseSer.getStatusCode());
                            response.setMessage(responseSer.getMessage());
                            response.setEntity(responseSer.getEntity());
                            transaction.setStatus("Failed");

                        }


                    } else {
                        response.setStatusCode(HttpStatus.EXPECTATION_FAILED.value());
                        response.setMessage("Transaction failed.Check your balance");
                        response.setEntity("");

                    }
                    transactionRepo.save(transaction);

                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("Error " + e.getMessage());
                response.setStatusCode(HttpStatus.EXPECTATION_FAILED.value());
                response.setMessage("Transaction failed.Check your balance");
                response.setEntity("");
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
//            }
        } catch (Exception e) {

            EntityResponse response = new EntityResponse();
            response.setMessage(e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity("");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

    }

    //Receive USSD Transfer Request
    @RequestMapping("/ussdTransfer")
    public ResponseEntity<?> receiveUSSDTransfer(@RequestBody USSDTransfer ussdTransfer) {
        EntityResponse response = new EntityResponse();
        Gson gson = new Gson();
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                log.info("USSD Transfer Req Received - " + gson.toJson(ussdTransfer));
                TransactionHeader transaction = transactionHeaderService.prepareTransaction(ussdTransfer);
                if (transaction != null) {
                    try {
                        ResponseEntity<EntityResponse> res = systemTransaction(transaction);
                        Integer statuscode = Integer.valueOf(res.getBody().getStatusCode());
                        if (statuscode == 200) {
                            transaction.setStatus("Success");
                            response.setMessage(ussdTransfer.getSessionId());
                            response.setStatusCode(200);
                            transactionRepo.save(transaction);
                            response.setEntity("Success," + transaction.getTransactionCode());
                            System.out.println("entity ::" + response.getEntity());
                        } else {
                            transaction.setStatus("Failed");
                            transactionRepo.save(transaction);
                            response.setMessage(ussdTransfer.getSessionId());
                            response.setStatusCode(res.getBody().getStatusCode());
                            response.setEntity(res.getBody().getMessage());
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error("Error " + e.getMessage());
                        response.setMessage(ussdTransfer.getSessionId());
                        response.setStatusCode(HttpStatus.EXPECTATION_FAILED.value());
                        response.setEntity(e.getMessage());


                    }

                } else {
                    response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
                    response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                    response.setEntity(HttpStatus.NOT_FOUND);
                }


                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } catch (Exception e) {
            response.setMessage(e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setEntity("");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }


    @RequestMapping("mpesa/deposit")
    public ResponseEntity<?> getMpesadeposits(@RequestBody MpesaDepositsAPIRequest depositsAPIRequest) {
        log.info("----Mpesa deposit callback recieved at " + LocalDateTime.now() + "----");
        log.info("Saving deposit request...");
        mpesaDepositsAPIRequestRepo.save(depositsAPIRequest);
        log.info("Saved! ");

        EntityResponse response = new EntityResponse<>();
        System.out.println("Deposit details == " + depositsAPIRequest);
        TransactionHeader t = new TransactionHeader();
        t.setEntityId(EntityRequestContext.getCurrentEntityId());
        t.setTransactionDate(new Date());
        t.setRcre(new Date());
        t.setCurrency("KES");
        t.setTransactionType("Cash Deposit");
        t.setEnteredBy("USSD");
        t.setEnteredFlag(CONSTANTS.YES);
        t.setEnteredTime(new Date());
        t.setMpesacode(depositsAPIRequest.getMpesaReceiptNumber());


        ArrayList<PartTran> partTrans = new ArrayList<>();
        PartTran p1 = new PartTran();
        p1.setCurrency("KES");

        //get Mpesa Debit account
        p1.setAcid("601000");
        p1.setIsoFlag('S');
        p1.setTransactionDate(new Date());
        p1.setExchangeRate("1");
//        p1.setAccountBalance();
        p1.setTransactionParticulars(depositsAPIRequest.getNarration());
        p1.setPartTranType("Debit");
        p1.setTransactionAmount(Double.valueOf(depositsAPIRequest.getTranAmount()));

        PartTran p2 = new PartTran();
        p2.setCurrency("KES");
        p2.setAcid(depositsAPIRequest.getCreditAccount());
        p2.setIsoFlag('S');
        p2.setTransactionDate(new Date());
        p2.setExchangeRate("1");
//        p2.setAccountBalance();
        p2.setTransactionParticulars(depositsAPIRequest.getNarration());
        p2.setPartTranType("Credit");
        p2.setTransactionAmount(Double.valueOf(depositsAPIRequest.getTranAmount()));

        partTrans.add(p1);
        partTrans.add(p2);

        t.setPartTrans(partTrans);

        ResponseEntity<EntityResponse> res = systemTransaction(t);
        Integer statuscode = Integer.valueOf(res.getBody().getStatusCode());
        if (statuscode == 200) {
            t.setStatus("Success");
            log.info("Updating mpesa reqest table...");
            MpesaDepositsAPIRequest dr = mpesaDepositsAPIRequestRepo.findByMpesaReceiptNumber(depositsAPIRequest.getMpesaReceiptNumber());
            dr.setStatus("Success");
            dr.setTransactionDate(t.getTransactionDate());
            mpesaDepositsAPIRequestRepo.save(dr);
            response.setMessage(depositsAPIRequest.getSessionId());
            response.setStatusCode(200);
            transactionRepo.save(t);
            log.info("Mpesa deposit successful");
            response.setEntity("Success," + t.getTransactionCode());
            System.out.println("entity ::" + response.getEntity());
        } else {
            t.setStatus("Failed");
            transactionRepo.save(t);
            response.setMessage(depositsAPIRequest.getSessionId());
            response.setStatusCode(res.getBody().getStatusCode());
            response.setEntity(res.getBody().getMessage());
            log.info("Mpesa deposit failed");
        }
        return ResponseEntity.ok().body(response);

    }


    @RequestMapping("mpesa/withdrawal")
    public ResponseEntity<?> getMpesadeposits(@RequestBody MpesaWithdrawalAPIRequest withdrawalRequest) {
        log.info("----Mpesa deposit callback recieved at " + LocalDateTime.now() + "----");
        log.info("Saving deposit request...");
        mpesaWithdrawalAPIRequestRepo.save(withdrawalRequest);
        log.info("Saved! ");


        EntityResponse response = new EntityResponse();

        System.out.println("Withdrawal Request details == " + withdrawalRequest);
        TransactionHeader t = new TransactionHeader();
        t.setEntityId(EntityRequestContext.getCurrentEntityId());
        t.setTransactionDate(new Date());
        t.setRcre(new Date());
        t.setCurrency("KES");
        t.setTransactionType("Cash Withdrawal");
        t.setEnteredBy("USSD");
        t.setEnteredFlag(CONSTANTS.YES);
        t.setEnteredTime(new Date());
        t.setMpesacode(withdrawalRequest.getMpesaReceiptNumber());

        ArrayList<PartTran> partTrans = new ArrayList<>();
        PartTran p1 = new PartTran();
        p1.setCurrency("KES");

        //get Mpesa Debit account
        p1.setAcid(withdrawalRequest.getDebitAccount());
        p1.setIsoFlag('S');
        p1.setTransactionDate(new Date());
        p1.setExchangeRate("1");
//        p1.setAccountBalance();
        p1.setTransactionParticulars(withdrawalRequest.getNarration());
        p1.setPartTranType("Debit");
        p1.setTransactionAmount(Double.valueOf(withdrawalRequest.getTranAmount()));

        PartTran p2 = new PartTran();
        p2.setCurrency("KES");
        p2.setAcid("100016");
        p2.setIsoFlag('S');
        p2.setTransactionDate(new Date());
        p2.setExchangeRate("1");
//        p2.setAccountBalance();
        p2.setTransactionParticulars(withdrawalRequest.getNarration());
        p2.setPartTranType("Credit");
        p2.setTransactionAmount(Double.valueOf(withdrawalRequest.getTranAmount()));

        partTrans.add(p1);
        partTrans.add(p2);

        t.setPartTrans(partTrans);

        ResponseEntity<EntityResponse> res = systemTransaction(t);
        Integer statuscode = Integer.valueOf(res.getBody().getStatusCode());
        if (statuscode == 200) {
            log.info("updating mpesa widthdwawal request table ..");
            MpesaWithdrawalAPIRequest wr = mpesaWithdrawalAPIRequestRepo.findByMpesaReceiptNumber(withdrawalRequest.getMpesaReceiptNumber());
            wr.setStatus("Success");
            wr.setTransactionDate(t.getTransactionDate());
            mpesaWithdrawalAPIRequestRepo.save(wr);
            t.setStatus("Success");
            response.setMessage(withdrawalRequest.getSessionId());
            response.setStatusCode(200);
            transactionRepo.save(t);
            log.info("Mpesa widthdwawal request successful");
            response.setEntity("Success," + t.getTransactionCode());
            System.out.println("entity ::" + response.getEntity());
        } else {
            log.error(res.getBody().getMessage());
            t.setStatus("Failed");
            transactionRepo.save(t);
            response.setMessage(withdrawalRequest.getSessionId());
            response.setStatusCode(res.getBody().getStatusCode());
            response.setEntity(res.getBody().getMessage());
            log.info("Mpesa widthdwawal request failed");
        }
        return ResponseEntity.ok().body(response);
    }

//    @GetMapping("reverse")
//    public ResponseEntity<EntityResponse> reverseTransaction(@RequestParam String transactioncode) {
//
//        log.info("-----Reversal of transaction " + transactioncode + " initiated ----");
//        EntityResponse response = new EntityResponse();
//        try {
//            log.info("Reversing transaction...");
//            EntityResponse res = transactionHeaderService.reverse(transactioncode);
//            if (res.getStatusCode() == 200) {
//                response.setStatusCode(HttpStatus.OK.value());
//                response.setMessage("Reversal was successful!");
//
//            } else {
//                log.error("Error :: " + res.getMessage());
//                response.setStatusCode(res.getStatusCode());
//                response.setMessage("Reversal Failed!");
//            }
//            return ResponseEntity.ok().body(response);
//        } catch (Exception e) {
//            log.error("Error " + e.getMessage());
//            e.printStackTrace();
//            response.setStatusCode(HttpStatus.EXPECTATION_FAILED.value());
//            response.setMessage("Reversal Failed!");
//            return ResponseEntity.badRequest().body(response);
//        }
//
//    }

    @GetMapping("ministatement/{acid}")
    public ResponseEntity<EntityResponse> getministatement(@PathVariable String acid) {
        log.info("Fethch ministatement data....");

        EntityResponse response = new EntityResponse();
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
//            else if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
//                response.setMessage("Entity not present in the Request Header");
//                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                response.setEntity("");
//                return new ResponseEntity<>(response, HttpStatus.OK);
//            }
            else {
                log.info("Fetching transaction for account " + acid + " ...");
                List<MinistatementInterface> ls = transactionHeaderService.getMinistatement(acid);
                if (!ls.isEmpty()) {
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setMessage("Transactions found!");
                    response.setEntity(ls);

                } else {
                    log.error("No data found for account " + acid);
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setMessage("Data not found for account " + acid);
                    response.setEntity(ls);
                }
                return ResponseEntity.ok().body(response);
            }
        } catch (Exception e) {
            log.error("Error " + e.getMessage());
            e.printStackTrace();
            response.setStatusCode(HttpStatus.EXPECTATION_FAILED.value());
            response.setMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            return ResponseEntity.badRequest().body(response);
        }


    }

    @GetMapping("transactionStatement/{acid}")
    public ResponseEntity<EntityResponse> getTransactionStatement(@PathVariable String acid) {
        log.info("Going to get ministatement data..");

        EntityResponse response = new EntityResponse();
        try {
                log.info("Fetching transaction for account: " + acid + " ...");
                List<TransactionStatementInterface> ls = transactionHeaderService.getTransactionStatement(acid);
                if (!ls.isEmpty()) {
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setMessage("Transactions found!");
                    response.setEntity(ls);

                } else {
                    log.error("No data found for account " + acid);
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setMessage("Data not found for account " + acid);
                    response.setEntity(ls);
                }
                return ResponseEntity.ok().body(response);

        } catch (Exception e) {
            log.error("Error " + e.getMessage());
            e.printStackTrace();
            response.setStatusCode(HttpStatus.EXPECTATION_FAILED.value());
            response.setMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            return ResponseEntity.badRequest().body(response);
        }


    }



    @GetMapping("statement")
    public ResponseEntity<EntityResponse> getstatement(@RequestParam String acid, @RequestParam String fromdate, @RequestParam String todate) {
        log.info("Fetching ministatement data....");

        EntityResponse response = new EntityResponse();
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                log.info("Fetching transaction for account " + acid + " ...");
                List<MinistatementInterface> ls = transactionHeaderService.getstatement(acid, fromdate, todate);
                if (!ls.isEmpty()) {
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setMessage("Transaction records found!");
                    response.setEntity(ls);

                } else {
                    log.error("Transaction statement for account " + acid + " not found.");
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setMessage("Transaction statement for account " + acid + " not found.");
                    response.setEntity(ls);
                }
                return ResponseEntity.ok().body(response);
            }
        } catch (Exception e) {
            log.error("Error " + e.getMessage());
            e.printStackTrace();
            response.setStatusCode(HttpStatus.EXPECTATION_FAILED.value());
            response.setMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            return ResponseEntity.badRequest().body(response);
        }


    }


    public HttpEntity<Object> systemTransactionold(TransactionHeader tranHeader) {
        return null;
    }
}

