package com.emtechhouse.accounts.Models.Accounts.Loans.LoanBooking;

import com.emtechhouse.accounts.Models.Accounts.AccountServices.AccountTransactionService;
import com.emtechhouse.accounts.Models.Accounts.Loans.Loan;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanCalculatorService;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanRepository;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanSchedule.LoanSchedule;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanSchedule.LoanScheduleRepo;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.GeneralProductDetails;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ItemServiceCaller;
import com.emtechhouse.accounts.Models.Accounts.Validators.ValidatorService;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.PartTrans.PartTran;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TransactionHeader;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TransactionsController;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.emtechhouse.accounts.Utils.DatesCalculator;
import com.emtechhouse.accounts.Utils.HttpInterceptor.EntityRequestContext;
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
public class LoanBookingService {
    @Autowired
    private LoanBookingRepo loanBookingRepo;
    @Autowired
    private DatesCalculator datesCalculator;
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private AccountTransactionService accountTransactionService;
    @Autowired
    private ValidatorService validatorsService;
    @Autowired
    private ItemServiceCaller itemServiceCaller;
    @Autowired
    private LoanCalculatorService loanCalculatorService;
    @Autowired
    private LoanScheduleRepo loanScheduleRepo;
    @Autowired
    private TransactionsController transactionsController;



    //check BOOK last date,
    //check sum BOOKED
    //check maturity date
    public EntityResponse manualBooking2(String acid){
        if (true)
            return null;
        try{
            log.info("initializing booking force");
            Optional<Loan> loanToBook = loanRepository.findByAcid(acid);
            if(loanToBook.isPresent()){
                Loan loan = loanToBook.get();
                if(loan.getLoanStatus().equalsIgnoreCase(CONSTANTS.DISBURSED)){
                    if(loan.getBookingLastDate().compareTo(new Date())!=0){
                        if(loan.getInterestCalculationMethod().equalsIgnoreCase(CONSTANTS.REDUCING_BALANCE)){
//                            return bookInterestForReducingBalance(loan);
                            return null;
                        }else {
                            return bookInterestForFlatRate(loan);
                        }
                    }else {
                        log.error("Failed to book-- Booking up to date");
                        EntityResponse response = new EntityResponse();
                        response.setMessage(" FAILED! BOOKED UP TO DATE: "+acid);
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
            }else{
                log.error("FAILED! LOAN ACCOUNT NOT FOUND: ");
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

    //

    //----------------------FLAT RATE-------------------------------------------
    public EntityResponse bookInterestForFlatRate(Loan loan){
        try {
            log.info("steppppppp2");
            String acid = loan.getAccount().getAcid();
            Date today= new Date ();
            String productCode= loan.getAccount().getProductCode();
            Date lastBookingDate = loan.getBookingLastDate();
            Double sumBookedAmount= loan.getSumBookedAmount();

            String entity = EntityRequestContext.getCurrentEntityId();
            if(entity==null){
                entity=CONSTANTS.SYSTEM_ENTITY;
            }

            LocalDate startDate=datesCalculator.convertDateToLocalDate(loan.getInstallmentStartDate());


//            List<LoanSchedule> schedules=loan.getLoanSchedules();
            List<LoanSchedule> schedules=loanScheduleRepo.findByLoan(loan);
            List<LoanSchedule> affectedSchedules =new ArrayList<>();
            LocalDate lastDateOfBooking= datesCalculator.convertDateToLocalDate(lastBookingDate);
            //except first accrual date
            if(lastDateOfBooking.compareTo(LocalDate.now())!=0 || (loan.getBookingLastDate().equals(loan.getInterestCalculationStartDate()) && loan.getSumBookedAmount()<1)){
                //get specific slabs affected

                if(schedules.size()>0){
//                    LocalDate bookingStartingDate = lastDateOfBooking; //plus one day //rcll
                    LocalDate bookingStartingDate;
                    if(loan.getBookingLastDate().equals(loan.getInterestCalculationStartDate())){
                        bookingStartingDate=lastDateOfBooking;
                    }else {
                        bookingStartingDate=datesCalculator.addDate(lastDateOfBooking,1,"DAYS");
                    }
                    LocalDate now = LocalDate.now();
                    final LocalDate[] firstSlabDemandDate = new LocalDate[1];
                    final LocalDate[] lastSlabDemandDate = new LocalDate[1];

                    LoanSchedule lastSchedule=schedules.get(schedules.size() -1);
                    LocalDate lastDemandScheduleDate=lastSchedule.getDemandDate();

                    schedules.forEach(schedule->{
                        LocalDate scheduleStartDatestartDate=schedule.getStartDate();
                        LocalDate scheduleDemandDate =schedule.getDemandDate();
                        if(scheduleStartDatestartDate.compareTo(bookingStartingDate)<=0 && scheduleDemandDate.compareTo(bookingStartingDate)>=0) {
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
                    schedules.forEach(schedule->{
                        //compare demandDates
                        LocalDate demandDate=schedule.getDemandDate();
                        if(firstSlabDemandDate[0] !=null && lastSlabDemandDate[0] != null){
                            if(demandDate.compareTo(firstSlabDemandDate[0])>=0 && demandDate.compareTo(lastSlabDemandDate[0])<=0){
                                affectedSchedules.add(schedule);
                            }
                        }
                    });


                    //calculating the number of affected days per slab
                    final Double[] bookedInterestAmount = {0.0};
                    if(affectedSchedules.size()>0){
                        affectedSchedules.forEach(affectedSchedule->{
                            Long affectedDays=0L;

                            LocalDate affectedScheduleStartDate=affectedSchedule.getStartDate();
                            LocalDate affectedScheduleDemandDate= affectedSchedule.getDemandDate();
                            //logic
                            if(bookingStartingDate.compareTo(affectedScheduleStartDate)>=0 && now.compareTo(affectedScheduleDemandDate)>0){
                                affectedDays=datesCalculator.getDaysDifference(bookingStartingDate,affectedScheduleDemandDate)+1;
                                bookedInterestAmount[0] = bookedInterestAmount[0] +(affectedDays*affectedSchedule.dailyInterest);
                                log.info("affected days"+affectedDays);
                            } else if (bookingStartingDate.compareTo(affectedScheduleStartDate)>=0 && now.compareTo(affectedScheduleDemandDate)<=0) {
                                affectedDays=datesCalculator.getDaysDifference(bookingStartingDate,now)+1;
                                bookedInterestAmount[0] = bookedInterestAmount[0] +(affectedDays*affectedSchedule.dailyInterest);
                                log.info("affected days"+affectedDays);
                            } else {
                                if(now.compareTo(affectedScheduleDemandDate)<=0){
                                    affectedDays=datesCalculator.getDaysDifference(affectedScheduleStartDate,now)+1;
                                    bookedInterestAmount[0] = bookedInterestAmount[0] +(affectedDays*affectedSchedule.dailyInterest);
                                    log.info("affected days"+affectedDays.toString());
                                }else {
                                    affectedDays=affectedSchedule.getDaysDifference();
                                    bookedInterestAmount[0] = bookedInterestAmount[0] +(affectedDays*affectedSchedule.dailyInterest);
                                    log.info("affected days"+affectedDays.toString());
                                }
                            }
                        });

                        log.info("total interest"+bookedInterestAmount[0].toString());

                        Double interestToBeBooked=bookedInterestAmount[0];

                        EntityResponse entityResponse= itemServiceCaller.getGeneralProductDetail1(productCode,
                                CONSTANTS.LOAN_ACCOUNT);
                        log.info("product code:"+productCode);

                        if(entityResponse.getStatusCode().equals(HttpStatus.OK.value())) {
                            GeneralProductDetails generalProductDetails= (GeneralProductDetails) entityResponse.getEntity();

                            // GET CURRENCY, DR ACCOUNT AND CR ACCOUNT
                            String drAc=generalProductDetails.getInt_receivable_ac();

                            String crAc=generalProductDetails.getPl_ac();

                            //------Validate cr and debit accounts------//
                            EntityResponse drAcValidator=validatorsService.acidValidator(drAc ,
                                    "Interest recievable");
                            if(drAcValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                                //Error
                                return drAcValidator;
                            }else if(drAcValidator.getStatusCode().equals(200)){
                                //validate crAc
                                EntityResponse crAcValidator=validatorsService.acidValidator(crAc,
                                        "Profit and los");
                                if(crAcValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                                    //Error
                                    return crAcValidator;
                                }else {
                                    //perform transaction
                                    // Perform transaction to Dr receivable and Cr P&L account
                                    String loanCurrency=loan.getAccount().getCurrency();
                                    log.info("calling transaction function");
                                    String transactionDescription="INTEREST BOOKED FROM LOAN ACCOUNT: "+acid+" START DATE: "+lastBookingDate.toString()+"END DATE: "+today.toString();
                                    TransactionHeader tranHeader= createTransactionHeader(loanCurrency,
                                            transactionDescription,
                                            interestToBeBooked,
                                            drAc,
                                            crAc);
                                    EntityResponse transactionRes= (EntityResponse) transactionsController.systemTransactionold(tranHeader).getBody();
                                    if(!transactionRes.getStatusCode().equals(HttpStatus.OK.value())){
                                        ///failed transactioN
                                        EntityResponse response = new EntityResponse();
                                        response.setMessage("TRANSACTION ERROR! COULD NOT PERFORM LOAN BOOKING TRANSACTION FOR LOAN ACCOUNT: "+acid+" START DATE: "+lastBookingDate.toString()+"END DATE: "+today.toString());
                                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                        response.setEntity("");
                                        return response;
                                    }else {
                                        String transactionCode= (String) transactionRes.getEntity();
                                        //Add into booking  table
                                        LoanBooking loanBooking = new LoanBooking();
                                        if(lastDemandScheduleDate.compareTo(now)<0){
                                            Date bookingLastDate= datesCalculator.convertLocalDateToDate(lastDemandScheduleDate);
                                            loanBooking.setToDate(bookingLastDate);
//                                            loan.setBookingLastDate(bookingLastDate);

                                            loanRepository.updateBookingLastDate(bookingLastDate,
                                                    loan.getSn());
                                        }else {
                                            loanBooking.setToDate(today);
                                            loan.setBookingLastDate(today);
                                            loanRepository.updateBookingLastDate(today,
                                                    loan.getSn());
                                        }
                                        loanBooking.setTransactionCode(transactionCode);
                                        loanBooking.setBookingCode(transactionCode);
                                        loanBooking.setAmountBooked(interestToBeBooked);
                                        loanBooking.setBookingFrequency("F");
                                        loanBooking.setFromDate(lastBookingDate);
                                        loanBooking.setAcid(acid);
                                        loanBooking.setLoan(loan);
                                        loanBooking.setInterestType(CONSTANTS.NORMAL_INTEREST);
                                        loanBooking.setBookedOn(today);
                                        loanBookingRepo.save(loanBooking);

                                        //update loan details
//                                        loan.setSumBookedAmount(sumBookedAmount+interestToBeBooked);
//                                        loanRepository.save(loan);

                                        loanRepository.updateLoanSumBookedAmount(interestToBeBooked,
                                                loan.getSn());

                                        EntityResponse response = new EntityResponse();
                                        response.setMessage("SUCCESSFULLY POSTED BOOKING TRANSACTION FOR LOAN ACCOUNT: "+acid+ ", START DATE: "+lastBookingDate.toString()+" END DATE: "+today.toString()+", TRANSACTION ID: "+transactionCode);
                                        response.setStatusCode(HttpStatus.CREATED.value());
                                        response.setEntity(loanBooking);
                                        return response;
                                    }
                                }
                            }else {
                                EntityResponse response = new EntityResponse();
                                response.setMessage("ACCOUNT VERIFICATION ERROR! COULD NOT PERFORM DEMAND GENERATION TRANSACTION FOR LOAN ACCOUNT: "+acid);
                                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                response.setEntity("");
                                return response;
                            }
                        }else {
                            return entityResponse;
                        }
                    }else {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("COULD NOT FIND ANY AFFECTED SCHEDULES "+acid);
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity("");
                        return response;
                    }

                }else{
                    EntityResponse response = new EntityResponse();
                    response.setMessage("SCHEDULE ERROR, NO SLABS");
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    return response;
                }
            }else {
                EntityResponse response = new EntityResponse();
                response.setMessage("BOOKED UP TO DATE");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return response;
            }


        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

//    public EntityResponse bookInterestForReducingBalance(Loan loan){
//        try {
//            String acid = loan.getAccount().getAcid();
//            Date today= new Date ();
//            String productCode= loan.getAccount().getProductCode();
//            Date lastBookingDate = loan.getBookingLastDate();
//            Double sumBookedAmount= loan.getSumBookedAmount();
//
//            String entity = EntityRequestContext.getCurrentEntityId();
//            if(entity==null){
//                entity=CONSTANTS.SYSTEM_ENTITY;
//            }
//
//            LocalDate startDate=datesCalculator.convertDateToLocalDate(loan.getInstallmentStartDate());
//
//
//            List<LoanSchedule> schedules=loan.getLoanSchedules();
//            List<LoanSchedule> affectedSchedules =new ArrayList<>();
//            LocalDate lastDateOfBooking= datesCalculator.convertDateToLocalDate(lastBookingDate);
//            if(lastDateOfBooking.compareTo(LocalDate.now())!=0 || (loan.getBookingLastDate().equals(loan.getInterestCalculationStartDate()) && loan.getSumBookedAmount()<1)){
//                //get specific slabs affected
//
//                if(schedules.size()>0){
////                    LocalDate bookingStartingDate = lastDateOfBooking; //plus one day //rcll
//                    LocalDate bookingStartingDate;
//                    if(loan.getBookingLastDate().equals(loan.getInterestCalculationStartDate())){
//                        bookingStartingDate=lastDateOfBooking;
//                    }else {
//                        bookingStartingDate=datesCalculator.addDate(lastDateOfBooking,1,"DAYS");
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
//                        if(scheduleStartDatestartDate.compareTo(bookingStartingDate)<=0 && scheduleDemandDate.compareTo(bookingStartingDate)>=0) {
//                            //first slab demand date
//                            firstSlabDemandDate[0] =schedule.getDemandDate();
//                            log.info("start date->"+schedule.getDemandDate().toString());
//                        }
//                        // last slab demand date
//                        if(scheduleStartDatestartDate.compareTo(now)<=0 && scheduleDemandDate.compareTo(now)>=0) {
//                            lastSlabDemandDate[0] =schedule.getDemandDate();
//                            log.info("dd date->"+schedule.getDemandDate().toString());
//                        }
//
//                        if(lastDemandScheduleDate.compareTo(now)<0){
//                            lastSlabDemandDate[0]=lastDemandScheduleDate;
//                        }
//
//                    });
//
//                    //fetch all the affected slabs
//                    schedules.forEach(schedule->{
//                        //compare demandDates
//                        LocalDate demandDate=schedule.getDemandDate();
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
//                        final Double[] bookedInterestAmount = {0.0};
//                        final LocalDate[] bookedUpTo = new LocalDate[1];
//                        affectedSchedules.forEach(affectedSchedule->{
//                            Long affectedDays=0L;
//                            LocalDate affectedScheduleStartDate=affectedSchedule.getStartDate();
//                            LocalDate affectedScheduleDemandDate= affectedSchedule.getDemandDate();
//                            //logic
//                            if(bookingStartingDate.compareTo(affectedScheduleStartDate)>=0 && now.compareTo(affectedScheduleDemandDate)>0){
//                                affectedDays=datesCalculator.getDaysDifference(bookingStartingDate,affectedScheduleDemandDate)+1;
//                                Long daysDifference=affectedSchedule.getDaysDifference();
//                                Double interestToBePaid=affectedSchedule.getLoanCustomerPaymentSchedule().getInterestToBePaid();
//                                Double dailyInterest=(interestToBePaid/daysDifference);
//                                bookedInterestAmount[0] = bookedInterestAmount[0] +(affectedDays*dailyInterest);
//                                bookedUpTo[0]=affectedScheduleDemandDate;
//                            } else if (bookingStartingDate.compareTo(affectedScheduleStartDate)>=0 && now.compareTo(affectedScheduleDemandDate)<=0) {
//                                affectedDays=datesCalculator.getDaysDifference(bookingStartingDate,now)+1;
//                                Long daysDifference=affectedSchedule.getDaysDifference();
//                                Double interestToBePaid=affectedSchedule.getLoanCustomerPaymentSchedule().getInterestToBePaid();
//                                Double dailyInterest=(interestToBePaid/daysDifference);
//                                bookedInterestAmount[0] = bookedInterestAmount[0] +(affectedDays*dailyInterest);
//                                bookedUpTo[0]=now;
//                            } else {
//                                if(now.compareTo(affectedScheduleDemandDate)<=0){
//                                    affectedDays=datesCalculator.getDaysDifference(affectedScheduleStartDate,now)+1;
//                                    Long daysDifference=affectedSchedule.getDaysDifference();
//                                    Double interestToBePaid=affectedSchedule.getLoanCustomerPaymentSchedule().getInterestToBePaid();
//                                    Double dailyInterest=(interestToBePaid/daysDifference);
//                                    bookedInterestAmount[0] = bookedInterestAmount[0] +(affectedDays*dailyInterest);
//                                    bookedUpTo[0]=now;
//                                }else {
//                                    affectedDays=affectedSchedule.getDaysDifference();
//                                    Long daysDifference=affectedSchedule.getDaysDifference();
//                                    Double interestToBePaid=affectedSchedule.getLoanCustomerPaymentSchedule().getInterestToBePaid();
//                                    Double dailyInterest=(interestToBePaid/daysDifference);
//                                    bookedInterestAmount[0] = bookedInterestAmount[0] +(affectedDays*dailyInterest);
//                                    bookedUpTo[0]=affectedScheduleDemandDate;
//                                }
//                            }
//                        });
//
//                        log.info("total interest"+bookedInterestAmount[0].toString());
//
//                        Double interestToBeBooked=bookedInterestAmount[0];
//
//                        EntityResponse entityResponse=productItemService.getGeneralProductDetail1(productCode, CONSTANTS.LOAN_ACCOUNT);
//                        if(entityResponse.getStatusCode().equals(HttpStatus.OK.value())) {
//                            // GET CURRENCY, DR ACCOUNT AND CR ACCOUNT
//                            GeneralProductDetails generalProductDetails= (GeneralProductDetails) entityResponse.getEntity();
//
//                            // GET CURRENCY, DR ACCOUNT AND CR ACCOUNT
//                            String drAc=generalProductDetails.getInt_receivable_ac();
//
//                            String crAc=generalProductDetails.getPl_ac();
//
//                            //------Validate cr and debit accounts------//
//                            EntityResponse drAcValidator=validatorsService.acidValidator(drAc ,"Interest recievable");
//                            if(drAcValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
//                                //Error
//                                return drAcValidator;
//                            }else if(drAcValidator.getStatusCode().equals(200)){
//                                //validate crAc
//                                EntityResponse crAcValidator=validatorsService.acidValidator(crAc, "Profit and los");
//                                log.info("crAc"+crAc);
//                                if(crAcValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
//                                    //Error
//                                    return crAcValidator;
//                                }else {
//                                    //perform transaction
//                                    // Perform transaction to Dr receivable and Cr P&L account
//                                    String loanCurrency=loan.getAccount().getCurrency();
//                                    OutgoingTransactionDetails outgoingTransactionDetails =createTransactionModel(loanCurrency,
//                                            entity,
//                                            interestToBeBooked,
//                                            drAc, crAc, acid,
//                                            lastBookingDate, today);
//                                    EntityResponse transactionRes=accountTransactionService.createTransaction1(outgoingTransactionDetails);
//                                    if(!transactionRes.getStatusCode().equals(HttpStatus.OK.value())){
//                                        ///failed transactioN
//                                        EntityResponse response = new EntityResponse();
//                                        response.setMessage("TRANSACTION ERROR! COULD NOT PERFORM LOAN BOOKING TRANSACTION FOR LOAN ACCOUNT: "+acid+" START DATE: "+lastBookingDate.toString()+"END DATE: "+today.toString());
//                                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                                        response.setEntity("");
//                                        return response;
//                                    }else {
//                                        String transactionCode= (String) transactionRes.getEntity();
//                                        //Add into booking  table
//                                        Date bookedUpToDate=datesCalculator.convertLocalDateToDate(bookedUpTo[0]);
//                                        LoanBooking loanBooking = new LoanBooking();
//                                        loanBooking.setBookingCode(transactionCode);
//                                        loanBooking.setTransactionCode(transactionCode);
//                                        loanBooking.setAmountBooked(interestToBeBooked);
//                                        loanBooking.setBookingFrequency("F");
//                                        loanBooking.setFromDate(lastBookingDate);
//                                        loanBooking.setToDate(bookedUpToDate);
//                                        loanBooking.setAcid(acid);
//                                        loanBooking.setLoan(loan);
//                                        loanBooking.setInterestType(CONSTANTS.NORMAL_INTEREST);
//                                        loanBooking.setBookedOn(today);
//                                        loanBookingRepo.save(loanBooking);
//
//                                        //update loan details
//                                        loan.setSumBookedAmount(sumBookedAmount+interestToBeBooked);
//                                        loan.setBookingLastDate(bookedUpToDate);
//                                        loanRepository.save(loan);
//
//
//                                        EntityResponse response = new EntityResponse();
//                                        response.setMessage("SUCCESSFULLY POSTED BOOKING TRANSACTION FOR LOAN ACCOUNT: "+acid+ ", START DATE: "+lastBookingDate.toString()+" END DATE: "+today.toString()+", TRANSACTION ID: "+transactionCode);
//                                        response.setStatusCode(HttpStatus.CREATED.value());
//                                        response.setEntity(loanBooking);
//                                        return response;
//                                    }
//                                }
//                            }else {
//                                EntityResponse response = new EntityResponse();
//                                response.setMessage("ACCOUNT VERIFICATION ERROR! COULD NOT PERFORM DEMAND GENERATION TRANSACTION FOR LOAN ACCOUNT: "+acid);
//                                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                                response.setEntity("");
//                                return response;
//                            }
//                        }else {
//                            return entityResponse;
//                        }
//                    }else {
//                        EntityResponse response = new EntityResponse();
//                        response.setMessage("COULD NOT FIND ANY AFFECTED SCHEDULES "+acid);
//                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                        response.setEntity("");
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
//                response.setMessage("BOOKED UP TO DATE");
//                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                return response;
//            }
//
//
//        }catch (Exception e){
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }

        public EntityResponse bookInterestForReducingBalance(Loan loan,
                                                             Double interestToBeBooked,
                                                             Date bookedUpToDate){
        try {
            log.info("booking in progress");
            Date today= new Date ();
            String acid = loan.getAccount().getAcid();
            String productCode= loan.getAccount().getProductCode();
            Date lastBookingDate = loan.getBookingLastDate();
            lastBookingDate=datesCalculator.addDate(lastBookingDate,1,"DAYS");
            Double sumBookedAmount= loan.getSumBookedAmount();

            EntityResponse entityResponse= itemServiceCaller.getGeneralProductDetail1(productCode, CONSTANTS.LOAN_ACCOUNT);
            if(entityResponse.getStatusCode().equals(HttpStatus.OK.value())) {
                // GET CURRENCY, DR ACCOUNT AND CR ACCOUNT
                GeneralProductDetails generalProductDetails= (GeneralProductDetails) entityResponse.getEntity();

                // GET CURRENCY, DR ACCOUNT AND CR ACCOUNT
                String drAc=generalProductDetails.getInt_receivable_ac();
                String crAc=generalProductDetails.getPl_ac();

                //------Validate cr and debit accounts------//
                EntityResponse drAcValidator=validatorsService.acidValidator(drAc ,
                        "Interest recievable");
                if(drAcValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                    //Error
                    return drAcValidator;
                }else if(drAcValidator.getStatusCode().equals(200)){
                    //validate crAc
                    EntityResponse crAcValidator=validatorsService.acidValidator(crAc,
                            "Profit and los");
                    log.info("crAc"+crAc);
                    if(crAcValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                        //Error
                        log.error("profit and loss account error "+ crAcValidator.toString());
                        return crAcValidator;
                    }else {
                        //perform transaction
                        // Perform transaction to Dr receivable and Cr P&L account
                        String loanCurrency=loan.getAccount().getCurrency();
                        String transactionDescription="INTEREST BOOKED FROM LOAN ACCOUNT: "+acid+" START DATE: "+lastBookingDate.toString()+"END DATE: "+today.toString();
                        TransactionHeader tranHeader= createTransactionHeader(loanCurrency,
                                transactionDescription,
                                interestToBeBooked,
                                drAc,
                                crAc);
                        EntityResponse transactionRes= (EntityResponse) transactionsController.systemTransactionold(tranHeader).getBody();
                        if(!transactionRes.getStatusCode().equals(HttpStatus.OK.value())){
                            log.error("failed booking transaction");
                            ///failed transactioN
                            EntityResponse response = new EntityResponse();
                            response.setMessage("TRANSACTION ERROR! COULD NOT PERFORM LOAN BOOKING TRANSACTION FOR LOAN ACCOUNT: "+acid+" START DATE: "+lastBookingDate.toString()+"END DATE: "+today.toString());
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            response.setEntity("");
                            return response;
                        }else {
                            String transactionCode= (String) transactionRes.getEntity();
                            //Add into booking  table

                            LoanBooking loanBooking = new LoanBooking();
                            loanBooking.setBookingCode(transactionCode);
                            loanBooking.setTransactionCode(transactionCode);
                            loanBooking.setAmountBooked(interestToBeBooked);
                            loanBooking.setBookingFrequency("F");
                            loanBooking.setFromDate(lastBookingDate);
                            log.info("interest booked: "+interestToBeBooked.toString());
                            loanBooking.setToDate(bookedUpToDate);
                            loanBooking.setAcid(acid);
                            loanBooking.setLoan(loan);
                            loanBooking.setInterestType(CONSTANTS.NORMAL_INTEREST);
                            loanBooking.setBookedOn(today);
                            loanBookingRepo.save(loanBooking);

                            //update loan details
//                            loan.setSumBookedAmount(sumBookedAmount+interestToBeBooked);
                            loanRepository.updateLoanSumBookedAmount(interestToBeBooked,
                                    loan.getSn());
//                            loan.setBookingLastDate(bookedUpToDate);
                            loanRepository.updateBookingLastDate(bookedUpToDate,
                                    loan.getSn());
//                            loanRepository.save(loan);


                            EntityResponse response = new EntityResponse();
                            response.setMessage("SUCCESSFULLY POSTED BOOKING TRANSACTION FOR LOAN ACCOUNT: "+acid+ ", START DATE: "+lastBookingDate.toString()+" END DATE: "+today.toString()+", TRANSACTION ID: "+transactionCode);
                            response.setStatusCode(HttpStatus.CREATED.value());
                            response.setEntity(loanBooking);
                            return response;
                        }
                    }
                }else {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("ACCOUNT VERIFICATION ERROR! COULD NOT PERFORM DEMAND GENERATION TRANSACTION FOR LOAN ACCOUNT: "+acid);
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                    return response;
                }
            }else {
                return entityResponse;
            }
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }


    public EntityResponse bookInterest(Loan loan,
                                       Double interestToBeBooked,
                                       Date bookedUpToDate){
        try {
            log.info("booking in progress");
            Date today= new Date ();
            String acid = loan.getAccount().getAcid();
            String productCode= loan.getAccount().getProductCode();
            Date lastBookingDate =loanRepository.getBookingLastDate(loan.getSn());
            lastBookingDate=datesCalculator.addDate(lastBookingDate,1,"DAYS");

            EntityResponse entityResponse= itemServiceCaller.getGeneralProductDetail1(productCode,
                    CONSTANTS.LOAN_ACCOUNT);
            if(entityResponse.getStatusCode().equals(HttpStatus.OK.value())) {
                // GET CURRENCY, DR ACCOUNT AND CR ACCOUNT
                GeneralProductDetails generalProductDetails= (GeneralProductDetails) entityResponse.getEntity();

                // GET CURRENCY, DR ACCOUNT AND CR ACCOUNT
                String drAc=generalProductDetails.getInt_receivable_ac();
                String crAc=generalProductDetails.getPl_ac();

                //------Validate cr and debit accounts------//
                EntityResponse drAcValidator=validatorsService.acidValidator(drAc ,
                        "Interest recievable");
                if(drAcValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                    //Error
                    return drAcValidator;
                }else if(drAcValidator.getStatusCode().equals(200)){
                    //validate crAc
                    EntityResponse crAcValidator=validatorsService.acidValidator(crAc,
                            "Profit and los");
                    log.info("crAc"+crAc);
                    if(crAcValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                        //Error
                        log.error("profit and loss account error "+ crAcValidator.toString());
                        return crAcValidator;
                    }else {
                        //perform transaction
                        // Perform transaction to Dr receivable and Cr P&L account
                        String loanCurrency=loan.getAccount().getCurrency();
                        String transactionDescription="INTEREST BOOKED FROM LOAN ACCOUNT: "+acid+" START DATE: "+lastBookingDate.toString()+"END DATE: "+today.toString();
                        TransactionHeader tranHeader= createTransactionHeader(loanCurrency,
                                transactionDescription,
                                interestToBeBooked,
                                drAc,
                                crAc);
                        EntityResponse transactionRes= (EntityResponse) transactionsController.systemTransactionold(tranHeader).getBody();
                        if(!transactionRes.getStatusCode().equals(HttpStatus.OK.value())){
                            log.error("failed booking transaction");
                            ///failed transactioN
                            EntityResponse response = new EntityResponse();
                            response.setMessage("TRANSACTION ERROR! COULD NOT PERFORM LOAN BOOKING TRANSACTION FOR LOAN ACCOUNT: "+acid+" START DATE: "+lastBookingDate.toString()+"END DATE: "+today.toString());
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            response.setEntity("");
                            return response;
                        }else {
                            String transactionCode= (String) transactionRes.getEntity();
                            //Add into booking  table

                            LoanBooking loanBooking = new LoanBooking();
                            loanBooking.setBookingCode(transactionCode);
                            loanBooking.setTransactionCode(transactionCode);
                            loanBooking.setAmountBooked(interestToBeBooked);
                            loanBooking.setBookingFrequency("F");
                            loanBooking.setFromDate(lastBookingDate);
                            log.info("interest booked: "+interestToBeBooked.toString());
                            loanBooking.setToDate(bookedUpToDate);
                            loanBooking.setAcid(acid);
                            loanBooking.setLoan(loan);
                            loanBooking.setInterestType(CONSTANTS.NORMAL_INTEREST);
                            loanBooking.setBookedOn(today);
                            loanBookingRepo.save(loanBooking);

                            //update loan details
//                            loan.setSumBookedAmount(sumBookedAmount+interestToBeBooked);
                            loanRepository.updateLoanSumBookedAmount(interestToBeBooked,
                                    loan.getSn());
//                            loan.setBookingLastDate(bookedUpToDate);
                            loanRepository.updateBookingLastDate(bookedUpToDate,
                                    loan.getSn());
//                            loanRepository.save(loan);


                            EntityResponse response = new EntityResponse();
                            response.setMessage("SUCCESSFULLY POSTED BOOKING TRANSACTION FOR LOAN ACCOUNT: "+acid+ ", START DATE: "+lastBookingDate.toString()+" END DATE: "+today.toString()+", TRANSACTION ID: "+transactionCode);
                            response.setStatusCode(HttpStatus.CREATED.value());
                            response.setEntity(loanBooking);
                            return response;
                        }
                    }
                }else {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("ACCOUNT VERIFICATION ERROR! COULD NOT PERFORM DEMAND GENERATION TRANSACTION FOR LOAN ACCOUNT: "+acid);
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                    return response;
                }
            }else {
                return entityResponse;
            }
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }


    //dr p&l
    //cr int receivable
    public EntityResponse reverseBooking(Loan loan,
                                         Double interestToBeBooked){
        try {
            log.info("Reversal booking in progress");
            Date today= new Date ();
            String acid = loan.getAccount().getAcid();
            String productCode= loan.getAccount().getProductCode();
            Date lastBookingDate = loanRepository.getBookingLastDate(loan.getSn());
            lastBookingDate=datesCalculator.addDate(lastBookingDate,1,"DAYS");
            Double sumBookedAmount= loan.getSumBookedAmount();

            EntityResponse entityResponse= itemServiceCaller.getGeneralProductDetail1(productCode,
                    CONSTANTS.LOAN_ACCOUNT);
            if(entityResponse.getStatusCode().equals(HttpStatus.OK.value())) {
                // GET CURRENCY, DR ACCOUNT AND CR ACCOUNT
                GeneralProductDetails generalProductDetails= (GeneralProductDetails) entityResponse.getEntity();

                // GET CURRENCY, DR ACCOUNT AND CR ACCOUNT
                String crAc=generalProductDetails.getInt_receivable_ac();
                String drAc=generalProductDetails.getPl_ac();

                //------Validate cr and debit accounts------//
                EntityResponse drAcValidator=validatorsService.acidValidator(drAc ,
                        "Interest recievable");
                if(drAcValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                    //Error
                    return drAcValidator;
                }else if(drAcValidator.getStatusCode().equals(200)){
                    //validate crAc
                    EntityResponse crAcValidator=validatorsService.acidValidator(crAc,
                            "Profit and los");
                    log.info("crAc"+crAc);
                    if(crAcValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                        //Error
                        log.error("profit and loss account error "+ crAcValidator.toString());
                        return crAcValidator;
                    }else {
                        //perform transaction
                        // Perform transaction to Dr receivable and Cr P&L account
                        String loanCurrency=loan.getAccount().getCurrency();
                        String transactionDescription="REVERSAL OF INTEREST BOOKED FROM LOAN ACCOUNT: "+acid+" START DATE: "+lastBookingDate.toString()+"END DATE: "+today.toString();
                        TransactionHeader tranHeader= createTransactionHeader(loanCurrency,
                                transactionDescription,
                                interestToBeBooked,
                                drAc,
                                crAc);
                        EntityResponse transactionRes= (EntityResponse) transactionsController.systemTransactionold(tranHeader).getBody();
                        if(!transactionRes.getStatusCode().equals(HttpStatus.OK.value())){
                            log.error("failed booking transaction");
                            ///failed transactioN
                            EntityResponse response = new EntityResponse();
                            response.setMessage("TRANSACTION ERROR! COULD NOT PERFORM LOAN BOOKING REVERSAL TRANSACTION FOR LOAN ACCOUNT: "+acid+" START DATE: "+lastBookingDate.toString()+"END DATE: "+today.toString());
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            response.setEntity("");
                            return response;
                        }else {
                            String transactionCode= (String) transactionRes.getEntity();
                            //Add into booking  table

                            LoanBooking loanBooking = new LoanBooking();
                            loanBooking.setBookingCode(transactionCode);
                            loanBooking.setTransactionCode(transactionCode);
                            loanBooking.setAmountBooked((interestToBeBooked*-1));
                            loanBooking.setBookingFrequency("F");
                            loanBooking.setFromDate(lastBookingDate);
                            log.info("interest booked: "+interestToBeBooked.toString());
                            loanBooking.setToDate(new Date());
                            loanBooking.setAcid(acid);
                            loanBooking.setLoan(loan);
                            loanBooking.setInterestType(CONSTANTS.NORMAL_INTEREST);
                            loanBooking.setBookedOn(today);
                            loanBookingRepo.save(loanBooking);

                            //update loan details
//                            loan.setSumBookedAmount(sumBookedAmount+interestToBeBooked);
                            loanRepository.updateLoanSumBookedAmount(interestToBeBooked,
                                    loan.getSn());
//                            loan.setBookingLastDate(bookedUpToDate);
                            loanRepository.updateBookingLastDate(new Date(),
                                    loan.getSn());
//                            loanRepository.save(loan);


                            EntityResponse response = new EntityResponse();
                            response.setMessage("SUCCESSFULLY POSTED BOOKING TRANSACTION FOR LOAN ACCOUNT: "+acid+ ", START DATE: "+lastBookingDate.toString()+" END DATE: "+today.toString()+", TRANSACTION ID: "+transactionCode);
                            response.setStatusCode(HttpStatus.CREATED.value());
                            response.setEntity(loanBooking);
                            return response;
                        }
                    }
                }else {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("ACCOUNT VERIFICATION ERROR! COULD NOT PERFORM DEMAND GENERATION TRANSACTION FOR LOAN ACCOUNT: "+acid);
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                    return response;
                }
            }else {
                return entityResponse;
            }
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

//    public List<EntityResponse> bookForAllLoans(){
//        try{
//            log.info("booking in progress");
//            List<EntityResponse> responses= new ArrayList<>();
//            List<Loan> activeLoans =loanRepository.getLoansToBook();
//            if(activeLoans.size()>0){
//                log.info("active loans:"+activeLoans.size());
//                activeLoans.forEach(activeLoan->{
//                    String acid = activeLoan.getAccount().getAcid();
////                    EntityResponse entityResponse=manualBooking2(acid);
//                    EntityResponse entityResponse=bookInterestForFlatRate(activeLoan);
//                    responses.add(entityResponse);
//                });
//            }else {
//                EntityResponse response = new EntityResponse();
//                response.setMessage("THERE ARE NO ACTICE LOANS");
//                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                responses.add(response);
//            }
//            return responses;
//        }catch (Exception e){
//            log.info("Catched Error {} " + e);
//            return null;
//        }
//    }



    public TransactionHeader createTransactionHeader(String loanCurrency,
                                                     String transDesc,
                                                     Double totalAmount,
                                                     String drAc,
                                                     String crAc){
//        TransactionHeader transactionHeader = new TransactionHeader();
//        transactionHeader.setTransactionType(CONSTANTS.SYSTEM_USERNAME);
//        transactionHeader.setCurrency(loanCurrency);
//        transactionHeader.setTransactionDate(new Date());
//        transactionHeader.setEntityId(CONSTANTS.SYSTEM_ENTITY);
//        transactionHeader.setTotalAmount(totalAmount);
//
//        PartTran drPartTran = new PartTran();
//        drPartTran.setPartTranType(CONSTANTS.DEBITSTRING);
//        drPartTran.setTransactionAmount(totalAmount);
//        drPartTran.setAcid(drAc);
//        drPartTran.setCurrency(loanCurrency);
//        drPartTran.setExchangeRate("");
//        drPartTran.setTransactionDate(new Date());
//        drPartTran.setTransactionParticulars(transDesc);
//        drPartTran.setIsoFlag(CONSTANTS.YES);
//
//        PartTran crPartTran = new PartTran();
//        crPartTran.setPartTranType(CONSTANTS.CREDITSTRING);
//        crPartTran.setTransactionAmount(totalAmount);
//        crPartTran.setAcid(crAc);
//        crPartTran.setCurrency(loanCurrency);
//        crPartTran.setExchangeRate("");
//        crPartTran.setTransactionDate(new Date());
//        crPartTran.setTransactionParticulars(transDesc);
//        crPartTran.setIsoFlag(CONSTANTS.YES);
//
//        List<PartTran> partTranList =new ArrayList<>();
//        partTranList.add(drPartTran);
//        partTranList.add(crPartTran);
//
//        transactionHeader.setPartTrans(partTranList);
        return null;
    }



    //------------------------fetching ----------------------------------------------------------
    public EntityResponse<List<LoanBooking>> getInterestBookedOnACertainDate(LocalDate date){
        try {
            EntityResponse userValidator= validatorsService.userValidator();
            if(userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                return userValidator;
            }else {
                List<LoanBooking> bookingInfo=loanBookingRepo.getBookedInterestByDate(date);
                EntityResponse listChecker = validatorsService.listLengthChecker(bookingInfo);
                return listChecker;
            }
        }catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    public EntityResponse<List<LoanBooking>> getBookingInfoPerAcid(String acid){
        try{
            EntityResponse userValidator= validatorsService.userValidator();
            if(userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                return userValidator;
            }else {
                List<LoanBooking> retailAccounts= loanBookingRepo.findByAcid(acid);
                EntityResponse listChecker = validatorsService.listLengthChecker(retailAccounts);
                return listChecker;
            }
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse<Integer> getCountBookedLoansOnACertainDate(LocalDate bookedOn){
        try{
            EntityResponse userValidator= validatorsService.userValidator();
            if(userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                return userValidator;
            }else {
                Integer count= loanBookingRepo.getCountBookedLoansOnACertainDate(bookedOn);
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

    public EntityResponse<List<LoanBookingRepo.AccountsIds>> getAllAcidsNotBookedOnACertainDate(LocalDate bookingDate){
        try{
            EntityResponse userValidator= validatorsService.userValidator();
            if(userValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                return userValidator;
            }else {
                List<LoanBookingRepo.AccountsIds> accountsNotBooked= loanBookingRepo.getAllAcidsNotBookedOnACertainDate(bookingDate);
                EntityResponse listChecker = validatorsService.listLengthChecker(accountsNotBooked);
                return listChecker;
            }
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }
}
