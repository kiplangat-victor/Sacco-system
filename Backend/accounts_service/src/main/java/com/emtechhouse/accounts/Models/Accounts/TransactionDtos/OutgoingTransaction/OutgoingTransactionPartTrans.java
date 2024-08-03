package com.emtechhouse.accounts.Models.Accounts.TransactionDtos.OutgoingTransaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OutgoingTransactionPartTrans {
    private String partTranType;
    private Double transactionAmount;
    private String acid;
    private String currency;
    private String exchangeRate;
    private Date transactionDate;
    private String transactionParticulars;
    private String isoFlag;
}
