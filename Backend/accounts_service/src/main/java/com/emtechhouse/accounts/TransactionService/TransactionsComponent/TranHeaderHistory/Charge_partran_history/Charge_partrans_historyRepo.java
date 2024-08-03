package com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeaderHistory.Charge_partran_history;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface Charge_partrans_historyRepo extends JpaRepository<Charge_partrans_history,Long> {
}
