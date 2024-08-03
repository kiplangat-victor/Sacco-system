package com.emtechhouse.accounts.Models.Accounts.TermDeposit;

import com.emtechhouse.accounts.Models.Accounts.Account;
import com.emtechhouse.accounts.Models.Accounts.AccountClosure.AccountClosureInfo;
import com.emtechhouse.accounts.Models.Accounts.AccountClosure.AccountClosureRepo;
import com.emtechhouse.accounts.Models.Accounts.AccountRepository;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanRepository;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.GeneralProductDetails;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ItemServiceCaller;
import com.emtechhouse.accounts.Models.Accounts.TermDeposit.Accrual.TermDepositAccrualService;
import com.emtechhouse.accounts.Models.Accounts.TermDeposit.Schedule.TermDepositScheduleRepo;
import com.emtechhouse.accounts.Models.Accounts.TermDeposit.Schedule.TermDepositScheduleService;
import com.emtechhouse.accounts.Models.Accounts.TermDeposit.TermDepositTransactios.TermDepositTransaction;
import com.emtechhouse.accounts.Models.Accounts.TermDeposit.TermDepositTransactios.TermDepositTransactionRepo;
import com.emtechhouse.accounts.Models.Accounts.Validators.ValidatorService;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.PartTrans.PartTran;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TransactionHeader;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TransactionsController;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.emtechhouse.accounts.Utils.DatesCalculator;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TermDepositService {
    @Autowired
    private TermDepositScheduleRepo termDepositScheduleRepo;
    @Autowired
    private TermDepositRepo termDepositRepo;
    @Autowired
    private ValidatorService validatorService;
    @Autowired
    private TransactionsController transactionsController;
    @Autowired
    private TermDepositTransactionRepo termDepositTransactionRepo;
    @Autowired
    private TermDepositScheduleService termDepositScheduleService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ItemServiceCaller itemServiceCaller;
    @Autowired
    private DatesCalculator datesCalculator;
    @Autowired
    private TermDepositAccrualService termDepositAccrualService;
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private AccountClosureRepo accountClosureRepo;
    @Value("${accounts.fixed-deposit-repayment-account}")
    String interestExpenseAccount;

    //todo: cr account == term deposit account
    public EntityResponse initiateTermDepositAcCrTransaction(TermDeposit termDeposit){
        try {
            System.out.println("initiateTermDepositAcCrTransaction(TermDeposit termDeposit){");
            EntityResponse res =new EntityResponse<>();
            String crAcid= termDeposit.getAccount().getAcid();
            Double transactionAmount =termDeposit.getTermDepositAmount();
            String drAcid =termDeposit.getPrincipalDrAccountId();
            res=validatorService.acidValidator(drAcid, "Debit account");
            System.out.println(res);
            if(res.getStatusCode()== HttpStatus.OK.value()) {
                System.out.println("if(res.getStatusCode()== HttpStatus.OK.value()) {");
                res=validatorService.acidValidator(crAcid, "Credit account");
                System.out.println(res);
                if(res.getStatusCode()== HttpStatus.OK.value()) {
                    String transactionDescription="TDA DEPOSIT "+crAcid;
                    String currency="KES";
                    System.out.println();
                    TransactionHeader tranHeader= createTransactionHeader(currency,
                            transactionDescription,
                            transactionAmount,
                            drAcid,
                            crAcid, "ACRUAL");

                    EntityResponse transactRes= transactionsController.systemTransaction1(tranHeader).getBody();
                    if(!transactRes.getStatusCode().equals(HttpStatus.OK.value())){
                        ///failed transaction
                        //save failed transaction in disbursment report
                        saveTransactionDetails(crAcid,drAcid, transactionDescription, transactionAmount,
                                CONSTANTS.FAILED, CONSTANTS.NOT_FOUND, termDeposit);
                        //----------
                        EntityResponse response = new EntityResponse();
                        response.setMessage("TRANSACTION ERROR! COULD NOT PERFORM THE TRANSACTION");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity(tranHeader);

                        return response;
                    }else {
                        String transactionCode = (String) transactRes.getEntity();
                        //save successful transaction in disbursment report
                        saveTransactionDetails(crAcid, drAcid, transactionDescription, transactionAmount,
                                CONSTANTS.SUCCUSSEFUL, transactionCode, termDeposit);

                        EntityResponse response = new EntityResponse();
                        response.setMessage("THE TRANSACTION WAS SUCCESSFUL");
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(transactionCode);

                        return response;
                    }
                }else {
                    return res;
                }
            }else {
                return res;
            }
        }catch (Exception e) {
            log.info("Catched Error {} line: " +e.getStackTrace()[0].getClassName()+" "+e.getStackTrace()[0].getLineNumber()+" " + e);
            return null;
        }
    }

    public List<EntityResponse> initiateTermDepositPayment(String acid) {
        try {
            List<EntityResponse> res= new ArrayList<>();
            EntityResponse response = new EntityResponse<>();

            Optional<Account> ac= accountRepository.findByAcidAndDeleteFlag(acid,CONSTANTS.NO);
            if(ac.isPresent()) {
                Account account= ac.get();
                if(account.getAccountType().equals(CONSTANTS.TERM_DEPOSIT)) {
                    TermDeposit td= account.getTermDeposit();

                    return payMaturedTermDeposit(td);
                } else {
                    response.setMessage("Provided account is not a term deposit account");
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    res.add(response);
                }
            } else {
                response.setMessage("Account not found");
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                res.add(response);
            }
            return res;
        }catch (Exception e) {
            log.info("Catched Error {} line: " +e.getStackTrace()[0].getClassName()+" "+e.getStackTrace()[0].getLineNumber()+" " + e);
            return null;
        }
    }
    public List<EntityResponse> payMaturedTermDeposit(TermDeposit termDeposit) {
        try {
            List<EntityResponse> res= new ArrayList<>();
            EntityResponse response = new EntityResponse<>();
            //check whether it has matured
            Date maturityDate= termDeposit.getMaturityDate();
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            Date now = formatter.parse(formatter.format(new Date()));

            if(maturityDate.compareTo(now)<=0) {
                EntityResponse entityResponse = initiateTermDepositAcDrTransaction(termDeposit);
                res.add(entityResponse);
                return res;
            }else {
                response.setMessage("Failed !, term deposit maturity date is :"+maturityDate);
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                res.add(response);
                return res;
            }
        }catch (Exception e) {
            log.info("Catched Error {} line: " +e.getStackTrace()[0].getClassName()+" "+e.getStackTrace()[0].getLineNumber()+" " + e);
            return null;
        }
    }

    public TransactionHeader principalTransaction(String currency, String transactionDescription, Double principalAmount,
                                               String drAcid, String principalCrAcid) {
        TransactionHeader tranHeader= createTransactionHeader(currency,
                transactionDescription,
                principalAmount,
                drAcid,
                principalCrAcid, CONSTANTS.Normal);

      return tranHeader;
    }

    public TransactionHeader interestTransaction(String currency, String transactionDescription, Double interestAmount,
                                              String profitAndLossAccount, String interestCrAcid) {
        return createTransactionHeader(currency,
                transactionDescription,
                interestAmount,
                profitAndLossAccount,
                interestCrAcid,
                CONSTANTS.INTEREST);
    }

    //todo:principal ==> from term deposit ==> operative
    //todo: interest ==> transaction
    public EntityResponse initiateTermDepositAcDrTransaction(TermDeposit termDeposit) {
        try {
            List<EntityResponse> entityResponses= new ArrayList<>();
            EntityResponse res =new EntityResponse<>();
            String drAcid= termDeposit.getAccount().getAcid();
            String productCode=termDeposit.getAccount().getProductCode();
            //
//            Double principalAmount= termDeposit.getTermDepositAmount();
            Double principalAmount= termDeposit.getAccount().getAccountBalance();

            if (termDepositScheduleRepo.hasWithdrawals(termDeposit.getAccount().getAcid()) > 0 ) {
                System.out.println("This account has withdrawals");
                EntityResponse response = new EntityResponse();
                response.setMessage("This account has withdrawals");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                entityResponses.add(response);
                return response;
            }

            //todo: calculate interest amount
            System.out.println("To calculate interest");
            Double interestAmount= termDepositScheduleService.calcInterestToPay(termDeposit);
            if (interestAmount == null || interestAmount < 1 || interestAmount.isNaN()) {
                System.out.println("INTEREST AMOUNT CANNOT BE NULL");
                EntityResponse response = new EntityResponse();
                response.setMessage("INTEREST AMOUNT CANNOT BE NULL");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                entityResponses.add(response);
                return response;
            }
            System.out.println("Interest amount: "+interestAmount);
            //todo: get profit and loss account from product
            EntityResponse entityResponse= itemServiceCaller.getGeneralProductDetail1(
                    productCode,
                    CONSTANTS.LOAN_ACCOUNT);
            System.out.println(entityResponse);
            if(entityResponse.getStatusCode()== HttpStatus.OK.value()){
                GeneralProductDetails newGeneralProductDetails = (GeneralProductDetails) entityResponse.getEntity();

//                String profitAndLossAccount= newGeneralProductDetails.getPl_ac();
//                log.info("Profit and loss account is :: "+profitAndLossAccount);

                String principalCrAcid =termDeposit.getPrincipalCrAccountId();
                String interestCrAcid =termDeposit.getAccount().getAcid();

                //todo: accounts validations
                res=validatorService.acidValidator(drAcid, "Debit account");
                if(res.getStatusCode()== HttpStatus.OK.value()) {
                    res=validatorService.acidValidator(principalCrAcid, "Principal Credit account");
                    if(res.getStatusCode()== HttpStatus.OK.value()) {
                        res=validatorService.acidValidator(interestCrAcid, "Interest Credit account");
                        if(res.getStatusCode()== HttpStatus.OK.value()) {
                            double rollOverAmount = 0.0;


                            // TODO: 3/17/2023 -Transaction one
                            //todo: perform principal transaction
                            String transactionDescriptionPrincipal="TD Principal after closer "+drAcid;
                            String currency="KES";


                            String transactionDescriptionInterest="TD Interest for "+drAcid;

                            TransactionHeader header = interestTransaction(currency,
                                    transactionDescriptionInterest,
                                    interestAmount,
                                    interestExpenseAccount,
                                    interestCrAcid);

                            if (termDeposit.getRollOver() == 'Y') {
                                if (termDeposit.getRollOverType().equalsIgnoreCase("INTEREST_ONLY")){
                                    rollOverAmount = interestAmount;
                                }else  if (termDeposit.getRollOverType().equalsIgnoreCase("PRINCIPAL_ONLY")) {
                                    rollOverAmount = principalAmount;
                                }else  if (termDeposit.getRollOverType().equalsIgnoreCase("WHOLE_AMOUNT")) {
                                    rollOverAmount = principalAmount + interestAmount;
                                }
                                if (rollOverAmount > 0) {
                                    TransactionHeader transactionHeaderPrincipal = principalTransaction(currency,
                                            transactionDescriptionPrincipal,
                                            principalAmount+interestAmount-rollOverAmount,
                                            drAcid,
                                            principalCrAcid);
                                    header.getPartTrans().addAll(transactionHeaderPrincipal.getPartTrans());
                                }


                            } else {
                                TransactionHeader transactionHeaderPrincipal = principalTransaction(currency,
                                        transactionDescriptionPrincipal,
                                        principalAmount+interestAmount,
                                        drAcid,
                                        principalCrAcid);
                                header.getPartTrans().addAll(transactionHeaderPrincipal.getPartTrans());
                            }


                            // TODO: 3/17/2023 transaction 2
                            //todo: perform interest transaction
                            String currency2="KES";


                            EntityResponse transactRes1= transactionsController.systemTransaction1(header).getBody();
                            if(!transactRes1.getStatusCode().equals(HttpStatus.OK.value())) {
                                //----------
                                EntityResponse response = new EntityResponse();
                                response.setMessage("TRANSACTION ERROR! COULD NOT PERFORM THE TRANSACTION");
                                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                response.setEntity(null);

                                return response;
//                            return response;
                            } else {
                                String transactionCode = (String) transactRes1.getEntity();
                                //save successful transaction in disbursment report

                                saveTransactionDetails(principalCrAcid, drAcid, transactionDescriptionPrincipal, principalAmount,
                                        CONSTANTS.SUCCUSSEFUL, transactionCode, termDeposit);
                                saveTransactionDetails(interestCrAcid, interestExpenseAccount, transactionDescriptionPrincipal, interestAmount,
                                        CONSTANTS.SUCCUSSEFUL, transactionCode, termDeposit);
                                termDeposit.setInterestPaid(interestAmount);
                                termDeposit.setPrincipalPaid(principalAmount);
                                termDeposit.setPaymentDate(new Date());
                                termDeposit.getAccount().setAccountStatus(CONSTANTS.CLOSED);
                                termDepositRepo.save(termDeposit);

                                EntityResponse response = new EntityResponse();
                                response.setMessage("THE TRANSACTION WAS SUCCESSFUL AMT: "+interestAmount);
                                response.setStatusCode(HttpStatus.OK.value());
                                response.setEntity(transactionCode);

                                if (termDeposit.getRollOver() == 'Y' && rollOverAmount > 0) {
                                    rollOver(termDeposit.getId(), rollOverAmount);
                                }

                                return response;
//                            return response;
                            }

//                            entityResponses.add(response2);

                            // TODO: 3/17/2023 close account if the two transaction are successful
//                            closeTermDepositAccount(termDeposit);
                        } else {
                            entityResponses.add(res);
                        }
                    } else {
                        entityResponses.add(res);
                    }
                } else {
                    entityResponses.add(res);
                }
            } else {
                entityResponses.add(entityResponse);
            }
            return null;
        } catch (Exception e) {
            log.info("Catched Error {} line: " +e.getStackTrace()[0].getClassName()+" "+e.getStackTrace()[0].getLineNumber()+" " + e);
            return null;
        }
    }

    private void rollOver(Long id, double rollOverAmount) {
        System.out.println("In rollover");
       TermDeposit termDeposit =  termDepositRepo.getById(id);
       Account account = termDeposit.getAccount();
       termDeposit.setAccount(null);
       termDeposit.setId(null);
       account.getHistoricalTDAs().add(termDeposit);

       TermDeposit termDeposit1 =  termDepositRepo.getById(id);
       Date valueDate = termDeposit1.getMaturityDate();
        termDeposit1.setAccount(null);
        termDeposit1.setId(null);
        termDeposit1.setValueDate(valueDate);
        termDeposit1.setMaturityDate(datesCalculator.addDate(valueDate, termDeposit1.getRollOverDurationInMonths(), "MONTHS"));
        termDeposit1.setRollOver('N');
        termDeposit1.setRollOverType("");
        termDeposit1.setTermDepositTransactions(new ArrayList<>());
        termDeposit1.setTermDepositAmount(rollOverAmount);
        termDeposit1.setRollOverDurationInMonths(0);
        termDepositRepo.save(termDeposit1);
    }

    public EntityResponse verifyTermDepositAccount(Account account, String verifiedBy) {
        try {
            System.out.println("EntityResponse verifyTermDepositAccount(Account account, String verifiedBy) {");
            // all checks have been handled in the accounts service
            //perform transactions
            //update account flags so as to be able to perform the transactions
            account.setVerifiedBy(verifiedBy);
            account.setVerifiedTime(LocalDate.now().toDate());
            account.setVerifiedFlag(CONSTANTS.YES);
            Account savedAccount=accountRepository.save(account);

            TermDeposit t= savedAccount.getTermDeposit();
            System.out.println(t);
            //initiate a transaction
            EntityResponse res=initiateTermDepositAcCrTransaction(t);
            if(res.getStatusCode()==HttpStatus.OK.value()){
                //update term deposit status
                System.out.println("after tda transaction");
                termDepositRepo.updateTermDepositStatus(CONSTANTS.APPROVED, t.getId());

                String transactionCode = (String) res.getEntity();

                EntityResponse response = new EntityResponse<>();
                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage("Verification was successful, Transaction code :"+transactionCode);
                response.setEntity(res);
                return response;
            }else {
                account.setVerifiedBy(verifiedBy);
                account.setVerifiedTime(null);
                account.setVerifiedFlag(CONSTANTS.NO);
                Account acc=accountRepository.save(account);

                return res;
            }
        }catch (Exception e) {
            log.info("Catched Error {} line: " +e.getStackTrace()[0].getClassName()+" "+e.getStackTrace()[0].getLineNumber()+" " + e);
            return null;
        }
    }

    public void closeTermDepositAccount(TermDeposit termDeposit){
        try {
            log.info("Closing term deposit account");
            // TODO: 3/17/2023 check accrued interest
            Double sumAccruedAmt = termDeposit.getSumAccruedAmount();
            log.info("sum accrued amt is :: "+sumAccruedAmt);
            // TODO: 3/17/2023 check interest paid
            Double interestPaid= termDeposit.getInterestPaid();
            log.info("interest paid amt is :: "+interestPaid);
            if(interestPaid>sumAccruedAmt){
                termDepositAccrualService.accrueInterest(termDeposit);
            }
            termDepositRepo.updateTermDepositStatus(CONSTANTS.MATURED,termDeposit.getId());

            loanRepository.updateAccountStatus(CONSTANTS.CLOSED, termDeposit.getAccount().getId());
            AccountClosureInfo info= new AccountClosureInfo();
            info.setClosingDate(new Date());
            info.setPostedFlag(CONSTANTS.YES);
            info.setVerifiedFlag(CONSTANTS.NO);
            info.setClosureReason("Term deposit matured and paid");
            info.setAccount(termDeposit.getAccount());
            info.setClosedBy(CONSTANTS.SYSTEM_USERNAME);
            accountClosureRepo.save(info);
        }catch (Exception e){
            log.info("Catched Error {} line: " +e.getStackTrace()[0].getClassName()+" "+e.getStackTrace()[0].getLineNumber()+" " + e);
//            return null;
        }
    }

    public void saveTransactionDetails(String crAccount, String drAccount, String transcDesc, Double amt,
                                        String transcStatus, String transCode, TermDeposit termDeposit) {
        try{
            TermDepositTransaction termDepositTransaction = new TermDepositTransaction();
            termDepositTransaction.setCreditAccount(crAccount);
            termDepositTransaction.setDebitAccount(drAccount);
            termDepositTransaction.setTransactionDescription(transcDesc);
            termDepositTransaction.setAmount(amt);
            termDepositTransaction.setTransactionStatus(transcStatus);
            termDepositTransaction.setTransactionCode(transCode);
            termDepositTransaction.setTermDeposit(termDeposit);
            termDepositTransactionRepo.save(termDepositTransaction);
        }catch (Exception e){
            log.info("Catched Error {} line: " +e.getStackTrace()[0].getClassName()+" "+e.getStackTrace()[0].getLineNumber()+" " + e);
        }
    }
    public TransactionHeader createTransactionHeader(String loanCurrency,
                                                     String transDesc,
                                                     Double totalAmount,
                                                     String drAc,
                                                     String crAc, String identity){
        TransactionHeader transactionHeader = new TransactionHeader();
        transactionHeader.setTransactionType(CONSTANTS.SYSTEM_USERNAME);
        transactionHeader.setCurrency(loanCurrency);
        transactionHeader.setTransactionDate(new Date());
        transactionHeader.setEntityId(CONSTANTS.SYSTEM_ENTITY);
        transactionHeader.setTotalAmount(totalAmount);

        PartTran drPartTran = new PartTran();
        drPartTran.setPartTranType(CONSTANTS.DEBITSTRING);
        drPartTran.setTransactionAmount(totalAmount);
        drPartTran.setAcid(drAc);
        drPartTran.setCurrency(loanCurrency);
        drPartTran.setExchangeRate("");
        drPartTran.setTransactionDate(new Date());
        drPartTran.setTransactionParticulars(transDesc);
        drPartTran.setIsoFlag(CONSTANTS.YES);
        drPartTran.setParttranIdentity(identity);

        PartTran crPartTran = new PartTran();
        crPartTran.setPartTranType(CONSTANTS.CREDITSTRING);
        crPartTran.setTransactionAmount(totalAmount);
        crPartTran.setAcid(crAc);
        crPartTran.setCurrency(loanCurrency);
        crPartTran.setExchangeRate("");
        crPartTran.setTransactionDate(new Date());
        crPartTran.setTransactionParticulars(transDesc);
        crPartTran.setIsoFlag(CONSTANTS.YES);

        List<PartTran> partTranList =new ArrayList<>();
        partTranList.add(drPartTran);
        partTranList.add(crPartTran);

        transactionHeader.setPartTrans(partTranList);
        return transactionHeader;
    }
    public List<PartTran> createPartTrans(String loanCurrency,
                                                     String transDesc,
                                                     Double totalAmount,
                                                     String drAc,
                                                     String crAc, String identity) {
        PartTran drPartTran = new PartTran();
        drPartTran.setPartTranType(CONSTANTS.DEBITSTRING);
        drPartTran.setTransactionAmount(totalAmount);
        drPartTran.setAcid(drAc);
        drPartTran.setCurrency(loanCurrency);
        drPartTran.setExchangeRate("");
        drPartTran.setTransactionDate(new Date());
        drPartTran.setTransactionParticulars(transDesc);
        drPartTran.setIsoFlag(CONSTANTS.YES);
        drPartTran.setParttranIdentity(identity);

        PartTran crPartTran = new PartTran();
        crPartTran.setPartTranType(CONSTANTS.CREDITSTRING);
        crPartTran.setTransactionAmount(totalAmount);
        crPartTran.setAcid(crAc);
        crPartTran.setCurrency(loanCurrency);
        crPartTran.setExchangeRate("");
        crPartTran.setTransactionDate(new Date());
        crPartTran.setTransactionParticulars(transDesc);
        crPartTran.setIsoFlag(CONSTANTS.YES);

        List<PartTran> partTranList =new ArrayList<>();
        partTranList.add(drPartTran);
        partTranList.add(crPartTran);

        return partTranList;
    }
}
