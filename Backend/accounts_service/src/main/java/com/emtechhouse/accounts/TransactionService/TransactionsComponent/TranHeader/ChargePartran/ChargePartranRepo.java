package com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.ChargePartran;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChargePartranRepo extends JpaRepository<ChargePartran,Long> {
}
