package emt.sacco.middleware.AgencyBanking.Dtos.Withdrawal;

import lombok.Data;

@Data
public class AgencyRequest {
    private  String cardNumber;
    private String pin;
}
