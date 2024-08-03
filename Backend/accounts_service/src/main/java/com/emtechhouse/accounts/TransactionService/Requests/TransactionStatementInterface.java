package com.emtechhouse.accounts.TransactionService.Requests;

import javax.persistence.Column;

public interface TransactionStatementInterface {

    Double getTran_amount();

    @Column(name = "transaction_code")
    String getTransaction_code();
}
