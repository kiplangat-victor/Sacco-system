package emt.sacco.middleware.ATM.Dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AtmGlResponse {
    private String acid;
    private String accountName;
    private String openingDate;
    private String solCode;
    private String verifiedFlag;


}
