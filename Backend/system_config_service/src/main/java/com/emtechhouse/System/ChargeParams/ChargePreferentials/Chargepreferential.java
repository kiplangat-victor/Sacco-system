package com.emtechhouse.System.ChargeParams.ChargePreferentials;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class Chargepreferential {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 6, nullable = false)
    private Long entityId;

    private String chrg_preferential;
    private String event_type;
    private String event_id;
    private String account_id;
    private String cif_id;
    private String organization_id;

    private Date start_date;
    private LocalDateTime end_date;
    private String chrg_derivation;
    private Double percentage_val;
    private Double fixed_amt;
    private String min_amt_ccy;
    private Double min_amt;
    private String max_amt_ccy;
    private Double max_amt;

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
