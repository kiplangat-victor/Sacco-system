package com.emtechhouse.accounts.Models.Accounts.Loans;

import com.emtechhouse.accounts.Models.Accounts.Account;
import com.emtechhouse.accounts.Models.Accounts.AccountRepository;
import com.emtechhouse.accounts.Models.Accounts.AccountsLookupInterfaces.AccountLookUpInterface;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanAccrual.LoanAccrualRepo;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanBooking.LoanBookingRepo;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.DemandSatisfaction.LoanDemandSatisfactionRepo;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.LoanDemand;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.LoanDemandRepository;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanSchedule.LoanCustomerPaymentSchedule.LoanCustomerPaymentSchedule;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanSchedule.LoanSchedule;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ProductInterestDetails.ProductInterestDetails;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ProductInterestDetails.ProductInterestService;
import com.emtechhouse.accounts.Models.Accounts.Validators.ValidatorService;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.NewTransactionService;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TranHeaderRepository;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.emtechhouse.accounts.Utils.DatesCalculator;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class LoanService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TranHeaderRepository tranHeaderRepository;

    @Autowired
    private DatesCalculator datesCalculator;
    
    @Autowired
    private NewTransactionService newTransactionService;

    @Autowired
    private ValidatorService validatorsService;

    @Autowired
    private LoanCalculatorService loanCalculatorService;

    @Autowired
    private LoanDemandRepository loanDemandRepository;

    @Autowired
    private LoanDemandSatisfactionRepo loanDemandSatisfactionRepo;
    @Autowired
    private LoanBookingRepo loanBookingRepo;
    @Autowired
    private LoanAccrualRepo loanAccrualRepo;
    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private ProductInterestService productInterestService;

    public EntityResponse<List<LoanSchedule>> getReducingBalanceLoanSchedule(
            Integer numberOfInstallments,
            String freqId,
            Integer freqPeriod,
            Double principalAmount,
            Double interestRate, LocalDate startDate){
        try {
            EntityResponse userValidator= validatorsService.userValidator();
            if(userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                return userValidator;
            }else {
                List<LoanSchedule> loanSchedules=loanCalculatorService.reducingBalanceloanScheduleCalculator(numberOfInstallments,  freqId,  freqPeriod,  principalAmount,  interestRate, startDate);
                EntityResponse listChecker = validatorsService.listLengthChecker(loanSchedules);
                return listChecker;
            }
        }catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse<List<LoanSchedule>> fixedRateBalanceLoanSchedule(Integer numberOfInstallments, String freqId, Integer freqPeriod, Double principalAmount, Double interestRate, LocalDate startDate){
        try {
            EntityResponse userValidator= validatorsService.userValidator();
            if(userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                return userValidator;
            }else {
                List<LoanSchedule> loanSchedules=loanCalculatorService.fixedRateLoanScheduleCalculator(numberOfInstallments,  freqId,  freqPeriod,  principalAmount,  interestRate, startDate);
                EntityResponse listChecker = validatorsService.listLengthChecker(loanSchedules);
                return listChecker;
            }
        }catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse<LoanCustomerPaymentSchedule> getCustomerPaymentSchedule(Double beginningBalance, Double principalToBeMade, Double interestRate){
        try {
            EntityResponse userValidator= validatorsService.userValidator();
            if(userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                return userValidator;
            }else {
                LoanCustomerPaymentSchedule loanCustomerPaymentSchedule =loanCalculatorService.createCustomerPaymentSchedule(beginningBalance,principalToBeMade,interestRate);
                EntityResponse entityResponse= new EntityResponse();
                entityResponse.setMessage("f");
                entityResponse.setEntity(loanCustomerPaymentSchedule);
                entityResponse.setStatusCode(900);
                return entityResponse;
            }
        }catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse<Integer> getAllDisbursedLoans() {
        try{
            EntityResponse userValidator= validatorsService.userValidator();
            if(userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                return userValidator;
            }else {
                Integer count= loanRepository.countByLoanStatus(CONSTANTS.DISBURSED);
                EntityResponse entityResponse =new EntityResponse<>();
                entityResponse.setMessage(HttpStatus.OK.getReasonPhrase());
                entityResponse.setEntity(count);
                entityResponse.setStatusCode(HttpStatus.OK.value());
                return entityResponse;

            }
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse<List<LoanDemandRepository.DemandsInfo>> getDemandsGeneratedOnACertainDate(LocalDate date){
        try {
            EntityResponse userValidator= validatorsService.userValidator();
            if(userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                return userValidator;
            }else {
                List<LoanDemandRepository.DemandsInfo> demandsInfo=loanDemandRepository.getDemandGeneratedByDate(date);
                EntityResponse listChecker = validatorsService.listLengthChecker(demandsInfo);
                return listChecker;
            }
        }catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse<List<LoanDemandSatisfactionRepo.DemandSatisfactionInfo>> getDemandsSatisfiedOnACertainDate(LocalDate date){
        try {
            EntityResponse userValidator= validatorsService.userValidator();
            if(userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                return userValidator;
            }else {
                List<LoanDemandSatisfactionRepo.DemandSatisfactionInfo> demandsInfo=loanDemandSatisfactionRepo.getDemandSatisfiedByDate(date);
                EntityResponse listChecker = validatorsService.listLengthChecker(demandsInfo);
                return listChecker;
            }
        }catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse<Integer> countDemandGeneratedOnACertainDate(LocalDate createdOn){
        try{
            EntityResponse userValidator= validatorsService.userValidator();
            if(userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                return userValidator;
            }else {
                Integer count= loanDemandRepository.countDemandGeneratedOnACertainDate(createdOn);
                EntityResponse entityResponse =new EntityResponse<>();
                entityResponse.setMessage(HttpStatus.OK.getReasonPhrase());
                entityResponse.setEntity(count);
                entityResponse.setStatusCode(HttpStatus.OK.value());
                return entityResponse;

            }
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse<List<LoanDemandRepository.AccountsIdsInfo>> getAccountsWhoseDemandsWereNotGeneratedByDate(LocalDate dDate){
        try{
            EntityResponse userValidator= validatorsService.userValidator();
            if(userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                return userValidator;
            }else {
                List<LoanDemandRepository.AccountsIdsInfo> accountsNotDemanded= loanDemandRepository.getAccountsWhoseDemandsWereNotGeneratedByDate(dDate);
                EntityResponse listChecker = validatorsService.listLengthChecker(accountsNotDemanded);
                return listChecker;
            }
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse<List<LoanDemandRepository.AccountsIds>> getAccountsWhoseDemandsWereGeneratedButNotFoundBySatisfactionFunction(){
        try{
            EntityResponse userValidator= validatorsService.userValidator();
            if(userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                return userValidator;
            }else {
                List<LoanDemandRepository.AccountsIds> demandsGeneratedButNotSatisfied= loanDemandRepository.getAccountsWhoseDemandsWereGeneratedButNotFoundBySatisfactionFunction();
                EntityResponse listChecker = validatorsService.listLengthChecker(demandsGeneratedButNotSatisfied);
                return listChecker;
            }
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse pauseLoanAccountDemands(String acid) {
        try {
            Optional<Account> account= accountRepository.findByAcid(acid);
            EntityResponse et= new EntityResponse<>();
            if(account.isPresent()){
                Account account1= account.get();
                if(account1.getLoan() != null){
                    Loan loan = account1.getLoan();
                    if(loan.getPausedDemandsFlag().equals(CONSTANTS.NO)){
                        loan.setPausedDemandsFlag(CONSTANTS.YES);
                        loanRepository.save(loan);
                        et.setMessage("Demands for loan account "+acid+ " paused successfully");
                        et.setStatusCode(HttpStatus.OK.value());
                    }else {
                        et.setMessage("Demands are already paused");
                        et.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    }
                }else {
                    et.setMessage("Loan account not found");
                    et.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                }
            }else {
                et.setMessage("Account not found");
                et.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            }
            return et;
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse massiveDemandReversals(String acid, Date startDate, Date endDate, Boolean checkIfSystemUser) {
        try {
            System.out.println("acid: " + acid + ", startDate: " + startDate + ", endDate: " + endDate);
            Optional<Account> account = accountRepository.findByAcid(acid);
            EntityResponse et = new EntityResponse<>();

            if (account.isPresent()) {
                Account account1 = account.get();
                if (account1.getLoan() != null) {
                    String startDateString = datesCalculator.dateFormat(startDate);
                    String endDateString = datesCalculator.dateFormat(endDate);

                    List<String> transactionCodes = tranHeaderRepository.transactionsForMassiveReversal(acid, startDateString, endDateString);
                    int count = 0;

                    for (String transactionCode : transactionCodes) {
                        System.out.println(transactionCode);
                        EntityResponse transactionRes = newTransactionService.systemReverseTransaction(transactionCode, checkIfSystemUser);
                        System.out.println(transactionRes);

                        if (transactionRes.getStatusCode().equals(HttpStatus.OK.value())) {
                            loanDemandRepository.deleteByTransactionCode(transactionCode);
                            count++;
                        } else {
                            // Log the error message or take other actions if needed
                            log.error("Failed to reverse transaction {}: {}", transactionCode, transactionRes.getMessage());
                            // Continue to the next iteration after an unsuccessful reversal
                            continue;
                        }
                    }

                    et.setStatusCode(HttpStatus.OK.value());
                    et.setMessage("Executed " + count);
                    return et;
                } else {
                    et.setMessage("Loan account not found");
                    et.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                }
            } else {
                et.setMessage("Account not found");
                et.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            }

            return et;
        } catch (Exception e) {
            log.error("Error in massiveDemandReversals: {}", e.getMessage(), e);
            // Return an EntityResponse indicating the error
            return new EntityResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error");
        }
    }



    public EntityResponse unpauseLoanAccountDemands(String acid) {
        try {
            Optional<Account> account= accountRepository.findByAcid(acid);
            EntityResponse et= new EntityResponse<>();
            if (account.isPresent()) {
                Account account1= account.get();
                if (account1.getLoan() != null){
                    Loan loan = account1.getLoan();
                    if (loan.getPausedDemandsFlag().equals(CONSTANTS.YES)) {
                        loan.setPausedDemandsFlag(CONSTANTS.NO);
                        loanRepository.save(loan);
                        et.setMessage("Demands for loan account "+acid+ " unpaused successfully");
                        et.setStatusCode(HttpStatus.OK.value());
                    } else {
                        et.setMessage("Demands are already paused");
                        et.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    }
                }else {
                    et.setMessage("Loan account not found");
                    et.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                }
            }else {
                et.setMessage("Account not found");
                et.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            }
            return et;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }


    public EntityResponse pauseLoandemandsatisfaction(String acid) {
        try {
            Optional<Account> account= accountRepository.findByAcid(acid);
            EntityResponse et= new EntityResponse<>();
            if(account.isPresent()){
                Account account1= account.get();
                if(account1.getLoan() != null){
                    Loan loan = account1.getLoan();
                    if(loan.getPausedDemandsFlag().equals(CONSTANTS.NO)){
                        loan.setPausedSatisfactionFlag(CONSTANTS.YES);
                        loanRepository.save(loan);
                        et.setMessage("Satisfaction for loan account "+acid+ " paused successfully");
                        et.setStatusCode(HttpStatus.OK.value());
                    }else {
                        et.setMessage("Demands are already paused");
                        et.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    }
                }else {
                    et.setMessage("Loan account not found");
                    et.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                }
            }else {
                et.setMessage("Account not found");
                et.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            }
            return et;
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse unpauseLoandemandsatisfaction(String acid) {
        try {
            Optional<Account> account= accountRepository.findByAcid(acid);
            EntityResponse et= new EntityResponse<>();
            if(account.isPresent()){
                Account account1= account.get();
                if(account1.getLoan() != null){
                    Loan loan = account1.getLoan();
                    if(loan.getPausedDemandsFlag().equals(CONSTANTS.NO)) {
                        loan.setPausedSatisfactionFlag(CONSTANTS.NO);
                        loanRepository.save(loan);
                        et.setMessage("Demands for loan account "+acid+ " unpaused successfully");
                        et.setStatusCode(HttpStatus.OK.value());
                    }else {
                        et.setMessage("Demands are already paused");
                        et.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    }
                }else {
                    et.setMessage("Loan account not found");
                    et.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                }
            }else {
                et.setMessage("Account not found");
                et.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            }
            return et;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse resumeDemandGeneration(String acid){
        try {
            Optional<Account> account= accountRepository.findByAcid(acid);
            EntityResponse et= new EntityResponse<>();
            if(account.isPresent()){
                Account account1= account.get();
                if(account1.getLoan() != null){
                    Loan loan = account1.getLoan();
                    if(loan.getPausedDemandsFlag().equals(CONSTANTS.YES)){
                        loan.setPausedDemandsFlag(CONSTANTS.NO);
                        loanRepository.save(loan);
                        et.setMessage("Demands for loan account "+acid+ "paused successfully");
                        et.setStatusCode(HttpStatus.OK.value());
                    }else {
                        et.setMessage("Demands are not paused");
                        et.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    }
                }else {
                    et.setMessage("Loan account not found");
                    et.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                }
            }else {
                et.setMessage("Account not found");
                et.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            }
            return et;
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }


    // TODO: 3/22/2023 get current loan interest from product
    public Double getLoanInterest(String productCode, Double principalAmount, Double interestPreferential){
        try {
            Double interest=null;
            EntityResponse res= productInterestService.getLoanInterest(productCode,principalAmount);
            System.out.println(res);
            if(res.getStatusCode() == HttpStatus.OK.value()){
                ProductInterestDetails p= (ProductInterestDetails) res.getEntity();
                System.out.println(p);
                if(p.getInterestPeriod().equals("p.m")) {
                    interest=(p.getInterestRate()*12)+interestPreferential;
                } else if (p.getInterestPeriod().equals("p.a")) {
                    interest=(p.getInterestRate())+interestPreferential;
                }
            }

            log.info("Interest rate is :::: "+interest);
            return interest;
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public  EntityResponse<List<AccountLookUpInterface>> getAllDisbursementApprovalList() {
        EntityResponse res = new EntityResponse();
        List<Account> accountList = new ArrayList<>();
        res.setStatusCode(HttpStatus.NOT_FOUND.value());
        res.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
        try {
            List<AccountLookUpInterface> accounts = accountRepository.getAllDisbursementApprovalList();
            res = validatorsService.listLengthChecker(accounts);

            return res;
        } catch (Exception e) {
            log.info("Caught Error {}" + e);
            return null;
        }
    }
}

