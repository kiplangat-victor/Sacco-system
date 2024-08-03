package com.emtechhouse.accounts.TransactionService.EODProcess.performEOD.EODResponses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class EODResponse {
    List<EODRes> eodRes;
    private boolean eodStatus = false;
    private String newSystemDate = String.valueOf(new Date());
}
