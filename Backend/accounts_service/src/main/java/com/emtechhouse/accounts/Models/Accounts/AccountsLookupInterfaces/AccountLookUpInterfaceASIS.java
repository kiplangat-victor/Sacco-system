package com.emtechhouse.accounts.Models.Accounts.AccountsLookupInterfaces;

import java.util.Date;

public interface AccountLookUpInterfaceASIS {
    Long getId();
    String getAcid();
    String getAccountType();
    String getAccountStatus();
    String getSolCode();
    String getProductCode();
    String getProductCodeDesc();
    String getCustomerCode();
    String getNationalId();
    String getAccountName();
    Character getVerifiedFlag();
    Date getPostedTime();
}

