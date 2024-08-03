package com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ProductInterestDetails;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductInterestDetails {
    Double interestRate;
    String interestCalculationMethod; //flatrate or reducingbalance
    String interestPeriod; //p.m or p.a
    Double penalInterestAmount;
}
