package co.ke.emtechhouse.OfficeProducts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class Officeproduct {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 6, nullable = false)
    private String entityId;
    @Column(length = 15, nullable = false, updatable = false, unique = true)
    private String productCode;
    @Column(length = 6, nullable = false, updatable = false)
    private String productType = "OAB";
    //    private String scheme_type;
    @Column(length = 100)
    private String productCodeDesc;
    //  General Details
    private LocalDate effective_from_date;
    private LocalDate effective_to_date;
    //GeneralDetails
    private LocalDate effectiveDate;
    private LocalDate expiryDate;
    @Column(length = 80)
    private String schemeName; //Assets/Liabilities
    @Column(length = 80)
    private String tranRestriction;  //Credit Not Allowed, Debit not Allowed , Both Br_Cr not allowed  ==> C,D,B
    @Column(length = 80)
    private String acidGeneration; //System or Number
    private String acidStructure;
    private Long increment;
    @Column(length = 80)
    private String shortName;
    @Column(length = 80)
    private String acPrefix;
    @Column(length = 80)
    private String minPostWorkClass;
    @Column(length = 80)
    private String schemeSupervisorID;
    @Column(length = 80)
    private String acCurrency;
    @Column(length = 80)
    private String productGroup;
    @Column(length = 80)
    private Double cashLimitDr = 0.00;
    @Column(length = 80)
    private Double cashLimitCr = 0.00;
    @Column(length = 80)
    private Double transferLimitDr = 0.00;
    @Column(length = 80)
    private Double transferLimitCr = 0.00;
    //exceptions
    @Column(length = 10)
    private String acIsFrozenExce;
    @Column(length = 10)
    private String backDatedTransactionExce;
    @Column(length = 10)
    private String valueDatedTransactionExce;
    @Column(length = 10)
    private String defaultParametersOverriddenExce;
    @Column(length = 10)
    private String transferTranExce;
    @Column(length = 10)
    private String sanctionLimitExpiredExce;
    @Column(length = 10)
    private String sanctionExceedLimitExce;
    @Column(length = 10)
    private String custDrWithoutChqExce;
    @Column(length = 10)
    private String referredAcClosureExce;
    @Column(length = 10)
    private String acIsInDrBalExce;
    @Column(length = 10)
    private String acIsInCrBalExce;
    @Column(length = 10)
    private String acBalBelowMinExce;
    @Column(length = 10)
    private String minBalPenalFeeNotCalculatedExce;
    @Column(length = 10)
    private String intForPastDueNotUptodateExce;
    @Column(length = 10)
    private String drTranOnPastDueAcExce;
    @Column(length = 10)
    private String eodMinBalExce;
    @Column(length = 10)
    private String eodMaxBalExce;
    @Column(length = 10)
    private String cashLimitExceededDrExce;
    @Column(length = 10)
    private String cashLimitExceededCrExce;
    @Column(length = 10)
    private String transferLimitExceededCrExce;
    @Column(length = 10)
    private String tranAmtLimitExce;
    @Column(length = 10)
    private String drTranRestrictedExce;
    @Column(length = 10)
    private String crTranRestrictedExce;
    @Column(length = 10)
    private String operationOnCrncyHolidayExce;
    @Column(length = 10)
    private String cashLimitDrExce;
    @Column(length = 10)
    private String cashLimitCrExce;
    @Column(length = 10)
    private String drBalLimitExce;
    @Column(length = 10)
    private String clearingLimitDrExce;
    @Column(length = 10)
    private String clearingLimitCrExce;
    @Column(length = 10)
    private String transferLimitDrExce;
    @Column(length = 10)
    private String transferLimitCrExce;
    @Column(length = 10)
    private String eodBalanceCheckExce;
    //Scheme Details
//    contraPartType /dr or cr
    //*****************Operational Audit *********************
    @Column(length = 60, nullable = false)
    private String postedBy;
    @Column(length = 2, nullable = false)
    private Character postedFlag = 'Y';
    @Column(length = 50, nullable = false)
    private Date postedTime;
    @Column(length = 60)
    private String modifiedBy;
    @Column(length = 2)
    private Character modifiedFlag = 'N';
    @Column(length = 50)
    private Date modifiedTime;
    @Column(length = 60)
    private String verifiedBy;
    @Column(length = 2)
    private Character verifiedFlag = 'N';
    @Column(length = 50)
    private Date verifiedTime;
    @Column(length = 60)
    private String deletedBy;
    @Column(length = 2)
    private Character deletedFlag = 'N';
    @Column(length = 50)
    private Date deletedTime;
}
