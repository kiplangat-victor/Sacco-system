package com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TranCoinage;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "coinageCode", "transaction_header_id" }) })
public class TranCoinage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 10, nullable = false)
    @JsonProperty(required = true)
    private String coinageCode;
    @Column(nullable = false)
    private  Double value;
    @Column(nullable = false)
    private  Integer count;
    @Column(nullable = false)
    private  Double total;
    @Column(length = 15, nullable = false)
    private String currencyType;
}


