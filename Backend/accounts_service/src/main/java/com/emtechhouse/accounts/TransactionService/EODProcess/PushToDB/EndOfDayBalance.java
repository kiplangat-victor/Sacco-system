package com.emtechhouse.accounts.TransactionService.EODProcess.PushToDB;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
@Data
@Table(name = "eod_bal")
public class EndOfDayBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "acid", nullable = false)
    private String acid;

    @Column(name = "account_balance", nullable = false)
    private BigDecimal accountBalance;

    @Column(name = "tran_amount")
    private BigDecimal transactionAmount;

    @Column(name = "date",nullable = false)
    private Date date;

    @Column(name = "endOfDayDate", nullable = false)
    private Date endOfDayDate;

}
