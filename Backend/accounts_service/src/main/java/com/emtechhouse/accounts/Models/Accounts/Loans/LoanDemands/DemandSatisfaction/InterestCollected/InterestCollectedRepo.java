package com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.DemandSatisfaction.InterestCollected;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InterestCollectedRepo extends JpaRepository<InterestCollected, Long> {
}
