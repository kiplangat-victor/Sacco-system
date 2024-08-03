package com.emtechhouse.usersservice.MailService;

import lombok.Data;

@Data
public class MailDto {
    private String entityId;
    private String to;
    private String subject;
    private String message;
}
