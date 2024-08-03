package com.emtechhouse.accounts.Models.Accounts.Validators.ValidatorInterfaces;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductMinMaxHolder {
    private String minAmt;
    private String maxAmt;
    private String minPeriod;
    private String maxPeriod;
}
