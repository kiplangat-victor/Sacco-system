package emt.sacco.middleware.ATM.Dto;
import lombok.Data;

@Data
public class AtmRequest {
    private  String cardNumber;
    private String pin;
}
