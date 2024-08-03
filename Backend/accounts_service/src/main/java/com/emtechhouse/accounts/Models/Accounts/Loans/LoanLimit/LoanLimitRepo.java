package com.emtechhouse.accounts.Models.Accounts.Loans.LoanLimit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface LoanLimitRepo extends JpaRepository<LoanLimit, Long> {

    @Query(value = "Select * from loan_limit where acid =:acid order by id desc limit 1", nativeQuery = true)
    Optional<LoanLimit> findbyAcid(String acid);
}
