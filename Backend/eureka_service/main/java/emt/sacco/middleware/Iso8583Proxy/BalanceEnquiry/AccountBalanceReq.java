package emt.sacco.middleware.Iso8583Proxy.BalanceEnquiry;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AccountBalanceReq {

    @JsonProperty("Account")
    private String Account;

    @JsonProperty("Name")
    private String Name;
}
