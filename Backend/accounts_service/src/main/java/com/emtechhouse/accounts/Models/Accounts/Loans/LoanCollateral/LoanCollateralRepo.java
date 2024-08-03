package com.emtechhouse.accounts.Models.Accounts.Loans.LoanCollateral;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanCollateralRepo extends JpaRepository<LoanCollateral, Long> {
}
