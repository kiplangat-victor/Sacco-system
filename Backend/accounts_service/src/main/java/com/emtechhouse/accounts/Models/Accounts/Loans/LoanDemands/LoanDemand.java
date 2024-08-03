package com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands;

import com.emtechhouse.accounts.Models.Accounts.Loans.Loan;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.DemandSatisfaction.InterestCollected.InterestCollected;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.DemandSatisfaction.LoanDemandSatisfaction;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class LoanDemand {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "sn", nullable = false)
    private Long sn;
    private String demandCode;
    private String transactionCode;
    private String acid;
    private Double demandAmount;
    private Double interestAmount;
    private Double principalAmount;
    private Double feeAmount=0.0;
    private Double penalInterestAmount;
    private Date demandDate;
    private String demandType;  //principal Demand or interest Demand
    private Date adjustmentDate; //
    private Double adjustmentAmount = 0.00;
    private Double loanRemainingBalance;
    private Long loanScheduleId;
    private Double overDueCarriedForward;
    private Character demandCarriedFowardFlag='N';
    private Date carriedForwardOn;
    private Character satisfactionCallerFlag='N';
    private Character deletedFlag = 'N';
    private Character penaltyPlaced = 'N';
    private String createdBy;
    private Date createdOn;

    private String batchNo = "afterreversals";

    @Transient
    public Double totalAmountAssigned;
    @Transient
    public Double totalInterestAssigned;
    @Transient
    public Double totalPenaltyAssigned;
    @Transient
    public Double totalPrincipleAssigned;

    @Transient
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Double interestAdjusted;
    @Transient
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Double principalAdjusted;
    @Transient
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Double interestBalance;
    @Transient
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Double principalBalance;

    @Transient
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Double sumInterestCollected;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "loanDemand")
    private List<LoanDemandSatisfaction> loanDemandSatisfactions = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "loanDemand")
    private List<InterestCollected> interestCollected = new ArrayList<>();

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    @JoinColumn(name = "loan_sn")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Loan loan;

    public void setLoanDemandSatisfactions(List<LoanDemandSatisfaction> loanDemandSatisfactions) {
        loanDemandSatisfactions.forEach(loanDemandSatisfaction -> loanDemandSatisfaction.setLoanDemand(this));
        this.loanDemandSatisfactions = loanDemandSatisfactions;
    }

    public Double getInterestAdjusted(){
        Double intAdjusted= 0.0;
        if(this.adjustmentAmount>=this.interestAmount){
            intAdjusted=this.interestAmount;
        }else {
            intAdjusted=this.adjustmentAmount;
        }
        return intAdjusted;
    }
    public Double getPrincipalAdjusted(){
        Double principalAdjusted= 0.0;
        if(this.adjustmentAmount>this.interestAmount){
            principalAdjusted=this.adjustmentAmount-this.interestAmount;
        }
        return principalAdjusted;
    }

    public Double getInterestBalance(){
        return this.interestAmount-this.getInterestAdjusted();
    }
    public Double getPrincipalBalance(){
        return this.principalAmount-this.getPrincipalAdjusted();
    }

    public Double getSumInterestCollected(){
        Double sum=0.0;
        if(this.interestCollected.size()>0){
             sum = this.interestCollected.stream()
                    .collect(Collectors.summingDouble(InterestCollected::getAmount));
        }
        return sum;
    }

    public boolean generatePenaltyInterestDemand() {
        return principalAmount > 0.0 && (penaltyPlaced == null || penaltyPlaced == 'N') ;
    }
}

