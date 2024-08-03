package com.emtechhouse.accounts.Models.Accounts.Nominees;

import com.emtechhouse.accounts.Models.Accounts.Account;
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
public class Nominee {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String emailAddress;
    @Column(nullable = false)
    private String firstName;
    private  String middleName;
    @Column(nullable = false)
    private String lastName;
    @Column

    private String identificationNo;
    private String occupation;
    private Long phone;
    private String relationship;
//    private Character nomineeMinor; //Yes or No
    private String nomineeMinor; //Yes or No
    @Temporal(TemporalType.TIMESTAMP)
    private Date dob;

//    @OneToOne(mappedBy = "nominee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private Guardian guardian;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id_fk")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Account account;






}
