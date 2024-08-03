package com.emtechhouse.accounts.Models.Accounts.Loans;

import com.emtechhouse.accounts.Models.Accounts.Loans.LoanSchedule.LoanCustomerPaymentSchedule.LoanCustomerPaymentSchedule;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanSchedule.LoanSchedule;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.emtechhouse.accounts.Utils.DatesCalculator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class LoanCalculatorService {

    @Autowired private LoanRepository loanRepository;
    @Autowired
    DatesCalculator datesCalculator;
    @Autowired
    LoanCalculatorService loanCalculatorService;

//    public List<Loan> demandDueLoans(Date date){
//        return loanRepository.findByNextRepaymentDate(date);
//    }

    //--------LOAN PERIOD------------------------------------------------------------//
    public Long calculateLoanPeriodInDays(Loan loan){
        try{
            Date installmentStartDate = loan.getInstallmentStartDate();
            Date maturityDate =calculateMaturityDate(loan);
            long daysDifference=datesCalculator.getDaysDifference(installmentStartDate,maturityDate)+1;
            return daysDifference;
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }

    public Long calculateLoanPeriodMonths(Loan loan){
        try {
            Date startDate=loan.getInterestCalculationStartDate();
            log.info("startDate"+startDate);
            Date maturityDate=calculateMaturityDate(loan);
            log.info("maturityDate"+maturityDate);
            Long monthsDifference=datesCalculator.getMonthsDifference(startDate, maturityDate);
            return monthsDifference;
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }

    public Long calculateLoanPeriodMonths(Date interestCalcStartDate, Date maturityDate){
        try {
            Long monthsDifference=datesCalculator.getMonthsDifference(interestCalcStartDate, maturityDate);
            return monthsDifference;
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }

    //------------INTERESTS---------------------------------------------------------------------------------//

    public Double calculateMonthlyInterest(Double interestRatePerAnnum){
        try{
            Double monthlyInterest=(interestRatePerAnnum/12);
            return monthlyInterest;
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }

//    public Double calculateInterestToBePaidForFlatRate(Loan loan){
//        try{
//            Double interestRate =loan.getInterestRate();
//            Double principalAmt =loan.getPrincipalAmount();
//
//            Double interest = principalAmt*(interestRate/100);
//            return interest;
//        }catch (Exception e){
//            log.info("Caught Error {}"+e);
//            return null;
//        }
//    }

    public Double calculateInterestToBePaidForFlatRate(Loan loan){
        try{
            Double interestRatePerAnnum=loan.getInterestRate();
            Long loanPeriodInMonths=calculateLoanPeriodMonths(loan);
            Double monthlyRate=calculateMonthlyInterest(interestRatePerAnnum);
            Double interestRateToBeApplied=(monthlyRate*loanPeriodInMonths);
            Double principalAmt=loan.getPrincipalAmount();
            Double totalInterest= principalAmt*(interestRateToBeApplied/100);
            return totalInterest;
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }
    public Double calculateDailyInterestToBePaid(Loan loan){
        try{
            Double interestRate =loan.getInterestRate();
            Double principalAmt =loan.getPrincipalAmount();

            Double interestTotal = principalAmt*(interestRate/100);
            Double interest =interestTotal/calculateLoanPeriodInDays(loan);
            return interest;
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }
    public Double calculateInterestToBePaid(Double interestRatePerAnnum, Double principalAmt){
        try{
            Double interest = principalAmt*(interestRatePerAnnum/100);
            interest=roundOff(interest);
            return interest;
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }

    public Double calculateInterestToBePaid(Double interestRatePerAnnum, Double principalAmt, Long loanPeriodInMonths){
        try{
            Double monthlyRate=calculateMonthlyInterest(interestRatePerAnnum);
            Double interestRateToBeApplied=(monthlyRate*loanPeriodInMonths);
            Double totalInterest= principalAmt*(interestRateToBeApplied/100);
            return totalInterest;
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }
    //TODO: PRINCIPAL PLUS INTEREST

    public Double calculateNetAmount(Loan loan){
        try{
            Double principalAmt =loan.getPrincipalAmount();
            Double interest=calculateInterestToBePaidForFlatRate(loan);
            Double total= principalAmt+interest;
            return total;
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }

    //Works only for flat rate
    public Double calculateInterestDemandAmount(Loan loan){
        try {
            Integer numberOfInstallments = loan.getNumberOfInstallments();
            Double interestDemandAmount = (calculateInterestToBePaidForFlatRate(loan)/numberOfInstallments);
            interestDemandAmount= roundOff(interestDemandAmount);
            return (interestDemandAmount);
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }
    public Double calculateInterestDemandAmount(Integer numberOfInstallments,Double principalAmt,Double interestRate){
        try {
            return (calculateInterestToBePaid(interestRate, principalAmt)/numberOfInstallments);
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }
    public Double calculatePrincipalDemandAmount(Loan loan){
        try {
            Integer numberOfInstallments = loan.getNumberOfInstallments();
            Double principalAmount = loan.getPrincipalAmount();
            Double principalDemand=(principalAmount/numberOfInstallments);
            principalDemand=roundOff(principalDemand);
            return (principalDemand);
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }

    //    public Date calculateMaturityDate(Loan loan){
//        try{
//            String repaymentPeriodId= loan.getRepaymentPeriodId();
//            Integer repaymentPeriod= loan.getRepaymentPeriod();
//            Date startDate = loan.getInstallmentStartDate();
//            LocalDate installmentStartDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//            LocalDate mDate =addDate(installmentStartDate,repaymentPeriod,repaymentPeriodId);
//
//            Date maturityDate = Date.from(mDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
//            return maturityDate;
//
//        }catch (Exception e){
//            log.info("Caught Error {}"+e);
//            return null;
//        }
//    }
    public Double calculateInstallmentAmount(Loan loan){
        try {
            Double installmentAmount=0.0;
            log.info("interest calc method: "+loan.getInterestCalculationMethod());
            if(loan.getInterestCalculationMethod().equals(CONSTANTS.FLAT_RATE)){
                Integer numberOfInstallments = loan.getNumberOfInstallments();
                installmentAmount=calculateNetAmount(loan)/ numberOfInstallments;
            }else if(loan.getInterestCalculationMethod().equals(CONSTANTS.REDUCING_BALANCE)){
                installmentAmount=getMonthlyPayment(loan.getPrincipalAmount(), loan.getInterestRate(), loan.getNumberOfInstallments());
            }
            installmentAmount=roundOff(installmentAmount);
            return installmentAmount;
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }
    // ---- when opening an account----------
//    public Date calculateNextRepaymentDate(Loan loan){
//        try{
//            String freqId =loan.getFrequencyId();
//            Integer freqPeriod =loan.getFrequencyPeriod();
//            Date startDate = loan.getInstallmentStartDate();
//            Date nextRepaymentDate =datesCalculator.addDate(startDate,freqPeriod,freqId);
//            return nextRepaymentDate;
//
//        }catch (Exception e){
//            log.info("Caught Error {}"+e);
//            return null;
//        }
//    }

    public Double calculateOutstandingPrincipal(Loan loan){
        try {

            return 0.00;
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }

    public Double calculateOutStandingInterest(Loan loan){
        try {
            return 0.00;
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }

    public Double calculateTotalLoanBalance(Loan loan){
        try {
            return 0.00;
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }
    public Date calculateMaturityDate(Loan loan){
        try{
            String frequencyId= loan.getFrequencyId();
            Integer frequencyPeriod= loan.getFrequencyPeriod();
            Integer numberOfInstallments=loan.getNumberOfInstallments();
            Date startDate = getInterestCalculationStartDateDate(loan);
            for (Integer i =0; i<numberOfInstallments;i++){
                startDate =datesCalculator.addDate(startDate,frequencyPeriod, frequencyId);
            }
            return  startDate;

        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }

    public Date calculateMaturityDate(Date IntCalcStartDate,
                                      Integer frequencyPeriod,
                                      String frequencyId,
                                      Integer numberOfInstallments){
        try{
            for (Integer i =0; i<numberOfInstallments;i++){
                IntCalcStartDate =datesCalculator.addDate(IntCalcStartDate,frequencyPeriod, frequencyId);
            }
            return  IntCalcStartDate;

        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }

    public Date calculateMaturityDate(String frequencyId,Integer frequencyPeriod, Integer numberOfInstallments, Date startDate){
        try{
            for (Integer i =0; i<numberOfInstallments;i++){
                startDate =datesCalculator.addDate(startDate,frequencyPeriod, frequencyId);
            }
            return  startDate;

        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }
    public Date getInterestCalculationStartDateDate(Loan loan){
        try{
            String frequencyId= loan.getFrequencyId();
            Integer frequencyPeriod= loan.getFrequencyPeriod();

            Date installmentsStartDate =loan.getInstallmentStartDate();
            Date intrestStartDate =datesCalculator.substractDate(installmentsStartDate,frequencyPeriod , frequencyId);
            intrestStartDate =datesCalculator.addDate(intrestStartDate,1 , "DAYS");
            return  intrestStartDate;
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }

//    public List<LoanSchedule> generateLoanSchedules(Loan loan){
//        try {
//            Integer numberOfInstallments=loan.getNumberOfInstallments();
//            String freqId =loan.getFrequencyId();
//            Integer freqPeriod =loan.getFrequencyPeriod();
//
//            Double principalDemandAmount= calculatePrincipalDemandAmount(loan);
//            Double outstandingPrinciple = loan.getPrincipalAmount()-principalDemandAmount;
//            Double interestDemandAmount = calculateInterestDemandAmount(loan);
//
//            Date startDate = loan.getInstallmentStartDate();
//            LocalDate demandStartDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//
//            LocalDate demandDate = datesCalculator.addDate(demandStartDate, freqPeriod, freqId);
//
//            List<LoanSchedule> loanSchedules= new ArrayList<>();
//
//            for(Integer i=0; i< numberOfInstallments; i++){
//                if(i.equals(0)){
//                    Date demStartDate = Date.from(demandStartDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
//
//                    Date demDate = Date.from(demandDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
//
//                    LoanSchedule loanSchedule = new LoanSchedule();
////                    loanSchedule.setStartDate(demStartDate);
//                    loanSchedule.setInstallmentNumber(i+1);
//                    loanSchedule.setInstallmentDescription("equated Demand");
//                    loanSchedule.setInstallmentAmount(principalDemandAmount+interestDemandAmount);
//                    loanSchedule.setPrincipleAmount(principalDemandAmount);
//                    loanSchedule.setInterestAmount(interestDemandAmount);
//                    loanSchedule.setPrincipalOutstanding(outstandingPrinciple);
////                    loanSchedule.setNextDemandDate(demDate);
//                    loanSchedule.setStatus("NOT-PAID");
//                    loanSchedule.setDeleteFlag('N');
//                    loanSchedules.add(loanSchedule);
//
//                    demandDate = datesCalculator.addDate(demandStartDate, freqPeriod, freqId);
//                    demandStartDate= demandDate;
//                    outstandingPrinciple=outstandingPrinciple-principalDemandAmount;
//                }else{
//                    Date demStartDate = Date.from(demandStartDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
//
//                    Date demDate = Date.from(demandDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
//
//                    LoanSchedule loanSchedule = new LoanSchedule();
////                    loanSchedule.setStartDate(Date.from(datesCalculator.addDate(demandDate, 1, "DAYS").atStartOfDay(ZoneId.systemDefault()).toInstant()));
//                    loanSchedule.setInstallmentNumber(i+1);
//                    loanSchedule.setInstallmentDescription("equated Demand");
//                    loanSchedule.setInstallmentAmount(principalDemandAmount+interestDemandAmount);
//                    loanSchedule.setPrincipleAmount(principalDemandAmount);
//                    loanSchedule.setInterestAmount(interestDemandAmount);
//                    loanSchedule.setPrincipalOutstanding(outstandingPrinciple);
////                    loanSchedule.setNextDemandDate(Date.from(datesCalculator.addDate(demandDate, freqPeriod, freqId).atStartOfDay(ZoneId.systemDefault()).toInstant()));
//                    loanSchedule.setStatus("NOT-PAID");
//                    loanSchedule.setDeleteFlag('N');
//                    loanSchedules.add(loanSchedule);
//
//                    demandDate = datesCalculator.addDate(demandStartDate, freqPeriod, freqId);
//                    demandStartDate= demandDate;
//                    outstandingPrinciple=outstandingPrinciple-principalDemandAmount;
//                }
//
//
//            }
//
//            return loanSchedules;
//
//        }catch (Exception e){
//            log.info("Caught Error {}"+e);
//            return null;
//        }
//    }

    public List<LoanSchedule> generateLoanSchedules(Loan loan){
        try{
            //check interest calculation method
            Integer numberOfInstallments=loan.getNumberOfInstallments();
            String freqId=loan.getFrequencyId();
            Integer freqPeriod=loan.getFrequencyPeriod();
            Double principalAmount=loan.getPrincipalAmount();
            Double interestRate=loan.getInterestRate();
            Double loanPeriodInYears= Double.valueOf(loan.getLoanPeriodMonths()/12);
            LocalDate startDate=datesCalculator.convertDateToLocalDate(loan.getInstallmentStartDate());

            List<LoanSchedule> loanSchedules= new ArrayList<>();

            String interstCalculationMethod= loan.getInterestCalculationMethod();
            if(interstCalculationMethod.equalsIgnoreCase(CONSTANTS.REDUCING_BALANCE)){
                loanSchedules= reducingBalanceloanScheduleCalculator(numberOfInstallments,  freqId,  freqPeriod,  principalAmount,  interestRate,  startDate);
            }else {
                //flat-rate
                loanSchedules=fixedRateLoanScheduleCalculator( numberOfInstallments,  freqId,  freqPeriod,  principalAmount,  interestRate,  startDate);
            }
            return loanSchedules;

        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }

    //INTEREST RATE GIVEN IS PER ANUM
    public List<LoanSchedule> fixedRateLoanScheduleCalculator(Integer numberOfInstallments,
                                                              String freqId,
                                                              Integer freqPeriod,
                                                              Double principalAmount,
                                                              Double interestRatePerAnnum,
                                                              LocalDate startDate){
        try {
            Date DstartDate=datesCalculator.convertLocalDateToDate(startDate);
            Date maturityDate=calculateMaturityDate(freqId, freqPeriod,  numberOfInstallments, DstartDate);
            Long monthsDifference=datesCalculator.getMonthsDifference(DstartDate, maturityDate);

            Double totalInterest=calculateInterestToBePaid(interestRatePerAnnum,  principalAmount,  monthsDifference);

            Double monthlyInterest= (totalInterest/monthsDifference);
            roundOff(monthlyInterest);
            Double monthlyInterestRate= (interestRatePerAnnum/12);

            List<LoanSchedule> loanSchedules= new ArrayList<>();

            LocalDate startingDate=datesCalculator.substractDate(startDate,freqPeriod,freqId);
            startingDate=datesCalculator.addDate(startingDate,1,"DAYS");
            LocalDate nextDate= startDate;
            Double outstandingPrincipal=principalAmount;
            Double sumInterest=0.0;

            Double interestToBePaidPerSchedule=(totalInterest/numberOfInstallments);

            interestToBePaidPerSchedule=roundOff(interestToBePaidPerSchedule);
            Double installmentPerDemand=interestToBePaidPerSchedule+(principalAmount/numberOfInstallments);
            installmentPerDemand=roundOff(installmentPerDemand);

            for (Integer i=0; i<numberOfInstallments;i++){
                if(i.equals(0)){
                    LoanSchedule loanSchedule = new LoanSchedule();
                    loanSchedule.setBeginningBalance(outstandingPrincipal);

                    sumInterest=sumInterest+interestToBePaidPerSchedule;
                    Double installmentPrincipal=(installmentPerDemand-interestToBePaidPerSchedule);
                    installmentPrincipal=roundOff(installmentPrincipal);

                    outstandingPrincipal= outstandingPrincipal-installmentPrincipal;
                    outstandingPrincipal=roundOff(outstandingPrincipal);

                    Long daysDifference =datesCalculator.getDaysDifference(startingDate,nextDate)+1;

                    Double dailyInterest= (interestToBePaidPerSchedule/daysDifference);
                    dailyInterest=roundOff(dailyInterest);


                    loanSchedule.setStartDate(startingDate);
                    loanSchedule.setInstallmentNumber(i+1);
                    loanSchedule.setInstallmentDescription(CONSTANTS.FLAT_RATE);
                    loanSchedule.setInstallmentAmount(installmentPrincipal+interestToBePaidPerSchedule);
                    loanSchedule.setPrincipleAmount(installmentPrincipal);
                    loanSchedule.setInterestAmount(interestToBePaidPerSchedule);
                    loanSchedule.setPrincipalOutstanding(outstandingPrincipal);
                    loanSchedule.setDemandDate(nextDate);
                    loanSchedule.setStatus("NOT-PAID");
                    loanSchedule.setDeleteFlag('N');
                    loanSchedule.setCumulativeInterest(sumInterest);
                    loanSchedule.setDaysDifference(daysDifference);
                    loanSchedule.setDailyInterest(dailyInterest);
                    loanSchedule.setMonthlyInterest(monthlyInterest);

                    loanSchedule.setMonthlyInterestRate(monthlyInterestRate);

                    loanSchedules.add(loanSchedule);

                    startingDate=datesCalculator.addDate(startingDate,freqPeriod,freqId);
                    nextDate= datesCalculator.addDate(nextDate,freqPeriod,freqId);
                }else {
                    LoanSchedule loanSchedule = new LoanSchedule();
                    loanSchedule.setBeginningBalance(outstandingPrincipal);

                    sumInterest=sumInterest+interestToBePaidPerSchedule;
                    Double installmentPrincipal=(installmentPerDemand-interestToBePaidPerSchedule);
                    installmentPrincipal=roundOff(installmentPrincipal);
                    outstandingPrincipal= outstandingPrincipal-installmentPrincipal;
                    outstandingPrincipal=roundOff(outstandingPrincipal);

                    Long daysDifference =datesCalculator.getDaysDifference(startingDate,nextDate)+1;

                    Double dailyInterest= (interestToBePaidPerSchedule/daysDifference);
                    dailyInterest=roundOff(dailyInterest);


                    loanSchedule.setStartDate(startingDate);
                    loanSchedule.setInstallmentNumber(i+1);
                    loanSchedule.setInstallmentDescription(CONSTANTS.FLAT_RATE);
                    loanSchedule.setInstallmentAmount(installmentPrincipal+interestToBePaidPerSchedule);
                    loanSchedule.setPrincipleAmount(installmentPrincipal);
                    loanSchedule.setInterestAmount(interestToBePaidPerSchedule);
                    loanSchedule.setPrincipalOutstanding(outstandingPrincipal);
                    loanSchedule.setDemandDate(nextDate);
                    loanSchedule.setStatus("NOT-PAID");
                    loanSchedule.setDeleteFlag('N');
                    loanSchedule.setCumulativeInterest(sumInterest);
                    loanSchedule.setDaysDifference(daysDifference);
                    loanSchedule.setDailyInterest(dailyInterest);
                    loanSchedule.setMonthlyInterest(monthlyInterest);

                    loanSchedule.setMonthlyInterestRate(monthlyInterestRate);
                    loanSchedules.add(loanSchedule);

                    startingDate=datesCalculator.addDate(startingDate,freqPeriod,freqId);
                    nextDate= datesCalculator.addDate(nextDate,freqPeriod,freqId);
                }
            }
            return loanSchedules;

        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }

    //--INTEREST RATE IS FOR THE ENTIRE LOAN PERIOD
//    public List<LoanSchedule> fixedRateLoanScheduleCalculator(Integer numberOfInstallments, String freqId, Integer freqPeriod, Double principalAmount, Double interestRate, LocalDate startDate){
//        try {
//            List<LoanSchedule> loanSchedules= new ArrayList<>();
//
//            LocalDate startingDate=datesCalculator.substractDate(startDate,freqPeriod,freqId);
//            startingDate=datesCalculator.addDate(startingDate,1,"DAYS");
//            LocalDate nextDate= startDate;
//            Double outstandingPrincipal=principalAmount;
//            Double sumInterest=0.0;
//            Double interestToBePaidPerSchedule=(calculateInterestToBePaid(interestRate,principalAmount)/numberOfInstallments);
//
//            interestToBePaidPerSchedule=roundOff(interestToBePaidPerSchedule);
//            Double installmentPerDemand=interestToBePaidPerSchedule+(principalAmount/numberOfInstallments);
//            installmentPerDemand=roundOff(installmentPerDemand);
//
//            for (Integer i=0; i<numberOfInstallments;i++){
//                sumInterest=sumInterest+interestToBePaidPerSchedule;
//                Double installmentPrincipal=(installmentPerDemand-interestToBePaidPerSchedule);
//                outstandingPrincipal= outstandingPrincipal-installmentPrincipal;
//                outstandingPrincipal=roundOff(outstandingPrincipal);
//
//                Long daysDifference =datesCalculator.getDaysDifference(startingDate,nextDate)+1;
//                Double dailyInterest= (interestToBePaidPerSchedule/daysDifference);
//                dailyInterest=roundOff(dailyInterest);
//
//                LoanSchedule loanSchedule = new LoanSchedule();
//                loanSchedule.setStartDate(startingDate);
//                loanSchedule.setInstallmentNumber(i+1);
//                loanSchedule.setInstallmentDescription(CONSTANTS.FLAT_RATE);
//                loanSchedule.setInstallmentAmount(installmentPrincipal+interestToBePaidPerSchedule);
//                loanSchedule.setPrincipleAmount(installmentPrincipal);
//                loanSchedule.setInterestAmount(interestToBePaidPerSchedule);
//                loanSchedule.setPrincipalOutstanding(outstandingPrincipal);
//                loanSchedule.setDemandDate(nextDate);
//                loanSchedule.setStatus("NOT-PAID");
//                loanSchedule.setDeleteFlag('N');
//                loanSchedule.setDaysDifference(daysDifference);
//                loanSchedule.setDailyInterest(dailyInterest);
//                loanSchedules.add(loanSchedule);
//
//                startingDate=datesCalculator.addDate(startingDate,freqPeriod,freqId);
//                nextDate= datesCalculator.addDate(nextDate,freqPeriod,freqId);
//            }
//            log.info("Sum interest=>"+sumInterest);
//            return loanSchedules;
//
//        }catch (Exception e){
//            log.info("Caught Error {}"+e);
//            return null;
//        }
//    }


    //-------------------- REDUCING BALANACE LOAN CALCULATION-------------------------------

    // interest rate should be per annum
    public List<LoanSchedule> reducingBalanceloanScheduleCalculator(Integer numberOfInstallments,
                                                                    String freqId,
                                                                    Integer freqPeriod,
                                                                    Double principalAmount,
                                                                    Double interestRate,
                                                                    LocalDate startDate){
        try {
            List<LoanSchedule> loanSchedules= new ArrayList<>();

            LocalDate startingDate=datesCalculator.substractDate(startDate,freqPeriod,freqId);
            startingDate=datesCalculator.addDate(startingDate,1,"DAYS");
            LocalDate nextDate= startDate;
            Double outstandingPrincipal=principalAmount;
            Double sumInterest=0.0;
            Double installmentPerDemand=getMonthlyPayment(principalAmount,interestRate, numberOfInstallments);
            installmentPerDemand=roundOff(installmentPerDemand);


            for (Integer i=0; i<numberOfInstallments;i++){

                if(i.equals(0)){
                    LoanSchedule loanSchedule = new LoanSchedule();
                    loanSchedule.setBeginningBalance(outstandingPrincipal);
                    Double interestToBePaid=(calculateInterestToBePaid(interestRate,outstandingPrincipal)/12);
                    interestToBePaid=roundOff(interestToBePaid);

                    sumInterest=sumInterest+interestToBePaid;
                    Double installmentPrincipal=(installmentPerDemand-interestToBePaid);
                    installmentPrincipal=roundOff(installmentPrincipal);
                    outstandingPrincipal= outstandingPrincipal-installmentPrincipal;
                    outstandingPrincipal=roundOff(outstandingPrincipal);

                    Long daysDifference =datesCalculator.getDaysDifference(startingDate,nextDate)+1;
                    Double dailyInterest= (interestToBePaid/daysDifference);
                    dailyInterest=roundOff(dailyInterest);


                    loanSchedule.setStartDate(startingDate);
                    loanSchedule.setInstallmentNumber(i+1);
                    loanSchedule.setInstallmentDescription(CONSTANTS.REDUCING_BALANCE);
                    loanSchedule.setInstallmentAmount(installmentPrincipal+interestToBePaid);
                    loanSchedule.setPrincipleAmount(installmentPrincipal);
                    loanSchedule.setInterestAmount(interestToBePaid);
                    loanSchedule.setCumulativeInterest(sumInterest);
                    loanSchedule.setPrincipalOutstanding(outstandingPrincipal);
                    loanSchedule.setDemandDate(nextDate);
                    loanSchedule.setStatus("NOT-PAID");
                    loanSchedule.setDeleteFlag('N');
                    loanSchedule.setDaysDifference(daysDifference);
                    loanSchedule.setDailyInterest(dailyInterest);
                    loanSchedules.add(loanSchedule);

                    startingDate=datesCalculator.addDate(startingDate,freqPeriod,freqId);
                    nextDate= datesCalculator.addDate(nextDate,freqPeriod,freqId);

                }else{
                    Double interestToBePaid=(calculateInterestToBePaid(interestRate,outstandingPrincipal)/12);
                    interestToBePaid=roundOff(interestToBePaid);

                    sumInterest=sumInterest+interestToBePaid;
                    Double installmentPrincipal=(installmentPerDemand-interestToBePaid);
                    installmentPrincipal=roundOff(installmentPrincipal);
                    outstandingPrincipal= outstandingPrincipal-installmentPrincipal;
                    outstandingPrincipal=roundOff(outstandingPrincipal);

                    Long daysDifference =datesCalculator.getDaysDifference(startingDate,nextDate)+1;
                    Double dailyInterest= (interestToBePaid/daysDifference);
                    dailyInterest=roundOff(dailyInterest);

                    LoanSchedule loanSchedule = new LoanSchedule();
                    loanSchedule.setStartDate(startingDate);
                    loanSchedule.setInstallmentNumber(i+1);
                    loanSchedule.setInstallmentDescription(CONSTANTS.REDUCING_BALANCE);
                    loanSchedule.setInstallmentAmount(installmentPrincipal+interestToBePaid);
                    loanSchedule.setPrincipleAmount(installmentPrincipal);
                    loanSchedule.setInterestAmount(interestToBePaid);
                    loanSchedule.setPrincipalOutstanding(outstandingPrincipal);
                    loanSchedule.setDemandDate(nextDate);
                    loanSchedule.setStatus("NOT-PAID");
                    loanSchedule.setDeleteFlag('N');
                    loanSchedule.setDaysDifference(daysDifference);
                    loanSchedule.setDailyInterest(dailyInterest);
                    loanSchedules.add(loanSchedule);

                    startingDate=datesCalculator.addDate(startingDate,freqPeriod,freqId);
                    nextDate= datesCalculator.addDate(nextDate,freqPeriod,freqId);
                }
            }
            return loanSchedules;
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }

    // create a customer payment schedule

    public LoanCustomerPaymentSchedule createCustomerPaymentSchedule(Double beginningBalance, Double principalToBeMade, Double interestRate){
        try{
            Double interestToBePaid= (beginningBalance*(interestRate/100));
            Double totalInstallmentAmountToBePaid = principalToBeMade+interestToBePaid;
            Double totalLoanBalance=beginningBalance+interestToBePaid;
            Double amountPaid=0.0;
            Double remaingBalanceAfterPayment=totalLoanBalance;

            LoanCustomerPaymentSchedule loanCustomerPaymentSchedule = new LoanCustomerPaymentSchedule();
            loanCustomerPaymentSchedule.setBeginningBalance(beginningBalance);
            loanCustomerPaymentSchedule.setTotalInstallmentToBePaid(totalInstallmentAmountToBePaid);
            loanCustomerPaymentSchedule.setPrincipalToBePaid(principalToBeMade);
            loanCustomerPaymentSchedule.setInterestToBePaid(interestToBePaid);
            loanCustomerPaymentSchedule.setTotalLoanBalance(totalLoanBalance);
            loanCustomerPaymentSchedule.setAmountPaid(0.00);
            loanCustomerPaymentSchedule.setRemainingBalanceAfterPayment(totalLoanBalance);
            loanCustomerPaymentSchedule.setLoanSchedule(null);

            return loanCustomerPaymentSchedule;
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }

    //---pmt formular---- >
    //---m -compounding periods
    //---t is the number of years
    //---if rate is per annum
    //---r->interest rate
    //---i=(r/m) interest per period
    //n= (m*t)
    //assupmtion
    //---interest rate is per anum
    //----compounding period 12 per annum
    public Double getMonthlyPayment(Double principal, Double ratePerAnnum, Integer compoundingPeriods){
        try {
            Double i= ((ratePerAnnum/100)/12);
            double n=(compoundingPeriods*1);
            Double r=((principal*i)/(1-(Math.pow((1+i),-n))));
            return roundOff(r);
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }

    public  Double calculateTotalInterestToBePaidReducingBalance(Loan loan){
        try {
            Double interest=0.0;

            Integer numberOfInstallments = loan.getNumberOfInstallments();
            String freqId =loan.getFrequencyId();
            Integer freqPeriod=loan.getFrequencyPeriod();
            Double principalAmount=loan.getPrincipalAmount();
            Double interestRate=loan.getInterestRate();
            Double loanPeriodInYears= Double.valueOf((loan.getLoanPeriodMonths()/12));
            LocalDate startDate=datesCalculator.convertDateToLocalDate(loan.getInstallmentStartDate());
            List<LoanSchedule> loanSchedules=reducingBalanceloanScheduleCalculator( numberOfInstallments,  freqId,  freqPeriod,  principalAmount,  interestRate,  startDate);
            for(Integer i=0;i<loanSchedules.size();i++){
                interest=interest+loanSchedules.get(i).getInterestAmount();
            }
            return roundOff(interest);
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }

    public  Double calculateTheFirstScheduleInterestRate(Double interestRate, Double principalAmt){
        try {
            Double interestToBePaid=0.0;
            interestToBePaid=(calculateInterestToBePaid(interestRate,principalAmt)/12);
            interestToBePaid=roundOff(interestToBePaid);
            return roundOff(interestToBePaid);
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }

    public Double getMonthlyPayablePrincipalReducingBalance(Loan loan){
        try {
            Double monthlyPrincipal=0.0;
            Double principal=loan.getPrincipalAmount();
            Double rate=loan.getInterestRate();
            Integer compoundingPeriods=loan.getNumberOfInstallments();
            Long loanPeriodInMonths=loan.getLoanPeriodMonths();
            Double loanPeriodInYears= Double.valueOf((loanPeriodInMonths/12));
            monthlyPrincipal=getMonthlyPayment( principal,  rate,  compoundingPeriods);
            return monthlyPrincipal;
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }


    public Double roundOff(Double number){
        try{
            number=Math.round(number * 100.0) / 100.0;
            return number;
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }
}