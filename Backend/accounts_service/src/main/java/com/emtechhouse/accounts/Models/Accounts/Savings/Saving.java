package com.emtechhouse.accounts.Models.Accounts.Savings;

import com.emtechhouse.accounts.Models.Accounts.Account;
//import com.emtechhouse.accounts.Models.Accounts.AccountsLedgerFees.LedgerFee;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Saving {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

//    @Column(nullable = false)
    private Integer sba_savingPeriod;

    //@JsonIgnore
    @Temporal(TemporalType.DATE)
    private Date sba_maturedDate;

//    @Column(nullable = false)
    private Double sba_maturedValue;

//    @Column(nullable = false)
    private Double sba_monthlyValue;

//    @Column(nullable = false)
    private Date sba_startDate;



    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id_fk")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Account account;


}