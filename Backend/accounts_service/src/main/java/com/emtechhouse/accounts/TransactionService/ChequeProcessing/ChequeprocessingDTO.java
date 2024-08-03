package com.emtechhouse.accounts.TransactionService.ChequeProcessing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChequeprocessingDTO {
    private Long id;
    private String chequeRandCode;
    private String chequeNumber;
    private String entityId;
    private String bankName; //kcb
    private String bankCode; //002
    private String bankBranch; // cbd
    private String debitOABAccount; ///lookup up office account for that check .... equityCheck, kcpCheck
    private Date maturityDate; //12/2022
    private Double amount = 0.00; //234000
    private Double penaltyAmount = 0.00;
    private String penaltyCollAc;
    private String nameAsPerCheque;//optional
    private String transactionType; //cashdeposit
    private String creditCustOperativeAccount;
}
