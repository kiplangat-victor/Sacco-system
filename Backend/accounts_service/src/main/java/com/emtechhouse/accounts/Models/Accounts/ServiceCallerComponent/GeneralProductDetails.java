package com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeneralProductDetails <T>{
    private String productCode;
    private String productType;
    private String productCodeDesc;
    private LocalDate effective_from_date;
    private LocalDate effective_to_date;
    private String principal_lossline_ac;
    private String recovery_lossline_ac;
    private String charge_off_ac;
    private String number_generation;
    private String number_generation_code;
    private String pl_ac_ccy;
    private String pl_ac;
    //private String int_receivale_applicable;
//    private String normal_int_receivable_ac;
    private String int_receivable_ac;
    private String penal_int_receivable_ac;
//    private String normal_int_received_ac;
//    private String penal_int_received_ac;
    private String advance_int_ac;
    private String dr_int_compounding_freq;
    private String int_cal_freq_dr_week;
    private String app_discounted_int_rate;
    private String int_cal_freq_dr_day;
    private String int_cal_freq_dr_date;
    private String int_cal_freq_dr_holiday;
    private Object specificDetails;

    //acid generation
    private String acid_structure;
    private Integer running_no_size;

    //ledger fee
    public Boolean collectLedgerFee;
    private String ledgerFeeEventIdCode;
}
