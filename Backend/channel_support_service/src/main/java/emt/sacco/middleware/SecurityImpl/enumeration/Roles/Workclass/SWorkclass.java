package emt.sacco.middleware.SecurityImpl.enumeration.Roles.Workclass;

import com.fasterxml.jackson.annotation.JsonIgnore;
import emt.sacco.middleware.SecurityImpl.enumeration.Roles.Workclass.Privileges.BasicActions.SBasicactions;
import emt.sacco.middleware.SecurityImpl.enumeration.Roles.Workclass.Privileges.SPrivilege;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class SWorkclass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;
    @Column(length = 6, nullable = false)
    private String entityId;
    @Column(nullable = false)
    private String workClass;
    private Long roleId;
    //    TODO: One workClass has many priviledges
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "SWorkclass")
    private List<SPrivilege> SPrivileges = new ArrayList<>();

//    TODO: One workclass has many basic actions
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "SWorkclass")
    @JsonIgnore
    private List<SBasicactions> basicactions;

    public void setSPrivileges(List<SPrivilege> SPrivileges){
        SPrivileges.forEach(privilege->privilege.setSWorkclass(this));
        this.SPrivileges = SPrivileges;
    }
    //*****************Operational Audit *********************
    @Column(length = 30, nullable = false)
//    @JsonIgnore
    private String postedBy;
    @Column(nullable = false)
    @JsonIgnore
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
    @JsonIgnore
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
