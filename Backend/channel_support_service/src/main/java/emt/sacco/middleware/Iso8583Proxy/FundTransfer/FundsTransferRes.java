package emt.sacco.middleware.Iso8583Proxy.FundTransfer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FundsTransferRes {

    @JsonProperty("isoCode")
    private String isoCode;

    @JsonProperty("statusCode")
    private String statusCode;

    @JsonProperty("statusDescription")
    private String statusDescription;
}
