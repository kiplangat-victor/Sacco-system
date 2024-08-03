package com.emtechhouse.accounts.TransactionService.Responses;

public interface ChequeInfo {

    //select a.acid,ck.account_number,a.account_balance,a.account_type,
    // ck.micr_code,a.account_name from accounts a join chequebook ck on ck.account_number=a.acid
    // where ck.micr_code=:chequeNo

    String getAcid();
    Double getAccount_balance();
    String getAccount_number();
    String getAccount_type();
    String getMicr_code();
    String getAccount_name();
}
