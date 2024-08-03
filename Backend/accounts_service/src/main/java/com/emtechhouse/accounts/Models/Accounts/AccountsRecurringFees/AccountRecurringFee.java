package com.emtechhouse.accounts.Models.Accounts.AccountsRecurringFees;

import com.emtechhouse.accounts.Models.Accounts.Account;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class AccountRecurringFee {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String eventCode;
    @Column(nullable = false)
    private Date nextCollectionDate;
    private Date lastRunAttemptDate;
    @Column(nullable = false)
    private Date endDate;
    @Column(nullable = false)
    private String frequency = "MONTH";
    @ManyToOne( fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id_fk")
    @JsonIgnore
    @ToString.Exclude
    private Account account;

    @CreationTimestamp
    @Column(nullable = false)
    private Date createdOn;
}