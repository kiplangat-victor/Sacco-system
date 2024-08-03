package com.emtechhouse.accounts.TransactionService.MpesaDeposits;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class MpesaDepositsAPIRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String mpesaReceiptNumber;
    private String creditAccount;
    private Double tranAmount;
    private String tranDate;
    private String merchantRequestId;
    private String memberNo;
    private String sessionId;
    private int responseCode;
    private String responseDesc;
    private String phoneNumber;
    private String narration;
    private String paybillNo;
    private String status;
    private Date transactionDate;

}