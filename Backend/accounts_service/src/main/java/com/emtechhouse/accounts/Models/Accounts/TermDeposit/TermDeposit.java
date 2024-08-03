package com.emtechhouse.accounts.Models.Accounts.TermDeposit;
import com.emtechhouse.accounts.Models.Accounts.Account;
import com.emtechhouse.accounts.Models.Accounts.TermDeposit.Accrual.TermDepositAccrual;
import com.emtechhouse.accounts.Models.Accounts.TermDeposit.TermDepositTransactios.TermDepositTransaction;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class TermDeposit{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    //    scheme details
    @Column(nullable = false)
    private Double termDepositAmount;
    @Column(nullable = false)
    private Double maturityValue;
    @Column(nullable = false)
    private Integer periodInMonths; //in months e.g 2 months
    @Column(nullable = false)
    private Long periodInDays; //in months e.g 2 months
    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date maturityDate; //calculated by system
    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date valueDate;
    @Column(nullable = false)
    private Double interestAmount;

//    @Column(nullable = false)
//    private String renewalInstructions; //P - renew Principal PI - renew P & I

//    interest and tax
    @Column(nullable = false)
    private Double interestRate; //interest table code e.g 12.5 per annum
    @Column(nullable = false, precision = 10, scale = 10)
    private Double interestPreferential=0.0;
    @Column(nullable = false)
    private Double withholdingTax=0.0; //e.g 2%
    @Column(nullable = false)
    private Double withholdingTaxAmount=0.0; //e.g 2%

    @Column(nullable = false)
    private String interestCrAccountId;// acid for savings account
    @Column(nullable = false)
    private String principalCrAccountId;// acid for savings account
    @Column(nullable = false)
    private String principalDrAccountId;


    
    @Column(nullable = false)
    private Date accrualLastDate;
    @Column(nullable = false, precision = 10, scale = 10)
    private Double sumAccruedAmount=0.0;

    @Column(nullable = false)
    private String termDepositStatus;
    private Character pauseAccrualFlag='N';

    @Column(nullable = false)
    private Double interestPaid=0.0;
    private Double principalPaid=0.0;
    private Date paymentDate;

    private Character rollOver = 'N';
    private Integer rollOverDurationInMonths;
    private String rollOverType = "";//INTEREST_ONLY/PRINCIPAL_ONLY/WHOLE_AMOUNT


    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "termDeposit")
    @ToString.Exclude
    private List<TermDepositAccrual> termDepositAccruals = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "termDeposit")
    @ToString.Exclude
    private List<TermDepositTransaction> termDepositTransactions = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Account account;
}

