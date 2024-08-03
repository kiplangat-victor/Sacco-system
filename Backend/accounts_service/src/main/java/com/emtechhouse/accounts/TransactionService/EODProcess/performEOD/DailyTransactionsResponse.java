package com.emtechhouse.accounts.TransactionService.EODProcess.performEOD;

import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TransactionHeader;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class DailyTransactionsResponse {
    private List<TransactionHeader> completedTransactions;
    private List<TransactionHeader> unverifiedTransactions;
    private List<TransactionHeader> notPostedTransactions;
}
