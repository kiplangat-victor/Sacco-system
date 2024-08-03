package com.emtechhouse.System.CustomerType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class CustomerType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 6, nullable = false)
    private String entityId;
    @Column(length = 10, nullable = false, unique = true)
    private String customerType;
    @Column(length = 40, nullable = false)
    private String dbTable;
    @Column(length = 200, nullable = false, columnDefinition = "TEXT")
    private String description;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String codeStructure = "TYPERUNNO";
    @Column(nullable = false, length = 4,columnDefinition = "INT")
    private Integer runningSize ;
    @Column(columnDefinition = "DOUBLE")
    private Double minimumMonthlyContribution  = 300.00;
    private String contributionProduct;
    @Column(nullable = false)
    private Character isJunior = 'N';
    @Column(nullable = false)
    private Character chargeRegFee = 'N';
    private Double registrationFeeAmount = 0.0;
    private Character autoCreateAccounts = 'N';
    private String autoCreateAccountProductCode;

    @Column(length = 30, nullable = false)
    private String postedBy;
    @Column(nullable = false)
    private Character postedFlag = 'Y';
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