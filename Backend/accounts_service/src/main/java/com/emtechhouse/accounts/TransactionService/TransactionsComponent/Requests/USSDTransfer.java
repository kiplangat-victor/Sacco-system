package com.emtechhouse.accounts.TransactionService.TransactionsComponent.Requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class USSDTransfer {
    private String creditAccount;
    private String debitAccount;
    private String tranAmount;
    private String narration;
    private String sessionId;
}
