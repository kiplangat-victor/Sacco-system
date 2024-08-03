package com.emtechhouse.accounts.Models.Accounts.Loans.LoanAccrual;

import com.emtechhouse.accounts.Models.Accounts.Loans.Loan;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanCalculatorService;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanRepository;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanSchedule.LoanSchedule;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanSchedule.LoanScheduleRepo;
import com.emtechhouse.accounts.Models.Accounts.Validators.ValidatorService;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.emtechhouse.accounts.Utils.DatesCalculator;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class LoanAccrualService {
    @Autowired
    private LoanAccrualRepo loanAccrualRepo;
    @Autowired
    private DatesCalculator datesCalculator;
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private ValidatorService validatorsService;
    @Autowired
    private LoanCalculatorService loanCalculatorService;
    @Autowired
    private LoanScheduleRepo loanScheduleRepo;


//    public EntityResponse accrueInterestProcess(Loan loan){
//        try{
//            String acid = loan.getAccount().getAcid();
//
//            Date today= new Date ();
//            Date lastAccrualDate = loan.getAccrualLastDate();
//            Date interestStartDate =loan.getInterestCalculationStartDate();
//            Double loanDailyInterest= loan.getDailyInterestAmount();
//            Double sumAccrualAmount= loan.getSumAccruedAmount();
//            Long daysDifference=datesCalculator.getDaysDifference(lastAccrualDate,today);
//
//            if(lastAccrualDate.equals(interestStartDate)){
//                daysDifference=datesCalculator.getDaysDifference(lastAccrualDate,today)+1;
//            }else {
//                daysDifference=datesCalculator.getDaysDifference(lastAccrualDate,today);
//            }
//            if(daysDifference>0){
//                Double interestToBeAccrued= Math.ceil(loanDailyInterest* Double.valueOf(daysDifference));
//
//                //Add into accrual  table
//                LoanAccrual loanAccrual = new LoanAccrual();
//                loanAccrual.setAccrualCode("ACCRUAL");
//                loanAccrual.setAmountAccrued(interestToBeAccrued);
//                loanAccrual.setAcid(acid);
//                loanAccrual.setFromDate(lastAccrualDate);
//                loanAccrual.setToDate(today);
//                loanAccrual.setAccrualFrequency("F");
//                loanAccrualRepo.save(loanAccrual);
//
//                //update loan accrual date to today
//                loan.setSumAccruedAmount(sumAccrualAmount+interestToBeAccrued);
//                loan.setAccrualLastDate(today);
//                loanRepository.save(loan);
//
//                EntityResponse response = new EntityResponse();
//                response.setMessage("LOAN INTEREST SUCCESSFULLY ACCRUED, ACCOUNT: "+acid+ ", START DATE: "+lastAccrualDate.toString()+" END DATE: "+today.toString());
//                response.setStatusCode(HttpStatus.CREATED.value());
//                response.setEntity(null);
//                return response;
//            }else {
//                EntityResponse response = new EntityResponse();
//                response.setMessage(" ACCRUAL UP TO DATE FOR ACCOUNT: "+acid);
//                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                response.setEntity("");
//                return response;
//            }
//
//
//
//        }catch (Exception e){
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }

//    public void accrueInterest(){
//        try{
//            List<Loan> loans = loanRepository.findAll();
//
//            if(loans.size()>0){
//                loans.forEach(loan -> {
//                    if(loan.getMaturityDate().compareTo(new Date())>0){
//                        accrueInterestProcess(loan);
//                    }else {
//                        log.info("---");
//                        log.info(String.valueOf(loan.getMaturityDate().compareTo(new Date())));
//                    }
//                });
//            }
//        }catch (Exception e){
//            log.info("Catched Error {} " + e);
////            return null;
//        }
//    }

//    public void manualAccrual(String acid){
//        try{
//            Optional<Loan> loanToDemand = loanRepository.findByAcid(acid);
//            if(loanToDemand.isPresent()){
//                Loan loan = loanToDemand.get();
//                if(loan.getMaturityDate().compareTo(new Date())>0){
//                    accrueInterestProcess(loan);
//                }else {
//                    log.info("---");
//                    log.info(String.valueOf(loan.getMaturityDate().compareTo(new Date())));
//                }
//            }
//        }catch (Exception e){
//            log.info("Catched Error {} " + e);
////            return null;
//        }
//    }

//    public EntityResponse manualAccrual(String acid){
//        try{
//
//            Optional<Loan> loanToDemand = loanRepository.findByAcid(acid);
//            if(loanToDemand.isPresent()){
//                Loan loan = loanToDemand.get();
//                if(loan.getMaturityDate().compareTo(new Date())>0){
//                    return accrueInterestProcess(loan);
//
//                }else {
//                    EntityResponse response = new EntityResponse();
//                    response.setMessage(" FAILED! LOAN MATURED: "+acid);
//                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                    response.setEntity("");
//                    return response;
//                }
//            }else {
//                EntityResponse response = new EntityResponse();
//                response.setMessage(" FAILED! LOAN ACCOUNT NOT FOUND: "+acid);
//                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                response.setEntity("");
//                return response;
//            }
//
//        }catch (Exception e){
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }

    //check accrual last date,
    //check sum accrued
    //check maturity date
    //check if loan is disbursed or verified
    public EntityResponse manualAccrual2(String acid){ //reducing balance
        try{
            Optional<Loan> loanToDemand = loanRepository.findByAcid(acid);
            if(loanToDemand.isPresent()){

                Loan loan = loanToDemand.get();
                if(loan.getLoanStatus().equalsIgnoreCase(CONSTANTS.DISBURSED)){
                    if(loan.getAccrualLastDate().compareTo(new Date())!=0){
                        if(loan.getInterestCalculationMethod().equalsIgnoreCase(CONSTANTS.FLAT_RATE)){
//                        return accrueInterestReducingBalance(loan);
                            return accrueInterestFlatRate(loan);
                        }else {
                            return null;
                        }
                    }else {
                        EntityResponse response = new EntityResponse();
                        response.setMessage(" FAILED! ACCRUED UP TO DATE: "+acid);
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity("");
                        return response;
                    }
                }else {
                    log.error("FAILED! LOAN IS EITHER DISBURSED OR FULLY PAID");
                    EntityResponse response = new EntityResponse();
                    response.setMessage("FAILED! LOAN IS EITHER DISBURSED OR FULLY PAID");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity(null);
                    return response;
                }

            }else {
                EntityResponse response = new EntityResponse();
                response.setMessage(" FAILED! LOAN ACCOUNT NOT FOUND: "+acid);
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return response;
            }

        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }


    //---------------ACCRUAL FOR ---------------------------------------------------------------
    public EntityResponse accrueInterestFlatRate(Loan loan){
        try {
            log.info("Accrual step 3 initialized");
            String acid = loan.getAccount().getAcid();

            Date today= new Date ();
            Date lastAccrualDate = loan.getAccrualLastDate();
            Double sumAccrualAmount= loan.getSumAccruedAmount();


//            List<LoanSchedule> schedules=loan.getLoanSchedules();
            List<LoanSchedule> schedules=loanScheduleRepo.findByLoan(loan);
            List<LoanSchedule> affectedSchedules =new ArrayList<>();
            LocalDate lastDateOfAccrual= datesCalculator.convertDateToLocalDate(lastAccrualDate);
            if(lastDateOfAccrual.compareTo(LocalDate.now())!=0 || (loan.getAccrualLastDate().equals(loan.getInterestCalculationStartDate())) && loan.getSumAccruedAmount()<1){
                //get specific slabs affected
                if(schedules.size()>0){
//                    LocalDate accrualStartingDate = lastDateOfAccrual; //plus one day //rcll
                    LocalDate accrualStartingDate;
                    if(loan.getAccrualLastDate().equals(loan.getInterestCalculationStartDate())){
                        accrualStartingDate=lastDateOfAccrual;
                    }else {
                        accrualStartingDate=datesCalculator.addDate(lastDateOfAccrual,1,"DAYS");
                    }
                    LocalDate now = LocalDate.now();
                    final LocalDate[] firstSlabDemandDate = new LocalDate[1];
                    final LocalDate[] lastSlabDemandDate = new LocalDate[1];

                    LoanSchedule lastSchedule=schedules.get(schedules.size() -1);
                    LocalDate lastDemandScheduleDate=lastSchedule.getDemandDate();

                    schedules.forEach(schedule->{
                        LocalDate scheduleStartDatestartDate=schedule.getStartDate();
                        LocalDate scheduleDemandDate =schedule.getDemandDate();

                        if(scheduleStartDatestartDate.compareTo(accrualStartingDate)<=0 && scheduleDemandDate.compareTo(accrualStartingDate)>=0) {
                            //first slab demand date
                            firstSlabDemandDate[0] =schedule.getDemandDate();
                        }
                        // last slab demand date
                        if(scheduleStartDatestartDate.compareTo(now)<=0 && scheduleDemandDate.compareTo(now)>=0) {
                            lastSlabDemandDate[0] =schedule.getDemandDate();
                        }

                        if(lastDemandScheduleDate.compareTo(now)<0){
                            lastSlabDemandDate[0]=lastDemandScheduleDate;
                        }
                    });


                    //fetch all the affected slabs
                    //fetch schedule for both reducing balance and flat rate loans
                    schedules.forEach(schedule->{
                        //compare demandDates
                        LocalDate demandDate=schedule.getDemandDate();
                        //confirm if first and last slabs are not null
                        if(firstSlabDemandDate[0] !=null && lastSlabDemandDate[0] != null){
                            if(demandDate.compareTo(firstSlabDemandDate[0])>=0 && demandDate.compareTo(lastSlabDemandDate[0])<=0){
                                affectedSchedules.add(schedule);
                            }
                        }
                    });


                    //calculating the number of affected days per slab
                    final Double[] accruedInterestAmount = {0.0};
                    if(affectedSchedules.size()>0){
                        affectedSchedules.forEach(affectedSchedule->{
                            Long affectedDays=0L;

                            LocalDate affectedScheduleStartDate=affectedSchedule.getStartDate();
                            LocalDate affectedScheduleDemandDate= affectedSchedule.getDemandDate();
                            //logic
                            if(accrualStartingDate.compareTo(affectedScheduleStartDate)>=0 && now.compareTo(affectedScheduleDemandDate)>0){
                                affectedDays=datesCalculator.getDaysDifference(accrualStartingDate,affectedScheduleDemandDate)+1;
                                accruedInterestAmount[0] = accruedInterestAmount[0] +(affectedDays*affectedSchedule.dailyInterest);

                            } else if (accrualStartingDate.compareTo(affectedScheduleStartDate)>=0 && now.compareTo(affectedScheduleDemandDate)<=0) {
                                affectedDays=datesCalculator.getDaysDifference(accrualStartingDate,now)+1;
                                accruedInterestAmount[0] = accruedInterestAmount[0] +(affectedDays*affectedSchedule.dailyInterest);

                            } else {
                                if(now.compareTo(affectedScheduleDemandDate)<=0){
                                    affectedDays=datesCalculator.getDaysDifference(affectedScheduleStartDate,now)+1;

                                }else {
                                    affectedDays=affectedSchedule.getDaysDifference();
                                }
                                accruedInterestAmount[0] = accruedInterestAmount[0] +(affectedDays*affectedSchedule.dailyInterest);
                            }
                        });

                        LoanAccrual loanAccrual = new LoanAccrual();
                        if(lastDemandScheduleDate.compareTo(now)<0){
                            Date accrualLastDate= datesCalculator.convertLocalDateToDate(lastDemandScheduleDate);
                            loanAccrual.setAccrualCode("ACCR");
                            loanAccrual.setAmountAccrued(accruedInterestAmount[0]);
                            loanAccrual.setAcid(acid);
                            loanAccrual.setFromDate(lastAccrualDate);
                            loanAccrual.setToDate(accrualLastDate);
                            loanAccrual.setAccrualFrequency("F");
                            loanAccrual.setLoan(loan);
                            loanAccrual.setInterestType(CONSTANTS.NORMAL_INTEREST);
                            loanAccrual.setAccruedOn(today);
                            loanAccrualRepo.save(loanAccrual);

                            //update loan accrual date to today
//                            loan.setSumAccruedAmount(sumAccrualAmount+accruedInterestAmount[0]);
//                            loan.setAccrualLastDate(accrualLastDate);
//                            loanRepository.save(loan);

                            loanRepository.updateLoanSumAccruedAmount(accruedInterestAmount[0], loan.getSn());
//                            loan.setAccrualLastDate(today);
                            loanRepository.updateAccrualLastDate(accrualLastDate, loan.getSn());
                        }else {
                            //Add into accrual  table
                            loanAccrual.setAccrualCode("ACCR");
                            loanAccrual.setAmountAccrued(accruedInterestAmount[0]);
                            loanAccrual.setAcid(acid);
                            loanAccrual.setFromDate(lastAccrualDate);
                            loanAccrual.setToDate(today);
                            loanAccrual.setAccrualFrequency("F");
                            loanAccrual.setLoan(loan);
                            loanAccrual.setAccruedOn(today);
                            loanAccrual.setInterestType(CONSTANTS.NORMAL_INTEREST);
                            loanAccrualRepo.save(loanAccrual);

                            //update loan accrual date to today
//                            loan.setSumAccruedAmount(sumAccrualAmount+accruedInterestAmount[0]);
                            loanRepository.updateLoanSumAccruedAmount(accruedInterestAmount[0], loan.getSn());
//                            loan.setAccrualLastDate(today);
                            loanRepository.updateAccrualLastDate(today, loan.getSn());
//                            loanRepository.save(loan);
                        }

                        EntityResponse response = new EntityResponse();
                        response.setMessage("OK");
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(loanAccrual);
                        return response;
                    }else {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("COULD NOT FIND ANY AFFECTED SCHEDULES: "+loan.getAccount().getAcid());
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        return response;
                    }
                }else{
                    EntityResponse response = new EntityResponse();
                    response.setMessage("SCHEDULE ERROR, NO SLABS: "+loan.getAccount().getAcid());
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    return response;
                }
            }else {
                EntityResponse response = new EntityResponse();
                response.setMessage("ACCRUED UP TO DATE" + "accrual last date:==>"+lastDateOfAccrual.toString()+ " now: "+LocalDate.now().toString());
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return response;
            }


        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

//    public EntityResponse accrueInterestReducingBalance(Loan loan){
//        try {
//            String acid = loan.getAccount().getAcid();
//
//            Date today= new Date ();
//            Date lastAccrualDate = loan.getAccrualLastDate();
//            Double sumAccrualAmount= loan.getSumAccruedAmount();
//
//            List<LoanSchedule> schedules=loan.getLoanSchedules();
//
//            List<LoanSchedule> affectedSchedules =new ArrayList<>();
//            LocalDate lastDateOfAccrual= datesCalculator.convertDateToLocalDate(lastAccrualDate);
//            if(lastDateOfAccrual.compareTo(LocalDate.now())!=0 || (loan.getAccrualLastDate().equals(loan.getInterestCalculationStartDate())) && loan.getSumAccruedAmount()<1){
//                //get specific slabs affected
//
//                if(schedules.size()>0){
////                    LocalDate accrualStartingDate = lastDateOfAccrual; //plus one day //rcll
//                    LocalDate accrualStartingDate;
//                    if(loan.getAccrualLastDate().equals(loan.getInterestCalculationStartDate())){
//                        accrualStartingDate=lastDateOfAccrual;
//                    }else {
//                         accrualStartingDate=datesCalculator.addDate(lastDateOfAccrual,1,"DAYS");
//                    }
//                    LocalDate now = LocalDate.now();
//                    final LocalDate[] firstSlabDemandDate = new LocalDate[1];
//                    final LocalDate[] lastSlabDemandDate = new LocalDate[1];
//
//                    LoanSchedule lastSchedule=schedules.get(schedules.size() -1);
//                    LocalDate lastDemandScheduleDate=lastSchedule.getDemandDate();
//
//                    schedules.forEach(schedule->{
//                        LocalDate scheduleStartDatestartDate=schedule.getStartDate();
//                        LocalDate scheduleDemandDate =schedule.getDemandDate();
//
//                        if(scheduleStartDatestartDate.compareTo(accrualStartingDate)<=0 && scheduleDemandDate.compareTo(accrualStartingDate)>=0) {
//                            //first slab demand date
//                            firstSlabDemandDate[0] =schedule.getDemandDate();
//                        }
//                        // last slab demand date
//                        if(scheduleStartDatestartDate.compareTo(now)<=0 && scheduleDemandDate.compareTo(now)>=0) {
//                            lastSlabDemandDate[0] =schedule.getDemandDate();
//                        }
//
//                        if(lastDemandScheduleDate.compareTo(now)<0){
//                            lastSlabDemandDate[0]=lastDemandScheduleDate;
//                        }
//
//                    });
//
//                    //fetch schedule for both reducing balance and flat rate loans
//                    schedules.forEach(schedule->{
//                        //compare demandDates
//                        LocalDate demandDate=schedule.getDemandDate();
//
//                        if(firstSlabDemandDate[0] !=null && lastSlabDemandDate[0] != null){
//                            if(demandDate.compareTo(firstSlabDemandDate[0])>=0 && demandDate.compareTo(lastSlabDemandDate[0])<=0){
//                                if(schedule.getLoanCustomerPaymentSchedule()!=null){
//                                    affectedSchedules.add(schedule);
//                                }
//                            }
//                        }
//                    });
//
//
//                    //calculating the number of affected days per slab
//                    if(affectedSchedules.size()>0){
//                        final Double[] accruedInterestAmount = {0.0};
//                        final LocalDate[] accruedUpTo = new LocalDate[1];
//
//                        affectedSchedules.forEach(affectedSchedule->{
//                            Long affectedDays=0L;
//
//                            LocalDate affectedScheduleStartDate=affectedSchedule.getStartDate();
//                            LocalDate affectedScheduleDemandDate= affectedSchedule.getDemandDate();
//                            //logic
//                            if(accrualStartingDate.compareTo(affectedScheduleStartDate)>=0 && now.compareTo(affectedScheduleDemandDate)>0){
//                                affectedDays=datesCalculator.getDaysDifference(accrualStartingDate,affectedScheduleDemandDate)+1;
//                                Long daysDifference=affectedSchedule.getDaysDifference();
//                                Double interestToBePaid=affectedSchedule.getLoanCustomerPaymentSchedule().getInterestToBePaid();
//                                Double dailyInterest=(interestToBePaid/daysDifference);
//                                accruedInterestAmount[0] = accruedInterestAmount[0] +(affectedDays*dailyInterest);
//                                accruedUpTo[0] =affectedScheduleDemandDate;
//
//
//                            } else if (accrualStartingDate.compareTo(affectedScheduleStartDate)>=0 && now.compareTo(affectedScheduleDemandDate)<=0) {
//                                affectedDays=datesCalculator.getDaysDifference(accrualStartingDate,now)+1;
//                                Long daysDifference=affectedSchedule.getDaysDifference();
//                                Double interestToBePaid=affectedSchedule.getLoanCustomerPaymentSchedule().getInterestToBePaid();
//                                Double dailyInterest=(interestToBePaid/daysDifference);
//                                accruedInterestAmount[0] = accruedInterestAmount[0] +(affectedDays*dailyInterest);
//                                accruedUpTo[0] =now;
//
//
//                            } else {
//                                if(now.compareTo(affectedScheduleDemandDate)<=0){
//                                    affectedDays=datesCalculator.getDaysDifference(affectedScheduleStartDate,now)+1;
//                                    Long daysDifference=affectedSchedule.getDaysDifference();
//                                    Double interestToBePaid=affectedSchedule.getLoanCustomerPaymentSchedule().getInterestToBePaid();
//                                    Double dailyInterest=(interestToBePaid/daysDifference);
//                                    accruedInterestAmount[0] = accruedInterestAmount[0] +(affectedDays*dailyInterest);
//                                    accruedUpTo[0] =now;
//
//
//                                }else {
//                                    affectedDays=affectedSchedule.getDaysDifference();
//                                    Long daysDifference=affectedSchedule.getDaysDifference();
//                                    Double interestToBePaid=affectedSchedule.getLoanCustomerPaymentSchedule().getInterestToBePaid();
//                                    Double dailyInterest=(interestToBePaid/daysDifference);
//                                    accruedInterestAmount[0] = accruedInterestAmount[0] +(affectedDays*dailyInterest);
//                                    accruedUpTo[0] =affectedScheduleDemandDate;
//
//                                }
//                            }
//                        });
//                        //Add into accrual  table
//                        Date accruedUpTodate=datesCalculator.convertLocalDateToDate(accruedUpTo[0]);
//                        LoanAccrual loanAccrual = new LoanAccrual();
//
//                        if(lastDemandScheduleDate.compareTo(now)<0){
//                            loanAccrual.setAccrualCode("ACCR");
//                            loanAccrual.setAmountAccrued(accruedInterestAmount[0]);
//                            loanAccrual.setAcid(acid);
//                            loanAccrual.setFromDate(lastAccrualDate);
//                            loanAccrual.setToDate(accruedUpTodate);
//                            loanAccrual.setAccruedOn(today);
//                            loanAccrual.setAccrualFrequency("F");
//                            loanAccrual.setLoan(loan);
//                            loanAccrual.setInterestType(CONSTANTS.NORMAL_INTEREST);
//                            loanAccrual.setAccruedOn(new Date());
//                            loanAccrualRepo.save(loanAccrual);
//
//                            //update loan accrual date to today
//                            loan.setSumAccruedAmount(sumAccrualAmount+accruedInterestAmount[0]);
//                            loan.setAccrualLastDate(accruedUpTodate);
//                            loanRepository.save(loan);
//                        }else {
//                            loanAccrual.setAccrualCode("ACCR");
//                            loanAccrual.setAmountAccrued(accruedInterestAmount[0]);
//                            loanAccrual.setAcid(acid);
//                            loanAccrual.setFromDate(lastAccrualDate);
//                            loanAccrual.setToDate(accruedUpTodate);
//                            loanAccrual.setAccruedOn(today);
//                            loanAccrual.setAccrualFrequency("F");
//                            loanAccrual.setLoan(loan);
//                            loanAccrual.setInterestType(CONSTANTS.NORMAL_INTEREST);
//                            loanAccrual.setAccruedOn(new Date());
//                            loanAccrualRepo.save(loanAccrual);
//
//                            //update loan accrual date to today
//                            loan.setSumAccruedAmount(sumAccrualAmount+accruedInterestAmount[0]);
//                            loan.setAccrualLastDate(accruedUpTodate);
//                            loanRepository.save(loan);
//                        }
//
//                        EntityResponse response = new EntityResponse();
//                        response.setMessage("SUCCESS");
//                        response.setStatusCode(HttpStatus.OK.value());
//                        response.setEntity(loanAccrual);
//                        return response;
//
//                    }else {
//                        EntityResponse response = new EntityResponse();
//                        response.setMessage("ACCRUAL SCHEDULES NOT FOUND");
//                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                        return response;
//                    }
//
//                }else{
//                    EntityResponse response = new EntityResponse();
//                    response.setMessage("SCHEDULE ERROR, NO SLABS");
//                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
//                    return response;
//                }
//            }else {
//                EntityResponse response = new EntityResponse();
//                response.setMessage("ACCRUED UP TO DATE");
//                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                return response;
//            }
//        }catch (Exception e){
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }


        public void accrueInterestReducingBalance(Loan loan,
                                                            Double accruedInterest,
                                                            Date accruedUpTodate){
            try {
                log.info("accrual check 1");
                Date accrualLastDate = datesCalculator.addDate(loan.getAccrualLastDate(),1,"DAYS");
                LoanAccrual loanAccrual= new LoanAccrual();
                loanAccrual.setAccrualCode("ACCR");
                loanAccrual.setAmountAccrued(accruedInterest);
                loanAccrual.setAcid(loan.getAccount().getAcid());
                loanAccrual.setFromDate(accrualLastDate);
                loanAccrual.setToDate(accruedUpTodate);
                loanAccrual.setAccruedOn(new Date());
                loanAccrual.setAccrualFrequency("F");
                log.info("accrual check 2");
                loanAccrual.setLoan(loan);
                log.info("accrual check 3");
                loanAccrual.setInterestType(CONSTANTS.NORMAL_INTEREST);
                log.info("accrual check 4");
                loanAccrualRepo.save(loanAccrual);

                //update loan accrual date to today
//                loan.setSumAccruedAmount(loan.getSumAccruedAmount()+accruedInterest);
                loanRepository.updateLoanSumAccruedAmount(accruedInterest, loan.getSn());
//                loan.setAccrualLastDate(accruedUpTodate);
                loanRepository.updateAccrualLastDate(accruedUpTodate, loan.getSn());
//                loanRepository.save(loan);
            }catch (Exception e){
                log.info("Catched Error {} " + e);

            }
    }

    //reversed interest should be negative
    public void reverseAccruedNormalInterest(Loan loan,
                                              Double accruedInterest){
        try {
            log.info("accrual check 1");
            Date accrualLastDate = datesCalculator.addDate(loan.getAccrualLastDate(),1,"DAYS");
            LoanAccrual loanAccrual= new LoanAccrual();
            loanAccrual.setAccrualCode("ACCR");
            loanAccrual.setAmountAccrued(accruedInterest*-1);
            loanAccrual.setAcid(loan.getAccount().getAcid());
            loanAccrual.setFromDate(accrualLastDate);
            loanAccrual.setToDate(new Date());
            loanAccrual.setAccruedOn(new Date());
            loanAccrual.setAccrualFrequency("F");
            log.info("accrual check 2");
            loanAccrual.setLoan(loan);
            log.info("accrual check 3");
            loanAccrual.setInterestType(CONSTANTS.NORMAL_INTEREST);
            loanAccrual.setAccruedOn(new Date());
            log.info("accrual check 4");
            loanAccrualRepo.save(loanAccrual);

            //update loan accrual date to today
//                loan.setSumAccruedAmount(loan.getSumAccruedAmount()+accruedInterest);
            loanRepository.updateLoanSumAccruedAmount(accruedInterest, loan.getSn());
//                loan.setAccrualLastDate(accruedUpTodate);
            loanRepository.updateAccrualLastDate(new Date(), loan.getSn());
//                loanRepository.save(loan);
        }catch (Exception e){
            log.info("Catched Error {} " + e);

        }
    }

    public void accrueInterest(Loan loan,
                       Double accruedInterest,
                       Date accruedUpToDate){
        try {
            log.info("accrual check 1");
            Date accrualLastDate = loanRepository.getAccrualLastDate(loan.getSn());
            accrualLastDate = datesCalculator.addDate(accrualLastDate,1,"DAYS");
            LoanAccrual loanAccrual= new LoanAccrual();
            loanAccrual.setAccrualCode("ACCR");
            loanAccrual.setAmountAccrued(accruedInterest);
            loanAccrual.setAcid(loan.getAccount().getAcid());
            loanAccrual.setFromDate(accrualLastDate);
            loanAccrual.setToDate(accruedUpToDate);
            loanAccrual.setAccruedOn(new Date());
            loanAccrual.setAccrualFrequency("F");
            log.info("accrual check 2");
            loanAccrual.setLoan(loan);
            log.info("accrual check 3");
            loanAccrual.setInterestType(CONSTANTS.NORMAL_INTEREST);
            log.info("accrual check 4");
            loanAccrualRepo.save(loanAccrual);

            //update loan accrual date to today
//                loan.setSumAccruedAmount(loan.getSumAccruedAmount()+accruedInterest);
            loanRepository.updateLoanSumAccruedAmount(accruedInterest, loan.getSn());
//                loan.setAccrualLastDate(accruedUpTodate);
            loanRepository.updateAccrualLastDate(accruedUpToDate, loan.getSn());
//                loanRepository.save(loan);
        }catch (Exception e){
            log.info("Catched Error {} " + e);

        }
    }

    public EntityResponse accruePenalInterest(Loan loan, Double amount){
        try{
            LoanAccrual loanAccrual = new LoanAccrual();
            loanAccrual.setAccrualCode("ACCR");
            loanAccrual.setAmountAccrued(amount);
            loanAccrual.setAcid(loan.getAccount().getAcid());
            loanAccrual.setFromDate(new Date());
            loanAccrual.setToDate(new Date());
            loanAccrual.setAccruedOn(new Date());
            loanAccrual.setAccrualFrequency("F");
            loanAccrual.setLoan(loan);
            loanAccrual.setInterestType(CONSTANTS.PENAL_INTEREST);
            loanAccrualRepo.save(loanAccrual);

            EntityResponse response = new EntityResponse();
            response.setMessage("SUCCESS");
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(loanAccrual);
            return response;

        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }


    public List<EntityResponse> accrualForAllLoans(){
        try{
            log.info("accrual in progress");
            List<EntityResponse> responses= new ArrayList<>();
//            List<Loan> activeLoans =loanRepository.findByLoanStatus(CONSTANTS.DISBURSED);
            List<Loan> activeLoans =loanRepository.getLoansToAccrue();
            if(activeLoans.size()>0){
                activeLoans.forEach(activeLoan->{
                    String acid = activeLoan.getAccount().getAcid();
//                    EntityResponse entityResponse=manualAccrual2(acid);
                    EntityResponse entityResponse= accrueInterestFlatRate(activeLoan);
                    responses.add(entityResponse);
                });
            }else {
                EntityResponse response = new EntityResponse();
                response.setMessage("THERE ARE NO ACTICE LOANS");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                responses.add(response);
            }
            return responses;
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }






    //---------------------------fetching-----------------------------//

    public EntityResponse<List<LoanAccrual>> getAccruedOnACertainDate(LocalDate date){
        try {
            EntityResponse userValidator= validatorsService.userValidator();
            if(userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                return userValidator;
            }else {
                List<LoanAccrual> accrualInfo=loanAccrualRepo.getInterestAccruedByDate(date);
                EntityResponse listChecker = validatorsService.listLengthChecker(accrualInfo);
                return listChecker;
            }
        }catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    public EntityResponse<List<LoanAccrual>> getAccrualInfoPerAcid(String acid){
        try{
            EntityResponse userValidator= validatorsService.userValidator();
            if(userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                return userValidator;
            }else {
                List<LoanAccrual> retailAccounts= loanAccrualRepo.findByAcid(acid);
                EntityResponse listChecker = validatorsService.listLengthChecker(retailAccounts);
                return listChecker;
            }
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse<Integer> getCountAccruedLoansOnACertainDate(LocalDate accruedOn){
        try{
            EntityResponse userValidator= validatorsService.userValidator();
            if(userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                return userValidator;
            }else {
                Integer count= loanAccrualRepo.getCountAccruedLoansOnACertainDate(accruedOn);
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

    public EntityResponse<List<LoanAccrualRepo.AccountsIds>> getAllAcidsNotAccruedOnACertainDate(LocalDate accrualDate){
        try{
            EntityResponse userValidator= validatorsService.userValidator();
            if(userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                return userValidator;
            }else {
                List<LoanAccrualRepo.AccountsIds> accountsNotAccrued= loanAccrualRepo.getAllAcidsNotAccruedOnACertainDate(accrualDate);
                EntityResponse listChecker = validatorsService.listLengthChecker(accountsNotAccrued);
                return listChecker;
            }
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }


}
