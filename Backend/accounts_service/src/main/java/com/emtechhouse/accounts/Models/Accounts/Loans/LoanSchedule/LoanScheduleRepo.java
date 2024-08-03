package com.emtechhouse.accounts.Models.Accounts.Loans.LoanSchedule;
import com.emtechhouse.accounts.Models.Accounts.Loans.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanScheduleRepo extends JpaRepository<LoanSchedule, Long> {
    List<LoanSchedule> findByDeleteFlag(Character deleteFlag);

    @Query(value = "select id from loan_schedule where loan_sn=:loanSn ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Long findAccountLastEntry(Long loanSn);

    List<LoanSchedule> findByLoan(Loan loan);
}
