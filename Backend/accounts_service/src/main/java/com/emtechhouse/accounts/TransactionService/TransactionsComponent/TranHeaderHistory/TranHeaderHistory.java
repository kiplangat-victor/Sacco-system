package com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeaderHistory;


import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeaderHistory.Charge_partran_history.Charge_partrans_history;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeaderHistory.PartTransHistory.PartTranHistory;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;
import java.util.List;


@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "htd")
public class TranHeaderHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long sn;
    private String chequeType;
    @Column(length = 50)
    private String chequeNo;
    @Column(length = 20, nullable = false, unique = true)
    private String transactionCode;
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

    @OneToMany(targetEntity = PartTranHistory.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "transaction__history_header_id", referencedColumnName = "sn")
    private List<PartTranHistory> partTrans;
    @OneToMany(targetEntity = Charge_partrans_history.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "transaction__history_header_id", referencedColumnName = "sn")
    private List<Charge_partrans_history> chargePartran;

    //*****************Operational Audit *********************
    private String postedBy;
    private Character postedFlag = 'N';
    private Date postedTime;
    private String modifiedBy;
    private Character modifiedFlag = 'N';
    private Date modifiedTime;
    private String verifiedBy;
    private Character verifiedFlag = 'N';
    private Date verifiedTime;
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
