package com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.DemandSatisfaction;

import com.emtechhouse.accounts.Models.Accounts.Account;
import com.emtechhouse.accounts.Models.Accounts.AccountClosure.AccountClosureInfo;
import com.emtechhouse.accounts.Models.Accounts.AccountClosure.AccountClosureRepo;
import com.emtechhouse.accounts.Models.Accounts.AccountRepository;
import com.emtechhouse.accounts.Models.Accounts.AccountServices.AccountTransactionService;
import com.emtechhouse.accounts.Models.Accounts.Loans.Loan;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanAccrual.LoanAccrualRepo;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanAccrual.LoanAccrualService;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanBooking.LoanBookingRepo;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanBooking.LoanBookingService;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanCalculatorService;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.DemandGenerationService;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.DemandSatisfaction.InterestCollected.InterestCollected;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.DemandSatisfaction.InterestCollected.InterestCollectedRepo;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.LoanDemand;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.LoanDemandRepository;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanPenalties.LoanPenaltyService;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanRepository;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanSchedule.LoanCustomerPaymentSchedule.LoanCustomerPaymentScheduleRepo;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanSchedule.LoanScheduleRepo;
import com.emtechhouse.accounts.Models.Accounts.SavingContributionInstructions.SavingContributionInstructionsService;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.EmailDto.MailDto;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.GeneralProductDetails;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ItemServiceCaller;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ProductInterestDetails.ProductInterestDetails;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ProductInterestDetails.ProductInterestService;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.SpecificProductDetails.LoanAccountProduct;
import com.emtechhouse.accounts.Models.Accounts.Validators.ValidatorService;
import com.emtechhouse.accounts.Requests.LoanPrepayRequest;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.PartTrans.PartTran;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TransactionHeader;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TransactionsController;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.emtechhouse.accounts.Utils.DatesCalculator;
import com.emtechhouse.accounts.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.mail.Message;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class DemandSatisfactionService {
    @Autowired
    private LoanDemandRepository loanDemandRepository;
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    LoanDemandSatisfactionRepo loanDemandSatisfactionRepo;

    @Autowired
    private LoanScheduleRepo loanScheduleRepo;

    @Autowired
    private LoanCustomerPaymentScheduleRepo loanCustomerPaymentScheduleRepo;

    @Autowired
    private LoanCalculatorService loanCalculatorService;
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ValidatorService validatorsService;
    @Autowired
    private AccountTransactionService accountTransactionService;
    @Autowired
    private DatesCalculator datesCalculator;
    @Autowired
    private DemandGenerationService demandGenerationService;
    @Autowired
    private ItemServiceCaller itemServiceCaller;

    @Autowired
    private SavingContributionInstructionsService savingContributionInstructionsService;

    @Autowired
    private AssetClassificationService assetClassificationService;
    @Autowired
    private TransactionsController transactionsController;

    @Autowired
    private AccountClosureRepo accountClosureRepo;

    @Autowired
    private LoanAccrualRepo loanAccrualRepo;
    @Autowired
    private LoanBookingRepo loanBookingRepo;

    @Autowired
    private LoanAccrualService loanAccrualService;
    @Autowired
    private LoanBookingService loanBookingService;
    @Autowired
    private ProductInterestService productInterestService;
    @Autowired
    private InterestCollectedRepo interestCollectedRepo;

    @Autowired
    private LoanPenaltyService penaltyService;

    public List<EntityResponse> satisfyAllDemands() {
        log.info("demand satisfaction in progress");
        try{
            List<EntityResponse> responses = new ArrayList<>();

            //fetch all unsatisfied demands
            List<LoanDemand> loanDemands = loanDemandRepository.findUnsatisfiedLoanDemands(CONSTANTS.NO);
            if(loanDemands.size()>0){
                System.out.println("to demand");
                List<EntityResponse> entityResponse=satisfyDemandManualForceAll(loanDemands);
                responses.addAll(entityResponse);
//                for (LoanDemand loanToDemand : loanDemands) {
//                    String operativeAcid=loanToDemand.getAcid();
//                    List<EntityResponse> entityResponse= satisfyDemandManualForce(operativeAcid);
//                    responses.addAll(entityResponse);
//                }
            }else {
                System.out.println("THERE ARE NO DEMANDS TO SATISFY");
                EntityResponse response = new EntityResponse();
                response.setMessage("THERE ARE NO DEMANDS TO SATISFY");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                responses.add(response);
            }
            return responses;
        }catch (Exception e) {
            log.info("Catched Error {} line: "+e.getStackTrace()[0].getLineNumber()+" " + e);
            return null;
        }
    }

    //------STEP ONE
    public List<EntityResponse> satisfyDemandManualForce(String acid) {
        try{
            log.info("Satisfying demand force");
            List<EntityResponse> entityResponses=new ArrayList<>();
            List<LoanDemand>loanDemands= loanDemandRepository.findDemandsToSatisfy(acid, CONSTANTS.NO);
            log.info("demands size is :: "+loanDemands.size());
            if(loanDemands.size()>0) {
                loanDemands.forEach(loanDemand -> {
                    Double adjustmentAmount=loanDemand.getAdjustmentAmount();
                    Integer statusCode= HttpStatus.CREATED.value();
                    Double operativeAccountBalance=0.00;
//                    while (!statusCode.equals(HttpStatus.NOT_ACCEPTABLE.value())){
                    if(adjustmentAmount>=loanDemand.getDemandAmount()) {
                        log.info("demand fully satisfied");
                        EntityResponse response = new EntityResponse();
                        response.setMessage("DEMAND FULLY SATISFIED");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        entityResponses.add(response);
//                            break;
                    }else {
                        //check if the demand is being satisfied for the first time...
                        String operativeAcid=loanDemand.getLoan().getOperativeAcountId();
                        operativeAccountBalance=accountRepository.getAvailableBalance(operativeAcid);
                        if(operativeAccountBalance==null){
                            operativeAccountBalance=0.00;
                        }

                        EntityResponse entityResponse1=getAmountToSatisfyDemandWith(loanDemand, operativeAcid,operativeAccountBalance);
                        entityResponses.add(entityResponse1);
                        statusCode=entityResponse1.getStatusCode();


                        Long loanDemandId= loanDemand.getSn();
                        LoanDemand newLoanDemand= loanDemandRepository.findById(loanDemandId).get();
                        adjustmentAmount=newLoanDemand.getAdjustmentAmount();

                        operativeAccountBalance=accountRepository.getAccountBalance(operativeAcid);
                    }
//                    }
                });
            }else {
                EntityResponse response = new EntityResponse();
                response.setMessage("NO DEMANDS FOR THIS ACCOUNT");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                entityResponses.add(response);
            }
            return entityResponses;

        }catch (Exception e){
            log.info("Catched Error {} line: "+e.getStackTrace()[0].getLineNumber()+" " + e);
            return null;
        }
    }

    public List<EntityResponse> satisfyDemandManualForceAll(List<LoanDemand>loanDemands) {
        try{
            log.info("satisfying demands all manual");
            List<EntityResponse> entityResponses=new ArrayList<>();
            if(loanDemands.size()>0) {
                loanDemands.forEach(loanDemand -> {
                    if (loanDemand.getDeletedFlag() == 'Y' || loanDemand.getDeletedFlag() == 'y')
                        return;
                    Double adjustmentAmount=loanDemand.getAdjustmentAmount();
                    Integer statusCode= HttpStatus.CREATED.value();
                    Double operativeAccountBalance=0.00;
                    boolean penaltyGenerated = false; // new boolean variable to track whether a penalty has already been generated for a demand
                    if(adjustmentAmount>=loanDemand.getDemandAmount()) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("DEMAND FULLY SATISFIED");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        entityResponses.add(response);
//                            break;
                    } else {
                        log.info("check one");
                        savingContributionInstructionsService.executeOne(loanDemand.getLoan().getAccount().getCustomerCode());
                        //check if the demand is being satisfied for the first time
                        String operativeAcid=loanDemand.getLoan().getOperativeAcountId();
                        operativeAccountBalance=accountRepository.getAvailableBalance(operativeAcid);
                        System.out.println("Available balance for "+operativeAcid+" Is "+ operativeAccountBalance);
                        if(operativeAccountBalance==null || operativeAccountBalance < 5) {
                            EntityResponse response = new EntityResponse();
                            response.setMessage("Cannot proceed with no money in operative account");
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            entityResponses.add(response);
                            System.out.println("Cannot proceed with no money in operative account");
                            return;
                        }
                        log.info("check two");

                        EntityResponse entityResponse1=getAmountToSatisfyDemandWith(loanDemand,
                                operativeAcid,
                                operativeAccountBalance);
                        entityResponses.add(entityResponse1);
                        statusCode=entityResponse1.getStatusCode();

                        System.out.println(entityResponse1);

                        Long loanDemandId= loanDemand.getSn();
                        LoanDemand newLoanDemand= loanDemandRepository.findById(loanDemandId).get();
                        adjustmentAmount=newLoanDemand.getAdjustmentAmount();

                        operativeAccountBalance=accountRepository.getAccountBalance(operativeAcid);

                        //create penal interest demand if necessary
                        if (statusCode.equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                            // check if a penalty has already been generated for the demand
                            if (!penaltyGenerated && !newLoanDemand.generatePenaltyInterestDemand()) {
//                                    EntityResponse penalRes= penaltyService.createPenalInterestDemand(loanDemand);
//                                    entityResponses.add(penalRes);
                                penaltyGenerated = true;
                            }
                        }
                    }
//                    }
                });
            } else {
                EntityResponse response = new EntityResponse();
                response.setMessage("NO DEMANDS FOR THIS ACCOUNT");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                entityResponses.add(response);
            }
            return entityResponses;

        }catch (Exception e) {
            log.info("Catched Error {} line: "+e.getStackTrace()[0].getLineNumber()+" " + e);
            return null;
        }
    }

    @CacheEvict(value = "first", allEntries = true)
    public EntityResponse getAmountToSatisfyDemandWith(LoanDemand loanDemand,
                                                       String operativeAcid,
                                                       Double operativeAccountBalance) {
        try {
            log.info("saved l d a amount is: 1.2"+loanRepository.getAdjustmentAmount(loanDemand.getSn()));
            String loanAcid=loanDemand.getAcid();
            String loanCurrency=loanDemand.getLoan().getAccount().getCurrency();
            String entity = EntityRequestContext.getCurrentEntityId();
            //get amount demanded
            Double amountDemanded= loanDemand.getDemandAmount()-loanDemand.getAdjustmentAmount();


            log.info("Demanded amt is, "+loanDemand.getDemandAmount());
            log.info("adjusted amount is, "+loanDemand.getAdjustmentAmount());
            log.info("The overflow amount id"+ loanRepository.getOverFlowAmt(loanDemand.getLoan().getSn()));
            //get operative account
            Optional<Account> account = accountRepository.findByAcid(operativeAcid);
            if(account.isPresent()){
                Double amountToTransact=0.0; //drawn from operative account
                Double amountFromOverFlow=0.0; // drawn from overflow amount
                Double amountToSatisfyDemand=0.0; // the total
                Double overFlowAmount=loanRepository.getOverFlowAmt(loanDemand.getLoan().getSn());

                //check if there is overflow amount in the loan
                if(overFlowAmount>0) {
                    log.info("Overflow amount is :: "+overFlowAmount);
                    if (overFlowAmount>=amountDemanded) {
                        log.info("Overflow amount equal to demanded amt :: "+overFlowAmount);
                        amountFromOverFlow=amountDemanded;
                        amountToSatisfyDemand=amountDemanded;
                    }else {
                        amountFromOverFlow=overFlowAmount;
                        Double remainingAmount=amountDemanded-amountFromOverFlow;

                        if(operativeAccountBalance>=remainingAmount) {
                            amountToTransact=remainingAmount;
                        } else if (operativeAccountBalance > 0) {
                            amountToTransact=operativeAccountBalance;
                        } else {
                            amountToTransact=0.0;
                        }
                        amountToSatisfyDemand=amountToTransact+overFlowAmount;
                    }
                }else {
                    //check from operative account balance
                    //get operative account balance
                    if(operativeAccountBalance>=amountDemanded) {
                        amountToTransact=amountDemanded;
                    } else if (operativeAccountBalance > 0) {
                        amountToTransact=operativeAccountBalance;
                    } else {
                        amountToTransact=0.0;
                    }

                    if(amountToTransact>=(-1*loanDemand.getLoan().getAccount().getAccountBalance())) {
                        amountToTransact=(-1*loanDemand.getLoan().getAccount().getAccountBalance());
                    }
                    amountToSatisfyDemand=amountToTransact;

                    System.out.println("Amount to transact "+amountToTransact);
                }

                log.info("amount to satisfy demand=: "+amountToSatisfyDemand);
                log.info("operative account balance=: "+operativeAccountBalance);

                if(loanDemand.getDemandType().equals(CONSTANTS.INTEREST_AND_PRINCIPAL_DEMAND)) {
                    log.info("Demand type: "+loanDemand.getDemandType());
                    if(loanDemand.getSatisfactionCallerFlag().equals(CONSTANTS.NO)) {
                        log.info("check1");
                        if((amountToSatisfyDemand)<=(loanDemand.getDemandAmount()-1)) {
                            //create a penal interest demand
//                            EntityResponse res=createPenalInterestDemandModel(loanDemand,  300.00);
                            EntityResponse penalInterestCheckerRes= applyPenalInterest(loanDemand);
                            Boolean chargePenalInterest=true;
                            if(penalInterestCheckerRes.getStatusCode().equals(HttpStatus.OK.value())){
                                chargePenalInterest= (Boolean) penalInterestCheckerRes.getEntity();
                            }
                            log.info("charge penal interest == ::"+ chargePenalInterest);
                            if(chargePenalInterest.equals(true)) {
                                log.info("initializing penal int demand");
//                                EntityResponse penalRes= penaltyService.createPenalInterestDemand(loanDemand);
//                                if(penalRes.getStatusCode().equals(HttpStatus.CREATED.value())){
//                                    log.info("account bal before updating loan bal is :: "+accountRepository.fetchAccountBalance(loanDemand.getAcid()));
//                                    LoanDemand sLoanDemand= (LoanDemand) penalRes.getEntity();
//                                    Double penalInterestAmount= sLoanDemand.getPenalInterestAmount();
//                                    Loan loan=loanDemand.getLoan();
//                                    Double loanRemaingBalance= loan.getTotalLoanBalance()+penalInterestAmount;
//
//                                    //update loan
//                                    //use native query
//
//                                    loanRepository.updateLoanBalance(loanRemaingBalance, loan.getSn());
//                                    log.info("account bal after updating loan bal is :: "+accountRepository.fetchAccountBalance(loanDemand.getAcid()));
//                                }else {
//
//                                }
                            }
                        }
                    }
                }

//                loanDemandRepository.save(loanDemand);
                //create a transaction
                EntityResponse response = null;
                if(amountToSatisfyDemand>0) {
                    if(amountToTransact>0) {

                        // TODO: 5/5/2023 check amount to pay interest and principal
                        Double amtToPayInt=0.0;
                        Double amtToPayPrincipal=amountToTransact;


                        Double interestBal= loanDemand.getInterestBalance();
                        if(interestBal>0){
                            if(amountToTransact>interestBal){
                                amtToPayInt=interestBal;
                                amtToPayPrincipal=amountToTransact-amtToPayInt;
                            }else {
                                amtToPayInt=amountToTransact;
                                amtToPayPrincipal=0.0;
                            }
                        }else {
                            amtToPayPrincipal=amountToTransact;
                        }
                        // TODO: 5/5/2023 create two partrans in the following function

//                        response = initializeDemandSatisfactionTransaction(loanDemand, operativeAcid, loanAcid,
//                                loanAcid, entity, loanCurrency, amountToSatisfyDemand,  amountToTransact,
//                                amountFromOverFlow);
                        response = initializeDemandSatisfactionTransaction2(loanDemand, operativeAcid, loanAcid,
                                loanAcid, entity, loanCurrency, amountToSatisfyDemand,  amountToTransact,
                                amountFromOverFlow,amtToPayPrincipal,amtToPayInt);
                    } else {
                        response=satisfyDemandFlatRate3(loanDemand, amountToSatisfyDemand, "OVERFLOW");
                    }
                } else {
                    loanDemandRepository.updateSatisfactionCallerFlag(CONSTANTS.YES,loanDemand.getSn());

                    log.info("saved l d a amount is: 1.3"+loanRepository.getAdjustmentAmount(loanDemand.getSn()));
                    //asset classification
                    assetClassificationService.classifyLoan(loanAcid);

                    log.warn("DEMAND NOT SATISFIED DUE TO INSUFFICIENT FUNDS IN OPERATIVE ACCOUNT F");
                    response = new EntityResponse();
                    response.setMessage("DEMAND NOT SATISFIED DUE TO INSUFFICIENT FUNDS IN OPERATIVE ACCOUNT FOR LOAN ACCOUNT: "+loanAcid);
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                }
                return response;


            }else {
                EntityResponse response = new EntityResponse();
                response.setMessage("COULD NOT FIND OPERATIVE ACCOUNT ");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return response;
            }
        }catch (Exception e){
            log.info("Catched Error {} line: "+e.getStackTrace()[0].getLineNumber()+" " + e);
            return null;
        }
    }

    //------STEP THREE
    @CacheEvict(value = "first", allEntries = true)
    public EntityResponse initializeDemandSatisfactionTransaction(LoanDemand loanDemand,
                                                                  String drAc,
                                                                  String crAc,
                                                                  String loanAcid,
                                                                  String entity,
                                                                  String loanCurrency,
                                                                  Double totalAmtToSatisfyDemand,
                                                                  Double transactedAmount,
                                                                  Double overFlowAmount){
        try {
            EntityResponse drAcValidator=validatorsService.acidValidator(drAc, "Customer operative");
            if(drAcValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                return drAcValidator;
            }else {
                EntityResponse crAcValidator=validatorsService.acidValidator(crAc, "Loan");
                if(crAcValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                    return crAcValidator;
                }else {
                    String transactionDescription="\"Loan repayment - demand date: "+loanDemand.getDemandDate().toString();
                    TransactionHeader tranHeader= createTransactionHeader(loanCurrency,
                            transactionDescription,
                            transactedAmount,
                            drAc,
                            crAc,
                            "Normal");
                    EntityResponse transactionRes= transactionsController.systemTransaction1(tranHeader).getBody();
//                    EntityResponse transactionRes=accountTransactionService.createTransaction1(outgoingTransactionDetails);
                    if(!transactionRes.getStatusCode().equals(HttpStatus.OK.value())){
                        ///failed transactiom
                        EntityResponse response = new EntityResponse();
                        response.setMessage("TRANSACTION ERROR! COULD NOT PERFORM DEMAND SATISFACTION TRANSACTION FOR LOAN ACCOUNT: "+loanAcid);
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity(tranHeader);
                        return response;

                    }else {
                        log.info("The overflow amt is:"+loanRepository.getOverFlowAmount(loanDemand.getLoan().getSn()));
                        String transactionCode= (String) transactionRes.getEntity();

                        EntityResponse entityResponse;
                        entityResponse = satisfyDemandReducingBalance2(loanDemand,
                                totalAmtToSatisfyDemand,
                                transactionCode,
                                transactedAmount,
                                overFlowAmount);
                        return entityResponse;
                    }
                }
            }
        }catch (Exception e){
            log.info("Catched Error {} line: "+e.getStackTrace()[0].getLineNumber()+" " + e);
            return null;
        }
    }

    @CacheEvict(value = "first", allEntries = true)
    public EntityResponse initializeDemandSatisfactionTransaction2(LoanDemand loanDemand,
                                                                   String drAc,
                                                                   String crAc,
                                                                   String loanAcid,
                                                                   String entity,
                                                                   String loanCurrency,
                                                                   Double totalAmtToSatisfyDemand,
                                                                   Double transactedAmount,
                                                                   Double overFlowAmount,
                                                                   Double principalAmount,
                                                                   Double intAmt) {
        try {
            EntityResponse drAcValidator=validatorsService.acidValidator(drAc, "Customer operative");
            if(drAcValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                return drAcValidator;
            }else {
                EntityResponse crAcValidator=validatorsService.acidValidator(crAc, "Loan");
                if(crAcValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                    return crAcValidator;
                }else {
                    List<PartTran> partTranList = new ArrayList<>();
                    if(intAmt>0) {
                        // TODO: 5/5/2023 interest partrans
                        String desc= "Interest collection for loan account "+drAc;
                        PartTran drPartTran= createPartTranModel(CONSTANTS.DEBITSTRING, intAmt, drAc, "KES",  desc, "interest");
                        PartTran crPartTran= createPartTranModel(CONSTANTS.CREDITSTRING, intAmt, crAc, "KES",  desc, "interest");

                        partTranList.add(drPartTran);
                        partTranList.add(crPartTran);
                    }

                    if (principalAmount>0) {
                        // TODO: 5/5/2023 principal partrans
                        String desc= "Principal collection for loan account "+drAc;
                        PartTran drPartTran= createPartTranModel(CONSTANTS.DEBITSTRING, principalAmount, drAc, "KES",  desc, "principal");
                        PartTran crPartTran= createPartTranModel(CONSTANTS.CREDITSTRING, principalAmount, crAc, "KES",  desc, "principal");

                        partTranList.add(drPartTran);
                        partTranList.add(crPartTran);
                    }

                    // TODO: 5/5/2023 create a tranheader and combine the two partrans
                    TransactionHeader transactionHeader= createTransactionHeader2("KES",
                            transactedAmount,
                            partTranList);

//                    String transactionDescription="\"Loan repayment - demand date: "+loanDemand.getDemandDate().toString();
//                    TransactionHeader tranHeader= createTransactionHeader(loanCurrency,
//                            transactionDescription,
//                            transactedAmount,
//                            drAc,
//                            crAc);
                    EntityResponse transactionRes= transactionsController.systemTransaction1(transactionHeader).getBody();
//                    EntityResponse transactionRes=accountTransactionService.createTransaction1(outgoingTransactionDetails);
                    if(!transactionRes.getStatusCode().equals(HttpStatus.OK.value())){
                        ///failed transactiom
                        EntityResponse response = new EntityResponse();
                        response.setMessage("TRANSACTION ERROR! COULD NOT PERFORM DEMAND SATISFACTION TRANSACTION FOR LOAN ACCOUNT: "+loanAcid);
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity(transactionHeader);
                        return response;

                    }else {
                        log.info("The overflow amt is:"+loanRepository.getOverFlowAmount(loanDemand.getLoan().getSn()));
                        String transactionCode= (String) transactionRes.getEntity();

                        EntityResponse entityResponse;
                        entityResponse = satisfyDemandReducingBalance2(loanDemand,
                                totalAmtToSatisfyDemand,
                                transactionCode,
                                transactedAmount,
                                overFlowAmount);
                        return entityResponse;
                    }
                }
            }
        }catch (Exception e){
            log.info("Catched Error {} line: "+e.getStackTrace()[0].getLineNumber()+" " + e);
            return null;
        }
    }


    //STEP:: 3.1
    @CacheEvict(value = "first", allEntries = true)
    public EntityResponse initializeIncomeCollectionTransaction(LoanDemand loanDemand,
                                                                String drAc,
                                                                String crAc,
                                                                Double amountToCollect) {
        try {
            EntityResponse drAcValidator=validatorsService.acidValidator(drAc, "Interest receivable");
            if(drAcValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                return drAcValidator;
            }else {
                EntityResponse crAcValidator=validatorsService.acidValidator(crAc, "Profit and loss");
                if(crAcValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                    return crAcValidator;
                } else {
                    Loan loan = loanDemand.getLoan();
//                    Double alreadyIncomedInterest = loan.getAlreadyIncomedInterest();


                    String transactionDescription="\"Interest Income from "+loanDemand.getAcid();
                    TransactionHeader tranHeader= createTransactionHeader("KES",
                            transactionDescription,
                            amountToCollect,
                            drAc,
                            crAc,
                            "Interest");
                    EntityResponse transactionRes= transactionsController.systemTransaction1(tranHeader).getBody();
                    if(!transactionRes.getStatusCode().equals(HttpStatus.OK.value())){
                        ///failed transactiom
                        EntityResponse response = new EntityResponse();
                        response.setMessage("TRANSACTION ERROR! COULD NOT PERFORM INCOME COLLECTION TRANSACTION FOR LOAN ACCOUNT: ");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity(tranHeader);
                        return response;

                    }else {
                        String transactionCode= (String) transactionRes.getEntity();

                        InterestCollected interestCollected = new InterestCollected();
                        interestCollected.setCollectedOn(new Date());
                        interestCollected.setAmount(amountToCollect);
                        interestCollected.setTransactionCode(transactionCode);
                        interestCollected.setLoanDemand(loanDemand);

                        //persist
                        InterestCollected sInterestCollected=interestCollectedRepo.save(interestCollected);

                        EntityResponse entityResponse =new EntityResponse<>();
                        entityResponse.setStatusCode(HttpStatus.OK.value());
                        entityResponse.setMessage(HttpStatus.OK.getReasonPhrase());
                        entityResponse.setEntity(sInterestCollected);

                        return entityResponse;
                    }
                }
            }
        }catch (Exception e){
            log.info("Catched Error {} line: "+e.getStackTrace()[0].getLineNumber()+" " + e);
            return null;
        }
    }


    @CacheEvict(value = "first", allEntries = true)
    public EntityResponse initializeIncomeCollectionTransaction2(LoanPrepayRequest loanPrepayRequest,
                                                                 String drAc,
                                                                 String crAc,
                                                                 Double amountToCollect){
        try {
            EntityResponse drAcValidator=validatorsService.acidValidator(drAc, "Interest receivable");
            if(drAcValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())) {
                return drAcValidator;
            }else {
                EntityResponse crAcValidator=validatorsService.acidValidator(crAc, "Profit and loss");
                if(crAcValidator.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE.value())){
                    return crAcValidator;
                }else {
                    String transactionDescription="\"Interest Income from "+loanPrepayRequest.getLoanAcid();
                    TransactionHeader tranHeader= createTransactionHeader("KES",
                            transactionDescription,
                            amountToCollect,
                            drAc,
                            crAc,
                            "Interest");
                    EntityResponse transactionRes= transactionsController.systemTransaction1(tranHeader).getBody();
                    if(!transactionRes.getStatusCode().equals(HttpStatus.OK.value())){
                        ///failed transactiom
                        EntityResponse response = new EntityResponse();
                        response.setMessage("TRANSACTION ERROR! COULD NOT PERFORM INCOME COLLECTION TRANSACTION FOR LOAN ACCOUNT: ");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity(tranHeader);
                        return response;

                    }else {
                        String transactionCode= (String) transactionRes.getEntity();

                        InterestCollected interestCollected = new InterestCollected();
                        interestCollected.setCollectedOn(new Date());
                        interestCollected.setAmount(amountToCollect);
                        interestCollected.setTransactionCode(transactionCode);
                        interestCollected.LoanPrepayRequest(loanPrepayRequest);

                        //persist
                        InterestCollected sInterestCollected=interestCollectedRepo.save(interestCollected);

                        EntityResponse entityResponse =new EntityResponse<>();
                        entityResponse.setStatusCode(HttpStatus.OK.value());
                        entityResponse.setMessage(HttpStatus.OK.getReasonPhrase());
                        entityResponse.setEntity(sInterestCollected);

                        return entityResponse;
                    }
                }
            }
        }catch (Exception e){
            log.info("Catched Error {} line: "+e.getStackTrace()[0].getLineNumber()+" " + e);
            return null;
        }
    }


    @CacheEvict(value = "first", allEntries = true)
    public EntityResponse satisfyDemandReducingBalance2(LoanDemand loanDemand,
                                                        Double amountToSatisfyDemand,
                                                        String transactionCode,
                                                        Double transactedAmount,
                                                        Double overFlowAmount){
        try {
            //get loan
            LoanDemand newLoanDemand= loanDemandRepository.findById(loanDemand.getSn()).get();
            Loan loan=loanRepository.findByAcountId(loanDemand.getAcid());
            Double overFlowAmt= loanRepository.getOverFlowAmount(loanDemand.getLoan().getSn());
            Double newOverFlowAmt= overFlowAmt-amountToSatisfyDemand;
//            loan.setOverFlowAmount(newOverFlowAmt);

            //update loan overflow amount
            loanRepository.updateLoanOverFlowAmount(newOverFlowAmt, loan.getSn());
            String acid=loanDemand.getAcid();
            Double loanRemaingBalance1=loan.getTotalLoanBalance()-amountToSatisfyDemand;
            Double newAdjustedAmount=loanDemand.getAdjustmentAmount()+amountToSatisfyDemand;

            if(overFlowAmount>0){

                //create loan demand satis
                List<LoanDemandSatisfaction> ldsList=new ArrayList<>();
                //amt transacted
                LoanDemandSatisfaction loanDemandSatisfaction1 = new LoanDemandSatisfaction();
                loanDemandSatisfaction1.setAcid(loanDemand.getAcid());
                loanDemandSatisfaction1.setLoanDemand(loanDemand);
                loanDemandSatisfaction1.setAmount(transactedAmount);
                loanDemandSatisfaction1.setTransactionCode(transactionCode);
                loanDemandSatisfaction1.setDate(new Date());
                ldsList.add(loanDemandSatisfaction1);

                //overflow amt
                LoanDemandSatisfaction loanDemandSatisfaction2 = new LoanDemandSatisfaction();
                loanDemandSatisfaction2.setAcid(loanDemand.getAcid());
                loanDemandSatisfaction2.setLoanDemand(loanDemand);
                loanDemandSatisfaction2.setAmount(overFlowAmount);
                loanDemandSatisfaction2.setTransactionCode("OVERFLOW");
                loanDemandSatisfaction2.setDate(new Date());
                ldsList.add(loanDemandSatisfaction2);

                loanDemandSatisfactionRepo.saveAllAndFlush(ldsList);
            }else {
                LoanDemandSatisfaction loanDemandSatisfaction1 = new LoanDemandSatisfaction();
                loanDemandSatisfaction1.setAcid(loanDemand.getAcid());
                loanDemandSatisfaction1.setLoanDemand(loanDemand);
                loanDemandSatisfaction1.setAmount(transactedAmount);
                loanDemandSatisfaction1.setTransactionCode(transactionCode);
                loanDemandSatisfaction1.setDate(new Date());
                loanDemandSatisfactionRepo.saveAndFlush(loanDemandSatisfaction1);
            }

            log.info(" account balance after transaction: 3"+accountRepository.fetchAccountBalance(loan.getAccount().getAcid()));

            //update loan demand
            //todo: update loan balance
            loanRepository.updateLoanBalance(loanRemaingBalance1, loan.getSn());

            loanDemandRepository.updateAdjustmentAmount(newAdjustedAmount, loanDemand.getSn());
            loanDemandRepository.updateAdjustmentDate(new Date(), loanDemand.getSn());
            loanDemandRepository.updateLoanRemainingBalance(loanRemaingBalance1, loanDemand.getSn());
            loanDemandRepository.updateSatisfactionCallerFlag(CONSTANTS.YES, loanDemand.getSn());

            if(Math.abs(loanRemaingBalance1) >= 0.0){
                loanAccrualService.manualAccrual2(loan.getAccount().getAcid());
                loanBookingService.manualBooking2(loan.getAccount().getAcid());
                loanRepository.updateLoanStatus(CONSTANTS.FULLY_PAID, loan.getSn());

                balanceLoanAccountForClosure(loan, loanDemand);
                closeLoanAccount(loan.getAccount());
            }

            //asset classification
            assetClassificationService.classifyLoan(acid);
            System.out.println("****************************************************************************");
            //transfer to income account
            //reload saved loan demand from db

            log.info("Completed demand satisfaction processssssss");

            LoanDemand loanDemand2= loanDemandRepository.findById(loanDemand.getSn()).get();
            Double interestAmount=loanDemand2.getInterestAmount();
            log.info("Demanded interest :: "+interestAmount);
            Double interestCollected=loanDemand2.getSumInterestCollected();
            log.info("Collected interest :: "+interestCollected);
            Double interestToCollect= interestAmount-interestCollected;
            if(amountToSatisfyDemand<interestToCollect){
                interestToCollect=amountToSatisfyDemand;
            }
            log.info("Interest to collect :: "+interestToCollect);
            if(interestToCollect>0){
                String productCode=loanDemand.getLoan().getAccount().getProductCode();
                // TODO: 5/5/2023 get int receivable and p&l from product
                EntityResponse entityResponse= itemServiceCaller.getGeneralProductDetail1(
                        productCode,
                        CONSTANTS.LOAN_ACCOUNT);
                if(entityResponse.getStatusCode().equals(HttpStatus.OK.value())) {
                    GeneralProductDetails newGeneralProductDetails = (GeneralProductDetails) entityResponse.getEntity();
                    if (!newGeneralProductDetails.getProductType().equalsIgnoreCase("LAA")) {
                        System.out.println("Product code is not of loan");
                        return entityResponse;
                    }
                    String drAcid = newGeneralProductDetails.getInt_receivable_ac();
                    log.info("dr acid ::"+ drAcid);
                    String crAcid = newGeneralProductDetails.getPl_ac();
                    log.info("cr acid ::"+ crAcid);

                    initializeIncomeCollectionTransaction(loanDemand2,
                            drAcid,
                            crAcid,
                            interestToCollect);
                }
            }

            EntityResponse response = new EntityResponse();
            response.setMessage("SUCCESSFULLY POSTED DEMAND SATISFACTION TRANSACTION FOR LOAN ACCOUNT: "+acid+ " AMOUNT: "+amountToSatisfyDemand);
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(null);
            return response;
        }catch (Exception e){
            log.info("Catched Error {} line: "+e.getStackTrace()[0].getLineNumber()+" " + e);
            return null;
        }
    }

    public EntityResponse satisfyDemandFlatRate3(LoanDemand loanDemand,
                                                 Double amountToSatisfyDemand,
                                                 String transactionCode){
        try {
            log.info("Satisfying from the overflow");
            //get loan
            Loan loan=loanDemand.getLoan();
            Double loanOverFlowAmount=loanRepository.getOverFlowAmt(loan.getSn());
            Double newOverFlowAmount= loanOverFlowAmount-amountToSatisfyDemand;
            log.info("The new overflow amount is :: "+ newOverFlowAmount);
            loanRepository.updateLoanOverFlowAmount(newOverFlowAmount, loan.getSn());

            String acid=loanDemand.getAcid();
            //calculate loan remaining balance
            Double loanBal=loanRepository.getLoanTotalBalance(loan.getSn());
            Double loanRemaingBalance= loanBal-amountToSatisfyDemand;
            log.info("************Loan  balance is **** "+loanRemaingBalance);
            log.info("************amountToSatisfyDemand is **** "+amountToSatisfyDemand);
            log.info("************Loan remaining balance is **** "+loanRemaingBalance);

            Double newAdjustedAmount=loanDemand.getAdjustmentAmount()+amountToSatisfyDemand;
            //update loan demand

            loanDemandRepository.updateAdjustmentAmount(newAdjustedAmount, loanDemand.getSn());
            loanDemandRepository.updateAdjustmentDate(new Date(), loanDemand.getSn());
            loanDemandRepository.updateLoanRemainingBalance(loanRemaingBalance, loanDemand.getSn());
            loanDemandRepository.updateSatisfactionCallerFlag(CONSTANTS.YES, loanDemand.getSn());

            loanRepository.updateLoanBalance(loanRemaingBalance, loan.getSn());

            //update loan
            // if loan remaing balance <=0
            //update loan as fully paid
            if(Math.abs(loanRemaingBalance) >= 0.0){
                loanAccrualService.manualAccrual2(loan.getAccount().getAcid());
//                loanBookingService.manualBooking2(loan.getAccount().getAcid());
                loanRepository.updateLoanStatus(CONSTANTS.FULLY_PAID, loan.getSn());

                balanceLoanAccountForClosure( loan, loanDemand);
                closeLoanAccount(loan.getAccount());
            }

            //create loan demand satis
            LoanDemandSatisfaction loanDemandSatisfaction = new LoanDemandSatisfaction();
            loanDemandSatisfaction.setAcid(loanDemand.getAcid());
            loanDemandSatisfaction.setLoanDemand(loanDemand);
            loanDemandSatisfaction.setAmount(amountToSatisfyDemand);
            loanDemandSatisfaction.setTransactionCode(transactionCode);
            loanDemandSatisfaction.setDate(new Date());
            loanDemandSatisfactionRepo.save(loanDemandSatisfaction);

            //asset classification
            assetClassificationService.classifyLoan(acid);

            System.out.println("****************************************************************************");
            //transfer to income account
            //reload saved loan demand from db

            log.info("Completed demand satisfaction processssssss");

            LoanDemand loanDemand2= loanDemandRepository.findById(loanDemand.getSn()).get();
            Double interestAmount=loanDemand2.getInterestAmount();
            log.info("Demanded interest :: "+interestAmount);
            Double interestCollected=loanDemand2.getSumInterestCollected();
            log.info("Collected interest :: "+interestCollected);
            Double interestToCollect= interestAmount-interestCollected;
            if(amountToSatisfyDemand<interestToCollect){
                interestToCollect=amountToSatisfyDemand;
            }
            log.info("Interest to collect :: "+interestToCollect);
            if(interestToCollect>0){
                String productCode=loanDemand.getLoan().getAccount().getProductCode();
                // TODO: 5/5/2023 get int receivable and p&l from product
                EntityResponse entityResponse= itemServiceCaller.getGeneralProductDetail1(
                        productCode,
                        CONSTANTS.LOAN_ACCOUNT);
                if(entityResponse.getStatusCode().equals(HttpStatus.OK.value())) {
                    GeneralProductDetails newGeneralProductDetails = (GeneralProductDetails) entityResponse.getEntity();
                    if (!newGeneralProductDetails.getProductType().equalsIgnoreCase("LAA")) {
                        System.out.println("Product code is not of loan");
                        return entityResponse;
                    }
                    String drAcid = newGeneralProductDetails.getInt_receivable_ac();
                    String crAcid = newGeneralProductDetails.getPl_ac();

                    initializeIncomeCollectionTransaction(loanDemand2,
                            drAcid,
                            crAcid,
                            interestToCollect);
                }
            }

            EntityResponse response = new EntityResponse();
            response.setMessage("SUCCESSFULLY POSTED DEMAND SATISFACTION TRANSACTION FOR LOAN ACCOUNT: "+acid+ "AMOUNT: "+amountToSatisfyDemand.toString());
            response.setStatusCode(HttpStatus.CREATED.value());
            response.setEntity(null);
            return response;
        }catch (Exception e){
            log.info("Catched Error {} line: "+e.getStackTrace()[0].getLineNumber()+" " + e);
            return null;
        }
    }

    public EntityResponse applyPenalInterest(LoanDemand loanDemand){
        try{
            String customerCode= loanDemand.getLoan().getAccount().getCustomerCode();
            log.info("The customer code is :: "+customerCode);
            //get grace period from product
            EntityResponse res= new EntityResponse<>();
            String productCode=loanDemand.getLoan().getAccount().getProductCode();
            EntityResponse loanAccountProductEntity= itemServiceCaller.getLoanAccountProductDetails(productCode);
            if(loanAccountProductEntity.getStatusCode().equals(HttpStatus.OK.value())){
                LoanAccountProduct loanAccountProduct= (LoanAccountProduct) loanAccountProductEntity.getEntity();
                String applyLateFeePayment=(loanAccountProduct.getApply_late_fee_for_delayed_payment());
                if(applyLateFeePayment.equals(CONSTANTS.YES_STRING)){
                    Integer grace_period_for_late_fee_mm= Integer.valueOf(loanAccountProduct.getGrace_period_for_late_fee_mm());
                    log.info("grace_period_for_late_fee_mm:"+grace_period_for_late_fee_mm.toString());
                    LocalDate demandDate= datesCalculator.convertDateToLocalDate(loanDemand.getDemandDate());

                    LocalDate penalInterestApplicationDate= datesCalculator.addDate(demandDate, grace_period_for_late_fee_mm,"DAYS");
                    LocalDate now = LocalDate.now();

                    Date penaltyDate = datesCalculator.convertLocalDateToDate(penalInterestApplicationDate);

                    if (penalInterestApplicationDate.compareTo(now) <= 0){
//                        sendReminderEmail(penaltyDate, loanDemand.getAcid(), productCode, loanDemand.getLoan().getPrincipalDemandAmount(),customerCode);
                        log.info("Apply penal interest == true");
                        log.info("penalInterestApplicationDate===>"+penalInterestApplicationDate.toString());
                        res.setStatusCode(HttpStatus.OK.value());
                        res.setEntity(true);
                    } else {
                        sendReminderEmail(penaltyDate, loanDemand.getAcid(), productCode, loanDemand.getLoan().getPrincipalDemandAmount(),customerCode);
                        log.info("now:"+now);
                        res.setStatusCode(HttpStatus.OK.value());
                        res.setEntity(false);
                    }
                }else {
                    log.info("product doe not charge fee for late payment");
                    res.setStatusCode(HttpStatus.OK.value());
                    res.setEntity(false);
                }
                return res;
            }else {
                return loanAccountProductEntity;
            }
        }catch (Exception e){
            log.info("Catched Error {} line: "+e.getStackTrace()[0].getLineNumber()+" " + e);
            return null;
        }
    }
    public TransactionHeader createTransactionHeader(String loanCurrency,
                                                     String transDesc,
                                                     Double totalAmount,
                                                     String drAc,
                                                     String crAc,
                                                     String partTranIdentity){
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
        crPartTran.setParttranIdentity(partTranIdentity);
        crPartTran.setTransactionDate(new Date());
        crPartTran.setTransactionParticulars(transDesc);
        crPartTran.setIsoFlag(CONSTANTS.YES);

        List<PartTran> partTranList =new ArrayList<>();
        partTranList.add(drPartTran);
        partTranList.add(crPartTran);

        transactionHeader.setPartTrans(partTranList);
        return transactionHeader;
    }

    public TransactionHeader createTransactionHeader2(String loanCurrency,
                                                      Double totalAmount,
                                                      List<PartTran> partTranList) {
        TransactionHeader transactionHeader = new TransactionHeader();
        transactionHeader.setTransactionType(CONSTANTS.SYSTEM_USERNAME);
        transactionHeader.setCurrency(loanCurrency);
        transactionHeader.setTransactionDate(new Date());
        transactionHeader.setEntityId(CONSTANTS.SYSTEM_ENTITY);
        transactionHeader.setTotalAmount(totalAmount);




        transactionHeader.setPartTrans(partTranList);
        return transactionHeader;
    }

    public PartTran createPartTranModel(String type, Double amount, String acid, String currency,
                                        String desc, String identity) {
        PartTran partTran = new PartTran();
        partTran.setPartTranType(type);
        partTran.setTransactionAmount(amount);
        partTran.setAcid(acid);
        partTran.setCurrency(currency);
        partTran.setExchangeRate("");
        partTran.setParttranIdentity(identity);
        partTran.setTransactionDate(new Date());
        partTran.setTransactionParticulars(desc);
        partTran.setIsoFlag(CONSTANTS.YES);

        return partTran;
    }

    public Double getTotalInterestPaid(String acid){
        try{
            return loanDemandSatisfactionRepo.getTotalInterestPaid(acid);
        }catch (Exception e){
            log.info("Catched Error {} line: "+e.getStackTrace()[0].getLineNumber()+" " + e);
            return null;
        }
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    public Double getTotalInterestUnPaid(String acid){
        try{
            return loanDemandRepository.getTotalInterestUnPaid(acid);
        }catch (Exception e){
            log.info("Catched Error {} line: "+e.getStackTrace()[0].getLineNumber()+" " + e);
            return null;
        }
    }





    public Double getTotalPrincipalPaid(String acid){
        try{
            return loanDemandSatisfactionRepo.getTotalPrincipalPaid(acid);
        }catch (Exception e){
            log.info("Catched Error {} line: "+e.getStackTrace()[0].getLineNumber()+" " + e);
            return null;
        }
    }

    public void balanceLoanAccountForClosure(Loan loan, LoanDemand loanDemand){
        try{
            //get loan balance
            String acid=loan.getAccount().getAcid();
            Double loanBalance= loanRepository.getLoanTotalBalance(loan.getSn());
            //get sum accrued normal interest
            Double accruedInterest= loanAccrualRepo.getSumAccruedInterestByInterestType(CONSTANTS.NORMAL_INTEREST,
                    acid);
            //get sum booked interest
            Double bookedInterest= loanBookingRepo.getSumBookedInterestByInterestType(CONSTANTS.NORMAL_INTEREST,
                    acid);
            //get total paid interest
            Double totalInterestPaid= loanDemandSatisfactionRepo.getTotalInterestPaid(acid);
            if(Math.abs(accruedInterest-totalInterestPaid) > 2){
                //accrual balance
                log.info("Accrual balance");
                Date accrueUpTo= loanDemand.getDemandDate();
                accrualBalance(loan,totalInterestPaid,accruedInterest,accrueUpTo);
            }
            if(Math.abs(bookedInterest-totalInterestPaid) > 2){
                log.info("Booked Interest:"+ bookedInterest);
                log.info("Paid interest :"+ totalInterestPaid);

                Date bookedUpTo= loanDemand.getDemandDate();
                bookingBalance(loan,totalInterestPaid,bookedInterest,bookedUpTo);

            }
        }catch (Exception e){
            log.info("Catched Error {} line: "+e.getStackTrace()[0].getLineNumber()+" " + e);
//            return null;
        }
    }

    public void closeLoanAccount(Account account) {
        try{
            log.info("Closing a loan account");
            String acid= account.getAcid();
            //check the loan account balance
            Double accountBalance= accountRepository.fetchAccountBalance(acid);
            //check the sum accrued amount
            Double accruedInterest= loanAccrualRepo.getSumAccruedInterestByInterestType(CONSTANTS.NORMAL_INTEREST,
                    acid);
            Double loanBalance=loanRepository.getLoanTotalBalance(account.getLoan().getSn());
            //check interest booked
            Double bookedInterest= loanBookingRepo.getSumBookedInterestByInterestType(CONSTANTS.NORMAL_INTEREST,
                    acid);
            // get interest paid
            Double totalInterestPaid= loanDemandSatisfactionRepo.getTotalInterestPaid(acid);
            // get the principal paid
            Double totalPrincipalPaid= loanDemandSatisfactionRepo.getTotalPrincipalPaid(acid);
            //get the penal interest paid
            Double totalPenalInterestPaid= loanDemandSatisfactionRepo.getPenalInterestPaid(acid);
            //get the penal interest accrued
            Double penalInterestAccrued= loanAccrualRepo.getSumAccruedInterestByInterestType(CONSTANTS.PENAL_INTEREST,
                    acid);

            if(Math.abs(accountBalance) <1){
                log.info(" account balance is ok");
                log.info(" The loan balance is" + loanBalance);
                log.info(" The loan balance is absolute value" + Math.abs(loanBalance));
                if(Math.abs(loanBalance) <1){

                    log.info(" loan balance is ok");
                    if(Math.abs(accruedInterest-totalInterestPaid) < 2){
                        log.info(" accrued interest ok");
                        if(Math.abs(bookedInterest-totalInterestPaid) < 2){
                            log.info(" booked interest ok");
                            if(Math.abs(totalPrincipalPaid-account.getLoan().getPrincipalAmount()) < 2){
                                log.info(" principal check ok");
                                if(Math.abs(totalPenalInterestPaid-penalInterestAccrued) < 2){
                                    log.info("Account can be closed");

                                    loanRepository.updateAccountStatus(CONSTANTS.CLOSED, account.getId());
                                    AccountClosureInfo info= new AccountClosureInfo();
                                    info.setClosingDate(new Date());
                                    info.setPostedFlag(CONSTANTS.YES);
                                    info.setVerifiedFlag(CONSTANTS.NO);
                                    info.setClosureReason("Loan fully paid");
                                    info.setAccount(account);
                                    info.setClosedBy(CONSTANTS.SYSTEM_USERNAME);
                                    accountClosureRepo.save(info);



                                }else {
                                    log.warn("Penal Interest error");
                                }
                            }else {
                                log.warn("Principal amount error");
                            }
                        }else {
                            log.warn("Booked interest error");
                        }
                    }else {
                        log.warn("Accrued interest error");
                    }
                }else {
                    log.warn("Loan balance error");
                }
            }else {
                log.warn("Account balance errur");
            }

        }catch (Exception e){
            log.info("Catched Error {} line: "+e.getStackTrace()[0].getLineNumber()+" " + e);
//            return null;
        }
    }

    //Balances the acccrued interest and the received interest
    public void accrualBalance(Loan loan,
                               Double totalInterestPaid,
                               Double accruedInterest,
                               Date accruedUpTo){
        try{
            Double interestDifference=Math.abs(accruedInterest-totalInterestPaid);
            if(totalInterestPaid>accruedInterest){
                //accrue the interest which is not accrued
                loanAccrualService.accrueInterest(loan,interestDifference,accruedUpTo);
            }else {
                //reverse the accrual of the excess interest
                loanAccrualService.reverseAccruedNormalInterest(loan,
                        interestDifference);
            }
        }catch (Exception e){
            log.info("Catched Error {} line: "+e.getStackTrace()[0].getLineNumber()+" " + e);
//            return null;
        }
    }

    //Balance booked interest
    public void bookingBalance(Loan loan,
                               Double totalInterestPaid,
                               Double bookedInterest,
                               Date bookedUpTo){
        try{
            log.info("Booking balance");
            Double interestDifference=Math.abs(bookedInterest-totalInterestPaid);
            if(totalInterestPaid>bookedInterest){
                //book the interest which has not been booked
                loanBookingService.bookInterest(loan,interestDifference,bookedUpTo);
            }else {
                //reverse the accrual of the excess interest
                loanBookingService.reverseBooking( loan,
                        interestDifference);
            }
        }catch (Exception e){
            log.info("Catched Error {} line: "+e.getStackTrace()[0].getLineNumber()+" " + e);
//            return null;
        }
    }

    public void sendReminderEmail(
            Date penaltyDate,
            String loanAcid,
            String productCode,
            Double loanAmt,
            String customerCode){
        try {
            log.info("Sending reminder email");
            //get customer email
            EntityResponse mailRes=itemServiceCaller.getCustomerEmail(customerCode);
            if(mailRes.getStatusCode() == HttpStatus.OK.value()){
                String email= (String) mailRes.getEntity();
                log.info("Email address is :: "+email);

                EntityResponse penalIntRes=productInterestService.getLoanInterest(productCode,
                        loanAmt);

                String penaltyAmt = "";
                if(penalIntRes.getStatusCode().equals(HttpStatus.OK.value())) {
                    ProductInterestDetails productInterestDetails= (ProductInterestDetails) penalIntRes.getEntity();
                    Double penaltyAmount= productInterestDetails.getPenalInterestAmount();
                    penaltyAmt=penaltyAmount.toString();
                }

                log.info("sending email");
                Double arrears= loanDemandRepository.getSumArrears(loanAcid);
                String subject="Loan Payment Reminder";
                String message = "This is to inform you that, you have  loan arrears amounting to Ksh "+arrears+
                        ". Kindly pay your loan by  "+penaltyDate+",  failure to which will attract penalty/interest of Ksh "+penaltyAmt;

                MailDto mailDto= new MailDto();
                mailDto.setSubject(subject);
                mailDto.setMessage(message);
                mailDto.setTo(email);


                itemServiceCaller.sendEmail(mailDto);
            }


        }catch (Exception e){
            log.info("Catched Error {} line: "+e.getStackTrace()[0].getLineNumber()+" " + e);
//            return null;
        }
    }



    public EntityResponse prepayLoan(LoanPrepayRequest loanPrepayRequest) {
        Optional<Account> loanAccountOptional = accountRepository.findByAccountId(loanPrepayRequest.getLoanAcid());
        Optional<Account> repaymentAccountOptional;
        EntityResponse response = new EntityResponse();
        if (loanAccountOptional.isPresent() && loanAccountOptional.get().getAccountType().equalsIgnoreCase("LAA")) {
            Account loanAccount = loanAccountOptional.get();
            String operativeAcid = null;
            if (loanPrepayRequest.getUseRepaymentAccount().equals('Y')) {
                operativeAcid = loanAccount.getLoan().getOperativeAcountId();
            } else {
                operativeAcid = loanPrepayRequest.getRepaymentAccount();
            }

            repaymentAccountOptional = accountRepository.findByAccountId(operativeAcid);
            if (!repaymentAccountOptional.isPresent()) {
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setMessage("Operative account not found");
                return response;
            }
            Account repaymentAccount = repaymentAccountOptional.get();
            if (!repaymentAccount.getAccountStatus().equalsIgnoreCase("ACTIVE")) {
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setMessage("Repayment account is not active");
                return response;
            }

            Double amount;
            if (loanPrepayRequest.getRepayOneInstallment().equals('Y')) {
                amount = loanAccount.getLoan().getInstallmentAmount();
                log.info("This is installment Amount: " + loanAccount.getLoan().getInstallmentAmount());
            } else {
                amount = loanPrepayRequest.getAmount();
                log.info("This is the total loan Amount: " + loanPrepayRequest.getAmount());
            }

            if (amount <= 0) {
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setMessage("Repayment amount cannot be "+amount);
                return response;
            }

            if (Math.abs(loanAccount.getAccountBalance()) < amount ){
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setMessage("Repayment amount is more than loan balance.");
                return response;
            }

            if (accountRepository.getAvailableBalance(operativeAcid) < amount) {
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setMessage("There is no enough amount in repayment account");
                return response;
            }

            log.info("This is the total loan Amount: " + loanPrepayRequest.getAmount()+ "======"+amount);

            Double interestPendingAmount = loanDemandRepository.getSumUnpaidInterest(loanPrepayRequest.getLoanAcid());

            Double amountToCollect = Math.min(interestPendingAmount, amount);
            log.info("The Interest Amount is : " +amountToCollect);

            Double principalAmountToTransact = amount - amountToCollect;

            log.info("The principal Amount is : " +principalAmountToTransact);

            TransactionHeader tranHeader= createTransactionHeader("KES",
                    "Loan repayment",
                    amount,
                    repaymentAccount.getAcid(),
                    loanAccount.getAcid(),
                    "Normal");

            tranHeader.getPartTrans().clear();
            tranHeader.setPartTrans(new ArrayList<>());

            if (amountToCollect > 0.01) {
                TransactionHeader tranHeaderForInterest = createTransactionHeader("KES",
                        "Interest repayment",
                        amountToCollect,
                        repaymentAccount.getAcid(),
                        loanAccount.getAcid(),
                        "Interest");
                tranHeader.getPartTrans().addAll(tranHeaderForInterest.getPartTrans());
            }

            if (principalAmountToTransact > 0.01) {
                TransactionHeader tranHeaderForPrincipal = createTransactionHeader("KES",
                        "Principal repayment",
                        principalAmountToTransact,
                        repaymentAccount.getAcid(),
                        loanAccount.getAcid(),
                        "Principal");
                tranHeader.getPartTrans().addAll(tranHeaderForPrincipal.getPartTrans());
            }

            EntityResponse transactionRes = transactionsController.systemTransaction1(tranHeader).getBody();

            if (!transactionRes.getStatusCode().equals(HttpStatus.OK.value())) {
                Double overflow = loanRepository.getOverFlowAmount(loanAccount.getLoan().getSn());
                loanRepository.updateLoanOverFlowAmount(overflow + amount, loanAccount.getLoan().getSn());
                loanAccount = loanAccountOptional.get();
                return transactionRes;
            }

            // Initialize income collection using the loanDemand from the repository
            System.out.println("Initializing income collection");
            LoanDemand loanDemand = loanDemandRepository.findById(loanAccount.getLoan().getSn()).orElse(null);

            if (loanDemand != null && amountToCollect > 0) {

                String productCode = accountRepository.findAccountByProductCode(loanPrepayRequest.getLoanAcid());
                System.out.println("The account product code is: " + productCode);



                // TODO: 5/5/2023 get int receivable and p&l from product
                EntityResponse entityResponse = itemServiceCaller.getGeneralProductDetail1(
                        productCode,
                        CONSTANTS.LOAN_ACCOUNT);

                if (entityResponse.getStatusCode().equals(HttpStatus.OK.value())) {
                    GeneralProductDetails newGeneralProductDetails = (GeneralProductDetails) entityResponse.getEntity();

                    System.out.println("Product code =" + productCode);
                    if (!newGeneralProductDetails.getProductType().equalsIgnoreCase("LAA")) {
                        System.out.println("Product code is not of loan");
                        return entityResponse;
                    }

                    String drAcid = newGeneralProductDetails.getInt_receivable_ac();
                    System.out.println("drAcid =" + drAcid );
                    String crAcid = newGeneralProductDetails.getPl_ac();
                    System.out.println("crAcid =" + crAcid );

                    initializeIncomeCollectionTransaction2(loanPrepayRequest, drAcid, crAcid, amountToCollect);
                }
            }

            return transactionRes;

        } else {
            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            response.setMessage("Loan account not found");
            return response;
        }
    }



    public EntityResponse prepayAllLoan(LoanPrepayRequest loanPrepayRequest) {
        Optional<Account> loanAccountOptional = accountRepository.findByAccountId(loanPrepayRequest.getLoanAcid());
        Optional<Account> repaymentAccountOptional;
        EntityResponse response = new EntityResponse();
        if (loanAccountOptional.isPresent() &&  loanAccountOptional.get().getAccountType().equalsIgnoreCase("LAA")) {
            Account loanAccount = loanAccountOptional.get();
            String operativeAcid = null;
            if (loanPrepayRequest.getUseRepaymentAccount().equals('Y')) {
                operativeAcid = loanAccount.getLoan().getOperativeAcountId();
            } else {
                operativeAcid = loanPrepayRequest.getRepaymentAccount();
            }

            repaymentAccountOptional = accountRepository.findByAccountId(operativeAcid);
            if (!repaymentAccountOptional.isPresent()) {
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setMessage("Operative account not found");
                return response;
            }
            Account repaymentAccount = repaymentAccountOptional.get();
            if (!repaymentAccount.getAccountStatus().equalsIgnoreCase("ACTIVE")) {
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setMessage("Repayment account is not active");
                return response;
            }

            Double amount;
            if (loanPrepayRequest.getRepayOneInstallment().equals('Y')) {
                amount = loanAccount.getLoan().getInstallmentAmount();
            } else {
                amount = loanPrepayRequest.getAmount();
            }

            if (amount <= 0) {
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setMessage("Repayment amount cannot be "+amount);
                return response;
            }

            if (Math.abs(loanAccount.getAccountBalance()) < amount ){
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setMessage("Repayment amount is more than loan balance.");
                return response;
            }

            if (accountRepository.getAvailableBalance(operativeAcid) < amount) {
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setMessage("There is no enough amount in repayment account");
                return response;
            }

            Double interestPendingAmount = loanDemandRepository.getSumUnpaidInterest(loanPrepayRequest.getLoanAcid());

            Double interestAmountToTransact = Math.min(interestPendingAmount, loanPrepayRequest.getAmount());

            Double principalAmountToTransact = loanPrepayRequest.getAmount() - interestAmountToTransact;


            TransactionHeader tranHeader= createTransactionHeader("KES",
                    "Loan repayment",
                    amount,
                    repaymentAccount.getAcid(),
                    loanAccount.getAcid(),
                    "Normal");

            tranHeader.getPartTrans().clear();
            tranHeader.setPartTrans(new ArrayList<>());

            if (interestAmountToTransact > 0.01) {
                TransactionHeader tranHeaderForInterest = createTransactionHeader("KES",
                        "Interest repayment",
                        interestAmountToTransact,
                        repaymentAccount.getAcid(),
                        loanAccount.getAcid(),
                        "Interest");
                tranHeader.getPartTrans().addAll(tranHeaderForInterest.getPartTrans());
            }

            if (principalAmountToTransact > 0.01) {
                TransactionHeader tranHeaderForPrincipal = createTransactionHeader("KES",
                        "Principal repayment",
                        principalAmountToTransact,
                        repaymentAccount.getAcid(),
                        loanAccount.getAcid(),
                        "Principal");
                tranHeader.getPartTrans().addAll(tranHeaderForPrincipal.getPartTrans());
            }

            EntityResponse transactionRes= transactionsController.systemTransaction1(tranHeader).getBody();
//                    EntityResponse transactionRes=accountTransactionService.createTransaction1(outgoingTransactionDetails);
            if(!transactionRes.getStatusCode().equals(HttpStatus.OK.value())) {
                Double overflow = loanRepository.getOverFlowAmount(loanAccount.getLoan().getSn());
                loanRepository.updateLoanOverFlowAmount( overflow+amount, loanAccount.getLoan().getSn());
                loanAccount = loanAccountOptional.get();
                return transactionRes;
            }

            // Initialize income collection using the loanDemand from the repository
            System.out.println("Initializing income collection");
            LoanDemand loanDemand = loanDemandRepository.findById(loanAccount.getLoan().getSn()).orElse(null);

            if (loanDemand != null && interestAmountToTransact > 0) {
                String productCode = accountRepository.findAccountByProductCode(loanPrepayRequest.getLoanAcid());
                System.out.println("The account product code is: " + productCode);




                // TODO: 5/5/2023 get int receivable and p&l from product
                EntityResponse entityResponse = itemServiceCaller.getGeneralProductDetail1(
                        productCode,
                        CONSTANTS.LOAN_ACCOUNT);

                if (entityResponse.getStatusCode().equals(HttpStatus.OK.value())) {
                    GeneralProductDetails newGeneralProductDetails = (GeneralProductDetails) entityResponse.getEntity();

                    System.out.println("Product code = " + productCode);
                    if (!newGeneralProductDetails.getProductType().equalsIgnoreCase("LAA")) {
                        System.out.println("Product code is not of loan");
                        return entityResponse;
                    }

                    String drAcid = newGeneralProductDetails.getInt_receivable_ac();
                    System.out.println("drAcid =" + drAcid );
                    String crAcid = newGeneralProductDetails.getPl_ac();
                    System.out.println("crAcid =" + crAcid );

                    initializeIncomeCollectionTransaction2(loanPrepayRequest, drAcid, crAcid, interestAmountToTransact);
                }
            }
            return transactionRes;
        }
        else {
            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            response.setMessage("Loan account not found");
            return response;
        }
    }




    public EntityResponse prepaySTOLoan(LoanPrepayRequest loanPrepayRequest) {
        Optional<Account> loanAccountOptional = accountRepository.findByAccountId(loanPrepayRequest.getLoanAcid());
        Optional<Account> repaymentAccountOptional;
        EntityResponse response = new EntityResponse();
        if (loanAccountOptional.isPresent() &&  loanAccountOptional.get().getAccountType().equalsIgnoreCase("LAA")) {
            Account loanAccount = loanAccountOptional.get();
            String operativeAcid = null;
            if (loanPrepayRequest.getUseRepaymentAccount().equals('Y')) {
                operativeAcid = loanAccount.getLoan().getOperativeAcountId();
            } else {
                operativeAcid = loanPrepayRequest.getRepaymentAccount();
            }

            repaymentAccountOptional = accountRepository.findByAccountId(operativeAcid);
            if (!repaymentAccountOptional.isPresent()) {
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setMessage("Operative account not found");
                return response;
            }
            Account repaymentAccount = repaymentAccountOptional.get();
            if (!repaymentAccount.getAccountStatus().equalsIgnoreCase("ACTIVE")) {
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setMessage("Repayment account is not active");
                return response;
            }

            Double amount;
            if (loanPrepayRequest.getRepayOneInstallment().equals('Y')) {
                amount = loanAccount.getLoan().getInstallmentAmount();
            } else {
                amount = loanPrepayRequest.getAmount();
            }

            if (amount <= 0) {
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setMessage("Repayment amount cannot be "+amount);
                return response;
            }

            if (Math.abs(loanAccount.getAccountBalance()) < amount ){
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setMessage("Repayment amount is more than loan balance.");
                return response;
            }

            if (accountRepository.getAvailableBalance(operativeAcid) < amount) {
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setMessage("There is no enough amount in repayment account");
                return response;
            }

            Double interestPendingAmount = loanDemandRepository.getSumUnpaidInterest(loanPrepayRequest.getLoanAcid());

            Double interestAmountToTransact = Math.min(interestPendingAmount, loanPrepayRequest.getAmount());

            Double principalAmountToTransact = loanPrepayRequest.getAmount() - interestAmountToTransact;


            TransactionHeader tranHeader= createTransactionHeader("KES",
                    "Loan repayment",
                    amount,
                    repaymentAccount.getAcid(),
                    loanAccount.getAcid(),
                    "Normal");

            tranHeader.getPartTrans().clear();
            tranHeader.setPartTrans(new ArrayList<>());

            if (interestAmountToTransact > 0.01) {
                TransactionHeader tranHeaderForInterest = createTransactionHeader("KES",
                        "Interest repayment",
                        interestAmountToTransact,
                        repaymentAccount.getAcid(),
                        loanAccount.getAcid(),
                        "Interest");
                tranHeader.getPartTrans().addAll(tranHeaderForInterest.getPartTrans());
            }

            if (principalAmountToTransact > 0.01) {
                TransactionHeader tranHeaderForPrincipal = createTransactionHeader("KES",
                        "Principal repayment",
                        principalAmountToTransact,
                        repaymentAccount.getAcid(),
                        loanAccount.getAcid(),
                        "Principal");
                tranHeader.getPartTrans().addAll(tranHeaderForPrincipal.getPartTrans());
            }

            EntityResponse transactionRes= transactionsController.systemTransaction1(tranHeader).getBody();
//                    EntityResponse transactionRes=accountTransactionService.createTransaction1(outgoingTransactionDetails);
            if(!transactionRes.getStatusCode().equals(HttpStatus.OK.value())) {
                Double overflow = loanRepository.getOverFlowAmount(loanAccount.getLoan().getSn());
                loanRepository.updateLoanOverFlowAmount( overflow+amount, loanAccount.getLoan().getSn());
                loanAccount = loanAccountOptional.get();
                return transactionRes;
            } else {
                return transactionRes;
            }
        } else {
            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            response.setMessage("Loan account not found");
            return response;
        }
    }


}
