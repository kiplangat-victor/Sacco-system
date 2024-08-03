package com.emtechhouse.CollateralService;

import com.emtechhouse.CollateralService.CollateralDocuments.Collateraldocument;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
@Entity
public class Collateral {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long sn;
    @Column(nullable = false, length = 30)
    private String entityId;

    private String collateralCode;

    private String collateralType;

    private String customerCode;

    private String customerName;

    private String description;

    private String ceilingLimit;

    private String currencyCollateral;

    private BigDecimal marginPercent;

    private String marketValue;

    private BigDecimal loanValue;

    private String otherDetails;

    private String chargeEventForWithdrawal;

    private BigDecimal percentageDrawingPower;

    private String percentageLoanToTake;

    private Date lastEvaluationDate;

    private BigDecimal yearofManufacture;

    private Date dateofPurchase;

    private String registrationNumber;

    private String chasisNumber;

    private String engineNo;

    private String registeredownerName;

    private String model;

    private String manufacture;

    private String machineNo;

    private String propertyDocumentNo;

    private Date purchaseDate;

    private String builtArea;

    private String landArea;

    private BigDecimal unitMeasurement;

    private String propertyAddress;

    private String leased;

    private Date leasedExpiryDate;

    private String ageBuilding;

    private String lodgedDate;

    private Double collateralValue;

    private String frequencyforSubmission;

    private BigDecimal applypenalInterest;

    private Date reviewDate;

    private Date dueDate;

    private Date withdrawnDate;

    private BigDecimal depositAccountNo;

    private BigDecimal denominationsNo;

    private String fullBenefit;

    private BigDecimal apportionedValue;

    private BigDecimal lienAmount;

    private String companyDetails;

    private String sharesCapital;

    private String nosharesIsssued;

    private String contactDetails;

    private String insuranceType;

    private String policyNo;

    private BigDecimal policyAmount;

    private String insurerDetails;
    private Date risk_cover_start_date;
    private Date risk_cover_end_date;
    private Date last_premium_paid_date;

    private BigDecimal premiumAmount;

    private String frequency;

    private String itemsInsured;

    private String notes;

    private String name;

    private String city;

    private String address;

    private String state;

    private String postal_code;

    private String receipt_type;

    private BigDecimal receipt_amount;

    private String payment_type;

    private BigDecimal payment_amount;

    private Date due_date_for_rec;

    private Date paid_received_date;

    private Date date_from;

    private Date to_date;

    private Date proof_verified_date;

    private String mode_of_pay;

    private String remarks;

    private String inspection_type;

    private String insp_address;

    private String insp_city;

    private String insp_state;

    private String insp_postal_code;

    private String insp_telephone_no;

    private Date due_date_for_visit;

    private Date date_of_visit;

    private BigDecimal inspected_value;

    private String inspection_emp_id;

    private String insp_remarks;

    private String brokerName;

    private Date sent_for_sale_on;

    private Date sales_due_date;

    private Date sales_review_date;

    private Date proceeds_received_on;

    private BigDecimal expected_min_price;

    private BigDecimal sales_proceed_received;

    private String sales_notes;
    private BigDecimal percentage_amount_collected;

    private BigDecimal collected_amount;
    @OneToMany(cascade = CascadeType.ALL, targetEntity = Collateraldocument.class, fetch = FetchType.LAZY )
    @JoinColumn(name="Collateral_fk", referencedColumnName = "sn")
    private List<Collateraldocument> documents;
    //*****************Operational Audit *********************
    @Column(length = 30, nullable = false)
    private String postedBy;
    @Column(nullable = false)
    private Character postedFlag = 'Y';
    @Column(nullable = false)
    private Date postedTime;
    private String modifiedBy;
    private Character modifiedFlag = 'N';
    private Date modifiedTime;
    private String verifiedBy;
    private Character verifiedFlag = 'N';
    private Date verifiedTime;
    private String deletedBy;
    private Character deletedFlag = 'N';
    private Date deletedTime;
}
