package com.emtechhouse.accounts.Models.Accounts.AccountClosure;

import com.emtechhouse.accounts.Models.Accounts.Account;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class AccountClosureInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public String closedBy;
    public Date closingDate;
    public Character postedFlag;
    //verification
    public Character verifiedFlag;
    public String verifiedBy;
    public Date verificationDate;
    //reason
    public String closureReason;

    @OneToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id_fk")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Account account;
}
