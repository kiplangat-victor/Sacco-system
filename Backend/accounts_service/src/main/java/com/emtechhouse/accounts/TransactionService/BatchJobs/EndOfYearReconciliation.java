//package com.emtechhouse.accounts.TransactionService.BatchJobs;
//
//import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.PartTrans.PartTran;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import lombok.ToString;
//
//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import java.util.Date;
//import java.util.List;
//
////@AllArgsConstructor
////@NoArgsConstructor
////@ToString
//@Data
//@Entity
//public class EndOfYearReconciliation {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String entityId;
//    private String transactionType;
//    private Date transactionDate;
//    private String currency;
//    private String enteredBy;
//    private Character enteredFlag;
//    private Date enteredTime;
//    private String transactionCode;
////    private String acid;
//    // Additional fields as needed
//
//    // Constructors, getters, and setters
//}