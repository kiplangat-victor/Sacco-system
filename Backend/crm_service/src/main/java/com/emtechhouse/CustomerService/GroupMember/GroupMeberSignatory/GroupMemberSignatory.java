package com.emtechhouse.CustomerService.GroupMember.GroupMeberSignatory;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@ToString
@Data
@EqualsAndHashCode(of = {"id"})
@DynamicUpdate
@Entity
@Table(name = "group_member_signatory")
public class GroupMemberSignatory implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
//    @Column(name = "id", updatable = false, nullable = false, unique = true)
    private Long id;

    @Column(name = "member_code")
    private String memberCode;

    @Column(name = "member_name")
    private String memberName;

    @Column(name = "sign_operation")
    private String signOperation;

    @Column(name = "nationality")
    private String nationality;

    @Column(name = "verification_document")
    private String verificationDocument;

    @Column(name = "Verification_document_no")
    private String verificationDocumentNo;

    @Column(name = "expiry_date")
    private Date expriyDate;

    @Column(name = "designation")
    private String designation;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "must_sign")
    private Boolean mustSign;
}
