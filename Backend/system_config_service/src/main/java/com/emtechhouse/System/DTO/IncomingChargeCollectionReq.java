package com.emtechhouse.System.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class IncomingChargeCollectionReq {
    private String debitAc;
    private Double transactionAmount;
    private String chargeCode;
    private String transParticulars;
}
