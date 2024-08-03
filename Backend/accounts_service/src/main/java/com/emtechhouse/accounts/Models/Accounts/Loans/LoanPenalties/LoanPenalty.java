package com.emtechhouse.accounts.Models.Accounts.Loans.LoanPenalties;

import com.emtechhouse.accounts.Models.Accounts.Loans.Loan;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "loan_penalties")
public class LoanPenalty {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Double penaltyAmount;
    private String penaltyDescription;
    private String loanAcid;
    private String loanName;
    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_sn")
    @JsonIgnore
    private Loan loan;
    //Operation Flags
    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Character postedFlag='Y';
    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date postedTime;
    @Column(nullable = false, length = 15)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String postedBy;
    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Character verifiedFlag = 'N';
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date verifiedTime;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(length = 15)
    private String verifiedBy;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(length = 15)
    private String modifiedBy;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(nullable = false)
    private Character modifiedFlag='N';
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date modifiedTime;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(nullable = false)
    private Character deletedFlag='N';
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date deleteTime;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String deletedBy;
}
