package com.emtechhouse.System.Employer;

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
public class Employer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 20, nullable = false)
    private String entityId;
    @Column(length = 20, nullable = false, unique = true, updatable = false)
    private String employerCode;
    @Column(length = 20, unique = true)
    private String customerCode;
    @Column(nullable = false, length = 200)
    private String customerName;
    @Column(length = 100, nullable = false, unique = true)
    private String name;
    private String registrationNumber;
    private String town;
    private String organizationType;
    private String address;
    private String website;
    private String email;
    private String phone;
    private String defaultSalariesPaymentAccount;
    @Column(length = 30, nullable = false)
    private String postedBy;
    @Column(nullable = false)
    private Character postedFlag = 'Y';
    private Date postedTime;
    private String modifiedBy;
    private Character modifiedFlag = 'N';
    private Date modifiedTime;
    private String verifiedBy;
    private Character verifiedFlag = 'N';
    private Date verifiedTime;
    private String deletedBy;
    private Character deletedFlag = 'N';
    private Date deletedTime;
}