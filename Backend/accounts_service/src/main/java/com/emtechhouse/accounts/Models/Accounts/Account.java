package com.emtechhouse.accounts.Models.Accounts;

import com.emtechhouse.accounts.Models.Accounts.AccountClosure.AccountActivationInfo;
import com.emtechhouse.accounts.Models.Accounts.AccountClosure.AccountClosureInfo;
import com.emtechhouse.accounts.Models.Accounts.AccountDocuments.AccountDocument;
import com.emtechhouse.accounts.Models.Accounts.AccountServices.AccountService;
import com.emtechhouse.accounts.Models.Accounts.AccountsRecurringFees.RecurringFeeDemand;
import com.emtechhouse.accounts.Models.Accounts.CurrentAccount.CurrentAccount;
import com.emtechhouse.accounts.Models.Accounts.LienComponent.Lien;
import com.emtechhouse.accounts.Models.Accounts.Loans.Loan;
import com.emtechhouse.accounts.Models.Accounts.Nominees.Nominee;
import com.emtechhouse.accounts.Models.Accounts.OfficeAccount.OfficeAccount;
import com.emtechhouse.accounts.Models.Accounts.AccountsRecurringFees.AccountRecurringFee;
import com.emtechhouse.accounts.Models.Accounts.RelatedParties.RelatedParties;
import com.emtechhouse.accounts.Models.Accounts.Savings.Saving;
import com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.SpecificProductDetails.SavingsAccountProduct;
import com.emtechhouse.accounts.Models.Accounts.TermDeposit.TermDeposit;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(exclude = {"savings", "loan"})
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Cacheable(false)
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String entityId;
   @Column(nullable = false, updatable = false, length = 15, unique=true)
    private String acid;
    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date openingDate;
    @Column(nullable = false, length = 3)
    private String currency;
    @Column(nullable = false, scale = 2, precision = 15)
    private Double accountBalance=0.0;
    @Column(nullable = false, scale = 2, precision = 15)
    private Double bookBalance=0.0;
    @Column(nullable = false, scale = 2, precision = 15)
    private Double lienAmount=0.0;
    @Column(nullable = false)
    private String accountStatus; // Whether active, dormant , suspended, frozen e.t.c
    @Column(nullable = false)
    private String statusChangedBy = "";
    private Date statusChangedWhen;
    @Column(nullable = false, updatable = false, length = 6)
    private String productCode;
    @Column(nullable = false)
    private String solCode;
    @Column(nullable = false)
    private String glSubhead;
    private String glCode;
    @Column(nullable = false)
    private String accountType; // LAA, SBA, CAA, ODA, OBA,--- PRODUCT TYPE
    @Column(length = 100)
    private String accountName;
    @Column(length = 20, nullable = false)
    private String customerCode;
    private String accountManager;
    private String managerCode;
    private Boolean withholdingTax;

    private String cashExceptionLimitDr;
    private String cashExceptionCr;
    private String transferExceptionLimitDr;
    private String transferExceptionLimitCr;
    private String dispatchMode;

    private String customerType;

    private String misSectorCode;

    private String misSubSectorCode;
    private Boolean accountStatement;
    private Boolean receiveSmsNotifications;
    private Boolean receiveEmailNotifications;
    private String transactionPhone;
    private String statementFreq;
    @Column(nullable = false)
    private Boolean isWithdrawalAllowed = true;
    @Column(length = 15, nullable = false)
    private Double minAccountBalance = 0.00;
    private String operationMode;
    private Boolean checkedJointAccount;
    @Column(nullable = false)
    private Boolean tellerAccount=false;
    private Boolean agencyAccount=false;

    @JsonIgnore
    private Character overdrawingPower;
    @Column(length = 15)
    private String referredBy;
    @Column(nullable = false)
    private String accountOwnership;

    @OneToMany(targetEntity = AccountRecurringFee.class, cascade = CascadeType.ALL, mappedBy = "account")
    @ToString.Exclude
    private List<AccountRecurringFee> accountRecurringFees;

    @OneToMany(targetEntity = RecurringFeeDemand.class, cascade = CascadeType.ALL, mappedBy = "account")
    @ToString.Exclude
    private List<RecurringFeeDemand> recurringFeeDemands;

    //Operation Flags
    private String savingsAccountTypeFlag; // ORDINARY SAVINGS OSA, SHARE CAPITAL SCA, DEPOSIT CONTRIBUTION DCA,
    @Column(nullable = false)
    private Character postedFlag;
    @Column(nullable = false)
    private Date postedTime;
    @Column(nullable = false, length = 15)
    private String postedBy;
    @Column(nullable = false)
    private Character verifiedFlag;
    private Date verifiedTime;
    @Column(length = 15)
    private String verifiedBy;
    @Column(length = 15)
    private String modifiedBy;
    private Date modifiedTime;
    @Column(nullable = false)
    private Character deleteFlag;
    private Date deleteTime;
    @Column(length = 15)
    private String deletedBy;
    private String approvalSentBy;
    private String approvalSentTo;
    private Character approvalSentFlag = 'N';
    private Date approvalSentTime;
    private Character rejectedFlag ='N';
    private Date rejectedTime;
    private String rejectedBy;

    @Transient
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private SavingsAccountProduct savingsAccountProduct; //interest rate code

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private Loan  loan;

    //Savings Account
    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private Saving  savings;

    //Term Deposit
    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private TermDeposit termDeposit;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "history_account")
    private List<TermDeposit> historicalTDAs = new ArrayList<>();

    //Current account
    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private CurrentAccount currentAccount;
    //Office Account
    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private OfficeAccount officeAccount;

    //Related parties
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<RelatedParties> relatedParties;

    @OneToMany(targetEntity = Nominee.class, cascade = CascadeType.ALL, mappedBy = "account")
    @ToString.Exclude
    private List<Nominee> nominees;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "account")
    @ToString.Exclude
    private List<AccountDocument> accountDocuments = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "sourceAccount")
    @ToString.Exclude
    private List<Lien> debitLiens = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "destinationAccount")
    @ToString.Exclude
    private List<Lien> creditLiens = new ArrayList<>();

    //account closure
    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private AccountClosureInfo accountClosureInfo;

    //account activation
    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private AccountActivationInfo accountActivationInfo;
    @OneToMany(targetEntity = RecurringFeeDemand.class, cascade = CascadeType.ALL, mappedBy = "account")
    @ToString.Exclude
    private List<RecurringFeeDemand> recurringFeesDemandDemands;

    public void  setLoan(Loan loan) {
        loan.setAccount(this);
        this.loan = loan;
    }
    public void setTermDeposit(TermDeposit termDeposit) {
        termDeposit.setAccount(this);
        this.termDeposit=termDeposit;
    }
    public void setOfficeAccount(OfficeAccount officeAccount){
        officeAccount.setAccount(this);
        this.officeAccount=officeAccount;
    }

    public void setSavings(Saving savings){
        savings.setAccount(this);
        this.savings=savings;
    }
    public void setCurrentAccount(CurrentAccount currentAccount){
        currentAccount.setAccount(this);
        this.currentAccount=currentAccount;
    }
    public void setRelatedParties( List<RelatedParties> relatedParties){
        relatedParties.forEach(relatedParty -> relatedParty.setAccount(this));
        this.relatedParties=relatedParties;
    }

    public void setNominees(List<Nominee> nominees) {
        nominees.forEach(nominee -> nominee.setAccount(this));
        this.nominees=nominees;
    }

    public void setAccountDocuments(List<AccountDocument> accountDocuments) {
        accountDocuments.forEach(accountDocument -> accountDocument.setAccount(this));
        this.accountDocuments = accountDocuments;
    }

//    public SavingsAccountProduct getSavingsAccountProduct(){
//        AccountService s= ApplicationContextProvider.bean(AccountService.class);
//        return s.getSavingProdDetails(this.getProductCode());
//    }

}
