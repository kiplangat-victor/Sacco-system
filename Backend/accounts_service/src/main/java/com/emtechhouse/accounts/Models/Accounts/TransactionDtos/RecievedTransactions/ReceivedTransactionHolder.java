package com.emtechhouse.accounts.Models.Accounts.TransactionDtos.RecievedTransactions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReceivedTransactionHolder {
    private List<RecievedTransactionDetails> transactions;
}
