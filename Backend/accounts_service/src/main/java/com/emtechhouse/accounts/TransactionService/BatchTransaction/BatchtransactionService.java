package com.emtechhouse.accounts.TransactionService.BatchTransaction;

import com.emtechhouse.accounts.Models.Accounts.Account;
import com.emtechhouse.accounts.Models.Accounts.AccountRepository;
import com.emtechhouse.accounts.TransactionService.BatchTransaction.CreditAccounts.CreditAccountsRepository;
import com.emtechhouse.accounts.TransactionService.BatchTransaction.CreditAccounts.Creditaccount;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.*;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.PartTrans.PartTran;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.emtechhouse.accounts.Utils.DataNotFoundException;
import com.emtechhouse.accounts.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.accounts.Utils.HttpInterceptor.UserRequestContext;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
public class BatchtransactionService {
    private final CreditAccountsRepository creditaccountsRepository;
    private final BatchtransactionRepository batchtransactionRepo;
    private final TranHeaderService tranHeaderService;
    private final TranHeaderRepository tranHeaderRepository;
    private final NewTransactionService newTransactionService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionsController transactionsController;

    @Autowired
    private TranHeaderRepository tranrepo;

    public BatchtransactionService(BatchtransactionRepository batchtransactionRepo, TranHeaderService tranHeaderService, TranHeaderRepository tranHeaderRepository, NewTransactionService newTransactionService,
                                   CreditAccountsRepository creditaccountsRepository) {
        this.batchtransactionRepo = batchtransactionRepo;
        this.tranHeaderService = tranHeaderService;
        this.tranHeaderRepository = tranHeaderRepository;
        this.newTransactionService = newTransactionService;
        this.creditaccountsRepository = creditaccountsRepository;
    }

    public Batchtransaction addBatchtransaction(Batchtransaction batchupload) {
        try {
            batchupload.setBatchUploadCode("M" + tranHeaderService.generateRandomCode(5).toUpperCase());
            batchupload.setEnteredBy(UserRequestContext.getCurrentUser());
            batchupload.setEnteredFlag(CONSTANTS.YES);
            batchupload.setEnteredTime(new Date());
            return batchtransactionRepo.save(batchupload);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public List<Batchtransaction> findAllBatchtransactions() {
        try {
            return batchtransactionRepo.findAll();
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public Batchtransaction findById(Long id) {
        try {
            return batchtransactionRepo.findById(id).orElseThrow(() -> new DataNotFoundException("Data " + id + "was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public Batchtransaction updateBatchtransaction(Batchtransaction batchupload) {
        try {
            return batchtransactionRepo.save(batchupload);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public void deleteBatchtransaction(Long id) {
        try {
            batchtransactionRepo.deleteById(id);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
        }
    }

    public Integer checkAccountExistance(String acid) {
        Integer isExist = tranHeaderRepository.checkAccountExistance(acid);
        return isExist;
    }

    public List<Batchtransaction> filterBydate(String fromDate, String toDate, String entityId, String action) {
        List<Batchtransaction> batchuploadList = new ArrayList<>();
        if (action.equalsIgnoreCase("Entered")) {
            batchuploadList = batchtransactionRepo.getentered(fromDate, toDate, entityId);

        } else if (action.equalsIgnoreCase("Modified")) {
            batchuploadList = batchtransactionRepo.getmodified(fromDate, toDate, entityId);
        } else if (action.equalsIgnoreCase("Posted")) {
            batchuploadList = batchtransactionRepo.getposted(fromDate, toDate, entityId);
        } else if (action.equalsIgnoreCase("Verified")) {
            batchuploadList = batchtransactionRepo.getVerified(fromDate, toDate, entityId);
        } else if (action.equalsIgnoreCase("Deleted")) {
            batchuploadList = batchtransactionRepo.getDeleted(fromDate, toDate, entityId);
        }
        return batchuploadList;
    }

    public String validateBatch(Batchtransaction batchupload) {
        Optional<Account> accountOptional = accountRepository.findByAcid(batchupload.getDebitAccount());
        if (!accountOptional.isPresent()) {
            return "Account: " + batchupload.getDebitAccount() + " does not exist";
        }
        Account mainAccount = accountOptional.get();
        if (!mainAccount.getAccountStatus().equalsIgnoreCase("ACTIVE")) {
            return "Account: " + batchupload.getDebitAccount() + " status is " + mainAccount.getAccountStatus();
        }
//        if (!mainAccount.getAccountType().equalsIgnoreCase("SBA")) {
//            return "Cannot debit "+batchupload.getDebitAccount()+" in batch processing ";
//        }

        Double totalAmount = 0.0;

        for (int i = 0; i < batchupload.getCreditaccounts().size(); i++) {
            Creditaccount employeedetails = batchupload.getCreditaccounts().get(i);
//            System.out.println(employeedetails);
            Optional<Account> accountOptional1 = accountRepository.findByAcid(employeedetails.getAccount());
            if (!accountOptional1.isPresent()) {
                return "Account: " + employeedetails.getAccount() + " does not exist";
            } else {
                System.out.println("Account: " + employeedetails.getAccount() + " exists ");
            }

            Account account = accountOptional1.get();
            if (account.getAccountStatus().equalsIgnoreCase("CLOSED")) {
                return "Account: " + employeedetails.getAccount() + " status is " + account.getAccountStatus();
            }

            if (!account.getAccountType().equalsIgnoreCase("SBA")) {
                return "Cannot credit " + batchupload.getDebitAccount() + " in batch processing. The account is " + account.getAccountType();
            }

//            System.out.println(account);
            System.out.println(employeedetails.getMemberNumber());
            System.out.println(account.getCustomerCode());
            System.out.println(account.getAcid());
            if (employeedetails.getMemberNumber().isEmpty() ||
                    !(account.getCustomerCode().trim().toUpperCase().endsWith(employeedetails.getMemberNumber().trim()))) {
                return "Account: " + employeedetails.getAccount() + " has has a wrong member number";
            }

            employeedetails.setAccountName(account.getAccountName());
            totalAmount += employeedetails.getAmount();
        }

        if (!mainAccount.getAccountType().equalsIgnoreCase("OAB") && mainAccount.getAccountBalance() < totalAmount) {
            return "Account: " + mainAccount.getAcid() + " does not have enough money";
        }
        return "";
    }

    public EntityResponse<?> salariesCount() {
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
                Integer totalSalaries = batchtransactionRepo.countAllSalaries();
                if (totalSalaries > 0) {
                    response.setMessage("Total Unverified Salaries is " + totalSalaries);
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(totalSalaries);
                } else {
                    response.setMessage("Total Unverified Salaries is " + totalSalaries);
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


    // TODO: 4/6/2023
    public EntityResponse verify(String batchUploadCode) {
        try {
            EntityResponse res = new EntityResponse<>();
            Optional<Batchtransaction> batchUpload = batchtransactionRepo.findByEntityIdAndBatchUploadCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), batchUploadCode, 'N');
            if (batchUpload.isPresent()) {
                Batchtransaction pBatchUpload = batchUpload.get();
                if (pBatchUpload.getVerifiedFlag().equals('N')) {
                    pBatchUpload.setVerifiedBy(UserRequestContext.getCurrentUser());
                    pBatchUpload.setVerifiedTime(new Date());
                    pBatchUpload.setVerifiedFlag('Y');
                    pBatchUpload = batchtransactionRepo.save(pBatchUpload);
                    //get employees details
                    res.setMessage("Verified Successfully");
                    res.setStatusCode(HttpStatus.OK.value());
                    res.setEntity(pBatchUpload);
                } else if (pBatchUpload.getVerifiedFlag_2().equals('N')) {
                    if (pBatchUpload.getVerifiedBy().trim().equalsIgnoreCase(UserRequestContext.getCurrentUser().trim())) {
                        res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        res.setMessage("Another person should do the second verification");
                    } else {
                        List<Creditaccount> emps = pBatchUpload.getCreditaccounts();
                        if (emps.size() > 0) {
                            String transactipnParticulars = pBatchUpload.getTranParticulars();
                            String drAcid = pBatchUpload.getDebitAccount();
                            String transactionType = CONSTANTS.BATCH_UPLOAD;
                            res = postTransaction1(drAcid, transactipnParticulars, transactionType, emps, pBatchUpload.getEventIdCode(), "Credit");

                            if (res.getStatusCode() == HttpStatus.CREATED.value()) {
                                JSONObject obj = new JSONObject(res.getEntity());
                                String transactionCode = obj.get("transactionCode").toString();
                                String transactionDate = obj.get("transactionDate").toString();

                                pBatchUpload.setDrTransactionDate(transactionDate);
                                pBatchUpload.setDrTransactionCode(transactionCode);
                                pBatchUpload.setVerifiedBy_2(UserRequestContext.getCurrentUser());
                                pBatchUpload.setVerifiedTime_2(new Date());
                                pBatchUpload.setVerifiedFlag_2('Y');
                                pBatchUpload = batchtransactionRepo.save(pBatchUpload);
                                res.setMessage("Verified Successfully " + transactionCode);
                                res.setStatusCode(HttpStatus.OK.value());
                                res.setEntity(pBatchUpload);
                            }
                        } else {
                            res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            res.setMessage("Could not find the attached employee details");
                        }
                    }
                } else {
                    res.setMessage("Batch already verified");
                    res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                }

            } else {
                res.setStatusCode(HttpStatus.NOT_FOUND.value());
                res.setMessage("Batch upload not found");
            }
            return res;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }


    // TODO: 4/6/2023
    public EntityResponse rejectBatchTransacion(String batchUploadCode) {
        try {
            EntityResponse res = new EntityResponse<>();
            Optional<Batchtransaction> batchUpload = batchtransactionRepo.findByEntityIdAndBatchUploadCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), batchUploadCode, 'N');
            if (batchUpload.isPresent()) {
                Batchtransaction pBatchUpload = batchUpload.get();
                if (pBatchUpload.getVerifiedFlag().equals('N')) {
                    pBatchUpload.setVerifiedBy(UserRequestContext.getCurrentUser());
                    pBatchUpload.setVerifiedTime(new Date());
                    pBatchUpload.setVerifiedFlag('Y');
                    pBatchUpload = batchtransactionRepo.save(pBatchUpload);
                    //get employees details
                } else if (pBatchUpload.getVerifiedFlag_2().equals('N')) {
                    if (pBatchUpload.getVerifiedBy().trim().equalsIgnoreCase(UserRequestContext.getCurrentUser().trim())) {
                        res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        res.setMessage("Another person should do the second verification");
                    } else {
                        List<Creditaccount> emps = pBatchUpload.getCreditaccounts();
                        if (emps.size() > 0) {
                            String transactipnParticulars = pBatchUpload.getTranParticulars();
                            String drAcid = pBatchUpload.getDebitAccount();
                            String transactionType = CONSTANTS.BATCH_UPLOAD;
                            res = postTransaction1(drAcid, transactipnParticulars, transactionType, emps, pBatchUpload.getEventIdCode(), "Credit");

                            if (res.getStatusCode() == HttpStatus.CREATED.value()) {
                                JSONObject obj = new JSONObject(res.getEntity());
                                String transactionCode = obj.get("transactionCode").toString();
                                String transactionDate = obj.get("transactionDate").toString();

                                pBatchUpload.setDrTransactionDate(transactionDate);
                                pBatchUpload.setDrTransactionCode(transactionCode);
                                pBatchUpload.setVerifiedBy_2(UserRequestContext.getCurrentUser());
                                pBatchUpload.setVerifiedTime_2(new Date());
                                pBatchUpload.setVerifiedFlag_2('Y');
                                pBatchUpload = batchtransactionRepo.save(pBatchUpload);
                                res.setMessage("Verified Successfully " + transactionCode);
                                res.setStatusCode(HttpStatus.OK.value());
                                res.setEntity(pBatchUpload);
                            }
                        } else {
                            res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            res.setMessage("Could not find the attached employee details");
                        }
                    }
                } else {
                    res.setMessage("Batch already verified");
                    res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                }

            } else {
                res.setStatusCode(HttpStatus.NOT_FOUND.value());
                res.setMessage("Batch upload not found");
            }
            return res;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse postTransaction1(String drAcid,
                                           String transactipnParticulars,
                                           String transactionType,
                                           List<Creditaccount> emps,
                                           String chargeCode,
                                           String chargeAccount) {
        try {
            AtomicReference<Double> credits = new AtomicReference<>((double) 0);
            AtomicReference<Double> debits = new AtomicReference<>((double) 0);
            EntityResponse response = new EntityResponse();
            String user = UserRequestContext.getCurrentUser();
            final Double[] totalDrAmt = {0.0};
            TransactionHeader transactionHeader = new TransactionHeader();
            transactionHeader.setVerifiedFlag('Y');
            transactionHeader.setVerifiedBy(user);
            transactionHeader.setVerifiedTime(new Date());
            transactionHeader.setVerifiedFlag_2('Y');
            transactionHeader.setVerifiedBy_2(user);
            transactionHeader.setVerifiedTime_2(new Date());
            transactionHeader.setTransactionType(transactionType);
            List<PartTran> partTranList = new ArrayList<>();
            //create dr partrans
            List<PartTran> partTranCrs = new ArrayList<>();
            emps.forEach(emp -> {
                String part = transactipnParticulars + " - " + emp.getParticulars();
                PartTran newP = createPartran(emp.getAccount(), round(emp.getAmount()), part);
                partTranCrs.add(newP);
                totalDrAmt[0] = totalDrAmt[0] + round(emp.getAmount());
                System.out.println(emp.getAccount());
            });
            PartTran partTranDr = new PartTran();
            partTranDr.setAcid(drAcid);
            partTranDr.setIsoFlag('Y');
            partTranDr.setExchangeRate("1");
            partTranDr.setParttranIdentity(CONSTANTS.Normal);
            partTranDr.setPartTranType(CONSTANTS.Debit);
            partTranDr.setTransactionAmount(totalDrAmt[0]);
            partTranDr.setTransactionDate(new Date());
            partTranDr.setTransactionParticulars(CONSTANTS.Debit + " " + transactipnParticulars);
            partTranDr.setParttranIdentity(CONSTANTS.Normal);
            partTranList.add(partTranDr);
            partTranList.addAll(partTranCrs);
            transactionHeader.setPartTrans(partTranList);
            response = newTransactionService.enter(transactionHeader);
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public double round(double a) {
        return Math.round(a * 100.0) / 100.0;
    }

    public PartTran createPartran(String crAcid, Double amount, String transactipnParticulars) {
        PartTran partTranCr = new PartTran();
        partTranCr.setAcid(crAcid);
        partTranCr.setIsoFlag('Y');
        partTranCr.setExchangeRate("1");
        partTranCr.setParttranIdentity(CONSTANTS.Normal);
        partTranCr.setPartTranType(CONSTANTS.Credit);
        partTranCr.setTransactionAmount(amount);
        partTranCr.setTransactionDate(new Date());
        partTranCr.setTransactionParticulars(transactipnParticulars);

        return partTranCr;
    }

    public PartTran createTaxPartranDividend(String account, Double amount, String tranType) {
        PartTran partTranCr = new PartTran();
        partTranCr.setAcid(account);
        partTranCr.setIsoFlag('Y');
        partTranCr.setExchangeRate("1");
        partTranCr.setParttranIdentity(CONSTANTS.Tax);
        partTranCr.setPartTranType(tranType);
        partTranCr.setTransactionAmount(amount);
        partTranCr.setTransactionDate(new Date());
        partTranCr.setTransactionParticulars("Withholding tax for dividends");

        return partTranCr;
    }


    public EntityResponse post(String batchUploadCode) {
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
                Optional<Batchtransaction> batchupload1 = batchtransactionRepo.findByEntityIdAndBatchUploadCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), batchUploadCode, 'N');
                if (batchupload1.isPresent()) {
                    Batchtransaction salUplaod = batchupload1.get();
                    if (salUplaod.getPostedFlag() == 'N') {
                        if (salUplaod.getVerifiedFlag_2() == 'Y') {
                            response = newTransactionService.post(salUplaod.getDrTransactionCode());
                            if (response.getStatusCode() == HttpStatus.OK.value()) {
                                salUplaod.setPostedTime(new Date());
                                salUplaod.setPostedBy(user);
                                salUplaod.setPostedFlag('Y');
                                batchtransactionRepo.save(salUplaod);
                                response.setMessage("Batch posted successfully");
                                response.setStatusCode(HttpStatus.OK.value());
                                response.setEntity(salUplaod);
                            } else {
                                response = response;
                            }
                        } else {
                            response.setMessage("Full verification needed before posting");
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        }
                    } else {
                        response.setMessage("Batch has already been posted!");
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
            log.error(e.getMessage());
            res.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
            res.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return res;
        }
    }

    public EntityResponse<?> batchTransactionsCount() {
        try {
            EntityResponse response = new EntityResponse();
            String user = UserRequestContext.getCurrentUser();
            String entityId = EntityRequestContext.getCurrentEntityId();
            if (user.isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            } else if (entityId.isEmpty()) {
                response.setMessage("Entity not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            } else {
                Integer totalBatchTransactions = batchtransactionRepo.countAllBatchTransactions();
                if (totalBatchTransactions > 0) {
                    response.setMessage("Total Unverified Batch Transactions is:- " + totalBatchTransactions);
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(totalBatchTransactions);
                } else {
                    response.setMessage("Total Unverified Batch Transactions is:- " + totalBatchTransactions);
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setEntity(totalBatchTransactions);
                }
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse<?> rejectBatchUpload(String batchUploadCode) {
        try {
            EntityResponse response = new EntityResponse();
            String user = UserRequestContext.getCurrentUser();
            String entityId = EntityRequestContext.getCurrentEntityId();
            if (user.isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            } else if (entityId.isEmpty()) {
                response.setMessage("Entity ID not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            } else {
                Optional<Batchtransaction> checkTransactionCode = batchtransactionRepo.findByBatchUploadCode(batchUploadCode);
                if (!checkTransactionCode.isPresent()) {
                    response.setMessage("Batch Transaction with Code : " + batchUploadCode + " NOT Registered in our DATABASES!!");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                } else {
                    Batchtransaction batchtransaction = checkTransactionCode.get();
                    if (batchtransaction.getEnteredBy().equalsIgnoreCase(user)) {
                        response.setMessage("Hello " + user + ", You Can NOT REJECT Transaction That You Created!!");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    } else if (batchtransaction.getVerifiedFlag_2().equals('Y')) {
                        response.setMessage("Batch Transaction ALREADY VERIFIED");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    } else if (batchtransaction.getRejectedFlag().equals('Y')) {
                        response.setMessage("Batch Transaction ALREADY REJECTED");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    } else {
                        batchtransaction.setRejectedBy(user);
                        batchtransaction.setRejectedTime(new Date());
                        batchtransaction.setRejectedFlag('Y');
                        Batchtransaction rejectTransaction = batchtransactionRepo.save(batchtransaction);
                        response.setMessage("Batch Transaction With Code :" + rejectTransaction.getBatchUploadCode() + " REJECTED Successfully At " + rejectTransaction.getRejectedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(rejectTransaction);
                    }
                }
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
}
