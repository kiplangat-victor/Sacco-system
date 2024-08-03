package com.emtechhouse.accounts.Models.Accounts.AccountDtos;

import lombok.Data;

@Data
public class SearchRequestDto {
    private String columnName;
    private String value;
}
