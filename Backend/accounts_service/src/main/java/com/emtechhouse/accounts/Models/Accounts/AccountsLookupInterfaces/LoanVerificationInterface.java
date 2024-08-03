package com.emtechhouse.accounts.Models.Accounts.AccountsLookupInterfaces;

import java.util.Date;

public interface LoanVerificationInterface {
    Long getid();
    String getacid();
    String getaccount_type();
    String getverification();
    String getaccount_status();
    String getsol_code();
    String getproduct_code();
    String getproduct_code_desc();
    String getcustomer_code();
    String getnational_id();
    String getaccount_name();
    Character getverified_flag();
    Date getPosted_time();
}
