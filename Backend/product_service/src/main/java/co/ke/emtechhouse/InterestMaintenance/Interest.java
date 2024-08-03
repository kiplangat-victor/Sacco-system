package co.ke.emtechhouse.InterestMaintenance;

import co.ke.emtechhouse.AmountSlabs.AmountSlab;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Interest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 6, nullable = false)
    private String entityId;

    @Column(length = 6, nullable = false, unique = true)
    private String interestCode;
    @Column(nullable = false)
    private Character fullDiff;

    private double penalInterest;
    @Temporal(TemporalType.DATE)
    private Date startDate;
    @Temporal(TemporalType.DATE)
    private Date endDate;
    @Column(nullable = false, length = 3)
    private String currency;

    @OneToMany(mappedBy = "interest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AmountSlab> amountSlabs = new ArrayList<>();

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
