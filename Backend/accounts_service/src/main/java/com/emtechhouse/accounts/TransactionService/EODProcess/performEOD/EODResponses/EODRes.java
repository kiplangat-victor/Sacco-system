package com.emtechhouse.accounts.TransactionService.EODProcess.performEOD.EODResponses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class EODRes {
    private String eodStep;
    private boolean status;
    private String message;
    private Object issues;
}
