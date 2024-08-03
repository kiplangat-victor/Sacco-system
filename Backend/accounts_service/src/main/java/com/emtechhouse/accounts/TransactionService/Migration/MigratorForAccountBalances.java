package com.emtechhouse.accounts.TransactionService.Migration;


import com.emtechhouse.accounts.TransactionService.Migration.AccountsBalancesRequest.AccountBalancesRepositoryForMigration;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.PartTrans.PartTran;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TranHeaderRepository;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TranHeaderService;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TransactionHeader;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("migration")
@Slf4j
public class MigratorForAccountBalances {
    @Autowired
    private TranHeaderService transactionHeaderService;

    @Autowired
    private AccountBalancesRepositoryForMigration accountBalancesRepositoryForMigration;


    @Autowired
    private TranHeaderRepository transactionHeaderRepository;


    private final String TREASURY_ACCOUNT = "130013";
    private final String INTEREST_PROVISION_ACCOUNT = "402009";

    private final String migrationBatchCode = "2023-MIGRATION-EMTECH";
    private final String interestProvisionBatchCode = "UNMIGRATED-INTEREST-PROVISION";
    @Transactional
    @PostMapping("migrate-balances-two")
    public ResponseEntity<EntityResponse> postBalancesInTransaction() throws MessagingException, IOException {
        double totalMigrationAmount  = accountBalancesRepositoryForMigration.getAccountBalance(TREASURY_ACCOUNT);
        TransactionHeader transactionHeader = new TransactionHeader();
        EntityResponse response = new EntityResponse();

        List<AccountBalancesRepositoryForMigration.AccountBalance> accountBalances = accountBalancesRepositoryForMigration.getAllCustomerAccounts();

        //Collecting all partrans
        List<PartTran> allPartran = new ArrayList<>();

        String particulars = "Account Migration";

        for (AccountBalancesRepositoryForMigration.AccountBalance accountBalance : accountBalances) {
            totalMigrationAmount =  totalMigrationAmount - accountBalance.getAccount_balance();
            System.out.println(accountBalance.getAcid()+"  "+accountBalance.getAccount_type()+" "+ accountBalance.getAccount_balance());
            if (accountBalance.getProduct_code().equalsIgnoreCase("SA01")) {
                particulars = "Migrate share balance";
            }else if (accountBalance.getProduct_code().equalsIgnoreCase("SA02")) {
                particulars = "Migrated Non-withdrawable Account Balance";
            }else if (accountBalance.getProduct_code().equalsIgnoreCase("SA06")) {
                particulars = "Migrated loan repayment amount";
            }else if (accountBalance.getProduct_code().equalsIgnoreCase("LA20")) {
                particulars = "loan balance";
            }else if (accountBalance.getProduct_code().equalsIgnoreCase("LA30")) {
                particulars = "Migrated from flat rate loan";
            }
            PartTran p = new PartTran();
            p.setBatchCode(migrationBatchCode);
            p.setTransactionDate(new Date());
            p.setTransactionParticulars(particulars);
            p.setAcid(accountBalance.getAcid());
            p.setAccountType(accountBalance.getAccount_type());
            p.setCurrency("KES");
            p.setIsoFlag('M');
            if (accountBalance.getAccount_balance() <  0) {
                p.setTransactionAmount(Math.abs(accountBalance.getAccount_balance()));
                p.setPartTranType("Debit");
            } else {
                p.setTransactionAmount(accountBalance.getAccount_balance());
                p.setPartTranType("Credit");
            }
            p.setAccountBalance(0);
            p.setExchangeRate("1");
            allPartran.add(p);


            PartTran p2 = new PartTran();
            p2.setBatchCode(migrationBatchCode);
            p2.setTransactionDate(new Date());
            p2.setTransactionParticulars("Account migration for "+accountBalance.getAcid());
            p2.setAcid(TREASURY_ACCOUNT);
            p2.setAccountType("OAB");
            p2.setCurrency("KES");
            p2.setIsoFlag('M');

            if (accountBalance.getAccount_balance() <  0) {
                p2.setTransactionAmount(Math.abs(accountBalance.getAccount_balance()));
                p2.setPartTranType("Credit");
            } else {
                p2.setTransactionAmount(accountBalance.getAccount_balance());
                p2.setPartTranType("Debit");
            }

            p2.setAccountBalance(0);
            p2.setExchangeRate("1");
            allPartran.add(p2);
            System.out.println(totalMigrationAmount);
        }

        for (PartTran partTran: allPartran) {
            System.out.println(partTran.getAcid()+"  "+partTran.getPartTranType()+" "+ partTran.getTransactionAmount());
        }

        transactionHeader.setPartTrans(allPartran);
        transactionHeader.setBatchCode(migrationBatchCode);

//        accountBalancesRepositoryForMigration.updateAccountBalance(TREASURY_ACCOUNT, totalMigrationAmount);

        transactionHeader.setEnteredBy("Migrated");
        transactionHeader.setEnteredFlag(CONSTANTS.YES);
        transactionHeader.setEnteredTime(new Date());

        transactionHeader.setPostedBy("Migrated");
        transactionHeader.setPostedFlag(CONSTANTS.YES);
        transactionHeader.setTransactionType("Migration");
        transactionHeader.setPostedTime(new Date());


        transactionHeader.setTransactionDate(new Date());

        transactionHeader.setDeletedFlag(CONSTANTS.NO);
        transactionHeader.setVerifiedBy("Migrated");
        transactionHeader.setVerifiedFlag('Y');
        transactionHeader.setStatus("Success");
        transactionHeader.setCurrency("KES");
        transactionHeader.setEntityId("001");
        System.out.println(transactionHeader);

        EntityResponse res = transactionHeaderService.saveTransactionHeader(transactionHeader);
        response.setMessage(res.getMessage());
        response.setStatusCode(res.getStatusCode());
        response.setEntity(res.getEntity());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

//@Transactional
//@PostMapping("provision-transaction")
//    public ResponseEntity<EntityResponse> postProvisionBalances() throws MessagingException, IOException {
//        TransactionHeader transactionHeader = new TransactionHeader();
//
//        double totalProvision = 0;
//        EntityResponse response = new EntityResponse();
//
//        List<AccountBalancesRepositoryForMigration.LoanProvision> interestProvisions = accountBalancesRepositoryForMigration.getInterestProvisions();
//
//        //Collecting all partrans
//        List<PartTran> allPartran = new ArrayList<>();
//
//        for (AccountBalancesRepositoryForMigration.LoanProvision loanProvision : interestProvisions) {
//            totalProvision = totalProvision + loanProvision.getProvision_amount();
//            System.out.println(loanProvision.getAcid()+"  "+loanProvision.getOperative_acount_id()+" "+ loanProvision.getProvision_amount());
//            PartTran p = new PartTran();
//            p.setBatchCode(interestProvisionBatchCode);
//            p.setTransactionDate(new Date());
//            p.setTransactionParticulars("Provision for unmigrated interest for loan "+loanProvision.getAcid());
//            p.setAcid(loanProvision.getOperative_acount_id());
//            p.setAccountType(loanProvision.getOperative_acount_id().substring(5,7));
//            p.setCurrency("KES");
//            p.setIsoFlag('M');
//
//            p.setTransactionAmount(loanProvision.getProvision_amount());
//            p.setPartTranType("Credit");
//
//            p.setAccountBalance(accountBalancesRepositoryForMigration.getAccountBalance(loanProvision.getOperative_acount_id()));
//            p.setExchangeRate("1");
//            allPartran.add(p);
//
//
//            PartTran p2 = new PartTran();
//            p2.setBatchCode(interestProvisionBatchCode);
//            p2.setTransactionDate(new Date());
//            p2.setTransactionParticulars("Provision for unmigrated interest for account "+loanProvision.getAcid());
//            p2.setAcid(INTEREST_PROVISION_ACCOUNT);
//            p2.setAccountType(loanProvision.getOperative_acount_id().substring(5,7));
//            p2.setCurrency("KES");
//            p2.setIsoFlag('M');
//
//            p2.setTransactionAmount(Math.abs(loanProvision.getProvision_amount()));
//            p2.setPartTranType("Debit");
//
//            p2.setAccountBalance(0);
//            p2.setExchangeRate("1");
//            allPartran.add(p2);
//        }
//
//        for (PartTran partTran: allPartran) {
//            System.out.println(partTran.getAcid()+"  "+partTran.getPartTranType()+" "+ partTran.getTransactionAmount());
//        }
//
//        transactionHeader.setPartTrans(allPartran);
//        transactionHeader.setBatchCode(interestProvisionBatchCode);
//
//        for (AccountBalancesRepositoryForMigration.LoanProvision loanProvision : interestProvisions) {
//            accountBalancesRepositoryForMigration.updateAccountBalance(loanProvision.getOperative_acount_id(), loanProvision.getProvision_amount()+ accountBalancesRepositoryForMigration.getAccountBalance(loanProvision.getOperative_acount_id()));
//        }
//        accountBalancesRepositoryForMigration.updateAccountBalance(INTEREST_PROVISION_ACCOUNT, -1*totalProvision);
//
//        transactionHeader.setEnteredBy("Migrated");
//        transactionHeader.setEnteredFlag(CONSTANTS.YES);
//        transactionHeader.setEnteredTime(new Date());
//
//        transactionHeader.setPostedBy("Migrated");
//        transactionHeader.setPostedFlag(CONSTANTS.YES);
//        transactionHeader.setTransactionType("Migration");
//        transactionHeader.setPostedTime(new Date());
//
//
//        transactionHeader.setTransactionDate(new Date());
//
//        transactionHeader.setDeletedFlag(CONSTANTS.NO);
//        transactionHeader.setVerifiedBy("Migrated");
//        transactionHeader.setVerifiedFlag('Y');
//        transactionHeader.setStatus("Success");
//        transactionHeader.setCurrency("KES");
//        transactionHeader.setEntityId("001");
//        System.out.println(transactionHeader);
//        EntityResponse res = transactionHeaderService.saveTransactionHeader(transactionHeader);
//        response.setMessage(res.getMessage());
//        response.setStatusCode(res.getStatusCode());
//        response.setEntity(res.getEntity());
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }


//    @PutMapping("update/operative/ac/balances")
//    public ResponseEntity<EntityResponse> updateOpAcBal(){
//        try {
//            log.info("updating balances");
//            accountBalancesRepositoryForMigration.updateOperativeAccountBalances();
//            log.info("Balances updated");
//            return new ResponseEntity<>( HttpStatus.OK);
//
//        }catch (Exception e){
//            log.info("Exception {} "+e);
//            return null;
//        }
//    }

    @DeleteMapping("Delete/transaction")
    public ResponseEntity<EntityResponse> deleteTransactions(){
        try {

//            List<TransactionHeaderIdDto> ids = accountBalancesRepositoryForMigration.getPartranIdTodelete1();
//            log.info(" operative transactions size :: "+ids.size());
            List<TransactionHeaderIdDto> ids2 = accountBalancesRepositoryForMigration.getPartranIdTodelete2();
            log.info(" Loan transactions size :: "+ids2.size());
//            ids.forEach(id->{
//                Optional<TransactionHeader> transaction = transactionHeaderRepository.findById(id.getId());
//                if(transaction.isPresent()){
//                    transactionHeaderRepository.deleteById(id.getId());
//                    log.info("Deleted successful.");
//                }else {
//                    log.info("Account not present.");
//                }
//            });
//            log.info("completed deleting operative account transactions");

            ids2.forEach(id->{
                Optional<TransactionHeader> transaction = transactionHeaderRepository.findById(id.getId());
                if(transaction.isPresent()){
                    transactionHeaderRepository.deleteById(id.getId());
                    log.info("Deleted successful.");
                }else {
                    log.info("Account not present.");
                }
            });

            log.info("completed deleting loan account transactions");
            return new ResponseEntity<>( HttpStatus.OK);

        }catch (Exception e){
            log.info("Exception {} "+e);
            return null;
        }
    }

}