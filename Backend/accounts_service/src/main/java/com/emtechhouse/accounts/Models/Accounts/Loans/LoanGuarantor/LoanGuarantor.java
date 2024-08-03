package com.emtechhouse.accounts.Models.Accounts.Loans.LoanGuarantor;

import com.emtechhouse.accounts.Models.Accounts.Loans.Loan;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class LoanGuarantor {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String guarantorCustomerCode;
    private String loanSeries;
    private String guarantorType; //SALARY, DEPOSITS
    private Double guaranteeAmount;
    @Column(name="initial_gee_amount")
    private Double initialAmt;
    @ToString.Exclude
    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "loan_sn")
    @JsonIgnore
    private Loan loan;
}