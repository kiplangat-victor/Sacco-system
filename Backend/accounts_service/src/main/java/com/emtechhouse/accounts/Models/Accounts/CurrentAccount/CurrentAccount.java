package com.emtechhouse.accounts.Models.Accounts.CurrentAccount;

import com.emtechhouse.accounts.Models.Accounts.Account;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class CurrentAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id_fk")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Account account;

}
