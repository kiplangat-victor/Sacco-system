package com.emtechhouse.accounts.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ContactInfo {
    String phoneNumber;
    String emailAddress;
    String customerName;
}
