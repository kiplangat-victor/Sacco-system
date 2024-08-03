package co.ke.emtechhouse.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class IncomingChargeCollectionReq {
    private String debitAc;
    private Double transactionAmount;
    private String chargeCode;
    private String transParticulars;
    private String productCode;
}
