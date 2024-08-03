package emt.sacco.middleware.POS;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountLookupDto {
    private String acid;
    private String accountName;
    private String accountStatus;
    private String solCode;
    private String verifiedFlag;

    private Long id;
    private String entityId;
    private String terminalId;

    private String merchantAcid;
    private String merchantName;
    private String merchantLocation;
    private String branchSol;
    private String serialNumber;
    private String ipAddress;
    private String ipHost;
    private String portNumber;

    //private Date deletedTime;
}
