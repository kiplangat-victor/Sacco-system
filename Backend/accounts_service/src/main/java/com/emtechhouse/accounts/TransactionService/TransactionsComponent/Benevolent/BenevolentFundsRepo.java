package com.emtechhouse.accounts.TransactionService.TransactionsComponent.Benevolent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BenevolentFundsRepo extends JpaRepository<BenevolentFunds,Long> {

    List<BenevolentFunds> findByStatusAndEntityId(String status, String entity_id);
}
