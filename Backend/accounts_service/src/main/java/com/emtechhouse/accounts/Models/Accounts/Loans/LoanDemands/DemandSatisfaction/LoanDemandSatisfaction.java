package com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.DemandSatisfaction;

import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.LoanDemand;
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
public class LoanDemandSatisfaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column(name = "acid")
    private String acid;
    private Double amount;
    private Date date;
    private String transactionCode;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "loan_demand_fk")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private LoanDemand loanDemand;
}
