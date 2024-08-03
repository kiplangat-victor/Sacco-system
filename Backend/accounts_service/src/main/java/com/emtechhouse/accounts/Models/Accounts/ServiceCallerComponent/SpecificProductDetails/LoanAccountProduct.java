package com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.SpecificProductDetails;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoanAccountProduct {
    private Double loan_amt_min;
    private Double loan_max_amt;
    private Long loan_period_min_mm;
    private Long loan_period_max_mm;
    private String loan_repayment_method;
    private String loan_repayment_hold_op_ac;
    private String loan_repayment_upront_installment_collection;
    private String flow_offset_based_on;
    private String int_base;
    private String int_prod;
    private String int_route_through;
    private String fee_routed_through;
    private String loan_int_ac;
    private String penal_int_recognition_method;
    private String int_on_principal;
    private String principal_demand_overdue_at_endmonth;
    private Integer principal_overdue_after_mm;
    private Integer int_overdue_after_mm;
    private Integer charge_overdue_after_mm;
    private String int_on_demand;
    private String overdue_int_on_principal;
    private String int_demand_overdue_at_end_month;
    private String apply_preferential_int_for_overdue_int;
    private String charge_demand_overdue_end_month;
    private String int_rate_based_on_loan_amt;
    private String grace_period_for_late_fee_mm; //should be in days
    private String apply_late_fee_for_delayed_payment;
    private String penal_int_on_principal_overdue;
    private String penal_int_on_int_demand_overdue;
    private String penal_int_based_on;
    private String penal_int_product_method;
    private String normal_int_product_method;
    private String penal_int_rate_method;
    private String grace_period_for_penal_int_mm;
    private String  equated_installment;
    private String int_compound_freq;
    private String outstanding_amt_after_last_inst;
    private String ei_formula;
    private String shift_installment_for_holiday;
    private String includ_maturity_date_incase_for_holiday ;
    private String upfront_int_collection;
    private String discounted_int;

    //guarantor details
    private Integer minGuarantorCount;
    private Integer maxGuarantorCount;
}
