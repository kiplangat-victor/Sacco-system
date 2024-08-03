package com.emtechhouse.accounts.Models.Accounts.TermDeposit.Accrual;

import com.emtechhouse.accounts.Models.Accounts.Loans.Loan;
import com.emtechhouse.accounts.Models.Accounts.TermDeposit.TermDeposit;
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
public class TermDepositAccrual {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "sn", nullable = false)
    private Integer id;
    private String interestType; // normal , penal
    private Double amountAccrued;
    private String acid;
    private Date fromDate;
    private Date toDate;
    private String accrualFrequency; //EG MONTHS, WEEKS, DAYS, OTHER
    private Date accruedOn;
    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "term_deposit_fk")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private TermDeposit termDeposit;
}
