package co.ke.emtechhouse.CustomerProducts.Product_specific_details.SBA_Details;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class SBA_Details {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 150)
    private String  no_of_withdrawals;
    @Column(length = 150)
    private String no_int_if_withdwl_exceeded;
    @Column(length = 150)
    private String ac_statement_charged_by;
    @Column(length = 150)
    private String fee_withrawal;
    @Column(length = 150)
    private String inactive_ac_abnormal_tran_limit;
    @Column(length = 150)
    private String dormant_ac_abnormal_trans_limit;
    @Column(length = 150)
    private String duration_to_mark_ac_as_inactive;
    @Column(length = 150)
    private String duration_from_inactive_to_dormant;
    @Column(length = 150)
    private String int_calc_based_local_calender;
    @Column(length = 50)
    private String int_method;
    private Double minimum_monthly_contribution = 0.0;
    private Double min_account_balance = 0.0;
    private Double ledger_fee_amount= 0.0;
    private String ledger_fee_account;
    @Column(length = 100)
    private LocalDateTime bal_frm_date;
    @Column(length = 100)
    private LocalDateTime bal_to_date;
    @Column(length = 150)
    private String   recover_fee_for_chq_issue;
    @Column(length = 150)
    private Boolean withdrawalsAllowed = false;
    private String withdrawalCharge;
    @Column(length = 150, nullable = false)
    private Boolean allow_mobile_transactions = false;
    private String eventTypeCode;
    private String eventIdCode;
}