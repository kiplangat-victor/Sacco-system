package com.emtechhouse.accounts.Requests;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class LoanPrepayRequest {

    @NotNull
    String loanAcid;

    @NotNull
    Character useRepaymentAccount;
    @NotNull
    Character repayOneInstallment;

    Double amount;

    String repaymentAccount;
}

