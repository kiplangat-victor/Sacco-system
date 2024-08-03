package com.emtechhouse.accounts.AutomatedProcesses;

import com.emtechhouse.accounts.Models.Accounts.Account;
import com.emtechhouse.accounts.Models.Accounts.AccountRepository;
import com.emtechhouse.accounts.Models.Accounts.AccountsRecurringFees.AccountRecurringFeeService;
import com.emtechhouse.accounts.Models.Accounts.LienComponent.Lien;
import com.emtechhouse.accounts.Models.Accounts.LienComponent.LienService;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanAccrual.LoanAccrualService;
//import com.emtechhouse.accounts.Models.Accounts.Loans.LoanBooking.LoanBookingService;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.DemandGenerationService;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.DemandSatisfaction.AssetClassificationService;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.DemandSatisfaction.DemandSatisfactionService;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.LoanDemandRepository;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanFees.FeeDemandGeneration;
import com.emtechhouse.accounts.Models.Accounts.SavingContributionInstructions.SavingContributionInstructionsService;
import com.emtechhouse.accounts.Models.Accounts.TermDeposit.TermDepositRepo;
import com.emtechhouse.accounts.Models.Accounts.TermDeposit.TermDepositService;
import com.emtechhouse.accounts.StandingOrdersComponent.StandingorderExecutor;
import com.emtechhouse.accounts.TransactionService.BatchJobs.PerformEndOfYearReconciliation;
import com.emtechhouse.accounts.TransactionService.EODProcess.PushToDB.EODStatusReportService;
import com.emtechhouse.accounts.TransactionService.EODProcess.performEOD.EODProcessService;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.NewTransactionService;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.PartTrans.PartTran;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TranHeaderRepository;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TransactionHeader;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TransactionsController;
import com.emtechhouse.accounts.Utils.CONSTANTS;
//import com.emtechhouse.accounts.TransactionService.BatchJobs.SchedulerConfig.EndOfYearReconciliation;
import com.emtechhouse.accounts.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
public class AutomatedService {
    @Value("${controls.pause-demands}")
    Boolean pauseDemands;
    @Value("${controls.pause-demands-satisfaction}")
    Boolean pauseDemandSatisfaction;
    @Value("${controls.runstos}")
    Boolean runstos;
    @Value("${controls.executerecurring}")
    Boolean executerecurring;
    @Value("${controls.initiaterecurring}")
    Boolean initiaterecurring;
    @Value("${controls.reverse-trans}")
    Boolean reverseTransactions;
    @Value("${data.backup.pause}")
    Boolean pauseBackup;
    @Value("${data.backup.folder}")
    String backupFolder;
    @Value("${spring.datasource.password}")
    String dbPassword;
    @Value("${spring.datasource.username}")
    String dbUsername;
    @Value("${spring.datasource.dbname}")
    String dbName;
    @Autowired
    private DemandGenerationService demandGenerationService;
    @Autowired
    private DemandSatisfactionService demandSatisfactionService;
    @Autowired
    private AutoRepo autoRepo;

    @Autowired
    private EODStatusReportService eodStatusReportService;

    @Autowired
    EODProcessService eodProcessService;

    @Autowired
    private LoanAccrualService loanAccrualService;

//    private LoanBookingService loanBookingService;
    @Autowired
    private FeeDemandGeneration feeDemandGeneration;
    @Autowired
    private SavingContributionInstructionsService savingContributionInstructionsService;

    @Autowired
    private PerformEndOfYearReconciliation performEndOfYearReconciliation;

    @Autowired
    private TransactionsController transactionsController;
    @Autowired
    private StandingorderExecutor standingorderExecutor;

    @Autowired
    private AssetClassificationService assetClassificationService;
    @Autowired
    private AccountRecurringFeeService accountRecurringFeeService;
    @Autowired
    private NewTransactionService newTransactionService;

    @Autowired
    private LienService lienService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TranHeaderRepository tranHeaderRepository;

    @Autowired
    private LoanDemandRepository loanDemandRepository;

    @Autowired
    private TermDepositService termDepositService;



    @Autowired
    private TermDepositRepo termDepositRepo;

    public void satisfyLiens() {
        System.out.println("About to satisfy liens");
        try {
            List<Lien> liens = lienService.activeLiens();
            System.out.println("Lien size is :: "+liens.size());
//            System.out.println(Arrays.deepToString(liens.toArray()));
            for (Lien lien: liens) {
//                System.out.println(lien);
                lienService.satisfyLien(lien);
//                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Catched Error {} " + e);
        }
    }

    public void saveEod() {
        try {
            System.out.println("Saving Eod Balances");
            LocalDate currentDate = LocalDate.now();

            String entityId = EntityRequestContext.getCurrentEntityId();

            String tranDate = currentDate.toString(); // Convert LocalDate to String
//            String tranDate = "2024-02-24";
            System.out.println("Transaction Date: "+tranDate);
            eodStatusReportService.fetchTodayTransactions(tranDate); // Pass the formatted date
            System.out.println("Done saving");
        } catch (Exception e) {
            log.info("Caught Error {} ", e); // Corrected log statement
        }
    }




    public void endOfYearReconciliation(){
        System.out.println("About to execute End Of Year Recon");
        performEndOfYearReconciliation.endOfYearReconciliation();
        System.out.println("Done executing End Of Year Recon");

    }

    public void generateDemands() {
       if (pauseDemands) return;
        System.out.println("About to generate demands");
        System.out.println("]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]");
//        System.cu
        try {
            demandGenerationService.generateDemandsForAllLoans();
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
        }
        System.out.println("Done generating demands");
    }

    public void executeStandingOrders() {
        if (!runstos) return;
        try {
            System.out.println("About to execute standing orders");
            standingorderExecutor.executeNew();
            System.out.println("Done executing standing orders");
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
        }
    }

    public void initiateRecurringFees() {
        if (!initiaterecurring)
            return;
        try {
            System.out.println("Initiating recurring fees");
            accountRecurringFeeService.initiateRecurringFees();
            System.out.println("Done Initiating recurring fees");
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
        }
    }


    public void executeRecurringFees() {
        if (!executerecurring)
            return;
        try {
            System.out.println("execute recurring fees");
            accountRecurringFeeService.executeRecurringFees();
            System.out.println("Done executing recurring fees");
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
        }
    }

    public void reverseSystemTransactions() {
        try {
            if (!reverseTransactions) return;
            System.out.println("execute reversals");
            List<String> transactionCodes = autoRepo.findTransactionsToReverse();
            System.out.println(transactionCodes.size());
            for (String string: transactionCodes) {
//                System.out.println(string);
                EntityResponse r =  newTransactionService.systemReverseTransaction(string, "AUTO", "001", true);
                System.out.println(r);
            }
            System.out.println("Done reversing");
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
        }
    }

    @Transactional
    public void generateFeeDemands() {
        try {
            System.out.println("About to demand fees");
//            feeDemandGeneration.startDemandingFees();
//            System.out.println("Done demanding fees");
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
        }
    }


//    @Scheduled(cron = "*/10 * * * * ?")
    public void  satisfyDemands() {
        System.out.println("About to satisfy demands");
        if (pauseDemandSatisfaction) return;
        try {
            demandSatisfactionService.satisfyAllDemands();
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
        }
    }

    public void  incomeInstructions() {
        System.out.println("About to execute income instructions");
        try {
            savingContributionInstructionsService.executeSavingInstructions();
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
        }
    }

    public void  classifyLoans() {
        System.out.println("About to classify demands");
//        if (pauseDemands) return;
        try {
            List<String> loansToClassify = loanDemandRepository.loansToClassify();
//            System.out.println(Arrays.deepToString(loansToClassify.toArray()));
            for (String loan: loansToClassify) {
                System.out.println("Classify "+loan);
//                loan = "KAH000041";
                assetClassificationService.classifyLoan(loan);
            }
            System.out.println("Done classifying");
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
        }
    }

    //    @Bean
    public void moveDividends() {
        List<Account> divAccounts = accountRepository.findLoadedDividendAccounts();
        for (Account divAccount: divAccounts) {
            Optional<Account> operative = accountRepository.findOperativeAccount(divAccount.getCustomerCode());
            if (operative.isPresent()) {
                System.out.println(divAccount.getCustomerCode());
                System.out.println("------------------------------------");
                System.out.println(divAccount.getAcid());
                System.out.println(operative.get().getAcid());
                TransactionHeader tranHeader= createTransactionHeader("KES",
                        "2022 Dividend after tax",
                        divAccount.getAccountBalance(),
                        divAccount.getAcid(),
                        operative.get().getAcid());
//                System.out.println(tranHeader);
                EntityResponse transactionRes= transactionsController.systemTransaction1(tranHeader).getBody();
                if(!transactionRes.getStatusCode().equals(HttpStatus.OK.value())) {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("Could not move money for  "+divAccount.getAccountType());
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity(tranHeader);
                    System.out.println(response);
                } else {
                    System.out.println("Transaction successful");
                }
            }
        }
    }

    public TransactionHeader createTransactionHeader(String loanCurrency,
                                                     String transDesc,
                                                     Double totalAmount,
                                                     String drAc,
                                                     String crAc) {
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
        drPartTran.setTransactionDate(new Date());
        drPartTran.setParttranIdentity("NORMAL");
        drPartTran.setTransactionParticulars(transDesc);
        drPartTran.setIsoFlag(CONSTANTS.YES);

        PartTran crPartTran = new PartTran();
        crPartTran.setPartTranType(CONSTANTS.CREDITSTRING);
        crPartTran.setTransactionAmount(totalAmount);
        crPartTran.setAcid(crAc);
        crPartTran.setCurrency(loanCurrency);
        crPartTran.setExchangeRate("");
        crPartTran.setParttranIdentity("NORMAL");
        crPartTran.setTransactionDate(new Date());
        crPartTran.setTransactionParticulars(transDesc);
        crPartTran.setIsoFlag(CONSTANTS.YES);

        List<PartTran> partTranList =new ArrayList<>();
        partTranList.add(drPartTran);
        partTranList.add(crPartTran);

        transactionHeader.setPartTrans(partTranList);
        return transactionHeader;
    }
    public void autoDumpDatabaseBackup() {
        System.out.println("About to dump backup");
        if (pauseBackup) return;
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            Date date = new Date();

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm");
            String dumpString = "mysqldump -u "+dbUsername+" --password='"+dbPassword+"' "+dbName+" > "+backupFolder+"sacco-" + formatter.format(date) + ".sql";
            System.out.println(dumpString);
            processBuilder.command("bash", "-c", dumpString);
            processBuilder.start();
            log.info("the dump was collected");
        } catch (Exception ex) {
            log.info("failed to auto dump with error {}", ex.getMessage());
        }
    }

    public void closeFixedDeposits() {
        List<String> termDeposits = termDepositRepo.getMaturedFDs();
        System.out.println("FD count: "+termDeposits.size());
        for (String acid: termDeposits) {
            List<EntityResponse> entityResponse = termDepositService.initiateTermDepositPayment(acid);
            System.out.println(Arrays.deepToString(entityResponse.toArray()));
        }
    }



    public List<EntityResponse> updateDormantAccounts() {
        try {
            log.info("Dormant account update in progress");

            // Use the repository method to find dormant accounts
            List<Account> dormantAccounts = accountRepository.findDormantAccounts();

            log.info("=======================Count = " + dormantAccounts.size() + " ==============================================");

            List<EntityResponse> responses = new ArrayList<>();

            if (!dormantAccounts.isEmpty()) {
                for (Account dormantAccount : dormantAccounts) {
                    String acid = dormantAccount.getAcid();
                    log.info("---------------------" + acid + "-----------------------");

                    // Update only the account_status
                    dormantAccount.setAccountStatus(CONSTANTS.DORMANT);

                    // Save the updated Account
                    accountRepository.save(dormantAccount);

                    // Add a response or log statement as needed
                    EntityResponse response = new EntityResponse();
                    response.setMessage("Account updated to DORMANT successfully: " + acid);
                    response.setStatusCode(HttpStatus.OK.value());
                    responses.add(response);
                }
            } else {
                EntityResponse response = new EntityResponse();
                response.setMessage("THERE ARE NO DORMANT ACCOUNTS TO UPDATE");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                responses.add(response);
            }

            return responses;
        } catch (Exception e) {
            log.error("Error updating dormant accounts: {} - {}", e.getStackTrace()[0].getLineNumber(), e.getMessage());
            // Handle response or throw an exception if necessary
            return null;
        }
    }


    public List<EntityResponse> updateAccountsStatus() {
        try {
            log.info("Dormant account update in progress");

            // Use the repository method to find  accounts status / active or closed
            List<Account> statusAccounts = accountRepository.findAccountsStatus();


            log.info("=======================Count = " + statusAccounts.size() + " ==============================================");

            List<EntityResponse> responses = new ArrayList<>();

            if (!statusAccounts.isEmpty()) {
                for (Account statusAccount : statusAccounts) {
                    String acid = statusAccount.getAcid();
                    log.info("---------------------" + acid + "-----------------------");

                    // Update only the account_status
                    statusAccount.setAccountStatus(CONSTANTS.ACTIVE);

                    // Save the updated Account
                    accountRepository.save(statusAccount);


                    // Add a response or log statement as needed
                    EntityResponse response = new EntityResponse();
                    response.setMessage("Account updated to ACTIVE successfully: " + acid);
                    response.setStatusCode(HttpStatus.OK.value());
                    responses.add(response);
                }
            } else {
                EntityResponse response = new EntityResponse();
                response.setMessage("THERE ARE NO DORMANT ACCOUNTS TO UPDATE");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                responses.add(response);

            }

            log.info("Updating loan status");
            accountRepository.updateLoanStatus();

            return responses;
        } catch (Exception e) {
            log.error("Error updating dormant accounts: {} - {}", e.getStackTrace()[0].getLineNumber(), e.getMessage());
            // Handle response or throw an exception if necessary
            return null;
        }
    }





}