package com.emtechhouse.accounts.Models.Accounts.Validators.ValidatorInterfaces;

import java.time.LocalDate;

public interface ProductItem {
    Long getid();
    String getproduct_type();
    String getproduct_code();
    Character getverified_flag();
    Character getdeleted_flag();

    LocalDate geteffective_from_date();
    LocalDate geteffective_to_date();
}
