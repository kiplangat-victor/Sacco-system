package emt.sacco.middleware.ATM.Dto;

import lombok.Data;

@Data
public class AtmCardValidationDto {
    private String cardNumber;
    private String pin;
    private String amount;

}
