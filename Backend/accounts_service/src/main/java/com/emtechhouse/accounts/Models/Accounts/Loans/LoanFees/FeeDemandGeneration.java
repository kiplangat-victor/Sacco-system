package com.emtechhouse.accounts.Models.Accounts.Loans.LoanFees;


import com.emtechhouse.accounts.Models.Accounts.AccountRepository;
import com.emtechhouse.accounts.Models.Accounts.AccountServices.AccountTransactionService;
import com.emtechhouse.accounts.Models.Accounts.LienComponent.LienRepository;
import com.emtechhouse.accounts.Models.Accounts.Loans.Loan;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanAccrual.LoanAccrualService;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanBooking.LoanBookingService;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.DemandGenerationService;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.GenerateDemandsForOneLoan;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.LoanDemand;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.LoanDemandRepository;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanRepository;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanSchedule.LoanScheduleRepo;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ProductFees.ProductFeesService;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ProductInterestDetails.ProductInterestService;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ItemServiceCaller;
import com.emtechhouse.accounts.Models.Accounts.Validators.ValidatorService;
import com.emtechhouse.accounts.TransactionService.TransactionUtils;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class FeeDemandGeneration {
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
    private TransactionUtils transactionUtils;

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
    private DemandGenerationService demandGenerationService;
    @Autowired
    private GenerateDemandsForOneLoan generateDemandsForOneLoan;

    @Autowired
    private TransactionsController transactionsController;

    @Autowired
    private ProductFeesService productFeesService;


    private FeeAmountRecur getRecurringFeeAmount(LoanFees loanFees) {
        return productFeesService.getOneProductFeeRecurring(accountRepository.productForLoan(loanFees.getLoan().getSn()),
                loanFees.getLoan().getPrincipalAmount()-loanFees.getLoan().getSumPrincipalDemand(),
                loanFees.getEventIdCode());
    }

    @CacheEvict(value = "first", allEntries = true)
    public List<EntityResponse> generateLoanFeeDemand(Loan loan1) {
        try{
            Loan loan=loan1;
            List<EntityResponse> entityResponseList= new ArrayList<>();
//            if(demandGenerationService.wasPreviousDemandSatisfactionCaller(loan.getAccount().getAcid(), CONSTANTS.FEE_DEMAND)) {
            //get all the monthly fees that are attached to a loan
            Long loanSn= loan.getSn();
            List<LoanFees> loanMonthlyFees=loanFeesRepo.findByAcountId(loanSn);
            if(loanMonthlyFees.size()>0) {
                //for each fee create a transaction
                //dr loan account
                // credit fee collection account
                // if transaction is successful create a fee demand
                for(Integer i=0;i<loanMonthlyFees.size();i++) {
                    System.out.println("Found a fee to demand");
                    System.out.println(loanMonthlyFees.get(i));
                    //cr ac = fee collection acount
                    String crAc=loanMonthlyFees.get(i).getChargeCollectionAccount();
//                        Double feeAmount=

                    FeeAmountRecur feeAmountRecur = getRecurringFeeAmount(loanMonthlyFees.get(i));
                    System.out.println("Fee amount: "+feeAmountRecur.amount);
                    String drAc= loan.getAccount().getAcid();
                    String loanCurrency= loan.getAccount().getCurrency();
                    Date demandDate=loanMonthlyFees.get(i).getNextCollectionDate();

                    LoanFees loanFees1= loanMonthlyFees.get(i);

                    //validate cr Ac
                    EntityResponse crAcValidator=validatorsService.acidValidator(crAc, "Fees receivable account");
                    if(crAcValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                        log.error("Fees receivable account error "+crAcValidator.getMessage());
                        entityResponseList.add(crAcValidator);
                    }else {
                        //perform transaction;
                        System.out.println("To perform a transaction");
                        System.out.println(demandDate);
                        if (demandDate.compareTo(new SimpleDateFormat("dd/MM/yyyy").parse("10/10/2027")) < 0 ) {
                            String transactionDescription="FEE COLLECTION FOR LOAN ACCOUNT:  "+drAc;
                            System.out.println(transactionDescription);
                            TransactionHeader tranHeader= transactionUtils.createTransactionHeader(loanCurrency,
                                    transactionDescription,
                                    feeAmountRecur.amount,
                                    drAc,
                                    crAc);
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
                                Date nextCollectionDate= datesCalculator.addDate(demandDate,1, feeAmountRecur.recurPeriod);
                                loanFeesRepo.updateNextCollectionDate(nextCollectionDate,loanFees1.getId());
                                //create a demand model and save
                                Double totalLoanBal= loanRepository.getLoanTotalBalance(loan.getSn());
                                Double newBalance=totalLoanBal+feeAmountRecur.getAmount();
                                LoanDemand loanDemand= generateDemandsForOneLoan.createLoanDemandModel(drAc,
                                        loan,
                                        feeAmountRecur.amount,
                                        transactionCode,
                                        newBalance,
                                        demandDate);

                                loanDemandRepository.save(loanDemand);

                                Double sumRecurringFeeDemand= loanRepository.getSumRecurringFeeDemand(loan.getSn());
                                loanRepository.updateLoanRecurringFeeDemand(sumRecurringFeeDemand+feeAmountRecur.getAmount(), loan.getSn());
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
//            }else {
//                EntityResponse response = new EntityResponse();
//                response.setMessage("PREVIOUS DEMAND SATISFACTION WAS NOT INITIATED");
//                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                entityResponseList.add(response);
//            }

            return entityResponseList;
        }catch (Exception e){
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public void startDemandingFees() {
        List<LoanFees> loanFees= loanFeesRepo.getFeesToDemand(CONSTANTS.DISBURSED);
        if(loanFees.size()>0) {
            for (LoanFees loanFees1 : loanFees) {
                generateLoanFeeDemand(loanFees1.getLoan());
            }
        }else {
            System.out.println("No fee to demand");
        }
    }
}