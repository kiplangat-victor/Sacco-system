package com.emtechhouse.accounts.Models.Accounts.Loans.LoanCollateral;

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
public class LoanCollateral {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String collateralSerial;
    private String collateralName;
    private String collateralType;
    @Column(nullable = false)
    public Double collateralValue;
    public Character verifiedFlag='N';

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "loan_sn")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Loan loan;
}
