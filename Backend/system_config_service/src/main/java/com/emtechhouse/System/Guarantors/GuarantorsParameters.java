package com.emtechhouse.System.Guarantors;

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
public class GuarantorsParameters {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 6, nullable = false)
    private String entityId;
    @Column(length = 3, nullable = false)
    private String activeness_status="N";
    @Column(length = 3, nullable = false)
    private String subsequent_guarantee_status="N";
    @Column(length = 3, nullable = false)
    private String loan_status="N";
    @Column(length = 3, nullable = false)
    private String shares_qualification_status="N";
    @Column(length = 3, nullable = false)
    private String maximum_active_guaranteed_no="N";
    @Column(length = 3, nullable = false)
    private int max_no_guaranteed=0;

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