package com.emtechhouse.accounts.TransactionService.MpesaWithdrawals;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MpesaWithdrawalAPIRequestRepo extends JpaRepository<MpesaWithdrawalAPIRequest,Long> {

    MpesaWithdrawalAPIRequest findByMpesaReceiptNumber(String mpesa_receiptNo);
}
