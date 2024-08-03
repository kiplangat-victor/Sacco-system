package com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.ChecqueInstruments;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ChequeInstruments {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long sn;
    private String instrumentNo; //chequeNo;
    private String instrumentType; //CHQ
    private Date instrumentDate;//cheque date
    private Integer leafNo;



}
