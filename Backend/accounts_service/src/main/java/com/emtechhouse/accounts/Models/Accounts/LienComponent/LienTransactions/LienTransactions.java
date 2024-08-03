package com.emtechhouse.accounts.Models.Accounts.LienComponent.LienTransactions;
import com.emtechhouse.accounts.Models.Accounts.LienComponent.Lien;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class LienTransactions {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String transactionCode;
    private Date transactionDate;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = "lien_fk")
    @JsonIgnore
    private Lien lien;
}
