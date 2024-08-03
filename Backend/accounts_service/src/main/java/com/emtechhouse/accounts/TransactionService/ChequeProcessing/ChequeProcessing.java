package com.emtechhouse.accounts.TransactionService.ChequeProcessing;

import com.emtechhouse.accounts.Utils.CONSTANTS;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
public class ChequeProcessing {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false, updatable = false)
    private String chequeRandCode;
    @Column(nullable = false)
    private String chequeNumber;
    @Column(length = 6, nullable = false)
    private String entityId;
    @Column(nullable = false)
    private String bankName; //kcb
    private String bankCode; //002
    private String bankBranch; // cbd
    @Column(nullable = false)
    private String debitOABAccount; ///lookup up office account for that check .... equityCheck, kcpCheck
    @Column(nullable = false)
    private Date maturityDate; //12/2022
    @Column(nullable = false)
    private Double amount = 0.00; //234000
    private Double penaltyAmount = 0.00;
    private Double chargeAmount = 0.00;
    private String penaltyCollAc;
    @Column(length = 300 , nullable = false)
    private String nameAsPerCheque;//optional

    private String chargeCode;

    @Column(nullable = false)
    private String creditCustOperativeAccount; //restricted to customer accounts
    @Lob
    private String chequeDocument;

    private String transactionCode = "-";
    private String transactionDate;

    //*****************Operational Audit *********************
    private String status = CONSTANTS.ENTERED;
    @Column(length = 30, nullable = false)
    private String enteredBy;
    @Column(nullable = false)
    private Character enteredFlag = 'Y';
    @Column(nullable = false)
    private Date enteredTime;
    private String modifiedBy;
    private Character modifiedFlag = 'N';
    private Date modifiedTime;
    private String rejectedBy;
    private Character rejectedFlag = 'N';
    private Date rejectedTime;
    private String verifiedBy;
    private Character verifiedFlag = 'N';
    private Date verifiedTime;
    private String verifiedBy_2;
    private Character verifiedFlag_2 = 'N';
    private Date verifiedTime_2;
    private String clearedBy;
    private Character clearedFlag = 'N';
    private Date clearedTime;
    private String bouncedBy;
    private Character bouncedFlag = 'N';
    private Date bouncedTime;
    private String postedBy;
    private Character postedFlag = 'N';
    private Date postedTime;
    private String deletedBy;
    private Character deletedFlag = 'N';
    private Date deletedTime;
}
