package com.emtechhouse.CustomerService.RetailMember.Nextofkeen;

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
public class Nextofkin {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.DATE)
    private Date dob;
    @Column(length = 50, nullable = false)
    private String relationship;
    private String occupation;
    private String name;
    private String phone;
    private Boolean is_beneficiary;
    private Boolean is_next_kin;
    private double allocation;
    private String birthCertificateNo;
    private String nationalId;
    private String supplimentaryInstructions;
}
