package emt.sacco.middleware.AgencyBanking.Dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ResponseDto {
    @JsonProperty("isoCode")
    private String isoCode;

    @JsonProperty("statusCode")
    private String statusCode;

    @JsonProperty("statusDescription")
    private String statusDescription;

}
