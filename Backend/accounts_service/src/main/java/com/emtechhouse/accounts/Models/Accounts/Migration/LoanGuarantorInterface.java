package com.emtechhouse.accounts.Models.Accounts.Migration;

public interface LoanGuarantorInterface {
    String getacid();
    Long getid();
    Double getguarantee_amount();
    String getguarantor_customer_code();
    String getloan_series();
    Long getloan_sn();
    Double getinitial_gee_amount();
    String getguarantor_type();
}
