package com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeaderHistory.PartTransHistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartTranHIstoryRepository extends JpaRepository<PartTranHistory, Long> {
}

