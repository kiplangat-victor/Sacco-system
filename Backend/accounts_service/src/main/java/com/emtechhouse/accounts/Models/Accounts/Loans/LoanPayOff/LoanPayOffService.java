package com.emtechhouse.accounts.Models.Accounts.Loans.LoanPayOff;

import com.emtechhouse.accounts.Models.Accounts.Account;
import com.emtechhouse.accounts.Models.Accounts.AccountClosure.AccountClosureInfo;
import com.emtechhouse.accounts.Models.Accounts.AccountClosure.AccountClosureRepo;
import com.emtechhouse.accounts.Models.Accounts.AccountDocuments.AccountDocumentRepository;
import com.emtechhouse.accounts.Models.Accounts.AccountRepository;
import com.emtechhouse.accounts.Models.Accounts.AccountServices.AccountTransactionService;
import com.emtechhouse.accounts.Models.Accounts.Loans.Loan;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.DemandGenerationService;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.DemandSatisfaction.DemandSatisfactionService;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.DemandSatisfaction.LoanDemandSatisfactionRepo;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.GenerateDemandsForOneLoan;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.LoanDemand;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.LoanDemandRepository;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanRepository;
import com.emtechhouse.accounts.Models.Accounts.Validators.ValidatorService;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.PartTrans.PartTran;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TransactionHeader;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TransactionsController;
import com.emtechhouse.accounts.Utils.AcidGenerator;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.emtechhouse.accounts.Utils.HttpInterceptor.UserRequestContext;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.Cache;
import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class LoanPayOffService {
    @Autowired
    private LoanDemandRepository loanDemandRepository;
    @Autowired
    private LoanDemandSatisfactionRepo loanDemandSatisfactionRepo;
    @Autowired
    private ValidatorService validatorService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private LoanPayOffRepo loanPayOffRepo;
    @Autowired
    private AccountTransactionService accountTransactionService;
    @Autowired
    private EntityManager em;
    @Autowired
    private TransactionsController transactionsController;

    @Autowired
    private AccountClosureRepo accountClosureRepo;
    @Autowired
    private GenerateDemandsForOneLoan generateDemandsForOneLoan;
    @Autowired
    private DemandSatisfactionService demandSatisfactionService;
    @Autowired
    private DemandGenerationService demandGenerationService;
    @Autowired
    private AcidGenerator acidGenerator;

    public EntityResponse<?> initiateLoanPayOff(String loanAccountAcid, String operativeAccountAcid){
        try {
            //validate all the provided accounts
            EntityResponse operativeAcEty= validatorService.acidValidator(operativeAccountAcid, "Operative account");
            if(operativeAcEty.getStatusCode().equals(HttpStatus.OK.value())){
                EntityResponse loanAcEty= validatorService.acidValidator(loanAccountAcid, "Loan account");
                if(loanAcEty.getStatusCode().equals(HttpStatus.OK.value())){
                    Account loanAccount= accountRepository.findByAccountId(loanAccountAcid).get();
                    EntityResponse entityResponse= new EntityResponse<>();
                    if(loanAccount.getLoan()!=null){
                        Loan loan = loanAccount.getLoan();
                        if(loan.getLoanStatus().equals(CONSTANTS.DISBURSED)){
                            if(loan.getPayOffFlag() != null){
                                log.info("flag==="+ loan.getPayOffFlag());
                                if(!loan.getPayOffFlag().equals(CONSTANTS.YES)){
                                    //calculate the payoff amount i.e total loan balances
                                    Double loanTotalBalance = loan.getTotalLoanBalance();
                                    Double overFlowAmount =loan.getOverFlowAmount();
                                    Double payOffAmount= loanTotalBalance-overFlowAmount;
                                    if(loanTotalBalance>=overFlowAmount){
                                        if(payOffAmount>=0){
                                            // confirm if the operative account balance is equal or greater than payoff
                                            Account operativeAccount= accountRepository.findByAccountId(operativeAccountAcid).get();
                                            Double operativeAcBalance= operativeAccount.getAccountBalance();

                                            //get operative account type
                                            String accountType= operativeAccount.getAccountType();

                                            if(operativeAcBalance>=payOffAmount || accountType.equals(CONSTANTS.OFFICE_ACCOUNT)){

                                                if(operativeAccount.getIsWithdrawalAllowed()){
                                                    //update payoff flag
//                                    loan.setPayOffFlag(CONSTANTS.YES);
                                                    loanRepository.updatePayoffFlag(CONSTANTS.YES, loan.getSn());
//                                    loan.setPayOffDate(new Date());
                                                    loanRepository.updatePayOffDate(new Date(), loan.getSn());
//                                    loan.setPayOffInitiatedBy(UserRequestContext.getCurrentUser());
                                                    loanRepository.updatePayOffInitiatedBy(UserRequestContext.getCurrentUser(), loan.getSn());
//                                    loanRepository.save(loan)

                                                    //add loan payoff info
                                                    LoanPayOffInfo loanPayOffInfo= new LoanPayOffInfo();
                                                    loanPayOffInfo.setLoan(loan);
                                                    loanPayOffInfo.setOperativeAccount(operativeAccountAcid);
                                                    loanPayOffInfo.setPayOffAmount(payOffAmount);
                                                    LoanPayOffInfo sL=loanPayOffRepo.save(loanPayOffInfo);


                                                    entityResponse.setMessage("Pay off was successfully initiated, now waiting for verification");
                                                    entityResponse.setStatusCode(HttpStatus.OK.value());
                                                    entityResponse.setEntity(sL);
                                                }else {
                                                    entityResponse.setMessage("Account withdrawal is not allowed");
                                                    entityResponse.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                                }

                                            }else{
                                                entityResponse.setMessage("Operative account balance is less than total loan balance of: "+payOffAmount.toString());
                                                entityResponse.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                            }
                                        }else{
                                            entityResponse.setMessage("Seems like you have overflows amounting to "+(overFlowAmount-loanTotalBalance));
                                            entityResponse.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                        }

                                    }else {
                                        entityResponse.setMessage("Seems like you have overflows amounting to "+(overFlowAmount-loanTotalBalance));
                                        entityResponse.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                    }

                                }else {
                                    entityResponse.setMessage("Pay off was initiated on  "+ loan.getPayOffDate().toString());
                                    entityResponse.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                }
                            }else {
                                log.info("flag==null===="+ loan.getPayOffFlag());
                                //calculate the payoff amount i.e total loan balances
                                Double loanTotalBalance = loan.getTotalLoanBalance();
                                Double overFlowAmount =loan.getOverFlowAmount();
                                Double payOffAmount= loanTotalBalance-overFlowAmount;
                                // confirm if the operative account balance is equal or greater than payoff
                                Account operativeAccount= accountRepository.findByAccountId(operativeAccountAcid).get();

                                //get operative account type
                                String accountType= operativeAccount.getAccountType();

                                Double operativeAcBalance= operativeAccount.getAccountBalance();

                                if(operativeAcBalance>=payOffAmount || accountType.equals(CONSTANTS.OFFICE_ACCOUNT)){
                                    if(operativeAccount.getIsWithdrawalAllowed()){
                                        //update payoff flag
//                                    loan.setPayOffFlag(CONSTANTS.YES);
                                        loanRepository.updatePayoffFlag(CONSTANTS.YES, loan.getSn());
//                                    loan.setPayOffDate(new Date());
                                        loanRepository.updatePayOffDate(new Date(), loan.getSn());
//                                    loan.setPayOffInitiatedBy(UserRequestContext.getCurrentUser());
                                        loanRepository.updatePayOffInitiatedBy(UserRequestContext.getCurrentUser(), loan.getSn());
//                                    loanRepository.save(loan);

                                        //add loan payoff info
                                        LoanPayOffInfo loanPayOffInfo= new LoanPayOffInfo();
                                        loanPayOffInfo.setLoan(loan);
                                        loanPayOffInfo.setOperativeAccount(operativeAccountAcid);
                                        loanPayOffInfo.setPayOffAmount(payOffAmount);
//                                    loanPayOffInfo.set
                                        LoanPayOffInfo sL=loanPayOffRepo.save(loanPayOffInfo);

//                                    AccountClosureInfo info= new AccountClosureInfo();
//                                    info.setClosingDate(new Date());
//                                    info.setPostedFlag(CONSTANTS.YES);
//                                    info.setVerifiedFlag(CONSTANTS.NO);
//                                    info.setClosureReason("Loan Pay off");
//                                    info.setAccount(loanAccount);
//                                    info.setClosedBy(UserRequestContext.getCurrentUser());
//                                    accountClosureRepo.save(info);

                                        entityResponse.setMessage("Pay off was successfully initiated, now waiting for verification");
                                        entityResponse.setStatusCode(HttpStatus.OK.value());
                                        entityResponse.setEntity(sL);
                                    }else {
                                        entityResponse.setMessage("Account withdrawal is not allowed");
                                        entityResponse.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                    }

                                }else {
                                    entityResponse.setMessage("Operative account balance is less than total loan balance of: "+payOffAmount.toString());
                                    entityResponse.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                }
                            }
                        }else {
                            entityResponse.setMessage("Loan not disbursed or either fully paid");
                            entityResponse.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        }
                    }else{
                        entityResponse.setMessage("Provided account is not a loan account");
                        entityResponse.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    }
                    return entityResponse;
                }else {
                    return loanAcEty;
                }
            }else {
                return operativeAcEty;
            }
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }

    public EntityResponse<?> getPayOffAmount(String loanAcid){
        try {
            EntityResponse acidEty= validatorService.acidValidator(loanAcid, "Operative account");
            if(acidEty.getStatusCode().equals(HttpStatus.OK.value())){
                Account loanAccount= accountRepository.findByAccountId(loanAcid).get();
                EntityResponse entityResponse= new EntityResponse<>();
                if(loanAccount.getLoan()!=null){
                    Loan loan = loanAccount.getLoan();
                    if(loan.getLoanStatus().equals(CONSTANTS.DISBURSED)){
                        Double loanTotalBalance = loan.getTotalLoanBalance();
                        Double overFlowAmount =loan.getOverFlowAmount();
                        Double payOffAmount= loanTotalBalance-overFlowAmount;

                        entityResponse.setMessage("Loan Payoff For account: " + loanAcid+" is: " + payOffAmount);
                        entityResponse.setStatusCode(HttpStatus.OK.value());
                        entityResponse.setEntity(payOffAmount);
                    }else {
                        entityResponse.setMessage("Loan not disbursed or either fully paid");
                        entityResponse.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    }
                }else {
                    entityResponse.setMessage("Provided account is not a loan account");
                    entityResponse.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                }
                return entityResponse;
            }else {
                return acidEty;
            }
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }

//    public EntityResponse<?> verifyLoanPayOff(String loanAcid){
//        try {
//            EntityResponse acidEty= validatorService.acidValidator(loanAcid, "Loan account");
//            if(acidEty.getStatusCode().equals(HttpStatus.OK.value())){
//                Account loanAccount= accountRepository.findByAccountId(loanAcid).get();
//                EntityResponse entityResponse= new EntityResponse<>();
//                if(loanAccount.getLoan()!=null){
//                    Loan loan = loanAccount.getLoan();
//                    if(loan.getPayOffFlag() !=null){
//                        if(loan.getPayOffFlag().equals(CONSTANTS.YES)){
//                            if(!loan.getPayOffInitiatedBy().equals(UserRequestContext.getCurrentUser())){
//
//                                //get operative account id
//                                LoanPayOffInfo loanPayOffInfo= loan.getLoanPayOffInfo();
//                                String operativeAccountId= loanPayOffInfo.getOperativeAccount();
//                                String loanAccountAcid= loan.getAccount().getAcid();
//
//                                //calculate the payoff amount i.e total loan balances
//                                Double loanTotalBalance = loan.getTotalLoanBalance();
//                                Double overFlowAmount =loan.getOverFlowAmount();
//                                Double payOffAmount= loanTotalBalance-overFlowAmount;
//
//                                // confirm if the operative account balance is equal or greater than payoff
//                                Account operativeAccount= accountRepository.findByAccountId(operativeAccountId).get();
//                                Double operativeAcBalance= operativeAccount.getAccountBalance();
//
//                                //get operative account type
//                                String accountType= operativeAccount.getAccountType();
//
//                                if(operativeAcBalance>=payOffAmount || accountType.equals(CONSTANTS.OFFICE_ACCOUNT)){
//                                    String loanCurrency= loan.getAccount().getCurrency();
//                                    //initialize a pay off transaction
//                                    String transactionDescription="LOAN PAY OFF ROR ACCOUNT: "+loanAccountAcid;
//
//                                    TransactionHeader tranHeader= createTransactionHeader(loanCurrency,
//                                            transactionDescription,
//                                            payOffAmount,
//                                            operativeAccountId,
//                                            loanAccountAcid);
//
//                                    EntityResponse transactionRes= transactionsController.systemTransaction1(tranHeader).getBody();
//                                    if(!transactionRes.getStatusCode().equals(HttpStatus.OK.value())){
//                                        ///failed transactiom
//                                        EntityResponse response = new EntityResponse();
//                                        response.setMessage("TRANSACTION ERROR! COULD NOT PERFORM PAY OFF TRANSACTION FOR LOAN ACCOUNT: "+loanAccountAcid);
//                                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                                        response.setEntity(tranHeader);
//                                        return response;
//                                    }else {
//                                        String transactionCode= (String) transactionRes.getEntity();
//
//                                        //update loan payoff info
////                                        loanPayOffInfo.setPayOffAmount(payOffAmount);
////                                        loanPayOffInfo.setTransactionCode(transactionCode);
////                                        loanPayOffRepo.save(loanPayOffInfo);
//
//                                        loanPayOffRepo.updatePayOffAmount(payOffAmount, loanPayOffInfo.getId());
//                                        loanPayOffRepo.updateTransactionCode(transactionCode, loanPayOffInfo.getId());
//
//                                        //update loan
//                                        em.getEntityManagerFactory().getCache().evictAll();
//
//
//                                        Cache cache = em.getEntityManagerFactory().getCache();
//                                        Double amt= -10000.0;
//                                        if (cache.contains(Account.class, amt)) {
//                                            // the data is cached
//                                            log.info("cached");
//                                        } else {
//                                            // the data is NOT cached
//                                            log.info("not cached");
//                                        }
//                                        Account loanAccount1= accountRepository.findByAccountId(loanAcid).get();
//                                        log.info("Calling update");
//                                        Double accountBalance=loanPayOffRepo.fetchAccountBalance(loanAcid);
//                                        loanAccount1.setAccountBalance(accountBalance);
//                                        log.info("acccc bal:"+ loanAccount1.getAccountBalance());
//                                        Loan loan1=loanAccount1.getLoan();
//
////                                        loan1.setPayOffVerifiedBy(UserRequestContext.getCurrentUser());
//                                        loanRepository.updatePayOffVerifiedBy(UserRequestContext.getCurrentUser(), loan.getSn());
////                                        loan1.setPayOffVerificationFlag(CONSTANTS.YES);
//                                        loanRepository.updatePayOffVerificationFlag(CONSTANTS.YES, loan.getSn());
////                                        loan1.setPayOffVerificationDate(new Date());
//                                        loanRepository.updatePayOffVerificationDate(new Date(), loan.getSn());
//
//                                        //---------tb updated
//                                        loan1.setLoanStatus(CONSTANTS.FULLY_PAID);
//                                        loan1.setOverFlowAmount(0.00);
//                                        loan1.setTotalLoanBalance(0.00);
////
////                                        //update account status to be closed
//                                        loanAccount1.setAccountStatus(CONSTANTS.CLOSED);
//                                        loanAccount1.setLoan(loan1);
//
//                                        //account closure info
//                                        AccountClosureInfo info= loanAccount.getAccountClosureInfo();
//                                        info.setVerifiedFlag(CONSTANTS.YES);
//                                        info.setVerifiedBy(UserRequestContext.getCurrentUser());
//                                        info.setVerificationDate(new Date());
//
//                                        loanAccount.setAccountClosureInfo(info);
//
//                                        accountRepository.save(loanAccount1);
//
//                                        entityResponse.setMessage("OK");
//                                        entityResponse.setStatusCode(HttpStatus.OK.value());
//                                        entityResponse.setEntity(tranHeader);
//                                    }
//                                }else {
//                                    entityResponse.setMessage("Operative account balance is less than total loan balance of: "+payOffAmount.toString());
//                                    entityResponse.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                                }
//                            }else {
//                                entityResponse.setMessage("Failed! You cannot authorize what you initiated");
//                                entityResponse.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                            }
//                        }else {
//                            entityResponse.setMessage("Failed! Pay off was never initiated");
//                            entityResponse.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                        }
//                    }else {
//                        entityResponse.setMessage("Failed! Pay off was never initiated");
//                        entityResponse.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                    }
//
//                }else {
//                    entityResponse.setMessage("Provided account is not a loan account");
//                    entityResponse.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                }
//                return entityResponse;
//            }else {
//                return acidEty;
//            }
//        }catch (Exception e){
//            log.info("Caught Error {}"+e);
//            return null;
//        }
//    }

    public EntityResponse<?> verifyLoanPayOff1(String loanAcid){
        try {
            EntityResponse acidEty= validatorService.acidValidator(loanAcid, "Loan account");
            if(acidEty.getStatusCode().equals(HttpStatus.OK.value())){
                Account loanAccount= accountRepository.findByAccountId(loanAcid).get();
                EntityResponse entityResponse= new EntityResponse<>();
                if(loanAccount.getLoan()!=null){
                    Loan loan = loanAccount.getLoan();
                    if(loan.getPayOffFlag() !=null){
                        if(loan.getPayOffFlag().equals(CONSTANTS.YES)){
                            if(loan.getPayOffVerificationFlag() != null){
                                if(!loan.getPayOffVerificationFlag().equals(CONSTANTS.YES)){
                                    if(!loan.getPayOffInitiatedBy().equals(UserRequestContext.getCurrentUser())) {

                                        //get operative account id
                                        LoanPayOffInfo loanPayOffInfo= loan.getLoanPayOffInfo();
                                        String operativeAccountId= loanPayOffInfo.getOperativeAccount();
                                        String loanAccountAcid= loan.getAccount().getAcid();

                                        //calculate the payoff amount i.e total loan balances
                                        Double loanTotalBalance = loan.getTotalLoanBalance();
                                        Double overFlowAmount =loan.getOverFlowAmount();
                                        Double payOffAmount= loanTotalBalance-overFlowAmount;

                                        // confirm if the operative account balance is equal or greater than payoff
                                        Account operativeAccount= accountRepository.findByAccountId(operativeAccountId).get();
                                        Double operativeAcBalance= operativeAccount.getAccountBalance();

                                        //get operative account type
                                        String accountType= operativeAccount.getAccountType();

                                        if(operativeAcBalance>=payOffAmount || accountType.equals(CONSTANTS.OFFICE_ACCOUNT)){
                                            String loanCurrency= loan.getAccount().getCurrency();
                                            //initialize a pay off transaction
                                            String transactionDescription="LOAN PAY OFF ROR ACCOUNT: "+loanAccountAcid;

                                            TransactionHeader tranHeader= createTransactionHeader(loanCurrency,
                                                    transactionDescription,
                                                    payOffAmount,
                                                    operativeAccountId,
                                                    loanAccountAcid);

                                            EntityResponse transactionRes= transactionsController.systemTransaction1(tranHeader).getBody();
                                            if(!transactionRes.getStatusCode().equals(HttpStatus.OK.value())){
                                                ///failed transactiom
                                                EntityResponse response = new EntityResponse();
                                                response.setMessage("TRANSACTION ERROR! COULD NOT PERFORM PAY OFF TRANSACTION FOR LOAN ACCOUNT: "+loanAccountAcid);
                                                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                                response.setEntity(tranHeader);
                                                return response;
                                            }else {
                                                String transactionCode= (String) transactionRes.getEntity();

                                                //update loan payoff info
//                                        loanPayOffInfo.setPayOffAmount(payOffAmount);
//                                        loanPayOffInfo.setTransactionCode(transactionCode);
//                                        loanPayOffRepo.save(loanPayOffInfo);

                                                loanPayOffRepo.updatePayOffAmount(payOffAmount, loanPayOffInfo.getId());
                                                loanPayOffRepo.updateTransactionCode(transactionCode, loanPayOffInfo.getId());

//                                        Account loanAccount1= accountRepository.findByAccountId(loanAcid).get();
                                                log.info("Calling update");
//                                        Double accountBalance=loanPayOffRepo.fetchAccountBalance(loanAcid);
//                                        loanAccount1.setAccountBalance(accountBalance);
//                                        log.info("acccc bal:"+ loanAccount1.getAccountBalance());
//                                        Loan loan1=loanAccount1.getLoan();

//                                        loan1.setPayOffVerifiedBy(UserRequestContext.getCurrentUser());
                                                loanRepository.updatePayOffVerifiedBy(UserRequestContext.getCurrentUser(), loan.getSn());
//                                        loan1.setPayOffVerificationFlag(CONSTANTS.YES);
                                                loanRepository.updatePayOffVerificationFlag(CONSTANTS.YES, loan.getSn());
//                                        loan1.setPayOffVerificationDate(new Date());
                                                loanRepository.updatePayOffVerificationDate(new Date(), loan.getSn());

                                                //---------tb updated
                                                //force demands
                                                //get the total unsatisfied demands amount
                                                Double loanUnsatisfiedAmt=loanDemandSatisfactionRepo.getSumUnsatisfiedDemands(loanAcid);

//                                                if(loanUnsatisfiedAmt<loanTotalBalance){
                                                Double diff=loanTotalBalance-loanUnsatisfiedAmt;
                                                log.info("loan Bal :: "+loanTotalBalance);
                                                log.info("unsatisfied demands :: "+loanUnsatisfiedAmt);
                                                log.info("diff :: "+diff);

                                                if(diff>0){
                                                    //create a demand difference
                                                    String demandCode =acidGenerator.generateDemandCode(CONSTANTS.INTEREST_DEMAND);
                                                    LoanDemand loanDemand=generateDemandsForOneLoan.createDemandModel(
                                                            demandCode,
                                                            loanAcid,
                                                            diff,
                                                            0.0,
                                                            diff,
                                                            CONSTANTS.INTEREST_AND_PRINCIPAL_DEMAND,
                                                            new Date(),
                                                            "PAYOFF",
                                                            loan,
                                                            null,
                                                            loanTotalBalance
                                                    );
                                                    log.info("Loan demand saved");
                                                    loanDemandRepository.save(loanDemand);

                                                    log.info("Satisfying payoff created demand");
                                                    demandSatisfactionService.satisfyDemandManualForce(loanAcid);
//                                                }
                                                }else {
                                                    log.info("Closing loan account from pay off");
                                                    demandSatisfactionService.closeLoanAccount(accountRepository.findByAcid(loanAccountAcid).get());
                                                }


                                                entityResponse.setMessage("OK");
                                                entityResponse.setStatusCode(HttpStatus.OK.value());
                                                entityResponse.setEntity(tranHeader);
                                            }
                                        }else {
                                            entityResponse.setMessage("Operative account balance is less than total loan balance of: "+payOffAmount.toString());
                                            entityResponse.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                        }
                                    }else {
                                        entityResponse.setMessage("Failed! You cannot authorize what you initiated");
                                        entityResponse.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                    }
                                }else {
                                    entityResponse.setMessage("Failed ! Seems like pay off was verified");
                                    entityResponse.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                }
                            }else {
                                //get operative account id
                                LoanPayOffInfo loanPayOffInfo= loan.getLoanPayOffInfo();
                                String operativeAccountId= loanPayOffInfo.getOperativeAccount();
                                String loanAccountAcid= loan.getAccount().getAcid();

                                //calculate the payoff amount i.e total loan balances
                                Double loanTotalBalance = loan.getTotalLoanBalance();
                                Double overFlowAmount =loan.getOverFlowAmount();
                                Double payOffAmount= loanTotalBalance-overFlowAmount;

                                // confirm if the operative account balance is equal or greater than payoff
                                Account operativeAccount= accountRepository.findByAccountId(operativeAccountId).get();
                                Double operativeAcBalance= operativeAccount.getAccountBalance();

                                //get operative account type
                                String accountType= operativeAccount.getAccountType();

                                if(operativeAcBalance>=payOffAmount || accountType.equals(CONSTANTS.OFFICE_ACCOUNT)){
                                    String loanCurrency= loan.getAccount().getCurrency();
                                    //initialize a pay off transaction
                                    String transactionDescription="LOAN PAY OFF ROR ACCOUNT: "+loanAccountAcid;

                                    TransactionHeader tranHeader= createTransactionHeader(loanCurrency,
                                            transactionDescription,
                                            payOffAmount,
                                            operativeAccountId,
                                            loanAccountAcid);

                                    EntityResponse transactionRes= transactionsController.systemTransaction1(tranHeader).getBody();
                                    if(!transactionRes.getStatusCode().equals(HttpStatus.OK.value())){
                                        ///failed transactiom
                                        EntityResponse response = new EntityResponse();
                                        response.setMessage("TRANSACTION ERROR! COULD NOT PERFORM PAY OFF TRANSACTION FOR LOAN ACCOUNT: "+loanAccountAcid);
                                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                        response.setEntity(tranHeader);
                                        return response;
                                    }else {
                                        String transactionCode= (String) transactionRes.getEntity();

                                        //update loan payoff info
//                                        loanPayOffInfo.setPayOffAmount(payOffAmount);
//                                        loanPayOffInfo.setTransactionCode(transactionCode);
//                                        loanPayOffRepo.save(loanPayOffInfo);

                                        loanPayOffRepo.updatePayOffAmount(payOffAmount, loanPayOffInfo.getId());
                                        loanPayOffRepo.updateTransactionCode(transactionCode, loanPayOffInfo.getId());

//                                        Account loanAccount1= accountRepository.findByAccountId(loanAcid).get();
                                        log.info("Calling update");
//                                        Double accountBalance=loanPayOffRepo.fetchAccountBalance(loanAcid);
//                                        loanAccount1.setAccountBalance(accountBalance);
//                                        log.info("acccc bal:"+ loanAccount1.getAccountBalance());
//                                        Loan loan1=loanAccount1.getLoan();

//                                        loan1.setPayOffVerifiedBy(UserRequestContext.getCurrentUser());
                                        loanRepository.updatePayOffVerifiedBy(UserRequestContext.getCurrentUser(), loan.getSn());
//                                        loan1.setPayOffVerificationFlag(CONSTANTS.YES);
                                        loanRepository.updatePayOffVerificationFlag(CONSTANTS.YES, loan.getSn());
//                                        loan1.setPayOffVerificationDate(new Date());
                                        loanRepository.updatePayOffVerificationDate(new Date(), loan.getSn());

                                        //---------tb updated
                                        //force demands
                                        //get the total unsatisfied demands amount
                                        Double loanUnsatisfiedAmt=loanDemandSatisfactionRepo.getSumUnsatisfiedDemands(loanAcid);

//                                                if(loanUnsatisfiedAmt<loanTotalBalance){
                                        Double diff=loanTotalBalance-loanUnsatisfiedAmt;
                                        log.info("loan Bal :: "+loanTotalBalance);
                                        log.info("unsatisfied demands :: "+loanUnsatisfiedAmt);
                                        log.info("diff :: "+diff);

                                        if(diff>0){
                                            //create a demand difference
                                            String demandCode =acidGenerator.generateDemandCode(CONSTANTS.INTEREST_DEMAND);
                                            LoanDemand loanDemand = generateDemandsForOneLoan.createDemandModel(
                                                    demandCode,
                                                    loanAcid,
                                                    diff,
                                                    0.0,
                                                    diff,
                                                    CONSTANTS.INTEREST_AND_PRINCIPAL_DEMAND,
                                                    new Date(),
                                                    "PAYOFF",
                                                    loan,
                                                    null,
                                                    loanTotalBalance
                                            );
                                            log.info("Loan demand saved");
                                            loanDemandRepository.save(loanDemand);

                                            log.info("Satisfying payoff created demand");
                                            demandSatisfactionService.satisfyDemandManualForce(loanAcid);
//                                                }
                                        }else {
                                            log.info("Closing loan account from pay off");
                                            demandSatisfactionService.closeLoanAccount(accountRepository.findByAcid(loanAccountAcid).get());
                                        }


                                        entityResponse.setMessage("OK");
                                        entityResponse.setStatusCode(HttpStatus.OK.value());
                                        entityResponse.setEntity(tranHeader);
                                    }
                                }else {
                                    entityResponse.setMessage("Operative account balance is less than total loan balance of: "+payOffAmount.toString());
                                    entityResponse.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                }
                            }

                        }else {
                            entityResponse.setMessage("Failed! Pay off was never initiated");
                            entityResponse.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        }
                    }else {
                        entityResponse.setMessage("Failed! Pay off was never initiated");
                        entityResponse.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    }

                }else {
                    entityResponse.setMessage("Provided account is not a loan account");
                    entityResponse.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                }
                return entityResponse;
            }else {
                return acidEty;
            }
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }


    public TransactionHeader createTransactionHeader(String loanCurrency,
                                                     String transDesc,
                                                     Double totalAmount,
                                                     String drAc,
                                                     String crAc){
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
}