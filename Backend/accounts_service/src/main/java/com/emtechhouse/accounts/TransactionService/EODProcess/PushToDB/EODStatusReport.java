package com.emtechhouse.accounts.TransactionService.EODProcess.PushToDB;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
@Data
public class EODStatusReport {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date prevSystemDate;
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date nextSystemDate;
    @Column(nullable = false)
    private String entityId;
    @Column(nullable = false)
    private boolean htdDataBackup = false;
    @Column(nullable = false)
    private boolean dtdDataBackup = false;
    @Column(nullable = false)
    private boolean disableUserAccounts = false;
    @Column(nullable = false)
    private boolean preEodDbBackup = false;
    @Column(nullable = false)
    private boolean checkNotPostedTrans = false;
    @Column(nullable = false)
    private boolean checkUnverifiedTrans = false;
    @Column(nullable = false)
    private boolean checkDrAndCrSum = false;
    @Column(nullable = false)
    private boolean dtdToHtdBackup = false;
    @Column(nullable = false)
    private boolean moveSystemDate = false;
    @Column(nullable = false)
    private boolean enableUserAccounts = false;
    @Column(nullable = false)
    private boolean postEodDbBackup = false;
    @Column(nullable = false)
    private boolean eodStatus = false;

    @Column(length = 30, nullable = false)
    private String eodDoneBy;
    @Column(nullable = false)
    private Date eodTime;
}
