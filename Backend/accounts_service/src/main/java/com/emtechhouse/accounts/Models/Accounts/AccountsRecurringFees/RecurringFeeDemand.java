package com.emtechhouse.accounts.Models.Accounts.AccountsRecurringFees;

import com.emtechhouse.accounts.Models.Accounts.Account;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class RecurringFeeDemand {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String eventCode;
    private Date feeDate;
    private Double amount;
    private Date collectedOn;
    private String transactionCode;
    private Double exerciseDutyCollected;
    private String exerciseTranscode;
    private Character accountDutyCollectedFlag='N';

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id_fk")
    @JsonIgnore
    private Account account;
}