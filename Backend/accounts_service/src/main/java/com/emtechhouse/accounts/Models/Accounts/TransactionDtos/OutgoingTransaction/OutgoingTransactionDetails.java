package com.emtechhouse.accounts.Models.Accounts.TransactionDtos.OutgoingTransaction;

import com.emtechhouse.accounts.Utils.CONSTANTS;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OutgoingTransactionDetails {
    private String transactionType;
    private String transactionSubType;
    private String currency;
    private Date transactionDate;
    private String entityId;
    private Double totalAmount;
    private String tellerAccount= CONSTANTS.SYSTEM_USERNAME;
    private List<OutgoingTransactionPartTrans> partTrans;
    private List<OutgoingTransactionPartTrans> chargePartran;
}
