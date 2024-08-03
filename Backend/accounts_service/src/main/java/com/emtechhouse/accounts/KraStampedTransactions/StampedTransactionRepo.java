package com.emtechhouse.accounts.KraStampedTransactions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StampedTransactionRepo extends JpaRepository<StampedTransaction, Long> {
    StampedTransaction findFirstByOrderByIdDesc();
    Optional<StampedTransaction> findByInvId(String invId);
}
