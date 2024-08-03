package emt.sacco.middleware.SecurityImpl.Tellersaccounts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@ToString
public class Telleraccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;
    @Column(length = 6, nullable = false)
    private String entityId;
    @Column(nullable = false, unique = true)
    private String tellerId;
    @Column(nullable = false, unique = true)
    private String tellerAc;
    @Column(nullable = true, unique = true)
    private String shortageAc;
    @Column(unique = true)
    private String agencyAc;
    @Column(nullable = true, unique = true)
    private String excessAc;
    private String tellerUserName;

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
