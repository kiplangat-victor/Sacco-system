package emt.sacco.middleware.ATM.Reports;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/reports")
public class ReportController {

    private final ReportService reportservice;

    public ReportController(ReportService reportservice) {
        this.reportservice = reportservice;

    }

    @GetMapping("/error/{format}")
    public String generateErrorReports(@PathVariable String format) {
        return reportservice.exportErrorReports(format);

    }
    @GetMapping("/transactions/{format}")
    public String generateTransReports(@PathVariable String format) {
        return reportservice.exportTransactionReports(format);

    }

    @GetMapping("/reversals /{format}")
    public String generateReversalsReports(@PathVariable String format) {
        return reportservice.exportReversalReports(format);

    }
}
