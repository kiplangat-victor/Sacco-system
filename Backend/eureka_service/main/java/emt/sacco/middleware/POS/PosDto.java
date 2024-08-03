package emt.sacco.middleware.POS;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.Date;

@Data
@Component
public class PosDto {
    private Long id;
  //  private String entityId;
    private String terminalId;

    private String merchantAcid;
    private String merchantName;
    private String merchantLocation;
    private String branchSol;
    private String serialNumber;
    private String ipAddress;
    private String ipHost;
    private String portNumber;
    private String accountStatus;

    //private Date deletedTime;
    private Character verifiedFlag;
}
