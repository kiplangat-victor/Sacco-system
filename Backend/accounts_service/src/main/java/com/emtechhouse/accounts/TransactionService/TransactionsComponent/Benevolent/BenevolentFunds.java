package com.emtechhouse.accounts.TransactionService.TransactionsComponent.Benevolent;

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
public class BenevolentFunds {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long sn;
    private String accountName;
    private Double amount;
    private String acid;
    private Date collectionDate;
    private String month;
    private String status="pending";
    @Column(nullable = false)
    private String entityId;
}