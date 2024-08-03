package com.emtechhouse.accounts.TransactionService.Requests;

public interface RepaymentAcounts {
   // product_code,account_type,acid,account_balance,account_status

    String getProductCode();
    String getAccountType();
    String getAcid();
    Double getAccountBalance();
    String getAccountStatus();
    String getAccountName();


}
