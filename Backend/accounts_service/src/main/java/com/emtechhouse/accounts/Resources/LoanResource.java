package com.emtechhouse.accounts.Resources;

import com.emtechhouse.accounts.Models.Accounts.Account;
import com.emtechhouse.accounts.Models.Accounts.AccountRepository;
import com.emtechhouse.accounts.Models.Accounts.AccountsLookupInterfaces.AccountLookUpInterface;
import com.emtechhouse.accounts.Models.Accounts.Loans.Loan;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanAccrual.LoanAccrual;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanAccrual.LoanAccrualRepo;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanAccrual.LoanAccrualService;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanBooking.LoanBooking;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanBooking.LoanBookingRepo;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanBooking.LoanBookingService;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanCalculatorService;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.DemandGenerationService;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.DemandSatisfaction.AssetClassificationService;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.DemandSatisfaction.DemandSatisfactionService;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.DemandSatisfaction.LoanDemandSatisfactionRepo;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.GenerateDemandsForOneLoan;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.LoanDemand;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.LoanDemandRepository;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDisbursment.LoanDisbursmentInfo;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDisbursment.LoanDisbursmentService;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanFees.LoanFeesService;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanGuarantor.LoanGuarantorService;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanRepository;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanRestructuring.LoanRestructuringService;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanSchedule.LoanCustomerPaymentSchedule.LoanCustomerPaymentSchedule;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanSchedule.LoanSchedule;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanService;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ProductFees.ProductFeesService;
import com.emtechhouse.accounts.Requests.LoanPrepayRequest;
import com.emtechhouse.accounts.Utils.DatesCalculator;
import com.emtechhouse.accounts.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.accounts.Utils.HttpInterceptor.UserRequestContext;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

//@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("loans")
@Slf4j
public class LoanResource {
    @Autowired
    private LoanCalculatorService loanCalculatorService;
    @Autowired
    private LoanDisbursmentService loanDisbursmentService;
    @Autowired
    private LoanAccrualService loanAccrualService;
    @Autowired
    private DatesCalculator datesCalculator;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private LoanBookingService loanBookingService;
    @Autowired
    private ProductFeesService productFeesService;
    @Autowired
    private LoanService loanService;
    @Autowired
    private AssetClassificationService assetClassificationService;
    @Autowired
    private DemandSatisfactionService demandSatisfactionService;
    @Autowired
    private LoanRestructuringService loanRestructuringService;
    @Autowired
    private DemandGenerationService demandGenerationService;
    @Autowired
    private GenerateDemandsForOneLoan generateDemandsForOneLoan;
    @Autowired
    private LoanGuarantorService loanGuarantorService;

    @Autowired
    private LoanFeesService loanFeesService;
    @Autowired
    private LoanRepository loanRepository;

    @PostMapping("customer/schedule")
    public ResponseEntity<EntityResponse<LoanCustomerPaymentSchedule>> getCustomerPaymentSchedule(@RequestParam Double beginningBalance, @RequestParam Double principalToBeMade, @RequestParam Double interestRate) {
        try {
            return ResponseEntity.ok().body(loanService.getCustomerPaymentSchedule(beginningBalance, principalToBeMade, interestRate));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PostMapping("reducing/balance/loan/schedules")
    public ResponseEntity<EntityResponse<List<LoanSchedule>>> reducingBalanceLoanSchedules(@RequestParam Integer numberOfInstallments, @RequestParam String freqId, @RequestParam Integer freqPeriod, @RequestParam Double principalAmount, @RequestParam Double interestRate, @RequestParam String startDate) {
        try {
            return ResponseEntity.ok().body(loanService.getReducingBalanceLoanSchedule(numberOfInstallments, freqId, freqPeriod, principalAmount, interestRate, (LocalDate.parse(startDate))));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PostMapping("fixed/rate/loan/schedules")
    public ResponseEntity<EntityResponse<List<LoanSchedule>>> fixedRateLoanSchedules(@RequestParam Integer numberOfInstallments, @RequestParam String freqId, @RequestParam Integer freqPeriod, @RequestParam Double principalAmount, @RequestParam Double interestRate, @RequestParam String startDate) {
        try {
            return ResponseEntity.ok().body(loanService.fixedRateBalanceLoanSchedule(numberOfInstallments, freqId, freqPeriod, principalAmount, interestRate, (LocalDate.parse(startDate))));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("disbursed/loans")
    public ResponseEntity<EntityResponse<Integer>> getAllDisbursedLoans() {
        try {
            return ResponseEntity.ok().body(loanService.getAllDisbursedLoans());
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("disbursement/approvallist")
    public ResponseEntity<EntityResponse<List<AccountLookUpInterface>>> getDisbursementApprovalList() {
        try {
            return ResponseEntity.ok().body(loanService.getAllDisbursementApprovalList());
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }


    //---------------DEMANDS--------------------------------------------
    @GetMapping("demands/info/per/acid/{acid}")
    public ResponseEntity<EntityResponse<List<LoanDemand>>> fetchDemandInfoPerAcid(@PathVariable String acid) {
        try {
            return ResponseEntity.ok().body(demandGenerationService.getDemandInfoPerAcid(acid));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("demands/generated/on/date/{date}")
    public ResponseEntity<EntityResponse<List<LoanDemandRepository.DemandsInfo>>> getDemandsGeneratedOnACertainDate(@PathVariable String date) {
        try {
            return ResponseEntity.ok().body(loanService.getDemandsGeneratedOnACertainDate(LocalDate.parse(date)));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("demands/satisfied/on/date/{date}")
    public ResponseEntity<EntityResponse<List<LoanDemandSatisfactionRepo.DemandSatisfactionInfo>>> getDemandsSatisfiedOnACertainDate(@PathVariable String date) {
        try {
            return ResponseEntity.ok().body(loanService.getDemandsSatisfiedOnACertainDate(LocalDate.parse(date)));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }


    @PutMapping("demand/force")
    public ResponseEntity<List<EntityResponse>> demandForce(@RequestParam String acid, @RequestParam Integer daysAhead) {
        try {
            ResponseEntity responseEntity = ResponseEntity.ok().body(generateDemandsForOneLoan.demandManualForce3(acid, daysAhead));

//            demandSatisfactionService.satisfyDemandManualForce(acid);

            return responseEntity;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PutMapping("fee/demand/force")
    public ResponseEntity<List<EntityResponse>> feeDemandForce(@RequestParam String acid) {
        try {
            return ResponseEntity.ok().body(generateDemandsForOneLoan.feeDemandManualForce(acid));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PutMapping("demand/all")
    public ResponseEntity<List<EntityResponse>> demandAll() {
        try {
            return ResponseEntity.ok().body(demandGenerationService.generateDemandsForAllLoans());
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }


    @PutMapping("satisfy/demand/force")
    public ResponseEntity<List<EntityResponse>> satisfyDemandForce(@RequestParam String acid) {
        try {
            return ResponseEntity.ok().body(demandSatisfactionService.satisfyDemandManualForce(acid));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PutMapping("prepay")
    public ResponseEntity<EntityResponse> prepayLoan(@RequestBody LoanPrepayRequest loanPrepayRequest) {
        try {
            return ResponseEntity.ok().body(demandSatisfactionService.prepayLoan(loanPrepayRequest));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PutMapping("prepay/all")
    public ResponseEntity<EntityResponse> prepayAllLoan(@RequestBody LoanPrepayRequest loanPrepayRequest) {
        try {
            return ResponseEntity.ok().body(demandSatisfactionService.prepayAllLoan(loanPrepayRequest));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }


    @GetMapping("get/total/interest/paid")
    public ResponseEntity<?> getTotalInterestPaid(@RequestParam String acid) {
        try {
            return ResponseEntity.ok().body(demandSatisfactionService.getTotalInterestPaid(acid));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("get/total/principal/paid")
    public ResponseEntity<?> getTotalPrincipalPaid(@RequestParam String acid) {
        try {
            return ResponseEntity.ok().body(demandSatisfactionService.getTotalPrincipalPaid(acid));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PutMapping("satisfy/demand/all")
    public ResponseEntity<List<EntityResponse>> satisfyDemandAll() {
        try {
            return ResponseEntity.ok().body(demandSatisfactionService.satisfyAllDemands());
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("demands/count/demanded/loans/on/date/{date}")
    public ResponseEntity<EntityResponse<Integer>> countDemandGeneratedOnACertainDate(@PathVariable String date) {
        try {
            return ResponseEntity.ok().body(loanService.countDemandGeneratedOnACertainDate(LocalDate.parse(date)));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("demands/accounts/not/demanded/on/{date}")
    public ResponseEntity<EntityResponse<List<LoanDemandRepository.AccountsIdsInfo>>> getAccountsWhoseDemandsWereNotGeneratedByDate(@PathVariable String date) {
        try {
            return ResponseEntity.ok().body(loanService.getAccountsWhoseDemandsWereNotGeneratedByDate(LocalDate.parse(date)));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("demands/get/accounts/of/demands/generated/but/not/satisfied/on")
    public ResponseEntity<EntityResponse<List<LoanDemandRepository.AccountsIds>>> getAccountsWhoseDemandsWereGeneratedButNotFoundBySatisfactionFunction() {
        try {
            return ResponseEntity.ok().body(loanService.getAccountsWhoseDemandsWereGeneratedButNotFoundBySatisfactionFunction());
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    //------------------------booking apis-----------------------------------------------
    @GetMapping("booking/info/per/acid/{acid}")
    public ResponseEntity<EntityResponse<List<LoanBooking>>> fetchBookingInfoPerAcid(@PathVariable String acid) {
        try {
            return ResponseEntity.ok().body(loanBookingService.getBookingInfoPerAcid(acid));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PutMapping("book/force")
    public EntityResponse bookForce(@RequestParam String acid) {
        try {
            return loanBookingService.manualBooking2(acid);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("booking/interest/booked/on/date/{date}")
    public ResponseEntity<EntityResponse<List<LoanBooking>>> getInterestBookedOnACertainDate(@PathVariable String date) {
        try {
            return ResponseEntity.ok().body(loanBookingService.getInterestBookedOnACertainDate(LocalDate.parse(date)));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("booking/count/booked/loans/on/date/{date}")
    public ResponseEntity<EntityResponse<Integer>> getCountBookedLoansOnACertainDate(@PathVariable String date) {
        try {
            return ResponseEntity.ok().body(loanBookingService.getCountBookedLoansOnACertainDate(LocalDate.parse(date)));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("booking/accounts/not/booked/loans/on/date/{date}")
    public ResponseEntity<EntityResponse<List<LoanBookingRepo.AccountsIds>>> getAllAcidsNotBookedOnACertainDate(@PathVariable String date) {
        try {
            return ResponseEntity.ok().body(loanBookingService.getAllAcidsNotBookedOnACertainDate(LocalDate.parse(date)));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
//--------------------------ACCRUAL------------------------------------------------------------------------------------------//

    @GetMapping("accrual/info/per/acid/{acid}")
    public ResponseEntity<EntityResponse<List<LoanAccrual>>> fetchLoanAccrual(@PathVariable String acid) {
        try {
            return ResponseEntity.ok().body(loanAccrualService.getAccrualInfoPerAcid(acid));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PutMapping("accrual/force")
    public ResponseEntity<EntityResponse> accrualForce(@RequestParam String acid) {
        try {
            return ResponseEntity.ok().body(loanAccrualService.manualAccrual2(acid));

        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PutMapping("accrual/all")
    public ResponseEntity<List<EntityResponse>> accrualAll() {
        try {
            return ResponseEntity.ok().body(loanAccrualService.accrualForAllLoans());

        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("accrual/interest/accrued/on/date/{date}")
    public ResponseEntity<EntityResponse<List<LoanAccrual>>> getInterestAccruedOnACertainDate(@PathVariable String date) {
        try {
            return ResponseEntity.ok().body(loanAccrualService.getAccruedOnACertainDate(LocalDate.parse(date)));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }


    @GetMapping("accrual/count/accrued/loans/on/date/{date}")
    public ResponseEntity<EntityResponse<Integer>> getCountAccruedLoansOnACertainDate(@PathVariable String date) {
        try {
            return ResponseEntity.ok().body(loanAccrualService.getCountAccruedLoansOnACertainDate(LocalDate.parse(date)));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("accrual/accounts/not/accrued/loans/on/date/{date}")
    public ResponseEntity<EntityResponse<List<LoanAccrualRepo.AccountsIds>>> getAllAcidsNotAccruedOnACertainDate(@PathVariable String date) {
        try {
            return ResponseEntity.ok().body(loanAccrualService.getAllAcidsNotAccruedOnACertainDate(LocalDate.parse(date)));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }


    @PutMapping("disburse")
    public ResponseEntity<EntityResponse> disburseLoan(@RequestParam String acid) {
        try {
            return ResponseEntity.ok().body(loanDisbursmentService.disburseLoan(acid));

        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PutMapping("verify/loan/disbursment")
    public ResponseEntity<List<EntityResponse>> verifyLoanDisbursement(@RequestParam String acid) {
        try {
                return ResponseEntity.ok().body(loanDisbursmentService.verifyLoanDisbursment(acid));

        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }


    @PutMapping("verify/mobile/loan/disbursment")
    public ResponseEntity<List<EntityResponse>> verifyMobileLoanDisbursements(@RequestParam String acid) {
        try {
            System.out.println("Arrived to verify mobile loan");
            String isAccountPostedByMM = accountRepository.findIfAccountIsPostedByMM(acid, EntityRequestContext.getCurrentEntityId());
            if(isAccountPostedByMM.equalsIgnoreCase("EM-MDWARE")){
                return ResponseEntity.ok().body(loanDisbursmentService.verifyMobileLoanDisbursment(acid));
            }else {
                log.info("Not a mobile loan");
                EntityResponse response = new EntityResponse<>();
                response.setEntity("");
                response.setMessage("Not a mobile loan");
                response.setStatusCode(HttpStatus.BAD_GATEWAY.value());
            }

        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
        return null;
    }


    @PutMapping("reject/loan/disbursement")
    public EntityResponse rejectLoanDisbursment(@RequestParam String acid) {
        try {
            return ResponseEntity.ok().body(loanDisbursmentService.rejectLoanDisbursement(acid)).getBody();

        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }





    @GetMapping("get/loan/disbursment/info/per/acid")
    public ResponseEntity<EntityResponse<List<LoanDisbursmentInfo>>> getLoanDisbursmentInfoPerAcid(@RequestParam String acid) {
        try {
            return ResponseEntity.ok().body(loanDisbursmentService.getLoanDisbursementReportByAcid(acid));

        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("prodItem/fees")
    public ResponseEntity<?> productFees(@RequestParam String productCode, @RequestParam Double amount) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(productFeesService.getProductFees(productCode, amount));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PutMapping("restructure/loan")
    public ResponseEntity<?> restructureLoan(@RequestParam String acid, @RequestParam Integer installmentsNumber, @RequestParam String installmentFreq, @RequestParam Integer freqPeriod, @RequestParam String installmentStartDate) {
        try {
            Date installmentStartDate1 = datesCalculator.convertLocalDateToDate(LocalDate.parse(installmentStartDate));
            return ResponseEntity.status(HttpStatus.CREATED).body(loanRestructuringService.restructureLoan(acid, installmentsNumber, installmentFreq, freqPeriod, installmentStartDate1));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("guarantor/available/amt")
    public ResponseEntity<?> getGuarantorAvailableAMount(@RequestParam String customerCode, @RequestParam Double amount) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(loanGuarantorService.getGuarantorAvailableAmount(customerCode, amount));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @DeleteMapping("remove/loan-fee")
    public ResponseEntity<?> removeLoanFee(@RequestParam Long feeId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(loanFeesService.removeLoanFee(feeId));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PutMapping("date/lsat/day")
    public ResponseEntity<Long> lastDay(@RequestParam String startDate, @RequestParam String endDate) {
        try {
            return ResponseEntity.ok().body(datesCalculator.getMonthsDifference(LocalDate.parse(startDate), LocalDate.parse(endDate)));

        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PutMapping("pause/demands")
    public ResponseEntity<?> pauseLoanAccountDemands(@RequestParam String acid) {
        try {
            return ResponseEntity.ok().body(loanService.pauseLoanAccountDemands(acid));

        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PutMapping("classify")
    public ResponseEntity<?> assetClassificationService(@RequestParam String acid) {
        try {
            return ResponseEntity.ok().body(assetClassificationService.classifyLoan(acid));

        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PutMapping("unpause/demands")
    public ResponseEntity<?> unpauseLoanAccountDemands(@RequestParam String acid) {
        try {
            return ResponseEntity.ok().body(loanService.unpauseLoanAccountDemands(acid));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PutMapping("massive/demandreversals")
    public ResponseEntity<?> massiveDemandReversals(
            @RequestParam String acid,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            Date startDateTime = datesCalculator.stringToDate(startDate);
            Date endDateTime = datesCalculator.stringToDate(endDate);
            return ResponseEntity.ok().body(loanService.massiveDemandReversals(acid, startDateTime, endDateTime, false));
        } catch (Exception e) {
            log.error("Caught Error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred during massive demand reversals.");
        }
    }



    @PutMapping("very/very/massive/demandreversals")
    public ResponseEntity<?> veryVerymassiveDemandReversals(@RequestParam String key,
                                                            @RequestParam String endDate) {
        try {
            List<LoanRepository.NonPerformanceLoanReversals> accountsForReversals = loanRepository.loansForNonPerformingLoans();
            Date endDateTime = datesCalculator.stringToDate(endDate);

            for (LoanRepository.NonPerformanceLoanReversals nonPerformanceLoanReversals : accountsForReversals) {
                EntityResponse entityResponse = loanService.massiveDemandReversals(
                        nonPerformanceLoanReversals.getAcid(),
                        nonPerformanceLoanReversals.getDate(),
                        endDateTime,
                        false
                );
            }

            return ResponseEntity.ok().body("Done");
        } catch (Exception e) {
            log.error("Caught Error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred during very massive demand reversals.");
        }
    }


    @PutMapping("pause/demandsatisfaction")
    public ResponseEntity<?> pauseLoandemandsatisfaction(@RequestParam String acid) {
        try {
            return ResponseEntity.ok().body(loanService.pauseLoandemandsatisfaction(acid));

        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    @PutMapping("unpause/demandsatisfaction")
    public ResponseEntity<?> unpauseLoandemandsatisfaction(@RequestParam String acid) {
        try {
            return ResponseEntity.ok().body(loanService.unpauseLoandemandsatisfaction(acid));

        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PutMapping("resume/demands")
    public ResponseEntity<?> resumeDemandGeneration(@RequestParam String acid) {
        try {
            return ResponseEntity.ok().body(loanService.resumeDemandGeneration(acid));

        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PutMapping("unverified/loan/disbursement/list")
    public ResponseEntity<List<EntityResponse>> unverifiedLoanDisbursement(@RequestParam String acid) {
        try {
            return ResponseEntity.ok().body(loanDisbursmentService.verifyLoanDisbursment(acid));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }


    @GetMapping("total/loans/disbursement/count")
    public ResponseEntity<EntityResponse<?>> loanDisbursementCount() {
        try {
            return ResponseEntity.ok().body(loanDisbursmentService.loanDisbursementCount());
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    @GetMapping("all/unverified/loan/disbursements")
    public ResponseEntity<EntityResponse<?>> findAllUnVerifiedLoanDisbursement() {
        try {
            return ResponseEntity.ok().body(loanDisbursmentService.findAllUnVerifiedLoanDisbursement());
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
}