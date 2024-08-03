package com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.SpecificProductDetails;

import lombok.Data;

@Data
public class TdaProdDto {
    private Long id;
    private Double tda_deposit_amt_min = 0.00;
    private Double tda_deposit_amt_max = 0.00;
    private Integer tda_period_mm_min;
    private Integer tda_period_dd_min;
    private Integer tda_period_mm_max;
    private Integer tda_period_dd_max;
    private String tda_sundry_deposit_ph;
    private String tda_repayment_report_code;
    private String tda_pre_closure_rate;
    private String tda_pre_closure_penalty_rate;
    private String tda_frequency_for_int_calc_on_preclosure_month;
    private String tda_repayment_ac_ph;
    private String tda_renewal_allowed_within_days;
    private String int_cal_freq_dr_week;
}
