package com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader;

import com.emtechhouse.accounts.Models.Accounts.Account;
import com.emtechhouse.accounts.Models.Accounts.AccountRepository;

import java.util.Optional;

public class AccountUtil {
    private final AccountRepository accountRepository;

    public AccountUtil(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    //    TODO: Get account Balance
    public Double getAccountBalance(String acid){
        Double accountBalance = 0.00;
        Optional<Account> account = accountRepository.findByAcid(acid);
        if (account.isPresent()){
            accountBalance = account.get().getAccountBalance();
        }else {
            accountBalance = accountBalance;
        }
        return accountBalance;
    }
//    Todo: Check account verification
//    public Boolean isAccountVerified(String acid)
//    TODO: Update account balance
//    TODO: Check account activeness
//    TODO: Check account allowed debit
}
