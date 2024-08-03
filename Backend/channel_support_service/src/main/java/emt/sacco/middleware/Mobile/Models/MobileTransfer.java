package emt.sacco.middleware.Mobile.Models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MobileTransfer {
    @JsonProperty("CreditAccount")
    private String creditAccount;

    @JsonProperty("DebitAccount")
    private String debitAccount;

    @JsonProperty("TranAmount")
    private String tranAmount;

    @JsonProperty("Narration")
    private String narration;

    @JsonProperty("SessionId")
    private String sessionId;

    @JsonProperty("PhoneNumber")
    private String phoneNumber;

//    @JsonProperty("EntityId")
//    private String entityId;

}
