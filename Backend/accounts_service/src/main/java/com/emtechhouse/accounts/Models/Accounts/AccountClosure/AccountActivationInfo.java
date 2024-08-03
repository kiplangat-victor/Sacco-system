package com.emtechhouse.accounts.Models.Accounts.AccountClosure;

import com.emtechhouse.accounts.Models.Accounts.Account;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity

public class AccountActivationInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public String activatedBy;
    public Date activationDate;
    public Character postedFlag;
    //verification
    public Character verifiedFlag;
    public String verifiedBy;
    public Date verificationDate;
    //reason
    public String activationReason;

    @OneToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id_fk")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Account account;
}

