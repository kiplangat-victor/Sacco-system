package com.emtechhouse.System.SharecapitalParams;

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
public class SharecapitalParam {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 6, nullable = false)
    private String entityId;

    @Column(length = 6, nullable = false, unique = true)
    @JsonProperty(required = true)
    private String shareCapitalCode;

    private Integer share_capital_unit;
    private Double share_capital_amount_per_unit;
    private Integer share_min_unit;
    private String shares_office_ac;
    private Boolean isActive = false;

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
    private Character verifiedFlag = 'N';;
    private Date verifiedTime;
    private String deletedBy;
    private Character deletedFlag = 'N';;
    private Date deletedTime;
}