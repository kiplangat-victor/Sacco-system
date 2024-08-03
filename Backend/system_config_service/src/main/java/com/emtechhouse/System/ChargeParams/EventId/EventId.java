package com.emtechhouse.System.ChargeParams.EventId;

import com.emtechhouse.System.ChargeParams.EventId.TieredCharges.Tieredcharges;
import com.emtechhouse.System.InterestCodeParams.Interestcodeslabs.Interestcodeslabs;
import com.emtechhouse.System.Utils.CONSTANTS;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class EventId {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 6, nullable = false)
    private String entityId;

    @Column(length = 12, nullable = false, unique = true, updatable = false)
    private String eventIdCode;
    @Column(nullable = false)
    private String event_id_desc = "undefined";
    private String eventTypeCode = "undefined";
    private String event_type_desc = "undefined";
    @Column(nullable = false)
    private String ac_placeholder = "Undefined";
    @Column(nullable = false)
    private String amt_derivation_type = "Undefined";
    private String chrg_preferentials = "Undefined";
    private String linked_organization = "Undefined";
    private String linked_organization_detail = "Undefined";
    private Double amt = 0.00;
    private Double percentage = 0.00;
    private String chrg_code = "Undefined";
    private String chrg_calc_crncy = "Undefined";
    @Column(nullable = false)
    private String chrg_coll_crncy = "Undefined";
    private String min_amt_ccy = "Undefined";
    private Double min_amt = 0.00;
    private String max_amt_ccy;
    private Double max_amt= 0.00;
    private String chargeType = "Other";
    private String fee_report_code = "Undefined";
    private String rate_code = "Undefined";
    private String tran_remarks_state= "Undefined";
    private String tran_remarks = "Undefined";
    private String tran_particulars_state = "Undefined";
    private String tran_particulars = "Charges Collection";
    private String round_off_flag = "Undefined";
    private String round_off_value = "Undefined";
    private String has_exercise_duty = "N";
    private String excise_duty_derivation = CONSTANTS.PERCENT;
    private String exciseDutyCollAc;
    private Double exercise_duty_percentage;
    private Double exercise_duty_fixed_amt = 0.00;
    private Double monthlyFee = 0.00;

    private Character chargeMonthlyFee = 'N';
    private String monthlyEventId;
    private Character chargeAnnualFee = 'Y';
    private String annualEventId;

    @OneToMany(targetEntity = Tieredcharges.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "eventid_fk", referencedColumnName = "id")
    private List<Tieredcharges> tieredcharges;

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