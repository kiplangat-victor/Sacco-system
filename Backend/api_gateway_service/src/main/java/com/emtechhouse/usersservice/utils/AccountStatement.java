package com.emtechhouse.usersservice.utils;

import java.util.Date;

public interface AccountStatement {
    String getCls();
    String getProduct_code();
    String getProduct_code_desc();
    String getAccount_name();
    String getAcid();
    String getDtd_sn();
    String getSn();
    String getTransaction_code();
    Date getTran_date();
    String getTran_particulars();
    String getC_amount();
    String getD_amount();
    String getRel_amount();
    String getTran_amount();
    String getRelative_amount();
}
