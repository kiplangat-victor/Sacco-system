package co.ke.emtechhouse.AmountSlabs;

import co.ke.emtechhouse.InterestMaintenance.Interest;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class AmountSlab {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 6, nullable = false)
    private String entityId;

    @Column(length = 5, nullable = false)
    private String interestCode;
    @Column(nullable = false)
    private Integer version;
    @Column(precision = 10, scale = 2)
    private Double fromAmount;
    @Column(precision = 10, scale = 2)
    private Double toAmount;
    @Column(precision = 10, scale = 2, nullable = false)
    private Double rate;
    @Column(nullable = false)
    private Character drCr;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Interest interest;

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
