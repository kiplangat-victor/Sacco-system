package com.emtechhouse.accounts.Models.Accounts.Loans.InterestReversal.IntReversalItem;

import com.emtechhouse.accounts.Models.Accounts.Loans.InterestReversal.InterestReversal;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class IntReversalItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String loanAccount;
    private String accountName;
    private String memberNumber;
    private Date reversalStartDate;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = "salaryupload_fk")
    @JsonIgnore
    @ToString.Exclude
    private InterestReversal interestReversal;
}