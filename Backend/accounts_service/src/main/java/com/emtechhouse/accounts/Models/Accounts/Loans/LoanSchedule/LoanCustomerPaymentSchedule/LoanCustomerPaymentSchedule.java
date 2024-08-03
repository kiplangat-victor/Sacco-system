package com.emtechhouse.accounts.Models.Accounts.Loans.LoanSchedule.LoanCustomerPaymentSchedule;

import com.emtechhouse.accounts.Models.Accounts.Loans.LoanSchedule.LoanSchedule;
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
public class LoanCustomerPaymentSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Double beginningBalance;
    private Double totalInstallmentToBePaid;
    private Double interestToBePaid;
    private Double principalToBePaid;
    private Double totalLoanBalance;
    private Double amountPaid;
    private Double remainingBalanceAfterPayment;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "loanSchedule_fk",  unique=true, nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private LoanSchedule loanSchedule;
}
