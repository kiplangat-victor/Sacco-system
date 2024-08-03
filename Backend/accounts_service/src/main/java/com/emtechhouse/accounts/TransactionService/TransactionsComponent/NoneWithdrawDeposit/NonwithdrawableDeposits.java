package com.emtechhouse.accounts.TransactionService.TransactionsComponent.NoneWithdrawDeposit;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class NonwithdrawableDeposits {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long sn;
    private String accountName;
    private Double amount;
    @Column(nullable = false)
    private String entityId;
    private String acid;
    private Date collectionDate;
    private String month;
    private String status="pending";

}

