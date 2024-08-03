package com.emtechhouse.accounts.KraStampedTransactions.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StampedTransactionDTO {
         private String pin;//": "P000000002",
         private String bhfId;//": "00",
         private String dvcSrlNo;//": "3i7oBokRdPHqbHfzqYBm2Gg65g",
         private Long dt;;//": 2020022212000000,
         private String accNo; //": "1440182251610",
         private String transTyCd;//": "N",
         private String rcptTyCd;//": "S",
         private String invId; //": 6,
         private String refId;//": 0,
         private String tarrifTyCd;//": "001",
         private Double exRt;//": 20,
         private Double trnAmt;//": 30.00,
         private Double exDutyAmt;//": 6.00

}
