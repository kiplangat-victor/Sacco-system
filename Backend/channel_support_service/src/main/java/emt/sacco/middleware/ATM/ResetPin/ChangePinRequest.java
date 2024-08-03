package emt.sacco.middleware.ATM.ResetPin;

import lombok.Data;

@Data

public class ChangePinRequest {

//   private String entityId;
    private String cardNumber;
    private String previousPin;
    private String newPin;
    private String confirmNewPin;
}
