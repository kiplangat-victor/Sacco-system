package com.emtechhouse.accounts.TransactionService.EODProcess.PushToDB;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EndOfDayBalanceRepo extends JpaRepository <EndOfDayBalance, Long>{

    @Query(value = "SELECT a.acid, sum(CASE WHEN pt.part_tran_type = 'Debit' THEN -1 * pt.tran_amount ELSE pt.tran_amount END) AS balance, :tranDate AS tranDate FROM part_tran pt " +
            "JOIN dtd ON pt.transaction_header_id = dtd.sn JOIN accounts a ON a.acid = pt.acid" +
            " WHERE dtd.deleted_flag = 'N' AND dtd.posted_flag = 'Y' " +
            "AND pt.tran_date <= :tranDate  GROUP BY a.acid", nativeQuery = true)
    List<EndOfDayBalance> calculateEOD(String tranDate);

}
