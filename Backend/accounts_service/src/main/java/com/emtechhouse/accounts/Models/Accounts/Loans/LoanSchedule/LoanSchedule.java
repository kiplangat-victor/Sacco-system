package com.emtechhouse.accounts.Models.Accounts.Loans.LoanSchedule;

import com.emtechhouse.accounts.Models.Accounts.Loans.Loan;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
//@EqualsAndHashCode(exclude = {"loanCustomerPaymentSchedule"})
public class LoanSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public LocalDate startDate;
    public Integer installmentNumber;
    public String installmentDescription;
    public Double installmentAmount;
    public Double principleAmount;
    public Double interestAmount;
    //added
    public Double beginningBalance;
    public Double principalOutstanding;// it should be total loan outstanding not principal outstanding
    public LocalDate demandDate;
    public Long daysDifference;
    public Double dailyInterest;
    public Double monthlyInterest;
    public Double monthlyInterestRate;
    public Double cumulativeInterest;
    public String status;

    public Boolean overdue=false;

    @Column(nullable = false)
    private Character deleteFlag;
    private Date deleteTime;
    private String deletedBy;

//    @OneToOne(mappedBy = "loanSchedule", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @ToString.Exclude
//    private LoanCustomerPaymentSchedule loanCustomerPaymentSchedule;
    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "loan_sn")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ToString.Exclude
    private Loan loan;
}
