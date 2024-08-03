package com.emtechhouse.System.PayablesAndReceivables;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
public class Payablesandreceivables {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 6, nullable = false)
    private String entityId;
    @Column(nullable = false)
    private String nonWithdrawableDepositAc;
    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date nonWithrawableDepositCollectionDate = new Date();
    @Column(nullable = false)
    private BigDecimal nonWithdrawalDepositAmount = new BigDecimal(0.0);
    @Column(nullable = false)
    private String benevolentAc;
    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date benevolentCollectionDate = new Date();
    @Column(nullable = false)
    private BigDecimal benevolentAmount = new BigDecimal(0.0);

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
