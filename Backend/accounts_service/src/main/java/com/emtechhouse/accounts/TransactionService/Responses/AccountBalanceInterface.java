package com.emtechhouse.accounts.TransactionService.Responses;

public interface AccountBalanceInterface {

    Double getAccount_balance();
    Double getBook_balance();
    String getAccount_name();
    String getAccount_type();
    String getCurrency();
    String getAccount_status();
}
