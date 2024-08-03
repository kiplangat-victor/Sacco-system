package com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.EmailDto;

import lombok.Data;

@Data
public class MailDto {
    private String to;
    private String subject;
    private String message;
}
