package com.emtechhouse.accounts.KraStampedTransactions.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class KraStampedTransactionRequestDTO {
    private StampedTransactionDTO data;
}
