package com.emtechhouse.accounts.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChargeCollectionReq {
    private String debitAc;
    private Double transactionAmount;
    private String chargeCode;
    private String transParticulars;
    private String parttranIdentity;
}
