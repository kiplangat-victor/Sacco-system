package emt.sacco.middleware.AgencyBanking.Dtos.Withdrawal;

import lombok.Data;

@Data
public class AgencyTranRequest {
    private String amount;
    private String acid;
    private String ip;
}
