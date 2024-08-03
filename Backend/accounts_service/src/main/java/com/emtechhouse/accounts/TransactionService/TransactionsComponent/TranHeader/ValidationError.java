package com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ValidationError {
    private String message;
    private boolean validationError;
}
