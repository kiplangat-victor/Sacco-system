package emt.sacco.middleware.ATM.Dto;

import lombok.Data;

@Data
public class GetAtmBalanceRequest {
    private String terminalId;
    private String entityId;
}
