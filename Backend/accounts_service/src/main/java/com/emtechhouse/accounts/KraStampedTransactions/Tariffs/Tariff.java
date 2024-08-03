package com.emtechhouse.accounts.KraStampedTransactions.Tariffs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Tariff {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false, unique = true)
    private String tarrifCd;//": "001"
    private String tarrifNm;//": "ATM withdrawal",
    private String exDtCharge;//": "Y",
    private Double exdutyRt;//": 20
}
