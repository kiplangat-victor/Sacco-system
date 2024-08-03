package com.emtechhouse.usersservice.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MailDto {
    private String text;
    private String mailTo;
    private String subject;
    private String phoneNumber;
}
