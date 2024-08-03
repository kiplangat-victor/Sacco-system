package com.emtechhouse.accounts.Models.Accounts.Validators.ValidatorInterfaces;

public interface OfficeProductItem {
    Long getid();
    String getproduct_type();
    String getproduct_code();
    Character getverified_flag();
    Character getdeleted_flag();
}
