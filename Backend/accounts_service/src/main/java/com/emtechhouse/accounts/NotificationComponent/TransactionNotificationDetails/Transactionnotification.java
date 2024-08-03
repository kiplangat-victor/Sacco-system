package com.emtechhouse.accounts.NotificationComponent.TransactionNotificationDetails;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
//successfull notifications will be Deleted after 3 months
public class Transactionnotification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String debitedAcName;
    @Column(nullable = false)
    private String debitedAcNo;
    @Column(nullable = false)
    private String creditedAcName;
    @Column(nullable = false)
    private String creditedAcNo;
    @Column(nullable = false)
    private Double amount;
    @Column(nullable = false)
    private String transactionId;
    @Column(nullable = false)
    private Date transactionDate;
    @Column(nullable = false)
    private String status = "Pending"; //Successful
    @Column(nullable = false)
    private Character sentFlag = 'N';
    @Column(nullable = false)
    private Date sentTime;
}
