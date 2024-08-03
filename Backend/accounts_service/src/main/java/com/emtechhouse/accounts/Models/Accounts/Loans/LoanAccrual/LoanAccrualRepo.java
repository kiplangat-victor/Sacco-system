package com.emtechhouse.accounts.Models.Accounts.Loans.LoanAccrual;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface LoanAccrualRepo extends JpaRepository<LoanAccrual, Long> {
    List<LoanAccrual> findByAcid(String acid);

    @Query(value = "select * from loan_accrual where date(accrued_on)=:accruedOn", nativeQuery = true)
    List<LoanAccrual> getInterestAccruedByDate(LocalDate accruedOn);

    @Query(value = "select count(*) from loan_accrual where date(accrued_on)=:accruedOn", nativeQuery = true)
    Integer getCountAccruedLoansOnACertainDate(LocalDate accruedOn);

    @Query(value = "select a.id,a.acid from loan l JOIN accounts a ON l.account_id_fk=a.id where l.loan_status='DISBURSED' and l.interest_calculation_start_date<=:accrualDate and l.sn NOT IN (SELECT la.loan_sn FROM loan_accrual la WHERE date(la.accrued_on)=:accrualDate)", nativeQuery = true)
    List<AccountsIds> getAllAcidsNotAccruedOnACertainDate(LocalDate accrualDate);
    interface AccountsIds{
        Long getId();
        String getAcid();
    }

    @Query(value = "SELECT COALESCE(SUM(`amount_accrued`),0) FROM loan_accrual WHERE `interest_type`=:interestType AND `acid`=:acid", nativeQuery = true)
    Double getSumAccruedInterestByInterestType(String interestType, String acid);
}
