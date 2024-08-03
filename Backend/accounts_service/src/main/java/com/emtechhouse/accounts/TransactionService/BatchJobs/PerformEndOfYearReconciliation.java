package com.emtechhouse.accounts.TransactionService.BatchJobs;

import com.emtechhouse.accounts.Models.Accounts.Account;
import com.emtechhouse.accounts.Models.Accounts.AccountRepository;
import com.emtechhouse.accounts.TransactionService.Requests.MinistatementInterface;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.*;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.PartTrans.PartTran;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.Month;


import static com.emtechhouse.accounts.Utils.CONSTANTS.RECONCILE_ACCOUNTS;

@Slf4j
@Configuration
@Component
public class PerformEndOfYearReconciliation {
    @Autowired
    private TranHeaderService tranHeaderService;
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionsController controller;

    @Autowired
    private TranHeaderRepository tranrepo;

    @Autowired
    private NewTransactionService newTransactionService;

    @Autowired
    private TransactionProcessing transactionProcessing;

    // Main method for the end-of-year account reconciliation process
    @Transactional
    public void endOfYearReconciliation() {

        // To hold The Transaction Codes That will be Verifed and Posted
        List<String> expenseTransactionCodes = new ArrayList<>();
        List<String> incomesTransactionCodes = new ArrayList<>();

        // Step 1: Fetch expenses with GL code 40
        List<Account> expenseAccounts = accountRepository.findExpensesByGlCode("40", CONSTANTS.NO);

        // Step 2: Fetch expenseAccountsincomes with GL code 30
        List<Account> incomeAccounts = accountRepository.findIncomesByGlCode("30", CONSTANTS.NO);

        // Step 3: Process expense accounts
        expenseTransactionCodes = processExpenseAccounts(expenseAccounts);

        // Step 4: Process income accounts
        incomesTransactionCodes = processIncomeAccounts(incomeAccounts);

        // Step 5: Verify Transactions To Prepare them To For Posting
        verifyAndLogReconciliationResults(expenseTransactionCodes, incomesTransactionCodes);

        // Step 6: Post the Transactions To Update the Account Balances
        postReconciliationTransactions(expenseTransactionCodes, incomesTransactionCodes);
    }

    // Helper method to process expense accounts
    private static final String END_OF_YEAR_DATE = "2022-12-31";

    public List<String> processExpenseAccounts(List<Account> expenseAccounts) {

        List<TransactionHeader> reconTransactions = new ArrayList<>();
        List<String> transactionCodes = new ArrayList<>();
        for (Account account : expenseAccounts) {

            System.out.println();
            // Fetch the expense account balance at the end of the year
            Double accountBalance = tranrepo.getExpenseBalanceAtEndOfYear(account.getAcid(), END_OF_YEAR_DATE);

            System.out.println(account.getAcid() + " Balance at " + END_OF_YEAR_DATE + ": " + accountBalance);

//          Check if the balance object is not null before accessing its properties
//          If balance is not zero, create an end-of-year reconciliation entity
            if (accountBalance != null && accountBalance != 0) {
                TransactionHeader endOfYearReconciliation = createEndOfYearReconciliation(account, accountBalance > 0 ? "Debit" : "Credit", "250007", Math.abs(accountBalance));
                reconTransactions.add(endOfYearReconciliation);

                tranrepo.save(endOfYearReconciliation);

                // Save the transaction Code the Reconciliation Transaction
                transactionCodes.add(endOfYearReconciliation.getTransactionCode());
            }
        }

        systemTransactions(reconTransactions);
        System.out.println("Expense accounts processed!");
        return transactionCodes;
    }






    // Helper method to process income accounts
    public List<String> processIncomeAccounts(List<Account> incomeAccounts) {

        List<TransactionHeader> reconTransactions = new ArrayList<>();
        List<String> transactionCodes = new ArrayList<>();
        // Process each income account
        for (Account account : incomeAccounts) {
            // Fetch the income account balance at the end of the year
            Double accountBalance = tranrepo.getIncomeBalanceAtEndOfYear(account.getAcid(), END_OF_YEAR_DATE);

            System.out.println(account.getAcid() + " Balance at " + END_OF_YEAR_DATE + ": " + accountBalance);

//          Check if the balance object is not null before accessing its properties
//          If balance is not zero, create an end-of-year reconciliation entity
            if (accountBalance != null && accountBalance != 0) {
                TransactionHeader endOfYearReconciliation = createEndOfYearReconciliation(account, accountBalance < 0 ? "Credit" : "Debit", "250007", Math.abs(accountBalance));
                reconTransactions.add(endOfYearReconciliation);
                tranrepo.save(endOfYearReconciliation);

                // Save the transaction Code the Reconciliation Transaction
                transactionCodes.add(endOfYearReconciliation.getTransactionCode());
            }
        }

        systemTransactions(reconTransactions);
        System.out.println("Income accounts processed!");
        return transactionCodes;
    }



    // Helper method to create an end-of-year reconciliation entity
    public TransactionHeader createEndOfYearReconciliation(Account account, String partTranType, String creditAcid, Double balance) {
        LocalDateTime endOfYearDateTime = LocalDateTime.of(2022, Month.DECEMBER, 31, 23, 59);
        Date endOfYearDate = Date.from(endOfYearDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant());

        TransactionHeader transactionHeader = new TransactionHeader();
        transactionHeader.setEntityId("001");
        transactionHeader.setTransactionDate(endOfYearDate);
        transactionHeader.setCurrency("KES");
        transactionHeader.setTransactionType(RECONCILE_ACCOUNTS);
        transactionHeader.setEnteredBy("SYSTEM");
        transactionHeader.setEnteredFlag(CONSTANTS.YES);
        transactionHeader.setEnteredTime(endOfYearDate);
        transactionHeader.setTransactionCode(tranHeaderService.generatecSystemCode(6));

        // Set additional fields if needed

        // Create part transactions
        PartTran partTran1 = new PartTran();
        partTran1.setCurrency("KES");
        partTran1.setAcid(account.getAcid());
        partTran1.setIsoFlag('S');
        partTran1.setTransactionDate(endOfYearDate);
        partTran1.setExchangeRate("1");
        partTran1.setPartTranType(partTranType);
        partTran1.setTransactionAmount(balance);
        partTran1.setTransactionParticulars(CONSTANTS.RECONCILIATION_TRANSACTION_PARTICULARS);

        PartTran partTran2 = new PartTran();
        partTran2.setCurrency("KES");
        partTran2.setAcid(creditAcid);
        partTran2.setIsoFlag('S');
        partTran2.setTransactionDate(endOfYearDate);
        partTran2.setExchangeRate("1");
        partTran2.setPartTranType((partTranType.equals("Debit")) ? "Credit" : "Debit");
        partTran2.setTransactionAmount(balance);
        partTran2.setTransactionParticulars(CONSTANTS.RECONCILIATION_TRANSACTION_PARTICULARS);

        // Add part transactions to the list
        List<PartTran> partTrans = new ArrayList<>();
        partTrans.add(partTran1);
        partTrans.add(partTran2);

        // Set part transactions for the TransactionHeader object
        transactionHeader.setPartTrans(partTrans);

        return transactionHeader;
    }
    // Helper method to verify balances and log reconciliation results
    private void verifyAndLogReconciliationResults(List<String> expenseTransactionCodes, List<String> incomeTransactionCodes) {
        // Verify the reconciliation transactions of the expenses accounts
        for(String expenseTransactionCode: expenseTransactionCodes) {
            System.out.println("Verify: " + expenseTransactionCode);
            verify(expenseTransactionCode);
        }

        // Verify the reconciliation transactions of the income accounts
        for(String incomeTransactionCode: incomeTransactionCodes) {
            System.out.println("Verify: " + incomeTransactionCode);
            verify(incomeTransactionCode);
        }
    }

    // Helper method to run the system transaction
    private void systemTransactions(List<TransactionHeader> reconciliations) {
        log.info("Initiating system transactions...");

        for (TransactionHeader reconciliation : reconciliations) {
            try {
                // Convert EndOfYearReconciliation to TransactionHeader
//                TransactionHeader transactionHeader = convertToTransactionHeader(reconciliation);

                // Execute system transaction
                ResponseEntity<EntityResponse> response = controller.systemTransaction2(reconciliation);
                EntityResponse res = response.getBody();
                log.info("Response Message: " + res.getMessage());
                log.info("Status code: " + res.getStatusCode());
            } catch (Exception e) {
                log.error("Error: " + e.getMessage());
            }
        }
    }

    private void verify(String transactionCode) {
        Optional<TransactionHeader> transactionHeader = tranrepo.findByTransactionCode(transactionCode);
        if (transactionHeader.isPresent()) {
            if (transactionHeader.get().getVerifiedFlag() == 'N') {
                TransactionHeader transactionHeader1 = transactionHeader.get();
                transactionHeader1.setVerifiedFlag('Y');
                transactionHeader1.setVerifiedBy("System");
                transactionHeader1.setApprovalSentFlag('N');
                transactionHeader1.setVerifiedTime(new Date());
                transactionHeader1.setStatus(CONSTANTS.VERIFIED);

                transactionHeader1.setVerifiedFlag_2('Y');
                transactionHeader1.setVerifiedBy_2("System");
                transactionHeader1.setApprovalSentFlag('N');
                transactionHeader1.setStatus(CONSTANTS.VERIFIED);
                transactionHeader1.setVerifiedTime_2(new Date());
                tranrepo.save(transactionHeader1);

            }
        } else {
            System.out.println("Transaction with transaction code " + transactionCode + " Not found");
        }
    }



    private void postReconciliationTransactions(List<String> expenseTransactionCodes, List<String> incomeTransactionCodes) {
        for(String expenseTransactionCode: expenseTransactionCodes) {
            System.out.println("Post: " + expenseTransactionCode);
            post(expenseTransactionCode);
        }
        for(String incomeTransactionCode: incomeTransactionCodes) {
            System.out.println("Post: " + incomeTransactionCode);
            post(incomeTransactionCode);
        }
    }
    private void post(String transactionCode) {
        Optional<TransactionHeader> transactionHeader = tranrepo.findByTransactionCode(transactionCode);
        if (transactionHeader.isPresent()) {
//                check if transaction is already posted
            if (transactionHeader.get().getPostedFlag() == 'N') {
//                    if (transactionHeader.get().getAcknowledgedFlag() == null){
//                        transactionHeader.get().setAcknowledgedFlag('N');
//                    }
                if (transactionHeader.get().getVerifiedFlag() == 'Y' && transactionHeader.get().getVerifiedFlag_2() == 'Y') {
                    //                check if transaction is verified
                    TransactionHeader transactionHeader1 = transactionHeader.get();
                    transactionHeader1.setPostedFlag('Y');
                    transactionHeader1.setPostedBy("System");
                    transactionHeader1.setStatus(CONSTANTS.POSTED);
                    transactionHeader1.setPostedTime(new Date());
                    if (transactionHeader1.getReversedFlag() == 'Y') {
                        Optional<TransactionHeader> reversedHeaderOptional = tranrepo.findByTransactionCode(transactionHeader1.getReversalTransactionCode());
                        if (reversedHeaderOptional.isPresent()) {
                            TransactionHeader reversedHeader = reversedHeaderOptional.get();
                            reversedHeader.setReversalPostedFlag('Y');
                            transactionHeader1.setReversalPostedFlag('Y');
                            tranrepo.save(reversedHeader);
                        }
                    }
                    tranrepo.save(transactionHeader1);
//                Update account balance
                    transactionProcessing.updateAccountBalances(transactionHeader1.getPartTrans());
//                Send notifications
                    System.out.println("Transaction posted successfully and account balance updated successfully.");
                } else {
                    System.out.println("Transaction with transaction code " + transactionCode + " Not fully verified! Verification 1: " + transactionHeader.get().getVerifiedFlag() + " Verification 2: " + transactionHeader.get().getVerifiedFlag_2());
                }
            } else {
                System.out.println("Transaction with transaction code " + transactionCode + " is already posted!");
            }
        } else {
            System.out.println("Transaction with transaction code " + transactionCode + " Not found");
        }
    }

}