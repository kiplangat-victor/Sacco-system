package com.emtechhouse.accounts.TransactionService.MpesaDeposits;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MpesaDepositsAPIRequestRepo extends JpaRepository<MpesaDepositsAPIRequest,Long> {

    MpesaDepositsAPIRequest findByMpesaReceiptNumber(String mpesareciept_no);
}
