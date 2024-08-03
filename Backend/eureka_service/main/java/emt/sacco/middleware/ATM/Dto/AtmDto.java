package emt.sacco.middleware.ATM.Dto;

import lombok.Data;

@Data
public class AtmDto {
    private Long id;
    private String entityId;
    private String solCode;
    private String atmName;
    private String terminalId;
    private String location;
//    private String status;
    private String atmAccount;
    private String tcpPort;
    private String terminalMasterKey;
    private String ipAddress;
    private String terminalPinKey;
    private String vendorName;
    private Character verifiedFlag;
}