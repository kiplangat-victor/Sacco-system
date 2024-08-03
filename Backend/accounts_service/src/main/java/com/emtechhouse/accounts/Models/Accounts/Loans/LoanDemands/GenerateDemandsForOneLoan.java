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
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ItemServiceCaller;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ProductFees.ProductFeesService;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ProductInterestDetails.ProductInterestService;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class GenerateDemandsForOneLoan {
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


    public int daysDiffference(Date date1, Date date2) {
        long time_difference = date2.getTime() - date1.getTime();

        // Calucalte time difference in days using TimeUnit class
        long days_difference = TimeUnit.MILLISECONDS.toDays(time_difference) % 365;

        return (int) days_difference;
    }


    public List<EntityResponse> feeDemandManualForce(String acid){
        try{
            List<EntityResponse> entityResponses=new ArrayList<>();
            Optional<Loan> loanToDemand = loanRepository.findByAcid(acid);
            if(loanToDemand.isPresent()){
                //confirm if loan is disbursed
                Loan loan = loanToDemand.get();
                if(loan.getLoanStatus().equalsIgnoreCase(CONSTANTS.DISBURSED)){
                    return generateLoanFeeDemand(loan);
                }else {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("FAILED! LOAN IS EITHER DISBURSED OR FULLY PAID");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity(null);
                    entityResponses.add(response);
                }
                return entityResponses;
            }else {
                EntityResponse response = new EntityResponse();
                response.setMessage("NOT_FOUND");
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setEntity(null);
                entityResponses.add(response);
                return entityResponses;
            }
        }catch (Exception e){
            log.info("Catched Error {} " + e.getStackTrace()[0].getLineNumber()+" - "+e);
            return null;
        }
    }

    //--STEP TWO
    public List<EntityResponse> demandManualForce(String acid, int daysAhead) {
        try{
            log.info("-----Creating demand manually---");

            List<EntityResponse> entityResponses=new ArrayList<>();
            Optional<Loan> loanToDemand = loanRepository.findByAcid(acid);
            if(loanToDemand.isPresent()) {
                log.info("Loan to demand is present");
                //confirm if loan is disbursed
                EntityResponse response = new EntityResponse();
                Loan loan = loanToDemand.get();
                System.out.println("After Loan to demand is present>>>>>>>>>>>>>>>>..");
                if (loan.getAccount().getAccountBalance() > -30) {
                    response.setMessage("Loan paid fully already");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity(null);
//                    System.out.println(response);
                    entityResponses.add(response);
                }else {
                    if(loan.getLoanStatus().equalsIgnoreCase(CONSTANTS.DISBURSED)) {
                        if(loan.getPausedDemandsFlag().equals(CONSTANTS.NO)) {
                            Date nextRepaymentDate =loan.getNextRepaymentDate();
                            System.out.println("Here with Next repayment date: ...." +nextRepaymentDate);
                            System.out.println("Heading to demand");
                            Double sumPrincipleDemand=loan.getSumPrincipalDemand();
                            System.out.println("PRINCIPLE DEMAND"+sumPrincipleDemand);
                            System.out.println("After sum principle");
//                        if(nextRepaymentDate.compareTo(new Date ())<0) {
                            if(daysDiffference(nextRepaymentDate, new Date ()) >= (-1*daysAhead)) {
                                EntityResponse demandCreationEntity;
//                        demandCreationEntity=createDemandByForFlatRate(loan);
                                System.out.println("INSIDE DEMANDER");
                                if(loan.getInterestCalculationMethod().equalsIgnoreCase(CONSTANTS.REDUCING_BALANCE)) {
                                    demandCreationEntity=createDemandForReducingBalance(loan);
//                            demandCreationEntity=createDemandByForFlatRate(loan);
                                }else {
                                    demandCreationEntity=createDemandByForFlatRate(loan);
                                }
                                entityResponses.add(demandCreationEntity);
                                System.out.println("After interest transaction");
                                System.out.println(demandCreationEntity);
                                Integer responseCode=demandCreationEntity.getStatusCode();
                                if(responseCode.equals(HttpStatus.CREATED.value())) {
                                    loan = loanRepository.findByAcid(acid).get();
                                    nextRepaymentDate =loan.getNextRepaymentDate();
                                    sumPrincipleDemand =loan.getSumPrincipalDemand();
//                                continue;
                                } else {
//                                break;
                                }
                            }
                        }else {
                            response.setMessage("DEMANDS FOR THIS LOAN ARE PAUSED");
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            response.setEntity(null);
                            entityResponses.add(response);
                        }

                    }else {

                        response.setMessage("FAILED! LOAN IS EITHER DISBURSED OR FULLY PAID");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity(null);
                        entityResponses.add(response);
                    }
                }
                return entityResponses;
            }else {
                EntityResponse response = new EntityResponse();
                response.setMessage("LOAN_ACCOUNT_NOT_FOUND");
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setEntity(null);
                entityResponses.add(response);
                return entityResponses;
            }
        }catch (Exception e){
            System.out.println("Error here.................");
            e.printStackTrace();
            log.info("Catched Error {} " + e.getStackTrace()[0].getLineNumber()+" - "+e);
            return null;
        }
    }


    public List<EntityResponse> demandManualForce1(String acid, int daysAhead) {
        try{
            log.info("-----Creating demand manually In YEAR---");

            List<EntityResponse> entityResponses=new ArrayList<>();
            Optional<Loan> loanToDemand = loanRepository.findByAcid(acid);
            if(loanToDemand.isPresent()) {
                log.info("Loan to demand is present In YEAR");
                //confirm if loan is disbursed
                EntityResponse response = new EntityResponse();
                Loan loan = loanToDemand.get();
                System.out.println("After Loan to demand is present In YEAR>>>>>>>>>>>>>>>>..");
                if (loan.getAccount().getAccountBalance() > -30) {
                    response.setMessage("Loan paid fully already");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity(null);
//                    System.out.println(response);
                    entityResponses.add(response);
                }else {
                    if(loan.getLoanStatus().equalsIgnoreCase(CONSTANTS.DISBURSED)) {
                        if(loan.getPausedDemandsFlag().equals(CONSTANTS.NO)) {
                            Date nextRepaymentDate =loan.getDisbursementDate();
                            System.out.println("Here with Next repayment as disbursement date In YEAR: ...." +nextRepaymentDate);
                            Double sumPrincipleDemand=loan.getSumPrincipalDemand();
                            System.out.println(" Sum Principle Demand in YEAR ++++: " +sumPrincipleDemand);
//                        if(nextRepaymentDate.compareTo(new Date ())>0) {
                            if(daysDiffference(nextRepaymentDate, new Date ()) >= (-1*daysAhead)) {
                                EntityResponse demandCreationEntity;
//                        demandCreationEntity=createDemandByForFlatRate(loan);
                                System.out.println("INSIDE DEMANDER In YEAR");
                                if(loan.getInterestCalculationMethod().equalsIgnoreCase(CONSTANTS.REDUCING_BALANCE)) {
                                    demandCreationEntity=createDemandForReducingBalance(loan);
//                            demandCreationEntity=createDemandByForFlatRate(loan);
                                }else {
                                    demandCreationEntity=createDemandByForFlatRate(loan);
                                }
                                entityResponses.add(demandCreationEntity);
                                System.out.println("After interest transaction");
                                System.out.println(demandCreationEntity);
                                Integer responseCode=demandCreationEntity.getStatusCode();
                                if(responseCode.equals(HttpStatus.CREATED.value())) {
                                    loan = loanRepository.findByAcid(acid).get();
                                    nextRepaymentDate =loan.getNextRepaymentDate();
                                    sumPrincipleDemand =loan.getSumPrincipalDemand();
//                                continue;
                                } else {
//                                break;
                                }
                            }
                        }else {
                            response.setMessage("DEMANDS FOR THIS LOAN ARE PAUSED");
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            response.setEntity(null);
                            entityResponses.add(response);
                        }

                    }else {

                        response.setMessage("FAILED! LOAN IS EITHER DISBURSED OR FULLY PAID");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity(null);
                        entityResponses.add(response);
                    }
                }
                return entityResponses;
            }else {
                EntityResponse response = new EntityResponse();
                response.setMessage("LOAN_ACCOUNT_NOT_FOUND");
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setEntity(null);
                entityResponses.add(response);
                return entityResponses;
            }
        }catch (Exception e){
            System.out.println("Error here.................");
            e.printStackTrace();
            log.info("Catched Error {} " + e.getStackTrace()[0].getLineNumber()+" - "+e);
            return null;
        }
    }


    public List<EntityResponse> demandManualForce3(String acid, int daysAhead) {
        try{
            log.info("-----Creating demand manually---");

            List<EntityResponse> entityResponses=new ArrayList<>();
            Optional<Loan> loanToDemand = loanRepository.findByAcid(acid);
            if(loanToDemand.isPresent()) {
                log.info("Loan to demand is present");
                //confirm if loan is disbursed
                EntityResponse response = new EntityResponse();
                Loan loan = loanToDemand.get();
                System.out.println("After Loan to demand is present KABISAA>>>>>>>>>>>>>>>>..");
                if (loan.getAccount().getAccountBalance() > -30) {
                    response.setMessage("Loan paid fully already");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity(null);
//                    System.out.println(response);
                    entityResponses.add(response);
                }else {
                    if(loan.getLoanStatus().equalsIgnoreCase(CONSTANTS.DISBURSED)) {
                        if(loan.getPausedDemandsFlag().equals(CONSTANTS.NO)) {
                            Date nextRepaymentDate =loan.getNextRepaymentDate();
                            System.out.println("Here Now with Next repayment date OBVIOUS: ...." +nextRepaymentDate);
                            System.out.println("About to get sum principal");
                            Double sumPrincipleDemand=loan.getSumPrincipalDemand();
                            System.out.println("Sum Principal Demand Hapa: "+sumPrincipleDemand);
//                        if(nextRepaymentDate.compareTo(new Date ()) > 0) {
//                            if(daysDiffference(nextRepaymentDate, new Date ()) >= (-1*daysAhead)) {
                                EntityResponse demandCreationEntity;
//                        demandCreationEntity=createDemandByForFlatRate(loan);
                                System.out.println("INSIDE DEMANDER");
                                if(loan.getInterestCalculationMethod().equalsIgnoreCase(CONSTANTS.REDUCING_BALANCE)) {
                                    demandCreationEntity=createDemandForReducingBalance1(loan);
//                            demandCreationEntity=createDemandByForFlatRate(loan);
                                }else {
                                    demandCreationEntity=createDemandByForFlatRate1(loan);
                                }
                                entityResponses.add(demandCreationEntity);
                                System.out.println("After interest transaction");
                                System.out.println(demandCreationEntity);
                                Integer responseCode=demandCreationEntity.getStatusCode();
                                if(responseCode.equals(HttpStatus.CREATED.value())) {
                                    loan = loanRepository.findByAcid(acid).get();
                                    nextRepaymentDate =loan.getNextRepaymentDate();
                                    sumPrincipleDemand =loan.getSumPrincipalDemand();
//                                continue;
                                } else {
//                                break;
                                }
//                            }
                        }else {
                            response.setMessage("DEMANDS FOR THIS LOAN ARE PAUSED");
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            response.setEntity(null);
                            entityResponses.add(response);
                        }

                    }else {

                        response.setMessage("FAILED! LOAN IS EITHER DISBURSED OR FULLY PAID");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity(null);
                        entityResponses.add(response);
                    }
                }
                return entityResponses;
            }else {
                EntityResponse response = new EntityResponse();
                response.setMessage("LOAN_ACCOUNT_NOT_FOUND");
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setEntity(null);
                entityResponses.add(response);
                return entityResponses;
            }
        }catch (Exception e){
            System.out.println("Error here.................");
            e.printStackTrace();
            log.info("Catched Error {} " + e.getStackTrace()[0].getLineNumber()+" - "+e);
            return null;
        }
    }





    public boolean wasPreviousDemandSatisfactionCaller1(String acid) {
        try {
            Optional<LoanDemand> loanDemand = loanDemandRepository.getLastLoanDemand1(acid);
            if (loanDemand.isPresent()) {
                Character satisfactionCaller = loanDemand.get().getSatisfactionCallerFlag();
                if (satisfactionCaller.equals(CONSTANTS.YES)) {
                    return true;
                } else if (satisfactionCaller.equals(CONSTANTS.NO)) {
                    return false;
                }
            } else {
                return true;
            }
        } catch (Exception e) {
            System.out.println("Error here");
            log.info("Caught Error {} " + e.getStackTrace()[0].getLineNumber() + " - " + e);
            return false;
        }
        return false;
    }



    public boolean wasPreviousDemandSatisfactionCaller2(String acid) {
        try {
            Optional<LoanDemand> loanDemand = loanDemandRepository.getLastLoanDemand1(acid);
            if (loanDemand.isPresent()) {
                Character satisfactionCaller = loanDemand.get().getSatisfactionCallerFlag();
                if (satisfactionCaller.equals(CONSTANTS.YES)) {
                    return true;
                } else if (satisfactionCaller.equals(CONSTANTS.NO)) {
                    return true;
                }
            } else {
                return true;
            }
        } catch (Exception e) {
            System.out.println("Error here");
            log.info("Caught Error {} " + e.getStackTrace()[0].getLineNumber() + " - " + e);
            return false;
        }
        return false;
    }



    //--STEP TWO.one
    public EntityResponse createDemandForReducingBalance(Loan loan) {
        try {
            log.info("check 3");
            //acid
//            Account account=accountRepository.findByLoan(loan).get();
            String acid= loan.getAccount().getAcid();
//            String acid= account.getAcid();
            if(wasPreviousDemandSatisfactionCaller1(acid)) {
                List<LoanSchedule> schedules=loanScheduleRepo.findByLoan(loan);
                final LoanSchedule[] affectedSchedule = new LoanSchedule[1];
                LocalDate loanNxtRepaymentDate= datesCalculator.convertDateToLocalDate(loan.getNextRepaymentDate());
                schedules.forEach(schedule->{
                    LocalDate scheduleDemandDate= schedule.getDemandDate();
                    if(Math.abs(datesCalculator.getDaysDifference(loanNxtRepaymentDate, scheduleDemandDate)) < 20) {
                        affectedSchedule[0] =schedule;
                    }
                });

                if(affectedSchedule[0]!=null || loan.getSumPrincipalDemand()>=(loan.getPrincipalAmount()-5)) {

                    //get product code and loan currency
                    String productCode= loan.getAccount().getProductCode();
                    String loanCurrency=loan.getAccount().getCurrency();
                    //get debit and credit acids, debit= loan ac cr= in receivable
                    String drAcid=loan.getAccount().getAcid();
                    //get in recevable ac from product
                    EntityResponse entityResponse= itemServiceCaller.getGeneralProductDetail1(productCode,
                            CONSTANTS.LOAN_ACCOUNT);
                    if(entityResponse.getStatusCode().equals(HttpStatus.OK.value())){
                        GeneralProductDetails newGeneralProductDetails= (GeneralProductDetails) entityResponse.getEntity();
                        String crAcid=newGeneralProductDetails.getInt_receivable_ac();
                        //validate both acs
                        EntityResponse drAcValidator=validatorsService.acidValidator(drAcid, "Loan");
                        if(drAcValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                            return drAcValidator;
                        }else {
                            EntityResponse crAcValidator=validatorsService.acidValidator(crAcid, "Interest recievable");
                            if(crAcValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                                return crAcValidator;
                            }else {
                                //get loan schedule id
                                //calculate the interest to be paid
                                //calculated from the loan remaining balance
                                //get the total loan balance
                                log.info("***Calculating interest to be demanded ******");
                                Double totalLoanBalance = loan.getTotalLoanBalance();
                                Double interestRate = loan.getInterestRate(); //p.a

//                                Double interestToBePaid= (totalLoanBalance*((interestRate/100)/12));
//                                Double interestDemand=(loan.getPrincipalAmount()*((loan.getCurrentRate()/100)/12));
                                Double interestToBePaid= (totalLoanBalance*((loan.getCurrentRate()/100)/12));
                                interestToBePaid=roundOff2(interestToBePaid, 2);

                                Double newLoanBalance=totalLoanBalance+interestToBePaid;
                                newLoanBalance=roundOff2(newLoanBalance, 2);

                                String transactionDescription= "Interest due "+datesCalculator.dateFormat(loanNxtRepaymentDate)+" "+acid;

                                TransactionHeader tranHeader= createTransactionHeader(loanCurrency,
                                        transactionDescription,
                                        interestToBePaid,
                                        drAcid,
                                        crAcid,
                                        "Interest");
                                EntityResponse transactionRes= transactionsController.systemTransaction1(tranHeader).getBody();
                                if(!transactionRes.getStatusCode().equals(HttpStatus.OK.value())){
                                    ///failed transaction
                                    log.error("Failed Transaction "+transactionRes.getMessage());
                                    EntityResponse response = new EntityResponse();
                                    response.setMessage("TRANSACTION ERROR! COULD NOT PERFORM DEMAND GENERATION TRANSACTION FOR LOAN ACCOUNT: "+acid);
                                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                    response.setEntity(tranHeader);
                                    return response;

                                } else {

                                    // check if all the principal demands have been generated
                                    //is so generate interest demand only
                                    String transactionCode= (String) transactionRes.getEntity();
                                    if(loan.getSumPrincipalDemand()>=(loan.getPrincipalAmount()-2)){
                                        log.info("Generating principal demand only");
                                        //create an interest only demand
                                        String demandCode =acidGenerator.generateDemandCode(CONSTANTS.INTEREST_AND_PRINCIPAL_DEMAND);
                                        LoanDemand intDemand =createDemandModel(
                                                demandCode,
                                                acid,
                                                interestToBePaid,
                                                interestToBePaid,
                                                0.0,
                                                CONSTANTS.INTEREST_DEMAND,
                                                loan.getNextRepaymentDate(),
                                                transactionCode,
                                                loan,
                                                0L,
                                                loan.getTotalLoanBalance()+interestToBePaid);
                                        LoanDemand savedDemand=loanDemandRepository.save(intDemand);

//                                        Account account1= accountRepository.findByAccountId(loan.getAccount().getAcid()).get();
                                        Date nextRepaymentDate = datesCalculator.addDate(loan.getNextRepaymentDate(),loan.getFrequencyPeriod(), loan.getFrequencyId());
                                        loan.setNextRepaymentDate(nextRepaymentDate);
                                        loanRepository.updateLoanNextRepaymentDate(nextRepaymentDate, loan.getSn());
//                                        loan.setTotalLoanBalance(newLoanBalance);
                                        loanRepository.updateLoanTotalBalance(newLoanBalance, loan.getSn());
//                                        loanRepository.save(loan);
//                                        account1.setLoan(loan);
                                    }else {
                                        String demandCode =acidGenerator.generateDemandCode(CONSTANTS.INTEREST_AND_PRINCIPAL_DEMAND);
                                        Long loanScheduleId=affectedSchedule[0].getId();
                                        Double principalToBePaid= affectedSchedule[0].getPrincipleAmount();
                                        Double totalInstallment=interestToBePaid+principalToBePaid;

                                        LoanDemand loanDemand = new LoanDemand();
                                        loanDemand.setDemandCode(demandCode);
                                        loanDemand.setAcid(acid);
                                        loanDemand.setDemandAmount(totalInstallment);
                                        loanDemand.setInterestAmount(interestToBePaid);
                                        loanDemand.setPrincipalAmount(principalToBePaid);
                                        loanDemand.setDemandType(CONSTANTS.INTEREST_AND_PRINCIPAL_DEMAND);
                                        loanDemand.setAdjustmentDate(null);
                                        loanDemand.setAdjustmentAmount(0.0);
                                        loanDemand.setDemandDate(loan.getNextRepaymentDate());
                                        loanDemand.setCreatedBy(CONSTANTS.SYSTEM_USERNAME);
                                        loanDemand.setTransactionCode(transactionCode);
                                        loanDemand.setDemandCarriedFowardFlag(CONSTANTS.NO);
                                        loanDemand.setLoan(loan);
                                        loanDemand.setCreatedOn(new Date());
                                        loanDemand.setLoanScheduleId(loanScheduleId);
                                        loanDemand.setLoanRemainingBalance(newLoanBalance);
                                        LoanDemand savedDemand=loanDemandRepository.save(loanDemand);
//                    //update loan
//                                        Account account1= accountRepository.findByAccountId(loan.getAccount().getAcid()).get();
                                        Date nextRepaymentDate = datesCalculator.addDate(loan.getNextRepaymentDate(),loan.getFrequencyPeriod(), loan.getFrequencyId());
                                        loanRepository.updateLoanNextRepaymentDate(nextRepaymentDate, loan.getSn());
                                        loan.setNextRepaymentDate(nextRepaymentDate);
//                                        loan.setTotalLoanBalance(newLoanBalance);
                                        loanRepository.updateLoanTotalBalance(newLoanBalance, loan.getSn());
//                                        loan.setSumPrincipalDemand(loan.getSumPrincipalDemand()+ principalToBePaid);
                                        Double newSumPrincipalDemand=loan.getSumPrincipalDemand()+ principalToBePaid;
                                        newSumPrincipalDemand=roundOff2(newSumPrincipalDemand, 2);
                                        loanRepository.updateLoanSumPrincipalDemand(newSumPrincipalDemand, loan.getSn());
//                                        loanRepository.save(loan);
//                                        account1.setLoan(loan);
//                                        Account savedAccount=accountRepository.save(account1);

                                        // accrue
                                        Loan newLoan =loanRepository.findById(loan.getSn()).get();

                                        if(loan.getPauseBookingFlag().equals('N')){
                                            //book
//                                            loanBookingService.bookInterestForReducingBalance(
//                                                    newLoan,
//                                                    interestToBePaid,
//                                                    savedDemand.getDemandDate());
                                        }
                                        if(loan.getPauseAccrualFlag().equals('N')){
                                            loanAccrualService.accrueInterestReducingBalance(
                                                    newLoan,
                                                    interestToBePaid,
                                                    savedDemand.getDemandDate());
                                        }
                                    }
                                    log.info("about to generate fee demand");
                                    generateLoanFeeDemand(loan);


                                    EntityResponse response = new EntityResponse();
                                    response.setMessage("SUCCESS");
                                    response.setStatusCode(HttpStatus.CREATED.value());
//                                    response.setEntity("");
                                    return response;
                                }
                            }
                        }

                    }else {
                        return entityResponse;
                    }

                } else {
                    //generate interest demand only
                    EntityResponse response = new EntityResponse();
                    response.setMessage("SCHEDULE ERROR");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity(null);
                    return response;
                }
            } else {
                EntityResponse response = new EntityResponse();
                response.setMessage("PREVIOUS DEMAND SATISFACTION WAS NOT INITIATED");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return response;
            }

        }catch (Exception e){
            log.info("Catched Error {} " + e.getStackTrace()[0].getLineNumber()+" - "+e);
            return null;
        }
    }



    public LoanDemand createDemandModel(String demandCode,
                                        String acid,
                                        Double demandedAmt,
                                        Double interestAmt,
                                        Double principalAmt,
                                        String demandType,
                                        Date demandDate,
                                        String transactionCode,
                                        Loan loan,
                                        Long loanScheduleId,
                                        Double loanRemaingBal) {
        LoanDemand loanDemand = new LoanDemand();
        loanDemand.setDemandCode(demandCode);
        loanDemand.setAcid(acid);
        loanDemand.setDemandAmount(demandedAmt);
        loanDemand.setInterestAmount(interestAmt);
        loanDemand.setPrincipalAmount(principalAmt);
        loanDemand.setDemandType(demandType);
        loanDemand.setAdjustmentDate(null);
        loanDemand.setAdjustmentAmount(0.0);
        loanDemand.setDemandDate(demandDate);
        loanDemand.setCreatedBy(CONSTANTS.SYSTEM_USERNAME);
        loanDemand.setTransactionCode(transactionCode);
        loanDemand.setDemandCarriedFowardFlag(CONSTANTS.NO);
        loanDemand.setLoan(loan);
        loanDemand.setCreatedOn(new Date());
        loanDemand.setLoanScheduleId(loanScheduleId);
        loanDemand.setLoanRemainingBalance(loanRemaingBal);

        return loanDemand;
    }
    public Double roundOff2(Double number, int scale) {
        try {
            BigDecimal bigDecimal= new BigDecimal(number).setScale(scale, RoundingMode.HALF_EVEN);
            return bigDecimal.doubleValue();
        }catch (Exception e){
            log.info("Caught Error {}"+e);
            return null;
        }
    }


    public EntityResponse createDemandForReducingBalance1(Loan loan) {
        try {
            log.info("check 3");
            //acid
//            Account account=accountRepository.findByLoan(loan).get();
            String acid= loan.getAccount().getAcid();
//            String acid= account.getAcid();
            if(wasPreviousDemandSatisfactionCaller2(acid)) {
                List<LoanSchedule> schedules=loanScheduleRepo.findByLoan(loan);
                final LoanSchedule[] affectedSchedule = new LoanSchedule[1];
                LocalDate loanNxtRepaymentDate= datesCalculator.convertDateToLocalDate(loan.getNextRepaymentDate());
                schedules.forEach(schedule->{
                    LocalDate scheduleDemandDate= schedule.getDemandDate();
                    if(Math.abs(datesCalculator.getDaysDifference(loanNxtRepaymentDate, scheduleDemandDate)) < 20) {
                        affectedSchedule[0] =schedule;
                    }
                });

                if(affectedSchedule[0]!=null || loan.getSumPrincipalDemand()>=(loan.getPrincipalAmount()-5)) {

                    //get product code and loan currency
                    String productCode= loan.getAccount().getProductCode();
                    String loanCurrency=loan.getAccount().getCurrency();
                    //get debit and credit acids, debit= loan ac cr= in receivable
                    String drAcid=loan.getAccount().getAcid();
                    //get in recevable ac from product
                    EntityResponse entityResponse= itemServiceCaller.getGeneralProductDetail1(productCode,
                            CONSTANTS.LOAN_ACCOUNT);
                    if(entityResponse.getStatusCode().equals(HttpStatus.OK.value())){
                        GeneralProductDetails newGeneralProductDetails= (GeneralProductDetails) entityResponse.getEntity();
                        String crAcid=newGeneralProductDetails.getInt_receivable_ac();
                        //validate both acs
                        EntityResponse drAcValidator=validatorsService.acidValidator(drAcid, "Loan");
                        if(drAcValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                            return drAcValidator;
                        }else {
                            EntityResponse crAcValidator=validatorsService.acidValidator(crAcid, "Interest recievable");
                            if(crAcValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                                return crAcValidator;
                            }else {
                                //get loan schedule id
                                //calculate the interest to be paid
                                //calculated from the loan remaining balance
                                //get the total loan balance
                                log.info("***Calculating interest to be demanded ******");
                                Double totalLoanBalance = loan.getTotalLoanBalance();
                                Double interestRate = loan.getInterestRate(); //p.a

//                                Double interestToBePaid= (totalLoanBalance*((interestRate/100)/12));
//                                Double interestDemand=(loan.getPrincipalAmount()*((loan.getCurrentRate()/100)/12));
                                Double interestToBePaid= (totalLoanBalance*((loan.getCurrentRate()/100)/12));
                                interestToBePaid=roundOff2(interestToBePaid, 2);

                                Double newLoanBalance=totalLoanBalance+interestToBePaid;
                                newLoanBalance=roundOff2(newLoanBalance, 2);

                                String transactionDescription= "Interest due "+datesCalculator.dateFormat(loanNxtRepaymentDate)+" "+acid;

                                TransactionHeader tranHeader= createTransactionHeader(loanCurrency,
                                        transactionDescription,
                                        interestToBePaid,
                                        drAcid,
                                        crAcid,
                                        "Interest");
                                EntityResponse transactionRes= transactionsController.systemTransaction1(tranHeader).getBody();
                                if(!transactionRes.getStatusCode().equals(HttpStatus.OK.value())){
                                    ///failed transaction
                                    log.error("Failed Transaction "+transactionRes.getMessage());
                                    EntityResponse response = new EntityResponse();
                                    response.setMessage("TRANSACTION ERROR! COULD NOT PERFORM DEMAND GENERATION TRANSACTION FOR LOAN ACCOUNT: "+acid);
                                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                    response.setEntity(tranHeader);
                                    return response;

                                } else {

                                    // check if all the principal demands have been generated
                                    //is so generate interest demand only
                                    String transactionCode= (String) transactionRes.getEntity();
                                    if(loan.getSumPrincipalDemand()>=(loan.getPrincipalAmount()-2)){
                                        log.info("Generating principal demand only");
                                        //create an interest only demand
                                        String demandCode =acidGenerator.generateDemandCode(CONSTANTS.INTEREST_AND_PRINCIPAL_DEMAND);
                                        LoanDemand intDemand =createDemandModel(
                                                demandCode,
                                                acid,
                                                interestToBePaid,
                                                interestToBePaid,
                                                0.0,
                                                CONSTANTS.INTEREST_DEMAND,
                                                loan.getNextRepaymentDate(),
                                                transactionCode,
                                                loan,
                                                0L,
                                                loan.getTotalLoanBalance()+interestToBePaid);
                                        LoanDemand savedDemand=loanDemandRepository.save(intDemand);

//                                        Account account1= accountRepository.findByAccountId(loan.getAccount().getAcid()).get();
                                        Date nextRepaymentDate = datesCalculator.addDate(loan.getNextRepaymentDate(),loan.getFrequencyPeriod(), loan.getFrequencyId());
                                        loan.setNextRepaymentDate(nextRepaymentDate);
                                        loanRepository.updateLoanNextRepaymentDate(nextRepaymentDate, loan.getSn());
//                                        loan.setTotalLoanBalance(newLoanBalance);
                                        loanRepository.updateLoanTotalBalance(newLoanBalance, loan.getSn());
//                                        loanRepository.save(loan);
//                                        account1.setLoan(loan);
                                    }else {
                                        String demandCode =acidGenerator.generateDemandCode(CONSTANTS.INTEREST_AND_PRINCIPAL_DEMAND);
                                        Long loanScheduleId=affectedSchedule[0].getId();
                                        Double principalToBePaid= affectedSchedule[0].getPrincipleAmount();
                                        Double totalInstallment=interestToBePaid+principalToBePaid;

                                        LoanDemand loanDemand = new LoanDemand();
                                        loanDemand.setDemandCode(demandCode);
                                        loanDemand.setAcid(acid);
                                        loanDemand.setDemandAmount(totalInstallment);
                                        loanDemand.setInterestAmount(interestToBePaid);
                                        loanDemand.setPrincipalAmount(principalToBePaid);
                                        loanDemand.setDemandType(CONSTANTS.INTEREST_AND_PRINCIPAL_DEMAND);
                                        loanDemand.setAdjustmentDate(null);
                                        loanDemand.setAdjustmentAmount(0.0);
                                        loanDemand.setDemandDate(loan.getNextRepaymentDate());
                                        loanDemand.setCreatedBy(CONSTANTS.SYSTEM_USERNAME);
                                        loanDemand.setTransactionCode(transactionCode);
                                        loanDemand.setDemandCarriedFowardFlag(CONSTANTS.NO);
                                        loanDemand.setLoan(loan);
                                        loanDemand.setCreatedOn(new Date());
                                        loanDemand.setLoanScheduleId(loanScheduleId);
                                        loanDemand.setLoanRemainingBalance(newLoanBalance);
                                        LoanDemand savedDemand=loanDemandRepository.save(loanDemand);
//                    //update loan
//                                        Account account1= accountRepository.findByAccountId(loan.getAccount().getAcid()).get();
                                        Date nextRepaymentDate = datesCalculator.addDate(loan.getNextRepaymentDate(),loan.getFrequencyPeriod(), loan.getFrequencyId());
                                        loanRepository.updateLoanNextRepaymentDate(nextRepaymentDate, loan.getSn());
                                        loan.setNextRepaymentDate(nextRepaymentDate);
//                                        loan.setTotalLoanBalance(newLoanBalance);
                                        loanRepository.updateLoanTotalBalance(newLoanBalance, loan.getSn());
//                                        loan.setSumPrincipalDemand(loan.getSumPrincipalDemand()+ principalToBePaid);
                                        Double newSumPrincipalDemand=loan.getSumPrincipalDemand()+ principalToBePaid;
                                        newSumPrincipalDemand=roundOff2(newSumPrincipalDemand, 2);
                                        loanRepository.updateLoanSumPrincipalDemand(newSumPrincipalDemand, loan.getSn());
//                                        loanRepository.save(loan);
//                                        account1.setLoan(loan);
//                                        Account savedAccount=accountRepository.save(account1);

                                        // accrue
                                        Loan newLoan =loanRepository.findById(loan.getSn()).get();

                                        if(loan.getPauseBookingFlag().equals('N')){
                                            //book
//                                            loanBookingService.bookInterestForReducingBalance(
//                                                    newLoan,
//                                                    interestToBePaid,
//                                                    savedDemand.getDemandDate());
                                        }
                                        if(loan.getPauseAccrualFlag().equals('N')){
                                            loanAccrualService.accrueInterestReducingBalance(
                                                    newLoan,
                                                    interestToBePaid,
                                                    savedDemand.getDemandDate());
                                        }
                                    }
                                    log.info("about to generate fee demand");
                                    generateLoanFeeDemand(loan);


                                    EntityResponse response = new EntityResponse();
                                    response.setMessage("SUCCESS");
                                    response.setStatusCode(HttpStatus.CREATED.value());
//                                    response.setEntity("");
                                    return response;
                                }
                            }
                        }

                    }else {
                        return entityResponse;
                    }

                } else {
                    //generate interest demand only
                    EntityResponse response = new EntityResponse();
                    response.setMessage("SCHEDULE ERROR");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity(null);
                    return response;
                }
            } else {
                EntityResponse response = new EntityResponse();
                response.setMessage("PREVIOUS DEMAND SATISFACTION WAS NOT INITIATED");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return response;
            }

        }catch (Exception e){
            log.info("Catched Error {} " + e.getStackTrace()[0].getLineNumber()+" - "+e);
            return null;
        }
    }




//    public boolean wasPreviousDemandSatisfactionCaller(String acid,String demandType){
//        try{
//            Optional<LoanDemand> loanDemand=loanDemandRepository.getLastLoanDemand(acid, demandType);
//            if(loanDemand.isPresent()){
//                Character satisfactionCaller= loanDemand.get().getSatisfactionCallerFlag();
//                if(satisfactionCaller.equals(CONSTANTS.YES)) {
//                    return true;
//                }else {
//                    return false;
//                }
//            }else {
//                return true;
//            }
//        }catch (Exception e){
//            log.info("Catched Error {} " + e.getStackTrace()[0].getLineNumber()+" - "+e);
//            return false;
//        }
//    }

    public boolean wasPreviousDemandSatisfactionCaller(String acid, String demandType) {
        try {
            // Get the asset classification for the loan
            Loan loan= accountRepository.findByAcid(acid).get().getLoan();
            String assetClassification=loanRepository.getAssetClassification(loan.getSn());

            System.out.println("Classification: "+assetClassification);

            // Check if the asset classification is "PERFORMING"
            if (assetClassification != null && assetClassification.equals(CONSTANTS.PERFORMING)) {

                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.info("Caught Error at line " + e.getStackTrace()[0].getLineNumber() + " - " + e);
            return false;
        }
    }


    @CacheEvict(value = "first", allEntries = true)
    public List<EntityResponse> generateLoanFeeDemand(Loan loan1) {
        try{
            Loan loan=loan1;
            List<EntityResponse> entityResponseList= new ArrayList<>();
            if(wasPreviousDemandSatisfactionCaller(loan.getAccount().getAcid(), CONSTANTS.FEE_DEMAND)) {
                //get all the monthly fees that are attached to a loan
                Long loanSn= loan.getSn();
                List<LoanFees> loanMonthlyFees=loanFeesRepo.findByAcountId(loanSn);
                log.info("Monthy fee size is"+loanMonthlyFees.size());
                if(loanMonthlyFees.size()>0){
                    //for each fee create a transaction
                    //dr loan account
                    // credit fee collection account
                    // if transaction is successful create a fee demand
                    for(Integer i=0;i<loanMonthlyFees.size();i++) {
                        System.out.println("Found a fee to demand");
//                        log.info("kkkk");
                        System.out.println(+loanMonthlyFees.get(i).getInitialAmt());
//                        log.info("22222");
                        //cr ac = fee collection acount
                        String crAc=loanMonthlyFees.get(i).getChargeCollectionAccount();
//                        log.info("3333");

                        Double feeAmount= loanMonthlyFees.get(i).getMonthlyAmount();
                        String drAc= loan.getAccount().getAcid();
                        String loanCurrency= loan.getAccount().getCurrency();

                        //convert date to
                        Date demandDate=loanMonthlyFees.get(i).getNextCollectionDate();

                        LoanFees loanFees1= loanMonthlyFees.get(i);

                        //validate cr Ac
                        log.info("getting fee receivable account");
                        EntityResponse crAcValidator=validatorsService.acidValidator(crAc, "Fees receivable account");
                        log.info("service caller response status code is "+crAcValidator.getStatusCode());
                        log.info("service caller response status code is "+crAcValidator.getMessage());
                        if(crAcValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                            log.error("Fees receivable account error "+crAcValidator.getMessage());
                            entityResponseList.add(crAcValidator);
                        }else {
                            //perform transaction;
                            System.out.println("To perform a transaction");
                            if (demandDate.compareTo(new Date()) < 0 ) {
                                String transactionDescription="FEE COLLECTION FOR LOAN ACCOUNT:  "+drAc;
                                System.out.println(transactionDescription);
                                TransactionHeader tranHeader= createTransactionHeader(loanCurrency,
                                        transactionDescription,
                                        feeAmount,
                                        drAc,
                                        crAc,
                                        "Fee");
                                System.out.println(tranHeader);
                                EntityResponse transactionRes= transactionsController.systemTransaction1(tranHeader).getBody();
                                if(!transactionRes.getStatusCode().equals(HttpStatus.OK.value())) {
                                    ///failed transactiom
                                    log.error("Failed fee demand transaction "+transactionRes.getMessage());
                                    EntityResponse response = new EntityResponse();
                                    response.setMessage("TRANSACTION ERROR! COULD NOT PERFORM FEE DEMAND GENERATION TRANSACTION FOR LOAN ACCOUNT: "+drAc);
                                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                    response.setEntity(tranHeader);
                                    entityResponseList.add(response);
                                }else {
                                    //update fee
                                    String transactionCode= (String) transactionRes.getEntity();
                                    Date nextCollectionDate= datesCalculator.addDate(demandDate,1, "MONTHS");
                                    loanFeesRepo.updateNextCollectionDate(nextCollectionDate,loanFees1.getId());
                                    //create a demand model and save
                                    Double totalLoanBal= loanRepository.getLoanTotalBalance(loan.getSn());
                                    Double newBalance=totalLoanBal+feeAmount;
                                    LoanDemand loanDemand= createLoanDemandModel(drAc,
                                            loan,
                                            feeAmount,
                                            transactionCode,
                                            newBalance,
                                            demandDate);

                                    loanDemandRepository.save(loanDemand);

                                    Double sumMonthlyFeeDemand= loanRepository.getSumMonthlyFeeDemand(loan.getSn());
                                    loanRepository.updateLoanSumMonthlyFeeDemand(sumMonthlyFeeDemand+feeAmount, loan.getSn());
                                    loanRepository.updateLoanBalance(newBalance, loan.getSn());
                                    loan=loanRepository.findByAcid(drAc).get();

                                    EntityResponse response = new EntityResponse();
                                    response.setMessage("FEE DEMAND CREATED SUCCESSFULLY");
                                    response.setStatusCode(HttpStatus.CREATED.value());
                                    response.setStatusCode(HttpStatus.CREATED.value());
                                    response.setEntity("");
                                    entityResponseList.add(response);
                                }
                            }else{
                                System.out.println("Fees date not yet");
                            }
                        }
                    }

                }else {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("NO MONTHLY FEES FOR THIS ACCOUNT");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity(null);

                }
            }else {
                EntityResponse response = new EntityResponse();
                response.setMessage("PREVIOUS DEMAND SATISFACTION WAS NOT INITIATED");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                entityResponseList.add(response);
            }

            return entityResponseList;
        }catch (Exception e){
            log.info("Catched Error {} " + e.getStackTrace()[0].getLineNumber()+" - "+e);
            return null;
        }
    }


    public LoanDemand createLoanDemandModel(String acid,
                                            Loan loan,
                                            Double feeAmount,
                                            String transactionCode,
                                            Double loanBalance,
                                            Date demandDate){
        try{

            String demandCode =acidGenerator.generateDemandCode(CONSTANTS.FEE_DEMAND);
            LoanDemand loanDemand = new LoanDemand();
            loanDemand.setDemandCode(demandCode);
            loanDemand.setAcid(acid);
            loanDemand.setDemandAmount(feeAmount);
            loanDemand.setInterestAmount(0.0);
            loanDemand.setPrincipalAmount(0.0);
            loanDemand.setDemandType(CONSTANTS.FEE_DEMAND);
            loanDemand.setAdjustmentDate(null);
            loanDemand.setAdjustmentAmount(0.0);
            loanDemand.setDemandDate(demandDate);
            loanDemand.setCreatedBy(CONSTANTS.SYSTEM_USERNAME);
            loanDemand.setTransactionCode(transactionCode);
            loanDemand.setDemandCarriedFowardFlag(CONSTANTS.NO);
            loanDemand.setLoan(loan);
            loanDemand.setCreatedOn(new Date());
            loanDemand.setLoanScheduleId(0L);
            loanDemand.setLoanRemainingBalance(loanBalance);
            loanDemand.setFeeAmount(feeAmount);
            loanDemand.setPenalInterestAmount(0.0);
            LoanDemand sLoanDemand=loanDemandRepository.save(loanDemand);

            return loanDemand;
        }catch (Exception e){
            log.info("Catched Error {} " + e.getStackTrace()[0].getLineNumber()+" - "+e);
            return null;
        }
    }




    public TransactionHeader createTransactionHeader(String loanCurrency,
                                                     String transDesc,
                                                     Double totalAmount,
                                                     String drAc,
                                                     String crAc,
                                                     String partTranIdentity) {
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
        drPartTran.setParttranIdentity(partTranIdentity);
        drPartTran.setTransactionDate(new Date());
        drPartTran.setTransactionParticulars(transDesc);
        drPartTran.setIsoFlag(CONSTANTS.YES);

        PartTran crPartTran = new PartTran();
        crPartTran.setPartTranType(CONSTANTS.CREDITSTRING);
        crPartTran.setTransactionAmount(totalAmount);
        crPartTran.setAcid(crAc);
        crPartTran.setCurrency(loanCurrency);
        crPartTran.setExchangeRate("");
        drPartTran.setParttranIdentity(partTranIdentity);
        crPartTran.setTransactionDate(new Date());
        crPartTran.setTransactionParticulars(transDesc);
        crPartTran.setIsoFlag(CONSTANTS.YES);

        List<PartTran> partTranList =new ArrayList<>();
        partTranList.add(drPartTran);
        partTranList.add(crPartTran);

        transactionHeader.setPartTrans(partTranList);
        return transactionHeader;
    }

    //==============STEP TWO. two
    public EntityResponse createDemandByForFlatRate(Loan loan) {
        try {
            //acid
            System.out.println("---------Flat Rate--------");
            String acid= loan.getAccount().getAcid();
            if(wasPreviousDemandSatisfactionCaller1(acid)){
                List<LoanSchedule> schedules=loanScheduleRepo.findByLoan(loan);
                //get the affected schedule
                final LoanSchedule[] affectedSchedule = new LoanSchedule[1];
                LocalDate loanNxtRepaymentDate= datesCalculator.convertDateToLocalDate(loan.getNextRepaymentDate());
                schedules.forEach(schedule->{
                    LocalDate scheduleDemandDate= schedule.getDemandDate();
                    int daysDifference = (int) Math.abs(datesCalculator.getDaysDifference(loanNxtRepaymentDate, scheduleDemandDate));
                    System.out.println("Next repayment: "+datesCalculator.dateFormat(loanNxtRepaymentDate));
                    System.out.println("Schedule date: "+datesCalculator.dateFormat(scheduleDemandDate));
                    System.out.println("Days difference "+daysDifference);
                    if(Math.abs(datesCalculator.getDaysDifference(loanNxtRepaymentDate, scheduleDemandDate)) < 20) {
                        affectedSchedule[0] =schedule;
                        System.out.println("Found schedule");
                    } else {
                        System.out.println("Schedule not found");
                    }
                });
                if(affectedSchedule[0]!=null || loan.getSumPrincipalDemand()>=(loan.getPrincipalAmount()-5)) {
                    String productCode= loan.getAccount().getProductCode();
                    String loanCurrency=loan.getAccount().getCurrency();
                    String drAcid=loan.getAccount().getAcid();
                    //get in recevable ac from product;
                    EntityResponse entityResponse= itemServiceCaller.getGeneralProductDetail1(
                            productCode,
                            CONSTANTS.LOAN_ACCOUNT);
                    if(entityResponse.getStatusCode().equals(HttpStatus.OK.value())) {
                        GeneralProductDetails newGeneralProductDetails = (GeneralProductDetails) entityResponse.getEntity();
                        if (!newGeneralProductDetails.getProductType().equalsIgnoreCase("LAA")) {
                            System.out.println("Product code is not of loan");
                            return entityResponse;
                        }
                        String crAcid = newGeneralProductDetails.getInt_receivable_ac();
                        //validate both acs
                        EntityResponse drAcValidator=validatorsService.acidValidator(drAcid,
                                "Loan");
                        if(drAcValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                            return drAcValidator;
                        }else {
                            EntityResponse crAcValidator=validatorsService.acidValidator(crAcid,
                                    "Interest recievable");
                            if(crAcValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                                return crAcValidator;
                            }else {
//                                Double interestDemand=(loan.getPrincipalAmount()*((loan.getInterestRate()/100)/12));
                                Double interestDemand=(loan.getPrincipalAmount()*((loan.getCurrentRate()/100)/12));
                                if (loan.getAccount().getProductCode().trim().equalsIgnoreCase("BNS")
                                        || loan.getAccount().getProductCode().trim().equalsIgnoreCase("KAH") ) {
                                    interestDemand = (loan.getPrincipalAmount() * ((loan.getCurrentRate() / 100)));
                                }
                                interestDemand=roundOff2(interestDemand, 2);
                                String transactionDescription=  "Interest due "+datesCalculator.dateFormat(loanNxtRepaymentDate)+" "+acid;
                                TransactionHeader tranHeader= createTransactionHeader(loanCurrency,
                                        transactionDescription,
                                        interestDemand,
                                        drAcid,
                                        crAcid,
                                        "Interest");
                                EntityResponse transactionRes= transactionsController.systemTransaction1(tranHeader).getBody();
                                System.out.println(transactionRes);
                                if(!transactionRes.getStatusCode().equals(HttpStatus.OK.value())){
                                    ///failed transactiom
                                    EntityResponse response = new EntityResponse();
                                    response.setMessage("TRANSACTION ERROR! COULD NOT PERFORM DEMAND GENERATION TRANSACTION FOR LOAN ACCOUNT: "+acid);
                                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                    response.setEntity(tranHeader);
                                    return response;

                                } else {
                                    Double currentLoanBalance=loanRepository.getLoanTotalBalance(loan.getSn());
                                    Double newLoanBalance=currentLoanBalance+interestDemand;
                                    newLoanBalance=roundOff2(newLoanBalance, 2);
                                    // check if all the principal demands have been generated
                                    //if so generate interest demand only
                                    String transactionCode= (String) transactionRes.getEntity();
                                    if(loan.getSumPrincipalDemand()>=(loan.getPrincipalAmount()-2)){
                                        //create an interest only demand
                                        String demandCode =acidGenerator.generateDemandCode(CONSTANTS.INTEREST_DEMAND);
                                        LoanDemand intDemand =createDemandModel(demandCode,
                                                acid,
                                                interestDemand,
                                                interestDemand,
                                                0.0,
                                                CONSTANTS.INTEREST_DEMAND,
                                                loan.getNextRepaymentDate(),
                                                transactionCode,
                                                loan,
                                                0L,
                                                newLoanBalance);
                                        System.out.println(interestDemand);
                                        LoanDemand savedDemand=loanDemandRepository.save(intDemand);

                                        Account account1= accountRepository.findByAccountId(loan.getAccount().getAcid()).get();
                                        Date nextRepaymentDate = datesCalculator.addDate(loan.getNextRepaymentDate(),loan.getFrequencyPeriod(), loan.getFrequencyId());
                                        System.out.println(loan.getAccount().getProductCode());
                                        System.out.println(loan.getNextRepaymentDate()+" next =>   "+nextRepaymentDate);
                                        loan.setNextRepaymentDate(nextRepaymentDate);
                                        loanRepository.updateLoanNextRepaymentDate(nextRepaymentDate, loan.getSn());
//                                        loan.setTotalLoanBalance(newLoanBalance);
                                        loanRepository.updateLoanTotalBalance(newLoanBalance, loan.getSn());
//                                        loanRepository.save(loan);
//                                        account1.setLoan(loan);
                                    } else {
                                        LoanSchedule loanSchedule=affectedSchedule[0];
                                        Long loanScheduleId=loanSchedule.getId();
                                        String demandCode =acidGenerator.generateDemandCode(CONSTANTS.INTEREST_AND_PRINCIPAL_DEMAND);
                                        LoanDemand loanDemand = new LoanDemand();
                                        loanDemand.setDemandCode(demandCode);
                                        loanDemand.setAcid(acid);
                                        loanDemand.setDemandAmount(loanSchedule.getInterestAmount()+loanSchedule.getPrincipleAmount());
                                        loanDemand.setInterestAmount(loanSchedule.getInterestAmount());
                                        loanDemand.setPrincipalAmount(loanSchedule.getPrincipleAmount());
                                        loanDemand.setDemandType(CONSTANTS.INTEREST_AND_PRINCIPAL_DEMAND);
                                        loanDemand.setAdjustmentDate(null);
                                        loanDemand.setAdjustmentAmount(0.0);
                                        loanDemand.setDemandDate(loan.getNextRepaymentDate());
                                        loanDemand.setCreatedBy(CONSTANTS.SYSTEM_USERNAME);
                                        loanDemand.setTransactionCode(transactionCode);
                                        loanDemand.setDemandCarriedFowardFlag(CONSTANTS.NO);
                                        loanDemand.setLoan(loan);
                                        loanDemand.setCreatedOn(new Date());
                                        loanDemand.setLoanScheduleId(loanScheduleId);
                                        loanDemand.setLoanRemainingBalance(newLoanBalance);
                                        LoanDemand savedDemand=loanDemandRepository.save(loanDemand);

//
//                    //update loan;
//                                        loanRepository.save(loan);
                                        Date nextRepaymentDate = datesCalculator.addDate(loan.getNextRepaymentDate(),loan.getFrequencyPeriod(), loan.getFrequencyId());

                                        loanRepository.updateLoanNextRepaymentDate(nextRepaymentDate, loan.getSn());
                                        loan.setNextRepaymentDate(nextRepaymentDate);
//                                        loan.setTotalLoanBalance(newLoanBalance);
                                        loanRepository.updateLoanTotalBalance(newLoanBalance, loan.getSn());
//                                        loan.setSumPrincipalDemand(loan.getSumPrincipalDemand()+ principalToBePaid);
                                        Double newSumPrincipalDemand =loan.getSumPrincipalDemand()+ loanSchedule.getPrincipleAmount();
                                        newSumPrincipalDemand=roundOff2(newSumPrincipalDemand, 2);
                                        loanRepository.updateLoanSumPrincipalDemand(newSumPrincipalDemand, loan.getSn());
//                                        loanRepository.save(loan);
//                                        account1.setLoan(loan);
//                                        Account savedAccount=accountRepository.save(account1);
                                    }

                                    log.info("about to generate fee demand");
                                    generateLoanFeeDemand(loan);

                                    //create interest demand only
                                    //The demanded interest should be credited to suspended interest account

                                    EntityResponse response = new EntityResponse();
                                    response.setMessage("DEMAND CREATED SUCCESSFULLY");
                                    response.setStatusCode(HttpStatus.CREATED.value());
                                    response.setEntity("");
                                    return response;
                                }
                            }
                        }
                    } else {
                        return entityResponse;
                    }
                } else {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("SCHEDULE ERROR");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity(null);
                    return response;
                }
            }else {
                EntityResponse response = new EntityResponse();
                response.setMessage("PREVIOUS DEMAND SATISFACTION WAS NOT INITIATED");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return response;
            }
        }catch (Exception e){
            log.info("Catched Error {} " + e.getStackTrace()[0].getLineNumber()+" - "+e);
//            log.info("Catched Error {} " + e.getStackTrace()[0].getLineNumber()+" - "+e);
            return null;
        }
    }


    public EntityResponse createDemandByForFlatRate1(Loan loan) {
        try {
            //acid
            System.out.println("---------Flat Rate- inside side-------");
            String acid= loan.getAccount().getAcid();
            if(wasPreviousDemandSatisfactionCaller2(acid)){
                List<LoanSchedule> schedules=loanScheduleRepo.findByLoan(loan);
                //get the affected schedule
                final LoanSchedule[] affectedSchedule = new LoanSchedule[1];
                LocalDate loanNxtRepaymentDate= datesCalculator.convertDateToLocalDate(loan.getNextRepaymentDate());
                schedules.forEach(schedule->{
                    LocalDate scheduleDemandDate= schedule.getDemandDate();
                    int daysDifference = (int) Math.abs(datesCalculator.getDaysDifference(loanNxtRepaymentDate, scheduleDemandDate));
                    System.out.println("Next repayment: "+datesCalculator.dateFormat(loanNxtRepaymentDate));
                    System.out.println("Schedule date: "+datesCalculator.dateFormat(scheduleDemandDate));
                    System.out.println("Days difference "+daysDifference);
                    if(Math.abs(datesCalculator.getDaysDifference(loanNxtRepaymentDate, scheduleDemandDate)) < 20) {
                        affectedSchedule[0] =schedule;
                        System.out.println("Found schedule");
                    } else {
                        System.out.println("Schedule not found");
                    }
                });
                if(affectedSchedule[0]!=null || loan.getSumPrincipalDemand()>=(loan.getPrincipalAmount()-5)) {
                    String productCode= loan.getAccount().getProductCode();
                    String loanCurrency=loan.getAccount().getCurrency();
                    String drAcid=loan.getAccount().getAcid();
                    //get in recevable ac from product;
                    EntityResponse entityResponse= itemServiceCaller.getGeneralProductDetail1(
                            productCode,
                            CONSTANTS.LOAN_ACCOUNT);
                    if(entityResponse.getStatusCode().equals(HttpStatus.OK.value())) {
                        GeneralProductDetails newGeneralProductDetails = (GeneralProductDetails) entityResponse.getEntity();
                        if (!newGeneralProductDetails.getProductType().equalsIgnoreCase("LAA")) {
                            System.out.println("Product code is not of loan");
                            return entityResponse;
                        }
                        String crAcid = newGeneralProductDetails.getInt_receivable_ac();
                        //validate both acs
                        EntityResponse drAcValidator=validatorsService.acidValidator(drAcid,
                                "Loan");
                        if(drAcValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                            return drAcValidator;
                        }else {
                            EntityResponse crAcValidator=validatorsService.acidValidator(crAcid,
                                    "Interest recievable");
                            if(crAcValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                                return crAcValidator;
                            }else {
//                                Double interestDemand=(loan.getPrincipalAmount()*((loan.getInterestRate()/100)/12));
                                Double interestDemand=(loan.getPrincipalAmount()*((loan.getCurrentRate()/100)/12));
                                if (loan.getAccount().getProductCode().trim().equalsIgnoreCase("BNS")
                                        || loan.getAccount().getProductCode().trim().equalsIgnoreCase("KAH") ) {
                                    interestDemand = (loan.getPrincipalAmount() * ((loan.getCurrentRate() / 100)));
                                }
                                interestDemand=roundOff2(interestDemand, 2);
                                String transactionDescription=  "Interest due "+datesCalculator.dateFormat(loanNxtRepaymentDate)+" "+acid;
                                TransactionHeader tranHeader= createTransactionHeader(loanCurrency,
                                        transactionDescription,
                                        interestDemand,
                                        drAcid,
                                        crAcid,
                                        "Interest");
                                EntityResponse transactionRes= transactionsController.systemTransaction1(tranHeader).getBody();
                                System.out.println(transactionRes);
                                if(!transactionRes.getStatusCode().equals(HttpStatus.OK.value())){
                                    ///failed transactiom
                                    EntityResponse response = new EntityResponse();
                                    response.setMessage("TRANSACTION ERROR! COULD NOT PERFORM DEMAND GENERATION TRANSACTION FOR LOAN ACCOUNT: "+acid);
                                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                    response.setEntity(tranHeader);
                                    return response;

                                } else {
                                    Double currentLoanBalance=loanRepository.getLoanTotalBalance(loan.getSn());
                                    Double newLoanBalance=currentLoanBalance+interestDemand;
                                    newLoanBalance=roundOff2(newLoanBalance, 2);
                                    // check if all the principal demands have been generated
                                    //if so generate interest demand only
                                    String transactionCode= (String) transactionRes.getEntity();
                                    if(loan.getSumPrincipalDemand()>=(loan.getPrincipalAmount()-2)){
                                        //create an interest only demand
                                        String demandCode =acidGenerator.generateDemandCode(CONSTANTS.INTEREST_DEMAND);
                                        LoanDemand intDemand =createDemandModel(demandCode,
                                                acid,
                                                interestDemand,
                                                interestDemand,
                                                0.0,
                                                CONSTANTS.INTEREST_DEMAND,
                                                loan.getNextRepaymentDate(),
                                                transactionCode,
                                                loan,
                                                0L,
                                                newLoanBalance);
                                        System.out.println(interestDemand);
                                        LoanDemand savedDemand=loanDemandRepository.save(intDemand);

                                        Account account1= accountRepository.findByAccountId(loan.getAccount().getAcid()).get();
                                        Date nextRepaymentDate = datesCalculator.addDate(loan.getNextRepaymentDate(),loan.getFrequencyPeriod(), loan.getFrequencyId());
                                        System.out.println(loan.getAccount().getProductCode());
                                        System.out.println(loan.getNextRepaymentDate()+" next =>   "+nextRepaymentDate);
                                        loan.setNextRepaymentDate(nextRepaymentDate);
                                        loanRepository.updateLoanNextRepaymentDate(nextRepaymentDate, loan.getSn());
//                                        loan.setTotalLoanBalance(newLoanBalance);
                                        loanRepository.updateLoanTotalBalance(newLoanBalance, loan.getSn());
//                                        loanRepository.save(loan);
//                                        account1.setLoan(loan);
                                    } else {
                                        LoanSchedule loanSchedule=affectedSchedule[0];
                                        Long loanScheduleId=loanSchedule.getId();
                                        String demandCode =acidGenerator.generateDemandCode(CONSTANTS.INTEREST_AND_PRINCIPAL_DEMAND);
                                        LoanDemand loanDemand = new LoanDemand();
                                        loanDemand.setDemandCode(demandCode);
                                        loanDemand.setAcid(acid);
                                        loanDemand.setDemandAmount(loanSchedule.getInterestAmount()+loanSchedule.getPrincipleAmount());
                                        loanDemand.setInterestAmount(loanSchedule.getInterestAmount());
                                        loanDemand.setPrincipalAmount(loanSchedule.getPrincipleAmount());
                                        loanDemand.setDemandType(CONSTANTS.INTEREST_AND_PRINCIPAL_DEMAND);
                                        loanDemand.setAdjustmentDate(null);
                                        loanDemand.setAdjustmentAmount(0.0);
                                        loanDemand.setDemandDate(loan.getNextRepaymentDate());
                                        loanDemand.setCreatedBy(CONSTANTS.SYSTEM_USERNAME);
                                        loanDemand.setTransactionCode(transactionCode);
                                        loanDemand.setDemandCarriedFowardFlag(CONSTANTS.NO);
                                        loanDemand.setLoan(loan);
                                        loanDemand.setCreatedOn(new Date());
                                        loanDemand.setLoanScheduleId(loanScheduleId);
                                        loanDemand.setLoanRemainingBalance(newLoanBalance);
                                        LoanDemand savedDemand=loanDemandRepository.save(loanDemand);

//
//                    //update loan;
//                                        loanRepository.save(loan);
                                        Date nextRepaymentDate = datesCalculator.addDate(loan.getNextRepaymentDate(),loan.getFrequencyPeriod(), loan.getFrequencyId());
                                        loanRepository.updateLoanNextRepaymentDate(nextRepaymentDate, loan.getSn());
                                        loan.setNextRepaymentDate(nextRepaymentDate);
//                                        loan.setTotalLoanBalance(newLoanBalance);
                                        loanRepository.updateLoanTotalBalance(newLoanBalance, loan.getSn());
//                                        loan.setSumPrincipalDemand(loan.getSumPrincipalDemand()+ principalToBePaid);
                                        Double newSumPrincipalDemand =loan.getSumPrincipalDemand()+ loanSchedule.getPrincipleAmount();
                                        newSumPrincipalDemand=roundOff2(newSumPrincipalDemand, 2);
                                        loanRepository.updateLoanSumPrincipalDemand(newSumPrincipalDemand, loan.getSn());
//                                        loanRepository.save(loan);
//                                        account1.setLoan(loan);
//                                        Account savedAccount=accountRepository.save(account1);
                                    }

                                    log.info("about to generate fee demand");
                                    generateLoanFeeDemand(loan);

                                    //create interest demand only
                                    //The demanded interest should be credited to suspended interest account

                                    EntityResponse response = new EntityResponse();
                                    response.setMessage("DEMAND CREATED SUCCESSFULLY");
                                    response.setStatusCode(HttpStatus.CREATED.value());
                                    response.setEntity("");
                                    return response;
                                }
                            }
                        }
                    } else {
                        return entityResponse;
                    }
                } else {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("SCHEDULE ERROR");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity(null);
                    return response;
                }
            }else {
                EntityResponse response = new EntityResponse();
                response.setMessage("PREVIOUS DEMAND SATISFACTION WAS NOT INITIATED");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return response;
            }
        }catch (Exception e){
            log.info("Catched Error {} " + e.getStackTrace()[0].getLineNumber()+" - "+e);
//            log.info("Catched Error {} " + e.getStackTrace()[0].getLineNumber()+" - "+e);
            return null;
        }
    }



}