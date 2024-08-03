package emt.sacco.middleware.ATM.Model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AtmPayload {
        private String atmAccount;
        private String entityId;
        private Long id;
        private String ipAddress;
        private String location;
        private String atmName;
//        private String status;
        private String tcpPort;
        private String terminalId;
        private String terminalMasterKey;
        private String terminalPinKey;
        private String vendorName;
        private String verifiedFlag;
        private String solCode;
}
