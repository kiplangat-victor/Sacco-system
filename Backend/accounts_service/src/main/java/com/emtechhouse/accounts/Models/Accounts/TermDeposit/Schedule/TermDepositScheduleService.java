package com.emtechhouse.accounts.Models.Accounts.TermDeposit.Schedule;
import com.emtechhouse.accounts.Models.Accounts.TermDeposit.TermDeposit;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.PartTrans.PartTran;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.PartTrans.PartTranRepository;
import com.emtechhouse.accounts.Utils.DatesCalculator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class TermDepositScheduleService {
    @Autowired
    private TermDepositScheduleRepo termDepositScheduleRepo;

    @Autowired
    private PartTranRepository partTranRepository;

    @Autowired
    private DatesCalculator datesCalculator;

    public Date calculateMaturityDate(String frequencyId,
                                      Integer frequencyPeriod,
                                      Integer numberOfInstallments,
                                      Date startDate){
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
    //

    public Date calculateMaturityDate( Integer periodInMonths,
                                      Date startDate){
        try{
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            startDate = formatter.parse(formatter.format(startDate));

            for (Integer i =0; i<periodInMonths;i++){
                startDate =datesCalculator.addDate(startDate,1, "MONTHS");
                log.info("date ::"+ startDate);
            }
            return  datesCalculator.substractDate(startDate,1, "DAYS");

        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }

    public Double calculateMonthlyInterest(Double interestRatePerAnnum){
        try{
            Double monthlyInterest=(interestRatePerAnnum/12);
            return monthlyInterest;
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }
    public Double calculateInterestToBePaid(Double interestRatePerAnnum,
                                            Double principalAmt,
                                            Integer periodTermMonths){
        try{
            log.info("Calculating total interest to be paid");
            log.info("principal amt is :: "+principalAmt);
            log.info("period in months is :: "+periodTermMonths);
            log.info("Interest rate per annum :: "+interestRatePerAnnum);
            Double monthlyRate=calculateMonthlyInterest(interestRatePerAnnum);
            log.info("Monthly interest rate is :: "+monthlyRate);
            Double interestRateToBeApplied=(monthlyRate*periodTermMonths);
            Double totalInterest= principalAmt*(interestRateToBeApplied/100);
            totalInterest=roundOff(totalInterest);
            log.info("The total interest is "+totalInterest);
            return totalInterest;
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }

    public Double calcMaturityValue(Double interestRatePerAnnum,
                                    Double principalAmt,
                                    Integer periodTermMonths){
        try{
            Double interest=calculateInterestToBePaid(interestRatePerAnnum,principalAmt,periodTermMonths);
            Double maturityValue= interest+principalAmt;
            maturityValue=roundOff(maturityValue);
            return maturityValue;
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }

    public Double calcInterestToPay(Double dailyInterest, Long daysDiff){
        try{
            return dailyInterest*daysDiff;
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }



    public Double calcInterestToPay(TermDeposit termDeposit) {
        try {
            //now
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            Date now = formatter.parse(formatter.format(new Date()));

            Date endDate;

            Date maturityDate = termDeposit.getMaturityDate();



            endDate = maturityDate;
//            if(maturityDate.compareTo(now)<=0) {
//                endDate=maturityDate;
//            }else {
//                endDate=now;
//            }
            Date valueDate= termDeposit.getValueDate();
            if (valueDate.compareTo(maturityDate) >= 0) {
                return null;
            }

            Double totalInterestAmount = 0.0;

            List<PartTran> partTranList = partTranRepository.getDeposits(termDeposit.getAccount().getAcid());

            for (PartTran partTran: partTranList) {
                Long daysDiff= datesCalculator.getDaysDifference(partTran.getTransactionDate(),endDate)+1;
                System.out.println("Amount: "+partTran.getTransactionAmount()+", date: "+partTran.getTransactionDate());
                System.out.println(daysDiff);
                System.out.println("Rate: "+termDeposit.getInterestRate());
                System.out.println("Days: "+((double)daysDiff)/365);
                Double interest = ((termDeposit.getInterestRate()/100) * (((double)daysDiff)/365) * partTran.getTransactionAmount());
                System.out.println("Interest amount: "+interest);
                totalInterestAmount += interest;
            }

            System.out.println("Total Interest amount: "+totalInterestAmount);

//            System.out.println(" "+termDeposit.getInterestAmount()+", "+termDeposit.getPeriodInDays());


            return totalInterestAmount;
        } catch (Exception e) {
            log.info("Caught Error {}"+e);
            return null;
        }
    }

    public Long calculateTermDepositPeriodInDays(Date startDate, Date maturityDate){
        try{
            Long daysDifference =datesCalculator.getDaysDifference(startDate,maturityDate)+1;
            return daysDifference;
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }

    //interest calculation
    //simple interest

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
