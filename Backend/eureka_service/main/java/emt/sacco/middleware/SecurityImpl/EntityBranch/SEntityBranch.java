package emt.sacco.middleware.SecurityImpl.EntityBranch;

import emt.sacco.middleware.SecurityImpl.SaccoEntity.SSaccoEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
@Entity
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SEntityBranch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 6, nullable = false)
    private String entityId;
    @Column(length = 6, nullable = false, unique = true)
    private String branchCode;
    @Column(length = 6, nullable = false)
    private String saccoEntityCode;
    @Column(length = 200, nullable = false)
    private String branchDescription;
    @Column(length = 20, nullable = false)
    private  String location;
    @Column(length = 100, nullable = false,unique = true)
    private String email;
    @Column(length = 13, nullable = false, unique = true)
    private String phoneNumber;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "ssacco_entity_id")
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private SSaccoEntity SSaccoEntity;

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
