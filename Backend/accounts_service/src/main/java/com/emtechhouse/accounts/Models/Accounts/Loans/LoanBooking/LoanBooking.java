package com.emtechhouse.accounts.Models.Accounts.Loans.LoanBooking;

import com.emtechhouse.accounts.Models.Accounts.Loans.Loan;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class LoanBooking {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "sn", nullable = false)
    private Integer id;
    private String transactionCode;
    private String interestType; // normal , penal etc
    private String bookingCode;
    private Double amountBooked;
    private String acid;
    private Date fromDate;
    private Date toDate;
    private String bookingFrequency; //EG MONTHS, WEEKS, DAYS, OTHER

    private Date bookedOn;
    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "loan_sn")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Loan loan;

}
