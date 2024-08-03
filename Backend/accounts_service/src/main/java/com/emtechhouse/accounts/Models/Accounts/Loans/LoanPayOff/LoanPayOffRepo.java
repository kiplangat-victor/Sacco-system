package com.emtechhouse.accounts.Models.Accounts.Loans.LoanPayOff;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface LoanPayOffRepo extends JpaRepository<LoanPayOffInfo, Long> {

    @Query(value = "select account_balance from accounts where acid=:acid", nativeQuery = true)
    Double fetchAccountBalance(String acid);


    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE `loan_pay_off_info` SET `pay_off_amount`=:amount WHERE `id`=:sn", nativeQuery = true)
    void updatePayOffAmount(Double amount, Long sn);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE `loan_pay_off_info` SET `transaction_code`=:transactionCode WHERE `id`=:sn", nativeQuery = true)
    void updateTransactionCode(String transactionCode, Long sn);
}
