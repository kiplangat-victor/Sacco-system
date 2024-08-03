package com.emtechhouse.accounts.Models.Accounts.TransactionDtos.RecievedTransactions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RecievedTransactionDetails {
    private String partTranType;
    private Double transactionAmount;
    private String acid;
    private String transactionDesc;
}
