package com.emtechhouse.accounts.Models.Accounts.Loans.LoanRestructuring;

import com.emtechhouse.accounts.Models.Accounts.Loans.Loan;
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
public class LoanRestructuringInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private Date restructuredOn;
    @Column(nullable = false)
    private Double previousPrincipalAmount;
    @Column(nullable = false)
    private Double newPrincipalAmount;
    @Column(nullable = false)
    private Integer noOfDemandsCarriedForward;
    @Column(nullable = false)
    private Integer previousNumberOfInstallments; //
    @Column(nullable = false)
    private String previousFrequencyId;
    private String restructuredBy;
    @Column(nullable = false)
    private Double previousInstallmentAmount;
    @Column(nullable = false)
    private Date previousInstallmentStartDate;
    @Column(nullable = false)
    private Double previousInterestRate;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_sn")
    @JsonIgnore
    private Loan loan;
}
