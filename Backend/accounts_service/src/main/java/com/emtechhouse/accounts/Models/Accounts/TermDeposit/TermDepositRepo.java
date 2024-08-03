package com.emtechhouse.accounts.Models.Accounts.TermDeposit;

import com.emtechhouse.accounts.Models.Accounts.Loans.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
@Transactional
public interface TermDepositRepo extends JpaRepository<TermDeposit, Long> {
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE `term_deposit` SET `accrual_last_date`=:date WHERE`id`=:id", nativeQuery = true)
    void updateLastAccrualDate(Date date, Long id);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE `term_deposit` SET `term_deposit_status`=:status WHERE `id`=:id", nativeQuery = true)
    void updateTermDepositStatus(String status, Long id);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE `term_deposit` SET `sum_accrued_amount`=`sum_accrued_amount`+:amt WHERE `id`=:id", nativeQuery = true)
    void updateTermDepositSumAccruedAmount(Double amt, Long id);

    @Query(value = "SELECT * FROM term_deposit t JOIN accounts a ON t.`account_id`=a.id WHERE t.`term_deposit_status`='APPROVED' AND t.pause_accrual_flag='N' AND date(t.`accrual_last_date`) < date(NOW()) AND a.delete_flag='N' AND a.verified_flag='Y' AND a.account_status='ACTIVE'", nativeQuery = true)
    List<TermDeposit> getAccountsToAccrue();

    @Query(value = "SELECT a.acid FROM term_deposit t JOIN accounts a ON t.`account_id`=a.id WHERE t.maturity_date < CURDATE()  AND a.delete_flag='N' AND a.verified_flag='Y' AND a.account_status='ACTIVE' AND a.account_balance > 0 ", nativeQuery = true)
    List<String> getMaturedFDs();
}

