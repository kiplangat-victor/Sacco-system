package com.emtechhouse.reports.Resources;

import lombok.Data;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;

@Data
@Entity
public class ReportRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    String fromdate;
    String todate;
    String todaydate;
    String branchCode;
    String memberType;
    String username;
    String acid;
    String accountType;
    String employerCode;
    String groupCode;
    String glSubheadCode;
    String productCode;
    String classification;
    String transactionType;
    String customerCode;
    String solCode;
    String glCode;
    String welfareCode;
    String fileName;
    String welfareActionCode;
    String year;
    String checkNumber;
    String accountStatus;

    Character charge;

    @Transient
    List<String> conversionsFrom;
    @Transient
    List<String> conversionsTo;

    @Transient
    HashMap<String, String> conversions = new HashMap<>();
}