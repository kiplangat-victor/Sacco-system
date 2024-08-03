package emt.sacco.middleware.ATM.Reports;

import emt.sacco.middleware.Utils.RestService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ReportService {

    @Autowired
    private final AtmErrorLogs atmErrorLogs;
    private final RestService restService;

    public ReportService(AtmErrorLogs atmErrorLogs, RestService restService) {
        this.atmErrorLogs = atmErrorLogs;
        this.restService = restService;
    }


    public String exportErrorReports(String reportFormat) {
    return restService.generateErrorReports(reportFormat);
    }

    public String exportTransactionReports(String reportFormat) {
        return restService.generateTransactionReports(reportFormat);
    }
    public String exportReversalReports(String reportFormat) {
        return restService.generateReversalReports(reportFormat);
    }


}