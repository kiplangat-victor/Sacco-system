package com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.DemandSatisfaction.InterestCollected;

import com.emtechhouse.accounts.Models.Accounts.Account;
import com.emtechhouse.accounts.Models.Accounts.Loans.Loan;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.LoanDemand;
import com.emtechhouse.accounts.Requests.LoanPrepayRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class InterestCollected {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "sn", nullable = false)
    private Long sn;
    private Double amount;
    private String transactionCode;
    private Date collectedOn;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    @JoinColumn(name = "loan_sn")
    @JsonIgnore
    private LoanDemand loanDemand;



    public void LoanPrepayRequest(LoanPrepayRequest loanPrepayRequest) {
    }

    public void Account(Account loanAccount) {
    }
}