package com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.ProductFees;

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
public class FeeDto {
    private Double min_amt ;
    private Double max_amt;
    private Double amt ;
    private Double percentage ;
    private String amt_derivation_type;
    private String ac_placeholder;
    private Double exercise_duty_fixed_amt;
    private String exciseDutyCollAc;
    private String excise_duty_derivation ;// Percent,Fixed
    private String has_exercise_duty;

    private Double exercise_duty_percentage;
}

