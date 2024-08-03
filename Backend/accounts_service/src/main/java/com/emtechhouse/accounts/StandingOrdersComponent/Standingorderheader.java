package com.emtechhouse.accounts.StandingOrdersComponent;

import com.emtechhouse.accounts.Models.Accounts.Nominees.Nominee;
import com.emtechhouse.accounts.StandingOrdersComponent.StandingOrderDestination.Standingorderdestination;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Standingorderheader {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String entityId;
    @Column(nullable = false, unique = true)
    private String standingOrderCode;
    @Column(nullable = false)
    private Date applicationDate;
    @Column(nullable = false)
    private String status = "ACTIVE";
    @Column(nullable = false)
    private String sourceAccountNo;
    @Column(nullable = false)
    private String customerCode;
    private String description = "Standing order payments";
    @Column(nullable = false)
    private Double amount = 0.00;
    @Column(nullable = false)
    private Integer maxTrialDays = 0;
    private String type;
    @Column(nullable = false)
    private String duration;
    @Column(nullable = false)
    private Date effectiveStartDate;
    @Column(nullable = false)
    private Date endDate;
    @Column(nullable = false)
    private Date nextRunDate;
    @Column(nullable = false)
    private String allowPartialDeduction;

    @OneToMany(mappedBy = "standingorderheader", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @ToString.Exclude
    List<Standingorderdestination> standingorderdestinations;


    public void setStandingorderdestinations(List<Standingorderdestination> standingorderdestinations) {
        standingorderdestinations.forEach(standingorderdestination -> standingorderdestination.setStandingorderheader(this));
        this.standingorderdestinations=standingorderdestinations;
    }

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