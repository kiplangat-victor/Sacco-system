package com.emtechhouse.System.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PartTran {
    private String partTranType;
    private String parttranIdentity;
    private double monthlyFee;
    private double transactionAmount;
    private String acid;
    private String currency;
    private String transactionParticulars;

}