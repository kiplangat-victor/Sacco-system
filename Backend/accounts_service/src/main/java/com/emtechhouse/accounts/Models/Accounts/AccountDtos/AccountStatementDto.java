package com.emtechhouse.accounts.Models.Accounts.AccountDtos;

import lombok.Data;

@Data
public class AccountStatementDto {
    private String drAccount;
    private String description;
//    private Double amount;
    private String eventId;
    private Integer pagesNumber;
}
