package com.emtechhouse.accounts.Models.Accounts.Migration;

import java.util.Date;

public interface LoanInterface {
    String getacid();
    String getfrequency_id();
    Integer getfrequency_period();
    Integer getnumber_of_installments();
    String getoperative_acount_id();
    String getdisbursment_account();
    Double getprincipal_amount();
    Double getfees_amount();
    Double getinterest_rate();
    String getinterest_calculation_method();
    Double getdisbursement_amount();
    Date getdisbursement_date();
    String getcustomer_code();
    String getcustomer_type();
    String getproduct_code();
    Double getaccount_balance();
    Date getinstallment_start_date();


    String getAccount_name();
    String getApproved_by();
    String getPosted_by();

    Date getApproval_date();
}
