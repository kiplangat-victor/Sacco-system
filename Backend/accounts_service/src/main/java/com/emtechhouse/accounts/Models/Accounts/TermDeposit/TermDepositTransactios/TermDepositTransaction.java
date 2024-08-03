package com.emtechhouse.accounts.Models.Accounts.TermDeposit.TermDepositTransactios;

import com.emtechhouse.accounts.Models.Accounts.TermDeposit.TermDeposit;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class TermDepositTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String creditAccount;
    @Column(nullable = false)
    private String debitAccount;
    @Column(nullable = false)
    private String transactionDescription;
    @Column(nullable = false)
    private Double amount;
    @Column(nullable = false)
    private String transactionStatus; //SUCCESSFULL, FAILED
    private String transactionCode;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "term_deposit_fk")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private TermDeposit termDeposit;
}
