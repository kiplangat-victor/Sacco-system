package com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.SpecificProductDetails;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SavingsAccountProduct {
    private String id;
    private Boolean withdrawalsAllowed;
    private String eventTypeCode;
    private String eventIdCode;
}