package com.emtechhouse.usersservice.Roles.Workclass.Privileges.BasicActions;

import com.emtechhouse.usersservice.Roles.Workclass.Privileges.Privilege;
import com.emtechhouse.usersservice.Roles.Workclass.Workclass;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class Basicactions implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    private boolean selected;
    private String code;

    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "privilegeFk")
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Privilege privilege;


    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "workclassFk")
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Workclass workclass;

    //*****************Operational Audit *********************
    @Column(length = 30, nullable = false)
//    @JsonIgnore
    private String postedBy;
    @JsonIgnore
    @Column(nullable = false)
    private Character postedFlag = 'Y';
    @Column(nullable = false)
//    @JsonIgnore
    private Date postedTime;
    @JsonIgnore
    private String modifiedBy;
    @JsonIgnore
    private Character modifiedFlag = 'N';
    @JsonIgnore
    private Date modifiedTime;
//    @JsonIgnore
    private String verifiedBy;
//    @JsonIgnore
    private Character verifiedFlag = 'N';
    @JsonIgnore
    private Date verifiedTime;
    @JsonIgnore
    private String deletedBy;
    @JsonIgnore
    private Character deletedFlag = 'N';
    @JsonIgnore
    private Date deletedTime;
}
