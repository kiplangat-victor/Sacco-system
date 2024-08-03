package com.emtechhouse.accounts.Models.Accounts.TermDeposit.Schedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TermDepositScheduleRepo extends JpaRepository<TermDepositSchedule, Long> {

    @Query(value = "SELECT COUNT(sn) AS count FROM part_tran pt WHERE pt.acid = :acid and part_tran_type = 'debit' and transaction_header_id IN (SELECT sn FROM dtd WHERE posted_flag = 'Y')  ", nativeQuery = true)
    Integer hasWithdrawals(@Param("acid") String acid);
}

