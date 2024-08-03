package com.emtechhouse.accounts.Models.Accounts.Loans.LoanFees;

import com.emtechhouse.accounts.Models.Accounts.Loans.Loan;
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
public class LoanFees {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String eventIdCode;
    private String eventTypeDesc;
    private String chargeCollectionAccount;
    private Double initialAmt;
    private Double monthlyAmount=0.0;
    private Date nextCollectionDate;
    private String recurEventIdCode;
    private Character paidFlag='N';

    private Character closedFlag='N';
    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "loan_sn")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Loan loan;

}