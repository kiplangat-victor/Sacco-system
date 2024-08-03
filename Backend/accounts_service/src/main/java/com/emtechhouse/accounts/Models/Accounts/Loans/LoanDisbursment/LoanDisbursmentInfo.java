package com.emtechhouse.accounts.Models.Accounts.Loans.LoanDisbursment;

import com.emtechhouse.accounts.Models.Accounts.Loans.Loan;
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
public class LoanDisbursmentInfo {
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
    @JoinColumn(name = "loan_sn")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Loan loan;
}
