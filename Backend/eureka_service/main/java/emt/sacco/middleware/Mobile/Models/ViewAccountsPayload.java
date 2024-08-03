package emt.sacco.middleware.Mobile.Models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
@Data
public class ViewAccountsPayload {
    @JsonProperty("CustomerCode")
    private String customerCode;
}
