package com.emtechhouse.accounts.Models.Accounts.AccountDtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
public class SmsDto {
    private String msisdn;
    private String text;
}

