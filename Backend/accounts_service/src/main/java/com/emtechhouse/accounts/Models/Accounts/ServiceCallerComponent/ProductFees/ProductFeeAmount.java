package com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ProductFees;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductFeeAmount {
    String eventIdCode;
    String eventTypeDesc;
    String chargeCollectionAccount;
    Double initialAmt;
    Double monthlyAmt;


    Character chargeMonthlyFee = 'N';
    String monthlyEventId;
    Character chargeAnnualFee = 'Y';
    String annualEventId;
}
