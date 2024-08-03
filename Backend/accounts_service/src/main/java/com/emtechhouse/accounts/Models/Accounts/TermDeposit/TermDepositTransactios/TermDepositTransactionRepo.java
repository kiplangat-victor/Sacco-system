package com.emtechhouse.accounts.Models.Accounts.TermDeposit.TermDepositTransactios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TermDepositTransactionRepo extends JpaRepository<TermDepositTransaction, Long> {
}
