package com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.CollateralDto;

import lombok.Data;

@Data
public class CollateralDto {
    private Long sn;
    private String collateralCode;
    private Double collateralValue;
    private String collateralType;
    private String description;
    private String phoneNumber;
    private String emailAddress;
    private Character verifiedFlag;
}
