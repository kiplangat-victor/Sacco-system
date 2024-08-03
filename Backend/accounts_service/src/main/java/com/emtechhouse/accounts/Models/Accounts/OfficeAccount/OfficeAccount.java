package com.emtechhouse.accounts.Models.Accounts.OfficeAccount;

import com.emtechhouse.accounts.Models.Accounts.Account;
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
public class OfficeAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String accountHeadName;
    @Column(nullable = false)
    private String accountSupervisorName;
    @Column(nullable = false)
    private String accountSupervisorId;
    @Column(nullable = false)
    private Double cashLimitDr;
    @Column(nullable = false)
    private Double cashLimitCr;
    @Column(nullable = false)
    private Double transferLimitDr;
    @Column(nullable = false)
    private Double transferLimitCr;
    @Column(nullable = false)
    private String clearingLimitCrExce;


    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id_fk")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Account account;
}
