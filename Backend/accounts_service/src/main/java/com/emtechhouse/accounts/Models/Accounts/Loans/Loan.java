package com.emtechhouse.accounts.Models.Accounts.Loans;

import com.emtechhouse.accounts.Models.Accounts.Account;
import com.emtechhouse.accounts.Models.Accounts.ApplicationContextProvider;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanAccrual.LoanAccrual;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanBooking.LoanBooking;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanCollateral.LoanCollateral;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDemands.LoanDemand;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanDisbursment.LoanDisbursmentInfo;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanFees.LoanFees;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanGuarantor.LoanGuarantor;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanPayOff.LoanPayOffInfo;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanPenalties.LoanPenalty;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanRescheduling.LoanReschedulingInfo;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanRestructuring.LoanRestructuringInfo;
import com.emtechhouse.accounts.Models.Accounts.Loans.LoanSchedule.LoanSchedule;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"loanGuarantors"})
@Entity
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long sn;
    private  String loanType; //Normal term loan , transfer , overdue
    @Column(length = 12, nullable = false, scale = 2, precision = 10)
    private Double principalAmount; // loan amount given
    @Column(precision = 10, scale = 10, nullable = false)
    private Double interestAmount; //backend
    @Column(precision = 10, scale = 10)
    private Double feesAmount; //backend
    @Column(precision = 10, scale = 10, nullable = false)
    private Double netAmount;// backend amount to be paid (principal+interest)
    @Column(nullable = false, scale = 10, precision = 10)
    private Double interestDemandAmount;// backend per demand e.g monthly, depends on the number of instalments
    @Column(nullable = false, precision = 10, scale = 2)
    private Double principalDemandAmount;// backend per demand e.g monthly
    @Column(nullable = false, precision = 10, scale = 2)
    private Double sumPrincipalDemand;//backend all the demanded  principal amount up to last demand
    @Column(nullable = false, precision = 10, scale = 2)
    private Double sumMonthlyFeeDemand=0.0;
    @Column(nullable = false, precision = 10, scale = 2)
    private Double sumRecurringFeeDemand=0.0;
//    @Column(nullable = false)
    private String repaymentPeriodId; // 'DAYS', 'WEEKS' ,'MONTHS', 'YEARS
//    @Column(nullable = false)
    private Integer repaymentPeriod; // eg 2 years
    @Column(nullable = false)
    private Date maturityDate; // backend
    @Column(nullable = false)
    private Long loanPeriodDays; //backend
    @Column(nullable = false)
    private Long loanPeriodMonths; //backend
    @Column(nullable = false)
    private Boolean collectInterest =true; //backend
    @Column(nullable = false)
    private Double dailyInterestAmount; //backend
    @Column(nullable = false)
    private Date interestCalculationStartDate; //backend
    @Column(nullable = false)
    private String interestCalculationMethod; //FIXED-RATE OR REDUCING-BALANCE
    @Column(nullable = false)
    private Date accrualLastDate;// holds the last accrual date, always start with the first interestCalculationStartDate
    @Column(nullable = false, precision = 10, scale = 10)
    private Double sumAccruedAmount=0.0;
    @Column(nullable = false)
    private Date bookingLastDate;// // holds the last booking date, always start with the first interestCalculationStartDate
    @Column(nullable = false, precision = 10, scale = 10)
    private Double sumBookedAmount=0.0;
    private String collateral;
    //-------------DISBURSMENT DETAILS------
    @Column(name = "disbursement_amount", precision = 10, scale = 10)
    private Double disbursementAmount = 0.00;
    //    @Column(nullable = false)
    private Date disbursementDate;
    private String disbursedBy;
    @Column(nullable = false)
    private Character disbursementFlag;
    @Column(nullable = false)
    private Character disbursementVerifiedFlag = 'N';
    private Date disbursmentVerifiedOn;
    private String disbursmentVerifiedBy;

    @Column(nullable = false, precision = 10, scale = 2)
    private Double overFlowAmount; // backend start set =0
    @Column(nullable = false)
    private Integer demandCarryForward = 0; // backend


//    interest details;
    @Column(nullable = false)
    private Double interestRate; //interest rate code
    @Transient
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Double currentRate; //interest rate code

    @Column(nullable = false, precision = 10, scale = 10)
    private Double interestPreferential=0.0;
    @Column(nullable = false)
    private String operativeAcountId;// savings account
    @Column(nullable = false)
    private String disbursmentAccount;
//    payment schedule
    @Column(nullable = false)
    private Integer numberOfInstallments; //
    @Column(nullable = false)
    private String frequencyId;//  'DAYS', 'WEEKS' ,'MONTHS', 'YEARS'
    @Column(nullable = false)
    private Integer frequencyPeriod;//  frequency in months
    @Column(nullable = false, precision = 10, scale = 10)
    private Double installmentAmount; //backend
    private Double alreadyIncomedInterest = 0.0;
    @Column(nullable = false)
    private Date installmentStartDate;
    @Column(nullable = false)
    private Date nextRepaymentDate; // backend
//    @Column(nullable = false)
    private Date nextInterestDemandDate; // backend
    private Date backupNextRepaymentDate; // backend
    private Date previousDemandDate; // backend
    @Column(nullable = false)
    private Double outStandingPrincipal; //backend
    @Column(nullable = false)
    private Double outStandingInterest; // backend
    @Column(nullable = false, precision = 10, scale = 10)
    private Double totalLoanBalance; // backend
    @Column(nullable = false)
    private String loanStatus; //NEW, APPROVED, DISBURSED, FULLY-PAID

    private String assetClassification = "NOT_CLASSIFIED"; // backend Performing Watch Sub standard Doubtful
    //payoff
    private Character payOffFlag;
    private Character payOffVerificationFlag;
    private Character payOffCancellationFlag;
    private String payOffInitiatedBy;
    private String payOffVerifiedBy;
    private String payOffCanceledBy;
    private Date payOffDate;
    private Date payOffVerificationDate;
    private String payOffCancellationDate;

    //guarantors amount and collaterals total
    private Double collateralsTotal =0.0;
    private Double guarantorsTotal= 0.0;
    private Double totalCollateralsAndGuarantors= 0.0;

    //restructuring

    private Character restructuredFlag='N';

    //demands
    private Character pausedDemandsFlag='N';
    private Character pausedSatisfactionFlag='N';

    private Character pauseBookingFlag= 'N';
    private Character pauseAccrualFlag = 'N';

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "loan")
    @ToString.Exclude
//    @JsonIgnore
    private List<LoanDemand> loanDemands = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "loan")
    @ToString.Exclude
    private List<LoanGuarantor> loanGuarantors = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "loan")
    @ToString.Exclude
    private List<LoanSchedule> loanSchedules = new ArrayList<>();


    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "loan")
    @ToString.Exclude
    private List<LoanFees> loanFees = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "loan")
    @ToString.Exclude
    private List<LoanCollateral> loanCollaterals = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "loan")
    @JsonIgnore
    @ToString.Exclude
    private List<LoanBooking> loanBooking = new ArrayList<>();
    @ToString.Exclude

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "loan")
    @JsonIgnore
    private List<LoanAccrual> loanAccrual = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "loan")
    @ToString.Exclude
    @JsonIgnore
    private List<LoanDisbursmentInfo> loanDisbursmentTransactions = new ArrayList<>();

    @OneToOne(mappedBy = "loan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @JsonIgnore
    private LoanPayOffInfo loanPayOffInfo;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "loan")
    @JsonIgnore
    private List<LoanRestructuringInfo> loanRestructuringInfos = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "loan")
    @JsonIgnore
    private List<LoanReschedulingInfo> loanReschedulingInfo = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "loan")
    @JsonIgnore
    private List<LoanPenalty> loanPenalties = new ArrayList<>();

    @OneToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id_fk")
    @JsonIgnore
    @ToString.Exclude
    private Account account;

    public void setLoanDemands(List<LoanDemand> loanDemands) {
        loanDemands.forEach(loanDemand -> loanDemand.setLoan(this));
        this.loanDemands = loanDemands;
    }

    public void setLoanGuarantors(List<LoanGuarantor> loanGuarantors) {
        loanGuarantors.forEach(loanGuarantor -> loanGuarantor.setLoan(this));
        this.loanGuarantors = loanGuarantors;
    }
    public void setLoanSchedules(List<LoanSchedule> loanSchedules) {
        loanSchedules.forEach(loanSchedule -> loanSchedule.setLoan(this));
        this.loanSchedules = loanSchedules;
    }

    public void setLoanCollaterals(List<LoanCollateral> loanCollaterals) {
        loanCollaterals.forEach(loanCollateral -> loanCollateral.setLoan(this));
        this.loanCollaterals = loanCollaterals;
    }

    public void setLoanFees(List<LoanFees> loanFees) {
        loanFees.forEach(loanFee -> {
            loanFee.setNextCollectionDate(this.installmentStartDate);
            loanFee.setLoan(this);});
        this.loanFees = loanFees;
    }

    public void setLoanBooking(List<LoanBooking> loanBooking) {
        loanBooking.forEach(loanBooking1 -> loanBooking1.setLoan(this));
        this.loanBooking = loanBooking;
    }

    public void setLoanAccrual(List<LoanAccrual> loanAccrual) {
        loanAccrual.forEach(loanAccrual1 -> loanAccrual1.setLoan(this));
        this.loanAccrual = loanAccrual;
    }
    public void setLoanDisbursmentTransactions(List<LoanDisbursmentInfo> loanDisbursmentTransactions) {
        loanDisbursmentTransactions.forEach(loanDisbursmentTransaction -> loanDisbursmentTransaction.setLoan(this));
        this.loanDisbursmentTransactions = loanDisbursmentTransactions;
    }

    public void setLoanPayOffInfo(LoanPayOffInfo loanPayOffInfo) {
        loanPayOffInfo.setLoan(this);
        this.loanPayOffInfo = loanPayOffInfo;
    }

    public Double getCurrentRate(){
        LoanService s= ApplicationContextProvider.bean(LoanService.class);
        return s.getLoanInterest(this.getAccount().getProductCode(),this.principalAmount,this.interestPreferential);
    }

}