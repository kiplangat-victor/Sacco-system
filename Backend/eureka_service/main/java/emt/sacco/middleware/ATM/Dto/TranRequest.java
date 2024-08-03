package emt.sacco.middleware.ATM.Dto;
import lombok.Data;

@Data
public class TranRequest {
    private String amount;
    private String acid;
    private String ip;
}
