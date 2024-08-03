package com.emtechhouse.accounts.AutomatedProcesses;

import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TransactionHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AutoRepo extends JpaRepository<TransactionHeader, Long> {
//    @Query(value =
//            "select transaction_code from dtd where sn in (select transaction_header_id from part_tran where tran_particulars like '%interest due%' and part_tran_type = 'debit' and tran_date > '2023-04-25' and acid in (SELECT acid FROM accounts WHERE account_type = 'laa' and account_balance < -100 ) and acid in (select acid from part_tran where  tran_particulars like '%interest due%' and part_tran_type = 'debit' and tran_date < '2023-04-25' and acid in (SELECT acid FROM accounts WHERE account_type = 'laa' and account_balance < -100 ) )  )  and  transaction_code  not in (select  transaction_code  from loan_demand where created_on < DATE_ADD(demand_date, INTERVAL 1 DAY)  and transaction_code is not null);"
//            , nativeQuery = true)
    @Query(value =
            " select transaction_code from dtd where reversal_posted_flag = 'N' and sn in (select transaction_header_id from part_tran where acid in (select acid from accounts where account_type = 'laa') and part_tran_type = 'credit' and tran_date > '2023-06-26');"
            , nativeQuery = true)
    List<String> findTransactionsToReverse();
}