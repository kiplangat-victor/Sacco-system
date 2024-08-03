package com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.ChargePartran;


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
public class ChargePartran {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "sn")
    private Long sn;
    @Column(name = "part_tran_type", nullable = false)
    private String partTranType;
    @Column(name = "tran_amount", nullable = false)
    private double transactionAmount;
    @Column(name = "acid", nullable = false,length = 40)
    private String acid;
    @Column(name = "currency", nullable = false)
    private String currency;
    @Column(name = "exchange_rate")
    private String exchangeRate;
    @Column(name = "tran_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date transactionDate;
    @Column(name = "tran_particulars")
    private String transactionParticulars;
    @Column(name = "iso_flag", length = 1, nullable = false)
    private Character isoFlag;
    @Column(nullable = false)
    private double accountBalance;
    private String accountType;
}
