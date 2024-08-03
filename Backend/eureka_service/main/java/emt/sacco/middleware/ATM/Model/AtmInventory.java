package emt.sacco.middleware.ATM.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

import org.springframework.stereotype.Component;


@Component
@Data

@JsonIgnoreProperties(ignoreUnknown = true)
public class AtmInventory {
    @JsonProperty("BatchId")
    private String batchId;
    private Long id;
    private int branchId;
    private String Status;
    private String vendorName;
    private String BranchName;
    private String cardType;
    private int quantity;
    private String cardRange;
    private String requestStatus;
    private String enteredBy;
    private String statusCode;
//    private String verifiedBy;
//    private String postedBy;
//    private String rejectedBy;
}
