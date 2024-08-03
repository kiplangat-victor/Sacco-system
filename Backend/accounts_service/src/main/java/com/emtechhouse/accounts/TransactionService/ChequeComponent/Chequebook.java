package com.emtechhouse.accounts.TransactionService.ChequeComponent;

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
public class Chequebook {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String entityId;
    private String accountName;
    private String accountNumber;
    private  Date expiryDate;
    private Date dateOfIssue;
    @Column(length=5)
    private Integer noOfLeafs;
    private String leafStatus;
    private Integer startSerialNo;
    private Integer endSerialNo;
    private String customerCode;
    private String micrCode;
    @Column(length = 500)
    private String ChqLvsStat;

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
