package com.emtechhouse.accounts.Models.Accounts.ServiceCallerComponent.LoanLimitDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoanLimitDto {
    private Long id;
    private String productCode;
    private String conditionType;
    private Double accMultiplier = 1.0;
    private Integer privTranPeriodMonth = 1;
    private Double historyMultiplier = 1.0;
    private Integer activeMonths = 1;
    private String limitStatementInWords;
}
