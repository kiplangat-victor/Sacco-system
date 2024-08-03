package com.emtechhouse.accounts.TransactionService.TransactionsComponent.NoneWithdrawDeposit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoneWithdrawableDepositsRepo extends JpaRepository<NonwithdrawableDeposits,Long> {

    List<NonwithdrawableDeposits> findByStatusAndEntityId(String status,String entity_id);
}
