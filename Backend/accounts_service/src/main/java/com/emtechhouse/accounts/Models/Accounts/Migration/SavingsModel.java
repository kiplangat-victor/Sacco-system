package com.emtechhouse.accounts.Models.Accounts.Migration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SavingsModel {
    public String customerCode;
    public String accountName;
}
