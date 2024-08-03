package com.emtechhouse.accounts.NotificationComponent.SmsService;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SMSResponse {
    private int responseCode;
    private String messageId;
}
