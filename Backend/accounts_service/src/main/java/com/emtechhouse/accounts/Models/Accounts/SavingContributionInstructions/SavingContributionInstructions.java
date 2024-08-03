package com.emtechhouse.accounts.Models.Accounts.SavingContributionInstructions;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Cacheable(false)
public class SavingContributionInstructions {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false, unique = true)
    String savingCode;
    @Column(nullable = false)
    String customerCode;
    @Column(nullable = false)
    private Date nextCollectionDate;
    @Column(nullable = false)
    private Date lastCollectionDate;
    @Column(nullable = false)
    private Date applicationDate;
    @Column(nullable = false)
    private Date endDate;
    @Column(nullable = false)
    private String frequency = "MONTH";
    @Column(nullable = false)
    private String description;
    private String status;
    @Column(nullable = false)
    Double  amount;
    @Column(nullable = false)
    String executionType; //  BOTH, DEPOSITS_ONLY, SHARES_ONLY

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
    private String rejectedBy;
    private Character rejectedFlag = 'N';
    private Date rejectedTime;
    private String deletedBy;
    private Character deletedFlag = 'N';
    private Date deletedTime;

}