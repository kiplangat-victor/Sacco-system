package com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader;

import com.emtechhouse.accounts.Models.Accounts.AccountRepository;
import com.emtechhouse.accounts.Models.Accounts.Validators.ValidatorService;
import com.emtechhouse.accounts.TransactionService.Requests.MinistatementInterface;
import com.emtechhouse.accounts.TransactionService.Requests.TransactionInterface;
import com.emtechhouse.accounts.TransactionService.Requests.TransactionStatementInterface;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.Requests.USSDTransfer;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.ChargePartran.ChargePartran;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.PartTrans.PartTran;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.PartTrans.PartTranRepository;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.emtechhouse.accounts.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.accounts.Utils.HttpInterceptor.UserRequestContext;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
public class TranHeaderService {
    @Autowired
    private ValidatorService validatorService;

    @Autowired
    private TransactionProcessing transactionProcessing;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TranHeaderRepository transactionHeaderRepository;
//    @Autowired
//    NotificationService notificationService;


    @Autowired
    private PartTranRepository partTranRepository;


    @Autowired
    private TransactionServiceCaller transactionServiceCaller;

    //    @Autowired
//    private RestTemplate template;
    private static final DecimalFormat df = new DecimalFormat("0.00");

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

    public EntityResponse saveTransactionHeader(TransactionHeader transactionHeader) throws MessagingException, IOException {
        EntityResponse response = new EntityResponse<>();
        if (!transactionHeader.getTransactionType().equalsIgnoreCase("SALARY_PROCESSING"))
            transactionHeader.setTransactionCode(generateRandomCode(6).toUpperCase());
        List<PartTran> partTranList = transactionHeader.getPartTrans();
        Boolean balanceCheck = checktransactionbalance(partTranList);
        if (balanceCheck == false) {
            response.setMessage("Transaction not balance");
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return response;
        } else if (balanceCheck == true) {
            transactionHeaderRepository.save(transactionHeader);
            response.setEntity(transactionHeader);
            response.setMessage("Transaction Entered!. Transction Code: " + transactionHeader.getTransactionCode());
            response.setStatusCode(HttpStatus.CREATED.value());
//            notificationService.sendNotification(transactionHeader);
        }
        return response;
    }

    public EntityResponse create(TransactionHeader transactionHeader) {
        EntityResponse response = new EntityResponse<>();
        transactionHeader.setTransactionCode(generateRandomCode(6).toUpperCase());
        List<PartTran> partTranList = transactionHeader.getPartTrans();
        Boolean balanceCheck = checktransactionbalance(partTranList);
        if (balanceCheck == false) {
            response.setMessage("Transaction not balance");
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return response;
        } else if (balanceCheck == true) {
            transactionHeaderRepository.save(transactionHeader);
            response.setEntity(transactionHeader);
            response.setMessage("Transaction Entered!. Transction Code: " + transactionHeader.getTransactionCode());
            response.setStatusCode(HttpStatus.CREATED.value());
//            TODO: Send Email
//            notificationService.sendDebitNotification(transactionHeader.getTransactionCode(), transactionHeader.getTransactionDate(), transactionHeader.getPartTrans());
        }


        return response;
    }


    public EntityResponse updateTransaction(List<PartTran> partTranList) {

        EntityResponse response = new EntityResponse<>();

        try {

            Boolean balanceCheck = checktransactionbalance(partTranList);
            if (!balanceCheck) {
                log.info("Transaction is not balance");
                response.setMessage("Transaction not balance");
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                return response;

            } else if (balanceCheck) {
                log.info("Transaction balanced.");
                System.out.println("\n");

                //TODO:update affected accounts
                log.info("Updating accounts...");

                List<TransactionInterface> transactionInterfaceList = getAccounteffectSummary(partTranList);

                System.out.println("Transactions Intefece List " + transactionInterfaceList);
                EntityResponse res = updateAccounts(transactionInterfaceList);
                System.out.println(res);
                Integer statuscode = res.getStatusCode();
                System.out.println("Status code : " + res.getStatusCode());
                if (statuscode == 200) {
                    log.info("Done.");
                    log.info("Transaction was successful.");
                    response.setMessage("Transaction Saved.");
                    response.setEntity(res.getEntity());
                    response.setStatusCode(HttpStatus.OK.value());
                    log.info("Transaction complete.");

//                    String debitAc,
//                    Double debitAmount,
//                    String tranId,
//                    String tranDate,
//                    List<PartTran> credits



                //
                    return response;
                } else {
                    System.out.println("Transaction seems not successful");
                    response.setMessage(res.getMessage());
                    response.setEntity(res.getEntity());
                    response.setStatusCode(res.getStatusCode());
                    return response;
                }
            } else {
                response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                return response;
            }
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.EXPECTATION_FAILED.value());
            return response;
        }
    }

    public EntityResponse updateLienTransaction(List<PartTran> partTranList) {

        EntityResponse response = new EntityResponse<>();

        try {

            Boolean balanceCheck = checktransactionbalance(partTranList);
            if (balanceCheck == false) {
                log.info("Transaction is not balance");
                response.setMessage("Transaction not balance");
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                return response;

            } else if (balanceCheck == true) {
                log.info("Transaction balanced.");
                System.out.println("\n");

                //TODO:update affected accounts
                log.info("Updating accounts...");

                List<TransactionInterface> transactionInterfaceList = getAccounteffectSummaryLien(partTranList);

                System.out.println("Transactions Intefece List " + transactionInterfaceList);
                EntityResponse res = updateAccounts(transactionInterfaceList);
                Integer statuscode = res.getStatusCode();
                System.out.println("Status code : " + res.getStatusCode());
                if (statuscode == 200) {
                    log.info("Done.");
                    log.info("Transaction was successful.");
                    response.setMessage("Transaction Saved.");
                    response.setEntity(res.getEntity());
                    response.setStatusCode(HttpStatus.OK.value());
                    log.info("Transaction complete.");
                    return response;
                } else {
                    response.setMessage(res.getMessage());
                    response.setEntity(res.getEntity());
                    response.setStatusCode(res.getStatusCode());
                    return response;
                }
            } else {
                response.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                return response;
            }
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.EXPECTATION_FAILED.value());
            return response;
        }
    }


    public boolean checktransactionbalance(List<PartTran> partTrans) {
        log.info("Checking balance ");
        AtomicReference<Double> sumCredits = new AtomicReference<>(0.0);
        AtomicReference<Double> sumDebits = new AtomicReference<>(0.0);
        for (PartTran partTran : partTrans) {
            if (partTran.getPartTranType().equalsIgnoreCase("Credit")) {
                sumCredits.updateAndGet(v -> v + partTran.getTransactionAmount());
            } else if (partTran.getPartTranType().equalsIgnoreCase("Debit")) {
                sumDebits.updateAndGet(v -> v + partTran.getTransactionAmount());
            }
        }
        if (sumCredits.get().doubleValue() != sumDebits.get().doubleValue()) {
            log.error("Transaction not balanced  Total Debit amount : " + sumDebits.get().doubleValue() + " Total Credit amount :" + sumCredits.get().doubleValue());
            return false;

        } else if (sumCredits.get().doubleValue() == sumDebits.get().doubleValue()) {
            log.info("Transaction balance");
            return true;
        } else {
            return false;
        }
    }

    public List<TransactionInterface> getAccounteffectSummary(List<PartTran> partTrans) {
        List<TransactionInterface> transactionInterfaceList = new ArrayList<>();
        try {
            for (PartTran p : partTrans) {
                Double tranAmout = p.getTransactionAmount();
                String tranType = p.getPartTranType();
                String currency = p.getCurrency();
                String acid = p.getAcid();
//                    Double amt = Double.valueOf(df.format(serviceCaller.getSellingRate(curreny,tranAmout)));
                TransactionInterface tri = new TransactionInterface();
                if (tranType.equalsIgnoreCase("Debit")) {
                    tri.setTransactionAmount(0 - tranAmout);
                } else if (tranType.equalsIgnoreCase("Credit")) {
                    tri.setTransactionAmount(tranAmout);
                }

                tri.setAcid(acid);
                tri.setPartTranType(tranType);

                transactionInterfaceList.add(tri);
            }
            return transactionInterfaceList;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error " + e.getMessage());
        }
        return transactionInterfaceList;

    }

    public List<TransactionInterface> getAccounteffectSummaryLien(List<PartTran> partTrans) {
        List<TransactionInterface> transactionInterfaceList = new ArrayList<>();
        try {
            for (PartTran p : partTrans) {
                Double tranAmout = p.getTransactionAmount();
                String tranType = p.getPartTranType();


                String currency = p.getCurrency();
                String acid = p.getAcid();
//                    Double amt = Double.valueOf(df.format(serviceCaller.getSellingRate(curreny,tranAmout)));
                TransactionInterface tri = new TransactionInterface();
                if (tranType.equalsIgnoreCase("Debit")) {
                    tri.setTransactionAmount(0 - tranAmout);
                } else if (tranType.equalsIgnoreCase("Credit")) {
                    tri.setTransactionAmount(tranAmout);
                }

                tri.setAcid(acid);
                tri.setPartTranType(tranType);
                tri.setTransactionDesc("LIEN");

                transactionInterfaceList.add(tri);
            }
            return transactionInterfaceList;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error " + e.getMessage());
        }
        return transactionInterfaceList;

    }

    public EntityResponse updateAccounts(List<TransactionInterface> transactionInterfaceList) {
        try {

            EntityResponse res = transactionServiceCaller.updateAccounts1(transactionInterfaceList);
            return res;
        } catch (Exception e) {
            EntityResponse res = new EntityResponse<>();
            log.error(e.getMessage());

            res.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            res.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return res;
        }
    }


    public boolean balanceCheck(List<PartTran> partTrans) {

        AtomicReference<Double> sumCredits = new AtomicReference<>(0.0);
        AtomicReference<Double> sumDebits = new AtomicReference<>(0.0);


//        log.info("balance checking in progress ...");
        List<Integer> integerList = new ArrayList<>();
        for (PartTran p : partTrans) {
            if (p.getPartTranType().equalsIgnoreCase("Credit")) {
                sumCredits.updateAndGet(v -> v + p.getTransactionAmount());
            } else if (p.getPartTranType().equalsIgnoreCase("Debit")) {
                sumDebits.updateAndGet(v -> v + p.getTransactionAmount());
            }
            String tranType = p.getPartTranType();
            String acid = p.getAcid();

            if (tranType.equalsIgnoreCase("Debit")) {

                Double balance = transactionHeaderRepository.Accountbalance(acid);
                Double lienAmount = transactionHeaderRepository.getLienAmount(acid);
                Double bookBalance = transactionHeaderRepository.getBookBalance(acid);
                String accountType = transactionHeaderRepository.getAccountType(acid);

                Double accountbalance = balance - lienAmount-bookBalance;
//                System.out.println("Total debits " + sumDebits);
                Double totalDebit = Double.valueOf(String.valueOf(sumDebits));
//                System.out.println("Total converted debits " + totalDebit);
                try {
                    log.info("Checking if balance in enough for debit");
                    if (accountbalance < totalDebit && accountType.startsWith("LA") || accountType.startsWith("O") || accountType.startsWith("TD")) {// OAB or Loan Account
//                        System.out.println("office Account/Loan Account ==" + acid);
                        integerList.add(0);
//                        return  1;//fail

                    } else if (accountbalance > totalDebit) {
//                        log.info("Check passed");
                        integerList.add(0);

//                        return 0;// through
                    } else if (accountbalance < totalDebit && !accountType.startsWith("O")) {
                        log.info("Insuffficient balance");
                        integerList.add(1);

//                        return 1;// fail
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    log.error(e.getMessage());

                }


            }


        }

        if (integerList.contains(1)) {
            System.out.println("false");
            return false;
        } else {
            System.out.println("True");
            return true;
        }


    }

    public boolean balanceCheckLienTransaction(List<PartTran> partTrans) {


        AtomicReference<Double> sumCredits = new AtomicReference<>(0.0);
        AtomicReference<Double> sumDebits = new AtomicReference<>(0.0);


        log.info("balance checking in progress ...");
        List<Integer> integerList = new ArrayList<>();
        for (PartTran p : partTrans) {
            if (p.getPartTranType().equalsIgnoreCase("Credit")) {
                sumCredits.updateAndGet(v -> v + p.getTransactionAmount());
            } else if (p.getPartTranType().equalsIgnoreCase("Debit")) {
                sumDebits.updateAndGet(v -> v + p.getTransactionAmount());
            }
            String tranType = p.getPartTranType();
            String acid = p.getAcid();

            if (tranType.equalsIgnoreCase("Debit")) {

                Double balance = transactionHeaderRepository.Accountbalance(acid);
//                Double lienAmount = transactionHeaderRepository.getLienAmount(acid);
                String accountType = transactionHeaderRepository.getAccountType(acid);

                Double accountbalance = balance;
                System.out.println("Total debits " + sumDebits);
                Double totalDebit = Double.valueOf(String.valueOf(sumDebits));
                System.out.println("Total converted debits " + totalDebit);
                try {
                    log.info("Checking if balance in enough for debit");
                    if (accountbalance < totalDebit && accountType.startsWith("LA") || accountType.startsWith("O")) {// OAB or Loan Account
                        System.out.println("office Account/Loan Account ==" + acid);
                        integerList.add(0);
//                        return  1;//fail

                    } else if (accountbalance > totalDebit) {
                        log.info("Check passed");
                        integerList.add(0);

//                        return 0;// through
                    } else if (accountbalance < totalDebit && !accountType.startsWith("O")) {
                        log.info("Insuffficient balance");
                        integerList.add(1);

//                        return 1;// fail
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    log.error(e.getMessage());

                }


            }


        }

        if (integerList.contains(1)) {
            System.out.println("false");
            return false;
        } else {
            System.out.println("True");
            return true;
        }


    }
//Handle reversal


    public EntityResponse reverse(String transactionCode) {
        EntityResponse response = new EntityResponse();
        try {
            Optional<TransactionHeader> transactionCheck = transactionHeaderRepository.findByTransactionCode(transactionCode);
            if (transactionCheck.isPresent()) {
                TransactionHeader transaction = transactionCheck.get();
                List<PartTran> partTrans = transaction.getPartTrans();
                List<ChargePartran> chargePartrans = transaction.getChargePartran();

                List<PartTran> allPartran = new ArrayList<>();
                List<PartTran> newPartransList = new ArrayList<>();
                for (ChargePartran cp : chargePartrans) {
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
                if (newPartransList.isEmpty()) {
                    allPartran.addAll(partTrans);
                } else {
                    allPartran.addAll(partTrans);
                    allPartran.addAll(newPartransList);
                }
//                Runnable r = ()-> System.out.print("Run method");
                for (PartTran p : allPartran) {

                    if (p.getPartTranType().equalsIgnoreCase("Credit")) {
                        p.setPartTranType("Debit");

                    } else if (p.getPartTranType().equalsIgnoreCase("Debit")) {
                        p.setPartTranType("Credit");
                    }

                }
                System.out.println("All reversal partrans " + allPartran);

                Boolean bc = false;
                try {
                    bc = balanceCheck(allPartran);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error(e.getMessage());
                }
                System.out.println("Balance check pass " + bc);
                if (bc == true) {

                    transaction.setPostedBy(UserRequestContext.getCurrentUser());
                    transaction.setPostedFlag(CONSTANTS.YES);
                    transaction.setPostedTime(new Date());

                    EntityResponse responseSer = updateTransaction(allPartran);
                    Integer statuscode = responseSer.getStatusCode();
                    if (statuscode == 200) {
                        response.setMessage("Transaction Posted!. Transaction Code: " + transaction.getTransactionCode());
                        response.setStatusCode(HttpStatus.OK.value());
                        transaction.setPostedBy(UserRequestContext.getCurrentUser());
                        transaction.setPostedFlag(CONSTANTS.YES);
                        transaction.setPostedTime(new Date());
                        transactionHeaderRepository.save(transaction);
                        return response;
                    } else {
                        response.setStatusCode(responseSer.getStatusCode());
                        response.setMessage(responseSer.getMessage());
                        response.setEntity(responseSer.getEntity());
                        return response;
                    }

                }

                log.error("");
                response.setMessage("Reversal found");
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                return response;

            } else {
                response.setMessage("Transaction Not found");
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                return response;
            }

        } catch (Exception e) {
            log.error("Error " + e.getMessage());
            response.setMessage("Transaction Not found");
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            e.printStackTrace();
            return response;
        }

    }

    public List<TransactionHeader> filterBydate(String fromDate, String toDate, String entityId, String action) {
        List<TransactionHeader> transactionHeaderList = new ArrayList<>();
        if (action.equalsIgnoreCase("Entered")) {
            transactionHeaderList = transactionHeaderRepository.getentered(fromDate, toDate, entityId);

        } else if (action.equalsIgnoreCase("Modified")) {
            transactionHeaderList = transactionHeaderRepository.getmodified(fromDate, toDate, entityId);
        } else if (action.equalsIgnoreCase("Posted")) {
            transactionHeaderList = transactionHeaderRepository.getposted(fromDate, toDate, entityId);

        } else if (action.equalsIgnoreCase("Verified")) {
            transactionHeaderList = transactionHeaderRepository.getVerified(fromDate, toDate, entityId);
        } else if (action.equalsIgnoreCase("Deleted")) {
            transactionHeaderList = transactionHeaderRepository.getDeleted(fromDate, toDate, entityId);
        }
        return transactionHeaderList;
    }


    public List<TransactionHeader> verifiedTransactions() {
        return transactionHeaderRepository.findByVerifiedFlagAndDeleteFlag("Y", "N");
    }
    //    TODO: Handle Universal Transactions
    public EntityResponse standingordertransaction(TransactionHeader transactionHeader){
        EntityResponse response = new EntityResponse();
//        TODO: VALIDATE EACH OF THE ACCOUNT
        boolean validationCheckFailure = false;
        boolean allAcAreValid = true;
        String reasonForFailure;
          List<PartTran>  partTrans = transactionHeader.getPartTrans();
          for(int i=0; i<partTrans.size(); i++){
              EntityResponse response1 = validatorService.acidValidator(partTrans.get(i).getAcid(),"Standing Order a/c");
              if (response1.getStatusCode()==HttpStatus.OK.value()){
//                  if account is for debit, check lien amount and allow withrawable
                  boolean debitAccountFailure = false;
                  if (partTrans.get(i).getPartTranType().equalsIgnoreCase("Debit")){
                      EntityResponse checkAllowWithdrawal = validatorService.acidWithdrawalValidator(partTrans.get(i).getAcid());
                      if (checkAllowWithdrawal.getStatusCode()==HttpStatus.OK.value()){
//                          Account allow withdrawal
//                          Check if enough balance + lien
                          Double accountBalance = accountRepository.getAccountBalance(partTrans.get(i).getAcid());
                          Optional<AccountRepository.LienAmount> lien = accountRepository.getLienAmountByAcidAndDeletedFlag(partTrans.get(i).getAcid());
                          Double lientAmount = 0.0;
                          if (lien.isPresent()){
                              lientAmount = lien.get().getLien_amount();
                          }
                          Double availableBalance = accountBalance - lientAmount;
                          if (availableBalance < partTrans.get(i).getTransactionAmount()){
                              debitAccountFailure = true;
                              validationCheckFailure = true;
                          }else {
                          }
                      }
                  }else{
                  }
              }else{
                  allAcAreValid = false;
                  validationCheckFailure = true;
                  reasonForFailure = response1.getMessage();
              }
          }
//        TODO: IF ALL ACCOUNTS ARE VALID, CHECK IF TRANSACTION IS BALANCED
        if(balanceCheck(partTrans)){
            validationCheckFailure = false;
        }else{
            validationCheckFailure = true;
            reasonForFailure = "Transaction not balanced!";
        }
        if (validationCheckFailure = false){
            transactionHeader.setTransactionCode(generatecSystemCode(6));
            transactionHeader.setTransactionDate(new Date());
            transactionHeaderRepository.save(transactionHeader);
        }
        return response;
    }
//    TODO: Handle Transfer Transactions
    public EntityResponse transfer(){
        EntityResponse response = new EntityResponse();
        return response;
    }
    //    TODO: Handle Cash Deposit Transactions
    public EntityResponse cashDeposit(){
        EntityResponse response = new EntityResponse();
        return response;
    }
    //    TODO: Handle Cash Withdrawal Transactions
    public EntityResponse cashWithdrawal(){
        EntityResponse response = new EntityResponse();
        return response;
    }
    public TransactionHeader retrieveTranHeader(String transactionCode) {
        Optional<TransactionHeader> transactionCheck = transactionHeaderRepository.findByTransactionCode(transactionCode);
        if (transactionCheck.isPresent()) {
            return transactionCheck.get();
        } else {
            return null;
        }


    }

    public void deleteTransactionByid(Long id) {
        try {
            log.info("Deleting transaction.");
            Optional<TransactionHeader> transaction = transactionHeaderRepository.findById(id);
            if (transaction.isPresent()) {

//                transaction.get().setDeletedFlag(CONSTANTS.YES);
//                transaction.get().setDeletedTime(new Date());
//                transaction.get().setDeletedBy(UserRequestContext.getCurrentUser());
                transactionHeaderRepository.deleteById(id);
                log.info("Deleting successful.");
            }

        } catch (Exception e) {
            log.error("Error ", e.getMessage());

        }
    }

    public EntityResponse updateTransaction(TransactionHeader transaction) {
        EntityResponse response = new EntityResponse<>();
        try {

            Optional<TransactionHeader> transactionHeaderInDBOptional
                    = transactionHeaderRepository.findByTransactionCode(transaction.getTransactionCode());
            if (transactionHeaderInDBOptional.isPresent()
                    && Objects.equals(transactionHeaderInDBOptional.get().getSn(), transaction.getSn())
                    &&  transactionHeaderInDBOptional.get().getPostedFlag() != 'Y'
            ) {
                TransactionHeader transactionHeaderInDB = transactionHeaderInDBOptional.get();
                transactionHeaderInDB.setPartTrans(transaction.getPartTrans());

                log.info("Updating transaction...");
                transaction.setModifiedBy(UserRequestContext.getCurrentUser());
                transaction.setModifiedTime(new Date());
                transaction.setRejectedFlag(CONSTANTS.NO);
                transaction.setModifiedFlag(CONSTANTS.YES);
                transaction.setStatus(CONSTANTS.ENTERED);
                transaction.setVerifiedFlag('N');
                transaction.setVerifiedFlag_2('N');
                transactionHeaderRepository.save(transactionHeaderInDB);
                response.setMessage("Transaction updated successfully");
                response.setStatusCode(HttpStatus.OK.value());
                log.info("Transaction updated successfully");
            }else {
                response.setMessage("You cannot proceed with modification on this.");
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            }


        } catch (Exception e) {
            log.error("Error ", e.getMessage());
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
        }
        return response;
    }

    public TransactionHeader prepareTransaction(USSDTransfer ussdTransfer) {
        TransactionHeader t = new TransactionHeader();
        try {

            t.setEntityId(EntityRequestContext.getCurrentEntityId());
            t.setTransactionDate(new Date());
            t.setRcre(new Date());
            t.setCurrency("KES");
            t.setTransactionType("Transfer");
            t.setEnteredBy("USSD");
            t.setEnteredFlag(CONSTANTS.YES);
            t.setEnteredTime(new Date());

            ArrayList<PartTran> partTrans = new ArrayList<>();
            PartTran p1 = new PartTran();
            p1.setCurrency("KES");
            p1.setAcid(ussdTransfer.getDebitAccount());
            p1.setIsoFlag('S');
            p1.setTransactionDate(new Date());
            p1.setExchangeRate("1");
//        p1.setAccountBalance();
            p1.setPartTranType("Debit");
            p1.setTransactionAmount(Double.valueOf(ussdTransfer.getTranAmount()));

            PartTran p2 = new PartTran();
            p2.setCurrency("KES");
            p2.setAcid(ussdTransfer.getCreditAccount());
            p2.setIsoFlag('S');
            p2.setTransactionDate(new Date());
            p2.setExchangeRate("1");
//        p2.setAccountBalance();
            p2.setPartTranType("Credit");
            p2.setTransactionAmount(Double.valueOf(ussdTransfer.getTranAmount()));

            partTrans.add(p1);
            partTrans.add(p2);


            t.setPartTrans(partTrans);


            return t;

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error ", e.getMessage());
            return null;

        }
    }

    public List<MinistatementInterface> getMinistatement(String acid) {
        List<MinistatementInterface> ministatement = transactionHeaderRepository.getMinistatement(acid);
        return ministatement;
    }

    public List<TransactionStatementInterface> getTransactionStatement(String acid) {
        List<TransactionStatementInterface> TransactionStatement = transactionHeaderRepository.getTransactionStatement(acid);
        return TransactionStatement;
    }

    public List<MinistatementInterface> getstatement(String acid, String fromDate, String toDate) {

        List<MinistatementInterface> ministatement = transactionHeaderRepository.getstatement(acid, fromDate, toDate);

        return ministatement;
    }

    public Boolean balanceCheck(TransactionHeader transaction) {
        EntityResponse response = transactionProcessing.accountDebitBalanceValidation(transaction);
        System.out.println(response);
        if (response.getStatusCode() != HttpStatus.ACCEPTED.value())
            return false;
        response = transactionProcessing.isTransactionBalanced(transaction.getPartTrans());
        System.out.println(response);
        return response.getStatusCode() == HttpStatus.ACCEPTED.value();
    }
}