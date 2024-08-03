package com.emtechhouse.System.InterestCodeParams;

import com.emtechhouse.System.InterestCodeParams.Interestcodeslabs.Interestcodeslabs;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class Interestcodeparams {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 6, nullable = false)
    private String entityId;
    @Column(length = 6, nullable = false, unique = true)
    @JsonProperty(required = true)
    private String interestCode;
    @Column(length = 200, nullable = false)
    private String interestName;
    @Column(length = 200, nullable = false)
    private String interestType; //slab or flat rate;
    @Column(length = 200, nullable = false)
    private String parttrantype;// Dr/Cr
    private Double interestrate = 0.00;
    private Date fromDate;
    private Date toDate;
    private Double penalInterest;
    @Column(nullable = false)
    private String  penalIntBasedOn;
    private String penalInterestType = "Fixed_amount"; //Fixed_amount or Percentage
    private String calculationMethod;
    private String interestPeriod;

    @OneToMany(targetEntity = Interestcodeslabs.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "interestcodeparams_id", referencedColumnName = "id")
    private List<Interestcodeslabs> interestcodeslabs;

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
