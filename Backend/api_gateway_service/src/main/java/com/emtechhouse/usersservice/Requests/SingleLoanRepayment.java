package com.emtechhouse.usersservice.Requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SingleLoanRepayment {
    public String account;
    public Double amount;
    public String transactionParticulars;
    public String partTranType;
    public Date transactionDate;
    public String solCode;
    public String postedBy;
    public Date postedTime;
    public String postedFlag;
}
