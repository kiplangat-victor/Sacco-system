package com.emtechhouse.usersservice.Roles;

import com.emtechhouse.usersservice.Roles.Workclass.Privileges.BasicActions.Basicactions;
import com.emtechhouse.usersservice.Roles.Workclass.Workclass;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 6, nullable = false)
    @JsonIgnore
    private String entityId;
    @Column(length = 200, nullable = false)
    private String name;
//    //    TODO: One Role has many workclass
//    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "role")
//    @JsonIgnore
//    @EqualsAndHashCode.Exclude
//    @ToString.Exclude
//    private List<Workclass> workclass = new ArrayList<>();

//    //    TODO: One workclass has many basic actions
//    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "workclass")
//    @JsonIgnore
//    @ToString.Exclude
//    private List<Basicactions> basicactions;

    //*****************Operational Audit *********************
    @Column(length = 30, nullable = false)
//    @JsonIgnore
    private String postedBy;
    @Column(nullable = false)
//    @JsonIgnore
    private Character postedFlag = 'Y';
//    @Column(nullable = false)
//    @JsonIgnore
    private Date postedTime;
//    @JsonIgnore
    private String modifiedBy;
    private String roleCode;
//    @JsonIgnore
    private Character modifiedFlag = 'N';
//    @JsonIgnore
    private Date modifiedTime;
//    @JsonIgnore
    private String verifiedBy;
//    @JsonIgnore
    private Character verifiedFlag = 'N';
//    @JsonIgnore
    private Date verifiedTime;
//    @JsonIgnore
    private String deletedBy;
//    @JsonIgnore
    private Character deletedFlag = 'N';
//    @JsonIgnore
    private Date deletedTime;
}