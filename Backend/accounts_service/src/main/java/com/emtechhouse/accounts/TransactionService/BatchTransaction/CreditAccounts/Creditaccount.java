package com.emtechhouse.accounts.TransactionService.BatchTransaction.CreditAccounts;

import com.emtechhouse.accounts.TransactionService.BatchTransaction.Batchtransaction;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Data
//@AllArgsConstructor
//@NoArgsConstructor
@ToString
@Entity
public class Creditaccount {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String account;
    private String externalTranCode;
    private String idNumber;
    private Double amount;
    private String accountName;
    private String memberNumber;
    private String particulars;;
    private String Parttrantype = "Credit";

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_fk")
    @JsonIgnore
    private Batchtransaction batchtransaction;
    //tes
}
