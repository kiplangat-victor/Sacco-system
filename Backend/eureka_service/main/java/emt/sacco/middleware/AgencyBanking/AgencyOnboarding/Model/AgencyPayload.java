package emt.sacco.middleware.AgencyBanking.AgencyOnboarding.Model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AgencyPayload {

@JsonProperty("Id")
private Long Id;
    @JsonProperty("agentPrimaryAcid")
    private String agentPrimaryAcid;
    @JsonProperty("agentId")
    private String agentId;
    @JsonProperty("agentName")
    private String agentName;
    @JsonProperty("agentsLocation")
    private String agentsLocation;
    @JsonProperty("serialNumber")
    private String serialNumber;
    @JsonProperty("branchSol")
    private int branchSol;
    @JsonProperty("branchName")
    private String branchName;
    @JsonProperty("ipAddress")
    private String ipAddress;
    @JsonProperty("ipHost")
    private String ipHost;
    @JsonProperty("portNumber")
    private int portNumber;
//    @JsonProperty("TcpIp")
//    private  String TcpIp;


}
