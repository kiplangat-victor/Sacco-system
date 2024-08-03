package com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader;

import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.ChargePartran.ChargePartran;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.ChecqueInstruments.ChequeInstruments;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.PartTrans.PartTran;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TranCoinage.TranCoinage;
import com.emtechhouse.accounts.Utils.CONSTANTS;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "dtd")
public class TransactionHeader {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sn;
    private String chequeType;
    private String staffCustomerCode;
    @Column(length = 20, nullable = false, unique = true)
    private String transactionCode;
    private String reversalTransactionCode;
    @Column(nullable = false)
    private String transactionType;
    @Column( nullable = false)
    private String currency;
    private String eodStatus="N";
    @Column(length = 6, nullable = false)
    private String entityId;
    @JsonIgnore
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date rcre = new Date();
    @Column(name = "tran_date",nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date transactionDate;
    private String status = CONSTANTS.ENTERED;
    private String mpesacode;
    private String chargeEventId;
    private String salaryuploadCode;
    private String tellerAccount;
    private String batchCode;

    private String conductedBy;
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = PartTran.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "transaction_header_id", referencedColumnName = "sn")
    private List<PartTran> partTrans;
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = TranCoinage.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "transaction_header_id", referencedColumnName = "sn")
    private List<TranCoinage> coinages;
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = ChargePartran.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "transaction_header_id", referencedColumnName = "sn")
    private List<ChargePartran> chargePartran;
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = ChequeInstruments.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "transaction_header_id", referencedColumnName = "sn")
    private List<ChequeInstruments> chequeInstruments;

    //*****************Operational Audit *********************
    private String acknowledgedBy;
    private Character acknowledgedFlag = 'N';
    private Date acknowledgedTime;

    private String approvalSentBy;
    private String approvalSentTo;
    private Character approvalSentFlag = 'N';
    private Date approvalSentTime;

    private String rejectedBy;
    private Character rejectedFlag = 'N';
    private Date rejectedTime;
    private String rejectedReason;

//    private String verifyRequestTo;
//    private Character verifyRequestFlag = 'N';
//    private Date verifyRequestTime;

    private String postedBy;
    private Character postedFlag = 'N';
    private Date postedTime;
    private String modifiedBy;
    private Character modifiedFlag = 'N';
    private Date modifiedTime;
    private String verifiedBy;
    private Character verifiedFlag = 'N';
    private Date verifiedTime;
    private String verifiedBy_2;
    private Character verifiedFlag_2 = 'N';
    private Date verifiedTime_2;
    private String reversedBy;
    private Character reversedFlag = 'N';
    private Character reversalPostedFlag = 'N';
    private String reversedWithTransactionCode;
    private Date reversedTime;
    private Double totalAmount;
    private String deletedBy;
    private Character deletedFlag = 'N';
    private Date deletedTime;
    @Column(length = 100, nullable = false)
    private String enteredBy;
    @Column(length = 1, nullable = false)
    private Character enteredFlag;
    @Column(nullable = false)
    private Date enteredTime;
}