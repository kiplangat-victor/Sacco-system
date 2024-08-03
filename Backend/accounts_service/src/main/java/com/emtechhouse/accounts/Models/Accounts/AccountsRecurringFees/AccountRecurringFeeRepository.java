package com.emtechhouse.accounts.Models.Accounts.AccountsRecurringFees;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Repository
@Transactional
public interface AccountRecurringFeeRepository  extends JpaRepository<AccountRecurringFee, Long> {
    @Query(value = "SELECT * FROM account_recurring_fee WHERE DATE(next_collection_date) <= CURDATE() AND current_timestamp <  end_date and account_id_fk in (select id from accounts where account_type = 'sba' and  (account_balance-book_balance-1) > 0 ) AND account_id_fk IN (SELECT id FROM accounts WHERE account_type = 'sba' and account_balance - book_balance > 0 )  order by last_run_attempt_date asc limit 5000 ", nativeQuery = true)
    List<AccountRecurringFee> findAllDue();

    @Query(value = "SELECT distinct ac.acid as account FROM accounts ac " +
            " WHERE product_code = :productCode AND ac.id NOT IN (SELECT account_id_fk  FROM account_recurring_fee WHERE event_code = :eventCode) ", nativeQuery = true)
    List<String> getNewAccountRecurringFees(String productCode, String eventCode);

    @Modifying(clearAutomatically = true)
    @Query(value = "update account_recurring_fee SET `next_collection_date`=:date WHERE `id`=:sn", nativeQuery = true)
    void updateNextRunDate(Date date, Long sn);

    @Modifying(clearAutomatically = true)
    @Query(value = "update account_recurring_fee SET `last_run_attempt_date`=:date WHERE `id`=:sn", nativeQuery = true)
    void updateLastRunAttemptDate(Date date, Long sn);
}