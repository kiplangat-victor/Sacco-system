package com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.PartTrans;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class PartTran {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "sn")
    private Long sn;
    @Column(name = "part_tran_type", nullable = false)
    private String partTranType;

    private String parttranIdentity = "Normal";
    private String conductedBy;

    @Column(name = "tran_amount", scale = 2, precision = 15, nullable = false)
    private double transactionAmount;
    @Column(name = "acid", nullable = false)
    private String acid;
//    @Column(name = "transactionCode", nullable = false,length = 40)
    private String transactionCode;
    @Column(name = "currency", nullable = false)
    private String currency;
    @Column(name = "exchange_rate")
    private String exchangeRate;
    @Column(name = "tran_date")
    private Date transactionDate;
    @Column(name = "tran_particulars")
    private String transactionParticulars;
    @Column(name = "iso_flag", length = 1, nullable = false)
    private Character isoFlag;
    @Column(nullable = false)
    private double accountBalance;
    private String accountType;
    private String batchCode;
    private Character chargeFee = 'Y';

    private Boolean isWelfare = false;
    private String welfareCode;
    private String welfareAction;
    private String welfareMemberCode;
}