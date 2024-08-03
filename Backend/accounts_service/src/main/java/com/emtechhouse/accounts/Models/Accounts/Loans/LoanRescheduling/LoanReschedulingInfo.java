package com.emtechhouse.accounts.Models.Accounts.Loans.LoanRescheduling;

import com.emtechhouse.accounts.Models.Accounts.Loans.Loan;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class LoanReschedulingInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private Date rescheduledOn;
    @Column(nullable = false)
    private String previousFrequencyId;
    @Column(nullable = false)
    private Double previousInstallmentAmount;
    @Column(nullable = false)
    private Date previousInstallmentStartDate;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_sn")
    @JsonIgnore
    private Loan loan;
}
