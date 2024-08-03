package emt.sacco.middleware.ATM.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AtmBalanceResponse {
    private String terminalId;
    private String entityId;
    private String balance;
}
