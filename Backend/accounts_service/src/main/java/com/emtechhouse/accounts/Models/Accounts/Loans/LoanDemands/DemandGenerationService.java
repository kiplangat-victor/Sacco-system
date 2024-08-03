package com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands;

import com.emtechhouse.accounts.Models.Accounts.Account;
import com.emtechhouse.accounts.Models.Accounts.AccountRepository;
import com.emtechhouse.accounts.Models.Accounts.AccountServices.AccountTransactionService;
import com.emtechhouse.accounts.Models.Accounts.LienComponent.LienRepository;
import com.emtechhouse.accounts.Models.Accounts.Loans.Loan;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanAccrual.LoanAccrualService;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanBooking.LoanBookingService;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanFees.LoanFees;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanFees.LoanFeesRepo;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanRepository;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanSchedule.LoanSchedule;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanSchedule.LoanScheduleRepo;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.GeneralProductDetails;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ProductFees.ProductFeesService;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ProductInterestDetails.ProductInterestDetails;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ProductInterestDetails.ProductInterestService;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ItemServiceCaller;
import com.emtechhouse.accounts.Models.Accounts.Validators.ValidatorService;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.PartTrans.PartTran;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TransactionHeader;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TransactionsController;
import com.emtechhouse.accounts.Utils.AcidGenerator;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.emtechhouse.accounts.Utils.DatesCalculator;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class DemandGenerationService {
    @Autowired
    private LienRepository lienRepository;
    @Autowired
    private DatesCalculator datesCalculator;
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private LoanDemandRepository loanDemandRepository;
    @Autowired
    private ItemServiceCaller itemServiceCaller;

    @Autowired
    private ValidatorService validatorsService;

    @Autowired
    private GenerateDemandsForOneLoan generateDemandsForOneLoan;
    @Autowired
    private AccountTransactionService accountTransactionService;
    @Autowired
    private LoanFeesRepo loanFeesRepo;
    @Autowired
    private ProductInterestService productInterestService;
    @Autowired
    private AcidGenerator acidGenerator;
    @Autowired
    private LoanAccrualService loanAccrualService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private LoanBookingService loanBookingService;
    @Autowired
    private LoanScheduleRepo loanScheduleRepo;

    @Autowired
    private TransactionsController transactionsController;

    @Autowired
    private ProductFeesService productFeesService;




    public EntityResponse<List<LoanDemand>> getDemandInfoPerAcid(String acid) {
        try {
            EntityResponse userValidator= validatorsService.userValidator();
            if(userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                return userValidator;
            } else {
                List<LoanDemand> retailAccounts= loanDemandRepository.findByAcidAndDemandCarriedFowardFlag(acid, CONSTANTS.NO);
                EntityResponse listChecker = validatorsService.listLengthChecker(retailAccounts);
                return listChecker;
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e.getStackTrace()[0].getLineNumber()+" - "+e);
            return null;
        }
    }



    public List<EntityResponse> generateDemandsForAllLoans() {
        try {
            log.info("Demand generation in progress");
            LocalDate today = LocalDate.now();
            Date now = datesCalculator.convertLocalDateToDate(today);
            List<EntityResponse> responses = new ArrayList<>();

            List<Loan> loansToDemandMonths = loanRepository.getLoansToDemand(now, CONSTANTS.DISBURSED);
            System.out.println("=======================Count (MONTHS) = " + loansToDemandMonths.size() + " ==============================================");
            List<Loan> loansToDemandMonthsOd = loanRepository.getOdLoansToDemand(now, CONSTANTS.DISBURSED);
            System.out.println("=======================Count (OD MONTHS) = " + loansToDemandMonthsOd.size() + " ==============================================");



            List<Loan> loansToDemandYears = loanRepository.getYearLoansToDemand(now, CONSTANTS.DISBURSED);
            System.out.println("=======================Count (YEARS) = " + loansToDemandYears.size() + " ==============================================");

            List<Loan> combinedLoansToDemand = new ArrayList<>();
            combinedLoansToDemand.addAll(loansToDemandMonths);
//            System.out.println("Here are monthly loans: "+loansToDemandMonths);
            combinedLoansToDemand.addAll(loansToDemandYears);
//            System.out.println("Here are Yearly Loans: "+loansToDemandYears);
            combinedLoansToDemand.addAll(loansToDemandMonthsOd);

            if (!combinedLoansToDemand.isEmpty()) {
                for (Loan loanToDemand : combinedLoansToDemand) {
                    String acid = loanToDemand.getAccount().getAcid();
                    log.info("Account ACID: " + acid);

                    String loanFrequency = loanRepository.getLoanFrequency(acid);
                    System.out.println("The account Loan Frequency is: " + loanFrequency);

                    String productCode = loanRepository.findAccountByProduct(acid);
                    System.out.println("The account Product Code is: " + productCode);

                    if ("MONTHS".equals(loanFrequency)) {
                        if ("OD".equals(productCode)){
                            List<EntityResponse> entityResponse = generateDemandsForOneLoan.demandManualForce1(acid, 0);
                            responses.addAll(entityResponse);
                            log.info(entityResponse.toString());
                        } else {
                            List<EntityResponse> entityResponse = generateDemandsForOneLoan.demandManualForce(acid, 0);
                            responses.addAll(entityResponse);
                            log.info(entityResponse.toString());
                        }
                    } else if ("YEARS".equals(loanFrequency)) {
                        List<EntityResponse> entityResponse = generateDemandsForOneLoan.demandManualForce1(acid, 0);
                        responses.addAll(entityResponse);
                        log.info(entityResponse.toString());
                    }
                }
            }
            else {
                EntityResponse response = new EntityResponse();
                response.setMessage("THERE ARE NO LOANS TO DEMAND");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                responses.add(response);
            }
            return responses;
        } catch (Exception e) {
            log.info("Caught Error at line number: " + e.getStackTrace()[0].getLineNumber() + " - " + e);
            return null;
        }
    }





    public List<EntityResponse> demandManualForce(String acid, int daysAhead) {
        return generateDemandsForOneLoan.demandManualForce(acid, daysAhead);
    }


}