package com.emtechhouse.accounts.Models.Accounts.AccountDocuments;

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
public class AccountDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long sn;
    @Lob
//    @Column(nullable = false)
    private String documentImage;
//    @Column(length = 100, nullable = false)
    private String documentTitle;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id_fk")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Account account;

}
