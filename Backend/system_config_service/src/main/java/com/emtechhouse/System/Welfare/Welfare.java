package com.emtechhouse.System.Welfare;

import com.emtechhouse.System.GLSubhead.GLSubhead;
import com.emtechhouse.System.Welfare.WelfareAction.WelfareAction;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class Welfare {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 6, nullable = false)
    private String entityId;
    @Column(length = 12, nullable = false, updatable = false, unique = true)
    @JsonProperty(required = true)
    private String welfareCode;
    @Column(length = 60, nullable = false)
    private String welfareName;

    @OneToMany(cascade = CascadeType.ALL)
    private List<WelfareAction> actions;


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
