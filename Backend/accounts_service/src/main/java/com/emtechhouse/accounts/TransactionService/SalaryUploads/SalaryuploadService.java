package com.emtechhouse.accounts.TransactionService.SalaryUploads;


import com.emtechhouse.accounts.Models.Accounts.Account;
import com.emtechhouse.accounts.Models.Accounts.AccountRepository;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.DemandGenerationService;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.DemandSatisfaction.DemandSatisfactionService;
import com.emtechhouse.accounts.Models.Accounts.SavingContributionInstructions.SavingContributionInstructions;
import com.emtechhouse.accounts.Models.Accounts.SavingContributionInstructions.SavingContributionInstructionsRepo;
import com.emtechhouse.accounts.Models.Accounts.SavingContributionInstructions.SavingContributionInstructionsService;
import com.emtechhouse.accounts.Requests.LoanPrepayRequest;
import com.emtechhouse.accounts.TransactionService.SalaryUploads.EmployeeDetails.Employeedetails;
import com.emtechhouse.accounts.TransactionService.SalaryUploads.EmployeeDetails.EmployeedetailsRepo;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.*;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.PartTrans.PartTran;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.emtechhouse.accounts.Utils.DataNotFoundException;
import com.emtechhouse.accounts.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.accounts.Utils.HttpInterceptor.UserRequestContext;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class SalaryuploadService {
    private final EmployeedetailsRepo employeedetailsRepo;
    private final SalaryuploadRepo salaryuploadRepo;
    private final TranHeaderService tranHeaderService;
    private final TranHeaderRepository tranHeaderRepository;
    private final NewTransactionService newTransactionService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionsController transactionsController;

    @Autowired
    private DemandGenerationService demandGenerationService;
    //    @Autowired
//    private DemandGenerationService demandGenerationService;
    @Autowired
    private SavingContributionInstructionsService savingContributionInstructionsService;

    @Autowired
    private SavingContributionInstructionsRepo savingContributionInstructionsRepo;
    @Autowired
    private DemandSatisfactionService demandSatisfactionService;
    @Autowired
    private TranHeaderRepository tranrepo;

    public SalaryuploadService(SalaryuploadRepo salaryuploadRepo, TranHeaderService tranHeaderService, TranHeaderRepository tranHeaderRepository, NewTransactionService newTransactionService,
                               EmployeedetailsRepo employeedetailsRepo) {
        this.salaryuploadRepo = salaryuploadRepo;
        this.tranHeaderService = tranHeaderService;
        this.tranHeaderRepository = tranHeaderRepository;
        this.newTransactionService = newTransactionService;
        this.employeedetailsRepo = employeedetailsRepo;
    }

    public Salaryupload addSalaryupload(Salaryupload salaryupload) {
        try {
            salaryupload.setSalaryUploadCode("M" + tranHeaderService.generateRandomCode(5).toUpperCase());
            salaryupload.setEnteredBy(UserRequestContext.getCurrentUser());
            salaryupload.setEnteredFlag(CONSTANTS.YES);
            salaryupload.setEnteredTime(new Date());
            return salaryuploadRepo.save(salaryupload);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public List<Salaryupload> findAllSalaryuploads() {
        try {
            return salaryuploadRepo.findAll();
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public Salaryupload findById(Long id) {
        try {
            return salaryuploadRepo.findById(id).orElseThrow(() -> new DataNotFoundException("Data " + id + "was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public Salaryupload updateSalaryupload(Salaryupload salaryupload) {
        try {
            return salaryuploadRepo.save(salaryupload);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public void deleteSalaryupload(Long id) {
        try {
            salaryuploadRepo.deleteById(id);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
        }
    }

    public Integer checkAccountExistance(String acid) {
        Integer isExist = tranHeaderRepository.checkAccountExistance(acid);
        return isExist;
    }

    public List<Salaryupload> filterBydate(String fromDate, String toDate, String entityId, String action) {
        List<Salaryupload> salaryuploadList = new ArrayList<>();
        if (action.equalsIgnoreCase("Entered")) {
            salaryuploadList = salaryuploadRepo.getentered(fromDate, toDate, entityId);

        } else if (action.equalsIgnoreCase("Modified")) {
            salaryuploadList = salaryuploadRepo.getmodified(fromDate, toDate, entityId);
        } else if (action.equalsIgnoreCase("Posted")) {
            salaryuploadList = salaryuploadRepo.getposted(fromDate, toDate, entityId);
        } else if (action.equalsIgnoreCase("Verified")) {
            salaryuploadList = salaryuploadRepo.getVerified(fromDate, toDate, entityId);
        } else if (action.equalsIgnoreCase("Deleted")) {
            salaryuploadList = salaryuploadRepo.getDeleted(fromDate, toDate, entityId);
        }
        return salaryuploadList;
    }

    public String validateSalary(Salaryupload salaryupload) {
        Optional<Account> accountOptional = accountRepository.findByAcid(salaryupload.getDebitAccount());
        String errors = "";
        if (!accountOptional.isPresent()) {
            return " " + "Account: " + salaryupload.getDebitAccount() + " does not exist.";
        }
        Account mainAccount = accountOptional.get();

        if (!mainAccount.getAccountStatus().equalsIgnoreCase("ACTIVE")) {
            errors += " " + "Account: " + salaryupload.getDebitAccount() + " status is " + mainAccount.getAccountStatus() + ".";
        }

        if (!(mainAccount.getAccountType().equalsIgnoreCase("SBA")
                || mainAccount.getAccountType().equalsIgnoreCase("OAB"))) {
            errors += " " + "Cannot debit " + salaryupload.getDebitAccount() + " in salary processing. ";
        }

        Double totalAmount = 0.0;

        for (int i = 0; i < salaryupload.getEmployeeDetails().size(); i++) {
            System.out.println("Loop through recei");
            Employeedetails employeedetails = salaryupload.getEmployeeDetails().get(i);
//            System.out.println(employeedetails);
            if (employeedetails.getAccount() == null || employeedetails.getAccount().isEmpty()) {
                Optional<String> acid = accountRepository.getAccountByCustomerCodeAndNationalId(employeedetails.getMemberNumber(), employeedetails.getIdNumber());
                System.out.println("Before check avail");
                if (!acid.isPresent()) {
                    System.out.println("In not found");
                    acid = accountRepository.getAccountByCustomerCodeAndNationalId("0" + employeedetails.getMemberNumber(), employeedetails.getIdNumber());
                    employeedetails.setMemberNumber("0" + employeedetails.getMemberNumber());
                    System.out.println(salaryupload);
                }
                if (!acid.isPresent()) {
                    errors += " " + "Could not find account for member " + employeedetails.getMemberNumber() + ".";
                    continue;
                }
                employeedetails.setAccount(acid.get());
                System.out.println(acid.get());
            }

//            System.out.println(employeedetails);
            Optional<Account> accountOptional1 = accountRepository.findByAcid(employeedetails.getAccount());
            if (!accountOptional1.isPresent()) {
                errors += " " + "Account: " + employeedetails.getAccount() + " does not exist.";
                continue;
            } else {
                System.out.println("Account: " + employeedetails.getAccount() + " exists. ");
            }

            Account account = accountOptional1.get();
            if (account.getAccountStatus().equalsIgnoreCase("CLOSED")) {
                errors += " " + "Account: " + employeedetails.getAccount() + " status is " + account.getAccountStatus() + ".";
            }

            if (!account.getAccountType().equalsIgnoreCase("SBA")) {
                errors += " " + "Cannot credit " + employeedetails.getAccount() + " in salary processing. The account is " + account.getAccountType() + ".";
            }

//            System.out.println(account);
//            System.out.println(employeedetails.getMemberNumber());
//            System.out.println(account.getCustomerCode());
            System.out.println(account.getAcid());
            if (employeedetails.getMemberNumber().isEmpty()) {
                errors += " " + "Account: " + employeedetails.getAccount() + " has a wrong member number.";
            }

            if (!(account.getCustomerCode().trim().toUpperCase().equalsIgnoreCase(employeedetails.getMemberNumber().trim()))) {
                if ((account.getCustomerCode().trim().toUpperCase().equalsIgnoreCase("0" + employeedetails.getMemberNumber().trim()))) {
                    employeedetails.setMemberNumber("0" + employeedetails.getMemberNumber());
                } else {
                    errors += " " + "Account: " + employeedetails.getAccount() + " has has a wrong member number.";
                }
            }

            employeedetails.setAccountName(account.getAccountName());
            totalAmount += employeedetails.getAmount();
        }

        if (mainAccount.getAccountType().equalsIgnoreCase("SBA")
                && mainAccount.getAccountBalance() < totalAmount) {
            errors += " " + "Account: " + mainAccount.getAcid() + " does not have enough money. ";
        }
        return errors;
    }

    // TODO: 4/6/2023
    public EntityResponse verify(String salaryUploadCode) {
        try {
            EntityResponse res = new EntityResponse<>();
            Optional<Salaryupload> salaryUpload = salaryuploadRepo.findByEntityIdAndSalaryUploadCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), salaryUploadCode, 'N');
            if (salaryUpload.isPresent()) {
                Salaryupload pSalaryUpload = salaryUpload.get();
                if (pSalaryUpload.getVerifiedFlag().equals('N')) {
                    pSalaryUpload.setVerifiedBy(UserRequestContext.getCurrentUser());
                    pSalaryUpload.setVerifiedTime(new Date());
                    pSalaryUpload.setVerifiedFlag('Y');
                    pSalaryUpload = salaryuploadRepo.save(pSalaryUpload);
                    res.setStatusCode(HttpStatus.OK.value());
                    res.setMessage("Verified successfully");
                    res.setEntity(pSalaryUpload);
                } else if (pSalaryUpload.getVerifiedFlag_2().equals('N')) {
                    if (pSalaryUpload.getVerifiedBy().trim().equalsIgnoreCase(UserRequestContext.getCurrentUser().trim())) {
                        res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        res.setMessage("Another person should do the second verification");
                    } else {
                        List<Employeedetails> emps = pSalaryUpload.getEmployeeDetails();
                        if (emps.size() > 0) {
                            String transactipnParticulars = pSalaryUpload.getTranParticulars();
                            String drAcid = pSalaryUpload.getDebitAccount();
                            String transactionType = CONSTANTS.SALARY_UPLOAD;
                            String chargeCode = pSalaryUpload.getEventIdCode();
                            String chargeAccount = pSalaryUpload.getChargeFrom();
                            res = postTransaction1(drAcid, transactipnParticulars, transactionType, emps, chargeCode, chargeAccount);

                            if (res.getStatusCode() == HttpStatus.CREATED.value()) {
                                JSONObject obj = new JSONObject(res.getEntity());
                                String transactionCode = obj.get("transactionCode").toString();
                                String transactionDate = obj.get("transactionDate").toString();
                                Double charge = 0.00;
                                JSONArray ja = obj.getJSONArray("partTrans");
                                for (Object o : ja) {
                                    JSONObject ob = new JSONObject(o.toString());
                                    String parttranIdentity = ob.get("parttranIdentity").toString();
                                    if (parttranIdentity.equalsIgnoreCase(CONSTANTS.Fee)) {
                                        charge = ob.getDouble("transactionAmount");
                                    } else {
                                        charge = 0.00;
                                    }
                                }
                                pSalaryUpload.setDrTchargeAmount(charge);
                                pSalaryUpload.setDrTransactionDate(transactionDate);
                                pSalaryUpload.setDrTransactionCode(transactionCode);
                                pSalaryUpload.setVerifiedBy_2(UserRequestContext.getCurrentUser());
                                pSalaryUpload.setVerifiedTime_2(new Date());
                                pSalaryUpload.setVerifiedFlag_2('Y');
                                pSalaryUpload = salaryuploadRepo.save(pSalaryUpload);
                                res.setMessage("Verified Successfully " + transactionCode);
                                res.setStatusCode(HttpStatus.OK.value());
                                res.setEntity(pSalaryUpload);
                            }
                        } else {
                            res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            res.setMessage("Could not find the attached employee details");
                        }
                    }
                } else {
                    res.setMessage("Salary already verified");
                    res.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                }

            } else {
                res.setStatusCode(HttpStatus.NOT_FOUND.value());
                res.setMessage("Salary upload not found");
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
                                           List<Employeedetails> emps,
                                           String chargeCode,
                                           String chargeAccount) {
        try {
            log.info("posting a transaction");
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
                String part = transactipnParticulars + " for member " + emp.getMemberNumber();
                PartTran newP = createPartran(emp.getAccount(), emp.getAmount(), part);
                partTranCrs.add(newP);
                totalDrAmt[0] = totalDrAmt[0] + emp.getAmount();
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
            response = newTransactionService.enterSalaryProcessing(transactionHeader, chargeCode,
                    chargeAccount);
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
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


    public EntityResponse post(String salaryUploadCode) {
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
                Optional<Salaryupload> salaryupload1 = salaryuploadRepo.findByEntityIdAndSalaryUploadCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), salaryUploadCode, 'N');
                if (salaryupload1.isPresent()) {
                    Salaryupload salUplaod = salaryupload1.get();
                    if (salUplaod.getPostedFlag() == 'N') {
                        if (salUplaod.getVerifiedFlag_2() == 'Y') {
                            response = newTransactionService.post(salUplaod.getDrTransactionCode());
                            if (response.getStatusCode() == HttpStatus.OK.value()) {

                                salUplaod.setPostedTime(new Date());
                                salUplaod.setPostedBy(user);
                                salUplaod.setPostedFlag('Y');
                                salaryuploadRepo.save(salUplaod);

                                deductFromSalaries(salUplaod);

                                response.setMessage("Salary posted successfully");
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
                        response.setMessage("Salary has already been posted!");
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

    private void deductFromSalaries(Salaryupload salUplaod) {
        if (salUplaod.getRecipientType() != null && salUplaod.getRecipientType().equalsIgnoreCase("Suppliers")) {
            System.out.println("Ignoring deductions");
            return;
        }

        for (Employeedetails employeedetails : salUplaod.getEmployeeDetails()) {
            Optional<SavingContributionInstructions> instructions = savingContributionInstructionsRepo.findDueByCustomerCode(employeedetails.getMemberNumber(), 15);
            if (instructions.isPresent()) {
                savingContributionInstructionsService.executeOne(instructions.get(), employeedetails.getAccount());
            }
            Double totalDeducted = 0.0;
            List<Account> loans = accountRepository.getLoansByCustomerCode(employeedetails.getMemberNumber());
            for (Account loan : loans) {
                if (loan.getLoan().getPausedSatisfactionFlag() == 'Y' )
                    continue;
                Double amount = loan.getLoan().getInstallmentAmount();
                Double availableAmount = accountRepository.getAvailableBalance(employeedetails.getAccount());
                if (amount + totalDeducted > ((employeedetails.getAmount() * 2) / 3)) {
                    amount = ((employeedetails.getAmount() * 2) / 3) - totalDeducted;
                }
                if (availableAmount < amount) {
                    amount = availableAmount * 2 / 3;
                }
                if (amount > Math.abs(loan.getAccountBalance())) {
                    amount = Math.abs(loan.getAccountBalance());
                }
                LoanPrepayRequest loanPrepayRequest = new LoanPrepayRequest(loan.getAcid(), 'N', 'N');
                loanPrepayRequest.setAmount(amount);
                loanPrepayRequest.setRepaymentAccount(employeedetails.getAccount());
                totalDeducted += amount;
                demandGenerationService.demandManualForce(loan.getAcid(), 15);
                demandSatisfactionService.satisfyDemandManualForce(loan.getAcid());
            }
        }
    }

    public EntityResponse<?> salaryUploadsCount() {
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
                Integer totalSalaryUploads = salaryuploadRepo.countAllSalaryUploads();
                if (totalSalaryUploads > 0) {
                    response.setMessage("Total Unverified Transactions is : " + totalSalaryUploads);
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(totalSalaryUploads);
                } else {
                    response.setMessage("Total Unverified Transactions is : " + totalSalaryUploads);
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setEntity(totalSalaryUploads);
                }
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse rejectSalaryUpload(String salaryUploadCode) {
        try {
            EntityResponse response = new EntityResponse<>();
            Optional<Salaryupload> checkSalaryUploadCode = salaryuploadRepo.findByEntityIdAndSalaryUploadCodeAndDeletedFlag(EntityRequestContext.getCurrentEntityId(), salaryUploadCode, 'N');
            if (checkSalaryUploadCode.isPresent()) {
                Salaryupload salaryupload = checkSalaryUploadCode.get();
                if (salaryupload.getEnteredBy().equalsIgnoreCase(UserRequestContext.getCurrentUser())) {
                    response.setMessage("Hello " + UserRequestContext.getCurrentUser() + ", You Can NOT REJECT Transaction That You Created!!");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                } else if (salaryupload.getRejectedFlag().equals('Y')) {
                    response.setMessage("Transaction Salary Upload With Code :" + checkSalaryUploadCode.get().getSalaryUploadCode() + " ALREADY REJECTED");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                } else if (salaryupload.getVerifiedFlag_2().equals('Y')) {
                    response.setMessage("Transaction Salary Upload ALREADY VERIFIED");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                } else {
                    salaryupload.setRejectedBy(UserRequestContext.getCurrentUser());
                    salaryupload.setRejectedTime(new Date());
                    salaryupload.setRejectedFlag('Y');
                    Salaryupload rejectSalaryUpLoad = salaryuploadRepo.save(salaryupload);
                    response.setMessage("Transaction Salary Upload With Code :" + rejectSalaryUpLoad.getSalaryUploadCode() + " REJECTED Successfully At " + rejectSalaryUpLoad.getRejectedTime());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(rejectSalaryUpLoad);
                }
            } else {
                response.setMessage("No Salary Upload Found:");
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
}