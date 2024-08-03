package com.emtechhouse.accounts.TransactionService.Migration.AccountsBalancesRequest;

import com.emtechhouse.accounts.TransactionService.Migration.TransactionHeaderIdDto;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TransactionHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Transactional
public interface AccountBalancesRepositoryForMigration extends JpaRepository<TransactionHeader, Long> {
    @Query(value = "SELECT id, account_balance, acid, account_type, product_code FROM accounts where account_balance <> 0 and account_type = 'SBA'", nativeQuery = true)
    public List<AccountBalance> getAllSavingAccountBalances();
//    @Query(value = "SELECT id, account_balance, acid, account_type, product_code FROM accounts where account_balance <> 0 and (account_type = 'SBA' or account_type = 'LAA')", nativeQuery = true)
//    List<AccountBalance> getAllCustomerAccounts();


    // TODO: 3/30/2023  impact only the remigrated loan accounts
    @Query(value = "SELECT id, account_balance, acid, account_type, product_code FROM accounts where acid IN(SELECT acid FROM loan_migration_closed_ac)", nativeQuery = true)
    List<AccountBalance> getAllCustomerAccounts();


    @Query(value = "SELECT id, account_balance, acid, account_type, product_code FROM accounts where account_balance <> 0 and account_type = 'LAA'", nativeQuery = true)
    public List<AccountBalance> getAllLoanAccountBalances();

    @Query(value = "SELECT * FROM migration_interest_provision where provision_amount <> 0 ", nativeQuery = true)
    public List<LoanProvision> getInterestProvisions();

    @Query(value = "SELECT sum(account_balance) AS balance FROM accounts ", nativeQuery = true)
    public Double getTotalBalances();
    @Query(value = "SELECT account_balance AS balance FROM accounts where acid = :operative_acount_id", nativeQuery = true)

    public double getAccountBalance(@Param("operative_acount_id") String operative_acount_id);

    @Modifying
    @Query(value = "UPDATE accounts SET account_balance = :balance where acid = :acid", nativeQuery = true)
    void updateAccountBalance(@Param("acid") String acid, @Param("balance") double balance);

    interface AccountBalance {
        Long getId();
        double getAccount_balance();
        String getAcid();
        String getAccount_type();
        String getProduct_code();
    }

    interface LoanProvision {
        Double getProvision_amount();
        String getAcid();
        String getOperative_acount_id();
    }


    @Modifying
    @Query(value = "UPDATE accounts a set a.account_balance=(SELECT sum(`tran_amount`) as amount FROM `part_tran` WHERE `part_tran_type`='Credit' AND acid IN(SELECT operative_acount_id FROM loan_migration) AND acid=a.acid)  WHERE a.acid IN (SELECT acid as pacid FROM `part_tran` WHERE `part_tran_type`='Credit' AND acid IN(SELECT operative_acount_id FROM loan_migration) GROUP BY `acid`)", nativeQuery = true)
    void updateOperativeAccountBalances();
    @Query(value = "SELECT `transaction_header_id` AS id FROM part_tran WHERE acid IN (SELECT operative_acount_id FROM loan_migration) AND part_tran_type='Debit'", nativeQuery = true)
    public List<TransactionHeaderIdDto> getPartranIdTodelete1();

    @Query(value = "SELECT `transaction_header_id` AS id FROM part_tran WHERE acid IN (SELECT acid FROM loan_migration_closed_ac) ", nativeQuery = true)
    public List<TransactionHeaderIdDto> getPartranIdTodelete2();
}
