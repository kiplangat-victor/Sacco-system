package com.emtechhouse.accounts.TransactionService.Requests;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TransactionInterface {
    private String partTranType;
    private Double transactionAmount;
    private String acid;
    private String tranID;
    private String transactionDesc="NORMAL";
}