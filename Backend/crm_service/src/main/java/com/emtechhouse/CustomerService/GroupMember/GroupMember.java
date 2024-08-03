package com.emtechhouse.CustomerService.GroupMember;

import com.emtechhouse.CustomerService.CustomerImage.CustomerImage;
import com.emtechhouse.CustomerService.GroupMember.GroupMeberSignatory.GroupMemberSignatory;
import com.emtechhouse.CustomerService.GroupMember.GroupMembers.AllGroupMembers;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@ToString
@Data
@EqualsAndHashCode(of = {"id"})
@DynamicUpdate
@Entity
@Table(name = "group_member")
public class GroupMember implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "entity_id")
    private String entityId;

    @Column(length = 50)
    private String employerCode;
    @Column(length = 50)
    private String employerName;

    @Column(nullable = false, unique = true, length = 100)
    private String uniqueId; //Only for backend

    @Column(nullable = false, length = 100)
    private String uniqueType; //only for backend

    @Column(length = 14, unique = true)
    private String customerCode;

    @Column(name = "branch_code")
    private String branchCode;

    @Column(name = "group_type")
    private String groupType;

    @Column(name = "group_name")
    private String groupName;

    @Column(name = "membership_date")
    private Date membershipDate;

    @Column(name = "country")
    private String country;

    @Lob
    @Column(name = "verification_document")
    private String verificationDocument;

    @Column(name = "verification_doc_number")
    private String verificationDocNumber;

    @Column(name = "status")
    private String status;

    @Column(name = "joint_account")
    private Boolean jointAccount = false;

    @Column(name = "has_approval")
    private Boolean hasApproval = false;

    @Column(name = "group_manager_id")
    private String groupManagerId;

    @Column(name = "group_manager_name")
    private String groupManagerName;

    @Column(name = "business_type")
    private String businessType;

    @Column(name = "other_business_type")
    private String otherBusinessType;

    @Column(name = "registration_date")
    private Date registrationDate;

    @Column(name = "business_reg_no")
    private String businessRegNo;

    @Column(name = "business_nature")
    private String businessNature;

    @Column(name = "post_code")
    private String postCode;

    @Column(name = "town")
    private String town;

    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "county")
    private String county;

    @Column(name = "sub_county")
    private String subCounty;

    @Column(name = "ward")
    private String ward;

    @Column(name = "postal_address")
    private String postalAddress;

    @Column(name = "home_address")
    private String homeAddress;

    @Column(name = "current_address")
    private String currentAddress;

    @Column(name = "primary_phone")
    private String primaryPhone;

    @Column(name = "other_phone")
    private String otherPhone;

    @Column(name = "group_mail")
    private String groupMail;

    @Column(name = "representative")
    private String representative;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "branch_name")
    private String bankBranch;

    @Column(name = "bank_account_no")
    private String bankAccountNo;

    @OneToMany(cascade = CascadeType.ALL, targetEntity = GroupMemberSignatory.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "group_signatory_fk", referencedColumnName = "id")
    private List<GroupMemberSignatory> groupMemberSignatories;

    @OneToMany(cascade = CascadeType.ALL, targetEntity = AllGroupMembers.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "group_member_fk", referencedColumnName = "id")
    private List<AllGroupMembers> allGroupMembers;


    @OneToMany(cascade = CascadeType.ALL, targetEntity = CustomerImage.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "group_member_fk")
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
