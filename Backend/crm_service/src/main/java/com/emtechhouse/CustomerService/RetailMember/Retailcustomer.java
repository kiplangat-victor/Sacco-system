package com.emtechhouse.CustomerService.RetailMember;

import com.emtechhouse.CustomerService.CustomerImage.CustomerImage;
import com.emtechhouse.CustomerService.RetailMember.Nextofkeen.Nextofkin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class Retailcustomer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 6, nullable = false)
    private String entityId;
    //General Details
    @Column(length = 2, nullable = false)
    private String memberType; // 01/02
    @Column(length = 14, unique = true)
    private String customerCode; //01-001
    @Column(nullable = false)
    private String branchCode; //001
    @Column(nullable = false, length = 100)
    private String firstName;
    @Column(length = 100)
    private String middleName;
    @Column(nullable = false, length = 100)
    private String lastName;
    @Column(nullable = false, length = 50)
    private String gender;
    @Column(length = 100)
    private String maritalStatus;
    @Column(length = 100)
    private String citizen;
    @Column(length = 100)
    private String  isMinor;
    @Column(length = 100)
    private String birthCertificateNo;
    @Column(length = 100)
    private String nationalId;
    @Temporal(TemporalType.DATE)
    @Column(length = 100)
    private Date dob;
    @Column(length = 100)
    private String kraPin;
    @Column(nullable = false, unique = true, length = 100)
    private String uniqueId; //Only for backend
    @Column(nullable = false, length = 100)
    private String uniqueType; //only for backend
    @Column(length = 100)
    private String segment;
    @Column(length = 100)
    private String subSegment;
    @Column(nullable = false)
    private Double monthlyContribution = 0.00;
    //Contct Information
    @Column(length = 20, unique = true)
    private String phoneNumber;
    @Column(length = 100)
    private String alternatePhone;
    @Column(length = 50)
    private String employerCode;
    @Column(length = 50)
    private String employerName;
    @Column(length = 100)
    private String emailAddress;
    @Column(length = 100)
    private String postalAddress;
    @Column(length = 100)
    private String town;
    @Column(length = 100)
    private String residenceType;
    @Column(length = 100)
    private String nearestSchool;
    @Column(length = 100)
    private String nearestChurch;
    @Column(length = 100)
    private String county;
    @Column(length = 100)
    private String subCounty;
    @Column(length = 100)
    private String location;
    private String onBoardingMethod; ///mobile or web
            //Employment Details;
    @Column(length = 100)
    private String employmentType; //Self-Employed or Employed
    @Column(length = 100)
    private String profession;
    @Column(length = 100)
    private String currentOccupation;
    @Column(length = 100)
    private String organization;
    @Column(length = 100)
    private String workType;
    @Column(length = 100)
    private String employeeNo;
    @Column(length = 100, nullable = true)
    private String companyAddress;
    @Column(length = 100)
    private String workLocation;
    @Column(length = 100)
    private Double salary = 0.00;
    private String status = "NEW";

    @OneToMany(cascade = CascadeType.ALL, targetEntity = Nextofkin.class, fetch = FetchType.LAZY )
    @JoinColumn(name="Retailcustomer_fk", referencedColumnName = "id")
    private List<Nextofkin> nextofkins;

    @OneToMany(cascade = CascadeType.ALL, targetEntity = CustomerImage.class, fetch = FetchType.LAZY)
    @JoinColumn(name="Retailcustomer_fk", referencedColumnName = "id")
    private List<CustomerImage> images;

    //*****************Operational Audit *********************
    @Column(length = 30, nullable = false)
    private String postedBy;
    @Column(nullable = false)
    private Character postedFlag = 'Y';
    @Column(nullable = false)
    private Date postedTime;
    private String modifiedBy;
    private Character modifiedFlag = 'N';
    private Date modifiedTime;
    private String rejectedBy;
    private Character rejectedFlag = 'N';
    private Date rejectedTime;
    private String verifiedBy;
    private Character verifiedFlag = 'N';
    private Date verifiedTime;
    private String deletedBy;
    private Character deletedFlag = 'N';
    private Date deletedTime;
}