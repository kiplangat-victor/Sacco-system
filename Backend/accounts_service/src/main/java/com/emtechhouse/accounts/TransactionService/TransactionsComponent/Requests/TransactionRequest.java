package com.emtechhouse.accounts.TransactionService.TransactionsComponent.Requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TransactionRequest {
    @JsonProperty(required = true)
    private String debitAccount;
    private String creditAccount;
    @JsonProperty(required = true)
    private Double amount;
    @JsonProperty(required = true)
    private String currency;
    private String transactionDetails;
    @JsonProperty(required = true)
    private Character transactionType;
    @JsonProperty(required = true)
    private String transactionSubType;
    @JsonProperty(required = true)
    private String paymentChannel;
    private Character command;
}
