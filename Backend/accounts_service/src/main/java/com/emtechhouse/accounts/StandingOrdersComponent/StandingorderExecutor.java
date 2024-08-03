package com.emtechhouse.accounts.StandingOrdersComponent;

import com.emtechhouse.accounts.Models.Accounts.Account;
import com.emtechhouse.accounts.Models.Accounts.AccountRepository;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.DemandSatisfaction.InterestCollected.InterestCollected;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.DemandSatisfaction.InterestCollected.InterestCollectedRepo;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.LoanDemand;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.LoanDemandRepository;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanRepository;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.GeneralProductDetails;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ItemServiceCaller;
import com.emtechhouse.accounts.Models.Accounts.Validators.ValidatorService;
import com.emtechhouse.accounts.StandingOrdersComponent.StandingOrderDestination.Standingorderdestination;
import com.emtechhouse.accounts.StandingOrdersComponent.StandingOrderExecution.StandingOrderExecution;
import com.emtechhouse.accounts.StandingOrdersComponent.StandingOrderExecution.StandingOrderExecutionRepo;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.PartTrans.PartTran;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TranHeaderService;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TransactionHeader;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TransactionsController;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.emtechhouse.accounts.Utils.DatesCalculator;
import com.emtechhouse.accounts.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.accounts.Utils.HttpInterceptor.UserRequestContext;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import com.netflix.discovery.shared.transport.EurekaHttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class StandingorderExecutor {
    private final StandingorderheaderRepo standingorderheaderRepo;
    private final TranHeaderService tranHeaderService;
    private final AccountRepository accountRepository;

    @Autowired
    private StandingOrderExecutionRepo standingOrderExecutionRepo;

    @Autowired
    private ValidatorService validatorsService;

    @Autowired
    private InterestCollectedRepo interestCollectedRepo;

    @Autowired
    private LoanDemandRepository loanDemandRepository;

    @Autowired
    private ItemServiceCaller itemServiceCaller;


    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private DatesCalculator datesCalculator;

    @Autowired
    private TransactionsController transactionsController;
    private EurekaHttpResponse<Object> transactionRes;

    public StandingorderExecutor(StandingorderheaderRepo standingorderheaderRepo, TranHeaderService tranHeaderService, AccountRepository accountRepository) {
        this.standingorderheaderRepo = standingorderheaderRepo;
        this.tranHeaderService = tranHeaderService;
        this.accountRepository = accountRepository;
    }

    public String validateSourceAcount(Account account) {
        if (!account.getAccountStatus().equalsIgnoreCase("active")) {
            return "Account is not active";
        }
        if (!(account.getAccountType().equalsIgnoreCase("sba")
                || account.getAccountType().equalsIgnoreCase(CONSTANTS.OFFICE_ACCOUNT))) {
            return "Account is must be sba or office";
        }
        return null;
    }

    public void registerNewFail(Standingorderheader standingorderheader, String reason, Date nextRunDate) {
        System.out.println(reason);
        List<StandingOrderExecution> standingOrderExecutions = standingOrderExecutionRepo.findByStoAndRunDate(standingorderheader.getStandingOrderCode(), nextRunDate);

        if (standingOrderExecutions.size() > 0) {
            for (StandingOrderExecution standingOrderExecution: standingOrderExecutions) {
                standingOrderExecution.setStatus("FAIL");
                standingOrderExecution.setRunDate(nextRunDate);
                standingOrderExecution.setFailureReason(reason);
                standingOrderExecution.setPartTrans(new ArrayList<>());

                standingOrderExecutionRepo.save(standingOrderExecution);
            }
        } else {
            for (Standingorderdestination standingorderdestination: standingorderheader.standingorderdestinations) {
                StandingOrderExecution standingOrderExecution = new StandingOrderExecution();
                standingOrderExecution.setStandingOrderCode(standingorderheader.getStandingOrderCode());
                standingOrderExecution.setDestination(standingorderdestination);
                standingOrderExecution.setStatus("FAIL");
                standingOrderExecution.setRunDate(nextRunDate);
                standingOrderExecution.setFailureReason(reason);
                standingOrderExecution.setPartTrans(new ArrayList<>());

                standingOrderExecutionRepo.save(standingOrderExecution);
            }
        }
        if (standingorderheader.getNextRunDate().equals(nextRunDate)) {
            Date date = standingorderheader.getNextRunDate();
            standingorderheader.setNextRunDate(datesCalculator.addDate(date,1, "MONTHS"));
            standingorderheaderRepo.save(standingorderheader);
        }
    }


    public Object registerNewFail1(Standingorderheader standingorderheader, String reason, Date nextRunDate) {
        System.out.println(reason);
        System.out.println("inside new fail one");
        List<StandingOrderExecution> standingOrderExecutions = standingOrderExecutionRepo.findByStoAndRunDate(standingorderheader.getStandingOrderCode(), nextRunDate);

        if (standingOrderExecutions.size() > 0) {
            for (StandingOrderExecution standingOrderExecution: standingOrderExecutions) {
                standingOrderExecution.setStatus("FAIL");
                standingOrderExecution.setRunDate(nextRunDate);
                standingOrderExecution.setFailureReason(reason);
                standingOrderExecution.setPartTrans(new ArrayList<>());

                standingOrderExecutionRepo.save(standingOrderExecution);
            }
        } else {
            for (Standingorderdestination standingorderdestination: standingorderheader.standingorderdestinations) {
                StandingOrderExecution standingOrderExecution = new StandingOrderExecution();
                standingOrderExecution.setStandingOrderCode(standingorderheader.getStandingOrderCode());
                standingOrderExecution.setDestination(standingorderdestination);
                standingOrderExecution.setStatus("FAIL");
                standingOrderExecution.setRunDate(nextRunDate);
                standingOrderExecution.setFailureReason(reason);
                standingOrderExecution.setPartTrans(new ArrayList<>());

                standingOrderExecutionRepo.save(standingOrderExecution);
            }
        }
        if (standingorderheader.getNextRunDate().equals(nextRunDate)) {
            Date date = standingorderheader.getNextRunDate();
            standingorderheader.setNextRunDate(datesCalculator.addDate(date,1, "DAYS"));
            standingorderheaderRepo.save(standingorderheader);
        }
        return null;
    }


    public Object registerNewFail2(Standingorderheader standingorderheader, String reason, Date nextRunDate) {
        System.out.println(reason);
        System.out.println("inside new fail 2");
        List<StandingOrderExecution> standingOrderExecutions = standingOrderExecutionRepo.findByStoAndRunDate(standingorderheader.getStandingOrderCode(), nextRunDate);

        if (standingOrderExecutions.size() > 0) {
            for (StandingOrderExecution standingOrderExecution: standingOrderExecutions) {
                standingOrderExecution.setStatus("FAIL");
                Date date = standingorderheader.getNextRunDate();
                System.out.println("About to set next run date as constant: "+date);
                standingOrderExecution.setRunDate(date);
                standingOrderExecution.setFailureReason(reason);
                standingOrderExecution.setPartTrans(new ArrayList<>());

                standingOrderExecutionRepo.save(standingOrderExecution);
            }
        } else {
            for (Standingorderdestination standingorderdestination: standingorderheader.standingorderdestinations) {
                StandingOrderExecution standingOrderExecution = new StandingOrderExecution();
                standingOrderExecution.setStandingOrderCode(standingorderheader.getStandingOrderCode());
                standingOrderExecution.setDestination(standingorderdestination);
                standingOrderExecution.setStatus("FAIL");
                standingOrderExecution.setRunDate(nextRunDate);
                standingOrderExecution.setFailureReason(reason);
                standingOrderExecution.setPartTrans(new ArrayList<>());

                standingOrderExecutionRepo.save(standingOrderExecution);
            }
        }
        if (standingorderheader.getNextRunDate().equals(nextRunDate)) {
            Date date = standingorderheader.getNextRunDate();
            System.out.println("Setting date date: "+date);
            standingorderheader.setNextRunDate(date);
            standingorderheaderRepo.save(standingorderheader);
        }
        return null;
    }




    public void executeNew(Long id) {
        Optional<Standingorderheader> standingorderheader = standingorderheaderRepo.findById(id);
        standingorderheader.ifPresent(this::executeNew);
    }
    public void executeNew(String  stoCode) {
        Optional<Standingorderheader> standingorderheader = standingorderheaderRepo.findByStoCode(stoCode);
        standingorderheader.ifPresent(this::executeNew);
    }

    public void executeNew(Standingorderheader standingorderheader) {
        System.out.println("STO header json: " + standingorderheader);
        Optional<Account> sourceAccount = accountRepository.findByAcid(standingorderheader.getSourceAccountNo());
//        if (Math.abs(datesCalculator.getDaysDifference(standingorderheader.getNextRunDate(), new Date())) > standingorderheader.getMaxTrialDays()) {
//            registerNewFail(standingorderheader, "Execution date expired", standingorderheader.getNextRunDate());
//            return;
//        }
        if (standingorderheader.getNextRunDate().compareTo(new Date()) > 0) {
            registerNewFail2(standingorderheader, "Execution date Not Yet", standingorderheader.getNextRunDate());
            System.out.println("Execution date NOT YET");
            return;
        }
        if (!sourceAccount.isPresent()) {
            System.out.println();
            registerNewFail(standingorderheader, "No such source account", standingorderheader.getNextRunDate());
            return;
        }
        String valid = validateSourceAcount(sourceAccount.get());
        if (valid != null) {
            System.out.println(valid);
            registerNewFail(standingorderheader, "Invalid source account", standingorderheader.getNextRunDate());
            return;
        }
        String customerCode = sourceAccount.get().getCustomerCode();
        List<PartTran> partTranList = new ArrayList<>();
        System.out.println(standingorderheader);
        TransactionHeader dtd = new TransactionHeader();
        dtd.setEntityId(EntityRequestContext.getCurrentEntityId());
        dtd.setTransactionDate(new Date());
        dtd.setRcre(new Date());
        dtd.setCurrency("KES");
        dtd.setTransactionType("Transfer");
        dtd.setTransactionCode("STO" + tranHeaderService.generateRandomCode(6));
        dtd.setEnteredBy(UserRequestContext.getCurrentUser());
        dtd.setEnteredFlag(CONSTANTS.YES);
        dtd.setEnteredTime(new Date());

        boolean continueFromIn = false;

        Double cumulative = 0.0;
//                Get partran from destinations
        int j;
        for (j = 0; j < standingorderheader.getStandingorderdestinations().size(); j++) {
            if (continueFromIn)
                continue;
            Standingorderdestination standingorderdestination = standingorderheader.getStandingorderdestinations().get(j);
            Optional<Account> accountOptional = accountRepository.findByAcid(standingorderdestination.getDestinationAccountNo());
            if (!accountOptional.isPresent()) {
                System.out.println("Account not found");
                continueFromIn = true;
                registerNewFail(standingorderheader, "No such destination account", standingorderheader.getNextRunDate());
                continue;
            }

            String accountType = accountRepository.findAccountByType(standingorderdestination.getDestinationAccountNo());
            System.out.println("The account type for STO is: " + accountType);

            Account account = accountOptional.get();
            PartTran partTran2 = new PartTran();

            if ("LAA".equals(accountType)) {
                EntityResponse prepayResponse = prepaySTOLoan(sourceAccount.get(), account, standingorderdestination.getAmount(), standingorderheader);
                if (prepayResponse.getStatusCode() == HttpStatus.NOT_ACCEPTABLE.value()) {
                    registerNewFail1(standingorderheader, "No Money in source account", standingorderheader.getNextRunDate());
                    System.out.println("NOT Executed a standing order for Loan repayment");
                    return;
                } else {
                    Date date = standingorderheader.getNextRunDate();
                    standingorderheader.setNextRunDate(datesCalculator.addDate(date, 1, "MONTHS"));
                    standingorderheaderRepo.save(standingorderheader);
                    System.out.println("Executed a standing order for Loan repayment");
                    return;
                }

            } else {
                partTran2.setIsoFlag('N');
                partTran2.setCurrency("Ksh");
                partTran2.setAcid(standingorderdestination.getDestinationAccountNo());
                partTran2.setPartTranType("Credit");
                partTran2.setTransactionDate(new Date());
                partTran2.setParttranIdentity("Normal");
            }
            partTran2.setTransactionAmount(standingorderdestination.getAmount());
            partTran2.setTransactionParticulars("STO- " + standingorderheader.getDescription() + " " + datesCalculator.dateFormat(standingorderheader.getNextRunDate()));

            if (sourceAccount.get().getCustomerCode().equalsIgnoreCase(account.getCustomerCode())
                    && account.getAccountType().equalsIgnoreCase(CONSTANTS.LOAN_ACCOUNT)) {
                continueFromIn = true;

                registerNewFail(standingorderheader, "Dont use standing orders on own loan accounts", standingorderheader.getNextRunDate());
                continue;
            }

            cumulative += standingorderdestination.getAmount();

            System.out.println("-------------------" + cumulative + "----------------------");

            partTranList.add(partTran2);
        }
        if (continueFromIn) {
            return;
        }

        Double availableBalance = accountRepository.getAvailableBalance(standingorderheader.getSourceAccountNo());
        if (availableBalance < cumulative) {
                System.out.println("Source account balance not sufficient");
                registerNewFail(standingorderheader, "Source account balance not sufficient", standingorderheader.getNextRunDate());
                return;
        }
        PartTran partTran = new PartTran();
        partTran.setIsoFlag('N');
        partTran.setCurrency("Ksh");
        partTran.setParttranIdentity("Normal");
        partTran.setAcid(standingorderheader.getSourceAccountNo());
        partTran.setPartTranType("Debit");
        partTran.setTransactionDate(new Date());
        partTran.setTransactionAmount(standingorderheader.getAmount());
        partTran.setTransactionParticulars("STO- " + standingorderheader.getDescription() + " " + datesCalculator.dateFormat(standingorderheader.getNextRunDate()));
        partTran.setTransactionAmount(cumulative);
        partTranList.add(partTran);
        dtd.setPartTrans(partTranList);


        for (PartTran p : dtd.getPartTrans())
            System.out.println(p);


        ResponseEntity<EntityResponse> response = transactionsController.systemTransaction1(dtd);

        System.out.println(response);
        System.out.println("After standing order result");
        EntityResponse entityResponse = response.getBody();

        if (entityResponse.getStatusCode() == 200) {
            Date date = standingorderheader.getNextRunDate();
            TransactionHeader transactionHeader = tranHeaderService.retrieveTranHeader(dtd.getTransactionCode());
            PartTran partTranSaved;
//                    Double total;
            for (Standingorderdestination standingorderdestination :
                    standingorderheader.getStandingorderdestinations()) {
                System.out.println("##################");
                StandingOrderExecution standingOrderExecution = new StandingOrderExecution();
                standingOrderExecution.setStandingOrderCode(standingorderheader.getStandingOrderCode());
                standingOrderExecution.setDestination(standingorderdestination);
                standingOrderExecution.setRunDate(date);
                standingOrderExecution.setStatus("SUCCESS");
                standingOrderExecution.setFailureReason("");
                standingOrderExecution.setPartTrans(new ArrayList<>());
                partTranSaved = getPartTran(standingorderdestination.getDestinationAccountNo(),
                        transactionHeader.getPartTrans());
                if (partTranSaved != null) {
                    standingOrderExecution.getPartTrans().add(partTranSaved);
                    standingOrderExecution.setTotalAmount(partTran.getTransactionAmount());

                    standingOrderExecutionRepo.save(standingOrderExecution);
                } else {
                    standingOrderExecution.setTotalAmount(0.0);
                    standingOrderExecutionRepo.save(standingOrderExecution);
                }
            }
            standingorderheader.setNextRunDate(datesCalculator.addDate(date, 1, "MONTHS"));
            standingorderheaderRepo.save(standingorderheader);
            System.out.println("Executed a standing order");
        } else {
            registerNewFail(standingorderheader, response.getBody().getMessage().toString(), standingorderheader.getNextRunDate());
        }
//
    }


    //    TODO: Get Standing Order
    public void executeNew() {
        List<Standingorderheader> standingorderheaderList = standingorderheaderRepo.findByNextRunDate(new Date());
        if (standingorderheaderList.size()>0) {
            System.out.println(standingorderheaderList.size());
            for (int i=0; i<standingorderheaderList.size();  i++) {
                executeNew(standingorderheaderList.get(i));
            }
        }
    }

    private PartTran getPartTran(String destinationAccountNo, List<PartTran> partTrans) {
        for (PartTran partTran: partTrans){
            if (partTran.getAcid().equalsIgnoreCase(destinationAccountNo))
                return partTran;
        }
        return null;
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



    public EntityResponse initializeIncomeCollectionTransaction(Account loanAccount,
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
                    String transactionDescription="\"Interest Income from "+loanAccount.getAcid();
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
                        interestCollected.Account(loanAccount);

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






    public EntityResponse prepaySTOLoan(Account sourceAccount, Account loanAccount, Double amount, Standingorderheader standingorderheader) {
        System.out.println("INSIDE PREPAY STO LOAN");
        System.out.println("source account "+sourceAccount.getAcid()+" destination account: "+loanAccount.getAcid()+ " amount: "+amount);
        Optional<Account> loanAccountOptional = accountRepository.findByAccountId(loanAccount.getAcid());
        Optional<Account> repaymentAccountOptional;
        EntityResponse response = new EntityResponse();
        if (loanAccountOptional.isPresent() &&  loanAccountOptional.get().getAccountType().equalsIgnoreCase("LAA")) {
            String operativeAcid = null;

            repaymentAccountOptional = accountRepository.findByAccountId(sourceAccount.getAcid());

            Double interestPendingAmount1 = loanDemandRepository.getSumUnpaidInterest(loanAccount.getAcid());

            if (amount <= 0) {
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setMessage("Repayment amount cannot be "+amount);
                return response;
            }

            if (Math.abs(loanAccount.getAccountBalance()) + interestPendingAmount1 < amount ){
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setMessage("Repayment amount is more than loan balance MOORE.");
                return response;
            }


            if (accountRepository.getAvailableBalance(sourceAccount.getAcid()) +interestPendingAmount1 < amount) {
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setMessage("There is NOOO enough amount in repayment account");
                return response;

            }

            Double interestPendingAmount = loanDemandRepository.getSumUnpaidInterest(loanAccount.getAcid());

            Double interestAmountToTransact = Math.min(interestPendingAmount, amount);

            Double principalAmountToTransact = amount - interestAmountToTransact;


            TransactionHeader tranHeader= createTransactionHeader("KES",
                    "Loan repayment",
                    amount,
                    sourceAccount.getAcid(),
                    loanAccount.getAcid(),
                    "Normal");

            tranHeader.getPartTrans().clear();
            tranHeader.setPartTrans(new ArrayList<>());

            if (interestAmountToTransact > 0.01) {
                TransactionHeader tranHeaderForInterest = createTransactionHeader("KES",
                        "STO Interest repayment",
                        interestAmountToTransact,
                        sourceAccount.getAcid(),
                        loanAccount.getAcid(),
                        "Interest");
                tranHeader.getPartTrans().addAll(tranHeaderForInterest.getPartTrans());
            }

            if (principalAmountToTransact > 0.01) {
                TransactionHeader tranHeaderForPrincipal = createTransactionHeader("KES",
                        "STO Principal repayment",
                        principalAmountToTransact,
                        sourceAccount.getAcid(),
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
                String productCode = accountRepository.findAccountByProductCode(loanAccount.getAcid());
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

                    initializeIncomeCollectionTransaction(loanAccount, drAcid, crAcid, interestAmountToTransact);
                }
            }
            return transactionRes;
        } else {
            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            response.setMessage("Loan account not found");
            return response;
        }
    }

}