package com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.ChecqueInstruments;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChequeInstrumentsRepo extends JpaRepository<ChequeInstruments,Long> {
}
