package emt.sacco.middleware.Utils.CommonService;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import java.util.Date;
import java.util.List;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TransactionHeader {
    private Long sn;
    private String chequeType;
    private String staffCustomerCode;
    private String transactionCode;
    private String reversalTransactionCode;
    private String transactionType;
    private double totalAmount;
    private String currency;
    private String eodStatus="N";
    private String entityId;
    private Date rcre = new Date();
    private Date transactionDate;
    private String status ;
    private String mpesacode;
    private String chargeEventId;
    private String salaryuploadCode;
    private String tellerAccount;
    private String batchCode;
    private Character middleware = 'Y';

    @Column(nullable = false)
    private Channel conductedBy;
    private List<PartTran> partTrans;

    //*****************Operational Audit *********************
    private String acknowledgedBy;
    private Character acknowledgedFlag = 'N';
    private Date acknowledgedTime;

    private String approvalSentBy;
    private String approvalSentTo;
    private Character approvalSentFlag = 'N';

    private Date approvalSentTime;
    private String rejectedBy;
    private Character rejectedFlag = 'N';
    private Date rejectedTime;
    private String rejectedReason;

    private String enteredBy;
    private Character enteredFlag;
    private Date enteredTime;

    private String postedBy;
    private Character postedFlag = 'N';
    private Date postedTime;

    private String modifiedBy;
    private Character modifiedFlag = 'N';
    private Date modifiedTime;

    private String verifiedBy;
    private Character verifiedFlag = 'N';
    private Date verifiedTime;

    private String verifiedBy_2;
    private Character verifiedFlag_2 = 'N';
    private Date verifiedTime_2;

    private String reversedBy;
    private Character reversedFlag = 'N';
    private Character reversalPostedFlag = 'N';
    private String reversedWithTransactionCode;
    private Date reversedTime;

    private String deletedBy;
    private Character deletedFlag = 'N';
    private Date deletedTime;

}