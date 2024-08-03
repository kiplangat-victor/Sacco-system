package com.emtechhouse.accounts.Models.Accounts.Loans.LoanLimit;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class LoanLimiter {
    public String customerCode;
    public String productCode;
    public Double limitAmount;
    public Date date;
}
