package com.emtechhouse.usersservice.OrganizationSetup;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
public class Organizationsetup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String organizationName;
    private String organizationTagline;
    private String organizationAddress;
    private String organizationTel;
    private String organizationPhone;
    private String organizationMail;
    private String organizationHqoffices;
    private String organizationWebsite;
    private String organizationTwitter;
    private String organizationFacebook;
    private String organizationLinkedin;
    private String organizationLogo;
    //*****************Operational Audit *********************
    @Column(length = 30, nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String postedBy;
    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Character postedFlag = 'Y';
    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date postedTime;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String modifiedBy;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Character modifiedFlag = 'N';
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date modifiedTime;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String verifiedBy;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Character verifiedFlag = 'N';
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date verifiedTime;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String deletedBy;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Character deletedFlag = 'N';
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date deletedTime;
}
