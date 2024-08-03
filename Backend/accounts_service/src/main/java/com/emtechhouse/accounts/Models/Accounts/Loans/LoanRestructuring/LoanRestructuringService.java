package com.emtechhouse.accounts.Models.Accounts.Loans.LoanRestructuring;

import com.emtechhouse.accounts.Models.Accounts.Loans.Loan;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanCalculatorService;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.LoanDemand;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.LoanDemandRepository;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanRepository;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanSchedule.LoanSchedule;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanSchedule.LoanScheduleRepo;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanSchedule.LoanScheduleService;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ProductInterestDetails.ProductInterestDetails;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ProductInterestDetails.ProductInterestService;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.emtechhouse.accounts.Utils.HttpInterceptor.UserRequestContext;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class LoanRestructuringService {
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private LoanCalculatorService loanCalculatorService;
    @Autowired
    private LoanScheduleRepo loanScheduleRepo;
    @Autowired
    private LoanScheduleService loanScheduleService;
    @Autowired
    private LoanDemandRepository loanDemandRepository;
    @Autowired
    private LoanRestructuringRepo loanRestructuringRepo;
    @Autowired
    private ProductInterestService productInterestService;

    //get total loan balance- use at principal to generate a new loan schedule
    // get interest from product
    //get frequency

    //get loan from db
    //update frequency period, and length
    //update principal amount
    //update total loan balance
    //generate new schedule and update the existing ones
    // which are not full satisfied loan demands

    //NO. OF INSTALLMENTS*
    //INSTALLMENT FREQUENCY*
//    FREQUENCY PERIOD*
//    INSTALLMENT START DATE

    public EntityResponse restructureLoan(String acid, Integer installmentsNumber, String installmentFreq,
                                          Integer freqPeriod,Date installmentStartDate) {
        try {
            log.info("restructuring a loan");
            Optional<Loan> loan=loanRepository.findByAcid(acid);
            if(loan.isPresent()){
                Loan existingLoan =loan.get();
                Double loanBalance =existingLoan.getTotalLoanBalance();
//                Double principleAmount =existingLoan.getPrincipalAmount();
                //all the unsatisfied demands
                log.info("getting all unsatisfied demands");
//                List<LoanDemand> loanDemands =loanDemandRepository.findUnsatisfiedLoanDemandsByAcid(CONSTANTS.NO, acid);
                List<LoanDemand> loanDemands =loanDemandRepository.findByLoan(existingLoan);
                //create loan schedule info
                log.info("setting restructuring info");

                LoanRestructuringInfo loanRestructuringInfo= new LoanRestructuringInfo();
                loanRestructuringInfo.setRestructuredOn(new Date());
                loanRestructuringInfo.setNewPrincipalAmount(loanBalance);
                loanRestructuringInfo.setPreviousPrincipalAmount(existingLoan.getPrincipalAmount());
                loanRestructuringInfo.setNoOfDemandsCarriedForward(loanDemands.size());
                loanRestructuringInfo.setPreviousNumberOfInstallments(existingLoan.getNumberOfInstallments());
                loanRestructuringInfo.setPreviousFrequencyId(existingLoan.getFrequencyId());
                loanRestructuringInfo.setPreviousInstallmentAmount(existingLoan.getInstallmentAmount());
                loanRestructuringInfo.setPreviousInstallmentStartDate(existingLoan.getInstallmentStartDate());
                loanRestructuringInfo.setPreviousInterestRate(existingLoan.getInterestRate());
                loanRestructuringInfo.setRestructuredBy(UserRequestContext.getCurrentUser());
                loanRestructuringInfo.setLoan(existingLoan);
                //-------update existing loan
                log.info("updating exsting loan");
                existingLoan.setPrincipalAmount(loanBalance);
                existingLoan.setNumberOfInstallments(installmentsNumber);
                existingLoan.setFrequencyId(installmentFreq);
                existingLoan.setFrequencyPeriod(freqPeriod);
                existingLoan.setInstallmentStartDate(installmentStartDate);
                existingLoan.setInterestCalculationStartDate(loanCalculatorService.getInterestCalculationStartDateDate(existingLoan));
                existingLoan.setNextRepaymentDate(installmentStartDate);
                // TODO: 6/16/2023 get intersst details from product
                Double productInterest=getAnnualInterestRate(existingLoan.getAccount().getProductCode(),
                        existingLoan.getPrincipalAmount());
                existingLoan.setInterestRate(productInterest);
                System.out.println("Product interest :: "+productInterest);

                String interestCalculationMethod=getIntCalMethod(existingLoan.getAccount().getProductCode(),
                        existingLoan.getPrincipalAmount());
                existingLoan.setInterestCalculationMethod(interestCalculationMethod);
                System.out.println("Interest calculation method :: "+interestCalculationMethod);

                Double interestToBePaid=0.0;
                Double principalAmount=loanBalance;
                existingLoan.setMaturityDate(loanCalculatorService.calculateMaturityDate(existingLoan));

                existingLoan.setNetAmount(loanCalculatorService.calculateNetAmount(existingLoan));
                existingLoan.setInterestDemandAmount(loanCalculatorService.calculateInterestDemandAmount(existingLoan));
                existingLoan.setPrincipalDemandAmount(loanCalculatorService.calculatePrincipalDemandAmount(existingLoan));
                existingLoan.setSumPrincipalDemand(0.00);
                existingLoan.setSumMonthlyFeeDemand(0.00);
                existingLoan.setOverFlowAmount(0.0);
                existingLoan.setOutStandingPrincipal(existingLoan.getPrincipalAmount());
                existingLoan.setInterestAmount(interestToBePaid);
                existingLoan.setOutStandingInterest(interestToBePaid);
                existingLoan.setInterestAmount(interestToBePaid);
                existingLoan.setOutStandingInterest(interestToBePaid);
                existingLoan.setTotalLoanBalance(loanBalance);
                existingLoan.setLoanPeriodDays(loanCalculatorService.calculateLoanPeriodInDays(existingLoan));
                existingLoan.setDailyInterestAmount(loanCalculatorService.calculateDailyInterestToBePaid(existingLoan));
                existingLoan.setInstallmentAmount(loanCalculatorService.calculateInstallmentAmount(existingLoan));
                existingLoan.setAccrualLastDate(loanCalculatorService.getInterestCalculationStartDateDate(existingLoan));
                existingLoan.setBookingLastDate(loanCalculatorService.getInterestCalculationStartDateDate(existingLoan));
                existingLoan.setRestructuredFlag(CONSTANTS.YES);

                //update all unstatisfied demands
                log.info("updating all the unsatisfied demands");
                updateUnsatisfiedDemands(loanDemands);
                List<LoanSchedule>  loanSchedules=loanCalculatorService.generateLoanSchedules(existingLoan);
                //drop all the schedules, and create other ones
                List<LoanSchedule> loanScheduleList=existingLoan.getLoanSchedules();
                loanScheduleService.deleteLoanSchedules(loanScheduleList);

                existingLoan.setLoanSchedules(loanSchedules);
                loanRestructuringRepo.save(loanRestructuringInfo);
                Loan savedLoan=loanRepository.save(existingLoan);

                EntityResponse response = new EntityResponse();
                response.setMessage("SUCCESS");
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(existingLoan);
                return response;
            } else {
                // throw an error
                EntityResponse response = new EntityResponse();
                response.setMessage("Account not found");
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity("");
                return response;
            }
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }

    public Double getAnnualInterestRate(String prodCode, Double principalAmount){
        Double interest=null;
        EntityResponse res= productInterestService.getLoanInterest(prodCode,
                principalAmount);
        if(res.getStatusCode().equals(HttpStatus.NOT_FOUND.value())){
            log.error("Product interest not found");

        }else {
            ProductInterestDetails productInterestDetails= (ProductInterestDetails) res.getEntity();
            Double productLoanInterestRate=productInterestDetails.getInterestRate();
            String productInterestPeriod=productInterestDetails.getInterestPeriod();
            System.out.println("Getting interest details");

            if(productInterestPeriod.equalsIgnoreCase("p.m")){
                interest=(productLoanInterestRate*12);
            } else if (productInterestPeriod.equalsIgnoreCase("p.a")) {
                interest=productLoanInterestRate;
            }
        }
        return interest;
    }

    public String getIntCalMethod(String prodCode, Double principalAmount){
        String intCalcMethod=null;
        EntityResponse res= productInterestService.getLoanInterest(prodCode,
                principalAmount);
        if(res.getStatusCode().equals(HttpStatus.NOT_FOUND.value())){
            log.error("Product interest not found");

        }else {
            ProductInterestDetails productInterestDetails= (ProductInterestDetails) res.getEntity();
            String productInterestCalculationMethod=productInterestDetails.getInterestCalculationMethod();
            System.out.println("Getting interest details");

            if(productInterestCalculationMethod.equalsIgnoreCase("flat_rate")){
                intCalcMethod=CONSTANTS.FLAT_RATE;
            } else if (productInterestCalculationMethod.equalsIgnoreCase("reducing_balance")) {
                intCalcMethod=CONSTANTS.REDUCING_BALANCE;
            }
        }
        return intCalcMethod;
    }

    //update all unsatisfied demands

    public void updateUnsatisfiedDemands(List<LoanDemand> loanDemands) {
        try{
            for(int i=0;i<loanDemands.size();i++) {
                LoanDemand loanDemand=loanDemands.get(i);
                Double demandedAmt=loanDemand.getDemandAmount();
                Double adjustedAmount=loanDemand.getAdjustmentAmount();
                Double amountToCarryForward=demandedAmt-adjustedAmount;
                loanDemand.setOverDueCarriedForward(amountToCarryForward);
                loanDemand.setDemandCarriedFowardFlag(CONSTANTS.YES);
                loanDemand.setDeletedFlag(CONSTANTS.YES);
                loanDemand.setCarriedForwardOn(new Date());
                loanDemandRepository.save(loanDemand);
            }
        } catch (Exception e) {
            log.info("Caught Error {}"+e);
        }
    }
}
