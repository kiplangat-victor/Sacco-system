package com.emtechhouse.accounts.TransactionService.MpesaWithdrawals;

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
public class MpesaWithdrawalAPIRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String mpesaReceiptNumber;
    private String debitAccount;
    private Double tranAmount;
    private String tranDate;
    private String memberNo;
    private String sessionId;
    private String senderPhoneNumber;
    private String recipientPhoneNumber;
    private String narration;
    private String paybillNo;
    private String originatorConversationId;
    private String conversationId;
    private int responseCode;
    private String responseDesc;
    private String resultType;
    private String status;
    private Date transactionDate;
}