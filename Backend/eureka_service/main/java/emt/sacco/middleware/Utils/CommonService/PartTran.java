package emt.sacco.middleware.Utils.CommonService;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PartTran {
    private Long sn;
    private String partTranType;

    private String partTranIdentity = "Normal";

    @Column(nullable = false)
    private Channel conductedBy;

    private double transactionAmount;
    private String acid;
//    @Column(name = "transactionCode", nullable = false,length = 40)
    private String transactionCode;
    private String currency;
    private String exchangeRate;
    private Date transactionDate;
    private String transactionParticulars;
    private Character isoFlag;
    private double accountBalance;
    private String accountType;
    private String batchCode;
    private Character chargeFee = 'Y';

    private Boolean isWelfare = false;
    private String welfareCode;
    private String welfareAction;
    private String welfareMemberCode;
}