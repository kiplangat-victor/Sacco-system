package com.emtechhouse.CollateralService;

public interface CollateralDto {

    Long getSn();
    String getCollateralCode();
    Double getCollateralValue();
    String getCollateralType();
    String getDescription();
    String getPhoneNumber();
    String getEmailAddress();
    Character getVerifiedFlag();
}
