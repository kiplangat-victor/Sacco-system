package com.emtechhouse.accounts.KraStampedTransactions.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StampedTransactionResponse {
    private String dt;//":"20230520142806",
    private String totRcptNo;//":14,
    private String rcptTyCd;//":"S",
    private String rcptNo;//":14,
    private String transTyCd;//":"N",
    private String rcptStamp;//":"4e23532331342f31342033302e30362e30"}
}
