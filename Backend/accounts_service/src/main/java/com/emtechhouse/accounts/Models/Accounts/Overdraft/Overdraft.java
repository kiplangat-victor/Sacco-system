package com.emtechhouse.accounts.Models.Accounts.Overdraft;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Overdraft {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    String chequeAllowed;
    private boolean nomination;
    private String debitBalLimit;
}
