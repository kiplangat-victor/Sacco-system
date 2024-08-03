package com.emtechhouse.CustomerService.RetailMember.Beneficiary;

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
public class Beneficiary {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 50, nullable = false)
    private String firstName;
    private String middleName;
    @Column(length = 50, nullable = false)
    private String lastName;
    private String isMinor;
    private String birthCertificateNo;
    private String nationalId;
    @Temporal(TemporalType.DATE)
    private Date dob;
    private String occupation;
    @Column(length = 50, nullable = false)
    private String relationship;
    private String supplimentaryInstructions;
}
