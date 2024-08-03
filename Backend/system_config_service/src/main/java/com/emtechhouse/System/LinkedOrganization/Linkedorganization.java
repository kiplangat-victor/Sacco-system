package com.emtechhouse.System.LinkedOrganization;

import com.emtechhouse.System.LinkedOrganization.OrganizationCharges.Organizationeventid;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class Linkedorganization{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 6, nullable = false)
    private String entityId;
    @Column(length = 6, nullable = false, unique = true)
    @JsonProperty(required = true)
    private String linkedOrganizationCode;
    private String organization_name;
    private String organization_tel;
    private String organization_mail;
    private String organization_address;
    private String organization_website;
    private String organization_country;
    private String organization_main_office;

    private String org_chrg_application;
    private String org_chrg_crncy;
    private String org_chrg_derivation;
    private String org_cust_cif;
    private Double org_fixed_amt;
    private Double org_percentage_val;

    private String first_priority;
    private String second_priority;
    private String third_priority;
    private String fourth_priority;

    //   Organization Charges
    @OneToMany(targetEntity = Organizationeventid.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "organization_eventid", referencedColumnName = "id")
    private List<Organizationeventid> organization_charges;

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
    private String verifiedBy;
    private Character verifiedFlag = 'N';
    private Date verifiedTime;
    private String deletedBy;
    private Character deletedFlag = 'N';
    private Date deletedTime;
}
