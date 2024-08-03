package com.emtechhouse.System.InterestCodeParams;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class Interestdetails {
    Double rate;
    String calculationMethod;
    String interestPeriod;
    Double penalInterest;
    String penalInterestType;
}
