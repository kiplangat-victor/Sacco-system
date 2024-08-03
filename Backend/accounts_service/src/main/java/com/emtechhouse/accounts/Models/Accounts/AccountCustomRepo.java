package com.emtechhouse.accounts.Models.Accounts;

import java.util.List;

public interface AccountCustomRepo {
    Account customFindMethod(Long id);

    List<Account> findUsingNativeQuery(String query);
//    List<Account> lookup();
}
